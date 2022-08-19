package com.saver.storysaver.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.saver.storysaver.ListActivity;
import com.saver.storysaver.R;
import com.saver.storysaver.utils.Util;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Log.d("PATH_FILE","------------ ROOT ------- " + Util.ROOT);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean(Util.IS_PERMISSION, false))
                    startActivity(new Intent(SplashScreenActivity.this, GettingStartedActivity.class));
                else
                    startActivity(new Intent(SplashScreenActivity.this, ListActivity.class));


                    /*if (!getSharedPreferences(getPackageName(), MODE_PRIVATE).getBoolean("isLogin", false))
                    startActivity(new Intent(SplashScreenActivity.this, ExploreActivity.class));
                else
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));*/
                finish();
            }
        }, 3000);

    }
}