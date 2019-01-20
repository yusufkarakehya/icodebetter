package iwb.controller;

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

import iwb.adapter.soap.SoapAdapter;
import iwb.adapter.soap.impl.AxisSoap1_4;
//import iwb.adapter.soap.SoapAdapter;
//import iwb.adapter.soap.impl.AxisSoap1_4;
import iwb.adapter.ui.ViewAdapter;
import iwb.adapter.ui.extjs.ExtJs3_4;
import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.domain.db.W5GlobalFunc;
import iwb.domain.db.W5GlobalFuncParam;
import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.exception.IWBException;
import iwb.service.FrameworkService;
import iwb.util.GenericUtil;
import iwb.util.UserUtil;

@Controller
@RequestMapping("/soap")
public class SoapController implements InitializingBean {
	private static Logger logger = Logger.getLogger(SoapController.class);

	private ViewAdapter ext3_4 = new ExtJs3_4();
	private SoapAdapter soap = new AxisSoap1_4();
	
	@Autowired
	private FrameworkService engine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

	
	@RequestMapping("/*/*")
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
			String projectId=u[u.length-2]; 
			String method=u[u.length-1]; 
			if(requestParams.containsKey("wsdl") || requestParams.containsKey("WSDL") || method.endsWith(".wsdl") || method.endsWith(".WSDL")){
				if(method.endsWith(".wsdl") || method.endsWith(".WSDL"))method=method.substring(0, method.length()-5);
			    W5WsServer wss = FrameworkCache.getWsServer(projectId, method);
				if(wss==null)throw new IWBException("soap","WS Not Found",0,method, "WS Not Found", null);
				Map<String, Object> wsmoMap = engine.getWsServerMethodObjects(wss);
				response.getWriter().write(soap.serializeSoapWSDL(wss, wsmoMap).toString());
			} else { //exec
			    W5WsServer wss = FrameworkCache.getWsServer(projectId, method);
				if(wss==null)throw new IWBException("soap","WS Not Found",0,method, "WS Not Found", null);
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
				if(methodName==null)throw new IWBException("soap","Method not Defined",0,method, "Method not Defined", null);
				if(methodName.equals("login")){
					requestParams.put("_remote_ip", request.getRemoteAddr());
					requestParams.put("_mobile", ""+GenericUtil.uInt(requestParams, "deviceType", 0));
					String xlocale = GenericUtil.uStrNvl(request.getParameter("locale"),FrameworkCache.getAppSettingStringValue(0, "locale"));
					W5GlobalFuncResult result = engine.executeFunc(new HashMap(), 1, requestParams, (short) 4); // user Authenticate DbFunc:1
					W5GlobalFuncResult dfr = new W5GlobalFuncResult(-1);dfr.setResultMap(new HashMap());dfr.setErrorMap(new HashMap());
					List<W5GlobalFuncParam> arl = new ArrayList();
					dfr.setGlobalFunc(new W5GlobalFunc());dfr.getGlobalFunc().set_dbFuncParamList(arl);
					arl.add(new W5GlobalFuncParam("tokenKey"));arl.add(new W5GlobalFuncParam("errorMsg"));
					W5WsServerMethod wsm = wss.get_methods().get(0);
					// 4 success 5 errorMsg 6 userId 7 expireFlag 8 smsFlag 9 roleCount 
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
						throw new IWBException("session","No Session",0,null, "No valid token", null);
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
							response.getWriter().write(soap.serializeDbFunc(wsm, engine.executeFunc(scd, wsm.getObjectId(), requestParams, (short)1)).toString());
							response.getWriter().close();
							break;
						case	19: //run Query
							response.getWriter().write(soap.serializeQueryData(wsm, engine.executeQuery(scd, wsm.getObjectId(), requestParams)).toString());
							response.getWriter().close();
							break;
						case	31:case	32:case	33:
							throw new IWBException("soap","TODO",0,null, "Methods Not Implemented", null);
						}
						return;
					}
					throw new IWBException("soap","Method not Found",0,u[1], "Method not Found", null);
				}
//				W5SOAPResult  sr = engine.executeSOAPMethod(0, u[u.length-1], methodName, requestParams);
//				response.getWriter().write(soap.serializeSOAPResult(sr).toString());
				
			}
		} catch (IWBException e) {
			response.getWriter().write(soap.serializeException(e).toString());
		} catch (Exception e) {
			if(e.getCause()!=null && e.getCause() instanceof IWBException){
				response.getWriter().write(soap.serializeException((IWBException)e.getCause()).toString());
			} else response.getWriter().write(soap.serializeException(new IWBException("framework","Undefined Exception",0,null, e.getMessage(), e.getCause())).toString());
		}
		response.getWriter().close();		
	}
	}
