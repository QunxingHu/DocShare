package com.ustc.quincy.docshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.ustc.quincy.docshare.R;

/**
 * Created by Administrator on 2016-07-04.
 */
public class Synthesize extends Activity {
    private Button template1;
    private Button template2;
    private Button template3;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synthesize);
        template1 = (Button) findViewById(R.id.template1);
        template1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Synthesize.this, Template1.class);
                startActivity(intent1);
            }
        });
        template2 = (Button) findViewById(R.id.template2);
        template2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Synthesize.this, Template2.class);
                startActivity(intent2);
            }
        });
        template3 = (Button) findViewById(R.id.template3);
        template3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(Synthesize.this, Template3.class);
                startActivity(intent3);
            }
        });
    }

}
