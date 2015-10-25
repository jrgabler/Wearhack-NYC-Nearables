package com.BeaconsWearhacksGmailCom.BeaconWhereKot;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class DisplayPhotoUploaded extends AppCompatActivity {
    private ImageView photoTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_display_photo_uploaded);

        //the imageview
        photoTaken = (ImageView) findViewById(R.id.uploadImgView);

        //get the intent and load into the object
        Intent intent= getIntent();
        Bitmap thumbnail = intent.getParcelableExtra("bitmap-image");

        //create the button object
        Button upload = (Button) findViewById(R.id.button_upload);

        //create a thumbnail view for the page
        photoTaken.setImageBitmap(thumbnail); //creates a thumbnail preview
        Uri tempUri = getImageUri(getApplicationContext(), thumbnail);
        new File(getRealPathFromURI(tempUri));

        //print out the phototaken
        setContentView(photoTaken);
    }

    /**
     * on click, the intent finishes
     */
    private void finishAct(){
        finish();
    }

        protected Uri getImageUri(Context inContext, Bitmap inImage){ //upload server
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, b);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    protected String getRealPathFromURI(Uri uri){ //upload server
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        int idx = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        }
        return cursor.getString(idx);
    }


}
