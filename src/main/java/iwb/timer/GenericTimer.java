package iwb.timer;

import java.util.Map;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.domain.db.W5JobSchedule;
import iwb.domain.db.W5Project;
import iwb.service.FrameworkService;

public class GenericTimer extends TimerTask {
	
	
	public GenericTimer(TaskExecutor taskExecutor, FrameworkService service) {
		super();
		this.taskExecutor = taskExecutor;
		this.service = service;
	}

	private FrameworkService service;
	
	private TaskExecutor taskExecutor;

	private void checkJobs(){
		for(String projectId:FrameworkCache.wJobs.keySet()) {
			W5Project po = FrameworkCache.getProject(projectId);
			if(po!=null && (po.getCustomizationId()==0 || po.getCustomizationId()==140 || !FrameworkSetting.cloud)) {
				Map<Integer, W5JobSchedule> miv = FrameworkCache.wJobs.get(projectId);
				if(miv!=null)for(W5JobSchedule j:miv.values()) {
					if(j.runCheck()) { //
						taskExecutor.execute(new Runnable() {
				            @Override
				            public void run() {
				            	System.out.println("la la land");
				            	service.runJob(j);
				            }
				        });
					}
				}
			}
			
		}

	}

	public void run() {
		System.out.println("timer is running");
		checkJobs();
		checkWorkflowEscalations();
	}

	private void checkWorkflowEscalations() {
		// TODO Auto-generated method stub
		
	}
}
