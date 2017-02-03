package net.wwwfred.framework.util.charset;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 汉字简繁体转换类
 * 
 * @author stuxuhai (dczxxuhai@gmail.com)
 * @version 1.0
 */
public class ChineseUtil {
	private static final Properties CHINESE_TABLE = PinyinResource
			.getChineseTable();

	/**
	 * 将单个繁体字转换为简体字
	 * 
	 * @param c
	 *            需要转换的繁体字
	 * @return 转换后的简体字
	 */
	public static char convertToSimplifiedChinese(char c) {
		if (isTraditionalChinese(c)) {
			return CHINESE_TABLE.getProperty(String.valueOf(c)).charAt(0);
		}
		return c;
	}

	/**
	 * 将单个简体字转换为繁体字
	 * 
	 * @param c
	 *            需要转换的简体字
	 * @return 转换后的繁字体
	 */
	public static char convertToTraditionalChinese(char c) {
		String hanzi = String.valueOf(c);
		if (CHINESE_TABLE.containsValue(hanzi)) {
			Iterator<Entry<Object, Object>> itr = CHINESE_TABLE.entrySet()
					.iterator();
			while (itr.hasNext()) {
				Entry<Object, Object> e = itr.next();
				if (e.getValue().toString().equals(hanzi)) {
					return e.getKey().toString().charAt(0);
				}
			}
		}
		return c;
	}

	/**
	 * 将繁体字转换为简体字
	 * 
	 * @param str
	 *            需要转换的繁体字
	 * @return 转换后的简体体
	 */
	public static String convertToSimplifiedChinese(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			sb.append(convertToSimplifiedChinese(c));
		}
		return sb.toString();
	}

	/**
	 * 将简体字转换为繁体字
	 * 
	 * @param str
	 *            需要转换的简体字
	 * @return 转换后的繁字体
	 */
	public static String convertToTraditionalChinese(String str) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = str.length(); i < len; i++) {
			char c = str.charAt(i);
			sb.append(convertToTraditionalChinese(c));
		}
		return sb.toString();
	}

	/**
	 * 判断某个字符是否为繁体字
	 * 
	 * @param c
	 *            需要判断的字符
	 * @return 是繁体字返回true，否则返回false
	 */
	public static boolean isTraditionalChinese(char c) {
		return CHINESE_TABLE.containsKey(String.valueOf(c));
	}

	/**
	 * 判断某个字符是否为汉字
	 * 
	 * @param c
	 *            需要判断的字符
	 * @return 是汉字返回true，否则返回false
	 */
	public static boolean isChinese(char c) {
		String regex = "[\\u4e00-\\u9fa5]";
		return String.valueOf(c).matches(regex);
	}
	
	/**
	 * 判断字符串是否是乱码
	 *
	 * @param strName 字符串
	 * @return 是否是乱码
	 */
	public static boolean isMessyCode(String strName) {
	   return isMessyCode(strName, null);
	}
	
	/**
	 * 判断字符串是否为乱码
	 * @author wangwenwu
	 * 2014年11月11日  上午11:25:06
	 */
	public static boolean isMessyCode(String s,Charset charset)
	{
		charset = charset==null?Charset.defaultCharset():charset;
		CharsetEncoder charsetEncoder = charset.newEncoder();
		char[] array = s.toCharArray();
		for(char c : array)
		{
			if(!charsetEncoder.canEncode(c))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 用默认的字符编码方式移除字符串中乱码字符，返回新的字符串
	 * @author wangwenwu
	 * 2014年11月11日  上午11:29:30
	 */
	public static String removeMessyCode(String s)
	{
		StringBuffer sb = new StringBuffer();
		Charset charset = Charset.defaultCharset();
		CharsetEncoder charsetEncoder = charset.newEncoder();
		char[] array = s.toCharArray();
		for(char c : array)
		{
			if(charsetEncoder.canEncode(c))
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 字符串过滤器
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',//.<\\\\>?~！@#￥%……&*中（）\\[\\]——+|{}【】§№☆★○●◎◇◆□■△▲※→←↑↓〓＃＆＠＼＾＿￣―‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
}
