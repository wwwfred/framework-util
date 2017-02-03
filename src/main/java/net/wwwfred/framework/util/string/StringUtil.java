package net.wwwfred.framework.util.string;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.wwwfred.framework.util.code.CodeUtil;
import net.wwwfred.framework.util.date.DatetimeFormat;
import net.wwwfred.framework.util.date.DatetimeUtil;
import net.wwwfred.framework.util.log.LogUtil;
import net.wwwfred.framework.util.math.MathUtil;

/**
 * 字符串处理工具类
 * @author wangwenwu
 *2014年8月26日 下午1:10:20
 */
public class StringUtil
{	
    public static String BOOLEAN_TRUE_VALUE = "true";
    public static String BOOLEAN_FALSE_VALUE = "false";
    public static String BOOLEAN_TRUE_INT_VALUE = "1";
    public static String BOOLEAN_FALSE_INT_VALUE = "0";
    
    public static String UNICODE_STRING_START_TAG = "\\u";
    public static Integer UNICODE_STRING_LENGTH = 4;
    public static Integer UNICODE_VIEW_MIN_VALUE = 0;
    public static Integer UNICODE_VIEW_MAX_VALUE = 127;
    
    private static DatetimeFormat STRING_DATE_FORMAT = DatetimeFormat.STANDARED_DATE_TIME_FORMAT;
    
	/**
	 * 从字符串中获取期望类型的实际值；期望类型包括Character,Boolean,Byte,Short,Integer,Long,Float,Double；默认返回String类型的值；若value为null，返回null
	 * createdDatetime 2014年8月26日 下午1:10:20
	 * @param value	字符串
	 * @param expectedClazz	期望的类型
	 * @return Object 返回Object的对象
	 */
	public static Object convertStringValue(String value, Class<?> expectedClazz)
	{
		Object result;
		try
		{
			result = getValueFromString(value, expectedClazz);
		}
		catch(Exception e)
		{
			LogUtil.e(StringUtil.class.getSimpleName(), "convertStringValue illegal,s="+value+",expectedClazz="+expectedClazz+","+e.getMessage(), e);
			result = value;
		}
		return result;
	}

	/**
	 * createdDatetime 2014年8月26日 下午1:22:23
	 * @param value
	 * @param expectedClazz
	 * @return boolean
	 */
	private static boolean isNumber(String value, Class<?> expectedClazz) {
		if((Number.class.isAssignableFrom(expectedClazz)||byte.class.isAssignableFrom(expectedClazz)
				||short.class.isAssignableFrom(expectedClazz)||int.class.isAssignableFrom(expectedClazz)
				||long.class.isAssignableFrom(expectedClazz)||float.class.isAssignableFrom(expectedClazz)
				||double.class.isAssignableFrom(expectedClazz))
				&&(MathUtil.isNumber(value)))
			return true;
		return false;
	}

	/**
	 * createdDatetime 2014年8月26日 下午1:19:42
	 * @param value
	 * @param expectedClazz
	 * @return
	 */
	private static boolean isBoolean(String value, Class<?> expectedClazz) {
//		if((boolean.class.isAssignableFrom(expectedClazz)||Boolean.class.isAssignableFrom(expectedClazz))
//				&&("true".equalsIgnoreCase(value)||"false".equalsIgnoreCase(value)))
//			return true;
//		return false;
	    
	    if(value==null||expectedClazz==null)
	        return false;
	    boolean clazzResult = boolean.class.isAssignableFrom(expectedClazz)||Boolean.class.isAssignableFrom(expectedClazz);
	    boolean valueResult = BOOLEAN_TRUE_VALUE.equalsIgnoreCase(value)||BOOLEAN_FALSE_VALUE.equalsIgnoreCase(value)
	            ||BOOLEAN_TRUE_INT_VALUE.equalsIgnoreCase(value)||BOOLEAN_FALSE_INT_VALUE.equalsIgnoreCase(value);
	    return clazzResult&&valueResult;
	}
	
	/** 获取Boolean类型的值 */
    private static Boolean getBoolean(String value) {
        if(BOOLEAN_TRUE_VALUE.equalsIgnoreCase(value)||BOOLEAN_TRUE_INT_VALUE.equalsIgnoreCase(value))
            return true;
        else if(BOOLEAN_FALSE_VALUE.equalsIgnoreCase(value)||BOOLEAN_FALSE_INT_VALUE.equalsIgnoreCase(value))
            return false;
        return null;
    }

	/**
	 * @author wangwwy
	 * createdDatetime 2014年8月26日 下午1:16:56
	 * @param value
	 * @param expectedClazz
	 * @return
	 */
	private static boolean isCharacter(String value, Class<?> expectedClazz) {
		if((char.class.isAssignableFrom(expectedClazz)||Character.class.isAssignableFrom(expectedClazz))
				&&(value.length()==1))
			return true;
		return false;
	}

	/**
     * 将字符串转换为其它基本类型
     * String value to declared type Object
     * @param value  字符串
     * @param clazz  需转换的类型
     * @return <T> T  返回期望类型对象
     * @throws TeshehuiRuntimeException
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValueFromString(String value,Class<? extends T> clazz)
    {
        if(value==null)
            return null;
        
        T result;
        
        Object obj;
        value = value.trim();
        if(clazz==null)
        {
            throw new StringException("StringUtil.getValueFromString clazz is null.");
        }
        else if (String.class.isAssignableFrom(clazz))
        {
            obj = value;
        } else if(isCharacter(value,clazz))
        {
            obj = value.charAt(0);
        }
        else if(isBoolean(value,clazz))
        {
            obj = getBoolean(value);
//          obj = Boolean.parseBoolean(value);
        }
        else if(isNumber(value,clazz))
        {
            Class<? extends Number> numberClazz = (Class<? extends Number>) clazz;
            obj = MathUtil.getNumber(value, numberClazz);
        }
        else if(Enum.class.isAssignableFrom(clazz))
        {
            @SuppressWarnings({"rawtypes" })
            Class<? extends Enum> enumType = (Class<? extends Enum>) clazz;
            Enum<?> enumObject = Enum.valueOf(enumType, value);
            obj = enumObject;
        }
        else if(Date.class.isAssignableFrom(clazz))
        {
        	obj = new Date(DatetimeUtil.dateTimeStringToLong(value, STRING_DATE_FORMAT));
        }
        else if(Object.class.equals(clazz))
        {
            obj = value;
        }
        else if(!clazz.isPrimitive()&&"".equals(value.trim()))
        {
            obj = null;
        }
        else
        {
            throw new StringException("StringUtil.getValueFromString param clazz not support,clazz="+clazz+",value="+value);
        }
        
//      if(obj==null)
//      {
//          LogUtil.w(StringUtil.class.getSimpleName(), "getValueFromString result null,s="+value+",expectedClazz="+clazz, null);
//      }

        result = (T)obj;
        return result;
    }

    /**
	 * 将字符串转换为其它基本类型
	 * String value to declared type Object
	 * @param value  字符串
	 * @param clazz  需转换的类型
	 * @return <T> T  返回期望类型对象
	 * @throws TeshehuiRuntimeException
	 */
	public static <T> T getValueFromString(String value,Class<? extends T> clazz,T defaultValue)
	{
	    T result;
	    try
	    {
	        result = getValueFromString(value, clazz);
	    }
	    catch(Exception e)
	    {
	        LogUtil.e(StringUtil.class.getSimpleName(), "getValueFromString illegal,s="+value+",expectedClazz="+clazz, e);
	        result = null;
	    }
	    if(result == null)
	    {
	        result = defaultValue;
	    }
	    else if(String.class.isAssignableFrom(clazz)&&CodeUtil.isEmpty(result))
	    {
	        result = defaultValue;
	    }
	    return result;
	}
	
	/** 对象toString */
	public static String toString(Object obj)
	{
	    if(obj==null)
	        return null;
	    return obj.toString();
	}
	
	/** 将List集合对象转换为指定分隔符的字符串 */
	public static String listToString(List<?> list, String separatorTag)
	{
	    if(list!=null)
	    {
	        StringBuffer sb = new StringBuffer();
	        int length = list.size();
	        if(length>0)
	        {
	            sb.append(list.get(0));
	            for (int i = 1; i < length; i++) {
                    sb.append(separatorTag).append(list.get(i));
                }
	        }
	        return sb.toString();
	    }
	    return null;
	}
	
	/**
	 * 字符串转换成 List 集合
	 * @since 2014-03-14 13:53
	 * @version 2014-07-30
	 * for example: s = "data,userList,role,roleName"
	 * @param s   字符串
	 * @param separatorTag=',' 分隔符号
	 * @return List<String> 返回List集合
	 */
	public static List<String> stringToList(String s,String separatorTag)
	{
		List<String> result = new LinkedList<String>();

		if (s != null && separatorTag != null && !s.isEmpty() && !separatorTag.isEmpty()) {

//			 String startTag = "[";
//			 String endTag = "]";
			// if(!s.startsWith(startTag)||!s.endsWith(endTag))
			// throw new
			// TeshehuiRuntimeException("string to list string should starts with '" +
			// startTag + "' and end with '" + endTag + "'.");
			// s = s.substring(startTag.length()).trim();
			
			s = s.trim();
//			if(s.startsWith(startTag))
//			{
//				s = s.substring(startTag.length());
//			}
//			if(s.endsWith(endTag))
//			{
//				s = s.substring(0,s.length()-endTag.length());
//			}

			while (true) {
				s = s.trim();
				int indexOf = s.indexOf(separatorTag);
				if (indexOf != -1) {
					String value = s.substring(0, indexOf).trim();
					result.add(value);
					s = s.substring(indexOf + separatorTag.length());
				} else {
					result.add(s);
					break;
				}
			}

		}
		return result;
	}
	
	/** 将Map集合转换为指定分隔符的字符串 */
	public static String mapToString(Map<?, ?> map, String separatorTag, String keyValueSeparatorTag)
	{
	    if(map!=null)
	    {
	        StringBuffer sb = new StringBuffer();
	        int count = 0;
	        Set<?> keySet = map.keySet();
	        for (Object key : keySet) {
                Object value = map.get(key);
                if(count==0)
                {
                    sb.append(key).append(keyValueSeparatorTag).append(value);
                }
                else
                {
                    sb.append(separatorTag).append(key).append(keyValueSeparatorTag).append(value);
                }
                count++;
            }
	        
	    }
	    return null;
	}
	
	/**
	 * 字符串转换为 Map 集合
	 * @since 2014-03-14 14:25
	 * for example: s="k1=abc;k2=12;k3=9,5,2,7;k4=ade,bba,ccf"
	 * @param s	字符串
	 * @param separatorTag=';' 分隔符号
	 * @param keyValueSeparatorTag='=' key和value的连接符
	 * @return Map<String, String> 返回Map集合
	 */
	public static Map<String, String> stringToMap(String s,String separatorTag, String keyValueSeparatorTag)
	{
		Map<String, String> result = new LinkedHashMap<String, String>();

		if (s != null && separatorTag != null && keyValueSeparatorTag != null) {

			// String startTag = "{";
			// String endTag = "}";
			// if(!s.startsWith(startTag)||!s.endsWith(endTag))
			// throw new
			// TeshehuiRuntimeException("string to map string should starts with '" +
			// startTag + "' and end with '" + endTag + "'.");

			while (true) {
				s = s.trim();
				int indexOf = s.indexOf(separatorTag);
				if (indexOf != -1) {
					String value = s.substring(0, indexOf).trim();
					int indexOfKeyValueSeparatorTag = value
							.indexOf(keyValueSeparatorTag);
					if (indexOfKeyValueSeparatorTag != -1) {
						String key = value.substring(0,
								indexOfKeyValueSeparatorTag);
						if (!"".equals(key)) {
							result.put(
									key,
									value.substring(indexOfKeyValueSeparatorTag
											+ keyValueSeparatorTag.length()));
						}
					}

					s = s.substring(indexOf + separatorTag.length());
				} else {
					int indexOfKeyValueSeparatorTag = s
							.indexOf(keyValueSeparatorTag);
					if (indexOfKeyValueSeparatorTag != -1) {
						String key = s
								.substring(0, indexOfKeyValueSeparatorTag);
						if (!"".equals(key)) {
							result.put(
									key,
									s.substring(indexOfKeyValueSeparatorTag
											+ keyValueSeparatorTag.length()));
						}
					}
					break;
				}
			}

		}
		return result;
	}
	
	/**
     * 判断目标字符串中是否存在特定的字符串（引号",斜杠/"），并检测这些特定的字符串前面是否有特殊标记字符（转义字符\），若没有则加上特殊标记字符
     * @since 2014-03-14 15:51
     * for example: s="http://abc"efg"eaa" to \"http:\/\/abc\"efg\"eaa\"
     * @param originalString
     * @return String
     */
    public static String convertStringSpecialTag(String originalString,String specialTag, String flagTag)
    {
        String result = null;
        
        int indexOf = originalString.indexOf(specialTag);
        if(indexOf==-1)
        {
            result = originalString;
        }
        else if(indexOf==0)
        {
            result = flagTag + originalString.substring(0,1) + convertStringSpecialTag(originalString.substring(1),specialTag,flagTag);
        }
        else
        {
            String subString1 = originalString.substring(0,indexOf);
            String subString2 = originalString.substring(indexOf+specialTag.length());
            
            if(!subString1.endsWith(flagTag))
            {
                subString1 = subString1 + flagTag + specialTag;
            }
            else
            {
                subString1 = subString1 + specialTag;
            }
            subString2 = convertStringSpecialTag(subString2,specialTag,flagTag);
            result = subString1 + subString2;
        }
        return result;
    }
	
	/**
	 * 转义字符串中引号，在引号前加上转义字符"\",若引号前有转义字符则不加转义字符
	 * @since 2014-03-14 15:51
	 * for example: s="abc"efg"eaa" to \"abc\"efg\"eaa\"
	 * @param originalString
	 * @return String
	 */
	public static String convertStringQuoteTag(String originalString)
	{
		String result = null;
		
		char quoteEsc = '\\';
		String quoteTag = "\"";
		
		int indexOf = originalString.indexOf(quoteTag);
		if(indexOf==-1)
		{
			result = originalString;
		}
		else if(indexOf==0)
		{
			result = quoteEsc + originalString.substring(0,1) + convertStringQuoteTag(originalString.substring(1));
		}
		else
		{
			String subString1 = originalString.substring(0,indexOf);
			String subString2 = originalString.substring(indexOf+quoteTag.length());
			
			char previousChar = originalString.charAt(indexOf-1);
			if(previousChar!=quoteEsc)
			{
				subString1 = subString1 + quoteEsc + quoteTag;
			}
			else
			{
				subString1 = subString1 + quoteTag;
			}
			subString2 = convertStringQuoteTag(subString2);
			result = subString1 + subString2;
		}
		return result;
	}
	
	/**
	 * 转换字符串中引号前的转义字符
	 * for example: s=\"abc\"efg\"eaa\" to "abc"efg"eaa"
	 * 2014年9月24日  下午7:51:41
	 * @param originalString
	 * @return String
	 */
	public static String decodeStringQuoteTag(String originalString)
	{
		String s = originalString.trim();
		while(s.contains("\\\""))
		{
			s = s.replace("\\\"", "\"");
		}
		return s;
	}
	
	/**
	 * 旧字符串 替换为 新字符串
	 * @param str			整个字符串
	 * @param oldString		旧字符串
	 * @param newString		新字符串
	 * @return String	返回替换后的字符串
	 */
	public static String replaceString(String str,String oldString,String newString)
	{
	    if(str==null)
	        return null;
	    else if(oldString==null||newString==null)
	        return str;
	    
	    long circleTimeout = 3*1000;
	    
	    long startTime = System.currentTimeMillis();
		boolean flag = true;
		do{
		    if(System.currentTimeMillis()-startTime>circleTimeout)
		    {
		        LogUtil.w("StringUtil.replaceString", "oldString="+oldString+",newString="+newString + ",circle timeout="+circleTimeout+",str="+str, null);
		        flag = false;
		    }
			if(str.indexOf(oldString)>=0){
				str = str.replace(oldString, newString);
			}else{
				flag = false;
			}
		}while(flag);
		return str;
	}
	
	/**
	 * 判断字符串是否为空
	 * @author zhangqing
	 * 2014年11月26日  上午10:03:47
	 */
	public static boolean isEmpty(String arg){
		if(arg==null||"".equals(arg)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 字符串转换unicode，智能识别，当string中包含unicode时不转换
	 */
	public static String string2Unicode(String string) {
		
		// 判断空
		if(string==null)
			return null;
		
		// 判断是否有unicode
		if(!string.contains(UNICODE_STRING_START_TAG))
		{
			return string2UnicodeF(string);
		}
		
		StringBuffer unicode = new StringBuffer();
		// 1. 获得\\u开始的字符串数组String[] array,array中每一个都是只有一个\\u
	    List<String> hex = stringSeparator(string, UNICODE_STRING_START_TAG);
	    int len = UNICODE_STRING_START_TAG.length()+UNICODE_STRING_LENGTH;
	    for (String one : hex) {
	    	
	    	// 2. 判断是否是unicode
	    	if(!one.contains(UNICODE_STRING_START_TAG))
	    	{
	    		one = string2UnicodeF(one);
	    		unicode.append(one);
	    	}
	    	else
	    	{
	    		// 2. 转换出每一个,每一个中不转换前面的4位unicode
		    	if(one.length()>len)
		    	{
		    		one = one.substring(0,len) + string2UnicodeF(one.substring(len));
		    	}
		    	unicode.append(one);	
	    	}
	    	
	    	
		}
		
	    return unicode.toString();
	}
	
	/**
	 * 字符串转换unicode，强制将字符串的每一个字符转换为unicode
	 */
	private static String string2UnicodeF(String string) {

		// 判断空
		if(string==null)
			return null;
		
	    StringBuffer unicode = new StringBuffer();
	 
	    for (int i = 0; i < string.length(); i++) {
	 
	        // 取出每一个字符
	        char c = string.charAt(i);
	        
	        String hex = Integer.toHexString(c);
	        int length = hex.length();
	        if(length==1)
	        {
	        	hex = "000"+hex;
	        }
	        else if(length==2)
	        {
	        	hex = "00"+hex;
	        }
	        else if(length==3)
	        {
	        	hex = "0"+hex;
	        }
	 
	        // 转换为unicode
	        unicode.append("\\u" + hex);
	    }
	 
	    return unicode.toString();
	}
	
	/**
	 * unicode 转字符串，智能识别
	 * 
	 */
	public static String unicode2AscaiiString(String unicode) {
	 
		// 判断空
		if(unicode==null)
		{
			return null;
		}
	 
		// to unicode
		unicode = string2Unicode(unicode);
		
		// 1. 获得\\u开始的字符串数组String[] array,array中每一个都是只有一个\\u
//	    String[] hex = unicode.split("\\\\u");
	    List<String> hex = stringSeparator(unicode, UNICODE_STRING_START_TAG);
		
	    StringBuffer string = new StringBuffer();
	    int len = UNICODE_STRING_START_TAG.length()+UNICODE_STRING_LENGTH;
	    for (String one : hex) {
	    	String oneHex;
	    	if(one.length()>len)
	    	{
	    		oneHex = one.substring(UNICODE_STRING_START_TAG.length(),UNICODE_STRING_START_TAG.length()+UNICODE_STRING_LENGTH);
	    	}
	    	else
	    	{
	    		oneHex = one.substring(UNICODE_STRING_START_TAG.length());
	    	}
	    	string.append(getViewUnicode(oneHex));
 	    }
		
		return string.toString();
	}
	
	/**
	 * unicode 转字符串，若为空返回空，若不是unicode直接原样返回
	 * 1. 获得\\u开始的字符串数组String[] array,array中每一个都是只有一个\\u
	 * 2. 转换出每一个代码点
	 * 3. 每一个中最多转换前面的4位unicode
	 */
	public static String unicode2String(String unicode) {
		 
		// 判断空
		if(unicode==null)
		{
			return null;
		}
	 
		// 判断是否有unicode
		if(!unicode.contains(UNICODE_STRING_START_TAG))
		{
			return unicode;
		}
		
		// 1. 获得\\u开始的字符串数组String[] array,array中每一个都是只有一个\\u
//	    String[] hex = unicode.split("\\\\u");
	    List<String> hex = stringSeparator(unicode, UNICODE_STRING_START_TAG);
	    
	    StringBuffer string = new StringBuffer();
	    int len = UNICODE_STRING_START_TAG.length()+UNICODE_STRING_LENGTH;
	    for (String one : hex) {
 	    	
	    	// 2. 判断是否是unicode
	    	if(!one.contains(UNICODE_STRING_START_TAG))
	    	{
	    		string.append(one);
	    	}
	    	else
	    	{
	    		// 3. 每一个中最多转换前面的4位unicode
	 	    	String oneString;
	 	    	if(one.length()>len)
	 	    	{
	 	    		oneString = ((char)Integer.parseInt(one.substring(UNICODE_STRING_START_TAG.length(),len), 16)) + one.substring(len);
	 	    	}
	 	    	else
	 	    	{
	 	    		oneString = ((char)Integer.parseInt(one.substring(UNICODE_STRING_START_TAG.length()), 16)) + "";
	 	    	}
	 	 
	 	        // 追加成string
	 	        string.append(oneString);
	    	}
 	    }
	 
	    return string.toString();
	}
	
	private static String getViewUnicode(String unicode)
	{
		int number = Integer.parseInt(unicode,16);
		return (number>=UNICODE_VIEW_MIN_VALUE&&number<=UNICODE_VIEW_MAX_VALUE)?((char)number+""):(UNICODE_STRING_START_TAG+unicode);
	}
	
	private static List<String> stringSeparator(String s, String separatorTag)
	{
		List<String> list = new ArrayList<String>();
		
		if(s==null)
			return list;
		
		String tempS = s;
		while(true)
		{
			// 搜索标记符最新出现的位置
			int indexOf = tempS.indexOf(separatorTag);
			if(indexOf==0)
			{
				int indexOf2 = tempS.indexOf(separatorTag, indexOf+separatorTag.length());
				if(indexOf2>=0)
				{
					String prefix = tempS.substring(0,indexOf2);
					list.add(prefix);
					
					tempS = tempS.substring(indexOf2);
				}
				else
				{
					list.add(tempS);
					break;
				}
			}
			else if(indexOf>0)
			{
				String prefix = tempS.substring(0,indexOf);
				list.add(prefix);
				
				tempS = tempS.substring(indexOf);
			}
			else
			{
				list.add(tempS);
				break;
			}
		}
		
		return list;
	}
	
}
