package iwb.adapter.soap;

import java.util.Map;

import iwb.domain.db.W5WsServer;
import iwb.domain.db.W5WsServerMethod;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.domain.result.W5QueryResult;
import iwb.exception.IWBException;

public interface SoapAdapter {
	public	StringBuilder serializeSoapWSDL(W5WsServer wsServer, Map<String, Object> wsmoMap);
	public	StringBuilder serializeGetFormSimple(W5WsServerMethod wsm, W5FormResult fr);
	public	StringBuilder serializePostForm(W5WsServerMethod wsm, W5FormResult fr);
	public	StringBuilder serializeQueryData(W5WsServerMethod wsm, W5QueryResult qr);
	public 	StringBuilder serializeDbFunc(W5WsServerMethod wsm, W5GlobalFuncResult dbFuncResult);
	public	StringBuilder serializeException(IWBException e);

//	public	StringBuilder serializeWsServerMethodResult(W5WsServerMethodResult wsServerMethodResult);

}


