// Good reference - http://web.stanford.edu/class/cs193a/

package com.blastbeat.flashlight;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;

// Need to implement SurfaceHolder.Callback for Galaxy Nexus Support
public class FlashlightActivity extends Activity implements SurfaceHolder.Callback {

    static Camera myCamera = null;
    Camera.Parameters myCameraParameters;
    ImageButton myImageButton;
    boolean isLightOn = false;

    // Needed for Galaxy Nexus (et al) support
    public static SurfaceView preview;
    public static SurfaceHolder mHolder;

    private boolean isCameraPresent;
    private boolean isCameraFlashPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);

        // Use PackageManager to determine whether camera/camera flash is present
        isCameraPresent = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA);

        isCameraFlashPresent = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);


        if (!isCameraPresent) {
            //Use Alert to tell user camera isn't present
            finish();
        }

        if (!isCameraFlashPresent) {
            //Use Alert to tell user camera flash isn't present
            Log.d("TAG", "NO FLASH PRESENT");
            finish();
        }

        //findViewbyId -- get a pointer to something. Cast the pointer to ImageButton
        myImageButton = (ImageButton) findViewById(R.id.buttonSwitch);

        // Needed for Galaxy Nexus support
        preview = (SurfaceView) findViewById(R.id.PREVIEW);
        mHolder = preview.getHolder();
        mHolder.addCallback(this);

        // Open camera
        myCamera = Camera.open();

        // Needed for Galaxy Nexus support
        try {
            myCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // button.setOnClickListener - runs "toggleFlashlight" method when button is clicked.
        // This is a Java "anonymous inner class"
        // Anonymous Inner Classes have access to outer final variables
        myImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // toggleButtonImage();
                toggleFlashlight(isLightOn);
            }
        });
    }

    //Causing surfaceCreated and surfaceDestroyed null pointer problems
    // releasing and making null the problem probably?
    @Override
    protected void onPause() {
        super.onPause();
        //Release camera in SurfaceDestroyed instead of here to avoid
        // null pointer problems
        Log.d("TAG", "onPause hit");

        // Need this here to catch when lock screen is activated
        // because surfaceDestroyed isn't called then
        turnOffLight();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Re-attach camera is SurfaceCreated to avoid problems
        // HOWEVER
        //TODO have to add surfaceCreated code here as the surfaceCreated is bypassed when screen locking
        Log.d("TAG", "onResume Hit");
    }

    // Added this along with the line "android:configChanges="orientation|screenSize""
    // in AndroidManifest.xml to stop Activity being destroyed and recreated on orientation change
    // Avoids onPause/surfaceDestroyed onResume/surfaceCreated being called and turning off light on change
    // of orientation
    // Might not be the best thing to do!!!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("TAG", "onConfigurationChanged Hit");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.flashlight, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleFlashlight(boolean isLightOn) {
        if (isLightOn) {
            turnOffLight();
        } else {
            turnOnLight();
        }
    }

    private void turnOnLight() {
        toggleButtonImage();

        myCameraParameters = myCamera.getParameters();
        myCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        myCamera.setParameters(myCameraParameters);
        myCamera.startPreview();

        isLightOn = true;
    }

    private void turnOffLight() {
        toggleButtonImage();

        myCameraParameters = myCamera.getParameters();
        myCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        myCamera.setParameters(myCameraParameters);
        myCamera.stopPreview();

        isLightOn = false;
    }

    private void toggleButtonImage () {
        if (isLightOn) {
            myImageButton.setImageResource(R.drawable.off1);
        }
        else {
            myImageButton.setImageResource(R.drawable.on2);
        }
    }
    //Below is for Galaxy Nexus support - needs SurfaceView
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("TAG", "surfaceCreated hit");

        if (myCamera == null) {
            myCamera = Camera.open();
            isLightOn = false;
        }

       // Create the surfaceHolder and start updating the preview surface
       // The camera needs a surface to start the preview
        mHolder = holder;
        try {
            myCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("TAG", "SurfaceDestroyed hit");

        // Stop updating the preview surface
        myCamera.stopPreview();

        //Remove the surfaceHolder
        mHolder = null;

        // Need to release camera from SurfaceView
        // Would do it in onPause but this gets called afterwards
        // so it would result in a NullPointerException if this wasn't here
        if (myCamera != null) {
            myCamera.release();
            myCamera = null;
        }
    }
    
}