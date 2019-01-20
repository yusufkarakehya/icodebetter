package iwb.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import iwb.cache.FrameworkSetting;
import iwb.cache.LocaleMsgCache;
import iwb.custom.trigger.GlobalFuncTrigger;
import iwb.dao.rdbms_impl.MetadataLoaderDAO;
import iwb.dao.rdbms_impl.PostgreSQL;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableEvent;
import iwb.domain.result.W5FormResult;
import iwb.domain.result.W5GlobalFuncResult;
import iwb.exception.IWBException;
import iwb.service.FrameworkService;
import iwb.util.GenericUtil;
import iwb.util.RhinoContextFactory;

@Component
public class ScriptEngine {
	@Lazy
	@Autowired
	private PostgreSQL dao;

	@Lazy
	@Autowired
	private MetadataLoaderDAO metaDataDao;

	
	@Lazy
	@Autowired
	private ConversionEngine conversionEngine;

	@Lazy
	@Autowired
	private QueryEngine queryEngine;

	@Lazy
	@Autowired
	private WorkflowEngine workflowEngine;
	
	
	@Lazy
	@Autowired 
	private AccessControlEngine acEngine;

	
	@Lazy
	@Autowired 
	private FrameworkService frameworkEngine;
	
	public W5GlobalFuncResult executeFunc(Map<String, Object> scd, int globalFuncId, Map<String, String> parameterMap,
			short accessSourceType) {

		W5GlobalFuncResult globalFuncResult = null;

		globalFuncResult = metaDataDao.getGlobalFuncResult(scd, globalFuncId);
		if (!GenericUtil.isEmpty(globalFuncResult.getGlobalFunc().getAccessSourceTypes())
				&& !GenericUtil.hasPartInside2(globalFuncResult.getGlobalFunc().getAccessSourceTypes(), accessSourceType))
			throw new IWBException("security", "GlobalFunc", globalFuncId, null, "Access Source Type Control", null);
		/*
		 * if(execRestrictTip!=4 && checkAccessRecordControlViolation(scd, 4,
		 * 20, ""+dbFuncId)) throw new PromisException("security",
		 * "DbProc Execute2", dbFuncId, null, "Access Execute Control", null);
		 */
		dao.checkTenant(scd);
		globalFuncResult.setErrorMap(new HashMap());
		globalFuncResult.setRequestParams(parameterMap);
		GlobalFuncTrigger.beforeExec(globalFuncResult);

		dao.executeGlobalFunc(globalFuncResult, "");

		if (globalFuncResult.getErrorMap().isEmpty()) { // sorun yok
			// post sms
			if (!GenericUtil.isEmpty(globalFuncResult.getResultMap()))
				parameterMap.putAll(globalFuncResult.getResultMap()); // veli TODO
																	// acaba
																	// hata
																	// olabilir
																	// mi? baska
																	// bir map'e
																	// mi atsak
			// sadece burasi icin?
		}
		GlobalFuncTrigger.afterExec(globalFuncResult);

		switch (globalFuncId) {
		case -478: // reload locale msg cache
			for (Object[] m : (List<Object[]>) dao.executeSQLQuery(
					"select locale, locale_msg_key, dsc from iwb.w5_locale_msg where locale_msg_key=? AND customization_id=?",
					parameterMap.get("plocale_msg_key"), scd.get("customizationId"))) {
				LocaleMsgCache.set2((Integer) scd.get("customizationId"), (String) m[0], (String) m[1], (String) m[2]);
			}
		}
		if (globalFuncResult == null) {
			globalFuncResult = new W5GlobalFuncResult(globalFuncId);
			globalFuncResult.setSuccess(true);
			globalFuncResult.setRequestParams(new HashMap());
		}

		return globalFuncResult;
	}

	public W5GlobalFuncResult postEditGridGlobalFunc(Map<String, Object> scd, int dbFuncId, int dirtyCount,
			Map<String, String> requestParams, String prefix) {

		W5GlobalFuncResult dbFuncResult = metaDataDao.getGlobalFuncResult(scd, dbFuncId);
		if (!GenericUtil.isEmpty(dbFuncResult.getGlobalFunc().getAccessSourceTypes())
				&& !GenericUtil.hasPartInside2(dbFuncResult.getGlobalFunc().getAccessSourceTypes(), 1))
			throw new IWBException("security", "DbProc", dbFuncId, null, "Access Restrict Type Control", null);
		if (acEngine.checkAccessRecordControlViolation(scd, 4, 20, "" + dbFuncId))
			throw new IWBException("security", "DbProc Execute3", dbFuncId, null, "Access Execute Control", null);

		dbFuncResult.setErrorMap(new HashMap());
		dbFuncResult.setRequestParams(requestParams);
		for (int id = 1; id <= dirtyCount; id++) {

			GlobalFuncTrigger.beforeExec(dbFuncResult);
			dao.executeGlobalFunc(dbFuncResult, prefix + id);
			GlobalFuncTrigger.afterExec(dbFuncResult);

			if (!dbFuncResult.getErrorMap().isEmpty() || !dbFuncResult.isSuccess()) {
				throw new IWBException("validation", "GlobalFunc", -dbFuncId, null, "Detail Grid Validation", null);
			}
		}

		return dbFuncResult;
	}
	
	public void executeTableEvent(W5TableEvent ta, W5FormResult formResult, String action, Map<String, Object> scd,
			Map<String, String> requestParams, W5Table t, String ptablePk){
		
		ContextFactory factory = RhinoContextFactory.getGlobal();
		Context cx = factory.enterContext();

		// Context cx = Context.enter();
		StringBuilder sc = new StringBuilder();
		try {
			cx.setOptimizationLevel(-1);
			if (FrameworkSetting.rhinoInstructionCount > 0)
				cx.setInstructionObserverThreshold(FrameworkSetting.rhinoInstructionCount);
			// Initialize the standard objects (Object, Function,
			// etc.)
			// This must be done before scripts can be executed.
			// Returns
			// a scope object that we use in later calls.
			Scriptable scope = cx.initStandardObjects();
			if (ta.getTriggerCode().indexOf("$.") > -1) {
				RhinoEngine se = new RhinoEngine(scd, requestParams, dao, frameworkEngine);
				Object wrappedOut = Context.javaToJS(se, scope);
				ScriptableObject.putProperty(scope, "$", wrappedOut);
			}
			// Collect the arguments into a single string.
			sc.append("\nvar _scd=").append(GenericUtil.fromMapToJsonString(scd));
			sc.append("\nvar _request=").append(GenericUtil.fromMapToJsonString(requestParams));
			sc.append("\nvar triggerAction='").append(action).append("';");
			if (!GenericUtil.isEmpty(ptablePk))
				sc.append("\n").append(t.get_tableFieldList().get(0).getDsc()).append("='").append(ptablePk)
						.append("';");
			sc.append("\n").append(ta.getTriggerCode());

			// sc.append("'})';");
			// Now evaluate the string we've colected.
			cx.evaluateString(scope, sc.toString(), null, 1, null);

			Object result = null;
			if (scope.has("result", scope))
				result = scope.get("result", scope);

			String msg = LocaleMsgCache.get2(scd, ta.getDsc());
			boolean b = false;
			if (result != null && result instanceof Undefined)
				result = null;
			else if (result != null && result instanceof Boolean)
				if ((Boolean) result == false)
					result = null;

			if (result != null) {
				msg = result.toString();
				short resultAction = ta.getLkpResultAction();
				if (scope.has("resultAction", scope))
					resultAction = (short) GenericUtil.uInt(scope.get("resultAction", scope).toString());
				switch (resultAction) {
				case 1: // readonly
					formResult.setViewMode(true);
				case 0: // continue
					formResult.getOutputMessages().add(msg);
					break;
				case 2: // confirm & continue
					if (!requestParams.containsKey("_confirmId_" + ta.getTableTriggerId()))
						throw new IWBException("confirm", "ConfirmId", ta.getTableTriggerId(), null, msg,
								null);
					break;
				case 3: // stop with message
					throw new IWBException("security", "TableTrigger", ta.getTableTriggerId(), null, msg,
							null);
				}
			}
		} catch (Exception e) {
			throw new IWBException("rhino", "TableEvent", ta.getTableTriggerId(), sc.toString(),
					"[1209," + ta.getTableTriggerId() + "] " + ta.getDsc(), e);
		} finally {
			// Exit from the context.
			cx.exit();
		}
	}
}
