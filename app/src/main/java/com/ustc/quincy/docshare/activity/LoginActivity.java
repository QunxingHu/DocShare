package com.ustc.quincy.docshare.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ustc.quincy.docshare.R;
import com.ustc.quincy.docshare.util.NetUtil;

import java.util.HashMap;

/**
 * Author: Created by QuincyHu on 2016/6/29 0029 21:07.
 * Email:  zhihuqunxing@163.com
 */
public class LoginActivity extends AppCompatActivity {
    private Button login;
    private Button logout;
    private Button signUp;
    private EditText username;
    private EditText password;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                // 登录信息返回结果
                String result = msg.getData().getString("result");
                //显示状态，同时要将状态用sharedpreference 保存到本机
                if (result.equals("succeed\r\n")) {
                    Toast.makeText(LoginActivity.this, "succeed", Toast.LENGTH_LONG).show();
                    //登录成功将用户信息保存到sharedpreference
                    SharedPreferences.Editor editor = getSharedPreferences("user_data", MODE_PRIVATE).edit();
                    editor.putString("name", username.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.putInt("status", 1);
                    //  editor.putString("name","Tom");
                    editor.commit();
                } else {
                    Toast.makeText(LoginActivity.this, "false", Toast.LENGTH_LONG).show();
                }
            }

        }
    };
    Handler handlerout = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                // 登录信息返回结果
                String result = msg.getData().getString("result");
                //显示状态，同时要将状态用sharedpreference 保存到本机
                if (result.equals("succeed\r\n")) {
                    Toast.makeText(LoginActivity.this, "logout succeed", Toast.LENGTH_LONG).show();
                    //改写设备状态
                    SharedPreferences.Editor editor = getSharedPreferences("user_data", MODE_PRIVATE).edit();
                    editor.putString("name", username.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.putInt("status", 0);
                    editor.commit();
                } else {
                    Toast.makeText(LoginActivity.this, "logout failed", Toast.LENGTH_LONG).show();
                }
            }
        }

    };


    /////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button) findViewById(R.id.login_login);
        logout = (Button) findViewById(R.id.login_logout);
        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        signUp = (Button) findViewById(R.id.btn_signup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> message = new HashMap<String, String>();
                message.put("username", username.getText().toString());
                message.put("password", password.getText().toString());
                NetUtil.sendToServer("Login", message, handler);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
        ////////////////////
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("user_data", MODE_PRIVATE);
                String username = pref.getString("name", "");
                String password = pref.getString("password", "");
                //int status = pref.getInt("status", 0);
//                //如果没有找到传入默认值0

                HashMap<String, String> message = new HashMap<String, String>();
                message.put("username", username);
                message.put("password", password);
//                message.put("username",username.getText().toString());
                NetUtil.sendToServer("logout", message, handlerout);

//                SharedPreferences.Editor editor = getSharedPreferences("user_data",MODE_PRIVATE).edit();
//                editor.putString("name", username);
//                editor.putString("password", password);
//                editor.putInt("status",0);
//                editor.commit();
            }
        });

    }
}
