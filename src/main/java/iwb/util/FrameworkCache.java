package iwb.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import iwb.domain.db.M5List;
import iwb.domain.db.W5Approval;
import iwb.domain.db.W5Conversion;
import iwb.domain.db.W5Customization;
import iwb.domain.db.W5DataView;
import iwb.domain.db.W5DbFunc;
import iwb.domain.db.W5Feed;
import iwb.domain.db.W5Form;
import iwb.domain.db.W5Grid;
import iwb.domain.db.W5JobSchedule;
import iwb.domain.db.W5List;
import iwb.domain.db.W5LookUp;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Query;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableAccessConditionSql;
import iwb.domain.db.W5TableChild;
import iwb.domain.db.W5TableParam;
import iwb.domain.db.W5TableTrigger;
import iwb.domain.db.W5Template;
import iwb.domain.db.W5TsMeasurement;
import iwb.domain.db.W5TsPortlet;
import iwb.domain.db.W5Ws;
import iwb.domain.db.W5WsMethod;
import iwb.domain.db.W5WsServer;
import iwb.domain.helper.W5TableRecordHelper;

public class FrameworkCache {

	final public static Map<Integer, Map<Integer,W5LookUp>> wLookUps = new HashMap<Integer, Map<Integer,W5LookUp>>(); //customizationId
	final public static Map<Integer, Map<Integer, W5TsPortlet>> wTsPortlets = new HashMap<Integer, Map<Integer, W5TsPortlet>>();
	final public static Map<Integer, Map<Integer, W5TsMeasurement>> wTsMeasurements = new HashMap<Integer, Map<Integer, W5TsMeasurement>>();
	final public static Map<Integer, W5Query> wQueries = new HashMap<Integer, W5Query>();
	final public static Map<Integer, W5Conversion> wConversions = new HashMap<Integer, W5Conversion>();
	final public static Map<Integer, Map<Integer, W5Grid>> wGrids = new HashMap<Integer, Map<Integer, W5Grid>>();
	final public static Map<Integer, Map<Integer, W5List>> wListViews = new HashMap<Integer, Map<Integer, W5List>>();
	final public static Map<Integer, Map<Integer, M5List>> mListViews = new HashMap<Integer, Map<Integer, M5List>>();
	final public static Map<Integer, Map<Integer, W5DataView>> wDataViews = new HashMap<Integer, Map<Integer, W5DataView>>();
	final public static Map<Integer, Map<Integer, W5Form>> wForms = new HashMap<Integer, Map<Integer, W5Form>>();
	final public static Map<Integer, W5DbFunc> wDbFuncs = new HashMap<Integer, W5DbFunc>();
	final public static Map<Integer, Map<Integer, W5Template>> wTemplates = new HashMap<Integer, Map<Integer, W5Template>>();
	final public static Map<String, String> wExceptions = new HashMap<String, String>();
	final public static Map<Integer, Map<String, String>> appSettings= new HashMap<Integer, Map<String, String>>();
	final public static List<String> publishAppSettings= new ArrayList<String>();
	final public static List<Integer> publishLookUps= new ArrayList<Integer>();
	final public static Map<Integer, Map<Integer, String>> wRoles = new HashMap<Integer, Map<Integer, String>>();
	final public static Map<Integer, Map<Integer, W5Table>> wTables = new HashMap<Integer, Map<Integer, W5Table>>();
	final public static Map<Integer, W5Approval> wApprovals = new HashMap<Integer, W5Approval>();
	final public static Map<Integer, List<W5Feed>> wFeeds = new HashMap<Integer, List<W5Feed>>();
	final public static List<W5JobSchedule> wJobs= new ArrayList<W5JobSchedule>();
	final public static Map<String, Long> reloadCacheQueue = new HashMap<String, Long>();
	final public static Map<Integer, List<W5TableParam>> tableParamListMap = new HashMap<Integer, List<W5TableParam>>();
	final public static Map<Integer, List<W5TableChild>> tableChildListMap = new HashMap<Integer, List<W5TableChild>>();//copy
	final public static Map<Integer, List<W5TableChild>> tableParentListMap = new HashMap<Integer, List<W5TableChild>>();//watch,feed
//	final public static HashMap<String , List<Object> > lastUserAction=new HashMap<String, List<Object>>();
	final public static List<W5Customization> wCustomization = new ArrayList<W5Customization>();
	final public static Map<Integer, W5Customization> wCustomizationMap = new HashMap<Integer, W5Customization>();
	final public static Map<Integer, Map<Integer, List<W5TableTrigger>>> wTableTriggers = new HashMap<Integer, Map<Integer,List<W5TableTrigger>>>();	
	final public static Map<Integer, Integer> wTableFieldMap = new HashMap<Integer, Integer>();
	final public static Set<String> wDevEntityKeys = new HashSet<String>();

	public static List<W5QueryField> cachedOnlineQueryFields = null;
	private static Map<Integer,Map<Integer,Map<Integer,W5TableRecordHelper>>> wCachedObjectMap = new HashMap<Integer,Map<Integer,Map<Integer,W5TableRecordHelper>>>();
	public static Map<Integer,W5TableAccessConditionSql> wAccessConditionSqlMap = new HashMap<Integer,W5TableAccessConditionSql>();
	
	
	final public static Map<String, Date> wRequestUrls= new HashMap<String, Date>();
	final public static Map<String, Map<Integer,Set<String>>> wValidateLookupMap= new HashMap<String, Map<Integer,Set<String>>>();
	
	final public static Map<Integer, Map<String, W5Ws>> wWsClients = new HashMap<Integer, Map<String, W5Ws>>(); //wsId
	final public static Map<Integer, W5WsMethod> wWsMethods = new HashMap<Integer, W5WsMethod>(); //wsId
	final public static Map<Integer, Map<String, W5WsServer>> wWsServers = new HashMap<Integer, Map<String, W5WsServer>>(); //wsId
	final public static Map<String, W5Project> wProjects = new HashMap<String, W5Project>(); //projectUuid

	
	public static W5Customization getCustomization(int customizationId){
		if(wCustomization==null)return null;
		for(W5Customization c:wCustomization)if(c.getCustomizationId()==customizationId)return c;
		return null;
	}
	public static Map<Integer,W5TableRecordHelper> getTableCacheMap(int customizationId, int tableId){
		Map<Integer,Map<Integer,W5TableRecordHelper>> m1 = wCachedObjectMap.get(customizationId);
		if(m1==null){
			m1 = new HashMap<Integer,Map<Integer,W5TableRecordHelper>>();
			wCachedObjectMap.put(customizationId, m1);
		}
		Map<Integer,W5TableRecordHelper> m2 = m1.get(tableId);
		if(m2==null){
			m2 = new HashMap<Integer,W5TableRecordHelper>();
			m1.put(tableId, m2);
		}
		return m2;
	}
	public static void putTableCacheValue(int customizationId, int tableId, int tablePk, W5TableRecordHelper val){
		if(FrameworkSetting.cacheObject)getTableCacheMap(customizationId, tableId).put(tablePk, val);
	}
	public static W5TableRecordHelper getTableCacheValue(int customizationId, int tableId, int tablePk){
		return FrameworkSetting.cacheObject ? getTableCacheMap(customizationId, tableId).get(tablePk) : null;
	}
	
	public static void removeTableCacheValue(int customizationId, int tableId, int tablePk){
		if(FrameworkSetting.cacheObject)getTableCacheMap(customizationId, tableId).remove(tablePk);
	}
//	final static public Map<String, String> dbErrorCodes = new HashMap<String, String>();

	public	static void clearPreloadCache(){
		wQueries.clear();
		wGrids.clear();
		wForms.clear();
		wDbFuncs.clear();
		wTemplates.clear();
		wDataViews.clear();
		wListViews.clear();
	}

	
	public	static int getCustomizationId(Object o){
		if(o==null)return 0;
		if(o instanceof Integer)return (Integer)o;
		if(o instanceof String)return GenericUtil.uInt((String)o);
		if(o instanceof HashMap)return GenericUtil.uInt(((HashMap)o).get("customizationId"));;
		return 0;
	}
	
	public static W5DataView getDataView(int customizationId, int dataViewId) {
		int cusId = getCustomizationId(customizationId);
		if(cusId>0 && wDevEntityKeys.contains("930."+dataViewId))cusId=0;
		if(!wDataViews.containsKey(cusId)){
			wDataViews.put(cusId, new HashMap());
			return null;
		} else
			return wDataViews.get(cusId).get(dataViewId);
	}
	

	public static W5List getListView(int customizationId, int listId) {
		int cusId = getCustomizationId(customizationId);
		if(cusId>0 && wDevEntityKeys.contains("936."+listId))cusId=0;
		if(!wListViews.containsKey(cusId)){
			wListViews.put(cusId, new HashMap());
			return null;
		} else
			return wListViews.get(cusId).get(listId);
	}
	
	public static M5List getMListView(int customizationId, int listId) {
		int cusId = getCustomizationId(customizationId);
		if(cusId>0 && wDevEntityKeys.contains("1345."+listId))cusId=0;
		if(!mListViews.containsKey(cusId)){
			mListViews.put(cusId, new HashMap());
			return null;
		} else
			return mListViews.get(cusId).get(listId);
	}
	
	public static W5Grid getGrid(Object customizationId, int gridId){
		int cusId = getCustomizationId(customizationId);
		if(cusId>0 && wDevEntityKeys.contains("5."+gridId))cusId=0;
		if(wGrids.get(cusId)==null){
			wGrids.put(cusId, new HashMap());
			return null;
		} else
			return wGrids.get(cusId).get(gridId);
	}
	
	public static void addGrid(Object customizationId, W5Grid grid){
		int cusId = getCustomizationId(customizationId);
		int gridId = grid.getGridId();
		if(cusId>0 && wDevEntityKeys.contains("5."+gridId))cusId=0;
		if(wGrids.get(cusId)==null){
			wGrids.put(cusId, new HashMap());
		}
		wGrids.get(cusId).put(gridId, grid);
	}
	
	public static W5Form getForm(Object customizationId, int formId){
		int cusId = getCustomizationId(customizationId);
		if(cusId>0 && wDevEntityKeys.contains("40."+formId))cusId=0;

		if(wForms.get(cusId)==null){
			wForms.put(cusId, new HashMap());
			return null;
		} else
			return wForms.get(cusId).get(formId);
	}
	
	public static void addForm(Object customizationId, W5Form form){
		int cusId = getCustomizationId(customizationId);
		int formId = form.getFormId();
		if(cusId>0 && wDevEntityKeys.contains("40."+formId))cusId=0;

		if(wForms.get(cusId)==null){
			wForms.put(cusId, new HashMap());
		}
		wForms.get(cusId).put(formId, form);
	}
	
	public static W5Template getTemplate(Object customizationId, int templateId){
		int cusId = getCustomizationId(customizationId);
		if(cusId>0 && wDevEntityKeys.contains("63."+templateId))cusId=0;
		if(wTemplates.get(cusId)==null){
			wTemplates.put(cusId, new HashMap());
			return null;
		} else
			return wTemplates.get(cusId).get(templateId);
	}
	
	
	public static int getAppSettingIntValue(Object customizationId, String key){
		Map<String, String> map = appSettings.get(getCustomizationId(customizationId));
		if(map==null)map = appSettings.get(0);
		return GenericUtil.uInt(map.get(key));
	}
	public static int getAppSettingIntValue(Object customizationId, String key, int defaultValue){
		Map<String, String> map = appSettings.get(getCustomizationId(customizationId));
		if(map==null)map = appSettings.get(0);
		String res = map.get(key);
		return res==null ? defaultValue : GenericUtil.uInt(res);
	}
	public static String getAppSettingStringValue(Object customizationId, String key){
		Map<String, String> map = appSettings.get(getCustomizationId(customizationId));
		if(map==null)map = appSettings.get(0);
		return map.get(key);
	}	
	public static String getAppSettingStringValue(Object customizationId, String key, String defaultValue){
		Map<String, String> map = appSettings.get(getCustomizationId(customizationId));
		if(map==null)map = appSettings.get(0);
		String res = map.get(key);
		return res == null ? defaultValue : res;
	}
	public static W5LookUp getLookUp(Object customizationId, int lookUpId){
		int cusId = getCustomizationId(customizationId);
		if(cusId>0 && wDevEntityKeys.contains("13."+lookUpId))cusId=0;

		Map<Integer,W5LookUp> map = wLookUps.get(cusId);
		if(map==null)map = wLookUps.get(0);
		return map.get(lookUpId);
	}
/*	public static W5LookUp getLookUp(int customizationId, int lookUpId){
		Map<Integer,W5LookUp> map = wLookUps.get(customizationId);
		if(map==null)map = wLookUps.get(0);
		return map.get(lookUpId);
	}*/
	
	public static boolean roleAccessControl(Map scd,  int action){ 
		return true;
		//0:view, 1:edit, 2:insert, 3:delete, 11:bulkUpdateFlag; 
		//101:fileViewFlag;102:fileUploadFlag;103:commentMakeFlag;104:bulkEmailFlag; 105:gridReportViewFlag;106:showRelatedEmailFlag;107:lookupManageFlag;108:logViewFlag;109:smsEmailTemplateCrudFlag

	}
	
	public static W5Table getTable(Object customizationId, int tableId){
		int cusId = getCustomizationId(customizationId);
		if(cusId>0 && wDevEntityKeys.contains("15."+tableId))cusId=0;
		W5Customization cus = wCustomizationMap.get(cusId);
		if(cus==null)return null;
		Map<Integer, W5Table> map = wTables.get(cusId);
		if(map==null)map = wTables.get(0);
		return map.get(tableId);
	}



	synchronized private	static void reorganizeFeedList(List<W5Feed> lx){
		if(lx.size()>FrameworkSetting.feedMaxDepth){
			List<W5Feed> lx2 = new ArrayList<W5Feed>(FrameworkSetting.feedMaxDepth+10);
			for(W5Feed f:lx)if(f!=null)lx2.add(f);//aradaki null'lari atiyor
			if(lx2.size()>lx.size()/2){
				lx.clear();
				lx.addAll(lx2.subList(lx2.size() - lx.size()/2, lx2.size()));
			} else {
				lx.clear();
				lx.addAll(lx2);
			}
		}
	}
	public static boolean addFeed(Map scd,  W5Feed feed, boolean publish){
		List<W5Feed> lx = wFeeds.get((Integer)scd.get("customizationId"));
		if(lx==null){
			lx = new ArrayList<W5Feed>(FrameworkSetting.feedMaxDepth+10);
			wFeeds.put((Integer)scd.get("customizationId"), lx);
		}
		int maxDerinlik = FrameworkCache.getAppSettingIntValue(scd, "feed_control_depth");
		for(int qi=lx.size()-1;qi>=0 && maxDerinlik>0;maxDerinlik--,qi--){//bir onceki feedlerle iliskisi belirleniyor
			W5Feed lfeed =lx.get(qi); 
			if(lfeed==null)continue;
			if(lfeed.getTableId()==feed.getTableId() && lfeed.getTablePk()==feed.getTablePk() && lfeed.getFeedTip()==feed.getFeedTip() && feed.get_showFeedTip()==lfeed.get_showFeedTip() &&
					(feed.get_showFeedTip()!=1 || (feed.getDetailTableId()==lfeed.getDetailTableId()))){//edit haricinde birsey veya edit ise ayni tablo uzerinde detay seviyesinde
				lx.set(qi,null);
				feed.set_relatedFeedMap(new HashMap<Integer,W5Feed>());
				feed.get_relatedFeedMap().put(lfeed.getFeedId(),lfeed);
				if(lfeed.get_relatedFeedMap()!=null)feed.get_relatedFeedMap().putAll(lfeed.get_relatedFeedMap());
				break;
			}
		}
		if(lx.size()>FrameworkSetting.feedMaxDepth)reorganizeFeedList(lx);
		lx.add(feed);
		if(publish && FrameworkSetting.liveSyncRecord){
			UserUtil.syncAfterPostForm((Integer)scd.get("customizationId"), 671, "", (Integer)scd.get("userId"), "-", (short)2);;
		}
		return true;
	}
//	final public static Map<DeferredResult<List>, Integer> wLongPollRequests = new HashMap<DeferredResult<List>, Integer>(); //customizationId

	public static boolean requestUrlsControl(String remoteIpAdr){
		Calendar cal = Calendar.getInstance(); // creates calendar
	    cal.setTime(new Date()); 
	    cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
	    Date dt = cal.getTime();	    
			
	    //1 saati dolduran elemanlar temizleniyor 
		for (Object key : wRequestUrls.keySet()) {
			Date dt2 = wRequestUrls.get(key);
			if (dt2.compareTo(dt)>0){
				wRequestUrls.remove(key);
			}			
		}
		
		//server ip listede varmı?
		for (Object key : wRequestUrls.keySet()) {					
			if (key.toString().equals(remoteIpAdr)){
				return false;
			}						
		}		
		return true;
	}

	public static W5WsServer getWsServer(String serviceName){
		for(int cus:wWsServers.keySet()){
			Map<String, W5WsServer> wssMap = wWsServers.get(cus);
			for(String sn:wssMap.keySet())if(serviceName.equals(sn))return wssMap.get(sn);
			
		}
		return null;
	}
	public static W5Ws getWsClient(String serviceName){
		for(int cus:wWsClients.keySet()){
			Map<String, W5Ws> wssMap = wWsClients.get(cus);
			for(String sn:wssMap.keySet())if(serviceName.equals(sn))return wssMap.get(sn);
			
		}
		return null;
	}
	public static W5TsPortlet getTsPortlet(Map<String, Object> customizationId, int porletId) {
		int cid = getCustomizationId(customizationId);
		if(cid!=0){
			W5Customization cus = wCustomizationMap.get(cid);
		}
		if(wTsPortlets.get(cid)==null){
			wTsPortlets.put(cid, new HashMap());
			return null;
		} else
			return wTsPortlets.get(cid).get(porletId);
	}
	public static W5TsMeasurement getTsMeasurement(Map<String, Object> customizationId, int measurementId) {
		int cid = getCustomizationId(customizationId);
		if(cid!=0){
			W5Customization cus = wCustomizationMap.get(cid);
		}
		if(wTsMeasurements.get(cid)==null){
			wTsMeasurements.put(cid, new HashMap());
			return null;
		} else
			return wTsMeasurements.get(cid).get(measurementId);
	}
}
