package iwb.adapter.ui.surveyjs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.domain.db.W5Detay;
import iwb.domain.db.W5Form;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5FormCellProperty;
import iwb.domain.db.W5FormModule;
import iwb.domain.db.W5QueryField;
import iwb.domain.helper.W5FormCellHelper;
import iwb.domain.result.W5FormResult;
import iwb.util.GenericUtil;

public class SurveyJS {

	@SuppressWarnings("unchecked")
	private static StringBuilder serializeFormCellProperty4SurveyJs(W5FormCellHelper cellResult, W5FormResult formResult) {
		StringBuilder buf = new StringBuilder();
		if(!GenericUtil.isEmpty(cellResult.getFormCell().get_formCellPropertyList())) for(W5FormCellProperty fcp:cellResult.getFormCell().get_formCellPropertyList()){
			if(fcp.getLkpPropertyTip()==3) {
				buf.append(",description:'").append(GenericUtil.stringToJS(fcp.getVal())).append("'");				
			} else  for(W5FormCellHelper fcr:formResult.getFormCellResults())if(fcr.getFormCell().getFormCellId()==fcp.getRelatedFormCellId()) {
				if(fcr.getFormCell().getActiveFlag()!=0 && fcp.getLkpPropertyTip()!=3) {
					buf.append(",").append(new String[] {"requiredIf:\"{","visibleIf:\"{","enableIf:\"{"}[fcp.getLkpPropertyTip()]);
						buf.append(fcr.getFormCell().getDsc());
						buf.append("} ");
						switch(fcp.getLkpOperatorTip()) {
						case -1:
							buf.append(" empty ");
							break;
						case -2:
							buf.append(" notempty ");
							break;
						case 3:case 4:case 5:case 6://> < >= <=
							buf.append(FrameworkSetting.operatorMap[fcp.getLkpOperatorTip()]).append(" ").append(fcp.getVal());							
							break;
						case 0: case 1: //equals, not equals
							if(fcr.getFormCell().getControlTip()==5) {
								if(fcp.getLkpOperatorTip()==1)buf.append("=true");
								else buf.append("=false");							
							} else 
								buf.append(FrameworkSetting.operatorMap[fcp.getLkpOperatorTip()]).append(" ").append(fcp.getVal());
							break;
						case 8: case 9://contains , not contains
							if(fcr.getFormCell().getControlTip()==8 || fcr.getFormCell().getControlTip()==15) {//multi
								if(fcp.getLkpOperatorTip()==9)buf.append("not ");
								buf.append(" contains '").append(fcp.getVal()).append("'");
							
							} else {
								if(fcp.getLkpOperatorTip()==9)buf.append("!");
								buf.append("='").append(fcp.getVal()).append("'");
							}
//							buf.append(FrameworkSetting.operatorMap[fcp.getLkpOperatorTip()]).append(" ").append(fcp.getVal());
							break;
						default:
						
						}
						
						buf.append("\"");
					
					break;
				}
			}			
		}
		
		return buf;
	}
	@SuppressWarnings("unchecked")
	private static StringBuilder serializeFormModule4FormCells(W5FormResult formResult, List<W5FormCellHelper> lfc) {
		StringBuilder buf = new StringBuilder();
		Map scd = formResult.getScd();
		buf.append("{questions:[");
		boolean b = false;
		for(W5FormCellHelper fc:lfc) {
			Object o = serializeFormCell4SurveyJS(fc, formResult, false);
			if(o==null)continue;
			if(b)buf.append(",");
			else b = true;
			buf.append(o);
		}
		return buf.append("]}");
		
	}
	@SuppressWarnings("unchecked")
	public static StringBuilder serializeForm4SurveyJS(W5FormResult formResult, int renderer) { //1:extjs, 5:react
		StringBuilder buf = new StringBuilder();
		Map scd = formResult.getScd();
		W5Form f = formResult.getForm();
		Map<Integer, List<W5FormCellHelper>> map = new HashMap<Integer, List<W5FormCellHelper>>();
		map.put(0, new ArrayList<W5FormCellHelper>());
		if (formResult.getForm().get_moduleList() != null)
			for (W5FormModule m : formResult.getForm().get_moduleList()) {
				map.put(m.getFormModuleId(), new ArrayList<W5FormCellHelper>());
			}
		for (W5FormCellHelper m : formResult.getFormCellResults())
			if (m.getFormCell().getActiveFlag() != 0) {
				List<W5FormCellHelper> l = map.get(m.getFormCell()
						.getFormModuleId());
				if (l == null)
					l = map.get(0);
				l.add(m);
			}
		
		buf.append("\nvar __action__=").append(formResult.getAction()).append("\n");


		buf.append("var surveyJson = {title: '").append(LocaleMsgCache.get2(scd, f.getLocaleMsgKey()))
		.append("',showProgressBar: 'top',pages: [");
		
		if (map.get(0).size() > 0) {
			buf.append(serializeFormModule4FormCells(formResult, map.get(0))).append("\n,");
		}
		if (formResult.getForm().get_moduleList() != null)
			for (W5FormModule m : formResult.getForm().get_moduleList())
				if (m.getFormModuleId() != 0) {
					if ((m.getModuleViewTip() == 0 || formResult.getAction() == m
							.getModuleViewTip()) 
							&& GenericUtil.accessControl(formResult.getScd(),
									m.getAccessViewTip(),
									m.getAccessViewRoles(),
									m.getAccessViewUsers())) {
						switch (m.getModuleTip()) {
						case	0:
							if(!GenericUtil.isEmpty(map.get(m.getFormModuleId())))
								buf.append(serializeFormModule4FormCells(formResult, map.get(m.getFormModuleId()))).append("\n,");
							break;
						case	3:
							W5FormResult dfr = formResult.getModuleFormMap().get(m.getObjectId());
							if(dfr!=null) {
								buf.append(serializeFormModule4FormResult(formResult, dfr)).append("\n,");
							}
							
							break;
						
						}
					}
				}
		if(buf.charAt(buf.length()-1)==',')buf.setLength(buf.length()-1);
		buf.append("]}");
		

		// 24 nolu form form edit form olduğu için onu çevirmesin.
		String postCode = (formResult.getForm().get_renderTemplate() != null
				&& formResult.getForm().get_renderTemplate().getLocaleMsgFlag() == 1 && formResult
				.getFormId() != 24) ? GenericUtil.filterExt(
				formResult.getForm().getJsCode(), scd,
				formResult.getRequestParams(), null).toString() : formResult
				.getForm().getJsCode();

		boolean b = true;
		if (postCode != null && postCode.length() > 10) {
			if (postCode.charAt(0) == '!') {
				postCode = postCode.substring(1);
			} else
				b = false;
		} else
			postCode = "";
		
		if (!GenericUtil.isEmpty(postCode)) {
			buf.append("\ntry{").append(postCode).append("\n}catch(e){");
			buf.append(FrameworkSetting.debug ? "if(confirm('ERROR Survey.JS!!! Throw? : ' + e.message))throw e;"
					: "alert('System/Customization ERROR : ' + e.message)");
			buf.append("}\n");
		}

        
		buf.append("\nvar survey=new Survey.Model(surveyJson);survey.locale='").append(scd.get("locale"))
		.append("';\nsurvey.onComplete.add((xresult)=>iwb.postSurveyJs(").append(f.getFormId()).append(",__action__,xresult.data,()=>{}));\n")
		.append("survey.onUploadFiles.add((survey, options)=>iwb.fileUploadSurveyJs(").append(f.getObjectId()).append(",").append(-GenericUtil.getNextTmpId())
		.append(",survey, options,()=>{}));\n");
		
		if(renderer==5)//react
			buf.append("return _(CardBody,{},_(Survey.Survey,{model:survey}))");
		else //extjs
			buf.append("return new Ext.Panel({closable:!0, title:'").append(LocaleMsgCache.get2(formResult.getScd(), formResult.getForm().getLocaleMsgKey())).append("',html:'<div id=\"surveyElement-' + _page_tab_id + '\" style=\"width:100%;height:100%;overflow:auto\"></div>',listeners:{afterrender:()=>$('#surveyElement-' + _page_tab_id).Survey({ model: survey })}})");
		
		return buf;
	}

	private static StringBuilder serializeFormModule4FormResult(W5FormResult formResult, W5FormResult dfr) {
		StringBuilder buf = new StringBuilder();
		Map scd = formResult.getScd();
		W5Form df = dfr.getForm();
		buf.append("{questions:[{type: 'matrixdynamic',title:'").append(LocaleMsgCache.get2(scd, df.getLocaleMsgKey()))
		.append("', rowCount: 1, minRowCount: 1, name:'_form_").append(df.getFormId()).append("', columns:[{name: 'id',title: '#', cellType: 'expression', expression: '{rowIndex}'}");
		for(W5FormCellHelper fc:dfr.getFormCellResults()) {
			Object o = serializeFormCell4SurveyJS(fc, formResult, true);
			if(o!=null)buf.append(",").append(o);
		}
		return buf.append("]}]}");
	}
	@SuppressWarnings("unchecked")
	private static StringBuilder serializeFormCell4SurveyJS(W5FormCellHelper cellResult, W5FormResult formResult, boolean forMatrix) {
		StringBuilder buf = new StringBuilder();
		W5FormCell fc = cellResult.getFormCell();
		String value = cellResult.getValue(); // bu ilerde hashmap ten gelebilir
		if (fc.getControlTip() == 0 || fc.getControlTip() == 100 || fc.getControlTip() == 102 || fc.getControlTip() == 101 || cellResult.getHiddenValue() != null)return null;
		
		buf.append("{name:'").append(fc.getDsc()).append("', title:'").append(LocaleMsgCache.get2(formResult.getScd(), fc.getLocaleMsgKey())).append("'");
		if(fc.getNotNullFlag()!=0)buf.append(",isRequired:true");
		buf.append(serializeFormCellProperty4SurveyJs(cellResult, formResult));
		
		buf.append(",").append(!forMatrix ? "type":"cellType").append(":'");
		
		
		switch(fc.getControlTip()){
			case	1:buf.append("text'");
				if(GenericUtil.safeEquals(fc.getVtype(), "email"))buf.append(",inputType:'email',validators: [{type: 'email'}]");
				else if(GenericUtil.safeEquals(fc.getVtype(), "url"))buf.append(",inputType:'url',validators: [{type: 'url'}]");
			break;//string
			case	2:buf.append("text',inputType:'date'");break; //TODO:date
			case	18:buf.append("text',inputType:'datetime'");break; //TODO:datetime
			case	22:buf.append("text',inputType:'time'");break; //TODO:time
			case	3://double
			case	4://integer
				buf.append("text',inputType:'number'");
				break;
			case	5:buf.append("boolean'");break;

		
			case	6://combo static
			case	8:// lovcombo-static
			case	58:// superbox lovcombo-static
		
			case	7://combo query
			case	15://lovcombo query
			case	59://superbox lovcombo query
				buf.append("");
				if(fc.getControlTip()<8)
					buf.append(!forMatrix ? "radiogroup":"dropdown");//formResult!=null && fc.getParentFormCellId()==1?"radiogroup":"dropdown"
				else
					buf.append("checkbox");
				buf.append("', choices:[");//static combo
				if ((fc.getControlTip()==6 || fc.getControlTip()==8 ||fc.getControlTip()==58) && cellResult.getLookupListValues() != null) {
					boolean b1=false;
					
					for (W5Detay p : (List<W5Detay>) cellResult
							.getLookupListValues()) {
						if (b1)
							buf.append(",");
						else
							b1 = true;
						buf.append("{value:'").append(p.getVal()).append("',text:'")
								.append(cellResult.getLocaleMsgFlag() != 0 ? LocaleMsgCache
										.get2(formResult.getScd(), p.getDsc())
										: p.getDsc()).append("'");
						buf.append("}");
					}
				} else if ((fc.getControlTip()==7 || fc.getControlTip()==15 ||fc.getControlTip()==59)){
					if(cellResult.getLookupQueryResult()!=null && cellResult.getLookupQueryResult().getData() != null) {
						boolean b1 = false;
						for (Object[] p : cellResult.getLookupQueryResult().getData()) {
							if (b1)
								buf.append(",");
							else
								b1 = true;
							boolean bb = false;
							buf.append("{");
							for (W5QueryField f : cellResult.getLookupQueryResult().getQuery().get_queryFields()) {
								Object z = p[f.getTabOrder() - 1];
								if (bb)
									buf.append(",");
								else
									bb = true;
								if (z == null)z = "";
								buf.append(f.getDsc().equals("id")?"value":(f.getDsc().equals("dsc")?"text":f.getDsc())).append(":'")
										.append(f.getPostProcessTip() == 2 ? LocaleMsgCache
												.get2(formResult.getScd(),
														z.toString()) : GenericUtil
												.stringToJS(z.toString()))
										.append("'");
							}
							buf.append("}");
						}
					}
				}
				buf.append("]");
			break; 
			
			case	9://combo query remote
			case	16://lovcombo query remote
				buf.append("text'");
				break;
			case	10://advanced select: TODO ilk geldiginde oo loadOptions'ta atanacak
		
				buf.append("text'");
			break; // advanced select

			case	23://treecombo(local)
			case	26://lovtreecombo(local) TODO
				buf.append("text'");
			break; // 		
		
			case	12://html editor
				buf.append("text'");
			case	25://textarea(ozel tanimlama)
			case	41://codemirror
			case	11:
				buf.append("comment', rows:3");
				break; // textarea
		//	{ view:"label", label:'Fill the form below to access <br>the main datacore.'
			
			case	71://file attachment
				buf.append("file', imageHeight:200, storeDataAsText: false");
				break;
			
			default:			
				buf.append("text'");
				break;
		}

		
		buf.append("}");
		return buf;		
	}
}
