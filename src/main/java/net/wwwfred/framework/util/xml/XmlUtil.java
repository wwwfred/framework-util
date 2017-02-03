package net.wwwfred.framework.util.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.wwwfred.framework.util.date.DatetimeFormat;
import net.wwwfred.framework.util.date.DatetimeFormatAnnotation;
import net.wwwfred.framework.util.date.DatetimeUtil;
import net.wwwfred.framework.util.reflect.AliasAnnotation;
import net.wwwfred.framework.util.reflect.ReflectUtil;

public class XmlUtil
{
    /** 元素内容属性 */
    public static String XML_ELEMENT_TEXT_CONTENT = "elementTextContent";
    
    /** 元素命名空间 */
    public static String XML_NAMESPACE = "xmlns";
    
	private static XmlParse xmlParse = new SaxXmlParse();
	
	/**
	 * 设置Xml文件解析器
	 * @author wangwwy
	 * createdDatetime 2014年9月9日 下午4:21:24
	 * @param xmlParse
	 */
	public static void setXmlParse(XmlParse xmlParse)
	{
		XmlUtil.xmlParse = xmlParse;
	}

	/**
	 * 获取Xml文件解析器
	 * @author wangwwy
	 * createdDatetime 2014年9月9日 下午4:21:39
	 * @return
	 */
	public static XmlParse getXmlParse()
	{
		return xmlParse;
	}
	
	/**
	 * 获取Xml文件的编码
	 * @author wangwwy
	 * createdDatetime 2014年9月9日 下午4:21:54
	 * @param byteArrayData
	 * @return
	 */
	public static Charset getXmlEncoding(byte[] byteArrayData)
	{
		return xmlParse.getXmlEncoding(byteArrayData);
	}
	
	/**
	 * 解析Xml文件的内容为XML对象
	 * @author wangwwy
	 * createdDatetime 2014年9月9日 下午4:22:07
	 * @param byteArrayData
	 * @param clazz
	 * @return
	 */
	public static <T> T toObject(byte[] byteArrayData, Class<T> clazz)
	{
		InputStream in = new ByteArrayInputStream(byteArrayData);
		T result = toObject(in, clazz);
		try
		{
			in.close();
		}
		catch(Exception e)
		{
			throw new XmlException(e);
		}
		return result;
	}

	/**
	 * 解析Xml文件的内容为XML对象
	 * @author wangwwy
	 * createdDatetime 2014年9月9日 下午4:22:07
	 * @param in
	 * @param clazz
	 * @return
	 */
	public static <T> T toObject(InputStream in, Class<T> clazz)
	{
		return xmlParse.toObject(in, clazz);
	}
	
	/**
	 * 解析类路径下的某个Xml资源文件为XML对象
	 * @author wangwwy
	 * createdDatetime 2014年9月9日 下午4:22:45
	 * @param resourceName
	 * @param clazz
	 * @return
	 */
	public static <T> T toObject(String resourceName, Class<T> clazz)
	{
		InputStream in = XmlUtil.class.getClassLoader().getResourceAsStream(resourceName);
		if(in==null||clazz==null)
			throw new XmlException("XmlUtil.toObject xmlInputStream or clazz is null,resourceName="+resourceName);
		
		T result;
		try
		{
			result = toObject(in, clazz);
		}
		catch(Exception e)
		{
			throw new XmlException(e);
		}
		finally
		{
			if(in!=null)
			{
				try {
					in.close();
				} catch (IOException e) {
					throw new XmlException("close XmlInputStream exception occured.");
				}
			}
		}
		return result;
	}

	private static XmlConstructor xmlConstuctor = new XmlConstructorImpl();//new SaxXmlConstructor();

	/**
	 * 设置XML文件生成器
	 * @author wangwwy
	 * createdDatetime 2014年9月9日 下午4:23:46
	 * @param xmlConstuctor
	 */
	public static void setXmlConstuctor(XmlConstructor xmlConstuctor)
	{
		XmlUtil.xmlConstuctor = xmlConstuctor;
	}

	/**
	 * 获取XML文件生成器
	 * @author wangwwy
	 * createdDatetime 2014年9月9日 下午4:24:16
	 * @return
	 */
	public static XmlConstructor getXmlConstuctor()
	{
		return xmlConstuctor;
	}

	/**
	 * 将对象解析为XML文件内容
	 * @author wangwwy
	 * createdDatetime 2014年9月9日 下午4:24:33
	 * @param obj
	 * @return
	 */
	public static String toXmlString(Object obj)
	{
		Charset charset = Charset.defaultCharset();
		return xmlConstuctor.toXmlString(obj,charset);
	}
	
	public static String toXmlString(Object obj,Charset charset)
	{
		return xmlConstuctor.toXmlString(obj,charset);
	}
	
	/**
	 * 格式化XML数据
	 * @author wangwwy
	 * createdDatetime 2014年8月15日 上午11:56:14
	 * @param xmlByteArray
	 * @return
	 */
	public static byte[] formatXml(byte[] xmlByteArray)
	{
		return SaxXmlParse.formatXml(xmlByteArray);
	}
	
	public static Xml parseObject(Object o,String name)
	{
		Xml result;
		
		if(o==null)
		{
			result = new XmlText("");
		}
		else if(o instanceof Date)
		{
			result = new XmlText(DatetimeUtil.longToDateTimeString(((Date)o).getTime(), Xml.XML_STRING_DATE_FORMAT));
		}
		else if(o instanceof Enum||o instanceof String || o instanceof Character)
		{
			result = new XmlText(o.toString());
		}
		else if(o instanceof Boolean || o instanceof Byte||
				o instanceof Integer || o instanceof Short || o instanceof Long || o instanceof Float || o instanceof Double)
		{
			result = new XmlText(o.toString());
		}
		else
		{
			Class<?> clazz = o.getClass();
			
			// element name
			Class<AliasAnnotation> aliasAnnotationClass = AliasAnnotation.class;
			if(clazz.isAnnotationPresent(aliasAnnotationClass))
			{
				name = clazz.getAnnotation(aliasAnnotationClass).value();
			}
			if(name==null||"".equals(name.trim()))
			{
				name = clazz.getSimpleName();
			}
			XmlElement element = new XmlElement(name);
			
			// get filed and getMethod map
			Map<Field, Method> fieldNameAndGetmethodMap = ReflectUtil.getFieldAndGetmethodMap(clazz);

			// set value to string
			Set<Entry<Field,Method>> entrySet = fieldNameAndGetmethodMap.entrySet();
			for (Entry<Field, Method> entry : entrySet)
			{
				Field field = entry.getKey();
				String fieldName = ReflectUtil.getFieldGetMethodAliasName(clazz,field);
				
				Object fieldValue = ReflectUtil.getMethodInvoke(o, fieldName);
				if(fieldValue instanceof Date)
				{
					DatetimeFormat datetimeFormat = Xml.XML_STRING_DATE_FORMAT;
					Class<DatetimeFormatAnnotation> datetimeFormatAnnotationClass = DatetimeFormatAnnotation.class;
					if(field.isAnnotationPresent(datetimeFormatAnnotationClass))
					{
						datetimeFormat = field.getAnnotation(datetimeFormatAnnotationClass).value();
					}
					fieldValue = DatetimeUtil.longToDateTimeString(((Date)fieldValue).getTime(), datetimeFormat);
				}

				if (fieldValue != null)
				{
					Class<XmlElementAnnotation> xmlElementAnnotationClass = XmlElementAnnotation.class;
					
					Class<?> fieldType = fieldValue.getClass();
					if(fieldType.isArray())
					{
						Object[] array = (Object[])fieldValue;
						for(Object one : array)
						{
							if(field.isAnnotationPresent(xmlElementAnnotationClass)&&ReflectUtil.isSimpleClazz(one.getClass()))
							{
								XmlElement childXmlElement = new XmlElement(fieldName);
								childXmlElement.childXml.add(new XmlText(one.toString()));
								element.childXml.add(childXmlElement);
							}
							else
							{
								element.childXml.add(parseObject(one,fieldName));
							}
						}
					}
					else if(Collection.class.isAssignableFrom(fieldType))
					{
						@SuppressWarnings("unchecked")
						Collection<Object> collection = (Collection<Object>)fieldValue;
						for(Object one : collection)
						{
							if(field.isAnnotationPresent(xmlElementAnnotationClass)&&ReflectUtil.isSimpleClazz(one.getClass()))
							{
								XmlElement childXmlElement = new XmlElement(fieldName);
								childXmlElement.childXml.add(new XmlText(one.toString()));
								element.childXml.add(childXmlElement);
							}
							else
							{
								element.childXml.add(parseObject(one,fieldName));
							}
						}
					}
					else if(ReflectUtil.isSimpleClazz(fieldType))
					{
						if(field.isAnnotationPresent(xmlElementAnnotationClass))
						{
							XmlElement childXmlElement = new XmlElement(fieldName);
							childXmlElement.childXml.add(new XmlText(fieldValue.toString()));
							element.childXml.add(childXmlElement);
						}
						else if(XML_ELEMENT_TEXT_CONTENT.equals(fieldName))
                        {
                            XmlText childXmlText = new XmlText(fieldValue.toString());
                            element.childXml.add(childXmlText);
                        }
						else
						{
							element.attributeMap.put(fieldName, fieldValue.toString());
						}
					}
					else
					{
						element.childXml.add(parseObject(fieldValue,fieldName));
					}
				}
			}
			result = element;
		}
		return result;
	}
	
	
	public static interface Xml
	{
		String toXmlString();
		
		DatetimeFormat XML_STRING_DATE_FORMAT = DatetimeFormat.STANDARED_DATE_TIME_FORMAT;
	}

	public static class XmlText implements Xml
	{
		protected String s;

		public XmlText(String s)
		{
			this.s = s;
		}

		@Override
		public String toXmlString()
		{
			return s;
		}
	}

	public static class XmlElement implements Xml
	{
		private static final String ELEMENT_NAME_START_QUOTE_START_TAG = "<";
		private static final String ELEMENT_NAME_START_QUOTE_END_TAG = ">";
		private static final String ELEMENT_NAME_END_QUOTE_START_TAG = "</";
		private static final String ELEMENT_NAME_END_QUOTE_END_TAG = "/>";
//		private static final String XML_TEXT_VALUE_SEPARATOR_TAG = "|";
		private static final String ATTRIBUTE_KEY_VALUE_SEPARATOR_TAG = "=";
		private static final String ATTRIBUTE_VALUE_QUOTE_TAG_DOUBLE = "\"";
		private static final String ATTRIBUTE_SEPARATOR_TAG = " ";
//		private static final String ATTRIBUTE_VALUE_QUOTE_TAG_SINGLE = "'";

		public XmlElement(String name)
		{
			this.name = name;
		}

		public XmlElement(String name, Map<String, String> attributeMap,
				List<Xml> childXml)
		{
			this.name = name;
			this.attributeMap = attributeMap;
			this.childXml = childXml;
		}

		protected String name;
		protected Map<String, String> attributeMap = new LinkedHashMap<String, String>();
		protected List<Xml> childXml = new ArrayList<Xml>();

		public String toXmlString()
		{
			String result;

			StringBuffer sb = new StringBuffer();

			if(childXml.isEmpty()&&attributeMap.isEmpty())
			{
				sb.append(ELEMENT_NAME_START_QUOTE_START_TAG).append(name).append(ELEMENT_NAME_END_QUOTE_END_TAG);
				result = sb.toString();
			}
			else
			{
				// append name start
				sb.append(ELEMENT_NAME_START_QUOTE_START_TAG).append(name);
			
				// append attribute
				Set<Entry<String, String>> entrySet = attributeMap.entrySet();
				for (Entry<String, String> entry : entrySet)
				{
					String key = entry.getKey();
					String value = entry.getValue();
					if(key!=null&&value!=null)
					{
//						value = StringUtil.convertStringQuoteTag(value);
						value = ATTRIBUTE_VALUE_QUOTE_TAG_DOUBLE + value + ATTRIBUTE_VALUE_QUOTE_TAG_DOUBLE;
				
						sb.append(ATTRIBUTE_SEPARATOR_TAG).append(key)
						.append(ATTRIBUTE_KEY_VALUE_SEPARATOR_TAG)
						.append(value);
					}
				}
				
				if(childXml.isEmpty())
				{
					sb.append(ELEMENT_NAME_END_QUOTE_END_TAG);
				}
				else
				{
					sb.append(ELEMENT_NAME_START_QUOTE_END_TAG);
			
					// append child element
					for(Xml xml : childXml)
					{
						sb.append(xml.toXmlString());
						
//						List<Xml> xmlList = entry.getValue();
//						int valueLength = 0;
//						for(Xml xml : xmlList)
//						{
//							if(xml instanceof XmlText)
//							{
//								if(valueLength>0)
//								{
//									sb.append(XML_TEXT_VALUE_SEPARATOR_TAG);
//								}
//								sb.append(xml.toXmlString());
//								valueLength++;
//							}
//							else if(xml instanceof XmlElement)
//							{
//								XmlElement childXmlElement = (XmlElement)xml;
//								childXmlElement.name = entry.getKey();
//								sb.append(childXmlElement.toXmlString());
//							}
//						}
					}
					
					sb.append(ELEMENT_NAME_END_QUOTE_START_TAG).append(name).append(ELEMENT_NAME_START_QUOTE_END_TAG);
				}
			}	

			result = sb.toString();
			return result;
		}
	}
	
	public static class XmlDocument
	{
		protected String xmlVersion = "1.0";
		protected String encoding = "UTF-8";
		
		private static final String defaultDtd = "[]";
		
		protected String dtdDeclare; // PUBLIC SYSTEM
		protected String dtdUrl;
		
		protected Xml xml;
		
		public XmlDocument(Xml xml)
		{
			this.xmlVersion = "1.0";
			this.encoding = "UTF-8";
			this.xml = xml;
		}

		public XmlDocument(String xmlVersion, String encoding,
				String dtdDeclare, String dtdUrl, Xml xml)
		{
			this.xmlVersion = xmlVersion==null?"1.0":xmlVersion;
			this.encoding = encoding==null?"UTF-8":encoding;
			this.dtdDeclare = dtdDeclare;
			this.dtdUrl = dtdUrl;
			this.xml = xml;
		}

		public String toXmlString()
		{
			String result;

			StringBuffer sb = new StringBuffer();

			// append head
			sb.append("<?xml version=\"").append(xmlVersion)
					.append("\" encoding=\"").append(encoding).append("\"?>");
			
			// append dtd
			if(xml instanceof XmlElement &&( dtdDeclare!=null||dtdUrl!=null))
			{
				sb.append("<!DOCTYPE " + ((XmlElement)xml).name + " ");
				if(dtdDeclare!=null)
				{
					if("".equals(dtdDeclare.trim()))
					{
						sb.append(defaultDtd);
					}
					else
					{
						sb.append(dtdDeclare);
					}
				}
				if(dtdUrl!=null)
				{
					sb.append(" \"").append(dtdUrl).append("\"");
				}
				sb.append(">");
			}
			
			sb.append(xml.toXmlString());
			
			result = sb.toString();
			return result;
		}
		
	}
	
}
