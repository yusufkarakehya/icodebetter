package iwb.custom.trigger;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.domain.result.W5DbFuncResult;

public class DbFuncTrigger {
	public static void beforeExecDbFunc(W5DbFuncResult dbFuncResult){
		switch(dbFuncResult.getDbFuncId()){
		
		}
	}
	
	public static void afterExecDbFunc(W5DbFuncResult dbFuncResult){
		switch(dbFuncResult.getDbFuncId()){
		case	886://pcustom_grid_col_render
		case    467://help form	
			if(FrameworkSetting.preloadWEngine!=0){
				FrameworkCache.clearPreloadCache();
			}
		}
	}
}
