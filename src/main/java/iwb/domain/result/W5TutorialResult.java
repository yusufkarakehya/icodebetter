package iwb.domain.result;

import java.util.List;
import java.util.Map;
import java.util.Set;

import iwb.domain.db.W5Tutorial;



public class W5TutorialResult implements W5MetaResult{
	
	private	int	tutorialId;
	private W5Tutorial tutorial;
	private	short tutorialUserStatus;
	
	private	List<W5Tutorial> requiredTutorialList;
	private	List<W5Tutorial> recommendedTutorialList;

	private Map<String, Object> scd;
	private Map<String,String> requestParams;
	private Set<Integer> doneTutorials;
	
	public int getTutorialId() {
		return tutorialId;
	}
	public void setTutorialId(int tutorialId) {
		this.tutorialId = tutorialId;
	}
	public W5Tutorial getTutorial() {
		return tutorial;
	}
	public void setTutorial(W5Tutorial tutorial) {
		this.tutorial = tutorial;
	}
	public Map<String, Object> getScd() {
		return scd;
	}
	public void setScd(Map<String, Object> scd) {
		this.scd = scd;
	}
	public Map<String, String> getRequestParams() {
		return requestParams;
	}
	public void setRequestParams(Map<String, String> requestParams) {
		this.requestParams = requestParams;
	}
	public List<W5Tutorial> getRequiredTutorialList() {
		return requiredTutorialList;
	}
	public void setRequiredTutorialList(List<W5Tutorial> requiredTutorialList) {
		this.requiredTutorialList = requiredTutorialList;
	}
	public List<W5Tutorial> getRecommendedTutorialList() {
		return recommendedTutorialList;
	}
	public void setRecommendedTutorialList(List<W5Tutorial> recommendedTutorialList) {
		this.recommendedTutorialList = recommendedTutorialList;
	}
	public Set<Integer> getDoneTutorials() {
		return doneTutorials;
	}
	public void setDoneTutorials(Set<Integer> doneTutorials) {
		this.doneTutorials = doneTutorials;
	}
	public short getTutorialUserStatus() {
		return tutorialUserStatus;
	}
	public void setTutorialUserStatus(short tutorialUserStatus) {
		this.tutorialUserStatus = tutorialUserStatus;
	}

	private boolean dev = false;
	
	public boolean isDev() {
		return dev;
	}
	public void setDev(boolean dev) {
		this.dev = dev;
	}
	
	
}
