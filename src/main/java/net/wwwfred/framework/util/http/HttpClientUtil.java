package net.wwwfred.framework.util.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpClientUtil{

    
    /**组合Php请求头部信息及参数信息并发送获取返回值
     * @param url 请求地址
     * @param params 参数列表Map类型
     * @param imei 手机imei码
     * @param appFrom 请求来源
     * @param apiKey 约定标识
     * @param timestamp 时间戳
     * @param signature 
     * @return 请求结果
     */
    public static String getPhpApiResponse(String url,Map<String,String> params,String imei,String appFrom,String apiKey,String timestamp,String signature){
        String rtn="";
        //获取httpClient对象
        CloseableHttpClient client =  HttpClients.createDefault();
        //发送post请求
        HttpPost httpP = new HttpPost(url);
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        if(params!=null&&params.size()>0){
            Set<String>keys=params.keySet();
            for(String key : keys){
                formparams.add(new BasicNameValuePair(key, params.get(key)));
            }
        }
        
        UrlEncodedFormEntity uefEntity;  
        try {  
            //String[] pas = params.values().toArray(new String[]{});
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpP.setHeader("imei",imei);
            httpP.setHeader("app-from"  ,appFrom);
            httpP.setHeader("app-key"  ,apiKey);
            httpP.setHeader("timestamp" ,timestamp);
            httpP.setHeader("signature" ,signature);
            httpP.setEntity(uefEntity);
            //发送请求
            CloseableHttpResponse response = client.execute(httpP);
            //获取文件的内容
            HttpEntity entity =  response.getEntity();
            
            if(entity!=null){
                //获得返回值json，并转换成Map
                rtn=EntityUtils.toString(entity,"utf-8");
            }
        }catch(Exception e){ 
            e.printStackTrace();
        }
        return rtn;
    }
    
    /**组合Php请求头部信息及参数信息并发送获取返回值
     * @param url 请求地址
     * @param params 参数列表Map类型
     * @param imei 手机imei码
     * @param appFrom 请求来源
     * @param apiKey 约定标识
     * @param timestamp 时间戳
     * @param signature 
     * @return 请求结果
     */
    public static String getPhpApiResponse(String url,Map<String,String> params,String imei,String appFrom,String apiKey,String timestamp,String signature,Integer timeout){
        String rtn="";
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                  .setSocketTimeout(timeout)
                  .setConnectTimeout(timeout)
                  .setConnectionRequestTimeout(timeout)
                  .setStaleConnectionCheckEnabled(true)
                  .build();
        //获取httpClient对象
        //CloseableHttpClient client =  HttpClients.createDefault();
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        //发送post请求
        HttpPost httpP = new HttpPost(url);
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        if(params!=null&&params.size()>0){
            Set<String>keys=params.keySet();
            for(String key : keys){
                formparams.add(new BasicNameValuePair(key, params.get(key)));
            }
        }
        
        UrlEncodedFormEntity uefEntity;  
        try {  
            //String[] pas = params.values().toArray(new String[]{});
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpP.setHeader("imei",imei);
            httpP.setHeader("app-from"  ,appFrom);
            httpP.setHeader("app-key"  ,apiKey);
            httpP.setHeader("timestamp" ,timestamp);
            httpP.setHeader("signature" ,signature);
            httpP.setEntity(uefEntity);
            //发送请求
            CloseableHttpResponse response = client.execute(httpP);
            //获取文件的内容
            HttpEntity entity =  response.getEntity();
            
            if(entity!=null){
                //获得返回值json，并转换成Map
                rtn=EntityUtils.toString(entity,"utf-8");
            }
        }catch(Exception e){ 
            e.printStackTrace();
        }
        return rtn;
    }
    
    /**组合Php请求头部信息及参数信息并发送获取返回值
     * @param url 请求地址
     * @param params 参数列表Map类型
     * @param imei 手机imei码
     * @param appFrom 请求来源
     * @param apiKey 约定标识
     * @param timestamp 时间戳
     * @param signature 
     * @return 请求结果
     */
    public static String getPhpApiResponse(String url,Map<String,String> params,Map<String,String> heads){
        String rtn="";
        //获取httpClient对象
        CloseableHttpClient client =  HttpClients.createDefault();
        //发送post请求
        HttpPost httpP = new HttpPost(url);
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        if(params!=null&&params.size()>0){
            Set<String>keys=params.keySet();
            for(String key : keys){
                formparams.add(new BasicNameValuePair(key, params.get(key)));
            }
        }
        
        UrlEncodedFormEntity uefEntity;  
        try {  
            //String[] pas = params.values().toArray(new String[]{});
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            if(heads!=null){
                Set<String> headKeySet=heads.keySet();
                for(String key:headKeySet){
                    httpP.setHeader(key,heads.get(key));
                }
            }
            httpP.setEntity(uefEntity);
            //发送请求
            CloseableHttpResponse response = client.execute(httpP);
            //获取文件的内容
            HttpEntity entity =  response.getEntity();
            
            if(entity!=null){
                //获得返回值json，并转换成Map
                rtn=EntityUtils.toString(entity,"utf-8");
            }
        }catch(Exception e){ 
            e.printStackTrace();
        }
        return rtn;
    }
    
    /** 内部调用php接口方法
     * @param url 请求地址
     * @param params 参数列表Map类型
     * @return 请求结果
     */
    public static String sendHttpRequest(String url,Map<String,String> params){
        return getPhpApiResponse(url, params, "", "JAVA-WITHOUT-SESSION", "API-FOR-JAVA-WITHOUT-SESSION", "1234567890", "278cc203f59fc079dc01283533b44c4f");
    }
    
    /** 内部调用php接口方法
     * @param url 请求地址
     * @param params 参数列表Map类型
     * @return 请求结果
     */
    public static String sendHttpRequest(String url,Map<String,String> params,Integer timeout){
        return getPhpApiResponse(url, params, "", "JAVA-WITHOUT-SESSION", "API-FOR-JAVA-WITHOUT-SESSION", "1234567890", "278cc203f59fc079dc01283533b44c4f",timeout);
    }
    
    /** 内部调用php接口方法
     * @param url 请求地址
     * @param params 参数列表Map类型
     * @param headers 额外的头部参数
     * @return 请求结果
     */
    public static String sendHttpRequest(String url,Map<String,String> params, Map<String, String> headers)
    {
        if( headers == null || headers.isEmpty() )
            return HttpClientUtil.sendHttpRequest(url, params);
        
        headers.put("imei", "");
        headers.put("app-from", "JAVA-WITHOUT-SESSION");
        headers.put("app-key", "API-FOR-JAVA-WITHOUT-SESSION");
        headers.put("timestamp", "1234567890");
        headers.put("signature", "278cc203f59fc079dc01283533b44c4f");
        
        return getPhpApiResponse(url, params, headers);
    }
    
    public static void main(String args[]){
        Map<String,String> params=new HashMap<String, String>();
        params.put("parent_id", "2552");
        //System.out.println(new HttpClientUtil().sendHttpRequest("http://www.t.teshehui.com/v2/api/goods/category", params));
    }
}
