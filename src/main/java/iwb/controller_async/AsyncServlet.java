package iwb.controller_async;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import iwb.controller.AppServlet;
import iwb.domain.helper.W5DeferredResult;
import iwb.exception.PromisException;
import iwb.util.FrameworkSetting;
import iwb.util.GenericUtil;
import iwb.util.UserUtil;

@Controller
@RequestMapping("/async")
public class AsyncServlet {
	private static Logger logger = Logger.getLogger(AppServlet.class);


	@RequestMapping("/ajaxNotifications")
	@ResponseBody
	public W5DeferredResult hndAjaxNotifications(HttpServletRequest request,
			HttpServletResponse response) {
//		if(PromisSetting.debug)logger.info("getNotifications - LongPolling");		
		Map<String, Object> scd = null;
		try {
			scd = UserUtil.getScd(request, "scd-dev", false);
		} catch(PromisException e){
			try {
				response.getWriter().write(e.toJsonString(null));
				response.getWriter().close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		}
		final Integer customizationId = (Integer)scd.get("customizationId");
		final Integer userId = (Integer)scd.get("userId");
		final String webPageId = request.getParameter(".w");
		final String webPageId2 = GenericUtil.uInt(scd.get("mobile"))!=0 ? (String)scd.get("mobileDeviceId") : request.getParameter(".w");
		if(!webPageId.equals(webPageId2)){
			throw new PromisException("bu ne lan", "oho", 0, null, null, null);
		}
		
		final String activeTabId = request.getParameter(".at");
		final W5DeferredResult deferredResult = new W5DeferredResult(customizationId, userId, webPageId, FrameworkSetting.asyncTimeout*1000L, Collections.emptyList());
		
		String rpid = request.getParameter(".p");
		if(rpid!=null && !GenericUtil.safeEquals(rpid, scd.get("projectId"))){
		//	deferredResult.setResult("{\"success\":true, \"changeProjectId\":\""+scd.get("projectId")+"\"}"); //TODO
		}
		
		 /*	
	    if(UserUtil.wLongPollRequests.containsKey(userId)) {
	    	UserUtil.wLongPollRequests.get(userId).add(deferredResult);
	    } else {
	    	UserUtil.wLongPollRequests.put(userId, new ArrayList<W5DeferredResult>());
	    	UserUtil.wLongPollRequests.get(userId).add(deferredResult);
	    }
	 
	   deferredResult.onCompletion(new Runnable() {
	        @Override
	        public void run() {
	        	UserUtil.wLongPollRequests.get(userId).remove(deferredResult);
	        }
	    });*/
	    
	    deferredResult.onTimeout(new Runnable() {
	        @Override
	        public void run() {
	        	deferredResult.setResult("{\"success\":true}");
	        }
	    });

	    Object o = UserUtil.addDeferredResult(customizationId, userId, request.getSession().getId(), webPageId, activeTabId, scd, deferredResult);
	    
	    if(FrameworkSetting.liveSyncRecord && !GenericUtil.isEmpty(webPageId)){
	    	int	cnt =  GenericUtil.uInt(request, ".c");
	    	long now = System.currentTimeMillis();
	    	for(int i=0;i<cnt;i++){
	    		String	s = request.getParameter(".s"+i);
	    		if(!GenericUtil.isEmpty(s)){
	    			String[] sa = s.split(";");
	    			String key = sa[0];
	    			String tabId = sa[1];
	    			short syncTip = sa.length>2 ? GenericUtil.uShort(sa[2]) : 0; //0:yok, 1:var, 2:hybrid
	    			
	    			UserUtil.syncRecordEditMap( customizationId,  key, userId, webPageId, tabId,  now,  syncTip);
	    			
					
	    		}
	    	}
	    	
	    }
	    return deferredResult;
	}
}
