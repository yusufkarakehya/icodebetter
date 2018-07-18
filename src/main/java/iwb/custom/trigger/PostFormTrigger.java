package iwb.custom.trigger;

import iwb.dao.RdbmsDao;
import iwb.domain.db.Log5Notification;
import iwb.domain.db.W5Project;
import iwb.domain.result.W5FormResult;
import iwb.exception.IWBException;
import iwb.util.DBUtil;
import iwb.util.FrameworkCache;
import iwb.util.FrameworkSetting;
import iwb.util.GenericUtil;
import iwb.util.LocaleMsgCache;
import iwb.util.UserUtil;

public class PostFormTrigger {
	public static void beforePostForm(W5FormResult formResult, RdbmsDao dao, String prefix){
		switch(formResult.getFormId()){
		case	2491://SQL Script
			String sql = formResult.getRequestParams().get("extra_sql");
			if(!GenericUtil.isEmpty(sql) && (Integer)formResult.getScd().get("customizationId")>0 && DBUtil.checkTenantSQLSecurity(sql)) {
				throw new IWBException("security","SQL", 0, null, "Forbidden Command. Please contact iCodeBetter team ;)", null);
			}
			if(GenericUtil.uCheckBox(formResult.getRequestParams().get("run_local_flag"))!=0){// simple security check. TODO
				W5Project prj = FrameworkCache.wProjects.get(formResult.getScd().get("projectId").toString());
				if(prj.getSetSearchPathFlag()!=0) {
					dao.executeUpdateSQLQuery("set search_path="+prj.getRdbmsSchema());
				}
				dao.executeUpdateSQLQuery(sql);
			}
			break;
		}
	}
	
	
	public static void afterPostForm(W5FormResult formResult, RdbmsDao dao, String prefix){
		String msg;
		switch(formResult.getFormId()){
	
		
		case	551://comment
			if(formResult.getErrorMap().isEmpty() && formResult.getAction()==2){
				Object[] userIds = dao.listObjectCommentAndAttachUsers(formResult.getScd(), formResult.getRequestParams());
				int tableId = GenericUtil.uInt(formResult.getRequestParams().get("table_id"));
				int tablePk = GenericUtil.uInt(formResult.getRequestParams().get("table_pk"));
				int sessionUserId=(Integer)formResult.getScd().get("userId");
				if(userIds!=null)for(Object userId:userIds){
					String tmpStr = formResult.getRequestParams().get("dsc");
					if(tmpStr!=null){
						tmpStr= "<b>"+UserUtil.getUserName((Integer)formResult.getScd().get("customizationId"), sessionUserId)+"</b>: "+tmpStr;
						if(tmpStr.length()>100)tmpStr=tmpStr.substring(0,97)+"...";
					}
					Log5Notification n = new Log5Notification(formResult.getScd(), (Integer)userId, (short)1,  tableId,  tablePk, sessionUserId, null, 1,tmpStr); 
					dao.saveObject(n);
					n.set_tableRecordList(dao.findRecordParentRecords(formResult.getScd(),n.getTableId(),n.getTablePk(),0, true));
					UserUtil.publishNotification(n, false);
				}
			}
			break;
/*		case	44://file attachment
			FrameworkCache.getAppSettingIntValue(formResult.getScd(), "feed_flag");
	            // fall through
			//if(FrameworkCache.getAppSettingIntValue(formResult.getScd(), "feed_flag")!=0){
			//}
		    default:
			break;*/
		}
    	msg = LocaleMsgCache.get2(formResult.getScd(), "cache_will_be_reloaded");
		/*// TODO burada direk halledilecek
		if(formResult.getForm()!=null && formResult.getForm().get_sourceTable().getTableId()==15){ //w5_table
			if(ProformResult.getRequestParams().get("revision_flag"))
			
			//create table promis_revision.pm_urun_agaci_ex as
select r.revision_id,x.* from pm_urun_agaci_ex x, w5_revision r where rownum<1

		} */
		//String msg = LocaleMsgCache.get2(formResult.getScd(), "cache_will_be_reloaded");//"Yaptığınız değişiklikler 1 dakika içerisinde uygulanacaktır.";//PromisLocaleMsg.get(formResult.getScd().get("locale").toString(), "commons.info.cache_reload");
		if(formResult.getForm()!=null)switch(formResult.getForm().getObjectId()){
		default:
            break;
	
		case	13:
		case	14://lookup,detay		
			formResult.getOutputMessages().add(msg);
			FrameworkCache.reloadCacheQueue.put("1-"+formResult.getForm().getCustomizationId(), System.currentTimeMillis());
			break;	
		case	617:
		case	618://lookup_ext/detay
			formResult.getOutputMessages().add(msg);
			FrameworkCache.reloadCacheQueue.put("3-"+formResult.getForm().getCustomizationId(), System.currentTimeMillis());			
			break;
		case	79://apsetting
			formResult.getOutputMessages().add(msg);
			FrameworkCache.reloadCacheQueue.put("2-"+formResult.getForm().getCustomizationId(), System.currentTimeMillis());			
			break;

		case	389:
		case	390://aproval/step
			formResult.getOutputMessages().add(msg);
			FrameworkCache.reloadCacheQueue.put("4-"+formResult.getForm().getCustomizationId(), System.currentTimeMillis());			
			break;
		case	1209: //table_action_rule
			formResult.getOutputMessages().add(msg);
			FrameworkCache.reloadCacheQueue.put("9-"+formResult.getForm().getCustomizationId(), System.currentTimeMillis());			
			break;
		//case	688://bpm_process
		//case	691://bpm_process_step
		//case	692://bpm_action
		//case	711://bpm_action_link_condition
		//	formResult.getOutputMessages().add(msg);
		//	FrameworkCache.reloadCacheQueue.put("5-"+formResult.getForm().getCustomizationId(), System.currentTimeMillis());			
		//	break;
		case 1065:
		case 1069://integration/brand
			formResult.getOutputMessages().add(msg);
			FrameworkCache.reloadCacheQueue.put("8-"+formResult.getForm().getCustomizationId(), System.currentTimeMillis());			
			break;
		case	15:
		case	16:
		case	42://table/field/param
		case	409://object_sms_mail
		case	628://table_user_tip				
		case	657://w5_table_child	
		case	765://object_condition
		case	1059://w5_access_delegation
		case	1380://object_condition
			if(FrameworkSetting.preloadWEngine!=0){
				FrameworkCache.clearPreloadCache();
			}
			formResult.getOutputMessages().add(msg);
			FrameworkCache.reloadCacheQueue.put("6-"+formResult.getForm().getCustomizationId(), System.currentTimeMillis());			
			break;	
		case	1217://w5_table_access_condition_sql		
			dao.reloadTableAccessConditionSQLs();
			break;	
		case	336://w5_user	
			dao.reloadUsersCache((Integer)formResult.getScd().get("customizationId"));	
			break;
		case	338://w5_customization

			break;
		case 332://w5_user_role
		case 337://w5_role
		case 1180://w5_user_role
//			dao.reloadRoleModulesCache(formResult.getForm().getCustomizationId());
			break;
		case    674://w5_job_schedule
			formResult.getOutputMessages().add(msg);
			FrameworkCache.reloadCacheQueue.put("7-"+formResult.getForm().getCustomizationId(), System.currentTimeMillis());
			break;
		case	4:
		case	5://grid,grid_column
		case	8:
		case	9:
		case	10://query/field/param
		case	20:
		case	21://dbfunc/param
		case	40:
		case	41:	
		case	63:	
		case	64://template,template object	
		case	229:
		case	230:
		case	231://form_module/grid_module
		case	254://toolbar_item/menu_item
		case	634:
		case	707:
		case	708:	
		case	730://w5_query_param_ext
		case	790://help form			
		case	872:
		case	873://grid_custom_grid_column_condition,grid_custom_grid_column_renderer
		case    877:	
		case	930: //dataview
		case	936:
		case    937: //list, list_column	
		case    1168://form/cell/code_detail/form_sms_mail
		case	1173://condition_group
		case    1198://conversion,conversion_col,conversion_detail
			if(FrameworkSetting.preloadWEngine!=0){
				FrameworkCache.clearPreloadCache();
			}
		}
		
		if(formResult.getAction()==2 && (formResult.getForm().getObjectId()==14 || formResult.getForm().getObjectId()==618)){
			int lookUpId=GenericUtil.uInt((Object)formResult.getRequestParams().get("look_up_id"));
			if(lookUpId!=0)UserUtil.broadCastRecordForTemplates((Integer)formResult.getScd().get("customizationId"), -lookUpId, "", 2, (Integer)formResult.getScd().get("userId"));
		}
		
		
	}
}
