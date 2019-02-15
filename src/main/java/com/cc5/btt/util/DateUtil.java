package com.cc5.btt.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Date utility
 * 
 * @author clayyu
 *
 */
public final class DateUtil {

	private static final SimpleDateFormat YYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");

	private static final SimpleDateFormat YYYYMM = new SimpleDateFormat("yyyyMM");

	private static final SimpleDateFormat MDYYYY = new SimpleDateFormat("M/d/yyyy");

	private static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

	private static final int DEF_NEXT_FLOW_CRD = 20;
	
	static {
		YYYYMMDD.setLenient(false);
		YYYYMM.setLenient(false);
		MDYYYY.setLenient(false);
		YYYY_MM_DD.setLenient(false);
	}

	/**
	 * get next Flow CRD (default 20th as CRD). <br/>
	 * Note: not thread safe
	 * 
	 * @param mdyyyy
	 * @return
	 */
	public synchronized static Date getNextFlowCRDMDYYYY(String mdyyyy) {
		try {
			Date inputDate = MDYYYY.parse(mdyyyy);
			Calendar inputCal = Calendar.getInstance();
			inputCal.setTime(inputDate);
			if (DEF_NEXT_FLOW_CRD <= inputCal.get(Calendar.DAY_OF_MONTH)) {
				inputCal.add(Calendar.MONTH, 1);
				inputCal.set(Calendar.DAY_OF_MONTH, DEF_NEXT_FLOW_CRD);
			} else {
				inputCal.set(Calendar.DAY_OF_MONTH, DEF_NEXT_FLOW_CRD);
			}
			return inputCal.getTime();
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * get next Flow CRD (default 20th as CRD).
	 * 
	 * @param inputDate
	 * @return
	 */
	public static Date getNextFlowCRD(Date inputDate) {
		Calendar inputCal = Calendar.getInstance();
		inputCal.setTime(inputDate);
		if (DEF_NEXT_FLOW_CRD <= inputCal.get(Calendar.DAY_OF_MONTH)) {
			inputCal.add(Calendar.MONTH, 1);
			inputCal.set(Calendar.DAY_OF_MONTH, DEF_NEXT_FLOW_CRD);
		} else {
			inputCal.set(Calendar.DAY_OF_MONTH, DEF_NEXT_FLOW_CRD);
		}
		return inputCal.getTime();
	}

	/**
	 * get flow number by input date. <br/>
	 * Note: not thread safe
	 * 
	 * @param mdyyyy
	 * @return
	 */
	public synchronized static int getFlowByDateMDYYYY(String mdyyyy) {
		try {
			Date inputDate = MDYYYY.parse(mdyyyy);
			Calendar inputCal = Calendar.getInstance();
			inputCal.setTime(inputDate);
			if (DEF_NEXT_FLOW_CRD <= inputCal.get(Calendar.DAY_OF_MONTH)) {
				return (inputCal.get(Calendar.MONTH) + 1) % 3 + 1;
			} else {
				return inputCal.get(Calendar.MONTH) % 3 + 1;
			}
		} catch (ParseException e) {
			return 0;
		}
	}

	/**
	 * get flow number by input date.
	 * 
	 * @param inputDate
	 * @return
	 */
	public static int getFlowByDate(Date inputDate) {
		Calendar inputCal = Calendar.getInstance();
		inputCal.setTime(inputDate);
		if (DEF_NEXT_FLOW_CRD <= inputCal.get(Calendar.DAY_OF_MONTH)) {
			return (inputCal.get(Calendar.MONTH) + 1) % 3 + 1;
		} else {
			return inputCal.get(Calendar.MONTH) % 3 + 1;
		}
	}

	/**
	 * format date to YYYYMMDD string.
	 * 
	 * @param inputDate
	 * @return
	 */
	public static String formatYYYYMMDD(Date inputDate) {
		return null == inputDate ? null : YYYYMMDD.format(inputDate);
	}

	/**
	 * format date to YYYYMM string.
	 * 
	 * @param inputDate
	 * @return
	 */
	public static String formatYYYYMM(Date inputDate) {
		return null == inputDate ? null : YYYYMM.format(inputDate);
	}

	/**
	 * format date to MDYYYY string.
	 * 
	 * @param inputDate
	 * @return
	 */
	public static String formatMDYYYY(Date inputDate) {
		return null == inputDate ? null : MDYYYY.format(inputDate);
	}


	public static Long getLongTime (String data) {
		Long longTime = null;
		try {
			Date d = YYYY_MM_DD.parse(data);
			longTime = d.getTime();
		} catch (Exception e) {
			e.fillInStackTrace();
			return null;
		}
		return longTime;
	}



	public static String getDate (Long longTime) {
		String dateStr = null;
		try {
			Date date = new Date(longTime);
			dateStr = YYYY_MM_DD.format(date);
		} catch (Exception e) {
			e.fillInStackTrace();
			return null;
		}
		return dateStr;
	}

	public static List<String> getDateList (String minDate, String maxDate) {
		List<String> result = new ArrayList<>();
		if (minDate.equals(maxDate)) {
			result.add(maxDate);
			return result;
		}
		result.add(minDate);
		try {
			int i = 1;
			while (true) {
				Calendar calendar = new GregorianCalendar();
				Date date = YYYY_MM_DD.parse(minDate);
				calendar.setTime(date);
				calendar.add(Calendar.DATE, i);
				date = calendar.getTime();
				String newDate = YYYY_MM_DD.format(date);
				if (newDate.equals(maxDate)) {
					result.add(maxDate);
					break;
				}
				result.add(newDate);
				i++;
			}
		} catch (Exception e) {
			e.fillInStackTrace();
			return null;
		}
		return result;
	}

	public static String getWeek (String dataStr) {
		String[] weekDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
		String week = null;
		try {
			Date date = YYYY_MM_DD.parse(dataStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int i = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			if (i < 0) {
				i = 0;
			}
			week = weekDays[i];
		} catch (Exception e) {

		}
		return week;
	}


	/**
	 * 根据一个日期和相差天数，得出另一个日期
	 * @param dataStr
	 * @param day
	 * @return
	 */
	public static String getFixedDate (String dataStr, int day) {
		try {
			Calendar calendar = new GregorianCalendar();
			Date date = YYYY_MM_DD.parse(dataStr);
			calendar.setTime(date);
			//day如果是正数则是增加，负数则减少
			calendar.add(Calendar.DATE, day);
			date = calendar.getTime();
			String newDate = YYYY_MM_DD.format(date);
			return newDate;
		} catch (Exception e) {
			e.fillInStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println(getWeek("2019-02-28"));
	}
}
