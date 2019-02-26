package iwb.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.Log5Feed;
import iwb.domain.db.Log5Notification;
import iwb.domain.db.Log5WorkflowRecord;
import iwb.domain.db.W5Comment;
import iwb.domain.db.W5Form;
import iwb.domain.db.W5Workflow;
import iwb.domain.db.W5WorkflowRecord;
import iwb.domain.db.W5WorkflowStep;
import iwb.domain.helper.W5AccessControlHelper;
import iwb.domain.result.W5FormResult;
import iwb.exception.IWBException;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;
import iwb.util.ScriptUtil;
import iwb.util.UserUtil;

@Component
public class WorkflowEngine {
	@Lazy
	@Autowired
	private PostgreSQL dao;

	@Lazy
	@Autowired
	private NotificationEngine notificationEngine;
	
	@Lazy
	@Autowired
	private GlobalScriptEngine scriptEngine;

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
	    W5WorkflowRecord ar = (W5WorkflowRecord)dao.getCustomizedObject("from W5WorkflowRecord t where t.approvalRecordId=? AND t.projectUuid=?", approvalRecordId, scd.get("projectId"), "Workflow Record not Found");
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
	        	
	        	Object oz = scriptEngine.executeScript(scd, parameterMap, a.getAdvancedBeginSql(), null, "wf_"+a.getApprovalId()+"_abs");
				if(oz!=null) {
					if(oz instanceof Boolean) {
						if(!((Boolean)oz))
					          throw new IWBException(
						              "validation",
						              "WorkflowRecord",
						              approvalRecordId,
						              null,
						              LocaleMsgCache.get2(0, xlocale, "approval_request_denied"),
						              null);
					} else
						advancedStepSqlResult = ScriptUtil.fromScriptObject2Map(oz); 
				}
	          //Object[] oz = DBUtil.filterExt4SQL(a.getAdvancedBeginSql(), scd, parameterMap, null);
	          //advancedStepSqlResult = dao.runSQLQuery2Map(oz[0].toString(), (List) oz[1], null);
	          // donen bir cevap var, aktive_flag deger olarak var ve onun degeri 0 ise o zaman
	          // girmeyecek
	          /*if (advancedStepSqlResult != null) {
	            // onay başladıktan sonra AdvancedBeginSql ile tekrar active_flag kontrolü yapmak
	            // anlamsız olmuş bu yüzden burayı kapattım (çağlar)
	            //if(advancedStepSqlResult.get("active_flag")!=null && PromisUtil.uInt(advancedStepSqlResult.get("active_flag"))==0)//girmeyecek
	            //a = null; //approval olmayacak
	            if (advancedStepSqlResult.get("error_msg") != null) // girmeyecek
	            throw new IWBException(
	                  "security",
	                  "Workflow",
	                  a.getApprovalId(),
	                  null,
	                  (String) advancedStepSqlResult.get("error_msg"),
	                  null);
	          }*/
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
/*
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
	        }*/

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
	              scriptEngine.executeGlobalFunc(
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
	         /*   if (a.getApprovalFlowTip() == 3) { // dynamic onay DEPRECATED
	              if (ar.getApprovalStepId() == 903) { // yani bu su anda hala dinamik onayda. complekse gecmemis

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
	            }/* else if (a.getApprovalFlowTip() == 2) { // hiyerarsik onay: deprecated
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
	            }*/

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
	                Object oz = scriptEngine.executeScript(scd, parameterMap, currentStep.getOnApproveStepSql(), null, "wfs_"+nextStepId+"_ass");
					if(oz!=null) {
						if(oz instanceof Boolean) {
							if(!((Boolean)oz))
						          throw new IWBException(
							              "framework",
							              "WorkflowRecord",
							              approvalRecordId,
							              null,
							              LocaleMsgCache.get2(0, xlocale, "approval_denied"),
							              null);
						} else if(oz instanceof Integer) {
							nextStepId = (Integer) oz;
						} else
							advancedNextStepSqlResult = ScriptUtil.fromScriptObject2Map(oz); 
					}
	             /*   Object[] oz =
	                    DBUtil.filterExt4SQL(
	                        currentStep.getOnApproveStepSql(), scd, parameterMap, null);
	                advancedNextStepSqlResult =
	                    dao.runSQLQuery2Map(oz[0]. toString(), (List) oz[1], null);
	                if (advancedNextStepSqlResult.get("next_step_id") != null)
	                  nextStepId = GenericUtil.uInt(advancedNextStepSqlResult.get("next_step_id"));*/
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
/*
	        if (nextStep != null
	            && nextStep.getDynamicRoleUserSql() != null
	            && nextStep.getDynamicRoleUserSql().length() > 10) { // calisacak
	          Map<String, Object> dynamicRoleUserSql = null;
	          
				Object oz = scriptEngine.executeScript(scd, parameterMap, nextStep.getDynamicRoleUserSql(), null, "wfs_"+nextStep.getApprovalStepId()+"_drs");
				if(oz!=null) {
					if(oz instanceof Boolean) {
						if(!((Boolean)oz))
					          throw new IWBException(
						              "validation",
						              "WorkflowRecord",
						              approvalRecordId,
						              null,
						              LocaleMsgCache.get2(0, xlocale, "approval_denied"),
						              null);
					} else
						dynamicRoleUserSql = ScriptUtil.fromScriptObject2Map(oz); 
				}

				
//	          dynamicRoleUserSql = dao.runSQLQuery2Map(nextStep.getDynamicRoleUserSql(), scd, parameterMap, null);
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
*/
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
	                Object oz = scriptEngine.executeScript(scd, parameterMap, currentStep.getOnReturnStepSql(), null, "wfs_"+returnStepId+"_rss");
					if(oz!=null) {
						if(oz instanceof Boolean) {
							if(!((Boolean)oz))
						          throw new IWBException(
							              "validation",
							              "WorkflowRecord",
							              approvalRecordId,
							              null,
							              LocaleMsgCache.get2(0, xlocale, "return_denied"),
							              null);
						} else if(oz instanceof Integer) {
							returnStepId = (Integer) oz;
						} else
							advancedNextStepSqlResult = ScriptUtil.fromScriptObject2Map(oz); 
					}/*
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
	                returnStepId = GenericUtil.uInt(advancedNextStepSqlResult.get("return_step_id"));*/
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
	                    
	                    Object oz = scriptEngine.executeScript(scd, parameterMap, a.getAdvancedBeginSql(), null, "wf_"+a.getApprovalId()+"_abs");
						if(oz!=null) {
							if(oz instanceof Boolean) {
								if(!((Boolean)oz))ar.setApprovalUsers(String.valueOf(ar.getInsertUserId()));
							} else
								advancedStepSqlResult = ScriptUtil.fromScriptObject2Map(oz); 
						}
	                    //advancedStepSqlResult =  dao.runSQLQuery2Map(a.getAdvancedBeginSql(), scd, parameterMap, null);
	                    // donen bir cevap var, aktive_flag deger olarak var ve onun degeri 0 ise o
	                    // zaman girmeyecek
	                  /*  if (advancedStepSqlResult != null
	                        && advancedStepSqlResult.get("active_flag") != null
	                        && GenericUtil.uInt(advancedStepSqlResult.get("active_flag"))
	                            == 0) { // girmeyecek
	                      ar.setApprovalUsers(String.valueOf(ar.getInsertUserId()));
	                    } else */if (advancedStepSqlResult != null) {
	                      if (advancedStepSqlResult.get("approval_users") != null)
	                        ar.setApprovalUsers((String) advancedStepSqlResult.get("approval_users"));
	                      if (advancedStepSqlResult.get("approval_roles") != null)
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
	                  + "(gu.user_id in (select lar.user_id from iwb.log5_approval_record lar where (lar.approval_action_tip = 0 or lar.approval_action_tip = 901) and lar.approval_record_id = ?::integer) or "
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

	   /*   mailSubject =
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

	      messageBody = ar.getDsc() + mesaj;*/

	      ar.setFinishedFlag((short) 1);
	      ar.setApprovalRoles(null);
	      ar.setApprovalUsers(null);
	      ar.setAccessViewTip((short) 0);
	    }

	    /* Record Save Ediliyor */
	    dao.saveObject(logRecord);
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

	      notificationEngine.sendSms(
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
//	      comment.setCustomizationId(Integer.parseInt(scd.get("customizationId") + ""));
	      dao.saveObject(comment);
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

	      dao.saveObject(feed);
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
}
