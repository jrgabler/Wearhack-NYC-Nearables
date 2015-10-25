package com.BeaconsWearhacksGmailCom.BeaconWhereKot;

import android.app.Application;

import com.estimote.sdk.EstimoteSDK;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EstimoteSDK.initialize(getApplicationContext(), "beacon-where--kot", "7050d8d6cf0b91119d3554b9065b6aa1");

        // uncomment to enable debug-level logging
        // it's usually only a good idea when troubleshooting issues with the Estimote SDK
//        EstimoteSDK.enableDebugLogging(true);
    }
}
