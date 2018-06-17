package com.example.user.flashlightsensor;

import android.app.Activity;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;

// 7296948102

public class MainActivity1 extends Service implements SensorEventListener
{
    static boolean flag = true,isFlashOn=true,hasFlash;
    Camera camera;
    Camera.Parameters parameters;
    SensorManager sensorManager;
    Sensor sensor;
    SharedPreferences sharedPreferences;
    private static  final int SHAKE_THRESHOLD=500;
    private static final float SHAKE_GRAVITY=1.2f;
    private static  final int SHAKE_COUNT_RESET_TIME=1500;
    private long mTimeStamp;
    private int mShakeCount;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Accelerometer Sensor
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sharedPreferences = getSharedPreferences("light",MODE_PRIVATE);
        // Register sensor Listener
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);


        // First check if device is supporting flashlight or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity1.this);
            builder.setTitle("Error");
            builder.setMessage("Sorry, your device doesn't support flash light!");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    stopService(new Intent(MainActivity1.this,MainActivity1.class));
                }
            });
            return;
        }

        // get the camera
        getCamera();
    }

    // Get the camera
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                parameters = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. ", e.getMessage());
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==sensor.TYPE_ACCELEROMETER)
            getAcc(sensorEvent);
    }



    private void getAcc(SensorEvent sensorEvent) {
        float[] value = sensorEvent.values;
        float x = value[0];
        float y = value[1];
        float z = value[2];
        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        long time = System.currentTimeMillis();
        long gForce = (long) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        float x1 = sensorEvent.values[0];
        float y1 = sensorEvent.values[1];
        float z1 = sensorEvent.values[2];
        if (sharedPreferences.getInt("light", 0) == 1)
        {
            if (gForce > SHAKE_GRAVITY) {
                long curTime = System.currentTimeMillis();
                if (mTimeStamp + SHAKE_THRESHOLD > curTime) {
                    return;
                }
                if (mTimeStamp + SHAKE_COUNT_RESET_TIME < curTime) {
                    mShakeCount = 0;
                }
                mTimeStamp = curTime;
                mShakeCount++;
//                Log.d("Shake:::: ","Count::"+mShakeCount);

                if (flag&&mShakeCount == 2) {
                    mShakeCount = 0;
                    flag=false;
                    flashlightOn();
                }
                else if (!flag&&mShakeCount == 2) {
                    mShakeCount = 0;
                    flag=true;
                    flashlightOff();
                }
            }
        }
    }

    // Turning On flash
    private void flashlightOn() {

        if (isFlashOn) {

            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
            isFlashOn = false;

        }

    }


    // Turning Off flash
    private void flashlightOff() {
        if (!isFlashOn) {

            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.stopPreview();
            isFlashOn = true;
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}












































/*
public class MainActivity extends Activity implements SensorEventListener {

    SensorManager smgr;
    Sensor msensor;

    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Camera.Parameters parameters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //code for sensor

        smgr = (SensorManager)getSystemService(SENSOR_SERVICE);
        msensor = smgr.getDefaultSensor(Sensor.TYPE_LIGHT);







        // First check if device is supporting flashlight or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }

        // get the camera
        getCamera();


    }


    // Get the camera
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                parameters = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera Error. ", e.getMessage());
            }
        }
    }


    // Turning On flash
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || parameters == null) {
                return;
            }


            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
            isFlashOn = true;


        }

    }


    // Turning Off flash
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || parameters == null) {
                return;
            }


            parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(parameters);
            camera.stopPreview();
            isFlashOn = false;


        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        smgr.unregisterListener(this);


    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume turn on the flash
        smgr.registerListener(this, msensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera parameters
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

        if (event.values[0]<5.0="" &&  p="">)		{


                // turn on flash
                turnOnFlash();
		}
		else
        {
            turnOffFlash();
        }
    }

}
*/
