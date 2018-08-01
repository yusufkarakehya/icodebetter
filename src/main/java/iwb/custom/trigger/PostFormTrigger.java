package iwb.custom.trigger;

import java.util.List;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.Log5Notification;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableParam;
import iwb.domain.result.W5FormResult;
import iwb.exception.IWBException;
import iwb.util.DBUtil;
import iwb.util.GenericUtil;
import iwb.util.UserUtil;

public class PostFormTrigger {
	public static void beforePostForm(W5FormResult formResult, PostgreSQL dao, String prefix){
		switch(formResult.getFormId()){
		case	2491://SQL Script
			String sql = formResult.getRequestParams().get("extra_sql");
			if(!GenericUtil.isEmpty(sql) && (Integer)formResult.getScd().get("customizationId")>0 && DBUtil.checkTenantSQLSecurity(sql)) {
				throw new IWBException("security","SQL", 0, null, "Forbidden Command. Please contact iCodeBetter team ;)", null);
			}
			if(GenericUtil.uCheckBox(formResult.getRequestParams().get("run_local_flag"))!=0){// simple security check. TODO
				W5Project prj = FrameworkCache.getProject(formResult.getScd());
				dao.executeUpdateSQLQuery("set search_path="+prj.getRdbmsSchema());
				dao.executeUpdateSQLQuery(sql);
			}
			break;
		}
	}
	
	
	public static void afterPostForm(W5FormResult fr, PostgreSQL dao, String prefix){
		String msg;
		String projectId = fr.getScd()!=null ? (String)fr.getScd().get("projectId"):null;
		switch(fr.getFormId()){
	
		
		case	551://comment
			if(fr.getErrorMap().isEmpty() && fr.getAction()==2){
				Object[] userIds = dao.listObjectCommentAndAttachUsers(fr.getScd(), fr.getRequestParams());
				int tableId = GenericUtil.uInt(fr.getRequestParams().get("table_id"));
				int tablePk = GenericUtil.uInt(fr.getRequestParams().get("table_pk"));
				int sessionUserId=(Integer)fr.getScd().get("userId");
				if(userIds!=null)for(Object userId:userIds){
					String tmpStr = fr.getRequestParams().get("dsc");
					if(tmpStr!=null){
						tmpStr= "<b>"+UserUtil.getUserName((Integer)fr.getScd().get("customizationId"), sessionUserId)+"</b>: "+tmpStr;
						if(tmpStr.length()>100)tmpStr=tmpStr.substring(0,97)+"...";
					}
					Log5Notification n = new Log5Notification(fr.getScd(), (Integer)userId, (short)1,  tableId,  tablePk, sessionUserId, null, 1,tmpStr); 
					dao.saveObject(n);
					n.set_tableRecordList(dao.findRecordParentRecords(fr.getScd(),n.getTableId(),n.getTablePk(),0, true));
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
    	msg = LocaleMsgCache.get2(fr.getScd(), "reload_cache_manually");
		if(fr.getForm()!=null)switch(fr.getForm().getObjectId()){
		default:
            break;
	
		case	13:
		case	14://lookup,detay		
			fr.getOutputMessages().add(msg);
			int lookUpId = GenericUtil.uInt(fr.getOutputFields().get("look_up_id"+prefix));
			if(lookUpId==0)lookUpId = GenericUtil.uInt(fr.getRequestParams().get("look_up_id"+prefix));
			if(lookUpId==0)lookUpId = GenericUtil.uInt(fr.getRequestParams().get("tlook_up_id"+prefix));
			if(fr.getForm().getObjectId()==14 && lookUpId==0){
				int detayId = GenericUtil.uInt(fr.getRequestParams().get("tlook_up_detay_id"+prefix));
				if(detayId==0)detayId=GenericUtil.uInt(fr.getOutputFields().get("look_up_detay_id"+prefix));
				if(detayId>0){
					List qq = dao.executeSQLQuery("select x.look_up_id from iwb.w5_look_up_detay x where x.project_uuid=? AND x.look_up_detay_id=?", projectId, detayId);
					if(!GenericUtil.isEmpty(qq))lookUpId = GenericUtil.uInt(qq.get(0));
				}
			}
			FrameworkCache.addQueuedReloadCache(projectId,"13."+lookUpId);
			break;	
		case	79://apsetting
			fr.getOutputMessages().add(msg);
//			FrameworkCache.reloadCacheQueue.put("2-"+fr.getForm().getCustomizationId(), System.currentTimeMillis());			
			break;
			
			
		case	15://table
		case	16://tablefield
		case	945://tablefieldcalc
		case	42://tableparam
		case	657://w5_table_child	
//		case	764://w5_table_filter	
		case	1209: //tableevent
		case	1217://w5_table_access_condition_sql		
			if(FrameworkSetting.preloadWEngine!=0){
				FrameworkCache.clearPreloadCache(fr.getScd());
			}
			fr.getOutputMessages().add(msg);
			int tableId = GenericUtil.uInt(fr.getOutputFields().get("table_id"+prefix));
			if(tableId==0)lookUpId = GenericUtil.uInt(fr.getRequestParams().get("table_id"+prefix));
			if(tableId==0)lookUpId = GenericUtil.uInt(fr.getRequestParams().get("ttable_id"+prefix));
			if(tableId==0){
				W5Table tx = FrameworkCache.getTable(projectId, fr.getForm().getObjectId());
				W5TableParam tp = tx.get_tableParamList().get(0);
				int detayId = GenericUtil.uInt(fr.getRequestParams().get(tp.getDsc()+prefix));
				if(detayId==0)detayId=GenericUtil.uInt(fr.getOutputFields().get(tp.getExpressionDsc()+prefix));
				if(detayId>0){
					List qq = dao.executeSQLQuery("select x.table_id from "+tx.getDsc()+" x where x.project_uuid=? AND x."+tp.getExpressionDsc()+"=?", projectId, detayId);
					if(!GenericUtil.isEmpty(qq))tableId = GenericUtil.uInt(qq.get(0));
				}
			}
			FrameworkCache.addQueuedReloadCache(projectId,"15."+tableId);
			break;	

		case	389:
		case	390://workflow/step
			if(FrameworkSetting.preloadWEngine!=0){
				FrameworkCache.clearPreloadCache(fr.getScd());
			}
			int workflowId = GenericUtil.uInt(fr.getOutputFields().get("approval_id"+prefix));
			fr.getOutputMessages().add(msg);
			FrameworkCache.addQueuedReloadCache(projectId,"389."+workflowId);			
			break;

		case	336://w5_user	
			dao.reloadUsersCache(projectId);	
			break;
		case	338://w5_customization

			break;

		case    674://w5_job_schedule
			fr.getOutputMessages().add(msg);
//			FrameworkCache.reloadCacheQueue.put("7-"+fr.getForm().getCustomizationId(), System.currentTimeMillis());
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
				FrameworkCache.clearPreloadCache(fr.getScd());
			}
		}
		
		if(fr.getAction()==2 && (fr.getForm().getObjectId()==14)){
			int lookUpId=GenericUtil.uInt((Object)fr.getRequestParams().get("look_up_id"));
			if(lookUpId!=0)UserUtil.broadCastRecordForTemplates((Integer)fr.getScd().get("customizationId"), -lookUpId, "", 2, (Integer)fr.getScd().get("userId"));
		}
		
		
	}
}
