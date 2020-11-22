package com.example.tesisensori;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String LOG_TAG = "SENSOR_EXAMPLE";
    private static Context ctx;
    private static Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;
        act = this;
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.d(LOG_TAG, "Sul dispositivo sono presenti: "+sensorList.size()+" sensori.");
        int i = 0;
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore per la temperatura ambientale");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore per la gravità");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un fotometro");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore per l'acceleraizone lineare");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un magnetometro");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore per l'orientamento");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore di pressione");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore di prossimità");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore per l'umidità");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore per il vettore di rotazione");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore di temperatura");
            i++;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un giroscopio");
            i++;
            Button gyro = findViewById(R.id.gyro);
            gyro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ActivityCompat.checkSelfPermission(ctx, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(act, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 200);
                    }else{
                        final String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Intercettazioni";
                        File file = new File(fileName);
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        File gpxfile = new File(file, "gyro_samples");
                        Log.d(LOG_TAG, gpxfile.toString());
                        Intent intent = new Intent(ctx, GyroMic.class);
                        intent.putExtra("FILEPATH", gpxfile.toString());
                        startActivity(intent);
                    }
                }
            });
            }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un accelerometro");
            i++;
            Button accel = findViewById(R.id.accelerometro);
            accel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ActivityCompat.checkSelfPermission(ctx, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(act, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 200);
                    }else{
                        final String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Intercettazioni";
                        File file = new File(fileName);
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        File gpxfile = new File(file, "pin_logger");
                        Log.d(LOG_TAG, gpxfile.toString());
                        Intent intent = new Intent(ctx, PINlogger.class);
                        intent.putExtra("FILEPATH", gpxfile.toString());
                        startActivity(intent);
                    }
                }
            });
        }
        Log.d(LOG_TAG, "Ci sono "+i+" categorie di sensori su 13.");
    }
}