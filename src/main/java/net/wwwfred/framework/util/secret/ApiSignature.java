package net.wwwfred.framework.util.secret;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.wwwfred.framework.util.code.CodeUtil;
import net.wwwfred.framework.util.math.MathUtil;
import net.wwwfred.framework.util.sort.SortUtil;


/**PHP 访问加密类
 * @author Johney.lee liq
 *
 * 2014-7-15
 */
public class ApiSignature {
	
	/**MD5加密信息 MD5(params进行排序后+timestamp)
	 * @param params
	 * @param timestamp
	 * @return String
	 */
	public static String getApiSignature(String token,String[] params,String timestamp){
	    
		return EncryptUtil.getMD5(getSign(token,params,timestamp));
	}
	
	/**MD5加密信息 MD5(params进行排序后+timestamp)
     * @param params
     * @param timestamp
     * @return String
     */
    public static String getApiSignature(String token,String[] params,String timestamp,int sortNum){
        
        return EncryptUtil.getMD5(getSign(token,params,timestamp,sortNum));
    }
	
	/**算法密码 params进行排序后+timestamp
	 * @param params 
	 * @param timestamp
	 * @return String
	 */
	private static String getSign(String token,String[] params,String timestamp){
		StringBuffer keyString = new StringBuffer(token);
		if(params!=null)
		{
			Arrays.sort(params);
			for(String str : params){
				keyString.append(str);
			}
		}
		keyString.append(timestamp);
		return keyString.toString();
	}
	
	/**算法密码 params进行排序后+timestamp
     * @param params 
     * @param timestamp
     * @return String
     */
    private static String getSign(String token,String[] params,String timestamp,int sortNum){
        
        StringBuffer keyString = new StringBuffer(token);
        if(params!=null)
        {
            Arrays.sort(params, new Comparator<String>() {

                @Override
                public int compare(String o1, String o2) {
                    if(o1==null)
                    {
                        return -1;
                    }
                    else if(o2==null)
                    {
                        return 1;
                    }
                    else if(MathUtil.isNumber(o1)&&MathUtil.isNumber(o2)){
                    	
                        if(o1.startsWith("0")||o2.startsWith("0"))
                        {
                            return o1.compareTo(o2);
                        }
                        else
                        {
                            return MathUtil.getNumber(o1, Double.class).compareTo( MathUtil.getNumber(o2, Double.class));
                        }
                    }
                    else if(MathUtil.isNumber(o1))
                    {
                    	return -1;
                    }
                    else if(MathUtil.isNumber(o2))
                    {
                    	return 1;
                    }
                    else{
                       return o1.compareTo(o2);
                    }
                }
            });
            for(String str : params){
                keyString.append(str);
            }
        }
        keyString.append(timestamp);
        return keyString.toString();
    }
    
    /**
     * 用指定的ApiKey、指定的编码、指定的时间戳（单位到秒）计算出传输数据的MD5的签名值
     * @param apiKey 指定的ApiKey
     * @param paramMap 待签名的数据
     * @param encoding 指定的编码
     * @param timestamp 指定的时间戳（单位到秒）
     * @return
     */
    public static String getMD5Sign(String apiKey,Map<String,Object> paramMap,String encoding,String timestamp)
    {
    	// timstamp if expired
    	
    	String signData = "";
    	if(!CodeUtil.isEmpty(paramMap))
    	{
    		// remove empty key value
    		paramMap = getNotEmptyMap(paramMap);
    		
    		// sort: {k1=v1,k2=v2,k3=v3}
    		paramMap = SortUtil.getSortedKeyMap(paramMap);
    		
    		// signData：k1=v1&k2=v2&k3=v3
        	signData = getSignData(paramMap);
    	}
    	
    	// add apiKey and timestamp
    	signData = apiKey + signData + timestamp;
    	
    	// sign
    	return EncryptUtil.getMD5(signData,encoding).toUpperCase();
    }

	private static Map<String, Object> getNotEmptyMap(
			Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		Set<Entry<String, Object>> entrySet = paramMap.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey()==null?"":entry.getKey();
			String value = entry.getValue()==null?"":entry.getValue().toString();
			if(!"".equals(key.trim())&&!"".equals(value.trim()))
			{
				map.put(key, entry.getValue());
			}
		}
		return map;
	}

	private static String getSignData(Map<String, Object> paramMap) {
		StringBuilder sb = new StringBuilder();
		Set<Entry<String, Object>> entrySet = paramMap.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		String s = sb.toString();
		if(s.endsWith("&"))
		{
			s = s.substring(0,s.length()-1);
		}
		return s;
	}
    
}
