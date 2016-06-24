package com.ustc.quincy.docshare.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.ustc.quincy.docshare.R;
import com.ustc.quincy.docshare.ScanDevices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Author: Created by QuincyHu on 2016/6/24 0024 15:03.
 * Email:  zhihuqunxing@163.com
 */
public class ShowDevices extends AppCompatActivity {

    private ListView listView;
    private List<String> devicesList;
    private SimpleAdapter adapter;
    ArrayList<HashMap<String,String>> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);

        //扫描局域网设备
        ScanDevices scanDev = new ScanDevices(ShowDevices.this);
        devicesList = scanDev.scan();

        listView = (ListView) findViewById(R.id.device_list_view);

        //准备数据
        listData = new ArrayList<>();

        //生成适配器
       adapter = new SimpleAdapter(this,
                                listData,
                                R.layout.device_item,
                                new String[]{"ip","port"},
                                new int[]{R.id.device_ip, R.id.device_port});
        listView.setAdapter(adapter);

        //数据填充
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(devicesList != null)
                    for(int i=0; i<devicesList.size(); i++){
                        HashMap<String,String> map = new HashMap<>();
                        map.put("ip", devicesList.get(i));
                        map.put("port","6666");
                        listData.add(map);
                    }
                adapter.notifyDataSetChanged();
            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ShowDevices.this,"device " + devicesList.get(position),Toast.LENGTH_SHORT).show();
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Toast.makeText(ShowDevices.this,"device " + devicesList.get(0),Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
        }
        return true;
    }

}
