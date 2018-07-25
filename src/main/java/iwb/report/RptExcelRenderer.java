/*
 * Created on 10.Mar.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package iwb.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import iwb.cache.FrameworkSetting;
import iwb.domain.helper.W5ReportCellHelper;
import iwb.util.GenericUtil;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RptExcelRenderer extends AbstractExcelView {
	private	List<W5ReportCellHelper> mva = null;
	/*private	WReportResult baslik = null;
	
	/*
	@Override
	protected void buildExcelDocument(
			Map model, HSSFWorkbook wb, HttpServletRequest request, HttpServletResponse response)
			throws NoSuchMessageException {
		final short[] alignMap=new short[]{HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.ALIGN_RIGHT};
		mva = (List) model.get("report");
		baslik=mva.get(0);
		
		// As we use a from scratch document, we create a new sheet.

		HSSFSheet sheet = wb.createSheet("PromisCRM");
		// If we will use the first sheet from an existing document, replace by this:
		// sheet = wb.getSheetAt(0);

		// We simply put an error message on the first cell if no list is available
		// Nevertheless, it should never be null as the controller verify it.


		// We create a font for headers
		HSSFFont f = wb.createFont();
		// set font 1 to 12 point type
		f.setFontHeightInPoints((short) 12);
		// make it blue
//		f.setColor((short) 0xc);
		// make it bold arial is the default font
		f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		// We create a style for headers
		HSSFCellStyle cs = wb.createCellStyle();
		cs.setFont(f);
		cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		// The same for properties data
		HSSFFont fp = wb.createFont();
//		fp.setColor((short) 0xc);
		HSSFCellStyle csp[] = {wb.createCellStyle(),wb.createCellStyle(),wb.createCellStyle()};
		csp[0].setFont(fp);
		csp[0].setAlignment(HSSFCellStyle.ALIGN_CENTER);
		csp[1].setFont(fp);
		csp[1].setAlignment(HSSFCellStyle.ALIGN_LEFT);
		csp[2].setFont(fp);
		csp[2].setAlignment(HSSFCellStyle.ALIGN_RIGHT);

		// We create a date style
		HSSFCellStyle dateStyle = wb.createCellStyle();
		dateStyle.setFont(fp);
		dateStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("mm.dd.yyyy"));

		int rowid = 3, cellid;
		Iterator i;
		HSSFCell cell;
		sheet.setColumnWidth((short) 1, (short) (5));

		cell= getCell(sheet, rowid, 2);
		cell.setCellStyle(cs);
		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
		cell.setCellValue(baslik.getDeger());
		
		rowid+=2;

		for(WReportResult sp:mva)if(sp.getRowTip()==1){
//			sheet.setColumnWidth((short) cellid, (short) (s.getColumnLength().shortValue()*50));
			cell= getCell(sheet, rowid, 2);
			cell.setCellStyle(csp[2]);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellValue(sp.getTag());
			cell= getCell(sheet, rowid++, 3);
			cell.setCellStyle(csp[1]);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellValue(sp.getDeger());
		}
		rowid++;
		
// We create a second shhet for the data

		// We put now the headers of the list on the sheet
		int columnCount = 0;
		for(WReportResult c:mva)if(c.getRowTip()==2){
			columnCount++;
		} else if(c.getRowTip()>2)break;
		cellid=2;
		int[] column_aligns = new int[columnCount+1];
		for(WReportResult c:mva)if(c.getRowTip()==2){
			sheet.setColumnWidth((short) cellid, (short) (c.getCellTip()*50));
			cell= getCell(sheet, rowid, cellid++);
			cell.setCellStyle(cs);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellValue(c.getDeger());
			column_aligns[c.getColumnId()] = c.getColspan();
		}else if(c.getRowTip()>2)break;
		
//		rowid++;

		// We put now the countries from the list on the sheet
		for(WReportResult sonuc:mva)if(sonuc.getRowTip()==3) {
			if(sonuc.getCellTip()==4)continue;
			cell= getCell(sheet, sonuc.getRowId()+rowid, sonuc.getColumnId()+1);
			if(sonuc.getDeger()!=null){
				//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				cell.setCellStyle(csp[column_aligns[sonuc.getColumnId()]]);
//				cell.getCellStyle().setAlignment(column_aligns[s.getComp_id().getColumnId().intValue()]);
				cell.setCellValue(sonuc.getDeger());
			}
		}
	}*/

	
	@Override
	protected void buildExcelDocument(
			Map model, HSSFWorkbook wb, HttpServletRequest request, HttpServletResponse response)
			throws NoSuchMessageException {
		final short[] alignMap=new short[]{HSSFCellStyle.ALIGN_CENTER, HSSFCellStyle.ALIGN_LEFT, HSSFCellStyle.ALIGN_RIGHT};
		mva = (List) model.get("report");
		
		// As we use a from scratch document, we create a new sheet.
		List<HSSFSheet> sheetList = new ArrayList<HSSFSheet>();
		Map<Integer, Short> kolonAlignList = new HashMap<Integer,Short>();
		
		// The same for properties data
		HSSFFont fp = wb.createFont();
//		fp.setColor((short) 0xc);
		HSSFCellStyle csp[] = {wb.createCellStyle(),wb.createCellStyle(),wb.createCellStyle()};
		csp[0].setFont(fp);
		csp[0].setAlignment(HSSFCellStyle.ALIGN_CENTER);
		csp[1].setFont(fp);
		csp[1].setAlignment(HSSFCellStyle.ALIGN_LEFT);
		csp[2].setFont(fp);
		csp[2].setAlignment(HSSFCellStyle.ALIGN_RIGHT);

		// We create a date style
		HSSFCellStyle dateStyle = wb.createCellStyle();
		dateStyle.setFont(fp);
		dateStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("mm.dd.yyyy"));
		int carpan = 300;

		int rowid = 1, sheetIndex = 0, kolonSayisi = 0, lastRowId = 0, eksiRow = 0, artiRow = -1;
		HSSFCell cell;	
		boolean baslikVar = false;

		for(W5ReportCellHelper rp : mva){
			if (rp.getColumnId()<1)
				continue;
			//hangi sheet in kullanılacağı belirleniyor
			if(rp.getRowTip()==100){//yeni sayfa
				HSSFSheet sheet = wb.createSheet(rp.getDeger()== null ? "Page"+(sheetList.size()+1) : rp.getDeger());
				sheetList.add(sheet);	
				baslikVar = false;
				kolonSayisi = 0;
				artiRow = -1;
				eksiRow = eksiRow + lastRowId;
				if (sheetList.size()>1){
					for (int i = 0; i<kolonAlignList.size();i++){
						sheetList.get(sheetList.size()-1).setColumnWidth(i, sheetList.get(0).getColumnWidth(i));	
					}								
				}
			}
			if (sheetList.size()==0){
				HSSFSheet sheet = wb.createSheet("IWorkBetter");
				sheetList.add(sheet);								
			}
			sheetIndex = sheetList.size()-1;
			
			int RowId = rp.getRowId() + artiRow - eksiRow;
			String tag = rp.getTag();	
			if (tag == null) tag = "";
			String[] tagList = tag.split(";");
			
			//içerik hazırlanıyor
			if(rp.getRowTip()==0){//başlık
				baslikVar = true;
				cell= getCell(sheetList.get(sheetIndex), 0, rp.getColumnId()-1);
				setCellStyle2(wb, cell, HSSFCellStyle.ALIGN_LEFT, (short)12, HSSFFont.BOLDWEIGHT_BOLD, "", "");
				cell.setCellValue(rp.getDeger());	
				artiRow = artiRow + 1;
			}
			else if(rp.getRowTip()==1){//parametre
				cell= getCell(sheetList.get(sheetIndex), RowId, rp.getColumnId()-1);				
				cell.setCellValue(tag + " : " + rp.getDeger());	
				if (rp.getColspan()==-1){
					carpan = 50;
				}
			}
			else if(rp.getRowTip()==2){//kolon başlığı
				cell= getCell(sheetList.get(sheetIndex), RowId , rp.getColumnId()-1);
				short cwidth = rp.getCellTip()>200 ? 200 : rp.getCellTip();
				sheetList.get(sheetIndex).setColumnWidth(rp.getColumnId()-1, (cwidth * carpan));				
				cell.setCellValue(rp.getDeger());	
				if (kolonSayisi<(rp.getColumnId()-1))
					kolonSayisi = rp.getColumnId()-1;				
		        try{setCellStyle2(wb, cell, (getTagValue(tagList, "A").equals("") ? HSSFCellStyle.ALIGN_CENTER : GenericUtil.uShort(getTagValue(tagList, "A"))), (short)10, HSSFFont.BOLDWEIGHT_BOLD, getTagValue(tagList, "B"), getTagValue(tagList, "BC"));}catch(Exception e){}	        
		        //başlık kısmının colspan ayarı yapılıyor
				if (baslikVar){ 
					//sheetList.get(sheetIndex).addMergedRegion(new Region(0,(short)0,0,(short)kolonSayisi));
				}
				int algn =1;
				if (rp.getColspan()==0) 
					algn=2;
				else if (rp.getColspan()==2) 
					algn = 3;					
				kolonAlignList.put(rp.getColumnId(), (short)algn);
			}
			else if(rp.getRowTip()==3){//kolon içeriği
				cell= getCell(sheetList.get(sheetIndex), RowId, rp.getColumnId()-1);	
				String dataType = getTagValue(tagList, "T");
				if ((dataType != null)&&(dataType.equals("1"))){
					try{
						cell.setCellValue(Double.parseDouble(rp.getDeger().replace(",", ".")));
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					}catch(Exception e){
						cell.setCellValue(rp.getDeger());
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					};
				}
				else if ((dataType != null)&&(dataType.equals("2"))){					
					try{
						cell.setCellType(HSSFCell.CELL_TYPE_FORMULA);
						cell.setCellFormula(rp.getDeger());
					}catch(Exception e){cell.setCellValue(rp.getDeger());};					
				}
				else{
					cell.setCellValue(rp.getDeger());
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				}
				
				short cell_tip = rp.getCellTip();  
				short font_weight = HSSFFont.BOLDWEIGHT_NORMAL;
				if (cell_tip > 0){
					font_weight = HSSFFont.BOLDWEIGHT_BOLD;
				}
				try{setCellStyle2(wb, cell, (getTagValue(tagList, "A").equals("") ? kolonAlignList.get(rp.getColumnId()) : GenericUtil.uShort(getTagValue(tagList, "A"))), (short)10, font_weight, getTagValue(tagList, "B"),getTagValue(tagList, "BC"));}catch(Exception e){}
				
				if (rp.getColspan()>1){
					short toCol = (short)(rp.getColumnId()-1 + rp.getColspan()-1);
					sheetList.get(sheetIndex).addMergedRegion(new Region(RowId,(short)(rp.getColumnId()-1),RowId,toCol));
					cell= getCell(sheetList.get(sheetIndex), RowId, toCol);
					try{setCellStyle2(wb, cell, kolonAlignList.get(rp.getColumnId()), (short)10, font_weight, getTagValue(tagList, "B"),getTagValue(tagList, "BC"));}catch(Exception e){}
				}				
			}
			lastRowId= RowId;			
		}
				
		
	}
	
	private String getTagValue(String[] tagList, String key){
		String value = "";
		try
		{
			for(int i = 0; i<tagList.length; i++){
				String[] kv= tagList[i].split(":"); 
				if (kv[0].equals(key)){
					value = kv[1];
					break;
				}
			}
		} catch (Exception e) {
			if (FrameworkSetting.debug)
				e.printStackTrace();
		}	
		return value;
	}
	
	private void setCellStyle2(HSSFWorkbook wb, HSSFCell cell, short valign, short fontSize, short fontWeight, String border, String backroundColor) {
		HSSFFont f = wb.createFont();
		HSSFCellStyle style = wb.createCellStyle();
		style.setWrapText(true);
		f.setBoldweight(fontWeight);
		f.setFontHeightInPoints(fontSize);
		style.setFont(f);
		style.setAlignment(valign);
	    if (GenericUtil.uShort(border) != null){
	    	short borderw = GenericUtil.uShort(border);
			style.setBorderBottom(borderw);
			style.setBorderTop(borderw);
			style.setBorderLeft(borderw);
			style.setBorderRight(borderw);			
		};	
		if (GenericUtil.uShort(backroundColor) != null){
			style.setFillForegroundColor(GenericUtil.uShort(backroundColor));		 
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		}
        cell.setCellStyle(style);
    }

}
