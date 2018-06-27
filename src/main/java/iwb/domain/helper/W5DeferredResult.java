package iwb.domain.helper;

import org.springframework.web.context.request.async.DeferredResult;




public class W5DeferredResult extends DeferredResult<String> {
	private	int	customizationId;
	private	int	userId;
	private	String	webPageId;
	

	public int getCustomizationId() {
		return customizationId;
	}

	public void setCustomizationId(int customizationId) {
		this.customizationId = customizationId;
	}

	public W5DeferredResult(int customizationId, int userId, String webPageId, java.lang.Long l, java.lang.Object o) {
		super(l,o);
		this.customizationId = customizationId;
		this.userId = userId;
		this.webPageId = webPageId;
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

}
