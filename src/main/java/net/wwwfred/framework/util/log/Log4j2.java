package net.wwwfred.framework.util.log;

/**
 * log4j 日志打印工具类实现类
 * @author wanghang
 *
 */
public class Log4j2 implements Log
{
	public void d(String tag, Object message)
	{
		Log4j2Util.d(tag,message);
	}

	public void i(String tag, Object message)
	{
		Log4j2Util.i(tag, message);
	}

	public void w(String tag, Object message, Throwable e)
	{
		Log4j2Util.w(tag, message, e);
	}

	public void e(String tag, Object message, Throwable e)
	{
		Log4j2Util.e(tag, message, e);
	}

	public void d(Object message) {
		Log4j2Util.d(message);
	}

	public void i(Object message) {
		Log4j2Util.i(message);
	}

	public void w(Object message, Throwable e) {
		Log4j2Util.w(message, e);
	}

	public void e(Object message, Throwable e) {
		Log4j2Util.e(message, e);
	}

}
