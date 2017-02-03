package net.wwwfred.framework.util.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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

import javax.xml.parsers.DocumentBuilderFactory;

import net.wwwfred.framework.util.log.LogUtil;
import net.wwwfred.framework.util.reflect.ReflectUtil;
import net.wwwfred.framework.util.string.StringUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DomXmlParse implements XmlParse
{
	private static final String TAG = DomXmlParse.class.getSimpleName();
	
	@SuppressWarnings("unchecked")
	public <T> T toObject(InputStream in, Class<T> clazz)
	{
		T result = null;
		
		if(in==null||clazz==null)
			throw new XmlException("DomXmlParse.toObject inputStream and clazz should not be null.");
		
		in = new BufferedInputStream(in);
		Object resultObject = null;
		try
		{
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			LogUtil.d(TAG, "DomXmlParse xml inputStream encoding="+document.getXmlEncoding());
			Element rootElement = document.getDocumentElement();
			resultObject = parse(rootElement, clazz);
			
			if(resultObject!=null&&resultObject.getClass().equals(clazz))
			{
				result = (T)resultObject;
			}
			
		} catch (Exception e)
		{
			throw new XmlException("DomXmlParse.parse xmlInputStream clazz=" + clazz + " to Object exception occured",e);
		}
		return result;
	}
	
	@Override
	public Charset getXmlEncoding(byte[] byteArray)
	{
		BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(byteArray));
		try
		{
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			return Charset.forName(document.getXmlEncoding());
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
			throw new XmlException("DomXmlParse.toObject byteArray and clazz should not be null.");
		
		BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(byteArray));
		try
		{
			result = toObject(in, clazz);
			
		} catch (Exception e)
		{
			throw new XmlException("DomXmlParse.parse xmlInputStream clazz=" + clazz + " to Object exception occured",e);
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
			obj = rootElement.getTextContent().trim();
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
		
		Set<String> parsedFieldSet = new HashSet<String>();
		
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
					String elementTextContentValue = element.getTextContent();
					ReflectUtil.setMethodInvoke(obj, fieldName, elementTextContentValue);
				}
				else
				{
					NodeList childrenElement = element.getElementsByTagName(fieldName);
					if(childrenElement!=null)
					{
						int childrenLength = childrenElement.getLength();
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
												Node childNode = childrenElement.item(i);
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
												Node childNode = childrenElement.item(i);
												
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
												Node childNode = childrenElement.item(i);
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
										Node childNode = childrenElement.item(0);
										String valueString = childNode.getFirstChild().getNodeValue().trim();
										methodParam = StringUtil.convertStringValue(valueString, fieldClazz);
									}
									else if(Object.class.isAssignableFrom(fieldClazz))
									{
										Node childNode = childrenElement.item(0);
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
										
										parsedFieldSet.add(fieldName);
									} catch (Exception e)
									{
										LogUtil.e(TAG,"failure parse element " + fieldName + "value=" + methodParam.toString() + "exception occured.",e);
										throw new XmlException(e);
									} 
								}
							}
						}
					}
				}
			}
		}
		
		for(String parsedField : parsedFieldSet)
		{
			fieldAndSetmethodMap.remove(parsedField);
		}
	}
	
	private static void parseAttribute(Element element,Object obj,Map<String, Method> fieldAndSetmethodMap)
	{
		Set<String> parsedFieldSet = new HashSet<String>();
		
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
					Node attributeNode = element.getAttributeNode(fieldName);
					String attributeValue = (attributeNode==null?null:attributeNode.getNodeValue().trim());
					if(attributeValue!=null)
					{
						Object valueObject = StringUtil.convertStringValue(attributeValue, fieldClazz);
						if(valueObject!=null)
						{
							try
							{
								setMethod.invoke(obj,valueObject);
								
								LogUtil.d(TAG, "success parse attribute " + fieldName + " value=" + valueObject.toString());
								
								parsedFieldSet.add(fieldName);
							} catch (Exception e)
							{
								LogUtil.e(TAG,"failure parse attribute " + fieldName + " value=" + valueObject.toString() + "exception occured.",e);
								throw new XmlException(e);
							} 
						}
					}
				}
			}
		}
		
		for(String parsedField : parsedFieldSet)
		{
			fieldAndSetmethodMap.remove(parsedField);
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
