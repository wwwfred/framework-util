package net.wwwfred.framework.util.secret;

import java.io.IOException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class DesUtil {

	 /** 
     * 密钥算法 
    */  
    private static final String KEY_ALGORITHM = "DES";  
      
//    private static final String DEFAULT_CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";  
//  private static final String DEFAULT_CIPHER_ALGORITHM = "DES/ECB/ISO10126Padding";  
      
      
    /** 
     * 初始化密钥 
     *  
     * @return byte[] 密钥  
     * @throws Exception 
     */  
    public static byte[] initSecretKey() throws Exception{  
        //返回生成指定算法的秘密密钥的 KeyGenerator 对象  
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);  
        //初始化此密钥生成器，使其具有确定的密钥大小  
        kg.init(56);  
        //生成一个密钥  
        SecretKey  secretKey = kg.generateKey();  
        return secretKey.getEncoded();  
    }  
    
    
    /**
     * Description 根据键值进行加密
     * @param data 
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key) throws Exception {
        try
        {
        	byte[] bt = encrypt(data.getBytes(), key.getBytes());
        	String strs = new BASE64Encoder().encode(bt);
        	return strs;
        }
    	catch(Exception e)
    	{
    		throw new SecretException(e);
    	}
    }
 
    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data, String key) {
    	try
    	{
    		if (data == null)
                return null;
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] buf = decoder.decodeBuffer(data);
            byte[] bt = decrypt(buf,key.getBytes());
            return new String(bt);
    	}
    	catch(Exception e)
    	{
    		throw new SecretException(e);
    	}
    }
 
    /**
     * Description 根据键值进行加密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
     
     
    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
      
   /* *//** 
     * 转换密钥 
     *  
     * @param key   二进制密钥 
     * @return Key  密钥 
     * @throws Exception 
     *//*  
    private static Key toKey(byte[] key) throws Exception{  
        //实例化DES密钥规则  
        DESKeySpec dks = new DESKeySpec(key);  
        //实例化密钥工厂  
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_ALGORITHM);  
        //生成密钥  
        SecretKey  secretKey = skf.generateSecret(dks);  
        return secretKey;  
    }  
      
    *//** 
     * 加密 
     *  
     * @param data  待加密数据 
     * @param key   密钥 
     * @return byte[]   加密数据 
     * @throws Exception 
     *//*  
    public static byte[] encrypt(byte[] data,Key key) throws Exception{  
        return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);  
    }  
      
    *//** 
     * 加密 
     *  
     * @param data  待加密数据 
     * @param key   二进制密钥 
     * @return byte[]   加密数据 
     * @throws Exception 
     *//*  
    public static byte[] encrypt(byte[] data,byte[] key) throws Exception{  
        return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);  
    }  
      
      
    *//** 
     * 加密 
     *  
     * @param data  待加密数据 
     * @param key   二进制密钥 
     * @param cipherAlgorithm   加密算法/工作模式/填充方式 
     * @return byte[]   加密数据 
     * @throws Exception 
     *//*  
    public static byte[] encrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{  
        //还原密钥  
        Key k = toKey(key);  
        return encrypt(data, k, cipherAlgorithm);  
    }  
      
    *//** 
     * 加密 
     *  
     * @param data  待加密数据 
     * @param key   密钥 
     * @param cipherAlgorithm   加密算法/工作模式/填充方式 
     * @return byte[]   加密数据 
     * @throws Exception 
     *//*  
    public static byte[] encrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{  
        //实例化  
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);  
        //使用密钥初始化，设置为加密模式  
        cipher.init(Cipher.ENCRYPT_MODE, key);  
        //执行操作  
        return cipher.doFinal(data);  
    }  
      
      
      
    *//** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   二进制密钥 
     * @return byte[]   解密数据 
     * @throws Exception 
     *//*  
    public static byte[] decrypt(byte[] data,byte[] key) throws Exception{  
        return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);  
    }  
      
    *//** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   密钥 
     * @return byte[]   解密数据 
     * @throws Exception 
     *//*  
    public static byte[] decrypt(byte[] data,Key key) throws Exception{  
        return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);  
    }  
      
    *//** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   二进制密钥 
     * @param cipherAlgorithm   加密算法/工作模式/填充方式 
     * @return byte[]   解密数据 
     * @throws Exception 
     *//*  
    public static byte[] decrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{  
        //还原密钥  
        Key k = toKey(key);  
        return decrypt(data, k, cipherAlgorithm);  
    }  
  
    *//** 
     * 解密 
     *  
     * @param data  待解密数据 
     * @param key   密钥 
     * @param cipherAlgorithm   加密算法/工作模式/填充方式 
     * @return byte[]   解密数据 
     * @throws Exception 
     *//*  
    public static byte[] decrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{  
        //实例化  
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);  
        //使用密钥初始化，设置为解密模式  
        cipher.init(Cipher.DECRYPT_MODE, key);  
        //执行操作  
        return cipher.doFinal(data);  
    }  
      
    *//**
     * Description 根据键值进行加密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     *//*
    private static byte[] encrypt1(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES");
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
    
    
    private static String  showByteArray(byte[] data){  
        if(null == data){  
            return null;  
        }  
        StringBuilder sb = new StringBuilder("{");  
        for(byte b:data){  
            sb.append(b).append(",");  
        }  
        sb.deleteCharAt(sb.length()-1);  
        sb.append("}");  
        return sb.toString();  
    }  */
      
    public static void main(String[] args) throws Exception {  
       /* byte[] key = initSecretKey();  
//      byte[] key = "12345678".getBytes();  
        System.out.println("key："+ showByteArray(key));  
          
        Key k = toKey(key);  
          
        String data ="DES数据";  
        System.out.println("加密前数据: string:"+data);  
        System.out.println("加密前数据: byte[]:"+showByteArray(data.getBytes()));  
        System.out.println();  
        byte[] encryptData = encrypt(data.getBytes(), k);  
        System.out.println("加密后数据: byte[]:"+showByteArray(encryptData));  
        System.out.println("加密后数据: hexStr:"+Hex.encodeHexStr(encryptData));  
        System.out.println();  
        byte[] decryptData = decrypt(encryptData, k);  
        System.out.println("解密后数据: byte[]:"+showByteArray(decryptData));  
        System.out.println("解密后数据: string:"+new String(decryptData));  */
          
    } 
} 


// 附录php
//php 方法一 
//Php代码  收藏代码
//<?php  
//class DES1 {      
//    var $key;         
//    function    DES1($key) {          
//        $this->key = $key;         
//    }         
//    function encrypt($input) {        
//        $size = mcrypt_get_block_size('des', 'ecb');          
//        $input = $this->pkcs5_pad($input, $size);          
//        $key = $this->key;         
//        $td = mcrypt_module_open('des', '', 'ecb', '');       
//        $iv = @mcrypt_create_iv (mcrypt_enc_get_iv_size($td), MCRYPT_RAND);      
//        @mcrypt_generic_init($td, $key, $iv);         
//        $data = mcrypt_generic($td, $input);          
//        mcrypt_generic_deinit($td);      
//        mcrypt_module_close($td);         
//        $data = base64_encode($data);         
//        return $data;     
//    }         
//    function decrypt($encrypted) {        
//        $encrypted = base64_decode($encrypted);       
//        $key =$this->key;          
//        $td = mcrypt_module_open('des','','ecb','');   
//        //使用MCRYPT_DES算法,cbc模式                
//        $iv = @mcrypt_create_iv(mcrypt_enc_get_iv_size($td), MCRYPT_RAND);            
//        $ks = mcrypt_enc_get_key_size($td);               
//        @mcrypt_generic_init($td, $key, $iv);         
//        //初始处理                
//        $decrypted = mdecrypt_generic($td, $encrypted);         
//        //解密              
//        mcrypt_generic_deinit($td);         
//        //结束            
//        mcrypt_module_close($td);                 
//        $y=$this->pkcs5_unpad($decrypted);          
//        return $y;    
//    }         
//    function pkcs5_pad ($text, $blocksize) {          
//        $pad = $blocksize - (strlen($text) % $blocksize);         
//        return $text . str_repeat(chr($pad), $pad);   
//    }     
//    function pkcs5_unpad($text) {         
//        $pad = ord($text{strlen($text)-1});       
//        if ($pad > strlen($text))              
//            return false;         
//        if (strspn($text, chr($pad), strlen($text) - $pad) != $pad)               
//            return false;         
//        return substr($text, 0, -1 * $pad);   
//    }  
//}   
//        $key = "abcdefgh";  
//        $input = "a";  
//        $crypt = new DES1($key);  
//        echo "Encode:".$crypt->encrypt($input)."<br/>";  
//        echo "Decode:".$crypt->decrypt($crypt->encrypt($input));  
//?>   
//
//php 方法2 
//使用phpseclib中的DES 
//Php代码  收藏代码
//<?php  
//    include('DES.php');  
//  
//    $des = new Crypt_DES();  
//  
//    $des->setKey('abcdefgh');  
//    $plaintext = 'a';  
//    $jiami = base64_encode($des->encrypt($plaintext));  
//    echo "Encode:".$jiami."<br/>";  
//    echo "Decode:".$des->decrypt(base64_decode($jiami));  
//?>  
