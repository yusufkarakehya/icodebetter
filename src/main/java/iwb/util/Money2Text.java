package iwb.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import iwb.cache.FrameworkCache;
import iwb.cache.LocaleMsgCache;
import iwb.domain.db.W5LookUp;

public class Money2Text {

	//türkçe
	public static final String[] oneToTwentyTr = { "", "BİR", "İKİ", "ÜÇ",
		"DÖRT", "BEŞ", "ALTI", "YEDİ", "SEKİZ", "DOKUZ", "ON", "ONBİR",
		"ONİKİ", "ONÜÇ", "ONDÖRT", "ONBEŞ", "ONALTI", "ONYEDİ", "ONSEKİZ",
		"ONDOKUZ", "" };
	
	public static final String[] tensTr = { "", "ON", "YİRMİ", "OTUZ", "KIRK",
	"ELLİ", "ALTMIŞ", "YETMİŞ", "SEKSEN", "DOKSAN" };
	
	public static final String[] hundredsTr = { "", "YÜZ", "İKİYÜZ", "ÜÇYÜZ",
	"DÖRTYÜZ", "BEŞYÜZ", "ALTIYÜZ", "YEDİYÜZ", "SEKİZYÜZ", "DOKUZYÜZ" };
	
	public static final String[] carpanTr = {"", "BİN", "MİLYON", "MİLYAR"}; 
	
	//ingilizce
	public static final String[] oneToTwentyEn = { "", "ONE", "TWO", "THREE",
		"FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN",
		"TWELVE", "THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN", "EIGHTEEN",
		"NINETEEN", "" };
	
	public static final String[] tensEn = { "", "TEN", "TWENTY", "THIRTY", "FOURTY",
	"FIFTY", "SIXTY", "SEVENTY", "EIGHTY", "NINETY" };
	
	public static final String[] hundredsEn= { "", "ONE HUNDRED", "TWO HUNDRED", "THREE HUNDRED",
	"FOUR HUNDRED", "FIVE HUNDRED", "SIX HUNDRED", "SEVEN HUNDRED", "EIGHT HUNDRED", "NINE HUNDRED" };

	public static final String[] carpanEn = {"", "THOUSAND", "MILLION", "BILLION"}; 
	
	//almanca
	public static final String[] oneToTwentyDe = { "", "EINS", "ZWEI", "DREI",
		"VIER", "FÜNF", "SECHS", "SIEBEN", "ACHT", "NEUN", "ZEHN", "ELF",
		"ZWÖLF", "DREIZEHN", "VIERZEHN", "FÜNFZEHN", "SECHZEHN", "SIEBZEHN", "ACHTZEHN",
		"NEUNZEHN", "" };
	
	public static final String[] tensDe = { "", "ZEHN", "ZWANZIG", "DREIBIG", "VIERZIG",
	"FÜNFZIG", "SECHZIG", "SIEBZIG", "ACHTZIG", "NEUNZIG" };
	
	public static final String[] hundredsDe= { "", "EINS HUNDERT", "ZWEI HUNDERT", "DREI HUNDERT",
	"VIER HUNDERT", "FÜNF HUNDERT", "SECHS HUNDERT", "SIEBEN HUNDERT", "ACHT HUNDERT", "NEUN HUNDERT" };
	
	public static final String[] carpanDe = {"", "TAUSEND", "MILLION", "MILLIARDE"}; 
		
	//
	
	public static String convertToLocalMoney(String money){
		if(money.contains("TRL")){
			money= money.replace("TRL","TL");
		}		
		return money;
	}
		
	public static String convertGroup(int number, String locale) {		
        String[] oneToTwenty, tens, hundreds;
        String yuz= "";
    	String text= "";
    	
        if(locale.equals("tr")){
            oneToTwenty = oneToTwentyTr;
            tens = tensTr;
            hundreds = hundredsTr;
            yuz = "YÜZ";

        } else if(locale.equals("en")){
            oneToTwenty = oneToTwentyEn;
            tens = tensEn;
            hundreds = hundredsEn;
            yuz = "HUNDRED";
        }
        else{
            oneToTwenty = oneToTwentyDe;
            tens = tensDe;
            hundreds = hundredsDe;
            yuz = "HUNDERT";
        }

		if (number % 100 < 20) {
			text = oneToTwenty[number % 100];
			number = number / 100;
		} else {
			text = oneToTwenty[number % 10];
			number /= 10;
			text = tens[number % 10] + text;
			number /= 10;
		}
		if (number == 0) {
			return text;
		}
		if (number == 1) {
			number--;
		}
		
		return oneToTwenty[number] + yuz + text;
				
	}

	public static String getCarpanText(int carpan, String locale){
		String text = "";
		
        if(locale.equals("tr")){
            text = carpanTr[carpan];

        } else if(locale.equals("en")){
            text = carpanEn[carpan];
        }
        else{
            text = carpanDe[carpan];
        }		
		
		return text;
	}
	
	public static String convert(long number, String locale) {
		
		String text = "";
		String carpanText = "";
		int i = 0;
		long carpan[] = {1000, 1000000, 1000000000, 1000000000000L};
		
		while(i == 0 || carpan[i-1] <= number){
			if(i == 0 && number % carpan[i] != 0){
				text = convertGroup((int)(number % carpan[i]), locale);
			}
			if(i > 0){
				int sayi = (int)((number % carpan[i]) / carpan[i-1]);
				if(sayi > 0){
					if(!(i == 1 && sayi == 1)){
						text = convertGroup(sayi, locale)+getCarpanText(i, locale)+text;
					}
					else{
						text = getCarpanText(i, locale)+text;
					}
				}
			}
			i++;
		}
		
		return text;
	}

	public static String getKuruslar(String moneyType, String locale) {
		W5LookUp lud = FrameworkCache.getLookUp(0, 645);
		String moneyTypeDsc="";
		if(lud!=null){
			Integer moneyTypeId   = GenericUtil.uInt( lud.get_detayMap().get(moneyType).getVal());
			moneyTypeDsc = lud.get_detayList().get(moneyTypeId-1).getDsc();
			moneyTypeDsc=LocaleMsgCache.get2(0, locale, moneyTypeDsc);
		}		
		return moneyTypeDsc;
	}

	public static String StartConvert(double money, String  para_birimi, String locale) {	
		Locale dlocale = Locale.getDefault();
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(dlocale);
        String decimalSeperator=Character.toString(dfSymbols.getDecimalSeparator());  
        String textmoney[] =new DecimalFormat("########0.00").format(money).replace(decimalSeperator,"XXX").split("XXX");      
		
		String texttotal = "";
		if (Integer.parseInt(textmoney[0]) > 0) {
			W5LookUp lu = FrameworkCache.getLookUp(0, 672);
			String moneyTypeDsc="";
			if(lu!=null){
				moneyTypeDsc = lu.get_detayMap().get(para_birimi).getDsc();
				moneyTypeDsc=LocaleMsgCache.get2(0, locale, moneyTypeDsc);
			}
			texttotal = convert(Integer.parseInt(textmoney[0]),locale) + " "+convertToLocalMoney(moneyTypeDsc)+" ";
		}

		if (textmoney.length == 2) { // Kuruşlar varsa
			textmoney[1] += "0";
			textmoney[1] = textmoney[1].substring(0, 2);
			if (Integer.parseInt(textmoney[1]) != 0) {
				texttotal += convert(Long.parseLong(textmoney[1]),locale) + " "+getKuruslar(para_birimi, locale);
			}
		}

		return texttotal.toUpperCase();
	}

}