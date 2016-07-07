package com.ustc.quincy.docshare.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Author: Created by QuincyHu on 2016/7/1 0001 10:30.
 * Email:  zhihuqunxing@163.com
 */
public class HttpUtils {
    /**
     * 请求对应的基础URL
     */
    public static final String BASE_URL = "http://192.168.8.251:8080/login/servlet/";
    /**
     * 通过URL获取HttpPost请求
     * @param url
     * @return HttpPost
     */
    private static HttpPost getHttpPost(String url){
        HttpPost httpPost = new HttpPost(url);
        return httpPost;
    }
    /**
     * 通过HttpPost获取HttpPonse对象
     * @return httpPost
     * @throws ClientProtocolException
     * @throws IOException
     */
    private static HttpResponse getHttpResponse(HttpPost httpPost) throws ClientProtocolException, IOException {
        HttpResponse response = new DefaultHttpClient().execute(httpPost);
        return response;
    }
    /**
     * 将URL打包成HttpPost请求，发送，得到查询结果 网络异常 返回 "exception"
     * @param url
     * @return resultString
     */
    public static String getHttpPostResultForUrl(String url){
        System.out.println("url:"+url);
        HttpPost httpPost = getHttpPost(url);
        String resultString = null;

        try {
            HttpResponse response = getHttpResponse(httpPost);
            if(response.getStatusLine().getStatusCode() == 200)
                System.out.println("getstatuscode=200");
            resultString = EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            resultString = "exception";
            e.printStackTrace();
        } catch (IOException e) {
            resultString = "exception";
            e.printStackTrace();
        }

        return resultString;
    }


}
