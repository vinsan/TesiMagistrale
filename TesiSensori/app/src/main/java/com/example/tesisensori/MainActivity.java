package com.example.tesisensori;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final long refreshTime = 1000000000L;
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
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore per l'acceleraizone lineare");
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
            gyro.setOnClickListener(new SensorClickListener(ctx, act, "gyro_samples", Sensor.TYPE_GYROSCOPE));
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un accelerometro");
            i++;
            Button accel = findViewById(R.id.accelerometro);
            accel.setOnClickListener(new SensorClickListener(ctx, act, "accel_logger", Sensor.TYPE_ACCELEROMETER));
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore di prossimità");
            i++;
            Button proximity = findViewById(R.id.prox);
            proximity.setOnClickListener(new SensorClickListener(ctx, act, "proximity_log", Sensor.TYPE_PROXIMITY));
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un magnetometro");
            i++;
            Button magnet = findViewById(R.id.magnet);
            magnet.setOnClickListener(new SensorClickListener(ctx, act, "magnet_log", Sensor.TYPE_MAGNETIC_FIELD));
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore di pressione (Barometro)");
            i++;
            Button barometer = findViewById(R.id.pressure);
            barometer.setOnClickListener(new SensorClickListener(ctx, act, "pressure_log", Sensor.TYPE_PRESSURE));
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un fotometro");
            i++;
            Button light = findViewById(R.id.light);
            light.setOnClickListener(new SensorClickListener(ctx, act, "light_log", Sensor.TYPE_LIGHT));
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION) != null) {
            Log.d(LOG_TAG, "Sul dispositivo è presente un sensore per l'orientamento");
            i++;
            Button orientation = findViewById(R.id.orientation);
            orientation.setOnClickListener(new SensorClickListener(ctx, act, "orientation_log", Sensor.TYPE_ORIENTATION));
        }
        Log.d(LOG_TAG, "Ci sono "+i+" categorie di sensori su 13.");
    }
}