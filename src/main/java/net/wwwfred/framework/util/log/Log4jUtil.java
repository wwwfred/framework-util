package net.wwwfred.framework.util.log;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.wwwfred.framework.util.code.CodeUtil;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Log4jUtil {
	
//	private static String LOG4J_CONFIG_FILE_NAME = "log4j.properties";
	private static String lineSeparator = System.getProperty("line.separator");
//	private static String fileSepartor = System.getProperty("file.separator");
//	
//	private static String LOG_HOME = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log_home","/data/web_log/tomcat_log/java_log");
//	private static String LOG_SYSTEM = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log_system","log_system");
//	
//	private static String LOG_FILE_TYPE = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log_file_type", ".log");
//	
//	private static String APPENDER_LAYOUT_PATTERN = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "appender.layout.ConversionPattern","%n["+LOG_SYSTEM+"] [ %p ] [%d{yyyy-MM-dd HH:mm:ss,SSS}] [%c] [%t] - [%m]%n");
//	
////	private static String CONSOLE_APPENDER_NAME = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "console_appender_name","System.out");
////	private static String CONSOLE_APPENDER_LAYOUT_PATTERN = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.console.layout.ConversionPattern","%n["+LOG_SYSTEM+"] [ %p ] [%d{yyyy-MM-dd HH:mm:ss,SSS}] [%c] [%t] - [%m]%n");
////	private static String CONSOLE_APPENDER_THRESHOLD = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.console.Threshold","INFO");
////	private static String CONSOLE_APPENDER_TARGET = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.console.Target","System.out");
//	
////	private static String DEBUG_FILE_APPENDER_LAYOUT_PATTERN = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.debug.layout.ConversionPattern","%n["+LOG_SYSTEM+"] [ %p ] [%d{yyyy-MM-dd HH:mm:ss,SSS}] [%c] [%t] - [%m]%n");
//	private static String DEBUG_FILE_APPENDER_THRESHOLD = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.debug.Threshold","DEBUG");
//	private static String DEBUG_FILE_APPENDER_APPEND = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.debug.Append","true");
//	private static String DEBUG_FILE_APPENDER_IMMEDIATEFLUSH = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.debug.ImmediateFlush","true");
//	private static String DEBUG_FILE_APPENDER_MAXBACKUPINDEX = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.debug.MaxBackupIndex","100");
//	private static String DEBUG_FILE_APPENDER_MAXFILESIZE = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.debug.MaxFileSize","100MB");
//	
////	private static String INFO_FILE_APPENDER_LAYOUT_PATTERN = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.info.layout.ConversionPattern","%n["+LOG_SYSTEM+"] [ %p ] [%d{yyyy-MM-dd HH:mm:ss,SSS}] [%c] [%t] - [%m]%n");
//	private static String INFO_FILE_APPENDER_THRESHOLD = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.info.Threshold","INFO");
//	private static String INFO_FILE_APPENDER_APPEND = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.info.Append","true");
//	private static String INFO_FILE_APPENDER_IMMEDIATEFLUSH = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.info.debug.ImmediateFlush","true");
//	private static String INFO_FILE_APPENDER_MAXBACKUPINDEX = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.info.debug.MaxBackupIndex","100");
//	private static String INFO_FILE_APPENDER_MAXFILESIZE = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.info.debug.MaxFileSize","50MB");
//	
////	private static String WARN_FILE_APPENDER_LAYOUT_PATTERN = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.debug.layout.ConversionPattern","%n["+LOG_SYSTEM+"] [ %p ] [%d{yyyy-MM-dd HH:mm:ss,SSS}] [%c] [%t] - [%m]%n");
//	private static String WARN_FILE_APPENDER_THRESHOLD = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.warn.Threshold","WARN");
//	private static String WARN_FILE_APPENDER_APPEND = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.warn.Append","true");
//	private static String WARN_FILE_APPENDER_IMMEDIATEFLUSH = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.warn.ImmediateFlush","true");
//	private static String WARN_FILE_APPENDER_MAXBACKUPINDEX = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.warn.MaxBackupIndex","100");
//	private static String WARN_FILE_APPENDER_MAXFILESIZE = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.warn.MaxFileSize","30MB");
//	
////	private static String ERROR_FILE_APPENDER_LAYOUT_PATTERN = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.error.layout.ConversionPattern","%n["+LOG_SYSTEM+"] [ %p ] [%d{yyyy-MM-dd HH:mm:ss,SSS}] [%c] [%t] - [%m]%n");
//	private static String ERROR_FILE_APPENDER_THRESHOLD = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.error.Threshold","ERROR");
//	private static String ERROR_FILE_APPENDER_APPEND = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.error.Append","true");
//	private static String ERROR_FILE_APPENDER_IMMEDIATEFLUSH = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.error.ImmediateFlush","true");
//	private static String ERROR_FILE_APPENDER_MAXBACKUPINDEX = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.error.MaxBackupIndex","100");
//	private static String ERROR_FILE_APPENDER_MAXFILESIZE = PropertiesUtil.getValue(LOG4J_CONFIG_FILE_NAME, "log4j.appender.error.MaxFileSize","10MB");
//	
//	private final static String SERVER_ADDRESS = getServerAddress();
//	
////	// 当前注册的所有Appender
////	private static Map<String, Appender> appenderMap = new HashMap<String, Appender>();

	/** 静态输出字节流提前声明初始化，避免每次都去new */
	private static StringBuilder exceptionStackTraceStringBuilder;
	private static StringWriter exceptionStackTraceWriter;
	
	/**
     * 打印debug级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     */
    public static void d(Object message)
    {
        getLogger(Level.DEBUG).debug(message);
    }
    
//	private static String getServerAddress() {
//		String serverAddress;
//		try {
//			serverAddress = InetAddress.getLocalHost().getHostAddress();
//		} catch (UnknownHostException e) {
//			w("InetAddress.getLocalHost illegal",e);
//			serverAddress = "127.0.0.1";
//		}
//		return serverAddress;
//	}

	/**
     * 打印debug级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     */
    public static void d(String tag,Object message)
    {
        getLogger(tag,Level.DEBUG).debug(message);
    }
    
    /**
     * 打印info级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     */
    public static void i(Object message)
    {
    	getLogger(Level.INFO).info(message);
    }
    
    /**
     * 打印info级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     */
    public static void i(String tag,Object message)
    {
    	getLogger(tag,Level.INFO).info(message);
    }
    
    /**
     * 打印warn级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     * @param e         异常信息 Exception
     */
    public static void w(Object message,Throwable e)
    {
    	if(e!=null)
    	{
    		try
    		{
    			exceptionStackTraceStringBuilder = new StringBuilder();
    			e.printStackTrace(new PrintWriter(exceptionStackTraceWriter));
    			exceptionStackTraceStringBuilder.append(message).append(lineSeparator).append(exceptionStackTraceWriter.getBuffer());
    			exceptionStackTraceWriter.flush();
    			message = exceptionStackTraceStringBuilder;
    		}
    		catch(Exception e1)
    		{
    			w("exceptionStackTraceWriter operate illegal",e1);
    		}
    	}
    	getLogger(Level.WARN).warn(message);
    }
    
    /**
     * 打印warn级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     * @param e         异常信息 Exception
     */
    public static void w(String tag,Object message,Throwable e)
    {
    	if(e!=null)
    	{
    		try
    		{
    			exceptionStackTraceStringBuilder = new StringBuilder();
    			exceptionStackTraceWriter = new StringWriter();
    			e.printStackTrace(new PrintWriter(exceptionStackTraceWriter));
    			exceptionStackTraceWriter.close();
    			exceptionStackTraceStringBuilder.append(message).append(lineSeparator).append(exceptionStackTraceWriter.getBuffer());
    			message = exceptionStackTraceStringBuilder;
    		}
    		catch(Exception e1)
    		{
    			w("exceptionStackTraceWriter operate illegal",e1);
    		}
    	}
    	getLogger(tag,Level.WARN).warn(message);
    }
    
    /**
     * 打印错误error级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     * @param e         异常信息 Exception
     */
    public static void e(Object message,Throwable e)
    {
    	if(e!=null)
    	{
    		try
    		{
    			exceptionStackTraceStringBuilder = new StringBuilder();
    			exceptionStackTraceWriter = new StringWriter();
    			e.printStackTrace(new PrintWriter(exceptionStackTraceWriter));
    			exceptionStackTraceWriter.close();
    			exceptionStackTraceStringBuilder.append(message).append(lineSeparator).append(exceptionStackTraceWriter.getBuffer());
    			message = exceptionStackTraceStringBuilder;
    		}
    		catch(Exception e1)
    		{
    			w("exceptionStackTraceWriter operate illegal",e1);
    		}
    	}
    	getLogger(Level.ERROR).error(message);
    }
    
    /**
     * 打印错误error级别的日志信息
     * @param tag       所在类的类名
     * @param message   打印信息
     * @param e         异常信息 Exception
     */
    public static void e(String tag,Object message,Throwable e)
    {
    	if(e!=null)
    	{
    		try
    		{
    			exceptionStackTraceStringBuilder = new StringBuilder();
    			exceptionStackTraceWriter = new StringWriter();
    			e.printStackTrace(new PrintWriter(exceptionStackTraceWriter));
    			exceptionStackTraceWriter.close();
    			exceptionStackTraceStringBuilder.append(message).append(lineSeparator).append(exceptionStackTraceWriter.getBuffer());
    			message = exceptionStackTraceStringBuilder;
    		}
    		catch(Exception e1)
    		{
    			w("exceptionStackTraceWriter operate illegal",e1);
    		}
    	}
    	getLogger(tag,Level.ERROR).error(message);
    }
    
    /**
     * 获取当前打印日志的位置
     * @return
     */
    private static String getCurrentThreadLogLocation()
    {
    	String location = CodeUtil.getLocation(LogUtil.class.getName());
    	if(location==null||"".equals(location.trim()))
    	{
    		location = CodeUtil.getLocation(Log4jUtil.class.getName()); 
    	}
    	return "at " + location;
    }
    
    private static Logger getLogger(Level level)
    {
    	return getLogger(null, level);
    }
    
//    /** 获取rootLog当前注册的所有Appender */
//    private static Map<String, Appender> getLoggerAppenderMap(Logger log)
//    {
//    	Map<String, Appender> appenderMap = new HashMap<String, Appender>();
//    	
//    	Logger rootLog = Logger.getRootLogger();
//    	Logger tempLog = log;
//    	while(true)
//    	{
//    		if(tempLog==null)
//    		{
//    			break;
//    		}
//    		else
//    		{
//    			Enumeration<?> e = tempLog.getAllAppenders();
//    			while(e.hasMoreElements())
//    			{
//    				Appender appender = (Appender) e.nextElement();
//    				if(!appenderMap.containsKey(appender.getName()))
//    				{
////    				if(appender instanceof ConsoleAppender)
////    				{
////    					ConsoleAppender consoleAppender = (ConsoleAppender)appender;
////    					appenderMap.put(consoleAppender.getTarget(), consoleAppender);
////    				}
//////    				else if(appender instanceof FileAppender)
//////    				{
//////    					FileAppender fileAppender = (FileAppender)appender;
//////    					appenderMap.put(fileAppender.getFile(), fileAppender);
//////    				}
////    				else
////    				{
//    					appenderMap.put(appender.getName(), appender);
////    				}
//    				}
//    			}
//    			
//    			if(tempLog.equals(rootLog))
//    			{
//    				break;
//    			}
//    			tempLog = (Logger) tempLog.getParent();
//    		}
//    	}
//    	return appenderMap;
//    }
    
    private static Logger getLogger(String tag, Level level)
    {
//    	// 1. rootLog
//    	Logger rootLog = Logger.getRootLogger();
//    	
//    	// 2. 获取rootLog当前注册的所有Appender
//    	Map<String, Appender> appenderMap = getLoggerAppenderMap(rootLog);
//    	
////    	// console
////    	String consoleAppenderName = CONSOLE_APPENDER_NAME;
////    	if(!appenderMap.containsKey(consoleAppenderName))
////    	{
////    		Layout consoleAppenderLayout = new PatternLayout(CONSOLE_APPENDER_LAYOUT_PATTERN);
////        	ConsoleAppender consoleAppender = new ConsoleAppender(consoleAppenderLayout);
////        	consoleAppender.setName(consoleAppenderName);
////        	consoleAppender.setThreshold(Level.toLevel(CONSOLE_APPENDER_THRESHOLD));
////        	consoleAppender.setTarget(CONSOLE_APPENDER_TARGET);
////    		
////        	rootLog.addAppender(consoleAppender);
////    	}
//    	
//    	// 3. append 日期/系统/级别/小时文件 的RollingFileAppender, ConsoleAppender, JdbcAppender
//    	long nowtime = System.currentTimeMillis();
//    	
//    	// level int value
//    	int levelIntValue = level.toInt();
//
//    	if(levelIntValue>=Level.DEBUG.toInt())
//    	{
//    		// rollingFile debug
//        	Layout debugFileAppenderLayout = new PatternLayout(APPENDER_LAYOUT_PATTERN);
//        	Level debugFileLevel = Level.toLevel(DEBUG_FILE_APPENDER_THRESHOLD);
//        	Boolean debugFileAppend = Boolean.parseBoolean(DEBUG_FILE_APPENDER_APPEND);
//        	Boolean debugFileImmediateFlush = Boolean.parseBoolean(DEBUG_FILE_APPENDER_IMMEDIATEFLUSH);
//        	Integer debugFileMaxBackupIndex = Integer.parseInt(DEBUG_FILE_APPENDER_MAXBACKUPINDEX);
//        	String debugFileMaxFileSize = DEBUG_FILE_APPENDER_MAXFILESIZE;
//        	String debugFileName = DatetimeUtil.longToDateTimeString(nowtime, "MMdd") + fileSepartor
//        			+ LOG_SYSTEM + fileSepartor
//        			+ DEBUG_FILE_APPENDER_THRESHOLD + fileSepartor
//        			+ DatetimeUtil.longToDateTimeString(nowtime, "HH") + fileSepartor
//        			+ SERVER_ADDRESS + LOG_FILE_TYPE;
//        	String debugAppenderName = 	LOG_HOME + fileSepartor + debugFileName;
//        	if(!appenderMap.containsKey(debugAppenderName))
//        	{
//        		if(!IOUtil.isLocalExist(null, debugAppenderName))
//            	{
//            		IOUtil.createLocalFile(null, debugAppenderName);
//            	}
//        		
//        		RollingFileAppender debugFileAppender = null;
//    			try {
//    				debugFileAppender = new RollingFileAppender(debugFileAppenderLayout,debugAppenderName,debugFileAppend);
//    			} catch (IOException e) {
//    				w("create RollingFileAppender illegal,fileName="+debugAppenderName,e);
//    			}
//    			if(debugFileAppend!=null)
//    			{
//    				debugFileAppender.setName(debugAppenderName);
//    				debugFileAppender.setThreshold(debugFileLevel);
//    				debugFileAppender.setImmediateFlush(debugFileImmediateFlush);
//    				debugFileAppender.setMaxBackupIndex(debugFileMaxBackupIndex);
//    				debugFileAppender.setMaxFileSize(debugFileMaxFileSize);
//    				
//    				rootLog.addAppender(debugFileAppender);
//    			}
//        	}
//    	}
//    	
//    	if(levelIntValue>=Level.INFO.toInt())
//    	{
//    		// rollingFile info
//        	Layout infoFileAppenderLayout = new PatternLayout(APPENDER_LAYOUT_PATTERN);
//        	Level infoFileLevel = Level.toLevel(INFO_FILE_APPENDER_THRESHOLD);
//        	Boolean infoFileAppend = Boolean.parseBoolean(INFO_FILE_APPENDER_APPEND);
//        	Boolean infoFileImmediateFlush = Boolean.parseBoolean(INFO_FILE_APPENDER_IMMEDIATEFLUSH);
//        	Integer infoFileMaxBackupIndex = Integer.parseInt(INFO_FILE_APPENDER_MAXBACKUPINDEX);
//        	String infoFileMaxFileSize = INFO_FILE_APPENDER_MAXFILESIZE;
//        	String infoFileName = DatetimeUtil.longToDateTimeString(nowtime, "MMdd") + fileSepartor
//        			+ LOG_SYSTEM + fileSepartor
//        			+ INFO_FILE_APPENDER_THRESHOLD + fileSepartor
//        			+ DatetimeUtil.longToDateTimeString(nowtime, "HH") + fileSepartor
//        			+ SERVER_ADDRESS + LOG_FILE_TYPE;
//        	String infoAppenderName = LOG_HOME + fileSepartor + infoFileName;
//        	if(!appenderMap.containsKey(infoAppenderName))
//        	{
//        		if(!IOUtil.isLocalExist(null, infoAppenderName))
//            	{
//            		IOUtil.createLocalFile(null, infoAppenderName);
//            	}
//        		
//        		RollingFileAppender infoFileAppender = null;
//    			try {
//    				infoFileAppender = new RollingFileAppender(infoFileAppenderLayout,infoAppenderName,infoFileAppend);
//    			} catch (IOException e) {
//    				w("create RollingFileAppender illegal,fileName="+infoAppenderName,e);
//    			}
//    			if(infoFileAppend!=null)
//    			{
//    				infoFileAppender.setName(infoAppenderName);
//    				infoFileAppender.setThreshold(infoFileLevel);
//    				infoFileAppender.setImmediateFlush(infoFileImmediateFlush);
//    				infoFileAppender.setMaxBackupIndex(infoFileMaxBackupIndex);
//    				infoFileAppender.setMaxFileSize(infoFileMaxFileSize);
//    				
//    				rootLog.addAppender(infoFileAppender);
//    			}
//        	}
//    	}
//    	
//    	if(levelIntValue>=Level.WARN.toInt())
//    	{
//    		// rollingFile warn
//        	Layout warnFileAppenderLayout = new PatternLayout(APPENDER_LAYOUT_PATTERN);
//        	Level warnFileLevel = Level.toLevel(WARN_FILE_APPENDER_THRESHOLD);
//        	Boolean warnFileAppend = Boolean.parseBoolean(WARN_FILE_APPENDER_APPEND);
//        	Boolean warnFileImmediateFlush = Boolean.parseBoolean(WARN_FILE_APPENDER_IMMEDIATEFLUSH);
//        	Integer warnFileMaxBackupIndex = Integer.parseInt(WARN_FILE_APPENDER_MAXBACKUPINDEX);
//        	String warnFileMaxFileSize = WARN_FILE_APPENDER_MAXFILESIZE;
//        	String warnFileName = DatetimeUtil.longToDateTimeString(nowtime, "MMdd") + fileSepartor
//        			+ LOG_SYSTEM + fileSepartor
//        			+ WARN_FILE_APPENDER_THRESHOLD + fileSepartor
//        			+ DatetimeUtil.longToDateTimeString(nowtime, "HH") + fileSepartor
//        			+ SERVER_ADDRESS + LOG_FILE_TYPE;
//        	String warnAppenderName = LOG_HOME + fileSepartor + warnFileName;
//        	if(!appenderMap.containsKey(warnAppenderName))
//        	{
//        		if(!IOUtil.isLocalExist(null, warnAppenderName))
//            	{
//            		IOUtil.createLocalFile(null, warnAppenderName);
//            	}
//        		
//        		RollingFileAppender warnFileAppender = null;
//    			try {
//    				warnFileAppender = new RollingFileAppender(warnFileAppenderLayout,warnAppenderName,warnFileAppend);
//    			} catch (IOException e) {
//    				w("create RollingFileAppender illegal,fileName="+warnAppenderName,e);
//    			}
//    			if(warnFileAppend!=null)
//    			{
//    				warnFileAppender.setName(warnAppenderName);
//    				warnFileAppender.setThreshold(warnFileLevel);
//    				warnFileAppender.setImmediateFlush(warnFileImmediateFlush);
//    				warnFileAppender.setMaxBackupIndex(warnFileMaxBackupIndex);
//    				warnFileAppender.setMaxFileSize(warnFileMaxFileSize);
//
//            		rootLog.addAppender(warnFileAppender);
//    			}
//        	}
//    	}
//    	
//    	if(levelIntValue>=Level.ERROR.toInt())
//    	{
//    		// rollingFile error
//        	Layout errorFileAppenderLayout = new PatternLayout(APPENDER_LAYOUT_PATTERN);
//        	Level errorFileLevel = Level.toLevel(ERROR_FILE_APPENDER_THRESHOLD);
//        	Boolean errorFileAppend = Boolean.parseBoolean(ERROR_FILE_APPENDER_APPEND);
//        	Boolean errorFileImmediateFlush = Boolean.parseBoolean(ERROR_FILE_APPENDER_IMMEDIATEFLUSH);
//        	Integer errorFileMaxBackupIndex = Integer.parseInt(ERROR_FILE_APPENDER_MAXBACKUPINDEX);
//        	String errorFileMaxFileSize = ERROR_FILE_APPENDER_MAXFILESIZE;
//        	String errorFileName = DatetimeUtil.longToDateTimeString(nowtime, "MMdd") + fileSepartor
//        			+ LOG_SYSTEM + fileSepartor
//        			+ ERROR_FILE_APPENDER_THRESHOLD + fileSepartor
//        			+ DatetimeUtil.longToDateTimeString(nowtime, "HH") + fileSepartor
//        			+ SERVER_ADDRESS + LOG_FILE_TYPE;
//        	String errorAppenderName = LOG_HOME + fileSepartor + errorFileName;
//        	if(!appenderMap.containsKey(errorAppenderName))
//        	{
//        		if(!IOUtil.isLocalExist(null, errorAppenderName))
//            	{
//            		IOUtil.createLocalFile(null, errorAppenderName);
//            	}
//        		
//        		RollingFileAppender errorFileAppender = null;
//    			try {
//    				errorFileAppender = new RollingFileAppender(errorFileAppenderLayout,errorAppenderName,errorFileAppend);
//    			} catch (IOException e) {
//    				w("create RollingFileAppender illegal,fileName="+errorAppenderName,e);
//    			}
//    			if(errorFileAppend!=null)
//    			{
//    				errorFileAppender.setName(errorAppenderName);
//    				errorFileAppender.setThreshold(errorFileLevel);
//    				errorFileAppender.setImmediateFlush(errorFileImmediateFlush);
//    				errorFileAppender.setMaxBackupIndex(errorFileMaxBackupIndex);
//    				errorFileAppender.setMaxFileSize(errorFileMaxFileSize);
//
//            		rootLog.addAppender(errorFileAppender);
//    			}
//        	}
//    	}
    	
    	tag = (tag==null||"".equals(tag.trim()))?getCurrentThreadLogLocation():tag;
    	Logger log = LogManager.getLogger(tag);
    	
    	return log;
    }

}
