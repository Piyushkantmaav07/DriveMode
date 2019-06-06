package com.piya.driveMode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
    private static int  SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);




       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               Intent homeIntent  = new Intent(SplashActivity.this,MainActivity.class);
               startActivity(homeIntent);
               finish();
           }
       },SPLASH_TIME_OUT);


    }
}
