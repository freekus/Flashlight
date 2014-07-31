// Good reference - http://web.stanford.edu/class/cs193a/

package com.blastbeat.flashlight;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
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

    // Needed for Galaxy Nexus support
    public static SurfaceView preview;
    public static SurfaceHolder mHolder;

    // Use PackageManager to determine whether camera/camera flash is present
    //  final boolean isCameraPresent = myPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    //final boolean isCameraFlashPresent = myPackageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    // above is erroring out

    boolean isCameraPresent = true;
    boolean isCameraFlashPresent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);


        if (!isCameraPresent) {
            //Use Alert to tell user camera isn't present
            finish();
        }

        if (!isCameraFlashPresent) {
            //Use Alert to tell user camera flash isn't present
            finish();
        }

        //findViewbyId -- get a pointer to something. Cast the pointer to ImageButton
        myImageButton = (ImageButton) findViewById(R.id.buttonSwitch);

        // Needed for Galaxy Nexus support
        preview = (SurfaceView) findViewById(R.id.PREVIEW);
        mHolder = preview.getHolder();
        mHolder.addCallback(this);

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
                toggleFlashlight(isLightOn);
            }
        });
    }

    // Added test comment
    // Added a second test comment
    /* @Override - TODO: FIND OUT WHY NOT WORKING!!!
    protected void OnPause() {
        super.onPause();

        //Use this to turn off flashlight when paused
        turnOffLight();
    } */

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
        myCameraParameters = myCamera.getParameters();
        myCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        myCamera.setParameters(myCameraParameters);
        myCamera.startPreview();
        isLightOn = true;
    }

    private void turnOffLight() {
        myCameraParameters = myCamera.getParameters();
        myCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        myCamera.setParameters(myCameraParameters);
        myCamera.stopPreview();
        isLightOn = false;
    }

    //Below is for Galaxy Nexus support - needs SurfaceView
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {}

    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        try {
            myCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        myCamera.stopPreview();
        mHolder = null;
    }

}
