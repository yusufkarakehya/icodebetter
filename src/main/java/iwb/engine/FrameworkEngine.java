package iwb.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;

// import com.sun.xml.xsom.XSType;

import iwb.custom.trigger.GetFormTrigger;
import iwb.custom.trigger.GlobalFuncTrigger;
import iwb.custom.trigger.PostFormTrigger;
import iwb.custom.trigger.QueryTrigger;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.Log5Feed;
import iwb.domain.db.Log5GlobalNextval;
import iwb.domain.db.Log5Notification;
// import iwb.dao.tsdb_impl.InfluxDao;
import iwb.domain.db.Log5WorkflowRecord;
import iwb.domain.db.Log5WsMethodAction;
import iwb.domain.db.W5BIGraphDashboard;
import iwb.domain.db.W5Comment;
import iwb.domain.db.W5Conversion;
import iwb.domain.db.W5ConvertedObject;
import iwb.domain.db.W5Customization;
import iwb.domain.db.W5Detay;
import iwb.domain.db.W5Email;
import iwb.domain.db.W5FileAttachment;
import iwb.domain.db.W5Form;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5FormModule;
import iwb.domain.db.W5FormSmsMail;
import iwb.domain.db.W5FormSmsMailAlarm;
import iwb.domain.db.W5Grid;
import iwb.domain.db.W5GridColumn;
import iwb.domain.db.W5Jasper;
import iwb.domain.db.W5JasperObject;
import iwb.domain.db.W5JasperReport;
import iwb.domain.db.W5LookUp;
import iwb.domain.db.W5LookUpDetay;
import iwb.domain.db.W5ObjectMailSetting;
import iwb.domain.db.W5PageObject;
import iwb.domain.db.W5Param;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Query;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5QueryParam;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableChild;
import iwb.domain.db.W5TableEvent;
import iwb.domain.db.W5TableField;
import iwb.domain.db.W5TsMeasurement;
import iwb.domain.db.W5TsPortlet;
import iwb.domain.db.W5Tutorial;
import iwb.domain.db.W5UploadedImport;
import iwb.domain.db.W5VcsCommit;
import iwb.domain.db.W5VcsObject;
import iwb.domain.db.W5Workflow;
import iwb.domain.db.W5WorkflowRecord;
import iwb.domain.db.W5WorkflowStep;
import iwb.domain.db.W5Ws;
import iwb.domain.db.W5WsMethod;
import iwb.domain.db.W5WsMethodParam;
import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.domain.helper.W5AccessControlHelper;
import iwb.domain.helper.W5CommentHelper;
import iwb.domain.helper.W5FormCellHelper;
import iwb.domain.helper.W5QueuedActionHelper;
import iwb.domain.helper.W5ReportCellHelper;
import iwb.domain.helper.W5SynchAfterPostHelper;
import iwb.domain.helper.W5TableRecordHelper;
import iwb.domain.result.M5ListResult;
import iwb.domain.result.W5CardResult;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5GridResult;
import iwb.domain.result.W5JasperResult;
import iwb.domain.result.W5ListViewResult;
import iwb.domain.result.W5PageResult;
import iwb.domain.result.W5QueryResult;
import iwb.domain.result.W5TableRecordInfoResult;
import iwb.domain.result.W5TutorialResult;
import iwb.enums.FieldDefinitions;
import iwb.exception.IWBException;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;
import iwb.util.HttpUtil;
import iwb.util.JasperUtil;
import iwb.util.LogUtil;
import iwb.util.MailUtil;
import iwb.util.Money2Text;
import iwb.util.MyFactory;
import iwb.util.UserUtil;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;

@Service
@Transactional
public class FrameworkEngine {
  @Lazy @Autowired private PostgreSQL dao;

  public synchronized void reloadCache(int cid) {
    try {
      if (cid == -1) FrameworkSetting.systemStatus = 2; // suspended
      // dao.setEngine(this);
      dao.reloadFrameworkCaches(cid);
    } catch (Exception e) {
      if (FrameworkSetting.debug) e.printStackTrace();
    } finally {
      if (cid == -1) FrameworkSetting.systemStatus = 0;
    }
  }

  private boolean checkAccessRecordControlViolation(
      Map<String, Object> scd, int accessTip, int tableId, String tablePk) {
    Map<String, String> rm = new HashMap<String, String>();
    rm.put("xaccess_tip", "" + accessTip);
    rm.put("xtable_id", "" + tableId);
    rm.put("xtable_pk", tablePk);
    Map m = executeQuery2Map(scd, 588, rm);
    return (m != null
        && !GenericUtil.accessControl(
            scd,
            (short) accessTip,
            (String) m.get("access_roles"),
            (String) m.get("access_users")));
  }

  private List<W5FormCellHelper> formCell4AccessControl(
      Map<String, Object> scd, int tableId, String tablePk, int accessTip) {
    List<W5FormCellHelper> la = new ArrayList<W5FormCellHelper>();
    Map<String, String> rm = new HashMap<String, String>();
    String accessRoles = null, accessUsers = null;
    int accessTipFlag = 0;
    rm.put("xaccess_tip", "" + accessTip);
    rm.put("xtable_id", "" + tableId);
    rm.put("xtable_pk", tablePk);
    Map m = executeQuery2Map(scd, 588, rm);
    if (m != null) {
      accessRoles = (String) m.get("access_roles");
      accessUsers = (String) m.get("access_users");
      accessTipFlag = 1;
    }
    W5FormCell cellAccessTip = new W5FormCell();
    cellAccessTip.setDsc("_access_tip" + accessTip);
    cellAccessTip.setLocaleMsgKey("access_tip." + accessTip);
    cellAccessTip.setTabOrder((short) (900 + 3 * accessTip));
    cellAccessTip.setControlTip((short) 6);
    cellAccessTip.setLookupQueryId((short) 215);
    cellAccessTip.setControlWidth((short) 70);
    cellAccessTip.setNotNullFlag((short) 1);
    cellAccessTip.setFormModuleId(900);
    la.add(new W5FormCellHelper(cellAccessTip, "" + accessTipFlag));

    W5FormCell cellAccessRoles = new W5FormCell();
    cellAccessRoles.setDsc("_access_roles" + accessTip);
    cellAccessRoles.setLocaleMsgKey("access_roles." + accessTip);
    cellAccessRoles.setTabOrder((short) (901 + 3 * accessTip));
    cellAccessRoles.setControlTip((short) 15);
    cellAccessRoles.setLookupQueryId((short) 554);
    cellAccessRoles.setControlWidth((short) 300);
    cellAccessRoles.setFormModuleId(900);
    la.add(new W5FormCellHelper(cellAccessRoles, accessRoles));

    W5FormCell cellAccessUsers = new W5FormCell();
    cellAccessUsers.setDsc("_access_users" + accessTip);
    cellAccessUsers.setLocaleMsgKey("access_users." + accessTip);
    cellAccessUsers.setTabOrder((short) (901 + 3 * accessTip));
    cellAccessUsers.setControlTip((short) 15);
    cellAccessUsers.setLookupQueryId((short) 585);
    cellAccessUsers.setControlWidth((short) 300);
    cellAccessUsers.setFormModuleId(900);
    la.add(new W5FormCellHelper(cellAccessUsers, accessUsers));

    return la;
  } /*
    private	boolean updateAccessRecordControl(Map<String, Object> scd,int tableId, String tablePk, int accessTip, Map<String,String> requestParams){
    	Map<String,String> m = new HashMap<String,String>();
    	m.put("ptable_id", ""+tableId);
    	m.put("ptable_pk", tablePk);
    	m.put("paccess_tip", ""+accessTip);
    	m.put("paccess_flag", requestParams.get("_access_tip"+accessTip));
    	m.put("access_roles", requestParams.get("_access_roles"+accessTip));
    	m.put("access_users", requestParams.get("_access_users"+accessTip));
    	return executeGlobalFunc(scd, 269, m, (short)1).isSuccess();
    }*/

  public W5FormResult getFormResultByQuery(
      Map<String, Object> scd, int formId, int queryId, Map<String, String> requestParams) {
    W5FormResult formResult = dao.getFormResult(scd, formId, 1, requestParams);
    //		formResult.getForm().get_formCells().clear();
    if (formId != 1622) formResult.getForm().get_moduleList().clear(); // TODO: neden yapilmis???

    W5QueryResult queryResult = executeQuery(scd, queryId, requestParams);
    formResult.setFormCellResults(new ArrayList<W5FormCellHelper>(queryResult.getData().size()));

    short tabOrder = 1;
    for (Object[] d : queryResult.getData()) {
      W5FormCellHelper result = GenericUtil.getFormCellResultByQueryRecord(d);
      if (result.getFormCell().getTabOrder() == 0) result.getFormCell().setTabOrder(tabOrder);
      tabOrder++;
      formResult.getFormCellResults().add(result);
    }
    if (queryResult.getQuery().get_queryFields().get(7).getPostProcessTip()
        == 10) { // tipi lookup ise o zaman modulleri buraya koy
      for (W5LookUpDetay d :
          FrameworkCache.getLookUp(
                  scd, queryResult.getQuery().get_queryFields().get(7).getLookupQueryId())
              .get_detayList()) {
        W5FormModule m = new W5FormModule();
        m.setFormModuleId(GenericUtil.uInt(d.getVal()));
        m.setLocaleMsgKey(d.getDsc());
        formResult.getForm().get_moduleList().add(m);
      }
    }
    dao.loadFormCellLookups(scd, formResult.getFormCellResults(), requestParams, null);
    return formResult;
  }

  public W5FormResult getFormResult(
      Map<String, Object> scd, int formId, int action, Map<String, String> requestParams) {
    if (
    /*formId==0 && */ GenericUtil.uInt(requestParams.get("_tb_id")) != 0
        && GenericUtil.uInt(requestParams.get("_tb_pk"))
            != 0) { // isterse _tb_id,tb_pk degerleriyle de bir form acilabilir
      W5Table t = FrameworkCache.getTable(scd, GenericUtil.uInt(requestParams.get("_tb_id")));
      if (t == null) {
        throw new IWBException(
            "framework",
            "Table",
            GenericUtil.uInt(requestParams.get("_tb_id")),
            null,
            LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_such_table"),
            null);
      }
      formId = GenericUtil.uInt(requestParams, "_fid");
      if (formId == 0) formId = t.getDefaultUpdateFormId();
      if (formId == 0) {
        List ll =
            dao.executeSQLQuery(
                "select min(f.form_id) from iwb.w5_form f where f.project_uuid=? AND f.object_tip=2 AND f.object_id=?",
                scd.get("projectId"),
                t.getTableId());
        if (GenericUtil.isEmpty(ll)) {
          throw new IWBException(
              "framework",
              "Table",
              t.getTableId(),
              null,
              LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_no_form_for_table"),
              null);
        }
        formId = GenericUtil.uInt(ll.get(0));
      }
      requestParams.put(t.get_tableParamList().get(0).getDsc(), requestParams.get("_tb_pk"));
      action = 1;
      requestParams.put("a", "1");
    }
    W5FormResult formResult = null;
    try {
      formResult = dao.getFormResult(scd, formId, action, requestParams);
      checkTenant(formResult.getScd());
      formResult.setUniqueId(GenericUtil.getNextId("fi"));
      /*		if(requestParams.containsKey("_log5_log_id")){
      	if(!FrameworkCache.wTemplates.containsKey(scd.get("customizationId")))FrameworkCache.wTemplates.put((Integer)scd.get("customizationId"),new HashMap());
      	FrameworkCache.wTemplates.get((Integer)scd.get("customizationId")).put(668, (W5Template)dao.find("from W5Template t where t.templateId=668 AND t.customizationId=?", scd.get("customizationId")).get(0));
      } */
      //		boolean dev = scd.get("roleId")!=null && (Integer)scd.get("roleId")==0 &&
      // GenericUtil.uInt(requestParams,"_dev")!=0;
      String projectId = FrameworkCache.getProjectId(scd, "40." + formId);
      W5Table t = null;
      switch (formResult.getForm().getObjectTip()) {
        case 2: // table
          t =
              FrameworkCache.getTable(
                  projectId,
                  formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();
          boolean accessControlSelfFlag = true; // kendisi VEYA kendisi+master
          switch (t.getAccessViewTip()) {
            case 1:
              if (t.getAccessViewUserFields() == null
                  && !GenericUtil.accessControl(
                      scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())) {
                throw new IWBException(
                    "security",
                    "Form",
                    formId,
                    null,
                    LocaleMsgCache.get2(
                        0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"),
                    null);
              }
          }

          if (accessControlSelfFlag) accessControl4FormTable(formResult, null);
          if (formResult.getForm().get_moduleList() != null) {
            for (W5FormModule m : formResult.getForm().get_moduleList())
              if (m.getModuleTip() == 4
                  && GenericUtil.accessControl(
                      scd,
                      m.getAccessViewTip(),
                      m.getAccessViewRoles(),
                      m.getAccessViewUsers())) { // form
                if (m.getModuleViewTip() == 0
                    || (m.getModuleViewTip() == 1 && action == 1)
                    || (m.getModuleViewTip() == 2 && action == 2)) {
                  int newAction = GenericUtil.uInt(requestParams.get("a" + m.getTabOrder()));
                  if (newAction == 0) newAction = action;
                  if (formResult.getModuleFormMap() == null)
                    formResult.setModuleFormMap(new HashMap());
                  formResult
                      .getModuleFormMap()
                      .put(
                          m.getObjectId(),
                          getFormResult(scd, m.getObjectId(), newAction, requestParams));
                }
              }
          }
          break;
        case 5: // formByQuery:
          formResult.setQueryResult4FormCell(
              executeQuery(scd, formResult.getForm().getObjectId(), requestParams));
          formResult.setFormCellResults(new ArrayList());
          for (Object[] d : formResult.getQueryResult4FormCell().getData()) {
            W5FormCellHelper result = GenericUtil.getFormCellResultByQueryRecord(d);
            //				result.getFormCell().setTabOrder(tabOrder++);
            formResult.getFormCellResults().add(result);
          }
          break;
      }

      GetFormTrigger.beforeGetForm(formResult);
      if (formResult.getForm().getObjectTip() != 2)
        action = 2; // eger table degilse sadece initializeForm olabilir

      if (formResult.getForm().getObjectTip() != 5 && action == 9 /*edit (if not insert)*/) {
        action = dao.checkIfRecordsExists(scd, requestParams, t) ? 1 : 2;
      }

      /* tableTrigger before Show start*/
      if (formResult.getForm().getObjectTip() == 2 && action != 3)
        extFormTrigger(
            formResult,
            new String[] {"_", "su", "si", "_", "_", "si"}[action],
            scd,
            requestParams,
            t,
            null,
            null);
      /* end of tableTrigger*/

      if (formResult.getForm().getObjectTip() != 5)
        switch (action) {
          case 5: // copy
          case 1: // edit
            if (formResult.getForm().getObjectTip() == 2 && action == 1) {

              if (!GenericUtil.isEmpty(
                  formResult
                      .getForm()
                      .get_conversionList())) { // conversion olan bir form? o zaman sync olan
                // covnerted objeleri bul
                String inStr = "";
                for (W5Conversion cnv : formResult.getForm().get_conversionList())
                  if (GenericUtil.hasPartInside2(cnv.getActionTips(), action)
                      || cnv.getSynchOnUpdateFlag() != 0) { // synch varsa
                    inStr += "," + cnv.getConversionId();
                  }
                if (inStr.length() > 1) {
                  List<W5ConvertedObject> lco =
                      dao.find(
                          "from W5ConvertedObject x where x.projectUuid=? AND x.conversionId in ("
                              + inStr.substring(1)
                              + ") and x.srcTablePk=?",
                          projectId,
                          GenericUtil.uInt(requestParams, t.get_tableParamList().get(0).getDsc()));
                  if (!lco.isEmpty()) {
                    Map<Integer, List<W5ConvertedObject>> m = new HashMap();
                    formResult.setMapConvertedObject(m);
                    List<W5ConvertedObject> orphanCol = new ArrayList();
                    for (W5ConvertedObject co : lco) {
                      int dstTableId = 0;
                      for (W5Conversion cnv : formResult.getForm().get_conversionList())
                        if (cnv.getConversionId() == co.getConversionId()) {
                          dstTableId = cnv.getDstTableId();
                          break;
                        }
                      co.set_relatedRecord(
                          dao.findRecordParentRecords(
                              scd, dstTableId, co.getDstTablePk(), 10, false));
                      if (GenericUtil.isEmpty(co.get_relatedRecord())) {
                        orphanCol.add(co);
                      } else {
                        List<W5ConvertedObject> l = m.get(co.getConversionId());
                        if (l == null) {
                          l = new ArrayList();
                          m.put(co.getConversionId(), l);
                        }
                        l.add(co);
                      }
                    }
                    if (!orphanCol.isEmpty()) {
                      dao.removeAllObjects(orphanCol);
                    }
                  }
                }
              }
            }
            dao.loadFormTable(formResult);

            // eralp istedi, amaci freefieldlarda initial query deger verebilsin
            for (W5FormCellHelper fcx : formResult.getFormCellResults())
              if (fcx.getFormCell().getObjectDetailId()
                  == 0) { // bir tane freefield bulabilirse gir
                int initialQueryId = GenericUtil.uInt(requestParams, "_iqid");
                if (initialQueryId != 0) {
                  Map<String, Object> m = executeQuery2Map(scd, initialQueryId, requestParams);
                  if (m != null) {
                    for (W5FormCellHelper fc : formResult.getFormCellResults())
                      if (fc.getFormCell().getObjectDetailId() == 0) {
                        Object s =
                            m.get(
                                fc.getFormCell().getDsc().toLowerCase(FrameworkSetting.appLocale));
                        if (s != null) fc.setValue(s.toString());
                      }
                  }
                }
                for (W5FormCellHelper fcx2 : formResult.getFormCellResults())
                  if (fcx2.getFormCell().getObjectDetailId() == 0)
                    switch (fcx2.getFormCell().getInitialSourceTip()) {
                      case 0: // yok-sabit
                        fcx2.setValue(fcx2.getFormCell().getInitialValue());
                        break;
                      case 1: // request
                        fcx2.setValue(
                            formResult
                                .getRequestParams()
                                .get(fcx2.getFormCell().getInitialValue()));
                        break;
                      case 2:
                        Object o = formResult.getScd().get(fcx2.getFormCell().getInitialValue());
                        fcx2.setValue(o == null ? null : o.toString());
                        break;
                      case 3: // app_setting
                        fcx2.setValue(
                            FrameworkCache.getAppSettingStringValue(
                                formResult.getScd(), fcx2.getFormCell().getInitialValue()));
                        break;
                      case 4: // SQL
                        Object[] oz =
                            DBUtil.filterExt4SQL(
                                fcx2.getFormCell().getInitialValue(),
                                formResult.getScd(),
                                formResult.getRequestParams(),
                                null);
                        if (oz[1] != null && ((List) oz[1]).size() > 0) {
                          Map<String, Object> m =
                              dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
                          if (m != null && m.size() > 0)
                            fcx2.setValue(m.values().iterator().next().toString());
                        } else {
                          List l = dao.executeSQLQuery(oz[0].toString());
                          if (l != null && l.size() > 0 && l.get(0) != null)
                            fcx2.setValue(l.get(0).toString());
                        }
                        break;
                    }

                break;
              }
            for (W5FormCellHelper fcx : formResult.getFormCellResults())
              if (fcx.getFormCell().getFormCellId()
                  == 6060) { // mail sifre icin, baska seyler icin de kullanilabilir
                fcx.setValue("************");
              }

            if (action == 1) break;
          case 2: // insert
            if (action == 2) {
              dao.initializeForm(formResult, false);
              Map mq = null;
              boolean convb = false;
              if (requestParams.containsKey("_cnvId")
                  && requestParams.containsKey("_cnvTblPk")) { // conversion
                int conversionId = GenericUtil.uInt(requestParams, "_cnvId");
                int conversionTablePk = GenericUtil.uInt(requestParams, "_cnvTblPk");
                if (conversionId != 0 && conversionTablePk != 0) {
                  String prjId = FrameworkCache.getProjectId(scd, "707." + conversionId);
                  W5Conversion c =
                      (W5Conversion)
                          dao.getCustomizedObject(
                              "from W5Conversion t where t.conversionId=? AND t.projectUuid=?",
                              conversionId,
                              prjId,
                              "Conversion");
                  if (c == null || c.getDstFormId() != formResult.getFormId()) {
                    throw new IWBException(
                        "framework",
                        "Conversion",
                        conversionId,
                        null,
                        LocaleMsgCache.get2(0, (String) scd.get("locale"), "wrong_conversion"),
                        null);
                  }
                  if (c.getMaxNumofConversion() > 0) {
                    List l =
                        dao.find(
                            "select 1 from W5ConvertedObject c where c.conversionId=? AND c.projectUuid=? AND c.srcTablePk=?",
                            conversionId,
                            scd.get("projectId"),
                            conversionTablePk);
                    if (l.size() >= c.getMaxNumofConversion()) {
                      throw new IWBException(
                          "framework",
                          "Conversion",
                          conversionId,
                          null,
                          LocaleMsgCache.get2(
                                  0, (String) scd.get("locale"), "max_number_of_conversion_reached")
                              + " ("
                              + l.size()
                              + ")",
                          null);
                    }
                  }
                  W5Table srcTable = FrameworkCache.getTable(scd, c.getSrcTableId());
                  if (!GenericUtil.isEmpty(
                      srcTable
                          .get_approvalMap())) { // burda bir approval olabilir, kontrol etmek lazim
                    List l =
                        dao.find(
                            "select 1 from W5WorkflowRecord c where c.projectUuid=? AND c.finishedFlag=0 AND c.tableId=? AND c.tablePk=?",
                            scd.get("projectId"),
                            c.getSrcTableId(),
                            conversionTablePk);
                    if (!l.isEmpty()) {
                      throw new IWBException(
                          "framework",
                          "Conversion",
                          conversionId,
                          null,
                          LocaleMsgCache.get2(
                              0,
                              (String) scd.get("locale"),
                              "record_must_be_approved_before_conversion"),
                          null);
                    }
                  }
                  mq =
                      dao.interprateConversionTemplate(
                          c, formResult, conversionTablePk, true, false);
                  if (mq != null) {
                    convb = true;
                    formResult
                        .getOutputMessages()
                        .add(
                            LocaleMsgCache.get2(scd, "fw_converted_from")
                                + " <b><a href=# onclick=\"return mainPanel.loadTab({attributes:{href:'showForm?a=1&_tb_id="
                                + mq.get("_cnv_src_tbl_id")
                                + "&_tb_pk="
                                + mq.get("_cnv_src_tbl_pk")
                                + "&_fid="
                                + mq.get("_cnv_src_frm_id")
                                + "'}})\">"
                                + mq.get("_cnv_record")
                                + "</a></b> --> "
                                + LocaleMsgCache.get2(
                                    scd, mq.get("_cnv_name").toString() + " --> ?"));
                  }
                }
              } else if ((formId == 650 /* || formId==631*/)
                  && requestParams.containsKey(
                      "_fsmId")) { // formId=650/631 ise buna gore mail/sms hazirlanacak
                int fsmFrmId = GenericUtil.uInt(requestParams, "_fsmFrmId");
                W5FormResult fsmformResult =
                    getFormResult(formResult.getScd(), fsmFrmId, (short) 2, requestParams);
                int fsmId = GenericUtil.uInt(requestParams, "_fsmId");
                W5FormSmsMail fsm = fsmformResult.getForm().get_formSmsMailMap().get(fsmId);
                int fsmTableId = GenericUtil.uInt(requestParams, "_tableId");
                int fsmTablePk = GenericUtil.uInt(requestParams, "_tablePk");
                W5Email
                    email = /*formId==631 ? dao.interprateSmsTemplate(fsm, formResult.getScd(),requestParams,fsmTableId,fsmTablePk) : */
                        dao.interprateMailTemplate(
                            fsm, formResult.getScd(), requestParams, fsmTableId, fsmTablePk);
                if (formId == 650 && !GenericUtil.isEmpty(scd.get("mailSettingId"))) {
                  Map<String, Object> mapMailSign =
                      dao.runSQLQuery2Map(
                          "select x.EMAIL_SIGNATURE s from iwb.w5_object_mail_setting x where x.MAIL_SETTING_ID=${scd.mailSettingId} AND x.customization_id in (0,${scd.customizationId})",
                          scd,
                          requestParams,
                          null);
                  if (!GenericUtil.isEmpty(mapMailSign)
                      && !GenericUtil.isEmpty(mapMailSign.get("s"))) {
                    if (email == null || GenericUtil.isEmpty(email.getMailBody()))
                      mq.put(
                          "pmail_body",
                          FrameworkSetting.mailSeperator + (String) mapMailSign.get("s"));
                    else
                      mq.put(
                          "pmail_body",
                          email.getMailBody()
                              + FrameworkSetting.mailSeperator
                              + (String) mapMailSign.get("s"));
                  }
                }
                formResult
                    .getOutputMessages()
                    .add(
                        LocaleMsgCache.get2(scd, "fw_mail_converted_from")
                            + " <b><a href=# onclick=\"return mainPanel.loadTab({attributes:{href:'showForm?a=1&_tb_id="
                            + fsmTableId
                            + "&_tb_pk="
                            + fsmTablePk
                            + "&_fid="
                            + fsmFrmId
                            + "'}})\">"
                            + dao.getSummaryText4Record(scd, fsmTableId, fsmTablePk)
                            + "</a></b> --> "
                            + fsm.getDsc());
              } else if (requestParams.containsKey("_iqid")) {
                mq = executeQuery2Map(scd, GenericUtil.uInt(requestParams, "_iqid"), requestParams);
              }
              if (mq != null) {
                for (W5FormCellHelper fc : formResult.getFormCellResults()) {
                  Object s =
                      mq.get(fc.getFormCell().getDsc().toLowerCase(FrameworkSetting.appLocale));
                  if (s != null) {
                    fc.setValue(s.toString());
                    // conversion esnasinda, request'ten gelen degerleri simule ediyor, request'e
                    // manuel koyarak
                    if (convb
                        && (fc.getFormCell().getControlTip() == 0
                            || (fc.getFormCell().getNrdTip() == 2
                                && fc.getFormCell().getInitialSourceTip() == 1
                                && fc.getFormCell().getInitialValue() != null
                                && fc.getFormCell()
                                    .getInitialValue()
                                    .equals(fc.getFormCell().getDsc()))))
                      formResult.getRequestParams().put(fc.getFormCell().getDsc(), s.toString());
                  }
                }
              }
            } else { // copy
              if (formResult.getForm().getObjectTip() == 2)
                for (W5FormCellHelper fcr : formResult.getFormCellResults())
                  if (fcr.getFormCell().get_sourceObjectDetail() != null)
                    switch (((W5TableField) fcr.getFormCell().get_sourceObjectDetail())
                        .getCopySourceTip()) {
                      case 1: // request
                        fcr.setValue(
                            dao.getInitialFormCellValue(scd, fcr.getFormCell(), requestParams));
                        break;
                      case 6: // object_source:demek ki sorulacak degisecek mi diye?
                        break;
                      case 7: // object_source(readonly)
                        fcr.setHiddenValue("1");
                    }
              Map mq = null;
              if (requestParams.containsKey("_iqid")) {
                mq = executeQuery2Map(scd, GenericUtil.uInt(requestParams, "_iqid"), requestParams);
              }
              if (mq != null) {
                for (W5FormCellHelper fc : formResult.getFormCellResults())
                  if (fc.getValue() == null || fc.getValue().length() == 0) {
                    Object s =
                        mq.get(fc.getFormCell().getDsc().toLowerCase(FrameworkSetting.appLocale));
                    if (s != null) fc.setValue(s.toString());
                  }
              }
              action = 2;
              formResult.setAction(2);
            }

            break;
          default:
            throw new IWBException(
                "framework",
                "Form",
                formId,
                null,
                LocaleMsgCache.get2(0, (String) scd.get("locale"), "wrong_use_of_action")
                    + " ("
                    + action
                    + ")",
                null);
        }

      // form cell lookup load
      dao.loadFormCellLookups(
          formResult.getScd(),
          formResult.getFormCellResults(),
          formResult.getRequestParams(),
          FrameworkSetting.liveSyncRecord && formResult.getForm().getObjectTip() == 2
              ? formResult.getUniqueId()
              : null);

      for (W5FormCellHelper cr : formResult.getFormCellResults())
        if (cr.getFormCell().getControlTip()
            == 99) { // grid ise bunun icinde var mi editableFormCell
          W5Grid g = (W5Grid) cr.getFormCell().get_sourceObjectDetail();
          W5GridResult gr = new W5GridResult(g.getGridId());
          gr.setRequestParams(formResult.getRequestParams());
          gr.setScd(formResult.getScd());
          gr.setFormCellResultMap(new HashMap());

          for (W5GridColumn column : g.get_gridColumnList())
            if (column.get_formCell() != null) {
              gr.getFormCellResultMap()
                  .put(
                      column.get_formCell().getFormCellId(),
                      new W5FormCellHelper(column.get_formCell()));
            }

          gr.setGrid(g);
          if (formResult.getModuleGridMap() == null) formResult.setModuleGridMap(new HashMap());
          formResult.getModuleGridMap().put(g.getGridId(), gr);

          if (!gr.getFormCellResultMap().isEmpty())
            dao.loadFormCellLookups(
                gr.getScd(),
                new ArrayList(gr.getFormCellResultMap().values()),
                gr.getRequestParams(),
                null);
        }

      if (GenericUtil.uInt(formResult.getRequestParams().get("viewMode")) != 0)
        formResult.setViewMode(true);

      W5WorkflowStep approvalStep = null;
      if (formResult.getForm().getObjectTip() == 2
          && action == 1
          && /*formResult.getForm().get_sourceTable()*/ FrameworkCache.getTable(
                  scd, formResult.getForm().getObjectId())
              != null
          && formResult.getApprovalRecord() != null) {
        W5Workflow approval =
            FrameworkCache.getWorkflow(projectId, formResult.getApprovalRecord().getApprovalId());
        if (approval != null) {
          approvalStep =
              approval
                  .get_approvalStepMap()
                  .get(formResult.getApprovalRecord().getApprovalStepId())
                  .getNewInstance();
          if (approvalStep != null) {
            boolean canCancel =
                GenericUtil.hasPartInside2(approval.getAfterFinUpdateUserIds(), scd.get("userId"))
                        && formResult.getApprovalRecord().getApprovalActionTip() == 5
                        && formResult.getApprovalRecord().getApprovalStepId() == 998
                    ? true
                    : false;
            if (approvalStep.getApprovalStepId() != 901
                && approvalStep.getUpdatableFields() == null
                && !canCancel) formResult.setViewMode(true);
            formResult.setApprovalStep(approvalStep);
          }
        }
      }
      // normal/readonly/disabled show control/ozel kodlama
      int updatableFieldsCount = 0;
      for (W5FormCellHelper cr : formResult.getFormCellResults())
        if (cr.getFormCell().getControlTip() != 0
            && cr.getFormCell().getControlTip() != 13
            && cr.getFormCell().getControlTip() != 100) { // yok ve hidden ve buttondan baska
          W5TableField tf =
              formResult.getForm().getObjectTip() == 2
                      && cr.getFormCell().get_sourceObjectDetail() instanceof W5TableField
                  ? (W5TableField) cr.getFormCell().get_sourceObjectDetail()
                  : null;
          if (formResult.isViewMode()
              || cr.getHiddenValue() != null
              || (action == 1
                  && cr.getFormCell().getControlTip() == 31 /*ozel kodlama*/
                  && GenericUtil.uInt(cr.getFormCell().getLookupIncludedValues()) == 1
                  && !GenericUtil.hasPartInside(
                      cr.getFormCell().getLookupIncludedParams(),
                      "" + formResult.getScd().get("userId")))
              || cr.getFormCell().getNrdTip() != 0 // readonly/disabled
              || (approvalStep != null
                  && cr.getFormCell().get_sourceObjectDetail() != null
                  && !GenericUtil.hasPartInside(
                      approvalStep.getUpdatableFields(),
                      ""
                          + ((W5TableField) cr.getFormCell().get_sourceObjectDetail())
                              .getTableFieldId())) // approvalStepUpdatable Table Fields
              || (formResult.getForm().getObjectTip() == 2
                  && action == 1
                  && tf != null
                  && (tf.getCanUpdateFlag() == 0
                      || (tf.getAccessUpdateTip() != 0
                          && !GenericUtil.accessControl(
                              scd,
                              tf.getAccessUpdateTip(),
                              tf.getAccessUpdateRoles(),
                              tf.getAccessUpdateUsers())
                          && (GenericUtil.isEmpty(tf.getAccessUpdateUserFields())
                              || dao.accessUserFieldControl(
                                  t,
                                  tf.getAccessUpdateUserFields(),
                                  scd,
                                  requestParams,
                                  null)))))) { // ction=edit'te edti hakki yok

            String value = cr.getValue();
            cr.setHiddenValue(
                value == null || value.length() == 0 ? "_" : GenericUtil.stringToJS(value));
            switch (cr.getFormCell().getControlTip()) {
              case 5: // checkbox
                break;
              case 9:
              case 16:
              case 60: // remote Lookups
                //					cr.setHiddenValue(null);
                break;
              case 2: // date
                cr.setValue(GenericUtil.uDateStr(value));
                break;
              case 18: // timestamp
                cr.setValue(value);
                break;
              case 10:
              case 61: // autoselect, superboxselect-combo-query-advanced
                if (cr.getLookupQueryResult() != null
                    && cr.getLookupQueryResult().getData() != null
                    && cr.getLookupQueryResult().getData().size() != 0) {
                  cr.setValue((String) cr.getLookupQueryResult().getData().get(0)[0]);
                  //						cr.setHiddenValue(value);
                }
                break;
              case 6: // combo static
                //					cr.setHiddenValue(cr.getValue());
                if (cr.getLookupListValues() != null)
                  for (W5Detay d : (List<W5Detay>) cr.getLookupListValues()) {
                    if (d.getVal().equals(cr.getValue())) {
                      cr.setValue(
                          LocaleMsgCache.get2(
                              (Integer) scd.get("customizationId"),
                              (String) scd.get("locale"),
                              d.getDsc()));
                      break;
                    }
                  }
                break;
              case 7: // combo query
              case 23: // treecombo query
                //					cr.setHiddenValue(cr.getValue());
                if (cr.getLookupQueryResult() != null
                    && cr.getLookupQueryResult().getData() != null)
                  for (Object[] d : (List<Object[]>) cr.getLookupQueryResult().getData()) {
                    if (d[1] != null && d[1].toString().equals(cr.getValue())) {
                      cr.setValue(d[0].toString());
                      break;
                    }
                  }
                break;
              case 59: // superboxselect query
              case 15: // lov combo query
                String xval = "";
                if (cr.getLookupQueryResult() != null && cr.getValue() != null) {
                  Vector<String> vals = new Vector<String>();
                  for (String str : cr.getValue().split(",")) {
                    vals.add(str);
                  }
                  for (Object[] d : (List<Object[]>) cr.getLookupQueryResult().getData()) {
                    if (vals.contains(d[1].toString())) {
                      xval +=
                          cr.getLookupQueryResult()
                                      .getQuery()
                                      .get_queryFields()
                                      .get(0)
                                      .getPostProcessTip()
                                  == 2
                              ? LocaleMsgCache.get2(
                                      (Integer) scd.get("customizationId"),
                                      (String) scd.get("locale"),
                                      d[0].toString())
                                  + ", "
                              : d[0].toString() + ", ";
                    }
                  }
                }
                cr.setValue(xval.length() > 2 ? xval.substring(0, xval.length() - 2) : xval);
                break;

              case 58: // superboxselect static
              case 8: // lov combo static
                if (cr.getValue() != null) {
                  String xval2 = "";
                  String[] arr = cr.getValue().split(",");
                  for (int sindex = 0; sindex < arr.length; sindex++) {
                    int no = GenericUtil.getIndexNo(arr[sindex], cr.getLookupListValues());
                    W5LookUpDetay ld = (W5LookUpDetay) cr.getLookupListValues().get(no);
                    xval2 +=
                        LocaleMsgCache.get2(
                                (Integer) scd.get("customizationId"),
                                (String) scd.get("locale"),
                                ld.getDsc())
                            + " , ";
                  }
                  cr.setValue(xval2.substring(0, xval2.length() - 2));
                }
                break;
            }

          } else updatableFieldsCount++;
        } else if (cr.getFormCell().getControlTip()
            == 100) { // Buton ise butonun extra kodunda local mesajları cevirereceğiz inşallah
          cr.getFormCell()
              .setExtraDefinition(
                  GenericUtil.filterExt(
                          cr.getFormCell().getExtraDefinition(),
                          formResult.getScd(),
                          formResult.getRequestParams(),
                          null)
                      .toString());
        } else cr.setHiddenValue(null);

      if (formResult.getForm().getObjectTip() == 2) { // table ise
        if (updatableFieldsCount == 0) formResult.setViewMode(true);
        if (action == 1) { // eidt mode'da ise
          if (FrameworkSetting.alarm /* && !formResult.isViewMode()*/
              && !GenericUtil.isEmpty(
                  formResult.getForm().get_formSmsMailList())) { // readonly degil ise
            boolean alarm = false;
            for (W5FormSmsMail i : formResult.getForm().get_formSmsMailList())
              if (i.getAlarmFlag() != 0) {
                alarm = true;
                break;
              }
            if (alarm) {
              formResult.setFormAlarmList(
                  (List<W5FormSmsMailAlarm>)
                      dao.find(
                          "from W5FormSmsMailAlarm a where a.projectUuid=? AND a.insertUserId=? AND a.tableId=? AND a.tablePk=? ",
                          projectId,
                          scd.get("userId"),
                          formResult.getForm().getObjectId(),
                          GenericUtil.uInt(requestParams, t.get_tableParamList().get(0).getDsc())));
            }
          }
        }
      }

      GetFormTrigger.afterGetForm(formResult);

      return formResult;
    } catch (Exception e) {
      throw new IWBException(
          "framework",
          "Form",
          formId,
          null,
          "[40,"
              + formId
              + "]"
              + (formResult != null && formResult.getForm() != null
                  ? " " + formResult.getForm().getDsc()
                  : ""),
          e);
    }
  }

  private void accessControl4Table(
      Map<String, Object> scd,
      Map<String, String> requestParams,
      int tableId,
      int tablePk,
      int action,
      String prefix,
      W5FormResult formResult) {
    int formId = (formResult != null) ? formResult.getFormId() : 0;
    W5Table t = FrameworkCache.getTable(scd, tableId);
    String pkField = t.get_tableParamList().get(0).getDsc();
    Map requestParams2 = null;
    if (prefix == null) prefix = "";
    if (tablePk == 0 || requestParams.containsKey(pkField)) requestParams2 = requestParams;
    else {
      requestParams2 = new HashMap();
      requestParams2.putAll(requestParams);
      requestParams2.put(pkField + prefix, "" + tablePk);
    }

    W5Workflow approval = null;
    W5WorkflowRecord appRecord = null;
    W5WorkflowStep approvalStep = null;
    if ((action == 1 || action == 3)
        && t.get_approvalMap() != null
        && !t.get_approvalMap().isEmpty()) {
      List<W5WorkflowRecord> ll =
          dao.find(
              "from W5WorkflowRecord t where t.projectUuid=? AND t.tableId=? AND t.tablePk=?",
              scd.get("projectId"),
              tableId,
              tablePk);
      if (!ll.isEmpty()) {
        appRecord = ll.get(0);
        approval = FrameworkCache.getWorkflow(scd, appRecord.getApprovalId());
        if (appRecord.getApprovalStepId() > 0 && appRecord.getApprovalStepId() < 900)
          approvalStep =
              approval.get_approvalStepMap().get(appRecord.getApprovalStepId()).getNewInstance();
      }
    }
    if ((action == 1 || action == 3)
        && (t.getAccessDeleteTip() != 0
            && t.getAccessViewUserFields() != null
            && t.getAccessViewUserFields().length() > 0)) { // bu field'da
      if (dao.accessUserFieldControl(t, t.getAccessViewUserFields(), scd, requestParams2, prefix)) {
        throw new IWBException(
            "security",
            "Form",
            formId,
            null,
            LocaleMsgCache.get2(scd, "fw_guvenlik_onay_kontrol_goruntuleme"),
            null);
      }
    }
    boolean updatableUserFieldFlag = false;
    switch (action) {
      case 1: // update
        if (t.getAccessUpdateTip() != 0
            && t.getAccessUpdateUserFields() != null
            && t.getAccessUpdateUserFields().length() > 0) { // bu field'da
          if (dao.accessUserFieldControl(
              t, t.getAccessUpdateUserFields(), scd, requestParams2, prefix)) {
            if (formResult != null)
              formResult
                  .getOutputMessages()
                  .add(LocaleMsgCache.get2(scd, "fw_guvenlik_onay_kontrol_guncelleme"));
            if (formResult != null) formResult.setViewMode(true);
          } else updatableUserFieldFlag = true;
        }

        if (appRecord != null
            && (appRecord.getApprovalStepId() < 900
                || appRecord.getApprovalStepId() == 999)) { // eger bir approval sureci icindeyse
          if (appRecord.getApprovalStepId() == 999) {
            if (formResult != null)
              formResult
                  .getOutputMessages()
                  .add(
                      LocaleMsgCache.get2(
                          0, (String) scd.get("locale"), "fw_onay_kontrol_red_kayit_guncelleme"));
            if (formResult != null) formResult.setViewMode(true);
          } else if (!GenericUtil.accessControl(
                  scd,
                  appRecord.getAccessViewTip(),
                  appRecord.getAccessViewRoles(),
                  appRecord.getAccessViewUsers())
              || (approvalStep != null
                  && !GenericUtil.accessControl(
                      scd,
                      approvalStep.getAccessUpdateTip(),
                      approvalStep.getAccessUpdateRoles(),
                      approvalStep.getAccessUpdateUsers()))) {
            if (appRecord.getApprovalStepId() != 1
                || appRecord.getInsertUserId()
                    != (Integer)
                        scd.get("userId")) { // eger daha ilk asamada ve kaydi kaydeden bunu yapmaya
              // calisirsa, Veli Abi bu ne ya yapma yapma
              //						throw new PromisException("security","Workflow", appRecord.getApprovalId(),
              // null, "Onay süreci içerisinde bu kaydı Güncelleyemezsiniz", null);
              if (formResult != null)
                formResult
                    .getOutputMessages()
                    .add(
                        LocaleMsgCache.get2(
                            0, (String) scd.get("locale"), "fw_onay_kontrol_surec_devam"));
              if (formResult != null) formResult.setViewMode(true);
            }
          }
          if (formResult != null) {
            boolean canCancel =
                GenericUtil.hasPartInside2(approval.getAfterFinUpdateUserIds(), scd.get("userId"))
                        && formResult.getApprovalRecord().getApprovalActionTip() == 5
                        && formResult.getApprovalRecord().getApprovalStepId() == 998
                    ? true
                    : false;
            if (appRecord.getApprovalStepId() != 901
                && approvalStep.getUpdatableFields() == null
                && !canCancel) {
              formResult
                  .getOutputMessages()
                  .add(
                      LocaleMsgCache.get2(
                          0, (String) scd.get("locale"), "fw_onay_kontrol_surec_devam"));
              formResult.setViewMode(true);
            }
            formResult.setApprovalRecord(appRecord);
          }
        } else {
          // bu table'a update hakki var mi?
          if (!updatableUserFieldFlag
              && !GenericUtil.accessControl(
                  scd,
                  t.getAccessUpdateTip(),
                  t.getAccessUpdateRoles(),
                  t.getAccessUpdateUsers())) {
            //					throw new PromisException("security","Form", formId, null,
            // PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_guncelleme"), null);
            if (formResult != null)
              formResult
                  .getOutputMessages()
                  .add(
                      LocaleMsgCache.get2(
                          0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_guncelleme"));
            if (formResult != null) formResult.setViewMode(true);
          }

          if (t.getAccessTips() != null
              && t.getAccessTips().indexOf("0") > -1) { // kayit bazli kontrol var
            if (checkAccessRecordControlViolation(scd, 0, tableId, "" + tablePk)) {
              throw new IWBException(
                  "security",
                  "Form",
                  formId,
                  null,
                  LocaleMsgCache.get2(
                      0, (String) scd.get("locale"), "fw_guvenlik_kayit_bazli_kontrol_goruntuleme"),
                  null);
            }
          }
          if (t.getAccessTips() != null
              && t.getAccessTips().indexOf("1") > -1) { // kayit bazli kontrol var
            if (checkAccessRecordControlViolation(scd, 1, tableId, "" + tablePk)) {
              // throw new PromisException("security","Form", formId, null, "Kayıt Bazlı Kontrol:
              // Güncelleyemezsiniz", null);

              if (formResult != null)
                formResult
                    .getOutputMessages()
                    .add(
                        LocaleMsgCache.get2(
                            0,
                            (String) scd.get("locale"),
                            "fw_guvenlik_kayit_bazli_kontrol_guncelleme"));
              if (formResult != null) formResult.setViewMode(true);
            }
          }
          if (formResult != null) formResult.setApprovalRecord(appRecord);
        }
        break;
      case 2: // insert
        if (!GenericUtil.accessControl(
            scd, t.getAccessInsertTip(), t.getAccessInsertRoles(), t.getAccessInsertUsers())) {
          throw new IWBException(
              "security",
              "Form",
              formId,
              null,
              LocaleMsgCache.get2(
                  0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_kayit_ekleme"),
              null);
        }
        break;
      case 3: // delete
        if (t.getAccessDeleteTip() != 0
            && t.getAccessDeleteUserFields() != null
            && t.getAccessDeleteUserFields().length() > 0) { // bu field'da
          if (dao.accessUserFieldControl(
              t, t.getAccessDeleteUserFields(), scd, requestParams2, prefix))
            throw new IWBException(
                "security",
                "Form",
                formId,
                null,
                LocaleMsgCache.get2(
                    0, (String) scd.get("locale"), "fw_guvenlik_silinemez_kullanici_alan_kisit"),
                null);
        }

        if (appRecord != null) { // eger bir approval sureci icindeyse
          if (!GenericUtil.accessControl(
                  scd,
                  appRecord.getAccessViewTip(),
                  appRecord.getAccessViewRoles(),
                  appRecord.getAccessViewUsers())
              || !GenericUtil.accessControl(
                  scd,
                  approvalStep.getAccessDeleteTip(),
                  approvalStep.getAccessDeleteRoles(),
                  approvalStep.getAccessDeleteUsers())) {
            throw new IWBException(
                "security",
                "Workflow",
                appRecord.getApprovalId(),
                null,
                LocaleMsgCache.get2(
                    0,
                    (String) scd.get("locale"),
                    "fw_onay_sureci_icerisindeki_kaydi_silemezsiniz"),
                null);
          }
        } else {
          // bu table'a delete hakki var mi?
          if (!GenericUtil.accessControl(
              scd, t.getAccessDeleteTip(), t.getAccessDeleteRoles(), t.getAccessDeleteUsers())) {
            throw new IWBException(
                "security",
                "Form",
                formId,
                null,
                LocaleMsgCache.get2(
                    0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_silme"),
                null);
          }

          // kayit bazli kontrol var
          if (t.getAccessTips() != null && t.getAccessTips().indexOf("0") > -1) { // show
            if (checkAccessRecordControlViolation(scd, 0, tableId, "" + tablePk)) {
              throw new IWBException(
                  "security",
                  "Form",
                  formId,
                  null,
                  LocaleMsgCache.get2(
                      0, (String) scd.get("locale"), "fw_guvenlik_kayit_bazli_kontrol_goruntuleme"),
                  null);
            }
          }
          if (t.getAccessTips() != null && t.getAccessTips().indexOf("3") > -1) { // delete
            if (checkAccessRecordControlViolation(scd, 3, tableId, "" + tablePk)) {
              throw new IWBException(
                  "security",
                  "Form",
                  formId,
                  null,
                  LocaleMsgCache.get2(
                      0, (String) scd.get("locale"), "fw_guvenlik_kayit_bazli_kontrol_silme"),
                  null);
            }
          }
        }
    }
    if (formResult != null) formResult.setApprovalRecord(appRecord);
  }

  private void accessControl4FormTable(
      W5FormResult formResult, String paramSuffix) { // TODO: yukariya yonlendirilmesi lazim
    int formId = formResult.getFormId();
    int action = formResult.getAction();
    Map<String, Object> scd = formResult.getScd();
    Map<String, String> requestParams = formResult.getRequestParams();

    W5Table t =
        FrameworkCache.getTable(
            scd, formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();
    if (t == null) return; // TODO

    W5WorkflowRecord appRecord = null;
    W5WorkflowStep approvalStep = null;
    if ((action == 1 || action == 3)
        && t.get_approvalMap() != null
        && !t.get_approvalMap().isEmpty()) {
      List<W5WorkflowRecord> ll =
          dao.find(
              "from W5WorkflowRecord t where t.projectUuid=? AND t.tableId=? AND t.tablePk=?",
              scd.get("projectId"),
              t.getTableId(),
              GenericUtil.uInt(
                  requestParams.get(
                      t.get_tableParamList().get(0).getDsc()
                          + (paramSuffix != null ? paramSuffix : ""))));
      if (!ll.isEmpty()) {
        appRecord = ll.get(0);

        if (appRecord.getApprovalStepId() > 0 && appRecord.getApprovalStepId() < 900) {
          W5Workflow app = FrameworkCache.getWorkflow(scd, appRecord.getApprovalId());
          if (app != null)
            approvalStep =
                app.get_approvalStepMap().get(appRecord.getApprovalStepId()).getNewInstance();
        }
      }
    }
    // edit veya delete isleminde, accessViewControl by userFields control var mi?
    if ((action == 1 || action == 3)
        && (t.getAccessViewTip() != 0
            && t.getAccessViewUserFields() != null
            && !GenericUtil.accessControl(
                scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())
            && (t.getAccessViewUserFields() != null
                && t.getAccessViewUserFields().length() > 0
                && dao.accessUserFieldControl(
                    t,
                    t.getAccessViewUserFields(),
                    scd,
                    requestParams,
                    paramSuffix)))) { // bu field'da
      throw new IWBException(
          "security",
          "Form",
          formId,
          null,
          LocaleMsgCache.get2(
              0, (String) scd.get("locale"), "fw_guvenlik_onay_kontrol_goruntuleme"),
          null);
    }
    boolean updatableUserFieldFlag = false;
    boolean deletableUserFieldFlag = false;
    switch (action) {
      case 1: // update
        if (t.getAccessUpdateTip() != 0
            && t.getAccessUpdateUserFields() != null
            && t.getAccessUpdateUserFields().length() > 0
            && !GenericUtil.accessControl(
                scd,
                t.getAccessUpdateTip(),
                t.getAccessUpdateRoles(),
                t.getAccessUpdateUsers())) { // bu field'da
          if (dao.accessUserFieldControl(
              t, t.getAccessUpdateUserFields(), scd, requestParams, paramSuffix)) {
            formResult
                .getOutputMessages()
                .add(
                    LocaleMsgCache.get2(
                        0, (String) scd.get("locale"), "fw_guvenlik_onay_kontrol_guncelleme"));
            formResult.setViewMode(true);
          } else updatableUserFieldFlag = true;
        }

        if (appRecord != null
            && (appRecord.getApprovalStepId() < 900
                || appRecord.getApprovalStepId() == 999)) { // eger bir approval sureci icindeyse
          if (appRecord.getApprovalStepId() == 999) {
            formResult
                .getOutputMessages()
                .add(
                    LocaleMsgCache.get2(
                        0, (String) scd.get("locale"), "fw_onay_kontrol_red_kayit_guncelleme"));
            formResult.setViewMode(true);
          } else if (!GenericUtil.accessControl(
                  scd,
                  appRecord.getAccessViewTip(),
                  appRecord.getAccessViewRoles(),
                  appRecord.getAccessViewUsers())
              || (approvalStep != null
                  && ((!GenericUtil.accessControl(
                              scd,
                              approvalStep.getAccessUpdateTip(),
                              approvalStep.getAccessUpdateRoles(),
                              approvalStep.getAccessUpdateUsers())
                          && approvalStep.getAccessUpdateUserFields() == null)
                      || (approvalStep.getAccessUpdateUserFields() != null
                          && dao.accessUserFieldControl(
                              t,
                              approvalStep.getAccessUpdateUserFields(),
                              scd,
                              requestParams,
                              paramSuffix))))) {
            //					if(appRecord.getApprovalStepId()!=1 ||
            // appRecord.getInsertUserId()!=(Integer)scd.get("userId")){//eger daha ilk asamada ve
            // kaydi kaydeden bunu yapmaya calisirsa, Veli Abi bu ne ya yapma yapma
            //						throw new PromisException("security","Workflow", appRecord.getApprovalId(),
            // null, "Onay süreci içerisinde bu kaydı Güncelleyemezsiniz", null);
            formResult
                .getOutputMessages()
                .add(
                    LocaleMsgCache.get2(
                        0, (String) scd.get("locale"), "fw_onay_kontrol_surec_devam"));
            formResult.setViewMode(true);
            //					}
          }
          formResult.setApprovalRecord(appRecord);
        } else {
          // bu table'a update hakki var mi?
          if (!updatableUserFieldFlag
              && !GenericUtil.accessControl(
                  scd,
                  t.getAccessUpdateTip(),
                  t.getAccessUpdateRoles(),
                  t.getAccessUpdateUsers())) {
            //					throw new PromisException("security","Form", formId, null,
            // PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_guncelleme"), null);
            formResult
                .getOutputMessages()
                .add(
                    LocaleMsgCache.get2(
                        0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_guncelleme"));
            formResult.setViewMode(true);
          }

          if (t.getAccessTips() != null
              && t.getAccessTips().indexOf("0") > -1) { // kayit bazli kontrol var
            if (checkAccessRecordControlViolation(
                scd,
                0,
                t.getTableId(),
                requestParams.get(t.get_tableParamList().get(0).getDsc()))) {
              throw new IWBException(
                  "security",
                  "Form",
                  formId,
                  null,
                  LocaleMsgCache.get2(
                      0, (String) scd.get("locale"), "fw_guvenlik_kayit_bazli_kontrol_goruntuleme"),
                  null);
            }
          }
          if (t.getAccessTips() != null
              && t.getAccessTips().indexOf("1") > -1) { // kayit bazli kontrol var
            if (checkAccessRecordControlViolation(
                scd,
                1,
                t.getTableId(),
                requestParams.get(t.get_tableParamList().get(0).getDsc()))) {
              // throw new PromisException("security","Form", formId, null, "Kayıt Bazlı Kontrol:
              // Güncelleyemezsiniz", null);

              formResult
                  .getOutputMessages()
                  .add(
                      LocaleMsgCache.get2(
                          0,
                          (String) scd.get("locale"),
                          "fw_guvenlik_kayit_bazli_kontrol_guncelleme"));
              formResult.setViewMode(true);
            }
          }
          formResult.setApprovalRecord(appRecord);
        }
        break;
      case 2: // insert
        if (!GenericUtil.accessControl(
            scd,
            t.getAccessInsertTip(),
            t.getAccessInsertRoles(),
            t.getAccessInsertUsers())) { // Table access insert control
          throw new IWBException(
              "security",
              "Form",
              formId,
              null,
              LocaleMsgCache.get2(
                  0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_kayit_ekleme"),
              null);
        }

        break;
      case 3: // delete
        if (t.getAccessDeleteTip() != 0
            && !GenericUtil.isEmpty(t.getAccessDeleteUserFields())
            && !GenericUtil.accessControl(
                scd,
                t.getAccessDeleteTip(),
                t.getAccessDeleteRoles(),
                t.getAccessDeleteUsers())) { // bu field'da
          if (dao.accessUserFieldControl(
              t, t.getAccessDeleteUserFields(), scd, requestParams, paramSuffix)) {
            formResult
                .getOutputMessages()
                .add(
                    LocaleMsgCache.get2(
                        0, (String) scd.get("locale"), "fw_guvenlik_onay_kontrol_silme"));
            formResult.setViewMode(true);
          } else deletableUserFieldFlag = true;
        }
        /*
        if(t.getAccessDeleteTip()!=0 && t.getAccessDeleteUserFields()!=null && t.getAccessDeleteUserFields().length()>0){ // bu field'da
        	if(dao.accessUserFieldControl(t, t.getAccessDeleteUserFields(), scd, requestParams, paramSuffix))
        		throw new PromisException("security","Form", formId, null, PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_guvenlik_silinemez_kullanici_alan_kisit"), null);
        }*/

        if (appRecord != null) { // eger bir approval sureci icindeyse
          if (!GenericUtil.accessControl(
                  scd,
                  appRecord.getAccessViewTip(),
                  appRecord.getAccessViewRoles(),
                  appRecord.getAccessViewUsers())
              || (approvalStep != null
                  && !GenericUtil.accessControl(
                      scd,
                      approvalStep.getAccessDeleteTip(),
                      approvalStep.getAccessDeleteRoles(),
                      approvalStep.getAccessDeleteUsers()))) {
            throw new IWBException(
                "security",
                "Workflow",
                appRecord.getApprovalId(),
                null,
                LocaleMsgCache.get2(
                    0,
                    (String) scd.get("locale"),
                    "fw_onay_sureci_icerisindeki_kaydi_silemezsiniz"),
                null);
          }
        } else {
          // bu table'a delete hakki var mi?
          if (!deletableUserFieldFlag
              && !GenericUtil.accessControl(
                  scd,
                  t.getAccessDeleteTip(),
                  t.getAccessDeleteRoles(),
                  t.getAccessDeleteUsers())) {
            throw new IWBException(
                "security",
                "Form",
                formId,
                null,
                LocaleMsgCache.get2(
                    0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_silme"),
                null);
          } /*
            if(!PromisUtil.accessControl(scd, t.getAccessDeleteTip(), t.getAccessDeleteRoles(), t.getAccessDeleteUsers())){
            	throw new PromisException("security","Form", formId, null, PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_silme"), null);
            } */

          // kayit bazli kontrol var
          if (t.getAccessTips() != null && t.getAccessTips().indexOf("0") > -1) { // show
            if (checkAccessRecordControlViolation(
                scd,
                0,
                t.getTableId(),
                requestParams.get(t.get_tableParamList().get(0).getDsc()))) {
              throw new IWBException(
                  "security",
                  "Form",
                  formId,
                  null,
                  LocaleMsgCache.get2(
                      0, (String) scd.get("locale"), "fw_guvenlik_kayit_bazli_kontrol_goruntuleme"),
                  null);
            }
          }
          if (t.getAccessTips() != null && t.getAccessTips().indexOf("3") > -1) { // delete
            if (checkAccessRecordControlViolation(
                scd,
                3,
                t.getTableId(),
                requestParams.get(t.get_tableParamList().get(0).getDsc()))) {
              throw new IWBException(
                  "security",
                  "Form",
                  formId,
                  null,
                  LocaleMsgCache.get2(
                      0, (String) scd.get("locale"), "fw_guvenlik_kayit_bazli_kontrol_silme"),
                  null);
            }
          }
        }
    }
    formResult.setApprovalRecord(appRecord);
  }

  @SuppressWarnings({"unused", "unchecked"})
  private List<W5QueuedActionHelper> postForm4Table(
      W5FormResult formResult, String paramSuffix, Set<String> checkedParentRecords) {
    try {
      checkTenant(formResult.getScd());
      List<W5QueuedActionHelper> result = new ArrayList<W5QueuedActionHelper>();
      int formId = formResult.getFormId();
      int action = formResult.getAction();
      int realAction = action;
      Map<String, Object> scd = formResult.getScd();
      Map<String, String> requestParams = formResult.getRequestParams();

      PostFormTrigger.beforePostForm(formResult, dao, paramSuffix);
      boolean dev =
          scd.get("roleId") != null
              && (Integer) scd.get("roleId") == 0
              && GenericUtil.uInt(requestParams, "_dev") != 0;
      String projectId = dev ? FrameworkSetting.devUuid : (String) scd.get("projectId");
      W5Table t =
          FrameworkCache.getTable(
              projectId,
              formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();

      String schema = null;
      W5Workflow approval = null;
      W5WorkflowRecord appRecord = null;
      W5WorkflowStep approvalStep = null;
      boolean accessControlSelfFlag = true; // kendisi VEYA kendisi+master
      if (accessControlSelfFlag) {
        int outCnt = formResult.getOutputMessages().size();
        accessControl4FormTable(formResult, paramSuffix);
        if (formResult.isViewMode()) {
          throw new IWBException(
              "security",
              "Form",
              formId,
              null,
              formResult.getOutputMessages().size() > outCnt
                  ? formResult.getOutputMessages().get(outCnt)
                  : LocaleMsgCache.get2(
                      0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_guncelleme"),
              null);
        }
        if (FrameworkSetting.workflow) {
          appRecord = formResult.getApprovalRecord();
          if (appRecord != null) {
            approval =
                FrameworkCache.getWorkflow(
                    scd, appRecord.getApprovalId()); // dao.loadObject(W5Workflow.class,
            // formResult.getApprovalRecord().getApprovalId());
            boolean canCancel =
                GenericUtil.hasPartInside2(approval.getAfterFinUpdateUserIds(), scd.get("userId"))
                        && appRecord.getApprovalActionTip() == 5
                        && appRecord.getApprovalStepId() == 998
                    ? true
                    : false;
            approvalStep =
                approval.get_approvalStepMap().get(appRecord.getApprovalStepId()).getNewInstance();
            if (approvalStep != null
                && approvalStep.getApprovalStepId() != 901
                && approvalStep.getUpdatableFields() == null
                && !canCancel) {
              throw new IWBException(
                  "security",
                  "Form",
                  formId,
                  null,
                  LocaleMsgCache.get2(
                      0,
                      (String) scd.get("locale"),
                      "fw_onay_sureci_icerisinde_bu_kaydin_alanlarini_guncelleyemezsiniz"),
                  null);
            }
          }
        }
      }
      boolean mobile = GenericUtil.uInt(formResult.getScd().get("mobile")) != 0;
      int sourceStepId = -1;
      String ptablePk = null; // accessControl islemi icin
      String pcopyTablePk = null; // accessControl islemi icin
      Log5Feed feed = null;
      int feedTableId = 0, feedTablePk = 0;
      // once load edilmis eski objeye gerek var mi? wdiget'lar icin gerekir, condition olan yerler
      // icin de gerekebilir
      Map<String, Object> oldObj = null;

      if (action == 9 /*edit (if not insert)*/) {
        action = dao.checkIfRecordsExists(scd, requestParams, t) ? 1 : 2;
      }

      List<W5TableEvent> tla = FrameworkCache.getTableEvents(projectId, t.getTableId());
      /* tableTrigger Before Action start*/
      if (tla != null)
        extFormTrigger(
            formResult,
            new String[] {"_", "bu", "bi", "bd", "_", "bi"}[action],
            scd,
            requestParams,
            t,
            requestParams.get(t.get_tableParamList().get(0).getDsc() + paramSuffix),
            paramSuffix);
      /* end of tableTrigger*/

      switch (action) {
        case 1: // update
          ptablePk = requestParams.get(t.get_tableParamList().get(0).getDsc() + paramSuffix);
          if (GenericUtil.isEmpty(paramSuffix)) {
            formResult.setPkFields(new HashMap());
            formResult.getPkFields().put(t.get_tableParamList().get(0).getDsc(), ptablePk);
          }
          if (FrameworkSetting.workflow && accessControlSelfFlag) {

            if (appRecord == null
                && t.get_approvalMap()
                    != null) { // su anda bir onay icinde degil ve onay mekanizmasi var mi bunda?
              approval = t.get_approvalMap().get((short) 1); // action=1 for update

              if (approval != null
                  && approval.getActiveFlag() != 0) { // update approval mekanizmasi var
                Map<String, Object> advancedStepSqlResult = null;
                if (approval.getAdvancedBeginSql() != null
                    && approval.getAdvancedBeginSql().length() > 10) { // calisacak
                  advancedStepSqlResult =
                      dao.runSQLQuery2Map(approval.getAdvancedBeginSql(), scd, requestParams, null);
                  // donen bir cevap var, aktive_flag deger olarak var ve onun degeri 0 ise o zaman
                  // girmeyecek
                  if (advancedStepSqlResult != null
                      && advancedStepSqlResult.get("active_flag") != null
                      && GenericUtil.uInt(advancedStepSqlResult.get("active_flag"))
                          == 0) { // girmeyecek
                    approval = null; // approval olmayacak
                  }
                }
                if (approval != null) { // eger approval olacaksa
                  approvalStep = null;
                  if (approval.getApprovalFlowTip() == 0) { // simple
                    approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
                  } else { // complex
                    if (advancedStepSqlResult != null
                        && advancedStepSqlResult.get("approval_step_id") != null
                        && GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")) != 0)
                      approvalStep =
                          approval
                              .get_approvalStepMap()
                              .get(GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")));
                    else approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
                  }
                  if (approvalStep != null) { // step hazir
                    appRecord =
                        new W5WorkflowRecord(
                            scd,
                            approvalStep.getApprovalStepId(),
                            approval.getApprovalId(),
                            formResult.getForm().getObjectId(),
                            (short) 0,
                            approvalStep.getReturnFlag());
                    boolean bau =
                        advancedStepSqlResult != null
                            && advancedStepSqlResult.get("approval_users") != null;
                    appRecord.setApprovalUsers(
                        bau
                            ? (String) advancedStepSqlResult.get("approval_users")
                            : approvalStep.getApprovalUsers());
                    appRecord.setApprovalRoles(
                        bau
                            ? (String) advancedStepSqlResult.get("approval_roles")
                            : approvalStep.getApprovalRoles());
                    boolean bavt =
                        advancedStepSqlResult != null
                            && advancedStepSqlResult.get("access_view_tip") != null;
                    appRecord.setAccessViewTip(
                        bavt
                            ? (short) GenericUtil.uInt(advancedStepSqlResult.get("access_view_tip"))
                            : approvalStep.getAccessViewTip());
                    appRecord.setAccessViewRoles(
                        bavt
                            ? (String) advancedStepSqlResult.get("access_view_roles")
                            : approvalStep.getAccessViewRoles());
                    appRecord.setAccessViewUsers(
                        bavt
                            ? (String) advancedStepSqlResult.get("access_view_users")
                            : approvalStep.getAccessViewUsers());
                    if (appRecord.getAccessViewTip() != 0
                        && !GenericUtil.hasPartInside2(
                            appRecord.getAccessViewUsers(),
                            scd.get("userId"))) // goruntuleme kisiti var ve kendisi goremiyorsa,
                      // kendisini de ekle
                      appRecord.setAccessViewUsers(
                          appRecord.getAccessViewUsers() != null
                              ? appRecord.getAccessViewUsers() + "," + scd.get("userId")
                              : scd.get("userId").toString());
                  } else {
                    throw new IWBException(
                        "framework",
                        "Workflow",
                        approval.getApprovalId(),
                        null,
                        LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_hatali_onay_tanimi"),
                        null);
                  }
                }
              }

              if (approval == null) {
                approval =
                    t.get_approvalMap()
                        .get((short) 2); // action=2 insert mode ile başlatılıyor ayrı bi şov tabii
                // düzeltilmesi lazım
                if (approval != null
                    && approval.getApprovalRequestTip() == 2
                    && approval.getManualDemandStartAppFlag() == 0) approval = null;
              }

              if (appRecord == null
                  && t.get_approvalMap() != null
                  && formResult.getRequestParams().get("_aa") != null
                  && GenericUtil.uInt(formResult.getRequestParams().get("_aa"))
                      == -1) { // Insertle ilgili bir onay başlatma isteği var ve böylece artık
                // 901'e giriyor
                Map<String, Object> advancedStepSqlResult = null;
                if (approval.getAdvancedBeginSql() != null
                    && approval.getAdvancedBeginSql().length() > 10) {
                  Object[] oz =
                      DBUtil.filterExt4SQL(
                          approval.getAdvancedBeginSql(), scd, requestParams, null);
                  advancedStepSqlResult = dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
                  if (advancedStepSqlResult != null) {
                    if (advancedStepSqlResult.get("active_flag") != null
                        && GenericUtil.uInt(advancedStepSqlResult.get("active_flag")) == 0)
                      approval = null;
                    else {
                      approvalStep = new W5WorkflowStep();
                      approvalStep.setApprovalUsers("" + (Integer) scd.get("userId"));
                      approvalStep.setApprovalStepId(901);
                    }
                    if (advancedStepSqlResult.get("error_msg") != null)
                      throw new IWBException(
                          "security",
                          "Workflow",
                          approval.getApprovalId(),
                          null,
                          (String) advancedStepSqlResult.get("error_msg"),
                          null);
                  }
                } else {
                  approvalStep = new W5WorkflowStep();
                  approvalStep.setApprovalRoles(approval.getManualAppRoleIds());
                  approvalStep.setApprovalUsers(approval.getManualAppUserIds());
                  if (approval.getManualAppTableFieldIds() != null) {
                  } else if (approvalStep.getApprovalUsers() == null)
                    approvalStep.setApprovalUsers("" + (Integer) scd.get("userId"));

                  approvalStep.setApprovalStepId(901);
                }

                if (approvalStep != null) { // step hazir
                  appRecord =
                      new W5WorkflowRecord(
                          scd,
                          approvalStep.getApprovalStepId(),
                          approval.getApprovalId(),
                          formResult.getForm().getObjectId(),
                          (short) 0,
                          approvalStep.getReturnFlag());
                  appRecord.setApprovalUsers(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("approval_users") != null
                          ? (String) advancedStepSqlResult.get("approval_users")
                          : approvalStep.getApprovalUsers());
                  appRecord.setApprovalRoles(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("approval_roles") != null
                          ? (String) advancedStepSqlResult.get("approval_roles")
                          : approvalStep.getApprovalRoles());
                  appRecord.setAccessViewTip(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("access_view_tip") != null
                          ? (short) GenericUtil.uInt(advancedStepSqlResult.get("access_view_tip"))
                          : approvalStep.getAccessViewTip());
                  appRecord.setAccessViewRoles(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("access_view_roles") != null
                          ? (String) advancedStepSqlResult.get("access_view_roles")
                          : approvalStep.getAccessViewRoles());
                  appRecord.setAccessViewUsers(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("access_view_users") != null
                          ? (String) advancedStepSqlResult.get("access_view_users")
                          : approvalStep.getAccessViewUsers());
                  if (appRecord.getAccessViewTip() != 0
                      && !GenericUtil.hasPartInside2(
                          appRecord.getAccessViewUsers(),
                          scd.get("userId"))) // goruntuleme kisiti var ve kendisi goremiyorsa,
                    // kendisini de ekle
                    appRecord.setAccessViewUsers(
                        appRecord.getAccessViewUsers() != null
                            ? appRecord.getAccessViewUsers() + "," + scd.get("userId")
                            : scd.get("userId").toString());
                } else {
                  throw new IWBException(
                      "framework",
                      "Workflow",
                      formId,
                      null,
                      LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_hatali_onay_tanimi"),
                      null);
                }
              }
            } else {
              if (appRecord != null) {
                String noUpdateVersionNo =
                    FrameworkCache.getAppSettingStringValue(scd, "approval_no_update_version_no");
                if (GenericUtil.isEmpty(noUpdateVersionNo)
                    || !GenericUtil.hasPartInside(noUpdateVersionNo, "" + t.getTableId())) {
                  appRecord.setVersionNo(appRecord.getVersionNo() + 1);
                  dao.updateObject(appRecord);
                }
                if (FrameworkSetting.liveSyncRecord)
                  formResult.addSyncRecord(
                      new W5SynchAfterPostHelper(
                          (String) scd.get("projectId"),
                          392 /*w5_approval_record*/,
                          "" + appRecord.getApprovalRecordId(),
                          (Integer) scd.get("userId"),
                          requestParams.get(".w"),
                          (short) 1));
                appRecord = null; // bu kaydedilmeyecek
              }
            }
          }

          dao.updateFormTable(formResult, paramSuffix);

          //
          //	if(formResult.getErrorMap().isEmpty())FrameworkCache.removeTableCacheValue(t.getCustomizationId(), t.getTableId(),GenericUtil.uInt(ptablePk));//caching icin

          if (FrameworkSetting.workflow
              && accessControlSelfFlag
              && formResult.getErrorMap().isEmpty()
              && appRecord != null) { // aproval baslanmis
            int tablePk =
                GenericUtil.uInt(
                    formResult
                        .getOutputFields()
                        .get(
                            /*formResult.getForm().get_sourceTable()*/ FrameworkCache.getTable(
                                    scd, formResult.getForm().getObjectId())
                                .get_tableFieldList()
                                .get(0)
                                .getDsc()));
            if (tablePk == 0) {
              tablePk = GenericUtil.uInt(ptablePk);
            }
            appRecord.setTablePk(tablePk);
            String summaryText = dao.getSummaryText4Record(scd, t.getTableId(), tablePk);
            appRecord.setDsc(GenericUtil.uStrMax(summaryText, 512));
            saveObject(appRecord);
            if (FrameworkSetting.liveSyncRecord)
              formResult.addSyncRecord(
                  new W5SynchAfterPostHelper(
                      (String) scd.get("projectId"),
                      392 /*w5_approval_record*/,
                      "" + appRecord.getApprovalRecordId(),
                      (Integer) scd.get("userId"),
                      requestParams.get(".w"),
                      (short) 2));
            Log5WorkflowRecord logRecord = new Log5WorkflowRecord();
            logRecord.setApprovalActionTip(
                (short) 0); // start, approve, return, reject, time_limit_cont ,final_approve
            logRecord.setUserId((Integer) scd.get("userId"));
            logRecord.setApprovalRecordId(appRecord.getApprovalRecordId());
            logRecord.setApprovalStepId(sourceStepId);
            logRecord.setApprovalId(appRecord.getApprovalId());
            saveObject(logRecord);
            formResult
                .getOutputMessages()
                .add(
                    t.get_approvalMap().get((short) 2).getDsc()
                        + " "
                        + LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_onaya_sunulmustur")
                        + " ("
                        + summaryText
                        + ")");

            // Mail ve SMS işlemleri _aa=-1 gelirse //

            String appRecordUserList = null;
            String appRecordRoleList = null;
            String mesajBody = "";

            List<Object> notificationUsers =
                dao.executeSQLQuery(
                    "select distinct gu.user_id from iwb.w5_User gu where gu.customization_id= ? and gu.user_Id != ? and (gu.user_Id in (select ur.user_Id from iwb.w5_User_Role ur where ur.role_id in ((select x.satir from table(tool_parse_numbers(?,\',\'))x))) or gu.user_id in ((select x.satir from table(tool_parse_numbers(?,\',\'))x)))",
                    scd.get("customizationId"),
                    scd.get("userId"),
                    appRecord.getApprovalRoles(),
                    appRecord.getApprovalUsers());
            if (notificationUsers != null)
              for (Object o : notificationUsers) {
                dao.saveObject2(
                    new Log5Notification(
                        scd,
                        GenericUtil.uInt(o),
                        (short)
                            (appRecord.getApprovalStepId() == 901
                                    && approval.getApprovalFlowTip() == 3
                                ? 903
                                : 6),
                        approval.getTableId(),
                        appRecord.getTablePk(),
                        GenericUtil.uInt(scd.get("userId")),
                        null,
                        1),
                    scd);
              }

            if ((approval != null
                    && approval.getActiveFlag() != 0
                    && ((appRecord.getApprovalStepId() < 900
                        && (approvalStep.getSendMailOnEnterStepFlag() != 0)))
                || appRecord.getApprovalStepId() > 900)) {
              appRecordUserList = appRecord.getApprovalUsers();
              appRecordRoleList = appRecord.getApprovalRoles();
              List<String> emailList = null;
              if (approvalStep.getSendMailOnEnterStepFlag() != 0)
                emailList =
                    dao.executeSQLQuery(
                        "select gu.email from iwb.w5_user gu where gu.customization_Id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.email from iwb.w5_user gu where gu.customizationId=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
                        scd.get("customizationId"),
                        appRecordUserList,
                        scd.get("userId"),
                        scd.get("customizationId"),
                        appRecordRoleList,
                        scd.get("userId"));

              /*						if(emailList == null && (approval.getApprovalStrategyTip() == 1 || approval.getSendMailOnManualStepFlag() == 1)){ // Eğer manual başlat varsa
              	appRecordUserList = approval.getManualAppUserIds();
              	appRecordRoleList = approval.getManualAppRoleIds();
              	emailList = dao.executeSQLQuery("select gu.email from iwb.w5_user gu where gu.customization_id= ? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.email from iwb.w5_user gu where gu.customization_id= ? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",scd.get("customizationId"),appRecordUserList,scd.get("userId"),scd.get("customizationId"),appRecordRoleList,scd.get("userId"));
              }*/

              if (emailList != null && emailList.size() > 0) {
                String pemailList = "";
                Object[] m = emailList.toArray();
                for (int i = 0; i < m.length; i++) pemailList += "," + m[i];
                pemailList = pemailList.substring(1);

                int mail_setting_id = GenericUtil.uInt((Object) scd.get("mailSettingId"));
                if (mail_setting_id == 0)
                  mail_setting_id = FrameworkCache.getAppSettingIntValue(scd, "default_outbox_id");

                W5ObjectMailSetting oms =
                    (W5ObjectMailSetting)
                        dao.find(
                                "from W5ObjectMailSetting x where x.customizationId=? and x.mailSettingId=?",
                                (Integer) scd.get("customizationId"),
                                mail_setting_id)
                            .get(0);
                if (appRecord.getApprovalStepId() == 901) {
                  mesajBody =
                      "'"
                          + scd.get("completeName")
                          + "' "
                          + LocaleMsgCache.get2(
                              0, (String) scd.get("locale"), "onay_surecini_baslatmanizi_istiyor");
                } else {
                  mesajBody =
                      "'"
                          + scd.get("completeName")
                          + "' "
                          + LocaleMsgCache.get2(
                              0, (String) scd.get("locale"), "tarafindan_onaya_sunulmustur");
                }

                if (oms != null) {
                  W5Email email =
                      new W5Email(
                          pemailList,
                          null,
                          null,
                          t.get_approvalMap().get((short) 2).getDsc(),
                          " (" + summaryText + ") " + mesajBody,
                          null); // mail_keep_body_original ?
                  email.set_oms(oms);
                  String sonuc = MailUtil.sendMail(scd, email);
                  if (FrameworkCache.getAppSettingIntValue(0, "mail_debug_flag") != 0) {
                    if (sonuc != null) { // basarisiz, queue'ye at//
                      System.out.println(
                          LocaleMsgCache.get2(
                              0,
                              (String) scd.get("locale"),
                              "onay_mekanizmasi_akisinda_mail_gonderilemedi"));
                    } else {
                      System.out.println(
                          LocaleMsgCache.get2(
                              0,
                              (String) scd.get("locale"),
                              "onay_mekanizmasi_akisinda_mail_gonderildi"));
                    }
                  }
                }
              }
            }

            if ((approval != null
                    && approval.getActiveFlag() != 0
                    && ((appRecord.getApprovalStepId() < 900
                        && approvalStep.getSendSmsOnEnterStepFlag() != 0))
                || appRecord.getApprovalStepId() > 900)) {
              if (FrameworkCache.getAppSettingIntValue(scd, "sms_flag") != 0) {
                appRecordUserList = appRecord.getApprovalUsers();
                appRecordRoleList = appRecord.getApprovalRoles();

                List<String> gsmList =
                    dao.executeSQLQuery(
                        "select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
                        scd.get("customizationId"),
                        appRecordUserList,
                        scd.get("userId"),
                        scd.get("customizationId"),
                        appRecordRoleList,
                        scd.get("userId"));

                /*							if(gsmList == null && (approval.getApprovalStrategyTip() == 1 || approval.getSendSmsOnManualStepFlag() == 1)){ // Eğer manuel başlat varsa
                							appRecordUserList = approval.getManualAppUserIds();
                							appRecordRoleList = approval.getManualAppRoleIds();
                							gsmList = dao.executeSQLQuery("select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",scd.get("customizationId"),appRecordUserList,scd.get("userId"),scd.get("customizationId"),appRecordRoleList,scd.get("userId"));
                						}
                */
                if (gsmList.size() > 0) {
                  String phoneNumber = "";
                  for (String gsm : gsmList)
                    phoneNumber = phoneNumber + (GenericUtil.isEmpty(phoneNumber) ? "" : ",") + gsm;

                  sendSms(
                      GenericUtil.uInt(scd.get("customizationId")),
                      GenericUtil.uInt(scd.get("userId")),
                      phoneNumber,
                      t.get_approvalMap().get((short) 2).getDsc()
                          + " ("
                          + summaryText
                          + ") "
                          + mesajBody,
                      392,
                      appRecord.getApprovalRecordId());
                }
              }
            }
          }
          break;
        case 5: // copy
        case 2: // insert
          if (FrameworkSetting.workflow
              && accessControlSelfFlag
              && t.get_approvalMap() != null) { // onay mekanizmasi var mi bunda?
            approval = t.get_approvalMap().get((short) 2); // action=2 for insert
            if (approval != null
                && approval.getActiveFlag() != 0
                && approval.getApprovalRequestTip()
                    >= 1) { // insert approval mekanizmasi var ve automatic
              Map<String, Object> advancedStepSqlResult = null;
              switch (approval.getApprovalRequestTip()) { // eger approval olacaksa
                case 1: // automatic approval
                  if (approval.getAdvancedBeginSql() != null
                      && approval.getAdvancedBeginSql().length() > 10) { // calisacak
                    Object[] oz =
                        DBUtil.filterExt4SQL(
                            approval.getAdvancedBeginSql(), scd, requestParams, null);
                    advancedStepSqlResult =
                        dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
                    // donen bir cevap var, aktive_flag deger olarak var ve onun degeri 0 ise o
                    // zaman girmeyecek
                    if (advancedStepSqlResult != null) {
                      if (advancedStepSqlResult.get("active_flag") != null
                          && GenericUtil.uInt(advancedStepSqlResult.get("active_flag"))
                              == 0) // girmeyecek
                      approval = null; // approval olmayacak
                      if (advancedStepSqlResult.get("error_msg") != null) // girmeyecek
                      throw new IWBException(
                            "security",
                            "Workflow",
                            approval.getApprovalId(),
                            null,
                            (String) advancedStepSqlResult.get("error_msg"),
                            null);
                    }
                  }
                  approvalStep = null;
                  switch (approval.getApprovalFlowTip()) { // simple
                    case 0: // basit onay
                      approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
                      break;
                    case 1: // complex onay
                      if (advancedStepSqlResult != null
                          && advancedStepSqlResult.get("approval_step_id") != null
                          && GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")) != 0)
                        approvalStep =
                            approval
                                .get_approvalStepMap()
                                .get(
                                    GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")))
                                .getNewInstance();
                      else approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
                      break;
                    case 2: // hierarchical onay
                      int mngUserId = GenericUtil.uInt(scd.get("mngUserId"));
                      if (mngUserId != 0) {
                        approvalStep = new W5WorkflowStep();
                        approvalStep.setApprovalUsers("" + mngUserId);
                        approvalStep.setApprovalStepId(902);
                        approvalStep.setSendMailOnEnterStepFlag(approval.getSendMailFlag());
                        approvalStep.setSendSmsOnEnterStepFlag(approval.getSendSmsFlag());
                      } else { // direk duz approval kismine geciyor:TODO
                        if (approval.get_approvalStepList() != null
                            && !approval.get_approvalStepList().isEmpty())
                          approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
                        else approvalStep = null;
                      }

                      break;
                  }

                  break;
                case 2: // manual after action
                  if (approval.getManualDemandStartAppFlag() == 0
                      || (approval.getManualDemandStartAppFlag() == 1
                          && GenericUtil.uInt(formResult.getRequestParams().get("_aa"))
                              == -1)) { // Eğer onay mekanizması elle başlatılmayacaksa burada 901'e
                    // girmesi sağlanır
                    if (approval.getAdvancedBeginSql() != null
                        && approval.getAdvancedBeginSql().length() > 10) { // calisacak
                      Object[] oz =
                          DBUtil.filterExt4SQL(
                              approval.getAdvancedBeginSql(), scd, requestParams, null);
                      advancedStepSqlResult =
                          dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
                      // donen bir cevap var, aktive_flag deger olarak var ve onun degeri 0 ise o
                      // zaman girmeyecek
                      if (advancedStepSqlResult != null) {
                        if (advancedStepSqlResult.get("active_flag") != null
                            && GenericUtil.uInt(advancedStepSqlResult.get("active_flag"))
                                == 0) // girmeyecek
                        approval = null; // approval olmayacak
                        else {
                          approvalStep = new W5WorkflowStep();
                          if (approval.getManualAppUserIds() == null) {
                            approvalStep.setApprovalUsers("" + (Integer) scd.get("userId"));
                          } else {
                            approvalStep.setApprovalUsers(approval.getManualAppUserIds());
                          }
                          approvalStep.setApprovalRoles(approval.getManualAppRoleIds());
                          approvalStep.setApprovalStepId(901); // wait for starting approval
                          approvalStep.setSendMailOnEnterStepFlag(approval.getSendMailFlag());
                          approvalStep.setSendSmsOnEnterStepFlag(approval.getSendSmsFlag());
                        }
                        if (advancedStepSqlResult.get("error_msg") != null) // girmeyecek
                        throw new IWBException(
                              "security",
                              "Workflow",
                              approval.getApprovalId(),
                              null,
                              (String) advancedStepSqlResult.get("error_msg"),
                              null);
                      }
                    } else {
                      approvalStep = new W5WorkflowStep();
                      //								if(approval.getDynamicStepFlag()!=0))
                      approvalStep.setApprovalRoles(approval.getManualAppRoleIds());
                      approvalStep.setApprovalUsers(approval.getManualAppUserIds());
                      if (approval.getManualAppTableFieldIds()
                          != null) { // TODO: burda fieldlardan userlar alinacak ve approvalUsersa
                        // eklenecek
                        // Object o =
                        // formResult.getOutputFields().get(t.get_tableParamList().get(0).getDsc().substring(1));
                        // dao.getUsersFromUserFields(PromisCache.getTable(scd,
                        // approval.getTableId()), approval.getManualAppTableFieldIds(), scd,
                        // o.toString());
                      } else if (approvalStep.getApprovalUsers() == null) // TODO: yanlis
                      approvalStep.setApprovalUsers("" + (Integer) scd.get("userId"));

                      approvalStep.setApprovalStepId(901); // wait for starting approval
                      approvalStep.setSendMailOnEnterStepFlag(approval.getSendMailFlag());
                      approvalStep.setSendSmsOnEnterStepFlag(approval.getSendSmsFlag());
                    }
                  }
                  break;
              }
              if (approval != null
                  && (approval.getManualDemandStartAppFlag() == 0
                      || (approval.getManualDemandStartAppFlag() == 1
                          && GenericUtil.uInt(formResult.getRequestParams().get("_aa"))
                              == -1))) { // Onay Mek Başlat
                if (approvalStep != null) { // step hazir
                  //								if(approval.getApprovalStrategyTip()==0)schema =
                  // FrameworkCache.getAppSettingStringValue(scd, "approval_schema");
                  appRecord = new W5WorkflowRecord((String)scd.get("projectId"));
                  appRecord.setApprovalId(approval.getApprovalId());
                  appRecord.setApprovalStepId(approvalStep.getApprovalStepId());
                  appRecord.setApprovalActionTip(
                      (short) 0); // start,approve,return,reject,time_limit_exceed
                  appRecord.setTableId(formResult.getForm().getObjectId());
                  appRecord.setReturnFlag(approvalStep.getReturnFlag());
                  appRecord.setApprovalUsers(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("approval_users") != null
                          ? (String) advancedStepSqlResult.get("approval_users")
                          : approvalStep.getApprovalUsers());
                  appRecord.setApprovalRoles(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("approval_roles") != null
                          ? (String) advancedStepSqlResult.get("approval_roles")
                          : approvalStep.getApprovalRoles());
                  appRecord.setAccessViewTip(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("access_view_tip") != null
                          ? (short) GenericUtil.uInt(advancedStepSqlResult.get("access_view_tip"))
                          : approvalStep.getAccessViewTip());
                  appRecord.setAccessViewRoles(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("access_view_roles") != null
                          ? (String) advancedStepSqlResult.get("access_view_roles")
                          : approvalStep.getAccessViewRoles());
                  appRecord.setAccessViewUsers(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("access_view_users") != null
                          ? (String) advancedStepSqlResult.get("access_view_users")
                          : approvalStep.getAccessViewUsers());
                  if (appRecord.getAccessViewTip() != 0
                      && !GenericUtil.hasPartInside2(
                          appRecord.getAccessViewUsers(),
                          scd.get("userId"))) // goruntuleme kisiti var ve kendisi goremiyorsa,
                    // kendisini de ekle
                    appRecord.setAccessViewUsers(
                        appRecord.getAccessViewUsers() != null
                            ? appRecord.getAccessViewUsers() + "," + scd.get("userId")
                            : scd.get("userId").toString());
                  appRecord.setInsertUserId((Integer) scd.get("userId"));
                  appRecord.setVersionUserId((Integer) scd.get("userId"));
                  //								appRecord.setCustomizationId((Integer)scd.get("customizationId"));
                  appRecord.setHierarchicalLevel(0);
                } else {
                  throw new IWBException(
                      "framework",
                      "Workflow",
                      formId,
                      null,
                      LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_hatali_onay_tanimi"),
                      null);
                }
              }
            }
          }
          if (action == 2) // 2:insert
          dao.insertFormTable(formResult, paramSuffix);
          else { // 5:copy
            dao.copyFormTable(
                formResult, schema, paramSuffix, paramSuffix != null && paramSuffix.length() > 0);
            pcopyTablePk = requestParams.get(t.get_tableParamList().get(0).getDsc());
            action = 2;
          }

          if (formResult.getOutputFields() != null && !formResult.getOutputFields().isEmpty()) {
            Object o =
                formResult
                    .getOutputFields()
                    .get(t.get_tableParamList().get(0).getDsc().substring(1));
            // user fieldlardan gelen alanlar.
            /*if(approval.getManualAppTableFieldIds() != null){
            	dao.getUsersFromUserFields(PromisCache.getTable(scd, approval.getTableId()), approval.getManualAppTableFieldIds(), scd, o.toString());
            }*/
            if (o != null) {
              ptablePk = o.toString();
              requestParams.put(t.get_tableParamList().get(0).getDsc(), ptablePk);
              if (!GenericUtil.isEmpty(paramSuffix))
                requestParams.put(t.get_tableParamList().get(0).getDsc() + paramSuffix, ptablePk);
            }
          }

          if (FrameworkSetting.workflow
              && accessControlSelfFlag
              && formResult.getErrorMap().isEmpty()
              && appRecord != null) { // aproval baslanmis
            int tablePk =
                GenericUtil.uInt(
                    formResult
                        .getOutputFields()
                        .get(
                            /*formResult.getForm().get_sourceTable()*/ FrameworkCache.getTable(
                                    scd, formResult.getForm().getObjectId())
                                .get_tableFieldList()
                                .get(0)
                                .getDsc()));
            appRecord.setTablePk(tablePk);
            String summaryText = dao.getSummaryText4Record(scd, t.getTableId(), tablePk);
            appRecord.setDsc(summaryText);
            saveObject(appRecord);
            if (FrameworkSetting.liveSyncRecord)
              formResult.addSyncRecord(
                  new W5SynchAfterPostHelper(
                      (String) scd.get("projectId"),
                      392 /*w5_approval_record*/,
                      "" + appRecord.getApprovalRecordId(),
                      (Integer) scd.get("userId"),
                      requestParams.get(".w"),
                      (short) 1));
            Log5WorkflowRecord logRecord = new Log5WorkflowRecord();
            logRecord.setApprovalActionTip(
                (short)
                    0); // start, approve, return, reject, time_limit_cont ,final_approve, deleted
            logRecord.setUserId((Integer) scd.get("userId"));
            logRecord.setApprovalRecordId(appRecord.getApprovalRecordId());
            logRecord.setApprovalStepId(sourceStepId);
            logRecord.setApprovalId(appRecord.getApprovalId());
            saveObject(logRecord);

            approval = t.get_approvalMap().get((short) 2); // action=1 for update
            String appRecordUserList = null;
            String appRecordRoleList = null;
            String mesajBody = "";

            List<Object> notificationUsers =
                dao.executeSQLQuery(
                    "select distinct gu.user_id from iwb.w5_User gu where gu.customization_id= ? and gu.user_Id != ? and (gu.user_Id in (select ur.user_Id from iwb.w5_User_Role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x)) or gu.user_id in ((select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x)))",
                    scd.get("customizationId"),
                    scd.get("userId"),
                    appRecord.getApprovalRoles(),
                    appRecord.getApprovalUsers());
            if (notificationUsers != null)
              for (Object o : notificationUsers) {
                dao.saveObject2(
                    new Log5Notification(
                        scd,
                        GenericUtil.uInt(o),
                        (short)
                            (appRecord.getApprovalStepId() == 901
                                    && approval.getApprovalFlowTip() == 3
                                ? 903
                                : 6),
                        approval.getTableId(),
                        appRecord.getTablePk(),
                        GenericUtil.uInt(scd.get("userId")),
                        null,
                        1),
                    scd);
              }
            /*
             * Ekstra Bildirim Bilgileri, SMS, EMail ve Notification yükleniyor
             * SMS Mail Tip -> 0 SMS , 1 E-Mail, 2 Notification
             */

            Map<Integer, Map> extraInformData = new HashMap<Integer, Map>();

            /* Ekstra bildirim sonu */

            if ((approval != null
                    && approval.getActiveFlag() != 0
                    && ((appRecord.getApprovalStepId() < 900
                        && (approvalStep.getSendMailOnEnterStepFlag() != 0)))
                || appRecord.getApprovalStepId() > 900)) {
              appRecordUserList = appRecord.getApprovalUsers();
              appRecordRoleList = appRecord.getApprovalRoles();
              List<String> emailList = null;

              /* Bu onay aşamasında mail gönderilecek mi ? */

              if (approvalStep.getSendMailOnEnterStepFlag() != 0) {
                emailList =
                    dao.executeSQLQuery(
                        "select gu.email from iwb.w5_user gu where gu.customization_id = ?::integer and gu.user_id in ((select x.satir::integer from iwb.tool_parse_numbers(?,\',\')x)) and gu.user_status = 1 union "
                            + "select (select gu.email from iwb.w5_user gu where gu.customization_id=ur.customization_id and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.customization_id = ?::integer and ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\')x) and "
                            + "((select u.user_tip from iwb.w5_role u where u.role_id = ur.role_id and u.customization_id = ur.customization_id) <> 3 or "
                            + "((select u.user_tip from iwb.w5_role u where u.role_id = ur.role_id and u.customization_id = ur.customization_id) = 3))",
                        scd.get("customizationId"),
                        appRecordUserList,
                        scd.get("customizationId"),
                        appRecordRoleList);

                /*							if(emailList == null && (approval.getApprovalStrategyTip() == 1 || approval.getSendMailOnManualStepFlag() == 1)){ // Eğer manual başlat varsa
                	appRecordUserList = approval.getManualAppUserIds();
                	appRecordRoleList = approval.getManualAppRoleIds();
                	emailList = dao.executeSQLQuery("select gu.email from iwb.w5_user gu where gu.customization_id=?::integer and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\')x) and gu.user_id <> ?::integer union select (select gu.email from iwb.w5_user gu where gu.customization_id=?::integer and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\')x) and ur.user_id <> ?::integer",scd.get("customizationId"),appRecordUserList,scd.get("userId"),scd.get("customizationId"),appRecordRoleList,scd.get("userId"));
                } */

                if (extraInformData.get(1)
                    != null) { // eğer ekstra bilgilendirilecek birileri varsa
                  if (emailList == null) emailList = new ArrayList<String>();
                  List<Object> usersToInform =
                      dao.executeSQLQuery(
                          "select gu.email from iwb.w5_user gu where gu.customization_id=?::integer gu.email is not null and gu.user_id in "
                              + "(select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) or gu.user_id in "
                              + "(select ur.user_id from iwb.w5_user_role ur where ur.customization_id = gu.customization_id and ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\')x))",
                          scd.get("customizationId"),
                          extraInformData.get(1).get("users"),
                          extraInformData.get(1).get("roles"));
                  if (usersToInform != null && usersToInform.size() > 0) {
                    for (Object address : usersToInform) {
                      if (address != null) emailList.add(address.toString());
                    }
                  }
                }

                if (!GenericUtil.isEmpty(emailList)) {
                  String pemailList = "";
                  Object[] m = emailList.toArray();
                  for (int i = 0; i < m.length; i++) {
                    if (m[i] != null) pemailList += "," + m[i];
                  }
                  pemailList = pemailList.substring(1);

                  int mail_setting_id = GenericUtil.uInt((Object) scd.get("mailSettingId"));
                  if (mail_setting_id == 0)
                    mail_setting_id =
                        FrameworkCache.getAppSettingIntValue(scd, "default_outbox_id");

                  if (appRecord.getApprovalStepId() == 901) {
                    mesajBody =
                        "'"
                            + scd.get("completeName")
                            + "' "
                            + LocaleMsgCache.get2(
                                0,
                                (String) scd.get("locale"),
                                "onay_surecini_baslatmanizi_istiyor");
                  } else {
                    mesajBody =
                        "'"
                            + scd.get("completeName")
                            + "' "
                            + LocaleMsgCache.get2(
                                0, (String) scd.get("locale"), "tarafindan_onaya_sunulmustur");
                  }

                  W5Email email =
                      new W5Email(
                          pemailList,
                          null,
                          null,
                          t.get_approvalMap().get((short) 2).getDsc(),
                          " (" + summaryText + ") " + mesajBody,
                          null); // mail_keep_body_original ?
                  W5ObjectMailSetting oms =
                      (W5ObjectMailSetting)
                          dao.getCustomizedObject(
                              "from W5ObjectMailSetting w where w.mailSettingId=? AND w.customizationId=?",
                              (Integer) scd.get("mailSettingId"),
                              scd.get("customizationId"),
                              "MailSetting");
                  email.set_oms(oms);

                  String sonuc = MailUtil.sendMail(scd, email);
                  if (FrameworkCache.getAppSettingIntValue(0, "mail_debug_flag") != 0) {
                    if (sonuc != null) { // basarisiz, queue'ye at//
                      System.out.println(
                          LocaleMsgCache.get2(
                              0,
                              (String) scd.get("locale"),
                              "onay_mekanizmasi_akisinda_mail_gonderilemedi"));
                    } else {
                      System.out.println(
                          LocaleMsgCache.get2(
                              0,
                              (String) scd.get("locale"),
                              "onay_mekanizmasi_akisinda_mail_gonderildi"));
                    }
                  }
                }

                // Comment Yazma
                if (!GenericUtil.isEmpty((String) requestParams.get("_adsc"))) {
                  W5Comment comment = new W5Comment((String)scd.get("projectId"));
                  comment.setTableId(appRecord.getTableId());
                  comment.setTablePk(appRecord.getTablePk());
                  comment.setDsc(requestParams.get("_adsc") + "");
                  comment.setCommentUserId((Integer) scd.get("userId"));
                  comment.setCommentDttm(new java.sql.Timestamp(new Date().getTime()));
                  saveObject(comment);
                }
              }
            }

            if (approvalStep.getSendSmsOnEnterStepFlag() != 0
                && (approval != null
                        && approval.getActiveFlag() != 0
                        && (appRecord.getApprovalStepId() < 900)
                    || appRecord.getApprovalStepId() > 900)) {

              /* Bu onay aşamasında sms gönderilecek mi ? */
              if (FrameworkCache.getAppSettingIntValue(scd, "sms_flag") != 0) {
                appRecordUserList = appRecord.getApprovalUsers();
                appRecordRoleList = appRecord.getApprovalRoles();

                List<String> gsmList =
                    dao.executeSQLQuery(
                        "select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
                        scd.get("customizationId"),
                        appRecordUserList,
                        scd.get("userId"),
                        scd.get("customizationId"),
                        appRecordRoleList,
                        scd.get("userId"));

                if (gsmList == null
                    && approval.getSendSmsOnManualStepFlag() == 1) { // Eğer manuel başlat varsa
                  appRecordUserList = approval.getManualAppUserIds();
                  appRecordRoleList = approval.getManualAppRoleIds();
                  gsmList =
                      dao.executeSQLQuery(
                          "select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
                          scd.get("customizationId"),
                          appRecordUserList,
                          scd.get("userId"),
                          scd.get("customizationId"),
                          appRecordRoleList,
                          scd.get("userId"));
                }

                if (extraInformData.get(0) != null) { // eğer ekstra sms gönderilecek birileri varsa
                  if (gsmList == null) gsmList = new ArrayList<String>();
                  List<Object> usersToInform =
                      dao.executeSQLQuery(
                          "select gu.gsm from iwb.w5_user gu where gu.customization_id=? gu.gsm is not null and gu.user_id in "
                              + "(select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) or gu.user_id in "
                              + "(select ur.user_id from iwb.w5_user_role ur where ur.customization_id = gu.customization_id and ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x))",
                          scd.get("customizationId"),
                          extraInformData.get(0).get("users"),
                          extraInformData.get(0).get("roles"));
                  if (usersToInform != null && usersToInform.size() > 0) {
                    for (Object gsm : usersToInform) {
                      if (gsm != null) gsmList.add(gsm.toString());
                    }
                  }
                }

                if (gsmList != null) {
                  Object[] m = gsmList.toArray();
                  for (int i = 0; i < m.length; i++) {
                    sendSms(
                        Integer.valueOf(String.valueOf(scd.get("customizationId"))),
                        Integer.valueOf(String.valueOf(scd.get("userId"))),
                        (String) m[i],
                        t.get_approvalMap().get((short) 2).getDsc()
                            + " ("
                            + summaryText
                            + ") "
                            + mesajBody,
                        392,
                        appRecord.getApprovalRecordId());
                  }
                }
              }
            }

            if (appRecord.getApprovalStepId() != 901)
              formResult
                  .getOutputMessages()
                  .add(
                      t.get_approvalMap().get((short) 2).getDsc()
                          + ", "
                          + LocaleMsgCache.get2(
                              0, (String) scd.get("locale"), "fw_onaya_sunulmustur")
                          + " ("
                          + summaryText
                          + ")");
            else
              formResult
                  .getOutputMessages()
                  .add(
                      t.get_approvalMap().get((short) 2).getDsc()
                          + ", "
                          + LocaleMsgCache.get2(
                              0,
                              (String) scd.get("locale"),
                              "manuel_olarak_onay_surecini_baslatabilirsiniz")
                          + " ("
                          + summaryText
                          + ")");
          }
          break;
        case 3: // delete
          ptablePk = requestParams.get(t.get_tableParamList().get(0).getDsc() + paramSuffix);
          if (FrameworkSetting.vcs && t.getVcsFlag() != 0) {
            requestParams.put(
                "_iwb_vcs_dsc",
                dao.getTableRecordSummary(scd, t.getTableId(), GenericUtil.uInt(ptablePk), 32));
          }
          if (FrameworkSetting.workflow && accessControlSelfFlag) {
            if (appRecord != null) { // eger bir approval sureci icindeyse
              Log5WorkflowRecord logRecord = new Log5WorkflowRecord();
              logRecord.setApprovalActionTip(
                  (short)
                      6); // start, approve, return, reject, time_limit_cont ,final_approve, deleted
              logRecord.setUserId((Integer) scd.get("userId"));
              logRecord.setApprovalRecordId(appRecord.getApprovalRecordId());
              logRecord.setApprovalStepId(appRecord.getApprovalStepId());
              logRecord.setApprovalId(appRecord.getApprovalId());
              saveObject(logRecord);
              dao.removeObject(appRecord); // TODO:aslinda bir de loga atmali bunu
              appRecord = null;
            } else if (t.get_approvalMap() != null) { // onay mekanizmasi var mi bunda?
              approval = t.get_approvalMap().get((short) 3); // action=2 for delete
              if (approval != null
                  && approval.getActiveFlag() != 0
                  && approval.getApprovalRequestTip()
                      >= 1) { // insert approval mekanizmasi var ve automatic
                Map<String, Object> advancedStepSqlResult = null;
                switch (approval.getApprovalRequestTip()) { // eger approval olacaksa
                  case 1: // automatic approval
                    if (approval.getAdvancedBeginSql() != null
                        && approval.getAdvancedBeginSql().length() > 10) { // calisacak
                      Object[] oz =
                          DBUtil.filterExt4SQL(
                              approval.getAdvancedBeginSql(), scd, requestParams, null);
                      advancedStepSqlResult =
                          dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
                      // donen bir cevap var, aktive_flag deger olarak var ve onun degeri 0 ise o
                      // zaman girmeyecek
                      if (advancedStepSqlResult != null) {
                        if (advancedStepSqlResult.get("active_flag") != null
                            && GenericUtil.uInt(advancedStepSqlResult.get("active_flag"))
                                == 0) // girmeyecek
                        approval = null; // approval olmayacak
                        if (advancedStepSqlResult.get("error_msg") != null) // girmeyecek
                        throw new IWBException(
                              "security",
                              "Workflow",
                              approval.getApprovalId(),
                              null,
                              (String) advancedStepSqlResult.get("error_msg"),
                              null);
                      }
                    }
                    approvalStep = null;
                    switch (approval.getApprovalFlowTip()) { // simple
                      case 0: // basit onay
                        approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
                        break;
                      case 1: // complex onay
                        if (advancedStepSqlResult != null
                            && advancedStepSqlResult.get("approval_step_id") != null
                            && GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")) != 0)
                          approvalStep =
                              approval
                                  .get_approvalStepMap()
                                  .get(
                                      GenericUtil.uInt(
                                          advancedStepSqlResult.get("approval_step_id")));
                        else approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
                        break;
                      case 2: // hierarchical onay
                        int mngUserId = GenericUtil.uInt(scd.get("mngUserId"));
                        if (mngUserId != 0) {
                          approvalStep = new W5WorkflowStep();
                          approvalStep.setApprovalUsers("" + mngUserId);
                          approvalStep.setApprovalStepId(902);
                        } else { // direk duz approval kismine geciyor:TODO
                          if (approval.get_approvalStepList() != null
                              && !approval.get_approvalStepList().isEmpty())
                            approvalStep = approval.get_approvalStepList().get(0).getNewInstance();
                          else approvalStep = null;
                        }

                        break;
                    }

                    break;
                }

                if (approvalStep != null) { // step hazir
                  appRecord = new W5WorkflowRecord();
                  appRecord.setApprovalId(approval.getApprovalId());
                  appRecord.setApprovalStepId(approvalStep.getApprovalStepId());
                  appRecord.setApprovalActionTip(
                      (short) 0); // start,approve,return,reject,time_limit_exceed
                  appRecord.setTableId(formResult.getForm().getObjectId());
                  appRecord.setReturnFlag(approvalStep.getReturnFlag());
                  appRecord.setApprovalUsers(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("approval_users") != null
                          ? (String) advancedStepSqlResult.get("approval_users")
                          : approvalStep.getApprovalUsers());
                  appRecord.setApprovalRoles(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("approval_roles") != null
                          ? (String) advancedStepSqlResult.get("approval_roles")
                          : approvalStep.getApprovalRoles());
                  appRecord.setAccessViewTip(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("access_view_tip") != null
                          ? (short) GenericUtil.uInt(advancedStepSqlResult.get("access_view_tip"))
                          : approvalStep.getAccessViewTip());
                  appRecord.setAccessViewRoles(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("access_view_roles") != null
                          ? (String) advancedStepSqlResult.get("access_view_roles")
                          : approvalStep.getAccessViewRoles());
                  appRecord.setAccessViewUsers(
                      advancedStepSqlResult != null
                              && advancedStepSqlResult.get("access_view_users") != null
                          ? (String) advancedStepSqlResult.get("access_view_users")
                          : approvalStep.getAccessViewUsers());
                  if (appRecord.getAccessViewTip() != 0
                      && !GenericUtil.hasPartInside2(
                          appRecord.getAccessViewUsers(),
                          scd.get("userId"))) // goruntuleme kisiti var ve kendisi goremiyorsa,
                    // kendisini de ekle
                    appRecord.setAccessViewUsers(
                        appRecord.getAccessViewUsers() != null
                            ? appRecord.getAccessViewUsers() + "," + scd.get("userId")
                            : scd.get("userId").toString());
                  appRecord.setInsertUserId((Integer) scd.get("userId"));
                  appRecord.setVersionUserId((Integer) scd.get("userId"));
                  //								appRecord.setCustomizationId((Integer)scd.get("customizationId"));
                } else {
                  throw new IWBException(
                      "framework",
                      "Workflow",
                      formId,
                      null,
                      LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_hatali_onay_tanimi"),
                      null);
                }
              }
            }
          }

          if (appRecord == null
              && FrameworkSetting.feed
              && t.getShowFeedTip() != 0
              && (t.getTableId() != 671
                  && t.getTableId() != 329
                  && t.getTableId()
                      != 44)) { // TODO: delete icin onceden bakmak lazim yoksa gidecek kayit
            feed = new Log5Feed(scd);
            feed.set_showFeedTip(t.getShowFeedTip());
            switch (feed.get_showFeedTip()) {
              case 2: // master
                feed.setFeedTip((short) 3); // remove:master icin
                feed.setTableId(feedTableId = t.getTableId());
                feed.setTablePk(feedTablePk = GenericUtil.uInt(ptablePk));
                break;
              case 1: // detail
                feed.setFeedTip((short) 4); // edit:detaya gore
                feed.setDetailTableId(feedTableId = t.getTableId());
                feed.setDetailTablePk(feedTablePk = GenericUtil.uInt(ptablePk));
                break;
            }
            if (feed != null) {
              feed.set_tableRecordList(
                  dao.findRecordParentRecords(scd, feedTableId, feedTablePk, 0, true));
              if (feed.get_tableRecordList() != null
                  && feed.get_tableRecordList().size() > 1
                  && feed.get_showFeedTip() == 1) {
                W5TableRecordHelper trh = feed.get_tableRecordList().get(1);
                feed.setTableId(trh.getTableId());
                feed.setTablePk(trh.getTablePk());
                feed.set_commentCount(trh.getCommentCount());
              } else if (feed.get_tableRecordList() != null
                  && feed.get_tableRecordList().size() > 0)
                feed.set_commentCount(feed.get_tableRecordList().get(0).getCommentCount());
            }
          }
          Map<String, String> mz = new HashMap();
          mz.put("ptable_id", "" + t.getTableId());
          mz.put("ptable_pk", ptablePk);
          //			W5GlobalFuncResult dfr = executeFunc(scd, 690, mz, (short)2);//bu kaydin child
          // kayitlari var mi? iwb.w5_table_field'daki default_control_tip ve
          // default_lookup_table_id'ye bakiliyor
          if (ptablePk != null && appRecord == null) {
            boolean b = dao.deleteTableRecord(formResult, paramSuffix);
            if (!b)
              formResult.getOutputMessages().add(LocaleMsgCache.get2(scd, "record_not_found"));
          }
          if (formResult.getErrorMap().isEmpty()) {
            //					FrameworkCache.removeTableCacheValue(t.getCustomizationId(),
            // t.getTableId(),GenericUtil.uInt(requestParams.get(t.get_tableParamList().get(0).getDsc()+paramSuffix)));//caching icin

            if (FrameworkSetting.workflow && appRecord != null) { // aproval baslanmis
              int tablePk = GenericUtil.uInt(ptablePk);
              appRecord.setTablePk(tablePk);
              String summaryText = dao.getSummaryText4Record(scd, t.getTableId(), tablePk);
              appRecord.setDsc(summaryText);
              saveObject(appRecord);
              if (FrameworkSetting.liveSyncRecord)
                formResult.addSyncRecord(
                    new W5SynchAfterPostHelper(
                        (String) scd.get("projectId"),
                        392 /*w5_approval_record*/,
                        "" + appRecord.getApprovalRecordId(),
                        (Integer) scd.get("userId"),
                        requestParams.get(".w"),
                        (short) 1));

              Log5WorkflowRecord logRecord = new Log5WorkflowRecord();
              logRecord.setApprovalActionTip(
                  (short)
                      0); // start, approve, return, reject, time_limit_cont ,final_approve, deleted
              logRecord.setUserId((Integer) scd.get("userId"));
              logRecord.setApprovalRecordId(appRecord.getApprovalRecordId());
              logRecord.setApprovalStepId(sourceStepId);
              logRecord.setApprovalId(appRecord.getApprovalId());
              saveObject(logRecord);

              approval = t.get_approvalMap().get((short) 3); // action=3 for delete
              String appRecordUserList = null;
              String appRecordRoleList = null;
              String mesajBody = "";

              if ((approval != null
                      && approval.getActiveFlag() != 0
                      && ((appRecord.getApprovalStepId() < 900
                          && approvalStep.getSendMailOnEnterStepFlag() != 0))
                  || appRecord.getApprovalStepId() > 900)) {

                appRecordUserList = appRecord.getApprovalUsers();
                appRecordRoleList = appRecord.getApprovalRoles();
                List<String> emailList =
                    dao.executeSQLQuery(
                        "select gu.email from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.email from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
                        scd.get("customizationId"),
                        appRecordUserList,
                        scd.get("userId"),
                        scd.get("customizationId"),
                        appRecordRoleList,
                        scd.get("userId"));

                if (emailList.size() > 0) {
                  String pemailList = "";
                  Object[] m = emailList.toArray();
                  for (int i = 0; i < m.length; i++) pemailList += "," + m[i];
                  pemailList = pemailList.substring(1);

                  int mail_setting_id = GenericUtil.uInt((Object) scd.get("mailSettingId"));
                  if (mail_setting_id == 0)
                    mail_setting_id =
                        FrameworkCache.getAppSettingIntValue(scd, "default_outbox_id");

                  if (appRecord.getApprovalStepId() == 901) {
                    mesajBody =
                        "'"
                            + scd.get("completeName")
                            + "' "
                            + LocaleMsgCache.get2(
                                0,
                                (String) scd.get("locale"),
                                "onay_surecini_baslatmanizi_istiyor");
                  } else {
                    mesajBody =
                        "'"
                            + scd.get("completeName")
                            + "' "
                            + LocaleMsgCache.get2(
                                0, (String) scd.get("locale"), "tarafindan_onaya_sunulmustur");
                  }

                  W5Email email =
                      new W5Email(
                          pemailList,
                          null,
                          null,
                          t.get_approvalMap().get((short) 2).getDsc(),
                          " (" + summaryText + ") " + mesajBody,
                          null); // mail_keep_body_original ?
                  W5ObjectMailSetting oms =
                      (W5ObjectMailSetting)
                          dao.getCustomizedObject(
                              "from W5ObjectMailSetting w where w.mailSettingId=? AND w.customizationId=?",
                              (Integer) scd.get("mailSettingId"),
                              scd.get("customizationId"),
                              "MailSetting");
                  email.set_oms(oms);
                  String sonuc = MailUtil.sendMail(scd, email);
                  if (FrameworkCache.getAppSettingIntValue(0, "mail_debug_flag") != 0) {
                    if (sonuc != null) { // basarisiz, queue'ye at//
                      System.out.println(
                          LocaleMsgCache.get2(
                              0,
                              (String) scd.get("locale"),
                              "onay_mekanizmasi_akisinda_mail_gonderilemedi"));
                    } else {
                      System.out.println(
                          LocaleMsgCache.get2(
                              0,
                              (String) scd.get("locale"),
                              "onay_mekanizmasi_akisinda_mail_gonderildi"));
                    }
                  }
                }
              }

              if ((approval != null
                      && approval.getActiveFlag() != 0
                      && ((appRecord.getApprovalStepId() < 900
                          && approvalStep.getSendSmsOnEnterStepFlag() != 0))
                  || appRecord.getApprovalStepId() > 900)) {
                if (FrameworkCache.getAppSettingIntValue(scd, "sms_flag") != 0) {
                  appRecordUserList = appRecord.getApprovalUsers();
                  appRecordRoleList = appRecord.getApprovalRoles();

                  List<Object[]> gsmList =
                      dao.executeSQLQuery(
                          "select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_id <> ? union select (select gu.gsm from iwb.w5_user gu where gu.customization_id=? and gu.user_id = ur.user_id and gu.user_status=1) from iwb.w5_user_role ur where ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ur.user_id <> ?",
                          scd.get("customizationId"),
                          appRecordUserList,
                          scd.get("userId"),
                          scd.get("customizationId"),
                          appRecordRoleList,
                          scd.get("userId"));
                  if (gsmList != null) {
                    Object[] m = gsmList.toArray();
                    for (int i = 0; i < m.length; i++) {
                      sendSms(
                          Integer.valueOf(String.valueOf(scd.get("customizationId"))),
                          Integer.valueOf(String.valueOf(scd.get("userId"))),
                          (String) m[i],
                          t.get_approvalMap().get((short) 2).getDsc()
                              + " ("
                              + summaryText
                              + ") "
                              + mesajBody,
                          392,
                          appRecord.getApprovalRecordId());
                    }
                  }
                }
              }

              if (appRecord.getApprovalStepId() != 901)
                formResult
                    .getOutputMessages()
                    .add(
                        t.get_approvalMap().get((short) 3).getDsc()
                            + ", "
                            + LocaleMsgCache.get2(
                                0, (String) scd.get("locale"), "fw_onaya_sunulmustur")
                            + " ("
                            + summaryText
                            + ")");
              else
                formResult
                    .getOutputMessages()
                    .add(
                        t.get_approvalMap().get((short) 2).getDsc()
                            + ", "
                            + LocaleMsgCache.get2(
                                0,
                                (String) scd.get("locale"),
                                "islemlerinizi_tamamlayip_manuel_olarak_onay_surecini_baslatabilirsiniz")
                            + " ("
                            + summaryText
                            + ")");
            }

            // TODO				dao.executeUpdateSQLQuery("delete from iwb.w5_converted_object co where
            // co.customization_id=? AND co.DST_TABLE_PK=? AND exists(select 1 from
            // iwb.w5_conversion c where c.customization_id=co.customization_id AND c.DST_TABLE_ID=?
            // AND co.conversion_id=c.conversion_id)", scd.get("customizationId"), ptablePk,
            // t.getTableId());
          }

          break;
        default: // sorun var
          throw new IWBException(
              "validation",
              "Action",
              action,
              null,
              LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_wrong_form_action"),
              null);
      }

      if (formResult.getErrorMap().isEmpty()) { // sorun yok

        /*			if((action==1 || action==2) && t.getTableId()==15){ //TODO: simdilik daha yavas calistigi tespit edildi, o yuzden vazgecildi
        	dao.createTableAuditDefinition(scd,PromisUtil.uInt(ptablePk));
        } */

        boolean bc = false; // boolean copy
        if (realAction == 5
            && formResult.getForm().getObjectTip() == 2
            && /*formResult.getForm().get_sourceTable()*/ FrameworkCache.getTable(
                        scd, formResult.getForm().getObjectId())
                    .get_tableChildList()
                != null) { // copy ise o zaman detay var ise onlari da kopyala
          String copyTblIds = requestParams.get("_copy_tbl_ids");
          for (W5TableChild tc : /*formResult.getForm().get_sourceTable()*/
              FrameworkCache.getTable(scd, formResult.getForm().getObjectId()).get_tableChildList())
            if (tc.getCopyStrategyTip() != 0
                && (tc.getCopyStrategyTip() == 1
                    || GenericUtil.hasPartInside2(copyTblIds, "" + tc.getRelatedTableId()))) {
              dao.copyFormTableDetail(formResult, tc, null, schema, paramSuffix);
              bc = true;
            }
        }

        // detail records kaydet
        boolean bd = false;
        if (paramSuffix.length() == 0 && (action == 1 || action == 2)) { // detail kaydet
          for (int qi = 1; GenericUtil.uInt(requestParams.get("_fid" + qi)) != 0; qi++)
            try {
              if (qi == 1) {
                requestParams.putAll((Map) formResult.getOutputFields());
                checkedParentRecords.add(formResult.getForm().getObjectId() + "^" + ptablePk);
              }
              int detailFormId = GenericUtil.uInt(requestParams.get("_fid" + qi));
              int dirtyCount = GenericUtil.uInt(requestParams.get("_cnt" + qi));
              if (dirtyCount == 0) continue;
              Map<String, String> fieldConMap = new HashMap();
              for (String s1 : requestParams.keySet())
                if (s1.startsWith("_fid" + qi + "_con_")) {
                  fieldConMap.put(s1.substring(10), requestParams.get(s1));
                }
              if (detailFormId > 0) {
                if (!fieldConMap.isEmpty()) {
                  for (String key : fieldConMap.keySet())
                    if (formResult.getOutputFields().containsKey(fieldConMap.get(key)))
                      for (int qi2 = 1; qi2 <= dirtyCount; qi2++)
                        requestParams.put(
                            key + qi + "." + qi2,
                            formResult.getOutputFields().get(fieldConMap.get(key)).toString());
                }
                if (formResult.getOutputFields() != null) {
                  for (String key : formResult.getOutputFields().keySet())
                    for (int qi2 = 1; qi2 <= dirtyCount; qi2++)
                      requestParams.put(
                          key + qi + "." + qi2, formResult.getOutputFields().get(key).toString());
                }
                W5FormResult detailForm =
                    postEditGrid4Table(
                        scd,
                        detailFormId,
                        dirtyCount,
                        requestParams,
                        qi + ".",
                        checkedParentRecords);
                if (!GenericUtil.isEmpty(detailForm.getQueueActionList()))
                  result.addAll(detailForm.getQueueActionList());
                if (!GenericUtil.isEmpty(detailForm.getOutputFields()))
                  formResult.getOutputFields().put("_fid" + qi, detailForm.getOutputFields());
                if (FrameworkSetting.liveSyncRecord)
                  formResult.addSyncRecordAll(detailForm.getListSyncAfterPostHelper());
                bd = true;
              } else {
                postEditGridGlobalFunc(scd, -formId, dirtyCount, requestParams, qi + ".");
                bd = true;
              }
            } catch (Exception e) {
              throw new IWBException(
                  "framework",
                  "postForm(detail)",
                  GenericUtil.uInt(requestParams.get("_fid" + qi)),
                  null,
                  "[40," + GenericUtil.uInt(requestParams.get("_fid" + qi)) + "]",
                  e);
            }
        }
        /*			if(t.getCrudGlobalFuncId()!=0 && ((action==2 && GenericUtil.hasPartInside(t.getCrudActions(),"xi") || (action==1 && GenericUtil.hasPartInside(t.getCrudActions(),"xu"))))){
        W5GlobalFuncResult dbFuncResult = dao.getGlobalFuncResult(formResult.getScd(), t.getCrudGlobalFuncId());
        dbFuncResult.setErrorMap(new HashMap());
        Map m = new HashMap();
        m.putAll(formResult.getRequestParams());
        for(String key:formResult.getOutputFields().keySet())m.put("t"+key, formResult.getOutputFields().get(key).toString());
        m.put("triggerAction", action==2 ? "xi":"xu");//trigger action
        dbFuncResult.setRequestParams(m);
        dao.executeGlobalFunc(dbFuncResult,"");
        if(dbFuncResult.getErrorMap().isEmpty() && dbFuncResult.getResultMap()!=null)formResult.getOutputFields().putAll(dbFuncResult.getResultMap());
         	} */
        // approval
        if (FrameworkSetting.workflow
            && GenericUtil.uInt(requestParams.get("_arid" + paramSuffix))
                != 0) { // kaydet ve approve et???
          approveRecord(
              scd,
              GenericUtil.uInt(requestParams.get("_arid" + paramSuffix)),
              GenericUtil.uInt(requestParams.get("_aa" + paramSuffix)),
              requestParams);
        }

        /* tableTrigger After Action start*/
        if (tla != null)
          extFormTrigger(
              formResult,
              new String[] {"_", "au", "ai", "ad", "_", "ai"}[action],
              scd,
              requestParams,
              t,
              ptablePk,
              paramSuffix);
        /* end of tableTrigger*/

        /* form conversion*/
        extFormConversion(formResult, paramSuffix, action, scd, requestParams, t, ptablePk, false);
        /* end of form conversion*/

        /* alarm start*/
        extFormAlarm(formResult, action, scd, requestParams, t, mobile, ptablePk);
        /* end of alarm*/

        /* sms/mail customized templates*/
        extFormSmsMail(formResult, result, action, scd, requestParams, t, mobile, ptablePk);
        /* end of sms/mail customized templates*/

        /* vcs control*/
        extFormVcs(formResult, action, scd, requestParams, t, ptablePk);
        /* end of vcs*/

        if (action == 2) { // bir sorun yoksa, o zaman conversion kaydi yap
          if (GenericUtil.isEmpty(paramSuffix)
              && requestParams.containsKey("_cnvId")
              && requestParams.containsKey("_cnvTblPk")) { // conversion var burda
            int conversionId = GenericUtil.uInt(requestParams.get("_cnvId"));
            int conversionTablePk = GenericUtil.uInt(requestParams.get("_cnvTblPk"));
            List<W5Conversion> lcnv =
                dao.find(
                    "from W5Conversion x where x.conversionId=? AND x.projectUuid=?",
                    conversionId,
                    (String) scd.get("projectId"));
            if (lcnv.size() == 1
                && lcnv.get(0).getDstFormId() == formId) { // bu form'a aitmis conversion
              W5Conversion cnv = lcnv.get(0);
              W5ConvertedObject co =
                  new W5ConvertedObject(
                      scd, conversionId, conversionTablePk, GenericUtil.uInt(ptablePk));
              saveObject(co);
              if (cnv.getIncludeFileAttachmentFlag() != 0) {
                dao.executeUpdateSQLQuery(
                    "{call pcopy_file_attach( ?, ? , ?, ?, ?); }",
                    (Integer) scd.get("userRoleId"),
                    cnv.getSrcTableId(),
                    conversionTablePk,
                    cnv.getDstTableId(),
                    GenericUtil.uInt(ptablePk));
              }
              if (!GenericUtil.isEmpty(cnv.getRhinoCode())) {
                dao.executeRhinoScript(scd, requestParams, cnv.getRhinoCode(), null, "result");
              }
            }
          }
        }

        /* feed */
        extFormFeed(formResult, scd, requestParams, t, ptablePk, feed, feedTableId, feedTablePk);
        /* end of feed */
      }
      PostFormTrigger.afterPostForm(formResult, dao, paramSuffix);

      if (FrameworkSetting.liveSyncRecord
          && formResult.getErrorMap().isEmpty()
          && formResult.getForm() != null
          && formResult.getForm().getObjectTip() == 2) {
        int userId = (Integer) formResult.getScd().get("userId");
        //			int	customizationId = (Integer)formResult.getScd().get("customizationId");
        t = FrameworkCache.getTable(formResult.getScd(), formResult.getForm().getObjectId());
        String webPageId = formResult.getRequestParams().get(".w");
        if (t.getLiveSyncFlag() != 0 && webPageId != null) {
          String key = "";
          if (formResult.getAction() == 1 || formResult.getAction() == 3) {
            for (String k : formResult.getPkFields().keySet())
              if (!k.startsWith("customization")) key += "*" + formResult.getPkFields().get(k);
            key = formResult.getForm().getObjectId() + "-" + key.substring(1);
            formResult.setLiveSyncKey(key);
          }

          //					formResult.addSyncRecord(new W5SynchAfterPostHelper(customizationId,
          // t.getTableId(), key, userId, webPageId, (short)formResult.getAction())); //TODO

        }
      }

      if (false
          && !GenericUtil.isEmpty(
              formResult
                  .getMapWidgetCount()) /* && !PromisUtil.isEmpty(request.getParameter("_promis_token"))*/) {
        UserUtil.publishWidgetStatus(scd, formResult.getMapWidgetCount());
      }

      return result;
    } catch (Exception e) {
      throw new IWBException(
          "framework",
          "postForm",
          formResult.getFormId(),
          null,
          "[40," + formResult.getFormId() + "] " + formResult.getForm().getDsc(),
          e);
    }
  }

  private void extFormSmsMail(
      W5FormResult formResult,
      List<W5QueuedActionHelper> result,
      int action,
      Map<String, Object> scd,
      Map<String, String> requestParams,
      W5Table t,
      boolean mobile,
      String ptablePk) {
    if (!GenericUtil.isEmpty(formResult.getForm().get_formSmsMailList())) {
      Set<Integer> tplSet = new HashSet();
      for (W5FormSmsMail m : formResult.getForm().get_formSmsMailList()) {
        if (m.getAlarmFlag() == 0
            && m.getActiveFlag() == 1
            && (m.getSmsMailSentTip() == 0 || m.getSmsMailSentTip() == 3)
            && GenericUtil.hasPartInside2(m.getActionTips(), action)) {
          tplSet.add(m.getFormSmsMailId());
        }
      }
      String smsStr = requestParams.get("_smsStr");
      if (smsStr != null) {
        String[] arSmsStr = requestParams.get("_smsStr").split(",");
        if (arSmsStr != null && arSmsStr.length > 0)
          for (String ass : arSmsStr) {
            int fsmId = GenericUtil.uInt(ass);
            if (fsmId != 0) tplSet.add(fsmId);
          }
      }
      if (!tplSet.isEmpty()) { // smsMail
        List<Map<String, String>> previewMapList = null;
        for (int fsmId : tplSet)
          try {
            W5FormSmsMail fsm = formResult.getForm().get_formSmsMailMap().get(fsmId);
            if (fsm == null
                || fsm.getAlarmFlag() != 0
                || !GenericUtil.hasPartInside2(fsm.getActionTips(), action)
                || !GenericUtil.hasPartInside2(fsm.getWebMobileTips(), mobile ? "2" : "1"))
              continue;
            if (!GenericUtil.isEmpty(fsm.getConditionSqlCode())) {
              boolean conditionCheck =
                  dao.conditionRecordExistsCheck(
                      scd,
                      requestParams,
                      t.getTableId(),
                      GenericUtil.uInt(ptablePk),
                      fsm.getConditionSqlCode());
              if (!conditionCheck) continue;
            }
            Map<String, String> m = new HashMap();
            m.put("_tableId", "" + t.getTableId());
            m.put("_tablePk", ptablePk);
            if (fsm.getPreviewFlag() != 0
                && !mobile) { // simdi gonderilmeyecek, formda geri donecek
              m.put("_fsmId", "" + fsm.getFormSmsMailId());
              m.put("_fsmTip", "" + fsm.getSmsMailTip());
              if (previewMapList == null) {
                previewMapList = new ArrayList();
                formResult.setPreviewMapList(previewMapList);
              }
              previewMapList.add(m);
              continue;
            }
            switch (fsm.getSmsMailTip()) {
              case 0: // sms
                // parameterMap.get("phone"),parameterMap.get("body")
                //		m.putAll(dao.interprateSmsTemplate(fsm, scd, requestParams, t.getTableId(),
                // GenericUtil.uInt(ptablePk)));
                //		if(!GenericUtil.isEmpty(m.get("phone")))result.add(new W5QueuedActionHelper(scd,
                // -631, m, (short)1));
                break;
              case 1: // mail
                // W5Email email= new
                // W5Email(parameterMap.get("pmail_to"),parameterMap.get("pmail_cc"),parameterMap.get("pmail_bcc"),parameterMap.get("pmail_subject"),parameterMap.get("pmail_body"), parameterMap.get("pmail_keep_body_original"), fileAttachments);
                W5Email email =
                    dao.interprateMailTemplate(
                        fsm, scd, requestParams, t.getTableId(), GenericUtil.uInt(ptablePk));
                int ms =
                    fsm.getMailSettingId() != 0
                        ? fsm.getMailSettingId()
                        : (Integer) scd.get("mailSettingId");
                if (ms == 0) ms = 1;
                int cusId = ms != 1 ? (Integer) scd.get("customizationId") : 0;
                W5ObjectMailSetting oms =
                    (W5ObjectMailSetting)
                        dao.getCustomizedObject(
                            "from W5ObjectMailSetting w where w.mailSettingId=? AND w.customizationId=?",
                            fsm.getMailSettingId() != 0
                                ? fsm.getMailSettingId()
                                : (Integer) scd.get("mailSettingId"),
                            cusId,
                            ms != 1 ? "MailSetting" : null);
                if (oms == null) {
                  oms =
                      (W5ObjectMailSetting)
                          dao.getCustomizedObject(
                              "from W5ObjectMailSetting w where w.mailSettingId=? AND w.customizationId=?",
                              1,
                              0,
                              "SystemMailSetting");
                }
                email.set_oms(oms);
                if (fsm.getAsyncFlag() != 0) result.add(new W5QueuedActionHelper(email));
                else MailUtil.sendMail(scd, email);
                break;
            }
          } catch (Exception e) {
            if (FrameworkSetting.debug) e.printStackTrace();
            formResult
                .getOutputMessages()
                .add("CUSTOMIZED SMS/MAIL(" + fsmId + ") EXCEPTION: " + e.getMessage());
          }
      }
    }
  }

  private void extFormFeed(
      W5FormResult formResult,
      Map<String, Object> scd,
      Map<String, String> requestParams,
      W5Table t,
      String ptablePk,
      Log5Feed feed,
      int feedTableId,
      int feedTablePk) {
    if (FrameworkSetting.feed
        && FrameworkCache.getAppSettingIntValue(scd, "feed_flag") != 0
        && t.getShowFeedTip() != 0)
      try {
        // buraya diger yerlerden de gelmesi lazim: dbFunc(email,sms),fileAttach, checkMailz
        //				W5Feed feed = null;
        boolean dontSaveFeed = false;
        switch (formResult.getAction()) { // post action'a gore
          case 2: // post action:insert
            switch (t.getTableId()) {
              case 671: // feed: signal:
                feed = new Log5Feed(scd);
                feed.setFeedTip((short) 0); // feed:signal
                feed.setDsc(requestParams.get("dsc"));
                feed.setFeedId(GenericUtil.uInt(ptablePk));
                if (requestParams.get("access_roles") != null
                    || requestParams.get("access_users") != null) {
                  W5AccessControlHelper ach =
                      new W5AccessControlHelper(
                          requestParams.get("access_roles"), requestParams.get("access_users"));
                  feed.set_viewAccessControl(ach);
                }
                dontSaveFeed = true;
                break;
              case 329: // comment:eger wall 'dayse oraya comment olarak koy, ve yukari tasi; aksi
                // halde yeni bir comment koy
                feedTableId = GenericUtil.uInt(requestParams, "table_id");
                feedTablePk = GenericUtil.uInt(requestParams, "table_pk");
                List<Log5Feed> lx = FrameworkCache.wFeeds.get((Integer) scd.get("customizationId"));
                if (lx != null)
                  for (int qz = lx.size() - 1; qz >= 0; qz--) {
                    Log5Feed lf = lx.get(qz);
                    if (lf != null
                        && lf.getTableId() == feedTableId
                        && lf.getTablePk() == feedTablePk
                        && lf.getCustomizationId() == (Integer) scd.get("customizationId")) {
                      if (lf.get_tableCommentList() == null)
                        lf.set_tableCommentList(new ArrayList());
                      W5CommentHelper ch = new W5CommentHelper(scd);
                      ch.setDsc(requestParams.get("dsc"));
                      lf.get_tableCommentList().add(ch);
                      lf.set_commentCount(lf.get_commentCount() + 1);
                      lx.add(lf);
                      lx.set(
                          qz, null); // basa aliyor, ama isterse parametreik olabailr. basa almadan
                      // oldugu yerde
                      feed = lf;
                      dontSaveFeed = true;
                      break;
                    }
                  }
                if (feed == null) { // demek ki, bulamamis bir yerde simdikileri yapacak
                  feed = new Log5Feed(scd);
                  feed.setFeedTip((short) 11); // comment
                  //							feed.setDsc(requestParams.get("dsc"));
                  feed.setTableId(feedTableId = GenericUtil.uInt(requestParams, "table_id"));
                  feed.setTablePk(feedTablePk = GenericUtil.uInt(requestParams, "table_pk"));
                  feed.set_showFeedTip(
                      (short)
                          2); // master ??? PromisCache.getTable(scd, feedTableId).getShowFeedTip()
                  W5CommentHelper ch = new W5CommentHelper(scd);
                  ch.setDsc(requestParams.get("dsc"));
                  feed.set_tableCommentList(new ArrayList());
                  feed.get_tableCommentList().add(ch);
                  break;
                }
                break;
              case 44: // file_attach:TODO
                break;
              default: // diger herseyde
                feed = new Log5Feed(scd);
                feed.set_showFeedTip(t.getShowFeedTip());
                switch (feed.get_showFeedTip()) {
                  case 2: // master
                    feed.setFeedTip((short) 2); // insert:master icin
                    feed.setTableId(feedTableId = t.getTableId());
                    feed.setTablePk(feedTablePk = GenericUtil.uInt(ptablePk));
                    break;
                  case 1: // detail
                    feed.setFeedTip((short) 4); // edit:detaya gore
                    feed.setDetailTableId(feedTableId = t.getTableId());
                    feed.setDetailTablePk(feedTablePk = GenericUtil.uInt(ptablePk));
                    break;
                }
            }

            break;
          case 1: // post action:edit
            feed = new Log5Feed(scd);
            feed.set_showFeedTip(t.getShowFeedTip());
            switch (feed.get_showFeedTip()) {
              case 2: // master
                feed.setFeedTip((short) 1); // insert:master icin
                feed.setTableId(feedTableId = t.getTableId());
                feed.setTablePk(feedTablePk = GenericUtil.uInt(ptablePk));
                break;
              case 1: // detail
                feed.setFeedTip((short) 4); // edit:detaya gore
                feed.setDetailTableId(feedTableId = t.getTableId());
                feed.setDetailTablePk(feedTablePk = GenericUtil.uInt(ptablePk));
                break;
            }
            break;
          case 3: // post action:remove:TODO yukarida yapilmasi lazim
            break;
        }
        if (feed != null) {
          if (!dontSaveFeed) { // eger signal degilse
            if (formResult.getAction() != 3
                && feedTableId != 0
                && feedTablePk != 0) { // detail icinse
              feed.set_tableRecordList(
                  dao.findRecordParentRecords(scd, feedTableId, feedTablePk, 0, true));
              if (feed.get_tableRecordList() != null
                  && feed.get_tableRecordList().size() > 1
                  && feed.get_showFeedTip() == 1) {
                W5TableRecordHelper trh = feed.get_tableRecordList().get(1);
                feed.setTableId(trh.getTableId());
                feed.setTablePk(trh.getTablePk());
                feed.set_commentCount(trh.getCommentCount());
              } else if (feed.get_tableRecordList() != null
                  && feed.get_tableRecordList().size() > 0)
                feed.set_commentCount(feed.get_tableRecordList().get(0).getCommentCount());
            }
            saveObject(feed);
          }
          FrameworkCache.addFeed(scd, feed, true);
        }

      } catch (Exception e) {
        if (FrameworkSetting.debug) e.printStackTrace();
        formResult.getOutputMessages().add("FEED EXCEPTION: " + e.getMessage());
      }
  }

  private void extFormVcs(
      W5FormResult formResult,
      int action,
      Map<String, Object> scd,
      Map<String, String> requestParams,
      W5Table t,
      String ptablePk) {
    if (!FrameworkSetting.vcs || t.getVcsFlag() == 0) return;
    int tablePk = GenericUtil.uInt(ptablePk);
    if (tablePk == 0) return;
    switch (action) {
      case 5: // copy
      case 2: // insert
        W5VcsObject ivo = new W5VcsObject(scd, t.getTableId(), tablePk);
        dao.saveObject(ivo);
        break;
      case 1: // update
      case 3: // delete
        List l =
            dao.find(
                "from W5VcsObject t where t.tableId=? AND t.tablePk=? AND t.projectUuid=?",
                t.getTableId(),
                tablePk,
                scd.get("projectId"));
        if (l.isEmpty()) break;
        W5VcsObject vo = (W5VcsObject) l.get(0);
        vo.setVersionDttm(new Timestamp(new Date().getTime()));
        vo.setVersionUserId((Integer) scd.get("userId"));
        switch (vo.getVcsObjectStatusTip()) { // zaten insert ise
        case	0://ignored
          case 2: // insert: direk sil
          case 3: // zaten silinmisse boyle birsey olmamali
            if (action == 3) {
              dao.removeObject(vo);
            }
            if (vo.getVcsObjectStatusTip() == 3)
              formResult.getOutputMessages().add("VCS WARNING: Already Deleted VCS Object????");
            break;

          case 1:
          case 9: // synched ve/veya edit durumunda ise
            if (action == 3) { // delete edilidliyse
              vo.setVcsObjectStatusTip((short) 3);
              vo.setVcsCommitRecordHash(requestParams.get("_iwb_vcs_dsc").toString());
            } else { // update edildise simdi
              String newHash = dao.getObjectVcsHash(scd, t.getTableId(), tablePk);
              vo.setVcsObjectStatusTip(
                  (short) (vo.getVcsCommitRecordHash().equals(newHash) ? 9 : 1));
            }
            dao.updateObject(vo);
            break;
        }
        break;
    }
  }

  private void extFormTrigger(
      W5FormResult formResult,
      String action,
      Map<String, Object> scd,
      Map<String, String> requestParams,
      W5Table t,
      String ptablePk,
      String prefix) {
    List<W5TableEvent> tla = FrameworkCache.getTableEvents(scd, t.getTableId());
    if (tla == null) return;
    Map<String, String> newRequestParam = GenericUtil.isEmpty(prefix) ? requestParams : null;
    for (W5TableEvent ta : tla)
      if (GenericUtil.hasPartInside2(ta.getLkpTriggerActions(), action)) {
        if (ta.getLkpCodeType() == 1) { // javascript
          if (newRequestParam == null) {
            newRequestParam = new HashMap();
            if (!GenericUtil.isEmpty(requestParams))
              for (String key : requestParams.keySet())
                if (key != null && key.endsWith(prefix)) {
                  newRequestParam.put(
                      key.substring(0, key.length() - prefix.length()), requestParams.get(key));
                }
          }
          ContextFactory factory = MyFactory.getGlobal();
          Context cx = factory.enterContext();

          // Context cx = Context.enter();
          StringBuilder sc = new StringBuilder();
          try {
            cx.setOptimizationLevel(-1);
            if (FrameworkSetting.rhinoInstructionCount > 0)
              cx.setInstructionObserverThreshold(FrameworkSetting.rhinoInstructionCount);
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            Scriptable scope = cx.initStandardObjects();
            if (ta.getTriggerCode().indexOf("$iwb.") > -1) {
              ScriptEngine se = new ScriptEngine(scd, newRequestParam, dao, this);
              Object wrappedOut = Context.javaToJS(se, scope);
              ScriptableObject.putProperty(scope, "$iwb", wrappedOut);
            }
            // Collect the arguments into a single string.
            sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString(scd));
            sc.append("\nvar _request=").append(GenericUtil.fromMapToJsonString(newRequestParam));
            sc.append("\nvar triggerAction='").append(action).append("';");
            if (!GenericUtil.isEmpty(ptablePk))
              sc.append("\n")
                  .append(t.get_tableFieldList().get(0).getDsc())
                  .append("='")
                  .append(ptablePk)
                  .append("';");
            sc.append("\n").append(ta.getTriggerCode());

            // sc.append("'})';");
            // Now evaluate the string we've colected.
            cx.evaluateString(scope, sc.toString(), null, 1, null);

            Object result = null;
            if (scope.has("result", scope)) result = scope.get("result", scope);

            String msg = LocaleMsgCache.get2(scd, ta.getDsc());
            boolean b = false;
            if (result != null && result instanceof org.mozilla.javascript.Undefined) result = null;
            else if (result != null && result instanceof Boolean)
              if ((Boolean) result == false) result = null;

            if (result != null) {
              msg = result.toString();
              short resultAction = ta.getLkpResultAction();
              if (scope.has("resultAction", scope))
                resultAction =
                    (short) GenericUtil.uInt(scope.get("resultAction", scope).toString());
              switch (resultAction) {
                case 1: // readonly
                  formResult.setViewMode(true);
                case 0: // continue
                  formResult.getOutputMessages().add(msg);
                  break;
                case 2: // confirm & continue
                  if (!requestParams.containsKey("_confirmId_" + ta.getTableTriggerId()))
                    throw new IWBException(
                        "confirm", "ConfirmId", ta.getTableTriggerId(), null, msg, null);
                  break;
                case 3: // stop with message
                  throw new IWBException(
                      "security", "TableTrigger", ta.getTableTriggerId(), null, msg, null);
              }
            }
          } catch (Exception e) {
            throw new IWBException(
                "rhino",
                "TableEvent",
                ta.getTableTriggerId(),
                sc.toString(),
                "[1209," + ta.getTableTriggerId() + "] " + ta.getDsc(),
                e);
          } finally {
            // Exit from the context.
            cx.exit();
          }
        } else if (ta.getLkpCodeType() == 4)
          try { // sql
            Map<String, Object> obj = new HashMap();
            obj.put("triggerAction", action);
            Map<String, Object> m =
                dao.runSQLQuery2Map(ta.getTriggerCode(), scd, requestParams, obj);
            if (m != null) {
              String msg = LocaleMsgCache.get2(scd, ta.getDsc());
              if (m.get("result") != null) msg = m.get("result").toString();
              short resultAction = ta.getLkpResultAction();
              if (m.containsKey("resultAction"))
                resultAction = (short) GenericUtil.uInt(m.get("resultAction"));
              switch (resultAction) {
                case 1: // readonly
                  formResult.setViewMode(true);
                case 0: // continue
                  formResult.getOutputMessages().add(msg);
                  break;
                case 2: // confirm & continue
                  if (!requestParams.containsKey("_confirmId_" + ta.getTableTriggerId()))
                    throw new IWBException(
                        "confirm", "ConfirmId", ta.getTableTriggerId(), null, msg, null);
                  break;
                case 3: // stop with message
                  throw new IWBException(
                      "security", "TableTrigger", ta.getTableTriggerId(), null, msg, null);
              }
            }
          } catch (Exception e) {
            throw new IWBException(
                "sql",
                "Event",
                ta.getTableTriggerId(),
                ta.getTriggerCode(),
                "[1209," + ta.getTableTriggerId() + "] " + ta.getDsc(),
                e);
          }
      }
  }

  private void extFormAlarm(
      W5FormResult formResult,
      int action,
      Map<String, Object> scd,
      Map<String, String> requestParams,
      W5Table t,
      boolean mobile,
      String ptablePk) {
    if (!FrameworkSetting.alarm) return;
    Map<Integer, W5FormSmsMailAlarm> alMap = null;
    if (action == 1
        && FrameworkSetting.alarm
        && !GenericUtil.isEmpty(formResult.getForm().get_formSmsMailList())) {
      boolean alarm = false;
      for (W5FormSmsMail o : formResult.getForm().get_formSmsMailList())
        if (o.getAlarmFlag() != 0) {
          alarm = true;
          break;
        }
      if (alarm) {
        alMap = new HashMap();
        List<W5FormSmsMailAlarm> l =
            (List<W5FormSmsMailAlarm>)
                dao.find(
                    "from W5FormSmsMailAlarm a where a.projectUuid=? AND a.insertUserId=? AND a.tableId=? AND a.tablePk=? ",
                    scd.get("projectId"),
                    scd.get("userId"),
                    formResult.getForm().getObjectId(),
                    GenericUtil.uInt(ptablePk));
        for (W5FormSmsMailAlarm a : l) {
          alMap.put(a.getFormSmsMailId(), a);
        }
      }
    }

    if (FrameworkSetting.alarm && !GenericUtil.isEmpty(formResult.getForm().get_formSmsMailList()))
      try { // alarm
        String almStr = requestParams.get("_almStr");
        if (GenericUtil.isEmpty(almStr)) {
          for (W5FormSmsMail fsm : formResult.getForm().get_formSmsMailList())
            if (fsm.getAlarmFlag() != 0
                && GenericUtil.hasPartInside2(fsm.getActionTips(), action)
                && GenericUtil.hasPartInside2(fsm.getWebMobileTips(), mobile ? "2" : "1")
                && (fsm.getSmsMailSentTip() == 0 || fsm.getSmsMailSentTip() == 3)) {
              if (GenericUtil.isEmpty(almStr)) almStr = "" + fsm.getFormSmsMailId();
              else almStr += "," + fsm.getFormSmsMailId();
            }
          if (GenericUtil.isEmpty(almStr)) return;
        }

        List<Map<String, String>> previewMapList = null;
        String[] arSmsStr = almStr.split(",");
        if (arSmsStr != null && arSmsStr.length > 0)
          for (String ass : arSmsStr) {
            String dttm = null;
            if (ass.contains("-")) {
              dttm = ass.substring(ass.indexOf('-') + 1);
              ass = ass.substring(0, ass.indexOf('-'));
            }
            W5FormSmsMail fsm =
                formResult.getForm().get_formSmsMailMap().get(GenericUtil.uInt(ass));
            if (fsm == null
                || !GenericUtil.hasPartInside2(fsm.getActionTips(), action)
                || !GenericUtil.hasPartInside2(fsm.getWebMobileTips(), mobile ? "2" : "1"))
              continue;
            Date alarmDttm = null;
            if (dttm != null) {
              alarmDttm = GenericUtil.uDateTm(dttm);
            }
            boolean alarmDttmNull = false; // eger update olurken, alarmDttm null ise onu degistirme
            if (alarmDttm == null) {
              W5TableField atf = t.get_tableFieldMap().get(fsm.getAlarmDttmFieldId());
              if (atf != null) {
                String adt = requestParams.get(atf.getDsc());
                if (!GenericUtil.isEmpty(adt)) {
                  Date ad = GenericUtil.uDate(adt);
                  if (ad != null) {
                    if (!GenericUtil.isEmpty(fsm.getAlarmTm()))
                      switch (fsm.getAlarmTmTip()) {
                        case 0: // sabit
                          ad.setTime(
                              (1000 * 60 * 60 * 24) * (int) (ad.getTime() / (1000 * 60 * 60 * 24))
                                  + GenericUtil.uTime2Millis(fsm.getAlarmTm()));
                          break;
                        case 1: // +
                          ad.setTime(ad.getTime() + GenericUtil.uTime2Millis(fsm.getAlarmTm()));
                          break;
                        case 2: // -
                          ad.setTime(ad.getTime() - GenericUtil.uTime2Millis(fsm.getAlarmTm()));
                          break;
                      }
                    alarmDttm = ad;
                    alarmDttmNull = true;
                  }
                }
              }
            }
            if (alarmDttm != null) { // TODO
              if (!GenericUtil.isEmpty(fsm.getConditionSqlCode())) {
                boolean conditionCheck =
                    dao.conditionRecordExistsCheck(
                        scd,
                        requestParams,
                        t.getTableId(),
                        GenericUtil.uInt(ptablePk),
                        fsm.getConditionSqlCode());
                if (!conditionCheck) continue;
              }
              W5FormSmsMailAlarm fsma = null;
              if (action == 1 && alMap != null && alMap.containsKey(fsm.getFormSmsMailId())) {
                fsma = alMap.get(fsm.getFormSmsMailId());
                if (fsma.getStatus() == 1) {
                  if (!alarmDttmNull) fsma.setAlarmDttm(new Timestamp(alarmDttm.getTime()));
                  fsma.setDsc(dao.getSummaryText4Record(scd, t.getTableId(), fsma.getTablePk()));
                  dao.updateObject(fsma);
                }
                alMap.remove(fsm.getFormSmsMailId());
              } else {
                fsma = new W5FormSmsMailAlarm(scd);
                fsma.setTableId(t.getTableId());
                fsma.setTablePk(GenericUtil.uInt(ptablePk));
                fsma.setFormSmsMailId(fsm.getFormSmsMailId());
                fsma.setStatus((short) 1); // planned
                fsma.setAlarmDttm(new Timestamp(alarmDttm.getTime()));
                fsma.setDsc(dao.getSummaryText4Record(scd, t.getTableId(), fsma.getTablePk()));
                if (fsm.getPreviewFlag() == 0) {
                  dao.saveObject(fsma);
                  formResult
                      .getOutputMessages()
                      .add(
                          LocaleMsgCache.get2(scd, "new_alarm_added_at")
                              + " "
                              + GenericUtil.uFormatDateTime(alarmDttm));

                } else {
                  if (GenericUtil.isEmpty(formResult.getFormAlarmList()))
                    formResult.setFormAlarmList(new ArrayList());
                  formResult.getFormAlarmList().add(fsma);
                }
              }
            }
          }

      } catch (Exception e) {
        if (FrameworkSetting.debug) e.printStackTrace();
        formResult.getOutputMessages().add("ALARM EXCEPTION: " + e.getMessage());
      }
    if (action == 1 && FrameworkSetting.alarm && !GenericUtil.isEmpty(alMap))
      for (W5FormSmsMailAlarm a : alMap.values())
        if (a.getStatus() == 1) {
          dao.removeObject(a);
        }
  }

  private boolean extFormConversion(
      W5FormResult formResult,
      String prefix,
      int action,
      Map<String, Object> scd,
      Map<String, String> requestParams,
      W5Table t,
      String ptablePk,
      boolean cleanConversion) {
    if ((action == 0 || action == 1 || action == 2)
        && !GenericUtil.isEmpty(formResult.getForm().get_conversionList())) {
      Set<Integer> cnvSet = new HashSet();
      if (!cleanConversion)
        for (W5Conversion cnv : formResult.getForm().get_conversionList())
          if (
          /*(action==1 && cnv.getSynchOnUpdateFlag()!=0) || */ ((cnv.getConversionTip()
                      == 3 /*invisible*/
                  || cnv.getConversionTip() == 0 /*kesin*/)
              && GenericUtil.hasPartInside2(cnv.getActionTips(), action))) {
            cnvSet.add(cnv.getConversionId());
          }
      String cnvStr = requestParams.get("_cnvStr");
      if (cnvStr != null) {
        String[] arCnvStr = cnvStr.split(",");
        if (arCnvStr != null && arCnvStr.length > 0)
          for (String acs : arCnvStr) {
            int conversionId = GenericUtil.uInt(acs);
            if (conversionId != 0) cnvSet.add(conversionId);
          }
      }

      if (action == 1
          || action == 0) { // conversion olan bir form? o zaman sync olan covnerted objeleri bul
        String inStr = "";
        if (!cleanConversion) {
          for (W5Conversion cnv : formResult.getForm().get_conversionList())
            if (GenericUtil.hasPartInside2(cnv.getActionTips(), action)
                || cnv.getSynchOnUpdateFlag() != 0) { // synch varsa
              inStr += "," + cnv.getConversionId();
            }
        } else
          for (Integer s : cnvSet) {
            inStr += "," + s;
          }

        if (inStr.length() > 1) {
          List<W5ConvertedObject> lco =
              dao.find(
                  "from W5ConvertedObject x where x.projectUuid=? AND x.conversionId in ("
                      + inStr.substring(1)
                      + ") and x.srcTablePk=?",
                  scd.get("projectId"),
                  GenericUtil.uInt(requestParams, t.get_tableParamList().get(0).getDsc() + prefix));
          if (!lco.isEmpty()) {
            Map<Integer, List<W5ConvertedObject>> m = new HashMap();
            formResult.setMapConvertedObject(m);
            for (W5ConvertedObject co : lco) {
              List<W5ConvertedObject> l = m.get(co.getConversionId());
              if (l == null) {
                l = new ArrayList();
                m.put(co.getConversionId(), l);
              }
              l.add(co);
              cnvSet.add(co.getConversionId());
            }
          }
        }
      }

      List<Map<String, String>> previewConversionMapList = null;
      boolean convertedAny = false;
      W5Conversion c = null;
      for (int conversionId : cnvSet)
        try {
          c = null;
          for (W5Conversion cnv : formResult.getForm().get_conversionList())
            if (cnv.getConversionId() == conversionId) {
              c = cnv;
              break;
            }
          if (c == null) continue; // bulamadi
          //				if(!PromisUtil.hasPartInside2(c.getActionTips(), action) && (action!=1 ||
          // c.getSynchOnUpdateFlag()==0)) continue; // bulamadi veya buldugunun action'i uygun
          // degil
          if (c.getSrcDstTip() == 0) { // Table -> Table
            if (formResult.getMapConvertedObject() != null) {
              List<W5ConvertedObject> lco =
                  formResult.getMapConvertedObject().get(c.getConversionId());
              if (lco != null) { // dikkat daha once convert edilmis object var
                if (c.getMaxNumofConversion() == 0
                    || c.getMaxNumofConversion() > lco.size()
                    || (!cleanConversion && c.getSynchOnUpdateFlag() != 0)) {
                  if (c.getSynchOnUpdateFlag() != 0)
                    for (W5ConvertedObject co : lco) { // bu conversion'da synch var
                      //
                      //	formResult.getOutputMessages().add(PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_conversion_auto_error")+  "  ["+PromisLocaleMsg.get2(scd,c.getDsc())+"]");
                      int advancedConditionGroupId = 0;

                      String prefix2 = "";
                      Map<String, String> m = new HashMap();
                      m.put("a", "1");
                      m.put("_fid", "" + c.getDstFormId());
                      W5Table dt = FrameworkCache.getTable(scd, c.getDstTableId());
                      m.put(
                          dt.get_tableParamList().get(0).getDsc() + prefix2,
                          "" + co.getDstTablePk());
                      W5FormResult dstFormResult = dao.getFormResult(scd, c.getDstFormId(), 1, m);
                      Map<String, Object> mq =
                          dao.interprateConversionTemplate(
                              c, dstFormResult, GenericUtil.uInt(ptablePk), false, true);
                      if (GenericUtil.isEmpty(mq)) {
                        formResult
                            .getOutputMessages()
                            .add(
                                LocaleMsgCache.get2(
                                        0, (String) scd.get("locale"), "fw_conversion_sync_error")
                                    + "  ["
                                    + LocaleMsgCache.get2(scd, c.getDsc())
                                    + "]");
                        continue;
                      }
                      for (String k : mq.keySet())
                        if (!k.startsWith("_")) m.put(k + prefix2, mq.get(k).toString());

                      postForm4Table(dstFormResult, prefix2, new HashSet());
                      if (dstFormResult.getErrorMap().isEmpty()) {
                        co.setVersionNo(co.getVersionNo() + 1);
                        co.setVersionUserId((Integer) scd.get("userId"));
                        dao.updateObject(co);

                        if (FrameworkSetting.liveSyncRecord)
                          formResult.addSyncRecordAll(dstFormResult.getListSyncAfterPostHelper());
                      } else {
                        // cok dogru degil
                        // throw new PromisException("security","Form", 0, null,
                        // PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_conversion_auto_synch_error")+  "  2- ["+PromisLocaleMsg.get2(scd,c.getDsc())+"]: " + PromisUtil.fromMapToHtmlString(dstFormResult.getErrorMap()), null);
                      }
                    }
                } else {
                  formResult
                      .getOutputMessages()
                      .add(
                          LocaleMsgCache.get2(scd, "fw_conversion_msg_for")
                              + " ["
                              + LocaleMsgCache.get2(scd, c.getDsc())
                              + "]: "
                              + LocaleMsgCache.get2(scd, "fw_max_number_of_conversion"));
                  continue; // daha fazla conversion yok, sinira ulasmis
                }
              }
            }
            if (!GenericUtil.hasPartInside2(c.getActionTips(), action))
              continue; // bulamadi veya buldugunun action'i uygun degil

            Map<String, String> m = new HashMap();
            m.put("_cnvId", "" + conversionId);
            //				m.put("_cnvGroupId",""+advancedConditionGroupId);
            //				m.put("_cnvDetailTableIds",""+advancedConditionGroupId);
            m.put("_fid", "" + c.getDstFormId());
            m.put("_cnvTblPk", ptablePk);
            m.put("_cnvDsc", c.getDsc());
            m.put(".w", requestParams.get(".w"));
            if (c.getPreviewFlag() != 0) { // simdi gonderilmeyecek, formda geri donecek
              if (previewConversionMapList == null) {
                previewConversionMapList = new ArrayList();
                formResult.setPreviewConversionMapList(previewConversionMapList);
              }
              previewConversionMapList.add(m);
              continue;
            } else {
              m.put("a", "2");
              W5FormResult dstFormResult =
                  dao.getFormResult(scd, c.getDstFormId(), 2, requestParams);
              Map mq =
                  dao.interprateConversionTemplate(
                      c, dstFormResult, GenericUtil.uInt(ptablePk), false, false);
              if (GenericUtil.isEmpty(mq)) {
                formResult
                    .getOutputMessages()
                    .add(
                        LocaleMsgCache.get2(
                                0, (String) scd.get("locale"), "fw_conversion_auto_error")
                            + " 1- ["
                            + LocaleMsgCache.get2(scd, c.getDsc())
                            + "]");
                continue;
              }
              m.putAll(mq);

              if (GenericUtil.isEmpty(c.getRhinoCode()) || c.getRhinoCode().startsWith("!")) {
                W5FormResult newFormResult = postForm4Table(scd, c.getDstFormId(), 2, m, prefix);
                if (newFormResult.getErrorMap().isEmpty()) {
                  formResult
                      .getOutputMessages()
                      .add(
                          LocaleMsgCache.get2(
                                  0, (String) scd.get("locale"), "fw_conversion_auto_done")
                              + " ["
                              + LocaleMsgCache.get2(scd, c.getDsc())
                              + "]");
                  convertedAny = true;

                  if (FrameworkSetting.liveSyncRecord)
                    formResult.addSyncRecordAll(newFormResult.getListSyncAfterPostHelper());
                  if (!GenericUtil.isEmpty(c.getRhinoCode())) {
                    if (!GenericUtil.isEmpty(newFormResult.getOutputFields()))
                      for (String s : newFormResult.getOutputFields().keySet())
                        if (newFormResult.getOutputFields().get(s) != null)
                          m.put("out." + s, newFormResult.getOutputFields().get(s).toString());
                    dao.executeRhinoScript(scd, requestParams, c.getRhinoCode(), m, "result");
                  }
                } else {
                  if (FrameworkSetting.debug) System.out.println(newFormResult.getErrorMap());
                  if (c.getRowErrorStrategyTip() == 1)
                    throw new IWBException(
                        "security",
                        "Form",
                        0,
                        null,
                        LocaleMsgCache.get2(
                                0, (String) scd.get("locale"), "fw_conversion_auto_error")
                            + "  2- ["
                            + LocaleMsgCache.get2(scd, c.getDsc())
                            + "]",
                        null);
                  else {
                    formResult
                        .getOutputMessages()
                        .add(
                            LocaleMsgCache.get2(
                                    0, (String) scd.get("locale"), "fw_conversion_auto_error")
                                + "  3- ["
                                + LocaleMsgCache.get2(scd, c.getDsc())
                                + "]");
                    /*if(previewConversionMapList==null){ //TODO aslinda hata durumunda preivew acsin mi denilebilir. bir dusun
                    	previewConversionMapList= new ArrayList();
                    	formResult.setPreviewConversionMapList(previewConversionMapList);
                    }
                    previewConversionMapList.add(m);*/
                  }
                }
              } else {
                dao.executeRhinoScript(scd, requestParams, c.getRhinoCode(), m, "result");
              }
            }
          } else if (c.getSrcDstTip() == 1) { // Table -> Ws Method
            if (formResult.getMapConvertedObject() != null) {
              List<W5ConvertedObject> lco =
                  formResult.getMapConvertedObject().get(c.getConversionId());
              if (lco != null
                  && c.getMaxNumofConversion() > 0
                  && c.getMaxNumofConversion() <= lco.size()) { // yapilmayacak
                continue;
              }
            }

            W5WsMethod wsm = FrameworkCache.getWsMethod(scd, c.getDstTableId());
            if (wsm.get_params() == null) {
              wsm.set_params(
                  dao.find(
                      "from W5WsMethodParam t where t.wsMethodId=? AND t.projectUuid=? order by t.tabOrder",
                      wsm.getWsMethodId(),
                      (String) scd.get("projectId")));
            }

            Map mq =
                dao.interprateConversionTemplate4WsMethod(
                    scd, requestParams, c, GenericUtil.uInt(ptablePk), wsm);
            if (GenericUtil.isEmpty(mq)) {
              formResult
                  .getOutputMessages()
                  .add(
                      LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_conversion_auto_error")
                          + " 1- ["
                          + LocaleMsgCache.get2(scd, c.getDsc())
                          + "]");
              continue;
            }
            if (GenericUtil.isEmpty(c.getRhinoCode()) || c.getRhinoCode().startsWith("!")) {
              Map result =
                  REST(
                      scd,
                      wsm.get_ws().getDsc() + "." + wsm.getDsc(),
                      mq); // TODO: result ne yapayim
              if (result != null) {
                if (result.containsKey("faultcode"))
                  throw new IWBException(
                      "rhino",
                      "Conversion",
                      c.getConversionId(),
                      wsm.get_ws().getDsc() + "." + wsm.getDsc(),
                      result.get("faulcode") + ": " + result.get("faultstring"),
                      null);
                else {
                  if (formResult.getOutputFields() == null)
                    formResult.setOutputFields(new HashMap());
                  formResult.getOutputFields().putAll(result);
                }
                mq.putAll(result);
              }
            }
            if (!GenericUtil.isEmpty(c.getRhinoCode())) {
              dao.executeRhinoScript(scd, requestParams, c.getRhinoCode(), mq, "result");
            }
          }
        } catch (IWBException e) {
          if (c.getRowErrorStrategyTip() == 1 && cleanConversion) // throw e;
          throw new IWBException(
                "framework",
                "Conversion",
                conversionId,
                null,
                "[707," + conversionId + "] " + c.getDsc(),
                e);
          //				if(FrameworkSetting.debug)e.printStackTrace();
          formResult
              .getOutputMessages()
              .add("CONVERSION(" + conversionId + ") EXCEPTION: " + e.getMessage());
        } catch (Exception e) {
          if (c.getRowErrorStrategyTip() == 1 && cleanConversion)
            throw new IWBException(
                "framework",
                "Conversion",
                conversionId,
                null,
                "[707," + conversionId + "] " + c.getDsc(),
                e);
          //					throw new IWBException("framework","Conversion", conversionId, null,
          // LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_conversion_auto_error")+  "  3-
          // ["+e.getMessage()+"]", null);
          //				if(FrameworkSetting.debug)e.printStackTrace();
          formResult
              .getOutputMessages()
              .add("CONVERSION(" + conversionId + ") EXCEPTION: " + e.getMessage());
        }
      return convertedAny;
    } else return false;
  }

  public Map<String, Object> getFormCellCode(
      Map<String, Object> scd, Map<String, String> requestParams, int formCellId, int count) {
    /*		int formId = (Integer)dao.getCustomizedObject("select t.formId from W5FormCell t where t.formCellId=? AND t.customizationId=?", formCellId, (Integer)scd.get("customizationId"),"FormElement");
    		W5FormResult	formResult = dao.getFormResult(scd, formId, 2, requestParams);
    		for(W5FormCell fc:formResult.getForm().get_formCells())if(fc.getFormCellId()==formCellId){
    			if(GenericUtil.uInt(fc.getLookupIncludedParams())==0)break;
    			String res="";
    			Map m = new HashMap();
    			m.put("success", true);
    			for(W5FormCellCodeDetail fccd:fc.get_formCellCodeDetailList())switch(fccd.getCodeTip()){
    			case	1:res+=fccd.getCodeLength()>0 ? GenericUtil.lPad(fccd.getDefaultValue(),fccd.getCodeLength(),fccd.getFillCharacter()) : fccd.getDefaultValue();break; //sabit
    			case	2:// manuel(klavyeden)
    			case	5:// manuel(combo)
    			case	7:// keyword(combo)
    				m.put("msg", LocaleMsgCache.get2(scd, "js_manual_entry"));
    				return m;
    			case	3://Otomatik
    				Map<String, Object> qmz = dao.runSQLQuery2Map("select iwb.fnc_form_cell_code_detail_auto(${scd.customizationId},"+fccd.getFormCellCodeDetailId()+",'"+res+"') dsc from dual", scd, requestParams, null);
    				if(!GenericUtil.isEmpty(qmz)){
    					String val = qmz.values().toArray()[0].toString();
    					res+= fccd.getCodeLength()>0 ? GenericUtil.lPad(val,fccd.getCodeLength(),fccd.getFillCharacter()) : val;
    				} else
    					throw new IWBException("validation","FormElement", formCellId, null, "FormElementCode: wrong automatic definition", null);
    				break;
    			case	4://Formul/Advanced/SQL
    				String sql = fccd.getDefaultValue();
    				switch(fccd.getSourceFcQueryFieldId()){
    				case	1:case	2:case	3:
    					String[] qrs=new String[]{"yy","yymm","yymmdd"};
    					sql="select to_char(current_date,'"+qrs[fccd.getSourceFcQueryFieldId()-1]+"')";
    				default:
    					Map<String, Object> qm = dao.runSQLQuery2Map(sql, scd, requestParams, null);
    					if(!GenericUtil.isEmpty(qm)){
    						String val = qm.values().toArray()[0].toString();
    						res+=fccd.getCodeLength()>0 ? GenericUtil.lPad(val,fccd.getCodeLength(),fccd.getFillCharacter()) : val;
    					} else
    						throw new IWBException("validation","FormElement", formCellId, null, "FormElementCode: wrong SQL code 4 (Formul/Advanced)", null);
    					}
    				break;
    			case	6://formdan(combo)
    				String fdsc=null;
    				if(fccd.getSourceFcQueryFieldId()!=0){
    					List<String> lqf = (List<String>)dao.find("select qf.dsc from W5QueryField qf where qf.queryFieldId=?",fccd.getSourceFcQueryFieldId());
    					if(GenericUtil.isEmpty(lqf))
    						return m;	//TODO: not implemented (query'yi calistirip ordaki degeri almak lazim)
    					fdsc=lqf.get(0);
    				}
    				boolean notFound=true;
    				for(W5FormCell fc2:formResult.getForm().get_formCells())if(fc2.getFormCellId()==fccd.getSourceFormCellId()){
    					String sfcv = requestParams.get(fc2.getDsc());
    					if(GenericUtil.isEmpty(sfcv)){
    						m.put("msg", LocaleMsgCache.get2(scd, fc2.getLocaleMsgKey()) + LocaleMsgCache.get2(scd, "js_value_not_set"));
    						return m;
    					}
    					if(fdsc==null)
    						res+=fccd.getCodeLength()>0 ? GenericUtil.lPad(sfcv,fccd.getCodeLength(),fccd.getFillCharacter()) : sfcv;
    					else switch(fc2.getControlTip()){
    					case	7:case	10:
    						Map m2 = new HashMap();
    //						m2.put("_qid", ""+fc2.getLookupQueryId());
    						m2.put("xid", sfcv);
    						W5QueryResult qr2 = executeQuery(scd, fc2.getLookupQueryId(), m2);
    						if(GenericUtil.isEmpty(qr2.getErrorMap()) && !GenericUtil.isEmpty(qr2.getData())){
    							List<Object[]> l3 = qr2.getData();
    							int rTabOrder = -1, iTabOrder = -1;
    							for(W5QueryField qq:qr2.getNewQueryFields())if(qq.getDsc().equals(fdsc)){
    								rTabOrder=qq.getTabOrder();
    								if(iTabOrder!=-1)break;
    							} else if(qq.getDsc().equals("id")){
    								iTabOrder=qq.getTabOrder();
    								if(rTabOrder!=-1)break;
    							}
    							if(rTabOrder == -1 || iTabOrder ==-1){
    								throw new IWBException("validation","FormElement", formCellId, null, "FormElementCode: wrong SourceFormCell definition(Formdan/Combo) init (rTabOrder="+rTabOrder+",iTabOrder="+iTabOrder+")", null);
    							}
    							boolean found = false;
    							for(Object[] o2:l3)if(o2[iTabOrder-1].toString().equals(sfcv)){
    								res+=fccd.getCodeLength()>0 ? GenericUtil.lPad(o2[rTabOrder-1].toString(),fccd.getCodeLength(),fccd.getFillCharacter()) : o2[rTabOrder-1].toString();
    								found = true;
    								break;
    							}
    							if(!found){
    								throw new IWBException("validation","FormElement", formCellId, null, "FormElementCode: (Formdan/Combo) value note found (rTabOrder="+rTabOrder+",iTabOrder="+iTabOrder+")", null);
    							}
    						} else
    							return m;
    						break;
    					case	9:
    						throw new IWBException("validation","FormElement", formCellId, null, "FormElementCode: wrong SourceFormCell definiton (Formdan/Combo) is remote", null);
    					default:
    						res+=fccd.getCodeLength()>0 ? GenericUtil.lPad(sfcv,fccd.getCodeLength(),fccd.getFillCharacter()) : sfcv;
    					}
    					notFound = false;
    					break;
    				}
    				if(notFound)
    					throw new IWBException("validation","FormElement", formCellId, null, "FormElementCode: wrong SourceFormCell (Formdan/Combo)", null);
    				break;
    			}
    			m.put("result", res);
    			return m;
    		}
    		throw new IWBException("validation","FormElement", formCellId, null, "FormElementCode: wrong FormCellId", null);
    		*/
    return null;
  }

  public W5FormResult postForm4Table(
      Map<String, Object> scd,
      int formId,
      int action,
      Map<String, String> requestParams,
      String prefix) {
    if (formId == 0 && GenericUtil.uInt(requestParams.get("_tb_id")) != 0) {
      W5Table t = FrameworkCache.getTable(scd, GenericUtil.uInt(requestParams.get("_tb_id")));
      formId = t.getDefaultUpdateFormId();
      requestParams.put(t.get_tableParamList().get(0).getDsc(), requestParams.get("_tb_pk"));
      action = 1;
      requestParams.put("a", "1");
    }
    W5FormResult mainFormResult = dao.getFormResult(scd, formId, action, requestParams);
    boolean dev =
        scd.get("roleId") != null
            && (Integer) scd.get("roleId") == 0
            && GenericUtil.uInt(requestParams, "_dev") != 0;
    int customizationId = dev ? 0 : (Integer) scd.get("customizationId");
    W5Table t =
        FrameworkCache.getTable(
            scd,
            mainFormResult.getForm().getObjectId()); // mainFormResult.getForm().get_sourceTable();
    if (t.getAccessViewTip() == 0
        && (!FrameworkCache.roleAccessControl(scd, 0)
            || !FrameworkCache.roleAccessControl(scd, action))) {
      throw new IWBException(
          "security",
          "Module",
          0,
          null,
          LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"),
          null);
    }
    if (t.getAccessViewUserFields() == null
        && !GenericUtil.accessControl(
            scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())) {
      throw new IWBException(
          "security",
          "Form",
          formId,
          null,
          LocaleMsgCache.get2(
              0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"),
          null);
    }
    Set<String> checkedParentRecords = new HashSet<String>();
    mainFormResult.setQueuedActionList(
        postForm4Table(mainFormResult, prefix, checkedParentRecords));
    if (!mainFormResult.getErrorMap().isEmpty()) return mainFormResult;
    requestParams.remove(
        "_fid1"); // TODO: daha iyisi yapılana kadar en iyisi bu. Form extended işleminde a1.1 gibi
    // değerler bir kez daha gönderiliyordu.
    if (mainFormResult.getForm().get_moduleList() != null) {
      for (W5FormModule m : mainFormResult.getForm().get_moduleList())
        if (m.getModuleTip() == 4
            && GenericUtil.accessControl(
                scd,
                m.getAccessViewTip(),
                m.getAccessViewRoles(),
                m.getAccessViewUsers())) { // form imis
          if (m.getModuleViewTip() == 0
              || (m.getModuleViewTip() == 1 && action == 1)
              || (m.getModuleViewTip() == 2 && action == 2)) {
            int newAction = GenericUtil.uInt(requestParams.get("a" + m.getTabOrder()));
            if (newAction == 0) newAction = action;
            W5FormResult subFormResult =
                dao.getFormResult(scd, m.getObjectId(), newAction, requestParams);
            t =
                FrameworkCache.getTable(
                    scd,
                    mainFormResult
                        .getForm()
                        .getObjectId()); // mainFormResult.getForm().get_sourceTable();
            if ((t.getAccessViewTip() == 0 && !FrameworkCache.roleAccessControl(scd, 0))
                || (!GenericUtil.accessControl(
                    scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers()))) {
              // nothing
            } else {
              postForm4Table(subFormResult, prefix, checkedParentRecords);
              if (!subFormResult.getErrorMap().isEmpty()) {
                throw new IWBException(
                    "validation",
                    "Form",
                    m.getObjectId(),
                    null,
                    LocaleMsgCache.get2(
                            0, (String) scd.get("locale"), "fw_validation_error_detail_form")
                        + ": "
                        + GenericUtil.fromMapToJsonString(subFormResult.getErrorMap()),
                    null);
              }
              if (!GenericUtil.isEmpty(subFormResult.getOutputFields())
                  && !GenericUtil.isEmpty(mainFormResult.getOutputFields()))
                mainFormResult.getOutputFields().putAll(subFormResult.getOutputFields());
            }
          }
        }
    }
    if ((action == 1 || action == 3)
        && FrameworkSetting
            .liveSyncRecord) { // TODO edit eden diger kullanicilara bildirilmesi gerekiyor, boyle
      // bir kaydın guncellendigi/sildigi ve user'in kapattigi
      String webPageId = requestParams.get(".w");
      String tabId = requestParams.get(".t ");
    }
    return mainFormResult;
  }

  public List<W5ReportCellHelper> getGridReportResult(
      Map<String, Object> scd, int gridId, String gridColumns, Map<String, String> requestParams) {
    String xlocale = (String) scd.get("locale");
    W5GridResult gridResult = dao.getGridResult(scd, gridId, requestParams, true);
    int queryId = gridResult.getGrid().getQueryId();
    requestParams.remove("firstLimit");
    requestParams.remove("limit");
    requestParams.remove("start");
    requestParams.put("grid_report_flag", "1");
    W5QueryResult queryResult = executeQuery(scd, queryId, requestParams);

    if (queryResult.getErrorMap().isEmpty()) {

      List<W5ReportCellHelper> list = new ArrayList<W5ReportCellHelper>();
      //			list.add(new WReportResult(row_id, column_id, deger, row_tip, cell_tip, colspan, tag));
      list.add(
          new W5ReportCellHelper(
              0,
              1,
              LocaleMsgCache.get2(
                  (Integer) scd.get("customizationId"),
                  xlocale,
                  gridResult.getGrid().getLocaleMsgKey()),
              (short) 0,
              (short) 1,
              (short) 20,
              "1;30,70")); // baslik: id, font_size, dsc, row_tip, yatay?,
      list.add(
          new W5ReportCellHelper(
              1,
              1,
              GenericUtil.uFormatDateTime(new Date()),
              (short) 1,
              (short) 1,
              (short) -1,
              LocaleMsgCache.get2(scd, "report_time"))); // param: id, sira, dsc, row_tip,

      Map<String, W5QueryField> m1 = new HashMap<String, W5QueryField>();
      for (W5QueryField f : queryResult.getNewQueryFields()) {
        m1.put(f.getDsc(), f);
      }
      Map<Integer, W5GridColumn> m2 = new HashMap<Integer, W5GridColumn>();
      int nxtTmp = -1;
      for (W5GridColumn c : gridResult.getGrid().get_gridColumnList()) {
        if (c.getQueryFieldId() == 0) {
          c.setQueryFieldId(nxtTmp--);
        }
        m2.put(c.getQueryFieldId(), c);
      }
      List<W5QueryField> l1 = new ArrayList<W5QueryField>(queryResult.getNewQueryFields().size());
      String[] gcs = gridColumns.split(";");
      int startRow = 2;
      int startCol = 0;
      for (int g = 0; g < gcs.length; g++) {
        String[] cs = gcs[g].split(",");

        W5QueryField f = m1.get(cs[0]);
        if (f != null) {
          W5GridColumn c = m2.get(f.getQueryFieldId());
          if (f.getDsc()
              .equals(
                  FieldDefinitions.queryFieldName_Approval)) { // onay varsa,raporda gorunmesi icin
            c = new W5GridColumn();
            c.setLocaleMsgKey(
                LocaleMsgCache.get2(
                    (Integer) scd.get("customizationId"), xlocale, "approval_status"));
            c.setAlignTip((short) 1);
          }
          if (c != null) {
            list.add(
                new W5ReportCellHelper(
                    startRow,
                    (startCol + 1),
                    LocaleMsgCache.get2(
                        (Integer) scd.get("customizationId"), xlocale, c.getLocaleMsgKey()),
                    (short) 2,
                    (short) GenericUtil.uInt(cs[1]),
                    c.getAlignTip(),
                    "")); // column: id, font_size, dsc, row_tip,
            l1.add(f);
            startCol++;
          }
        }
      }

      int customizationId = (Integer) scd.get("customizationId");
      // startRow = gcs.length + 2;
      startRow = 3;
      for (int i = 0; i < queryResult.getData().size(); i++) {
        int g = 1;
        for (W5QueryField f : l1)
          if (f.getTabOrder() > 0) {
            String dataType = "";
            Object obj = queryResult.getData().get(i)[f.getTabOrder() - 1];
            if (obj != null && f.getDsc().equals(FieldDefinitions.queryFieldName_Approval)) {
              String[] ozs =
                  ((String) queryResult.getData().get(i)[f.getTabOrder() - 1]).split(";");
              int appId =
                  GenericUtil.uInt(ozs[1]); // approvalId: kendisi yetkili ise + , aksi halde -
              int appStepId = GenericUtil.uInt(ozs[2]); // approvalStepId
              W5Workflow appr = FrameworkCache.getWorkflow(scd, appId);
              String appStepDsc = "";
              if (appr != null
                  && appr.get_approvalStepMap().get(Math.abs(appStepId)).getNewInstance() != null)
                appStepDsc =
                    appr.get_approvalStepMap().get(Math.abs(appStepId)).getNewInstance().getDsc();
              obj = (LocaleMsgCache.get2(scd, appStepDsc));
            }
            String res = null;
            if (obj != null) {
              res = obj.toString();

              if (f.getDsc().contains("_flag")) {
                res = GenericUtil.uInt(res) != 0 ? "x" : "o";
              } else {
                switch (f.getPostProcessTip()) {
                  case 20:
                    res = UserUtil.getUserName(GenericUtil.uInt(obj));
                    break;
                  case 53:
                    res = UserUtil.getUserDsc(GenericUtil.uInt(obj));
                    break;
                  case 10:
                  case 11: // demek ki lookup'li deger tutulacak
                    W5LookUp lookUp = FrameworkCache.getLookUp(scd, f.getLookupQueryId());
                    if (lookUp != null) {
                      String[] ids = res.split(",");
                      res = "";
                      for (String sz : ids) {
                        res += ", ";
                        W5LookUpDetay d = lookUp.get_detayMap().get(sz);
                        if (d != null) {
                          String s = d.getDsc();
                          if (s != null) {
                            res += LocaleMsgCache.get2(scd, s);
                          }
                        } else if (d == null
                            && f.getLookupQueryId()
                                == 12) { // lookup static or lookup static(multi) ve empty
                          for (W5QueryField ff : queryResult.getNewQueryFields()) {
                            if (ff.getDsc().compareTo(f.getDsc() + "_qw_") == 0) {
                              res += queryResult.getData().get(i)[ff.getTabOrder() - 1];
                              break;
                            }
                          }
                        } else {
                          res += "???: " + sz;
                        }
                      }
                      if (res.length() > 0) res = res.substring(1);
                    }
                    break;
                  case 12:
                    for (W5QueryField ff : queryResult.getNewQueryFields()) {
                      if (ff.getDsc().compareTo(f.getDsc() + "_qw_") == 0) {
                        res =
                            queryResult.getData().get(i)[ff.getTabOrder() - 1] != null
                                ? queryResult.getData().get(i)[ff.getTabOrder() - 1].toString()
                                : "";
                        break;
                      }
                    }
                    break;

                  default:
                    if (f.getFieldTip() == 3 || f.getFieldTip() == 4) {
                      dataType = "T:1";
                    }
                    ;
                    for (W5QueryField ff : queryResult.getNewQueryFields()) {
                      if (ff.getDsc().compareTo(f.getDsc() + "_qw_") == 0) {
                        res =
                            queryResult.getData().get(i)[ff.getTabOrder() - 1] != null
                                ? queryResult.getData().get(i)[ff.getTabOrder() - 1].toString()
                                : "";
                        break;
                      }
                    }
                    break;
                }
              }
            }
            list.add(
                new W5ReportCellHelper(
                    i + startRow,
                    g++,
                    res,
                    (short) 3,
                    (short) 0,
                    (short) 1,
                    dataType)); // data: id, font_size, dsc, row_tip,
          }
      }
      return list;
    }
    return null;
  }

  private Map<String, Object> executeQuery2Map(
      Map<String, Object> scd, int queryId, Map<String, String> requestParams) {
    W5QueryResult queryResult = dao.getQueryResult(scd, queryId);
    queryResult.setErrorMap(new HashMap());
    queryResult.setRequestParams(requestParams);
    queryResult.prepareQuery(null);
    if (queryResult.getErrorMap().isEmpty()) {
      QueryTrigger.beforeExecuteQuery(queryResult, dao);
      Map<String, Object> m =
          dao.runSQLQuery2Map(
              queryResult.getExecutedSql(),
              queryResult.getSqlParams(),
              queryResult.getQuery().get_queryFields());
      QueryTrigger.afterExecuteQuery(queryResult, dao);
      return m;
    }
    return null;
  }

  private void checkTenant(Map<String, Object> scd) {
    W5Project po = FrameworkCache.getProject(scd);
    if (po != null) dao.executeUpdateSQLQuery("set search_path=" + po.getRdbmsSchema());
  }

  public W5QueryResult executeQuery(
      Map<String, Object> scd, int queryId, Map<String, String> requestParams) {
    boolean developer = scd.get("roleId") != null && GenericUtil.uInt(scd.get("roleId")) != 0;
    W5QueryResult queryResult = dao.getQueryResult(scd, queryId);
    if (queryId != 1
        && queryId != 824
        && queryResult.getMainTable() != null
        && (!FrameworkSetting.debug || developer)) {
      switch (queryResult.getQuery().getQuerySourceTip()) {
        case 0:
        case 15:
        case 8: // rhino, rdb table, group of query
          W5Table t = queryResult.getMainTable();
          if (t.getAccessViewTip() == 0 && !FrameworkCache.roleAccessControl(scd, 0)) {
            throw new IWBException(
                "security",
                "Module",
                0,
                null,
                LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"),
                null);
          }
          if (t.getAccessViewUserFields() == null
              && !GenericUtil.accessControl(
                  scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())) {
            throw new IWBException(
                "security",
                "Query",
                queryId,
                null,
                LocaleMsgCache.get2(
                    0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"),
                null);
          }

          break;
      }
    }
    /*		StringBuilder tmpx = new StringBuilder("ali baba ${obj.dsc} ve 40 haramiler ${lnk.pk_query_field_id.dsc} olmus");
    dao.interprateTemplate(scd, 5,1294, tmpx, true); */

    checkTenant(scd);
    queryResult.setErrorMap(new HashMap());
    queryResult.setRequestParams(requestParams);
    switch (queryResult.getQuery().getQuerySourceTip()) {
      case 2709: // TSDB (Influx)
        W5TsMeasurement tsm = getTsMeasurement(scd, queryResult.getQuery().getMainTableId());
        if (tsm != null
            && !GenericUtil.accessControl(
                scd, tsm.getAccessViewTip(), tsm.getAccessViewRoles(), tsm.getAccessViewUsers())) {
          throw new IWBException(
              "security",
              "Query",
              queryId,
              null,
              LocaleMsgCache.get2(
                  0, (String) scd.get("locale"), "fw_access_control_ts_measurement"),
              null);
        }
        queryResult.setMainTsMeasurement(tsm);
        queryResult.setFetchRowCount(
            GenericUtil.uIntNvl(
                requestParams, "limit", GenericUtil.uInt(requestParams, "firstLimit")));
        queryResult.setStartRowNumber(GenericUtil.uInt(requestParams, "start"));
        // influxDao.runQuery(FrameworkCache.wProjects.get(scd.get("projectId")), queryResult);
        break;
      case 1376: // WS Method
        W5WsMethod wsm = FrameworkCache.getWsMethod(scd, queryResult.getQuery().getMainTableId());
        if (wsm.get_params() == null) {
          wsm.set_params(
              dao.find(
                  "from W5WsMethodParam t where t.wsMethodId=? AND t.projectUuid=? order by t.tabOrder",
                  wsm.getWsMethodId(),
                  (String) scd.get("projectId")));
          wsm.set_paramMap(new HashMap());
          for (W5WsMethodParam wsmp : wsm.get_params())
            wsm.get_paramMap().put(wsmp.getWsMethodParamId(), wsmp);
        }
        W5WsMethodParam parentParam = null;
        for (W5WsMethodParam px : wsm.get_params())
          if (px.getOutFlag() != 0 && px.getParamTip() == 10) {
            parentParam = px;
            break;
          }
        Map<String, String> m2 = new HashMap();
        if (requestParams.get("filter[value]") != null) {
          requestParams.put("xdsc", requestParams.get("filter[value]"));
          requestParams.remove("filter[value]");
        }
        for (W5QueryParam qp : queryResult.getQuery().get_queryParams())
          if (!GenericUtil.isEmpty(requestParams.get(qp.getDsc()))) {
            m2.put(qp.getExpressionDsc(), requestParams.get(qp.getDsc()));
          }
        StringBuilder rc = new StringBuilder();
        rc.append("function _x_(x){\nreturn {")
            .append(queryResult.getQuery().getSqlSelect())
            .append("\n}}\nvar result=[], q=$iwb.REST('")
            .append(wsm.get_ws().getDsc() + "." + wsm.getDsc())
            .append("',")
            .append(GenericUtil.fromMapToJsonString2(m2))
            .append(");\nif(q && q.get('success')){q=q.get('")
            .append(parentParam.getDsc())
            .append("');for(var i=0;i<q.size();i++)result.push(_x_(q.get(i)));}");
        dao.executeQueryAsRhino(queryResult, rc.toString());
        break;

      case 0: // Rhino Query
        dao.executeQueryAsRhino(queryResult, null);
        break;
      default:
        queryResult.setViewLogModeTip((short) GenericUtil.uInt(requestParams, "_vlm"));

        //
        //	queryResult.setOrderBy(PromisUtil.uStrNvl(requestParams.get(PromisUtil.uStrNvl(PromisSetting.appSettings.get("sql_sort"),"sort")), queryResult.getQuery().getSqlOrderby()));
        if (!GenericUtil.isEmpty(requestParams.get("sort"))) {
          if (requestParams.get("sort").equals(FieldDefinitions.queryFieldName_Comment)) {
            //	queryResult.setOrderBy("coalesce((select qz.last_comment_id from
            // iwb.w5_comment_summary qz where qz.table_id= AND qz.table_pk= AND
            // qz.customization_id=),0) DESC");
            queryResult.setOrderBy(
                FieldDefinitions.queryFieldName_Comment); // + " " + requestParams.get("dir")
          } else if (!requestParams.get("sort").contains("_qw_")) {
            queryResult.setOrderBy(requestParams.get("sort"));
            if (requestParams.get("dir") != null) {
              if (queryResult.getMainTable() != null)
                for (W5QueryField f : queryResult.getQuery().get_queryFields())
                  if (queryResult.getOrderBy().equals(f.getDsc())) {
                    if (f.getMainTableFieldId() != 0
                        && queryResult
                            .getMainTable()
                            .get_tableFieldMap()
                            .containsKey(f.getMainTableFieldId())) {
                      queryResult.setOrderBy("x." + queryResult.getOrderBy());
                    }
                    break;
                  }
              //					queryResult.setOrderBy(((!queryResult.getQuery().getSqlFrom().contains(",") &&
              // !queryResult.getQuery().getSqlFrom().contains("join") &&
              // queryResult.getQuery().getSqlFrom().contains(" x")) ? "x." : "") +
              // queryResult.getOrderBy() + " " + requestParams.get("dir"));
              queryResult.setOrderBy(queryResult.getOrderBy() + " " + requestParams.get("dir"));
            }
          } else queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());
        } else queryResult.setOrderBy(queryResult.getQuery().getSqlOrderby());
        switch (queryResult.getQuery().getQueryTip()) {
          case 9:
          case 10:
            queryResult.prepareTreeQuery(null);
            break;
          case 15:
            queryResult.prepareDataViewQuery(null);
            break;
          default:
            queryResult.prepareQuery(null);
        }
        if (queryResult.getErrorMap().isEmpty()) {
          QueryTrigger.beforeExecuteQuery(queryResult, dao);
          queryResult.setFetchRowCount(
              GenericUtil.uIntNvl(
                  requestParams, "limit", GenericUtil.uInt(requestParams, "firstLimit")));
          queryResult.setStartRowNumber(GenericUtil.uInt(requestParams, "start"));
          dao.runQuery(queryResult);
          if (queryResult.getQuery().getShowParentRecordFlag() != 0
              && queryResult.getData() != null) {
            for (Object[] oz : queryResult.getData()) {
              int tableId = GenericUtil.uInt(oz[queryResult.getQuery().get_tableIdTabOrder() - 1]);
              int tablePk = GenericUtil.uInt(oz[queryResult.getQuery().get_tablePkTabOrder() - 1]);
              if (tableId != 0 && tablePk != 0)
                oz[oz.length - 1] = dao.findRecordParentRecords(scd, tableId, tablePk, 0, true);
            }
          }
          QueryTrigger.afterExecuteQuery(queryResult, dao);
        }
    }

    // postProcessFields : LookupQuery Control
    if (queryResult.getErrorMap().isEmpty()
        && !GenericUtil.isEmpty(queryResult.getNewQueryFields())
        && !GenericUtil.isEmpty(queryResult.getData())) {
      Map<Integer, W5QueryResult> qrm = new HashMap(); // cache
      for (W5QueryField qf : queryResult.getNewQueryFields())
        if ((qf.getPostProcessTip() == 16 || qf.getPostProcessTip() == 17)
            && qf.getLookupQueryId() != 0) { // LookupQuery den alinacak
          W5QueryResult lookupQueryResult = qrm.get(qf.getLookupQueryId());
          if (lookupQueryResult == null) {
            lookupQueryResult = dao.getQueryResult(scd, qf.getLookupQueryId());
            lookupQueryResult.setErrorMap(new HashMap());
            lookupQueryResult.setRequestParams(new HashMap());
            lookupQueryResult.setOrderBy(lookupQueryResult.getQuery().getSqlOrderby());
            switch (lookupQueryResult.getQuery().getQuerySourceTip()) {
              case 1376: // WS Method
                W5WsMethod wsm =
                    FrameworkCache.getWsMethod(scd, lookupQueryResult.getQuery().getMainTableId());
                if (wsm.get_params() == null) {
                  wsm.set_params(
                      dao.find(
                          "from W5WsMethodParam t where t.wsMethodId=? AND t.projectUuid=? order by t.tabOrder",
                          wsm.getWsMethodId(),
                          (String) scd.get("projectId")));
                  wsm.set_paramMap(new HashMap());
                  for (W5WsMethodParam wsmp : wsm.get_params())
                    wsm.get_paramMap().put(wsmp.getWsMethodParamId(), wsmp);
                }
                W5WsMethodParam parentParam = null;
                for (W5WsMethodParam px : wsm.get_params())
                  if (px.getOutFlag() != 0 && px.getParamTip() == 10) {
                    parentParam = px;
                    break;
                  }
                Map<String, String> m2 = new HashMap();
                for (W5QueryParam qp : lookupQueryResult.getQuery().get_queryParams())
                  if (!GenericUtil.isEmpty(requestParams.get(qp.getDsc()))) {
                    m2.put(qp.getExpressionDsc(), requestParams.get(qp.getDsc()));
                  }
                StringBuilder rc2 = new StringBuilder();
                rc2.append("function _x_(x){\nreturn {")
                    .append(lookupQueryResult.getQuery().getSqlSelect())
                    .append("\n}}\nvar result=[], q=$iwb.REST('")
                    .append(wsm.get_ws().getDsc() + "." + wsm.getDsc())
                    .append("',")
                    .append(GenericUtil.fromMapToJsonString2(m2))
                    .append(");\nif(q && q.get('success')){q=q.get('")
                    .append(parentParam.getDsc())
                    .append("');for(var i=0;i<q.size();i++)result.push(_x_(q.get(i)));}");
                dao.executeQueryAsRhino(lookupQueryResult, rc2.toString());
                break;
              case 15: // table
                switch (lookupQueryResult.getQuery().getQueryTip()) {
                  case 12:
                    lookupQueryResult.prepareTreeQuery(new HashMap());
                    break; // lookup tree query
                  default:
                    lookupQueryResult.prepareQuery(new HashMap());
                }
                if (lookupQueryResult.getErrorMap().isEmpty()) {
                  dao.runQuery(lookupQueryResult);
                }
            }
            qrm.put(qf.getLookupQueryId(), lookupQueryResult);
            ;
          }

          if (lookupQueryResult.getErrorMap().isEmpty()
              && !GenericUtil.isEmpty(lookupQueryResult.getData())) {
            for (W5QueryField qf2 : queryResult.getNewQueryFields())
              if (qf2.getDsc().equals(qf.getDsc() + "_qw_")) {
                int nqfi = qf2.getTabOrder() - 1;
                Map<String, Object[]> rmap = new HashMap();
                for (Object[] oo : lookupQueryResult.getData())
                  if (oo[0] != null && oo[1] != null) rmap.put(oo[1].toString(), oo);
                for (Object[] oo : queryResult.getData()) {
                  Object so = oo[qf.getTabOrder() - 1];
                  if (so != null) {
                    if (qf.getPostProcessTip() == 16) {
                      Object[] loo = rmap.get(so.toString()); // single
                      if (loo != null) {
                        oo[nqfi] = loo[0].toString();
                        if (loo.length > 2)
                          for (int zi = 2; zi < loo.length; zi++) {
                            oo[nqfi + zi - 1] = loo[zi];
                          }
                      }
                    } else {
                      String[] lso = so.toString().split(",");
                      StringBuilder sb = new StringBuilder();
                      for (String s : lso)
                        if (!GenericUtil.isEmpty(s)) {
                          Object[] loo = rmap.get(s);
                          if (loo != null) sb.append(loo[0]).append(",");
                        }
                      if (sb.length() > 0) {
                        sb.setLength(sb.length() - 1);
                        oo[nqfi] = sb.toString();
                      }
                    }
                  }
                }
                break;
              }
          }
        }
    }
    return queryResult;
  }

  /*public QueryResult executeInfluxQuery(Map<String, Object> scd, String sql, String dbName){
  	return influxDao.runQuery(FrameworkCache.wProjects.get((String)scd.get("projectId")), sql, dbName);
  }
  public void insertInfluxRecord(Map<String, Object> scd, String measurement, Map<String, Object> tags, Map<String, Object> fields, String date){
  	influxDao.insert(FrameworkCache.wProjects.get((String)scd.get("projectId")), measurement, tags, fields, date);
  }*/

  public W5PageResult getPageResult(
      Map<String, Object> scd, int pageId, Map<String, String> requestParams) {
    W5PageResult pr = null;
    try {
      boolean developer = scd.get("roleId") != null && (Integer) scd.get("roleId") == 0;
      boolean debugAndDeveloper = FrameworkSetting.debug && developer;
      pr = dao.getPageResult(scd, pageId);
      pr.setRequestParams(requestParams);
      pr.setPageObjectList(new ArrayList<Object>());
      List<W5PageObject> templateObjectListExt =
          new ArrayList<W5PageObject>(pr.getPage().get_pageObjectList().size() + 5);
      templateObjectListExt.addAll(pr.getPage().get_pageObjectList());

      requestParams.put("_dont_throw", "1");
      if (pageId == 238) { // Record Bazlı yetkilendirme gridi
        int objectId = GenericUtil.uInt(requestParams.get("_gid1"));
        if (objectId == 477) {
          W5Table t =
              FrameworkCache.getTable(scd, GenericUtil.uInt(requestParams.get("crud_table_id")));
          if (t.getAccessPermissionUserFields() != null)
            requestParams.put(
                t.get_tableParamList().get(0).getDsc(), requestParams.get("_table_pk"));
          // Kontrol atanmışsa ve kullanıcının yetkisi yoksa
          if (t.getAccessPermissionTip() == 1
              && !GenericUtil.accessControl(
                  scd,
                  t.getAccessPermissionTip(),
                  t.getAccessPermissionRoles(),
                  t.getAccessPermissionUsers())
              && (t.getAccessPermissionUserFields() == null
                  || dao.accessUserFieldControl(
                      t, t.getAccessPermissionUserFields(), scd, requestParams, null))) {
            throw new IWBException(
                "security",
                "Table",
                GenericUtil.uInt(requestParams.get("crud_table_id")),
                null,
                LocaleMsgCache.get2(
                    0, (String) scd.get("locale"), "fw_guvenlik_yetkilendirme_yetkisi"),
                null);
          }
        }
      }

      for (int i = 1;
          requestParams.containsKey("_gid" + i)
              || requestParams.containsKey("_fid" + i)
              || requestParams.containsKey("_dvid" + i)
              || requestParams.containsKey("_lvid" + i);
          i++) { // extra olarak _gid1=12&_gid=2 gibi seyler soylenebilir
        int objectId = GenericUtil.uInt(requestParams.get("_gid" + i)); // grid
        short objectTip = -1;
        if (objectId == 0) {
          objectId = GenericUtil.uInt(requestParams.get("_fid" + i)); // form
          objectTip = -3;
        }
        if (objectId == 0) {
          objectId = GenericUtil.uInt(requestParams.get("_dvid" + i)); // data view
          objectTip = -2;
        }
        if (objectId == 0) {
          objectId = GenericUtil.uInt(requestParams.get("_lvid" + i)); // list view
          objectTip = -7;
        }
        W5PageObject o = new W5PageObject();
        o.setObjectTip(objectTip);
        o.setObjectId(objectId);
        templateObjectListExt.add(o);
      }

      int objectCount = 0;
      if (pr.getPage().getTemplateTip() != 8) { // wizard'dan farkli ise
        W5Table masterTable = null;
        for (W5PageObject o : templateObjectListExt) {
          boolean accessControl =
              debugAndDeveloper
                  ? true
                  : GenericUtil.accessControl(
                      scd, o.getAccessViewTip(), o.getAccessViewRoles(), o.getAccessViewUsers());
          Object obz = null;
          W5Table mainTable = null;
          switch (Math.abs(o.getObjectTip())) {
            case 1: // grid
              W5GridResult gridResult =
                  dao.getGridResult(
                      scd, o.getObjectId(), requestParams, pageId == 298 /*|| objectCount!=0*/);
              if (pageId == 298) { // log template
                gridResult.setViewLogMode(true);
              }
              if (o.getObjectTip() < 0) {
                if (GenericUtil.uInt(requestParams, "_gid" + gridResult.getGridId() + "_a") != 0)
                  gridResult.setAction(
                      GenericUtil.uInt(requestParams, "_gid" + gridResult.getGridId() + "_a"));
                gridResult.setGridId(-gridResult.getGridId());
              }
              mainTable =
                  gridResult.getGrid() != null && gridResult.getGrid().get_query() != null
                      ? FrameworkCache.getTable(
                          scd, gridResult.getGrid().get_query().getMainTableId())
                      : null;
              if (!debugAndDeveloper
                  && mainTable != null
                  && (((mainTable.getAccessTips() == null
                          || mainTable.getAccessTips().indexOf("0") == -1)
                      && mainTable.getAccessViewUserFields() == null
                      && !GenericUtil.accessControl(
                          scd,
                          mainTable.getAccessViewTip(),
                          mainTable.getAccessViewRoles(),
                          mainTable.getAccessViewUsers())))) obz = gridResult.getGrid().getDsc();
              else {
                if (GenericUtil.uInt(requestParams, "_viewMode") != 0)
                  gridResult.setViewReadOnlyMode(true);
                else if (GenericUtil.uInt(requestParams, "_viewMode" + o.getObjectId()) != 0)
                  gridResult.setViewReadOnlyMode(true);
                obz = accessControl ? gridResult : gridResult.getGrid().getDsc();
              }
              if (obz instanceof W5GridResult) {
                Map m = new HashMap();
                gridResult.setTplObj(o);
                gridResult.setExtraOutMap(m);
                m.put("tplId", o.getTemplateId());
                m.put("tplObjId", o.getTemplateObjectId());
              }
              break;
            case 2: // card view
              W5CardResult cardResult =
                  dao.getDataViewResult(scd, o.getObjectId(), requestParams, objectCount != 0);
              if (o.getObjectTip() < 0) cardResult.setDataViewId(-cardResult.getDataViewId());
              mainTable =
                  cardResult.getCard() != null && cardResult.getCard().get_query() != null
                      ? FrameworkCache.getTable(
                          scd, cardResult.getCard().get_query().getMainTableId())
                      : null;
              if (!debugAndDeveloper
                  && mainTable != null
                  && (((mainTable.getAccessTips() == null
                          || mainTable.getAccessTips().indexOf("0") == -1)
                      && mainTable.getAccessViewUserFields() == null
                      && !GenericUtil.accessControl(
                          scd,
                          mainTable.getAccessViewTip(),
                          mainTable.getAccessViewRoles(),
                          mainTable.getAccessViewUsers())))) obz = cardResult.getCard().getDsc();
              else {
                obz = accessControl ? cardResult : cardResult.getCard().getDsc();
              }
              if (obz instanceof W5CardResult) {
                Map m = new HashMap();
                cardResult.setTplObj(o);
                cardResult.setExtraOutMap(m);
                m.put("tplId", o.getTemplateId());
                m.put("tplObjId", o.getTemplateObjectId());
              }
              break;
            case 7: // list view
              W5ListViewResult listViewResult =
                  dao.getListViewResult(scd, o.getObjectId(), requestParams, objectCount != 0);
              if (o.getObjectTip() < 0) listViewResult.setListId(-listViewResult.getListId());
              mainTable =
                  listViewResult.getListView() != null
                          && listViewResult.getListView().get_query() != null
                      ? FrameworkCache.getTable(
                          scd, listViewResult.getListView().get_query().getMainTableId())
                      : null;
              if (!debugAndDeveloper
                  && mainTable != null
                  && (((mainTable.getAccessTips() == null
                          || mainTable.getAccessTips().indexOf("0") == -1)
                      && mainTable.getAccessViewUserFields() == null
                      && !GenericUtil.accessControl(
                          scd,
                          mainTable.getAccessViewTip(),
                          mainTable.getAccessViewRoles(),
                          mainTable.getAccessViewUsers()))))
                obz = listViewResult.getListView().getDsc();
              else {
                obz = accessControl ? listViewResult : listViewResult.getListView().getDsc();
              }
              break;
            case 3: // form
              W5FormResult formResult =
                  getFormResult(
                      scd,
                      o.getObjectId(),
                      requestParams.get("a") != null ? GenericUtil.uInt(requestParams, "a") : 2,
                      requestParams);
              if (o.getObjectTip() < 0) formResult.setFormId(-formResult.getFormId());
              formResult.setObjectTip(o.getObjectTip()); // render icin gerekecek
              /*				if(PromisSetting.moduleAccessControl!=0 && formResult.getForm()!=null && formResult.getForm().get_sourceTable()!=null && !PromisCache.roleAccessControl(scd, formResult.getForm().get_sourceTable().getModuleId()))
              	obz = formResult.getForm().getDsc();
              else */
              obz = accessControl ? formResult : formResult.getForm().getDsc();
              break;
            case 4: // query
              Map paramMap = new HashMap();
              paramMap.putAll(requestParams);
              if (!GenericUtil.isEmpty(o.getPostJsCode())) {
                String[] ar1 = o.getPostJsCode().split("&");
                for (int it4 = 0; it4 < ar1.length; it4++) {
                  String[] ar2 = ar1[it4].split("=");
                  if (ar2.length == 2 && ar2[0] != null && ar2[1] != null)
                    paramMap.put(ar2[0], ar2[1]);
                }
              }
              obz = executeQuery(scd, o.getObjectId(), paramMap);
              break;
            case	8://component
            	obz = FrameworkCache.getComponent(scd, o.getObjectId());//dao.loadComponent(scd, o.getObjectId(), new HashMap());
            	break;
            case 10: // KPI Single Card
              obz = executeQuery(scd, o.getObjectId(), new HashMap());
              break;
            case 5: // dbFunc
              obz = executeFunc(scd, o.getObjectId(), requestParams, (short) 1);
              break;
            case 9: // graph dashboard
              W5BIGraphDashboard obz2 =
                  (W5BIGraphDashboard)
                      dao.getCustomizedObject(
                          "from W5BIGraphDashboard t where t.graphDashboardId=? AND t.projectUuid=?",
                          o.getObjectId(),
                          scd.get("projectId"),
                          null);
              if (accessControl) {
                obz = obz2;
              } else {
                obz = "graph" + o.getObjectId();
              }
          }
          if (pr.getPage().getTemplateTip() != 9
              && objectCount == 0) { // daha ilk objede sorun varsa exception ver
            if (obz instanceof String)
              throw new IWBException(
                  "security",
                  "Module",
                  o.getObjectId(),
                  null,
                  "Role Access Control(Page Object)",
                  null);
            else masterTable = mainTable;
          }
          if (obz != null) pr.getPageObjectList().add(obz);
          objectCount++;
        }
      }
      if ((Integer) scd.get("customizationId") == 1
          && GenericUtil.uInt(scd.get("mainTemplateId")) == pageId) {
        List<Object> params = new ArrayList();
        params.add(scd.get("projectId"));
        dao.executeUpdateSQLQuery(
            "update iwb.w5_project p set preview_count=preview_count+1 where p.project_uuid=?",
            params);
      }
      return pr;
    } catch (Exception e) {
      throw new IWBException(
          "framework",
          "Load.Page",
          pageId,
          null,
          "[63,"
              + pageId
              + "]"
              + (pr != null && pr.getPage() != null ? " " + pr.getPage().getDsc() : ""),
          e);
    }
  }

  public void sendMail(
      Map<String, Object> scd,
      String mailTo,
      String mailCc,
      String mailBcc,
      String subject,
      String body,
      String fileIds) {
    /*		List<W5FileAttachment> fileAttachments = null;
    String fas = parameterMap.get("pfile_attachment_ids");
    if(fas!=null && fas.length()>0){
    	String[] q = fas.split(",");
    	if(q.length>0){
    		parameterMap.put("pfile_attachment_ids", fas);
    		Object[] ps = new Object[q.length+1];
    		String sql = "from W5FileAttachment t where t.customizationId=? and  t.fileAttachmentId in (";
    		int i = 1;
    		ps[0]=scd.get("customizationId");
    		for(String s:q){
    			ps[i++] = GenericUtil.uInt(s);
    			sql+="?,";
    		}
    		fileAttachments = dao.find(sql.substring(0,sql.length()-1)+")", ps);
    	}
    }
    W5ObjectMailSetting oms = (W5ObjectMailSetting) dao.find("from W5ObjectMailSetting t where t.customizationId=? and t.mailSettingId=?", (Integer)scd.get("customizationId"),GenericUtil.uInt((Object)parameterMap.get("pmail_setting_id"))).get(0);
    if(oms!=null){
    	W5Email email= new W5Email(parameterMap.get("pmail_to"),parameterMap.get("pmail_cc"),parameterMap.get("pmail_bcc"),parameterMap.get("pmail_subject"),parameterMap.get("pmail_body"), parameterMap.get("pmail_keep_body_original"), fileAttachments);
    	result = MailUtil.sendMail(scd, oms, email);
    	if(result!=null){ //basarisiz, queue'ye at
    		parameterMap.put("perror_msg", result);
    	}
    }
    if(FrameworkCache.getAppSettingIntValue(scd, "feed_flag")!=0 && result==null)try{
    		W5Feed feed = new W5Feed(scd);
    		feed.setFeedTip((short)(dbFuncId==-631 ? 22:21)); //sms:mail
    		feed.setTableId(GenericUtil.uInt(parameterMap.get("_tableId")));feed.setTablePk(GenericUtil.uInt(parameterMap.get("_tablePk")));
    		if(dbFuncId!=-631){
    			List lx = new ArrayList(); lx.add(scd.get("customizationId"));lx.add(GenericUtil.uInt((Object)parameterMap.get("pmail_setting_id")));
    			Map mx = dao.runSQLQuery2Map("select cx.ACCESS_ROLES, cx.ACCESS_USERS from iwb.w5_access_control cx where cx.ACCESS_TIP=0 AND cx.customization_id=? AND cx.table_id=48 AND cx.table_pk=?", lx,null);
    			if(mx!=null && !mx.isEmpty())feed.set_viewAccessControl(new W5AccessControlHelper((String)mx.get("access_roles"),(String)mx.get("access_users")));
    		}
    		feed.set_tableRecordList(dao.findRecordParentRecords(scd,feed.getTableId(),feed.getTablePk(), 0, true));
    		if(feed.get_tableRecordList()!=null && feed.get_tableRecordList().size()>0){
    			feed.set_commentCount(feed.get_tableRecordList().get(0).getCommentCount());
    			if(feed.get_viewAccessControl()==null && feed.get_tableRecordList().get(0).getViewAccessControl()!=null){
    				feed.set_viewAccessControl(feed.get_tableRecordList().get(0).getViewAccessControl());
    			}
    		}
    		saveObject(feed);
    		FrameworkCache.addFeed(scd, feed, true);
    	} catch(Exception e){}
    *
    */
  }

  public W5GlobalFuncResult executeFunc(
      Map<String, Object> scd,
      int dbFuncId,
      Map<String, String> parameterMap,
      short accessSourceType) {

    W5GlobalFuncResult dbFuncResult = null;

    dbFuncResult = dao.getGlobalFuncResult(scd, dbFuncId);
    if (!GenericUtil.isEmpty(dbFuncResult.getGlobalFunc().getAccessSourceTypes())
        && !GenericUtil.hasPartInside2(
            dbFuncResult.getGlobalFunc().getAccessSourceTypes(), accessSourceType))
      throw new IWBException(
          "security", "GlobalFunc", dbFuncId, null, "Access Source Type Control", null);
    /*if(execRestrictTip!=4 && checkAccessRecordControlViolation(scd, 4, 20, ""+dbFuncId))
    throw new PromisException("security","DbProc Execute2", dbFuncId, null, "Access Execute Control", null);*/
    checkTenant(scd);
    dbFuncResult.setErrorMap(new HashMap());
    dbFuncResult.setRequestParams(parameterMap);
    GlobalFuncTrigger.beforeExec(dbFuncResult);

    dao.executeGlobalFunc(dbFuncResult, "");

    if (dbFuncResult.getErrorMap().isEmpty()) { // sorun yok
      // post sms
      if (!GenericUtil.isEmpty(dbFuncResult.getResultMap()))
        parameterMap.putAll(
            dbFuncResult
                .getResultMap()); // veli TODO acaba hata olabilir mi? baska bir map'e mi atsak
      // sadece burasi icin?
    }
    GlobalFuncTrigger.afterExec(dbFuncResult);

    switch (dbFuncId) {
      case -478: // reload locale msg cache
        for (Object[] m :
            (List<Object[]>)
                dao.executeSQLQuery(
                    "select locale, locale_msg_key, dsc from iwb.w5_locale_msg where locale_msg_key=? AND customization_id=?",
                    parameterMap.get("plocale_msg_key"),
                    scd.get("customizationId"))) {
          LocaleMsgCache.set2(
              (Integer) scd.get("customizationId"), (String) m[0], (String) m[1], (String) m[2]);
        }
    }
    if (dbFuncResult == null) {
      dbFuncResult = new W5GlobalFuncResult(dbFuncId);
      dbFuncResult.setSuccess(true);
      dbFuncResult.setRequestParams(new HashMap());
    }

    return dbFuncResult;
  }

  public W5FormResult bookmarkForm(
      Map<String, Object> scd, int formId, int action, Map<String, String> parameterMap) {
    W5FormResult formResult = dao.getFormResult(scd, formId, 2, parameterMap);
    dao.bookmarkForm(
        parameterMap.get("_dsc"),
        action > 10 ? -formId : formId,
        (Integer) scd.get("userId"),
        (Integer) scd.get("customizationId"),
        formResult);

    return formResult;
  }

  public void saveObject(Object o) {
    dao.saveObject(o);
    if (o instanceof W5FileAttachment) { // bununla ilgili islemler
      W5FileAttachment fa = (W5FileAttachment) o;
      W5FormResult formResult = new W5FormResult(0);
      formResult.setRequestParams(new HashMap());
      formResult.setScd(new HashMap());
      formResult.getRequestParams().put("table_id", "" + fa.getTableId());
      formResult.getRequestParams().put("table_pk", "" + fa.getTablePk());
      formResult.getScd().put("userId", fa.getUploadUserId());
      formResult.getScd().put("customizationId", fa.getCustomizationId());
      formResult.getScd().put("roleId", 2);
      formResult.getScd().put("userTip", 2);
      if (fa.getTableId() == 336
          && fa.getFileTypeId() != null
          && fa.getFileTypeId() == -999) { // profile picture
        makeProfilePicture(GenericUtil.uInt(fa.getTablePk()), fa);
      }
      PostFormTrigger.afterPostForm(formResult, dao, null);
    }
  }

  private boolean makeProfilePicture(int userId, W5FileAttachment fa) {
    if (FrameworkSetting.feed
        && FrameworkCache.getAppSettingIntValue(fa.getCustomizationId(), "feed_flag") != 0) {
      Map scd = new HashMap();
      scd.put("userId", fa.getUploadUserId());
      scd.put("roleId", 2);
      scd.put("customizationId", fa.getCustomizationId());
      scd.put("userTip", 2);
      Log5Feed feed = new Log5Feed(scd);
      feed.set_showFeedTip((short) 1);
      feed.setFeedTip((short) 24); // remove:master icin
      dao.saveObject(feed);
      FrameworkCache.addFeed(scd, feed, true);
    }
    UserUtil.setUserProfilePicture(userId, fa.getFileAttachmentId());

    return dao.executeUpdateSQLQuery(
            "update iwb.w5_user set profile_picture_id=? where user_id=?",
            fa.getFileAttachmentId(),
            userId)
        == 1;
  }

  public void updateObject(Object o) {
    dao.updateObject(o);
  }

  public W5FileAttachment loadFile(
      Map<String, Object> scd,
      int fileAttachmentId) { // +:fileId, -:userId : Map<String, Object> scd,
    if (fileAttachmentId < 0) {
      int newFileAttachmentId = UserUtil.getUserProfilePicture(-fileAttachmentId);
      if (newFileAttachmentId == 0) {
        List l =
            dao.executeSQLQuery(
                "select t.profile_picture_id from iwb.w5_user t where t.user_id=?",
                -fileAttachmentId);
        if (!GenericUtil.isEmpty(l)) {
          fileAttachmentId = GenericUtil.uInt(l.get(0));
        }
      } else fileAttachmentId = newFileAttachmentId;
      if (fileAttachmentId == 1 || fileAttachmentId == 2) {
        W5FileAttachment fa2 = new W5FileAttachment();
        fa2.setFileAttachmentId(fileAttachmentId);
        return fa2;
      }
    }
    if (fileAttachmentId <= 0) return null;
    List<W5FileAttachment> fal =
        dao.find("from W5FileAttachment t where t.fileAttachmentId=?", fileAttachmentId);
    if (GenericUtil.isEmpty(fal)) return null;
    W5FileAttachment fa = fal.get(0);
    //		if(scd==null){
    scd = new HashMap();
    scd.put("customizationId", fa.getCustomizationId());
    //		} else if((Integer)scd.get("customizationId")!=fa.getCustomizationId()){
    //			return null;
    //		}
    if (fa != null) { // bununla ilgili islemler
      if (checkAccessRecordControlViolation(scd, 0, fa.getTableId(), fa.getTablePk())) {
        throw new IWBException(
            "security",
            "FileAttachment",
            fa.getFileAttachmentId(),
            null,
            LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_dosya_yetki"),
            null);
      } else if (fa.getCustomizationId() != GenericUtil.uInt(scd.get("customizationId"))) {
        throw new IWBException(
            "security",
            "File Attachment",
            fa.getFileAttachmentId(),
            null,
            LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_dosya_yetki"),
            null);
      }
    }
    return fa;
  }

  public W5FormResult postBulkConversion(
      Map<String, Object> scd,
      int conversionId,
      int dirtyCount,
      Map<String, String> requestParams,
      String prefix) {
    W5Conversion cnv = null;
    try {
      int customizationId = (Integer) scd.get("customizationId");
      String projectId = (String) scd.get("projectId");
      if (/*customizationId > 0 && */scd != null && (Integer) scd.get("roleId") == 0)
        projectId = FrameworkCache.getProjectId(scd, "707." + conversionId);
      cnv =
          (W5Conversion)
              dao.getCustomizedObject(
                  "from W5Conversion t where t.conversionId=? AND t.projectUuid=?",
                  conversionId,
                  projectId,
                  null);
      if (cnv == null || GenericUtil.isEmpty(cnv.getActionTips()) || cnv.getActiveFlag() == 0)
        throw new IWBException(
            "validation",
            "Conversion",
            conversionId,
            null,
            "Conversion Control Error (" + conversionId + ")",
            null);
      Set<String> checkedParentRecords = new HashSet();

      if (GenericUtil.hasPartInside2(cnv.getActionTips(), 0)) { // manual conversion
        int srcFormId = cnv.getSrcFormId();
        W5FormResult formResult = dao.getFormResult(scd, srcFormId, 2, requestParams);
        W5Table t =
            FrameworkCache.getTable(
                scd, formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();
        int convertedCount = 0, errorConversionCount = 0;
        for (int id = 1; id <= dirtyCount; id++) {
          Map<String, String> m = new HashMap();
          m.put("_cnvStr", conversionId + "");
          m.put(
              t.get_tableParamList().get(0).getDsc(),
              requestParams.get("srcTablePk" + prefix + id));
          m.put(".w", requestParams.get(".w"));
          String pid = prefix + id;
          for (String k : requestParams.keySet())
            if (!k.startsWith("srcTablePk")
                && k.endsWith(
                    pid)) { // veli, eger request'ten gelen extra parametreler varsa, onlari da yeni
              // map'e koy
              m.put(k.substring(0, k.length() - pid.length()), requestParams.get(k));
            }
          boolean b =
              extFormConversion(
                  formResult,
                  prefix,
                  0,
                  scd,
                  m,
                  t,
                  requestParams.get("srcTablePk" + prefix + id),
                  true);
          if (!b || !formResult.getErrorMap().isEmpty()) {
            if (cnv.getRowErrorStrategyTip() == 1) {
              String msg =
                  LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_conversion_auto_error")
                      + "  2- ["
                      + LocaleMsgCache.get2(scd, cnv.getDsc())
                      + "]";
              if (!GenericUtil.isEmpty(formResult.getOutputMessages()))
                for (String s : formResult.getOutputMessages()) {
                  msg += "<br> - " + s;
                }
              throw new IWBException("security", "Form", 0, null, msg, null);
            } else {
              //
              //	formResult.getOutputMessages().add(PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_conversion_auto_error")+  "  3- ["+PromisLocaleMsg.get2(scd,cnv.getDsc())+"]");
              errorConversionCount++;
            }
            formResult.getErrorMap().clear();
          } else convertedCount++;
        }
        if (!GenericUtil.isEmpty(formResult.getOutputMessages()))
          formResult.getOutputMessages().add("-");
        formResult
            .getOutputMessages()
            .add(
                convertedCount
                    + " "
                    + LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_records_converted"));
        if (errorConversionCount > 0)
          formResult
              .getOutputMessages()
              .add(
                  errorConversionCount
                      + " "
                      + LocaleMsgCache.get2(
                          0, (String) scd.get("locale"), "fw_records_not_converted"));
        formResult.setQueuedActionList(new ArrayList<W5QueuedActionHelper>());
        return formResult;
      } else if (GenericUtil.hasPartInside2(cnv.getActionTips(), 3)) { // bulk conversion

        List<W5QueuedActionHelper> queuedGlobalFuncList = new ArrayList<W5QueuedActionHelper>();

        int dstFormId = cnv.getDstFormId();
        W5FormResult formResult = dao.getFormResult(scd, dstFormId, 2, requestParams);
        W5Table t =
            FrameworkCache.getTable(
                scd, formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();
        if (t.getAccessViewTip() == 0 && !FrameworkCache.roleAccessControl(scd, 0)) {
          throw new IWBException(
              "security",
              "Module",
              0,
              null,
              LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"),
              null);
        }
        if (t.getAccessViewUserFields() == null
            && !GenericUtil.accessControl(
                scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())) {
          throw new IWBException(
              "security",
              "Form",
              dstFormId,
              null,
              LocaleMsgCache.get2(
                  0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"),
              null);
        }
        Map originalRequestParams = formResult.getRequestParams();
        for (int id = 1; id <= dirtyCount; id++) {
          //			formResult.setAction(PromisUtil.uInt(requestParams.get("a"+prefix+id)));
          int srcTablePk = GenericUtil.uInt(requestParams, "srcTablePk" + prefix + id);
          Map mq = dao.interprateConversionTemplate(cnv, formResult, srcTablePk, false, false);
          if (!GenericUtil.isEmpty(mq)) {
            mq.put("_cnvId", "" + conversionId);
            mq.put("_cnvTblPk", "" + srcTablePk);
            formResult.setRequestParams(mq);
            queuedGlobalFuncList.addAll(postForm4Table(formResult, prefix, checkedParentRecords));
            formResult.setRequestParams(originalRequestParams);
            if (!formResult.getErrorMap().isEmpty()) {
              throw new IWBException(
                  "validation",
                  "Form",
                  dstFormId,
                  null,
                  "Detay Conversion Veri Geçerliliği("
                      + LocaleMsgCache.get2(
                          (Integer) scd.get("customizationId"),
                          (String) scd.get("locale"),
                          formResult.getForm().getLocaleMsgKey())
                      + "): "
                      + GenericUtil.fromMapToJsonString(formResult.getErrorMap()),
                  null);
            }

          } else
            throw new IWBException(
                "validation", "Conversion", conversionId, null, "Detay Conversion Hatası", null);
        }

        formResult.setQueuedActionList(queuedGlobalFuncList);
        if (formResult.getOutputMessages() != null && formResult.getOutputMessages().isEmpty())
          formResult.getOutputMessages().add("Toplam " + dirtyCount + " adet işlem gerçekleşti.");
        return formResult;
      } else
        throw new IWBException(
            "validation",
            "Conversion",
            conversionId,
            null,
            "Conversion Control Error2 (" + conversionId + ")",
            null);
    } catch (Exception e) {
      throw new IWBException(
          "framework",
          "Bulk.Conversion",
          conversionId,
          "",
          "[707," + conversionId + "]" + (cnv != null ? " " + cnv.getDsc() : ""),
          e);
    }
  }

  public W5FormResult postEditGrid4Table(
      Map<String, Object> scd,
      int formId,
      int dirtyCount,
      Map<String, String> requestParams,
      String prefix,
      Set<String> checkedParentRecords) {
    List<W5QueuedActionHelper> queuedGlobalFuncList = new ArrayList<W5QueuedActionHelper>();
    int preGlobalFuncId = GenericUtil.uInt(requestParams.get("_predid"));
    if (preGlobalFuncId != 0) {
      W5GlobalFuncResult dbResult = executeFunc(scd, preGlobalFuncId, requestParams, (short) 1);
      if (!dbResult.getErrorMap().isEmpty()) {
        throw new IWBException(
            "validation",
            "PreGlobalFunc",
            preGlobalFuncId,
            null,
            "PreGlobalFunc Validation Error",
            null);
      }
      if (!dbResult.isSuccess()) {
        throw new IWBException(
            "framework",
            "PreGlobalFunc",
            preGlobalFuncId,
            null,
            "PreGlobalFunc Success Error ",
            null);
      }
      if (!dbResult.getResultMap().isEmpty()) requestParams.putAll(dbResult.getResultMap());
    }

    W5FormResult formResult = dao.getFormResult(scd, formId, 2, requestParams);
    W5Table t =
        FrameworkCache.getTable(
            scd, formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();
    if (t.getAccessViewTip() == 0 && !FrameworkCache.roleAccessControl(scd, 0)) {
      throw new IWBException(
          "security",
          "Module",
          0,
          null,
          LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"),
          null);
    }
    if (t.getAccessViewUserFields() == null
        && !GenericUtil.accessControl(
            scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())) {
      throw new IWBException(
          "security",
          "Form",
          formId,
          null,
          LocaleMsgCache.get2(
              0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_goruntuleme"),
          null);
    }
    Map<String, Object> tmpOutputFields = new HashMap<String, Object>();
    for (int id = 1; id <= dirtyCount; id++) {
      formResult.setAction(GenericUtil.uInt(requestParams.get("a" + prefix + id)));
      queuedGlobalFuncList.addAll(postForm4Table(formResult, prefix + id, checkedParentRecords));

      if (!formResult.getErrorMap().isEmpty()) {
        throw new IWBException(
            "validation",
            "Form",
            formId,
            null,
            "Detay Mazgal Veri Geçerliliği("
                + LocaleMsgCache.get2(
                    (Integer) scd.get("customizationId"),
                    (String) scd.get("locale"),
                    formResult.getForm().getLocaleMsgKey())
                + "): "
                + GenericUtil.fromMapToJsonString(formResult.getErrorMap()),
            null);
      } else if (!GenericUtil.isEmpty(formResult.getOutputFields()))
        for (String key : formResult.getOutputFields().keySet()) {
          tmpOutputFields.put(key + prefix + id, formResult.getOutputFields().get(key));
          // Burada değişiklik var 29.06.2016
          tmpOutputFields.put(key + prefix + id, formResult.getOutputFields().get(key));
          // Detayın detayı kaydediliyor
          for (int i = 1;
              requestParams.containsKey("_fid" + prefix + id + "_" + i + "." + 1);
              i++) {
            String subPrefix = prefix + id + "_" + i + ".";
            int fid = GenericUtil.uInt(requestParams.get("_fid" + subPrefix + 1));
            // Master Primary Key
            requestParams.put(
                key + subPrefix + 1, formResult.getOutputFields().get(key).toString());
            // Master of Master Primary Key //
            if (requestParams.containsKey("root_pk")) {
              String pk = "";
              Object rootPk = requestParams.get(requestParams.get("root_pk"));
              if (rootPk instanceof BigDecimal) {
                pk = String.valueOf((BigDecimal) rootPk);
              } else {
                pk = rootPk.toString();
              }
              requestParams.put(requestParams.get("root_pk") + subPrefix + 1, pk);
            }
            W5FormResult fr =
                postEditGrid4Table(scd, fid, 1, requestParams, subPrefix, new HashSet<String>());
            if (!fr.getErrorMap().isEmpty()) {
              throw new IWBException(
                  "validation",
                  "Form",
                  fid,
                  null,
                  "Detay Mazgal Veri Geçerliliği("
                      + LocaleMsgCache.get2(
                          (Integer) scd.get("customizationId"),
                          (String) scd.get("locale"),
                          fr.getForm().getLocaleMsgKey())
                      + "): "
                      + GenericUtil.fromMapToJsonString(fr.getErrorMap()),
                  null);
            }
          }
        }
    }
    if (!GenericUtil.isEmpty(tmpOutputFields)) formResult.setOutputFields(tmpOutputFields);
    /*		if(t.getCrudGlobalFuncId()!=0 && GenericUtil.hasPartInside(t.getCrudActions(),"ap")){
    W5GlobalFuncResult dbFuncResult = dao.getGlobalFuncResult(formResult.getScd(), t.getCrudGlobalFuncId());
    dbFuncResult.setErrorMap(new HashMap());
    Map m = new HashMap();
    m.putAll(formResult.getRequestParams());
    for(String key:formResult.getOutputFields().keySet())m.put("t"+key, formResult.getOutputFields().get(key).toString());
    m.put("triggerAction", "ap");//trigger action
    dbFuncResult.setRequestParams(m);
    dao.executeGlobalFunc(dbFuncResult,"");
    if(dbFuncResult.getErrorMap().isEmpty() && dbFuncResult.getResultMap()!=null)formResult.getOutputFields().putAll(dbFuncResult.getResultMap());
     	}*/
    formResult.setQueuedActionList(queuedGlobalFuncList);
    if (formResult.getOutputMessages() != null && formResult.getOutputMessages().isEmpty())
      formResult.getOutputMessages().add("Toplam " + dirtyCount + " adet işlem gerçekleşti.");
    return formResult;
  }

  public W5GlobalFuncResult postEditGridGlobalFunc(
      Map<String, Object> scd,
      int dbFuncId,
      int dirtyCount,
      Map<String, String> requestParams,
      String prefix) {
    int preGlobalFuncId = GenericUtil.uInt(requestParams.get("_predid"));
    if (preGlobalFuncId != 0) {
      W5GlobalFuncResult dbResult = executeFunc(scd, preGlobalFuncId, requestParams, (short) 1);
      if (!dbResult.getErrorMap().isEmpty()) {
        throw new IWBException(
            "validation",
            "PreGlobalFunc",
            preGlobalFuncId,
            null,
            "PreGlobalFunc Validation Error",
            null);
      }
      if (!dbResult.isSuccess()) {
        throw new IWBException(
            "framework",
            "PreGlobalFunc",
            preGlobalFuncId,
            null,
            "PreGlobalFunc Success Error ",
            null);
      }
      if (!dbResult.getResultMap().isEmpty()) requestParams.putAll(dbResult.getResultMap());
    }

    W5GlobalFuncResult dbFuncResult = dao.getGlobalFuncResult(scd, dbFuncId);
    if (!GenericUtil.isEmpty(dbFuncResult.getGlobalFunc().getAccessSourceTypes())
        && !GenericUtil.hasPartInside2(dbFuncResult.getGlobalFunc().getAccessSourceTypes(), 1))
      throw new IWBException(
          "security", "DbProc", dbFuncId, null, "Access Restrict Type Control", null);
    if (checkAccessRecordControlViolation(scd, 4, 20, "" + dbFuncId))
      throw new IWBException(
          "security", "DbProc Execute3", dbFuncId, null, "Access Execute Control", null);

    dbFuncResult.setErrorMap(new HashMap());
    dbFuncResult.setRequestParams(requestParams);
    for (int id = 1; id <= dirtyCount; id++) {

      GlobalFuncTrigger.beforeExec(dbFuncResult);
      dao.executeGlobalFunc(dbFuncResult, prefix + id);
      GlobalFuncTrigger.afterExec(dbFuncResult);

      if (!dbFuncResult.getErrorMap().isEmpty() || !dbFuncResult.isSuccess()) {
        throw new IWBException(
            "validation", "GlobalFunc", -dbFuncId, null, "Detail Grid Validation", null);
      }
    }

    return dbFuncResult;
  }

  @SuppressWarnings("unused")
  private Object jasperReportObjectOrganizer(
      Object obj,
      W5JasperObject o,
      W5QueryField f,
      String xlocale,
      int customizationId,
      Object helperData) {

    String res = null;
    if (obj != null) {
      obj = (o.getHtmlFlag() == 1) ? JasperUtil.changeHtmlFont(obj.toString()) : obj;
      if (obj instanceof java.math.BigDecimal) {
        switch (f.getFieldTip()) {
          case 3:
            obj = ((java.math.BigDecimal) obj).doubleValue();
            break;
          case 4:
            obj = ((java.math.BigDecimal) obj).intValue();
            break;
          case 7:
            obj = obj;
            break;
        }
      }

      if (f.getPostProcessTip() == 2) { // locale filtresinden gececek
        obj = LocaleMsgCache.get2(customizationId, xlocale, obj.toString());
      } else if (f.getPostProcessTip() == 7) {
        if (helperData == null)
          helperData =
              FrameworkCache.getAppSettingStringValue(customizationId, "client_para_tip", "");
        obj =
            Money2Text.StartConvert(
                GenericUtil.udouble(obj.toString()), helperData.toString(), xlocale);
      } else if (f.getDsc().contains("_flag")) {
        obj = GenericUtil.uInt(res) != 0 ? "x" : "o";
      } else if (f.getPostProcessTip() == 10) { // demek ki lookup'li deger tutulacak
        W5LookUp lookUp =
            FrameworkCache.getLookUp(
                customizationId, f.getLookupQueryId()); // BUG customizationId olamaz
        if (lookUp != null) {
          W5LookUpDetay d = lookUp.get_detayMap().get(obj.toString());
          if (d != null) {
            String s = d.getDsc();
            if (s != null) {
              obj = GenericUtil.stringToJS(LocaleMsgCache.get2(customizationId, xlocale, s));
              // if(f.getDsc().equals("fatura_para_tip"))faturaParaTip=d.getVal();
            }
          } else {
            obj = "???: " + obj.toString();
          }
        }
      }
    }
    return obj;
  }

  public W5QueryResult getJasperMultipleData(
      Map<String, Object> scd, Map<String, String> requestParams, int jasperId) {
    W5Jasper jasper = (W5Jasper) dao.getObject(W5Jasper.class, jasperId);
    W5QueryResult queryResult = executeQuery(scd, jasper.getMultiJasperQueryId(), requestParams);
    return queryResult;
  }

  public W5JasperResult getJasperResult(
      Map<String, Object> scd, Map<String, String> requestParams, int jasperReportId) {
    String xlocale = (String) scd.get("locale");
    int customizationId = (Integer) scd.get("customizationId");

    W5JasperReport jasperreport = dao.getJasperReport(scd, jasperReportId);
    W5JasperResult jasperResult = dao.getJasperResult(scd, jasperreport, requestParams);
    String file_name = jasperreport.getReportFileName().toString();
    jasperResult.setFile_name(file_name);

    List<Map> detail = new ArrayList(); // detaylar için initialize ediliyor

    jasperResult.getResultMap().putAll(requestParams); // request oldugu gibi aktar

    int _saveTableId = GenericUtil.uInt(requestParams.get("_saveTableId"));
    String _saveTablePk = requestParams.get("_saveTablePk");
    if (_saveTableId != 0 && _saveTablePk != null) { // Bagli oldugu tabloyu oldugu gibi aktar.
      Map<String, Object> mainTableData =
          dao.getMainTableData(scd, _saveTableId, _saveTablePk).get(0);
      jasperResult.getResultMap().putAll(mainTableData); // .p((Map));
    }

    for (W5JasperObject o : jasperResult.getJasper().get_jasperObjects())
      switch (o.getObjectTip()) {
        case 3:
          continue;
        case 1: // query
          if (o.getJasperQueryType() == 1) { // Standart Query
            if (o.getSingleRecordFlag() == 1) { // param
              W5QueryResult queryResult = executeQuery(scd, o.getObjectId(), requestParams);
              if (queryResult != null && queryResult.getData().size() > 0) {
                for (W5QueryField f : queryResult.getQuery().get_queryFields()) {
                  W5TableField tf =
                      f.getMainTableFieldId() > 0
                          ? queryResult
                              .getMainTable()
                              .get_tableFieldMap()
                              .get(f.getMainTableFieldId())
                          : null;
                  if (tf != null
                      && !GenericUtil.accessControl(
                          scd,
                          tf.getAccessViewTip(),
                          tf.getAccessViewRoles(),
                          tf.getAccessViewUsers())) continue;
                  W5QueryField helper = null;
                  Object helperData = null;
                  for (W5QueryField h : queryResult.getQuery().get_queryFields()) {
                    if (h.getDsc().equals(f.getDsc() + "_hp_")) {
                      helperData = queryResult.getData().get(0)[h.getTabOrder() - 1];
                    }
                  }
                  Object obj = queryResult.getData().get(0)[f.getTabOrder() - 1];
                  jasperResult
                      .getResultMap()
                      .put(
                          f.getDsc(),
                          jasperReportObjectOrganizer(
                              obj, o, f, xlocale, customizationId, helperData));
                }
              }

            } else { // detail
              W5QueryResult queryResult = executeQuery(scd, o.getObjectId(), requestParams);
              for (int i = 0; i < queryResult.getData().size(); i++) {
                Map<String, Object> rowMap = new HashMap<String, Object>();

                for (W5QueryField f : queryResult.getQuery().get_queryFields()) {
                  W5TableField tf =
                      f.getMainTableFieldId() > 0
                          ? queryResult
                              .getMainTable()
                              .get_tableFieldMap()
                              .get(f.getMainTableFieldId())
                          : null;
                  if (tf != null
                      && !GenericUtil.accessControl(
                          scd,
                          tf.getAccessViewTip(),
                          tf.getAccessViewRoles(),
                          tf.getAccessViewUsers())) continue;
                  W5QueryField helper = null;
                  Object helperData = null;
                  for (W5QueryField h : queryResult.getQuery().get_queryFields()) {
                    if (h.getDsc().equals(f.getDsc() + "_hp_")) {
                      helperData = queryResult.getData().get(0)[h.getTabOrder() - 1];
                    }
                  }
                  Object obj = queryResult.getData().get(i)[f.getTabOrder() - 1];
                  rowMap.put(
                      f.getDsc().toUpperCase(FrameworkSetting.appLocale),
                      jasperReportObjectOrganizer(obj, o, f, xlocale, customizationId, helperData));
                }

                detail.add(rowMap);
              }
            }
          } else if (o.getJasperQueryType() == 2) { // Subreport Query
            W5QueryResult queryResult = executeQuery(scd, o.getObjectId(), requestParams);
            List<Map> list = new ArrayList<Map>();
            for (int i = 0; i < queryResult.getData().size(); i++) {
              Map<String, Object> rowMap = new HashMap<String, Object>();

              for (W5QueryField f : queryResult.getQuery().get_queryFields()) {
                W5TableField tf =
                    f.getMainTableFieldId() > 0
                        ? queryResult
                            .getMainTable()
                            .get_tableFieldMap()
                            .get(f.getMainTableFieldId())
                        : null;
                if (tf != null
                    && !GenericUtil.accessControl(
                        scd,
                        tf.getAccessViewTip(),
                        tf.getAccessViewRoles(),
                        tf.getAccessViewUsers())) continue;
                W5QueryField helper = null;
                Object helperData = null;
                for (W5QueryField h : queryResult.getQuery().get_queryFields()) {
                  if (h.getDsc().equals(f.getDsc() + "_hp_")) {
                    helperData = queryResult.getData().get(0)[h.getTabOrder() - 1];
                  }
                }
                Object obj = queryResult.getData().get(i)[f.getTabOrder() - 1];
                rowMap.put(
                    f.getDsc().toUpperCase(FrameworkSetting.appLocale),
                    jasperReportObjectOrganizer(obj, o, f, xlocale, customizationId, helperData));
              }
              list.add(rowMap);
            }
            jasperResult.getResultMap().put(queryResult.getQuery().getDsc(), list);
          }
          break;
        case 2: // dbFunc
          W5GlobalFuncResult dbFuncResult =
              executeFunc(scd, o.getObjectId(), requestParams, (short) 5);
          Map<String, String> resultMap = dbFuncResult.getResultMap();
          if (o.getHtmlFlag() == 1) {
            for (Iterator i = resultMap.keySet().iterator(); i.hasNext(); ) {
              String key = (String) i.next();
              String value = JasperUtil.changeHtmlFont(resultMap.get(key));
              resultMap.put(key.toString(), value);
            }
          }
          if (resultMap != null) jasperResult.getResultMap().putAll(resultMap);
          break;
      }

    if (!detail.isEmpty()) jasperResult.setResultDetail(detail);

    return jasperResult;
  }

  public Map<String, Object> userRoleSelect(
      int userId, int userRoleId, int customizationId, String projectId, String mobileDeviceId) {
    Map<String, Object> scd = new HashMap<String, Object>();
    scd.put("userId", userId);
    scd.put("userRoleId", userRoleId);
    scd.put("customizationId", customizationId);
    Map<String, String> rm = new HashMap();
    if (!GenericUtil.isEmpty(projectId)) rm.put("projectId", projectId);
    Map<String, Object> m = executeQuery2Map(scd, 2, rm); // mainSessionQuery
    if (m == null) return null;
    if (false && !GenericUtil.isEmpty(mobileDeviceId)) {
      Map parameterMap = new HashMap();
      parameterMap.put("pmobile_device_id", mobileDeviceId);
      parameterMap.put("pactive_flag", 1);
      executeFunc(m, 673, parameterMap, (short) 4);
    }
    return m;
  }

  public Map<String, Object> userRoleSelect4App(
      W5Project po, int userId, int userRoleId, String mobileDeviceId) {
    Map<String, Object> scd = new HashMap<String, Object>();
    scd.put("userId", userId);
    scd.put("userRoleId", userRoleId);
    scd.put("customizationId", po.getCustomizationId());
    scd.put("projectId", po.getProjectUuid());
    Map<String, String> rm = new HashMap();
    Map<String, Object> m = executeQuery2Map(scd, po.getSessionQueryId(), rm); // mainSessionQuery
    if (m == null) return null;
    return m;
  }

  public Map<String, Object> userSession4Auth(int userId, int customizationId) {
    Map<String, Object> scd = new HashMap<String, Object>();
    scd.put("userId", userId);
    scd.put("customizationId", customizationId);
    Map<String, String> rm = new HashMap();
    return executeQuery2Map(scd, 4546, rm); // auth.SessionQuery
  }

  private int approvalStepListControl(List<W5WorkflowStep> stepList) {
    int r = -1;
    int i = 0;
    if (stepList != null && !stepList.isEmpty()) {
      for (W5WorkflowStep s : stepList) {
        if (s.getApprovalStepId() < 901) {
          r = i;
          break;
        }
        i++;
      }
    }
    return r;
  }

  // TODO: onayda, iade'de, reject'te notification gitsin
  public Map<String, Object> approveRecord(
      Map<String, Object> scd,
      int approvalRecordId,
      int approvalAction,
      Map<String, String> parameterMap) {

    Map<String, Object> result = new HashMap<String, Object>();
    if (!FrameworkSetting.workflow) return result;

    int customizationId = (Integer) scd.get("customizationId");
    int userId = (Integer) scd.get("userId");
    int versionNo = GenericUtil.uInt(parameterMap.get("_avno"));
    W5WorkflowRecord ar =
        (W5WorkflowRecord)
            dao.find(
                    "from W5WorkflowRecord t where t.projectUuid=? and  t.approvalRecordId=?",
                    scd.get("projectId"),
                    approvalRecordId)
                .get(0);
    String mesaj = "";
    String xlocale = (String) scd.get("locale");

    if (ar == null || ar.getFinishedFlag() != 0) {
      result.put("status", false);
      return result;
    }

    W5Workflow a = FrameworkCache.getWorkflow(scd, ar.getApprovalId());
    if (a.getActiveFlag() == 0) {
      throw new IWBException(
          "validation",
          "Workflow",
          approvalRecordId,
          null,
          LocaleMsgCache.get2(0, xlocale, "approval_not_active"),
          null);
    }
    if (approvalAction != 901) {
      if (versionNo != ar.getVersionNo()) {
        throw new IWBException(
            "security",
            "WorkflowRecord",
            approvalRecordId,
            null,
            LocaleMsgCache.get2(0, xlocale, "approval_onay_kaydi_degismis"),
            null);
      }
      if (!GenericUtil.accessControl(
          scd, (short) 1, ar.getApprovalRoles(), ar.getApprovalUsers())) {
        throw new IWBException(
            "security",
            "WorkflowRecord",
            approvalRecordId,
            null,
            LocaleMsgCache.get2(0, xlocale, "approval_onay_kaydina_hakkiniz_yok"),
            null);
      }
    }
    boolean isFinished = false;

    W5WorkflowStep currentStep =
        a.get_approvalStepMap().get(ar.getApprovalStepId()).getNewInstance();

    W5WorkflowStep nextStep = null;
    Map<String, Object> advancedNextStepSqlResult = null;
    switch (approvalAction) {
      case 901: // start approval
        mesaj =
            " '"
                + scd.get("completeName")
                + "' "
                + LocaleMsgCache.get2(0, xlocale, "approval_presented_for_your_approval");
        if (a.getApprovalRequestTip() != 2)
          throw new IWBException(
              "security",
              "WorkflowRecord",
              approvalRecordId,
              null,
              LocaleMsgCache.get2(0, xlocale, "approval_onay_talebi_yapilamaz"),
              null);
        if (!GenericUtil.accessControl(
            scd, ar.getAccessViewTip(), ar.getApprovalRoles(), ar.getApprovalUsers()))
          throw new IWBException(
              "security",
              "WorkflowRecord",
              approvalRecordId,
              null,
              LocaleMsgCache.get2(0, xlocale, "approval_onay_talebi_hakkiniz_yok"),
              null);
        if (ar.getApprovalStepId() != 901)
          throw new IWBException(
              "security",
              "WorkflowRecord",
              approvalRecordId,
              null,
              LocaleMsgCache.get2(0, xlocale, "approval_onay_talebi_onceden_yapilmis"),
              null);
        Map<String, Object> advancedStepSqlResult = null;
        if (a.getAdvancedBeginSql() != null && a.getAdvancedBeginSql().length() > 10) { // calisacak
          Object[] oz = DBUtil.filterExt4SQL(a.getAdvancedBeginSql(), scd, parameterMap, null);
          advancedStepSqlResult = dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
          // donen bir cevap var, aktive_flag deger olarak var ve onun degeri 0 ise o zaman
          // girmeyecek
          if (advancedStepSqlResult != null) {
            // onay başladıktan sonra AdvancedBeginSql ile tekrar active_flag kontrolü yapmak
            // anlamsız olmuş bu yüzden burayı kapattım (çağlar)
            /*if(advancedStepSqlResult.get("active_flag")!=null && PromisUtil.uInt(advancedStepSqlResult.get("active_flag"))==0)//girmeyecek
            a = null; //approval olmayacak*/
            if (advancedStepSqlResult.get("error_msg") != null) // girmeyecek
            throw new IWBException(
                  "security",
                  "Workflow",
                  a.getApprovalId(),
                  null,
                  (String) advancedStepSqlResult.get("error_msg"),
                  null);
          }
        }
        nextStep = null;
        switch (a.getApprovalFlowTip()) { // simple
          case 0: // basit onay
            nextStep = a.get_approvalStepList().get(0).getNewInstance();
            break;
          case 1: // complex onay
            if (advancedStepSqlResult != null
                && advancedStepSqlResult.get("approval_step_id") != null
                && GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")) != 0) {
              nextStep =
                  a.get_approvalStepMap()
                      .get(GenericUtil.uInt(advancedStepSqlResult.get("approval_step_id")))
                      .getNewInstance();
              // if(advancedStepSqlResult.get("approval_users") !=
              // null)nextStep.setApprovalUsers(advancedStepSqlResult.get("approval_users").toString());
              // if(advancedStepSqlResult.get("approval_roles") !=
              // null)nextStep.setApprovalRoles(advancedStepSqlResult.get("approval_roles").toString());
            } else {
              nextStep = a.get_approvalStepList().get(0).getNewInstance();
            }
            break;
          case 2: // hierarchical onay //deprecated
            break;
          case 3: // dynamic onay //deprecated
            break;
        }

        if (nextStep != null
            && nextStep.getDynamicRoleUserSql() != null
            && nextStep.getDynamicRoleUserSql().length() > 10) { // calisacak
          Map<String, Object> dynamicRoleUserSql = null;
          dynamicRoleUserSql =
              dao.runSQLQuery2Map(nextStep.getDynamicRoleUserSql(), scd, parameterMap, null);
          // Ekstra Eklenecek Kullanıcı ve Roller varmı bu stepte
          if (dynamicRoleUserSql != null && dynamicRoleUserSql.get("approval_users") != null)
            nextStep.setApprovalUsers(
                nextStep.getApprovalUsers() == null
                    ? (String) dynamicRoleUserSql.get("approval_users")
                    : GenericUtil.addUniqueValToStr(
                        nextStep.getApprovalUsers(),
                        (String) dynamicRoleUserSql.get("approval_users"),
                        ","));
          if (dynamicRoleUserSql != null && dynamicRoleUserSql.get("approval_roles") != null)
            nextStep.setApprovalUsers(
                nextStep.getApprovalRoles() == null
                    ? (String) dynamicRoleUserSql.get("approval_roles")
                    : GenericUtil.addUniqueValToStr(
                        nextStep.getApprovalUsers(),
                        (String) dynamicRoleUserSql.get("approval_roles"),
                        ","));
        }

        break;
      case 1: // onay
        mesaj =
            " '"
                + scd.get("completeName")
                + "' "
                + LocaleMsgCache.get2(0, xlocale, "approval_presented_for_your_approval");
        switch (a.getApprovalFlowTip()) {
          case 0: // basit onay
            if (a.getActionTip() == 3) { // TODO: delete ise o zaman o kaydi ve bunu sil
              Map<String, String> mz = new HashMap();
              mz.put("ptable_id", "" + ar.getTableId());
              mz.put("ptable_pk", "" + ar.getTablePk());
              executeFunc(
                  scd, 690, mz,
                  (short) 2); // bu kaydin child kayitlari var mi? iwb.w5_table_field'daki
              // default_control_tip ve default_lookup_table_id'ye bakiliyor
              W5FormResult fr = new W5FormResult(-1);
              fr.setForm(new W5Form());
              //					fr.getForm().set_sourceTable(PromisCache.getTable(scd, ar.getTableId()));
              fr.setRequestParams(parameterMap);
              fr.setScd(scd);
              parameterMap.put(
                  "t"
                      + /*fr.getForm().get_sourceTable()*/ FrameworkCache.getTable(
                              scd, ar.getTableId())
                          .get_tableFieldList()
                          .get(0)
                          .getDsc(),
                  "" + ar.getTablePk());
              fr.setPkFields(new HashMap());
              fr.setOutputFields(new HashMap());
              dao.deleteTableRecord(fr, ""); // TODO burasi
            }
            isFinished = true; // basit onay ise hemen bitir
            ar.setApprovalActionTip((short) 5); // bitti(onaylandi)
            ar.setApprovalStepId(998);
            break;
          case 3: // dynamic onay
          case 2: // hierarchical
          case 1: // complex onay
            if (a.getApprovalFlowTip() == 3) { // dynamic onay
              if (ar.getApprovalStepId()
                  == 903) { // yani bu su anda hala dinamik onayda. complekse gecmemis
                /*if(currentStep.getOnApproveGlobalFuncId()!=0){
                	executeGlobalFunc(scd, currentStep.getOnApproveGlobalFuncId(), parameterMap, (short)6);
                }*/
                String uxs = ar.getApprovalUsers();
                if (GenericUtil.hasPartInside2(uxs, "" + userId)) {
                  String[] uxs2 = uxs.split(",");
                  if (uxs2.length == 1) { // bitmis. complex onaya gececek
                    int inx = approvalStepListControl(a.get_approvalStepList());
                    if (inx > -1) nextStep = a.get_approvalStepList().get(inx).getNewInstance();
                    else {
                      nextStep = null;
                      isFinished = true;
                      ar.setApprovalActionTip((short) 5); // bitti(onaylandi)
                      ar.setApprovalStepId(998);
                      currentStep.setSendMailOnEnterStepFlag(a.getSendMailFlag());
                      currentStep.setSendSmsOnEnterStepFlag(a.getSendSmsFlag());
                    }
                  } else {
                    uxs = "";
                    for (String ux : uxs2) if (!ux.equals("" + userId)) uxs += "," + ux;
                  }
                  ar.setApprovalActionTip((short) approvalAction);
                  ar.setApprovalRoles(null);
                  ar.setApprovalUsers(uxs.substring(1));
                } else
                  throw new IWBException(
                      "security",
                      "WorkflowRecord",
                      approvalRecordId,
                      null,
                      LocaleMsgCache.get2(0, xlocale, "approval_dynamic_not_approve"),
                      null);
                break;
              }
            } else if (a.getApprovalFlowTip() == 2) { // hiyerarsik onay: deprecated
              if (ar.getApprovalStepId()
                  == 902) { // yani bu su anda hala hiyerarsik onayda. complekse gecmemis
                boolean stepControl = false;
                int mngUserId = GenericUtil.uInt(scd.get("mngUserId"));
                if (mngUserId != 0) {
                  ar.setApprovalActionTip((short) approvalAction);
                  ar.setApprovalRoles(null);
                  ar.setApprovalUsers("" + mngUserId);
                } else {
                  stepControl = true;
                }
                if (stepControl == true) {
                  int inx = approvalStepListControl(a.get_approvalStepList());
                  if (inx > -1) nextStep = a.get_approvalStepList().get(inx).getNewInstance();
                  else {
                    nextStep = null;
                    isFinished = true;
                    ar.setApprovalActionTip((short) 5); // bitti(onaylandi)
                    ar.setApprovalStepId(998);
                    currentStep.setSendMailOnEnterStepFlag(a.getSendMailFlag());
                    currentStep.setSendSmsOnEnterStepFlag(a.getSendSmsFlag());
                  }
                }
                break;
              }
            }

            if (currentStep.getFinalStepFlag() == 0) {
              int nextStepId = currentStep.getOnApproveStepId();
              if (nextStepId == 0 && currentStep.getApprovalStepId() == 901) {
                throw new IWBException(
                    "validation",
                    "WorkflowRecord",
                    approvalRecordId,
                    null,
                    LocaleMsgCache.get2(0, xlocale, "approval_hatali_islem"),
                    null);
              }
              if (currentStep.getOnApproveStepSql() != null) {
                parameterMap.put("_tb_pk", "" + ar.getTablePk());
                Object[] oz =
                    DBUtil.filterExt4SQL(
                        currentStep.getOnApproveStepSql(), scd, parameterMap, null);
                advancedNextStepSqlResult =
                    dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
                if (advancedNextStepSqlResult.get("error_msg") != null)
                  throw new IWBException(
                      "validation",
                      "WorkflowRecord",
                      approvalRecordId,
                      null,
                      (String) advancedNextStepSqlResult.get("error_msg"),
                      null);
                if (advancedNextStepSqlResult.get("next_step_id") != null)
                  nextStepId = GenericUtil.uInt(advancedNextStepSqlResult.get("next_step_id"));
              }
              nextStep = a.get_approvalStepMap().get(nextStepId).getNewInstance();
            } else nextStep = null;
            if (nextStep == null) {
              isFinished = true;
              ar.setApprovalActionTip((short) 5); // bitti(onaylandi)
              ar.setApprovalStepId(998);
            } else if (advancedNextStepSqlResult != null) {
              if (GenericUtil.uInt(advancedNextStepSqlResult.get("finished_flag"))
                  != 0) { // bitti mi?
                isFinished = true; // approval bitti
                ar.setApprovalActionTip((short) 5);
                ar.setApprovalStepId(998);
              }
            }
            break;
        }

        if (nextStep != null
            && nextStep.getDynamicRoleUserSql() != null
            && nextStep.getDynamicRoleUserSql().length() > 10) { // calisacak
          Map<String, Object> dynamicRoleUserSql = null;
          dynamicRoleUserSql =
              dao.runSQLQuery2Map(nextStep.getDynamicRoleUserSql(), scd, parameterMap, null);
          // Ekstra Eklenecek Kullanıcı ve Roller varmı bu stepte
          if (dynamicRoleUserSql != null && dynamicRoleUserSql.get("approval_users") != null)
            nextStep.setApprovalUsers(
                nextStep.getApprovalUsers() == null
                    ? (String) dynamicRoleUserSql.get("approval_users")
                    : GenericUtil.addUniqueValToStr(
                        nextStep.getApprovalUsers(),
                        (String) dynamicRoleUserSql.get("approval_users"),
                        ","));
          if (dynamicRoleUserSql != null && dynamicRoleUserSql.get("approval_roles") != null)
            nextStep.setApprovalRoles(
                nextStep.getApprovalRoles() == null
                    ? (String) dynamicRoleUserSql.get("approval_roles")
                    : GenericUtil.addUniqueValToStr(
                        nextStep.getApprovalUsers(),
                        (String) dynamicRoleUserSql.get("approval_roles"),
                        ","));
        }

        break;
      case 2: // iade: TODO . baska?
        if (currentStep.getApprovalStepId() == 901) {
          throw new IWBException(
              "validation",
              "WorkflowRecord",
              approvalRecordId,
              null,
              LocaleMsgCache.get2(0, xlocale, "approval_hatali_islem"),
              null);
        }
        mesaj =
            " '"
                + scd.get("completeName")
                + "' "
                + LocaleMsgCache.get2(0, xlocale, "approval_were_returned_by");
        if (ar.getReturnFlag() == 0) { // yapilamaz
          throw new IWBException(
              "validation",
              "WorkflowRecord",
              approvalRecordId,
              null,
              LocaleMsgCache.get2(0, xlocale, "approval_not_return"),
              null);
        }
        if (currentStep.getOnReturnStepId() == 0) { // yapilamaz
          throw new IWBException(
              "validation",
              "WorkflowRecord",
              approvalRecordId,
              null,
              LocaleMsgCache.get2(0, xlocale, "approval_not_return_not_setting"),
              null);
        }
        switch (a.getApprovalFlowTip()) {
          case 0: // basit onay ise bir kisi geriye git
            if (a.getApprovalRequestTip() == 2 && ar.getReturnFlag() != 0) {
              nextStep = new W5WorkflowStep();
              nextStep.setReturnFlag(ar.getReturnFlag());
              nextStep.setApprovalUsers("" + ar.getInsertUserId());
              nextStep.setApprovalStepId(901);
            } else
              throw new IWBException(
                  "validation",
                  "WorkflowRecord",
                  approvalRecordId,
                  null,
                  LocaleMsgCache.get2(0, xlocale, "approval_not_return_simple_approval"),
                  null);
            break;
          case 1: // complex onay
            int returnStepId = currentStep.getOnReturnStepId();
            if (currentStep.getOnReturnStepSql() != null) {
              parameterMap.put("_tb_pk", "" + ar.getTablePk());
              Object[] oz =
                  DBUtil.filterExt4SQL(currentStep.getOnReturnStepSql(), scd, parameterMap, null);
              advancedNextStepSqlResult = dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
              if (advancedNextStepSqlResult.get("error_msg") != null)
                throw new IWBException(
                    "validation",
                    "WorkflowRecord",
                    approvalRecordId,
                    null,
                    (String) advancedNextStepSqlResult.get("error_msg"),
                    null);
              if (advancedNextStepSqlResult.get("return_step_id") != null)
                returnStepId = GenericUtil.uInt(advancedNextStepSqlResult.get("return_step_id"));
            }
            nextStep = a.get_approvalStepMap().get(returnStepId).getNewInstance();
            if (nextStep == null) {
              throw new IWBException(
                  "validation",
                  "WorkflowRecord",
                  approvalRecordId,
                  null,
                  (String) advancedNextStepSqlResult.get("error_msg"),
                  null);
            }
            break;
          case 2: // hierarchical onay
        }

        break;
      case 3: // red
        if (currentStep.getApprovalStepId() == 901) {
          throw new IWBException(
              "validation",
              "WorkflowRecord",
              approvalRecordId,
              null,
              LocaleMsgCache.get2(0, xlocale, "approval_hatali_islem"),
              null);
        }
        mesaj =
            " '"
                + scd.get("completeName")
                + "' "
                + LocaleMsgCache.get2(0, xlocale, "approval_rejected_by");
        if (a.getOnRejectTip() == 2) { // red olunca kaydi sil
          //				dao.copyTableRecord(a.getTableId(), ar.getTablePk() ,"promis", "promis_approval");
          // //TODO:buna benzer birsey
          List<Object[]> l =
              dao.find(
                  "select t.dsc, tp.expressionDsc "
                      + "from W5Table t, W5TableParam tp "
                      + "where t.customizationId=? AND t.tableId=? AND t.tableId=tp.tableId AND tp.tabOrder=1",
                  customizationId,
                  a.getTableId());
          String tableDsc = (l.get(0)[0]).toString();
          String tablePkDsc = (l.get(0)[1]).toString();
          int recordCound =
              dao.executeUpdateSQLQuery(
                  "delete from "
                      + tableDsc
                      + " x where x.customization_id=? and x."
                      + tablePkDsc
                      + "=?",
                  customizationId,
                  ar.getTablePk());
          if (recordCound != 1) {
            throw new IWBException(
                "validation",
                "Workflow Delete record",
                approvalRecordId,
                null,
                "Wrong number of delete record = " + recordCound,
                null);
          }
        } else { // "rejected" olarak isaretle. approve_record kaydi duracak. approve_step_id:999
          // olacak. finished olacak
          ar.setApprovalStepId(999);
          ar.setApprovalRoles(currentStep.getApprovalRoles());
          ar.setApprovalUsers(currentStep.getApprovalUsers());
          currentStep.setSendMailOnEnterStepFlag(a.getSendMailFlag());
          currentStep.setSendSmsOnEnterStepFlag(a.getSendSmsFlag());
        }

        isFinished = true;
        break;
      case 0: // red
        if (currentStep.getApprovalStepId() == 901) {
          throw new IWBException(
              "validation",
              "WorkflowRecord",
              approvalRecordId,
              null,
              LocaleMsgCache.get2(0, xlocale, "approval_hatali_islem"),
              null);
        }
    }
    Log5WorkflowRecord logRecord = new Log5WorkflowRecord();
    logRecord.setUserId(userId);
    logRecord.setApprovalRecordId(approvalRecordId);
    if (currentStep != null) logRecord.setApprovalStepId(currentStep.getApprovalStepId());
    logRecord.setApprovalId(ar.getApprovalId());
    logRecord.setDsc(parameterMap.get("_adsc"));

    ar.setVersionUserId(userId);

    if ((a.getApprovalFlowTip() != 2 && a.getApprovalFlowTip() != 3) || nextStep != null)
      switch (a.getActionTip()) { // bu hangi tip bir islem?
        case 2: // insert
          switch (approvalAction) {
            case 1: // onay
              if (isFinished) { // son adim mi?
                logRecord.setApprovalActionTip((short) 5); // finished(approved)

              } else { // kompleks adimlar
                logRecord.setApprovalActionTip((short) approvalAction);
                ar.setReturnFlag(nextStep.getReturnFlag());
                ar.setApprovalActionTip((short) approvalAction);
                if (advancedNextStepSqlResult != null
                    && (advancedNextStepSqlResult.get("approval_roles") != null
                        || advancedNextStepSqlResult.get("approval_users") != null)) {
                  ar.setApprovalRoles((String) advancedNextStepSqlResult.get("approval_roles"));
                  ar.setApprovalUsers((String) advancedNextStepSqlResult.get("approval_users"));
                } else {
                  ar.setApprovalRoles(nextStep.getApprovalRoles());
                  ar.setApprovalUsers(nextStep.getApprovalUsers());
                }
                ar.setApprovalStepId(nextStep.getApprovalStepId());
                ar.setAccessViewTip(nextStep.getAccessViewTip());
                ar.setAccessViewRoles(nextStep.getAccessViewRoles());
                ar.setAccessViewUsers(nextStep.getAccessViewUsers());
              }

              break;
            case 901: // onay baslat
            case 2: // iade
              if (nextStep.getApprovalStepId()
                  == 901) { // Dönülecek Adım aslında bir adım değil, onay başlangıcı olacak
                logRecord.setApprovalActionTip((short) approvalAction);
                ar.setApprovalActionTip((short) 0);
                ar.setApprovalRoles(a.getManualAppRoleIds());
                ar.setApprovalUsers(a.getManualAppUserIds());
                if (a.getManualAppRoleIds() == null && a.getManualAppUserIds() == null) {
                  if (a.getAdvancedBeginSql() != null
                      && a.getAdvancedBeginSql().length() > 10) { // calisacak
                    Map<String, Object> advancedStepSqlResult = null;
                    advancedStepSqlResult =
                        dao.runSQLQuery2Map(a.getAdvancedBeginSql(), scd, parameterMap, null);
                    // donen bir cevap var, aktive_flag deger olarak var ve onun degeri 0 ise o
                    // zaman girmeyecek
                    if (advancedStepSqlResult != null
                        && advancedStepSqlResult.get("active_flag") != null
                        && GenericUtil.uInt(advancedStepSqlResult.get("active_flag"))
                            == 0) { // girmeyecek
                      ar.setApprovalUsers(String.valueOf(ar.getInsertUserId()));
                    } else if (advancedStepSqlResult != null) {
                      if (advancedStepSqlResult != null
                          && advancedStepSqlResult.get("approval_users") != null)
                        ar.setApprovalUsers((String) advancedStepSqlResult.get("approval_users"));
                      if (advancedStepSqlResult != null
                          && advancedStepSqlResult.get("approval_roles") != null)
                        ar.setApprovalRoles((String) advancedStepSqlResult.get("approval_roles"));
                    }
                  } else {
                    ar.setApprovalUsers(String.valueOf(ar.getInsertUserId()));
                  }
                }
                ar.setApprovalStepId(
                    nextStep
                        .getApprovalStepId()); // nextStep.getApprovalStepId() = 901 geliyor sorun
                // yok
                nextStep.setSendMailOnEnterStepFlag(a.getSendMailFlag());
                nextStep.setSendSmsOnEnterStepFlag(a.getSendSmsFlag());
              } else {
                logRecord.setApprovalActionTip((short) approvalAction);
                ar.setReturnFlag(nextStep.getReturnFlag());
                ar.setApprovalStepId(nextStep.getApprovalStepId());
                if (approvalAction == 901) {
                  if (!GenericUtil.isEmpty(nextStep.getApprovalRoles())
                      || !GenericUtil.isEmpty(nextStep.getApprovalUsers())) {
                    ar.setApprovalRoles(nextStep.getApprovalRoles());
                    ar.setApprovalUsers(nextStep.getApprovalUsers());
                  }

                  if (!GenericUtil.isEmpty(nextStep.getAccessViewRoles())
                      || !GenericUtil.isEmpty(nextStep.getAccessViewUsers())) {
                    ar.setAccessViewTip(nextStep.getAccessViewTip());
                    ar.setAccessViewRoles(nextStep.getAccessViewRoles());
                    ar.setAccessViewUsers(nextStep.getAccessViewUsers());
                  }
                } else if (approvalAction == 2) {
                  ar.setApprovalUsers(null);
                  ar.setApprovalRoles(null);
                  if (!GenericUtil.isEmpty(nextStep.getApprovalRoles())
                      || !GenericUtil.isEmpty(nextStep.getApprovalUsers())) {
                    ar.setApprovalRoles(nextStep.getApprovalRoles());
                    ar.setApprovalUsers(nextStep.getApprovalUsers());
                  }
                  if (nextStep != null
                      && nextStep.getDynamicRoleUserSql() != null
                      && nextStep.getDynamicRoleUserSql().length() > 10) { // calisacak
                    Map<String, Object> dynamicRoleUserSql = null;
                    dynamicRoleUserSql =
                        dao.runSQLQuery2Map(
                            nextStep.getDynamicRoleUserSql(), scd, parameterMap, null);
                    // Ekstra Eklenecek Kullanıcı ve Roller varmı bu stepte
                    if (dynamicRoleUserSql != null
                        && dynamicRoleUserSql.get("approval_users") != null)
                      ar.setApprovalUsers(
                          ar.getApprovalUsers() == null
                              ? (String) dynamicRoleUserSql.get("approval_users")
                              : GenericUtil.addUniqueValToStr(
                                  ar.getApprovalUsers(),
                                  (String) dynamicRoleUserSql.get("approval_users"),
                                  ","));
                    if (dynamicRoleUserSql != null
                        && dynamicRoleUserSql.get("approval_roles") != null)
                      ar.setApprovalRoles(
                          ar.getApprovalRoles() == null
                              ? (String) dynamicRoleUserSql.get("approval_roles")
                              : GenericUtil.addUniqueValToStr(
                                  ar.getApprovalUsers(),
                                  (String) dynamicRoleUserSql.get("approval_roles"),
                                  ","));
                  }
                }
              }
              break;
            case 3: // red
              logRecord.setApprovalActionTip((short) approvalAction);
              break;
          }
          break;

        case 1: // edit
          break;

        case 3: // delete
          break;
      }
    else logRecord.setApprovalActionTip((short) approvalAction);

    /*
     * Ekstra Bildirim
     * SMS Mail Tip -> 0 SMS , 1 E-Mail, 2 Notification
     */

    /* Ekstra bildirim sonu */

    String pemailList = ""; // Email gönderilirken bu liste kullanılacak
    String mailSubject = ""; // Email Subject
    String mailBody = ""; // Email Body
    List<String> gsmList = new ArrayList<String>(); // SMS gönderilirken bu liste kullanılacak
    String messageBody = ""; // SMS Body
    List<String> notificationList =
        new ArrayList<String>(); // Notification gönderilirken bu liste kullanılacak

    if (isFinished) { // Finished ise

      if (approvalAction == 1)
        mesaj =
            " '"
                + scd.get("completeName")
                + "' "
                + LocaleMsgCache.get2(0, xlocale, "approval_approved_by"); // Onaylandı ise
      else if (approvalAction == 3)
        mesaj =
            " '"
                + scd.get("completeName")
                + "' "
                + LocaleMsgCache.get2(0, xlocale, "approval_rejected_by"); // reddedildi ise
      else if (approvalAction == 901)
        mesaj =
            " '"
                + scd.get("completeName")
                + "' "
                + LocaleMsgCache.get2(
                    0,
                    xlocale,
                    "approval_approved_by_901"); // dinamik onayda onaylayacak kişi seçmeden onay
      // istendiğinde ise
      else
        mesaj =
            " "; // Böyle bir durum olmaz aslında, bu kısma ya onaylanınca ya da reddedilince girer
      // diye biliyorum

      // Onay işlemleri bittikten sonra kaydedene, onay sürecini başlatana ve son adımdaki rol ve
      // userlara ve session da olmayan kişilere
      List<Object[]> approvalUsers =
          dao.executeSQLQuery(
              "select gu.gsm,gu.email,gu.user_id from iwb.w5_user gu where gu.customization_id=?::integer and gu.user_id <> ?::integer and "
                  + "(gu.user_id in (select lar.user_id from log5_approval_record lar where (lar.approval_action_tip = 0 or lar.approval_action_tip = 901) and lar.approval_record_id = ?::integer) or "
                  + "gu.user_id in (select ur.user_id from iwb.w5_user_role ur where ur.customization_id = ?::integer and ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and ((select u.user_tip from iwb.w5_role u where u.role_id = ur.role_id and u.customization_id = ur.customization_id) <> 3)) or "
                  + "gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x))",
              scd.get("customizationId"),
              scd.get("userId"),
              ar.getApprovalRecordId(),
              scd.get("customizationId"),
              ar.getApprovalRoles(),
              ar.getApprovalUsers());

      if (approvalUsers != null) { // Bu kullanıcı listesi mevcutsa
        for (Object[] liste : approvalUsers) {
          if (liste[0] != null
              && liste[0].toString().length() > 5
              && currentStep.getSendSmsOnEnterStepFlag()
                  != 0) { // sms gondermece, length kontrolü bazı kullanıcılar için 0 girilmiş bunun
            // için düz mantık
            gsmList.add(liste[0].toString());
          }

          if (liste[1] != null
              && currentStep.getSendMailOnEnterStepFlag()
                  != 0) { // mail adresi toplama (hepsini topla göndermek için toplanıyor)
            pemailList += "," + liste[1].toString();
          }

          if (liste[2] != null) { // user idler
            notificationList.add(liste[2].toString());
          }
        }
      }

      // bu adımdan önce bu kayıtla ilgili işlem yapmış herkese notification gönder(işlenmi yapan
      // hariç)
      for (Integer notificationUserId :
          (List<Integer>)
              dao.find(
                  "select distinct r.userId from Log5WorkflowRecord r where r.approvalRecordId=? AND r.userId!=?",
                  ar.getApprovalRecordId(),
                  userId)) {
        notificationList.add(notificationUserId.toString());
      }

      mailSubject =
          a.getDsc()
              + " ("
              + LocaleMsgCache.get2(
                  scd,
                  a.get_approvalStepMap().get(ar.getApprovalStepId()).getNewInstance().getDsc())
              + ")";
      mailBody =
          ar.getDsc()
              + mesaj
              + (!GenericUtil.isEmpty((String) parameterMap.get("_adsc"))
                  ? "\n\n" + parameterMap.get("_adsc")
                  : "");

      messageBody = ar.getDsc() + mesaj;

      ar.setFinishedFlag((short) 1);
      ar.setApprovalRoles(null);
      ar.setApprovalUsers(null);
      ar.setAccessViewTip((short) 0);
    }

    /* Record Save Ediliyor */
    saveObject(logRecord);
    ar.setVersionNo(ar.getVersionNo() + 1);
    dao.updateObject(ar);

    if (!isFinished && (approvalAction == 1 || approvalAction == 2 || approvalAction == 901)) {
      /*
      String stepName =a.get_approvalStepMap().get(ar.getApprovalStepId()).getNewInstance().getDsc();
      if(stepName == null)stepName =" ";
      else stepName =" '"+LocaleMsgCache.get2(ar.getCustomizationId(), xlocale,stepName)+"' "+ LocaleMsgCache.get2(0,xlocale,"adim") +" ";
      //Bir sonraki adım için ilgili kişilere mail gönderme işlemi
      if(nextStep!=null || a.getApprovalFlowTip()==2 || a.getApprovalFlowTip() == 3){//mail gondermece, burada hiyerarşiler aradında da mail gitsin mantığı var
      	List<Object[]> nextStepUsers= dao.executeSQLQuery(
      			"select gu.gsm,gu.email,gu.user_id from iwb.w5_user gu where gu.customization_id = ? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_status = 1 union " +
      			"select gu.gsm,gu.email,gu.user_id from iwb.w5_user_role ur, iwb.w5_user gu where gu.user_id = ur.user_id and gu.user_status = 1 and gu.customization_id = ? and " +
      			"ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and " +
                     "((select u.user_tip from iwb.w5_role u where u.role_id = ur.role_id and u.customization_id = ur.customization_id) <> 3 or "+
                     "((select u.user_tip from iwb.w5_role u where u.role_id = ur.role_id and u.customization_id = ur.customization_id) = 3))",
                     ar.getCustomizationId(), ar.getApprovalUsers(), ar.getCustomizationId(), ar.getApprovalRoles());
      		if(nextStepUsers == null && nextStep.getApprovalStepId() == 901){ // Manuel Onay'da kimlere mail gideceği
      			String userIds = ar.getInsertUserId()+(a.getManualAppUserIds() != null ? ","+a.getManualAppUserIds() : "")+(currentStep.getApprovalUsers() != null ? ","+currentStep.getApprovalUsers() : "");
      			String userRoles = (a.getManualAppRoleIds() != null ? a.getManualAppRoleIds() : "")+(currentStep.getApprovalRoles() != null ? (a.getManualAppRoleIds() != null ? ","+currentStep.getApprovalRoles() : currentStep.getApprovalRoles()) : "");
      			nextStepUsers = dao.executeSQLQuery(
      				"select gu.gsm,gu.email,gu.user_id from iwb.w5_user gu where gu.customization_id = ? and gu.user_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and gu.user_status = 1 union " +
      				"select gu.gsm,gu.email,gu.user_id from iwb.w5_user_role ur, iwb.w5_user gu where gu.user_id = ur.user_id and gu.user_status = 1 and gu.customization_id = ? and " +
      				"ur.role_id in (select x.satir::integer from iwb.tool_parse_numbers(?,\',\') x) and " +
                      "((select u.user_tip from iwb.w5_role u where u.role_id = ur.role_id and u.customization_id = ur.customization_id) <> 3 or "+
                      "((select u.user_tip from iwb.w5_role u where u.role_id = ur.role_id and u.customization_id = ur.customization_id) = 3 )",
                      ar.getCustomizationId(), userIds, ar.getCustomizationId(), userRoles);
      	}
      	if(nextStepUsers != null){ // Bu kullanıcı listesi mevcutsa
      		for(Object[] liste : nextStepUsers){
      			if(liste[0]!=null && liste[0].toString().length()> 5 && nextStep!=null && nextStep.getSendSmsOnEnterStepFlag()!=0){//sms gondermece, length kontrolü bazı kullanıcılar için 0 girilmiş bunun için düz mantık
      				gsmList.add(liste[0].toString());
      			}
      			if(liste[1]!=null && nextStep!=null && nextStep.getSendMailOnEnterStepFlag()!=0){//mail adresi toplama (hepsini topla göndermek için toplanıyor)
      				pemailList+=","+ liste[1].toString();
      			}
      			if(liste[2]!=null){//user idler
      				notificationList.add(liste[2].toString());
      			}
      		}
      	}
      	if(nextStep!=null && nextStep.getSendMailOnEnterStepFlag()!=0){
      		mailSubject = a.getDsc()+" ("+(approvalAction != 2 ? LocaleMsgCache.get2((Integer)scd.get("customizationId"),xlocale,a.get_approvalStepMap().get(ar.getApprovalStepId()).getNewInstance().getDsc()) : LocaleMsgCache.get2(0,xlocale,"returned"))+")";
      		mailBody = ar.getDsc()+" ("+ stepName+" - "+ mesaj +")" + (!GenericUtil.isEmpty((String)parameterMap.get("_adsc")) ? "\n\n"+parameterMap.get("_adsc") : "");
      	}
      	if(nextStep!=null && nextStep.getSendSmsOnEnterStepFlag()!=0){
      		messageBody = ar.getDsc()+ stepName + mesaj;
      	}
      }*/
    }

    if (pemailList.length() > 0) {
      /*			int mail_setting_id=GenericUtil.uInt((Object)scd.get("mailSettingId"));
      if(mail_setting_id==0) mail_setting_id=FrameworkCache.getAppSettingIntValue(scd, "default_outbox_id");
      W5ObjectMailSetting oms=  (W5ObjectMailSetting) dao.getCustomizedObject("from W5ObjectMailSetting x where x.mailSettingId=?", customizationId,mail_setting_id, null);
      if(oms!=null){
      	W5Email email= new W5Email(pemailList.substring(1), null, null, mailSubject, mailBody, null);
      	email.set_oms(oms);
      	String sonuc = MailUtil.sendMail(scd, email);
      	if(sonuc!=null){ //basarisiz, queue'ye at//
      		System.out.println("Hata! Onay Mekanizması Akışında Mail Gönderilemedi");
      	}else{
      		System.out.println("Onay Mekanızması Ä°çin Mail Başarıyla Gönderildi");
      	}
      }*/
    }

    if (gsmList.size() > 0) {
      String phoneNumber = "";
      for (String gsm : gsmList)
        phoneNumber = phoneNumber + (GenericUtil.isEmpty(phoneNumber) ? "" : ",") + gsm;

      sendSms(
          GenericUtil.uInt(scd.get("customizationId")),
          GenericUtil.uInt(scd.get("userId")),
          phoneNumber,
          messageBody,
          ar.getTableId(),
          ar.getTablePk());
    }

    if (notificationList.size() > 0) {
      for (String uId : notificationList) {
        dao.saveObject2(
            new Log5Notification(
                scd,
                Integer.parseInt(uId),
                (short) (2 + approvalAction),
                a.getTableId(),
                ar.getTablePk(),
                userId,
                null,
                1),
            scd);
      }
    }

    // Comment Yazma
    if (!GenericUtil.isEmpty((String) parameterMap.get("_adsc"))) {
      W5Comment comment = new W5Comment((String)scd.get("projectId"));
      comment.setTableId(ar.getTableId());
      comment.setTablePk(ar.getTablePk());
      comment.setDsc(parameterMap.get("_adsc") + "");
      comment.setCommentUserId(userId);
      comment.setCommentDttm(new java.sql.Timestamp(new Date().getTime()));
//      comment.setCustomizationId(Integer.parseInt(scd.get("customizationId") + ""));
      saveObject(comment);
    }

    // Feed Yazma
    if (FrameworkSetting.feed
        && FrameworkCache.getAppSettingIntValue(scd, "feed_flag") != 0
        && (a.getActionTip() != 3 || !isFinished)) {
      Log5Feed feed = new Log5Feed(scd);
      feed.setFeedTip((short) ((approvalAction > 900 ? 0 : 6) + approvalAction));
      feed.setTableId(a.getTableId());
      feed.setTablePk(ar.getTablePk());
      if (ar.getAccessViewTip() != 0)
        feed.set_viewAccessControl(
            new W5AccessControlHelper(ar.getAccessViewRoles(), ar.getAccessViewUsers()));
      feed.set_tableRecordList(
          dao.findRecordParentRecords(scd, feed.getTableId(), feed.getTablePk(), 0, true));
      if (feed.get_tableRecordList() != null && feed.get_tableRecordList().size() > 0)
        feed.set_commentCount(feed.get_tableRecordList().get(0).getCommentCount());

      saveObject(feed);
      FrameworkCache.addFeed(scd, feed, true);

      /*int maxDerinlik = PromisCache.getAppSettingIntValue(scd, "feed_control_depth");
      for(int qi=lx.size()-1;qi>=0 && maxDerinlik>0;maxDerinlik--,qi--){//bir onceki feedlerle iliskisi belirleniyor
      	W5Feed lfeed =lx.get(qi);
      	if(lfeed==null)continue;
      	if(lfeed.getTableId()==feed.getTableId() && lfeed.getTablePk()==feed.getTablePk() && lfeed.getFeedTip()==feed.getFeedTip()){//edit haricinde birsey veya edit ise ayni tablo uzerinde detay seviyesinde
      		lx.set(qi,null);
      		feed.set_relatedFeedMap(new HashMap<Integer,W5Feed>());
      		feed.get_relatedFeedMap().put(lfeed.getFeedId(),lfeed);
      		if(lfeed.get_relatedFeedMap()!=null)feed.get_relatedFeedMap().putAll(lfeed.get_relatedFeedMap());
      		break;
      	}
      }
      	*/
      // TODO: bu kadar basit degil. daha akillica olmasi lazim
      /*if(approvalAction==1 && isFinished){//onay ve bittiyse
      dao.getHibernateTemplate().flush();
      dao.copyTableRecord(392, ar.getApprovalRecordId(), PromisCache.getAppSettingStringValue(scd, "default_schema"), PromisCache.getAppSettingStringValue(scd, "log_crud_schema"));
      dao.removeObject(ar);
      }*/
    }

    result.put("status", true);

    String webPageId = parameterMap.get(".w");
    if (ar != null && !GenericUtil.isEmpty(webPageId)) {
      Map m = new HashMap();
      m.put(".w", webPageId);
      m.put(".pk", ar.getTableId() + "-" + ar.getTablePk());
      m.put(".a", "11");
      m.put(".e", "4");
      UserUtil.liveSyncAction(
          scd, m); // (customizationId, table_id+"-"+table_pk, userId, webPageId, false);
    }
    return result;
  }

  public W5FormResult importUploadedData(
      Map<String, Object> scd, int uploadedImportId, Map<String, String> requestParams) {
    W5UploadedImport ui =
        (W5UploadedImport)
            dao.find(
                    "from W5UploadedImport t where t.customizationId=? and t.uploadedImportId=?",
                    scd.get("customizationId"),
                    uploadedImportId)
                .get(0);
    W5FormResult formResult = dao.getFormResult(scd, ui.getFormId(), 2, requestParams);
    W5Table t =
        FrameworkCache.getTable(
            scd, formResult.getForm().getObjectId()); // formResult.getForm().get_sourceTable();
    String locale = (String) scd.get("locale");
    /*		if(t.getAccessViewTip()==0 && !PromisCache.roleAccessControl(scd, 0)){
    	throw new PromisException("security","Module", 0, null, "Modul Kontrol: Erişim kontrolünüz yok", null);
    }
    if(!PromisUtil.accessControl(scd, t.getAccessViewTip(), t.getAccessViewRoles(), t.getAccessViewUsers())){
    	throw new PromisException("security","Form", ui.getFormId(), null, PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_goruntuleme"), null);
    }*/
    PostFormTrigger.beforePostForm(formResult, dao, "");

    //		accessControl4FormTable(formResult);
    if (formResult.isViewMode()) {
      throw new IWBException(
          "security",
          "Form",
          ui.getFormId(),
          null,
          LocaleMsgCache.get2(
              0, (String) scd.get("locale"), "fw_guvenlik_tablo_kontrol_guncelleme"),
          null);
    }
    List<Object[]> uicl =
        dao.find(
            "select x.mappingType,x.importColumn,x.formCellId,x.defaultValue,y.dsc from W5UploadedImportCol x,W5FormCell y where y.customizationId=x.customizationId and y.customizationId=? AND y.formCellId=x.formCellId AND x.uploadedImportId=? order by y.tabOrder",
            scd.get("customizationId"),
            uploadedImportId);
    List<Object> sqlParams = new ArrayList();
    String detailSql = "select x.row_no";
    for (Object[] o : uicl) {
      short mappingType = (Short) o[0];
      String importColumn = (String) o[1];
      int formCellId = (Integer) o[2];
      String dsc = (String) o[4];
      if (/*importColumn==null || */ dsc == null) continue;
      switch (mappingType) {
        case 0: // constant
          if (o[3] != null) requestParams.put(dsc, (String) o[3]);
          break;
        case 1: // row-data
          if (o[3] != null) {
            detailSql += ", coalesce(x." + importColumn + ",?) " + dsc;
            sqlParams.add(o[3]);
          } else detailSql += ", x." + importColumn + " " + dsc;
          break;
        case 2: // mapping
          short cellControlTip = 0;
          int cellLookupQueryId = 0;
          for (W5FormCell c : formResult.getForm().get_formCells()) {
            if (c.getFormCellId() == formCellId) {
              cellControlTip = c.getControlTip();
              cellLookupQueryId = c.getLookupQueryId();
              break;
            }
          }
          if (cellControlTip == 6) { // static lookup
            detailSql +=
                ", (select ld.val from iwb.w5_look_up_detay ld where ld.customization_id=x.customization_id and ld.look_up_id="
                    + cellLookupQueryId
                    + " and lower(iwb.fnc_locale_msg(ld.customization_id,ld.dsc,'"
                    + locale
                    + "'))=lower(x."
                    + importColumn
                    + "))"
                    + dsc;
          } else if (cellControlTip == 7 || cellControlTip == 9) { // lookup query
            W5QueryResult queryResult = dao.getQueryResult(scd, cellLookupQueryId);
            if (queryResult.getMainTable() != null) {
              boolean dscExists = false;
              for (W5TableField f : queryResult.getMainTable().get_tableFieldList()) {
                if (f.getDsc().equals("dsc")) {
                  dscExists = true;
                  break;
                }
              }
              if (dscExists) {
                String pk =
                    queryResult.getMainTable().get_tableParamList().get(0).getExpressionDsc();
                detailSql +=
                    ", (select d."
                        + pk
                        + " from "
                        + queryResult.getMainTable().getDsc()
                        + " d where d.customization_id=x.customization_id and lower(d.dsc)=lower(x."
                        + importColumn
                        + "))"
                        + dsc;
              } else detailSql += ", x." + importColumn + " " + dsc;
            }
          } else {
            /*sqlParams.add(ui.getFileAttachmentId());
            sqlParams.add(formCellId);
            if(o[3]!=null){
            	detailSql+=", coalesce((select q.import_lookup_val from iwb.w5_uploaded_import_col_map q where q.customization_id=x.customization_id and q.file_attachment_id=? AND q.form_cell_id=? AND q.import_val=x."+importColumn+"),?)" + dsc;
            	sqlParams.add(o[3]);
            } else
            	detailSql+=", (select q.import_lookup_val from iwb.w5_uploaded_import_col_map q where q.customization_id=x.customization_id and q.file_attachment_id=? AND q.form_cell_id=? AND q.import_val=x."+importColumn+")" + dsc;
            	*/
            if (o[3] != null) {
              detailSql += ", x." + importColumn + " " + dsc;
            }
          }
          break;
        case 3: // auto-increment
          detailSql += ", rownum " + dsc;
          break;
      }
    }
    detailSql +=
        " from iwb.w5_uploaded_data x where x.customization_id=? and x.file_attachment_id=? and x.row_no>="
            + (ui.getStartRowNo() - 1);
    if (ui.getEndRowNo() != null) {
      detailSql += " and x.row_no<" + ui.getEndRowNo();
    }
    detailSql += " order by x.row_no";
    sqlParams.add(scd.get("customizationId"));
    sqlParams.add(ui.getFileAttachmentId());

    if (FrameworkSetting.debug)
      System.out.println("ERALP-XXX: " + GenericUtil.replaceSql(detailSql, sqlParams));
    List<Map> rl = dao.executeSQLQuery2Map(detailSql, sqlParams);
    int errorCount = 0, importedCount = 0;
    if (rl != null)
      for (Map m : rl) {
        m.putAll(requestParams);
        formResult.setRequestParams(m);
        formResult.setErrorMap(new HashMap());
        dao.insertFormTable(formResult, "");
        if (!formResult.getErrorMap().isEmpty()) { // hata var
          if (ui.getRowErrorStrategyTip() == 1) { // throw error
            formResult.getErrorMap().put(" [SATIR_NO]", m.get("row_no").toString());
            return formResult;
          } else errorCount++;
        } else importedCount++;
      }
    formResult.getOutputFields().put("errorCount", errorCount);
    formResult.getOutputFields().put("importedCount", importedCount);
    //		if(true)throw new PromisException("security","Form", ui.getFormId(), null,
    // PromisLocaleMsg.get2(0,(String)scd.get("locale"),"fw_guvenlik_tablo_kontrol_goruntuleme"),
    // null);
    return formResult;
  }

  public synchronized void scheduledFrameworkCacheReload() {
    /*		if (FrameworkCache.reloadCacheQueue.isEmpty())
    			return;
    		Set<Integer>	customizationSet = new HashSet();
    //		boolean notifiedAll = false;
    		Set<Integer> notifiedSet = new HashSet();
    		try {
    //			PromisSetting.systemStatus= 2;//suspended
    			long currentTimeMillis = System.currentTimeMillis();
    			List<String> keyz = new ArrayList();
    			for (String s : FrameworkCache.reloadCacheQueue.keySet()){
    				String[] qs=s.split("-");
    				int islem = GenericUtil.uInt(qs[0]);
    				int customizationId = GenericUtil.uInt(qs[1]);
    				customizationSet.add(customizationId);
    				long dirtyTimeMillis = FrameworkCache.reloadCacheQueue.get(s);
    				if(currentTimeMillis-dirtyTimeMillis>1000){//belirli bir aradan sonra
    					if(!notifiedSet.contains(customizationId)){ //!notifiedAll
    						FrameworkSetting.projectSystemStatus.put(customizationId,2);
    //						notifiedAll = true;
    						notifiedSet.add(customizationId);
    						UserUtil.publishNotification(new Log5Notification("System Reloading",2 , customizationId), true);//warning
    					}
    					switch(islem){
    						case 1://lookup,detay
    							dao.reloadLookUpCache(customizationId);
    							break;
    						case 2://apsetting
    							dao.reloadApplicationSettingsCache(customizationId);
    							break;
    						case 4://aproval/step
    							dao.reloadWorkflowCache(customizationId);
    							break;
    						case 6://table
    							dao.reloadTableParamListChildListParentListCache(customizationId);
    							dao.reloadTablesCache(customizationId);
    							dao.reloadTableFiltersCache(customizationId);
    							break;
    						case 7://job_schedule
    							dao.reloadJobsCache();
    							break;
    						case	9://tableActionRule
    							dao.reloadTableEventsCache(customizationId);
    							break;
    					}
    					keyz.add(s);
    				}
    			}
    			for(String s:keyz)FrameworkCache.reloadCacheQueue.remove(s);
    			if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.clearPreloadCache();
    		} catch (Exception e) {
    			if(FrameworkSetting.debug)e.printStackTrace();
    			System.out.println("Cache Yeniden Yüklenemedi, HATA: " + e.getMessage());
    		} finally {
    //			PromisSetting.systemStatus = 0;
    			for(Integer cusId:notifiedSet) {
    				FrameworkSetting.projectSystemStatus.put(cusId,0);
    			}
    			for(Integer i:customizationSet)
    				UserUtil.publishNotification(new Log5Notification("System Reloaded",0 , i), true);//warning
    		}*/
  }

  /*
  	public W5QueryResult executeQuery4BulkUpdate(Map<String, Object> scd,
  			int tableId, Map<String, String> requestParams, boolean onlyIdsFlag) {
  		W5Table t = FrameworkCache.getTable(scd, tableId);
  		W5QueryResult queryResult = new W5QueryResult(-tableId);
  		queryResult.setScd(scd);
  //query
  		W5Query q = new W5Query();
  		queryResult.setQuery(q);
  		q.setMainTableId(tableId);
  		q.setSqlFrom(t.getDsc()+" x");
  		q.setSqlSelect("x."+t.get_tableFieldList().get(0).getDsc()+" id" + (onlyIdsFlag ? "" : ",("+(t.getSummaryRecordSql()!=null? t.getSummaryRecordSql() : "'ERROR: define summary-sql'")+") dsc"));
  		if(t.get_tableParamList().size()>1 && t.get_tableParamList().get(1).getDsc().equals("customizationId"))
  			q.setSqlWhere("x.customization_id=${scd.customizationId}");
  //fields
  		q.set_queryFields(new ArrayList<W5QueryField>(2));
  		W5QueryField f1 = new W5QueryField();f1.setDsc("id");f1.setTabOrder((short)1);
  		q.get_queryFields().add(f1);
  		if(!onlyIdsFlag){
  			W5QueryField f2 = new W5QueryField();f2.setDsc("dsc");f2.setTabOrder((short)2);
  			q.get_queryFields().add(f2);
  		}
  //params
  		q.set_queryParams(new ArrayList());
  		String conditionFields = requestParams.get("pcondition_field_ids");
  		if(conditionFields!=null && conditionFields.length()>1){
  			String[] cfs = conditionFields.split(",");
  			for(String cf:cfs){
  				String[] ps = cf.replace(".", ",").split(",");
  				int tableFieldId = GenericUtil.uInt(ps[0]);
  				W5TableField tf = t.get_tableFieldMap().get(tableFieldId);
  				int operatorTip =  GenericUtil.uInt(ps[1]);
  				if(operatorTip!=14){//between den farkli
  					W5QueryParam qp = new W5QueryParam();
  					qp.setDsc("c_"+tf.getDsc()+"1");
  					qp.setExpressionDsc("x."+tf.getDsc());
  					qp.setOperatorTip((short)operatorTip);
  					qp.setSourceTip((short)1);
  					qp.setParamTip(tf.getFieldTip());
  					qp.setNotNullFlag(tf.getNotNullFlag());
  					q.get_queryParams().add(qp);
  				} else {
  					W5QueryParam qp1 = new W5QueryParam();
  					qp1.setDsc("c_"+tf.getDsc()+"1");
  					qp1.setExpressionDsc("x."+tf.getDsc());
  					qp1.setOperatorTip((short)5);
  					qp1.setSourceTip((short)1);
  					qp1.setParamTip(tf.getFieldTip());
  					qp1.setNotNullFlag(tf.getNotNullFlag());
  					q.get_queryParams().add(qp1);
  					W5QueryParam qp2 = new W5QueryParam();
  					qp2.setDsc("c_"+tf.getDsc()+"2");
  					qp2.setExpressionDsc("x."+tf.getDsc());
  					qp2.setOperatorTip((short)4);
  					qp2.setSourceTip((short)1);
  					qp2.setParamTip(tf.getFieldTip());
  					qp2.setNotNullFlag(tf.getNotNullFlag());
  					q.get_queryParams().add(qp2);
  				}
  			}
  		}
  		conditionFields = requestParams.get("pcondition_field2_ids");
  		if(conditionFields!=null && conditionFields.length()>1){
  			String[] cfs = conditionFields.split(",");
  			for(String cf:cfs){
  				String[] ps = cf.replace(".", ",").split(",");
  				int tableFieldCalculatedId = GenericUtil.uInt(ps[0]);
  				W5TableFieldCalculated tf = (W5TableFieldCalculated)dao.getCustomizedObject("from W5TableFieldCalculated t where t.tableFieldCalculatedId=? AND t.customizationId=?", tableFieldCalculatedId, t.getCustomizationId(), null);//t.get_tableFieldMap().get(tableFieldCalculatedId);
  				if(tf==null || tf.getTableId()!=t.getTableId())continue;
  				int operatorTip =  GenericUtil.uInt(ps[1]);
  				Object[] o = DBUtil.filterExt4SQL(tf.getSqlCode(), scd, requestParams, null);
  				if(operatorTip!=14){//between den farkli
  					W5QueryParam qp = new W5QueryParam();
  					qp.setDsc("c2_"+tf.getDsc()+"1");
  					qp.setExpressionDsc("("+o[0].toString()+")");
  //					q.get_queryParams().addAll((List)o[1]);
  					qp.setOperatorTip((short)operatorTip);
  					qp.setSourceTip((short)1);
  					qp.setParamTip(tf.getFieldTip());
  //					qp.setNotNullFlag(tf.getNotNullFlag());
  					q.get_queryParams().add(qp);
  				} else {
  					W5QueryParam qp1 = new W5QueryParam();
  					qp1.setDsc("c2_"+tf.getDsc()+"1");
  					qp1.setExpressionDsc("("+o[0].toString()+")");
  //					q.get_queryParams().addAll((List)o[1]);
  					qp1.setOperatorTip((short)5);
  					qp1.setSourceTip((short)1);
  					qp1.setParamTip(tf.getFieldTip());
  //					qp1.setNotNullFlag(tf.getNotNullFlag());
  					q.get_queryParams().add(qp1);
  					W5QueryParam qp2 = new W5QueryParam();
  					qp2.setDsc("c2_"+tf.getDsc()+"2");
  					qp2.setExpressionDsc("("+o[0].toString()+")");
  //					q.get_queryParams().addAll((List)o[1]);
  					qp2.setOperatorTip((short)4);
  					qp2.setSourceTip((short)1);
  					qp2.setParamTip(tf.getFieldTip());
  //					qp2.setNotNullFlag(tf.getNotNullFlag());
  					q.get_queryParams().add(qp2);
  				}
  			}
  		}
  		queryResult.setErrorMap(new HashMap());
  		queryResult.setRequestParams(requestParams);
  		queryResult.setViewLogModeTip((short)GenericUtil.uInt(requestParams,"_vlm"));
  //		queryResult.setOrderBy(PromisUtil.uStrNvl(requestParams.get(PromisUtil.uStrNvl(PromisSetting.appSettings.get("sql_sort"),"sort")), queryResult.getQuery().getSqlOrderby()));
  		queryResult.prepareQuery(null);
  		if(queryResult.getErrorMap().isEmpty()){
  	        queryResult.setFetchRowCount(GenericUtil.uIntNvl(requestParams, "limit", GenericUtil.uInt(requestParams,"firstLimit")));
  	        queryResult.setStartRowNumber(GenericUtil.uInt(requestParams,"start"));
          	dao.runQuery(queryResult);
  		}
  		return queryResult;
  	}
  	public W5GlobalFuncResult postBulkSmsMail4Table(Map<String, Object> scd, int formSmsMailId,
  			Map<String, String> requestParams) {
  		W5FormSmsMail fsm = (W5FormSmsMail)dao.getCustomizedObject("from W5FormSmsMail t where t.formSmsMailId=? AND t.customizationId=?", formSmsMailId, (Integer)scd.get("customizationId"),"FormSmsMail");
  		W5Form f = (W5Form)dao.getCustomizedObject("from W5Form t where t.formId=? AND t.customizationId=?", fsm.getFormId(), (Integer)scd.get("customizationId"), "Form");
  		int tableId = f.getObjectId();
  		W5Table t = FrameworkCache.getTable(scd, tableId);
  		if(!FrameworkCache.roleAccessControl(scd, 104)){
      		throw new IWBException("security","Module",0,null, "No Authorization for Bulk SMS/Email. Please contact Administrator", null);
  		}
  		requestParams.remove("firstLimit");requestParams.remove("limit");requestParams.remove("start");
  		W5QueryResult qr = executeQuery4BulkUpdate(scd, tableId, requestParams, true);
  		int updateCount = (int)qr.getResultRowCount();
  		List<String> arIds = null;
  		if(updateCount==0){
  			return null;
  		}
  		String selectedIds = requestParams.get("pselected_ids");
  		if(selectedIds!=null && selectedIds.split(",").length>0){
  			String[] sis = selectedIds.split(",");
  			updateCount = sis.length;
  			arIds = new ArrayList(updateCount);
  			Set<String> siss = new HashSet<String>();
  			for(String si:sis)siss.add(si);
  			for(Object[] os:qr.getData())if(siss.contains(os[0].toString())){
  				arIds.add(os[0].toString());
  				siss.remove(os[0].toString());
  				if(siss.isEmpty())break;
  			}
  		} else {
  			arIds = new ArrayList(updateCount);
  			for(Object[] os:qr.getData()){
  				arIds.add(os[0].toString());
  			}
  		}
  		W5GlobalFuncResult r = null;
  		for(String s:arIds){
  			if(fsm.getSmsMailTip() == 0){//sms
  			} else {//email
  				W5Email email = dao.interprateMailTemplate(fsm, scd,requestParams, tableId, GenericUtil.uInt(s));
  				MailUtil.sendMail(scd, email);
  			}
  		}
  		return r;
  	}
  	public W5FormResult postBulkUpdate4Table(Map<String, Object> scd, int tableId,
  			Map<String, String> requestParams) {
  		W5Table t = FrameworkCache.getTable(scd, tableId);
  		if(!FrameworkCache.roleAccessControl(scd, 11)){
      		throw new IWBException("security","Module",0,null, LocaleMsgCache.get2(0,(String)scd.get("locale"),"fw_guvenlik_no_authorization4bulk_update"), null);
  		}
  		requestParams.remove("firstLimit");requestParams.remove("limit");requestParams.remove("start");
  		W5QueryResult qr = executeQuery4BulkUpdate(scd, tableId, requestParams, true);
  		int updateCount = (int)qr.getResultRowCount();
  		List<String> arIds = null;
  		if(updateCount==0){
  			return null;
  		}
  		String selectedIds = requestParams.get("pselected_ids");
  		if(selectedIds!=null && selectedIds.split(",").length>0){
  			String[] sis = selectedIds.split(",");
  			updateCount = sis.length;
  			arIds = new ArrayList(updateCount);
  			Set<String> siss = new HashSet<String>();
  			for(String si:sis)siss.add(si);
  			for(Object[] os:qr.getData())if(siss.contains(os[0].toString())){
  				arIds.add(os[0].toString());
  				siss.remove(os[0].toString());
  				if(siss.isEmpty())break;
  			}
  		} else {
  			arIds = new ArrayList(updateCount);
  			for(Object[] os:qr.getData()){
  				arIds.add(os[0].toString());
  			}
  		}
  		Map<String, String> m = new HashMap<String, String>();
  		String updateFields = requestParams.get("pupdate_fields");
  		String[] ufs = updateFields.split(",");
  		int xi = 1;
  		for(String s: arIds){
  			m.put("a"+xi, "1");
  			m.put(t.get_tableParamList().get(0).getDsc()+xi, s);
  			for(String uf:ufs){
  				m.put(uf+xi, requestParams.get("u_"+uf));
  				String opStr = requestParams.get("u_"+uf + FrameworkSetting.bulkOperatorPostfix);
  				if(!GenericUtil.isEmpty(opStr))m.put(uf+FrameworkSetting.bulkOperatorPostfix+xi, opStr);
  			}
  			xi++;
  		}
  		return postEditGrid4Table(scd, t.getDefaultUpdateFormId(), arIds.size(), m, "", new HashSet<String>());
  	}
  */
  public JasperPrint prepareJasperPrint(
      Map<String, Object> scd, Map<String, String> requestParams, JRFileVirtualizer virtualizer) {
    JasperPrint jasperPrint = new JasperPrint();
    int customizationId = (Integer) scd.get("customizationId");
    int jasperReportId = GenericUtil.uInt(requestParams, "_jrid");
    int jasperTypeId =
        GenericUtil.uInt(
            requestParams,
            "_jtid"); // jasper raporlara ön yazı eklemek icin   _jtid isminde raporlar yolluyoruz.
    int jasperFooterId =
        GenericUtil.uInt(
            requestParams,
            "_jfid"); // jasper raporlara alt yazı eklemek icin   _jfid isminde raporlar yolluyoruz.
    W5JasperResult result = getJasperResult(scd, requestParams, jasperReportId);
    String locale =
        (requestParams.get("tlocale") == null)
            ? (String) scd.get("locale")
            : (String)
                requestParams.get(
                    "tlocale"); // Sisteme login dili değilde farklı bir dil seçeneği kullanılmak
    // isteniyosa
    String file_name = result.getFile_name();
    String fileLocalPath = FrameworkCache.getAppSettingStringValue(scd, "file_local_path");
    // String localePath=fileLocalPath+"/"+(jasperReportId <100000 ? 0:customizationId)+"/jasper/";
    Map<String, Object> resultMap = (Map) result.getResultMap();
    W5Customization c = FrameworkCache.wCustomizationMap.get(customizationId);

    resultMap.put(
        "CompanyLogoFilePath",
        fileLocalPath + "/0/jasper/inscada.png"); // firma logosunun pathi gönderiliyor

    try {
      String localePath = fileLocalPath + "/" + customizationId + "/jasper/";
      File f = new File(localePath + file_name);
      if (!f.exists()) {
        localePath = fileLocalPath + "/0/jasper/";
        f = new File(localePath + file_name);
      }
      resultMap.put("localePath", localePath); // jasper folder path
      resultMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
      jasperPrint =
          JasperFillManager.fillReport(
              localePath + file_name,
              resultMap,
              new JRMapCollectionDataSource(result.getResultDetail()));
      /*if(result.getJasper().getLocaleKeyFlag()==1){//Jasper Raporunda textfild'e Key degerleri verilmisse,Key degerinin karsılıgı alınıyor.
      	jasperPrint=JasperUtil.convertKey2LocaleMsg(jasperPrint,locale);
      }*/

      if (jasperTypeId > 0) { // Jasper Raporlar için Kapak Yazısı
        W5JasperReport jp = getJasperReport(scd, result.getJasperId(), requestParams, jasperTypeId);
        Map<String, Object> resultMapCover = (Map) result.getResultMap();
        resultMapCover.put("total_page_number", jasperPrint.getPages().size());
        file_name = jp.getReportFileName();
        localePath = fileLocalPath + "/" + customizationId + "/jasper/";
        f = new File(localePath + file_name);
        if (!f.exists()) {
          localePath = fileLocalPath + "/0/jasper/";
          f = new File(localePath + file_name);
        }
        JasperPrint coverJasperPrint =
            JasperFillManager.fillReport(
                localePath + file_name,
                resultMapCover,
                new JRMapCollectionDataSource(result.getResultDetail()));
        // coverJasperPrint=JasperUtil.convertKey2LocaleMsg(coverJasperPrint,locale);
        int index = 0;
        for (Object addPage : coverJasperPrint.getPages()) {
          jasperPrint.addPage(index, (JRPrintPage) addPage);
          index++;
        }
      }

      if (jasperFooterId > 0) { // Jasper Raporlar için Alt Yazı
        W5JasperReport jp =
            getJasperReport(scd, result.getJasperId(), requestParams, jasperFooterId);
        Map<String, Object> resultMapFooter = (Map) result.getResultMap();
        resultMapFooter.put("total_page_number", jasperPrint.getPages().size());
        file_name = jp.getReportFileName();
        localePath = fileLocalPath + "/" + customizationId + "/jasper/";
        f = new File(localePath + file_name);
        if (!f.exists()) {
          localePath = fileLocalPath + "/0/jasper/";
          f = new File(localePath + file_name);
        }
        JasperPrint footerJasperPrint =
            JasperFillManager.fillReport(
                localePath + file_name,
                resultMapFooter,
                new JRMapCollectionDataSource(result.getResultDetail()));
        // footerJasperPrint=JasperUtil.convertKey2LocaleMsg(footerJasperPrint,locale);
        int index = jasperPrint.getPages().size();
        for (Object addPage : footerJasperPrint.getPages()) {
          jasperPrint.addPage(index, (JRPrintPage) addPage);
          index++;
        }
      }

      /*if(result.getJasper().getLocaleKeyFlag()==1){//Jasper Raporunda textfild'e Key degerleri verilmisse,Key degerinin karsılıgı alınıyor.
      	jasperPrint=JasperUtil.convertKey2LocaleMsg(jasperPrint,locale);
      }		*/

    } catch (Exception e) {
      if (FrameworkSetting.debug) e.printStackTrace();
      throw new IWBException("Error", "Jasper", 0, "", e.getMessage(), e.getCause());
    }
    return jasperPrint;
  }

  public W5JasperReport getJasperReport(
      Map<String, Object> scd, int jasperId, Map<String, String> requestParams, int jasperTypeId) {
    List<W5JasperReport> l =
        (List<W5JasperReport>)
            dao.find(
                "from W5JasperReport t where t.customizationId=? and t.jasperReportId=?",
                scd.get("customizationId"),
                jasperTypeId);
    return l.isEmpty() ? null : l.get(0);
  }

  public W5TableRecordInfoResult getTableRecordInfo(
      Map<String, Object> scd, int tableId, int tablePk) {
    //		if(t==null)
    W5TableRecordInfoResult result = dao.getTableRecordInfo(scd, tableId, tablePk);
    if (result != null) {
      result.setParentList(dao.findRecordParentRecords(scd, tableId, tablePk, 10, false));
      result.setChildList(dao.findRecordChildRecords(scd, tableId, tablePk));
      return result;
    } else return null;
  }

  public W5QueryResult getTableRelationData(
      Map<String, Object> scd, int tableId, int tablePk, int relId) {
    W5Table mt = FrameworkCache.getTable(scd, tableId); // master table
    if (!GenericUtil.accessControlTable(scd, mt))
      throw new IWBException(
          "security",
          "Module",
          0,
          null,
          LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"),
          null);
    if (relId == 0 || GenericUtil.isEmpty(mt.get_tableChildList()))
      throw new IWBException(
          "security", "Table", tableId, null, "wrong relationId or no children data", null);
    W5TableChild tc = null;
    for (W5TableChild qi : mt.get_tableChildList())
      if (qi.getTableChildId() == relId) {
        tc = qi;
        break;
      }
    if (tc == null)
      throw new IWBException("security", "Table", tableId, null, "relation not found", null);

    W5Table t = FrameworkCache.getTable(scd, tc.getRelatedTableId()); // detail table
    if (GenericUtil.isEmpty(t.getSummaryRecordSql()))
      throw new IWBException(
          "framework", "Table", tableId, null, "ERROR: summarySql not defined", null);
    W5Query q = new W5Query(t.getTableId());
    q.setSqlSelect(
        "("
            + t.getSummaryRecordSql()
            + ") dsc, x."
            + t.get_tableFieldList().get(0).getDsc()
            + " id");
    q.setSqlFrom(t.getDsc() + " x");
    StringBuilder sqlWhere = new StringBuilder();
    sqlWhere
        .append("x.")
        .append(t.get_tableFieldMap().get(tc.getRelatedTableFieldId()).getDsc())
        .append("=${req.id}");
    if (tc.getRelatedStaticTableFieldId() != 0)
      sqlWhere
          .append("AND x.")
          .append(t.get_tableFieldMap().get(tc.getRelatedStaticTableFieldId()).getDsc())
          .append("=")
          .append(tc.getRelatedStaticTableFieldVal());
    if (t.get_tableParamList().size() > 1
        && t.get_tableParamList().get(1).getExpressionDsc().equals("customization_id"))
      sqlWhere.append("AND x.customization_id=${scd.customizationId}");
    q.setSqlWhere(sqlWhere.toString());
    Map<String, String> requestParams = new HashMap();
    requestParams.put("id", "" + tablePk);

    q.set_queryFields(
        dao.find("from W5QueryField f where f.queryId=15 order by f.tabOrder")); // queryField'in
    // lookUp'i
    q.set_queryParams(new ArrayList());

    W5QueryResult qr = new W5QueryResult(t.getTableId());
    qr.setScd(scd);
    qr.setRequestParams(requestParams);
    qr.setErrorMap(new HashMap());
    qr.setMainTable(t);
    qr.setQuery(q);
    boolean tabOrderFlag = false;
    for (W5TableField tf : t.get_tableFieldList())
      if (tf.getDsc().equals("tab_order")) {
        tabOrderFlag = true;
        break;
      }
    qr.setOrderBy(
        tabOrderFlag
            ? "x.tab_order asc,x." + t.get_tableFieldList().get(0).getDsc() + " desc"
            : "x." + t.get_tableFieldList().get(0).getDsc() + " desc");
    qr.prepareQuery(null);

    if (qr.getErrorMap().isEmpty()) {
      qr.setFetchRowCount(10);
      qr.setStartRowNumber(0);
      dao.runQuery(qr);
    }

    return qr;
  }

  public String getFormCellCodeDetail(
      Map<String, Object> scd, Map<String, String> requestParams, int fccdId) {
    /*		W5FormCellCodeDetail fccd = (W5FormCellCodeDetail)dao.getCustomizedObject("from W5FormCellCodeDetail t where t.formCellCodeDetailId=? AND t.customizationId=?", fccdId, (Integer)scd.get("customizationId"), null);
    if(fccd==null || fccd.getCodeTip()!=4)return "";
    String sql = fccd.getDefaultValue();
    switch(fccd.getSourceFcQueryFieldId()){
    case	1:case	2:case	3:
    	String[] qrs=new String[]{"yy","yymm","yymmdd"};
    	sql="select to_char(current_date,'"+qrs[fccd.getSourceFcQueryFieldId()-1]+"')";
    default:
    	Map<String, Object> qm = dao.runSQLQuery2Map(sql, scd, requestParams, null);
    	if(!GenericUtil.isEmpty(qm)){
    		String val = qm.values().toArray()[0].toString();
    		return fccd.getCodeLength()>0 ? GenericUtil.lPad(val,fccd.getCodeLength(),fccd.getFillCharacter()) : val;
    	} else
    		throw new IWBException("validation","FormElement", fccd.getFormCellId(), null, "FormElementCode: wrong SQL code 4 (Formul/Advanced)", null);
    }*/
    return "";
  }

  public void setJVMProperties(int customizationId) {
    List<Object[]> list =
        dao.executeSQLQuery(
            "select dsc,val from iwb.w5_app_Setting x where x.setting_tip=19 and x.customization_id=?",
            customizationId);
    if (list != null && !list.isEmpty()) {
      for (Object[] t : list) System.setProperty(t[0].toString(), t[1].toString());
    }
  }

  public void checkAlarms(Map<String, Object> scd) {
    if (true) return;
    /*		List<W5FormSmsMailAlarm> l = dao.find("from W5FormSmsMailAlarm t where t.status=1 AND t.customizationId=? order by t.alarmDttm", scd.get("customizationId"));
    		long d = new Date().getTime();
    		for(W5FormSmsMailAlarm a:l)if(d-a.getAlarmDttm().getTime()>1000*30)try{
    			scd.put("userId", a.getInsertUserId());
    			scd.put("userTip", 2);
    			List l2 = dao.find("from W5FormSmsMail x where x.formSmsMailId=? AND x.customizationId=?", a.getFormSmsMailId(), a.getCustomizationId());
    			W5GlobalFuncResult rdb = null;
    			if(l2.size()==1){
    				W5FormSmsMail fsm = (W5FormSmsMail)l2.get(0);
    				Map m = new HashMap();
    				m.put("ptable_id", a.getTableId());m.put("ptable_pk", a.getTablePk());
    				switch(fsm.getSmsMailTip()){
    				case	0://sms
    //parameterMap.get("phone"),parameterMap.get("body")
    	//				m.putAll(dao.interprateSmsTemplate(fsm, scd, new HashMap(), a.getTableId(), a.getTablePk()));
    	//				rdb = executeFunc(scd, -631, m, (short)1);
    					break;
    				case	1://mail
    //W5Email email= new W5Email(parameterMap.get("pmail_to"),parameterMap.get("pmail_cc"),parameterMap.get("pmail_bcc"),parameterMap.get("pmail_subject"),parameterMap.get("pmail_body"), parameterMap.get("pmail_keep_body_original"), fileAttachments);
    	//				m.put("pmail_setting_id", FrameworkCache.getAppSettingStringValue(scd, "default_outbox_id"));
    	//				m.putAll(dao.interprateMailTemplate(fsm, scd, new HashMap(), a.getTableId(), a.getTablePk()));
    	//				rdb = executeFunc(scd, -650, m, (short)1);
    					break;
    				default:
    					break;
    				}
    				Log5Notification n = new Log5Notification(a);
    				n.set_tableRecordList(dao.findRecordParentRecords(scd,a.getTableId(),a.getTablePk(),0, true));
    				UserUtil.publishNotification(n, false);
    				a.setStatus(rdb== null || rdb.isSuccess() ? (short)0 : (short)2); // 0:done, 1: active, 2:error sending, 3:canceled
    			} else a.setStatus((short)2);
    		} catch(Exception e) {
    			a.setStatus((short)2); // 0:done, 1: active, 2:error sending, 3:canceled
    		} finally{
    			dao.updateObject(a);
    		} else break;
    */
  }

  public int getSubDomain2CustomizationId(String subDomain) {
    int res = 0;
    try {
      List l =
          dao.executeSQLQuery(
              "select c.customization_id from iwb.w5_customization c where c.sub_domain=?",
              subDomain);
      if (!GenericUtil.isEmpty(l))
        for (Object o : l) {
          res = GenericUtil.uInt(o);
          break;
        }
    } catch (Exception e) {
      if (FrameworkSetting.debug) e.printStackTrace();
    }
    return res;
  }

  public HashMap<String, Object> getUser(int customizationId, int userId) {
    HashMap<String, Object> user = new HashMap<String, Object>();
    List<Object[]> userObject =
        dao.executeSQLQuery(
            "select x.dsc,x.user_id,x.gsm,x.email from iwb.w5_user x where x.customization_id=? and x.user_id = ?",
            customizationId,
            userId);
    if (userObject != null && userObject.size() > 0) {
      user.put("dsc", userObject.get(0)[0]);
      user.put("id", userObject.get(0)[1]);
      user.put("gsm", userObject.get(0)[2]);
      user.put("email", userObject.get(0)[3]);
    }
    return user;
  }

  /*
  public String fileToBase64Code(Map<String, Object> scd, int fileAttachmentId){
  	FileInputStream stream = null;
  	String code="";
  	try{
  		W5FileAttachment fa = loadFile(scd, fileAttachmentId);
      	if(fa==null){
      		return "";
      	}
      	String customizationId=String.valueOf( (scd.get("customizationId")==null) ? 0 : scd.get("customizationId"));
      	String file_path=FrameworkCache.getAppSettingStringValue(scd, "file_local_path");
      	File file = new File(file_path + "/" + customizationId + "/attachment/"+ fa.getSystemFileName());
  		byte[] fileData = new byte[(int) file.length()];
  		stream = new FileInputStream(file);
  		stream.read(fileData);
  		code = Base64.encodeBase64String(fileData);
  	} catch (Exception e) {
  		if(FrameworkSetting.debug)e.printStackTrace();
  	} finally {
          if (stream!=null)
  			try {stream.close(); }catch (IOException e) {}
  	}
  	return code;
  }
  */
  public String getCustomizationLogoFilePath(Map<String, Object> scd) {
    // firma logosu
    String fileLocalPath = FrameworkCache.getAppSettingStringValue(scd, "file_local_path");
    String logoFilePath = fileLocalPath + "/" + scd.get("customizationId") + "/jasper/logo.jpg";

    return logoFilePath;
  }

  public Map<String, String> sendMailForgotPassword(
      Map<String, Object> scd, Map<String, String> requestParams) {
    return null;
    /*	Map<String, String> res = new HashMap<String, String>();
    res.put("success", "0");
    res.put("msg", "");
    try{
    	String email = requestParams.get("email");
    	String pcustomization_id = requestParams.get("pcustomization_id");
    	if (!GenericUtil.isEmpty(email)){
    		List<Object[]> userList = dao.executeSQLQuery("select u.user_id, u.user_name, u.customization_id from iwb.w5_user u where u.email=? and (? is null or u.customization_id=?)", email, pcustomization_id, pcustomization_id);
    		if (userList!=null && !userList.isEmpty()){
    			requestParams.put("pcustomization_id", userList.get(0)[2].toString());
    			requestParams.put("puser_id", userList.get(0)[0].toString());
    			requestParams.put("table_pk", userList.get(0)[0].toString());
    		}else{
    			res.put("msg", "mail_not_found_in_system");
    			return res;
    		}
    	}
    	try{
    		W5GlobalFuncResult result = executeFunc(scd, 2, requestParams, (short)4); // user forgot pass
    		if(result.isSuccess() && !GenericUtil.isEmpty(result.getResultMap()) && (GenericUtil.uInt(result.getResultMap().get("pout_user_id"))!=0)){
    			res.put("success", "1");
    			//tanımlanmış mail varsa mail-sms gönderiliyor
    			try{
    				W5FormSmsMail fsm = (W5FormSmsMail)dao.getCustomizedObject("from W5FormSmsMail t where t.activeFlag=1 and t.formId=? AND t.customizationId=?", 576, (Integer)scd.get("customizationId"), null);
    				if (fsm!=null){
    					requestParams.put("table_id","336");
    					sendFormSmsMail(scd, fsm.getFormSmsMailId(), requestParams);
    					res.put("msg", "email_success");
    				}else{
    					res.put("msg", "mail_sms_setting_not_found");
    				}
    			}catch(Exception e){
    				if(FrameworkSetting.debug)e.printStackTrace();
    				res.put("msg", FrameworkSetting.debug? e.getMessage() : "mail_sending_error");
    				return res;
    			}
    		}else{
    			res.put("msg", "forgot_pass_no_such_user");
    		}
    	}catch(Exception e){
    		if(FrameworkSetting.debug)e.printStackTrace();
    		res.put("msg", FrameworkSetting.debug? e.getMessage() : "error");
    		return res;
    	}
    }catch(Exception e){
    	if(FrameworkSetting.debug)e.printStackTrace();
    }
    return res;*/
  }

  public void sendSms(
      int customizationId,
      int userId,
      String phoneNumber,
      String message,
      int tableId,
      int tablePk) {
    Map<String, String> smsMap = new HashMap<String, String>();
    smsMap.put("customizationId", customizationId + "");
    smsMap.put("userId", userId + "");
    smsMap.put("tableId", tableId + "");
    smsMap.put("tablePk", tablePk + "");
    smsMap.put("phoneNumber", phoneNumber);
    smsMap.put("message", message);

    //	messageSender.sendMessage("SEND_SMS","BMPADAPTER", smsMap);

  }

  public W5FormCellHelper reloadFormCell(
      Map<String, Object> scd, int fcId, String webPageId, String tabId) {
    String projectId = FrameworkCache.getProjectId(scd, null);
    int customizationId = (Integer) scd.get("customizationId");
    //		W5Customization cus = FrameworkCache.wCustomizationMap.get(customizationId);
    int userId = (Integer) scd.get("userId");
    W5FormCell c =
        (W5FormCell)
            dao.getCustomizedObject(
                "from W5FormCell fc where fc.formCellId=? AND fc.projectUuid=?",
                fcId,
                projectId,
                null);
    if (c == null) return null;
    W5FormCellHelper rc = new W5FormCellHelper(c);
    String includedValues = c.getLookupIncludedValues();
    Map<String, String> requestParams = null;
    switch (c.getControlTip()) {
      case 58: // superboxselect
      case 8: // lovcombo static
      case 6: // eger static combobox ise listeyi load et
        W5LookUp lookUp = FrameworkCache.getLookUp(scd, c.getLookupQueryId());
        rc.setLocaleMsgFlag((short) 1);
        requestParams =
            UserUtil.getTableGridFormCellReqParams(
                projectId,
                -c.getLookupQueryId(),
                userId,
                (String) scd.get("sessionId"),
                webPageId,
                tabId,
                -fcId);
        List<W5LookUpDetay> oldList =
            (List<W5LookUpDetay>)
                dao.find(
                    "from W5LookUpDetay t where t.projectUuid=? AND t.lookUpId=? order by t.tabOrder",
                    projectId,
                    c.getLookupQueryId());

        List<W5LookUpDetay> newList = null;
        if (includedValues != null && includedValues.length() > 0) {
          //				List<W5LookUpDetay> oldList = lookUp.get_detayList();
          boolean notInFlag = false;
          if (includedValues.charAt(0) == '!') {
            notInFlag = true;
            includedValues = includedValues.substring(1);
          }
          String[] ar1 = includedValues.split(",");
          newList = new ArrayList<W5LookUpDetay>(oldList.size());
          for (W5LookUpDetay p : oldList)
            if ((rc.getValue() != null && p.getVal().equals(rc.getValue()))
                || p.getActiveFlag() != 0) {
              boolean in = false;
              for (int it4 = 0; it4 < ar1.length; it4++)
                if (ar1[it4].equals(p.getVal())) {
                  in = true;
                  break;
                }
              if (in ^ notInFlag) newList.add(p);
            }
        } else if (requestParams != null && requestParams.get("_lsc" + c.getFormCellId()) != null) {
          String[] lsc = requestParams.get("_lsc" + c.getFormCellId()).split(",");
          newList = new ArrayList<W5LookUpDetay>();
          for (String q : lsc) {
            newList.add(lookUp.get_detayMap().get(q));
          }
        } else {
          newList = new ArrayList<W5LookUpDetay>(oldList.size());
          for (W5LookUpDetay p : oldList)
            if ((rc.getValue() != null && p.getVal().equals(rc.getValue()))
                || p.getActiveFlag() != 0) newList.add(p);
          //				newList = lookUp.get_detayList();
        }
        List<W5LookUpDetay> newList2 = new ArrayList<W5LookUpDetay>(newList.size());
        for (W5LookUpDetay ld : newList) {
          newList2.add(ld);
        }
        rc.setLookupListValues(newList2);
        break;
      case 7:
      case 15:
      case 59: // dynamic query, lovcombo, superbox
      case 23:
      case 24:
      case 55: // tree combo and treepanel
        Map paramMap = new HashMap();
        requestParams =
            UserUtil.getTableGridFormCellReqParams(
                (String) scd.get("projectId"),
                c.getLookupQueryId(),
                userId,
                (String) scd.get("sessionId"),
                webPageId,
                tabId,
                -fcId);
        String includedParams =
            GenericUtil.filterExt(c.getLookupIncludedParams(), scd, requestParams, null).toString();
        if (includedParams != null && includedParams.length() > 2) {
          String[] ar1 = includedParams.split("&");
          for (int it4 = 0; it4 < ar1.length; it4++) {
            String[] ar2 = ar1[it4].split("=");
            if (ar2.length == 2 && ar2[0] != null && ar2[1] != null) paramMap.put(ar2[0], ar2[1]);
          }
        }

        W5QueryResult lookupQueryResult = dao.getQueryResult(scd, c.getLookupQueryId());
        lookupQueryResult.setErrorMap(new HashMap());
        lookupQueryResult.setRequestParams(requestParams);
        lookupQueryResult.setOrderBy(lookupQueryResult.getQuery().getSqlOrderby());
        if (rc.getValue() != null
            && rc.getValue().length() > 0
            && GenericUtil.hasPartInside("7,10,61", Short.toString(c.getControlTip())))
          paramMap.put("pmust_load_id", rc.getValue());
        switch (lookupQueryResult.getQuery().getQueryTip()) {
          case 12:
            lookupQueryResult.prepareTreeQuery(paramMap);
            break; // lookup tree query
          default:
            lookupQueryResult.prepareQuery(paramMap);
        }
        rc.setLookupQueryResult(lookupQueryResult);

        if (lookupQueryResult.getErrorMap().isEmpty()) {
          dao.runQuery(lookupQueryResult);
          if (tabId != null && lookupQueryResult.getQuery().getMainTableId() != 0) {
            Set<Integer> keys =
                UserUtil.getTableGridFormCellCachedKeys(
                    (String) scd.get("projectId"),
                    lookupQueryResult.getQuery().getMainTableId(),
                    userId,
                    (String) scd.get("sessionId"),
                    requestParams.get(".w"),
                    tabId,
                    -c.getFormCellId(),
                    requestParams,
                    true);
            for (Object[] o : lookupQueryResult.getData()) keys.add(GenericUtil.uInt(o[1]));
          }
        }

        break;
    }
    return rc;
  }

  public int notifyChatMsgRead(Map<String, Object> scd, int userId, int chatId) {
    int cnt1 =
        dao.executeUpdateSQLQuery(
            "update iwb.w5_chat set DELIVER_STATUS_TIP=2, DELIVER_DTTM=iwb.fnc_sysdate(?) where RECEIVER_USER_ID=? AND SENDER_USER_ID=? AND DELIVER_STATUS_TIP in (0,1)",
            scd.get("customizationId"),
            scd.get("userId"),
            userId);
    return 0;
  }

  public W5FormResult postBulkConversionMulti(
      Map<String, Object> scd, int conversionCount, Map<String, String> parameterMap) {
    W5FormResult result = null;
    for (int i = 1; i <= conversionCount; i++) {
      int cnvId = GenericUtil.uInt(parameterMap, "_cnvId" + i);
      int dirtyCount = GenericUtil.uInt(parameterMap, "_cnt" + i);

      result = postBulkConversion(scd, cnvId, dirtyCount, parameterMap, i + ".");
    }
    return result;
  }

  public Map sendFormSmsMail(
      Map<String, Object> scd, int formSmsMailId, Map<String, String> requestParams) {
    String projectId = FrameworkCache.getProjectId(scd, null);
    W5FormSmsMail fsm =
        (W5FormSmsMail)
            dao.getCustomizedObject(
                "from W5FormSmsMail t where t.formSmsMailId=? AND t.projectUuid=?",
                formSmsMailId,
                projectId,
                "FormSmsMail");
    W5Form f =
        (W5Form)
            dao.getCustomizedObject(
                "from W5Form t where t.formId=? AND t.projectUuid=?",
                fsm.getFormId(),
                projectId,
                "Form");
    int tableId = f.getObjectId();
    W5Table t = FrameworkCache.getTable(scd, tableId);
    if (!FrameworkCache.roleAccessControl(scd, 0)) {
      throw new IWBException(
          "security",
          "Module",
          0,
          null,
          "No Authorization for SMS/Email. Please contact Administrator",
          null);
    }

    Map r = new HashMap();
    if (fsm.getSmsMailTip() == 0) { // sms
      r.put("success", false);
      r.put("error", "SMS Adapter Not Defined");
      return r;
    } else { // email
      W5Email email =
          dao.interprateMailTemplate(
              fsm, scd, requestParams, tableId, GenericUtil.uInt(requestParams.get("table_pk")));
      W5ObjectMailSetting oms =
          (W5ObjectMailSetting)
              dao.getCustomizedObject(
                  "from W5ObjectMailSetting w where w.mailSettingId=? AND w.customizationId=?",
                  (Integer) scd.get("mailSettingId"),
                  scd.get("customizationId"),
                  "MailSetting");
      //					(W5ObjectMailSetting)dao.getCustomizedObject("from W5ObjectMailSetting w where
      // w.mailSettingId=? AND w.customizationId in (0,?)", (Integer)scd.get("mailSettingId"),
      // (Integer)scd.get("customizationId"), "MailSetting");
      //			if(requestParams.get("pfile_attachment_ids")!=null)mq.put("pfile_attachment_ids",
      // requestParams.get("pfile_attachment_ids"));
      email.set_oms(oms);
      String error = MailUtil.sendMail(scd, email);
      if (GenericUtil.isEmpty(error)) {
        r.put("success", true);
      } else {
        r.put("success", false);
        r.put("error", error);
      }
    }
    return r;
  }

  public W5TutorialResult getTutorialResult(
      Map<String, Object> scd, int tutorialId, Map<String, String> requestParams) { // TODO

    W5Tutorial tutorial = (W5Tutorial) dao.getObject(W5Tutorial.class, tutorialId);
    if (!FrameworkCache.roleAccessControl(scd, 0))
      throw new IWBException(
          "security",
          "Module",
          tutorial.getModuleId(),
          null,
          LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_guvenlik_modul_kontrol"),
          null);

    W5TutorialResult tr = new W5TutorialResult();
    /*
    		tr.setTutorial(tutorial);
    		tr.setScd(scd);
    		tr.setRequestParams(requestParams);
    		List<Object> listOfDoneTutorials = dao.executeSQLQuery("select u.tutorial_id from iwb.w5_tutorial_user u where u.user_id=? AND u.customization_id=? AND u.FINISHED_FLAG=1", scd.get("userId"),scd.get("customizationId"));
    		tr.setDoneTutorials(new HashSet<Integer>());
    		if(listOfDoneTutorials!=null)for(Object o:listOfDoneTutorials){
    			tr.getDoneTutorials().add(GenericUtil.uInt(o));
    		}
    		List<Object> listOfTutorial = dao.executeSQLQuery("select u.FINISHED_FLAG from iwb.w5_tutorial_user u where u.user_id=? AND u.tutorial_id=? AND u.customization_id=?", scd.get("userId"),tutorialId, scd.get("customizationId"));
    		if(GenericUtil.isEmpty(listOfTutorial)){//0:daha kayit yok, 1. var ve bitmemmis, 2. var ve bitmis
    			tr.setTutorialUserStatus((short)0);
    		} else tr.setTutorialUserStatus((short)(1 + GenericUtil.uInt(listOfTutorial.get(0))));
    		if(!GenericUtil.isEmpty(tutorial.getRecommendedTutorialIds()))
    			tr.setRecommendedTutorialList(dao.find("from W5Tutorial t where t.tutorialId in ("+tutorial.getRecommendedTutorialIds()+")"));
    		tutorial.set_renderTemplate((W5Page)dao.getCustomizedObject("from W5Template t where t.templateId=? AND t.customizationId=?", tutorial.getRenderTemplateId(), 0, null));
    */
    return tr;
  }

  public int jasperFileAttachmentControl(
      int table_id, String table_pk, String file_name, Integer file_type_id) {
    int x =
        dao.executeUpdateSQLQuery(
            "update iwb.w5_file_attachment x  set x.active_flag=0  where x.table_id=? and x.table_pk=? and x.original_file_name=? and x.file_type_id=?",
            table_id,
            table_pk,
            file_name,
            file_type_id);

    return x;
  }

  public boolean changeActiveProject(Map<String, Object> scd, String projectUuid) {
    List<Object> params = new ArrayList();
    params.add(projectUuid);
    params.add(
        scd.get(scd.containsKey("ocustomizationId") ? "ocustomizationId" : "customizationId"));
    params.add(scd.get("userId"));
    List list =
        dao.executeSQLQuery2Map(
            "select x.customization_id,(select 1 from iwb.w5_query q where q.query_id=session_query_id AND x.project_uuid=q.project_uuid) rbac from iwb.w5_project x where x.project_uuid=? AND (x.customization_id=? OR exists(select 1 from iwb.w5_user_related_project ur where ur.user_id=? AND x.project_uuid=ur.related_project_uuid))",
            params);
    if (GenericUtil.isEmpty(list)) return false;
    Map p = (Map) list.get(0);
    int newCustomizationId = GenericUtil.uInt(p.get("customization_id"));

    if (newCustomizationId
        != (Integer) scd.get("customizationId")) { // TODO check for invited projects
      if (!scd.containsKey("ocustomizationId"))
        scd.put("ocustomizationId", (Integer) scd.get("customizationId"));
      scd.put("customizationId", newCustomizationId);
    }
    W5Project po = FrameworkCache.getProject(projectUuid);
    scd.put("projectId", projectUuid);
    scd.put("rbac", newCustomizationId > 0 ? GenericUtil.uInt(p.get("rbac")) : 1);
    scd.put("_renderer2", GenericUtil.getRenderer(po.getUiWebFrontendTip()));
    return true;
  }

  public int getGlobalNextval(
      String id, String projectUuid, int userId, int customizationId, String remoteAddr) {

    if (FrameworkSetting.log2tsdb) {
      LogUtil.logObject(
          new Log5GlobalNextval(userId, customizationId, id, remoteAddr, projectUuid));
    }

    /*		l = dao.executeSQLQuery("select 1 from iwb.w5_vcs_global_nextval u where u.seq_dsc=? AND u.active_flag=1", id);
    		if(false && GenericUtil.isEmpty(l))//TODO. hepsi tanimlaninca daha iyi olacak
    			throw new IWBException("framework","Wrong/Inactive Sequence Name for GlobalNextval", 0, id, id, null);
    */
    List l = dao.executeSQLQuery("select nextval('" + id + "')");

    return GenericUtil.uInt(l.get(0));
  }

  public boolean organizeTable(Map<String, Object> scd, String tableName) {
    boolean b = dao.organizeTable(scd, tableName);
    FrameworkCache.clearPreloadCache(scd);
    return b;
  }

  public void organizeQuery(Map<String, Object> scd, int queryId, short insertFlag) {
    dao.organizeQueryFields(scd, queryId, insertFlag);
    FrameworkCache.clearPreloadCache(scd);
  }

  public boolean organizeGlobalFunc(Map<String, Object> scd, String dbFuncName) {
    boolean b = dao.organizeGlobalFunc(scd, dbFuncName);
    FrameworkCache.clearPreloadCache(scd);
    return b;
  }

  public int buildForm(Map<String, Object> scd, String parameter) throws JSONException {
    int customizationId = (Integer) scd.get("customizationId");

    String projectUuid = (String) scd.get("projectId");
    W5Project po = FrameworkCache.getProject(projectUuid);
    int userId = (Integer) scd.get("userId");
    //		boolean vcs = FrameworkSetting.vcs && po.getVcsFlag()!=0;
    String createTableSql = "", tableName, fullTableName;
    JSONObject main;
    JSONArray detail;
    int parentTableId;
    boolean vcs = true;
    Locale en = new Locale("en");

    List p = new ArrayList();
    p.add(customizationId);

    JSONObject json = new JSONObject(parameter);
    String webPageId = json.has("_webPageId") ? json.getString("_webPageId") : null;
    int userTip = json.getInt("user_tip");
    main = json.getJSONObject("main");
    detail = json.getJSONArray("detail");

    StringBuilder s = new StringBuilder();
    String schema = po.getRdbmsSchema();
    if (GenericUtil.isEmpty(schema)) schema = "";
    else schema += ".";
    String formName = main.getString("form_name");
    tableName = GenericUtil.uStr2Alpha2(GenericUtil.uStr2English(formName), "x").toLowerCase(en);
    String gridName =
        GenericUtil.uStr2Alpha2(GenericUtil.uStr2English(main.getString("grid_name")), "x")
            .toLowerCase(en);
    //		gridName = main.getString("grid_name");
    String tablePrefix =
        FrameworkCache.getAppSettingStringValue(0, "form_builder_table_prefix", "x");
    if (!tablePrefix.endsWith("_")) tablePrefix += "_";
    String tableName2 = tablePrefix + tableName;
    fullTableName = schema + tableName2;
    parentTableId = main.has("parent_table_id") ? GenericUtil.uInt(main.get("parent_table_id")) : 0;
    s.append("create table ").append(tableName2).append(" (");
    s.append(tableName).append("_id integer not null");
    String relParentFieldName = null;
    if (parentTableId != 0) {
      W5Table pt = FrameworkCache.getTable(scd, parentTableId);
      if (pt != null) {
        relParentFieldName = pt.get_tableFieldList().get(0).getDsc();
        s.append(",\n ").append(relParentFieldName).append(" integer not null");
      } else parentTableId = 0;
    }
    for (int qi = 0; qi < detail.length(); qi++) {
      JSONObject d = detail.getJSONObject(qi);
      int controlTip = GenericUtil.uInt(d.get("real_control_tip"));
      if (controlTip == 102) continue;
      String fieldDsc = d.getString("real_dsc");
      if (GenericUtil.isEmpty(fieldDsc)) fieldDsc = d.getString("dsc");
      fieldDsc = fieldDsc.toLowerCase();
      s.append(",\n ").append(fieldDsc);
      int maxLen = GenericUtil.uInt(d.get("max_length"));
      switch (controlTip) {
        case 2:
          if (!fieldDsc.endsWith("_dt")) {
            s.append("_dt");
            fieldDsc = fieldDsc + "_dt";
          }
          s.append(" date");
          break; // date
        case 3:
          s.append(" numeric");
          if (maxLen > 0) {
            if (maxLen > 22) maxLen = 22;
            s.append("(").append(maxLen);
            int decimalPrecision =
                d.has("decimal_precision") ? GenericUtil.uInt(d.get("decimal_precision")) : 0;
            if (decimalPrecision > 18) decimalPrecision = 18;
            s.append(",").append(decimalPrecision > 0 ? decimalPrecision : 2);
            s.append(")");
          }
          break; // float
        case 4:
          s.append(maxLen < 5 ? " smallint" : " integer");
          break; // integer
        case 5:
          if (!fieldDsc.endsWith("_flag")) {
            s.append("_flag");
            fieldDsc = fieldDsc + "_flag";
          }
          s.append(" smallint default 0");
          break; // checkbox
        case 6:
        case 8:
        case 58:
          if (GenericUtil.uInt(d.get("look_up_id")) > 0) {
            int lookUpId = GenericUtil.uInt(d.get("look_up_id"));
            if (FrameworkCache.getLookUp(scd, lookUpId) == null)
              throw new IWBException(
                  "framework",
                  "Form+ Builder",
                  lookUpId,
                  null,
                  "Wrong Static LookupID: " + lookUpId,
                  null);
          } else {
            if (!d.has("list_of_values") || GenericUtil.isEmpty(d.get("list_of_values")))
              throw new IWBException(
                  "framework",
                  "Form+ Builder",
                  0,
                  null,
                  "LookupID OR Combo Values Not Defined",
                  null);
            String lov = d.getString("list_of_values");
            String[] vz = lov.split("\\r?\\n");

            int lookUpId =
                GenericUtil.getGlobalNextval(
                    "iwb.seq_look_up", projectUuid, userId, customizationId);
            dao.executeUpdateSQLQuery(
                "insert into iwb.w5_look_up "
                    + "(look_up_id, customization_id, dsc, version_no, insert_user_id, insert_dttm, version_user_id, version_dttm, project_uuid, oproject_uuid)"
                    + "values (?         , ?               , ?  , 1         , ?             , current_timestamp    , ?              , current_timestamp     , ?, ?)",
                lookUpId,
                scd.get("customizationId"),
                "lkp_" + fieldDsc,
                scd.get("userId"),
                scd.get("userId"),
                projectUuid,
                projectUuid);
            if (vcs) dao.saveObject(new W5VcsObject(scd, 13, lookUpId));
            int tabOrder = 1;
            for (String sx : vz)
              if (!GenericUtil.isEmpty(sx) && !GenericUtil.isEmpty(sx.trim())) {
                int lookUpIdDetail =
                    GenericUtil.getGlobalNextval(
                        "iwb.seq_look_up_detay", projectUuid, userId, customizationId);
                dao.executeUpdateSQLQuery(
                    "insert into iwb.w5_look_up_detay "
                        + "(look_up_detay_id, look_up_id, tab_order, val      , dsc, version_no, insert_user_id, insert_dttm, version_user_id, version_dttm, customization_id, project_uuid, oproject_uuid)"
                        + "values (?,        ?,                 ?        , ?        , ?  , 1         , ?             , current_timestamp    , ?              , current_timestamp     , ?, ?, ?)",
                    lookUpIdDetail,
                    lookUpId,
                    tabOrder,
                    "" + tabOrder,
                    sx.trim(),
                    scd.get("userId"),
                    scd.get("userId"),
                    scd.get("customizationId"),
                    projectUuid,
                    projectUuid);
                tabOrder++;
                if (vcs) dao.saveObject(new W5VcsObject(scd, 14, lookUpIdDetail));
              }
            d.put("look_up_id", "" + lookUpId);
          }
          s.append(
              controlTip == 6 || controlTip == 7 || controlTip == 10 || controlTip == 51
                  ? " integer"
                  : " character varying(256)");
          break;
        case 7:
        case 10:
        case 15:
        case 59:
          if (GenericUtil.uInt(d.get("look_up_id")) > 0) {
            int queryId = GenericUtil.uInt(d.get("look_up_id"));
            if (dao.executeSQLQuery(
                    "select 1 from iwb.w5_query x where x.query_id=? AND x.query_tip=3", queryId)
                == null)
              throw new IWBException(
                  "framework", "Form+ Builder", queryId, null, "Wrong QueryID: " + queryId, null);
          } else
            throw new IWBException(
                "framework", "Form+ Builder", 0, null, "QueryID not defined", null);
          s.append(
              controlTip == 6 || controlTip == 7 || controlTip == 10 || controlTip == 51
                  ? " integer"
                  : " character varying(256)");
          break;

        default:
          //				case	1:case	11:case	12:
          if (maxLen == 0 || maxLen > 3999) s.append(" text");
          else s.append(" character varying(").append(maxLen < 1024 ? 1024 : maxLen).append(")");
          break; // string, textarea, htmleditor
      }

      /*
      Object notNull = d.get("not_null_flag");
      if(false && notNull!=null){ //sonra degistirmek isteyebilir, o yzden koyma
      	if(notNull instanceof Boolean){
      		if((Boolean)notNull)s.append(" not null");
      	} else if(GenericUtil.uInt(notNull)!=0)s.append(" not null");
      }*/
      d.put("real_dsc", fieldDsc);
    }
    s.append(",\n version_no integer NOT NULL DEFAULT 1")
        .append(",\n insert_user_id integer NOT NULL DEFAULT 1")
        .append(
            ",\n  insert_dttm timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone")
        .append(
            ",\n version_user_id integer NOT NULL DEFAULT 1,\n version_dttm timestamp without time zone NOT NULL DEFAULT ('now'::text)::timestamp without time zone");

    s.append(",\n CONSTRAINT pk_")
        .append(tableName)
        .append(" PRIMARY KEY (")
        .append(tableName)
        .append("_id)");
    s.append(")");

    createTableSql = s.toString();
    dao.executeUpdateSQLQuery("set search_path=" + po.getRdbmsSchema());

    Map msg = null, nt = null;
    if (webPageId != null) {
      msg = new HashMap();
      msg.put("success", true);
      nt = new HashMap();
      msg.put("notification", nt);
    }

    try {
      dao.executeUpdateSQLQuery(createTableSql);
      String createSeqSql = "create sequence seq_" + tablePrefix + tableName;
      dao.executeUpdateSQLQuery(createSeqSql);

      if (vcs) {
        W5VcsCommit commit = new W5VcsCommit();
        commit.setCommitTip((short) 2);
        commit.setExtraSql(createTableSql + ";\n\n" + createSeqSql + ";");
        commit.setProjectUuid(projectUuid);
        commit.setComment("iWB. AutoCreate Scripts for Table: " + fullTableName);
        commit.setCommitUserId((Integer) scd.get("userId"));
        Object oi = dao.executeSQLQuery("select nextval('iwb.seq_vcs_commit')").get(0);
        commit.setVcsCommitId(-GenericUtil.uInt(oi));
        dao.saveObject(commit);
      }

      s.setLength(0);

      if (webPageId != null) {
        nt.put("_tmpStr", "Table Created on RDBMS");
        UserUtil.broadCast(
            projectUuid,
            (Integer) scd.get("userId"),
            (String) scd.get("sessionId"),
            webPageId,
            msg);
      }

    } catch (Exception e2) {
      throw new IWBException(
          "framework", "Create Table&Seq", 0, createTableSql, e2.getMessage(), e2);
    }

    boolean b = dao.organizeTable(scd, fullTableName);
    if (!b) throw new IWBException("framework", "Define Table", 0, parameter, "Define Table", null);
    if (webPageId != null) {
      nt.put("_tmpStr", "Table Imported to iCodeBetter");
      UserUtil.broadCast(
          projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId, msg);
    }

    int tableId =
        GenericUtil.uInt(
            dao.executeSQLQuery(
                    "select t.table_id from iwb.w5_table t where t.customization_id=? AND t.dsc=? AND t.project_uuid=?",
                    customizationId,
                    tableName2,
                    projectUuid)
                .get(0));

    try {
      main.put("table_id", tableId);
    } catch (JSONException e) {
    }
    //		dao.executeUpdateSQLQuery("supdate iwb.w5_table t set  where t.customization_id=? AND
    // t.table_id=?", customizationId, tableId);
    main.put("form_name", tableName);

    W5FormResult fr = postFormAsJson(scd, 181, 2, main, 182, detail);
    if (!fr.getErrorMap().isEmpty())
      throw new IWBException(
          "framework",
          "Save FormBuilder Data",
          0,
          parameter,
          GenericUtil.fromMapToJsonString(fr.getErrorMap()),
          null);

    int xformBuilderId = GenericUtil.uInt(fr.getOutputFields().get("xform_builder_id").toString());
    int parentTemplateId =
        parentTableId == 0 || !main.has("template_id") ? 0 : main.getInt("template_id");
    int parentTemplateObjectId =
        parentTableId == 0 || !main.has("parent_object_id") ? 0 : main.getInt("parent_object_id");
    int formId =
        GenericUtil.getGlobalNextval(
            "iwb.seq_form",
            projectUuid,
            userId,
            customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
    // nextval('seq_form')").get(0));
    //				  XFORM_ID := nextval('seq_form');
    dao.executeUpdateSQLQuery(
        "INSERT INTO iwb.w5_form("
            + "form_id, customization_id, object_tip, object_id, dsc, locale_msg_key, "
            + "default_width, default_height, tab_order, render_tip, code, label_width,"
            + "label_align_tip,  cont_entry_flag, "
            + "version_no, insert_user_id, insert_dttm, version_user_id,"
            + "version_dttm, render_template_id, project_uuid, oproject_uuid)"
            + "\nselect ?, XFORM_BUILDER.customization_id, 2, XFORM_BUILDER.table_id, 'frm_'||XFORM_BUILDER.form_name, ? ,"
            + "400, 300, 1, 1, null, XFORM_BUILDER.label_width,"
            + "XFORM_BUILDER.label_align, 0,"
            + "1, ?, current_timestamp, ?,"
            + "current_timestamp, 0, XFORM_BUILDER.project_uuid,XFORM_BUILDER.project_uuid from iwb.w5_xform_builder XFORM_BUILDER where XFORM_BUILDER.xform_builder_id=? AND XFORM_BUILDER.customization_id=?",
        formId,
        formName,
        userId,
        userId,
        xformBuilderId,
        customizationId);
    if (vcs) dao.saveObject(new W5VcsObject(scd, 40, formId));

    List lp = new ArrayList();
    lp.add(xformBuilderId);
    List<Map> lm =
        dao.executeSQLQuery2Map(
            "select x.* from iwb.w5_xform_builder_detail x where x.xform_builder_id=? order by 1",
            lp);
    int tabOrder = 1;
    for (Map m : lm) {
      int formCellId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_form_cell",
              projectUuid,
              userId,
              customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
      // nextval('seq_form_cell')").get(0));
      dao.executeUpdateSQLQuery(
          "INSERT INTO iwb.w5_form_cell("
              + "form_cell_id, customization_id, form_id, dsc, locale_msg_key,"
              + "control_tip, vtype, source_tip, not_null_flag, tab_order, control_width,"
              + "max_length, nrd_tip, lookup_query_id, lookup_included_params,"
              + "lookup_included_values, default_value, initial_value, initial_source_tip,"
              + "extra_definition, object_id, object_detail_id, version_no, insert_user_id,"
              + "insert_dttm, version_user_id, version_dttm, form_module_id, out_flag,"
              + "dialog_grid_id, x_order, parent_form_cell_id, active_flag, lookup_edit_form_id,"
              + "project_uuid, oproject_uuid)"
              + "\nselect  ?, x.customization_id, ?, coalesce(x.real_dsc, x.dsc), x.label,"
              + "case when x.real_control_tip!=0 then x.real_control_tip else  x.control_tip end, null, 1, x.not_null_flag, x.tab_order, x.width,"
              + " 0, 0, x.look_up_id, null, "
              + " null, null, x.initial_value, 0,"
              + " null, ?, (select f.table_field_id from iwb.w5_table_field f where f.customization_id=x.customization_id AND f.table_id=? AND f.dsc=coalesce(x.real_dsc, x.dsc)), 1, ?,"
              + " current_timestamp, ?, current_timestamp, 0, 0,"
              + " 0, 1, 0, 1, 0,"
              + " x.project_uuid,x.project_uuid from iwb.w5_xform_builder_detail x where x.xform_builder_detail_id=? AND x.customization_id=?",
          formCellId,
          formId,
          tableId,
          tableId,
          userId,
          userId,
          GenericUtil.uInt(m.get("xform_builder_detail_id")),
          customizationId);
      if (vcs) dao.saveObject(new W5VcsObject(scd, 41, formCellId));
    }
    if (relParentFieldName != null) {
      int formCellId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_form_cell",
              projectUuid,
              userId,
              customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
      // nextval('seq_form_cell')").get(0));
      dao.executeUpdateSQLQuery(
          "INSERT INTO iwb.w5_form_cell("
              + "form_cell_id, customization_id, form_id, dsc, locale_msg_key,"
              + "control_tip, vtype, source_tip, not_null_flag, tab_order, control_width,"
              + "max_length, nrd_tip, lookup_query_id, lookup_included_params,"
              + "lookup_included_values, default_value, initial_value, initial_source_tip,"
              + "extra_definition, object_id, object_detail_id, version_no, insert_user_id,"
              + "insert_dttm, version_user_id, version_dttm, form_module_id, out_flag,"
              + "dialog_grid_id, x_order, parent_form_cell_id, active_flag, lookup_edit_form_id,"
              + "project_uuid, oproject_uuid)"
              + "\nvalues(?, ?, ?, ?, ? ,"
              + "0, null, 1, 1, 10*?, 100,"
              + " 0, 0, 0, null, "
              + " null, null, null, 0,"
              + " null, ?, (select f.table_field_id from iwb.w5_table_field f where f.customization_id=? AND f.table_id=? AND f.dsc=?), 1, ?,"
              + " current_timestamp, ?, current_timestamp, 0, 0,"
              + " 0, 1, 0, 1, 0,"
              + " ?, ? )",
          formCellId,
          customizationId,
          formId,
          relParentFieldName,
          relParentFieldName,
          tabOrder++,
          tableId,
          customizationId,
          tableId,
          relParentFieldName,
          userId,
          userId,
          projectUuid,
          projectUuid);
      if (vcs) dao.saveObject(new W5VcsObject(scd, 41, formCellId));
    }
    if (webPageId != null) {
      nt.put("_tmpStr", "Form Created for CRUD Operations");
      UserUtil.broadCast(
          projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId, msg);
    }

    //				XQUERY_ID := nextval('seq_query');
    int queryId =
        GenericUtil.getGlobalNextval(
            "iwb.seq_query",
            projectUuid,
            userId,
            customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
    // nextval('seq_query')").get(0));
    dao.executeUpdateSQLQuery(
        "INSERT INTO iwb.w5_query("
            + "query_id, dsc, main_table_id, sql_select, sql_from, sql_where,"
            + "sql_groupby, sql_orderby, query_tip, log_level_tip, version_no,"
            + "insert_user_id, insert_dttm, version_user_id, version_dttm,"
            + "show_parent_record_flag, sql_post_select,"
            + "data_fill_direction_tip, opt_query_field_ids, opt_tip, project_uuid,oproject_uuid, customization_id)"
            + "select ?, 'qry_'||XFORM_BUILDER.form_name||'1', XFORM_BUILDER.table_id, 'x.*', (select t.dsc from iwb.w5_table t where t.table_id=XFORM_BUILDER.table_id AND t.customization_id=?)||' x', null,"
            + "null, 1, 1, 1, 1,"
            + "?, current_timestamp, ?, current_timestamp, "
            + "0,  null,"
            + "0, null, 0, XFORM_BUILDER.project_uuid,XFORM_BUILDER.project_uuid, XFORM_BUILDER.customization_id from iwb.w5_xform_builder XFORM_BUILDER where XFORM_BUILDER.xform_builder_id=?",
        queryId,
        customizationId,
        userId,
        userId,
        xformBuilderId);
    if (vcs) dao.saveObject(new W5VcsObject(scd, 8, queryId));

    dao.organizeQueryFields(scd, queryId, (short) 1);

    if (webPageId != null) {
      nt.put("_tmpStr", "Query Created");
      UserUtil.broadCast(
          projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId, msg);
    }

    dao.executeUpdateSQLQuery("set search_path=iwb");

    List llo = dao.find("from W5QueryFieldCreation t where queryFieldId<?", 500);
    //		dao.getHibernateTemplate().flush();
    //				XGRID_ID := nextval('seq_grid');
    //		List lw = dao.executeSQLQuery("select min(qf.query_field_id) from iwb.w5_query_field qf
    // where qf.query_id=? AND qf.customization_id=? AND qf.project_uuid=?", queryId,
    // customizationId, projectUuid);
    int gridId =
        GenericUtil.getGlobalNextval(
            "iwb.seq_grid",
            projectUuid,
            userId,
            customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
    // nextval('seq_grid')").get(0));
    dao.executeUpdateSQLQuery(
        "INSERT INTO iwb.w5_grid("
            + "grid_id, customization_id, dsc, query_id, locale_msg_key, grid_tip,"
            + "default_page_record_number, selection_mode_tip, pk_query_field_id,"
            + "auto_expand_field_id, "
            + "default_width, default_height, version_no, insert_user_id, insert_dttm,"
            + "version_user_id, version_dttm, default_sql_order_by, default_crud_form_id,"
            + "column_render_tip, grouping_field_id, "
            + "insert_edit_mode_flag, move_up_down_flag,"
            + "tree_master_field_id, summary_tip, row_color_fx_tip, row_color_fx_query_field_id,"
            + "row_color_fx_render_tip, row_color_fx_render_field_ids, code, project_uuid, oproject_uuid)"
            + "select ?, XFORM_BUILDER.customization_id, ? , ?, XFORM_BUILDER.grid_name, 0,"
            + "?, 1, (select min(qf.query_field_id) from iwb.w5_query_field qf where qf.query_id=?),"
            + "0, "
            + "400, 300, 1, ?, current_timestamp,"
            + "?, current_timestamp, null, ?,"
            + "0, 0, "
            + "0, 0,"
            + "0, 0, 0, 0,"
            + "0, null, null, XFORM_BUILDER.project_uuid,XFORM_BUILDER.project_uuid "
            + " from iwb.w5_xform_builder XFORM_BUILDER where XFORM_BUILDER.xform_builder_id=? AND XFORM_BUILDER.customization_id=?",
        gridId,
        "grd_" + gridName,
        queryId,
        parentTableId == 0 ? 20 : 0,
        queryId,
        userId,
        userId,
        formId,
        xformBuilderId,
        customizationId);
    if (vcs) dao.saveObject(new W5VcsObject(scd, 5, gridId));

    tabOrder = 1;
    for (Map m : lm) {
      int gridColumnId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_grid_column",
              projectUuid,
              userId,
              customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
      // nextval('seq_grid_column')").get(0));
      dao.executeUpdateSQLQuery(
          "INSERT INTO iwb.w5_grid_column("
              + "grid_column_id, query_field_id, grid_id, customization_id, locale_msg_key, tab_order,"
              + "visible_flag, sortable_flag, width, renderer, align_tip, version_no,"
              + "insert_user_id, insert_dttm, version_user_id, version_dttm, extra_definition,"
              + "grid_module_id, form_cell_id, filter_flag, project_uuid, oproject_uuid)"
              + "\nselect ?, (select f.query_field_id from iwb.w5_query_field f where f.dsc=coalesce(x.real_dsc, x.dsc) AND f.query_id=?), ?, x.customization_id, coalesce(x.grd_label, x.label), 10*?,"
              + "x.grd_visible_flag, 1, x.grd_width, null, x.grd_align_tip, 1,"
              + "?, current_timestamp, ?, current_timestamp, null,"
              + "0, case when y.grid_edit=1 AND x.grd_editable_flag=1 then (select c.form_cell_id from iwb.w5_form_cell c where c.dsc=coalesce(x.real_dsc, x.dsc) AND c.form_id=? AND c.customization_id=x.customization_id) else 0 end, 0, x.project_uuid, x.project_uuid"
              + " from iwb.w5_xform_builder_detail x,iwb.w5_xform_builder y "
              + "where x.xform_builder_id = y.xform_builder_id AND x.customization_id=y.customization_id "
              + "AND x.xform_builder_detail_id=?  AND x.customization_id=?",
          gridColumnId,
          queryId,
          gridId,
          tabOrder++,
          userId,
          userId,
          formId,
          GenericUtil.uInt(m.get("xform_builder_detail_id")),
          customizationId);
      if (vcs) dao.saveObject(new W5VcsObject(scd, 4, gridColumnId));
    }

    if (webPageId != null) {
      nt.put("_tmpStr", "Grid Created");
      UserUtil.broadCast(
          projectUuid, (Integer) scd.get("userId"), (String) scd.get("sessionId"), webPageId, msg);
    }

    //					if(pmaster_flag=1 AND XFORM_BUILDER.grid_search=1 AND (select count(1) from
    // iwb.w5_xform_builder_detail x where x.xform_builder_id=pxform_builder_id  AND
    // x.customization_id=XUSER_ROLE.customization_id AND x.project_uuid=pproject_uuid AND
    // x.grd_search_flag=1)>0) then
    if (parentTableId == 0) {
      tabOrder = 1;
      for (Map m : lm)
        if (GenericUtil.uInt(m.get("grd_search_flag")) != 0) {
          int queryParamId =
              GenericUtil.getGlobalNextval(
                  "iwb.seq_query_param",
                  projectUuid,
                  userId,
                  customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
          // nextval('seq_query_param')").get(0));
          dao.executeUpdateSQLQuery(
              "INSERT INTO iwb.w5_query_param("
                  + "query_param_id, query_id, dsc, param_tip, expression_dsc, operator_tip,"
                  + "not_null_flag, tab_order, source_tip, default_value, min_length,"
                  + "max_length, version_no, insert_user_id, insert_dttm, version_user_id,"
                  + "version_dttm, related_table_field_id, min_value, max_value, project_uuid, oproject_uuid, customization_id)"
                  + "SELECT  ?, ?, 'x'||coalesce(x.real_dsc, x.dsc), case when x.control_tip in (1,2,3,4) then x.control_tip else 1 end, 'x.'||coalesce(x.real_dsc, x.dsc), 0,"
                  + "0, 10*?, 1, null, 0,"
                  + "0, 1, ?, current_timestamp, ?,"
                  + "current_timestamp, (select f.table_field_id from iwb.w5_table_field f where f.dsc=coalesce(x.real_dsc, x.dsc) AND f.table_id=? AND f.customization_id=x.customization_id), null, null, x.project_uuid, x.project_uuid, x.customization_id "
                  + "from iwb.w5_xform_builder_detail x where "
                  + "x.xform_builder_detail_id=? AND x.customization_id=?",
              queryParamId,
              queryId,
              tabOrder++,
              userId,
              userId,
              tableId,
              GenericUtil.uInt(m.get("xform_builder_detail_id")),
              customizationId);
          if (vcs) dao.saveObject(new W5VcsObject(scd, 10, queryParamId));
        }

      //  XSFORM_ID := nextval('seq_form');
      int sformId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_form",
              projectUuid,
              userId,
              customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
      // nextval('seq_form')").get(0));
      dao.executeUpdateSQLQuery(
          "INSERT INTO iwb.w5_form("
              + "form_id, customization_id, object_tip, object_id, dsc, locale_msg_key, "
              + "default_width, default_height, tab_order, render_tip, code, label_width,"
              + "label_align_tip, cont_entry_flag, "
              + "version_no, insert_user_id, insert_dttm, version_user_id,"
              + "version_dttm, render_template_id, project_uuid, oproject_uuid)"
              + "\nselect ?, XFORM_BUILDER.customization_id, 1, ?, 'sfrm_'||XFORM_BUILDER.form_name, 'search_criteria',"
              + "400, 300, 1, 1, null, XFORM_BUILDER.label_width,"
              + "XFORM_BUILDER.label_align, 0,"
              + "1, ?, current_timestamp, ?, current_timestamp, 0, XFORM_BUILDER.project_uuid, XFORM_BUILDER.project_uuid from iwb.w5_xform_builder XFORM_BUILDER where XFORM_BUILDER.xform_builder_id=? AND XFORM_BUILDER.customization_id=?",
          sformId,
          gridId,
          userId,
          userId,
          xformBuilderId,
          customizationId);
      if (vcs) dao.saveObject(new W5VcsObject(scd, 40, sformId));

      for (Map m : lm)
        if (GenericUtil.uInt(m.get("grd_search_flag")) != 0) {
          int formCellId =
              GenericUtil.getGlobalNextval(
                  "iwb.seq_form_cell",
                  projectUuid,
                  userId,
                  customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
          // nextval('seq_form_cell')").get(0));
          int controlTip = GenericUtil.uInt(m.get("real_control_tip"));
          if (controlTip == 0) controlTip = GenericUtil.uInt(m.get("control_tip"));
          int lookUpId = GenericUtil.uInt(m.get("look_up_id"));
          switch (controlTip) {
            case 5:
              controlTip = 6;
              lookUpId = 143;
              break;
            case 11:
            case 12:
              controlTip = 1;
              break;
          }
          dao.executeUpdateSQLQuery(
              "INSERT INTO iwb.w5_form_cell("
                  + "form_cell_id, customization_id, form_id, dsc, locale_msg_key,"
                  + "control_tip, vtype, source_tip, not_null_flag, tab_order, control_width,"
                  + "max_length, nrd_tip, lookup_query_id, lookup_included_params,"
                  + "lookup_included_values, default_value, initial_value, initial_source_tip,"
                  + "extra_definition, object_id, object_detail_id, version_no, insert_user_id,"
                  + "insert_dttm, version_user_id, version_dttm, form_module_id, out_flag,"
                  + "dialog_grid_id, x_order, parent_form_cell_id, active_flag, lookup_edit_form_id, project_uuid, oproject_uuid)"
                  + "\nselect  ?, x.customization_id, ?, 'x'||coalesce(x.real_dsc, x.dsc), x.label,"
                  + "?, null, 1, 0, 10*?, 200,"
                  + "0, 0, ?, null,"
                  + "null, null, null, 0,"
                  + "null, ?, (select f.query_param_id from iwb.w5_query_param f where f.query_id=? AND f.dsc='x'||coalesce(x.real_dsc, x.dsc)), 1, ?,"
                  + "current_timestamp, ?, current_timestamp, 0, 0,"
                  + "0, 1, 0, 1, 0, x.project_uuid, x.project_uuid "
                  + "from iwb.w5_xform_builder_detail x where x.grd_search_flag=1 AND x.xform_builder_detail_id=? AND x.customization_id=?",
              formCellId,
              sformId,
              controlTip,
              tabOrder++,
              lookUpId,
              gridId,
              queryId,
              userId,
              userId,
              GenericUtil.uInt(m.get("xform_builder_detail_id")),
              customizationId);
          if (vcs) dao.saveObject(new W5VcsObject(scd, 41, formCellId));
        }

      if (webPageId != null) {
        nt.put("_tmpStr", "Form Created for Grid Search");
        UserUtil.broadCast(
            projectUuid,
            (Integer) scd.get("userId"),
            (String) scd.get("sessionId"),
            webPageId,
            msg);
      }
    }

    //				   if(pmaster_flag=1)then
    int templateId = 0, menuId = 0;
    if (parentTableId == 0) {
      //  XTEMPLATE_ID := nextval('seq_template');
      templateId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_template",
              projectUuid,
              userId,
              customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
      // nextval('seq_template')").get(0));
      dao.executeUpdateSQLQuery(
          "INSERT INTO iwb.w5_template("
              + "template_id, customization_id, template_tip, dsc, object_id,"
              + "object_tip, code, version_no, insert_user_id, insert_dttm, version_user_id,"
              + "version_dttm, locale_msg_flag, project_uuid, oproject_uuid)"
              + "VALUES (?, ?, 2, 'tpl_'||?||'1', 0, "
              + "0, null, 1, ?, current_timestamp, ?,"
              + "current_timestamp, 1, ?, ?)",
          templateId,
          customizationId,
          tableName,
          userId,
          userId,
          projectUuid,
          projectUuid);
      if (vcs) dao.saveObject(new W5VcsObject(scd, 63, templateId));

      int templateObjectId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_template_object",
              projectUuid,
              userId,
              customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
      // nextval('seq_template_object')").get(0));
      dao.executeUpdateSQLQuery(
          "INSERT INTO iwb.w5_template_object("
              + "template_object_id, template_id, customization_id, object_id, tab_order, object_tip,"
              + "version_no, insert_user_id, insert_dttm, version_user_id, version_dttm,"
              + "access_view_users, access_view_roles, access_view_tip, post_js_code,"
              + "parent_object_id, src_query_field_id, dst_query_param_id,"
              + "dst_static_query_param_val, dst_static_query_param_id, active_flag, project_uuid, oproject_uuid)"
              + "VALUES (?, ?, ?, ?, 1, 1,"
              + "1, ?, current_timestamp, ?, current_timestamp,"
              + "null, null, 0, null,"
              + "0, null, null,null, null, 1, ?, ?)",
          templateObjectId,
          templateId,
          customizationId,
          gridId,
          userId,
          userId,
          projectUuid,
          projectUuid);
      if (vcs) dao.saveObject(new W5VcsObject(scd, 64, templateObjectId));

      menuId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_menu",
              projectUuid,
              userId,
              customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
      // nextval('seq_template_object')").get(0));
      dao.executeUpdateSQLQuery(
          "INSERT INTO iwb.w5_menu("
              + "menu_id, parent_menu_id, user_tip, node_tip, locale_msg_key,"
              + "tab_order, img_icon, url, version_no, insert_user_id, insert_dttm,"
              + "version_user_id, version_dttm, customization_id, access_view_tip, project_uuid, oproject_uuid)"
              + "VALUES (?, 0, ?, 4, ?, "
              + "coalesce((select max(q.tab_order) from iwb.w5_menu q where q.customization_id=? AND q.user_tip=?),0)+10, null, 'showPage?_tid='||?::text, 1, ?, current_timestamp, "
              + "?, current_timestamp, ?, 0, ?, ?)",
          menuId,
          userTip,
          gridName,
          customizationId,
          userTip,
          templateId,
          userId,
          userId,
          customizationId,
          projectUuid,
          projectUuid);
      if (vcs) dao.saveObject(new W5VcsObject(scd, 65, menuId));
    } else {
      Object[] loo =
          (Object[])
              dao.executeSQLQuery(
                      "select f.dsc, f.table_field_id "
                          + "from iwb.w5_table_field f where f.customization_id=? AND f.table_id=? AND f.tab_order=2",
                      customizationId,
                      tableId)
                  .get(0);
      dao.executeUpdateSQLQuery(
          "UPDATE iwb.w5_table_field f SET can_update_flag=0 WHERE f.customization_id=? AND f.table_id=? AND f.tab_order=2",
          customizationId,
          tableId);
      dao.executeUpdateSQLQuery(
          "UPDATE iwb.w5_grid f SET code=f.dsc||'._postInsert=function(sel,url,a){var m=getMasterGridSel(a,sel);if(m)return url+\"&"
              + loo[0]
              + "=\" +(m."
              + loo[0]
              + " || m.get(\""
              + loo[0]
              + "\"));};' WHERE f.customization_id=? AND f.grid_id=?",
          customizationId,
          gridId);
      int tableChildId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_table_relation", projectUuid, userId, customizationId);
      dao.executeUpdateSQLQuery(
          "insert INTO iwb.w5_table_child "
              + "(table_child_id, locale_msg_key, relation_tip, table_id, table_field_id, related_table_id, related_table_field_id, related_static_table_field_id, related_static_table_field_val, version_no, insert_user_id, insert_dttm, version_user_id, version_dttm, copy_strategy_tip, on_readonly_related_action, on_invisible_related_action, on_delete_action, tab_order, on_delete_action_value, child_view_tip, child_view_object_id, revision_flag, project_uuid, customization_id) "
              + "values(?, ?, 2, ?     , ?             , ?               , ?                     , 0                            , 0                             , 1         , ?             , current_timestamp  , ?      , current_timestamp , 0          , 0                         , 0                          , 0               , 10       , null                  , 0             , 0                   , 0            , ?           , ?)",
          tableChildId,
          "rel_xxx2" + tableName,
          parentTableId,
          FrameworkCache.getTable(scd, parentTableId).get_tableFieldList().get(0).getTableFieldId(),
          tableId,
          loo[1],
          userId,
          userId,
          projectUuid,
          customizationId);
      if (vcs) dao.saveObject(new W5VcsObject(scd, 657, tableChildId));

      int queryParamId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_query_param",
              projectUuid,
              userId,
              customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
      // nextval('seq_query_param')").get(0));
      dao.executeUpdateSQLQuery(
          "INSERT INTO iwb.w5_query_param("
              + "query_param_id, query_id, dsc, param_tip, expression_dsc, operator_tip,"
              + "not_null_flag, tab_order, source_tip, default_value, min_length,"
              + "max_length, version_no, insert_user_id, insert_dttm, version_user_id,"
              + "version_dttm, related_table_field_id, min_value, max_value, project_uuid, oproject_uuid, customization_id)"
              + "values ("
              + "?, ?, 'x'||?, 4, 'x.'||?, 0, "
              + "1, 1, 1, null, 0,"
              + "0, 1, ?, current_timestamp, ?,"
              + "current_timestamp, ?, null, null, ?, ?, ?)",
          queryParamId,
          queryId,
          loo[0],
          loo[0],
          userId,
          userId,
          GenericUtil.uInt(loo[1]),
          projectUuid,
          projectUuid,
          customizationId);
      if (vcs) dao.saveObject(new W5VcsObject(scd, 10, queryParamId));

      int parentQueryId =
          GenericUtil.uInt(
              dao.executeSQLQuery(
                      "select g.query_id from iwb.w5_template_object q, iwb.w5_grid g where q.template_object_id=? AND q.customization_id=? "
                          + " AND g.customization_id=q.customization_id AND q.object_id=g.grid_id",
                      parentTemplateObjectId,
                      customizationId)
                  .get(0));

      int templateObjectId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_template_object",
              projectUuid,
              userId,
              customizationId); // 1000000+GenericUtil.uInt(dao.executeSQLQuery("select
      // nextval('seq_template_object')").get(0));
      dao.executeUpdateSQLQuery(
          "INSERT INTO iwb.w5_template_object("
              + "template_object_id, template_id, customization_id, object_id, tab_order, object_tip,"
              + "version_no, insert_user_id, insert_dttm, version_user_id, version_dttm,"
              + "access_view_users, access_view_roles, access_view_tip, post_js_code,"
              + "parent_object_id, src_query_field_id, dst_query_param_id,"
              + "dst_static_query_param_val, dst_static_query_param_id, active_flag, project_uuid, oproject_uuid)"
              + "VALUES ("
              + "?, ?, ?, ?, (select coalesce(max(q.tab_order),0)+1 from iwb.w5_template_object q where q.template_id=? AND q.customization_id=? ), 1,"
              + "1, ?, current_timestamp, ?, current_timestamp,"
              + "null, null, 0, null,"
              + "?, (select min(r.query_field_id) from iwb.w5_query_field r where r.query_id=? AND  "
              + "r.tab_order=(select min(f.tab_order) from iwb.w5_query_field f where f.query_id=?)), ?,"
              + "null, null, 1, ?, ?)",
          templateObjectId,
          parentTemplateId,
          customizationId,
          gridId,
          parentTemplateId,
          customizationId,
          userId,
          userId,
          parentTemplateObjectId,
          parentQueryId,
          parentQueryId,
          queryParamId,
          projectUuid,
          projectUuid);
      if (vcs) dao.saveObject(new W5VcsObject(scd, 64, templateObjectId));

      if (webPageId != null) {
        nt.put("_tmpStr", "Page & Menu Created");
        UserUtil.broadCast(
            projectUuid,
            (Integer) scd.get("userId"),
            (String) scd.get("sessionId"),
            webPageId,
            msg);
      }
    }
    dao.executeUpdateSQLQuery(
        "update iwb.w5_table t "
            + "set default_insert_form_id=?, default_update_form_id=?, default_view_grid_id=?, summary_record_sql='x.'||(select tf.dsc from iwb.w5_table_field tf where tf.tab_order=2 AND tf.table_id=t.table_id AND t.customization_id=tf.customization_id)||'::text' "
            + "where t.table_id=? AND t.customization_id=? ",
        formId,
        formId,
        gridId,
        tableId,
        customizationId);

    dao.executeUpdateSQLQuery(
        "update iwb.w5_table_field tf "
            + "set default_control_tip=(select fc.control_tip from iwb.w5_form_cell fc, iwb.w5_form f where fc.customization_id=f.customization_id AND f.form_id=fc.form_id AND f.object_tip=2 AND f.object_id=tf.table_id AND fc.dsc=tf.dsc), default_lookup_table_id=(select fc.lookup_query_id from iwb.w5_form_cell fc, iwb.w5_form f where fc.customization_id=f.customization_id AND f.form_id=fc.form_id AND f.object_tip=2 AND f.object_id=tf.table_id AND fc.dsc=tf.dsc) "
            + "where tf.table_id=? AND tf.customization_id=? AND tf.tab_order>1 AND tf.dsc not in ('version_no','insert_user_id','insert_dttm','version_user_id','version_dttm') ",
        tableId,
        customizationId);

    dao.executeUpdateSQLQuery(
        "update iwb.w5_query_field tf "
            + "set post_process_tip=(select case when f.default_control_tip in (6) then 10 when f.default_control_tip in (8) then 11 else 0 end from w5_table_field f where tf.customization_id=f.customization_id AND f.table_id=? AND f.dsc=tf.dsc)"
            + ", lookup_query_id=(select case when f.default_control_tip in (6,8) then f.default_lookup_table_id else 0 end from w5_table_field f where tf.customization_id=f.customization_id AND f.table_id=? AND f.dsc=tf.dsc)  "
            + ", main_table_field_id=(select f.table_field_id from w5_table_field f where tf.customization_id=f.customization_id AND f.table_id=? AND f.dsc=tf.dsc)  "
            + "where tf.query_id=? AND tf.customization_id=?",
        tableId,
        tableId,
        tableId,
        queryId,
        customizationId);

    if (parentTableId == 0) { // main Template
      return templateId;
    } else {
      return gridId;
    }
  }

  public W5FormResult postFormAsJson(
      Map<String, Object> scd,
      int mainFormId,
      int action,
      JSONObject mainFormData,
      int detailFormId,
      JSONArray detailFormData) {
    Map<String, String> requestParams = new HashMap<String, String>();
    Iterator<String> ik = mainFormData.keys();
    while (ik.hasNext())
      try {
        String k = ik.next();
        if (!GenericUtil.isEmpty(mainFormData.get(k)))
          requestParams.put(k, mainFormData.get(k).toString());
      } catch (Exception e) {
        throw new IWBException(
            "framework", "Json2FormPost(FormId)", mainFormId, null, e.getMessage(), null);
      }
    requestParams.put("_fid1", "" + detailFormId);
    requestParams.put("_cnt1", "" + detailFormData.length());
    for (int i = 0; i < detailFormData.length(); i++)
      try {
        JSONObject d = (JSONObject) detailFormData.get(i);
        ik = d.keys();
        requestParams.put("a1." + (i + 1), "2");
        while (ik.hasNext())
          try {
            String k = ik.next();
            if (!GenericUtil.isEmpty(d.get(k)))
              requestParams.put(k + "1." + (i + 1), d.get(k).toString());
          } catch (Exception e) {
            throw new IWBException(
                "framework", "Json2FormPost(FormId)", mainFormId, null, e.getMessage(), e);
          }
      } catch (Exception e) {
        throw new IWBException(
            "framework", "Json2FormPostDetail(FormId)", detailFormId, null, e.getMessage(), e);
      }
    return postForm4Table(scd, mainFormId, action, requestParams, "");
  }

  public M5ListResult getMListResult(
      Map<String, Object> scd, int listId, Map<String, String> parameterMap) {
    M5ListResult result = dao.getMListResult(scd, listId, parameterMap, false);
    return result;
  }

  
  public Map getUserNotReadChatMap(Map<String, Object> scd) {
    String s =
        "select k.sender_user_id user_id , count(1) cnt from iwb.w5_chat k where k.receiver_user_id=${scd.userId}::integer AND k.deliver_status_tip in (0,1) "// AND k.customization_id=${scd.customizationId}::integer "
            + "AND k.sender_user_id in (select u.user_id from iwb.w5_user u where ((u.customization_id=${scd.customizationId}::integer AND (u.global_flag=1 OR u.project_uuid='${scd.projectId}') AND u.user_status=1)) OR exists(select 1 from iwb.w5_user_related_project rp where rp.user_id=u.user_id AND rp.related_project_uuid='${scd.projectId}'))"
            + " group by k.sender_user_id";
    Object[] oz = DBUtil.filterExt4SQL(s, scd, null, null);
    List<Object[]> l = dao.executeSQLQuery2(oz[0].toString(), (List) oz[1]);
    Map r = new HashMap();
    if (l != null) for (Object[] o : l) r.put(o[0], o[1]);
    return r;
  }

  public Map executeQuery4Stat(
      Map<String, Object> scd, int gridId, Map<String, String> requestParams) {
    checkTenant(scd);
    return dao.executeQuery4Stat(scd, gridId, requestParams);
  }

  public Map executeQuery4StatTree(
      Map<String, Object> scd, int gridId, Map<String, String> requestParams) {
    checkTenant(scd);
    return dao.executeQuery4StatTree(scd, gridId, requestParams);
  }

  public List<W5BIGraphDashboard> getGraphDashboards(Map<String, Object> scd) {
    String dashIds =
        (String)
            dao.executeSQLQuery(
                    "select r.mobile_portlet_ids from iwb.w5_user_role r where r.customization_id=? AND r.user_role_id=?",
                    (Integer) scd.get("customizationId"),
                    (Integer) scd.get("userRoleId"))
                .get(0);
    if (GenericUtil.isEmpty(dashIds)) return null;
    String[] ds = dashIds.split(",");
    List<W5BIGraphDashboard> l = new ArrayList();
    for (String s : ds) {
      int id = GenericUtil.uInt(s);
      if (id == 0) continue;
      if (id < 0) id = -id;
      W5BIGraphDashboard gd =
          (W5BIGraphDashboard)
              dao.getCustomizedObject(
                  "from W5BIGraphDashboard t where t.graphDashboardId=? AND t.projectUuid=?",
                  id,
                  (String) scd.get("projectId"),
                  "GraphDashBoard");
      if (gd != null) l.add(gd);
    }
    return l;
  }

  /*
  public String projectAccessUrl(String instanceUuid, String remoteAddr) {
  	List l = dao.executeSQLQuery("select p.val from iwb.w5_app_setting p where p.customization_id=0 AND p.dsc=?", "iwb_active_projects4"+remoteAddr);
  	if(GenericUtil.isEmpty(l))return null;
  	return l.get(0).toString();
  } */

  public Object executeQuery4Debug(
      Map<String, Object> scd, int queryId, Map<String, String> requestParams) {
    W5QueryResult queryResult =
        queryId == -1 ? new W5QueryResult(-1) : dao.getQueryResult(scd, queryId);

    queryResult.setErrorMap(new HashMap());
    queryResult.setScd(scd);
    queryResult.setRequestParams(requestParams);
    if (queryId == -1 || queryResult.getQuery().getQuerySourceTip() != 0) {
      String orderBy = requestParams.get("sort");
      if (GenericUtil.isEmpty(orderBy)) orderBy = requestParams.get("_sql_orderby");
      else if (requestParams.containsKey("dir")) orderBy += " " + requestParams.get("dir");
      queryResult.prepareQuery4Debug(
          requestParams.get("_sql_select"),
          requestParams.get("_sql_from"),
          requestParams.get("_sql_where"),
          requestParams.get("_sql_groupby"),
          orderBy);

      if (queryResult.getErrorMap().isEmpty()) {
        queryResult.setFetchRowCount(20);
        queryResult.setStartRowNumber(0);
        String sqlFrom2 = requestParams.get("_sql_from").toLowerCase();
        W5Table t = null;
        if (!sqlFrom2.contains("select")
            && !sqlFrom2.contains(",")
            && !sqlFrom2.contains("left")
            && !sqlFrom2.contains("(")) {
          String[] ss = sqlFrom2.split(" ");
          if (ss.length < 3) {
            List l =
                dao.find(
                    "select t.tableId from W5Table t where t.projectUuid=? AND t.dsc=?",
                    scd.get("projectId"),
                    ss[0]);
            if (!l.isEmpty()) t = FrameworkCache.getTable(scd, (Integer) l.get(0));
          }
        }
        if (FrameworkSetting.cloud
            && (Integer) scd.get("customizationId") > 0
            && DBUtil.checkTenantSQLSecurity(queryResult.getExecutedSql())) {
          throw new IWBException(
              "security",
              "SQL",
              0,
              null,
              "Forbidden Command. Please contact iCodeBetter team ;)",
              null);
        }

        return dao.executeSQLQuery2Map4Debug(
            scd,
            t,
            queryResult.getExecutedSql(),
            queryResult.getSqlParams(),
            GenericUtil.uIntNvl(requestParams, "limit", 50),
            GenericUtil.uIntNvl(requestParams, "start", 0));
        //        	if(queryResult.getData()==null)queryResult.setData(new ArrayList());
      }
    } else { // rhino
      return dao.executeQueryAsRhino4Debug(queryResult, requestParams.get("_sql_from"));
    }

    return queryResult;
  }

  public List executeQuery4DataList(
      Map<String, Object> scd, int tableId, Map<String, String> requestParams) {
    return dao.executeQuery4DataList(scd, tableId, requestParams);
  }

  public List executeQuery4Pivot(
      Map<String, Object> scd, int tableId, Map<String, String> requestParams) {
    return dao.executeQuery4Pivot(scd, tableId, requestParams);
  }

  public W5GlobalFuncResult executeGlobalFunc4Debug(
      Map<String, Object> scd, int dbFuncId, Map<String, String> parameterMap) {
    return dao.executeGlobalFunc4Debug(scd, dbFuncId, parameterMap);
  }

  public Map<String, Object> getWsServerMethodObjects(W5WsServer wss) {
    Map<String, Object> wsmoMap = new HashMap();
    Map scd = new HashMap();
    scd.put("projectId", wss.getProjectUuid());
    for (W5WsServerMethod wsm : wss.get_methods())
      try {
        switch (wsm.getObjectTip()) {
          case 0:
          case 1:
          case 2:
          case 3: // form
            wsmoMap.put(
                wsm.getDsc(),
                dao.getFormResult(
                    scd,
                    wsm.getObjectId(),
                    wsm.getObjectTip() == 0 ? 1 : wsm.getObjectTip(),
                    new HashMap()));
            break;
          case 4:
            wsmoMap.put(wsm.getDsc(), dao.getGlobalFuncResult(scd, wsm.getObjectId()));
            break;
          case 19:
            wsmoMap.put(wsm.getDsc(), dao.getQueryResult(scd, wsm.getObjectId()));
            break;
          case 31:
          case 32:
          case 33:
            wsmoMap.put(wsm.getDsc(), FrameworkCache.getWorkflow(scd, wsm.getObjectId()));
            break;
          default:
            wsmoMap.put(wsm.getDsc(), "Wrong ObjectTip");
        }
      } catch (Exception e) {
        wsmoMap.put(wsm.getDsc(), "Invalid Object");
      }
    return wsmoMap;
  }

  public Map REST(Map<String, Object> scd, String name, Map requestParams) throws IOException {
    String[] u = name.replace('.', ',').split(",");
    if (u.length < 2)
      throw new IWBException(
          "ws", "Wrong ServiceName", 0, null, "Call should be [serviceName].[methodName]", null);
    W5Ws ws = FrameworkCache.getWsClient(scd, u[0]);
    if (ws == null)
      throw new IWBException("ws", "Wrong ServiceName", 0, null, "Could find [" + u[0] + "]", null);
    W5WsMethod wsm = null;
    for (W5WsMethod twm : ws.get_methods())
      if (twm.getDsc().equals(u[1])) {
        wsm = twm;
        break;
      }
    if (wsm == null)
      throw new IWBException("ws", "Wrong MethodName", 0, null, "Could find [" + u[1] + "]", null);

    if (!GenericUtil.accessControl(
            scd, ws.getAccessExecuteTip(), ws.getAccessExecuteRoles(), ws.getAccessExecuteUsers())
        || !GenericUtil.accessControl(
            scd,
            wsm.getAccessExecuteTip(),
            wsm.getAccessExecuteRoles(),
            wsm.getAccessExecuteUsers())) {
      throw new IWBException(
          "security", "WS Method Call", wsm.getWsMethodId(), null, "Access Forbidden", null);
    }
    try {
      String projectId = (String) scd.get("projectId");
      if (wsm.get_params() == null) {
        wsm.set_params(
            dao.find(
                "from W5WsMethodParam t where t.wsMethodId=? AND t.projectUuid=? order by t.tabOrder",
                wsm.getWsMethodId(),
                projectId));
        wsm.set_paramMap(new HashMap());
        for (W5WsMethodParam wsmp : wsm.get_params())
          wsm.get_paramMap().put(wsmp.getWsMethodParamId(), wsmp);
      }
      String tokenKey = null;
      Map m = new HashMap();
      Map errorMap = new HashMap();
      if (ws.getWssTip() == 2) { // token ise ve token yok ise
        if (ws.getWssLoginMethodId() == null
            || ws.getWssLoginMethodParamId() == null
            || ws.getWssLoginTimeout() == null)
          throw new IWBException(
              "security",
              "WS Method Call",
              wsm.getWsMethodId(),
              null,
              "WSS: Token Properties Not Defined",
              null);
        if (ws.getWssLoginMethodId() != wsm.getWsMethodId()
            && ws.getWssLoginMethodParamId() != null
            && (ws.getWssLogoutMethodId() == null
                || ws.getWssLogoutMethodId() == wsm.getWsMethodId())) {
          tokenKey = (String) ws.loadValue("tokenKey");
          Long tokenTimeout = (Long) ws.loadValue("tokenKey.timeOut");
          W5WsMethod loginMethod = FrameworkCache.getWsMethod(scd, ws.getWssLoginMethodId());
          if (loginMethod.get_params() == null) {
            loginMethod.set_params(
                dao.find(
                    "from W5WsMethodParam t where t.wsMethodId=? AND t.projectUuid=? order by t.tabOrder",
                    loginMethod.getWsMethodId(),
                    projectId));
            loginMethod.set_paramMap(new HashMap());
            for (W5WsMethodParam wsmp : loginMethod.get_params())
              loginMethod.get_paramMap().put(wsmp.getWsMethodParamId(), wsmp);
          }
          W5WsMethodParam tokenParam =
              loginMethod.get_paramMap().get(ws.getWssLoginMethodParamId());
          if (tokenKey == null
              || tokenTimeout == null
              || tokenTimeout <= System.currentTimeMillis()) { // yeni bir token alinacak
            if (tokenParam != null) {
              Map tokenResult = REST(scd, ws.getDsc() + "." + loginMethod.getDsc(), new HashMap());
              Object o = tokenResult.get(tokenParam.getDsc());
              if (o == null)
                throw new IWBException(
                    "security",
                    "WS Method Call",
                    wsm.getWsMethodId(),
                    null,
                    "WSS: Auto-Login Failed",
                    null);
              tokenKey = o.toString();
              ws.storeValue("tokenKey", tokenKey);
              ws.storeValue(
                  "tokenKey.timeOut",
                  System.currentTimeMillis() + ws.getWssLoginTimeout().longValue());
            }
          }
          requestParams.put(tokenParam.getDsc(), tokenKey);
        }
      }

      Map<String, Object> result = new HashMap();
      switch (ws.getWsTip()) {
        case 1: // soap
          break;
        case 2: // rest
          String url = ws.getWsUrl();
          if (!url.endsWith("/")) url += "/";
          url += GenericUtil.isEmpty(wsm.getRealDsc()) ? wsm.getDsc() : wsm.getRealDsc();
          String params = null;
          Map<String, String> reqPropMap = new HashMap();
          reqPropMap.put("Content-Language", "tr-TR");
          if (wsm.getHeaderAcceptTip() != null) {
              reqPropMap.put(
                  "Accept",
                  new String[] {"application/json", "application/xml"}[wsm.getHeaderAcceptTip() - 1]);
            }
          if(ws.getWssTip()==1 && !GenericUtil.isEmpty(ws.getWssCredentials())) { //credentials
        	  String[] lines = ws.getWssCredentials().split("\n");
        	  for(int qi=0;qi<lines.length;qi++) {
        		  int ii = lines[qi].indexOf(':');
        		  if(ii>0) {
        			  reqPropMap.put(lines[qi].substring(0, ii).trim(), lines[qi].substring(ii+1).trim());        					  
        		  }
        	  }        	  
          }
          if (!GenericUtil.isEmpty(wsm.get_params()) && wsm.getParamSendTip() > 0) {
            if (wsm.getParamSendTip() != 4) {
              for (W5WsMethodParam p : wsm.get_params())
                if (p.getOutFlag() == 0 && p.getParentWsMethodParamId() == 0) {// && p.getParentWsMethodParamId() == 0
                	if(p.getParamTip()==9 || p.getParamTip()==8) { //object/json
                		Map subMap = new HashMap();
                		m.put(p.getDsc(), subMap);
                		if(p.getSourceTip()==0) { //constant
                			
                		}
                		requestParams.get(p.getDsc());
                		
                	} else if(p.getParamTip()==10) {//array
                		List subList= new ArrayList();
                		m.put(p.getDsc(), subList);
                		
                	} else {
		                  Object o =
		                      GenericUtil.prepareParam(
		                          (W5Param) p,
		                          scd,
		                          requestParams,
		                          p.getSourceTip(),
		                          null,
		                          p.getNotNullFlag(),
		                          null,
		                          null,
		                          errorMap,
		                          dao);
		                  if (o != null && o.toString().length() > 0) {
		                	  if(p.getCredentialsFlag()!=0)
		                		  reqPropMap.put(p.getDsc(), o.toString());
		                	  else {
		                		  m.put(p.getDsc(), o);
		                	  }
		                  }
                	}
                }
              if (!errorMap.isEmpty()) {
                throw new IWBException(
                    "validation",
                    "WS Method Call",
                    wsm.getWsId(),
                    null,
                    "Wrong Parameters: + " + GenericUtil.fromMapToJsonString2(errorMap),
                    null);
              }
              switch (wsm.getParamSendTip()) {
                case 1: // form
                case 3: // form as post_url
                  params = GenericUtil.fromMapToURI(m);
                  if (wsm.getParamSendTip() == 3) {
                    if (!GenericUtil.isEmpty(params)) {
                      if (url.indexOf('?') == -1) url += "?";
                      url += params;
                    }
                    params = null;
                  }
                  reqPropMap.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                  break;
                case 2: // json
                  params = GenericUtil.fromMapToJsonString2Recursive(m);
                  reqPropMap.put("Content-Type", "application/json;charset=UTF-8");
                  break;
                case	6:// yaml
                	params = GenericUtil.fromMapToYamlString2Recursive(m, 0);
                    reqPropMap.put("Content-Type", "application/yaml;charset=UTF-8");
                  break;
              }
            } else { //free
              String postUrl = (String) requestParams.get("post_url");
              if (!GenericUtil.isEmpty(postUrl)) url += postUrl;
            }
          }
          if(wsm.getParamSendTip()==5) { //raw
          	params = (String)m.get("body");
          	m.clear();
          }

       
          Log5WsMethodAction log = new Log5WsMethodAction(scd, wsm.getWsMethodId(), url, params);
          String x =
              HttpUtil.send(
                  url,
                  params,
                  new String[] {"GET", "POST", "PUT", "PATCH", "DELETE"}[wsm.getCallMethodTip()],
                  reqPropMap);
          if (!GenericUtil.isEmpty(x))
            try {
              log.setResponse(x);
              String xx = x.trim();
              if (xx.length() > 0)
                switch (xx.charAt(0)) {
                  case '{':
                    JSONObject jo = new JSONObject(x);
                    result.putAll(GenericUtil.fromJSONObjectToMap(jo));
                    break;
                  case '[':
                    JSONArray ja = new JSONArray(x);
                    result.put("data", GenericUtil.fromJSONArrayToList(ja));
                    break;
                  default:
                    result.put("data", x);
                }
              if (GenericUtil.uInt(requestParams.get("_iwb_cfg")) != 0) {
                result.put("_iwb_cfg_rest_method", wsm);
              }
            } catch (JSONException e) {
              throw new RuntimeException(e);
            }
          if (FrameworkSetting.log2tsdb) {
            log.calcProcessTime();
            LogUtil.logObject(log);
          }
          break;
      }

      return result;
    } catch (Exception e) {
      throw new IWBException(
          "framework",
          "RESTService_Method",
          wsm.getWsMethodId(),
          null,
          "[1376," + wsm.getWsMethodId() + "] " + name,
          e);
    }
  }

  private W5TsMeasurement getTsMeasurement(Map<String, Object> scd, int measurementId) { // TODO
    W5TsMeasurement m = null;
    /*		if(FrameworkSetting.preloadWEngine==0 && (m=FrameworkCache.getTsMeasurement(scd,measurementId))==null){
    	m = (W5TsMeasurement)dao.getCustomizedObject("from W5TsMeasurement t where t.measurementId=? AND t.customizationId=?", measurementId, (Integer)scd.get("customizationId"), "TSMeasurement");
    	m.set_measurementFields(dao.find("from W5TsMeasurementField t where t.portletId=? AND t.customizationId=? order by t.tabOrder, t.portletObjectId", measurementId, (Integer)scd.get("customizationId")));
    	if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wTsMeasurements.get((Integer)scd.get("customizationId")).put(measurementId, m);
    } */
    return m;
  }

  public String getTsDashResult(
      Map<String, Object> scd, Map<String, String> requestParams, int porletId) { // TODO
    W5TsPortlet p = null;
    /*		if(FrameworkSetting.preloadWEngine==0 || (p=FrameworkCache.getTsPortlet(scd,porletId))==null){
    			p = (W5TsPortlet)dao.getCustomizedObject("from W5TsPortlet t where t.portletId=? AND t.customizationId=?", porletId, (Integer)scd.get("customizationId"), "TSPorlet");
    			p.set_portletObjects(dao.find("from W5TsPortletObject t where t.portletId=? AND t.customizationId=? order by t.tabOrder, t.portletObjectId", porletId, (Integer)scd.get("customizationId")));
    			if(FrameworkSetting.preloadWEngine!=0)FrameworkCache.wTsPortlets.get((Integer)scd.get("customizationId")).put(porletId, p);
    		}
    		StringBuilder s = new StringBuilder();
    		for(W5TsPortletObject po:p.get_portletObjects())switch(po.getObjectTip()){
    		case	2709://measurement
    			po.set_sourceObject(getTsMeasurement(scd, po.getObjectId()));
    //			Object o = influxDao.runQuery(po.getExtraCode());
    	//		resultMap.put(po.getPortletObjectId(), o);
    			break;
    		case	8://query
    			po.set_sourceObject(dao.getQueryResult(scd, po.getObjectId()));
    			break;
    		}
    		switch(p.getCodeTip()){
    		case	1://A*B/C
    			s.append("\nfunction(){\n").append(p.getExtraCode())
    			.append("\nvar result=[];for(var qi=0;qi<)");
    			break;
    		case	2:// function(A,B,C){}
    			s.append(p.getExtraCode());
    			break;
    		case	3:
    			s.append(p.getExtraCode());
    			break;
    		}
    		return s.toString(); */
    return null;
  }

  public boolean copyTable2Tsdb(Map<String, Object> scd, int tableId, int measurementId) {
    W5TsMeasurement tsm = getTsMeasurement(scd, measurementId);
    if (!GenericUtil.accessControl(
        scd, tsm.getAccessViewTip(), tsm.getAccessViewRoles(), tsm.getAccessViewUsers())) {
      throw new IWBException(
          "security",
          "W5TsMeasurement",
          measurementId,
          null,
          LocaleMsgCache.get2(0, (String) scd.get("locale"), "fw_access_control_ts_measurement"),
          null);
    }
    if (tsm.getMeasurementObjectId() != 2 || tsm.getMeasurementObjectId() == 0)
      throw new IWBException(
          "framework", "W5TsMeasurement", measurementId, null, "Source Not RDB Table or X", null);
    W5Table t = FrameworkCache.getTable(scd, tableId);
    W5TableField timeField = t.get_tableFieldMap().get(tsm.getMeasurementObjectId());
    StringBuilder s = new StringBuilder();
    s.append("SELECT x.").append(timeField.getDsc()).append(" xtime");
    if (!GenericUtil.isEmpty(tsm.getTagCode())) s.append(",").append(tsm.getTagCode());
    if (!GenericUtil.isEmpty(tsm.getExtraCode())) s.append(",").append(tsm.getExtraCode());
    s.append(" from ").append(t.getDsc()).append(" x");
    if (t.get_tableParamList().size() > 1
        && t.get_tableParamList().get(1).getDsc().equals("customizationId"))
      s.append(" WHERE x.customization_id=${scd.customizationId}");
    Object[] oz = DBUtil.filterExt4SQL(s.toString(), scd, new HashMap(), null);
    List<Map> lm = dao.executeSQLQuery2Map(oz[0].toString(), (List) oz[1]);

    return false;
  }

  public Map generateScdFromAuth(int socialCon, String token) {
    List<Object[]> list =
        dao.executeSQLQuery(
            "select u.user_id, u.customization_id from iwb.w5_user u"
                + " where u.lkp_auth_external_source=? AND u.user_status=1 AND u.auth_external_id=?",
            socialCon,
            token);
    if (!GenericUtil.isEmpty(list)) {
      Object[] oz = list.get(0);
      Map<String, Object> scd = userSession4Auth(GenericUtil.uInt(oz[0]), GenericUtil.uInt(oz[1]));
      if (scd != null) {
        dao.executeUpdateSQLQuery(
            "update iwb.w5_user set last_succesful_login_dttm=current_timestamp, succesful_login_count=succesful_login_count+1 where user_id=?",
            scd.get("userId"));
      }
      return scd;
    } else {
      return null;
    }
  }

  public void saveCredentials(
      int cusId,
      int userId,
      String picUrl,
      String fullName,
      int socialNet,
      String email,
      String nickName,
      List<Map> projects,
      List<Map> userTips) {
    if (dao.find("select 1 from W5Customization t where t.customizationId=?", cusId).isEmpty())
      dao.executeUpdateSQLQuery(
          "insert into iwb.w5_customization(customization_id, dsc, sub_domain) values (?,?,?)",
          cusId,
          socialNet,
          nickName.replace('.', '_').replace('-', '_'));
    FrameworkCache.wCustomizationMap.put(
        cusId,
        (W5Customization)
            dao.find("from W5Customization t where t.customizationId=?", cusId).get(0));

    FrameworkSetting.projectSystemStatus.put(projects.get(0).get("project_uuid").toString(), 0);
    if (GenericUtil.isEmpty(
        dao.executeSQLQuery("select 1 from iwb.w5_user u where u.user_id=?", userId))) {
      dao.executeUpdateSQLQuery(
          "insert into iwb.w5_user(user_id, customization_id, user_name, email, pass_word, user_status, dsc,login_rule_id, lkp_auth_external_source, auth_external_id, project_uuid) values (?,?,?,?,iwb.md5hash(?),?,?,?,?,?,?)",
          userId,
          cusId,
          nickName,
          email,
          nickName + 1,
          1,
          nickName,
          1,
          socialNet,
          email,
          projects.get(0).get("project_uuid"));
      int userRoleId =
          GenericUtil.getGlobalNextval(
              "iwb.seq_user_role", (String) projects.get(0).get("project_uuid"), userId, cusId);
      dao.executeUpdateSQLQuery(
          "insert into iwb.w5_user_role(user_role_id, user_id, role_id, customization_id,unit_id, project_uuid) values(?, ?, 0, ?,?, ?)",
          userRoleId,
          userId,
          cusId,
          0,
          projects.get(0).get("project_uuid"));
    }

    for (Map p : projects) {
      String projectId = (String) p.get("project_uuid");
      String oprojectId = (String) p.get("oproject_uuid");
      if (oprojectId == null) oprojectId = projectId;
      String vcsUrl = (String) p.get("vcs_url");

      if (GenericUtil.isEmpty(
          dao.executeSQLQuery(
              "select 1 from iwb.w5_project p where p.project_uuid=?", projectId))) {
        String schema =
            "c" + GenericUtil.lPad(cusId + "", 5, '0') + "_" + projectId.replace('-', '_');
        dao.executeUpdateSQLQuery(
            "insert into iwb.w5_project(project_uuid, customization_id, dsc, access_users,  rdbms_schema, vcs_url, vcs_user_name, vcs_password, oproject_uuid)"
                + " values (?,?,?, ?, ?,?,?,?, ?)",
            projectId,
            cusId,
            p.get("dsc"),
            "" + userId,
            schema,
            vcsUrl,
            nickName,
            "1",
            oprojectId);
        dao.executeUpdateSQLQuery("create schema " + schema + " AUTHORIZATION iwb");
      }

      dao.addProject2Cache(projectId);
      FrameworkSetting.projectSystemStatus.put(projectId, 0);
    }

    for (Map t : userTips) {
      String projectId = (String) t.get("project_uuid");
      String oprojectId = (String) t.get("oproject_uuid");
      if (oprojectId == null) oprojectId = projectId;
      int userTip = GenericUtil.uInt(t.get("user_tip"));
      //			List list = dao.executeSQLQuery("select 1 from iwb.w5_user_tip p where
      // p.user_tip=?",userTip);
      if (GenericUtil.isEmpty(
          dao.executeSQLQuery(
              "select 1 from iwb.w5_user_tip p where p.user_tip=? AND p.project_uuid=?",
              userTip,
              projectId))) {
        dao.executeUpdateSQLQuery(
            "insert into iwb.w5_user_tip(user_tip, dsc, customization_id, project_uuid, oproject_uuid, web_frontend_tip, default_main_template_id)"
                + " values (?,?,?, ?, ?, 5, 2307)",
            userTip,
            "Role Group 1",
            cusId,
            projectId,
            oprojectId);
        Map newScd = new HashMap();
        newScd.put("projectId", projectId);
        newScd.put("customizationId", cusId);
        newScd.put("userId", userId);
        W5VcsObject vo = new W5VcsObject(newScd, 369, userTip);
        vo.setVcsObjectStatusTip((short) 9);
        dao.saveObject(vo);
        if (GenericUtil.isEmpty(
            dao.executeSQLQuery(
                "select 1 from iwb.w5_role p where p.role_id=0 AND customization_id=?", cusId))) {
          dao.executeUpdateSQLQuery(
              "insert into iwb.w5_role(role_id, customization_id, dsc, user_tip, project_uuid) values (0,?,?,?,?)",
              cusId,
              "Role " + System.currentTimeMillis(),
              userTip,
              projectId);
        }
      }
    }
    dao.reloadFrameworkCaches(cusId);
    saveImage(picUrl, userId, cusId);
  }

  public void saveImage(String imageUrl, int userId, int cusId) {
    try {
      List lf =
          dao.find(
              "select t.fileAttachmentId from W5FileAttachment t where t.tableId=336 AND t.fileTypeId=-999 AND t.tablePk=? AND t.customizationId=? AND t.orijinalFileName=?",
              "" + userId,
              cusId,
              imageUrl);
      if (!lf.isEmpty()) {
        if (UserUtil.getUserProfilePicture(userId) == (Integer) lf.get(0)) return;
      }
      URL url = new URL(imageUrl);
      int length;
      int totalBytesRead = 0;
      InputStream is = url.openStream();
      long fileId = new Date().getTime();
      W5FileAttachment fa = new W5FileAttachment();

      fa.setSystemFileName(
          fileId + "." + GenericUtil.strUTF2En(FilenameUtils.getBaseName(url.getPath())));
      String testPath =
          FrameworkCache.getAppSettingStringValue(0, "file_local_path") + File.separator + cusId;
      File f = new File(testPath);

      if (!f.exists()) {
        boolean cDir = new File(testPath).mkdirs();
        boolean aDir = new File(testPath + File.separator + "attachment").mkdirs();
      }

      String filePath =
          FrameworkCache.getAppSettingStringValue(0, "file_local_path")
              + File.separator
              + cusId
              + File.separator
              + "attachment"
              + File.separator
              + fa.getSystemFileName();

      OutputStream os = new FileOutputStream(filePath);
      byte[] b = new byte[2048];

      while ((length = is.read(b)) != -1) {
        totalBytesRead += length;
        os.write(b, 0, length);
      }
      is.close();
      os.close();

      fa.setCustomizationId(cusId);
      fa.setOrijinalFileName(imageUrl);
      fa.setUploadUserId(userId);
      fa.setFileSize(totalBytesRead);
      fa.setFileTypeId(-999);
      fa.setTabOrder((short) 1);
      fa.setActiveFlag((short) 1);
      fa.setTableId(336);
      fa.setTablePk("" + userId);
      saveObject(fa);

    } catch (IOException io) {
      io.printStackTrace();
    }
  }

  public Map userExists(String email) {
    List<Object[]> list =
        dao.executeSQLQuery(
            "select u.user_id, u.customization_id, (select min(r.user_role_id)"
                + " from iwb.w5_user_role r where r.customization_id=u.customization_id AND r.user_id=u.user_id) user_role_id from iwb.w5_user u where "
                + "u.user_status=1 AND u.auth_external_id=?",
            email);
    if (!GenericUtil.isEmpty(list)) {
      Object[] oz = list.get(0);
      return userRoleSelect(
          GenericUtil.uInt(oz[0]), GenericUtil.uInt(oz[2]), GenericUtil.uInt(oz[1]), null, null);
    } else {
      return null;
    }
  }

  public void addToProject(int userId, String projectId, String email) {
    List<Object[]> list =
        dao.executeSQLQuery(
            "select u.user_id, u.related_project_uuid from iwb.w5_user_related_project u"
                + " where u.user_id=? AND u.related_project_uuid=?",
            userId,
            projectId);
    if (!GenericUtil.isEmpty(list)) {
      dao.executeUpdateSQLQuery(
          "insert into iwb.w5_user_related_project(user_id, related_project_uuid) values (?,?)",
          userId,
          projectId);
      dao.executeUpdateSQLQuery("update iwb.w5_user set email=? where user_id=?", email, userId);
    }
  }

  public Map runTests(Map<String, Object> scd, String testIds, String webPageId) {
    String projectUuid = scd.get("projectId").toString();
    Map m = new HashMap();
    m.put("success", true);
    List<Object[]> l = null;
    if (GenericUtil.isEmpty(testIds))
      l =
          dao.executeSQLQuery(
              "select x.test_id, x.dsc, x.code from iwb.w5_test x where x.lkp_test_type=0 AND x.project_uuid=? order by x.tab_order ",
              projectUuid);
    else {
      List params = new ArrayList();
      params.add(projectUuid);
      String[] xx = testIds.split(",");
      StringBuilder sql = new StringBuilder();
      sql.append(
          "select x.test_id, x.dsc, x.code from iwb.w5_test x where x.lkp_test_type=0 AND x.project_uuid=? AND x.test_id in(-1");
      for (String s : xx) {
        params.add(GenericUtil.uInt(s));
        sql.append(",?");
      }
      sql.append(") order by x.tab_order ");
      l = dao.executeSQLQuery(sql.toString(), params);
    }

    if (l != null) {
      Map tmp = new HashMap();
      Map msg = null, nt = null;
      if (webPageId != null) {
        msg = new HashMap();
        msg.put("success", true);
        nt = new HashMap();
        msg.put("notification", nt);
      }
      for (Object[] o : l)
        try {
          Object result = dao.executeRhinoScript(scd, tmp, o[2].toString(), tmp, "result");
          if (result != null) {
            if (result instanceof Double
                || result instanceof Integer
                || result instanceof Float
                || result instanceof BigDecimal) {
              if (GenericUtil.uInt(result) == 0) result = null;
            } else if (result instanceof Boolean) {
              if (!((Boolean) result)) result = null;
            } else if (result instanceof String) {
              if (((String) result).length() == 0) result = null;
            } else if (result instanceof NativeObject) {
            }
          }
          if (result != null) {
            m.put("dsc", o[1]);
            m.put("failId", o[0]);
            m.put("msg", result);
            return m;
          }
          if (webPageId != null) {
            nt.put("_tmpStr", "Passed: " + o[1].toString());
            UserUtil.broadCast(
                projectUuid,
                (Integer) scd.get("userId"),
                (String) scd.get("sessionId"),
                webPageId,
                msg);
          }
        } catch (Exception e) {
          m.put("dsc", o[1]);
          m.put("failId", o[0]);
          m.put("msg", e.getMessage());
          return m;
        }
    }
    return m;
  }

  public boolean changeChangeProjectStatus(
      Map<String, Object> scd, String projectUuid, int newStatus) {
    List params = new ArrayList();
    params.add(projectUuid);
    List<Map<String, Object>> l =
        dao.executeSQLQuery2Map(
            "SELECT x.*,(select q.customization_id from iwb.w5_project q where q.project_uuid=x.oproject_uuid) qcus_id FROM iwb.w5_project x WHERE x.project_uuid=? ",
            params);
    if (GenericUtil.isEmpty(l)) return false;
    Map m = l.get(0);
    if (GenericUtil.uInt(m.get("customization_id")) == 1) {
      if ((Integer) scd.get("customizationId") == 1
          || (newStatus == 2
              && GenericUtil.uInt(m.get("qcus_id")) == (Integer) scd.get("customizationId"))) {
        dao.executeUpdateSQLQuery(
            "update iwb.w5_project set project_status_tip=? WHERE project_uuid=?",
            newStatus,
            projectUuid);
        dao.addProject2Cache(projectUuid);
        return true;
      }
    }
    return false;
  }

  private W5WsMethodParam findWSMethodParamByName(List<W5WsMethodParam> l, String name) {
    if (GenericUtil.isEmpty(l)) return null;
    for (W5WsMethodParam p : l)
      if (p.getDsc().equals(name) && p.getOutFlag() != 0) {
        return p;
      }
    return null;
  }

  public Map organizeREST(Map<String, Object> scd, String serviceName) {
    Map result = new HashMap();
    result.put("success", true);
    try {
      Map rm = new HashMap();
      rm.put("_iwb_cfg", 1);
      Map<String, Object> r = REST(scd, serviceName, rm);
      W5WsMethod wsm = (W5WsMethod) r.get("_iwb_cfg_rest_method");
      W5WsMethodParam p = null;
      List<Object> dataObject = null;
      if (r.containsKey("data")) {
        Object data = r.get("data");
        p = findWSMethodParamByName(wsm.get_params(), "data");
        if (data != null && data instanceof List) {
          if (p == null) {
            p = new W5WsMethodParam();
            p.setWsMethodParamId(
                GenericUtil.getGlobalNextval(
                    "iwb.seq_ws_method_param",
                    scd.get("projectId").toString(),
                    (Integer) scd.get("userId"),
                    (Integer) scd.get("customizationId")));
            p.setWsMethodId(wsm.getWsMethodId());
            p.setDsc("data");
            p.setOutFlag((short) 1);
            p.setProjectUuid(scd.get("projectId").toString());
            p.setParamTip((short) 10);
            p.setTabOrder((short) 100);
            dao.saveObject(p);
            dao.saveObject(new W5VcsObject(scd, 1377, p.getWsMethodParamId()));
          }
          dataObject = (List) data;
        }

      } else
        for (String key : r.keySet())
          if (r.get(key) instanceof List) {
            dataObject = (List) r.get(key);
            p = findWSMethodParamByName(wsm.get_params(), key);
            if (p == null) {
              p = new W5WsMethodParam();
              p.setWsMethodParamId(
                  GenericUtil.getGlobalNextval(
                      "iwb.seq_ws_method_param",
                      scd.get("projectId").toString(),
                      (Integer) scd.get("userId"),
                      (Integer) scd.get("customizationId")));
              p.setWsMethodId(wsm.getWsMethodId());
              p.setDsc(key);
              p.setOutFlag((short) 1);
              p.setProjectUuid(scd.get("projectId").toString());
              p.setParamTip((short) 10);
              p.setTabOrder((short) 100);
              dao.saveObject(p);
              dao.saveObject(new W5VcsObject(scd, 1377, p.getWsMethodParamId()));
            }
          }
      if (dataObject != null)
        for (Object o : dataObject)
          if (o != null) {
            if (o instanceof Map) {
              short tabOrder = 110;
              Map<String, Object> om = (Map<String, Object>) o;
              for (String key : om.keySet())
                if (findWSMethodParamByName(wsm.get_params(), key) == null) {
                  W5WsMethodParam p2 = new W5WsMethodParam();
                  p2.setWsMethodParamId(
                      GenericUtil.getGlobalNextval(
                          "iwb.seq_ws_method_param",
                          scd.get("projectId").toString(),
                          (Integer) scd.get("userId"),
                          (Integer) scd.get("customizationId")));
                  p2.setWsMethodId(wsm.getWsMethodId());
                  p2.setParentWsMethodParamId(p.getWsMethodParamId());
                  p2.setDsc(key);
                  p2.setOutFlag((short) 1);
                  p2.setProjectUuid(scd.get("projectId").toString());
                  p2.setParamTip((short) 1);
                  if (om.get(key) != null && om.get(key) instanceof List)
                    p2.setParamTip((short) 10);
                  if (om.get(key) != null && om.get(key) instanceof Map) p2.setParamTip((short) 9);
                  p2.setTabOrder(tabOrder);
                  tabOrder += 10;
                  dao.saveObject(p2);
                  dao.saveObject(new W5VcsObject(scd, 1377, p2.getWsMethodParamId()));

                  if (om.get(key) != null && om.get(key) instanceof Map) {
                    short tabOrder2 = (short) (10 * tabOrder);
                    Map<String, Object> om2 = (Map<String, Object>) om;
                    for (String key2 : om2.keySet())
                      if (findWSMethodParamByName(wsm.get_params(), key2) == null) {
                        W5WsMethodParam p22 = new W5WsMethodParam();
                        p22.setWsMethodParamId(
                            GenericUtil.getGlobalNextval(
                                "iwb.seq_ws_method_param",
                                scd.get("projectId").toString(),
                                (Integer) scd.get("userId"),
                                (Integer) scd.get("customizationId")));
                        p22.setWsMethodId(wsm.getWsMethodId());
                        p22.setParentWsMethodParamId(p2.getWsMethodParamId());
                        p22.setDsc(key2);
                        p22.setOutFlag((short) 1);
                        p22.setProjectUuid(scd.get("projectId").toString());
                        p22.setParamTip((short) 1);
                        if (om2.get(key2) != null && om2.get(key2) instanceof List)
                          p22.setParamTip((short) 10);
                        p22.setTabOrder(tabOrder2);
                        tabOrder2 += 10;
                        dao.saveObject(p22);
                        dao.saveObject(new W5VcsObject(scd, 1377, p22.getWsMethodParamId()));
                      }
                  }
                }
            }
            break;
          }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }
}
