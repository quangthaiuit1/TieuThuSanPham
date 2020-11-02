package lixco.com.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class FormatHandler {
	private static FormatHandler instance_;
	private static final Lock createLock_ = new ReentrantLock();

	private FormatHandler() {
	}

	public static FormatHandler getInstance() {
		if (instance_ == null) {
			createLock_.lock();
			try {
				if (instance_ == null) {
					instance_ = new FormatHandler();
				}
			} finally {
				createLock_.unlock();
			}

		}
		return instance_;
	}

	public String getNumberFormat(double number, int max) {
		try {
			double r = roundCus(number, max);
			int sl = 0;
			String pattern = "#,###,###,###,###,###,##0.00";
			if (r % 1 == 0) {
				pattern = "#,###,###,###,###,###,##0";
			} else {
				String number1 = String.valueOf(number);
				sl = number1.split("\\.")[1].length();
			}
			DecimalFormat decimalFormat = new DecimalFormat(pattern);
			if (sl != 1) {
				sl = countZero(max);
			}
			decimalFormat.setMaximumFractionDigits(sl);
			return decimalFormat.format(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public String getNumberFormatEn(double number, int max) {
		try {
			double r = roundCus(number, max);
			int sl = 0;
			String pattern = "#,###,###,###,###,###,##0.00";
			if (r % 1 == 0) {
				pattern = "#,###,###,###,###,###,##0";
			} else {
				String number1 = String.valueOf(number);
				sl = number1.split("\\.")[1].length();
			}
			DecimalFormat decimalFormat = new DecimalFormat(pattern);
			if (sl != 1) {
				sl = countZero(max);
			}
			decimalFormat.setMaximumFractionDigits(sl);
			String str=decimalFormat.format(r);
			String[] arr=str.split("\\.");
			String last=arr[0].replaceAll(",", "\\.");
			if(arr.length>1){
				last=last+","+arr[1];
			}
			return last;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private int countZero(int number) {
		if (number == 0) {
			return 0;
		}
		int counter = 0;
		while (number % 10 == 0) {
			counter++;
			number /= 10;
		}
		return counter;
	}

	public double roundCus(double number, int max) {
		try {
			return (double) Math.round(number * max) / max;
		} catch (Exception e) {
			return 0;
		}
	}
	public double changeStringToNumber(String number) {
		try {
			String temp = number.replaceAll("\\.", "");
			temp = temp.replace(",", ".");
			double result = Double.parseDouble(temp);
			return result;
		} catch (Exception e) {
			return 0;
		}

	}
	public String fromToString(String str){
		String summary="";
		try{
			if(str!=null && str !=""){
				String[] arr=str.split(",");
				summary+=arr[0];
				if(arr.length>1){
					summary+=" -> "+arr[arr.length-1];
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return summary;
	}
	public double sum(double... params){
		BigDecimal result=BigDecimal.valueOf(0.0);
		BigDecimal t=null;
		for(double item:params){
			t=BigDecimal.valueOf(item);
			result=result.add(t);
		}
		return result.doubleValue();
	}
	public  String converViToEn(String s) {
		try{
			s=s.toLowerCase().trim();
			String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
			Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
			String result = pattern.matcher(temp).replaceAll("");
			return pattern.matcher(result).replaceAll("").replaceAll("đ", "d");
		}catch (Exception e) {
		} 
		return null;
	}
}
