package com.BeaconsWearhacksGmailCom.BeaconWhereKot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.BeaconsWearhacksGmailCom.BeaconWhereKot.estimote.BeaconID;
import com.BeaconsWearhacksGmailCom.BeaconWhereKot.estimote.EstimoteCloudBeaconDetails;
import com.BeaconsWearhacksGmailCom.BeaconWhereKot.estimote.EstimoteCloudBeaconDetailsFactory;
import com.BeaconsWearhacksGmailCom.BeaconWhereKot.estimote.ProximityContentManager;
import com.estimote.sdk.cloud.model.Color;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "MainActivity";
    private static final int CAM_REQUEST = 1313;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private static final Map<Color, Integer> BACKGROUND_COLORS = new HashMap<>();
    static {
        BACKGROUND_COLORS.put(Color.ICY_MARSHMALLOW, android.graphics.Color.rgb(255, 255, 255)); //android.graphics.Color.rgb(109, 170, 199));
        BACKGROUND_COLORS.put(Color.BLUEBERRY_PIE, android.graphics.Color.rgb(255, 255, 255)); //android.graphics.Color.rgb( 98,  84, 158));
        BACKGROUND_COLORS.put(Color.MINT_COCKTAIL,   android.graphics.Color.rgb(255,255,255));//android.graphics.Color.rgb(155, 186, 160));
    }
    private static final int BACKGROUND_COLOR_NEUTRAL = android.graphics.Color.rgb(255,255,255);
    private ProximityContentManager proximityContentManager;
    public ImageView photoTaken; //added imageview for the camera to show
    private Bitmap thumbnail;

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

    //TODO break down into modular functions and what not
    private void onPermissionGranted() {
        final Button btn = (Button) findViewById(R.id.button_continue);
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
                    text = "You found the " + beaconDetails.getBeaconName() + " checkpoint!";
                    backgroundColor = BACKGROUND_COLORS.get(beaconDetails.getBeaconColor());

                    //Animate the button to flash when viewable (Visible)
                    final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
                    animation.setDuration(500); // duration - half a second
                    animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                    animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                    animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
                    btn.startAnimation(animation);
                    btn.setVisibility(View.VISIBLE);

                    photoTaken = (ImageView) findViewById(R.id.imageView2); //awoodside96

                    btn.setOnClickListener(new btnTakeSelfieClicker()); //awoodside96

                    //set the image view to the beacon image for the imageView item
                    ImageView img= (ImageView) findViewById(R.id.imageView);
                    img.setImageResource(R.drawable.beacon);



                } else {
                    text = "No checkpoints in range. Keep walking!";
                    backgroundColor = null;

                    //gets rid of the animation and sets button to invisible
                    btn.clearAnimation();
                    btn.setVisibility(View.INVISIBLE);

                    //set the image view to the nobeacon image for the imageView item
                    ImageView img= (ImageView) findViewById(R.id.imageView);
                    img.setImageResource(R.drawable.nobeacon);

                }
                ((TextView) findViewById(R.id.textView)).setText(text);
                findViewById(R.id.relativeLayout).setBackgroundColor(
                        backgroundColor != null ? backgroundColor : BACKGROUND_COLOR_NEUTRAL);
            }
        });
    }
    /***************** PHOTOS FUNC ********************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAM_REQUEST && resultCode == RESULT_OK){
            //setPic(); //decode picture file when result is returned
            thumbnail = (Bitmap) data.getExtras().get("data");

           // showUpload(this);
            Context context = getApplicationContext();
            Toast.makeText(context, "Photo upload was a success", Toast.LENGTH_SHORT).show();
        }
    }
//upload show later
//    private void showUpload(Activity act){
//        if(thumbnail != null) {
//            Intent photoIntent = new Intent(this, DisplayPhotoUploaded.class);
//            photoIntent.putExtra("bitmap-thumbnail", thumbnail);
//            startActivityForResult(photoIntent, 134);
//        }
//    }

    class btnTakeSelfieClicker implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent cameraInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraInt.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraInt, CAM_REQUEST);
            }
        }
    }
    /*************************************************/

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
