package com.example.tesisensori;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class PINlogger extends AppCompatActivity {

    final private String TAG = "PINlogger";
    private PrintWriter m_printWriter;
    private SensorManager m_sensorMgr;
    private Sensor m_accelerometer;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinlogger);

        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        try {
            Intent intent = getIntent();
            Bundle extra = intent.getExtras();
            String filepath = extra.getString("FILEPATH");
            Log.d(TAG, "Output file: " + filepath);
            m_printWriter = new PrintWriter(filepath);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found exception");
        }

        m_sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        m_accelerometer = m_sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // register a shutdown intent handler
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };

        registerReceiver(receiver, new IntentFilter("seclab.GyroMic.intent.action.SHUTDOWN"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_sensorMgr.registerListener(onSensorChange, m_accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_sensorMgr.unregisterListener(onSensorChange);
        m_printWriter.flush();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver);
        m_sensorMgr.unregisterListener(onSensorChange);
    }

    private SensorEventListener onSensorChange = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        synchronized public void onSensorChanged(SensorEvent event) {
            long time = event.timestamp;
            float[] acceleration=event.values;  //x = 0, y = 1, z = 2
            float ax=acceleration[0];
            float ay=acceleration[1];
            float az=acceleration[2];
            String report = "Mtime "+time +" MX "+ax+" MY "+ay+" MZ "+az;
            m_printWriter.println(report);
        }
    };

}