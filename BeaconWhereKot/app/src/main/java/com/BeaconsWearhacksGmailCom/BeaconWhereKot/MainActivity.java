package com.BeaconsWearhacksGmailCom.BeaconWhereKot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.BeaconsWearhacksGmailCom.BeaconWhereKot.estimote.BeaconID;
import com.BeaconsWearhacksGmailCom.BeaconWhereKot.estimote.EstimoteCloudBeaconDetails;
import com.BeaconsWearhacksGmailCom.BeaconWhereKot.estimote.EstimoteCloudBeaconDetailsFactory;
import com.BeaconsWearhacksGmailCom.BeaconWhereKot.estimote.ProximityContentManager;
import com.estimote.sdk.cloud.model.Color;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;

    private static final Map<Color, Integer> BACKGROUND_COLORS = new HashMap<>();
    static {
        BACKGROUND_COLORS.put(Color.ICY_MARSHMALLOW, android.graphics.Color.rgb(109, 170, 199));
        BACKGROUND_COLORS.put(Color.BLUEBERRY_PIE,   android.graphics.Color.rgb( 98,  84, 158));
        BACKGROUND_COLORS.put(Color.MINT_COCKTAIL,   android.graphics.Color.rgb(155, 186, 160));
    }
    private static final int BACKGROUND_COLOR_NEUTRAL = android.graphics.Color.rgb(160, 169, 172);

    private ProximityContentManager proximityContentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: make sure the user knows why your app wants to access their location
            // you could make it part of your app's onboarding process, or precede the system popup
            // with your own one, which goes into more detail
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            onPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted();
                } else {
                    Log.w(TAG, "Permission to access user's location was denied, which means the app won't be able to scan for beacons");
                }
                break;
        }
    }

    private void onPermissionGranted() {
        Log.d(TAG, "Permission to access user's location granted, initializing ProximityContentManager");
        proximityContentManager = new ProximityContentManager(this,
                Arrays.asList(
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 57608, 12090),
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 29098, 1493),
                        new BeaconID("B9407F30-F5F8-466E-AFF9-25556B57FE6D", 25966, 60904)),
                new EstimoteCloudBeaconDetailsFactory());
        proximityContentManager.setListener(new ProximityContentManager.Listener() {
            @Override
            public void onContentChanged(Object content) {
                String text;
                Integer backgroundColor;
                if (content != null) {
                    EstimoteCloudBeaconDetails beaconDetails = (EstimoteCloudBeaconDetails) content;
                    text = "You're in " + beaconDetails.getBeaconName() + "'s range!";
                    backgroundColor = BACKGROUND_COLORS.get(beaconDetails.getBeaconColor());
                } else {
                    text = "No beacons in range.";
                    backgroundColor = null;
                }
                ((TextView) findViewById(R.id.textView)).setText(text);
                findViewById(R.id.relativeLayout).setBackgroundColor(
                        backgroundColor != null ? backgroundColor : BACKGROUND_COLOR_NEUTRAL);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (proximityContentManager != null) {
            Log.d(TAG, "Pausing ProximityContentManager content updates");
            proximityContentManager.stopContentUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (proximityContentManager != null) {
            Log.d(TAG, "Resuming ProximityContentManager content updates");
            proximityContentManager.startContentUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (proximityContentManager != null) {
            proximityContentManager.destroy();
        }
    }
}
