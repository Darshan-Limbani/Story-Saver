package com.saver.storysaver;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

public class MyApplication extends Application {

    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }

    public static Context getContext()
    {
        return context;
    }
}
