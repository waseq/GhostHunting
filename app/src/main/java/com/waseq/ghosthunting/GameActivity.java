package com.waseq.ghosthunting;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends Activity implements SensorEventListener {

    private Camera cameraObject;
    private ShowCamera showCamera;
    private GhostsView ghostsView;
    private RadarView radarView;
    private ImageView pic;
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;
    private Sensor sensorRotation;

    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;

    private float[] valuesAccelerometer_lowPass;
    private float[] valuesMagneticField_lowPass;

    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;

    private final float[] mRotationMatrix = new float[16];


    private Engine engine;
    private double lastroll = 0;
    private double rollfix = 0;
    double rollc = 1;
    private double rolc2 = 1;

    /**
     * Check if this device has a camera
     */
    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static Camera isCameraAvailable() {
        Camera object = null;
        try {

            object = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (object != null) {
            //object.setDisplayOrientation(0);
        } else
            System.out.println("Camera is null");


        return object;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        setContentView(R.layout.activity_game);
        //pic = (ImageView)findViewById(R.id.imageView1);
        if (checkCameraHardware(this)) {
            System.out.println("We have a camera ");
        } else {
            System.out.println("We dont have a camera ");
        }

        cameraObject = isCameraAvailable();
        showCamera = new ShowCamera(this, cameraObject);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(showCamera);


        ghostsView = new GhostsView(this, showCamera.getVerticalCameraAngle(), showCamera.getHorizontalCameraAngle());
        FrameLayout ghosts = (FrameLayout) findViewById(R.id.ghost_view);
        ghosts.addView(ghostsView);

        radarView = new RadarView(this);
        FrameLayout radar = (FrameLayout) findViewById(R.id.radar_view);
        radar.addView(radarView);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorRotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        for (Sensor s : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            System.out.println(s.getName() + " " + " " + s.getResolution());
        }
        //sensorRotation = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR );
        //System.out.println(sensorRotation.getVendor()+" "+sensorRotation.getVersion());
        PackageManager manager = getPackageManager();
        System.out.println(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER));
        System.out.println(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS));
        System.out.println(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE));
        System.out.println(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT));
        System.out.println(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY));

        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];
        valuesAccelerometer_lowPass = new float[3];
        valuesMagneticField_lowPass = new float[3];

        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];

        engine = new Engine();


        ghostsView.setEngine(engine);

        radarView.setEngine(engine);

        engine.start();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }


    @Override
    protected void onResume() {
//        if(sensorRotation==null) {
        sensorManager.registerListener(this,
                sensorAccelerometer,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this,
                sensorMagneticField,
                SensorManager.SENSOR_DELAY_GAME);
//        }else{
//        sensorManager.registerListener(this,
//                sensorRotation,
//                SensorManager.SENSOR_DELAY_GAME);}
//        if(cameraObject!=null)
//            try {
//                cameraObject.reconnect();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        super.onResume();
    }

    @Override
    protected void onPause() {
//        if(sensorRotation==null) {
        sensorManager.unregisterListener(this,
                sensorAccelerometer);
        sensorManager.unregisterListener(this,
                sensorMagneticField);
//    }else{
//        sensorManager.unregisterListener(this,
//                sensorRotation);}
//        if(cameraObject!=null)
//            cameraObject.unlock();
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    /*
 * time smoothing constant for low-pass filter
 * 0 ≤ alpha ≤ 1 ; a smaller value basically means more smoothing
 * See: http://en.wikipedia.org/wiki/Low-pass_filter#Discrete-time_realization
 */
    static final float ALPHA = 0.065f;

    /**
     * low pass filter from http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter
     */
    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        boolean success = false;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                for (int i = 0; i < 3; i++) {
                    valuesAccelerometer[i] = event.values[i];
                    valuesAccelerometer_lowPass = lowPass(valuesAccelerometer, valuesAccelerometer_lowPass);
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                for (int i = 0; i < 3; i++) {
                    //valuesMagneticField[i] = event.values[i];
                    valuesMagneticField_lowPass = lowPass(event.values.clone(), valuesMagneticField_lowPass);

                }
                break;
            case Sensor.TYPE_ROTATION_VECTOR:
                SensorManager.getRotationMatrixFromVector(
                        mRotationMatrix, event.values);

                break;
        }


//        if(sensorRotation==null){ // pouzijeme fallback k


        success = SensorManager.getRotationMatrix(
                matrixR,

                matrixI,
                valuesAccelerometer_lowPass,
                valuesMagneticField_lowPass);

        if (success) {
            float[] rotM = new float[9];
//               // TODO if rotation
//
//               // pro zarizeni na stole!!!
            SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotM); // otoceno o 90 stupnu
//
//               // pro augm reality

            if (lastroll >= 35 || lastroll <= -35) {


                SensorManager.remapCoordinateSystem(rotM.clone(), SensorManager.AXIS_X, SensorManager.AXIS_Z, rotM); // augmented reality
                //SensorManager.remapCoordinateSystem(rotM.clone(), SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotM); // fix roll

                rollfix = 90;
                rollc = -1;
                rolc2 = -1;
            } else {
                rollfix = 0;
                rollc = 1;
                rolc2 = 1;
            }
//               }else{
//                   SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Y, rotM);
//                   rollfix = 0;
//
//               }

            //if(device.get)
//              SensorManager.remapCoordinateSystem(rotM.clone(), SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotM);
//
//               rotM = matrixR;
            SensorManager.getOrientation(rotM, matrixValues);
            //SensorManager.getOrientation(matrixR,matrixValues);
        } else {
            return;
        }
//        }else {
//            // pouzijeme rot vect
//            SensorManager.getOrientation(mRotationMatrix, matrixValues);
//        }


        double azimuth = Math.toDegrees(matrixValues[0]);
        double pitch = Math.toDegrees(matrixValues[2]); // TODO fixme
        double roll = rolc2 * (rollc * (Math.toDegrees(matrixValues[1])) + rollfix);
        lastroll = roll;
        //remapCoordinateSystem(inR, AXIS_X, AXIS_Z, outR);

//            System.out.println("Azimuth: " + String.valueOf(azimuth));
//            System.out.println("Pitch: " + String.valueOf(pitch));
//            System.out.println("Roll: " + String.valueOf(roll));


        //azimuth = azimutAvger.getAvg();
        //roll = rollAvger.getAvg();

        ghostsView.rotationChange(azimuth, pitch, roll);
        TextView info = (TextView) findViewById(R.id.textView);
        //info.setText("Azimuth "+azimuth+"\nPitch "+pitch+"\nRoll "+roll);

        //TextView gameInfo = (TextView) findViewById(R.id.skore);
        info.setText("A " + azimuth + "P " + pitch + " R " + roll + "\nScore " + engine.getScore() + " level " + engine.getLevel() + " killed " + engine.getGhostsKilled());

        radarView.rotationChange(azimuth, pitch, roll);

        engine.rotationChange(azimuth, pitch, roll);


    }

    public void fire(View view) {
        engine.fire();


    }

}