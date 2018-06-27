package iwb.domain.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;


@Entity
@Immutable
@Table(name="w5_temp_action_record")
public class W5TempActionRecord implements java.io.Serializable {

	private int actionRecordId;
	private int tableId;
	private int tablePk;
	private short action;
	
	public W5TempActionRecord() {
	}
	@Id
	@Column(name="action_record_id")
	public int getActionRecordId() {
		return actionRecordId;
	}
	public void setActionRecordId(int actionRecordId) {
		this.actionRecordId = actionRecordId;
	}
	@Column(name="table_id")
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	@Column(name="table_pk")
	public int getTablePk() {
		return tablePk;
	}
	public void setTablePk(int tablePk) {
		this.tablePk = tablePk;
	}
	@Column(name="action_tip")
	public short getAction() {
		return action;
	}
	public void setAction(short action) {
		this.action = action;
	}

}
