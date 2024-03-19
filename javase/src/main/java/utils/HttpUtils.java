package utils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:HttpUtils
 * @author:pxf
 * @data:2023/03/17
 **/
public class HttpUtils {
    public static String doPost(String url, HashMap<String,String> headerMap, String body) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        // Add request headers
        if(headerMap != null){
            headerMap.entrySet().stream()
                    .forEach(entry ->  httpPost.setHeader(entry.getKey(),entry.getValue()));
            httpPost.setHeader("accept","application/json, text/plain, */*");
            httpPost.setHeader("content-type","application/json");
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        }else {
            httpPost.setHeader("accept","application/json, text/plain, */*");
            httpPost.setHeader("content-type","application/json");
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        }
        StringEntity requestEntity = new StringEntity(body);
        requestEntity.setContentEncoding("utf-8");
        httpPost.setEntity(requestEntity);
        requestEntity.setContentType("application/x-www-form-urlencoded;charset=utf-8");

        // Send the request and get the response
        HttpResponse httpResponse = httpClient.execute(httpPost);
        // Print the response status code and body
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        return  responseBody;
    }
    public static String doGet(String url) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("accept","*/*");
        httpGet.setHeader("content-type","application/json;charset=UTF-8");
        // Send the request and get the response
        HttpResponse httpResponse = httpClient.execute(httpGet);
        // Print the response status code and body
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        return  responseBody;
    }

    /**
     * 设置请求头
     */
    public static HashMap setPostHeader(HttpServletRequest request){
        HashMap<String, String> headerMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name	= enumeration.nextElement();
            if ("login-token".equals(name)|| "channel".equals(name)||"zzbs".equals(name)) {
                String value = request.getHeader(name);
                headerMap.put(name, value);
                System.out.println(value);
            }
        }
        headerMap.remove("content-length");
        System.out.println();
        return headerMap;
    }

    /**
     * 某一时间增加n小时
     * @param dkrq
     * @param n
     * @return
     */
    public static String getHoursDate(String dkrq,String dksj, int n) {
        String timeStr = "";
        dksj = dkrq+" "+dksj;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date da1 = sdf.parse(dksj);
            long time = (da1.getTime())+(n*60*60*1000);
            Date date = new Date(time);
            timeStr = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return timeStr;
        }
        return timeStr;
    }

    /**
     * 比较两个时间（HH:mm）大小（不包含）
     * @param dkrq
     * @param t2
     * @return
     */
    public static boolean compareDate(String dkrq,String dksj, String t2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dksj = dkrq+" "+dksj;
        try {
            Date da1 = sdf.parse(dksj);
            Date da2 = sdf.parse(t2);
            if(da1.compareTo(da2)<0) {
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
