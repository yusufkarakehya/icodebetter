package iwb.timer;

import java.util.Map;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import iwb.service.FrameworkService;

public class IWBTimer extends TimerTask {
	@Autowired
	private FrameworkService bus;
	
	@Autowired
	private SimpleAsyncTaskExecutor taskExecutor;

	private void checkAlarms(){
	/*	boolean soDebugFlag = FrameworkCache.getAppSettingIntValue(0, "system_optimizer_debug_flag")!=0;
		if(soDebugFlag)System.out.println("system optimizer debug: start");
		
		if (FrameworkSetting.debug)try {
			// ana server'da calismasi icin sadece bir check
			InetAddress addr = InetAddress.getLocalHost();
			String serverHost = FrameworkCache.getAppSettingStringValue(0, "server_ip");
			if(soDebugFlag)System.out.println("system optimizer debug: ana server'da calismasi icin sadece bir check, server_ip = " + serverHost);
			byte[] arr = addr.getAddress();
			if (serverHost != null && arr != null) {
				String q = "";
				for (int qi = 0; qi < arr.length; qi++)
					q += "." + (arr[qi] < 0 ? (256 + arr[qi]) : arr[qi]);
				if(soDebugFlag)System.out.println("system optimizer debug: current ip = "+q.substring(1));
				if (!q.substring(1).equals(serverHost))
					return;
			} else
				return;
		} catch (UnknownHostException e1) {
			if (FrameworkSetting.debug)
				e1.printStackTrace();
			return;
		}
		
		try {
			Map<String, Object> scd = new HashMap<String, Object>();
			int customizationId=FrameworkCache.getAppSettingIntValue(0,"default_customization_id");			
			scd.put("customizationId",customizationId);
			scd.put("userId", 1);
			scd.put("userTip", 2);
			Map<String, String> parameterMap = new HashMap<String, String>();
			

			for(W5Customization bd : FrameworkCache.wCustomization){//Customization bazlÄ± islemler 
				customizationId=bd.getCustomizationId();
				//scd.put("customizationId", customizationId);
				
				Map<String, Object> scdx = new HashMap<String, Object>();
				scdx.clear();
				scdx.put("customizationId",customizationId);
				scdx.put("userId", -1);
				scdx.put("userTip", 2);
				
				if(FrameworkSetting.alarm && FrameworkCache.getAppSettingIntValue(customizationId, "alarm_flag")!=0){
					taskExecutor.execute(new executeCheckAlarms(scdx));		
				}				
			}
			
			
		} catch (Exception e) {
			if (FrameworkSetting.debug)
				e.printStackTrace();
		}		
	*/
	}

	public void run() {
		if(true)return;
		//if(FrameworkSetting.mq)UserUtil.mqPublishOnlineUsers();
		checkAlarms();
		
		
		
	}
    

	private class executeCheckAlarms implements Runnable {
		private Map<String, Object> scd;

		@Override
		public void run() {
//			PromisSetting.alarmIntervalCount=-10000;
			bus.checkAlarms(scd);
		}

		public executeCheckAlarms(Map<String, Object> scd) {
			this.scd = scd;
		}
	}
	
}
