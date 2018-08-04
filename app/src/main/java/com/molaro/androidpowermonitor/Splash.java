package com.molaro.androidpowermonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;
import android.os.SystemClock;


import com.molaro.androidpowermonitor.MainActivity;

/*
*   Class
* ------------------------
*   Description
*
*/
public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }
}
