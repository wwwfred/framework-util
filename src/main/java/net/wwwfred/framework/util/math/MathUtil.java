package net.wwwfred.framework.util.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 
 * @author wangwenwu
 * createdDatetime 2014年8月25日 下午2:31:37
 */
public class MathUtil {

	/**
	 * 判断字符串是否为数字类型
	 * createdDatetime 2014年8月25日 下午2:31:37
	 * @param s	字符串
	 * @return boolean 返回 true 或 false
	 */
	public static boolean isNumber(String s)
	{
		try
		{
//			Double.parseDouble(s.trim());
		    new BigDecimal(s);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	/**
     * 将字符串转换为数字
     * createdDatetime 2014年8月25日 下午4:22:13
     * @param s 字符串
     * @return Number 返回 Double 类型数字
     */
    public static Number getNumber(String s)
    {
//      return Double.parseDouble(s);
        return new BigDecimal(s);
    }
	
	/**
	 * 判断字符串是否为指定类型的数字
	 * 2014年9月28日  下午10:14:40
	 * @param s	字符串
	 * @param clazz	指定的数字类型（byte，short，long，int，float，double等）
	 * @return boolean 返回 true 或 false
	 */
	public static boolean isNumber(String s, Class<?> clazz)
	{
		boolean result = MathUtil.isNumber(s);
		
		if(result&&clazz!=null)
		{
			if(byte.class.isAssignableFrom(clazz)||Byte.class.isAssignableFrom(clazz)
					||short.class.isAssignableFrom(clazz)||Short.class.isAssignableFrom(clazz)
					||int.class.isAssignableFrom(clazz)||Integer.class.isAssignableFrom(clazz)
					||long.class.isAssignableFrom(clazz)||Long.class.isAssignableFrom(clazz)
					||float.class.isAssignableFrom(clazz)||Float.class.isAssignableFrom(clazz)
					||double.class.isAssignableFrom(clazz)||Double.class.isAssignableFrom(clazz)
					||BigDecimal.class.isAssignableFrom(clazz)
					||BigInteger.class.isAssignableFrom(clazz))
			{
				result=true;
			}
			else
			{
				result = false;
			}
		}
		else
		{
			result = false;
		}
		
		return result;
	}
	
	/**
     * 获取指定类型的数字
     * createdDatetime 2014年8月25日 下午3:01:23
     * @param s 字符串
     * @param clazz 指定类型（仅限数字类型）
     * @return <T extends Number> T 返回指定类型数字
     * @throws TeshehuiRuntimeException
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T getNumber(String s, Class<? extends T> clazz)
    {
        T result;
        Number number;
        if(s==null)
        {
            return null;
        }
        
        s = s.trim();
        if(!isNumber(s))
        {
            number = null;
        }
        else
        {
            if(clazz==null)
            {
                throw new MathException("MathUtil.getNumber param clazz is null.");
            }
            else if(byte.class.isAssignableFrom(clazz)||Byte.class.isAssignableFrom(clazz))
            {
                number = new BigDecimal(s).byteValue();
            }
            else if(short.class.isAssignableFrom(clazz)||Short.class.isAssignableFrom(clazz))
            {
                number = new BigDecimal(s).shortValue();
            }
            else if(int.class.isAssignableFrom(clazz)||Integer.class.isAssignableFrom(clazz))
            {
                number = new BigDecimal(s).intValue();
            }
            else if(long.class.isAssignableFrom(clazz)||Long.class.isAssignableFrom(clazz))
            {
                number = new BigDecimal(s).longValue();
            }
            else if(float.class.isAssignableFrom(clazz)||Float.class.isAssignableFrom(clazz))
            {
                number = new BigDecimal(s).floatValue();
            }
            else if(double.class.isAssignableFrom(clazz)||Double.class.isAssignableFrom(clazz))
            {
                number = new BigDecimal(s).doubleValue();
            }
            else if(BigDecimal.class.isAssignableFrom(clazz))
            {
                number = new BigDecimal(s);
            }
            else if(BigInteger.class.isAssignableFrom(clazz))
            {
                number = new BigInteger(s);
            }
            else
            {
                throw new MathException("MathUtil.getNumber param clazz not support,s="+s+",clazz="+clazz);
            }
        }
        
        result = (T)number;
        return result;
    }

	/**
	 * 从一个Object对象中获取数字，如果对象为空，或转换过程中遇到异常，将返回默认数字
	 * 2014年9月19日  下午5:02:31
	 * @param obj	Object对象
	 * @param clazz	指定对象（仅限数字类型）
	 * @param defaultValue	默认值
	 * @return <T extends Number> T 返回指定类型数字
	 */
	public static <T extends Number> T getNumber(Object obj, Class<? extends T> clazz, T defaultValue)
	{
		T result;
		if(obj==null)
		{
			result = null;
		}
		else
		{
			String s = obj.toString();
			try
			{
				result = getNumber(s, clazz);
			}
			catch(Exception e)
			{
				result = null;
			}
		}
		
		if(result==null)
			return defaultValue;
		
		return result;
	}
	
	public static void main(String[] args) {
        String number = "6109628639653416289";
	    System.out.println(new Double(number).longValue());
	    System.out.println(new Long(number));
	    
        number = "6109628639653416288";
        System.out.println(new Double(number).longValue());
        System.out.println(new Long(number).longValue());
        
        System.out.println(new BigDecimal(Double.MAX_VALUE).toString());
        System.out.println(Long.MAX_VALUE);
        
        
        number = "9223372036854775807";
        System.out.println(new Double(number).longValue());
        System.out.println(new Long(number).longValue());
        
        number = "6109628639653416289";
        System.out.println(new Double(number).longValue());
        System.out.println(new Long(number).longValue());
        
    }
}
