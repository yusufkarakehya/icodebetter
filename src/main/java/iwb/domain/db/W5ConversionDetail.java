package iwb.domain.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

// Generated Jun 17, 2007 5:12:14 PM by Hibernate Tools 3.2.0.b9


@Entity
@Immutable
@Table(name="w5_conversion_detail",schema="iwb")
public class W5ConversionDetail implements java.io.Serializable {

	private int conversionDetailId;
	private int conversionId;
	private int customizationId;
	private String pkOnUpdate;
	private int dstTableId;
	private int dstFormId;
	private short rowErrorStrategyTip;
	private short tabOrder;
	private String conversionSqlCode;
	private int groupId;

	@Id
	@Column(name="conversion_detail_id")	
	public int getConversionDetailId() {
		return conversionDetailId;
	}
	public void setConversionDetailId(int conversionDetailId) {
		this.conversionDetailId = conversionDetailId;
	}
	@Column(name="conversion_id")
	public int getConversionId() {
		return conversionId;
	}
	public void setConversionId(int conversionId) {
		this.conversionId = conversionId;
	}
	

	
	@Column(name="dst_table_id")
	public int getDstTableId() {
		return dstTableId;
	}
	public void setDstTableId(int dstTableId) {
		this.dstTableId = dstTableId;
	}
	
	@Column(name="row_error_strategy_tip")
	public short getRowErrorStrategyTip() {
		return rowErrorStrategyTip;
	}
	public void setRowErrorStrategyTip(short rowErrorStrategyTip) {
		this.rowErrorStrategyTip = rowErrorStrategyTip;
	}
	
	@Id
	@Column(name="customization_id")
	public int getCustomizationId() {
		return customizationId;
	}
	public void setCustomizationId(int customizationId) {
		this.customizationId = customizationId;
	}
	
	@Column(name="dst_form_id")
	public int getDstFormId() {
		return dstFormId;
	}
	public void setDstFormId(int dstFormId) {
		this.dstFormId = dstFormId;
	}

	@Column(name="tab_order")
	public short getTabOrder() {
		return tabOrder;
	}
	public void setTabOrder(short tabOrder) {
		this.tabOrder = tabOrder;
	}
	
	
	@Column(name="pk_on_update")
	public String getPkOnUpdate() {
		return pkOnUpdate;
	}
	public void setPkOnUpdate(String pkOnUpdate) {
		this.pkOnUpdate = pkOnUpdate;
	}
	@Column(name="conversion_sql_code")
	public String getConversionSqlCode() {
		return conversionSqlCode;
	}
	
	public void setConversionSqlCode(String conversionSqlCode) {
		this.conversionSqlCode = conversionSqlCode;
	}
	@Column(name="group_id")
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	

}
