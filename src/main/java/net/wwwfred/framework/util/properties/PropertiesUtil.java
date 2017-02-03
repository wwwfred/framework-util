package net.wwwfred.framework.util.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import net.wwwfred.framework.util.code.CodeUtil;

public class PropertiesUtil
{
    /** 属性配置文件名后缀 */
    public static String PROPERTIES_FILE_NAME_SUFFIX = ".properties";
    
    /** 缓存最后一次加载的属性配置文件名 */
	private static String configFileName;
	/** 缓存最后一次加载的属性对象 */
	private static Properties properties;
	
	/**
	 *  io流 读取properties文件
	 * @param configFileName
	 */
	private static void load(String configFileName)
	{
		PropertiesUtil.configFileName = configFileName;
		
		if(properties!=null)
		{
			properties.clear();
		}
		else
		{
			properties = new Properties();
		}
		
		InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(configFileName);
//		if(in==null)
//		{
//			throw new PropertiesException("PropertiesUtil.load inputStream=null from configFileName="+configFileName);
//		}
		
		if(in!=null)
		{
		    try
	        {
	            properties.load(in);
	        } catch (IOException e)
	        {
	            throw new PropertiesException("PropertiesUtil.load load inputStream IOException occured.",e);
	        }
	        finally
	        {
	            try
	            {
	                if(in!=null)
	                {
	                    in.close();
	                }
	            } catch (IOException e)
	            {
	                throw new PropertiesException("PropertiesUtil.load close inputStream IOException occured.",e);
	            }
	        }
		}
		
	}
	
	/**
	 * 根据指定的配置文件名称获取配置文件属性Map
	 * @param configFileName
	 * @return
	 */
	public static Map<String, String> getPropertiesMap(String configFileName)
	{
	    Map<String, String> result = new LinkedHashMap<String, String>();
	    
	    if(configFileName==null)
	    {
	        throw new PropertiesException("PropertiesUtil.getPropertiesMap configFileName is null");
	    }
	    
	    if(properties==null||!configFileName.equals(PropertiesUtil.configFileName))
	    {
	        load(configFileName);
	    }
	    
	    Enumeration<?> names = properties.propertyNames();
	    while(names.hasMoreElements())
	    {
	        String name = names.nextElement().toString();
	        String value = properties.getProperty(name);
	        result.put(name, value);
	    }
	    return result;
	}
	
	/**
	 * 根据 key值获取 properties中对应的 value值
	 * @param configFileName  properties文件名称
	 * @param key	key值
	 * @return  String  与key对应的value值
	 */
	public static String getValue(String configFileName, String key)
	{
		String keyPrefix = CodeUtil.getLocationStackTrace(PropertiesUtil.class.getName()).getClassName()+".";
		return getKeyValue(configFileName, keyPrefix, key);
	}
	
	private static String getKeyValue(String configFileName,String keyPrefix, String key)
	{
		if(configFileName==null)
			throw new PropertiesException("PropertiesUtil.getValue(configFileName,key) configFileName is null");
		
		if(properties==null||!configFileName.equals(PropertiesUtil.configFileName))
		{
			load(configFileName);
		}
		String value;
		String locationKey = keyPrefix+key;
		if(properties.containsKey(locationKey))
		{
			value = properties.getProperty(locationKey);
		}
		else
		{
			value = properties.getProperty(key);
		}
		return value;
	}
	
	/**
	 * 根据 key值获取 properties中对应的 value值，如果value为null或者报异常，则返回默认值
	 * @param configFileName  properties文件名称
	 * @param key  key值
	 * @param defaultValue  默认值
	 * @return String  返回value值或默认值
	 */
	public static String getValue(String configFileName, String key, String defaultValue)
	{
		String value;
		String keyPrefix = CodeUtil.getLocationStackTrace(PropertiesUtil.class.getName()).getClassName()+".";
		try
		{
			value = getKeyValue(configFileName,keyPrefix, key);
		}
		catch(Exception e)
		{
			value = null;
		}
		
		if(value==null)
			return defaultValue;
		return value;
	}
	
}
