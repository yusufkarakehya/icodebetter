package iwb.adapter.soap.impl;

import java.util.Map;

import iwb.adapter.soap.SoapAdapter;
import iwb.cache.FrameworkCache;
import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.domain.db.W5WsServerMethodParam;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5QueryResult;
import iwb.exception.IWBException;
import iwb.util.GenericUtil;

public class MSSoap1 implements SoapAdapter{
	final static String[] elementTypes = new String[]{"","string","string","float","int","boolean","string","string","string"};
	public	StringBuilder serializeSoapWSDL(W5WsServer ws, Map<String, Object> wsmoMap){
		StringBuilder buf = new StringBuilder();
		buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
		.append("<wsdl:definitions xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:tm=\"http://microsoft.com/wsdl/mime/textMatching/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:mime=\"http://schemas.xmlsoap.org/wsdl/mime/\" xmlns:tns=\"").append(ws.getTargetNamespace()).append("\" xmlns:s=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://schemas.xmlsoap.org/wsdl/soap12/\" xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" targetNamespace=\"").append(ws.getTargetNamespace()).append("\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\">")
		.append("\n<wsdl:types><s:schema elementFormDefault=\"qualified\" targetNamespace=\"").append(ws.getTargetNamespace()).append("\">");
		StringBuilder buf2 = new StringBuilder();
		for(W5WsServerMethod wsm:ws.get_methods()){
			buf.append("\n<s:element name=\"").append(wsm.getDsc()).append("\">")
			.append("\n<s:complexType>")
			.append("\n<s:sequence>");
			
			for(W5WsServerMethodParam wsmp:wsm.get_params())if(wsmp.getOutFlag()==0 && wsmp.getParentWsMethodParamId()==0){
				buf.append("\n<s:element minOccurs=\"").append(wsmp.getNotNullFlag()!=0 ? "1":"0").append("\" maxOccurs=\"").append(wsmp.getParamTip()<10 ? "1":"unbounded").append("\" name=\"").append(wsmp.getDsc()).append("\" type=\"");
				if(wsmp.getParamTip()<9)buf.append("s:").append(elementTypes[wsmp.getParamTip()]);
				else {
					buf.append("tns:").append(wsm.getDsc()).append("_").append(wsmp.getDsc());
					buf2.append("\n<s:complexType name=\"").append(wsm.getDsc()).append("_").append(wsmp.getDsc()).append("\"><s:sequence>");
					for(W5WsServerMethodParam swsmp:wsm.get_params())if(swsmp.getParentWsMethodParamId()==wsmp.getWsServerMethodParamId()){
						buf2.append("\n<s:element minOccurs=\"").append(swsmp.getNotNullFlag()!=0 ? "1":"0").append("\" maxOccurs=\"1\" name=\"").append(swsmp.getDsc()).append("\" type=\"");
						if(swsmp.getParamTip()<9)buf2.append("s:").append(elementTypes[swsmp.getParamTip()]);
						else buf2.append("s:string");//TODO
						buf2.append("\" />");
					}
					buf2.append("</s:sequence></s:complexType>");
				}
				buf.append("\" />");
			}
			buf.append("\n</s:sequence>")
			.append("\n</s:complexType>")
			.append("\n</s:element>");
			
		}
		for(W5WsServerMethod wsm:ws.get_methods()){
			buf.append("<s:element name=\"").append(wsm.getDsc()).append("Response\">")
			.append("<s:complexType>")
			.append("<s:sequence>");
			for(W5WsServerMethodParam wsmp:wsm.get_params())if(wsmp.getOutFlag()!=0 && wsmp.getParentWsMethodParamId()==0){
				buf.append("\n<s:element minOccurs=\"0\" maxOccurs=\"").append(wsmp.getParamTip()<10 ? "1":"unbounded").append("\" name=\"").append(wsmp.getDsc()).append("\" type=\"");
				if(wsmp.getParamTip()<9)buf.append("s:").append(elementTypes[wsmp.getParamTip()]);
				else {
					buf.append("tns:").append(wsm.getDsc()).append("_").append(wsmp.getDsc());
					buf2.append("\n<s:complexType name=\"").append(wsm.getDsc()).append("_").append(wsmp.getDsc()).append("\"><s:sequence>");
					for(W5WsServerMethodParam swsmp:wsm.get_params())if(swsmp.getParentWsMethodParamId()==wsmp.getWsServerMethodParamId()){
						buf2.append("\n<s:element minOccurs=\"").append(swsmp.getNotNullFlag()!=0 ? "1":"0").append("\" maxOccurs=\"1\" name=\"").append(swsmp.getDsc()).append("\" type=\"");
						if(swsmp.getParamTip()<9)buf2.append("s:").append(elementTypes[swsmp.getParamTip()]);
						else buf2.append("s:string");//TODO
						buf2.append("\" />");
					}
					buf2.append("</s:sequence></s:complexType>");
				}
				buf.append("\" />");
			}
			buf.append("</s:sequence>")
			.append("</s:complexType>")
			.append("</s:element>");
		}
		
		buf.append(buf2);
    
		buf.append("</s:schema></wsdl:types>");
		
		for(W5WsServerMethod wsm:ws.get_methods()){
			buf.append("\n<wsdl:message name=\"").append(wsm.getDsc()).append("SoapIn\">")
			.append("<wsdl:part name=\"parameters\" element=\"tns:").append(wsm.getDsc()).append("\" />")
					.append("</wsdl:message>")
					.append("<wsdl:message name=\"").append(wsm.getDsc()).append("SoapOut\">")
					.append("<wsdl:part name=\"parameters\" element=\"tns:").append(wsm.getDsc()).append("Response\" />")
					.append("</wsdl:message>");
		}
  
  
		buf.append("\n<wsdl:portType name=\"").append(ws.getDsc()).append("Soap\">");
		for(W5WsServerMethod wsm:ws.get_methods()){
			buf.append("<wsdl:operation name=\"").append(wsm.getDsc()).append("\">")
			.append("<wsdl:documentation xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"></wsdl:documentation>")
			.append("<wsdl:input message=\"tns:").append(wsm.getDsc()).append("SoapIn\" />")
			.append("<wsdl:output message=\"tns:").append(wsm.getDsc()).append("SoapOut\" />")
			.append("</wsdl:operation>");
		}
		buf.append("</wsdl:portType>");
		
		buf.append("\n<wsdl:binding name=\"").append(ws.getDsc()).append("Soap\" type=\"tns:").append(ws.getDsc()).append("Soap\"><soap:binding transport=\"http://schemas.xmlsoap.org/soap/http\" />");
		for(W5WsServerMethod wsm:ws.get_methods()){
			buf.append("<wsdl:operation name=\"").append(wsm.getDsc()).append("\"><soap:operation soapAction=\"").append(FrameworkCache.getAppSettingStringValue(0, "ws_soap_server_url")).append(ws.getDsc()).append("/").append(wsm.getDsc()).append("\" style=\"document\" />")
			.append("<wsdl:input><soap:body use=\"literal\" /></wsdl:input>")
			.append("<wsdl:output><soap:body use=\"literal\" /></wsdl:output>")
			.append("</wsdl:operation>");
		}
		buf.append("</wsdl:binding>");
		
		
		buf.append("\n<wsdl:binding name=\"").append(ws.getDsc()).append("Soap12\" type=\"tns:").append(ws.getDsc()).append("Soap\"><soap12:binding transport=\"http://schemas.xmlsoap.org/soap/http\" />");
		for(W5WsServerMethod wsm:ws.get_methods()){
			buf.append("<wsdl:operation name=\"").append(wsm.getDsc()).append("\"><soap12:operation soapAction=\"").append(FrameworkCache.getAppSettingStringValue(0, "ws_soap_server_url")).append(ws.getDsc()).append("/").append(wsm.getDsc()).append("\" style=\"document\" />")
			.append("<wsdl:input><soap12:body use=\"literal\" /></wsdl:input>")
			.append("<wsdl:output><soap12:body use=\"literal\" /></wsdl:output>")
			.append("</wsdl:operation>");
		}
		buf.append("</wsdl:binding>");
  
  
		buf.append("\n<wsdl:service name=\"").append(ws.getDsc()).append("\"><wsdl:port name=\"").append(ws.getDsc()).append("Soap\" binding=\"tns:").append(ws.getDsc())
		.append("Soap\"><soap:address location=\"").append(FrameworkCache.getAppSettingStringValue(0, "ws_soap_server_url")).append(ws.getWsUrl()).append("\" /></wsdl:port>")
		.append("<wsdl:port name=\"").append(ws.getDsc()).append("Soap12\" binding=\"tns:").append(ws.getDsc()).append("Soap12\"><soap12:address location=\"").append(FrameworkCache.getAppSettingStringValue(0, "ws_soap_server_url")).append(ws.getWsUrl()).append("\" /></wsdl:port>")
		.append("</wsdl:service></wsdl:definitions>");
		
		return buf;
	}
	
/*	public	StringBuilder serializeSOAPResult(W5SOAPResult sr){
		StringBuilder buf = new StringBuilder();
		buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
		.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
		.append("<soap:Body>")
		.append("<").append(sr.getWsm().getDsc()).append("Response xmlns=\"http://tempuri.org/\">");

		switch(sr.getWsm().getObjectTip()){
		case	2://insert
			buf.append("<result>").append(sr.getOutputFields().values().iterator().next()).append("</result>");
			break;		
		case	19://queryResult
			List<W5WsServerMethodParam> params = new ArrayList<>();
			Map<Integer, W5QueryField> paramMap = new HashMap();
			W5Query q = sr.getQr().getQuery();
			for(W5WsServerMethodParam p:sr.getWsm().get_params())if(p.getDsc().equals("data") && p.getOutFlag()!=0){
				for(W5WsServerMethodParam p2:sr.getWsm().get_params())if(p2.getParentWsMethodParamId()==p.getWsServerMethodParamId()){
					params.add(p2);
					for(W5QueryField f:q.get_queryFields())if(f.getQueryFieldId()==p2.getObjectDetailId()){
						paramMap.put(p2.getWsServerMethodParamId(), f);
						break;
					}
				}
				break;
			}
			if(!params.isEmpty())for(Object[] o:sr.getQr().getData()){
				
				buf.append("<data>");
				for(W5WsServerMethodParam p:params){
					W5QueryField f = paramMap.get(p.getWsServerMethodParamId());
					if(f!=null){
						buf.append("<").append(p.getDsc()).append(">").append(o[f.getTabOrder()-1]).append("</").append(p.getDsc()).append(">");
					}
				}
				buf.append("</data>");
				
			}
			
			break;		
		
		}
		
		buf.append("</").append(sr.getWsm().getDsc()).append("Response>")
		.append("</soap:Body>")
		.append("</soap:Envelope>");
		return buf;
	}*/
	public	StringBuilder serializeGetFormSimple(W5WsServerMethod wsm, W5FormResult fr){
		return new StringBuilder();
	}
	public	StringBuilder serializePostForm(W5WsServerMethod wsm, W5FormResult fr){
		StringBuilder buf = new StringBuilder();
		if(!fr.getErrorMap().isEmpty()){
			return serializeException(new IWBException("validation",wsm.getDsc(), wsm.getWsServerMethodId(), null, GenericUtil.fromMapToJsonString(fr.getErrorMap()), null));
		}
		buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
		.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
		.append("<soap:Body>")
		.append("<").append(wsm.getDsc()).append("Response xmlns=\"http://tempuri.org/\">");
		buf.append("<result>").append(fr.getOutputFields().values().iterator().next()).append("</result>");
		buf.append("</").append(wsm.getDsc()).append("Response>")
		.append("</soap:Body>")
		.append("</soap:Envelope>");
		return buf;
	}
	public	StringBuilder serializeQueryData(W5WsServerMethod wsm, W5QueryResult qr){
		if(!qr.getErrorMap().isEmpty()){
			return serializeException(new IWBException("validation",wsm.getDsc(), wsm.getWsServerMethodId(), null, GenericUtil.fromMapToJsonString(qr.getErrorMap()), null));
		}
		return new StringBuilder();
	}
	public 	StringBuilder serializeDbFunc(W5WsServerMethod wsm, W5GlobalFuncResult dbFuncResult){
		return new StringBuilder();
	}

	public	StringBuilder serializeException(IWBException e){
		StringBuilder buf = new StringBuilder();
		buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
		.append("<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
		.append("<soap:Fault>")
		.append("<faultcode>").append(e.getErrorType()).append("</faultcode>")
		.append("<faultstring>").append(e.getMessage()).append("</faultstring>");

		buf.append("</soap:Fault>")
		.append("</soap:Envelope>");
		return buf;
		
	}

}
