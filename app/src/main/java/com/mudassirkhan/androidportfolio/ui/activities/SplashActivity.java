package com.mudassirkhan.androidportfolio.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Launching the Main Activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}
