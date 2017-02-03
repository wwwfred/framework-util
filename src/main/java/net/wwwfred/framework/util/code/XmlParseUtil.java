package net.wwwfred.framework.util.code;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * XmlParse
 * @author wangwwy
 * createdDatetime 2014年8月28日 下午12:06:02
 */
public class XmlParseUtil {
	
	public static String getJavaCodeContentFromXml(String packageString,String classSimpleName,byte[] xmlData) {
		
		FieldType fieldType;
		
		// parse xmlInputStream to document
		InputStream xmlInputStream = new ByteArrayInputStream(xmlData);
		try
		{
			Document document = new SAXReader().read(xmlInputStream);
			Element rootElement = document.getRootElement();
			fieldType = new FieldType(rootElement.getName());
			fieldType.isInnerClass = true;
			parseElement(fieldType, rootElement);
//			fieldType = parseRootElement(rootElement);
		}
		catch(Exception e)
		{
			throw new CodeException("CodeUtil.produceJavaCode SaxParse xmlInputStream exception occured.",e);
		}
		finally
		{
			if(xmlInputStream!=null)
			{
				try {
					xmlInputStream.close();
				} catch (IOException e) {
					throw new CodeException("CodeUtil.produceJavaCode close xmlInputStream exception occured.",e);
				}
			}
		}
		
		StringBuffer sb = new StringBuffer();
        // append packageString, such as: package com.teshehui.util.code;
        if(packageString!=null)
        {
            sb.append(CodeUtil.PACKAGE_PREFIX).append(packageString).append(CodeUtil.LINE_END_TAG);
        }
        
        // append class start, such as: public class JsonParseUtil {
        sb.append(CodeUtil.CLASS_PREFIX).append(classSimpleName).append(CodeUtil.CLASS_LEFT_TAG);
        
        sb.append(parseFieldType(fieldType.innerFieldTypeList));
        
        // append class end, such as: }
        sb.append(CodeUtil.CLASS_RIGHT_TAG);
		
		return sb.toString();
	}

	private static String parseFieldType(List<FieldType> fieldTypeList) {
		StringBuffer sb = new StringBuffer();
		
		for(FieldType fieldType : fieldTypeList)
		{
			sb.append(CodeUtil.FIELD_DECARATOR);
			if(fieldType.isArray&&fieldType.isInnerClass)
			{
				sb.append(fieldType.name + CodeUtil.ARRAY_LEFT_TAG + CodeUtil.ARRAY_RIGHT_TAG + " " + fieldType.name + "=" + " new " + fieldType.name + CodeUtil.ARRAY_LEFT_TAG + CodeUtil.ARRAY_RIGHT_TAG + CodeUtil.CLASS_LEFT_TAG + CodeUtil.CLASS_RIGHT_TAG + CodeUtil.LINE_END_TAG);
				sb.append(CodeUtil.INNER_CLASS_PREFIX + fieldType.name + CodeUtil.CLASS_LEFT_TAG);
				sb.append(parseFieldType(fieldType.innerFieldTypeList));
				sb.append(CodeUtil.CLASS_RIGHT_TAG);
			}
			else if(fieldType.isArray)
			{
				sb.append(CodeUtil.FIELD_TYPE_STRING + CodeUtil.ARRAY_LEFT_TAG + CodeUtil.ARRAY_RIGHT_TAG + " " + fieldType.name + "=" + " new " + CodeUtil.FIELD_TYPE_STRING + CodeUtil.ARRAY_LEFT_TAG + CodeUtil.ARRAY_RIGHT_TAG + CodeUtil.CLASS_LEFT_TAG + CodeUtil.CLASS_RIGHT_TAG + CodeUtil.LINE_END_TAG);
			}
			else if(fieldType.isInnerClass)
			{
				sb.append(fieldType.name + " " + fieldType.name + CodeUtil.LINE_END_TAG);
				sb.append(CodeUtil.INNER_CLASS_PREFIX + fieldType.name + CodeUtil.CLASS_LEFT_TAG);
				sb.append(parseFieldType(fieldType.innerFieldTypeList));
				sb.append(CodeUtil.CLASS_RIGHT_TAG);
			}
			else
			{
				sb.append(CodeUtil.FIELD_TYPE_STRING + " " + fieldType.name + CodeUtil.LINE_END_TAG);
			}
		}
		
		// add get and set method
		for(FieldType fieldType : fieldTypeList)
		{
			String fieldTypeName = fieldType.name;
			String methodName = fieldTypeName.startsWith("iS")?fieldTypeName:fieldTypeName.substring(0,1).toUpperCase()+fieldTypeName.substring(1);
			String getMethodName = CodeUtil.GET_METHOD_PREFIX + methodName;
			String setMethodName = CodeUtil.SET_METHOD_PREFIX + methodName;
			
			sb.append(CodeUtil.METHOD_DECARATOR);
			if(fieldType.isArray&&fieldType.isInnerClass)
			{
				sb.append(fieldTypeName + CodeUtil.ARRAY_LEFT_TAG + CodeUtil.ARRAY_RIGHT_TAG + " " + getMethodName + CodeUtil.METHOD_LEFT_TAG + CodeUtil.METHOD_RIGHT_TAG + CodeUtil.CLASS_LEFT_TAG + " " + CodeUtil.METHOD_RETURN_TAG + fieldTypeName + CodeUtil.LINE_END_TAG + CodeUtil.CLASS_RIGHT_TAG);
				sb.append(CodeUtil.METHOD_DECARATOR + CodeUtil.METHOD_VOID_TAG + setMethodName + CodeUtil.METHOD_LEFT_TAG + fieldTypeName + CodeUtil.ARRAY_LEFT_TAG + CodeUtil.ARRAY_RIGHT_TAG + " " + fieldTypeName + CodeUtil.METHOD_RIGHT_TAG + CodeUtil.CLASS_LEFT_TAG + " " + CodeUtil.OBJECT_THIS_TAG + "." + fieldTypeName + "=" + fieldTypeName + CodeUtil.LINE_END_TAG + CodeUtil.CLASS_RIGHT_TAG);
			}
			else if(fieldType.isArray)
			{
				sb.append(CodeUtil.FIELD_TYPE_STRING + CodeUtil.ARRAY_LEFT_TAG + CodeUtil.ARRAY_RIGHT_TAG + " " + getMethodName + CodeUtil.METHOD_LEFT_TAG + CodeUtil.METHOD_RIGHT_TAG + CodeUtil.CLASS_LEFT_TAG + " " + CodeUtil.METHOD_RETURN_TAG + fieldTypeName + CodeUtil.LINE_END_TAG + CodeUtil.CLASS_RIGHT_TAG);
				sb.append(CodeUtil.METHOD_DECARATOR + CodeUtil.METHOD_VOID_TAG + setMethodName + CodeUtil.METHOD_LEFT_TAG + CodeUtil.FIELD_TYPE_STRING + CodeUtil.ARRAY_LEFT_TAG + CodeUtil.ARRAY_RIGHT_TAG + " " + fieldTypeName + CodeUtil.METHOD_RIGHT_TAG + CodeUtil.CLASS_LEFT_TAG + " " + CodeUtil.OBJECT_THIS_TAG + "." + fieldTypeName + "=" + fieldTypeName + CodeUtil.LINE_END_TAG + CodeUtil.CLASS_RIGHT_TAG);
			}
			else if(fieldType.isInnerClass)
			{
				sb.append(fieldTypeName + " " + getMethodName + CodeUtil.METHOD_LEFT_TAG + CodeUtil.METHOD_RIGHT_TAG + CodeUtil.CLASS_LEFT_TAG + " " + CodeUtil.METHOD_RETURN_TAG + fieldTypeName + CodeUtil.LINE_END_TAG + CodeUtil.CLASS_RIGHT_TAG);
				sb.append(CodeUtil.METHOD_DECARATOR + CodeUtil.METHOD_VOID_TAG + setMethodName + CodeUtil.METHOD_LEFT_TAG + fieldTypeName + " " + fieldTypeName + CodeUtil.METHOD_RIGHT_TAG + CodeUtil.CLASS_LEFT_TAG + " " + CodeUtil.OBJECT_THIS_TAG + "." + fieldTypeName + "=" + fieldTypeName + CodeUtil.LINE_END_TAG + CodeUtil.CLASS_RIGHT_TAG);
			}
			else
			{
				sb.append(CodeUtil.FIELD_TYPE_STRING + " " + getMethodName + CodeUtil.METHOD_LEFT_TAG + CodeUtil.METHOD_RIGHT_TAG + CodeUtil.CLASS_LEFT_TAG + " " + CodeUtil.METHOD_RETURN_TAG + fieldTypeName + CodeUtil.LINE_END_TAG + CodeUtil.CLASS_RIGHT_TAG);
				sb.append(CodeUtil.METHOD_DECARATOR + CodeUtil.METHOD_VOID_TAG + setMethodName + CodeUtil.METHOD_LEFT_TAG + CodeUtil.FIELD_TYPE_STRING + " " + fieldTypeName + CodeUtil.METHOD_RIGHT_TAG + CodeUtil.CLASS_LEFT_TAG + " " + CodeUtil.OBJECT_THIS_TAG + "." + fieldTypeName + "=" + fieldTypeName + CodeUtil.LINE_END_TAG + CodeUtil.CLASS_RIGHT_TAG);
			}
		}
		
		return sb.toString();
	}

	
	
	private static class FieldType
	{
		private String name;
		private boolean isArray;
		private boolean isInnerClass;
		private List<FieldType> innerFieldTypeList = new ArrayList<FieldType>();
		public FieldType(String name) {
			this.name = name;
		}
	}
	
	private static void parseElement(FieldType fieldType, Element element) {
		
		Map<String, FieldType> innerFieldTypeMap = new LinkedHashMap<String, FieldType>();
		
		// parse attribute
		@SuppressWarnings("unchecked")
		List<Attribute> attributeList = element.attributes();
		for(Attribute attribute : attributeList)
		{
			String attributeName = attribute.getName();
			if(!innerFieldTypeMap.containsKey(attributeName))
			{
				innerFieldTypeMap.put(attributeName, new FieldType(attributeName));
			}
		}
		
		// parse children element
		@SuppressWarnings("unchecked")
		List<Element> childElementList = element.elements();
		for(Element childElement : childElementList)
		{
			String childElementName = childElement.getName();
			FieldType childFieldType;
			if(!innerFieldTypeMap.containsKey(childElementName))
			{
				childFieldType = new FieldType(childElementName);
				childFieldType.isInnerClass = true;
				if(element.elements(childElementName).size()>1)
				{
					childFieldType.isArray = true;
				}
				innerFieldTypeMap.put(childElementName, childFieldType);
			}
			else
			{
				childFieldType = innerFieldTypeMap.get(childElementName);
			}
			parseElement(childFieldType, childElement);
		}
		
		if(innerFieldTypeMap.isEmpty())
		{
			fieldType.isInnerClass = false;
		}
		else
		{
			fieldType.innerFieldTypeList = new ArrayList<FieldType>(innerFieldTypeMap.values());
		}
	}
	
//	private static FieldType parseRootElement(Element rootElement) {
//		
//		String fieldName = rootElement.getName();
//		FieldType fieldType = new FieldType(fieldName);
//		fieldType.isInnerClass = true;
//		
//		// childrenMap
//		Map<String, FieldType> innerMap = new LinkedHashMap<String, XmlParseUtil.FieldType>();
////		Map<String, List<FieldType>> innerFieldTypeMap = new HashMap<String, List<FieldType>>();
//		
//		// parseAttribute
//		@SuppressWarnings("unchecked")
//		List<Attribute> attributeList = rootElement.attributes();
//		for(Attribute attribute : attributeList)
//		{
//			String attributeName = attribute.getName();
//			if(!innerMap.containsKey(attributeName))
//			{
//				innerMap.put(attributeName, new FieldType(attributeName));
//			}
//			
////			innerFieldTypeMap.put(attributeName, new ArrayList<FieldType>());
//			
////			FieldType innerFieldType = new FieldType(attributeName);
//			
////			innerFieldTypeMap.get(attributeName).add(innerFieldType);
//		}
//		
//		// parse child element
//		@SuppressWarnings("unchecked")
//		List<Element> elementList = rootElement.elements();
//		for(Element element : elementList)
//		{
//			String elementName = element.getName();
//			FieldType innerFieldType;
//			if(!innerMap.containsKey(elementName))
//			{
//				innerFieldType = new FieldType(elementName);
//				innerFieldType.isInnerClass = true;
//				if(rootElement.elements(elementName).size()>1)
//				{
//					innerFieldType.isArray = true;
//				}
//			}
//			else
//			{
//				innerFieldType = innerMap.get(elementName);
//			}
//			
//			// parse innerFieldType innerFieldTypeList
//			parseElement(innerFieldType,element);
//		}
//		fieldType.innerFieldTypeList = new ArrayList<FieldType>(innerMap.values());
//		
//			
//			innerFieldTypeMap.put(elementName, new ArrayList<FieldType>());
//			
//			FieldType innerFieldType = new FieldType(elementName);
//			
//			if(rootElement.elements(elementName).size()>1)
//			{
//				innerFieldType.isArray = true;
//			}
//			
//			@SuppressWarnings("unchecked")
//			List<Attribute> elementAttributeList = element.attributes();
//			@SuppressWarnings("unchecked")
//			List<Element> childElementList = element.elements();
//			if(!elementAttributeList.isEmpty()||!childElementList.isEmpty())
//			{
//				innerFieldType.isInnerClass = true;
//				
//				Map<String, List<FieldType>> childElementInnerFieldTypeMap = new HashMap<String, List<FieldType>>();
//				if(!elementAttributeList.isEmpty())
//				{
//					for(Attribute attribute : elementAttributeList)
//					{
//						String attributeName = attribute.getName();
//						if(!childElementInnerFieldTypeMap.containsKey(attributeName))
//						{
//							childElementInnerFieldTypeMap.put(attributeName, new ArrayList<FieldType>());
//							childElementInnerFieldTypeMap.get(attributeName).add(new FieldType(attributeName));
//						}
//					}
//				}
//				
//				if(!childElementList.isEmpty())
//				{
//					for(Element childElement : childElementList)
//					{
//						String childElementName = childElement.getName();
//						childElementInnerFieldTypeMap.put(childElementName, new ArrayList<FieldType>());
//						childElementInnerFieldTypeMap.get(childElementName).add(parseRootElement(childElement));
//					}
//				}
//				
//				List<FieldType> childElementInnerFieldTypeList = new ArrayList<FieldType>();
//				for(Collection<FieldType> fieldTypeCollection : childElementInnerFieldTypeMap.values())
//				{
//					for(FieldType fieldType2 : fieldTypeCollection)
//					{
//						if(!fieldType2.isInnerClass)
//						{
//							childElementInnerFieldTypeList.add(0,fieldType2);
//						}
//						else
//						{
//							childElementInnerFieldTypeList.add(fieldType2);
//						}
//					}
////					childElementInnerFieldTypeList.addAll(fieldTypeCollection);
//				}
//				innerFieldType.innerFieldTypeList = childElementInnerFieldTypeList;
//			}
//			
//			innerFieldTypeMap.get(elementName).add(innerFieldType);
//		}
//
//		List<FieldType> innerFieldTypeList = new ArrayList<FieldType>();
//		Collection<List<FieldType>> collection = innerFieldTypeMap.values();
//		for(List<FieldType> fieldTypeList : collection)
//		{
//			for(FieldType fieldType2 : fieldTypeList)
//			{
//				if(!fieldType2.isInnerClass)
//				{
//					innerFieldTypeList.add(0, fieldType2);
//				}
//				else
//				{
//					innerFieldTypeList.add(fieldType2);
//				}
//			}
////			innerFieldTypeList.addAll(fieldTypeList);
//		}
//		fieldType.innerFieldTypeList = innerFieldTypeList;
//		
//		if(!fieldType.innerFieldTypeList.isEmpty())
//		{
//			fieldType.isInnerClass = true;
//		}
//			
//		return fieldType;
//	}


}
