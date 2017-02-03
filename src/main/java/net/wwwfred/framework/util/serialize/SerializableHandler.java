package net.wwwfred.framework.util.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.wwwfred.framework.util.io.IOUtil;

public class SerializableHandler {

	private static char[] HEX_ENCODE_TALBE = new char[] { '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private static byte[] HEX_DECODE_TABLE = new byte[255];

	static {
		HEX_DECODE_TABLE['0'] = 0;
		HEX_DECODE_TABLE['1'] = 1;
		HEX_DECODE_TABLE['2'] = 2;
		HEX_DECODE_TABLE['3'] = 3;
		HEX_DECODE_TABLE['4'] = 4;
		HEX_DECODE_TABLE['5'] = 5;
		HEX_DECODE_TABLE['6'] = 6;
		HEX_DECODE_TABLE['7'] = 7;
		HEX_DECODE_TABLE['8'] = 8;
		HEX_DECODE_TABLE['9'] = 9;
		HEX_DECODE_TABLE['A'] = 10;
		HEX_DECODE_TABLE['B'] = 11;
		HEX_DECODE_TABLE['C'] = 12;
		HEX_DECODE_TABLE['D'] = 13;
		HEX_DECODE_TABLE['E'] = 14;
		HEX_DECODE_TABLE['F'] = 15;
		HEX_DECODE_TABLE['a'] = 10;
		HEX_DECODE_TABLE['b'] = 11;
		HEX_DECODE_TABLE['c'] = 12;
		HEX_DECODE_TABLE['d'] = 13;
		HEX_DECODE_TABLE['e'] = 14;
		HEX_DECODE_TABLE['f'] = 15;
	}

	public static Object stringToObject(String s) {
		if (s == null) {
			return null;
		}
		byte[] objData = parseByteArray(s);
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(objData);
			ois = new ObjectInputStream(bis);
		} catch (Exception e) {
			throw new SerializeException("ByteArrayInputStream(byte[]) illegal",e);
		} finally {
		    IOUtil.closeInputStream(bis,ois);
		}
		try {
            return ois.readObject();
        } catch (IOException e) {
            throw new SerializeException("serializable streamToObject IOException",e);
        } catch (ClassNotFoundException e) {
            throw new SerializeException("streamToObject ClassNotFoundException",e);
        }
	}

	public static String objectToString(Object obj){
		if (!(obj instanceof Serializable)) {
			throw new SerializeException(
					"serializable object must implements Serializable.");
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		byte[] objData = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			objData = bos.toByteArray();
		} catch (Exception e) {
			throw new SerializeException("serializable objectToStream IOException",e);
		} finally {
		    IOUtil.closeOutputStream(bos,oos);
		}

		return formatByteArray(objData);
	}

	private static byte[] parseByteArray(String s) {
		char[] chars = s.toCharArray();
		byte[] result = new byte[chars.length / 2];
		for (int i = 0, j = 0; i < result.length; i++, j += 2) {
			int high = HEX_DECODE_TABLE[chars[j]];
			int low = HEX_DECODE_TABLE[chars[j + 1]];
			result[i] = (byte) (0xff & ((high << 4) + low));
		}
		return result;
	}

	private static String formatByteArray(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			result.append(HEX_ENCODE_TALBE[(bytes[i] & 0xf0) >>> 4]);
			result.append(HEX_ENCODE_TALBE[bytes[i] & 0x0f]);
		}
		return result.toString();
	}

}
