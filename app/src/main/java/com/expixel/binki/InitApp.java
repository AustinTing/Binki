package com.expixel.binki;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;


public class InitApp extends Application {
    public static final String TAG = "binki";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, this.getClass().getSimpleName() + ": onCreate");
    }
}
