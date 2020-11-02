package trong.lixco.com.util;


import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ToExcel {

	public static void writeXLS(List<Object[]> params, ServletOutputStream svl) throws IOException {
		// Create blank workbook
		HSSFWorkbook workbook = new HSSFWorkbook();
		// Create a blank sheet
		HSSFSheet spreadsheet = workbook.createSheet("Sheet1");

		// Create row object
		Row row;
		// This data needs to be written (Object[])
		Map<String, Object[]> empinfo = new TreeMap<String, Object[]>();
		for (int i = 0; i < params.size(); i++) {
			empinfo.put((i + 1) + "", params.get(i));
		}
		// Iterate over data and write to sheet
		Set<String> keyid = empinfo.keySet();
		int rowid = 0;
		SimpleDateFormat fd = new SimpleDateFormat("dd-MM-yyyy");
		for (String key : keyid) {
			row = spreadsheet.createRow(rowid++);
			Object[] objectArr = empinfo.get(key);
			int cellid = 0;
			for (Object obj : objectArr) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue(obj + "");
				if (obj != null && obj.getClass().equals(Double.class)) {
					cell.setCellValue((double) obj);
				} else if (obj != null && obj.getClass().equals(Date.class)) {

					cell.setCellValue(fd.format((Date) obj));
				} else {
					cell.setCellValue(obj + "");
				}
			}
		}

		// Write the workbook in file system
		workbook.write(svl);
	}

	public static void writeXLS2(List<Object[]> params, ServletOutputStream svl) throws IOException {
		// Create blank workbook
		HSSFWorkbook workbook = new HSSFWorkbook();
		// Create a blank sheet
		HSSFSheet spreadsheet = workbook.createSheet("Sheet1");
		// Create row object
		Row row;
		SimpleDateFormat fd = new SimpleDateFormat("dd-MM-yyyy");
		for (int i = 0; i < params.size(); i++) {
			row = spreadsheet.createRow(i + 1);
			for (int j = 0; j < params.get(i).length; j++) {
				Cell cell = row.createCell(j + 1);
				Object obj = params.get(i)[j];
				if (obj != null && obj.getClass().equals(Double.class)) {
					cell.setCellValue((double) obj);
				} else if (obj != null && obj.getClass().equals(Date.class)) {

					cell.setCellValue(fd.format((Date) obj));
				} else {
					cell.setCellValue(obj + "");
				}
			}
		}

		// Write the workbook in file system
		workbook.write(svl);
	}

	public static double formatNumber(int sole, double value) {
		BigDecimal bd = new BigDecimal(value);
		BigDecimal bd2 = bd.setScale(sole, BigDecimal.ROUND_HALF_UP);

		return bd2.doubleValue();
	}

	public static double formatNumber(double value) {
		BigDecimal bd = new BigDecimal(value);
		BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);

		return bd2.doubleValue();
	}

	
	public static void main(String[] args) {
		System.out.println(12345 + 54321l);
	}
	
}
