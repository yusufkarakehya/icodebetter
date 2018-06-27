package iwb.adapter.mail;

import java.util.Map;

import iwb.domain.db.W5Email;
import iwb.domain.db.W5ObjectMailSetting;

public interface MailAdapter {

	public String sendMail(Map<String, Object> scd, W5ObjectMailSetting oms, W5Email email);

}
