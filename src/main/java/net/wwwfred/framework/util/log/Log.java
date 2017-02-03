package net.wwwfred.framework.util.log;

/**
 * 日志打印工具类接口
 * @author wanghang
 *
 */
public interface Log
{
	void d(String tag,Object message);
	void i(String tag,Object message);
	void w(String tag,Object message, Throwable e);
	void e(String tag,Object message, Throwable e);
	
	void d(Object message);
	void i(Object message);
	void w(Object message, Throwable e);
	void e(Object message, Throwable e);
}
