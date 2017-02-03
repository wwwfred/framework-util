package net.wwwfred.framework.util.compare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.wwwfred.framework.util.code.CodeUtil;
import net.wwwfred.framework.util.string.StringUtil;

public class CompareUtil {
    
    /** 特殊字符串替换的字符 */
    @SuppressWarnings("serial")
    public static Map<String, String> REPLACE_MAP = new LinkedHashMap<String, String>(){{
        put(System.getProperty("line.separator"), "");
        put(" ","");
        put("（","(");
        put("【","[");
        put("《","<");
        put("｛","{");
        put("）",")");
        put("】","]");
        put("》",">");
        put("｝","}");
    }};
    
    /** 比较二维对象数组中每一个一维数组的前两个对象值是否相等 */
    public static boolean equals(Object[][] compareDataArray)
    {
        // param check
        String paramIllegalString=null;
        if(CodeUtil.isEmpty(new Object[]{compareDataArray}))
        {
           paramIllegalString = "待比较的对象数组不能为空";
        }
        for(Object[] array : compareDataArray)
        {
            if(array==null||array.length<2)
            {
                paramIllegalString = "待比较的对象数组中存在不合法的比较数据";
                break;
            }
        }
        if(paramIllegalString!=null)
            throw new CompareException(paramIllegalString);
        
        for(Object[] array : compareDataArray)
        {
            Object obj1 = array[0];
            Object obj2 = array[1];
            if(obj1==null)
            {
                if(obj2!=null)
                {
                    return false;
                }
            }
            else
            {
                if(!obj1.equals(obj2))
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    /** 模糊匹配两个字符串是否相等 */
    public static boolean match(String one, String other)
    {
        if(one==null)
        {
            if(other==null)
            {
                return true;
            }
        }
        else
        {
            if(other!=null)
            {
                // string trim
                String trimOne = one.trim();
                String trimOther = other.trim();
                
                // compare
                if(trimOne.equalsIgnoreCase(trimOther))
                    return true;
                
                // replace special string
                String replaceOne = trimOne;
                String replaceOther = trimOther;
                Set<Entry<String, String>> entrySet = REPLACE_MAP.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    String oldString = entry.getKey();
                    String newString = entry.getValue();
                    replaceOne = StringUtil.replaceString(replaceOne, oldString,newString);
                    replaceOther = StringUtil.replaceString(replaceOther, oldString,newString);
                    
                    // compare
                    if(replaceOne.equalsIgnoreCase(replaceOther))
                        return true;
                }
            }
        }
        return false;
    }
    
    /** 过滤掉集合中模糊相同的字符串，筛选出已匹配的数据Map */
    public static Map<String, List<String>> filterSameMap(List<String> list)
    {
        Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
        
        int size = list.size();
        if(size>1)
        {
            // filter same
            List<String> newList = new ArrayList<String>(new LinkedHashSet<String>(list));
            
            String target = list.get(0);
            List<String> targetList = new ArrayList<String>();
            
            boolean mathed = false;
            for (String one : list) {
                if(target.equals(one)||match(target, one))
                {
                    mathed = true;
                    newList.remove(one);
                    
                    targetList.add(one);
                }
            }
            if(mathed)
            {
                newList.remove(target);
                
                result.put(target, targetList);
            }
            
            result.putAll(filterSameMap(newList));
        }
        
        return result;
    }
    
    /** 过滤掉集合中模糊相同的字符串（包含） */
    public static String[] filterSame(List<String> list)
    {
        List<String> resultList = new ArrayList<String>();
        
        int size = list.size();
        if(size>1)
        {
            // filter same
            List<String> newList = new ArrayList<String>(new LinkedHashSet<String>(list));
            
            String target = list.get(0);
            boolean mathed = false;
            for (String one : list) {
                if(target.equals(one)||match(target, one))
                {
                    mathed = true;
                    newList.remove(one);
                }
            }
            if(mathed)
            {
                newList.remove(target);
            }
            
            resultList.add(target);
            resultList.addAll(Arrays.asList(filterSame(newList)));
        }
        else if(size==1)
        {
            resultList.add(list.get(0));
        }
        
        return resultList.toArray(new String[]{});
    }
    
//  public static String PROPERTIES_CONFIG_FILE_NAME = "config.properties";
//  public static String REPLACE_STRING = PropertiesUtil.getValue(PROPERTIES_CONFIG_FILE_NAME, CompareUtil.class.getName()+".replace_string", 
//          "["
//          + "{\"key\":\"" + System.getProperty("line.separator") + "\",\"value\":\"" + "" + "\"}," 
//          + "{\"key\":\"" + " " + "\",\"value\":\"" + "" + "\"}," 
//          + "{\"key\":\"" + "（" + "\",\"value\":\"" + "(" + "\"}," 
//          + "{\"key\":\"" + "【" + "\",\"value\":\"" + "[" + "\"}," 
//          + "{\"key\":\"" + "｛" + "\",\"value\":\"" + "{" + "\"}," 
//          + "{\"key\":\"" + "《" + "\",\"value\":\"" + "<" + "\"}," 
//          + "{\"key\":\"" + "）" + "\",\"value\":\"" + ")" + "\"}," 
//          + "{\"key\":\"" + "】" + "\",\"value\":\"" + "]" + "\"}," 
//          + "{\"key\":\"" + "｝" + "\",\"value\":\"" + "}" + "\"}," 
//          + "{\"key\":\"" + "》" + "\",\"value\":\"" + ">" + "\"}" 
//          + "]");
//  public static class ReplaceModel
//  {
//      private String key;
//      private String value;
//      public String getKey() {
//          return key;
//      }
//      public void setKey(String key) {
//          this.key = key;
//      }
//      public String getValue() {
//          return value;
//      }
//      public void setValue(String value) {
//          this.value = value;
//      }
//  }
//  public static ReplaceModel[] REPLACE_ARRAY = JSONUtil.toModel(REPLACE_STRING, ReplaceModel[].class);

    
    public static void main(String[] args) {
        List<String> list = Arrays.asList(new String[]{"aaa","aa ddd", "aad","aa(ddd)","aa","c", "bb bb","aa（ddd）","c  ", "aa(ddd)", "深   圳", "深 圳", "深圳","bb","ccc","c","c c c"});
        System.out.println(Arrays.asList(filterSame(list)));
        System.out.println(filterSameMap(list));
        
//        String s1 = "(";
//        String s2 = "（";
//        System.out.println(s1.equalsIgnoreCase(s2));
        
    }
}
