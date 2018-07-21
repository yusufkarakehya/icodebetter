package iwb.util;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import iwb.domain.db.Log5Base;

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
	
	public static void logObject(Log5Base o){
		if(o==null)return;
		String str = o.toInfluxDB();
		if(GenericUtil.isEmpty(str))return;
		StringBuilder s=new StringBuilder(str);
		s.append(" ").append(Instant.now().toEpochMilli()).append("000000");
		try {
			if(FrameworkSetting.logType==2){ //Asynchronized RabbitMQ
				//mqChannel.basicPublish("", FrameworkSetting.mqTsdbQueue, null, s.toString().getBytes());
				mqChannel.basicPublish("", FrameworkSetting.log2mqQueue, null, s.toString().getBytes("UTF-8"));
			} else { //Synchronized
				Map m = new HashMap();
				m.put("Content-Type", "application/json");
				HttpUtil.send(FrameworkSetting.log2tsdbUrl+"/write?db="+FrameworkSetting.log2tsdbDbName,s.toString(),"POST", m);
			}
			errorCount = 0;
		}catch (Exception e) {
			errorCount++;
			if(errorCount % 100 == 1){//TODO 100 defada bir birseyler yap
				
			}
		}
	}
}