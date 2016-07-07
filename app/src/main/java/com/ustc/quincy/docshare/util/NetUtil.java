package com.ustc.quincy.docshare.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class NetUtil {

    static private HttpClient client= new DefaultHttpClient();;
    static   String URLPREV;
    static private HashMap<String,String> netRequestHeads=new HashMap<String,String>();

    static
    {
        URLPREV="http://192.168.8.251:8080/login/servlet/";      /* http:\\localhost:8080\ */
    }

    public static  void sendToServer(final String url, final HashMap<String,String> message,final Handler resultHandler)
    {

        sendToServer(url,message,resultHandler,0x123);
    }


    public static  void sendToServer(final String url, final HashMap<String,String> message,final Handler resultHandler, final int messageWhat)
    {

        final int i=0;
        new Thread() {
            @Override
            public void run() {


                String response=null;

                //使用apache HTTP客户端实现
                String urlStr = URLPREV+url;
                HttpPost request = new HttpPost(urlStr);


                List<NameValuePair> params = new ArrayList<NameValuePair>();
                //添加参数
                Set<String> keySet= message.keySet();
                for (String key:keySet
                     ) {
                    Log.i("NetUtil",key);
                    Log.i("NetUtil",key  +" "+message.get(key));
                    params.add(new BasicNameValuePair(key, message.get(key)));
                }

                //params.add(new BasicNameValuePair("password", "12345"));
                try
                {
                    //设置请求参数项
                    request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));


                    //执行请求返回相应
                    HttpResponse httpResponse = client.execute(request);


                    //判断是否请求成功
                    if(httpResponse.getStatusLine().getStatusCode()==200)
                    {
                        //获得响应信息
                        response = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
                    }
                    else
                    {
                        switch (httpResponse.getStatusLine().getStatusCode())
                        {
                            case 404:
                                Log.e("NetUtil", "http result code is not 200");
                                break;
                            default:
                                Log.e("NetUtil", "http result code is not 200");
                                break;
                        }
                    }
                }catch(Exception e)
                {
                    Log.e("NetUtil", "http get connection error");
                    e.printStackTrace();
                }
                if(response==null)
                {
                    return;
                }
                Message resultMessage=new Message();
                resultMessage.what=messageWhat;
                Bundle bundle=new Bundle();
                bundle.putString("result",response);
                resultMessage.setData(bundle);
                resultHandler.sendMessage(resultMessage);
            }
        }.start();
    }



}
