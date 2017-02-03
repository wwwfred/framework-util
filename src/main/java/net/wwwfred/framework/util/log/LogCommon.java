package net.wwwfred.framework.util.log;

import net.wwwfred.framework.util.code.CodeUtil;

import org.apache.commons.logging.LogFactory;

/**
 * logJdk 日志打印实现类
 * @author wanghang
 *
 */
public class LogCommon implements Log
{

	public void d(String tag, Object message)
	{
		LogFactory.getLog(tag).debug("["+tag+"] "+message);
	}

	public void i(String tag, Object message)
	{
		LogFactory.getLog(tag).info("["+tag+"] "+message);
	}

	public void w(String tag, Object message, Throwable e)
	{
		LogFactory.getLog(tag).warn("["+tag+"] "+message,e);
	}

	public void e(String tag, Object message, Throwable e)
	{
		LogFactory.getLog(tag).error("["+tag+"] "+message,e);
	}

	@Override
	public void d(Object message) {
		LogFactory.getLog(CodeUtil.getLocation(LogUtil.class.getName())).debug(message);
	}

	@Override
	public void i(Object message) {
		LogFactory.getLog(CodeUtil.getLocation(LogUtil.class.getName())).info(message);
	}

	@Override
	public void w(Object message, Throwable e) {
		LogFactory.getLog(CodeUtil.getLocation(LogUtil.class.getName())).warn(message,e);
	}

	@Override
	public void e(Object message, Throwable e) {
		LogFactory.getLog(CodeUtil.getLocation(LogUtil.class.getName())).error(message,e);
	}

}
