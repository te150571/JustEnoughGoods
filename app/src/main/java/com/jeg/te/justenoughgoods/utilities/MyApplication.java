package com.jeg.te.justenoughgoods.utilities;

import android.app.Application;
import android.content.Context;
import android.util.TimeUtils;

import com.beardedhen.androidbootstrap.TypefaceProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        TypefaceProvider.registerDefaultIconSets();
    }

    public static Context getContext(){
        return mContext;
    }
}
