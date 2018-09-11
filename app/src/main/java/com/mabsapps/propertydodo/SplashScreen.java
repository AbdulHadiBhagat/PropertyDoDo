package com.mabsapps.propertydodo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1500;
    private static int SPLASH_TIME_OUT2 = 0000;
    private ProgressBar progressBar;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        },SPLASH_TIME_OUT2);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                executeIfConnected();
                handler.postDelayed(this,4000);
            }
        },5000);

    }

    private boolean isNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null) {
            return false;
        } else {

            if (activeNetworkInfo.isConnected()) {
                return true;

            } else {
                return true;
            }
        }
    }

    public void executeIfConnected(){

        if(isNetwork()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.removeCallbacksAndMessages(null);
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }else{
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_LONG).show();
        }
    }

}
