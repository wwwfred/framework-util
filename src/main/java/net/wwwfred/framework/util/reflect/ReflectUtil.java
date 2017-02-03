package net.wwwfred.framework.util.reflect;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.wwwfred.framework.util.code.CodeUtil;
import net.wwwfred.framework.util.properties.PropertiesUtil;
import net.wwwfred.framework.util.string.StringUtil;

/**
 * 
 * @author wangwenwu
 * 2014年9月9日 下午4:11:09
 */
public class ReflectUtil
{
    /**
     * 属性别名配置文件名
     */
    public static String fieldAliasNameConfigFileName = "fieldAliasName.properties";
    /** 配置文件中属性别名配置Map */
    public static Map<String, String> fieldAliasNameMap;
    
	/**
	 * 当输出一个类的属性时，属性排序规则比较器，默认为根据FieldSortAnnotation注解的值排序
	 */
	private static Comparator<Field> FIELD_SORT_COMPARATOR = getFieldSortComparator(); 
	
	/**
	 * 对象的属性及其GET方法组成的Map
	 */
	private static Map<Class<?>,Map<Field, Method>> FIELD_AND_GET_METHOD_MAP = new HashMap<Class<?>, Map<Field,Method>>();
	/**
	 * 对象的属性名及其GET方法组成的Map
	 */
	private static Map<Class<?>,Map<String, Method>> FIELD_NAME_AND_GET_METHOD_MAP = new HashMap<Class<?>, Map<String,Method>>();
	/**
	 * 对象的属性及其SET方法组成的Map
	 */
	private static Map<Class<?>,Map<Field, Method>> FIELD_AND_SET_METHOD_MAP = new HashMap<Class<?>, Map<Field,Method>>();
	/**
	 * 对象的属性名及其SET方法组成的Map
	 */
	private static Map<Class<?>,Map<String, Method>> FIELD_NAME_AND_SET_METHOD_MAP = new HashMap<Class<?>, Map<String,Method>>();
	
	/**
	 * 设置类属性输出的排序比较器
	 * createdDatetime 2014年9月9日 下午4:11:09
	 * @param fIELD_SORT_COMPARATOR
	 */
	public static void setFIELD_SORT_COMPARATOR(
			Comparator<Field> fIELD_SORT_COMPARATOR) {
		FIELD_SORT_COMPARATOR = fIELD_SORT_COMPARATOR;
	}
	
	/**
	 * 获取属性的getMethod别名，当没获取到别名采用FieldName,顺序：配置文件，AliasGetMethodAnnotation,AliasAnnotation
	 * @param clazz
	 * @param field
	 * @return
	 */
	public static String getFieldGetMethodAliasName(Class<?> clazz,Field field)
	{
	    String result = getFieldGetMethodAliasName(field);
        
	    if(fieldAliasNameMap==null)
	    {
	        fieldAliasNameMap = PropertiesUtil.getPropertiesMap(fieldAliasNameConfigFileName);
	    }
	    String fieldName = clazz.getName()+".get."+field.getName();
        String value = fieldAliasNameMap.get(fieldName);
        if(!CodeUtil.isEmpty(value))
        {
            result = value;
        }
	    
	    return result;
	}
	
	/**
     * 获取属性的getMethod别名，当没获取到别名采用FieldName
     * 2015年03月20日  下午12:53:33
     * return String
     */
	private static String getFieldGetMethodAliasName(Field field)
	{
	    String result = null;
        
	    Class<? extends Annotation> aliasAnnotationClazz;
	    aliasAnnotationClazz = AliasGetMethodAnnotation.class;
        if(field.isAnnotationPresent(aliasAnnotationClazz))
        {
            result = field.getAnnotation(AliasGetMethodAnnotation.class).value();
        }
        if(CodeUtil.isEmpty(result))
        {
            result = getFieldAliasName(field);
        }
        return result;
	}
	
	/**
	 * 获取属性的setMethod别名，当没获取到别名采用FieldName,顺序：配置文件，AliasGetMethodAnnotation,AliasAnnotation
	 * @param clazz
	 * @param field
	 * @return
	 */
	public static String getFieldSetMethodAliasName(Class<?> clazz,Field field)
	{
	    String result = getFieldSetMethodAliasName(field);
        
	    if(fieldAliasNameMap==null)
	    {
	        fieldAliasNameMap = PropertiesUtil.getPropertiesMap(fieldAliasNameConfigFileName);
	    }
        String fieldName = clazz.getName()+".set."+field.getName();
        String value = fieldAliasNameMap.get(fieldName);
        if(!CodeUtil.isEmpty(value))
        {
            result = value;
        }
        
        return result;
	}
	
	   /**
     * 获取属性的setMethod别名，当没获取到别名采用FieldName
     * 2015年03月20日  下午12:53:33
     * return String
     */
    private static String getFieldSetMethodAliasName(Field field)
    {
        String result = null;
        
        Class<? extends Annotation> aliasAnnotationClazz;
        aliasAnnotationClazz = AliasSetMethodAnnotation.class;
        if(field.isAnnotationPresent(aliasAnnotationClazz))
        {
            result = field.getAnnotation(AliasSetMethodAnnotation.class).value();
        }
        if(CodeUtil.isEmpty(result))
        {
            result = getFieldAliasName(field);
        }
        return result;
    }
	
    /**
     * 获取类的别名
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getClassAliasName(Class<?> objectClazz)
    {
        String result = "";
        
        Class<? extends Annotation> aliasAnnotationClazz;
        aliasAnnotationClazz = AliasAnnotation.class;
        if(objectClazz.isAnnotationPresent(aliasAnnotationClazz))
        {
            result = objectClazz.getAnnotation(AliasAnnotation.class).value();
        }
        else
        {
            try
            {
                Class<?> clazz = Class.forName("javax.persistence.Table");
                aliasAnnotationClazz = (Class<? extends Annotation>) clazz;
                if(objectClazz.isAnnotationPresent(aliasAnnotationClazz))
                {
                    Method nameMethod = clazz.getMethod("name", new Class<?>[]{});
                    Object obj = objectClazz.getAnnotation(aliasAnnotationClazz);
                    result = methodInvoke(obj, nameMethod, new Object[]{}).toString();
                }
            }
            catch(Exception e)
            {
                // such as: not found class "javax.persistence.Table" or not found its "name" method
            }
        }
        if(CodeUtil.isEmpty(result))
        {
            result = objectClazz.getName();
        }
        return result;
    }
    
    /**
     * 获取属性的别名，当没获取到别名采用FieldName
     * 2014年10月15日  下午4:41:33
     * return String
     */
    @SuppressWarnings("unchecked")
    public static String getFieldAliasName(Field field)
    {
        String result = "";
        
        Class<? extends Annotation> aliasAnnotationClazz;
        aliasAnnotationClazz = AliasAnnotation.class;
        if(field.isAnnotationPresent(aliasAnnotationClazz))
        {
            result = field.getAnnotation(AliasAnnotation.class).value();
        }
        else
        {
            try
            {
                Class<?> clazz = Class.forName("com.alibaba.fastjson.annotation.JSONField");
                aliasAnnotationClazz = (Class<? extends Annotation>) clazz;
                if(field.isAnnotationPresent(aliasAnnotationClazz))
                {
                    Method nameMethod = clazz.getMethod("name", new Class<?>[]{});
                    Object obj = field.getAnnotation(aliasAnnotationClazz);
                    result = methodInvoke(obj, nameMethod, new Object[]{}).toString();
                }
            }
            catch(Exception e)
            {
                // such as: not found class "com.alibaba.fastjson.annotation.JSONField" or not found its "name" method
                try
                {
                    Class<?> clazz = Class.forName("javax.persistence.Column");
                    aliasAnnotationClazz = (Class<? extends Annotation>) clazz;
                    if(field.isAnnotationPresent(aliasAnnotationClazz))
                    {
                        Method nameMethod = clazz.getMethod("name", new Class<?>[]{});
                        Object obj = field.getAnnotation(aliasAnnotationClazz);
                        result = methodInvoke(obj, nameMethod, new Object[]{}).toString();
                    }
                }
                catch(Exception innerE)
                {
                    // such as: not found class "javax.persistence.Column" or not found its "name" method
                }
            }
        }
        if(CodeUtil.isEmpty(result))
        {
            result = field.getName();
        }
        return result;
    }
    
    /**
     * createdDatetime 2014年9月4日 下午4:36:46
     * @return Comparator
     */
    private static Comparator<Field> getFieldSortComparator() {
        
        Comparator<Field> result = new Comparator<Field>() {
            public int compare(Field o1, Field o2) {
                Class<FieldSortAnnotation> fieldSortAnnotationClass = FieldSortAnnotation.class;
                int o1OrderValue = 0;
                int o2OrderValue = 0;
                if(o1.isAnnotationPresent(fieldSortAnnotationClass))
                {
                    o1OrderValue = o1.getAnnotation(fieldSortAnnotationClass).value();
                }
                if(o2.isAnnotationPresent(fieldSortAnnotationClass))
                {
                    o2OrderValue = o2.getAnnotation(fieldSortAnnotationClass).value();
                }
                return o1OrderValue-o2OrderValue;
            }
        };
        
        return result;
    }
	
	// private static Properties FIELD_NAME_CONFIG_PROPERTIES;
	//
	// public static Properties getFIELD_NAME_CONFIG_PROPERTIES()
	// {
	// return FIELD_NAME_CONFIG_PROPERTIES;
	// }
	// public static void setFIELD_NAME_CONFIG_PROPERTIES(
	// Properties fIELD_NAME_CONFIG_PROPERTIES)
	// {
	// FIELD_NAME_CONFIG_PROPERTIES = fIELD_NAME_CONFIG_PROPERTIES;
	// }
	//
	// private static synchronized void loadFiledNameConfig() throws IOException
	// {
	// FIELD_NAME_CONFIG_PROPERTIES = new Properties();
	// InputStream in =
	// ReflectUtil.class.getClassLoader().getResourceAsStream("fieldName.properties");
	// if(in!=null)
	// {
	// FIELD_NAME_CONFIG_PROPERTIES.load(in);
	// }
	// }
	//
	// public static void invokeSetMethod(Object object ,String fieldName,
	// String fieldStringValue)
	// {
	// if(object==null||fieldName==null)
	// throw new
	// ReflectException("ReflectUtil.setMethod object and fieldName should not be null");
	//
	//
	// if(FIELD_NAME_CONFIG_PROPERTIES==null)
	// {
	// try
	// {
	// loadFiledNameConfig();
	// } catch (IOException e)
	// {
	// throw new
	// ReflectException("ReflectUtil.loadFileNameConfig IOException occured.",e);
	// }
	// }
	// String value = FIELD_NAME_CONFIG_PROPERTIES.getProperty(fieldName);
	// if(value==null)
	// {
	// value = fieldName;
	// }
	//
	// Class<?> clazz = object.getClass();
	// Map<String, Method> fieldAndSetMethodMap =
	// getFieldAndSetmethodMap(clazz);
	// Method setMethod = fieldAndSetMethodMap.get(value);
	// if(setMethod==null)
	// {
	// throw new
	// ReflectException("ReflectUtil.setMethod fieldName.properties config error fieldName="+fieldName);
	// }
	// Class<?> paramType = setMethod.getParameterTypes()[0];
	// Object setMethodValue = StringUtil.getValueFromString(fieldStringValue,
	// paramType);
	//
	// try
	// {
	// setMethod.invoke(object, new Object[]{setMethodValue});
	// }
	// catch(Exception e)
	// {
	// throw new ReflectException("ReflectUtil.setMethod="+setMethod.getName()+
	// " invoke exception occured.",e);
	// }
	// }
	
	/**
	 * 判断是否为数字类型
	 * @param clazz
	 * @return
	 */
	public static boolean isNumberClazz(Class<?> clazz)
	{
	    if(clazz==null)
	        return false;
	    if (byte.class.isAssignableFrom(clazz)||Byte.class.isAssignableFrom(clazz)
                || short.class.isAssignableFrom(clazz)|| Short.class.isAssignableFrom(clazz)
                || int.class.isAssignableFrom(clazz)|| Integer.class.isAssignableFrom(clazz)
                || long.class.isAssignableFrom(clazz)|| Long.class.isAssignableFrom(clazz)
                || float.class.isAssignableFrom(clazz)|| Float.class.isAssignableFrom(clazz)
                || double.class.isAssignableFrom(clazz)|| Double.class.isAssignableFrom(clazz))
            return true;
	    return false;
	}
	
	/**
	 * 判断是否为简单类型
	 * @param clazz
	 * @return boolean
	 */
	public static boolean isSimpleClazz(Class<?> clazz)
	{
		if (clazz == null)
			return false;

		if (clazz.isPrimitive() || Character.class.isAssignableFrom(clazz)
				|| Boolean.class.isAssignableFrom(clazz)
				|| Byte.class.isAssignableFrom(clazz)
				|| Short.class.isAssignableFrom(clazz)
				|| Integer.class.isAssignableFrom(clazz)
				|| Long.class.isAssignableFrom(clazz)
				|| Float.class.isAssignableFrom(clazz)
				|| Double.class.isAssignableFrom(clazz)
				|| String.class.isAssignableFrom(clazz)
				||Enum.class.isAssignableFrom(clazz)
				||Number.class.isAssignableFrom(clazz))
			return true;
		return false;
	}
	
	/**
	 * 判断一个类或一种类型是否为Model类型
	 * 2014年9月28日  下午5:34:16
	 * @param obj
	 * @param targetClazz
	 * @return boolean
	 */
    public static boolean isModel(Object obj,Class<?> targetClazz)
    {
        if(obj==null&&targetClazz==null)
            return false;
        
        boolean objResult = true;
        if(obj!=null)
        {
            Map<Field,Method> fieldAndSetmethodMap = getFieldAndSetmethodMap(obj.getClass());
            objResult = !fieldAndSetmethodMap.isEmpty();
        }
        
        boolean targetClazzResult = true;
        if(targetClazz!=null)
        {
            Map<Field,Method> fieldAndSetmethodMap = getFieldAndSetmethodMap(targetClazz);
            targetClazzResult = !fieldAndSetmethodMap.isEmpty();
        }

        return objResult&&targetClazzResult;
    }
	
	/**
	 * 清除缓存
	 * 2014年9月28日  下午9:58:05
	 */
	public static void clear()
	{
		FIELD_AND_GET_METHOD_MAP.clear();
		FIELD_AND_SET_METHOD_MAP.clear();
		FIELD_NAME_AND_GET_METHOD_MAP.clear();
		FIELD_NAME_AND_SET_METHOD_MAP.clear();
	}
	
	/**
	 * 通过类的class，得到其方法、属性，便于我们直接调用某个类的方法，获取某个属性
	 * @param clazz 
	 * @param containParentField
	 * @return Map
	 */
	public static Map<Field, Method> initFieldAndSetmethodMap(Class<?> clazz, boolean containParentField)
	{
	    String methodReturnTypeVoidString = "void";
	    
		if (clazz == null)
			throw new ReflectException(
					"ReflectUtil.getFieldAndSetmethodMap clazz should not be null.");
		
		Map<Field, Method> fieldAndSetmethodMap = new LinkedHashMap<Field, Method>();
		
		// 过滤当子类重写父类某个同名属性时，不添加父类的该属性
		Set<String> fieldNameSet = new HashSet<String>();
		
		// 获取所有public setMethod
		List<Method> methodList = new ArrayList<Method>();
		Method[] methods = clazz.getMethods();
		for(Method method : methods)
		{
			if(methodReturnTypeVoidString.equals(method.getReturnType().toString())&&(method.getName().startsWith("set")))
			{
				methodList.add(method);
			}
		}
		
		Class<?> parentClazz = clazz;
		while(!Object.class.equals(parentClazz))
		{
			Field[] fields = parentClazz.getDeclaredFields();
			
			// 对fields排序
			Arrays.sort(fields, FIELD_SORT_COMPARATOR);
			
			for (Field field : fields) {
				Class<?> fieldClazz = field.getType();
				String fieldName = field.getName();
				
				String setMethodName;
				if (fieldClazz != null && fieldName != null) {
					if ((Boolean.class.isAssignableFrom(fieldClazz) || boolean.class
							.equals(fieldClazz))
							&& fieldName.startsWith("is")
							&& Character.isUpperCase(fieldName.charAt(2))) {
						setMethodName = "set" + fieldName.substring(2);

					} else {

						setMethodName = "set" + fieldName;
					}
					Method setMethod = getMethodByName(methodList, setMethodName);
					//2015-08-19兼容处理当某些set方法不是按jdk规范生成的时候，采用set+fieldName的方式生成setFieldName
					if(setMethod==null)
					{
					    setMethod = getMethodByName(methodList, "set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1));
					}
					if (setMethod != null) {
					    if(!fieldNameSet.contains(fieldName))
					    {
					        fieldNameSet.add(fieldName);
					        
					        fieldAndSetmethodMap.put(field, setMethod);
					    }
					}
				}
			}
			parentClazz = parentClazz.getSuperclass();
			if(!containParentField)
			{
				break;
			}
		}
		return fieldAndSetmethodMap;
	}
	
	/**
	 * 获取类的Field和Field对应的setMethod的Map集合
	 * @param clazz
	 * @param containParentField
	 * @return Map
	 */
	public static Map<Field, Method> getFieldAndSetmethodMap(Class<?> clazz, boolean containParentField)
	{
		Map<Field, Method> result;
		if(!FIELD_AND_SET_METHOD_MAP.containsKey(clazz))
		{
			result = initFieldAndSetmethodMap(clazz, containParentField);
			FIELD_AND_SET_METHOD_MAP.put(clazz, result);
		}
		else
		{
			result = new LinkedHashMap<Field, Method>(FIELD_AND_SET_METHOD_MAP.get(clazz));
		}
		return result;
	}
	
	/**
	 * 获取类的Field和Field对应的setMethod的Map集合
	 * @param clazz
	 * @return Map
	 */
	public static Map<Field, Method> getFieldAndSetmethodMap(Class<?> clazz)
	{
		return getFieldAndSetmethodMap(clazz,true);
	}
	/**
	 * 通过类的class，得到其方法、属性，便于我们直接调用某个类的方法，获取某个属性
	 * @param clazz   类对象
	 * @param containParentField  
	 * @return Map
	 */
	public static Map<String, Method> initFieldNameAndSetmethodMap(Class<?> clazz,boolean containParentField)
	{
		if (clazz == null)
			throw new ReflectException(
					"ReflectUtil.getFieldNameAndSetmethodMap clazz should not be null.");
		    
		Map<String, Method> result = new LinkedHashMap<String, Method>();
		
        Map<Field, Method> fieldAndSetMethodMap = getFieldAndSetmethodMap(clazz,containParentField);
        Set<Entry<Field, Method>> entrySet = fieldAndSetMethodMap.entrySet();
        for(Entry<Field,Method> entry : entrySet)
        {
            Field field = entry.getKey();
            String mapKey = getFieldSetMethodAliasName(clazz,field);
            
            if(!result.containsKey(mapKey))
            {
                result.put(mapKey, entry.getValue());
            }
        }
		
		return result;
	}

	/**
	 * 获取类的Field名字（若有别名使用别名）和Field对应的setMethod的Map集合
	 * @param clazz
	 * @param containParentField
	 * @return Map
	 */
	public static Map<String, Method> getFieldNameAndSetmethodMap(Class<?> clazz,boolean containParentField)
	{
		Map<String, Method> result;
		if(!FIELD_NAME_AND_SET_METHOD_MAP.containsKey(clazz))
		{
			result = initFieldNameAndSetmethodMap(clazz, containParentField);
			FIELD_NAME_AND_SET_METHOD_MAP.put(clazz, result);
		}
		else
		{
			result = new LinkedHashMap<String, Method>(FIELD_NAME_AND_SET_METHOD_MAP.get(clazz));
		}
		return result;
	}
	
	/**
	 * 获取类的Field名字（若有别名使用别名）和Field对应的setMethod的Map集合
	 * @param clazz
	 * @return Map
	 */
	public static Map<String, Method> getFieldNameAndSetmethodMap(Class<?> clazz)
	{
		return getFieldNameAndSetmethodMap(clazz, true);
	}
	
	/**
	 * 获取类的Field名字（若有别名使用别名）和Field对应的getMethod的Map集合
	 * @param clazz
	 * @param containParentField
	 * @return Map
	 */
	public static Map<Field, Method> initFieldAndGetmethodMap(Class<?> clazz, boolean containParentField)
	{
		if (clazz == null)
			throw new ReflectException(
					"ReflectUtil.getFieldAndGetmethodMap clazz should not be null.");
		
		Map<Field, Method> fieldAndGetmethodMap = new LinkedHashMap<Field, Method>();

		// 过滤当子类重写父类某个同名属性时，不添加父类的该属性
        Set<String> fieldNameSet = new HashSet<String>();
		
		List<Method> methodList = new ArrayList<Method>();
		Method[] methods = clazz.getMethods();
		for(Method method : methods)
		{
			String methodName = method.getName();
			if(CodeUtil.isEmpty(new Object[]{method.getParameterTypes()})&&(methodName.startsWith("is")||methodName.startsWith("get")))
			{
				methodList.add(method);
			}
		}
		
		Class<?> parentClazz = clazz;
		while(!Object.class.equals(parentClazz))
		{
			Field[] fields = parentClazz.getDeclaredFields();
			
			// 对Field排序
            Arrays.sort(fields,FIELD_SORT_COMPARATOR);
			
			for (Field field : fields) {
				Class<?> fieldClazz = field.getType();
				String fieldName = field.getName();
				
				String getMethodName;
				if (fieldClazz != null && fieldName != null) {
					if (boolean.class.equals(fieldClazz)) {
						if (fieldName.startsWith("is")
								&& Character.isUpperCase(fieldName.charAt(2))) {
							getMethodName = fieldName;
						}
						else if(fieldName.startsWith("Is"))
						{
							getMethodName = "get" + fieldName;
						}
						else {
							getMethodName = "is" + fieldName;
						}
					} else {

						getMethodName = "get" + fieldName;
					}
					Method getMethod = getMethodByName(methodList,
							getMethodName);
					//2015-08-19兼容处理当某些get方法不是按jdk规范生成的时候，采用set+fieldName的方式生成setFieldName
					if(getMethod==null)
					{
						getMethod = getMethodByName(methodList, "get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1));
					}
					if (getMethod != null) {
					    if(!fieldNameSet.contains(fieldName))
					    {
					        fieldNameSet.add(fieldName);
					        
					        fieldAndGetmethodMap.put(field, getMethod);
					    }
					}
				}	
			}
			parentClazz = parentClazz.getSuperclass();
			if(!containParentField)
			{
				break;
			}
		}
		
		return fieldAndGetmethodMap;		
	}
	
	/**
	 * 获取类的Field名字（若有别名使用别名）和Field所对应的getMethod方法的集合Map
	 * @param clazz
	 * @param containParentField
	 * @return Map
	 */
	public static Map<Field, Method> getFieldAndGetmethodMap(Class<?> clazz, boolean containParentField)
	{
		Map<Field, Method> result;
		if(!FIELD_AND_GET_METHOD_MAP.containsKey(clazz))
		{
			result = initFieldAndGetmethodMap(clazz, containParentField);
			FIELD_AND_GET_METHOD_MAP.put(clazz, result);
		}
		else
		{
			result = new LinkedHashMap<Field, Method>(FIELD_AND_GET_METHOD_MAP.get(clazz));
		}
		return result;	
	}
	
	/**
	 * 获取类的Field名字（若有别名使用别名）和Field所对应的getMethod方法的集合Map
	 * @param clazz
	 * @return Map
	 */
	public static Map<Field, Method> getFieldAndGetmethodMap(Class<?> clazz)
	{
		return getFieldAndGetmethodMap(clazz,true);
	}
	
	/**
	 * 获取类的Field名字（若有别名使用别名）和Field所对应的getMethod方法的集合Map
	 * @param clazz
	 * @param containParentField
	 * @return Map
	 */
	public static Map<String, Method> initFieldNameAndGetmethodMap(Class<?> clazz,boolean containParentField)
	{
		if (clazz == null)
			throw new ReflectException(
					"ReflectUtil.getFieldNameAndGetmethodMap clazz should not be null.");

		Map<String, Method> result = new LinkedHashMap<String, Method>();
		
		Map<Field, Method> fieldAndGetMethodMap = getFieldAndGetmethodMap(clazz,containParentField);
		Set<Entry<Field, Method>> entrySet = fieldAndGetMethodMap.entrySet();
		for(Entry<Field,Method> entry : entrySet)
		{
			Field field = entry.getKey();
			String mapKey = getFieldGetMethodAliasName(clazz,field);
			
			if(!result.containsKey(mapKey))
			{
			    result.put(mapKey, entry.getValue());
			}
		}
		
		return result;
	}

	/**
	 * 获取类的Field名字（若有别名使用别名）和Field所对应的getMethod方法的集合Map
	 * @param clazz
	 * @param containParentField
	 * @return Map
	 */
	public static Map<String, Method> getFieldNameAndGetmethodMap(Class<?> clazz,boolean containParentField)
	{
		Map<String, Method> result;
		if(!FIELD_NAME_AND_GET_METHOD_MAP.containsKey(clazz))
		{
			result = initFieldNameAndGetmethodMap(clazz, containParentField);
			FIELD_NAME_AND_GET_METHOD_MAP.put(clazz, result);
		}
		else
		{
			result = new LinkedHashMap<String, Method>(FIELD_NAME_AND_GET_METHOD_MAP.get(clazz));
		}
		return result;
	}
	
	/**
	 * 获取类的Field名字（若有别名使用别名）和Field所对应的getMethod方法的集合Map
	 * @param clazz
	 * @return Map
	 */
	public static Map<String, Method> getFieldNameAndGetmethodMap(Class<?> clazz)
	{
		return getFieldNameAndGetmethodMap(clazz,true);
	}
	
	/**
	 * 根据类类型和方法名获取指定的 methodName中第一个method
	 * @param clazz
	 * @param methodName
	 */
	public static Method getFirstPublicMethodByName(Class<?> clazz, String methodName) {
        Method[] methodArray = clazz.getMethods();
        return getMethodByName(Arrays.asList(methodArray), methodName);
    }
	
	private static Method getMethodByName(List<Method> methodList, String methodName)
	{
		for (Method method : methodList)
		{
			String name = method.getName();
			if (name.equalsIgnoreCase(methodName))
				return method;
		}
		return null;
	}
	
	/**
	 * 从Map中组建一个对象
	 * @since 2014-03-14 20:30
	 * 
	 *        for example: Person class has field(name, age),
	 *        resutMap{studentName
	 *        ="zhangsan",age=26,scord=89.2},keyMap={name=studentName}
	 * 
	 * @param clazz
	 * @param resultMap
	 * @return <T> T
	 */
    public static <T> T mapToObject(Map<String, ?> resultMap,Class<T> clazz)
	{
		T obj = newModel(clazz);

		Map<Field, Method> fieldAndSetMethodMap = getFieldAndSetmethodMap(clazz);
		Set<Entry<Field, Method>> entrySet = fieldAndSetMethodMap.entrySet();
		for (Entry<Field, Method> entry : entrySet)
		{
		    Field field = entry.getKey();
		    
		    Class<?> fieldType = field.getType();
		    String key = getFieldSetMethodAliasName(clazz,field);

			Object fieldValue;
			
			Object value = resultMap.get(key);
			if(value==null)
			{
			    fieldValue = null;
			}
			else
			{
			    Class<?> valueType = value.getClass();
			    if(ReflectUtil.isSimpleClazz(valueType))
			    {
			        String valueString = value.toString();
			        fieldValue = StringUtil.getValueFromString(valueString, fieldType);
			    }
			    else if(value instanceof Map)
			    {
			        Map<String, Object> newMap = new LinkedHashMap<String, Object>();
			        Map<?, ?> map = (Map<?, ?>)value;
			        Set<?> keySet = map.keySet();
			        for (Object keyOne : keySet) {
			            String newKeyOne = keyOne==null?null:keyOne.toString();
                        newMap.put(newKeyOne, map.get(keyOne));
                    }
			        fieldValue = mapToObject(newMap, fieldType);
			    }
			    else
			    {
			        fieldValue = value;
			    }
			}
//			else if(ReflectUtil.isSimpleClazz(value.getClass()))
//			{
//			    
//			}
//			else if(value instanceof Map)
//			{
//			    fieldValue = mapToObject((Map<String, ?>) value, fieldType);
//			}
//			else
//			{
//			    fieldValue = value;
//			}
			
			if(value!=null)
			{
			    setMethodInvoke(obj, key, fieldValue);
			}
			
		}

		return obj;
	}
	
	public static Map<String, Object> objectToMap(Object obj)
	{
	    if(obj==null)
	    {
	        return null;
	    }
	    
	    Map<String, Object> map = new LinkedHashMap<String, Object>();
	    
	    Class<?> clazz = obj.getClass();
	    Map<Field, Method> fieldAndGetmethodMap = getFieldAndGetmethodMap(clazz);
	    Set<Entry<Field, Method>> entrySet = fieldAndGetmethodMap.entrySet();
	    for(Entry<Field, Method> entry : entrySet)
	    {
	        Field field = entry.getKey();
	        Method getMethod = entry.getValue();
	        
	        String key = getFieldGetMethodAliasName(field);
	        
	        Object fieldValue = methodInvoke(obj, getMethod, new Object[]{});
	        
	        Object value;
	        // null
	        if(fieldValue==null)
	        {
	            value = null;
	        }
	        // array or collection
	        else if(isArray(fieldValue.getClass(), null)||fieldValue instanceof Collection)
	        {
	            if(isArray(fieldValue.getClass(),null))
	            {
	                Class<?> oneClass = fieldValue.getClass().getComponentType();
	                if(isModel(null, oneClass))
	                {
	                    Object[] array = getArray(fieldValue);
	                    int length = array.length;
	                    Map<?, ?>[] mapArray = new Map<?,?>[length];
	                    int i=0;
	                    for (Object one : array) {
                            mapArray[i++] = objectToMap(one);
                        }
	                    value = mapArray;
	                }
	                else
	                {
	                    value = fieldValue;
	                }
	            }
	            else
	            {
	                Class<?> oneClass = (Class<?>) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
	                if(isModel(null,oneClass))
	                {
	                    Collection<?> collection = (Collection<?>)fieldValue;
	                    List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
	                    for(Object one : collection)
	                    {
	                        Map<String, Object> oneMap = objectToMap(one);
	                        mapList.add(oneMap);
	                    }
	                    value = mapList;
	                }
	                else
	                {
	                    value = fieldValue;
	                }
	            }
	        }
	        // model
	        else if(isModel(fieldValue, null)||fieldValue instanceof Map)
            {
	            if(isModel(fieldValue, null))
	            {
	                value = objectToMap(fieldValue);
	            }
	            else
	            {
	                Map<?, ?> oldMap = (Map<?, ?>) map;
	                Map<String, Object> newMap = new LinkedHashMap<String, Object>();
                    Set<?> oldKeySet = oldMap.keySet();
                    for (Object oldKey : oldKeySet) {
                        newMap.put(StringUtil.toString(oldKey), oldMap.get(oldKey));
                    }
                    value = newMap;
	            }
            }
	        // other
	        else
	        {
	            value = fieldValue;
	        }
	        
	        if(value!=null&&!map.containsKey(key))
	        {
	            map.put(key, value);
	        }
	    }
	    return map;
	}

	/**
	 * 取得某个接口下所有实现这个接口的类
	 * @param c
	 * @return List<Class<?>>
	 */
	public static List<Class<?>> getAllClassByInterface(Class<?> c)
	{
		List<Class<?>> returnClassList = new LinkedList<Class<?>>();

		if (c.isInterface())
		{
			// 获取当前的包名
			String packageName = c.getPackage().getName();
			// 获取当前包下以及子包下所以的类
			List<Class<?>> allClass = getClasses(packageName);
			if (allClass != null)
			{
				returnClassList = new ArrayList<Class<?>>();
				for (Class<?> classes : allClass)
				{
					// 判断是否是同一个接口
					if (c.isAssignableFrom(classes))
					{
						// 本身不加入进去
						if (!c.equals(classes))
						{
							returnClassList.add(classes);
						}
					}
				}
			}
		}

		return returnClassList;
	}

	/**
	 * 取得某一类所在包的所有类名 不含迭代
	 * @param classLocation
	 * @param packageName
	 * @return String[]
	 */
	public static String[] getPackageAllClassName(String classLocation,
			String packageName)
	{
		// 将packageName分解
		String[] packagePathSplit = packageName.split("[.]");
		String realClassLocation = classLocation;
		int packageLength = packagePathSplit.length;
		for (int i = 0; i < packageLength; i++)
		{
			realClassLocation = realClassLocation + File.separator
					+ packagePathSplit[i];
		}
		File packeageDir = new File(realClassLocation);
		if (packeageDir.isDirectory())
		{
			String[] allClassName = packeageDir.list();
			return allClassName;
		}
		return null;
	}

	/**
	 * 从包package中获取所有的Class
	 * @param packageName   包名
	 * @return List<Class<?>>  返回Class集合
	 */
	public static List<Class<?>> getClasses(String packageName)
	{

		// 第一个class类的集合
		List<Class<?>> classes = new ArrayList<Class<?>>();
		// 是否循环迭代
		boolean recursive = true;
		// 获取包的名字 并进行替换
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		Enumeration<URL> dirs;
		try
		{
			dirs = Thread.currentThread().getContextClassLoader()
					.getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements())
			{
				// 获取下一个元素
				URL url = dirs.nextElement();
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol))
				{
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath,
							recursive, classes);
				} else if ("jar".equals(protocol))
				{
					// 如果是jar包文件
					// 定义一个JarFile
					JarFile jar;
					try
					{
						// 获取jar
						jar = ((JarURLConnection) url.openConnection())
								.getJarFile();
						// 从此jar包 得到一个枚举类
						Enumeration<JarEntry> entries = jar.entries();
						// 同样的进行循环迭代
						while (entries.hasMoreElements())
						{
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/')
							{
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName))
							{
								int idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1)
								{
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx)
											.replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if ((idx != -1) || recursive)
								{
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class")
											&& !entry.isDirectory())
									{
										// 去掉后面的".class" 获取真正的类名
										String className = name.substring(
												packageName.length() + 1,
												name.length() - 6);
										try
										{
											// 添加到classes
											classes.add(Class
													.forName(packageName + '.'
															+ className));
										} catch (ClassNotFoundException e)
										{
											e.printStackTrace();
										}
									}
								}
							}
						}
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName	包名
	 * @param packagePath	包路径
	 * @param recursive		
	 * @param classes
	 */
	public static void findAndAddClassesInPackageByFile(String packageName,
			String packagePath, final boolean recursive, List<Class<?>> classes)
	{
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory())
		{
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter()
		{
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file)
			{
				return (recursive && file.isDirectory())
						|| (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles)
		{
			// 如果是目录 则继续扫描
			if (file.isDirectory())
			{
				findAndAddClassesInPackageByFile(
						packageName + "." + file.getName(),
						file.getAbsolutePath(), recursive, classes);
			} else
			{
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0,
						file.getName().length() - 6);
				try
				{
					// 添加到集合中去
					classes.add(Class.forName(packageName + '.' + className));
				} catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 对象的getMethod调用
	 * 2014年9月28日  下午4:13:38
	 * @param obj
	 * @param fieldName
	 * @return Object
	 */
	public static Object getMethodInvoke(Object obj, String fieldName)
	{
		if(obj==null||fieldName==null)
			throw new ReflectException("getMethodInvoke parameter illegal,obj="+obj+",fieldName="+fieldName);
		
		Map<String, Method> fieldNameAndMethodMap;
		Class<?> clazz = obj.getClass();
		if(!FIELD_NAME_AND_GET_METHOD_MAP.containsKey(clazz))
		{
			fieldNameAndMethodMap = getFieldNameAndGetmethodMap(clazz);
			FIELD_NAME_AND_GET_METHOD_MAP.put(clazz, fieldNameAndMethodMap);
		}
		else
		{
			fieldNameAndMethodMap = FIELD_NAME_AND_GET_METHOD_MAP.get(clazz);
		}
		
		if(!fieldNameAndMethodMap.containsKey(fieldName))
		{
			throw new ReflectException("getMethodInvoke fieldName getMethod not exist,obj="+obj+",fieldName="+fieldName);
		}
		
		try {
			return fieldNameAndMethodMap.get(fieldName).invoke(obj, new Object[]{});
		} catch (Exception e) {
			throw new ReflectException("getMethodInvoke illegal,obj="+obj+",fieldName="+fieldName+","+e.getMessage(),e);
		}
	}
	
	/**
	 * 对象方法调用
	 * @param obj
	 * @param methodName
	 * @param parameter
	 * @return
	 */
	public static Object methodInvoke(Object obj, String methodName, Object... parameter)
    {
	    Class<?>[] parameterTypes = new Class<?>[]{};
	    if(parameter!=null)
	    {
	        int length = parameter.length;
	        parameterTypes = new Class<?>[length];
	        for(int i=0; i<length; i++)
	        {
	            parameterTypes[i] = parameter[i].getClass();
	        }
	    }
	    Method method;
	    Class<?> clazz = obj.getClass();
	    try {
	        method = clazz.getMethod(methodName, parameterTypes);
            return method.invoke(obj, parameter);
        } catch (Exception e) {
            throw new ReflectException("method illegal,obj="+obj+",methodName="+methodName+",parameter="+Arrays.asList(parameter)+","+e.getMessage(),e);
        }
    }
	
	/**
	 * 对象的方法调用
	 * 2014年9月28日  下午4:42:29
	 * @param obj
	 * @param method
	 * @param parameter
	 * @return Object
	 */
	public static Object methodInvoke(Object obj, Method method, Object... parameter)
	{
		try {
			return method.invoke(obj, parameter);
		} catch (Exception e) {
			throw new ReflectException("methodInvoke illegal,obj="+obj+",method="+method+",parameter="+Arrays.asList(parameter)+","+e.getMessage(),e);
		}
	}
	
	/**
	 * 对象的setMethod调用
	 * 2014年9月28日  下午4:38:18
	 * @param obj
	 * @param fieldName
	 * @param fieldValue
	 */
	public static void setMethodInvoke(Object obj, String fieldName, Object fieldValue)
	{
		if(obj==null||fieldName==null)
			throw new ReflectException("setMethodInvoke parameter illegal,obj="+obj+",fieldName="+fieldName);
		
		Map<String, Method> fieldNameAndMethodMap;
		Class<?> clazz = obj.getClass();
		if(!FIELD_NAME_AND_SET_METHOD_MAP.containsKey(clazz))
		{
			fieldNameAndMethodMap = getFieldNameAndSetmethodMap(clazz);
			FIELD_NAME_AND_SET_METHOD_MAP.put(clazz, fieldNameAndMethodMap);
		}
		else
		{
			fieldNameAndMethodMap = FIELD_NAME_AND_SET_METHOD_MAP.get(clazz);
		}
		
		if(!fieldNameAndMethodMap.containsKey(fieldName))
		{
			throw new ReflectException("setMethodInvoke fieldName setMethod not exist,obj="+obj+",fieldName="+fieldName);
		}
		
		try {
		    Method setMethod = fieldNameAndMethodMap.get(fieldName);
            setMethod.invoke(obj, fieldValue);
		} catch (Exception e) {
			throw new ReflectException("setMethodInvoke illegal,obj="+obj+",fieldName="+fieldName+","+e.getMessage(),e);
		}
	}
	
	/**
	 * 根据数组组成类型new出数组
	 * 2014年10月3日  上午7:35:56
	 * @param clazz		预创建数组类型
	 * @param length	预创建数组长度
	 * @return <T> T[]  返回数组
	 */
	public static <T> T[] newArray(Class<? extends T> clazz, int length)
	{
		@SuppressWarnings("unchecked")
		T[] result = (T[]) Array.newInstance(clazz, length);
		for(int i=0; i<length; i++)
		{
			result[i] = newModel(clazz);
		}
		return result;
	}
	
	/**
	 * 从collection中组装指定的子类
	 * @param collection
	 * @param collectionClass
	 * @return
	 */
	public static <T> Object newCollection(Collection<T> collection, Class<?> collectionClass) {
	    // TODO Auto-generated method stub
	    CodeUtil.emptyCheck(null,ReflectUtil.class+".newCollection collectionClass is null", new Object[]{collectionClass});
	    
	    Object result;
	    if(ArrayList.class.isAssignableFrom(collectionClass))
	    {
	        result = new ArrayList<T>(collection);
	    }
	    else if(LinkedList.class.isAssignableFrom(collectionClass))
	    {
	        result = new LinkedList<T>(collection);
	    }
	    else if(Vector.class.isAssignableFrom(collectionClass))
	    {
	        result = new Vector<T>(collection);
	    }
	    else if(List.class.isAssignableFrom(collectionClass))
	    {
	        result = new ArrayList<T>(collection);
	    }
	    else if(LinkedHashSet.class.isAssignableFrom(collectionClass))
	    {
	        result = new LinkedHashSet<T>(collection);
	    }
	    else if(HashSet.class.isAssignableFrom(collectionClass))
        {
            result = new HashSet<T>(collection);
        }
	    else if(TreeSet.class.isAssignableFrom(collectionClass))
	    {
	        result = new TreeSet<T>(collection);
	    }
	    else if(SortedSet.class.isAssignableFrom(collectionClass))
	    {
	        result = new TreeSet<T>(collection);
	    }
	    else if(ConcurrentLinkedQueue.class.isAssignableFrom(collectionClass))
	    {
	        result = new ConcurrentLinkedQueue<T>(collection);
	    }
	    else if(PriorityQueue.class.isAssignableFrom(collectionClass))
	    {
	        result = new PriorityQueue<T>(collection);
	    }
	    else if(Queue.class.isAssignableFrom(collectionClass))
	    {
	        result = new ConcurrentLinkedQueue<T>(collection);
	    }
	    else
	    {
	        result = collection;
	    }
		return result;	    
    }
	
	/**
	 * 根据类型new出对象
	 * 2014年10月3日  上午7:35:36
	 * @param clazz  	预创建类型
	 * @return <T> T  返回预创建类型的对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newModel(Class<? extends T> clazz)
	{
		try
		{
			T result;
			
			// 获取一个构造方法
			Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
			
			Object[] parameter;
			Class<?>[] parameterTypeArray = constructor.getParameterTypes();
			int length = parameterTypeArray.length;
			parameter = new Object[length];
			Class<?> parameterClazz;
			for(int i=0; i<length; i++)
			{
				parameterClazz = parameterTypeArray[i];
				if(parameterClazz.isPrimitive())
				{
					if(char.class.isAssignableFrom(parameterClazz))
					{
						parameter[i] = (char)0;
					}
					else if(boolean.class.isAssignableFrom(parameterClazz))
					{
						parameter[i] = false;
					}
					else if(short.class.isAssignableFrom(parameterClazz)) 
					{
						parameter[i] = new Integer(0).shortValue();
					}
					else if(byte.class.isAssignableFrom(parameterClazz))
					{
						parameter[i] = new Integer(0).byteValue();
					}
					else if(int.class.isAssignableFrom(parameterClazz))
					{
						parameter[i] = new Integer(0).intValue();
					}
					else if(long.class.isAssignableFrom(parameterClazz))
					{
					    parameter[i] = new Integer(0).longValue();
					}
					else if(float.class.isAssignableFrom(parameterClazz))
					{
						parameter[i] = new Integer(0).floatValue();
					}
					else
					{
						parameter[i] = new Integer(0).doubleValue();
					}
				}
				else
				{
					parameter[i] = null;
				}
			}
			
			Object o = constructor.newInstance(parameter);
			result = (T) o;	
			
			return result;
		}
		catch(Exception e)
		{
			throw new ReflectException("newModel illegal,clazz="+clazz+","+e.getMessage(),e);
		}
	}
	
	/**
     * 复制source的各个field value到target
     * @param containEmpty 是否复制值为null值的field
     * @author wangwenwu
     * 2014年10月20日  上午10:10:19
     */
    public static void cloneField(boolean containEmpty, Object source, Object target)
    {
        if(CodeUtil.isEmpty(source,target))
            throw new ReflectException("ReflectUtil.cloneField param illegal,source="+source+",target="+target);
        
        Class<?> sourceClazz = source.getClass();
        Class<?> targetClazz = target.getClass();
        Map<String, Method> fieldNameAndGetMethodMap = getFieldNameAndGetmethodMap(sourceClazz);
        Map<String,Method> fieldNameAndSetMethodMap = getFieldNameAndSetmethodMap(targetClazz);
        
        Set<Entry<String, Method>> entrySet = fieldNameAndSetMethodMap.entrySet();
        for(Entry<String,Method> entry : entrySet)
        {
            String fieldName = entry.getKey();
            Method setMethod = entry.getValue();
            
            if(fieldNameAndGetMethodMap.containsKey(fieldName))
            {
                Method getMethod = fieldNameAndGetMethodMap.get(fieldName);
                if(setMethod.getParameterTypes().length==1)
                {
                    boolean match = false;
                    Class<?> returnType = getMethod.getReturnType();
                    Class<?> parameterType = setMethod.getParameterTypes()[0];
                    if(
                            (byte.class.isAssignableFrom(parameterType)&&Byte.class.isAssignableFrom(returnType))
                          ||(Byte.class.isAssignableFrom(parameterType)&&byte.class.isAssignableFrom(returnType))
                          ||(short.class.isAssignableFrom(parameterType)&&Short.class.isAssignableFrom(returnType))
                          ||(Short.class.isAssignableFrom(parameterType)&&short.class.isAssignableFrom(returnType))
                          ||(int.class.isAssignableFrom(parameterType)&&Integer.class.isAssignableFrom(returnType))
                          ||(Integer.class.isAssignableFrom(parameterType)&&int.class.isAssignableFrom(returnType))
                          ||(long.class.isAssignableFrom(parameterType)&&Long.class.isAssignableFrom(returnType))
                          ||(Long.class.isAssignableFrom(parameterType)&&long.class.isAssignableFrom(returnType))
                          ||(float.class.isAssignableFrom(parameterType)&&Float.class.isAssignableFrom(returnType))
                          ||(Float.class.isAssignableFrom(parameterType)&&float.class.isAssignableFrom(returnType))
                          ||(double.class.isAssignableFrom(parameterType)&&Double.class.isAssignableFrom(returnType))
                          ||(Double.class.isAssignableFrom(parameterType)&&double.class.isAssignableFrom(returnType))
                          ||(boolean.class.isAssignableFrom(parameterType)&&Boolean.class.isAssignableFrom(returnType))
                          ||(Boolean.class.isAssignableFrom(parameterType)&&boolean.class.isAssignableFrom(returnType))
                          ||(char.class.isAssignableFrom(parameterType)&&Character.class.isAssignableFrom(returnType))
                          ||(Character.class.isAssignableFrom(parameterType)&&char.class.isAssignableFrom(returnType))
                         )
                       {
                           match = true;
                       }
                    else
                    {
                        match = parameterType.isAssignableFrom(returnType);
                    }
                    
                    if(match)
                    {
                        Object getMethodValue = getMethodInvoke(source, fieldName);
                        if(getMethodValue==null)
                        {
                            if(containEmpty)
                            {
                                setMethodInvoke(target, fieldName, getMethodValue);
                            }
                        }
                        else
                        {
                            setMethodInvoke(target, fieldName, getMethodValue);
                        }
                    }
                }
            }
        }
    }
	
    /**
     * 复制source的指定FieldName的Value到target
     * @author wangwenwu
     * 2014年10月22日  下午2:51:14
     */
    public static void cloneField(Object source, String[] targetFieldNameArray, Object target)
    {
        if(CodeUtil.isEmpty(source,target))
            throw new ReflectException("ReflectUtil.cloneField param illegal,source="+source+",target="+target);
        
        Class<?> sourceClazz = source.getClass();
        Map<String, Method> fieldNameAndGetMethodMap = getFieldNameAndGetmethodMap(sourceClazz);
        
        Class<?> targetClazz = target.getClass();
        Map<String, Method> fieldNameAndSetMethodMap = getFieldNameAndSetmethodMap(targetClazz);
        
        if(CodeUtil.isEmpty(new Object[]{targetFieldNameArray}))
        {
            targetFieldNameArray = new ArrayList<String>(fieldNameAndSetMethodMap.keySet()).toArray(new String[]{});
        }
        
        for(String fieldName : targetFieldNameArray)
        {
            Method setMethod = fieldNameAndSetMethodMap.get(fieldName);
            if(!CodeUtil.isEmpty(setMethod)&&fieldNameAndGetMethodMap.containsKey(fieldName))
            {
                Method getMethod = fieldNameAndGetMethodMap.get(fieldName);
                if(setMethod.getParameterTypes().length==1)
                {
                    boolean match = false;
                    Class<?> returnType = getMethod.getReturnType();
                    Class<?> parameterType = setMethod.getParameterTypes()[0];
                    if(parameterType.isPrimitive())
                    {
                        if(
                                 (byte.class.isAssignableFrom(parameterType)&&Byte.class.isAssignableFrom(returnType))
                               ||(byte.class.isAssignableFrom(parameterType)&&byte.class.isAssignableFrom(returnType))
                               ||(short.class.isAssignableFrom(parameterType)&&Short.class.isAssignableFrom(returnType))
                               ||(short.class.isAssignableFrom(parameterType)&&short.class.isAssignableFrom(returnType))
                               ||(int.class.isAssignableFrom(parameterType)&&Integer.class.isAssignableFrom(returnType))
                               ||(int.class.isAssignableFrom(parameterType)&&int.class.isAssignableFrom(returnType))
                               ||(long.class.isAssignableFrom(parameterType)&&Long.class.isAssignableFrom(returnType))
                               ||(long.class.isAssignableFrom(parameterType)&&long.class.isAssignableFrom(returnType))
                               ||(float.class.isAssignableFrom(parameterType)&&Float.class.isAssignableFrom(returnType))
                               ||(float.class.isAssignableFrom(parameterType)&&float.class.isAssignableFrom(returnType))
                               ||(double.class.isAssignableFrom(parameterType)&&Double.class.isAssignableFrom(returnType))
                               ||(double.class.isAssignableFrom(parameterType)&&double.class.isAssignableFrom(returnType))
                               ||(boolean.class.isAssignableFrom(parameterType)&&Boolean.class.isAssignableFrom(returnType))
                               ||(boolean.class.isAssignableFrom(parameterType)&&boolean.class.isAssignableFrom(returnType))
                               ||(char.class.isAssignableFrom(parameterType)&&Character.class.isAssignableFrom(returnType))
                               ||(char.class.isAssignableFrom(parameterType)&&char.class.isAssignableFrom(returnType))
                           )
                        {
                            match = true;
                        }
                    }
                    else
                    {
                        match = parameterType.isAssignableFrom(returnType);
                    }
                    
                    if(match)
                    {
                        Object getMethodValue = getMethodInvoke(source, fieldName);
                        setMethodInvoke(target, fieldName, getMethodValue);
                    }
                }
            }
        }
    }
	
    /** 获取对象集合中某个属性的集合 */
    public static <T> List<T> getFieldList(List<?> list, String fieldName)
    {
        List<T> fieldValueList = new ArrayList<T>();
        for (Object one : list) {
            T fieldValue;
            if(one==null)
            {
                fieldValue = null;
            }
            else
            {
                Object fieldValueObj = getMethodInvoke(one, fieldName);
                @SuppressWarnings("unchecked")
                T t = (T) fieldValueObj;
                fieldValue = t;
            }
            fieldValueList.add(fieldValue);
        }
        return fieldValueList;
    }
    
    /** 获取对象集合中以某个属性为key组成的Map */
    public static <K,T> Map<K, T> getFieldMap(List<T> list, String fieldName)
    {
        Map<K, T> fieldValueMap = new LinkedHashMap<K, T>();
        for (T one : list) {
            if(one!=null)
            {
                Object fieldValueObj = getMethodInvoke(one, fieldName);
                if(fieldValueObj!=null)
                {
                    @SuppressWarnings("unchecked")
                    K fieldValue = (K) fieldValueObj;
                    fieldValueMap.put(fieldValue, one);
                }
            }
        }
        return fieldValueMap;
    }
    
    /** 判断是否为数组 */
    public static boolean isArray(Class<?> clazz, Object o)
    {
        if(clazz==null&&o==null)
            return false;
        
        boolean clazzResult = true;
        if(clazz!=null)
        {
            clazzResult = clazz.isArray();
        }
        
        boolean objectResult = true;
        if(o!=null)
        {
            objectResult = o.getClass().isArray();
        }
        
        return clazzResult&&objectResult;
    }
    
    /** 当一个对象是数组类型时，转换为数组 */
    public static Object[] getArray(Object o)
    {
        if(!isArray(null,o))
            throw new ReflectException(ReflectUtil.class+".getArray param illegal,o="+o);
        
        Object[] array;
        
        Class<?> clazz = o.getClass();
        Class<?> oneClazz = clazz.getComponentType();
        if(oneClazz.isPrimitive())
        {
            if(byte.class.isAssignableFrom(oneClazz))
            {
                byte[] array1 = (byte[])o;
                int length = array1.length;
                Byte[] array2 = new Byte[length];
                for (int i = 0; i < length; i++) {
                    array2[i] = array1[i];
                }
                array = array2;
            }
            else if(short.class.isAssignableFrom(oneClazz))
            {
                short[] array1 = (short[])o;
                int length = array1.length;
                Short[] array2 = new Short[length];
                for (int i = 0; i < length; i++) {
                    array2[i] = array1[i];
                }
                array = array2;
            }
            else if(int.class.isAssignableFrom(oneClazz))
            {
                int[] array1 = (int[])o;
                int length = array1.length;
                Integer[] array2 = new Integer[length];
                for (int i = 0; i < length; i++) {
                    array2[i] = array1[i];
                }
                array = array2;
            }
            else if(long.class.isAssignableFrom(oneClazz))
            {
                long[] array1 = (long[])o;
                int length = array1.length;
                Long[] array2 = new Long[length];
                for (int i = 0; i < length; i++) {
                    array2[i] = array1[i];
                }
                array = array2;
            }
            else if(float.class.isAssignableFrom(oneClazz))
            {
                float[] array1 = (float[])o;
                int length = array1.length;
                Float[] array2 = new Float[length];
                for (int i = 0; i < length; i++) {
                    array2[i] = array1[i];
                }
                array = array2;
            }
            else if(double.class.isAssignableFrom(oneClazz))
            {
                double[] array1 = (double[])o;
                int length = array1.length;
                Double[] array2 = new Double[length];
                for (int i = 0; i < length; i++) {
                    array2[i] = array1[i];
                }
                array = array2;
            }
            else if(boolean.class.isAssignableFrom(oneClazz))
            {
                boolean[] array1 = (boolean[])o;
                int length = array1.length;
                Boolean[] array2 = new Boolean[length];
                for (int i = 0; i < length; i++) {
                    array2[i] = array1[i];
                }
                array = array2;
            }
            else if(char.class.isAssignableFrom(oneClazz))
            {
                char[] array1 = (char[])o;
                int length = array1.length;
                Character[] array2 = new Character[length];
                for (int i = 0; i < length; i++) {
                    array2[i] = array1[i];
                }
                array = array2;
            }
            else
            {
                throw new ReflectException(ReflectUtil.class+".getArray param.componentType illegal,oneClazz="+oneClazz);
            }
        }
        else
        {
            array = (Object[])o;
        }
        
        return array;
    }
    
}
