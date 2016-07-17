package com.prys;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;

/**
 * Created by edocsss on 10/7/16.
 */
public class PrysApp extends Application {
    public static final String APP_NAME = "KooPal";

    private static Context context;

    @Override
    public void onCreate () {
        super.onCreate();
        context = getApplicationContext();
        FacebookSdk.sdkInitialize(context);
    }

    public static Context getContext() {
        return context;
    }
}
