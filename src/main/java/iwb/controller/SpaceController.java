/*


 * Created on 07.Nis.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package iwb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import iwb.adapter.ui.ViewAdapter;
import iwb.adapter.ui.ViewMobileAdapter2;
import iwb.adapter.ui.extjs.ExtJs3_4;
import iwb.adapter.ui.f7.F7;
import iwb.adapter.ui.react.React16;
import iwb.adapter.ui.vue.Vue2;
import iwb.adapter.ui.webix.Webix3_3;
import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.domain.db.Log5UserAction;
import iwb.domain.db.W5BIGraphDashboard;
import iwb.domain.db.W5FileAttachment;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Query;
import iwb.domain.helper.W5FormCellHelper;
import iwb.domain.helper.W5QueuedActionHelper;
import iwb.domain.helper.W5QueuedPushMessageHelper;
import iwb.domain.helper.W5ReportCellHelper;
import iwb.domain.result.M5ListResult;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5PageResult;
import iwb.domain.result.W5QueryResult;
import iwb.domain.result.W5TableRecordInfoResult;
import iwb.domain.result.W5TutorialResult;
import iwb.exception.IWBException;
import iwb.report.RptExcelRenderer;
import iwb.report.RptPdfRenderer;
import iwb.service.FrameworkService;
import iwb.timer.Action2Execute;
import iwb.util.GenericUtil;
import iwb.util.UserUtil;

@Controller
@RequestMapping("/space")
public class SpaceController implements InitializingBean {
	private static Logger logger = Logger.getLogger(SpaceController.class);

	@Autowired
	private FrameworkService engine;

	@Autowired
	private TaskExecutor taskExecutor;

	private ViewAdapter ext3_4;
	private	ViewAdapter	webix3_3;
	private	ViewAdapter	react16;
	private	ViewAdapter	vue2;
	private ViewMobileAdapter2 f7;

	@Override
	public void afterPropertiesSet() throws Exception {
		ext3_4 = new ExtJs3_4();
		webix3_3 = new Webix3_3();
		f7 = new F7();
		react16 = new React16();
		vue2 = new Vue2();
	}


	private ViewAdapter getViewAdapter(Map<String, Object> scd, HttpServletRequest request, ViewAdapter defaultRenderer){
		if(GenericUtil.uInt(scd.get("mobile"))!=0)return ext3_4;
		if(request!=null){
			String renderer = request.getParameter("_renderer");
			if(renderer!=null && renderer.equals("ext3_4"))return ext3_4;
			if(renderer!=null && renderer.startsWith("webix"))return webix3_3;
			if(renderer!=null && renderer.equals("react16"))return react16;
			if(renderer!=null && renderer.equals("vue2"))return vue2;
		}
		if(scd!=null){
			String renderer = (String)scd.get("_renderer");
			if(renderer!=null && renderer.equals("ext3_4"))return ext3_4;
			if(renderer!=null && renderer.startsWith("webix"))return webix3_3;			
			if(renderer!=null && renderer.equals("react16"))return react16;
			if(renderer!=null && renderer.equals("vue2"))return vue2;
		}
		return defaultRenderer;
	}
	
	private ViewAdapter getViewAdapter(Map<String, Object> scd, HttpServletRequest request){
		return getViewAdapter(scd, request, ext3_4);
	}

	@RequestMapping("/*/dyn-res/*")
	public ModelAndView hndDynResource(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndJasperReport"); 
    	Map<String, Object> scd = UserUtil.getScd(request, "scd-dev", true);
    	String uri = request.getRequestURI();
    	if(uri.endsWith(".css")){
    		uri = uri.substring(uri.lastIndexOf('/')+1);
    		uri = uri.substring(0, uri.length()-4);
        	String css = FrameworkCache.getPageCss(scd, GenericUtil.uInt(uri));
        	if(css!=null){
        		response.setContentType("text/css; charset=UTF-8");
        		response.getWriter().write(css);
        	}
    	}
//    	int pageId =  ;

		response.getWriter().close();
    	return null;
		
	}

	@RequestMapping("/*/ajaxFormCellCode")
	public void hndAjaxFormCellCode(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int formCellId = GenericUtil.uInt(request, "_formCellId");
		logger.info("hndAjaxFormCellCode(" + formCellId + ")");
		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		Map m = engine.getFormCellCode(scd, GenericUtil.getParameterMap(request), formCellId, 1);
		// m.put("success", true);
		response.setContentType("application/json");
		response.getWriter().write(GenericUtil.fromMapToJsonString2(m));
		response.getWriter().close();
	}

	@RequestMapping("/*/ajaxChangeChatStatus")
	public void hndAjaxChangeChatStatus(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxChangeChatStatus");
		response.setContentType("application/json");
		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		int chatStatusTip = GenericUtil.uInt(request, "chatStatusTip");
		response.getWriter().write("{\"success\":" + UserUtil.updateChatStatus(scd, chatStatusTip) + "}");
		response.getWriter().close();
	}

	@RequestMapping("/*/ajaxQueryData4Stat")
	public void hndAjaxQueryData4Stat(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int gridId = GenericUtil.uInt(request, "_gid");
		if(gridId==0)gridId = -GenericUtil.uInt(request, "_qid");
		logger.info("hndAjaxQueryData4Stat(" + gridId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		response.setContentType("application/json");
		Map m = engine.executeQuery4Stat(scd, gridId, GenericUtil.getParameterMap(request));
		response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	@RequestMapping("/*/ajaxQueryData4StatTree")
	public void hndAjaxQueryData4StatTree(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int gridId = GenericUtil.uInt(request, "_gid");
		logger.info("hndAjaxQueryData4StatTree(" + gridId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		response.setContentType("application/json");
		Map m = engine.executeQuery4StatTree(scd, gridId, GenericUtil.getParameterMap(request));
		response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();
	}
	
	@RequestMapping("/*/ajaxQueryData")
	public void hndAjaxQueryData(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int queryId = GenericUtil.uInt(request, "_qid");
//		JSONObject jo = null;
		Map<String,String> requestMap = GenericUtil.getParameterMap(request);
/*		if(GenericUtil.safeEquals(request.getContentType(),"application/json")){
			JSONObject jo = HttpUtil.getJson(request);
			if(jo.has("_qid"))queryId = jo.getInt("_qid");
			requestMap.putAll(GenericUtil.fromJSONObjectToMap(jo));
		} */
		logger.info("hndAjaxQueryData(" + queryId + ")");
		Map<String, Object> scd = null;
		HttpSession session = request.getSession(false);
		String projectId = UserUtil.getProjectId(request,"space/");
		W5Project po = FrameworkCache.getProject(projectId,"Wrong Project");
		String scdKey="space-"+projectId;
		if ((queryId == 1 || queryId == 824) && (session == null || session.getAttribute(scdKey) == null
				|| ((HashMap<String, String>) session.getAttribute(scdKey)).size() == 0)) { // select
																							// role
			if (session == null) {
				response.getWriter().write("{\"success\":false,\"error\":\"no_session\"}");
				return;
			}
			scd = new HashMap<String, Object>();
			scd.put("locale", session.getAttribute("locale"));
			scd.put("userId", session.getAttribute("userId"));
			if (GenericUtil.uInt(session.getAttribute("mobile"))!=0)
				scd.put("mobile", session.getAttribute("mobile"));
			scd.put("customizationId", session.getAttribute("customizationId"));
		} else {
			if (queryId == 142) { // online users
				scd = UserUtil.getScd4PAppSpace(request);
				W5QueryResult qr = new W5QueryResult(142);
				W5Query q = new W5Query();
				q.setQueryTip((short) 0);
				qr.setQuery(q);
				qr.setScd(scd);
				qr.setErrorMap(new HashMap());
				qr.setNewQueryFields(FrameworkCache.cachedOnlineQueryFields);
				List<Object[]> lou = UserUtil.listOnlineUsers(scd);
				if (FrameworkSetting.chatShowAllUsers) {
					Map<Integer, Object[]> slou = new HashMap();
					slou.put((Integer) scd.get("userId"), new Object[] { scd.get("userId") });
					for (Object[] o : lou)
						slou.put(GenericUtil.uInt(o[0]), o);
					W5QueryResult allUsers = engine.executeQuery(scd, queryId, requestMap);
					for (Object[] o : allUsers.getData()) {
						String msg = (String) o[6];
						if (msg != null && msg.length() > 18) {
							o[3] = msg.substring(0, 19); // last_msg_date_time
							if (msg.length() > 19)
								o[6] = msg.substring(20);// msg
							else
								o[6] = null;
						} else {
							o[6] = null;
							o[3] = null;
						}

						int u = GenericUtil.uInt(o[0]);

						Object[] o2 = slou.get(u);
						if (o2 == null)
							lou.add(o);
						else if (u != (Integer) scd.get("userId")) {
							if (o2.length > 3)
								o2[3] = o[3];
							if (o2.length > 6)
								o2[6] = o[6];
							if (o2.length > 7)
								o2[7] = o[7];
						}
					}
				}
				qr.setData(lou);
				response.setContentType("application/json");
				response.getWriter().write(getViewAdapter(scd, request).serializeQueryData(qr).toString());
				response.getWriter().close();
				return;
			} else
				scd = UserUtil.getScd4PAppSpace(request);// TODO not auto
		}

		ViewAdapter va = getViewAdapter(scd, request);
		if(va instanceof Webix3_3){
			for(String s:requestMap.keySet())if(s.startsWith("sort[") && s.endsWith("]")){
				requestMap.put("sort", s.substring(5,  s.length()-1));
				requestMap.put("dir",requestMap.get(s));
				break;
			}
			
		}
		W5QueryResult queryResult = engine.executeQuery(scd, queryId, requestMap);

		response.setContentType("application/json");
		response.getWriter().write(va.serializeQueryData(queryResult).toString());
		response.getWriter().close();

	}

	
	@RequestMapping("/*/ajaxApproveRecord")
	public void hndAjaxApproveRecord(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxApproveRecord");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		if (FrameworkCache.getAppSettingIntValue(scd, "approval_flag") == 0) {
			response.setContentType("application/json");
			response.getWriter().write("{\"success\":false}");
			return;
		}

		String[] app_rec_ids = request.getParameterValues("_arids");
		Map<String, Object> b = null;
		int approvalAction = GenericUtil.uInt(request, "_aa"); // aprovalAction
		Map<String, String> parameterMap = GenericUtil.getParameterMap(request);

		if (app_rec_ids == null) {
			int approvalRecordId = GenericUtil.uInt(request, "_arid");
			b = engine.approveRecord(scd, approvalRecordId, approvalAction, parameterMap);
		} else {
			String[] version_ids = request.getParameterValues("_avnos");
			for (int i = 0; i < app_rec_ids.length; i++) {
				int approvalRecordId = GenericUtil.uInt(app_rec_ids[i]);
				parameterMap.put("_avno", "" + version_ids[i]);
				parameterMap.put("_arid", "" + approvalRecordId); // dbfunc
																	// varsa
																	// parametre
																	// olarak
																	// kullanılıyor
				b = engine.approveRecord(scd, approvalRecordId, approvalAction, parameterMap);
			}
		}

		response.setContentType("application/json");
		response.getWriter().write("{\"success\":\"" + b.get("status") + "\"");
		if (b.get("fileHash") != null)
			response.getWriter()
					.write(",\"fileHash\":\"" + b.get("fileHash") + "\",\"fileId\":\"" + b.get("fileId") + "\"");
		response.getWriter().write("}");
		response.getWriter().close();
	}

	@RequestMapping("/*/ajaxLiveSync")
	public void hndAjaxLiveSync(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("ajaxLiveSync");
		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		response.setContentType("application/json");
		response.getWriter().write("{\"success\":" + FrameworkSetting.liveSyncRecord + "}");
		response.getWriter().close();

		UserUtil.liveSyncAction(scd, GenericUtil.getParameterMap(request));
	}

	@RequestMapping("/*/ajaxGetTabNotifications")
	public void hndAjaxGetTabNotifications(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxGetTabNotifications");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		String webPageId = request.getParameter(".w");
		String tabId = request.getParameter(".t");
		int userId = (Integer) scd.get("userId");
		String projectId = (String) scd.get("projectId");
		String s = GenericUtil.fromMapToJsonString2Recursive(UserUtil.syncGetTabNotifications(projectId, userId,
				(String) scd.get("sessionId"), webPageId, tabId));
		response.setContentType("application/json");
		response.getWriter().write(s);
		response.getWriter().close();

	}


	@RequestMapping("/*/ajaxPostChatMsg")
	public void hndAjaxPostChatMsg(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		logger.info("hndAjaxPostChatMsg");
		response.setContentType("application/json");
		String msg = request.getParameter("msg");
		int userId = GenericUtil.uInt(request, "receiver_user_id");
		if (userId == 0 || GenericUtil.isEmpty(msg)) {
			response.getWriter().write("{\"success\":false}");
			return;
		}
		Map<String, String> m = GenericUtil.getParameterMap(request);
		String s = m.get("msg");
		if (GenericUtil.uInt(scd.get("mobile")) == 2)
			s = GenericUtil.encodeGetParamsToUTF8(s);// hack for android mobile app
		m.put("msg", s.contains("\\") ? s.replace('\\', '/') : s);
		W5FormResult formResult = engine.postForm4Table(scd, 1703, 2, m, "");

		response.setContentType("application/json");
		if (!GenericUtil.isEmpty(formResult.getErrorMap())) {
			response.getWriter().write("{\"success\":false}");
			response.getWriter().close();
			return;
		}

		Object chatId = formResult.getOutputFields().get("chat_id");
		List<W5QueuedPushMessageHelper> l = UserUtil.publishUserChatMsg(
				(Integer) scd.get("userId"), userId, msg, chatId);
		response.getWriter().write("{\"success\":true, \"delivered_cnt\":1, \"chatId\":"+chatId+"}");
		response.getWriter().close();
		
//		if(FrameworkSetting.mq)UserUtil.mqPublishUserChatMsg(scd, userId, msg, chatId);
		/*
		 * if(!GenericUtil.isEmpty(l)){ executeQueuedMobilePushMessage eqf = new
		 * executeQueuedMobilePushMessage(l); taskExecutor.execute(eqf); }
		 */
	}

	@RequestMapping("/*/ajaxNotifyChatMsgRead")
	public void hndAjaxNotifyChatMsgRead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		logger.info("hndAjaxNotifyChatMsgRead");
		int userId = GenericUtil.uInt(request, "u");
		int msgId = GenericUtil.uInt(request, "m");
		if (userId == 0 || msgId == 0) {
			response.getWriter().write("{\"success\":false}");
			return;
		}
		int countLeft = engine.notifyChatMsgRead(scd, userId, msgId);

		response.setContentType("application/json");
		response.getWriter().write("{\"success\":true, \"countLeft\":" + countLeft + "}");
		response.getWriter().close();

		if (countLeft == 0) {
			UserUtil.publishUserChatMsgRead(scd, userId, msgId);
		}
	}

	@RequestMapping("/*/ajaxPostForm")
	public void hndAjaxPostForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int formId = GenericUtil.uInt(request, "_fid");
		logger.info("hndAjaxPostForm(" + formId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		int action = GenericUtil.uInt(request, "a");
		Map<String,String> requestMap = GenericUtil.getParameterMap(request);
/*		if(GenericUtil.safeEquals(request.getContentType(),"application/json")){
			JSONObject jo = HttpUtil.getJson(request);
			requestMap.putAll(GenericUtil.fromJSONObjectToMap(jo));
		}*/
		W5FormResult formResult = engine.postForm4Table(scd, formId, action, requestMap, "");

		response.setContentType("application/json");
		response.getWriter().write(getViewAdapter(scd, request).serializePostForm(formResult).toString());
		response.getWriter().close();
		
		if (formResult.getQueueActionList() != null)
			for (W5QueuedActionHelper o : formResult.getQueueActionList()) {
				Action2Execute eqf = new Action2Execute(o, scd);
				taskExecutor.execute(eqf);
			}

		
		/*
		 * if(!GenericUtil.isEmpty(formResult.getQueuedPushMessageList())){
		 * executeQueuedMobilePushMessage eqf = new
		 * executeQueuedMobilePushMessage(formResult.getQueuedPushMessageList())
		 * ; taskExecutor.execute(eqf); }
		 */
		if (formResult.getErrorMap().isEmpty()){
			UserUtil.syncAfterPostFormAll(formResult.getListSyncAfterPostHelper());
//			UserUtil.mqSyncAfterPostFormAll(formResult.getScd4PAppSpace(), formResult.getListSyncAfterPostHelper());
		}


	}

	@RequestMapping("/*/ajaxPing")
	public void hndAjaxPing(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxPing");
		HttpSession session = request.getSession(false);
		String projectId = UserUtil.getProjectId(request,"space/");
		W5Project po = FrameworkCache.getProject(projectId,"Wrong Project");
		String scdKey="space-"+projectId;
		boolean notSessionFlag = session == null || session.getAttribute(scdKey) == null
				|| ((HashMap<String, String>) session.getAttribute(scdKey)).size() == 0;
		response.setContentType("application/json");
		Map cm = null;
		if(FrameworkSetting.chat && !notSessionFlag && GenericUtil.uInt(request, "c")!=0){
			cm = engine.getUserNotReadChatMap((Map)session.getAttribute(scdKey));
		}
		if(GenericUtil.uInt(request, "d")==0 || notSessionFlag)
			response.getWriter().write("{\"success\":true,\"session\":" + !notSessionFlag + (cm!=null ? ", \"newMsgCnt\":"+GenericUtil.fromMapToJsonString2Recursive(cm):"") + "}");
		else {
			Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
			response.getWriter().write("{\"success\":true,\"session\":" + GenericUtil.fromMapToJsonString2Recursive(scd) + (cm!=null ? ", \"newMsgCnt\":"+GenericUtil.fromMapToJsonString2Recursive(cm):"") + "}");
		}
		response.getWriter().close();
	}

	@RequestMapping("/*/ajaxPostConversionGridMulti")
	public void hndAjaxPostConversionGridMulti(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxPostConversionGridMulti");
		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		response.setContentType("application/json");
		int conversionCount = GenericUtil.uInt(request, "_ccnt");
		if (conversionCount > 0) {
			W5FormResult formResult = engine.postBulkConversionMulti(scd, conversionCount,
					GenericUtil.getParameterMap(request));

			response.getWriter().write(getViewAdapter(scd, request).serializePostForm(formResult).toString());
			response.getWriter().close();

			for (W5QueuedActionHelper o : formResult.getQueueActionList()) {
				Action2Execute eqf = new Action2Execute(o, scd);
				taskExecutor.execute(eqf);
			}
			
			if (formResult.getErrorMap().isEmpty()){
				UserUtil.syncAfterPostFormAll(formResult.getListSyncAfterPostHelper());
//				UserUtil.mqSyncAfterPostFormAll(formResult.getScd4PAppSpace(), formResult.getListSyncAfterPostHelper());
			}
		} else
			response.getWriter().write("{\"success\":false}");
	}

	@RequestMapping("/*/ajaxPostEditGrid")
	public void hndAjaxPostEditGrid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxPostEditGrid");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		response.setContentType("application/json");
		int dirtyCount = GenericUtil.uInt(request, "_cnt");
		int formId = GenericUtil.uInt(request, "_fid");
		if (formId > 0) {
			W5FormResult formResult = engine.postEditGrid4Table(scd, formId, dirtyCount,
					GenericUtil.getParameterMap(request), "", new HashSet<String>());
			response.getWriter().write(getViewAdapter(scd, request).serializePostForm(formResult).toString());
			response.getWriter().close();

			for (W5QueuedActionHelper o : formResult.getQueueActionList()) {
				Action2Execute eqf = new Action2Execute(o, scd);
				taskExecutor.execute(eqf);
			}

			if (formResult.getErrorMap().isEmpty()){
				UserUtil.syncAfterPostFormAll(formResult.getListSyncAfterPostHelper());
//				UserUtil.mqSyncAfterPostFormAll(formResult.getScd4PAppSpace(), formResult.getListSyncAfterPostHelper());
				
			}

		} else if (formId < 0) { // negatifse direk -dbFuncId
			// int dbFuncId= GenericUtil.uInt(request, "_did");
			W5GlobalFuncResult dbFuncResult = engine.postEditGridGlobalFunc(scd, -formId, dirtyCount,
					GenericUtil.getParameterMap(request), "");
			response.getWriter().write(getViewAdapter(scd, request).serializeGlobalFunc(dbFuncResult).toString());
		} else {
			int conversionId = GenericUtil.uInt(request, "_cnvId");
			if (conversionId > 0) {
				W5FormResult formResult = engine.postBulkConversion(scd, conversionId, dirtyCount,
						GenericUtil.getParameterMap(request), "");
				response.getWriter().write(getViewAdapter(scd, request).serializePostForm(formResult).toString());
				response.getWriter().close();

				for (W5QueuedActionHelper o : formResult.getQueueActionList()) {
					Action2Execute eqf = new Action2Execute(o, scd);
					taskExecutor.execute(eqf);
				}
				
				if (formResult.getErrorMap().isEmpty()){
					UserUtil.syncAfterPostFormAll(formResult.getListSyncAfterPostHelper());
//					UserUtil.mqSyncAfterPostFormAll(formResult.getScd4PAppSpace(), formResult.getListSyncAfterPostHelper());
				}

			} else {
				response.getWriter().write("{\"success\":false}");
			}
		}
	}

	@RequestMapping("/*/ajaxBookmarkForm")
	public void hndAjaxBookmarkForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxBookmarkForm");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		int formId = GenericUtil.uInt(request, "_fid");
		int action = GenericUtil.uInt(request, "a");
		W5FormResult formResult = engine.bookmarkForm(scd, formId, action, GenericUtil.getParameterMap(request));

		response.setContentType("application/json");
		response.getWriter().write("{\"success\":true,\"id\":" + formResult.getPkFields().get("id") + "}");

	}

	@RequestMapping("/*/ajaxExecDbFunc")
	public void hndAjaxExecDbFunc(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxExecDbFunc");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		int dbFuncId = GenericUtil.uInt(request, "_did"); // +:dbFuncId,
															// -:formId
		if (dbFuncId == 0) {
			dbFuncId = -GenericUtil.uInt(request, "_fid"); // +:dbFuncId,
															// -:formId
		}
		W5GlobalFuncResult dbFuncResult = engine.executeFunc(scd, dbFuncId, GenericUtil.getParameterMap(request),
				(short) 1);

		response.setContentType("application/json");
		response.getWriter().write(getViewAdapter(scd, request).serializeGlobalFunc(dbFuncResult).toString());
		response.getWriter().close();

	}

	

	@RequestMapping("/*/ajaxGetFormSimple")
	public void hndGetFormSimple(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int formId = GenericUtil.uInt(request, "_fid");
		logger.info("hndGetFormSimple(" + formId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		int action = GenericUtil.uInt(request, "a");
		W5FormResult formResult = engine.getFormResult(scd, formId, action, GenericUtil.getParameterMap(request));

		response.setContentType("application/json");
		response.getWriter().write(getViewAdapter(scd, request).serializeGetFormSimple(formResult).toString());
		response.getWriter().close();

	}

	@RequestMapping("/*/ajaxReloadFormCell")
	public void hndAjaxReloadFormCell(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxReloadFormCell");
		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		int fcId = GenericUtil.uInt(request, "_fcid");
		String webPageId = request.getParameter(".w");
		String tabId = request.getParameter(".t");
		W5FormCellHelper rc = engine.reloadFormCell(scd, fcId, webPageId, tabId);
		response.setContentType("application/json");
		response.getWriter()
				.write(ext3_4
						.serializeFormCellStore(rc, (Integer) scd.get("customizationId"), (String) scd.get("locale"))
						.toString());
		response.getWriter().close();
	}

	@RequestMapping("/*/ajaxGetFormCellCodeDetail")
	public void hndAjaxGetFormCellCodeDetail(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxGetFormCellCodeDetail");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		int fccdId = GenericUtil.uInt(request, "_fccdid");
		String result = engine.getFormCellCodeDetail(scd, GenericUtil.getParameterMap(request), fccdId);
		response.setContentType("application/json");
		response.getWriter().write("{\"success\":true,\"result\":\"" + result + "\"}");
		response.getWriter().close();

	}
/*
	@RequestMapping("/ * /ajaxFeed")
	public void hndAjaxFeed(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxFeed");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		response.setContentType("application/json");

		int platestFeedIndex = request.getParameter("_lfi") == null ? -1 : GenericUtil.uInt(request, "_lfi");
		int pfeedTip = request.getParameter("_ft") == null ? -1 : GenericUtil.uInt(request, "_ft");
		int proleId = request.getParameter("_ri") == null ? -1 : GenericUtil.uInt(request, "_ri");
		int puserId = request.getParameter("_ui") == null ? -1 : GenericUtil.uInt(request, "_ui");
		int pmoduleId = request.getParameter("_mi") == null ? -1 : GenericUtil.uInt(request, "_mi");
		// response.setContentType("application/json");
		response.getWriter()
				.write(getViewAdapter(scd, request).serializeFeeds(scd, platestFeedIndex, pfeedTip, proleId, puserId, pmoduleId).toString());
		response.getWriter().close();
		if (FrameworkSetting.liveSyncRecord) {
			UserUtil.getTableGridFormCellCachedKeys((String) scd.get("projectId"),
					 671, (Integer) scd.get("userId"), (String) scd.get("sessionId"),
					request.getParameter(".w"), request.getParameter(".t"),  919, null, true);
		}
	}
	*/

	@RequestMapping("/*/ajaxTsPortletData")
	public void hndAjaxTsPortletData(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxTsPortletData");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		response.setContentType("application/json");

		int porletId = GenericUtil.uInt(request, "_pid");
		String s = engine.getTsDashResult(scd, GenericUtil.getParameterMap(request), porletId);
		response.getWriter().write(s);
		response.getWriter().close();
	}


	@RequestMapping("/*/showForm")
	public void hndShowForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int formId = GenericUtil.uInt(request, "_fid");
		logger.info("hndShowForm(" + formId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		int action = GenericUtil.uInt(request, "a");
		W5FormResult formResult = engine.getFormResult(scd, formId, action, GenericUtil.getParameterMap(request));

		response.setContentType("application/json");
		response.getWriter().write(getViewAdapter(scd, request).serializeShowForm(formResult).toString());
		response.getWriter().close();

	}
	
	@RequestMapping("/*/showMForm")
	public void hndShowMForm(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int formId = GenericUtil.uInt(request, "_fid");
		logger.info("hndShowMForm(" + formId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		int action = GenericUtil.uInt(request, "a");
		W5FormResult formResult = engine.getFormResult(scd, formId, action, GenericUtil.getParameterMap(request));

		response.getWriter().write(f7.serializeGetForm(formResult).toString());
		response.getWriter().close();

	}

	@RequestMapping("/*/showTutorial")
	public void hndShowTutorial(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int tutorialId = GenericUtil.uInt(request, "_ttid");
		logger.info("showTutorial(" + tutorialId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		W5TutorialResult tutorialResult = engine.getTutorialResult(scd, tutorialId,
				GenericUtil.getParameterMap(request));

		response.setContentType("application/json");
		if (GenericUtil.uInt(scd.get("mobile")) != 0) {
			response.getWriter().write("{\"success\":false}");
		} else {
			response.getWriter().write(getViewAdapter(scd, request).serializeShowTutorial(tutorialResult).toString());
		}
		response.getWriter().close();

	}

	@RequestMapping("/*/ajaxLogoutUser")
	public void hndAjaxLogoutUser(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxLogoutUser");
		HttpSession session = request.getSession(false);
		response.setContentType("application/json");
		String projectId = UserUtil.getProjectId(request,"space/");
		W5Project po = FrameworkCache.getProject(projectId,"Wrong Project");
		String scdKey="space-"+projectId;
		if (session != null) {
			Map<String, Object> scd = (Map) session.getAttribute(scdKey);
			if (scd != null) {
				UserUtil.onlineUserLogout((Integer) scd.get("userId"), scd.containsKey("mobile") ? (String)scd.get("mobileDeviceId") : session.getId());
				if(scd.containsKey("mobile")){
					Map parameterMap = new HashMap(); parameterMap.put("pmobile_device_id", scd.get("mobileDeviceId"));parameterMap.put("pactive_flag", 0);
					engine.executeFunc(scd, 673, parameterMap, (short)4);
				}
			}
			session.removeAttribute(scdKey);
		}
		if(GenericUtil.uInt(request, "d")!=0)throw new IWBException("session","No Session",0,null, "No valid session", null);
		else response.getWriter().write("{\"success\":true}");
	}
	@RequestMapping("/*/ajaxAuthenticateUser")
	public void hndAjaxAuthenticateUser(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxAuthenticateUser(" + request.getParameter("userName") + ")");
		String projectId = UserUtil.getProjectId(request,"space/");
		W5Project po = FrameworkCache.getProject(projectId,"Wrong Project");
//		if(po.getSessionQueryId()==0)response.sendRedirect("main.htm"); //TODO


		Map<String, String> requestParams = GenericUtil.getParameterMap(request);
		requestParams.put("_remote_ip", request.getRemoteAddr());
	/*	if (request.getSession(false) != null && request.getSession(false).getAttribute("securityWordId") != null)
			requestParams.put("securityWordId", request.getSession(false).getAttribute("securityWordId").toString());
*/
		String scdKey="space-"+projectId;
		if (request.getSession(false) != null) {
			request.getSession(false).removeAttribute(scdKey);
		}
		
		Map<String, Object> scd = new HashMap<String, Object>();
		scd.put("projectId", projectId);
		W5GlobalFuncResult result = engine.executeFunc(scd, po.getAuthenticationFuncId(), requestParams, (short) 7); // user Authenticate DbFunc:1

		/*
		 * 4 success 5 errorMsg 6 userId 7 expireFlag 8 smsFlag 9 roleCount
		 */
		boolean success = GenericUtil.uInt(result.getResultMap().get("success")) != 0;
		String errorMsg = result.getResultMap().get("errorMsg");
		int userId = GenericUtil.uInt(result.getResultMap().get("userId"));
		String xlocale = GenericUtil.uStrNvl(request.getParameter("locale"), FrameworkCache.getAppSettingStringValue(0, "locale"));
//		int deviceType = GenericUtil.uInt(request.getParameter("_mobile"));
		if (!success)errorMsg = LocaleMsgCache.get2(0, xlocale, errorMsg);
		int userRoleId = GenericUtil.uInt(requestParams.get("userRoleId"));
		response.setContentType("application/json");
		scd = null;
		if (success) { // basarili simdi sira diger islerde
			scd = engine.userRoleSelect4App(po, userId, userRoleId, null);

			if (scd == null) {
				if (FrameworkSetting.debug)logger.info("empty scd");
				response.getWriter().write("{\"success\":false"); // error
			} else {
				if(GenericUtil.uInt(scd.get("renderer"))>1)scd.put("_renderer",GenericUtil.getRenderer(scd.get("renderer")));
				HttpSession session = request.getSession(true);
				session.removeAttribute(scdKey);
				session.setAttribute(scdKey, scd);
				scd.put("locale", xlocale);
				scd.put("customizationId", po.getCustomizationId());
				scd.put("ocustomizationId", po.getCustomizationId());
				scd.put("projectId", po.getProjectUuid());scd.put("projectName", po.getDsc());
				scd.put("renderer", po.getUiWebFrontendTip());
				scd.put("_renderer", GenericUtil.getRenderer(po.getUiWebFrontendTip()));
				scd.put("mainTemplateId", po.getUiMainTemplateId());

				scd.put("sessionId", session.getId());
				scd.put("path", "../");

//				UserUtil.onlineUserLogin(scd, request.getRemoteAddr(), session.getId(), (short) 0, request.getParameter(".w"));
				response.getWriter().write("{\"success\":true,\"session\":" + GenericUtil.fromMapToJsonString2(scd)); // hersey duzgun
			}

			response.getWriter().write("}");
		} else {
			response.getWriter().write("{\"success\":false,\"errorMsg\":\"" + errorMsg + "\"}");
		}
		response.getWriter().close();
	}
	@RequestMapping("/*/login.htm") //getScd4PAppSpace
	public void hndLoginPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		logger.info("hndLoginPage");
		String projectId = UserUtil.getProjectId(request,"space/");
		W5Project po = FrameworkCache.getProject(projectId,"Not a valid Project");
		if(po.getSessionQueryId()==0)
			response.sendRedirect("main.htm");
			
		HttpSession session = request.getSession(false);
		if (session != null) {
			String scdKey = "space-"+projectId;
			Map<String, Object> scd = (Map<String, Object>) session.getAttribute(scdKey);
			if (scd != null)UserUtil.onlineUserLogout( (Integer) scd.get("userId"), (String) scd.get("sessionId"));
			session.removeAttribute(scdKey);
		}


		Map<String, Object> scd = new HashMap();
		scd.put("userId", 1);
		scd.put("roleId", 1);
		scd.put("customizationId", po.getCustomizationId());
		scd.put("projectId", projectId);scd.put("projectName", po.getDsc());
		scd.put("locale", "en");
		scd.put("path", "../");

		W5PageResult pageResult = engine.getPageResult(scd, po.getUiLoginTemplateId()==0?1:po.getUiLoginTemplateId(), GenericUtil.getParameterMap(request));
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().write(getViewAdapter(scd, request).serializeTemplate(pageResult).toString());
		response.getWriter().close();

	}


	@RequestMapping("/*/main.htm")
	public void hndMainPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndMainPage");
		
		Map<String, Object> scd = null;
		try{
			scd = UserUtil.getScd4PAppSpace(request);
		} catch(Exception e){
			scd = null;
		}
		if(scd==null){
			response.sendRedirect("login.htm");
			return;
		}

		if (scd.get("mobile") != null)
			scd.remove("mobile");

		W5Project po = FrameworkCache.getProject(scd);
		
		W5PageResult pageResult = engine.getPageResult(scd, po.getUiMainTemplateId(), GenericUtil.getParameterMap(request));
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().write(getViewAdapter(scd, request).serializeTemplate(pageResult).toString());
		response.getWriter().close();

	}
	
	
	@RequestMapping("/*/showPage")
	public void hndShowPage(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int templateId = GenericUtil.uInt(request, "_tid");
		logger.info("hndShowPage(" + templateId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		W5PageResult pageResult = engine.getPageResult(scd, templateId, GenericUtil.getParameterMap(request));
		// if(pageResult.getTemplate().getTemplateTip()!=2 && templateId!=218 &&
		// templateId!=611 && templateId!=551 && templateId!=566){ //TODO:cok
		// amele
		// throw new PromisException("security","Template",0,null, "Wrong
		// Template Tip (must be page)", null);
		// }

		if(pageResult.getPage().getTemplateTip()!=0)
			response.setContentType("application/json");

		response.getWriter().write(getViewAdapter(scd, request).serializeTemplate(pageResult).toString());
		response.getWriter().close();

	}
	

	@RequestMapping("/*/showMList")
	public void hndShowMList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int listId = GenericUtil.uInt(request, "_lid");
		logger.info("hndShowMList(" + listId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		M5ListResult listResult = engine.getMListResult(scd, listId, GenericUtil.getParameterMap(request));
		// if(pageResult.getTemplate().getTemplateTip()!=2 && templateId!=218 &&
		// templateId!=611 && templateId!=551 && templateId!=566){ //TODO:cok
		// amele
		// throw new PromisException("security","Template",0,null, "Wrong
		// Template Tip (must be page)", null);
		// }

		response.setContentType("application/json");
		response.getWriter().write(f7.serializeList(listResult).toString());
		response.getWriter().close();

	}

	
	@RequestMapping("/*/grd/*")
	public ModelAndView hndGridReport(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndGridReport");
		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		int gridId = GenericUtil.uInt(request, "_gid");
		String gridColumns = request.getParameter("_columns");

		List<W5ReportCellHelper> list = engine.getGridReportResult(scd, gridId, gridColumns,
				GenericUtil.getParameterMap(request));
		if (list != null) {
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("report", list);
			m.put("scd", scd);
			ModelAndView result = null;
			if (request.getRequestURI().indexOf(".xls") != -1 || "xls".equals(request.getParameter("_fmt")))
				result = new ModelAndView(new RptExcelRenderer(), m);
			else if (request.getRequestURI().indexOf(".pdf") != -1)
				result = new ModelAndView(new RptPdfRenderer(engine.getCustomizationLogoFilePath(scd)), m);
			else if (request.getRequestURI().indexOf(".csv") != -1) {
				response.setContentType("application/octet-stream");
				response.getWriter().print(GenericUtil.report2csv(list));
			} else if (request.getRequestURI().indexOf(".txt") != -1) {
				response.setContentType("application/octet-stream");
				response.getWriter().print(GenericUtil.report2text(list));
			}
			return result;

		} else {
			response.getWriter().write("Hata");
			response.getWriter().close();

			return null;
		}

	}


	@RequestMapping("/*/dl/*")
	public void hndFileDownload(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndFileDownload");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		int fileAttachmentId = GenericUtil.uInt(request, "_fai");
		String customizationId = String.valueOf((scd.get("customizationId") == null) ? 0 : scd.get("customizationId"));
		String local_path = FrameworkCache.getAppSettingStringValue(scd, "file_local_path");
		String file_path = "";
		if (fileAttachmentId == -1000) { // default company logo
			file_path = local_path + "/0/jasper/iworkbetter.png";
			response.setContentType("image/png");
		} else {
			W5FileAttachment fa = engine.loadFile(scd, fileAttachmentId);
			if (fa == null) {
				throw new IWBException("validation", "File Attachment", fileAttachmentId, null,
						"Invalid Id: " + fileAttachmentId, null);
			}
			ServletOutputStream out = response.getOutputStream();
			file_path = local_path + "/" + customizationId + "/attachment/" + fa.getSystemFileName();

			if (fa.getFileTypeId() == null || fa.getFileTypeId() != -999)
				response.setContentType("application/octet-stream");
			else {
				long expiry = new Date().getTime() + FrameworkSetting.cacheAge * 1000;
				response.setContentType("image/"
						+ fa.getOrijinalFileName().substring(fa.getOrijinalFileName().lastIndexOf(".") + 1));
				response.setDateHeader("Expires", expiry);
				response.setHeader("Cache-Control", "max-age=" + FrameworkSetting.cacheAge);
			}
		}
		ServletOutputStream out = null;
		InputStream stream = null;
		try {
			stream = new FileInputStream(file_path);
			out = response.getOutputStream();
			// write the file to the file specified
			int bytesRead = 0;
			byte[] buffer = new byte[8192];

			while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			if (FrameworkCache.getAppSettingIntValue(scd, "log_download_flag") != 0) {
				Log5UserAction ua = new Log5UserAction(scd);
				ua.setActionTip((short) 1);
				ua.setTableId(44);
				ua.setTablePk(fileAttachmentId);
				ua.setUserIp(request.getRemoteAddr());
				engine.saveObject(ua);

			}
		} catch (Exception e) {
			if (FrameworkSetting.debug)
				e.printStackTrace();
			// bus.logException(e.getMessage(),GenericUtil.uInt(scd.get("customizationId")),GenericUtil.uInt(scd.get("userRoleId")));
			throw new IWBException("generic", "File Attacment", fileAttachmentId, "Unknown Exception",
					e.getMessage(), e.getCause());
		} finally {
			if (out != null)
				out.close();
			if (stream != null)
				stream.close();
		}
	}

	@RequestMapping("/*/sf/*")
	public void hndShowFile(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int fileAttachmentId = GenericUtil.uInt(request, "_fai");
		logger.info("hndShowFile(" + fileAttachmentId + ")");
		Map<String, Object> scd = null;
		if (fileAttachmentId == 0) {
			scd = UserUtil.getScd4PAppSpace(request);
			String spi = request.getRequestURI();
			String startStr = "/space/" + scd.get("projectId") + "/sf/pic";
			if (spi!=null && spi.startsWith(startStr) && spi.contains(".")) {
				spi = spi.substring(startStr.length());
				spi = spi.substring(0, spi.indexOf("."));
				fileAttachmentId = -GenericUtil.uInt(spi);
			}
			if (fileAttachmentId == 0)
				fileAttachmentId = -GenericUtil.uInt(request, "userId");
		}
		InputStream stream = null;
		String filePath = null;

		W5FileAttachment fa = engine.loadFile(scd, fileAttachmentId);
		if (fa == null) { // bulunamamis TODO
			throw new IWBException("validation", "File Attachment", fileAttachmentId, null,
					"Wrong Id: " + fileAttachmentId, null);
		}

		if (fa.getFileAttachmentId() == 1 || fa.getFileAttachmentId() == 2) { // bayan
																				// veya
																				// erkek
																				// resmi
			filePath = fa.getFileAttachmentId() == 2 ? AppController.womanPicPath : AppController.manPicPath;
		} else {
			if (scd == null)scd = UserUtil.getScd4PAppSpace(request);
			String customizationId = String
					.valueOf((scd.get("customizationId") == null) ? 0 : scd.get("customizationId"));
			String file_path = FrameworkCache.getAppSettingStringValue(scd, "file_local_path");
			filePath = file_path + "/" + customizationId + "/attachment/" + fa.getSystemFileName();
		}
		if (request.getParameter("_ct") == null)
			response.setContentType("image/jpeg");

		ServletOutputStream out = response.getOutputStream();
		try {
			/*
			 * if(fileAttachmentId<0)try { stream = new
			 * FileInputStream(filePath); } catch(Exception e0){ stream = new
			 * FileInputStream(request.getRealPath("/images/custom/wv.png")); }
			 * else stream = new FileInputStream(filePath);
			 */

			if (stream == null)
				try {
					stream = new FileInputStream(filePath);
				} catch (Exception e0) {
					stream = new FileInputStream(AppController.brokenPicPath);
				}

			// write the file to the file specified
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
//			if (FrameworkSetting.debug)e.printStackTrace();
			// bus.logException(e.getMessage(),GenericUtil.uInt(scd.get("customizationId")),GenericUtil.uInt(scd.get("userRoleId")));
			throw new IWBException("generic", "File Attachment", fileAttachmentId, "Unknown Exception",e.getMessage(), e);
		} finally {
			if(out!=null)out.close();
			if(stream!=null)stream.close();
		}
	}


	@RequestMapping("/*/showFormByQuery")
	public void hndShowFormByQuery(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndShowFormByQuery");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		int formId = GenericUtil.uInt(request, "_fid");
		int queryId = GenericUtil.uInt(request, "_qid");
		W5FormResult formResult = engine.getFormResultByQuery(scd, formId, queryId,
				GenericUtil.getParameterMap(request));

		response.setContentType("application/json");
		response.getWriter().write(getViewAdapter(scd, request).serializeShowForm(formResult).toString());
		response.getWriter().close();

	}




	@RequestMapping("/*/getTableRecordInfo")
	public void hndGetTableRecordInfo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndGetTableRecordInfo");
		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		int tableId = GenericUtil.uInt(request, "_tb_id");
		int tablePk = GenericUtil.uInt(request, "_tb_pk");
		W5TableRecordInfoResult r = engine.getTableRecordInfo(scd, tableId, tablePk);
		response.setContentType("application/json");
		response.getWriter().write(r != null ? getViewAdapter(scd, request).serializeTableRecordInfo(r).toString() : "{\"success\":false}");
		response.getWriter().close();
	}
	
	@RequestMapping("/*/getGraphDashboards")
	public void hndGetGraphDashboards(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndGetGraphDashboards");
		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		List<W5BIGraphDashboard> l = engine.getGraphDashboards(scd);
		if(GenericUtil.isEmpty(l)){
			response.getWriter().write("{\"success\":true,\"data\":[]}");
		} else {
			StringBuilder s = new StringBuilder();
			s.append("{\"success\":true,\"data\":[");
			boolean b = false;
			for(W5BIGraphDashboard gd:l){
				if(b)s.append(","); else b=true;
				s.append(f7.serializeGraphDashboard(gd, scd));
			}
			s.append("]}");

			response.getWriter().write(s.toString());
			
		}
		response.getWriter().close();
	}




	@RequestMapping(value = "/*/multiupload.form", method = RequestMethod.POST)
	@ResponseBody
	public String multiFileUpload(@RequestParam("files") MultipartFile[] files,
			@RequestParam("customizationId") Integer customizationId, @RequestParam("userId") Integer userId,
			@RequestParam("table_pk") String table_pk, @RequestParam("table_id") Integer table_id,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("multiFileUpload");
		// Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		String path = FrameworkCache.getAppSettingStringValue(customizationId, "file_local_path") + File.separator
				+ customizationId + File.separator + "attachment";

		File dirPath = new File(path);
		if (!dirPath.exists()) {
			if (!dirPath.mkdirs())
				return "{ \"success\":false, \"msg\":\"wrong file path: " + path + "\"}";
		}
		List<W5FileAttachment> lfa = new ArrayList<W5FileAttachment>();
		if (files.length > 0) {
			for (MultipartFile f : files)
				try {
					long fileId = new Date().getTime();
					W5FileAttachment fa = new W5FileAttachment();
					response.setContentType("application/json; charset=UTF-8");
					fa.setSystemFileName(fileId + "." + GenericUtil.strUTF2En(f.getOriginalFilename()));
					f.transferTo(new File(path + File.separator + fa.getSystemFileName()));
					int totalBytesRead = (int) f.getSize();
					fa.setCustomizationId(customizationId);
					fa.setOrijinalFileName(f.getOriginalFilename());
					fa.setTableId(table_id);
					fa.setTablePk(table_pk);
					fa.setTabOrder(Short.parseShort("1"));
					fa.setUploadUserId(userId);
					fa.setFileSize(totalBytesRead);
					fa.setActiveFlag((short) 1);
					lfa.add(fa);
					engine.saveObject(fa);
					String webPageId = request.getParameter(".w");
					if (!GenericUtil.isEmpty(webPageId))
						try {
							Map m = new HashMap();
							m.put(".w", webPageId);
							m.put(".pk", table_id + "-" + table_pk);
							m.put(".a", "11");
							m.put(".e", "2");
							Map scd = new HashMap();
							scd.put("customizationId", customizationId);
							scd.put("userId", userId);
							scd.put("sessionId", request.getSession(false).getId());
							UserUtil.liveSyncAction(scd, m);// (customizationId,
															// table_id+"-"+table_pk,
															// userId,
															// webPageId,
															// false);
						} catch (Exception e) {
							if (FrameworkSetting.debug)
								e.printStackTrace();
						}
					return "{ \"success\": true, \"fileId\": " + fa.getFileAttachmentId() + ", \"fileName\": '"
							+ GenericUtil.strUTF2En(GenericUtil.stringToJS(f.getOriginalFilename())) + "'}";

					// out.write("{success: true, fileId: "+
					// fa.getFileAttachmentId() +", fileName:
					// '"+f.getOriginalFilename()+"'}");
				} catch (Exception e) {
					if (FrameworkSetting.debug)
						e.printStackTrace();
					return "{ \"success\": false }";
				} finally {
					try {
						if (f.getInputStream() != null)
							f.getInputStream().close();
					} catch (Exception e2) {
						if (FrameworkSetting.debug)
							e2.printStackTrace();
					}
					// out.close();
				}
			// bus.saveAllObjectz(lfa);

		}
		return "{\"success\": false }";
	}

	@RequestMapping(value = "/*/upload.form", method = RequestMethod.POST)
	@ResponseBody
	public String singleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("table_pk") String table_pk,
			@RequestParam("table_id") Integer table_id, @RequestParam("profilePictureFlag") Integer profilePictureFlag,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("singleFileUpload");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		Map<String, String> requestParams = GenericUtil.getParameterMap(request);

		String path = FrameworkCache.getAppSettingStringValue(scd.get("customizationId"), "file_local_path")
				+ File.separator + scd.get("customizationId") + File.separator + "attachment";

		File dirPath = new File(path);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}

		long fileId = new Date().getTime();
		int totalBytesRead = (int) file.getSize();

		W5FileAttachment fa = new W5FileAttachment(scd);
		boolean ppicture = (GenericUtil.uInt(scd.get("customizationId")) == 0 || FrameworkCache
						.getAppSettingIntValue(scd.get("customizationId"), "profile_picture_flag") != 0)
				&& profilePictureFlag != null && profilePictureFlag != 0;
		try {
			if (ppicture) {
				int maxFileSize = FrameworkCache.getAppSettingIntValue(0, "profile_picture_max_file_size", 51200);
				if (maxFileSize < totalBytesRead)
					return "{ \"success\": false , \"msg\":\"" + LocaleMsgCache.get2(scd, "max_file_size") + " = "
							+ Math.round(maxFileSize / 1024) + " KB\"}";
				fa.setFileTypeId(-999);// profile picture upload etti
			} else if (table_id == 338) {
				int maxFileSize = FrameworkCache.getAppSettingIntValue(0, "company_picture_max_file_size", 512000);
				if (maxFileSize < totalBytesRead)
					return "{ \"success\": false , \"msg\":\"" + LocaleMsgCache.get2(scd, "max_file_size") + " = "
							+ Math.round(maxFileSize / 1024) + " KB\"}";
				fa.setFileTypeId(-998);// company picture upload etti
			}
			fa.setSystemFileName(fileId + "." + GenericUtil.strUTF2En(file.getOriginalFilename()));
			file.transferTo(new File(path + File.separator + fa.getSystemFileName()));
			fa.setOrijinalFileName(file.getOriginalFilename());
			fa.setTableId(table_id);
			fa.setTablePk(table_pk);
			fa.setTabOrder((short) 1);
			fa.setFileSize(totalBytesRead);
			fa.setActiveFlag((short) 1);
			try {
				if(!ppicture)if (GenericUtil.uStrNvl(requestParams.get("file_type_id"), "") != null) {
					fa.setFileTypeId(Integer.parseInt(GenericUtil.uStrNvl(requestParams.get("file_type_id"), "")));
				}
				if (GenericUtil.uStrNvl(requestParams.get("file_comment"), "") != null) {
					fa.setFileComment(GenericUtil.uStrNvl(requestParams.get("file_comment"), ""));
				}
			} catch (Exception e) {

			}
			engine.saveObject(fa);
			String webPageId = request.getParameter(".w");
			if (!GenericUtil.isEmpty(webPageId)) {
				Map m = new HashMap();
				m.put(".w", webPageId);
				m.put(".pk", table_id + "-" + table_pk);
				m.put(".a", "11");
				m.put(".e", "2");
				UserUtil.liveSyncAction(scd, m);// (customizationId,
												// table_id+"-"+table_pk,
												// userId, webPageId, false);

			}
			return "{ \"success\": true, \"fileId\": " + fa.getFileAttachmentId() + ", \"fileName\": \""
					+ GenericUtil.stringToJS(file.getOriginalFilename()) + "\", \"fileUrl\": \"" + "sf/"
					+ fa.getSystemFileName() + "?_fai=" + fa.getFileAttachmentId() + "\"}";
		} catch (Exception e) {
			if (true || FrameworkSetting.debug)
				e.printStackTrace();
			return "{ \"success\": false }";
		} /*
			 * finally { // transferTo yüzünden zaten hep exceptiona düşüyor.
			 * try {
			 * if(file.getInputStream()!=null)file.getInputStream().close(); }
			 * catch (Exception e2) {
			 * if(PromisSetting.debug)e2.printStackTrace(); } //
			 * response.getWriter().close(); }
			 */

	}

	@RequestMapping(value = "/upload2.form", method = RequestMethod.POST)
	@ResponseBody
	public String singleFileUpload4Webix(@RequestParam("upload") MultipartFile file, @RequestParam("table_pk") String table_pk,
			@RequestParam("table_id") Integer table_id, @RequestParam("profilePictureFlag") Integer profilePictureFlag,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("singleFileUpload4Webix");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		Map<String, String> requestParams = GenericUtil.getParameterMap(request);

		String path = FrameworkCache.getAppSettingStringValue(scd.get("customizationId"), "file_local_path")
				+ File.separator + scd.get("customizationId") + File.separator + "attachment";

		File dirPath = new File(path);
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}

		long fileId = new Date().getTime();
		int totalBytesRead = (int) file.getSize();

		W5FileAttachment fa = new W5FileAttachment();
		boolean ppicture = (GenericUtil.uInt(scd.get("customizationId")) == 0 || FrameworkCache
						.getAppSettingIntValue(scd.get("customizationId"), "profile_picture_flag") != 0)
				&& profilePictureFlag != null && profilePictureFlag != 0;
		try {
			// fa.setFileComment(bean.getFile_comment());
			fa.setCustomizationId(GenericUtil.uInt(scd.get("customizationId")));
			if (ppicture) {
				int maxFileSize = FrameworkCache.getAppSettingIntValue(0, "profile_picture_max_file_size", 51200);
				if (maxFileSize < totalBytesRead)
					return "{ \"success\": false , \"msg\":\"" + LocaleMsgCache.get2(scd, "max_file_size") + " = "
							+ Math.round(maxFileSize / 1024) + " KB\"}";
				fa.setFileTypeId(-999);// profile picture upload etti
			} else if (table_id == 338) {
				int maxFileSize = FrameworkCache.getAppSettingIntValue(0, "company_picture_max_file_size", 512000);
				if (maxFileSize < totalBytesRead)
					return "{ \"success\": false , \"msg\":\"" + LocaleMsgCache.get2(scd, "max_file_size") + " = "
							+ Math.round(maxFileSize / 1024) + " KB\"}";
				fa.setFileTypeId(-998);// company picture upload etti
			}
			fa.setSystemFileName(fileId + "." + GenericUtil.strUTF2En(file.getOriginalFilename()));
			file.transferTo(new File(path + File.separator + fa.getSystemFileName()));
			fa.setOrijinalFileName(file.getOriginalFilename());
			fa.setTableId(table_id);
			fa.setTablePk(table_pk);
			fa.setTabOrder((short) 1);
			fa.setUploadUserId(GenericUtil.uInt(scd.get("userId")));
			fa.setFileSize(totalBytesRead);
			fa.setActiveFlag((short) 1);
			try {
				if(!ppicture)if (GenericUtil.uStrNvl(requestParams.get("file_type_id"), "") != null) {
					fa.setFileTypeId(Integer.parseInt(GenericUtil.uStrNvl(requestParams.get("file_type_id"), "")));
				}
				if (GenericUtil.uStrNvl(requestParams.get("file_comment"), "") != null) {
					fa.setFileComment(GenericUtil.uStrNvl(requestParams.get("file_comment"), ""));
				}

			} catch (Exception e) {

			}
			engine.saveObject(fa);
			String webPageId = request.getParameter(".w");
			if (!GenericUtil.isEmpty(webPageId)) {
				Map m = new HashMap();
				m.put(".w", webPageId);
				m.put(".pk", table_id + "-" + table_pk);
				m.put(".a", "11");
				m.put(".e", "2");
				UserUtil.liveSyncAction(scd, m);// (customizationId,
												// table_id+"-"+table_pk,
												// userId, webPageId, false);

			}
			return "{ \"success\": true, \"fileId\": " + fa.getFileAttachmentId() + ", \"fileName\": \""
					+ GenericUtil.stringToJS(file.getOriginalFilename()) + "\", \"fileUrl\": \"" + "sf/"
					+ fa.getSystemFileName() + "?_fai=" + fa.getFileAttachmentId() + "\"}";
		} catch (Exception e) {
			if (true || FrameworkSetting.debug)
				e.printStackTrace();
			return "{ \"success\": false }";
		} /*
			 * finally { // transferTo yüzünden zaten hep exceptiona düşüyor.
			 * try {
			 * if(file.getInputStream()!=null)file.getInputStream().close(); }
			 * catch (Exception e2) {
			 * if(PromisSetting.debug)e2.printStackTrace(); } //
			 * response.getWriter().close(); }
			 */

	}
	



	@RequestMapping("/*/ajaxSendFormSmsMail")
	public void hndAjaxSendFormSmsMail(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxSendFormSmsMail");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		response.setContentType("application/json");
		int smsMailId = GenericUtil.uInt(request, "_fsmid");
		Map result = engine.sendFormSmsMail(scd, smsMailId, GenericUtil.getParameterMap(request));
		response.getWriter().write(GenericUtil.fromMapToJsonString(result));
		response.getWriter().close();

	}

	
	@RequestMapping("/*/ajaxCallWs")
	public void hndAjaxCallWs(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxCallWs"); 
	    Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
	    
		Map m =engine.REST(scd, request.getParameter("serviceName"), GenericUtil.getParameterMap(request));
		response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		response.getWriter().close();		
	}


	@RequestMapping("/*/ajaxQueryData4Debug")
	public void hndAjaxQueryData4Debug(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndAjaxQueryData4Debug"); 
		
    	Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);
		int roleId =(Integer)scd.get("roleId");
		if(roleId!=0){
			throw new IWBException("security","Developer",0,null, "You Have to Be Developer TO Run this", null);
		}

		int queryId= GenericUtil.uInt(request, "_qid");

		Object o = engine.executeQuery4Debug(scd, queryId, GenericUtil.getParameterMap(request));
		
		response.setContentType("application/json");
		if(o instanceof W5QueryResult)
			response.getWriter().write(getViewAdapter(scd, request).serializeQueryData((W5QueryResult)o).toString());
		else {
			Map m = (Map)o;//new HashMap();
			m.put("success", true);
//			m.put("data", queryResult.getData());
//			Map m2 = new HashMap();m2.put("startRow", 0);m2.put("fetchCount", queryResult.getData().size());m2.put("totalCount", queryResult.getData().size());
//			m.put("browseInfo", m2);
	//		m.put("sql", queryResult.getExecutedSql());
			response.getWriter().write(GenericUtil.fromMapToJsonString2Recursive(m));
		}
		response.getWriter().close();

	}
	
	@RequestMapping("/*/ajaxQueryData4Pivot")
	public void hndAjaxQueryData4Pivot(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int tableId = GenericUtil.uInt(request, "_tid");
		logger.info("hndAjaxQueryData4Pivot(" + tableId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		response.setContentType("application/json");
		response.getWriter().write(GenericUtil.fromListToJsonString2Recursive(engine.executeQuery4Pivot(scd, tableId, GenericUtil.getParameterMap(request))));
		response.getWriter().close();
	}
	
	@RequestMapping("/*/ajaxQueryData4DataList")
	public void hndAjaxQueryData4DataList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int tableId = GenericUtil.uInt(request, "_tid");
		logger.info("hndAjaxQueryData4DataList(" + tableId + ")");

		Map<String, Object> scd = UserUtil.getScd4PAppSpace(request);

		response.setContentType("application/json");
		response.getWriter().write(GenericUtil.fromListToJsonString2Recursive(engine.executeQuery4DataList(scd, tableId, GenericUtil.getParameterMap(request))));
		response.getWriter().close();
	}
	
	
}