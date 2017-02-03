package net.wwwfred.framework.util.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.wwwfred.framework.util.code.CodeUtil;
import net.wwwfred.framework.util.date.DatetimeFormat;
import net.wwwfred.framework.util.date.DatetimeFormatAnnotation;
import net.wwwfred.framework.util.date.DatetimeUtil;
import net.wwwfred.framework.util.math.MathUtil;
import net.wwwfred.framework.util.reflect.ReflectUtil;
import net.wwwfred.framework.util.string.StringUtil;

public class JSONUtil {
	
private static DatetimeFormat JSON_STRING_DATE_FORMAT = DatetimeFormat.STANDARED_DATE_TIME_FORMAT;
    
    /**
     * 指定JsonUtil.parseJsonString date format
     * @author wangwenwu
     * 2014年10月15日  上午11:39:19
     */
    public static void setJSON_STRING_DATE_FORMAT(
            DatetimeFormat jSON_STRING_DATE_FORMAT) {
        if(jSON_STRING_DATE_FORMAT==null)
            throw new JSONException("JSONUtil.setJsonStringDateFormat parameter null");
        
        JSON_STRING_DATE_FORMAT = jSON_STRING_DATE_FORMAT;
    }
    
    /**
     * Json对象字符串转化为指定的对象
     * @author wangwwy
     * createdDatetime 2014年8月26日 下午3:07:34
     * @param s
     * @param clazz
     * @return
     */
    public static <T> T toModel(String s, Class<T> clazz)
    {
        if(clazz==null)
            throw new JSONException("json string to model,clazz="+clazz);
        
        Json json = parseString(s);
        if(json instanceof JsonObject)
        {
            if(Map.class.isAssignableFrom(clazz))
            {
                @SuppressWarnings("unchecked")
                T t = (T) toObject(json);
                return t;
            }
            else
            {
                T t = ((JsonObject)json).getModel(clazz);
                return t;
            }
        }
        else if(json instanceof JsonArray)
        {
            if(Collections.class.isAssignableFrom(clazz))
            {
                @SuppressWarnings("unchecked")
                T t = (T) toObject(json);
                return t;
            }
            else if(clazz.isArray())
            {
                @SuppressWarnings("unchecked")
                T t = (T) ((JsonArray)json).getArrayValue(clazz);
                return t;
            }
            else
            {
                return null;
            }
        }
        else if(json instanceof JsonString)
        {
            @SuppressWarnings("unchecked")
            T t = (T) ((JsonString)json).getValue(clazz);
            return t;
        }
        return null;
    }
    
    /**
     * Json字符串转化为期望类型的对象
     * @author wangwwy
     * createdDatetime 2014年8月26日 下午3:08:20
     * @param s
     * @param expectedClazz
     * @return
     * @throws JSONException
     */
    public static Object toObject(String s, Class<?> expectedClazz)
    {
        Json json = parseString(s);
        
        if(expectedClazz==null)
            return toObject(json);
            
        return json.toJsonObject(expectedClazz);
    }
    
    private static Object toObject(Json json)
    {
        if(json instanceof JsonObject)
        {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            
            Map<String, Json> jsonMap = ((JsonObject)json).map;
            Set<Entry<String, Json>> entrySet = jsonMap.entrySet();
            for(Entry<String,Json> entry : entrySet)
            {
                map.put(entry.getKey(), toObject(entry.getValue()));
            }
            return map;
        }
        else if(json instanceof JsonArray)
        {
            List<Object> list = new ArrayList<Object>();
            
            List<Json> jsonList = ((JsonArray)json).list;
            for(Json oneJson : jsonList)
            {
                list.add(toObject(oneJson));
            }
            return list;
        }
        else if(json instanceof JsonString)
        {
            Object result;
            String value = ((JsonString)json).getS().trim();
            if(Json.JSON_STRING_VALUE_NULL.equalsIgnoreCase(value))
            {
                result = null;
            }
            else if(Json.JSON_STRING_VALUE_TRUE.equalsIgnoreCase(value)||Json.JSON_STRING_VALUE_FALSE.equalsIgnoreCase(value))
            {
                result = Boolean.parseBoolean(value);
            }
            else if(MathUtil.isNumber(value))
            {
                result = MathUtil.getNumber(value);
            }
            else
            {
                // 去掉首尾对称的双引号
                int length;
                if(value.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE)&&value.endsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE))
                {
                    length = Json.JSON_STRING_QUOTE_TAG_DOUBLE.length();
                    value = value.substring(length, value.length()-length);
                }
                
                // 去掉特殊符号前的转义字符
             // 去掉特殊符号前的转义字符
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_CONVERT, Json.JSON_STRING_SPECIAL_TAG_CONVERT_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_B, Json.JSON_STRING_SPECIAL_TAG_B_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_F, Json.JSON_STRING_SPECIAL_TAG_F_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_N, Json.JSON_STRING_SPECIAL_TAG_N_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_QUOTE, Json.JSON_STRING_SPECIAL_TAG_QUOTE_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_R, Json.JSON_STRING_SPECIAL_TAG_R_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_REVERSE_CONVERT, Json.JSON_STRING_SPECIAL_TAG_REVERSE_CONVERT_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_T, Json.JSON_STRING_SPECIAL_TAG_T_VALUE);
                result = value;
            }
            return result;
        }
        return null;
    }
    
    /**
     * Json对象字符串转化为Map
     * @author wangwwy
     * createdDatetime 2014年9月16日 下午11:31:59
     * @param s
     * @param expectedClazz
     * @return
     */
    public static <T> Map<String, T> toMap(String s, Class<T> expectedClazz)
    {
        Json json = parseString(s);
        if(json instanceof JsonObject)
        {
            return ((JsonObject)json).getMapValue(expectedClazz);
        }
        return Collections.emptyMap();
    }
    
    /**
     * Json对象字符串转化为Collection
     * @author wangwwy
     * createdDatetime 2014年9月16日 下午11:34:42
     * @param s
     * @param expectedClazz
     * @return
     */
    public static <T> Collection<T> toCollection(String s, Class<T> expectedClazz)
    {
        Json json = parseString(s);
        if(json instanceof JsonArray)
        {
            return ((JsonArray)json).getCollectionValue(expectedClazz);
        }
        return Collections.emptyList();
    }
    
    public static String toString(Object o)
    {
        return toString(o, Json.JSON_STRING_QUOTE_TAG_DOUBLE);
    }
    
    public static String toString(Object o, String jsonObjectKeyQuoteTag)
    {
        return toString(o, jsonObjectKeyQuoteTag, false);
    }
    
    public static String toString(Object o, String jsonObjectKeyQuoteTag,boolean jsonObjectValueEmptyShow)
    {
        return parseObject(o).toJsonString(jsonObjectKeyQuoteTag,jsonObjectValueEmptyShow);
    }
    
    private static int parseJsonObject(JsonObject jsonObject, String s)
    {
        int result;

        int oldLength = s.length();
        
        if(!s.startsWith(Json.JSON_OBJECT_STRING_START_TAG))
            throw new JSONException("parse JsonObject jsonString=" + s + "\n error: jsonObject start tag + '" + Json.JSON_OBJECT_STRING_START_TAG + "' not found.");
        s = s.substring(Json.JSON_OBJECT_STRING_START_TAG.length()).trim();

        while (true)
        {
            if (s.startsWith(JsonObject.JSON_OBJECT_STRING_END_TAG))
            {
                s = s.substring(JsonObject.JSON_OBJECT_STRING_END_TAG.length())
                        .trim();
                break;
            }

            int indexOfKeyValueSeparatorTag = s
                    .indexOf(JsonObject.JSON_OBJECT_KEY_VALUE_SEPARATOR_TAG);
            if (indexOfKeyValueSeparatorTag == -1)
            {
                throw new JSONException("parse jsonObject jsonString=" + s
                        + "\n error: jsonObject key value separator tag '"
                        + JsonObject.JSON_OBJECT_KEY_VALUE_SEPARATOR_TAG
                        + "' not found.");
            }

            // parse key
            String key = s.substring(0, indexOfKeyValueSeparatorTag).trim();
            if (key.startsWith(JsonObject.JSON_OBJECT_KEY_QUOTE_TAG_DOUBLE)
                    || key.startsWith(JsonObject.JSON_OBJECT_KEY_QUOTE_TAG_SINGLE))
            {
                if (key.startsWith(JsonObject.JSON_OBJECT_KEY_QUOTE_TAG_DOUBLE))
                {
                    key = key.substring(
                            JsonObject.JSON_OBJECT_KEY_QUOTE_TAG_DOUBLE
                                    .length()).trim();
                } else
                {
                    key = key.substring(
                            JsonObject.JSON_OBJECT_KEY_QUOTE_TAG_SINGLE
                                    .length()).trim();
                }
            }
            if (key.endsWith(JsonObject.JSON_OBJECT_KEY_QUOTE_TAG_DOUBLE)
                    || key.endsWith(JsonObject.JSON_OBJECT_KEY_QUOTE_TAG_SINGLE))
            {
                int keyLength = key.length();
                if (key.endsWith(JsonObject.JSON_OBJECT_KEY_QUOTE_TAG_DOUBLE))
                {
                    key = key
                            .substring(
                                    0,
                                    keyLength
                                            - JsonObject.JSON_OBJECT_KEY_QUOTE_TAG_DOUBLE
                                                    .length()).trim();
                } else
                {
                    key = key
                            .substring(
                                    0,
                                    keyLength
                                            - JsonObject.JSON_OBJECT_KEY_QUOTE_TAG_SINGLE
                                                    .length()).trim();
                }
            }
            if ("".equals(key))
            {
                throw new JSONException("parse jsonObject jsonString=" + s
                        + "\n error: jsonObject key should not be blank.");
            }

            s = s.substring(
                    indexOfKeyValueSeparatorTag
                            + JsonObject.JSON_OBJECT_KEY_VALUE_SEPARATOR_TAG
                                    .length()).trim();

            // parse value
            if (s.startsWith(Json.JSON_OBJECT_STRING_START_TAG))
            {
                JsonObject childJsonObject = new JsonObject();
                int length = parseJsonObject(childJsonObject, s);
                jsonObject.getMap().put(key, childJsonObject);
                s = s.substring(length).trim();
            } else if (s.startsWith(Json.JSON_ARRAY_STRING_START_TAG))
            {
                JsonArray childJsonArray = new JsonArray();
                int length = parseJsonArray(childJsonArray, s);
                jsonObject.getMap().put(key, childJsonArray);
                s = s.substring(length).trim();
            } 
            else
            {
                JsonString jsonString = new JsonString();
                int length = parseJsonString(jsonString, s, Json.JSON_OBJECT_VALUE_SEPARATOR_TAG, Json.JSON_OBJECT_STRING_END_TAG);
                jsonObject.getMap().put(key, jsonString);
                s = s.substring(length).trim();
            }

            if (s.startsWith(Json.JSON_OBJECT_VALUE_SEPARATOR_TAG))
            {
                s = s.substring(
                        Json.JSON_OBJECT_VALUE_SEPARATOR_TAG.length())
                        .trim();
            }
        }

        result = oldLength - s.length();
        return result;
    }
    
    private static int parseJsonArray(JsonArray jsonArray, String s)
    {
        int result;

        int oldLength = s.length();
        
        if(!s.startsWith(Json.JSON_ARRAY_STRING_START_TAG))
            throw new JSONException("parse JsonArray jsonString=" + s + "\n error: jsonArray start tag + '" + Json.JSON_ARRAY_STRING_START_TAG + "' not found.");
        s = s.substring(Json.JSON_ARRAY_STRING_START_TAG.length()).trim();

        while (true)
        {
            if (s.startsWith(Json.JSON_ARRAY_STRING_END_TAG))
            {
                s = s.substring(Json.JSON_ARRAY_STRING_END_TAG.length()).trim();
                break;
            }

            if (s.startsWith(Json.JSON_OBJECT_STRING_START_TAG))
            {
                JsonObject childJsonObject = new JsonObject();
                int length = parseJsonObject(childJsonObject, s);
                jsonArray.getList().add(childJsonObject);
                s = s.substring(length);
            } else if (s.startsWith(Json.JSON_ARRAY_STRING_START_TAG))
            {
                JsonArray childJsonArray = new JsonArray();
                int length = parseJsonArray(childJsonArray, s);
                jsonArray.getList().add(childJsonArray);
                s = s.substring(length);
            } else
            {
                JsonString jsonString = new JsonString();
                int length = parseJsonString(jsonString, s, Json.JSON_ARRAY_VALUE_SEPARATOR_TAG, Json.JSON_ARRAY_STRING_END_TAG);
                jsonArray.getList().add(jsonString);
                s = s.substring(length);
            }

            if (s.startsWith(JsonArray.JSON_ARRAY_VALUE_SEPARATOR_TAG))
            {
                s = s.substring(
                        JsonArray.JSON_ARRAY_VALUE_SEPARATOR_TAG.length())
                        .trim();
            }
        }

        result = oldLength - s.length();
        return result;
    }
    
    private static int parseJsonString(JsonString jsonString,String s, String separatorTag, String endTag)
    {
        int result;
        
        int oldLength = s.length();
        
        int indexOfEnd;
        if(s.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE)||s.startsWith(Json.JSON_STRING_QUOTE_TAG_SINGLE))
        {
            if(s.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE))
            {
                int fromIndex = Json.JSON_STRING_QUOTE_TAG_DOUBLE.length();
                do
                {
                    indexOfEnd = s.indexOf(Json.JSON_STRING_QUOTE_TAG_DOUBLE,fromIndex);
                    if(indexOfEnd<fromIndex)
                    {
                        throw new JSONException("parseJsonString illegal," + s +" starts with " + Json.JSON_STRING_QUOTE_TAG_DOUBLE + " not found end " + Json.JSON_STRING_QUOTE_TAG_DOUBLE);
                    }
                    else if(indexOfEnd==fromIndex)
                    {
                        break;
                    }
                    else
                    {
                        if(s.charAt(indexOfEnd-1)!='\\')
                        {
                            break;
                        }
                        else
                        {
                            fromIndex = indexOfEnd+Json.JSON_STRING_QUOTE_TAG_DOUBLE.length();
                        }
                    }
                }
                while(fromIndex<s.length());
                indexOfEnd += Json.JSON_STRING_QUOTE_TAG_DOUBLE.length();
            }
            else
            {
                int fromIndex = Json.JSON_STRING_QUOTE_TAG_SINGLE.length();
                do
                {
                    indexOfEnd = s.indexOf(Json.JSON_STRING_QUOTE_TAG_SINGLE,fromIndex);
                    if(indexOfEnd<fromIndex)
                    {
                        throw new JSONException("parseJsonString illegal," + s +" starts with " + Json.JSON_STRING_QUOTE_TAG_SINGLE + " not found end " + Json.JSON_STRING_QUOTE_TAG_SINGLE);
                    }
                    else if(indexOfEnd==fromIndex)
                    {
                        break;
                    }
                    else
                    {
                        if(s.charAt(indexOfEnd-1)!='\\')
                        {
                            break;
                        }
                        else
                        {
                            fromIndex = indexOfEnd+Json.JSON_STRING_QUOTE_TAG_SINGLE.length();
                        }
                    }
                }
                while(fromIndex<s.length());
                indexOfEnd += Json.JSON_STRING_QUOTE_TAG_SINGLE.length();
            }
        }
        else
        {
            indexOfEnd = s.indexOf(endTag);
            if (indexOfEnd == -1)
                throw new JSONException("parse jsonString=" + s
                    + " not found end tag '" +
                    endTag + "'.");
            int indexOfSeparatorTag = s
                .indexOf(separatorTag);
            if (indexOfSeparatorTag != -1
                && indexOfSeparatorTag < indexOfEnd)
            {
                indexOfEnd = indexOfSeparatorTag;
            }
        }
        
        String valueString = s.substring(0, indexOfEnd).trim();
        if((valueString.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE)&&valueString.endsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE))
                ||(valueString.startsWith(Json.JSON_STRING_QUOTE_TAG_SINGLE)&&valueString.endsWith(Json.JSON_STRING_QUOTE_TAG_SINGLE)))
        {
            int length;
            if(valueString.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE)&&valueString.endsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE))
            {
                length = Json.JSON_STRING_QUOTE_TAG_DOUBLE.length();
            }
            else
            {
                length = Json.JSON_STRING_QUOTE_TAG_SINGLE.length();
            }
            valueString = Json.JSON_STRING_QUOTE_TAG_DOUBLE + valueString.substring(length,valueString.length()-length) + Json.JSON_STRING_QUOTE_TAG_DOUBLE;
        }
        jsonString.setS(valueString);
        s = s.substring(indexOfEnd);
        
        result = oldLength - s.length();
        return result;
    }

    public static Json parseString(String s)
    {
        Json result;
        
        if(s!=null)
        {
            s = s.trim();
        }
        
        if(s==null)
        {
            result = new JsonString(Json.JSON_STRING_VALUE_NULL);
        }
        else if(s.startsWith(Json.JSON_ARRAY_STRING_START_TAG)&&s.endsWith(Json.JSON_ARRAY_STRING_END_TAG))
        {
            JsonArray jsonArray = new JsonArray();
            parseJsonArray(jsonArray, s);
            result = jsonArray;
        }
        else if(s.startsWith(Json.JSON_OBJECT_STRING_START_TAG)&&s.endsWith(Json.JSON_OBJECT_STRING_END_TAG))
        {
            JsonObject jsonObject = new JsonObject();
            parseJsonObject(jsonObject, s);
            result = jsonObject;
        }
        else if((s.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE)&&s.endsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE))
                ||(s.startsWith(Json.JSON_STRING_QUOTE_TAG_SINGLE)&&s.endsWith(Json.JSON_STRING_QUOTE_TAG_SINGLE)))
        {
            int length;
            if(s.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE)&&s.endsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE))
            {
                length = Json.JSON_STRING_QUOTE_TAG_DOUBLE.length();
            }
            else
            {
                length = Json.JSON_STRING_QUOTE_TAG_SINGLE.length();
            }
            s = Json.JSON_STRING_QUOTE_TAG_DOUBLE + s.substring(length,s.length()-length) + Json.JSON_STRING_QUOTE_TAG_DOUBLE;
            result = new JsonString(s);
        }
        else if(Json.JSON_STRING_VALUE_NULL.equalsIgnoreCase(s))
        {
            result = new JsonString(Json.JSON_STRING_VALUE_NULL);
        }
        else if(Json.JSON_STRING_VALUE_TRUE.equalsIgnoreCase(s)||Json.JSON_STRING_VALUE_FALSE.equalsIgnoreCase(s))
        {
            result = new JsonString(Boolean.parseBoolean(s)+"");
        }
        else if(MathUtil.isNumber(s))
        {
            result = new JsonString(s);
        }
        else
        {
            result = new JsonString(Json.JSON_STRING_QUOTE_TAG_DOUBLE+s+Json.JSON_STRING_QUOTE_TAG_DOUBLE);
        }
        return result;
    }
    
    public static Json parseObject(Object o)
    {
        Json result;
        
        if(o==null)
        {
            result = new JsonString(Json.JSON_STRING_VALUE_NULL);
        }
        else if(o instanceof Date)
        {
            result = new JsonString(Json.JSON_STRING_QUOTE_TAG_DOUBLE + DatetimeUtil.longToDateTimeString(((Date)o).getTime(), JSON_STRING_DATE_FORMAT) + Json.JSON_STRING_QUOTE_TAG_DOUBLE);
        }
        else if(o instanceof Enum||o instanceof String || o instanceof Character)
        {
            result = new JsonString(Json.JSON_STRING_QUOTE_TAG_DOUBLE + o.toString() + Json.JSON_STRING_QUOTE_TAG_DOUBLE);
        }
        else if(o instanceof Boolean || o instanceof Byte||
                o instanceof Integer || o instanceof Short || o instanceof Long || o instanceof Float || o instanceof Double|| o instanceof Number)
        {
            result = new JsonString(o.toString());
        }
        else if(o instanceof Collection || o instanceof Object[])
        {
            List<Json> list = new ArrayList<Json>();
            if(o instanceof Collection)
            {
                Collection<?> collection = (Collection<?>)o;
                for(Object obj : collection)
                {
                    Json json = parseObject(obj);
                    list.add(json);
                }
            }
            else
            {
                Object[] array = (Object[])o;
                for(Object obj : array)
                {
                    Json json = parseObject(obj);
                    list.add(json);
                }
            }
            result = new JsonArray(list);
        }
        else if(o instanceof Map)
        {
            Map<String, Json> map = new LinkedHashMap<String, Json>();
            Map<?, ?> objectMap = (Map<?,?>)o;
            Set<?> keySet = objectMap.keySet();
            for(Object key : keySet)
            {
                Object objectValue = objectMap.get(key);
                if(key!=null&&!"".equals(key.toString().trim()))
                {
                    map.put(key.toString(), parseObject(objectValue));
                }
            }
            result = new JsonObject(map);
        }
        else if(ReflectUtil.isModel(o, null))
        {
            Map<String, Json> map = new LinkedHashMap<String, Json>();
            
            // get filed and getMethod map
            Class<?> clazz = o.getClass();
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
                    Class<DatetimeFormatAnnotation> datetimeFormatAnnotationClass = DatetimeFormatAnnotation.class;
                    if(field.isAnnotationPresent(datetimeFormatAnnotationClass))
                    {
                        DatetimeFormat datetimeFormat = field.getAnnotation(datetimeFormatAnnotationClass).value();
                        fieldValue = DatetimeUtil.longToDateTimeString(((Date)fieldValue).getTime(), datetimeFormat);
                    }
                }
                
                if(!map.containsKey(fieldName))
                {
//                    if(fieldValue==null)
//                    {
//                        Class<EmptyShowAnnotation> emptyShowAnnotationClass = EmptyShowAnnotation.class;
//                        if(clazz.isAnnotationPresent(emptyShowAnnotationClass)||field.isAnnotationPresent(emptyShowAnnotationClass))
//                        {
//                            map.put(fieldName, parseObject(fieldValue));
//                        }
//                    }
//                    else
//                    {
//                        map.put(fieldName, parseObject(fieldValue));
//                    }
                    map.put(fieldName, parseObject(fieldValue));
                }
            }
            
            result = new JsonObject(map);
        }
        else
        {
            result = new JsonString(Json.JSON_STRING_QUOTE_TAG_DOUBLE + o.toString()+Json.JSON_STRING_QUOTE_TAG_DOUBLE);
        }
        return result;
    }
    
    /**
     * 通过指定的key(用.和[]组合来拼接key，例如refKey=a.b[0][0].c[0][0][0].d.e[1],
     * json={"a":{"b":[[{"c":[[[{"d":{"e":[3,9,7]}}]]]}]]}}，
     * 结果为9
     * @param encoding json字符编码
     * @param byteData json字符二进制数据
     * @param refKey json中指定字段的key
     * @return 根据key返回值
     * @throws JSONException 抛出JSONException封装获取数据过程中key不合法的各种错误情况
     */
    public static String getRefValue(String encoding, byte[] byteData, String refKey) throws JSONException
    {
        String json = new String(byteData,Charset.forName(encoding));
        try {
            Object obj = JSONUtil.toObject(json, null);
            Object value;
            if(obj instanceof Map)
            {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) obj;
                value = getMapRefValue(map, refKey);
            }
            else if(obj instanceof List)
            {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) obj;
                value = getListIndexValue(list, refKey);
            }
            else
            {
                throw new JSONException("JSON string getRefValue json illegal,json="+json);
            }
            if(value instanceof Map||value instanceof List)
            {
                return JSONUtil.toString(value);
            }
            else
            {
                return StringUtil.toString(value);
            }
        } catch (Exception e) {
            JSONException te = (e instanceof JSONException)?(JSONException)e:new JSONException("JSON string getRefValue illegal,refKey="+refKey+",json="+json);
            throw te;
        }
    }
    
    private static Object getMapRefValue(Map<String, Object> map, String refKey) {
        Object result = null;
        
        String separator1 = ".";
        String arrayStartTag = "[";
        String arrayEndTag = "]";
        
        if(CodeUtil.isEmpty(map,refKey))
        {
           throw new JSONException("JSONObject string getRefValue param empty"); 
        }
        List<String> refKeyContainsEmptyList = StringUtil.stringToList(refKey, separator1);
        List<String> refKeyList = new ArrayList<String>();
        for (String one : refKeyContainsEmptyList) {
            if(!CodeUtil.isEmpty(one))
            {
                refKeyList.add(one);
            }
        }
        int index=0;
        Map<String, Object> tempMap = new LinkedHashMap<String, Object>(map);
        while(index<refKeyList.size())
        {
            String keyNotHandlerArrayTag = refKeyList.get(index++);
            String key = keyNotHandlerArrayTag;
            int indexOfArrayStartTag = keyNotHandlerArrayTag.indexOf(arrayStartTag);
            if(indexOfArrayStartTag>=0)
            {
                int indexOfArrayEndTag = keyNotHandlerArrayTag.indexOf(arrayEndTag,indexOfArrayStartTag);
                if(indexOfArrayEndTag<indexOfArrayStartTag)
                {
                    throw new JSONException("JSONObject string getRefValue refKey illegal,refKey="+keyNotHandlerArrayTag);
                }
                key = keyNotHandlerArrayTag.substring(0,indexOfArrayStartTag);
            }
            
            Object value = tempMap.get(key);
            if(value==null)
            {
                result = null;
                break;
            }
            else
            {
                if(index==refKeyList.size())
                {
                    if(indexOfArrayStartTag<0)
                    {
                    }
                    else
                    {
                        if(value instanceof List)
                        {
                            @SuppressWarnings("unchecked")
                            List<Object> list = (List<Object>) value;
                            String refIndex = keyNotHandlerArrayTag.substring(indexOfArrayStartTag);
                            value = getListIndexValue(list, refIndex);
                        }
                        else
                        {
                            throw new JSONException("JSONObject string getRefValue refKey json match illegal,refKey="+keyNotHandlerArrayTag+",json value clazz="+value.getClass());
                        }
                    }
                    result = value;
                }
                else
                {
                    if(indexOfArrayStartTag<0)
                    {
                        if(value instanceof Map)
                        {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> map1 = (Map<String, Object>) value;
                            tempMap = map1;
                        }
                        else
                        {
                            throw new JSONException("JSONObject string getRefValue refKey json match illegal,expected json value clazz="+Map.class+",real json value clazz="+value.getClass());
                        }
                    }
                    else
                    {
                        if(value instanceof List)
                        {
                            @SuppressWarnings("unchecked")
                            List<Object> list = (List<Object>) value;
                            String refIndex = keyNotHandlerArrayTag.substring(indexOfArrayStartTag);
                            value = getListIndexValue(list, refIndex);
                            if(value instanceof Map)
                            {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> map1 = (Map<String, Object>) value;
                                tempMap = map1;
                            }
                            else
                            {
                                throw new JSONException("JSONObject string getRefValue refKey json match illegal,expected json value clazz="+Map.class+",real json value clazz="+value.getClass());
                            }
                        }
                        else
                        {
                            throw new JSONException("JSONObject string getRefValue refKey json match illegal,refKey="+keyNotHandlerArrayTag+",json value clazz="+value.getClass());
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    private static Object getListIndexValue(List<Object> list, String refIndex) {
        Object result = null;
        
        String arrayStartTag = "[";
        String arrayEndTag = "]";
        
        if(CodeUtil.isEmpty(list,refIndex))
            throw new JSONException("JSONArray string getRefValue param empty"); 
        
        int indexOfArrayStartTag = refIndex.indexOf(arrayStartTag);
        if(indexOfArrayStartTag<0)
        {
            throw new JSONException("JSONArray string getRefValue refKey illegal,refKey="+refIndex); 
        }
        int indexOfArrayEndTag = refIndex.indexOf(arrayEndTag,indexOfArrayStartTag);
        if(indexOfArrayEndTag<=indexOfArrayStartTag)
        {
            throw new JSONException("JSONArray string getRefValue refKey illegal,refKey="+refIndex); 
        }
        Integer arrayIndex = null;
        String arrayIndexString = refIndex.substring(indexOfArrayStartTag+arrayStartTag.length(),indexOfArrayEndTag);
        try {
            arrayIndex = Integer.parseInt(arrayIndexString);
        } catch (Exception e) {
            throw new JSONException("JSONArray string getRefValue refKey is illegal number,refIndex="+arrayIndexString); 
        }
        
        if(arrayIndex>list.size())
        {
            throw new JSONException("JSONArray string getRefValue refKey is illegal number,refIndex="+arrayIndex+",array length="+list.size()); 
        }
        Object value = list.get(arrayIndex);
        if(value==null)
        {
            result = null;
        }
        else
        {
            int indexOfArrayStartTag2 = refIndex.indexOf(arrayStartTag, indexOfArrayEndTag);
            if(indexOfArrayStartTag2>indexOfArrayEndTag)
            {
                if(value instanceof Map)
                {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) value;
                    String refKey = refIndex.substring(indexOfArrayEndTag+arrayEndTag.length());
                    return getMapRefValue(map, refKey);
                }
                if(value instanceof List)
                {
                    @SuppressWarnings("unchecked")
                    List<Object> list1 = (List<Object>) value;
                    refIndex = refIndex.substring(indexOfArrayEndTag+arrayEndTag.length());
                    return getListIndexValue(list1, refIndex);
                }
                else
                {
                    throw new JSONException("JSONArray string getRefValue refKey json match illegal,expected json value clazz="+Map.class+",or "+List.class+",real json value clazz="+value.getClass());
                }
            }
            else
            {
                result = value;
            }
        }
        return result;
    }
    
    public static interface Json
    {
        String JSON_OBJECT_STRING_START_TAG = "{";
        String JSON_OBJECT_STRING_END_TAG = "}";
        String JSON_OBJECT_KEY_QUOTE_TAG_DOUBLE = "\"";
        String JSON_OBJECT_KEY_QUOTE_TAG_SINGLE = "'";
        String JSON_OBJECT_KEY_VALUE_SEPARATOR_TAG = ":";
        String JSON_OBJECT_VALUE_SEPARATOR_TAG = ",";
        
        String JSON_ARRAY_STRING_START_TAG = "[";
        String JSON_ARRAY_STRING_END_TAG = "]";
        String JSON_ARRAY_VALUE_SEPARATOR_TAG = ",";
        
        String JSON_STRING_QUOTE_TAG_DOUBLE = "\"";
        String JSON_STRING_QUOTE_TAG_SINGLE = "'";
        String JSON_STRING_VALUE_NULL = "null";
        String JSON_STRING_VALUE_TRUE = "true";
        String JSON_STRING_VALUE_FALSE = "false";
        
        String JSON_STRING_SPECIAL_TAG_QUOTE = "\\\"";
        String JSON_STRING_SPECIAL_TAG_CONVERT = "\\\\";
        String JSON_STRING_SPECIAL_TAG_REVERSE_CONVERT = "\\/";
        String JSON_STRING_SPECIAL_TAG_B = "\\b";
        String JSON_STRING_SPECIAL_TAG_F = "\\f";
        String JSON_STRING_SPECIAL_TAG_N = "\\n";
        String JSON_STRING_SPECIAL_TAG_R = "\\r";
        String JSON_STRING_SPECIAL_TAG_T = "\\t";
        
        String JSON_STRING_SPECIAL_TAG_QUOTE_VALUE = "\"";
        String JSON_STRING_SPECIAL_TAG_CONVERT_VALUE = "\\";
        String JSON_STRING_SPECIAL_TAG_REVERSE_CONVERT_VALUE = "/";
        String JSON_STRING_SPECIAL_TAG_B_VALUE = "\b";
        String JSON_STRING_SPECIAL_TAG_F_VALUE = "\f";
        String JSON_STRING_SPECIAL_TAG_N_VALUE = "\n";
        String JSON_STRING_SPECIAL_TAG_R_VALUE = "\r";
        String JSON_STRING_SPECIAL_TAG_T_VALUE = "\t";
        
        /**
         * 获取标准的JSON字符串
         * @author wangwwy
         * createdDatetime 2014年9月16日 下午11:21:37
         * @param jsonObjectKeyQuoteTag
         * @return
         */
        String toJsonString(String jsonObjectKeyQuoteTag, boolean jsonObjectValueEmptyShow);
        
        /**
         * 获取JSON对象转化成的JAVA对象
         * @author wangwwy
         * createdDatetime 2014年9月16日 下午11:21:49
         * @param expectedClazz
         * @return
         */
        Object toJsonObject(Class<?> expectedClazz);
    }
    
    public static class JsonArray implements Json
    {
        private List<Json> list = new LinkedList<Json>();

        public JsonArray()
        {
        }
        
        public JsonArray(List<Json> list)
        {
            this.list = list;
        }
        
        public Object toJsonObject(Class<?> expectedClazz) {
            
            Object result;
            
            if(expectedClazz==null||Collections.class.isAssignableFrom(expectedClazz))
            {
                result = toObject(this);
            }
            else if(expectedClazz.isArray())
            {
                result = getArrayValue(expectedClazz);
            }
            else
            {
                result = null;
            }
            return result;
        }

        public String toJsonString(String jsonObjectKeyQuoteTag, boolean jsonObjectValueEmptyShow)
        {
            StringBuffer sb = new StringBuffer(JSON_ARRAY_STRING_START_TAG);

            int i=0;
            for (Json json : list)
            {
                if(i>0)
                {
                    sb.append(Json.JSON_ARRAY_VALUE_SEPARATOR_TAG);
                }
                i++;
                
                String value = json.toJsonString(jsonObjectKeyQuoteTag,jsonObjectValueEmptyShow);
                sb.append(value);
            }

            sb.append(JSON_ARRAY_STRING_END_TAG);
            return sb.toString();
        }
        
        public Object[] getArrayValue(Class<?> expectedClazz)
        {
            Class<?> clazz = expectedClazz.getComponentType();
            int size = list.size();
            Object[] array = (Object[]) Array.newInstance(clazz, size);
            for(int i=0; i<size; i++)
            {
                Json json = list.get(i);
                array[i] = json==null?null:json.toJsonObject(clazz);
            }
            return array;
        }
        
        public <T> Collection<T> getCollectionValue(Class<T> expectedClazz)
        {
            Collection<T> result = new ArrayList<T>();
            for(Json json : list)
            {
                if(json==null)
                {
                    result.add(null);
                }
                else
                {
                    @SuppressWarnings("unchecked")
                    T t = (T) json.toJsonObject(expectedClazz);
                    result.add(t);
                }
            }
            return result;
        }

        public List<Json> getList()
        {
            return list;
        }

        public void setList(List<Json> list)
        {
            this.list = list;
        }
    }
    
    public static class JsonString implements Json
    {
        private String s;

        public JsonString()
        {
            
        }

        public JsonString(String s)
        {
            this.s = s;
        }
        
        public Object toJsonObject(Class<?> clazz)
        {
            return getValue(clazz);
        }

        public String toJsonString(String jsonObjectKeyQuoteTag, boolean jsonObjectValueEmptyShow)
        {
            String result;
            
            String value = s.trim();
            if(Json.JSON_STRING_VALUE_NULL.equalsIgnoreCase(value))
            {
                result = Json.JSON_STRING_VALUE_NULL;
            }
            else if(Json.JSON_STRING_VALUE_TRUE.equalsIgnoreCase(value)||Json.JSON_STRING_VALUE_FALSE.equalsIgnoreCase(value))
            {
                result = Boolean.parseBoolean(value)+"";
            }
            else if(MathUtil.isNumber(value))
            {
                result = value;
            }
            else
            {
                // 去掉首尾对称的双引号
                int length;
                if(value.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE)&&value.endsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE))
                {
                    length = Json.JSON_STRING_QUOTE_TAG_DOUBLE.length();
                    value = value.substring(length, value.length()-length);
                }
                
                // 为特殊符号添加转义字符
//                value = StringUtil.convertStringSpecialTag(value, Json.JSON_STRING_SPECIAL_TAG_CONVERT_VALUE, Json.JSON_STRING_SPECIAL_TAG_CONVERT_VALUE);
//                value = StringUtil.convertStringSpecialTag(value, Json.JSON_STRING_SPECIAL_TAG_QUOTE_VALUE, Json.JSON_STRING_SPECIAL_TAG_CONVERT_VALUE);
//                value = StringUtil.convertStringSpecialTag(value, Json.JSON_STRING_SPECIAL_TAG_REVERSE_CONVERT_VALUE, Json.JSON_STRING_SPECIAL_TAG_CONVERT_VALUE);
//                value = StringUtil.replaceString(value, Json.JSON_STRING_SPECIAL_TAG_B_VALUE, Json.JSON_STRING_SPECIAL_TAG_B);
//                value = StringUtil.replaceString(value, Json.JSON_STRING_SPECIAL_TAG_F_VALUE, Json.JSON_STRING_SPECIAL_TAG_F);
//                value = StringUtil.replaceString(value, Json.JSON_STRING_SPECIAL_TAG_N_VALUE, Json.JSON_STRING_SPECIAL_TAG_N);
//                value = StringUtil.replaceString(value, Json.JSON_STRING_SPECIAL_TAG_R_VALUE, Json.JSON_STRING_SPECIAL_TAG_R);
//                value = StringUtil.replaceString(value, Json.JSON_STRING_SPECIAL_TAG_T_VALUE, Json.JSON_STRING_SPECIAL_TAG_T);
                
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_CONVERT_VALUE, Json.JSON_STRING_SPECIAL_TAG_CONVERT);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_QUOTE_VALUE, Json.JSON_STRING_SPECIAL_TAG_QUOTE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_REVERSE_CONVERT_VALUE, Json.JSON_STRING_SPECIAL_TAG_REVERSE_CONVERT);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_B_VALUE, Json.JSON_STRING_SPECIAL_TAG_B);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_F_VALUE, Json.JSON_STRING_SPECIAL_TAG_F);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_N_VALUE, Json.JSON_STRING_SPECIAL_TAG_N);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_R_VALUE, Json.JSON_STRING_SPECIAL_TAG_R);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_T_VALUE, Json.JSON_STRING_SPECIAL_TAG_T);
                
                // 在字符串首尾加上成对的双引号
                result = Json.JSON_STRING_QUOTE_TAG_DOUBLE 
                        + value
                        + Json.JSON_STRING_QUOTE_TAG_DOUBLE;
            }
            return result;
        }
        
//        /**
//         * 去掉首尾对称的双单引号
//         * @param s
//         * @return
//         */
//        private static String handleJsonString(String s)
//        {
//            s = s.trim();
//            if((s.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE)&&s.endsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE))
//                    ||(s.startsWith(Json.JSON_STRING_QUOTE_TAG_SINGLE)&&s.endsWith(Json.JSON_STRING_QUOTE_TAG_SINGLE)))
//            {
//                int length;
//                if(s.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE)&&s.endsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE))
//                {
//                    length = Json.JSON_STRING_QUOTE_TAG_DOUBLE.length();
//                }
//                else
//                {
//                    length = Json.JSON_STRING_QUOTE_TAG_SINGLE.length();
//                }
//                s = s.substring(length,s.length()-length);
//            }
//            else
//            {
////              throw new JSONException("JsonString illegal,s="+s);
//            }
//            return s;
//        }
        
        public <T> Object getValue(Class<T> clazz)
        {
            T result;
            
            if(s==null)
                return null;
            
            String value = s.trim();
            if(Json.JSON_STRING_VALUE_NULL.equalsIgnoreCase(value))
            {
                result = null;
            }
            else if((Boolean.class.isInstance(clazz)||boolean.class.isInstance(clazz))
            		&&(Json.JSON_STRING_VALUE_TRUE.equalsIgnoreCase(value)||Json.JSON_STRING_VALUE_FALSE.equalsIgnoreCase(value)))
            {
                Boolean booleanValue = Boolean.parseBoolean(value);
                @SuppressWarnings("unchecked")
                T t = (T) booleanValue;
                result = t;
            }
            else if(MathUtil.isNumber(value,clazz))
            {
                @SuppressWarnings("unchecked")
                Class<? extends Number> numberClazz = (Class<? extends Number>) clazz;
                Number numberValue = MathUtil.getNumber(value, numberClazz);
                @SuppressWarnings("unchecked")
                T t = (T) numberValue;
                result = t;
            }
            else
            {
                // 去掉首尾对称的双引号
                int length;
                if(value.startsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE)&&value.endsWith(Json.JSON_STRING_QUOTE_TAG_DOUBLE))
                {
                    length = Json.JSON_STRING_QUOTE_TAG_DOUBLE.length();
                    value = value.substring(length, value.length()-length);
                }
                
                // 去掉特殊符号前的转义字符
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_CONVERT, Json.JSON_STRING_SPECIAL_TAG_CONVERT_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_B, Json.JSON_STRING_SPECIAL_TAG_B_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_F, Json.JSON_STRING_SPECIAL_TAG_F_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_N, Json.JSON_STRING_SPECIAL_TAG_N_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_QUOTE, Json.JSON_STRING_SPECIAL_TAG_QUOTE_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_R, Json.JSON_STRING_SPECIAL_TAG_R_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_REVERSE_CONVERT, Json.JSON_STRING_SPECIAL_TAG_REVERSE_CONVERT_VALUE);
                value = value.replace(Json.JSON_STRING_SPECIAL_TAG_T, Json.JSON_STRING_SPECIAL_TAG_T_VALUE);
                
                result = StringUtil.getValueFromString(value, clazz);
            }
            return result;
        }
        
        public String getS() {
            return s;
        }
        public void setS(String s) {
            this.s = s;
        }
    }
    
    public static class JsonObject implements Json
    {
        private Map<String, Json> map = new LinkedHashMap<String, Json>();
        
        public JsonObject()
        {
        }
        
        public JsonObject(Map<String,Json> map)
        {
            this.map = map;
        }
        
        public Object toJsonObject(Class<?> expectedClazz) {
            
            Object result;
            if(expectedClazz==null||Map.class.isAssignableFrom(expectedClazz))
            {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) toObject(this);
                result = map;
            }
            else
            {
                result = getModel(expectedClazz);
            }
            return result;
        }
        
        public String toJsonString(String jsonObjectKeyQuoteTag, boolean jsonObjectValueEmptyShow)
        {
            StringBuffer sb = new StringBuffer(JSON_OBJECT_STRING_START_TAG);

            Set<Entry<String, Json>> entrySet = map.entrySet();
            for (Entry<String, Json> entry : entrySet)
            {
                String key = entry.getKey().trim();
                Json json = entry.getValue();

                if (key != null && !"".equals(key))
                {
                    if (!key.startsWith(jsonObjectKeyQuoteTag))
                    {
                        key = jsonObjectKeyQuoteTag + key;
                    }
                    if (!key.endsWith(jsonObjectKeyQuoteTag))
                    {
                        key = key + jsonObjectKeyQuoteTag;
                    }

                    String value = json.toJsonString(jsonObjectKeyQuoteTag,jsonObjectValueEmptyShow);
                    if(Json.JSON_STRING_VALUE_NULL.equals(value)&&!jsonObjectValueEmptyShow)
                    {
                        continue;
                    }
                    sb.append(key).append(JSON_OBJECT_KEY_VALUE_SEPARATOR_TAG)
                            .append(value).append(Json.JSON_OBJECT_VALUE_SEPARATOR_TAG);
                }
            }
            
            int lastIndexOf = sb.lastIndexOf(Json.JSON_OBJECT_VALUE_SEPARATOR_TAG);
            if(lastIndexOf>=0)
            {
                sb.delete(lastIndexOf,lastIndexOf+Json.JSON_OBJECT_VALUE_SEPARATOR_TAG.length());
            }
            
            sb.append(JSON_OBJECT_STRING_END_TAG);
            return sb.toString();
        }

        public Map<String, Json> getMap()
        {
            return map;
        }

        public void setMap(Map<String, Json> map)
        {
            this.map = map;
        }

        public <T> T getModel(Class<T> clazz)
        {
            T result = null;

            result = ReflectUtil.newModel(clazz);

            // get filed and setMethod map
            Map<Field, Method> fieldAndSetmethodMap = ReflectUtil.getFieldAndSetmethodMap(clazz);

            // initialize value
            Set<Entry<Field, Method>> entrySet = fieldAndSetmethodMap.entrySet();
            for (Entry<Field, Method> entry : entrySet)
            {
                Field field = entry.getKey();
                Class<?> fieldType = field.getType();
                
                String mapKey = ReflectUtil.getFieldSetMethodAliasName(clazz,field);
                
                Object fieldValue = null;
                
                Json json = map.get(mapKey);
                if(json instanceof JsonArray)
                {
                    JsonArray jsonArray = (JsonArray)json;
                    if(Collection.class.isAssignableFrom(fieldType))
                    {
                        Class<?> oneClazz = (Class<?>) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                        fieldValue = jsonArray.getCollectionValue(oneClazz);
                    }
                    else
                    {
                        fieldValue = jsonArray.getArrayValue(fieldType);
                    }
                }
                else if(json instanceof JsonObject)
                {
                    JsonObject jsonObject = (JsonObject)json;
                    if(Map.class.isAssignableFrom(fieldType))
                    {
                        Class<?> oneClazz = (Class<?>) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[1];
                        fieldValue = jsonObject.getMapValue(oneClazz);
                    }
                    else
                    {
                        fieldValue = jsonObject.getModel(fieldType);
                    }
                }
                else if(json instanceof JsonString)
                {
                    JsonString jsonString = (JsonString)json;
                    String stringValue = jsonString.getS().trim();
                    
                    // 去掉首尾对称的双引号
                    if((stringValue.startsWith(JSON_STRING_QUOTE_TAG_DOUBLE)&&stringValue.endsWith(JSON_STRING_QUOTE_TAG_DOUBLE))
                            ||stringValue.startsWith(JSON_STRING_QUOTE_TAG_SINGLE)&&stringValue.endsWith(JSON_OBJECT_KEY_QUOTE_TAG_SINGLE))
                    {
                        int length = stringValue.startsWith(JSON_STRING_QUOTE_TAG_DOUBLE)?JSON_STRING_QUOTE_TAG_DOUBLE.length():JSON_STRING_QUOTE_TAG_SINGLE.length();
                        stringValue = stringValue.substring(length, stringValue.length()-length).trim();
                    }
                    
                    if(Date.class.isAssignableFrom(fieldType))
                    {
                        DatetimeFormat datetimeFormat = JSON_STRING_DATE_FORMAT;
                        Class<DatetimeFormatAnnotation> dateFormatAnnotationClass = DatetimeFormatAnnotation.class;
                        if(field.isAnnotationPresent(dateFormatAnnotationClass))
                        {
                            datetimeFormat = field.getAnnotation(dateFormatAnnotationClass).value();
                        }
                        fieldValue = new Date(DatetimeUtil.dateTimeStringToLong(stringValue, datetimeFormat));
                    }
                    else
                    {
                        fieldValue = jsonString.getValue(fieldType);
                    }
                }
                
                if(fieldValue!=null)
                {
                    ReflectUtil.setMethodInvoke(result, mapKey, fieldValue);
                }
            }
            return result;
        }
        
        public <T> Map<String, T> getMapValue(Class<T> fieldType)
        {
            Map<String, T> result = new LinkedHashMap<String, T>();
            for(Entry<String, Json> entry : map.entrySet())
            {
                String key = entry.getKey();
                Json json = entry.getValue();
                
                @SuppressWarnings("unchecked")
                T fieldValue = (T) json.toJsonObject(fieldType);
                
                if(fieldValue!=null)
                {
                    result.put(key, fieldValue);
                }
            }
            return result;
        }
    }
    
    public static void main(String[] args) {
        String s = "'9sa'";
        System.out.println(s.substring(1,s.length()-1));
    }
}