package io.kiah.common.pool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
	private static final Logger log = LoggerFactory.getLogger(DateUtils.class);
	private static final String TEMPLATE = "yyyy-MM-dd HH:mm:ss.SSS";
	private static final Date DATE_APP_START = new Date();

	/**
	 * 获得当前时间
	 */
	public static String getCurrentDateStr() {
		return date2String(getCurrentDate());
	}

	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 把date型的变成String类型
	 */
	public static String date2String(Date date) {
		SimpleDateFormat SF = new SimpleDateFormat(TEMPLATE);
		return SF.format(date);
	}

	/**
	 * 把String类型转换成date类型
	 */
	public static Date string2Date(String strDate) {
		if (StringUtils.isNullOrEmpty(strDate))
			return DATE_APP_START;

		char tz = strDate.charAt(strDate.length() - 6);
		if (tz == '-' || tz == '+') {
			strDate = strDate.substring(0, strDate.length() - 6);
		}

		if (strDate.indexOf(".") < 0) {
			strDate += ".000";
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(TEMPLATE);
			return sdf.parse(strDate);
		} catch (ParseException e) {
			throw new IllegalArgumentException(
					"'" + strDate + "' is not accord with template:'" + TEMPLATE + "' \n" + e);
		}
	}

	/**
	 * 获得系统当前时间的：小时值
	 */
	public static int getNewHour() {
		GregorianCalendar gcalendar = new GregorianCalendar();
		gcalendar.setTime(new Date(System.currentTimeMillis()));
		return gcalendar.get(GregorianCalendar.HOUR_OF_DAY);
	}

	/**
	 * 给指定的date添加给定的时间
	 */
	public static Date getNewDate(Date oldDate, int hour) {
		GregorianCalendar gcalendar = new GregorianCalendar();
		gcalendar.setTime(oldDate);
		gcalendar.add(GregorianCalendar.HOUR_OF_DAY, hour);
		return gcalendar.getTime();
	}

	/**
	 * 给指定的date添加给定的时间
	 */
	public static Date getThenNewDate(int days) {
		GregorianCalendar gcalendar = new GregorianCalendar();
		gcalendar.add(GregorianCalendar.DATE, days);
		return gcalendar.getTime();
	}

	/**
	 * 把指定的时间字符串（xxH:xxM:xxS）设置给当前的时间
	 */
	public static Date getDateFromDateStr(String dateStr) {
		String template = "yyyy MM dd";
		SimpleDateFormat sf = new SimpleDateFormat(template);
		String useDateStr = sf.format(new Date(System.currentTimeMillis()));

		String hour = "0";
		String minute = "0";
		String second = "0";
		if (dateStr == null || dateStr.isEmpty()) {
			throw new IllegalArgumentException("arg:dateStr is null Or arg=\"\"! ");
		}
		int hIndex = dateStr.indexOf("H") == -1 ? dateStr.indexOf("h") : dateStr.indexOf("H");
		int mIndex = dateStr.indexOf("M") == -1 ? dateStr.indexOf("m") : dateStr.indexOf("M");
		int sIndex = dateStr.indexOf("S") == -1 ? dateStr.indexOf("s") : dateStr.indexOf("S");
		// 获得用户配置的hour值
		if (hIndex != -1) {
			hour = dateStr.substring(0, hIndex);
		}
		// 获得用户配置的minute值
		if (mIndex != -1 && hIndex != -1) {
			minute = dateStr.substring(hIndex + 2, mIndex);
		} else if (mIndex != -1) {
			minute = dateStr.substring(0, mIndex);
		}
		// 获得用户配置的second值
		if (sIndex != -1 && mIndex != -1) {
			second = dateStr.substring(mIndex + 2, sIndex);
		} else if (mIndex == -1) {
			second = dateStr.substring(0, sIndex);
		}

		// 将1位的值变成2位
		if (hour.length() == 1) {
			hour = "0" + hour;
		}
		if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if (second.length() == 1) {
			second = "0" + second;
		}

		// 拼接用户定义的时间
		useDateStr = useDateStr + " " + hour + ":" + minute + ":" + second;
		return string2Date(useDateStr);
	}

	/**
	 * 判断当前时间 是否在给定的时间段中(时间段的格式:08H:23M-20H:30M)
	 */
	public static boolean isInRange(String rangeStr) {
		if (rangeStr == null || rangeStr.isEmpty() || !rangeStr.contains("-")) {
			throw new IllegalArgumentException("arg:rangeStr is null Or arg=\"\" Or not have '-' ");
		}
		String[] arr = rangeStr.split("-");
		String min = arr[0];
		String max = arr[1];
		Date minDate = getDateFromDateStr(min);
		Date maxDate = getDateFromDateStr(max);

		Date currentDate = new Date(System.currentTimeMillis());
		// System.out.println(currentDate.after(minDate));
		// System.out.println(currentDate.before(maxDate));
		if (minDate.after(maxDate)) {
			// 表示时间范围过24点了,这样要给max加上1day
			return currentDate.after(minDate) || currentDate.before(maxDate);
		} else {
			return currentDate.after(minDate) && currentDate.before(maxDate);
		}

	}

	/**
	 * 测试
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Date date = getThenNewDate(0);
		Date date2 = getThenNewDate(-1);
		System.out.println(date.compareTo(date2));
		for (int i = 0; i < 100; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println(string2Date("1921-05-04 12:23:34.234"));
				}
			}).start();
		}

	}
}
