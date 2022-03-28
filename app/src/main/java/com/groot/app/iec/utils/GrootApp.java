package com.groot.app.iec.utils;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class GrootApp extends Application {

    public static GrootApp grootApp;

    public static GrootApp getGrootApp() {
        return grootApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        grootApp = this;

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

     //   MobileAds.initialize(this) {}

        // initialize the AdMob app
    //    MobileAds.initialize(this, "ca-app-pub-8226757287939653~6186996254");
    }
}
