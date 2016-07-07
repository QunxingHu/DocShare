package com.ustc.quincy.docshare.activity;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.ustc.quincy.docshare.R;
import com.ustc.quincy.docshare.model.Device;
import com.ustc.quincy.docshare.util.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Author: Created by QuincyHu on 2016/6/24 0024 15:03.
 * Email:  zhihuqunxing@163.com
 */
public class ShowDevices extends AppCompatActivity {

    private ListView listView;
    private TextView localIp;
    private SimpleAdapter adapter;
    ArrayList<Device> devices;
    ArrayList<HashMap<String,String>> listData;
    private String resultString="";

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);

        listView = (ListView) findViewById(R.id.device_list_view);
        localIp = (TextView) findViewById(R.id.txt_ip);

        localIp.setText(GetIpAddress());
        devices = new ArrayList<>();
        listData = new ArrayList<>();
        handler= new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 100:{
                        Log.v("DocShare", "receive data from server");
                        //更新数据
                        listData.clear();
                        for(int i=0; i<devices.size();i++){
                            HashMap<String,String> map = new HashMap<>();
                            map.put("user_name",devices.get(i).getUserName());
                            map.put("device_name",devices.get(i).getDeviceName());
                            map.put("ip_address",devices.get(i).getIpAddress());
                            listData.add(map);
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ShowDevices.this,"数据刷新成功！",Toast.LENGTH_SHORT).show();
                    }break;
                }
            }
        };
        //扫描局域网设备
        //ScanDevices scanDev = new ScanDevices(ShowDevices.this);
        //listData = scanDev.scan();
        try {
            searchDevices();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //将数据显示出来
        for(int i=0; i<devices.size();i++){
            HashMap<String,String> map = new HashMap<>();
            map.put("user_name",devices.get(i).getUserName());
            map.put("device_name",devices.get(i).getDeviceName());
            map.put("ip_address",devices.get(i).getIpAddress());
            listData.add(map);
        }
        //生成适配器
        adapter = new SimpleAdapter(ShowDevices.this,
                listData,
                R.layout.device_item,
                new String[]{"user_name","device_name","ip_address"},
                new int[]{R.id.user_name, R.id.device_name, R.id.device_ip});
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ShowDevices.this,"device " + listData.get(position).get("ip_address"),Toast.LENGTH_SHORT).show();
                Device tempDevice = new Device();
                tempDevice.setUserName(listData.get(position).get("user_name"));
                tempDevice.setDeviceName(listData.get(position).get("device_name"));
                tempDevice.setIpAddress(listData.get(position).get("ip_address"));
                Intent intent = new Intent(ShowDevices.this, SendFile.class);
                intent.putExtra("target",tempDevice);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //刷新设备列表
        if (id == R.id.action_refresh) {
            try {
                searchDevices();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public String GetIpAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        return (i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF)+ "." +
                ((i >> 24 ) & 0xFF );
    }


    public void searchDevices() throws Exception{
        final String url = HttpUtils.BASE_URL+"scan";
        new Thread(new Runnable() {
            @Override
            public void run() {
                resultString= HttpUtils.getHttpPostResultForUrl(url);
                Log.v("DocShare","result string: " +resultString);
                parseJson(resultString);
                Message message = new Message();
                message.what = 100;
                handler.sendMessage(message);
            }
        }).start();
    }

    /** 方法名：parseJson()
     *  功能：解析多个json
     *  参数：param-->jsonstr
     *
     *
     * */
    public void parseJson(String jsonstr) {
        String str;
        try {
            devices.clear();
            JSONArray deviceArray = new JSONObject(jsonstr).getJSONArray("devices" );
            for(int i=0; i<deviceArray.length();i++) {
                JSONObject deviceInfo = deviceArray.getJSONObject(i);
                Device device = new Device();
                device.setUserName(deviceInfo.getString("user_name"));
                device.setDeviceName(deviceInfo.getString("device_name"));
                device.setIpAddress(deviceInfo.getString("ip_address"));
                str = "user_name:"+ device.getUserName()+"device_name:" +device.getDeviceName()+"ip_address"+device.getIpAddress();
                Log.v("DocShare","receive data: " + str);
                devices.add(device);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
