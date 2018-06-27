package iwb.controller_ws;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//import iwb.adapter.soap.SoapAdapter;
//import iwb.adapter.soap.impl.AxisSoap1_4;
import iwb.adapter.ui.ViewAdapter;
import iwb.adapter.ui.extjs.ExtJs3_3;
import iwb.controller.AppServlet;
import iwb.domain.db.W5DbFunc;
import iwb.domain.db.W5DbFuncParam;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5QueryParam;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableParam;
import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.domain.db.W5WsServerMethodParam;
import iwb.domain.helper.W5QueuedDbFuncHelper;
import iwb.domain.result.W5DbFuncResult;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5QueryResult;
import iwb.engine.FrameworkEngine;
import iwb.exception.PromisException;
import iwb.util.FrameworkCache;
import iwb.util.FrameworkSetting;
import iwb.util.GenericUtil;
import iwb.util.LocaleMsgCache;
import iwb.util.UserUtil;

@Controller
@RequestMapping("/ws")
public class WsServlet implements InitializingBean {
	private static Logger logger = Logger.getLogger(AppServlet.class);

	private ViewAdapter ext3_4 = new ExtJs3_3();
	//private SoapAdapter soap = new AxisSoap1_4();
	
	@Autowired
	private FrameworkEngine engine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

	/*
	@RequestMapping("/soap/*")
	public void hndSOAP(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndSOAP:"+request.getRequestURI());
		String[] u = request.getRequestURI().replace('/', ',').split(",");
		Map<String, String> requestParams = GenericUtil.getParameterMap(request);
		response.setContentType("text/xml");
		request.setCharacterEncoding( "UTF-8" );
		response.setCharacterEncoding( "UTF-8" );
		try {
			String method=u[u.length-1]; 
			if(requestParams.containsKey("wsdl") || requestParams.containsKey("WSDL") || method.endsWith(".wsdl") || method.endsWith(".WSDL")){
				if(method.endsWith(".wsdl") || method.endsWith(".WSDL"))method=method.substring(0, method.length()-5);
			    W5WsServer wss = FrameworkCache.getWsServer(method);
				if(wss==null)throw new PromisException("soap","WS Not Found",0,method, "WS Not Found", null);
				Map<String, Object> wsmoMap = engine.getWsServerMethodObjects(wss);
				response.getWriter().write(soap.serializeSoapWSDL(wss, wsmoMap).toString());
			} else { //exec
			    W5WsServer wss = FrameworkCache.getWsServer(method);
				if(wss==null)throw new PromisException("soap","WS Not Found",0,method, "WS Not Found", null);
				requestParams.clear();
				StringBuilder jb = new StringBuilder();
				String line = null;
				BufferedReader reader = request.getReader();
				while ((line = reader.readLine()) != null)jb.append(line);
				
				MessageFactory messageFactory = MessageFactory.newInstance();
				InputStream stream = new ByteArrayInputStream(jb.toString().getBytes("UTF-8"));
				SOAPMessage soapMessage = messageFactory.createMessage(null, stream);
	//				SOAPPart soapPart = soapMessage.getSOAPPart();
				SOAPBody soapBody = soapMessage.getSOAPBody();
				org.w3c.dom.Node node = soapBody.getFirstChild();
				String methodName = node.getNodeName();
				if(node.hasChildNodes())for(int qi=0;qi<node.getChildNodes().getLength();qi++){
					org.w3c.dom.Node cnode = node.getChildNodes().item(qi); 
					requestParams.put(cnode.getNodeName(), cnode.getTextContent());
				} else if(node.getNextSibling()!=null && node.getNextSibling().getLocalName()!=null){
					methodName = node.getNextSibling().getLocalName();
					org.w3c.dom.Node cnode = node.getNextSibling().getFirstChild();
					while(cnode!=null){
						if(cnode.getNodeName()!=null && cnode.getNodeName().startsWith("iwor:")){
							requestParams.put(cnode.getNodeName().substring(5), cnode.getTextContent());
						}
						cnode = cnode.getNextSibling();
					}
					
				}
				if(methodName==null)throw new PromisException("soap","Method not Defined",0,method, "Method not Defined", null);
				if(methodName.equals("login")){
					requestParams.put("_remote_ip", request.getRemoteAddr());
					requestParams.put("_mobile", ""+GenericUtil.uInt(requestParams, "deviceType", 0));
					String xlocale = GenericUtil.uStrNvl(request.getParameter("locale"),FrameworkCache.getAppSettingStringValue(0, "locale"));
					W5DbFuncResult result = engine.executeDbFunc(new HashMap(), 1, requestParams, (short) 4); // user Authenticate DbFunc:1
					W5DbFuncResult dfr = new W5DbFuncResult(-1);dfr.setResultMap(new HashMap());dfr.setErrorMap(new HashMap());
					List<W5DbFuncParam> arl = new ArrayList();
					dfr.setDbFunc(new W5DbFunc());dfr.getDbFunc().set_dbFuncParamList(arl);
					arl.add(new W5DbFuncParam("tokenKey"));arl.add(new W5DbFuncParam("errorMsg"));
					W5WsServerMethod wsm = wss.get_methods().get(0);
					/* 4 success 5 errorMsg 6 userId 7 expireFlag 8 smsFlag 9 roleCount * /
					boolean success = GenericUtil.uInt(result.getResultMap().get("success")) != 0;
					boolean expireFlag = GenericUtil.uInt(result.getResultMap().get("expireFlag")) != 0;
					if (!success || expireFlag){
						dfr.getResultMap().put("errorMsg", "Wrong User or Pass");
						response.getWriter().write(soap.serializeDbFunc(wsm, dfr).toString());
						return;
					}
					
					
					int userId = GenericUtil.uInt(result.getResultMap().get("userId"));
					int roleCount = GenericUtil.uInt(result.getResultMap().get("roleCount"));
					int deviceType = GenericUtil.uInt(request.getParameter("deviceType"));
					int forceUserRoleId = GenericUtil.uInt(requestParams.get("userRoleId"));
					if (roleCount < 0 || forceUserRoleId != 0) {
						if (forceUserRoleId == 0)forceUserRoleId = -roleCount;
						Map<String, Object> scd = engine.userRoleSelect(userId, forceUserRoleId,
								GenericUtil.uInt(result.getResultMap().get("defaultUserCustomizationId")),
								GenericUtil.uInt(requestParams.get("customizationId")), null, deviceType != 0 ? request.getParameter("deviceId") : null);
						if (scd == null){
							dfr.getResultMap().put("errorMsg", "Session not created :(");
							response.getWriter().write(soap.serializeDbFunc(wsm, dfr).toString());
							return;
						}
						scd.put("locale", xlocale);
						if (deviceType != 0) {
							scd.put("mobileDeviceId", request.getParameter("deviceId"));
							scd.put("mobile", deviceType);
							UserUtil.onlineUserLogin(scd, request.getRemoteAddr(), null, (short) deviceType, request.getParameter("deviceId"));
						}
						dfr.getResultMap().put("tokenKey", UserUtil.generateTokenFromScd(scd, 0, request.getRemoteAddr(), 24 * 60 * 60 * 1000));
						response.getWriter().write(soap.serializeDbFunc(wsm, dfr).toString());

//						response.getWriter().write("{\"success\":true,\"token\":\""+UserUtil.generateTokenFromScd(scd, 0, request.getRemoteAddr(), 24 * 60 * 60 * 1000)+"\",\"session\":" + GenericUtil.fromMapToJsonString2(scd)+"}"); // hersey duzgun
		
						return;
		
					} else {
						dfr.getResultMap().put("errorMsg", "Too many roles. Use [forceUserRoleId]");
						response.getWriter().write(soap.serializeDbFunc(wsm, dfr).toString());
						return;
					}
				} else if(methodName.equals("logout")){
				} else {
					W5FormResult fr=null; 
					Map<String, Object> scd = GenericUtil.isEmpty(requestParams.get("tokenKey")) ? null : UserUtil.getScdFromToken(requestParams.get("tokenKey"), "");
					if(GenericUtil.isEmpty(scd)){
						throw new PromisException("session","No Session",0,null, "No valid token", null);
					}

					for(W5WsServerMethod wsm:wss.get_methods())if(wsm.getDsc().equals(methodName)){
						switch(wsm.getObjectTip()){
						case	0://show Record
							fr = engine.getFormResult(scd, wsm.getObjectId(), 1, requestParams);
							response.getWriter().write(soap.serializeGetFormSimple(wsm, fr).toString());
							response.getWriter().close();
							break;
						case	1://update Record by Form
						case	2://insert Record by Form
						case	3://delete Record by Form
							if(FrameworkSetting.liveSyncRecord4WS)requestParams.put(".w","ws-server");
							fr = engine.postForm4Table(scd, wsm.getObjectId(), wsm.getObjectTip(), requestParams, "");
							response.getWriter().write(soap.serializePostForm(wsm, fr).toString());
							response.getWriter().close();
							
							if (fr.getErrorMap().isEmpty()){
								UserUtil.syncAfterPostFormAll(fr.getListSyncAfterPostHelper());
							}
							
							
							break;
						case	4://run Rhino
							response.getWriter().write(soap.serializeDbFunc(wsm, engine.executeDbFunc(scd, wsm.getObjectId(), requestParams, (short)1)).toString());
							response.getWriter().close();
							break;
						case	19: //run Query
							response.getWriter().write(soap.serializeQueryData(wsm, engine.executeQuery(scd, wsm.getObjectId(), requestParams)).toString());
							response.getWriter().close();
							break;
						case	31:case	32:case	33:
							throw new PromisException("soap","TODO",0,null, "Methods Not Implemented", null);
						}
						return;
					}
					throw new PromisException("soap","Method not Found",0,u[1], "Method not Found", null);
				}
//				W5SOAPResult  sr = engine.executeSOAPMethod(0, u[u.length-1], methodName, requestParams);
//				response.getWriter().write(soap.serializeSOAPResult(sr).toString());
				
			}
		} catch (PromisException e) {
			response.getWriter().write(soap.serializeException(e).toString());
		} catch (Exception e) {
			if(e.getCause()!=null && e.getCause() instanceof PromisException){
				response.getWriter().write(soap.serializeException((PromisException)e.getCause()).toString());
			} else response.getWriter().write(soap.serializeException(new PromisException("framework","Undefined Exception",0,null, e.getMessage(), e.getCause())).toString());
		}
		response.getWriter().close();		
	}
	*/
	
	@RequestMapping("/rest/*")
	public void hndRESTWadl(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndREST:"+request.getRequestURI());
		request.setCharacterEncoding( "UTF-8" );
		response.setCharacterEncoding( "UTF-8" );
		response.setContentType("text/xml");
		try {
			String[] u = request.getRequestURI().replace('/', ',').split(",");
			Map<String, String> requestParams = GenericUtil.getParameterMap(request);
			String method=u[u.length-1]; 
			if(method.endsWith(".wadl") || method.endsWith(".WADL")){
				if(method.endsWith(".wadl") || method.endsWith(".WADL"))method=method.substring(0, method.length()-5);
			    W5WsServer wss = FrameworkCache.getWsServer(method);
				if(wss==null)throw new PromisException("soap","WS Not Found",0,method, "WS Not Found", null);
				Map<String, Object> wsmoMap = engine.getWsServerMethodObjects(wss);
				response.getWriter().write(serializeRestWADL(wss, wsmoMap).toString());
			} else
				throw new PromisException("framework","Undefined Method",0,null, "User [methodName].wadl", null);
		} catch (PromisException e) {
			Map scd = new HashMap();scd.put("locale",FrameworkCache.getAppSettingStringValue(0, "locale"));scd.put("customizationId",0);
			response.getWriter().write(e.toJsonString(scd));
		} catch (Exception e) {
			Map scd = new HashMap();scd.put("locale",FrameworkCache.getAppSettingStringValue(0, "locale"));scd.put("customizationId",0);
			if(e.getCause()!=null && e.getCause() instanceof PromisException){
				response.getWriter().write(((PromisException)e.getCause()).toJsonString(scd));
			} else 
				response.getWriter().write(new PromisException("framework","Undefined Exception",0,null, e.getMessage(), e.getCause()).toJsonString(scd));
		}
	}

				
	@RequestMapping("/rest/*/*")
	public void hndREST(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("hndREST:"+request.getRequestURI());
		request.setCharacterEncoding( "UTF-8" );
		response.setCharacterEncoding( "UTF-8" );
		response.setContentType("application/json");
		try {
			String[] u = request.getRequestURI().replace('/', ',').split(",");
			Map<String, String> requestParams = GenericUtil.getParameterMap(request);
			String token = requestParams.get("tokenKey");
			String method=u[u.length-1]; 
			if(method.equals("login")){
				requestParams.put("_remote_ip", request.getRemoteAddr());
				requestParams.put("_mobile", ""+GenericUtil.uInt(requestParams, "deviceType", 0));
				String xlocale = GenericUtil.uStrNvl(request.getParameter("locale"),FrameworkCache.getAppSettingStringValue(0, "locale"));
				W5DbFuncResult result = engine.executeDbFunc(new HashMap(), 1, requestParams, (short) 4); // user Authenticate DbFunc:1
				W5DbFuncResult dfr = new W5DbFuncResult(-1);dfr.setResultMap(new HashMap());dfr.setErrorMap(new HashMap());
				List<W5DbFuncParam> arl = new ArrayList();
				dfr.setDbFunc(new W5DbFunc());dfr.getDbFunc().set_dbFuncParamList(arl);
				arl.add(new W5DbFuncParam("tokenKey"));arl.add(new W5DbFuncParam("errorMsg"));
//				W5WsServerMethod wsm = wss.get_methods().get(0);
				/* 4 success 5 errorMsg 6 userId 7 expireFlag 8 smsFlag 9 roleCount */
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
							GenericUtil.uInt(result.getResultMap().get("defaultUserCustomizationId")),
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
			} else if(method.equals("ping")){
				response.getWriter().write("{\"success\":true,\"session\":" + !(GenericUtil.isEmpty(token) || GenericUtil.isEmpty(UserUtil.getScdFromToken(token, ""))) + "}");
				return;
			} else {
				Map<String, Object> scd = GenericUtil.isEmpty(token) ? null : UserUtil.getScdFromToken(token, "");
				if(GenericUtil.isEmpty(scd)){
					throw new PromisException("session","No Session",0,null, "No valid token", null);
				}
				int customizationId = (Integer)scd.get("customizationId");
				Map<String, W5WsServer> wssMap = FrameworkCache.wWsServers.get(customizationId);

				W5WsServer wss = wssMap.get(u[u.length-2]);
				if(wss==null){//gerek var mi?
					wssMap = FrameworkCache.wWsServers.get(0);
					wss = wssMap.get(u[u.length-2]);
				}
				if(wss==null)throw new PromisException("ws","WS Not Found",0,u[u.length-2], "WS Not Found", null);
				
				W5FormResult fr=null; 
				for(W5WsServerMethod wsm:wss.get_methods())if(wsm.getDsc().equals(method)){
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
						response.getWriter().write(ext3_4.serializeDbFunc(engine.executeDbFunc(scd, wsm.getObjectId(), requestParams, (short)1)).toString());
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
						throw new PromisException("ws","TODO",0,null, "Methods Not Implemented", null);
					}
					return;
				}
				throw new PromisException("ws","Method not Found",0,method, "Method not Found", null);
			}
		} catch (PromisException e) {
			Map scd = new HashMap();scd.put("locale",FrameworkCache.getAppSettingStringValue(0, "locale"));scd.put("customizationId",0);
			response.getWriter().write(e.toJsonString(scd));
		} catch (Exception e) {
			Map scd = new HashMap();scd.put("locale",FrameworkCache.getAppSettingStringValue(0, "locale"));scd.put("customizationId",0);
			if(e.getCause()!=null && e.getCause() instanceof PromisException){
				response.getWriter().write(((PromisException)e.getCause()).toJsonString(scd));
			} else 
				response.getWriter().write(new PromisException("framework","Undefined Exception",0,null, e.getMessage(), e.getCause()).toJsonString(scd));
		}
	}

	public	StringBuilder serializeRestWADL(W5WsServer ws, Map<String, Object> wsmoMap){
		String[] elementTypes = new String[]{"","string","string","float","int","boolean","string","string","string"};
		String wsRestUrl = FrameworkCache.getAppSettingStringValue(0, "ws_rest_server_url","");

		StringBuilder buf = new StringBuilder();
		buf.append("<?xml version=\"1.0\"?><application xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:html=\"http://www.w3.org/1999/xhtml\" xmlns=\"http://wadl.dev.java.net/2009/02\">")
			.append("\n<doc xmlns:iworkbetter=\"http://www.iworkbetter.com/\" /><resources base=\"").append(wsRestUrl).append("\">");
		buf.append("\n<resource path=\"").append(ws.getDsc()).append("\">");
			
		
		for(W5WsServerMethod wsm:ws.get_methods()){
			List<W5WsServerMethodParam> lwsmp = new ArrayList();
			if(wsm.getObjectTip()!=4 || wsm.getObjectId()!=3){
				W5WsServerMethodParam tokenKey =new W5WsServerMethodParam(-998, "tokenKey", (short)1);tokenKey.setOutFlag((short)0);tokenKey.setNotNullFlag((short)1);
				lwsmp.add(tokenKey);
			}
//			buf.append("\n<element name=\"").append(wsm.getDsc()).append("\"><complexType><sequence>");
			buf.append("\n<resource path=\"").append(wsm.getDsc()).append("\">");
			buf.append("\n<method name=\"GET\" id=\"").append(wsm.getDsc()).append("\">");
			
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
				t = FrameworkCache.getTable(ws.getCustomizationId(), fr.getForm().getObjectId());
				for(W5TableParam tp:t.get_tableParamList())if(tp.getSourceTip()==1)lwsmp.add(new W5WsServerMethodParam(tp, (short)(wsm.getObjectTip()==2 ? 1:0),wsm.getObjectTip()==2?-999:0));
				if(wsm.getObjectTip()!=3)for(W5FormCell fc:fr.getForm().get_formCells())if(fc.getActiveFlag()!=0 && fc.get_sourceObjectDetail()!=null){
					lwsmp.add(new W5WsServerMethodParam(fc, (short)(wsm.getObjectTip()==0 ? 1:0), wsm.getObjectTip()==0 ? -999:0));
				}
				W5WsServerMethodParam outMsg =new W5WsServerMethodParam(-999, "outMsg", (short)1);outMsg.setParentWsMethodParamId(-999);
				lwsmp.add(outMsg);
				break;
			case	4:
				W5DbFuncResult dfr=(W5DbFuncResult)o;
				for(W5DbFuncParam dfp:dfr.getDbFunc().get_dbFuncParamList())if(dfp.getSourceTip()==1 && dfp.getOutFlag()!=0){
					lwsmp.add(new W5WsServerMethodParam(-999, "result", (short)9));
					break;
				}
				if(dfr.getDbFunc().getRelatedTableId()!=0)t = FrameworkCache.getTable(ws.getCustomizationId(), dfr.getDbFunc().getRelatedTableId());
				for(W5DbFuncParam dfp:dfr.getDbFunc().get_dbFuncParamList())if(dfp.getSourceTip()==1){
					lwsmp.add(new W5WsServerMethodParam(dfp, dfp.getOutFlag(), dfp.getOutFlag()==0 ? 0:-999));
				}
				break;
			case	19:
				W5QueryResult qr=(W5QueryResult)o;
				lwsmp.add(new W5WsServerMethodParam(-999, "data", (short)10));
				if(qr.getQuery().getMainTableId()!=0)t = FrameworkCache.getTable(ws.getCustomizationId(), qr.getQuery().getMainTableId());
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
