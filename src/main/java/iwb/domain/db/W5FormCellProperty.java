package iwb.domain.db;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import iwb.util.GenericUtil;

@Entity
@Immutable
@Table(name = "W5_FORM_CELL_PROPERTY",schema="iwb")
public class W5FormCellProperty implements java.io.Serializable, W5Base {

	private int formCellProperyId;
	private int formCellId;
	private int relatedFormCellId;
	private String val;
	private short lkpPropertyTip;
	private short lkpOperatorTip;


	@Id
	@Column(name = "FORM_CELL_PROPERTY_ID")
	public int getFormCellProperyId() {
		return formCellProperyId;
	}

	public void setFormCellProperyId(int formCellProperyId) {
		this.formCellProperyId = formCellProperyId;
	}

	@Column(name = "RELATED_FORM_CELL_ID")
	public int getRelatedFormCellId() {
		return relatedFormCellId;
	}

	public void setRelatedFormCellId(int relatedFormCellId) {
		this.relatedFormCellId = relatedFormCellId;
	}

	@Column(name = "VAL")
	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	@Column(name = "LKP_PROPERTY_TIP")
	public short getLkpPropertyTip() {
		return lkpPropertyTip;
	}

	public void setLkpPropertyTip(short lkpPropertyTip) {
		this.lkpPropertyTip = lkpPropertyTip;
	}

	@Column(name = "LKP_OPERATOR_TIP")
	public short getLkpOperatorTip() {
		return lkpOperatorTip;
	}

	public void setLkpOperatorTip(short lkpOperatorTip) {
		this.lkpOperatorTip = lkpOperatorTip;
	}

	@Id
	@Column(name = "FORM_CELL_ID")
	public int getFormCellId() {
		return formCellId;
	}

	public void setFormCellId(int formCellId) {
		this.formCellId = formCellId;
	}


	public W5FormCellProperty(int formCellId) {
		super();
		this.formCellId = formCellId;
	}

	public W5FormCellProperty() {
		super();
	}

	@Transient
	public boolean safeEquals(W5Base q) {

			return false;
	}
	
	private String projectUuid;
	@Id
	@Column(name="project_uuid")
	public String getProjectUuid() {
		return projectUuid;
	}

	public void setProjectUuid(String projectUuid) {
		this.projectUuid = projectUuid;
	}


}
