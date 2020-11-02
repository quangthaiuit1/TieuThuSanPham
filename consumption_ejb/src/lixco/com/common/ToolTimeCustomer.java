package lixco.com.common;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class ToolTimeCustomer {
	public interface IDay{
		int MONDAY=2;
		int TUESDAY=3;
		int WEDNESDAY=4;
		int THURSDAY=5;
		int FIRDAY=6;
		int SATURDAY=7;
		int SUNDAY=1;
		int END_DATE=1;
		int START_DATE=0;
		int END_WEEK=52;
	}
	public static int minDay(int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, month - 1);
		cal.set(Calendar.YEAR, year);
		int minDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		return minDay;
	}

	public static int maxDay(int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, month - 1);
		cal.set(Calendar.YEAR, year);
		int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return maxDay;
	}
	public static int getDayOFWeek(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}
	public static int getDayM(Date date){
		try{
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int daym=c.get(Calendar.DAY_OF_MONTH);
			return daym;
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	public static Date getDateMaxCustomer(int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, month - 1);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}

	public static Date getDateMinCustomer(int month, int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, month - 1);
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return cal.getTime();
	}
	
	public static Date convertStringToDateShort(String str_date) {
		try {
			DateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy");
			Date date = dateFormat.parse(str_date);
			return date;
			
		} catch (Exception e) {
			return null;
		}

	}
	public static Date convertStringToDate(String str_date){
		try {
			DateFormat dateFormat= new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				Date date = dateFormat.parse(str_date);
				return date;
			
		} catch (Exception e) {
			return null;
		}
	}
	public static Date convertStringToDate(String strDate,String pattern){
		try{
			DateFormat dateFormat= new SimpleDateFormat(pattern);
			Date date=dateFormat.parse(strDate);
			return date;
		}catch(Exception e){
			return null;
		}
	}
	public static Date getLastDateOfDay(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
	public static Date getFirstDateOfDay(Date date) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	public static String convertDatetoString(Date date){
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String dateFormat = formatter.format(date);
			return dateFormat;
		}catch(Exception e){
			return null;
		}
		
	}
	public static String convertDateToString(Date date,String pattern){
		try{
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			String dateFormat = formatter.format(date);
			return dateFormat;
		}catch(Exception e){
			return null;
		}
	}
	public static String convertDateToStringShort(Date date){
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String dateFormat = formatter.format(date);
			return dateFormat;
		}catch(Exception e){
		}
		return null;
	}
	public static Date minusOrAddDate(Date date,int track){
		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE,track);
			return cal.getTime();
		}catch(Exception e){
		}
		return null;
	}
	public static <T> int getWeekForDate(T dateObj){
		try{
			Calendar calendar = Calendar.getInstance();

			if(dateObj instanceof Date){
				calendar.setTime((Date)dateObj);
			}else if(dateObj instanceof String){
				calendar.setTime(ToolTimeCustomer.convertStringToDateShort((String)dateObj));
			}else{
				return -1;
			} 
			if(calendar.get(Calendar.DAY_OF_WEEK)==IDay.SUNDAY){
				int kt=calendar.get(Calendar.WEEK_OF_YEAR)-1;
				return kt==0 ? IDay.END_WEEK : kt;
			}else{
				return calendar.get(Calendar.WEEK_OF_YEAR);
			}
		}catch(Exception e){
		}
		return -1;
	}
	public static Date getDateForDayAndWeek(int weekYear, int weekOfYear,int dayOfWeek, int st){
		try{
			Calendar cal=Calendar.getInstance();
			if(dayOfWeek==IDay.SUNDAY){
				weekOfYear=weekOfYear+1;
			}
			cal.setWeekDate(weekYear, weekOfYear, dayOfWeek);
			if(st==IDay.END_DATE){
				 cal.set(Calendar.HOUR_OF_DAY, 23);
			     cal.set(Calendar.MINUTE, 59);
			     cal.set(Calendar.SECOND, 59);
			     cal.set(Calendar.MILLISECOND, 0);
			}else{
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
			}
			return cal.getTime();
		}catch(Exception e){
		}
		return null;
	}
	public static int getYearForDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	public static int getYearCurrent(){
		 Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			return cal.get(Calendar.YEAR);
	}
	public static int getMonthCurrent(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return cal.get(Calendar.MONTH)+1;
	}
	public static int getMonthM(Date date){
		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.get(Calendar.MONTH)+1;
		}catch(Exception e){
		}
		return -1;
	}
	public static int getYearM(Date date){
		try{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.get(Calendar.YEAR);
		}catch(Exception e){
		}
		return -1;
	}
	public static String timerAbout(Date fromDate,Date toDate){
		try{
			Date fromDate2=getFirstDateOfDay(fromDate);
			Date toDate2=getFirstDateOfDay(toDate);
			long diff2=toDate2.getTime()- fromDate2.getTime();
			long dayDiff2 =TimeUnit.MILLISECONDS.toDays(diff2);
			long diff=toDate.getTime() -fromDate.getTime();
			long fms=16*60*60*1000;
			long lms=15*60*60*1000;
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDate);
			int hourFrom =cal.get(Calendar.HOUR_OF_DAY);
			long realTime=0;
			if(dayDiff2>=1){
				if(hourFrom>=12){
					realTime= diff-lms-((dayDiff2-1)*fms);
				}else{
					realTime=diff-(dayDiff2 * fms);
				}
				if(realTime<=0){
					realTime=60*1000;
					
				}
				return String.format("%02d:%02d:%02d", 
						TimeUnit.MILLISECONDS.toHours(realTime),
						TimeUnit.MILLISECONDS.toMinutes(realTime) -  
						TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(realTime)), // The change is in this line
						TimeUnit.MILLISECONDS.toSeconds(realTime) - 
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(realTime)));
			}else{
				return String.format("%02d:%02d:%02d", 
						TimeUnit.MILLISECONDS.toHours(diff),
						TimeUnit.MILLISECONDS.toMinutes(diff) -  
						TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)), // The change is in this line
						TimeUnit.MILLISECONDS.toSeconds(diff) - 
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));
			}
		}catch(Exception e){
		}
		return "0";
	}
}
