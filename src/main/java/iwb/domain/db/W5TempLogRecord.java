package iwb.domain.db;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import iwb.domain.helper.W5TableRecordHelper;


@Entity
@Immutable
@Table(name="w5_temp_log_record")
public class W5TempLogRecord implements java.io.Serializable {

	private int logRecordId;
	private int tableId;
	private int tablePk;
	private String dsc;
	private short logLevel;
	private List<W5TableRecordHelper> _parentRecords;
	
	public W5TempLogRecord() {
	}
	@Id
	@Column(name="log_record_id")
	public int getLogRecordId() {
		return logRecordId;
	}
	public void setLogRecordId(int logRecordId) {
		this.logRecordId = logRecordId;
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
	@Column(name="dsc")
	public String getDsc() {
		return dsc;
	}
	public void setDsc(String dsc) {
		this.dsc = dsc;
	}
	@Column(name="log_level")
	public short getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(short logLevel) {
		this.logLevel = logLevel;
	}
	@Transient
	public List<W5TableRecordHelper> get_parentRecords() {
		return _parentRecords;
	}
	public void set_parentRecords(List<W5TableRecordHelper> parentRecords) {
		_parentRecords = parentRecords;
	}
	
	
	
}
