package iwb.adapter.mail.generic;

import java.io.File;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.stereotype.Component;

import com.sun.mail.smtp.SMTPAddressFailedException;
import com.sun.mail.smtp.SMTPMessage;

import iwb.adapter.mail.MailAdapter;
import iwb.dao.RdbmsDao;
import iwb.domain.db.W5Email;
import iwb.domain.db.W5FileAttachment;
import iwb.domain.db.W5ObjectMailSetting;
import iwb.util.FrameworkCache;
import iwb.util.FrameworkSetting;
import iwb.util.GenericUtil;
import iwb.util.HtmlFilter;
import iwb.util.LocaleMsgCache;

@Component
public class GenericMailAdapter implements MailAdapter {
	private RdbmsDao dao;

	public void setDao(RdbmsDao dao) {
		this.dao = dao;
	}

	public static InternetAddress[] getEmailAddress(String[] adresler){
		InternetAddress[] ia = null;		
		try {
			Vector<String> v = new Vector<String>();
			for(int i = 0; i<adresler.length; i++){
				adresler[i]=adresler[i].trim();
				if(GenericUtil.isValidEmailAddress(adresler[i]) && !v.contains(adresler[i]))	{
					v.add(adresler[i]);
				}
			}			
			ia = new InternetAddress[v.size()];			
			for(int i=0; i<v.size() ; i++){
				ia[i] = new InternetAddress(v.get(i)); 
			}
		} catch (AddressException e) {
			if(FrameworkSetting.debug)e.printStackTrace();
		}
		
		return ia;  
	} 


	@Override
	public String sendMail(Map<String, Object> scd, W5ObjectMailSetting oms, W5Email email) {
		String txt;
		boolean mailDebug = FrameworkCache.getAppSettingIntValue(0, "mail_debug_flag")!=0;

		try {
						
			Properties properties = new Properties();			
			String hostingType = oms.getOutboxServerSslFlag() == 1 ? "smtps" : "smtp";
			
			properties.put("mail.mime.charset", "UTF-8"); 	
			properties.put("mail.transport.protocol", hostingType);
			properties.put("mail."+hostingType+".host", oms.getOutboxServer());
			properties.put("mail."+hostingType+".auth", oms.getOutboxAuthTip()==0 ? "false" :"true");
			properties.put("mail."+hostingType+".port", oms.getOutboxServerPort());	
            properties.put("mail."+hostingType+".sendpartial", "true");

			Session session = Session.getInstance(properties);
			Transport transport = session.getTransport();
			Multipart multipart = new MimeMultipart();
			//txt = (PromisUtil.uInt(email.getMailKeepBodyOriginal()) != 0) ? email.getMailBody() : email.getMailBody().replaceAll("\n", "<br>");
			//txt = (PromisUtil.uInt(email.getMailKeepBodyOriginal()) != 0) ? email.getMailBody() : email.getMailBody();
			txt = new HtmlFilter().filter(email.getMailBody());
			SMTPMessage  message = new SMTPMessage(session);
			message.setHeader("X-Mailer", MimeUtility.encodeText("ProMIS BMP Mailer, version 0.2b"));
			message.setSentDate(Calendar.getInstance().getTime());
			if(oms.getOutboxRequestReadFlag()!=0){  
				message.setHeader("Disposition-Notification-To", MimeUtility.encodeText(oms.getEmailAddress().indexOf(">")>-1 ? oms.getEmailAddress():"\""+oms.getDsc()+"\" <"+oms.getEmailAddress()+">"));
			}
			
			InternetAddress[] addressTo = email.getMailTo() != null ? getEmailAddress(email.getMailTo().split(",")) : null;
			InternetAddress[] addressCC = email.getMailCc() != null ? getEmailAddress(email.getMailCc().split(",")) : null;
			InternetAddress[] addressBCC = email.getMailBcc() != null ? getEmailAddress(email.getMailBcc().split(",")) : null;	
			
			message.setFrom(new InternetAddress(oms.getEmailAddress()));
			//message.setReplyTo(addresses);
			
			if(addressTo != null){ message.setRecipients(Message.RecipientType.TO, addressTo); }
			if(addressCC != null){message.setRecipients(Message.RecipientType.CC, addressCC);}
			if(addressBCC != null){message.setRecipients(Message.RecipientType.BCC, addressBCC);}
			
			MimeBodyPart messagePart = new MimeBodyPart();
			
		//	txt+="\n\n"+"****** Bu mesaj bilgilendirme amaçlı otomatik olarak yollanmıştır, hata oldugunu dusunuyorsaniz, REPLY ALL Yapınız ******\n"; //bu kısım fonksiyonlarda ayarlandı.
/*		if(PromisCache.getAppSettingIntValue(scd, "mail_signature_override_flag") != 0 || oms.getEmailSignature()==null){
				if(PromisCache.getAppSettingStringValue(scd, "mail_signature")!=null)txt+="<br>"+PromisCache.getAppSettingStringValue(scd, "mail_signature");
			} else{
				if(oms.getEmailSignature()!=null)txt+="<br>"+oms.getEmailSignature();
			}
*/
			if(FrameworkCache.getAppSettingIntValue(scd, "mail_send_bmp_advertisement_flag") != 0) txt+="<br>"+GenericUtil.getMailReklam(scd);
			
			message.setSubject(email.getMailSubject(), "utf-8");
			messagePart.setContent(txt, "text/html; charset=utf-8");
			
			multipart.addBodyPart(messagePart);
			
			// Attachmentlar ekleniyor
			
			if(email.get_fileAttachments() != null && email.get_fileAttachments().size()>0){	
				for (W5FileAttachment f1 : email.get_fileAttachments()){
					MimeBodyPart attachmentPart = new MimeBodyPart();
					
					FileDataSource fileDataSource = new FileDataSource(new File(FrameworkCache.getAppSettingStringValue(scd, "file_local_path")+File.separator+GenericUtil.uInt((Object)scd.get("customizationId"))+File.separator+"attachment"+File.separator+f1.getSystemFileName()));
			        attachmentPart.setDataHandler(new DataHandler(fileDataSource));
			        attachmentPart.setFileName(f1.getOrijinalFileName());
					multipart.addBodyPart(attachmentPart);
				}
			}
	
			message.setContent(multipart);
			if(oms.getOutboxRequestDeliveryFlag()!=0){
				message.setReturnOption(SMTPMessage.RETURN_HDRS);
				message.setNotifyOptions(SMTPMessage.NOTIFY_DELAY|SMTPMessage.NOTIFY_FAILURE|SMTPMessage.NOTIFY_SUCCESS);
			}	
			
			if(oms.getOutboxAuthTip()==0)transport.connect();	//userName PassWord gerektirmeyen durumlarda.*
			else transport.connect(oms.getOutboxServer(), oms.getOutboxServerPort(), oms.getOutboxServerUserName(), GenericUtil.PRMDecrypt(oms.getOutboxServerPassWord()));
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		}catch (SMTPAddressFailedException e) {//Yanlis adres ise hata verme
			if(FrameworkSetting.debug || mailDebug)e.printStackTrace();
			return null;
		}catch (MessagingException e) {
			if(FrameworkSetting.debug || mailDebug)e.printStackTrace();		
			if(e.getMessage()==null) return "unknown error";
			if(e.getNextException() !=null && e.getNextException().getClass().getName().equals("com.sun.mail.smtp.SMTPAddressFailedException"))	return null;
			if(e.getMessage().toLowerCase().contains("authentication")) return LocaleMsgCache.get2((Integer)scd.get("customizationId"), scd.get("locale").toString(), "error.mail.authentication_failed");
			return e.getMessage();
			
		}catch (Exception e) {
			if(FrameworkSetting.debug || mailDebug)e.printStackTrace();
			return e.getMessage()==null ? "unknown error" : e.getMessage();
		}
		return null;
	}
	
}
