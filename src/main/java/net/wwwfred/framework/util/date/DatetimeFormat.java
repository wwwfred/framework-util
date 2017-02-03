package net.wwwfred.framework.util.date;

public enum DatetimeFormat {
	
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	STANDARED_DATE_TIME_FORMAT("yyyy-MM-dd HH:mm:ss")
	/**
	 * yyyy-MM-dd HH:mm:ss.SSS
	 */
	,STANDARED_DATE_TIME_MILLIS_FORMAT("yyyy-MM-dd HH:mm:ss.SSS")
	/**
	 * yyyy-MM-dd'T'HH:mm:ss.SSSZ
	 */
	,INTERNAL_DATE_TIME_MILLIS_ZONE_FORMAT("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	/**
	 * yyyy-MM-dd'T'HH:mm:ssZ
	 */
	,INTERNAL_DATE_TIME_ZONE_FORMAT("yyyy-MM-dd'T'HH:mm:ssZ")
	/**
	 * yyyy-MM-dd'T'HH:mm:ss
	 */
	,INTERNAL_DATE_TIME_FORMAT("yyyy-MM-dd'T'HH:mm:ss")
	/**
	 * yyyy-M-d H:m:s
	 */
	,STANDARED_DATE_TIME_SINGLE_FORMAT("yyyy-M-d H:m:s")
	/**
	 * yyyy-MM-dd
	 */
	,STANDARED_DATE_FORMAT("yyyy-MM-dd")
	/**
	 * HH:mm:ss
	 */
	,STANDARED_TIME_FORMAT("HH:mm:ss")
	/**
	 * yyyy-MM
	 */
	,STANDARED_DATE_NO_DAY_FORMAT("yyyy-MM")
	/**
	 * HH:mm
	 */
	,STANDARED_TIME_NO_SECONDS_FORMAT("HH:mm")
	/**
	 * yyyy-MM-dd'T'HH:mm:ss:SSS+08:00
	 */
	,INTERNAL_DATE_TIME_MILLIS_ZONE_COLON_SEPARATOR_FORMAT("yyyy-MM-dd'T'HH:mm:ss:SSS+08:00")
	/**
	 * yyyy-MM-dd'T'HH:mm:ss+08:00
	 */
	,INTERNAL_DATE_TIME_ZONE_COLON_SEPARATOR_FORMAT("yyyy-MM-dd'T'HH:mm:ss+08:00");
	
	private DatetimeFormat(String format) {
		this.format = format;
	}
	private String format;
	public static DatetimeFormat getInstance(String format)
	{
		for(DatetimeFormat dateTimeFormat : DatetimeFormat.values())
		{
			if(dateTimeFormat.format.equals(format))
			{
				return dateTimeFormat;
			}
		}
		return null;
	}
	public String getFormat() {
		return format;
	}
}
