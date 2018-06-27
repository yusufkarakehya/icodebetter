package iwb.domain.helper;

import java.util.Map;

public class Log5VisitedPageHelper {
	private	Map<String, Object> scd;
	private	String pageName;
	private	int pageId;
	private	String ip;
	private int duration;
	public Log5VisitedPageHelper(Map<String, Object> scd, String pageName, int pageId, String ip, int duration) {
		this.scd = scd;
		this.pageName = pageName;
		this.pageId = pageId;
		this.ip = ip;
		this.duration = duration;
	}
	public Map<String, Object> getScd() {
		return scd;
	}
	public int getPageId() {
		return pageId;
	}
	public String getIp() {
		return ip;
	}
	public int getDuration() {
		return duration;
	}
	public String getPageName() {
		return pageName;
	}

}
