/* Copyright 2016 Michael Sladoje and Mike Schälchli. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package ch.zhaw.facerecognition.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.util.Date;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView;
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognition.R;

import java.text.SimpleDateFormat;

import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;


public class AddPersonPreviewActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, SensorEventListener {
    public static final int TIME = 0;
    public static final int MANUALLY = 1;
    private CustomCameraView mAddPersonView;
    // The timerDiff defines after how many milliseconds a picture is taken
    private long timerDiff;
    private long lastTime;
    private PreProcessorFactory ppF;
    private FileHelper fh;
    private String folder;
    private String subfolder;
    private String name;
    private int total;
    private int numberOfPictures;
    private int method;
    private ImageButton btn_Capture;
    private boolean capturePressed;
    private boolean front_camera;
    private boolean night_portrait;
    private int exposure_compensation;

    private TextView xTextAccelerometer, yTextAccelerometer, zTextAccelerometer, xTextGyroscope, yTextGyroscope, zTextGyroscope, textLight, textProximity;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private SensorManager sensorManager;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person_preview);

        Intent intent = getIntent();
        folder = intent.getStringExtra("Folder");
        if(folder.equals("Test")){
            subfolder = intent.getStringExtra("Subfolder");
        }
        name = intent.getStringExtra("Name");
        method = intent.getIntExtra("Method", 0);
        capturePressed = false;
        if(method == MANUALLY){
            btn_Capture = (ImageButton)findViewById(R.id.btn_Capture);
            btn_Capture.setVisibility(View.VISIBLE);
            btn_Capture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capturePressed = true;
                }
            });
        }

        fh = new FileHelper();
        total = 0;
        lastTime = new Date().getTime();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        timerDiff = Integer.valueOf(sharedPrefs.getString("key_timerDiff", "500"));

        mAddPersonView = (CustomCameraView) findViewById(R.id.AddPersonPreview);
        // Use camera which is selected in settings
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        front_camera = sharedPref.getBoolean("key_front_camera", true);

        numberOfPictures = Integer.valueOf(sharedPref.getString("key_numberOfPictures", "100"));

        night_portrait = sharedPref.getBoolean("key_night_portrait", false);
        exposure_compensation = Integer.valueOf(sharedPref.getString("key_exposure_compensation", "50"));

        if (front_camera){
            mAddPersonView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        } else {
            mAddPersonView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        }
        mAddPersonView.setVisibility(SurfaceView.VISIBLE);
        mAddPersonView.setCvCameraViewListener(this);

        int maxCameraViewWidth = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_width", "640"));
        int maxCameraViewHeight = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_height", "480"));
        mAddPersonView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);


        // Create our Sensor Manager
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Accelerometer Sensor
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Gyroscope Sensor
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // Light Sensor
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //Proximity Sensor
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Register sensor Listener
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        // Assign TextView
        xTextAccelerometer = (TextView)findViewById(R.id.xTextAccelerometer);
        yTextAccelerometer = (TextView)findViewById(R.id.yTextAccelerometer);
        zTextAccelerometer = (TextView)findViewById(R.id.zTextAccelerometer);

        xTextGyroscope = (TextView)findViewById(R.id.xTextGyroscope);
        yTextGyroscope = (TextView)findViewById(R.id.yTextGyroscope);
        zTextGyroscope = (TextView)findViewById(R.id.zTextGyroscope);

        textLight = (TextView)findViewById(R.id.textLight);

        textProximity = (TextView)findViewById(R.id.textProximity);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        if (night_portrait) {
            mAddPersonView.setNightPortrait();
        }

        if (exposure_compensation != 50 && 0 <= exposure_compensation && exposure_compensation <= 100)
            mAddPersonView.setExposure(exposure_compensation);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat imgRgba = inputFrame.rgba();
        Mat imgCopy = new Mat();
        imgRgba.copyTo(imgCopy);
        // Selfie / Mirror mode
        if(front_camera){
            Core.flip(imgRgba,imgRgba,1);
        }

        long time = new Date().getTime();
        if((method == MANUALLY) || (method == TIME) && (lastTime + timerDiff < time)){
            lastTime = time;

            // Check that only 1 face is found. Skip if any or more than 1 are found.
            List<Mat> images = ppF.getCroppedImage(imgCopy);
            if (images != null && images.size() == 1){
                Mat img = images.get(0);
                if(img != null){
                    Rect[] faces = ppF.getFacesForRecognition();
                    //Only proceed if 1 face has been detected, ignore if 0 or more than 1 face have been detected
                    if((faces != null) && (faces.length == 1)){
                        faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
                        if(((method == MANUALLY) && capturePressed) || (method == TIME)){
                            String date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                            MatName m = new MatName(date + "_" + total, img);
                            if (folder.equals("Test")) {
                                String wholeFolderPath = fh.TEST_PATH + name + "/" + subfolder;
                                new File(wholeFolderPath).mkdirs();
                                fh.saveMatToImage(m, wholeFolderPath + "/");
                            } else {
                                String wholeFolderPath = fh.TRAINING_PATH + name;
                                new File(wholeFolderPath).mkdirs();
                                fh.saveMatToImage(m, wholeFolderPath + "/");
                            }

                            for(int i = 0; i<faces.length; i++){
                                MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[i], String.valueOf(total), front_camera);
                            }

                            total++;

                            // Stop after numberOfPictures (settings option)
                            if(total >= numberOfPictures){
                                Intent intent = new Intent(getApplicationContext(), AddPersonActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            capturePressed = false;
                        } else {
                            for(int i = 0; i<faces.length; i++){
                                MatOperation.drawRectangleOnPreview(imgRgba, faces[i], front_camera);
                            }
                        }
                    }
                }
            }
        }

        return imgRgba;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ppF = new PreProcessorFactory(this);
        mAddPersonView.enableView();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mAddPersonView != null)
            mAddPersonView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mAddPersonView != null)
            mAddPersonView.disableView();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }


    @Override
    public void onSensorChanged(SensorEvent event){
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xTextAccelerometer.setText("X_accelerometer: " + event.values[0]);
            yTextAccelerometer.setText("Y: " + event.values[1]);
            zTextAccelerometer.setText("Z: " + event.values[2]);
        }


        else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            xTextGyroscope.setText("X_gyroscope : " + (int)x + " rad/s");
            yTextGyroscope.setText("Y : " + (int)y + " rad/s");
            zTextGyroscope.setText("Z : " + (int)z + " rad/s");

        }

        else if(event.sensor.getType() == Sensor.TYPE_LIGHT) {

            textLight.setText("Light: " + event.values[0]);
        }

        else if(event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

            textProximity.setText("Proximity: " + event.values[0]);
        }
    }


}
