package com.ustc.quincy.docshare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.ustc.quincy.docshare.activity.LoginActivity;
import com.ustc.quincy.docshare.activity.ReceiveFile;
import com.ustc.quincy.docshare.activity.ShowDevices;
import com.ustc.quincy.docshare.activity.Synthesize;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton btnSearchDevices;
    private ImageButton btnEditImg;
    private ImageButton btnSendFile;
    private ImageButton btnReceiveFile;
    private TextView txtUser;



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
        txtUser = (TextView) navigationView.inflateHeaderView(R.layout.nav_header_main).findViewById(R.id.main_uer_name);

        //搜索设备
        btnSearchDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast.makeText(MainActivity.this,"search", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ShowDevices.class);
                startActivity(intent);
            }
        });

        //图片编辑
        btnEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit images
                Intent intent = new Intent(MainActivity.this, Synthesize.class);
                startActivity(intent);
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
               Intent intent = new Intent(MainActivity.this, ReceiveFile.class);
                startActivity(intent);
            }
        });


        //自动登录
        SharedPreferences pref = getSharedPreferences("user_data",MODE_PRIVATE);
        String name = pref.getString("name","");
        String password= pref.getString("password","");
        int status = pref.getInt("status",0);
        if(status==1){
            txtUser.setText(name);
            Toast.makeText(MainActivity.this,name +"登录成功",Toast.LENGTH_SHORT).show();
        }

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


}
