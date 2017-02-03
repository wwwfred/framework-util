package net.wwwfred.framework.util.log;

import net.wwwfred.framework.util.code.CodeUtil;
import net.wwwfred.framework.util.log.Log;
import net.wwwfred.framework.util.log.LogUtil;
import net.wwwfred.framework.util.properties.PropertiesUtil;

/**
 * log日志打印工具类
 * @author Administrator
 *
 */
public class LogUtil
{
    /** 配置服务文件名称 */
    private static String PROPERTIES_CONFIG_FILE_NAME = "config.properties";
    /** 测试日志LogTag */
    private static String LOGGER_TEST = PropertiesUtil.getValue(PROPERTIES_CONFIG_FILE_NAME, "logger_test", "TEST");

    public static String LOG_DEFAULT_IMPL_CLASS = PropertiesUtil.getValue(PROPERTIES_CONFIG_FILE_NAME, "log_default_impl_class","com.teshehui.util.log.Log4j");
    
    private static Log log = getLog();
    
    public static void setLog(Log log)
    {
        LogUtil.log = log;
    }

	public static Log getLog() {
		if (log == null) {
			if(!CodeUtil.isEmpty(LOG_DEFAULT_IMPL_CLASS))
			{
				try {
					log = (Log) Class.forName(LOG_DEFAULT_IMPL_CLASS).newInstance();
					return log;
				} catch (Exception e) {
					throw new RuntimeException("illegal log_impl_class="+LOG_DEFAULT_IMPL_CLASS);
				}
			}
			
			try {
				Class.forName("org.apache.logging.log4j.Logger");
				log = new Log4j2();
			} catch (ClassNotFoundException e1) {
				System.out.println("org.apache.logging.log4j.Logger ClassNotFound");
				try {
					Class.forName("org.apache.log4j.Logger");
					log = new Log4j();
				} catch (ClassNotFoundException e2) {
					System.out.println("org.apache.log4j.Logger ClassNotFound");
					try
					{
						Class.forName("org.apache.commons.logging.Log");
						log = new LogCommon();
					}catch(ClassNotFoundException e3)
					{
						System.out.println("org.apache.commons.logging.Log ClassNotFound");
						// class not found
						log = new LogJdk();
					}
				}
			}
		}
		return log;
	}
    
    /**
     * 打印debug级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     */
    public static void d(String tag,Object message)
    {
        log.d(tag!=null?tag:("at " + CodeUtil.getLocation(LogUtil.class.getName())),message);
    }
    
    public static void d(Object message)
    {
    	log.d(("at " + CodeUtil.getLocation(LogUtil.class.getName())),message);
    }
    
    /**
     * 打印info级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     */
    public static void i(String tag,Object message)
    {
        log.i(tag!=null?tag:("at " + CodeUtil.getLocation(LogUtil.class.getName())),message);
    }
    
    public static void i(Object message)
    {
    	log.i(("at " + CodeUtil.getLocation(LogUtil.class.getName())), message);
    }
    
    /**
     * 打印warn级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     * @param e         异常信息 Exception
     */
    public static void w(String tag,Object message,Throwable e)
    {
        log.w(tag!=null?tag:("at " + CodeUtil.getLocation(LogUtil.class.getName())),message, e);
    }
    
    public static void w(Object message,Throwable e)
    {
    	log.w(("at " + CodeUtil.getLocation(LogUtil.class.getName())), message, e);
    }
    
    /**
     * 打印错误error级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     * @param e         异常信息 Exception
     */
    public static void e(String tag,Object message,Throwable e)
    {
        log.e(tag!=null?tag:("at " + CodeUtil.getLocation(LogUtil.class.getName())),message, e);
    }
    
    public static void e(Object message,Throwable e)
    {
    	log.e(("at " + CodeUtil.getLocation(LogUtil.class.getName())), message, e);
    }
    
    public static void test(Object message)
    {
        log.i(LOGGER_TEST, message);
    }

}
