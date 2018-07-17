package iwb.custom.trigger;

import iwb.domain.result.W5DbFuncResult;
import iwb.util.FrameworkCache;
import iwb.util.FrameworkSetting;

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
