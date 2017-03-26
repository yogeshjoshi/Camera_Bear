package com.example.joshiyogesh.camera_bear;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri uri; //to store image ,, uri

    /*Directory Name where Image will be saved*/
    private static String IMAGE_DIRECTORY_NAME = "myDirectory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
    /*To check if this Device has Camera or Not*/
    private boolean checkCameraHardware(Context context){
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
           //this device has camera
            return true;
        }
            else{
            //this device doesnt have camera
                return false;
            }

    }
    /*Capturing image will launch camera app request capture*/
    private void captureImage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        /**/
        startActivityForResult(intent,CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    /*
    * Helper methods
    * */
    /*
    * Creating file uri to store image
    * */
    public Uri getOutputMediaFileUri(int type){
     return Uri.fromFile(getOutputMediaFile(type));
    }
    private static File getOutputMediaFile(int type){
        //External SD card location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);
        //Create Storage Directory if it does not exist
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d(IMAGE_DIRECTORY_NAME , "couldn't be able to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }else {return null;}

    return mediaFile;
    }

}
