package com.example.demoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class IntroScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(1400);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
                finally {
                    Intent in = new Intent(IntroScreen.this, MainActivity.class);
                    startActivity(in);
                    finish();
                }
            }
        };thread.start();

    }
}