package net.wwwfred.framework.util.secret;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import net.wwwfred.framework.util.code.CodeUtil;
import net.wwwfred.framework.util.log.LogUtil;

/**
 * 各种加密算法工具类，如：MD5,SHA1,DES等
 * createdDatetime 2014年10月30日  10:00
 * @author wanghang
 *
 */
public class EncryptUtil {
	/**
	 * DES加密
	 * @param message
	 * @param key
	 * @return String 返回加密后的字符串
	 */
	public static String encryptDES(String message, String key) {
		try
		{
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

			DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

			return toHexString(cipher.doFinal(message.getBytes("UTF-8")));
		}
		catch(Exception e)
		{
			throw new SecretException("encryptDES illegal,message="+message+",key="+key+","+e.getMessage(),e);
		}
	}
	
    /** 
     * MD5加密算法的实现  
     * @param sourceString  需加密的字符串
     * @param encoding 需加密的字符串的编码，若为空则采用默认编码
     * @return String 返回加密后的字符串
     */
    public static String getMD5(String sourceString, String encoding)
    {
    	LogUtil.d("before getMD5,sourceString="+sourceString+",encoding="+encoding);
        if(sourceString==null)
            return null;

        Charset charset = CodeUtil.isEmpty(encoding)?Charset.defaultCharset():Charset.forName(encoding);
        
        String result = null;
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] byteArrayBeforeMd5 = sourceString.getBytes(charset);
            md.update(byteArrayBeforeMd5);
            byte[] byteArrayAfterMd5 = md.digest();
            result = toHexString(byteArrayAfterMd5);
        }
        catch(Exception e)
        {
            Logger.getLogger(EncryptUtil.class.getName()).log(Level.WARNING,"getMD5 illegal,string="+sourceString+","+e.getMessage());
        }
        return result;
    }
    
    /** 
     * MD5加密算法的实现  
     * param sourceString  需加密的字符串
     * @return String 返回加密后的字符串
     */
    public static String getMD5(String sourceString)
    {
        return getMD5(sourceString, Charset.defaultCharset().name());
    }
	
	/** 
	 * SHA1加密算法的实现  
     * @param sourceString  需加密的字符串
	 * @return String 返回加密后的字符串
	 */ 
	public static String getSha1(String sourceString) {
		return getSha1(sourceString,null);
	}
	
	/** 
	 * SHA1加密算法的实现  
     * @param sourceString  需加密的字符串
     * @param encoding 需加密字符串的编码
	 * @return String 返回加密后的字符串
	 */ 
	public static String getSha1(String sourceString, String encoding) {
		if (sourceString == null || sourceString.length() == 0) {
			return null;
		}
		
		encoding = CodeUtil.isEmpty(encoding)?"UTF-8":encoding;
		
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };

		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
			
			mdTemp.update(sourceString.getBytes(encoding));

			byte[] md = mdTemp.digest();
			int j = md.length;
			char buf[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
				buf[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(buf);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 字节数组转换成十六进制字符串
	 *
	 */
	private static String toHexString(byte[] byteArray)
	{
		if(byteArray==null)
			return null;
		
		StringBuffer sb=new StringBuffer();  
        for(int i=0;i<byteArray.length;i++){    
            int v=byteArray[i]&0xff;  
            if(v<16){  
                sb.append(0);  
            }  
            sb.append(Integer.toHexString(v));  
        }
		return sb.toString();
	}


	public static void main(String[] args) {
		System.out.println(EncryptUtil.getSha1("7"));
		//f4de502e58723e6252e8856d4dc8fc3b
	}
}
