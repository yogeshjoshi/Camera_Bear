package com.example.joshiyogesh.camera_bear;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks , GoogleApiClient.OnConnectionFailedListener {

    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri; //to store image ,, uri

    /*Directory Name where Image will be saved*/
    private static String IMAGE_DIRECTORY_NAME = "myDirectory";

    ImageView imageView;
    Button takeImageButton;

    /*---------Google Api Client --------------*/
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    String mLongitude,mLatitude;
    Location mLastLocation;
    /*---------And Location request -----------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        imageView = (ImageView)findViewById(R.id.imageView);
        takeImageButton = (Button)findViewById(R.id.buttonClick);
        takeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraHardware(MainActivity.this)){
                    captureImage();
                }else {
                    Toast.makeText(MainActivity.this,"Device Doesn't have Camera",Toast.LENGTH_LONG)
                            .show();
                }
            }
        });



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
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        startActivityForResult(intent,CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    /*
    * Helper methods ------------------------------------------------------------
    * */
    /*
    * Creating file uri to store image
    * */
    public Uri getOutputMediaFileUri(int type){
     return Uri.fromFile(getOutputMediaFile(type));
    }
    private static File getOutputMediaFile(int type){
        //External SD card location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES),IMAGE_DIRECTORY_NAME);
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
/*---------------------------------------------------------------------------------*/
    /*
    * Receiving Activity result will be called after Camera Has been Closed  , i.e
    *image has been cliked
    * */
    @Override
    protected void onActivityResult(int requestCode , int resultCode ,Intent data){
//       if the result is capturing Image is Right
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE){
            if (resultCode == RESULT_OK){
            /*call Display Image methods*/
                preViewCaptureImage();
            }else if (resultCode == RESULT_CANCELED){
//                User Force to press Back Button ,, Image Doesnot Clicked
                Toast.makeText(MainActivity.this,"User Cancelled Image Capture",Toast.LENGTH_LONG)
                        .show();
            }
            else {
                /*SomeThing BAD with Code*/
                Toast.makeText(MainActivity.this,"Sorry ! Developer Fault",Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    /*methods to display Image Which Has been Clicked By User*/
    private void preViewCaptureImage(){
       try {
        /*Bitmap using for Image*/
           BitmapFactory.Options options = new BitmapFactory.Options();
           options.inSampleSize = 10;
           final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
           imageView.setImageBitmap(bitmap);
       }
       catch (NullPointerException e){
           e.printStackTrace();
       }

    }

    /*
    * Here we store the file url as it would be null after returning from
    *camera app
    * */
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri",fileUri);
    }
    /*here we have to store file uri again*/
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    /*---used for getting location only ----*/
    public void onStart(){
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop(){
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        /*Fetched Longitude and Latitude*/
        mLongitude = String.valueOf(mLastLocation.getLongitude());
        mLatitude = String.valueOf(mLastLocation.getLatitude());
        /*--------Testing -----*/
        Toast.makeText(MainActivity.this,mLongitude  + mLatitude ,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG_TAG","Google Api connection Has been Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LOG_TAG","Google Api Connection Has been Failed");
    }

}
