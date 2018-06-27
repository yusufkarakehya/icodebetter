package iwb.domain.db;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

// Generated Feb 5, 2007 3:58:07 PM by Hibernate Tools 3.2.0.b9

@Entity
@Immutable
@Table(name="w5_data_view",schema="iwb")
public class W5DataView implements java.io.Serializable {

	private int dataViewId;

	private int customizationId;
	
	private String dsc;

	private int queryId;

	private String localeMsgKey;

	private short defaultPageRecordNumber;

	private short defaultWidth;

	private short defaultHeight;

	private int pkQueryFieldId;

	private short saveUserInfoFlag;
	
	private short displayInfoFlag;
	
	private short gridReportFlag;
	
	private String defaultSqlOrderby;

	private int defaultCrudFormId;// burdan edit, insert vs olaylari incelenecek
	 
	
	private int _searchFormId;
	private	String templateCode;	
	private	String jsCode;	

	private Map<String, W5QueryField> _queryFieldMapDsc;
	private W5QueryField	_pkQueryField;
	private	List<W5ObjectToolbarItem>	_toolbarItemList;
	private	W5Form _defaultCrudForm;
	private	W5Table _mainTable;
	private W5Query	_query;	
	private List<W5QueryField> _postProcessQueryFields;
	private Map<Integer, W5QueryField> _queryFieldMap;

	
	@Id
	@Column(name="data_view_id")
	public int getDataViewId() {
		return dataViewId;
	}
	public void setDataViewId(int gridId) {
		this.dataViewId = gridId;
	}
	@Column(name="dsc")
	public String getDsc() {
		return dsc;
	}
	public void setDsc(String dsc) {
		this.dsc = dsc;
	}
	@Column(name="query_id")
	public int getQueryId() {
		return queryId;
	}
	public void setQueryId(int queryId) {
		this.queryId = queryId;
	}
	@Column(name="locale_msg_key")
	public String getLocaleMsgKey() {
		return localeMsgKey;
	}
	public void setLocaleMsgKey(String localeMsgKey) {
		this.localeMsgKey = localeMsgKey;
	}
	@Column(name="default_page_record_number")
	public short getDefaultPageRecordNumber() {
		return defaultPageRecordNumber;
	}
	public void setDefaultPageRecordNumber(short defaultPageRecordNumber) {
		this.defaultPageRecordNumber = defaultPageRecordNumber;
	}
	@Column(name="default_width")
	public short getDefaultWidth() {
		return defaultWidth;
	}
	public void setDefaultWidth(short defaultWidth) {
		this.defaultWidth = defaultWidth;
	}
	@Column(name="default_height")
	public short getDefaultHeight() {
		return defaultHeight;
	}
	public void setDefaultHeight(short defaultHeight) {
		this.defaultHeight = defaultHeight;
	}
	@Column(name="pk_query_field_id")
	public int getPkQueryFieldId() {
		return pkQueryFieldId;
	}
	public void setPkQueryFieldId(int pkQueryFieldId) {
		this.pkQueryFieldId = pkQueryFieldId;
	}
	@Column(name="save_user_info_flag")
	public short getSaveUserInfoFlag() {
		return saveUserInfoFlag;
	}
	public void setSaveUserInfoFlag(short saveUserInfoFlag) {
		this.saveUserInfoFlag = saveUserInfoFlag;
	}
	@Column(name="display_info_flag")
	public short getDisplayInfoFlag() {
		return displayInfoFlag;
	}
	public void setDisplayInfoFlag(short displayInfoFlag) {
		this.displayInfoFlag = displayInfoFlag;
	}
	@Column(name="default_sql_order_by")
	public String getDefaultSqlOrderby() {
		return defaultSqlOrderby;
	}
	public void setDefaultSqlOrderby(String defaultSqlOrderby) {
		this.defaultSqlOrderby = defaultSqlOrderby;
	}
	@Transient
	public W5QueryField get_pkQueryField() {
		return _pkQueryField;
	}
	public void set_pkQueryField(W5QueryField pkQueryField) {
		_pkQueryField = pkQueryField;
	}


	@Id
	@Column(name="customization_id")
	public int getCustomizationId() {
		return customizationId;
	}
	public void setCustomizationId(int customizationId) {
		this.customizationId = customizationId;
	}
	
	@Column(name="default_crud_form_id")
	public int getDefaultCrudFormId() {
		return defaultCrudFormId;
	}
	public void setDefaultCrudFormId(int defaultCrudFormId) {
		this.defaultCrudFormId = defaultCrudFormId;
	}
	
	@Transient
	public List<W5ObjectToolbarItem> get_toolbarItemList() {
		return _toolbarItemList;
	}
	public void set_toolbarItemList(List<W5ObjectToolbarItem> toolbarItemList) {
		_toolbarItemList = toolbarItemList;
	}
	
	@Transient
	public W5Form get_defaultCrudForm() {
		return _defaultCrudForm;
	}
	public void set_defaultCrudForm(W5Form defaultCrudForm) {
		_defaultCrudForm = defaultCrudForm;
	}
	
	@Transient
	public W5Query get_query() {
		return _query;
	}
	public void set_query(W5Query query) {
		_query = query;
	}
	@Column(name="grid_report_flag")
	public short getGridReportFlag() {
		return gridReportFlag;
	}
	public void setGridReportFlag(short gridReportFlag) {
		this.gridReportFlag = gridReportFlag;
	}

	
	@Transient
	public Map<String, W5QueryField> get_queryFieldMapDsc() {
		return _queryFieldMapDsc;
	}
	public void set_queryFieldMapDsc(Map<String, W5QueryField> queryFieldMapDsc) {
		_queryFieldMapDsc = queryFieldMapDsc;
	}
	
	@Transient
	public List<W5QueryField> get_postProcessQueryFields() {
		return _postProcessQueryFields;
	}
	public void set_postProcessQueryFields(List<W5QueryField> postProcessQueryFields) {
		_postProcessQueryFields = postProcessQueryFields;
	}
	@Transient	
	public W5Table get_mainTable() {
		return _mainTable;
	}
	public void set_mainTable(W5Table mainTable) {
		_mainTable = mainTable;
	}
	
	@Transient
	public Map<Integer, W5QueryField> get_queryFieldMap() {
		return _queryFieldMap;
	}

	public void set_queryFieldMap(Map<Integer, W5QueryField> queryFieldMap) {
		this._queryFieldMap = queryFieldMap;
	}
	@Transient
	public int get_searchFormId() {
		return _searchFormId;
	}
	public void set_searchFormId(int searchFormId) {
		_searchFormId = searchFormId;
	}

	@Column(name="template_code")
	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	@Column(name="js_code")
	public String getJsCode() {
		return jsCode;
	}
	public void setJsCode(String jsCode) {
		this.jsCode = jsCode;
	}
	
}
