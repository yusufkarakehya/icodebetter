package iwb.adapter.metadata;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import iwb.cache.FrameworkSetting;
import iwb.domain.db.M5List;
import iwb.domain.db.W5Card;
import iwb.domain.db.W5Conversion;
import iwb.domain.db.W5ConversionCol;
import iwb.domain.db.W5CustomGridColumnCondition;
import iwb.domain.db.W5CustomGridColumnRenderer;
import iwb.domain.db.W5Exception;
import iwb.domain.db.W5ExternalDb;
import iwb.domain.db.W5Form;
import iwb.domain.db.W5FormCell;
import iwb.domain.db.W5FormCellProperty;
import iwb.domain.db.W5FormHint;
import iwb.domain.db.W5FormModule;
import iwb.domain.db.W5FormSmsMail;
import iwb.domain.db.W5GlobalFunc;
import iwb.domain.db.W5GlobalFuncParam;
import iwb.domain.db.W5Grid;
import iwb.domain.db.W5GridColumn;
import iwb.domain.db.W5LookUp;
import iwb.domain.db.W5LookUpDetay;
import iwb.domain.db.W5ObjectMenuItem;
import iwb.domain.db.W5ObjectToolbarItem;
import iwb.domain.db.W5Page;
import iwb.domain.db.W5PageObject;
import iwb.domain.db.W5Project;
import iwb.domain.db.W5Query;
import iwb.domain.db.W5QueryField;
import iwb.domain.db.W5QueryParam;
import iwb.domain.db.W5Table;
import iwb.domain.db.W5TableChild;
import iwb.domain.db.W5TableEvent;
import iwb.domain.db.W5TableField;
import iwb.domain.db.W5TableFieldCalculated;
import iwb.domain.db.W5TableParam;
import iwb.domain.db.W5Workflow;
import iwb.domain.db.W5WorkflowStep;
import iwb.domain.db.W5Ws;
import iwb.domain.db.W5WsMethod;
import iwb.domain.db.W5WsMethodParam;

public class MetadataImport {
	
	private Object json2java(JSONObject j, Class c) {
		String mname = null;
		try {
			Constructor ct = c.getConstructor();
			Object o = ct.newInstance();
			Method[] ms = c.getMethods();
			for(String k:j.keySet()) {
				mname = "set"+k.substring(0,1).toUpperCase(FrameworkSetting.appLocale) + k.substring(1);
				for(Method m:ms)if(m.getName().equals(mname)) {
					Object value = j.get(k);
					Parameter p = m.getParameters()[0];
					if(p.getType()==Short.TYPE || p.getType()==Short.class) {
						m.invoke(o, (short)((int)value));
					} else if(p.getType()==BigDecimal.class) {
						m.invoke(o, new BigDecimal(value.toString()));
					} else m.invoke(o, value);
					break;					
				}
			}
			return o;
		}catch(Exception e) {
			System.out.println(c.getName() + "." + mname + " : " + e.getMessage());
			return null;
		}
	}
	
	private List jsonArray2java(JSONObject j, String key, Class c) {
		if(!j.has(key))return null;
		JSONArray ar = j.getJSONArray(key);
		List l = new ArrayList(ar.length());
		for(int qi=0;qi<ar.length();qi++)l.add(json2java(ar.getJSONObject(qi), c));
		return l;
	}
	
	public boolean fromJson(String s) {
		JSONObject j = new JSONObject(s);

		W5Project po = (W5Project)json2java(j.getJSONObject("project"), W5Project.class);

		List<W5LookUp> lookUps = jsonArray2java(j, ("lookUps"), W5LookUp.class);
		List<W5LookUpDetay> lookUpDetays = jsonArray2java(j, ("lookUpDetays"), W5LookUpDetay.class);
		if(lookUps!=null)for(W5LookUp m:lookUps) {
			if(lookUpDetays!=null)for(W5LookUpDetay d:lookUpDetays) if(m.getLookUpId()==d.getLookUpId()){
				if(m.get_detayList()==null) {
					m.set_detayList(new ArrayList<W5LookUpDetay>());
					m.set_detayMap(new HashMap<String, W5LookUpDetay>());
				}
				m.get_detayList().add(d);
				m.get_detayMap().put(d.getVal(), d);
			}		
		}
		
		List<W5Table> tables = jsonArray2java(j, ("tables"), W5Table.class);
		List<W5TableField> tableFields = jsonArray2java(j, ("tableFields"), W5TableField.class);
		List<W5TableParam> tableParams = jsonArray2java(j, ("tableParams"), W5TableParam.class);
		List<W5TableEvent> tableEvents = jsonArray2java(j, ("tableEvents"), W5TableEvent.class);
		List<W5TableFieldCalculated> tableFieldCalculateds = jsonArray2java(j, ("tableFieldCalculateds"), W5TableFieldCalculated.class);
		List<W5TableChild> tableChilds = jsonArray2java(j, ("tableChilds"), W5TableChild.class);
		if(tables!=null)for(W5Table m:tables) {
			if(tableFields!=null)for(W5TableField d:tableFields) if(m.getTableId()==d.getTableId()){
				if(m.get_tableFieldList()==null) {
					m.set_tableFieldList(new ArrayList<W5TableField>());
					m.set_tableFieldMap(new HashMap<Integer, W5TableField>());
				}
				m.get_tableFieldList().add(d);
				m.get_tableFieldMap().put(d.getTableFieldId(), d);
			}		
			if(tableParams!=null)for(W5TableParam d:tableParams) if(m.getTableId()==d.getTableId()){
				if(m.get_tableParamList()==null) {
					m.set_tableParamList(new ArrayList<W5TableParam>());
				}
				m.get_tableParamList().add(d);
			}
			if(tableChilds!=null)for(W5TableChild d:tableChilds) if(m.getTableId()==d.getTableId()){
				if(m.get_tableChildList()==null) {
					m.set_tableChildList(new ArrayList<W5TableChild>());
				}
				m.get_tableChildList().add(d);
			}
		}
		
		List<W5Ws> wss = jsonArray2java(j, ("wss"), W5Ws.class);
		List<W5WsMethod> wsMethods = jsonArray2java(j, ("wsMethods"), W5WsMethod.class);
		List<W5WsMethodParam> wsMethodParams = jsonArray2java(j, ("wsMethodParams"), W5WsMethodParam.class);
		if(wss!=null)for(W5Ws m:wss) {
			if(wsMethods!=null)for(W5WsMethod d:wsMethods) if(m.getWsId()==d.getWsId()){
				if(m.get_methods()==null) {
					m.set_methods(new ArrayList<W5WsMethod>());
				}
				m.get_methods().add(d);
				if(wsMethodParams!=null)for(W5WsMethodParam d2:wsMethodParams) if(d.getWsMethodId()==d2.getWsMethodId()){
					if(d.get_params()==null) {
						d.set_params(new ArrayList<W5WsMethodParam>());
						d.set_paramMap(new HashMap<Integer, W5WsMethodParam>());
					}
					d.get_params().add(d2);
					d.get_paramMap().put(d2.getWsMethodParamId(), d2);
				}		
			}		
		}
		
		List<W5GlobalFunc> funcs = jsonArray2java(j, ("funcs"), W5GlobalFunc.class);
		List<W5GlobalFuncParam> funcParams = jsonArray2java(j, ("funcParams"), W5GlobalFuncParam.class);
		if(funcs!=null)for(W5GlobalFunc m:funcs) {
			if(funcParams!=null)for(W5GlobalFuncParam d:funcParams) if(m.getDbFuncId()==d.getDbFuncId()){
				if(m.get_dbFuncParamList()==null) {
					m.set_dbFuncParamList(new ArrayList<W5GlobalFuncParam>());
				}
				m.get_dbFuncParamList().add(d);
			}		
		}
		
		List<W5Query> queries = jsonArray2java(j, ("queries"), W5Query.class);
		List<W5QueryField> queryFields = jsonArray2java(j, ("queryFields"), W5QueryField.class);
		List<W5QueryParam> queryParams = jsonArray2java(j, ("queryParams"), W5QueryParam.class);
		
		List<W5Form> forms = jsonArray2java(j, ("forms"), W5Form.class);
		List<W5FormCell> formCells = jsonArray2java(j, ("formCells"), W5FormCell.class);
		List<W5FormModule> formModules = jsonArray2java(j, ("formModules"), W5FormModule.class);
		List<W5FormCellProperty> formCellProperties = jsonArray2java(j, ("formCellProperties"), W5FormCellProperty.class);
		List<W5FormSmsMail> formSmsMails = jsonArray2java(j, ("formSmsMails"), W5FormSmsMail.class);
		List<W5FormHint> formHints = jsonArray2java(j, ("formHints"), W5FormHint.class);
		
		List<W5Grid> grids = jsonArray2java(j, ("grids"), W5Grid.class);
		List<W5GridColumn> gridColumns = jsonArray2java(j, ("gridColumns"), W5GridColumn.class);
		List<W5CustomGridColumnCondition> gridColumnCustomConditions = jsonArray2java(j, ("gridColumnCustomConditions"), W5CustomGridColumnCondition.class);
		List<W5CustomGridColumnRenderer> gridColumnCustomRenderers = jsonArray2java(j, ("gridColumnCustomRenderers"), W5CustomGridColumnRenderer.class);

		List<M5List> mobileLists = jsonArray2java(j, ("mobileLists"), M5List.class);
		
		List<W5Card> cards = jsonArray2java(j, ("cards"), W5Card.class);
		
		List<W5ObjectMenuItem> menuItems = jsonArray2java(j, ("menuItems"), W5ObjectMenuItem.class);
		List<W5ObjectToolbarItem> toolbarItems = jsonArray2java(j, ("toolbarItems"), W5ObjectToolbarItem.class);

		List<W5Workflow> workflows = jsonArray2java(j, ("workflows"), W5Workflow.class);
		List<W5WorkflowStep> workflowSteps = jsonArray2java(j, ("workflowSteps"), W5WorkflowStep.class);

		List<W5Conversion> conversions = jsonArray2java(j, ("conversions"), W5Conversion.class);
		List<W5ConversionCol> conversionCols = jsonArray2java(j, ("conversionCols"), W5ConversionCol.class);
		
		List<W5Page> pages = jsonArray2java(j, ("pages"), W5Page.class);
		List<W5PageObject> pageObjects = jsonArray2java(j, ("pageObjects"), W5PageObject.class);

//		List<W5Menu> menus"), W5Menu.class);
//		List<M5Menu> mmenus"), M5Menu.class);

		List<W5ExternalDb> externalDbs = jsonArray2java(j, ("externalDbs"), W5ExternalDb.class);
		List<W5Exception> exceptions = jsonArray2java(j, ("exceptions"), W5Exception.class);
		return true;		
	}

}
