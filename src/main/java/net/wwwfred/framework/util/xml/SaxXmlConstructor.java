package net.wwwfred.framework.util.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.wwwfred.framework.util.log.LogUtil;
import net.wwwfred.framework.util.reflect.AliasAnnotation;
import net.wwwfred.framework.util.reflect.ReflectUtil;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


public class SaxXmlConstructor implements XmlConstructor
{
	@Override
	public String toXmlString(Object obj,Charset charset)
	{
		String result = null;
		
		if(obj==null)
			throw new XmlException("XmlUtil obj to SimpleString obj and rootElementName should not be null.");
		if(charset==null)
			charset = Charset.defaultCharset();
		
		Class<?> clazz = obj.getClass();
		String rootElementName = "";
		Class<? extends AliasAnnotation> aliasAnnotationClazz = AliasAnnotation.class;
		if(clazz.isAnnotationPresent(aliasAnnotationClazz))
		{
			rootElementName = clazz.getAnnotation(aliasAnnotationClazz).value();
		}
		if("".equals(rootElementName))
		{
			rootElementName = clazz.getSimpleName();
		}
		
		Element rootElement = DocumentHelper.createElement(rootElementName);
		Document document = DocumentHelper.createDocument(rootElement);
		document.setXMLEncoding(charset.name());
		constructElement(rootElement, obj);
		
		String xmlString = document.asXML();
		
		// encoding
//      result = new String(xmlString.getBytes(),charset);
		result = xmlString;
		
		return result;
	}

//	public String toXmlString(Object obj,String rootElementName)
//	{
//		String result = null;
//		
//		if(obj==null||rootElementName==null)
//			throw new XmlException("XmlUtil obj to SimpleString obj and rootElementName should not be null.");
//		
//		Element rootElement = DocumentHelper.createElement(rootElementName);
//		Document document = DocumentHelper.createDocument(rootElement);
//		constructElement(rootElement, obj);
//		
//		result = document.asXML();
//		
//		return result;
//	}
	
	private static void constructElement(Element element,Object obj)
	{
		Class<?> objClazz = obj.getClass();
		Map<Field, Method> fieldAndGetmethodMap = ReflectUtil.getFieldAndGetmethodMap(objClazz);
		
		Set<Entry<Field, Method>> entrySet = fieldAndGetmethodMap.entrySet();
		for(Entry<Field, Method> entry : entrySet)
		{
			Field field = entry.getKey();
			String fieldName = ReflectUtil.getFieldGetMethodAliasName(objClazz, field);
			Method getMethod = entry.getValue();
			
			Class<?> fieldClazz = getMethod.getReturnType();
			Object fieldValue = null;
			try
			{
				fieldValue = getMethod.invoke(obj, new Object[]{});
			} catch (Exception e)
			{
				String errorMessage = e.getMessage();
				LogUtil.w("XmlUtil.toSimpleString",errorMessage , e);
			}
			
			if(fieldValue!=null)
			{
				if(ReflectUtil.isSimpleClazz(fieldClazz))
				{
				    String fieldStringValue = fieldValue.toString();
				    
				    if(XmlUtil.XML_ELEMENT_TEXT_CONTENT.equalsIgnoreCase(fieldName))
				    {
				        element.setText(fieldStringValue);
				    }
				    else
				    {
	                    Class<? extends XmlElementAnnotation> xmlElementAnnotationClazz = XmlElementAnnotation.class;
	                    if(field.isAnnotationPresent(xmlElementAnnotationClazz))
	                    {
	                        Element childElement = DocumentHelper.createElement(fieldName);
	                        childElement.setText(fieldStringValue);
	                        element.add(childElement);
	                    }
	                    else
	                    {
	                        if(XmlUtil.XML_NAMESPACE.equalsIgnoreCase(fieldName))
	                        {
	                            element.addAttribute(new QName(XmlUtil.XML_NAMESPACE,new Namespace(null, fieldStringValue)), "");
	                        }
	                        else
	                        {
	                            element.addAttribute(fieldName,fieldStringValue);
	                        }
	                    }
				    }
				}
				else if(fieldValue instanceof Collection)
				{
					Collection<?> collection = (Collection<?>) fieldValue;
					Iterator<?> collectionIterator = collection.iterator();
					while(collectionIterator.hasNext())
					{
						Object one = collectionIterator.next();
						Element childElement = DocumentHelper.createElement(fieldName);
						if(ReflectUtil.isSimpleClazz(one.getClass()))
						{
							childElement.setText(one.toString());
						}
						else
						{
							constructElement(childElement, one);
						}
						element.add(childElement);
					}
				}
				else if(fieldClazz.isArray())
				{
					Object[] objArray = (Object[])fieldValue;
					for(Object one : objArray)
					{
						Element childElement = DocumentHelper.createElement(fieldName);
						if(ReflectUtil.isSimpleClazz(one.getClass()))
						{
							childElement.setText(one.toString());
						}
						else
						{
							constructElement(childElement, one);
						}
						element.add(childElement);
					}
				}
				else if(fieldValue instanceof Object)
				{
					Element childElement = DocumentHelper.createElement(fieldName);
					constructElement(childElement, fieldValue);
					element.add(childElement);
				}
			}
		}
	}
}
