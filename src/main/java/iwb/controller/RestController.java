package iwb.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//import iwb.adapter.soap.SoapAdapter;
//import iwb.adapter.soap.impl.AxisSoap1_4;
import iwb.adapter.ui.ViewAdapter;
import iwb.adapter.ui.extjs.ExtJs3_4;
import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5GlobalFunc;
import iwb.domain.db.W5GlobalFuncParam;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5QueryParam;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableParam;
import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.domain.db.W5WsServerMethodParam;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5QueryResult;
import iwb.exception.IWBException;
import iwb.service.FrameworkService;
import iwb.util.GenericUtil;
import iwb.util.HttpUtil;
import iwb.util.UserUtil;

@Controller
@RequestMapping("/rest")
public class RestController implements InitializingBean {
	private static Logger logger = Logger.getLogger(RestController.class);

	private ViewAdapter ext3_4 = new ExtJs3_4();
	
	@Autowired
	private FrameworkService engine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

	@RequestMapping("/*/*/*")
	public void hndREST( //project/service/method
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException{
		request.setCharacterEncoding( "UTF-8" );
		response.setCharacterEncoding( "UTF-8" );
		try {
			String[] u = request.getRequestURI().replace('/', ',').split(",");
			String token = (String)request.getParameter("tokenKey");
			String projectId=u[2]; 
			String serviceName=u[3];
			String methodName=u[4]; 
			if(methodName.equals("login")){
				Map requestParams = GenericUtil.getParameterMap(request);
				requestParams.put("_remote_ip", request.getRemoteAddr());
				requestParams.put("_mobile", ""+GenericUtil.uInt(requestParams, "deviceType", 0));
				String xlocale = GenericUtil.uStrNvl(request.getParameter("locale"),FrameworkCache.getAppSettingStringValue(0, "locale"));
				W5GlobalFuncResult result = engine.executeFunc(new HashMap(), 1, requestParams, (short) 7); // user Authenticate DbFunc:1
				W5GlobalFuncResult dfr = new W5GlobalFuncResult(-1);dfr.setResultMap(new HashMap());dfr.setErrorMap(new HashMap());
				List<W5GlobalFuncParam> arl = new ArrayList();
				dfr.setGlobalFunc(new W5GlobalFunc());dfr.getGlobalFunc().set_dbFuncParamList(arl);
				arl.add(new W5GlobalFuncParam("tokenKey"));arl.add(new W5GlobalFuncParam("errorMsg"));
//				W5WsServerMethod wsm = wss.get_methods().get(0);
				// 4 success 5 errorMsg 6 userId 7 expireFlag 8 smsFlag 9 roleCount
				boolean success = GenericUtil.uInt(result.getResultMap().get("success")) != 0;
				boolean expireFlag = GenericUtil.uInt(result.getResultMap().get("expireFlag")) != 0;
				if (!success || expireFlag){
					String errorMsg = LocaleMsgCache.get2(0, xlocale, expireFlag ? "pass_expired":result.getResultMap().get("errorMsg"));
					response.getWriter().write("{\"success\":false,\"error\":\"" + GenericUtil.stringToJS2(errorMsg) + "\"}");
					return;
				}
				
				int userId = GenericUtil.uInt(result.getResultMap().get("userId"));
				int roleCount = GenericUtil.uInt(result.getResultMap().get("roleCount"));
				int deviceType = GenericUtil.uInt(request.getParameter("deviceType"));
				int forceUserRoleId = GenericUtil.uInt(requestParams.get("userRoleId"));
				if (roleCount < 0 || forceUserRoleId != 0) {
					if (forceUserRoleId == 0)forceUserRoleId = -roleCount;
					Map<String, Object> scd = engine.userRoleSelect(userId, forceUserRoleId,
							GenericUtil.uInt(requestParams.get("customizationId")), null, deviceType != 0 ? request.getParameter("deviceId") : null);
					if (scd == null){
						response.getWriter().write("{\"success\":false}"); // bir hata var
						return;
					}
					scd.put("locale", xlocale);
					if (deviceType != 0) {
						scd.put("mobileDeviceId", request.getParameter("deviceId"));
						scd.put("mobile", deviceType);
						UserUtil.onlineUserLogin(scd, request.getRemoteAddr(), null, (short) deviceType, request.getParameter("deviceId"));
					}
					dfr.getResultMap().put("tokenKey", UserUtil.generateTokenFromScd(scd, 0, request.getRemoteAddr(), 24 * 60 * 60 * 1000));
					response.getWriter().write("{\"success\":true,\"token\":\""+UserUtil.generateTokenFromScd(scd, 0, request.getRemoteAddr(), 24 * 60 * 60 * 1000)+"\",\"session\":" + GenericUtil.fromMapToJsonString2(scd)+"}"); // hersey duzgun
					
//					response.getWriter().write("{\"success\":true,\"token\":\""+UserUtil.generateTokenFromScd(scd, 0, request.getRemoteAddr(), 24 * 60 * 60 * 1000)+"\",\"session\":" + GenericUtil.fromMapToJsonString2(scd)+"}"); // hersey duzgun
	
					return;
	
				} else {
					dfr.getResultMap().put("errorMsg", "Too many roles. Use [forceUserRoleId]");
					response.getWriter().write("{\"success\":false,\"error\":\"Too many roles. Use [forceUserRoleId]\"}");
					return;
				}
			}
			response.setContentType("application/json");

			W5WsServer wss = FrameworkCache.getWsServer(projectId, serviceName);
			if(wss==null)
				throw new IWBException("framework","WrongService",0,null, "Wrong Service: Should Be [ServiceName].[MethodName]", null);
			W5WsServerMethod wsm = null;
			for(W5WsServerMethod wsmx:wss.get_methods())if(wsmx.getDsc().equals(methodName)){
				wsm = wsmx;
				break;
			}
			if(wsm==null)
				throw new IWBException("framework","WrongMethod",0,null, "Wrong Method: Should Be [ServiceName].[MethodName]", null);
			
			Map<String, Object> scd = null;
			if(GenericUtil.isEmpty(wsm.getAccessSourceTypes()) || GenericUtil.hasPartInside2(wsm.getAccessSourceTypes(), "1")){
				scd = GenericUtil.isEmpty(token) ? null : UserUtil.getScdFromToken(token, "");
				if(!GenericUtil.hasPartInside2(wsm.getAccessSourceTypes(), "6") && GenericUtil.isEmpty(scd)){
					throw new IWBException("session","No Session",0,null, "No valid token", null);
				}
			}
			if(scd==null){
				scd = new HashMap();
			}
			scd.put("projectId", projectId);
			Map requestParams = null;

			if(wsm.getDataAcceptTip()==2){//JSON
				JSONObject jo = HttpUtil.getJson(request);
				requestParams = new HashMap();
				if(jo!=null){
					if(wsm.getObjectTip()==4)
						requestParams.put("_json", jo);
					else
						requestParams.putAll(GenericUtil.fromJSONObjectToMap(jo));
				}
			} else 
				requestParams = GenericUtil.getParameterMap(request);
			
			W5FormResult fr=null; 
			switch(wsm.getObjectTip()){
			case	0://show Record
				fr = engine.getFormResult(scd, wsm.getObjectId(), 1, requestParams);
				response.getWriter().write(ext3_4.serializeGetFormSimple(fr).toString());
				response.getWriter().close();
				break;
			case	1://update Record by Form
			case	2://insert Record by Form
			case	3://delete Record by Form
				if(FrameworkSetting.liveSyncRecord4WS)requestParams.put(".w","ws-server");
				fr = engine.postForm4Table(scd, wsm.getObjectId(), wsm.getObjectTip(), requestParams, "");
				response.getWriter().write(ext3_4.serializePostForm(fr).toString());
				response.getWriter().close();		
				if (FrameworkSetting.liveSyncRecord4WS && fr.getErrorMap().isEmpty()){
					UserUtil.syncAfterPostFormAll(fr.getListSyncAfterPostHelper());
				}

				break;
			case	4://run Rhino
				response.getWriter().write(ext3_4.serializeGlobalFunc(engine.executeFunc(scd, wsm.getObjectId(), requestParams
						, GenericUtil.hasPartInside2(wsm.getAccessSourceTypes(), "1") ? (short)1:(short)6)).toString());
				response.getWriter().close();
				break;
			case	19: //run Query
				W5QueryResult qr = engine.executeQuery(scd, wsm.getObjectId(), requestParams);
				if(wsm.get_params()!=null){
					List<W5QueryField> lqf = new ArrayList();
					Map<String,W5QueryField> qfm = new HashMap();
					for(W5QueryField qf:qr.getQuery().get_queryFields()){
						qfm.put(qf.getDsc(), qf);
					}
					for(W5WsServerMethodParam wsmp:wsm.get_params())if(wsmp.getOutFlag()!=0 && wsmp.getParamTip()!=10){
						lqf.add(qfm.get(wsmp.getDsc()));
					}
					qr.setNewQueryFields(lqf);								
				}
				response.getWriter().write(ext3_4.serializeQueryData(qr).toString());
				response.getWriter().close();
				break;
			case	31:case	32:case	33:
				throw new IWBException("ws","TODO",0,null, "Methods Not Implemented", null);
			}
		} catch (Exception e) {
			response.getWriter().write(new IWBException("framework","REST Def",0,null, "Error", e).toJsonString(request.getRequestURI()));
		}
	}
	
	@RequestMapping("/*/*")
	public void hndRESTWadl(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndRESTWadl:"+request.getRequestURI());
		request.setCharacterEncoding( "UTF-8" );
		response.setCharacterEncoding( "UTF-8" );
		try {
			String[] u = request.getRequestURI().replace('/', ',').split(",");
			Map<String, String> requestParams = GenericUtil.getParameterMap(request);
			String token = requestParams.get("tokenKey");
			String projectId=u[2]; 
			String serviceName=u[3]; 
			response.setContentType("application/json");

			if(serviceName.endsWith(".wadl") || serviceName.endsWith(".WADL")){
				if(serviceName.endsWith(".wadl") || serviceName.endsWith(".WADL"))serviceName=serviceName.substring(0, serviceName.length()-5);
			    W5WsServer wss = FrameworkCache.getWsServer(projectId, serviceName);
				if(wss==null)throw new IWBException("rest","WS Not Found",0,serviceName, "WS Not Found", null);
				Map<String, Object> wsmoMap = engine.getWsServerMethodObjects(wss);
				response.setContentType("text/xml");
				response.getWriter().write(serializeRestWADL(wss, wsmoMap).toString());
				return;
			} else if(serviceName.equals("login")){
				requestParams.put("_remote_ip", request.getRemoteAddr());
				requestParams.put("_mobile", ""+GenericUtil.uInt(requestParams, "deviceType", 0));
				String xlocale = GenericUtil.uStrNvl(request.getParameter("locale"),FrameworkCache.getAppSettingStringValue(0, "locale"));
				W5GlobalFuncResult result = engine.executeFunc(new HashMap(), 1, requestParams, (short) 7); // user Authenticate DbFunc:1
				W5GlobalFuncResult dfr = new W5GlobalFuncResult(-1);dfr.setResultMap(new HashMap());dfr.setErrorMap(new HashMap());
				List<W5GlobalFuncParam> arl = new ArrayList();
				dfr.setGlobalFunc(new W5GlobalFunc());dfr.getGlobalFunc().set_dbFuncParamList(arl);
				arl.add(new W5GlobalFuncParam("tokenKey"));arl.add(new W5GlobalFuncParam("errorMsg"));
//				W5WsServerMethod wsm = wss.get_methods().get(0);
				// 4 success 5 errorMsg 6 userId 7 expireFlag 8 smsFlag 9 roleCount
				boolean success = GenericUtil.uInt(result.getResultMap().get("success")) != 0;
				boolean expireFlag = GenericUtil.uInt(result.getResultMap().get("expireFlag")) != 0;
				if (!success || expireFlag){
					String errorMsg = LocaleMsgCache.get2(0, xlocale, expireFlag ? "pass_expired":result.getResultMap().get("errorMsg"));
					response.getWriter().write("{\"success\":false,\"error\":\"" + GenericUtil.stringToJS2(errorMsg) + "\"}");
					return;
				}
				
				int userId = GenericUtil.uInt(result.getResultMap().get("userId"));
				int roleCount = GenericUtil.uInt(result.getResultMap().get("roleCount"));
				int deviceType = GenericUtil.uInt(request.getParameter("deviceType"));
				int forceUserRoleId = GenericUtil.uInt(requestParams.get("userRoleId"));
				if (roleCount < 0 || forceUserRoleId != 0) {
					if (forceUserRoleId == 0)forceUserRoleId = -roleCount;
					Map<String, Object> scd = engine.userRoleSelect(userId, forceUserRoleId,
							GenericUtil.uInt(requestParams.get("customizationId")), null, deviceType != 0 ? request.getParameter("deviceId") : null);
					if (scd == null){
						response.getWriter().write("{\"success\":false}"); // bir hata var
						return;
					}
					scd.put("locale", xlocale);
					if (deviceType != 0) {
						scd.put("mobileDeviceId", request.getParameter("deviceId"));
						scd.put("mobile", deviceType);
						UserUtil.onlineUserLogin(scd, request.getRemoteAddr(), null, (short) deviceType, request.getParameter("deviceId"));
					}
					dfr.getResultMap().put("tokenKey", UserUtil.generateTokenFromScd(scd, 0, request.getRemoteAddr(), 24 * 60 * 60 * 1000));
					response.getWriter().write("{\"success\":true,\"token\":\""+UserUtil.generateTokenFromScd(scd, 0, request.getRemoteAddr(), 24 * 60 * 60 * 1000)+"\",\"session\":" + GenericUtil.fromMapToJsonString2(scd)+"}"); // hersey duzgun
					
//					response.getWriter().write("{\"success\":true,\"token\":\""+UserUtil.generateTokenFromScd(scd, 0, request.getRemoteAddr(), 24 * 60 * 60 * 1000)+"\",\"session\":" + GenericUtil.fromMapToJsonString2(scd)+"}"); // hersey duzgun
	
					return;
	
				} else {
					dfr.getResultMap().put("errorMsg", "Too many roles. Use [forceUserRoleId]");
					response.getWriter().write("{\"success\":false,\"error\":\"Too many roles. Use [forceUserRoleId]\"}");
					return;
				}

	//		} else if(u[0].equals("selectUserRole")){
			} else if(serviceName.equals("ping")){
				response.getWriter().write("{\"success\":true,\"session\":" + !(GenericUtil.isEmpty(token) || GenericUtil.isEmpty(UserUtil.getScdFromToken(token, ""))) + "}");
				return;
			}
			response.getWriter().write("{\"success\":false,\"error\":\"Wrong Request\"}");
		} catch (Exception e) {
			response.getWriter().write(new IWBException("framework","WADL Def",0,null, "Error", e).toJsonString(request.getRequestURI()));
		}
	}

			

	public	StringBuilder serializeRestWADL(W5WsServer ws, Map<String, Object> wsmoMap){
		String[] elementTypes = new String[]{"","string","string","float","int","boolean","string","string","string"};
		String wsRestUrl = FrameworkCache.getAppSettingStringValue(0, "ws_rest_server_url","");

		StringBuilder buf = new StringBuilder();
		buf.append("<?xml version=\"1.0\"?><application xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:html=\"http://www.w3.org/1999/xhtml\" xmlns=\"http://wadl.dev.java.net/2009/02\">")
			.append("\n<doc xmlns:iworkbetter=\"http://www.iworkbetter.com/\" /><resources base=\"").append(wsRestUrl).append(ws.getProjectUuid()).append("\">");
		buf.append("\n<resource path=\"").append(ws.getDsc()).append("\">");
			
		
		for(W5WsServerMethod wsm:ws.get_methods()){
			List<W5WsServerMethodParam> lwsmp = new ArrayList();
			if(wsm.getObjectTip()!=4 || wsm.getObjectId()!=3){
				W5WsServerMethodParam tokenKey =new W5WsServerMethodParam(-998, "tokenKey", (short)1);tokenKey.setOutFlag((short)0);tokenKey.setNotNullFlag((short)1);
				lwsmp.add(tokenKey);
			}
//			buf.append("\n<element name=\"").append(wsm.getDsc()).append("\"><complexType><sequence>");
			buf.append("\n<resource path=\"").append(wsm.getDsc()).append("\">");
			buf.append("\n<method name=\"").append(wsm.getObjectTip()<19 ? "POST":"GET").append("\" id=\"").append(wsm.getDsc()).append("\">");
			
			W5Table t = null;
			Object o = wsmoMap.get(wsm.getDsc());
			if(o==null){//TODO ne yapilabilir?
				buf.append("</method>");
				buf.append("</resource>");
				continue;
			} else if(o instanceof String ){//TODO ne yapilabilir?
				buf.append("</method>");
				buf.append("</resource>");
				continue;
			}else switch(wsm.getObjectTip()){
			case	0:case 1:case 2:case 3:
				W5FormResult fr=(W5FormResult)o;
				lwsmp.add(new W5WsServerMethodParam(-999, "result", (short)9));
				t = FrameworkCache.getTable(ws.getProjectUuid(), fr.getForm().getObjectId());
				if(wsm.getObjectTip()!=2)for(W5TableParam tp:t.get_tableParamList())if(tp.getSourceTip()==1)lwsmp.add(new W5WsServerMethodParam(tp, (short)(wsm.getObjectTip()==2 ? 1:0),wsm.getObjectTip()==2?-999:0));
				if(wsm.getObjectTip()!=3)for(W5FormCell fc:fr.getForm().get_formCells())if(fc.getActiveFlag()!=0 && fc.get_sourceObjectDetail()!=null){
					lwsmp.add(new W5WsServerMethodParam(fc, (short)(wsm.getObjectTip()==0 ? 1:0), wsm.getObjectTip()==0 ? -999:0));
				}
				W5WsServerMethodParam outMsg =new W5WsServerMethodParam(-999, "outMsg", (short)1);outMsg.setParentWsMethodParamId(-999);
				lwsmp.add(outMsg);
				break;
			case	4:
				W5GlobalFuncResult dfr=(W5GlobalFuncResult)o;
				for(W5GlobalFuncParam dfp:dfr.getGlobalFunc().get_dbFuncParamList())if(dfp.getSourceTip()==1 && dfp.getOutFlag()!=0){
					lwsmp.add(new W5WsServerMethodParam(-999, "result", (short)9));
					break;
				}
				for(W5GlobalFuncParam dfp:dfr.getGlobalFunc().get_dbFuncParamList())if(dfp.getSourceTip()==1){
					lwsmp.add(new W5WsServerMethodParam(dfp, dfp.getOutFlag(), dfp.getOutFlag()==0 ? 0:-999));
				}
				break;
			case	19:
				W5QueryResult qr=(W5QueryResult)o;
				lwsmp.add(new W5WsServerMethodParam(-999, "data", (short)10));
				if(qr.getQuery().getMainTableId()!=0)t = FrameworkCache.getTable(ws.getProjectUuid(), qr.getQuery().getMainTableId());
				for(W5QueryParam qp:qr.getQuery().get_queryParams())if(qp.getSourceTip()==1){
					lwsmp.add(new W5WsServerMethodParam(qp, (short)0, 0));
				}
				for(W5QueryField qf:qr.getQuery().get_queryFields()){
					lwsmp.add(new W5WsServerMethodParam(qf, (short)1, -999));
				}
				break;
				
			
			}
			wsm.set_params(lwsmp);
		
			buf.append("\n<request>");
			for(W5WsServerMethodParam wsmp:wsm.get_params())if(wsmp.getOutFlag()==0 && wsmp.getParentWsMethodParamId()==0){
				buf.append("\n<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" style=\"query\"").append(wsmp.getNotNullFlag()==0 ? "":" required=\"true\"").append(" name=\"").append(wsmp.getDsc()).append("\" type=\"");
				if(wsmp.getParamTip()<9)buf.append("xs:").append(elementTypes[wsmp.getParamTip()]);
				else {
					buf.append("iwb:").append(wsm.getDsc()).append("_").append(wsmp.getDsc());
				/*	buf2.append("\n<complexType name=\"").append(wsm.getDsc()).append("_").append(wsmp.getDsc()).append("\"><sequence>");
					for(W5WsServerMethodParam swsmp:wsm.get_params())if(swsmp.getParentWsMethodParamId()==wsmp.getWsServerMethodParamId()){
						buf2.append("\n<element").append(swsmp.getNotNullFlag()==0 ? "":" required=\"true\"").append(" name=\"").append(swsmp.getDsc()).append("\" type=\"");
						if(swsmp.getParamTip()<9)buf2.append("xsd:").append(elementTypes[swsmp.getParamTip()]);
						else buf2.append("string");//TODO
						buf2.append("\" />");
					}
					buf2.append("</sequence></complexType>"); */
				}
				buf.append("\" />");
			}
			buf.append("</request>");
			
			buf.append("\n<response><representation mediaType=\"application/json\"/>  ");
			for(W5WsServerMethodParam wsmp:wsm.get_params())if(wsmp.getOutFlag()!=0 && wsmp.getParentWsMethodParamId()==0){
				buf.append("\n<param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" style=\"plain\"").append(wsmp.getNotNullFlag()==0 ? "":" required=\"true\"").append(" name=\"").append(wsmp.getDsc()).append("\" type=\"");
				if(wsmp.getParamTip()<9)buf.append("xs:").append(elementTypes[wsmp.getParamTip()]);
				else {
					buf.append("iwb:").append(wsm.getDsc()).append("_").append(wsmp.getDsc());
				/*	buf2.append("\n<complexType name=\"").append(wsm.getDsc()).append("_").append(wsmp.getDsc()).append("\"><sequence>");
					for(W5WsServerMethodParam swsmp:wsm.get_params())if(swsmp.getParentWsMethodParamId()==wsmp.getWsServerMethodParamId()){
						buf2.append("\n<element").append(swsmp.getNotNullFlag()==0 ? "":" required=\"true\"").append(" name=\"").append(swsmp.getDsc()).append("\" type=\"");
						if(swsmp.getParamTip()<9)buf2.append("xsd:").append(elementTypes[swsmp.getParamTip()]);
						else buf2.append("string");//TODO
						buf2.append("\" />");
					}
					buf2.append("</sequence></complexType>"); */
				}
				buf.append("\" />");
			}
			buf.append("</response>");
			buf.append("</method>");
			buf.append("</resource>");
		}
//	      <doc xml:lang="en" title="Register a new account">The account register service can be used to fill in account registration forms.</doc>
	/*      <response>
	         <representation mediaType="text/html"/>
	      </response>
	    </method>
 		<method name="POST" id="createUserAccount">
	        <doc xml:lang="en" title="Register a new account">
	          Creating the account after having filled in the registration form.
	        </doc>
		  <request>	
	        <param xmlns:xs="http://www.w3.org/2001/XMLSchema" type="xs:string" style="query" name="username">
			  <doc>The username</doc>
		    </param>
		    <param xmlns:xs="http://www.w3.org/2001/XMLSchema" type="xs:string" style="query" name="password">
			  <doc>The password</doc>
		    </param>
		    <representation mediaType="application/json"/>
		  </request>
	      <response>
	         <representation mediaType="text/html"/>
	      </response>
	    </method>*/
		buf.append("\n</resource></resources></application>");
		
		return buf;
	}
}
