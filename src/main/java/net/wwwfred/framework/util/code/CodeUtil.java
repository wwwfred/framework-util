package net.wwwfred.framework.util.code;

import java.nio.charset.Charset;

import net.wwwfred.framework.util.code.EmptyChecker;
import net.wwwfred.framework.util.code.EmptyCheckerImpl;
import net.wwwfred.framework.util.io.IOUtil;
import net.wwwfred.framework.util.json.JSONUtil;
import net.wwwfred.framework.util.json.JSONUtil.Json;
import net.wwwfred.framework.util.json.JSONUtil.JsonObject;
import net.wwwfred.framework.util.log.LogUtil;
import net.wwwfred.framework.util.reflect.AliasAnnotation;

public class CodeUtil {
	
    public static String PACKAGE_PREFIX = "package ";
    
    public static String FILE_SEPARATOR_TAG = System.getProperty("file.separator");

    public static String SEPARATOR_TAG = " ";
    
    public static String CLASS_NAME_SEPARATOR_TAG = ".";
    public static String JAVA_CODE_FILE_SUFFIX = ".java";
    
    public static String CLASS_IMPORT_TAG = "import ";
    
    public static String ANNOTATION_PREFIX_TAG = "@";
    
    public static String CLASS_PREFIX = "public class ";
    public static String INNER_CLASS_PREFIX = "public static class ";
    
    public static String CLASS_LEFT_TAG = "{";
    public static String CLASS_RIGHT_TAG = "}";
    public static String OBJECT_LEFT_TAG = "{";
    public static String OBJECT_RIGHT_TAG = "}";
    public static String ARRAY_LEFT_TAG = "[";
    public static String ARRAY_RIGHT_TAG = "]";
    public static String METHOD_LEFT_TAG = "(";
    public static String METHOD_RIGHT_TAG = ")";
    public static String METHOD_CONTENT_LEFT_TAG = "{";
    public static String METHOD_CONTENT_RIGHT_TAG = "}";
    
    public static Class<?> ALIAS_ANNOTATION_CLASS = AliasAnnotation.class;
    public static String ANNOTATION_LEFT_TAG = "(";
    public static String ANNOTATION_RIGHT_TAG = ")";
    public static String ANNOTATION_LEFT_QUOTE_TAG = "\"";
    public static String ANNOTATION_RIGHT_QUOTE_TAG = "\"";
    
    public static String FIELD_DECARATOR = "private ";
    
    public static String FIELD_TYPE_OBJECT = "Object";
    public static String FIELD_TYPE_STRING = "String";
    public static String FIELD_TYPE_BOOLEAN = "Boolean";
    public static String FIELD_TYPE_INTEGER = "Integer";
    public static String FIELD_TYPE_LONG = "Long";
    public static String FIELD_TYPE_DOUBLE = "Double";
    
    public static String FIELD_VALUE_SEPARATOR_TAG = "=";
    public static String FIELD_VALUE_NEW_TAG = "new ";
    
    public static String ILLEGAL_FIELD_NAME_PREFIX = "key";
    
    public static String METHOD_DECARATOR = "public ";
    public static String GET_METHOD_PREFIX = "get";
    public static String SET_METHOD_PREFIX = "set";
    
    public static String OBJECT_THIS_TAG = "this";
    
    public static String OBJECT_FIELD_SEPARATOR_TAG = ".";
    
    public static String METHOD_VOID_TAG = "void ";
    
    public static String METHOD_RETURN_TAG = "return ";
    
    public static String LINE_END_TAG = ";";
    
    public static String javaCodeProduce(String packagePrefix,String className,byte[] byteData, String encoding)
    {
        if(packagePrefix==null||className==null||byteData==null)
            throw new CodeException("CodeUtil.javaCodeProduce parameter is null.");
        
        String packageString;
        String classSimpleName;
        
        // parse package name
        int tempIndexOf = className.lastIndexOf(CodeUtil.CLASS_NAME_SEPARATOR_TAG);
        if(tempIndexOf!=-1)
        {
            packageString = className.substring(0, tempIndexOf);
            classSimpleName = className.substring(tempIndexOf+CodeUtil.CLASS_NAME_SEPARATOR_TAG.length());
        }
        else
        {
            packageString = null;
            classSimpleName = className;
        }
        
        String javaCode;
        Charset charset = encoding!=null?Charset.forName(encoding):Charset.defaultCharset();
        String stringData = new String(byteData,charset);
        Json json = JSONUtil.parseString(stringData);
        if(json instanceof JsonObject)
        {
            javaCode = JsonParseUtil.getJavaCodeContentFromJson(packageString,classSimpleName, stringData);
        }
        else
        {
            javaCode = XmlParseUtil.getJavaCodeContentFromXml(packageString,classSimpleName, byteData);
        }
        
        // 生成javaCode文件
        String fileRealPath = produceJavaCodeFile(packagePrefix,packageString,classSimpleName,javaCode.getBytes());
        LogUtil.i(CodeUtil.class.getSimpleName(), "CodeUtil.javaCodeProduce file realPath="+fileRealPath);
        return fileRealPath;
    }
    private static String produceJavaCodeFile(String packagePrefix,String packageString,String classSimpleName,byte[] javaCodeData) {
        // parse className
        String filePath = packagePrefix + CodeUtil.FILE_SEPARATOR_TAG + packageString.replace(CodeUtil.CLASS_NAME_SEPARATOR_TAG, CodeUtil.FILE_SEPARATOR_TAG);
        String fileName = classSimpleName+CodeUtil.JAVA_CODE_FILE_SUFFIX;
        
        return IOUtil.writeLocalData(javaCodeData, filePath, fileName);
    }
	
    public static EmptyChecker defaultEmptyChecker = new EmptyCheckerImpl();
    public static void setDefaultEmptyChecker(EmptyChecker defaultEmptyChecker) {
        CodeUtil.defaultEmptyChecker = defaultEmptyChecker;
    }
    
    public static boolean isEmpty(Object... obj)
    {
        if(obj==null||obj.length==0)
            return true;
        for (Object one : obj) {
            if(defaultEmptyChecker.isEmpty(one))
                return true;
        }
        return false;
    }
    
    public static void emptyCheck(String emptyDescription,Object[] obj)
    {
        if(isEmpty(obj))
            throw new CodeException(emptyDescription);
    }
    
    public static void emptyCheck(String code,String emptyDescription,Object[] obj)
    {
        if(isEmpty(obj))
            throw new CodeException(code,emptyDescription,null);
    }
    
    /** 获取当前线程的调用的代码的方法函数及位置 */
    public static StackTraceElement getLocationStackTrace(String className)
    {
    	StackTraceElement[] stackArray = Thread.currentThread().getStackTrace();
        if(stackArray!=null)
        {
        	int len = stackArray.length;
        	String endClassName = className;
        	Integer preIndex = null;
        	for (int i = len-1; i >= 0; i--) {
				String oneClassName = stackArray[i].getClassName();
				if(oneClassName.equals(endClassName)||(oneClassName.contains(endClassName)&&oneClassName.charAt(oneClassName.indexOf(endClassName)+endClassName.length()+1)=='$'))
				{
					preIndex = i;
				}
				else if(preIndex!=null)
				{
					return stackArray[preIndex+1];
				}
        	}
        }
        return null;
    }
    
    /** 获取当前线程的调用的代码的方法函数及位置 */
    public static String getLocation(String className)
    {
        StackTraceElement stack = getLocationStackTrace(className);
        return stack==null?null:(stack.getClassName()+"."+stack.getMethodName()+"("+stack.getFileName()+":"+stack.getLineNumber()+")");
    }
    
    /** 获取当前线程的调用的代码的方法函数及位置 */
    public static String getLocation()
    {
        StackTraceElement stack = getLocationStackTrace(CodeUtil.class.getName());
        return stack==null?null:(stack.getClassName()+"."+stack.getMethodName()+"("+stack.getFileName()+":"+stack.getLineNumber()+")");
    }
	
	public static void main(String[] args) {
	    
//	    // code produce test
//        String packagePrefix = "D:/java_project20141208/utils/src/test/java";
//        String className = "com.teshehui.util.json.test.TestOne1";
//        className = "com.teshehui.util.json.test.TestOne2";
//        byte[] fileData = IOUtil.getByteArrayFromInputStream(CodeUtil.class.getClassLoader().getResourceAsStream("ctripOrderInfoResponse.xml"));
//        fileData = IOUtil.getByteArrayFromInputStream(CodeUtil.class.getClassLoader().getResourceAsStream("jsonFile.json"));
//        javaCodeProduce(packagePrefix, className, fileData,null);
//        
//        // empty check test
        
		System.out.println(getLocation());
    }
}
