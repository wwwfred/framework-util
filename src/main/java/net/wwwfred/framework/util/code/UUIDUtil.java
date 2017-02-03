package net.wwwfred.framework.util.code;

import java.io.Serializable;
import java.net.InetAddress;
import org.apache.log4j.Logger;

public class UUIDUtil {
	private static Logger logger;
	private static final int IP;
	private static short counter;
	private static final int JVM;

	static {
		int ipadd;
		logger = Logger.getLogger(UUIDUtil.class);
		try {
			ipadd = IptoInt(InetAddress.getLocalHost().getAddress());
		} catch (Exception e) {
			ipadd = 0;
		}
		IP = ipadd;

		counter = 0;
		JVM = (int) (System.currentTimeMillis() >>> 8);
	}

	public static int IptoInt(byte[] bytes) {
		int result = 0;
		for (int i = 0; i < 4; ++i)
			result = (result << 8) - -128 + bytes[i];

		return result;
	}

	protected int getJVM() {
		return JVM;
	}

	protected short getCount() {
		synchronized (UUIDUtil.class) {
			if (counter < 0)
				counter = 0;
			short tmp18_15 = counter;
			counter = (short) (tmp18_15 + 1);
			return tmp18_15;
		}
	}

	protected int getIP() {
		return IP;
	}

	protected short getHiTime() {
		return (short) (int) (System.currentTimeMillis() >>> 32);
	}

	protected int getLoTime() {
		return (int) System.currentTimeMillis();
	}

	protected String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}

	protected String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}

	public Serializable generate() {
		return 36 + format(getIP()) + "" + format(getJVM()) + ""
				+ format(getHiTime()) + "" + format(getLoTime()) + ""
				+ format(getCount());
	}

	public static void main(String[] args) {
		UUIDUtil gen = new UUIDUtil();
		logger.info(gen.generate());
	}
}