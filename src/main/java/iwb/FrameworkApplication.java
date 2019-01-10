package iwb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import iwb.cache.FrameworkSetting;
import iwb.util.GenericUtil;
//import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@ServletComponentScan
/*@EnableRedisHttpSession*/
public class FrameworkApplication {

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(5);
		threadPoolTaskExecutor.setMaxPoolSize(10);
		threadPoolTaskExecutor.setQueueCapacity(25);
		return threadPoolTaskExecutor;
	}
	
	
	public static void main(String[] args) {
		if(args!=null && args.length>0) {
			for(int qi=0;qi<args.length;qi++){

				String arg = args[qi];
				if(!GenericUtil.isEmpty(arg)){
					String key = arg, value="1";
					if(arg.indexOf('=')>-1) {
						String[] oo = arg.replace('=', ',').split(",");
						key = oo[0];
						value = oo[1];
					}
					FrameworkSetting.argMap.put(key, value);
				}
			}
		
			String influxdb = FrameworkSetting.argMap.get("influxdb");
			if(influxdb!=null && !influxdb.equals("0")) {
				FrameworkSetting.log2tsdb = true;
				FrameworkSetting.log2tsdbUrl = influxdb.equals("1") ? "influxdb" : influxdb;
			}
			String logType = FrameworkSetting.argMap.get("logType");
			if(logType!=null)FrameworkSetting.logType = GenericUtil.uInt(logType);
		}
		ConfigurableApplicationContext appContext = SpringApplication.run(FrameworkApplication.class, args);
	}
}
