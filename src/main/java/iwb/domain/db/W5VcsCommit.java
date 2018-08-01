package iwb.domain.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.JSONException;
import org.json.JSONObject;

@Entity
@Table(name="w5_vcs_commit", schema="iwb")
public class W5VcsCommit  implements java.io.Serializable {

	private int vcsCommitId;
	private String projectUuid;
	private String extraSql;
	private String comment;
	private short commitTip;
	private int commitUserId;
	

	
	
	public W5VcsCommit(JSONObject o) {
		if(o==null)return;
		try {
			if(o.has("vcs_commit_id"))vcsCommitId=o.getInt("vcs_commit_id");
			if(o.has("project_uuid"))projectUuid=o.getString("project_uuid");
			if(o.has("extra_sql"))extraSql=o.getString("extra_sql");
			if(o.has("comment"))comment=o.getString("comment");
			if(o.has("commit_tip"))commitTip=(short)o.getInt("commit_tip");
		} catch (JSONException e) {e.printStackTrace();}
	}
	
	public W5VcsCommit() {
	}

	@Id
	@Column(name="project_uuid")
	public String getProjectUuid() {
		return projectUuid;
	}
	public void setProjectUuid(String projectUuid) {
		this.projectUuid = projectUuid;
	}
	
	@Id
	@Column(name="vcs_commit_id")
	public int getVcsCommitId() {
		return vcsCommitId;
	}
	public void setVcsCommitId(int vcsCommitId) {
		this.vcsCommitId = vcsCommitId;
	}
	
	@Column(name="extra_sql")
	public String getExtraSql() {
		return extraSql;
	}
	public void setExtraSql(String extraSql) {
		this.extraSql = extraSql;
	}
	
	@Column(name="comment")
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Column(name="commit_tip")
	public short getCommitTip() {
		return commitTip;
	}
	public void setCommitTip(short commitTip) {
		this.commitTip = commitTip;
	}

	@Column(name="commit_user_id")
	public int getCommitUserId() {
		return commitUserId;
	}

	public void setCommitUserId(int commitUserId) {
		this.commitUserId = commitUserId;
	}
	
	
	public W5VcsCommit newInstance(String newProjectUuid){
		W5VcsCommit n = new W5VcsCommit();
		n.setComment(comment);
		n.setCommitTip(commitTip);
		n.setCommitUserId(commitUserId);
		n.setExtraSql(extraSql);
		n.setProjectUuid(newProjectUuid);
		n.setVcsCommitId(vcsCommitId);
		return n;
	}
	
}
