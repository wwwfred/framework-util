package net.wwwfred.framework.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import net.wwwfred.framework.util.log.LogUtil;

public class DatetimeUtil {
	private static final long TIME_ZONE_MILLIS = getTimeZoneMillis();
	private static final long FIRST_DAY_MILLIS = 24*60*60*1000 + TIME_ZONE_MILLIS;
	 
//	/**
//	 * 所有的DateTimeFormat中value组成的formatMap,静态资源
//	 */
//	private static final Map<String, SimpleDateFormat> FORMAT_MAP = getFormatMap(DatetimeFormat.values());
	
	/** 锁对象 */
    private static final Object lockObj = new Object();

    /** 存放不同的日期模板格式的sdf的Map */
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
     * 
     * @param pattern
     * @return
     */
    public static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);

        // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
        if (tl == null) {
            synchronized (lockObj) {
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
                    LogUtil.i("put new sdf of pattern " + pattern + " to map");

                    // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
//                            LogUtil.i("thread: " + Thread.currentThread() + " init pattern: " + pattern);
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return tl.get();
    }

    /**
     * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    /**
     * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }
    
	
	/**
	 * 获取当前日期时间的时区的时间毫秒数与实际时间的毫秒数相隔的毫秒数
	 * createdDatetime 2014年8月23日 下午7:47:23
	 * @return long
	 */
	public static long getTimeZoneMillis()
	{
		return TimeZone.getDefault().getOffset(System.currentTimeMillis());
	}
	
//	/**
//	 * 获取所有format组成的Map
//	 * createdDatetime 2014年8月25日 上午11:14:44
//	 * @param values
//	 * @return Map
//	 */
//	private static Map<String, SimpleDateFormat> getFormatMap(
//			DatetimeFormat[] values) {
//		Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
//		
//		for(DatetimeFormat format : values)
//		{
//			String formatValue = format.getFormat();
//			formatMap.put(formatValue, new SimpleDateFormat(formatValue));
//		}
//		
//		return formatMap;
//	}
	
	/**
	 * 获取SimpleDateFormat by format
	 * createdDatetime 2014年8月25日 上午11:25:57
	 * @param format  日期格式
	 * @return SimpleDateFormat
	 */
	private static SimpleDateFormat getFormat(String format)
	{
		return getSdf(format);
	}

	/**
	 * 类似于System.currentTimeMillis获取当前日期的毫秒数
	 * createdDatetime 2014年8月23日 下午7:47:31
	 * @return long
	 */
	public static long currentDateMillis()
	{
		// todayDatetime
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		// today date year month day
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
	
	/**
	 * 日期时间形式的字符串转化为时间毫秒数（若仅有时间，则日期为今天）
	 * createdDatetime 2014年8月23日 下午3:48:48
	 * @param dateTimeString
	 * @param format 
	 * @return long
	 */
	public static long dateTimeStringToLong(String dateTimeString,String format)
	{
		try {
			long result = getFormat(format).parse(dateTimeString).getTime();
			if(isTimeString(dateTimeString,result))
			{
				result = result + currentDateMillis() + TIME_ZONE_MILLIS;
			}
			return result;
		} catch (Exception e) {
			throw new DatetimeException("dateTimeString illegal,dateTimeString="+dateTimeString+","+e.getMessage(),e);
		}
	}

	private static boolean isTimeString(String dateTimeString, long result) {
		return !dateTimeString.contains("-")&&result<FIRST_DAY_MILLIS;
	}

	/**
	 * 线程安全
	 * 日期时间形式的字符串转化为时间毫秒数（若仅有时间，则日期为今天）
	 * createdDatetime 2014年8月23日 下午4:54:32
	 * @param dateTimeString
	 * @param format
	 * @return long
	 */
	public static synchronized long dateTimeStringToLong(String dateTimeString,DatetimeFormat format)
	{
		try {
			long result;
			if(DatetimeFormat.INTERNAL_DATE_TIME_MILLIS_ZONE_COLON_SEPARATOR_FORMAT.equals(format))
			{
				int indexOfAdd = dateTimeString.indexOf("+");
				String suffix = dateTimeString.substring(indexOfAdd+"+".length());
				result = getFormat(DatetimeFormat.INTERNAL_DATE_TIME_MILLIS_ZONE_FORMAT.getFormat()).parse(dateTimeString.substring(0,indexOfAdd)+"+"+suffix.replace(":", "")).getTime();
			}
			else if(DatetimeFormat.INTERNAL_DATE_TIME_ZONE_COLON_SEPARATOR_FORMAT.equals(format))
			{
				int indexOfAdd = dateTimeString.indexOf("+");
				String suffix = dateTimeString.substring(indexOfAdd+"+".length());
				result = getFormat(DatetimeFormat.INTERNAL_DATE_TIME_ZONE_FORMAT.getFormat()).parse(dateTimeString.substring(0,indexOfAdd)+"+"+suffix.replace(":", "")).getTime();
			}
			else
			{
				result = getFormat(format.getFormat()).parse(dateTimeString).getTime();
			}
			if(isTimeString(dateTimeString,result))
			{
				result = result + currentDateMillis() + TIME_ZONE_MILLIS;
			}
			return result;
		} catch (Exception e) {
			throw new DatetimeException("dateTimeString illegal,dateTimeString="+dateTimeString+",format="+format.getFormat()+","+e.getMessage(),e);
		}
	}
	
	/**
	 * 时间毫秒数转化为标准时间没有秒数字符串
	 * createdDatetime 2014年8月23日 下午3:51:39
	 * @param minseconds
	 * @param format
	 * @return String
	 */
	public static String longToDateTimeString(long minseconds,DatetimeFormat format)
	{
		Date date = new Date(minseconds);
		if(DatetimeFormat.INTERNAL_DATE_TIME_MILLIS_ZONE_COLON_SEPARATOR_FORMAT.equals(format))
		{
			String s = getFormat(DatetimeFormat.INTERNAL_DATE_TIME_MILLIS_ZONE_FORMAT.getFormat()).format(date);
			int indexOfAdd = s.indexOf("+");
			String suffix = s.substring(indexOfAdd+"+".length());
			return s.substring(0, indexOfAdd)+"+"+suffix.substring(0,2)+":"+suffix.substring(2);
		}
		return getFormat(format.getFormat()).format(date);
	}
	
	/**
	 * 时间毫秒数转化为标准时间没有秒数字符串
	 * createdDatetime 2014年8月23日 下午3:51:39
	 * @param minseconds
	 * @param format
	 * @return String
	 */
	public static String longToDateTimeString(long minseconds,String format)
	{
		Date date = new Date(minseconds);
		return getFormat(format).format(date);
	}
	
	/**
	 * 线程安全
	 * 一种时间格式字符串转化为另一种时间格式字符串
	 * createdDatetime 2014年8月25日 上午11:42:56
	 * @param source
	 * @param sourceFormat
	 * @param targetFormat
	 * @return String
	 */
	public static synchronized String stringToString(String source, DatetimeFormat sourceFormat, DatetimeFormat targetFormat)
	{
		try {
			Date date;
			if(DatetimeFormat.INTERNAL_DATE_TIME_MILLIS_ZONE_COLON_SEPARATOR_FORMAT.equals(sourceFormat))
			{
				int indexOfAdd = source.indexOf("+");
				String suffix = source.substring(indexOfAdd+"+".length());
				date = getFormat(DatetimeFormat.INTERNAL_DATE_TIME_MILLIS_ZONE_FORMAT.getFormat()).parse(source.substring(0,indexOfAdd)+"+"+suffix.replace(":", ""));
			}
			else if(DatetimeFormat.INTERNAL_DATE_TIME_ZONE_COLON_SEPARATOR_FORMAT.equals(sourceFormat))
			{
				int indexOfAdd = source.indexOf("+");
				String suffix = source.substring(indexOfAdd+"+".length());
				date = getFormat(DatetimeFormat.INTERNAL_DATE_TIME_ZONE_FORMAT.getFormat()).parse(source.substring(0,indexOfAdd)+"+"+suffix.replace(":", ""));
			}
			else
			{
				date = getFormat(sourceFormat.getFormat()).parse(source);
			}
			if(DatetimeFormat.INTERNAL_DATE_TIME_MILLIS_ZONE_COLON_SEPARATOR_FORMAT.equals(targetFormat))
			{
				String s = getFormat(DatetimeFormat.INTERNAL_DATE_TIME_MILLIS_ZONE_FORMAT.getFormat()).format(date);
				int indexOfAdd = s.indexOf("+");
				String suffix = s.substring(indexOfAdd+"+".length());
				return s.substring(0, indexOfAdd)+"+"+suffix.substring(0,2)+":"+suffix.substring(2);
			}
			else if(DatetimeFormat.INTERNAL_DATE_TIME_ZONE_COLON_SEPARATOR_FORMAT.equals(targetFormat))
			{
				String s = getFormat(DatetimeFormat.INTERNAL_DATE_TIME_ZONE_FORMAT.getFormat()).format(date);
				int indexOfAdd = s.indexOf("+");
				String suffix = s.substring(indexOfAdd+"+".length());
				return s.substring(0, indexOfAdd)+"+"+suffix.substring(0,2)+":"+suffix.substring(2);
			}
			return getFormat(targetFormat.getFormat()).format(date);
		} catch (Exception e) {
			throw new DatetimeException("source illegal,source="+source+",sourceFormat="+sourceFormat.getFormat()+",targetFormat="+targetFormat.getFormat()+","+e.getMessage(),e);
		}
	}
	
	/**
	 * 一种时间格式字符串转化为另一种时间格式字符串
	 * createdDatetime 2014年8月25日 上午11:43:47
	 * @param source
	 * @param sourceFormat
	 * @param targetFormat
	 * @return String
	 */
	public static String stringToString(String source, String sourceFormat, String targetFormat)
	{
		try {
			return getFormat(targetFormat).format(getFormat(sourceFormat).parse(source));
		} catch (Exception e) {
			throw new DatetimeException("source illegal,source="+source+","+e.getMessage(),e);
		}
	}
		
	/**
	 * 一种时间格式字符串转化为标准时间格式化字符串
	 * createdDatetime 2014年8月25日 上午11:45:55
	 * @param source		日期字符串
	 * @param sourceFormat	日期格式
	 * @return String
	 */
	public static String stringToStandaredString(String source, String sourceFormat)
	{
		return stringToString(source, sourceFormat, DatetimeFormat.STANDARED_DATE_TIME_FORMAT.getFormat());
	}
	
	/**
	 * 一种时间格式字符串转化为标准时间格式化字符串
	 * createdDatetime 2014年8月25日 上午11:46:26
	 * @param source		日期字符串
	 * @param sourceFormat	日期格式
	 * @return String
	 */
	public static String stringToStandaredString(String source, DatetimeFormat sourceFormat)
	{
		return stringToString(source, sourceFormat, DatetimeFormat.STANDARED_DATE_TIME_FORMAT);
	}
	
	/**
	 * 标准时间格式化字符串转化为一种时间格式字符串
	 * createdDatetime 2014年8月26日 上午10:29:05
	 * @param source		日期字符串
	 * @param targetFormat	日期格式
	 * @return String
	 */
	public static String standaredStringToString(String source, String targetFormat)
	{
		return stringToString(source,DatetimeFormat.STANDARED_DATE_TIME_FORMAT.getFormat(), targetFormat);
	}
	
	/**
	 * 标准时间格式化字符串转化为一种时间格式字符串
	 * createdDatetime 2014年8月26日 上午10:30:25
	 * @param source		日期字符串
	 * @param targetFormat	日期格式
	 * @return String
	 */
	public static String standaredStringToString(String source, DatetimeFormat targetFormat)
	{
		return stringToString(source, DatetimeFormat.STANDARED_DATE_TIME_FORMAT, targetFormat);
	}


	/**
	 * 获取指定日期的毫秒数
	 * createdDatetime 2014年9月17日 下午10:47:31
	 * @param date	日期
	 * @return long
	 */
	public static long currentDateMillis(Date date)
	{
		SimpleDateFormat format = getFormat(DatetimeFormat.STANDARED_DATE_FORMAT.getFormat());
		try {
			return format.parse(format.format(date)).getTime();
		} catch (ParseException e) {
			throw new DatetimeException("currentDateMillis exception occured,"+e.getMessage(),e);
		}
	}

	/**
	 * 获取指定日期的毫秒数
	 * createdDatetime 2014年9月17日 下午10:47:31
	 * @param date 日期
	 * @return long
	 */
	public static long currentDateMillis(String date)
	{
		SimpleDateFormat format = getFormat(DatetimeFormat.STANDARED_DATE_FORMAT.getFormat());
		try {
			return format.parse(date).getTime();
		} catch (ParseException e) {
			throw new DatetimeException("currentDateMillis exception occured,"+e.getMessage(),e);
		}
	}

	/**
	 * 获取指定日期的秒数
	 * createdDatetime 2014年9月1日 下午7:47:31
	 * @param date	日期
	 * @return Integer
	 */
	public static Integer currentDateSecs(Date date)
	{
		try {
			return Integer.parseInt(currentDateMillis(date)/1000+"");
		} catch (NumberFormatException e) {
			throw new DatetimeException("currentDateSecs exception occured,"+e.getMessage(),e);
		}
	}

	/**
	 * 获取当前日期时间的秒数
	 * createdDatetime 2014年9月1日 下午7:47:31
	 * @return Integer
	 */
	public static Integer currentDateSecs()
	{
		try {
			return Integer.parseInt(System.currentTimeMillis()/1000+"");
		} catch (NumberFormatException e) {
			throw new DatetimeException("currentDateSecs exception occured,"+e.getMessage(),e);
		}
	}
	
    /**
     * 获取指定增加或减少月数的指定日的日期
	 * @param date 指定日期
	 * @param month 增加或减少的月数（负数为减少，0为当月）
	 * @param day 指定日（不能为0或负数）
	 * @return Date
	 */
	public static Date getFirstday_LastMonth(Date date,int month,int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(month!=0){
        	calendar.add(Calendar.MONTH, month);
        }
        if(day>0){
        	calendar.set(Calendar.DAY_OF_MONTH, day);
        }
        Date theDate = calendar.getTime();
        return theDate;
    }

	/**获取今天的日期号
	 * @return Integer 
	 */
	public static Integer getDay(){
		GregorianCalendar gc = new GregorianCalendar();
		return gc.get(Calendar.DATE);
	}
	
	/**
	 * date字符串转换为 date类型
	 * @param dateString
	 * @return
	 */
	public static Date stringToDate(String dateString){
		SimpleDateFormat sdf = null;
		if(dateString.length() == 19){
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}else if(dateString.length() == 16){
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}else if(dateString.length() == 13){
			sdf = new SimpleDateFormat("yyyy-MM-dd HH");
		}else if(dateString.length() == 10){
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}else if(dateString.length() == 7){
			sdf = new SimpleDateFormat("yyyy-MM");
		}
		
		try {
			return sdf.parse(dateString);
		} catch (ParseException e) {
			throw new DatetimeException("currentDateSecs exception occured,"+e.getMessage(),e);
		}
	}
	
	/**
	 * 将当前日期转换为 指定类型的字符串
	 * @param targetFormat
	 * @return
	 */
	public static String getTodayToString(DatetimeFormat targetFormat){
		SimpleDateFormat format = getFormat(targetFormat.getFormat());
		return format.format(new Date());
	}
	
	/**
	 * 获取当前年份
	 * @return
	 */
	public static int getYear(){
		GregorianCalendar gc = new GregorianCalendar();
		int retCode = gc.get(Calendar.YEAR);
		return retCode;
	}

	/**
	 * 获取当前月份
	 * @return
	 */
	public static int getMonth(){
		GregorianCalendar gc = new GregorianCalendar();
		int retCode = gc.get(Calendar.MONTH) + 1;
		return retCode;
	}

	/**
	 * 获取当前月份
	 * @return
	 */
	public static String getMonthForString(){
		String retCode = Integer.toString(getMonth());
		if (retCode.length() == 1){
			retCode = "0" + retCode;
		}
		return retCode;
	}

	/**
	 * 获取当前小时
	 * @return
	 */
	public static int getHour(){
		GregorianCalendar gc = new GregorianCalendar();
		return gc.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取当前分钟+秒
	 * @return 0101
	 */
	public static String getMinAndSec(){
		GregorianCalendar gc = new GregorianCalendar();
		String min = Integer.toString(gc.get(Calendar.MINUTE));
		String sec = Integer.toString(gc.get(Calendar.SECOND));
		if (min.length() == 1){
			min = "0" + min;
		}
		if (sec.length() == 1){
			sec = "0" + sec;
		}
		return min+sec;
	}
	
	public static String getDateToString(Date date,String targetFormat){
		SimpleDateFormat sdf = new SimpleDateFormat(targetFormat);;
		
			return sdf.format(date);
		
	}
	
}
