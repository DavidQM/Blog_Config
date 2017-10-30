package com.example.viper2.blog_config;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InitActivity extends AppCompatActivity {

    Button btnConnet, btnExit;
    //pag principal
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        btnConnet = (Button)findViewById(R.id.btnConnect);
        btnExit = (Button)findViewById(R.id.btnExit);

        btnConnet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(InitActivity.this, DevicesActivity.class);
                startActivity(i);
                finish();
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


    }
}
