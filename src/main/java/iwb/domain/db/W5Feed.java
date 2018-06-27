package iwb.domain.db;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import iwb.domain.helper.W5AccessControlHelper;
import iwb.domain.helper.W5CommentHelper;
import iwb.domain.helper.W5TableRecordHelper;


@Entity
@Table(name="w5_feed")
public class W5Feed implements java.io.Serializable {

	private int feedId;
	
	private int customizationId;
	private int tableId;
	private int tablePk;
	private int detailTableId;
	private int detailTablePk;
	private short feedTip;
	private int insertUserId;
	private int insertRoleId;
	private int insertUserTip;
	private String dsc;
	private W5AccessControlHelper _viewAccessControl;
	private	Map<Integer,W5Feed> _relatedFeedMap;
	private List<W5TableRecordHelper> _tableRecordList;
	private short _showFeedTip;
	private long _insertTime;
	private	int _commentCount;
	private List<W5CommentHelper> _tableCommentList;
	private	Timestamp insertDttm;
	
	public W5Feed(){
	}
	
	public W5Feed(Map<String, Object> scd) {
		customizationId=((Integer)scd.get("customizationId"));
		insertUserId=((Integer)scd.get("userId"));
		insertRoleId=((Integer)scd.get("roleId"));
		insertUserTip=((Integer)scd.get("userTip"));
		_insertTime = System.currentTimeMillis();
	}
	@SequenceGenerator(name="seqx_feed",sequenceName="seq_feed",allocationSize=1)
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seqx_feed")
	@Column(name="feed_id")
	public int getFeedId() {
		return feedId;
	}
	public void setFeedId(int feedId) {
		this.feedId = feedId;
	}
	@Id
	@Column(name="customization_id")
	public int getCustomizationId() {
		return customizationId;
	}
	public void setCustomizationId(int customizationId) {
		this.customizationId = customizationId;
	}

	@Column(name="table_id")
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	@Column(name="feed_tip")
	public short getFeedTip() {
		return feedTip;
	}
	public void setFeedTip(short feedTip) {
		this.feedTip = feedTip;
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
	@Column(name="insert_role_id")
	public int getInsertRoleId() {
		return insertRoleId;
	}
	public void setInsertRoleId(int insertRoleId) {
		this.insertRoleId = insertRoleId;
	}
	@Column(name="insert_user_id")
	public int getInsertUserId() {
		return insertUserId;
	}
	public void setInsertUserId(int insertUserId) {
		this.insertUserId = insertUserId;
	}
	@Transient
	public W5AccessControlHelper get_viewAccessControl() {
		return _viewAccessControl;
	}
	public void set_viewAccessControl(W5AccessControlHelper _viewAccessControl) {
		this._viewAccessControl = _viewAccessControl;
	}
	@Transient
	public Map<Integer,W5Feed> get_relatedFeedMap() {
		return _relatedFeedMap;
	}
	public void set_relatedFeedMap(Map<Integer,W5Feed> _relatedFeedMap) {
		this._relatedFeedMap = _relatedFeedMap;
	}
	@Transient
	public List<W5TableRecordHelper> get_tableRecordList() {
		return _tableRecordList;
	}
	public void set_tableRecordList(List<W5TableRecordHelper> _tableRecordList) {
		this._tableRecordList = _tableRecordList;
	}
	@Transient
	public short get_showFeedTip() {
		return _showFeedTip;
	}
	public void set_showFeedTip(short showFeedTip) {
		_showFeedTip = showFeedTip;
	}
	
	@Column(name="detail_table_id")
	public int getDetailTableId() {
		return detailTableId;
	}
	public void setDetailTableId(int detailTableId) {
		this.detailTableId = detailTableId;
	}
	@Column(name="detail_table_pk")
	public int getDetailTablePk() {
		return detailTablePk;
	}
	public void setDetailTablePk(int detailTablePk) {
		this.detailTablePk = detailTablePk;
	}
	@Transient
	public long get_insertTime() {
		return _insertTime;
	}
	public void set_insertTime(long insertTime) {
		_insertTime = insertTime;
	}
	
	@Column(name="insert_user_tip")
	public int getInsertUserTip() {
		return insertUserTip;
	}
	public void setInsertUserTip(int insertUserTip) {
		this.insertUserTip = insertUserTip;
	}
	@Transient
	public int get_commentCount() {
		return _commentCount;
	}
	public void set_commentCount(int commentCount) {
		_commentCount = commentCount;
	}
	@Transient
	public List<W5CommentHelper> get_tableCommentList() {
		return _tableCommentList;
	}
	public void set_tableCommentList(List<W5CommentHelper> tableCommentList) {
		_tableCommentList = tableCommentList;
	}
	@Column(name="insert_dttm", insertable=false)
	public Timestamp getInsertDttm() {
		return insertDttm;
	}

	public void setInsertDttm(Timestamp insertDttm) {
		this.insertDttm = insertDttm;
	}
	
	
}
