package iwb.util;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import iwb.cache.FrameworkSetting;
import jxl.CellReferenceHelper;


public class ExcelUtil {
	
	private String filePath;
	
	public ExcelUtil(String filePath){
		this.filePath = filePath;
	}
	
	public LinkedHashMap<String,List<HashMap<String,String>>> parseExcel(){
    	try{
			Workbook wb = WorkbookFactory.create(new FileInputStream(filePath));
		    return parseNewExcelFormats(wb);
	    }catch(org.apache.poi.hssf.OldExcelFormatException e){
		    // sadece biff5 iÃ§in farklÄ±
	    	return parseOldExcelFormats(filePath);
	    }catch (Exception e){		
	        if(FrameworkSetting.debug)e.printStackTrace();
			return null;
	    }
	}
	
	private LinkedHashMap<String,List<HashMap<String,String>>> parseNewExcelFormats(Workbook wb){
		LinkedHashMap<String,List<HashMap<String,String>>> tmp = new LinkedHashMap<String,List<HashMap<String,String>>>();
		try{
	        for (int k = 0; k < wb.getNumberOfSheets(); k++){	        	
	            Sheet sheet = wb.getSheetAt(k);
	            List<HashMap<String,String>> l = new ArrayList<HashMap<String,String>>();
	            int rows = sheet.getPhysicalNumberOfRows();
	            for (int r = 0; r < rows; r++) {
	                Row row = sheet.getRow(r);
	                if (row != null){
	                	HashMap<String,String> m = new HashMap<String,String>();
	                    int cells = row.getLastCellNum();
	                    if(cells>0){
	                        for (short c = 0; c < cells; c++){
	                            Cell cell  = row.getCell(c);
                                String cellValue = null;
	                            if (cell != null) {                                 
	                                switch (cell.getCellType()) {
	                                case Cell.CELL_TYPE_STRING:
	                                	cellValue = cell.getStringCellValue();
	                                    break;
	                                case Cell.CELL_TYPE_FORMULA:
	                                	cellValue = cell.getCellFormula();
	                                    break;
	                                case Cell.CELL_TYPE_NUMERIC:
	                                    if (DateUtil.isCellDateFormatted(cell))
	                                    	cellValue = (new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")).format(cell.getDateCellValue());
	                                    else{
	                                    	if(cell.getCellStyle().getDataFormatString() =="@")// format @ ise integer deÄŸilse double.
	                                    		cellValue = Integer.toString((int)cell.getNumericCellValue()); 
	                                    	else
	                                    		cellValue=BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();  	//4E+2 gibi gelen sayÄ±lar iÃ§in yapÄ±ldÄ±
	                                    		
                                    		if(cellValue.length()>2 && cellValue.substring(cellValue.length()-2,cellValue.length()).equalsIgnoreCase(".0"))
                                    		{
                                    			cellValue=cellValue.substring(0,cellValue.length()-2);
                                    		}
	                                    }
	                                    break;
	                                case Cell.CELL_TYPE_BLANK:
	                                	cellValue = "";
	                                    break;
	                                case Cell.CELL_TYPE_BOOLEAN:
	                                	cellValue = Boolean.toString(cell.getBooleanCellValue());
	                                    break;
	                                }
	                            }
	                            m.put(CellReference.convertNumToColString(c), cellValue);
	                        }
	                        l.add(m);
	                    }
	                }
	            }
	            tmp.put(sheet.getSheetName(),l);
	        }
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return tmp;
	}
	
	private LinkedHashMap<String,List<HashMap<String,String>>> parseOldExcelFormats(String filePath){
		LinkedHashMap<String,List<HashMap<String,String>>> tmp = new LinkedHashMap<String,List<HashMap<String,String>>>();		
		try{
			jxl.Cell rowData[] = null;
			jxl.Workbook wb = jxl.Workbook.getWorkbook(new FileInputStream(filePath));
	        for (int k = 0; k < wb.getNumberOfSheets(); k++){
	            jxl.Sheet sheet = wb.getSheet(k);
	            List<HashMap<String,String>> l = new ArrayList<HashMap<String,String>>();
	            int rows  = sheet.getRows();
	            for (int r = 0; r < rows; r++) {
	            	rowData = sheet.getRow(r);
	                if (rowData != null) { 
	                	HashMap<String,String> m = new HashMap<String,String>();
	                    int cells = rowData.length;
	                    if(cells>0){
                        	String cellValue = null;
	                        for (short c = 0; c < cells; c++){
	                        	jxl.Cell cell  = rowData[c];
	                            if (cell != null) {
		                            if (cell.getType() == jxl.CellType.NUMBER){
		                            	cellValue = cell.getContents();
		                            }
		                            else if (cell.getType() == jxl.CellType.DATE){
		                            	jxl.DateCell dc = (jxl.DateCell) cell;
		                            	SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		                            	format.setTimeZone(TimeZone.getTimeZone("GMT"));
		                            	cellValue = format.format(dc.getDate());
		                            }
		                            else{
		                            	cellValue = cell.getContents();	                            	
		                            }
	                            }
	                            m.put(CellReferenceHelper.getColumnReference(c), cellValue);
	                        }
	                        l.add(m);
	                    }
	                }
	            }
	            tmp.put(sheet.getName(),l);
	        }
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return tmp;
	}
}
