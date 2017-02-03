package net.wwwfred.framework.util.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.wwwfred.framework.util.log.LogUtil;
import net.wwwfred.framework.util.reflect.ReflectUtil;
import net.wwwfred.framework.util.string.StringUtil;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


public class SaxXmlParse implements XmlParse
{
	private static final String TAG = SaxXmlParse.class.getSimpleName();
	
	/**
	 * 格式化xml
	 * @author wangwwy
	 * createdDatetime 2014年8月15日 上午10:34:08
	 * @param byteArrayData
	 * @return
	 * @throws Exception
	 */
	public static byte[] formatXml(byte[] byteArrayData) {
		
		InputStream in = new ByteArrayInputStream(byteArrayData);
		Document doc;
		try
		{
			doc = new SAXReader().read(in);
		}
		catch(Exception e)
		{
			throw new XmlException("SaxReader.read xmlByteArray exception occured.",e);
		}
		finally
		{
			if(in!=null)
			{
				try {
					in.close();
				} catch (IOException e) {
					throw new XmlException("close xmlInputStream exception ocuured.", e);
				}
			}
		}
		
		OutputFormat formate=OutputFormat.createPrettyPrint();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{  
        	XMLWriter writer=new XMLWriter(baos,formate);
            writer.write(doc);  
        } catch (Exception e){  
           throw new XmlException("SaxXmlParse format xml exception occured.",e);
        } finally{  
           if(baos!=null)
           {
        	   try {
				baos.close();
			} catch (IOException e) {
				throw new XmlException("close xmlOutputStream exception occured.",e);
			}
           }
        }  
        return baos.toByteArray();
    } 
	
	@SuppressWarnings("unchecked")
	public <T> T toObject(InputStream in, Class<T> clazz)
	{
		T result = null;

		if(in==null||clazz==null)
			throw new XmlException("SaxXmlParse.toObject inputStream and clazz should not be null.");
		
		in = new BufferedInputStream(in);
		Object resultObject = null;
		try
		{
			Document document = new SAXReader().read(in);
			LogUtil.d(TAG, "SaxXmlParse xml inputStream encoding="+document.getXMLEncoding());
			Element rootElement = document.getRootElement();
			
			resultObject = parse(rootElement, clazz);
			
			if(resultObject!=null&&resultObject.getClass().equals(clazz))
			{
				result = (T)resultObject;
			}
			
		} catch (Exception e)
		{
			throw new XmlException("SaxXmlParse.parse xmlInputStream clazz=" + clazz + " to Object exception occured,"+e.getMessage(),e);
		}
		return result;
	}
	
	@Override
	public Charset getXmlEncoding(byte[] byteArray)
	{
		BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(byteArray));
		try
		{
			Document document = new SAXReader().read(in);
			return Charset.forName(document.getXMLEncoding());
		}
		catch(Exception e)
		{
			throw new XmlException("SaxXmlParse.parse xmlEncoding exception occured",e);
		}
		finally
		{
			closeInputStream(in);
		}
	}
	
	public <T> T toObject(byte[] byteArray, Class<T> clazz)
	{
		T result = null;

		if(byteArray==null||clazz==null)
			throw new XmlException("SaxXmlParse.toObject byteArray and clazz should not be null.");
		
		BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(byteArray));
		try
		{
			result = toObject(in, clazz);
			
		} catch (Exception e)
		{
			throw new XmlException("SaxXmlParse.parse xmlInputStream clazz=" + clazz + " to Object exception occured",e);
		}
		finally
		{
			closeInputStream(in);
		}
		
		return result;
	}
	
	private static void closeInputStream(InputStream in)
	{
		try
		{
			if(in!=null)
			{
				in.close();
			}
		}
		catch(Exception e)
		{
			throw new XmlException("closeInputStream exception occured.");
		}
	}
	
	private static Object parse(Element rootElement, Class<?> clazz)
	{
		Object obj = null;
		if(ReflectUtil.isSimpleClazz(clazz))
		{
			obj = rootElement.getText().trim();
		}
		else if(ReflectUtil.isModel(null, clazz))
		{
			obj = ReflectUtil.newModel(clazz);
		}
		else
		{
			throw new XmlException("parse illegal, rootElement="+rootElement+",clazz="+clazz);
		}
		Map<String, Method> fieldAndSetmethodMap = new HashMap<String, Method>(ReflectUtil.getFieldNameAndSetmethodMap(clazz));
		
		parseElement(rootElement, obj, fieldAndSetmethodMap);
		
		return obj;
	}
	
	private static void parseElement(Element element, Object obj,Map<String, Method> fieldAndSetmethodMap)
	{
		parseAttribute(element, obj, fieldAndSetmethodMap);
		
		Set<String> parsedFieldNameSet = new HashSet<String>();
		
		Set<Entry<String, Method>> entrySet = fieldAndSetmethodMap.entrySet();
		for(Entry<String, Method> entry : entrySet)
		{
			String fieldName = entry.getKey();
			Method setMethod = entry.getValue();
			
			if(fieldName!=null&&setMethod!=null)
			{
				// element text value
				if(fieldName.equalsIgnoreCase(XmlUtil.XML_ELEMENT_TEXT_CONTENT))
				{
					String elementTextContentValue = element.getText();
					ReflectUtil.setMethodInvoke(obj, fieldName, elementTextContentValue);
				}
				else
				{
					// children element
					List<?> childrenElement = element.elements(fieldName);
					if(childrenElement!=null)
					{
						int childrenLength = childrenElement.size();
						if(childrenLength>0)
						{
							Class<?>[] paramClazzArray = setMethod.getParameterTypes();
							if(paramClazzArray!=null&&paramClazzArray.length==1)
							{
								Class<?> fieldClazz = paramClazzArray[0];
								
								Object methodParam = null;
								if(fieldClazz!=null)
								{
									if(fieldClazz.isArray())
									{
										Class<?> arrayOneClazz = fieldClazz.getComponentType();
										if(arrayOneClazz!=null)
										{
											Object[] valueArray = (Object[])Array.newInstance(arrayOneClazz, childrenLength);
											for(int i=0; i<childrenLength; i++)
											{
												Object childNode = childrenElement.get(i);
												if(childNode instanceof Element)
												{
													Element childElement = (Element)childNode;
													valueArray[i] = parse(childElement,arrayOneClazz);
												}
											}
											methodParam = valueArray;
										}
									}
									else if(List.class.isAssignableFrom(fieldClazz))
									{
										Class<?> arrayOneClazz = getGenericTypeClass(setMethod);
										if(arrayOneClazz!=null)
										{
											List<Object> valueArray = new LinkedList<Object>();
											for(int i=0; i<childrenLength; i++)
											{
												Object childNode = childrenElement.get(i);
												
												if(childNode instanceof Element)
												{
													Element childElement = (Element)childNode;
													valueArray.add(parse(childElement,arrayOneClazz));
												}
											}
											methodParam = valueArray;
										}
									}
									else if(Set.class.isAssignableFrom(fieldClazz))
									{
										Class<?> arrayOneClazz = getGenericTypeClass(setMethod);
										if(arrayOneClazz!=null)
										{
											Set<Object> valueArray = new HashSet<Object>();
											for(int i=0; i<childrenLength; i++)
											{
												Object childNode = childrenElement.get(i);
												if(childNode instanceof Element)
												{
													Element childElement = (Element)childNode;
													valueArray.add(parse(childElement,arrayOneClazz));
												}
											}
											methodParam = valueArray;
										}
									}
									else if(ReflectUtil.isSimpleClazz(fieldClazz))
									{
										Object childNode = childrenElement.get(0);
										if(childNode instanceof Element)
										{
											String valueString = ((Element)childNode).getText().trim();
											methodParam = StringUtil.convertStringValue(valueString, fieldClazz);
										}
									}
									else if(Object.class.isAssignableFrom(fieldClazz))
									{
										Object childNode = childrenElement.get(0);
										if(childNode instanceof Element)
										{
											Element childElement = (Element)childNode;
											methodParam = parse(childElement, fieldClazz);
										}
									}
								}
								
								if(methodParam!=null)
								{
									try
									{
										setMethod.invoke(obj,methodParam);

										LogUtil.d(TAG, "success parse element " + fieldName + " value=" + methodParam.toString());
										
										parsedFieldNameSet.add(fieldName);
									} catch (Exception e)
									{
										LogUtil.e(TAG,"failure parse element " + fieldName + "value=" + methodParam.toString() + "exception occured.",e);
										throw new XmlException("parseElement illegal,setMethod="+setMethod+",object="+obj+",valueObject="+methodParam,e);
									} 
								}
							}
						}
					}
				}
			}
		}
		
		for(String parsedFieldName : parsedFieldNameSet)
		{
			fieldAndSetmethodMap.remove(parsedFieldName);
		}
	}
	
	private static void parseAttribute(Element element,Object obj,Map<String, Method> fieldAndSetmethodMap)
	{
		Set<String> parsedFieldNameSet = new HashSet<String>();
		
		Set<Entry<String, Method>> entrySet = fieldAndSetmethodMap.entrySet();
		for(Entry<String, Method> entry : entrySet)
		{
			String fieldName = entry.getKey();
			Method setMethod = entry.getValue();
			
			if(fieldName!=null&&setMethod!=null)
			{
				Class<?>[] paramClazzArray = setMethod.getParameterTypes();
				if(paramClazzArray!=null&&paramClazzArray.length==1)
				{
					Class<?> fieldClazz = paramClazzArray[0];
					String attributeValue = element.attributeValue(fieldName);
					if(attributeValue!=null)
					{
						Object valueObject = StringUtil.convertStringValue(attributeValue.trim(), fieldClazz);
						if(valueObject!=null)
						{
							try
							{
								setMethod.invoke(obj,valueObject);
								
								LogUtil.d(TAG, "success parse attribute " + fieldName + " value=" + valueObject.toString());
								
								parsedFieldNameSet.add(fieldName);
							} catch (Exception e)
							{
								LogUtil.e(TAG,"failure parse attribute " + fieldName + " value=" + valueObject.toString() + "exception occured.",e);
								throw new XmlException("parseAttribute illegal,setMethod="+setMethod+",object="+obj+",valueObject="+valueObject,e);
							} 
						}
					}
				}
			}
		}
		
		for(String parsedFieldName : parsedFieldNameSet)
		{
			fieldAndSetmethodMap.remove(parsedFieldName);
		}
	}
	
	private static Class<?> getGenericTypeClass(Method setMethod)
	{
		Class<?> result = null;
		
		Type[] typeArray = setMethod.getGenericParameterTypes();
		if(typeArray!=null&&typeArray.length>0)
		{
			Type genericType = typeArray[0];
			if(genericType instanceof ParameterizedType)
			{
				ParameterizedType parameterizedType = (ParameterizedType)genericType;
				Type[] actualTypeArray = parameterizedType.getActualTypeArguments();
				if(actualTypeArray!=null&&actualTypeArray.length>0)
				{
					result = (Class<?>) actualTypeArray[0];
				}
			}
		}
		
		return result;
	}
}
