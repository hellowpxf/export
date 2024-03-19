package utils;
import java.io.IOException;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:HttpUtils
 * @author:pxf
 * @data:2023/03/17
 **/
public class HttpUtils {
    public static String doGet(String url) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("accept","application/json, text/plain, */*");
        httpGet.setHeader("content-type","application/json;charset=UTF-8");
        // Send the request and get the response
        HttpResponse httpResponse = httpClient.execute(httpGet);
        // Print the response status code and body
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        System.out.println("Response status code: " + statusCode);
        return  responseBody;
    }

}
