package com.ustc.quincy.docshare;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Created by QuincyHu on 2016/6/23 0023 15:18.
 * Email:  zhihuqunxing@163.com
 */
public class ScanDevices {

    private int SERVER_PORT = 6666;  //端口号
    private String LOCAL_ADDRESS;    //本地IP前缀
    private int location; //存放ip最后一位地址0~255
    private Context context;
    private ArrayList<HashMap<String,String>> listDevices;

    //构造函数
    public ScanDevices(Context con){
        this.context = con;
    }

    //扫描局域网内设备,获取在线设备ip列表
    public ArrayList<HashMap<String,String>> scan(){
        //获取本机IP前缀
        LOCAL_ADDRESS = getLocAddrIndex();
        if (LOCAL_ADDRESS.equals("")){
            Toast.makeText(context,"扫描失败，请检查wifi连接",Toast.LENGTH_SHORT).show();
            return null;
        }

        listDevices = new ArrayList<>();

        for(int j=0; j<256; j++) {
            location = j;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String current_ip = LOCAL_ADDRESS + ScanDevices.this.location;
                    if(!current_ip.equals(getIpAddress())) {
                        try {
                            Process p = Runtime.getRuntime().exec("ping -c 1 -w 0.5 " + current_ip);
                            int status = p.waitFor();
                            if (status == 0) {
                                HashMap<String,String> map = new HashMap<>();
                                map.put("ip", current_ip);
                                map.put("port","6666");
                                synchronized (this) {
                                    listDevices.add(map);
                                }
                                Log.v("DocShare","连接" + current_ip + "成功");

                            } else
                                Log.v("DocShare", "连接失败" + current_ip);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }


        return listDevices;
    }


    //获取本机IP
    public String getIpAddress() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        return (i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF)+ "." +
                ((i >> 24 ) & 0xFF );
    }

    //获取IP前缀
    public String getLocAddrIndex(){
        String str = getIpAddress();
        if(!str.equals("")){
            return str.substring(0,str.lastIndexOf(".")+1);
        }
        return null;
    }

    //获取本机设备名称
    public String getLocDeviceName() {
        return android.os.Build.MODEL;
    }

    public void getDevicesFromMySQL(int deviceId){

    }

}
