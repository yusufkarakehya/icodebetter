package iwb.adapter.soap.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iwb.adapter.soap.SoapAdapter;
import iwb.cache.FrameworkCache;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5GlobalFuncParam;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5QueryParam;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableField;
import iwb.domain.db.W5TableParam;
import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.domain.db.W5WsServerMethodParam;
import iwb.domain.helper.W5FormCellHelper;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5QueryResult;
import iwb.exception.IWBException;
import iwb.util.GenericUtil;
// xmlns:apachesoap="http://xml.apache.org/xml-soap" xmlns:impl="http://bar.foo"
// xmlns:intf="http://bar.foo" xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
// xmlns:wsdlsoap="http://schemas.xmlsoap.org/wsdl/soap/"
// xmlns:xsd="http://www.w3.org/2001/XMLSchema"
public class AxisSoap1_4 implements SoapAdapter {
  static final String[] elementTypes =
      new String[] {
        "", "string", "string", "decimal", "int", "boolean", "string", "string", "string"
      };
  static final String[] elementTypes2 =
      new String[] {
        "", "string", "string", "decimal", "string", "string", "string", "string", "string"
      };

  public StringBuilder serializeSoapWSDL(W5WsServer ws, Map<String, Object> wsmoMap) {
    String wsSoapUrl = FrameworkCache.getAppSettingStringValue(0, "ws_soap_server_url", "");

    StringBuilder buf = new StringBuilder();
    buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        .append("<wsdl:definitions targetNamespace=\"")
        .append(ws.getTargetNamespace())
        .append("\" xmlns:apachesoap=\"http://xml.apache.org/xml-soap\" xmlns:impl=\"")
        .append(ws.getTargetNamespace())
        .append("\" xmlns:intf=\"")
        .append(ws.getTargetNamespace())
        .append(
            "\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:wsdlsoap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
        .append("\n<wsdl:documentation>Version: 1.3</wsdl:documentation>")
        .append("\n<wsdl:types><schema elementFormDefault=\"qualified\" targetNamespace=\"")
        .append(ws.getTargetNamespace())
        .append("\" xmlns=\"http://www.w3.org/2001/XMLSchema\">");
    StringBuilder buf2 = new StringBuilder();
    for (W5WsServerMethod wsm : ws.get_methods()) {
      List<W5WsServerMethodParam> lwsmp = new ArrayList();
      if (wsm.getObjectTip() != 4 || wsm.getObjectId() != 3) {
        W5WsServerMethodParam tokenKey = new W5WsServerMethodParam(-998, "tokenKey", (short) 1);
        tokenKey.setOutFlag((short) 0);
        tokenKey.setNotNullFlag((short) 1);
        lwsmp.add(tokenKey);
      }
      buf.append("\n<element name=\"").append(wsm.getDsc()).append("\"><complexType><sequence>");
      W5Table t = null;
      Object o = wsmoMap.get(wsm.getDsc());
      if (o == null) { // TODO ne yapilabilir?
        buf.append("</sequence></complexType></element>");
        continue;
      } else if (o instanceof String) { // TODO ne yapilabilir?
        buf.append("</sequence></complexType></element>");
        continue;
      } else
        switch (wsm.getObjectTip()) {
          case 0:
          case 1:
          case 2:
          case 3:
            W5FormResult fr = (W5FormResult) o;
            lwsmp.add(new W5WsServerMethodParam(-999, "result", (short) 9));
            t = FrameworkCache.getTable(ws.getProjectUuid(), fr.getForm().getObjectId());
            for (W5TableParam tp : t.get_tableParamList())
              if (tp.getSourceTip() == 1)
                lwsmp.add(
                    new W5WsServerMethodParam(
                        tp,
                        (short) (wsm.getObjectTip() == 2 ? 1 : 0),
                        wsm.getObjectTip() == 2 ? -999 : 0));
            if (wsm.getObjectTip() != 3)
              for (W5FormCell fc : fr.getForm().get_formCells())
                if (fc.getActiveFlag() != 0
                    && fc.get_sourceObjectDetail() != null
                    && fc.getSourceTip() == 1
                    && ((wsm.getObjectTip() == 1
                            && ((W5TableField) fc.get_sourceObjectDetail()).getCanUpdateFlag() == 1)
                        || (wsm.getObjectTip() == 2
                            && ((W5TableField) fc.get_sourceObjectDetail()).getCanInsertFlag()
                                == 1))) {
                  lwsmp.add(
                      new W5WsServerMethodParam(
                          fc,
                          (short) (wsm.getObjectTip() == 0 ? 1 : 0),
                          wsm.getObjectTip() == 0 ? -999 : 0));
                }
            W5WsServerMethodParam outMsg = new W5WsServerMethodParam(-999, "outMsg", (short) 1);
            outMsg.setParentWsMethodParamId(-999);
            lwsmp.add(outMsg);
            break;
          case 4:
            W5GlobalFuncResult dfr = (W5GlobalFuncResult) o;
            for (W5GlobalFuncParam dfp : dfr.getGlobalFunc().get_dbFuncParamList())
              if (dfp.getSourceTip() == 1 && dfp.getOutFlag() != 0) {
                lwsmp.add(new W5WsServerMethodParam(-999, "result", (short) 9));
                break;
              }
            for (W5GlobalFuncParam dfp : dfr.getGlobalFunc().get_dbFuncParamList())
              if (dfp.getSourceTip() == 1) {
                lwsmp.add(
                    new W5WsServerMethodParam(
                        dfp, dfp.getOutFlag(), dfp.getOutFlag() == 0 ? 0 : -999));
              }
            break;
          case 19:
            if (wsm.get_params() != null) {
              lwsmp = wsm.get_params();
            } else {
              W5QueryResult qr = (W5QueryResult) o;
              lwsmp.add(new W5WsServerMethodParam(-999, "data", (short) 10));
              if (qr.getQuery().getMainTableId() != 0)
                t = FrameworkCache.getTable(ws.getProjectUuid(), qr.getQuery().getMainTableId());
              for (W5QueryParam qp : qr.getQuery().get_queryParams())
                if (qp.getSourceTip() == 1) {
                  lwsmp.add(new W5WsServerMethodParam(qp, (short) 0, 0));
                }
              for (W5QueryField qf : qr.getQuery().get_queryFields()) {
                lwsmp.add(new W5WsServerMethodParam(qf, (short) 1, -999));
              }
            }
            break;
        }
      wsm.set_params(lwsmp);
      for (W5WsServerMethodParam wsmp : wsm.get_params())
        if (wsmp.getOutFlag() == 0 && wsmp.getParentWsMethodParamId() == 0) {
          buf.append("\n<element")
              .append(wsmp.getNotNullFlag() != 0 ? "" : " nillable=\"true\"")
              .append(wsmp.getParamTip() < 10 ? "" : " maxOccurs=\"unbounded\"")
              .append(" name=\"")
              .append(wsmp.getDsc())
              .append("\" type=\"");
          if (wsmp.getParamTip() < 9)
            buf.append("xsd:")
                .append(
                    wsmp.getNotNullFlag() == 0
                        ? elementTypes2[wsmp.getParamTip()]
                        : elementTypes[wsmp.getParamTip()]);
          else {
            buf.append("impl:").append(wsm.getDsc()).append("_").append(wsmp.getDsc());
            buf2.append("\n<complexType name=\"")
                .append(wsm.getDsc())
                .append("_")
                .append(wsmp.getDsc())
                .append("\"><sequence>");
            for (W5WsServerMethodParam swsmp : wsm.get_params())
              if (swsmp.getParentWsMethodParamId() == wsmp.getWsServerMethodParamId()) {
                buf2.append("\n<element")
                    .append(swsmp.getNotNullFlag() != 0 ? "" : " nillable=\"true\"")
                    .append(" name=\"")
                    .append(swsmp.getDsc())
                    .append("\" type=\"");
                if (swsmp.getParamTip() < 9)
                  buf2.append("xsd:")
                      .append(
                          swsmp.getNotNullFlag() == 0
                              ? elementTypes2[swsmp.getParamTip()]
                              : elementTypes[swsmp.getParamTip()]);
                else buf2.append("string"); // TODO
                buf2.append("\" />");
              }
            buf2.append("</sequence></complexType>");
          }
          buf.append("\" />");
        }
      buf.append("\n</sequence></complexType></element>");
    }
    for (W5WsServerMethod wsm : ws.get_methods()) {
      buf.append("\n<element name=\"")
          .append(wsm.getDsc())
          .append("Response\">")
          .append("<complexType>")
          .append("<sequence>");
      for (W5WsServerMethodParam wsmp : wsm.get_params())
        if (wsmp.getOutFlag() != 0 && wsmp.getParentWsMethodParamId() == 0) {
          buf.append("\n<element")
              .append(wsmp.getParamTip() < 10 ? "" : " maxOccurs=\"unbounded\"")
              .append(" name=\"")
              .append(wsm.getDsc())
              .append("Result\" type=\"");
          if (wsmp.getParamTip() < 9) buf.append("xsd:").append(elementTypes[wsmp.getParamTip()]);
          else {
            buf.append("impl:").append(wsm.getDsc()).append("_").append(wsmp.getDsc());
            buf2.append("\n<complexType name=\"")
                .append(wsm.getDsc())
                .append("_")
                .append(wsmp.getDsc())
                .append("\"><sequence>");
            for (W5WsServerMethodParam swsmp : wsm.get_params())
              if (swsmp.getParentWsMethodParamId() == wsmp.getWsServerMethodParamId()) {
                buf2.append("\n<element")
                    .append(swsmp.getNotNullFlag() != 0 ? "" : " nillable=\"true\"")
                    .append(" name=\"")
                    .append(swsmp.getDsc())
                    .append("\" type=\"");
                if (swsmp.getParamTip() < 9)
                  buf2.append("xsd:").append(elementTypes[swsmp.getParamTip()]);
                else buf2.append("string"); // TODO
                buf2.append("\" />");
              }
            buf2.append("</sequence></complexType>");
          }
          buf.append("\" />");
        }
      buf.append("</sequence>").append("</complexType>").append("</element>");
    }

    buf.append(buf2);

    buf.append("</schema></wsdl:types>");

    for (W5WsServerMethod wsm : ws.get_methods()) {
      buf.append("\n<wsdl:message name=\"")
          .append(wsm.getDsc())
          .append("Request\">")
          .append("<wsdl:part name=\"parameters\" element=\"impl:")
          .append(wsm.getDsc())
          .append("\" />")
          .append("</wsdl:message>")
          .append("<wsdl:message name=\"")
          .append(wsm.getDsc())
          .append("Response\">")
          .append("<wsdl:part name=\"parameters\" element=\"impl:")
          .append(wsm.getDsc())
          .append("Response\" />")
          .append("</wsdl:message>");
    }

    buf.append("\n<wsdl:portType name=\"").append(ws.getDsc()).append("\">");
    for (W5WsServerMethod wsm : ws.get_methods()) {
      buf.append("\n<wsdl:operation name=\"")
          .append(wsm.getDsc())
          .append("\">")
          //			.append("<wsdl:documentation
          // xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\"></wsdl:documentation>")
          .append("<wsdl:input message=\"impl:")
          .append(wsm.getDsc())
          .append("Request\" name=\"")
          .append(wsm.getDsc())
          .append("Request\"/>")
          .append("<wsdl:output message=\"impl:")
          .append(wsm.getDsc())
          .append("Response\" name=\"")
          .append(wsm.getDsc())
          .append("Response\"/>")
          .append("</wsdl:operation>");
    }
    buf.append("</wsdl:portType>");

    buf.append("\n<wsdl:binding name=\"")
        .append(ws.getDsc())
        .append("SoapBinding\" type=\"impl:")
        .append(ws.getDsc())
        .append(
            "\"><wsdlsoap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>");
    for (W5WsServerMethod wsm : ws.get_methods()) {
      buf.append("\n<wsdl:operation name=\"")
          .append(wsm.getDsc())
          .append("\"><wsdlsoap:operation soapAction=\"\"/>")
          .append("<wsdl:input name=\"")
          .append(wsm.getDsc())
          .append("Request\"><wsdlsoap:body use=\"literal\" /></wsdl:input>")
          .append("<wsdl:output name=\"")
          .append(wsm.getDsc())
          .append("Response\"><wsdlsoap:body use=\"literal\" /></wsdl:output>")
          .append("</wsdl:operation>");
    }
    buf.append("</wsdl:binding>");

    buf.append("\n<wsdl:service name=\"")
        .append(ws.getDsc())
        .append("Service\"><wsdl:port name=\"")
        .append(ws.getDsc())
        .append("\" binding=\"impl:")
        .append(ws.getDsc())
        .append("SoapBinding\"><wsdlsoap:address location=\"")
        .append(wsSoapUrl)
        .append(ws.getWsUrl())
        .append("\" /></wsdl:port>")
        .append("</wsdl:service></wsdl:definitions>");

    return buf;
  }

  /*	public	StringBuilder serializeWSResult(W5WSResult sr){
  	StringBuilder buf = new StringBuilder();
  	buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
  	.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
  	.append("<soap:Body>")
  	.append("<").append(sr.getWsm().getDsc()).append("Response xmlns=\"http://www.iworkbetter.com/\">");

  	switch(sr.getWsm().getObjectTip()){
  	case	2://insert
  		buf.append("<result>").append(sr.getOutputFields().values().iterator().next()).append("</result>");
  		break;
  	case	19://queryResult
  		List<W5WsServerMethodParam> params = new ArrayList();
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
  public StringBuilder serializeGetFormSimple(W5WsServerMethod wsm, W5FormResult fr) {
    if (!fr.getErrorMap().isEmpty()) {
      return serializeException(
          new IWBException(
              "validation",
              wsm.getDsc(),
              wsm.getWsServerMethodId(),
              null,
              GenericUtil.fromMapToJsonString(fr.getErrorMap()),
              null));
    }
    StringBuilder buf = new StringBuilder();
    buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        .append(
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
        .append("<soap:Body>")
        .append("<")
        .append(wsm.getDsc())
        .append("Response xmlns=\"http://www.iworkbetter.com\">");
    buf.append("\n<").append(wsm.getDsc()).append("_result>");
    for (W5FormCellHelper fc : fr.getFormCellResults()) {
      if (fc.getFormCell().getActiveFlag() != 0
          && fc.getFormCell().get_sourceObjectDetail() != null
          && fc.getFormCell().getControlTip() < 100) {
        String oo = fc.getValue();
        if (oo == null) oo = "";
        buf.append("\n<")
            .append(fc.getFormCell().getDsc())
            .append(">")
            .append(GenericUtil.strToSoap(oo))
            .append("</")
            .append(fc.getFormCell().getDsc())
            .append(">");
      }
    }
    buf.append("</").append(wsm.getDsc()).append("_result>");
    buf.append("</")
        .append(wsm.getDsc())
        .append("Response>")
        .append("\n</soap:Body>")
        .append("</soap:Envelope>");
    return buf;
  }

  public StringBuilder serializePostForm(W5WsServerMethod wsm, W5FormResult fr) {
    if (!fr.getErrorMap().isEmpty()) {
      return serializeException(
          new IWBException(
              "validation",
              wsm.getDsc(),
              wsm.getWsServerMethodId(),
              null,
              GenericUtil.fromMapToJsonString(fr.getErrorMap()),
              null));
    }
    StringBuilder buf = new StringBuilder();
    buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        .append(
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
        .append("<soap:Body>")
        .append("<")
        .append(wsm.getDsc())
        .append("Response xmlns=\"http://www.iworkbetter.com\">");
    buf.append("\n<").append(wsm.getDsc()).append("_result>");
    if (fr.getAction() == 2) { // insert
      W5Table t = FrameworkCache.getTable(wsm.getProjectUuid(), fr.getForm().getObjectId());
      for (W5TableParam tp : t.get_tableParamList())
        if (tp.getSourceTip()
            == 1) // lwsmp.add(new W5WsServerMethodParam(tp, (short)(wsm.getObjectTip()==2 ?
                  // 1:0),wsm.getObjectTip()==2?-999:0));
        buf.append("<")
              .append(tp.getDsc())
              .append(">")
              .append(
                  GenericUtil.strToSoap(fr.getOutputFields().get(tp.getExpressionDsc()).toString()))
              .append("</")
              .append(tp.getDsc())
              .append(">");
    }
    if (!GenericUtil.isEmpty(fr.getOutputMessages())) {
      buf.append("<outMsg>")
          .append(GenericUtil.strToSoap(fr.getOutputMessages().get(0)))
          .append("</outMsg>");
    }
    buf.append("</").append(wsm.getDsc()).append("_result>");
    buf.append("</")
        .append(wsm.getDsc())
        .append("Response>")
        .append("\n</soap:Body>")
        .append("</soap:Envelope>");
    return buf;
  }

  public StringBuilder serializeQueryData(W5WsServerMethod wsm, W5QueryResult qr) {
    if (!qr.getErrorMap().isEmpty()) {
      return serializeException(
          new IWBException(
              "validation",
              wsm.getDsc(),
              wsm.getWsServerMethodId(),
              null,
              GenericUtil.fromMapToJsonString(qr.getErrorMap()),
              null));
    }
    StringBuilder buf = new StringBuilder();
    buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        .append(
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
        .append("<soap:Body>")
        .append("<")
        .append(wsm.getDsc())
        .append("Response xmlns=\"http://www.iworkbetter.com\">");
    //		buf.append("<data>");
    List<W5QueryField> lqf = null;
    if (wsm.get_params() != null) {
      lqf = new ArrayList();
      Map<String, W5QueryField> qfm = new HashMap();
      for (W5QueryField qf : qr.getQuery().get_queryFields()) {
        qfm.put(qf.getDsc(), qf);
      }
      for (W5WsServerMethodParam wsmp : wsm.get_params())
        if (wsmp.getOutFlag() != 0 && wsmp.getParamTip() != 10) {
          lqf.add(qfm.get(wsmp.getDsc()));
        }
    }

    if (!GenericUtil.isEmpty(qr.getData()))
      for (Object[] oo : qr.getData()) {
        buf.append("\n<").append(wsm.getDsc()).append("Result>");
        if (wsm.get_params() == null) {
          for (W5QueryField qf : qr.getQuery().get_queryFields()) {
            Object oz = oo[qf.getTabOrder() - 1];
            if (oz == null) oz = "";
            buf.append("<")
                .append(qf.getDsc())
                .append(">")
                .append(GenericUtil.strToSoap(oz.toString()))
                .append("</")
                .append(qf.getDsc())
                .append(">");
          }
        } else {
          for (W5QueryField qf : lqf) {
            Object oz = oo[qf.getTabOrder() - 1];
            if (oz == null) oz = "";
            buf.append("<")
                .append(qf.getDsc())
                .append(">")
                .append(GenericUtil.strToSoap(oz.toString()))
                .append("</")
                .append(qf.getDsc())
                .append(">");
          }
        }
        buf.append("</").append(wsm.getDsc()).append("Result>");
      }
    //		buf.append("</data>");
    buf.append("</")
        .append(wsm.getDsc())
        .append("Response>")
        .append("</soap:Body>")
        .append("</soap:Envelope>");
    return buf;
  }

  public StringBuilder serializeDbFunc(W5WsServerMethod wsm, W5GlobalFuncResult dfr) {
    if (!dfr.getErrorMap().isEmpty()) {
      return serializeException(
          new IWBException(
              "validation",
              wsm.getDsc(),
              wsm.getWsServerMethodId(),
              null,
              GenericUtil.fromMapToJsonString(dfr.getErrorMap()),
              null));
    }
    StringBuilder buf = new StringBuilder();
    buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        .append(
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
        .append("<soap:Body>")
        .append("<")
        .append(wsm.getDsc())
        .append("Response xmlns=\"http://www.iworkbetter.com\">")
        .append("<")
        .append(wsm.getDsc())
        .append("Result>");
    for (W5GlobalFuncParam p : dfr.getGlobalFunc().get_dbFuncParamList())
      if (p.getOutFlag() != 0) {
        Object o = dfr.getResultMap().get(p.getDsc());
        if (o == null) o = "";
        buf.append("<")
            .append(p.getDsc())
            .append(">")
            .append(o.toString())
            .append("</")
            .append(p.getDsc())
            .append(">");
      }
    buf.append("</")
        .append(wsm.getDsc())
        .append("Result></")
        .append(wsm.getDsc())
        .append("Response>") // </").append(wsm.getDsc()).append("_result>
        .append("</soap:Body>")
        .append("</soap:Envelope>");
    return buf;
  }

  public StringBuilder serializeException(IWBException e) {
    StringBuilder buf = new StringBuilder();
    buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        .append(
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">")
        .append("<soap:Fault>")
        .append("<faultcode>")
        .append(e.getErrorType())
        .append("</faultcode>")
        .append("<faultstring>")
        .append(e.getMessage())
        .append("</faultstring>");

    buf.append("</soap:Fault>").append("</soap:Envelope>");
    return buf;
  }
}
