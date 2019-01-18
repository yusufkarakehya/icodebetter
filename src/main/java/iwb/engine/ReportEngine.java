package iwb.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.W5Customization;
import iwb.domain.db.W5Jasper;
import iwb.domain.db.W5JasperObject;
import iwb.domain.db.W5JasperReport;
import iwb.domain.db.W5LookUp;
import iwb.domain.db.W5LookUpDetay;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5TableField;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5JasperResult;
import iwb.domain.result.W5QueryResult;
import iwb.exception.IWBException;
import iwb.util.GenericUtil;
import iwb.util.JasperUtil;
import iwb.util.Money2Text;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;

@Component
public class ReportEngine {
	@Lazy
	@Autowired
	private PostgreSQL dao;
	

	@Lazy
	@Autowired
	private QueryEngine queryEngine;
	

	@Lazy
	@Autowired
	private XScriptEngine scriptEngine;

	public JasperPrint prepareJasperPrint(Map<String, Object> scd, Map<String, String> requestParams,
			JRFileVirtualizer virtualizer) {
		JasperPrint jasperPrint = new JasperPrint();
		int customizationId = (Integer) scd.get("customizationId");
		int jasperReportId = GenericUtil.uInt(requestParams, "_jrid");
		int jasperTypeId = GenericUtil.uInt(requestParams, "_jtid"); // jasper
																		// raporlara
																		// ön
																		// yazı
																		// eklemek
																		// icin
																		// _jtid
																		// isminde
																		// raporlar
																		// yolluyoruz.
		int jasperFooterId = GenericUtil.uInt(requestParams, "_jfid"); // jasper
																		// raporlara
																		// alt
																		// yazı
																		// eklemek
																		// icin
																		// _jfid
																		// isminde
																		// raporlar
																		// yolluyoruz.
		W5JasperResult result = getJasperResult(scd, requestParams, jasperReportId);
		String locale = (requestParams.get("tlocale") == null) ? (String) scd.get("locale")
				: (String) requestParams.get("tlocale"); // Sisteme login dili
															// değilde farklı
															// bir dil seçeneği
															// kullanılmak
		// isteniyosa
		String file_name = result.getFile_name();
		String fileLocalPath = FrameworkCache.getAppSettingStringValue(scd, "file_local_path");
		// String localePath=fileLocalPath+"/"+(jasperReportId <100000 ?
		// 0:customizationId)+"/jasper/";
		Map<String, Object> resultMap = (Map) result.getResultMap();
		W5Customization c = FrameworkCache.wCustomizationMap.get(customizationId);

		resultMap.put("CompanyLogoFilePath", fileLocalPath + "/0/jasper/inscada.png"); // firma
																						// logosunun
																						// pathi
																						// gönderiliyor

		try {
			String localePath = fileLocalPath + "/" + customizationId + "/jasper/";
			File f = new File(localePath + file_name);
			if (!f.exists()) {
				localePath = fileLocalPath + "/0/jasper/";
				f = new File(localePath + file_name);
			}
			resultMap.put("localePath", localePath); // jasper folder path
			resultMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
			jasperPrint = JasperFillManager.fillReport(localePath + file_name, resultMap,
					new JRMapCollectionDataSource(result.getResultDetail()));
			/*
			 * if(result.getJasper().getLocaleKeyFlag()==1){//Jasper Raporunda
			 * textfild'e Key degerleri verilmisse,Key degerinin karsılıgı
			 * alınıyor.
			 * jasperPrint=JasperUtil.convertKey2LocaleMsg(jasperPrint,locale);
			 * }
			 */

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
				JasperPrint coverJasperPrint = JasperFillManager.fillReport(localePath + file_name, resultMapCover,
						new JRMapCollectionDataSource(result.getResultDetail()));
				// coverJasperPrint=JasperUtil.convertKey2LocaleMsg(coverJasperPrint,locale);
				int index = 0;
				for (Object addPage : coverJasperPrint.getPages()) {
					jasperPrint.addPage(index, (JRPrintPage) addPage);
					index++;
				}
			}

			if (jasperFooterId > 0) { // Jasper Raporlar için Alt Yazı
				W5JasperReport jp = getJasperReport(scd, result.getJasperId(), requestParams, jasperFooterId);
				Map<String, Object> resultMapFooter = (Map) result.getResultMap();
				resultMapFooter.put("total_page_number", jasperPrint.getPages().size());
				file_name = jp.getReportFileName();
				localePath = fileLocalPath + "/" + customizationId + "/jasper/";
				f = new File(localePath + file_name);
				if (!f.exists()) {
					localePath = fileLocalPath + "/0/jasper/";
					f = new File(localePath + file_name);
				}
				JasperPrint footerJasperPrint = JasperFillManager.fillReport(localePath + file_name, resultMapFooter,
						new JRMapCollectionDataSource(result.getResultDetail()));
				// footerJasperPrint=JasperUtil.convertKey2LocaleMsg(footerJasperPrint,locale);
				int index = jasperPrint.getPages().size();
				for (Object addPage : footerJasperPrint.getPages()) {
					jasperPrint.addPage(index, (JRPrintPage) addPage);
					index++;
				}
			}

			/*
			 * if(result.getJasper().getLocaleKeyFlag()==1){//Jasper Raporunda
			 * textfild'e Key degerleri verilmisse,Key degerinin karsılıgı
			 * alınıyor.
			 * jasperPrint=JasperUtil.convertKey2LocaleMsg(jasperPrint,locale);
			 * }
			 */

		} catch (Exception e) {
			if (FrameworkSetting.debug)
				e.printStackTrace();
			throw new IWBException("Error", "Jasper", 0, "", e.getMessage(), e.getCause());
		}
		return jasperPrint;
	}

	public W5JasperReport getJasperReport(Map<String, Object> scd, int jasperId, Map<String, String> requestParams,
			int jasperTypeId) {
		List<W5JasperReport> l = (List<W5JasperReport>) dao.find(
				"from W5JasperReport t where t.customizationId=? and t.jasperReportId=?", scd.get("customizationId"),
				jasperTypeId);
		return l.isEmpty() ? null : l.get(0);
	}
	
	public W5JasperResult getJasperResult(Map<String, Object> scd, Map<String, String> requestParams,
			int jasperReportId) {
		String xlocale = (String) scd.get("locale");
		int customizationId = (Integer) scd.get("customizationId");

		W5JasperReport jasperreport = dao.getJasperReport(scd, jasperReportId);
		W5JasperResult jasperResult = dao.getJasperResult(scd, jasperreport, requestParams);
		String file_name = jasperreport.getReportFileName().toString();
		jasperResult.setFile_name(file_name);

		List<Map> detail = new ArrayList(); // detaylar için initialize ediliyor

		jasperResult.getResultMap().putAll(requestParams); // request oldugu
															// gibi aktar

		int _saveTableId = GenericUtil.uInt(requestParams.get("_saveTableId"));
		String _saveTablePk = requestParams.get("_saveTablePk");
		if (_saveTableId != 0 && _saveTablePk != null) { // Bagli oldugu tabloyu
															// oldugu gibi
															// aktar.
			Map<String, Object> mainTableData = dao.getMainTableData(scd, _saveTableId, _saveTablePk).get(0);
			jasperResult.getResultMap().putAll(mainTableData); // .p((Map));
		}

		for (W5JasperObject o : jasperResult.getJasper().get_jasperObjects())
			switch (o.getObjectTip()) {
			case 3:
				continue;
			case 1: // query
				if (o.getJasperQueryType() == 1) { // Standart Query
					if (o.getSingleRecordFlag() == 1) { // param
						W5QueryResult queryResult = queryEngine.executeQuery(scd, o.getObjectId(), requestParams);
						if (queryResult != null && queryResult.getData().size() > 0) {
							for (W5QueryField f : queryResult.getQuery().get_queryFields()) {
								W5TableField tf = f.getMainTableFieldId() > 0
										? queryResult.getMainTable().get_tableFieldMap().get(f.getMainTableFieldId())
										: null;
								if (tf != null && !GenericUtil.accessControl(scd, tf.getAccessViewTip(),
										tf.getAccessViewRoles(), tf.getAccessViewUsers()))
									continue;
								W5QueryField helper = null;
								Object helperData = null;
								for (W5QueryField h : queryResult.getQuery().get_queryFields()) {
									if (h.getDsc().equals(f.getDsc() + "_hp_")) {
										helperData = queryResult.getData().get(0)[h.getTabOrder() - 1];
									}
								}
								Object obj = queryResult.getData().get(0)[f.getTabOrder() - 1];
								jasperResult.getResultMap().put(f.getDsc(),
										jasperReportObjectOrganizer(obj, o, f, xlocale, customizationId, helperData));
							}
						}

					} else { // detail
						W5QueryResult queryResult = queryEngine.executeQuery(scd, o.getObjectId(), requestParams);
						for (int i = 0; i < queryResult.getData().size(); i++) {
							Map<String, Object> rowMap = new HashMap<String, Object>();

							for (W5QueryField f : queryResult.getQuery().get_queryFields()) {
								W5TableField tf = f.getMainTableFieldId() > 0
										? queryResult.getMainTable().get_tableFieldMap().get(f.getMainTableFieldId())
										: null;
								if (tf != null && !GenericUtil.accessControl(scd, tf.getAccessViewTip(),
										tf.getAccessViewRoles(), tf.getAccessViewUsers()))
									continue;
								W5QueryField helper = null;
								Object helperData = null;
								for (W5QueryField h : queryResult.getQuery().get_queryFields()) {
									if (h.getDsc().equals(f.getDsc() + "_hp_")) {
										helperData = queryResult.getData().get(0)[h.getTabOrder() - 1];
									}
								}
								Object obj = queryResult.getData().get(i)[f.getTabOrder() - 1];
								rowMap.put(f.getDsc().toUpperCase(FrameworkSetting.appLocale),
										jasperReportObjectOrganizer(obj, o, f, xlocale, customizationId, helperData));
							}

							detail.add(rowMap);
						}
					}
				} else if (o.getJasperQueryType() == 2) { // Subreport Query
					W5QueryResult queryResult = queryEngine.executeQuery(scd, o.getObjectId(), requestParams);
					List<Map> list = new ArrayList<Map>();
					for (int i = 0; i < queryResult.getData().size(); i++) {
						Map<String, Object> rowMap = new HashMap<String, Object>();

						for (W5QueryField f : queryResult.getQuery().get_queryFields()) {
							W5TableField tf = f.getMainTableFieldId() > 0
									? queryResult.getMainTable().get_tableFieldMap().get(f.getMainTableFieldId())
									: null;
							if (tf != null && !GenericUtil.accessControl(scd, tf.getAccessViewTip(),
									tf.getAccessViewRoles(), tf.getAccessViewUsers()))
								continue;
							W5QueryField helper = null;
							Object helperData = null;
							for (W5QueryField h : queryResult.getQuery().get_queryFields()) {
								if (h.getDsc().equals(f.getDsc() + "_hp_")) {
									helperData = queryResult.getData().get(0)[h.getTabOrder() - 1];
								}
							}
							Object obj = queryResult.getData().get(i)[f.getTabOrder() - 1];
							rowMap.put(f.getDsc().toUpperCase(FrameworkSetting.appLocale),
									jasperReportObjectOrganizer(obj, o, f, xlocale, customizationId, helperData));
						}
						list.add(rowMap);
					}
					jasperResult.getResultMap().put(queryResult.getQuery().getDsc(), list);
				}
				break;
			case 2: // dbFunc
				W5GlobalFuncResult dbFuncResult = scriptEngine.executeFunc(scd, o.getObjectId(), requestParams, (short) 5);
				Map<String, String> resultMap = dbFuncResult.getResultMap();
				if (o.getHtmlFlag() == 1) {
					for (Iterator i = resultMap.keySet().iterator(); i.hasNext();) {
						String key = (String) i.next();
						String value = JasperUtil.changeHtmlFont(resultMap.get(key));
						resultMap.put(key.toString(), value);
					}
				}
				if (resultMap != null)
					jasperResult.getResultMap().putAll(resultMap);
				break;
			}

		if (!detail.isEmpty())
			jasperResult.setResultDetail(detail);

		return jasperResult;
	}
	

	@SuppressWarnings("unused")
	private Object jasperReportObjectOrganizer(Object obj, W5JasperObject o, W5QueryField f, String xlocale,
			int customizationId, Object helperData) {

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
					helperData = FrameworkCache.getAppSettingStringValue(customizationId, "client_para_tip", "");
				obj = Money2Text.StartConvert(GenericUtil.udouble(obj.toString()), helperData.toString(), xlocale);
			} else if (f.getDsc().contains("_flag")) {
				obj = GenericUtil.uInt(res) != 0 ? "x" : "o";
			} else if (f.getPostProcessTip() == 10) { // demek ki lookup'li
														// deger tutulacak
				W5LookUp lookUp = FrameworkCache.getLookUp(customizationId, f.getLookupQueryId()); // BUG
																									// customizationId
																									// olamaz
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

	public W5QueryResult getJasperMultipleData(Map<String, Object> scd, Map<String, String> requestParams,
			int jasperId) {
		W5Jasper jasper = (W5Jasper) dao.getObject(W5Jasper.class, jasperId);
		W5QueryResult queryResult = queryEngine.executeQuery(scd, jasper.getMultiJasperQueryId(), requestParams);
		return queryResult;
	}
}
