package com.ustc.quincy.docshare;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;
import com.ustc.quincy.docshare.activity.LoginActivity;
import com.ustc.quincy.docshare.activity.ShowDevices;
import com.ustc.quincy.docshare.util.HttpUtils;
import com.ustc.quincy.docshare.util.JDBCUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton btnSearchDevices;
    private ImageButton btnEditImg;
    private ImageButton btnSendFile;
    private ImageButton btnReceiveFile;

    private Handler handler;
    private ServerSocket server;

    private List<String> deviceList;

    private int PORT = 6666;
    private String resultStr="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //控件初始化
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btnSearchDevices = (ImageButton) findViewById(R.id.btn_search_devices);
        btnEditImg = (ImageButton) findViewById(R.id.btn_edit_image);
        btnSendFile = (ImageButton) findViewById(R.id.btn_send_file);
        btnReceiveFile = (ImageButton) findViewById(R.id.btn_receive_file);

        //搜索设备
        btnSearchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast.makeText(MainActivity.this,"search", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ShowDevices.class);
                startActivity(intent);

//                try{
//                    sarchDevices();}
//                catch (Exception e){
//                    e.printStackTrace();
//                }
            }
        });

        //图片编辑
        btnEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit images
            }
        });

        //发送文件
        btnSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send files
            }
        });

        //接收文件
        btnReceiveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //receive files
            }
        });


        //消息处理
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        Log.v("DocShare", "收到消息");
                }
            }
        };

        //监听端口
        Thread listener = new Thread(new Runnable() {
            @Override
            public void run() {
                //绑定端口6666
                try {
                    //创建一个ServerSocket对象，并让这个Socket在6666端口监听
                    server = new ServerSocket(PORT);
                    //调用ServerSocket的accept()方法，接受客户端所发送的请求
                    Socket socket = server.accept();
                    //从Socket当中得到InputStream对象
                    InputStream inputStream = socket.getInputStream();
                    byte buffer [] = new byte[1024*4];
                    int temp = 0;
                    //从InputStream当中读取客户端所发送的数据
                    while((temp = inputStream.read(buffer)) != -1){
                        System.out.println(new String(buffer,0,temp));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(server != null){
                    Message.obtain(handler, 1, "本机IP：" + GetIpAddress() + " 监听端口:" + PORT).sendToTarget();
//                    while(true){
//                        //接收文件
//                    }
                }else{
                    Message.obtain(handler, 1, "绑定端口失败").sendToTarget();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.user) {
            // Handle the edit user profile action
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.history) {

        } else if (id == R.id.settting) {

        } else if (id == R.id.feedback) {

        } else if (id == R.id.about_us) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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

}
