package net.wwwfred.framework.util.log;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.wwwfred.framework.util.code.CodeUtil;

/**
 * logJdk 日志打印实现类
 * @author wanghang
 *
 */
public class LogJdk implements Log
{

	public void d(String tag, Object message)
	{
		Logger.getLogger(tag).log(Level.FINEST,"["+tag+"] "+message);
	}

	public void i(String tag, Object message)
	{
		Logger.getLogger(tag).log(Level.INFO,"["+tag+"] "+message);
	}

	public void w(String tag, Object message, Throwable e)
	{
		Logger.getLogger(tag).log(Level.WARNING, "["+tag+"] "+message,e);
	}

	public void e(String tag, Object message, Throwable e)
	{
		Logger.getLogger(tag).log(Level.SEVERE,"["+tag+"] "+message,e);
	}

	@Override
	public void d(Object message) {
		Logger.getLogger(CodeUtil.getLocation(LogUtil.class.getName())).log(Level.FINEST, ""+message);
	}

	@Override
	public void i(Object message) {
		Logger.getLogger(CodeUtil.getLocation(LogUtil.class.getName())).log(Level.INFO, ""+message);
	}

	@Override
	public void w(Object message, Throwable e) {
		Logger.getLogger(CodeUtil.getLocation(LogUtil.class.getName())).log(Level.WARNING, ""+message);
	}

	@Override
	public void e(Object message, Throwable e) {
		Logger.getLogger(CodeUtil.getLocation(LogUtil.class.getName())).log(Level.SEVERE, ""+message);
	}

}
