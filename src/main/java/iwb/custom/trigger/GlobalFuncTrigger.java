package iwb.custom.trigger;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.domain.result.W5GlobalFuncResult;

public class GlobalFuncTrigger {
	public static void beforeExec(W5GlobalFuncResult dbFuncResult){
		switch(dbFuncResult.getGlobalFuncId()){
		
		}
	}
	
	public static void afterExec(W5GlobalFuncResult dbFuncResult){
		switch(dbFuncResult.getGlobalFuncId()){
		
		}
	}
}
