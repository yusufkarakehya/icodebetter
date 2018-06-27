package iwb.domain.helper;

import iwb.util.FrameworkSetting;
import iwb.util.GenericUtil;

public class W5SynchAfterPostHelper {
	private	int customizationId;
	private	int tableId;
	private	String key;
	private	int userId;
	private	String webPageId;
	private	short action;
	
	
	
	public W5SynchAfterPostHelper(int customizationId, int userId, String chunk) {
		//			        	s.append(";").append(o.getTableId()).append(",").append(o.getKey()).append(",").append(o.getWebPageId()).append(",").append(o.getAction());
		this.customizationId = customizationId;
		this.userId = userId;
		String[] s=chunk.split(",");
		this.tableId = GenericUtil.uInt(s[0]); 
		this.key = s[1]; 
		this.webPageId = FrameworkSetting.instanceUuid+"-"+s[2]; 
		this.action = (short)GenericUtil.uInt(s[3]); 

	}
	public W5SynchAfterPostHelper(int customizationId, int tableId, String key,
			int userId, String webPageId, short action) {
		super();
		this.customizationId = customizationId;
		this.tableId = tableId;
		this.key = key;
		this.userId = userId;
		this.webPageId = webPageId;
		this.action = action;
	}
	public int getCustomizationId() {
		return customizationId;
	}
	public void setCustomizationId(int customizationId) {
		this.customizationId = customizationId;
	}
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getWebPageId() {
		return webPageId;
	}
	public void setWebPageId(String webPageId) {
		this.webPageId = webPageId;
	}
	public short getAction() {
		return action;
	}
	public void setAction(short action) {
		this.action = action;
	}
	
}
