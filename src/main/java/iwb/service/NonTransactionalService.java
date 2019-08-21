package iwb.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import iwb.cache.FrameworkCache;
import iwb.cache.FrameworkSetting;
import iwb.domain.db.Log5JobAction;
import iwb.domain.db.Log5Transaction;
import iwb.domain.db.W5JobSchedule;
import iwb.domain.db.W5Project;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.engine.GlobalScriptEngine;
import iwb.util.GenericUtil;
import iwb.util.LogUtil;

@Service
public class NonTransactionalService {
	@Lazy
	@Autowired
	private GlobalScriptEngine scriptEngine;
	
	
	public boolean runJob(W5JobSchedule job) {

		job.set_running(true);
		W5GlobalFuncResult res = null;
		String transactionId =  GenericUtil.getTransactionId();
//		if(FrameworkSetting.logType>0)LogUtil.logObject(new Log5Transaction(job.getProjectUuid(), "job", transactionId), true);

		Log5JobAction logJob = new Log5JobAction(job.getJobScheduleId(), job.getProjectUuid(), transactionId);
		try {// fonksiyon çalıştırılacak ise
			Map<String, String> requestParams = new HashMap<String, String>();
			requestParams.put("_trid_", transactionId);

			Map<String, Object> scd = new HashMap<String, Object>();
			W5Project po = FrameworkCache.getProject(job.getProjectUuid());
			scd.put("projectId", job.getProjectUuid());
			scd.put("locale", job.getLocale());
			scd.put("customizationId", po.getCustomizationId());
			scd.put("userRoleId", job.get_userRoleId());
			scd.put("roleId", job.getExecuteRoleId());
			scd.put("userId", job.getExecuteUserId());
			scd.put("administratorFlag", 1);
			res = scriptEngine.executeGlobalFunc(scd, job.getActionDbFuncId(), requestParams, (short) 7);
			if (FrameworkSetting.debug && res.isSuccess()) {
				System.out.println("Scheduled function is executed (funcId=" + job.getActionDbFuncId() + ")");
			}
		} catch (Exception e) {
			if (FrameworkSetting.debug)
				e.printStackTrace();
			logJob.setError(e.getMessage());
			return false;
		} finally {
			job.set_running(false);
			LogUtil.logObject(logJob, false);
		}
		return res.isSuccess();
	}
	
	public W5GlobalFuncResult executeFunc(Map<String, Object> scd, int dbFuncId, Map<String, String> parameterMap,
			short accessSourceType) {
		return scriptEngine.executeGlobalFunc(scd, dbFuncId, parameterMap, accessSourceType);

	}
}
