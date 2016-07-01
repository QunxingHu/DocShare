package com.ustc.quincy.docshare.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ustc.quincy.docshare.util.NetUtil;
import com.ustc.quincy.docshare.R;

import java.util.HashMap;

public class SignupSucceed extends AppCompatActivity {
    private Button confirm;
    private Button cancel;
    private TextView title;
    /////
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                // 登录信息返回结果
                String result = msg.getData().getString("result");
                //显示状态，同时要将状态用sharedpreference 保存到本机
                if (result.equals("succeed\r\n")) {
                    Toast.makeText(SignupSucceed.this, "succeed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignupSucceed.this, "false", Toast.LENGTH_LONG).show();
                }
            }

        }
    };
    /////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_succeed);
        confirm = (Button)findViewById(R.id.sign_confirm);
        cancel = (Button)findViewById(R.id.sign_cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String username = intent.getStringExtra("username");
                String password = intent.getStringExtra("password");
                HashMap<String, String> message = new HashMap<String, String>();
                message.put("username", username);
                message.put("password", password);
                NetUtil.sendToServer("Login", message, handler);
                //保存用户信息和登录状态
                SharedPreferences.Editor editor = getSharedPreferences("user_data", MODE_PRIVATE).edit();
                editor.putString("name", username);
                editor.putString("password", password);
                editor.putInt("status", 1);
                editor.commit();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
