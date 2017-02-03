package net.wwwfred.framework.util.code;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.wwwfred.framework.util.io.IOUtil;
import net.wwwfred.framework.util.json.JSONUtil;
import net.wwwfred.framework.util.json.JSONUtil.Json;
import net.wwwfred.framework.util.json.JSONUtil.JsonArray;
import net.wwwfred.framework.util.json.JSONUtil.JsonObject;
import net.wwwfred.framework.util.json.JSONUtil.JsonString;
import net.wwwfred.framework.util.math.MathUtil;

/**
 * XmlParse
 * @author wangwwy
 * createdDatetime 2014年8月28日 下午12:06:02
 */
public class JsonParseUtil {
	
	public static String getJavaCodeContentFromJson(String packageString,String classSimpleName,String jsonString) {
		
		StringBuffer sb = new StringBuffer();
		// append packageString, such as: package com.teshehui.util.code;
		if(packageString!=null)
		{
		    sb.append(CodeUtil.PACKAGE_PREFIX).append(packageString).append(CodeUtil.LINE_END_TAG);
		}
		
		// append class content
		Json json = JSONUtil.parseString(jsonString);
		if(!(json instanceof JsonObject))
		{
		    throw new CodeException("JsonParseUtil.getJavaCodeFromJson,jsonString is not a jsonObject,jsonString="+jsonString);
		}
		
		Set<Class<?>> importClazzSet = new HashSet<Class<?>>();
		String classContent = parseJsonObject((JsonObject)json,importClazzSet);
		for(Class<?> importClazz : importClazzSet)
		{
		    sb.append(CodeUtil.CLASS_IMPORT_TAG).append(importClazz.getName()).append(CodeUtil.LINE_END_TAG);
		}
		
		// append class start, such as: public class JsonParseUtil {
        sb.append(CodeUtil.CLASS_PREFIX).append(classSimpleName).append(CodeUtil.CLASS_LEFT_TAG);
        
		sb.append(classContent);
		
		// append class end, such as: }
		sb.append(CodeUtil.CLASS_RIGHT_TAG);
		
		return sb.toString();
	}

	private static String parseJsonObject(JsonObject jsonObject, Set<Class<?>> importClazzSet) {
	    
	    StringBuffer sb = new StringBuffer();
	    
	    Map<String, Json> map = jsonObject.getMap();
	    Set<Entry<String, Json>> entrySet = map.entrySet();
	    
	    // 记录所有的fieldName与fieldType组成的map便于后续生成get与set方法
	    Map<String, String> fieldNameFieldTypeMap = new LinkedHashMap<String, String>();
	    
	    // 当fieldName不合法时，采用系统生成fieldName名，同时此field加上别名注解例如@AliasAnnotation(key)
	    long keyIndex = 0;
	    for(Entry<String, Json> entry : entrySet)
	    {
	        String fieldName;
	        String key = entry.getKey();
	        if(key==null||"".equals(key.trim()))
	        {
	            throw new CodeException("JsonParseUtil.parseJsonObject key illegal,key="+key);
	        }
	        // 是否引入别名注解
	        if(!isFieldNameIllegal(key))
	        {
	            importClazzSet.add(CodeUtil.ALIAS_ANNOTATION_CLASS);
	            
	            sb.append(CodeUtil.ANNOTATION_PREFIX_TAG)
	            .append(CodeUtil.ALIAS_ANNOTATION_CLASS.getSimpleName())
	            .append(CodeUtil.ANNOTATION_LEFT_TAG)
	            .append(CodeUtil.ANNOTATION_LEFT_QUOTE_TAG)
	            .append(key)
	            .append(CodeUtil.ANNOTATION_RIGHT_QUOTE_TAG)
	            .append(CodeUtil.ANNOTATION_RIGHT_TAG);
	            fieldName = CodeUtil.ILLEGAL_FIELD_NAME_PREFIX + (keyIndex++);
	        }
	        else
	        {
	            fieldName = key;
	        }
	            
	        // append field declare, such as: private
            sb.append(CodeUtil.FIELD_DECARATOR);
	        
	        String fieldType;
	        Json mapValueJaon = entry.getValue();
	        if(mapValueJaon instanceof JsonObject)
	        {
	            // append object field type declare, such as: Data 
	            fieldType = key.substring(0,1).toUpperCase()+key.substring(1);
	            sb.append(fieldType).append(CodeUtil.SEPARATOR_TAG);
	            
	            // append object field value declare, such as: data;
	            sb.append(key).append(CodeUtil.LINE_END_TAG);
	            
	            // append inner class
	            sb.append(getInnerClassJavaCodeCotent(fieldType,(JsonObject)mapValueJaon,importClazzSet));
	        }
	        else if(mapValueJaon instanceof JsonArray)
	        {
	            JsonArray jsonArray = (JsonArray)mapValueJaon;
	            List<Json> list = jsonArray.getList();
	            Json firstOneJson = null;
	            
	            if(list.isEmpty())
	            {
	                fieldType = CodeUtil.FIELD_TYPE_OBJECT;
	            }
	            else
	            {
	                firstOneJson = list.get(0);
	                if(firstOneJson instanceof JsonObject)
	                {
	                    fieldType = key.substring(0,1).toUpperCase()+key.substring(1);
	                }
	                else if(firstOneJson instanceof JsonString)
	                {
	                    fieldType = getFieldTypeByJsonString((JsonString)firstOneJson);
	                }
	                else
                    {
                        throw new CodeException("JsonParseUtil.parseJsonObject list one json type not support.");
                    }
	            }
	            fieldType += CodeUtil.ARRAY_LEFT_TAG+CodeUtil.ARRAY_RIGHT_TAG;
	                
	            // append array field type declare, such as: Data[] or String[]
                sb.append(fieldType)
                .append(CodeUtil.SEPARATOR_TAG);
                
                // append array field value, such as: data = new Data[]{}; or s = new String[]{};
                sb.append(key).append(CodeUtil.FIELD_VALUE_SEPARATOR_TAG)
                .append(CodeUtil.FIELD_VALUE_NEW_TAG).append(fieldType)
                .append(CodeUtil.OBJECT_LEFT_TAG).append(CodeUtil.OBJECT_RIGHT_TAG)
                .append(CodeUtil.LINE_END_TAG);
                
                // append inner class 
                if(firstOneJson instanceof JsonObject)
                {
                    sb.append(getInnerClassJavaCodeCotent(fieldType, (JsonObject)firstOneJson,importClazzSet));
                }    
	        }
	        else if(mapValueJaon instanceof JsonString)
	        {
                // append string field type declare, such as : Boolean or Double or String
	            fieldType = getFieldTypeByJsonString((JsonString)mapValueJaon);
	            sb.append(fieldType).append(CodeUtil.SEPARATOR_TAG);
	            
	            // append string field value declare, such as s;
	            sb.append(fieldName).append(CodeUtil.LINE_END_TAG);
	        }
	        else
	        {
	            throw new CodeException("JsonParseUtil.parseJsonObject value json type not support.");
	        }
	        
	        fieldNameFieldTypeMap.put(fieldName, fieldType);
	    }
	    
	    // 遍历fieldNameFieldTypeMap生成get与set方法
	    Set<Entry<String, String>> fieldNameFieldTypeEntrySet = fieldNameFieldTypeMap.entrySet();
	    for(Entry<String, String> entry : fieldNameFieldTypeEntrySet)
	    {
	        String fieldName = entry.getKey();
	        String fieldNameFirstLetterUpperCase = fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
	        String fieldType = entry.getValue();
	        
	        // append get method declare,such as: public Data getData(){ or public Data[] getData() {
	        sb.append(CodeUtil.METHOD_DECARATOR)
	        .append(fieldType).append(CodeUtil.SEPARATOR_TAG)
	        .append(CodeUtil.GET_METHOD_PREFIX)
	        .append(fieldNameFirstLetterUpperCase)
	        .append(CodeUtil.METHOD_LEFT_TAG).append(CodeUtil.METHOD_RIGHT_TAG)
	        .append(CodeUtil.METHOD_CONTENT_LEFT_TAG);
	        
	        // append get method content, such as: return data;
	        sb.append(CodeUtil.METHOD_RETURN_TAG).append(fieldName).append(CodeUtil.LINE_END_TAG);
	        
	        // append get method end, such as: }
	        sb.append(CodeUtil.METHOD_CONTENT_RIGHT_TAG);
	        
	        // append set method declare,such as public void setData(Data[] data){ or public void setData(Data data){
	        sb.append(CodeUtil.METHOD_DECARATOR)
	        .append(CodeUtil.METHOD_VOID_TAG)
	        .append(CodeUtil.SET_METHOD_PREFIX)
	        .append(fieldNameFirstLetterUpperCase)
	        .append(CodeUtil.METHOD_LEFT_TAG)
	        .append(fieldType).append(CodeUtil.SEPARATOR_TAG)
	        .append(fieldName).append(CodeUtil.METHOD_RIGHT_TAG)
	        .append(CodeUtil.METHOD_CONTENT_LEFT_TAG);
	        
	        // append set method content, such as: this.data = data;
	        sb.append(CodeUtil.OBJECT_THIS_TAG).append(CodeUtil.OBJECT_FIELD_SEPARATOR_TAG)
	        .append(fieldName).append(CodeUtil.FIELD_VALUE_SEPARATOR_TAG).append(fieldName).append(CodeUtil.LINE_END_TAG);
	        
	        // append set method end, such as: }
	        sb.append(CodeUtil.METHOD_CONTENT_RIGHT_TAG);
            
	    }
	    
	    return sb.toString();
    }

    private static boolean isFieldNameIllegal(String key) {
        if(MathUtil.isNumber(key))
            return false;
        return true;
    }

    private static String getInnerClassJavaCodeCotent(String fieldType, JsonObject mapValueJaon, Set<Class<?>> importClazzSet) {
        StringBuffer sb = new StringBuffer();
        
        String className;
        String arrayTag = CodeUtil.ARRAY_LEFT_TAG+CodeUtil.ARRAY_RIGHT_TAG;
        if(fieldType.endsWith(arrayTag))
        {
            className = fieldType.substring(0,fieldType.lastIndexOf(arrayTag));
        }
        else
        {
            className = fieldType;
        }
        
        // append inner class start,such as: public static class Data{
        sb.append(CodeUtil.INNER_CLASS_PREFIX).append(className).append(CodeUtil.CLASS_LEFT_TAG);
        
        // append inner class content
        sb.append(parseJsonObject(mapValueJaon,importClazzSet));
        
        // append inner class end, such as: }
        sb.append(CodeUtil.CLASS_RIGHT_TAG);
        return sb.toString();
    }

    private static String getFieldTypeByJsonString(JsonString jsonString) {
        String result;
        String jsonStringValue = jsonString.getS().trim();
        if(Json.JSON_STRING_VALUE_TRUE.equalsIgnoreCase(jsonStringValue)
                ||Json.JSON_STRING_VALUE_FALSE.equalsIgnoreCase(jsonStringValue))
        {
            result = CodeUtil.FIELD_TYPE_BOOLEAN;
        }
        else if(MathUtil.isNumber(jsonStringValue,Integer.class))
        {
            result = CodeUtil.FIELD_TYPE_INTEGER;
        }
        else if(MathUtil.isNumber(jsonStringValue,Long.class))
        {
            result = CodeUtil.FIELD_TYPE_LONG;
        }
        else if(MathUtil.isNumber(jsonStringValue, Double.class))
        {
            result = CodeUtil.FIELD_TYPE_DOUBLE;
        }
        else
        {
            result = CodeUtil.FIELD_TYPE_STRING;
        }
        return result;
    }
    
    public static void main(String[] args) {
        
        String jsonString = new String(IOUtil.getByteArrayFromInputStream(JsonParseUtil.class.getClassLoader().getResourceAsStream("jsonFile.json")),Charset.forName("UTF-8"));
        System.out.println(getJavaCodeContentFromJson("com.teshehui.util.json.test","TestOne", jsonString));
        
    }
}
