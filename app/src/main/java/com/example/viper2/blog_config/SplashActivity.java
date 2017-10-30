package com.example.viper2.blog_config;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

   private static  final long SPLASH_DELAY = 5000;
    ImageView IAnimate;
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        IAnimate = (ImageView) findViewById(R.id.IAnimate);
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.animation_splashscreen);
        IAnimate.startAnimation(animation);

        TimerTask task= new TimerTask() {
            @Override

            public void run() {

                Intent i = new Intent().setClass(SplashActivity.this, InitActivity.class);
                startActivity(i);
                SplashActivity.this.finish();

            }

        };
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_DELAY);

    }



}
