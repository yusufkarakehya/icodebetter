package iwb.util;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import iwb.domain.db.Log5DbFuncAction;
import iwb.domain.db.Log5QueryAction;
import iwb.domain.db.Log5VisitedPage;
import iwb.exception.IWBException;

public class LogUtil {
	private static int errorCount = 0;
	private static Channel mqChannel = null;
	
	public static void activateMQ(){
		if(FrameworkSetting.logType!=2)return;
		try{
		    ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost(FrameworkSetting.log2mqUrl);
		    Connection connection = factory.newConnection();
		    Channel channel = connection.createChannel();

		    channel.queueDeclare(FrameworkSetting.log2mqQueue, false, false, false, null);
			mqChannel = channel;
		}catch(Exception e){
			FrameworkSetting.logType = 0;
			e.printStackTrace();
		}

	}	
	
	public static void logObject(String o){
		if(GenericUtil.isEmpty(o))return;
		StringBuilder s=new StringBuilder();
	/*	if(o instanceof Log5QueryAction){
			Log5QueryAction l = (Log5QueryAction)o;
			switch(l.getQueryId()){
			case -999:s.append("sql_query duration=").append(l.getProcessTime()).append("i,sql=\"").append(GenericUtil.stringToJS2(l.getDsc())).append("\"");
				break;
			case -998:s.append("sql_execute duration=").append(l.getProcessTime()).append("i,sql=\"").append(GenericUtil.stringToJS2(l.getDsc())).append("\"");
				break;
			default:
				s.append("query,query_id=").append(l.getQueryId()).append(" user_id=").append(l.getUserId()).append("i,duration=").append(l.getProcessTime()).append("i,sql=\"").append(GenericUtil.stringToJS2(l.getDsc())).append("\"");

			}
				
		} else if(o instanceof Log5DbFuncAction){
			Log5DbFuncAction l = (Log5DbFuncAction)o;
			s.append("db_script,db_func_id=").append(l.getDbFuncId()).append(" user_id=").append(l.getUserId()).append("i,duration=").append(l.getProcessTime()).append("i,sql=\"").append(GenericUtil.stringToJS2(l.getDsc())).append("\"");
		} else if(o instanceof Log5VisitedPage){
			Log5VisitedPage l = (Log5VisitedPage)o;
			s.append("visited_page,page_name=\"").append(l.getPageName()).append("\",page_id=").append(l.getPageId()).append("i user_id=").append(l.getScd().get("userId")).append("i,duration=").append(l.getDuration()).append("i,ip=\"").append(l.getIp()).append("\"");
		} else if(o instanceof IWBException){
			IWBException l = (IWBException)o;
			s.append("exception,error_type=\"").append(l.getErrorType()).append("\" object_type=\"").append(l.getObjectType()).append("\",object_id=").append(l.getObjectId()).append("i");
			if(l.getMessage()!=null)s.append(",message=\"").append(GenericUtil.stringToJS2(l.getMessage())).append("\"");
			if(l.getSql()!=null)s.append(",sql=\"").append(GenericUtil.stringToJS2(l.getSql())).append("\"");
//			if(l.getScd()!=null)s.append(",user_id=").append(l.getScd().get("userId")).append("i,role_id=").append(l.getScd().get("roleId")).append("i");
		} else return; //not supported*/
		s.append(o).append(" ").append(Instant.now().toEpochMilli()).append("000000");
		try {
			if(FrameworkSetting.logType==2){ //Asynchronized RabbitMQ
				//mqChannel.basicPublish("", FrameworkSetting.mqTsdbQueue, null, s.toString().getBytes());
				mqChannel.basicPublish("", FrameworkSetting.log2mqQueue, null, s.toString().getBytes("UTF-8"));
			} else { //Synchronized
				HttpUtil.send(FrameworkSetting.log2tsdbUrl+"/write?db="+FrameworkSetting.log2tsdbDbName, s.toString());
			}
			errorCount = 0;
		}catch (Exception e) {
			errorCount++;
			if(errorCount % 100 == 1){//TODO 100 defada bir birseyler yap
				
			}
		}
	}
}