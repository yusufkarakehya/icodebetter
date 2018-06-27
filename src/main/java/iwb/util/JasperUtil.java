package iwb.util;

import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;

public class JasperUtil {	
	
	public static JasperPrint convertKey2LocaleMsg(JasperPrint jasperPrint,String locale){ // Jasper Report elementlere verilen Key degerlerinin,karsılıgını yazıyor... 
		for(int i=0;i<jasperPrint.getPages().size();i++){  
			JRPrintPage firstPage = (JRPrintPage) jasperPrint.getPages().get(i);
			for(Object element : firstPage.getElements()) {
				if(element !=null && (element instanceof JRTemplatePrintText)) {			    	 
					JRTemplatePrintText printElement = (JRTemplatePrintText)element;  	
					if(printElement.getKey()!=null && !printElement.getKey().equals("")){
						if(printElement.getKey().equals("${page_number}")){
							printElement.setText(String.valueOf(i+1));
						}else if(printElement.getKey().equals("${total_page_number}")){
							printElement.setText(String.valueOf(jasperPrint.getPages().size()));
						}else{
							printElement.setText(LocaleMsgCache.get2(0, locale, printElement.getKey().toString()));
						}
					}	 
				}
				if(element !=null && (element instanceof JRTemplatePrintFrame)) {
					JRTemplatePrintFrame element1 = (JRTemplatePrintFrame)element;
					for(Object element2 : element1.getElements()){
						if(element2 !=null && (element2 instanceof JRTemplatePrintFrame)){
							JRTemplatePrintFrame element3 = (JRTemplatePrintFrame)element2;
							for(Object element4 : element3.getElements()){
								if(element4 !=null && (element4 instanceof JRTemplatePrintText)) {			    	 
									JRTemplatePrintText printElement = (JRTemplatePrintText)element4;  	
									if(printElement.getKey()!=null && !printElement.getKey().equals("")){
										if(printElement.getKey().equals("${page_number}")){
											printElement.setText(String.valueOf(i+1));
										}else if(printElement.getKey().equals("${total_page_number}")){
											printElement.setText(String.valueOf(jasperPrint.getPages().size()));
										}else{
											printElement.setText(LocaleMsgCache.get2(0, locale, printElement.getKey().toString()));
										}
									}	 
								}								
							}
						}
					}
				}				
			}
		}
		
		return  jasperPrint;
	}
	

	public static String changeHtmlFont(String html){
		if(html==null || html.length()==0)return html;
		
		String newHtml=html; 		
		/*if(html.indexOf("<font face")!=-1){
			String[] htmlArray=html.split("<font face=");
			newHtml=htmlArray[0];
			for(int i=1;i<htmlArray.length;i++){
				htmlArray[i]=htmlArray[i].substring(htmlArray[i].indexOf('>'));
				newHtml+="<font face=\"DejaVu Serif\""+htmlArray[i];				
			}	
		}
		if(html.indexOf("<FONT FACE")!=-1){
			String[] htmlArray=html.split("<FONT FACE=");
			newHtml=htmlArray[0];
			for(int i=1;i<htmlArray.length;i++){
				htmlArray[i]=htmlArray[i].substring(htmlArray[i].indexOf('>'));
				newHtml+="<font face=\"DejaVu Serif\""+htmlArray[i];			
			}				
		}
		if(html.indexOf("<FONT face")!=-1){
			String[] htmlArray=html.split("<FONT face=");
			newHtml=htmlArray[0];
			for(int i=1;i<htmlArray.length;i++){
				htmlArray[i]=htmlArray[i].substring(htmlArray[i].indexOf('>'));
				newHtml+="<font face=\"DejaVu Serif\""+htmlArray[i];			
			}				
		}		
		if(html.indexOf("<font FACE")!=-1){
			String[] htmlArray=html.split("<font FACE=");
			newHtml=htmlArray[0];
			for(int i=1;i<htmlArray.length;i++){
				htmlArray[i]=htmlArray[i].substring(htmlArray[i].indexOf('>'));
				newHtml+="<font face=\"DejaVu Serif\""+htmlArray[i];			
			}				
		}	*/
		
		newHtml=newHtml.replaceAll(Pattern.quote("\\n"), ""); 
		newHtml=newHtml.replaceAll("STRONG>", "B>");
		newHtml=newHtml.replaceAll("strong>", "b>");

		return newHtml;
	}
}
