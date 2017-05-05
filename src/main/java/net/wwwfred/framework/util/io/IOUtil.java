package net.wwwfred.framework.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

import javax.activation.MimetypesFileTypeMap;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import net.wwwfred.framework.util.code.CodeUtil;
import net.wwwfred.framework.util.properties.PropertiesUtil;
import net.wwwfred.framework.util.string.StringUtil;

/**
 * io 处理工具类
 * @author wangwenwu
 *
 */
public class IOUtil
{
	/**
	 * 证书信任根据主机名处理逻辑
	 * 2014年10月13日  下午3:57:00
	 * @param hostIp  证书字符串数组
	 */
	public static void httsVerifier(String... hostIp)
	{
		if(hostIp==null)
		{
			throw new IORuntimeException("httsVerifier parameter illegal,hostIp==null");
		}
		final String[] finalHostArray = hostIp.clone();
		HostnameVerifier hv = new HostnameVerifier() {  
	        public boolean verify(String urlHostName, SSLSession session) {  
//	            System.out.println("Warning: URL Host: " + urlHostName + " vs. "  
//	                               + session.getPeerHost());  
	        	for(String host : finalHostArray)
	        	{
	        		if(urlHostName.equals(host))
	        			return true;
	        	}
	            return false;  
	        }  
	    };
	    try
	    {
	    	trustAllHttpsCertificates();
	    }
	    catch(Exception e)
	    {
	    	throw new IORuntimeException("httpsVerifier trustAllHttpsCertificates illegal,"+e.getMessage(),e);
	    }
	    HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}
	
	
    
    /**
     * https访问证书忽略证书验证
     * @throws Exception
     */
	private static void trustAllHttpsCertificates() throws Exception {
		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
		javax.net.ssl.TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
				.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
				.getSocketFactory());
	}

	public static class miTM implements javax.net.ssl.TrustManager,
			javax.net.ssl.X509TrustManager {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(
				java.security.cert.X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType)
				throws java.security.cert.CertificateException {
			return;
		}
	}
	
	/**
	 * 判断本地文件是否存在
	 * createdDatetime 2014年9月9日 下午3:55:38
	 * @param filePath  本地文件路径
	 * @param fileName	本地文件名称
	 * @return boolean	返回 true 或 false
	 */
	public static boolean isLocalExist(String filePath, String fileName)
	{
		if (fileName == null)
		{
			// LogUtil.w("IOUtil",
			// "IOUtil.readLocal fileName should not be null", new
			// NullPointerException("fileName"));
			return false;
		}

		File f;
		if (filePath != null)
		{
			File fp = new File(filePath);
			if (!fp.exists() || !fp.isDirectory())
				return false;
			f = new File(fp, fileName);
		} else
		{
			f = new File(fileName);
		}

		if (f.exists() && f.isFile())
			return true;
		else
			return false;
	}

	/**
	 * 创建本地文件
	 * createdDatetime 2014年9月9日 下午3:55:56
	 * @param filePath	文件路径
	 * @param fileName	文件名称	
	 * @return File	创建的文件对象
	 */
	public static File createLocalFile(String filePath, String fileName)
	{
		if (fileName == null)
		{
			// LogUtil.w("IOUtil","IOUtil.writeLocal fileName should not be null.",new
			// NullPointerException("fileName"));
			return null;
		}

		File f;
		if (filePath == null)
		{
			f = new File(fileName);
		} else
		{
//			File fp = new File(filePath);
//			if (!fp.exists() || !fp.isDirectory())
//			{
//				fp.mkdirs();
//			}
			f = new File(filePath, fileName);
		}
		if (!f.exists() || !f.isFile())
		{
			try
			{
				String parent = f.getParent();
				if(parent!=null&&!"".equals(parent.trim()))
				{
					File fp = new File(parent);
					if (!fp.exists() || !fp.isDirectory())
					{
						fp.mkdirs();
					}
				}
				f.createNewFile();
			} catch (IOException e)
			{
				throw new IORuntimeException("IOUtil.writeLocal filePath="
						+ filePath + ", fileName=" + fileName
						+ " IOException occured.",e);
			}
		}

		return f;
	}

	/**
	 * 读取本地文件
	 * createdDatetime 2014年9月9日 下午3:56:18
	 * @param filePath	读取文件的路径
	 * @param fileName	读取文件的名称
	 * @return InputStream	输入流对象
	 */
	public static InputStream readLocal(String filePath, String fileName)
	{
		InputStream in = null;
		try
		{
			if (filePath == null)
			{
				in = new FileInputStream(new File(fileName));
			} else
			{
				in = new FileInputStream(new File(filePath, fileName));
			}
		} catch (FileNotFoundException e)
		{
			throw new IORuntimeException("IOUtil.readLocal filePath="
					+ filePath + "fileName=" + fileName + " is not exist.",e);
		}

		return in;
	}
	
	/**
	 * 读取本地文件的数据
	 * createdDatetime 2014年9月9日 下午3:56:44
	 * @param filePath	读取文件的路径
	 * @param fileName	读取文件的名称
	 * @return byte[]	返回本地文件的数据，字节数组
	 */
	public static byte[] readLoacalData(String filePath, String fileName)
	{
		byte[] result;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedInputStream bis = new BufferedInputStream(readLocal(filePath, fileName));
		int data;
		while(true)
		{
			try
			{
				data = bis.read();
			} catch (IOException e)
			{
				throw new IORuntimeException("IOutil.readLocalData IOException occured.",e);
			}
			
			if(data==-1)
				break;
			baos.write(data);
		}
		closeInputStream(bis);
		closeOutputStream(baos);	
		
		result = baos.toByteArray();
		return result;
	}

	/**
	 * 读取本地配置Properties文件
	 * createdDatetime 2014年9月9日 下午3:57:02
	 * @param filePath	读取文件的路径
	 * @param fileName	读取文件的名称
	 * @return Properties	properties文件对象
	 */
	public static Properties readLocalProperties(String filePath,
			String fileName)
	{
		Properties properties = new Properties();

		InputStream in = readLocal(filePath, fileName);
		if (in != null)
		{
			try
			{
				properties.load(readLocal(filePath, fileName));
			} catch (IOException e)
			{
				throw new IORuntimeException("IOUtil.readLocalProperties filePath="
						+ filePath + "fileName=" + fileName
						+ " IOException occured.",e);
			} finally
			{
				closeInputStream(in);
			}
		}
		return properties;
	}

	/**
	 * 获取本地文件的输出流
	 * createdDatetime 2014年9月9日 下午3:57:36
	 * @param filePath	文件路径
	 * @param fileName	文件名称	
	 * @return	OutputStream	输出流对象
	 */
	public static OutputStream writeLocal(String filePath, String fileName)
	{
		OutputStream out = new ByteArrayOutputStream();

		File f = createLocalFile(filePath, fileName);
			try
			{
				out = new FileOutputStream(f);
			} catch (FileNotFoundException e)
			{
				throw new IORuntimeException("IOUtil.writeLocal filePath=" + filePath
						+ ", fileName=" + fileName + " file not exist.",e);
			}
		return out;
	}
	
	/**
	 * 更新本地配置properties文件的部分属性
	 * createdDatetime 2014年9月9日 下午3:57:56
	 * @param properties	properties对象
	 * @param propertyKeyValueMap	需要更改的数据，map.key == properties.key
	 * @param filePath	 properties文件路径
	 * @param fileName	 properties文件名称
	 */
	public static void updateLocalProperties(Properties properties, Map<String, String> propertyKeyValueMap, String filePath, String fileName)
	{
		for(Entry<String, String> entry : propertyKeyValueMap.entrySet())
		{
			properties.setProperty(entry.getKey(), entry.getValue());
		}
		BufferedOutputStream bos = new BufferedOutputStream(writeLocal(filePath, fileName));
		try
		{
			properties.store(bos, "update properties keySet="+propertyKeyValueMap.keySet());
		} catch (IOException e)
		{
			throw new IORuntimeException("IOUtil.writeLocalProperties IOException occured.",e);
		}
		finally
		{
			closeOutputStream(bos);
		}
	}
	
	/**
	 * 往本地文件中写入数据
	 * createdDatetime 2014年9月9日 下午3:58:43
	 * @param data		写入本地文件的数据，字节数组
	 * @param filePath	本地文件路径
	 * @param fileName	本地文件名称
	 * @return fileAbsolutePath
	 */
	public static String writeLocalData(byte[] data, String filePath, String fileName)
	{
	    File f = createLocalFile(filePath, fileName);
		BufferedOutputStream bos = new BufferedOutputStream(writeLocal(
				filePath, fileName));
		try
		{
			bos.write(data);
		} catch (IOException e)
		{
			throw new IORuntimeException(
					"IOUtil.writeLocal IOException occured.",e);
		}
		finally
		{
			closeOutputStream(bos);
		}
		return f.getAbsolutePath();
	}
	
	/**
	 * 从输入流中读取数据
	 * createdDatetime 2014年9月9日 下午3:59:06
	 * @param in	输入流对象
	 * @return  byte[]  返回读取的数据，字节数组
	 */
	public static byte[] getByteArrayFromInputStream(InputStream in)
	{
		if(in==null)
			throw new IORuntimeException("IOUtil.getByteArrayFromInputStream parameter is null");
		
		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int data;
		try
		{
			while(true)
			{
				data = bis.read();
				if(data==-1)
					break;
				baos.write(data);
			}
		}
		catch(IOException e)
		{
			throw new IORuntimeException("IOUtil.readByteArrayFromInputStream IOException occured.",e);
		}
		finally
		{
			closeInputStream(bis);
			closeOutputStream(baos);
		}
		return baos.toByteArray();
	}
	
    public static byte[] getByteArrayFromHttpPostFileData(String httpUrl,Map<String, List<byte[]>> fileMap, Map<String, List<String>> fileNameMap, Map<String, String[]> fieldMap, Object[][] requestHeaderProperties)
    {
        // 定义HTTP post 文件上传请求数据分隔线
        String httpPostBoundary = "---------7d4a6d158c9";
        httpPostBoundary = "----MyFormBoundarySMFEtUYQG6r5B920";
        // 定义HTTP post 文件上传请求头部contentType字段名 
        String contentTypeName = "Content-Type";
        // 定义HTTP post 文件上传请求头部contentType字段值 
        String contentTypeValue = "multipart/form-data; boundary=" + httpPostBoundary;
        // 定义HTTP post 文件上传最后一行字符串数据内容
        String endStringData = "\r\n--" + httpPostBoundary + "--\r\n";
  
        List<Byte> postData = new ArrayList<Byte>();
        
        if(!CodeUtil.isEmpty(fieldMap))
        {
            Set<Entry<String, String[]>> fieldEntrySet = fieldMap.entrySet();
            for (Entry<String, String[]> entry : fieldEntrySet) {
                String name = entry.getKey();
                String[] fieldArray = entry.getValue();
                if(!CodeUtil.isEmpty(new Object[]{fieldArray}))
                {
                    for (String field : fieldArray) {
                        if(field!=null)
                        {
                            addByteArray(postData,"--".getBytes());
                            addByteArray(postData, httpPostBoundary.getBytes());
                            addByteArray(postData, "\r\n".getBytes());
                            addByteArray(postData, ("Content-Disposition: form-data;name=\""+name + "\"\r\n\r\n").getBytes());
                            //TODO cn field encoding
                            addByteArray(postData, field.getBytes());
                            //多个字段时，两个字段间加入这个 
                            addByteArray(postData, "\r\n".getBytes());
                        }
                    }
                }
            }
        }
        
        if(!CodeUtil.isEmpty(fileMap))
        {
            Set<Entry<String, List<byte[]>>> fileEntrySet = fileMap.entrySet();
            for (Entry<String, List<byte[]>> entry : fileEntrySet) {
                String name = entry.getKey();
                List<byte[]> fileArray = entry.getValue();
                List<String> fileNameArray = fileNameMap.get(name);
                if(!CodeUtil.isEmpty(new Object[]{fileArray}))
                {
                    int length = fileArray.size();
                    for (int i=0; i<length; i++) {
                        
                        String fileName = fileNameArray.get(i);
                        String contentType = getContentTypeByFileName(fileName);
                        
                        addByteArray(postData,"--".getBytes());
                        addByteArray(postData, httpPostBoundary.getBytes());
                        addByteArray(postData, "\r\n".getBytes());
                        addByteArray(postData, ("Content-Disposition: form-data;name=\""+name+"\";filename=\""+ fileNameArray.get(i) + "\"\r\n").getBytes());
                        addByteArray(postData, ("Content-Type:" + contentType + "\r\n\r\n").getBytes());
                              
                        InputStream in = new ByteArrayInputStream(fileArray.get(i));
                        byte[] fileData = IOUtil.getByteArrayFromInputStream(in);
                        closeInputStream(in);
                        addByteArray(postData,fileData);
                        //多个文件时，两个文件之间加入这个  
                        addByteArray(postData, "\r\n".getBytes());
                    }
                }
            }
        }
        
        addByteArray(postData, endStringData.getBytes());
        
        if(requestHeaderProperties==null)
        {
            requestHeaderProperties = new Object[][]{{contentTypeName,contentTypeValue}};
        }
        else
        {
            int length = requestHeaderProperties.length;
            Object[][] newRequestHeaderProperties = new Object[length+1][];
            System.arraycopy(requestHeaderProperties, 0, newRequestHeaderProperties, 0, length);
            newRequestHeaderProperties[length] = new Object[]{contentTypeName,contentTypeValue};
            requestHeaderProperties = newRequestHeaderProperties;
        }
        
        // postData
        int length = postData.size();
        byte[] postDataArray = new byte[length];
        for (int i = 0; i < length; i++) {
            postDataArray[i] = postData.get(i);
        }
        return getByteArrayFromHttpUrl(httpUrl, postDataArray, requestHeaderProperties);
    }
    
    private static void addByteArray(List<Byte> postData, byte[] byteArray) {
        for (byte b : byteArray) {
            postData.add(b);
        }
    }



    /** 根据文件名称获取文件的类型 */
    private static String getContentTypeByFileName(String fileName) {
        // TODO Auto-generated method stub
        String contentType = new MimetypesFileTypeMap().getContentType(fileName);
        if (fileName.endsWith(".png")) {  
            contentType = "image/png";  
        }  
        if (CodeUtil.isEmpty(contentType)) {  
            contentType = "application/octet-stream";  
        }  
        return contentType;
    }
	
	/**
	 * 从HttpURLConnection中获取请求返回数据
	 * createdDatetime 2014年9月9日 上午11:12:23
	 * @param url	
	 * @param requestData
	 * @param rquestHeaderProperties
	 * @return byte[] 返回请求的数据，字节数组
	 */
	public static byte[] getByteArrayFromHttpUrl(String url, byte[] postData, Object[][] requestHeaderProperties)
	{
	    String configFileName = "config.properties";
	    int readTimeout = StringUtil.getValueFromString(PropertiesUtil.getValue(configFileName, "get_byte_array_from_input_stream_read_timeout"),Integer.class,10*60*1000);
	    
		// parameter illegal check
		if(requestHeaderProperties!=null)
		{
			for(Object[] requestProperty : requestHeaderProperties)
			{
				if(requestProperty==null||(requestProperty.length!=0&&(requestProperty.length!=2||!(requestProperty[0] instanceof String)||!(requestProperty[1] instanceof String))))
				{
					throw new IORuntimeException("getByteArrayFromHttpUrl requestProperpties illegal");
				}
			}
		}
		
		// if URL is HTTPS protocol must verifyHttps
		if(url.startsWith("https://"))
		{
			int start = url.indexOf("https://")+"https://".length();
			int end = url.indexOf("/", start);
			String host = end>0?url.substring(start,end):url.substring(start);
			httsVerifier(host);
		}
		
		// URL connection
		HttpURLConnection con;
		try {
			URL unitResourceLocation = new URL(url);
			con = (HttpURLConnection) unitResourceLocation.openConnection();
		} catch (Exception e) {
			throw new IORuntimeException("new HttpURLConnection from url="+url + ", exception occured " + e.getMessage());
		}
		
		// request Property
		if(requestHeaderProperties!=null)
		{
			for(Object[] requestProperty : requestHeaderProperties)
			{
				if(requestProperty!=null&&requestProperty.length==2)
				{
					con.setRequestProperty(requestProperty[0].toString(), requestProperty[1].toString());
				}
			}
		}
		
		// request body
		if(postData!=null&&postData.length!=0)
		{
			try {
				con.setRequestProperty("Content-Length", ""+postData.length);
				con.setRequestMethod("POST");
				con.setReadTimeout(readTimeout);
			} catch (ProtocolException e) {
				throw new IORuntimeException("Http urlConnection set requestMethod POST exception occured "+ e.getMessage());
			}
			con.setDoOutput(true);
			BufferedOutputStream bos = null;
			try {
				bos = new BufferedOutputStream(con.getOutputStream());
				bos.write(postData);
			} catch (IOException e) {
				throw new IORuntimeException("Http post send data to url="+url + ", IOException occured " + e.getMessage());
			}
			finally
			{
				closeOutputStream(bos);
			}
		}
		
		byte[] result;
		InputStream in = null;
		try {
			in = con.getInputStream();
			result = IOUtil.getByteArrayFromInputStream(in);
		} catch (IOException e) {
			throw new IORuntimeException("Http urlConnection getInputStream IOException occured " + e.getMessage());
		}
		finally
		{
			closeInputStream(in);
		}
		
		return result;
	}
	
	   /** 用GZIP压缩数据 */
    public static byte[] gzipCompress(byte[] output)
    {
        ByteArrayOutputStream bais = null;
        GZIPOutputStream gzos = null;
        try {
            bais = new ByteArrayOutputStream();
            gzos = new GZIPOutputStream(bais);
            gzos.write(output);
            gzos.flush();
            return bais.toByteArray();
        } catch (Exception e) {
            throw new IORuntimeException("gzip compress outputStream illegal",e);
        }
        finally
        {
            closeOutputStream(gzos);
        }
    }
    
    /** 获取被GZIP压缩过的原始数据流 */
    public static byte[] gzipDecompress(byte[] input)
    {
        GZIPInputStream gzin = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(input);
            gzin = new GZIPInputStream(bais);
            return IOUtil.getByteArrayFromInputStream(gzin);
        } catch (Exception e) {
            throw new IORuntimeException("gzip decompress inputStream is not gzipInputStream",e);
        }
        finally
        {
            if(gzin!=null)
            {
                closeInputStream(gzin);
            }
        }
    }
	
	/**
	 * 对数据进行Java zip 压缩
	 * createdDatetime 2014年9月9日 下午3:59:34
	 * @param input  需压缩字节数组
	 * @return byte[] 返回压缩后的数据，字节数组
	 */
	public static byte[] compressBytes(byte input[])
	{
		Deflater compresser = new Deflater();
		int cachesize = 1024;
		compresser.reset();
		compresser.setInput(input);
		compresser.finish();
		byte output[] = new byte[0];
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
		try
		{
			byte[] buf = new byte[cachesize];
			int got;
			while (!compresser.finished())
			{
				got = compresser.deflate(buf);
				o.write(buf, 0, got);
			}
			output = o.toByteArray();
		} finally
		{
			closeOutputStream(o);
		}
		return output;
	}

	/**
	 * 对Java zip 压缩的数据进行解压
	 * createdDatetime 2014年9月9日 下午4:00:11
	 * @param input		需解压字节数组
	 * @return byte[] 返回解压后的数据，字节数组
	 */
	public static byte[] decompressBytes(byte input[])
	{
		Inflater decompresser = new Inflater();
		int cachesize = 1024;
		byte output[] = new byte[0];
		decompresser.reset();
		decompresser.setInput(input);
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
		try
		{
			byte[] buf = new byte[cachesize];
			int got;
			while (!decompresser.finished())
			{
				got = decompresser.inflate(buf);
				o.write(buf, 0, got);
			}
			output = o.toByteArray();
		} catch (DataFormatException e)
		{
			throw new IORuntimeException("IOUtil.decompressBytes dataFormatException occured.",e);
		} finally
		{
			closeOutputStream(o);
		}
		return output;
	}
	
	/**
	 * 关闭InputStream
	 * createdDatetime 2014年9月11日 下午8:14:04
	 * @param in 输入流对象
	 */
	public static void closeInputStream(InputStream... in)
	{
		if(in!=null)
		{
			try {
			    for (InputStream one : in) {
			        if(one!=null)
			        {
			            one.close();
			        }
                }
			} catch (IOException e) {
				throw new IORuntimeException("close inputStream exception occured.");
			}
		}
	}
	
	/**
	 * 关闭OutputStream
	 * createdDatetime 2014年9月11日 下午8:14:18
	 * @param out 输出流对象
	 */
	public static void closeOutputStream(OutputStream... out)
	{
		if(out!=null)
		{
			try {
			    for (OutputStream one : out) {
			        if(one!=null)
			        {
			            one.close();
			        }
			    }
			} catch (IOException e) {
				throw new IORuntimeException("close outputStream exception occured.");
			}
		}
	}
	
}
