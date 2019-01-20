package iwb.util;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;

import iwb.cache.FrameworkSetting;
import iwb.domain.db.Log5Base;

public class LogUtil {
	private static int errorCount = 0;
	private static Channel mqChannel = null;
	
	public static void activateMQ4Log(){
		if(FrameworkSetting.logType!=2)return;
		try{
			mqChannel = MQUtil.getChannel4Queue(FrameworkSetting.log2mqUrl, FrameworkSetting.log2mqQueue);
		}catch(Exception e){
			if(FrameworkSetting.debug)e.printStackTrace();
		}
		if(mqChannel==null)FrameworkSetting.logType = 0;

	}	
	
	public static void logObject(Log5Base o){
		if(!FrameworkSetting.log2tsdb || o==null)return;
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
				String sq = HttpUtil.send(FrameworkSetting.log2tsdbUrl+"/write?db="+FrameworkSetting.log2tsdbDbName,s.toString(),"POST", m);
				if(FrameworkSetting.debug && !GenericUtil.isEmpty(sq))System.out.println("log2tsdb response: "+sq);
			}
			errorCount = 0;
		}catch (Exception e) {
			errorCount++;
			if(errorCount % 100 == 1){//TODO 100 defada bir birseyler yap
				
			}
		}
	}
	
	public static void logCrud(String str){
		if(str==null)return;
		StringBuilder s=new StringBuilder(str);
		s.append(" ").append(Instant.now().toEpochMilli()).append("000000");
		try {
			if(FrameworkSetting.logType==2){ //Asynchronized RabbitMQ
				//mqChannel.basicPublish("", FrameworkSetting.mqTsdbQueue, null, s.toString().getBytes());
				mqChannel.basicPublish("", FrameworkSetting.log2mqQueue, null, s.toString().getBytes("UTF-8"));
			} else { //Synchronized
				Map m = new HashMap();
				m.put("Content-Type", "application/json");
				HttpUtil.send(FrameworkSetting.log2tsdbUrl+"/write?db="+FrameworkSetting.log2tsdbDbName4Crud,s.toString(),"POST", m);
			}
			errorCount = 0;
		}catch (Exception e) {
			errorCount++;
			if(errorCount % 100 == 1){//TODO 100 defada bir birseyler yap
				
			}
		}
	}

	public static void logVcs(String str) {
		if(str==null)return;
		StringBuilder s=new StringBuilder(str);
		s.append(" ").append(Instant.now().toEpochMilli()).append("000000");
		try {
			if(FrameworkSetting.logType==2){ //Asynchronized RabbitMQ
				//mqChannel.basicPublish("", FrameworkSetting.mqTsdbQueue, null, s.toString().getBytes());
				mqChannel.basicPublish("", FrameworkSetting.log2mqQueue, null, s.toString().getBytes("UTF-8"));
			} else { //Synchronized
				Map m = new HashMap();
				m.put("Content-Type", "application/json");
				HttpUtil.send(FrameworkSetting.log2tsdbUrl+"/write?db="+FrameworkSetting.log2tsdbDbName4Vcs,s.toString(),"POST", m);
			}
			errorCount = 0;
		}catch (Exception e) {
			errorCount++;
			if(errorCount % 100 == 1){//TODO 100 defada bir birseyler yap
				
			}
		}	
	}
}