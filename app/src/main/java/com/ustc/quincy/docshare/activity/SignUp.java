package com.ustc.quincy.docshare.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ustc.quincy.docshare.MainActivity;
import com.ustc.quincy.docshare.R;
import com.ustc.quincy.docshare.util.NetUtil;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    private Button signup;
    private EditText username;
    private EditText password;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                // 登录信息返回结果
                String result=msg.getData().getString("result");
                if(result.equals("succeed\r\n"))
                {
                    Toast.makeText(SignUp.this,"succeed",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(SignUp.this,"false",Toast.LENGTH_LONG).show();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signup = (Button)findViewById(R.id.sign_up);
        username = (EditText)findViewById(R.id.sign_username);
        password = (EditText)findViewById(R.id.sign_password);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> message=new HashMap<String, String>();
                message.put("username",username.getText().toString());
                message.put("password",password.getText().toString());
                message.put("device_name", android.os.Build.MODEL);
                message.put("ip_address", getIpAddress());
                NetUtil.sendToServer("Signup",message,handler);

                //注册之后直接登录
                HashMap<String, String> message2 = new HashMap<String, String>();
                message2.put("username", username.getText().toString());
                message2.put("password", password.getText().toString());
                NetUtil.sendToServer("Login", message2, handler);

                //保存用户信息和登录状态
                SharedPreferences.Editor editor = getSharedPreferences("user_data", MODE_PRIVATE).edit();
                editor.putString("name",username.getText().toString());
                editor.putString("password", password.getText().toString());
                editor.putInt("status", 1);
                editor.commit();

                finish();
            }
        });
    }

    //获取本机IP
    public String getIpAddress() {
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        return (i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF)+ "." +
                ((i >> 24 ) & 0xFF );
    }
}
