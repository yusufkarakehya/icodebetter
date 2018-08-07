package iwb.custom.trigger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.Log5Notification;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableParam;
import iwb.domain.db.W5VcsObject;
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
			if(FrameworkSetting.cloud && !GenericUtil.isEmpty(sql) && (Integer)formResult.getScd().get("customizationId")>1 && DBUtil.checkTenantSQLSecurity(sql)) {
				throw new IWBException("security","SQL", 0, null, "Suspicious Command! Download the platform and feel free to run all the commands ;)", null);
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
		Map scd = fr.getScd();
		String projectId = scd!=null ? (String)scd.get("projectId"):null;
		switch(fr.getFormId()){
	
		
		case	551://comment
			if(fr.getErrorMap().isEmpty() && fr.getAction()==2){
				Object[] userIds = dao.listObjectCommentAndAttachUsers(scd, fr.getRequestParams());
				int tableId = GenericUtil.uInt(fr.getRequestParams().get("table_id"));
				int tablePk = GenericUtil.uInt(fr.getRequestParams().get("table_pk"));
				int sessionUserId=(Integer)scd.get("userId");
				if(userIds!=null)for(Object userId:userIds){
					String tmpStr = fr.getRequestParams().get("dsc");
					if(tmpStr!=null){
						tmpStr= "<b>"+UserUtil.getUserName(sessionUserId)+"</b>: "+tmpStr;
						if(tmpStr.length()>100)tmpStr=tmpStr.substring(0,97)+"...";
					}
					Log5Notification n = new Log5Notification(scd, (Integer)userId, (short)1,  tableId,  tablePk, sessionUserId, null, 1,tmpStr); 
					dao.saveObject(n);
					n.set_tableRecordList(dao.findRecordParentRecords(scd,n.getTableId(),n.getTablePk(),0, true));
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
    	msg = LocaleMsgCache.get2(scd, "reload_cache_manually");
		if(fr.getErrorMap().isEmpty() && fr.getForm()!=null)switch(fr.getForm().getObjectId()){
		case	1277: //user_related_project
			if(fr.getAction()==2){
				int userId = GenericUtil.uInt(fr.getRequestParams().get("user_id"));
				if(userId>0){
					UserUtil.addProjectUser((String)scd.get("projectId"), userId);
				}
			}
			break;
		case	1407://project
			if((fr.getAction()==1 || fr.getAction()==3) && scd.containsKey("ocustomizationId") && GenericUtil.uInt(scd.get("ocustomizationId"))!=GenericUtil.uInt(scd.get("customizationId"))){
				throw new IWBException("security","Project", 0, null, "Forbidden Command. Can not manipulate a project on another tenant.", null);
			}
			switch(fr.getAction()){
			case	5://clone
			case	2://insert
				String newProjectId = fr.getOutputFields().get("project_uuid").toString();
				int customizationId = GenericUtil.uInt(scd.get("ocustomizationId"));
				String schema = "c"+GenericUtil.lPad(customizationId+"", 5, '0')+"_"+newProjectId.replace('-', '_');
				//validate from vcs server
				dao.executeUpdateSQLQuery("update iwb.w5_project set rdbms_schema=?, vcs_flag=1, vcs_url=?, vcs_user_name=?, vcs_password=?, customization_id=? where project_uuid=?", schema, FrameworkCache.getAppSettingStringValue(0, "vcs_url_new_project","http://81.214.24.77:8084/app/"), scd.get("userName"), "1", customizationId, newProjectId);
				dao.executeUpdateSQLQuery("create schema "+schema + " AUTHORIZATION iwb");
				if(fr.getAction()==5){ //clone
					Map<String, Object> newScd = new HashMap();
					newScd.putAll(scd);newScd.put("projectId", fr.getRequestParams().get("tproject_uuid"));
					dao.copyProject(scd, newProjectId, customizationId);
				} else {//insert
					int userTip = GenericUtil.getGlobalNextval("iwb.seq_user_tip", projectId, 0, customizationId);
					dao.executeUpdateSQLQuery("insert into iwb.w5_user_tip(user_tip, dsc, customization_id, project_uuid, web_frontend_tip, default_main_template_id) values (?,?,?, ?, 5, 2307)", userTip, "Role Group 1", customizationId, newProjectId);
					Map<String, Object> newScd = new HashMap();
					newScd.putAll(scd);newScd.put("projectId", newProjectId);
					dao.saveObject(new W5VcsObject(newScd, 369, userTip));
				}
				FrameworkCache.addProject((W5Project)dao.find("from W5Project t where t.projectUuid=?", newProjectId).get(0));
				FrameworkSetting.projectSystemStatus.put(newProjectId,0);
				break;
			case	3://delete all metadata
				String delProjectId = fr.getRequestParams().get("tproject_uuid").toLowerCase();
				W5Project po = FrameworkCache.getProject(delProjectId);
				if(po.getCustomizationId()==GenericUtil.uInt(scd.get("ocustomizationId")) && GenericUtil.uInt(dao.executeSQLQuery("select count(1) from iwb.w5_project x where x.customization_id=?", po.getCustomizationId()).get(0))>1){
					dao.deleteProjectMetadata(delProjectId);
					dao.executeUpdateSQLQuery("DROP SCHEMA IF EXISTS "+po.getRdbmsSchema()+" CASCADE");					
				}
			}
			break;
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
				FrameworkCache.clearPreloadCache(scd);
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
				FrameworkCache.clearPreloadCache(scd);
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
				FrameworkCache.clearPreloadCache(scd);
			}
		}
		
		if(fr.getAction()==2 && (fr.getForm().getObjectId()==14)){
			int lookUpId=GenericUtil.uInt((Object)fr.getRequestParams().get("look_up_id"));
			if(lookUpId!=0)UserUtil.broadCastRecordForTemplates(projectId, -lookUpId, "", 2, (Integer)scd.get("userId"));
		}
		
		
	}
}
