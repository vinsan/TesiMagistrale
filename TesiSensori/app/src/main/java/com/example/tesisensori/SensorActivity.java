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
import android.widget.TextView;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class SensorActivity extends AppCompatActivity {

    final private String TAG = "SensorActivity";
    private PrintWriter m_printWriter;
    private SensorManager m_sensorMgr;
    private Sensor sensor;
    private BroadcastReceiver receiver;
    private TextView tx;
    int sensorID;
    private long finalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        tx = findViewById(R.id.sensorView);
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        finalTime = 0;

        Intent intent = getIntent();
        Bundle extra = intent.getExtras();
        try {
            String filepath = extra.getString("FILEPATH");
            Log.d(TAG, "Output file: " + filepath);
            m_printWriter = new PrintWriter(filepath);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found exception");
        }

        m_sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorID = extra.getInt("SENSOR");
        sensor = m_sensorMgr.getDefaultSensor(sensorID);

        // register a shutdown intent handler
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };

        registerReceiver(receiver, new IntentFilter("tesisensori.SensorActivity.intent.action.SHUTDOWN"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_sensorMgr.registerListener(onSensorChange, sensor, SensorManager.SENSOR_DELAY_FASTEST);
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
            if(sensorID==Sensor.TYPE_LIGHT||sensorID==Sensor.TYPE_PROXIMITY){
                String val = event.timestamp + ": " + event.values[0];
                m_printWriter.println(val);
                tx.setText(tx.getText() + "\n" + val);
            }else if(sensorID==Sensor.TYPE_GYROSCOPE){
                String val = event.timestamp + " " + event.values[0] + " " + event.values[1] + " " + event.values[2];
                m_printWriter.println(val);
                if(finalTime==0){
                    finalTime = event.timestamp;
                    tx.setText(val);
                    return;
                }else{
                    if(event.timestamp-finalTime>=MainActivity.refreshTime){
                        finalTime = event.timestamp;
                        tx.setText(tx.getText()+"\n"+val);
                    }
                }
            }else if(sensorID==Sensor.TYPE_MAGNETIC_FIELD||sensorID==Sensor.TYPE_PRESSURE){
                String val = event.timestamp + ": " + event.values[0];
                m_printWriter.println(val);
                if (finalTime == 0) {
                    finalTime = event.timestamp;
                    tx.setText(val);
                    return;
                } else {
                    if (event.timestamp - finalTime >= MainActivity.refreshTime) {
                        finalTime = event.timestamp;
                        tx.setText(tx.getText() + "\n" + val);
                    }
                }
            }else if(sensorID==Sensor.TYPE_ACCELEROMETER){
                long time = event.timestamp;
                float[] acceleration=event.values;  //x = 0, y = 1, z = 2
                float ax=acceleration[0];
                float ay=acceleration[1];
                float az=acceleration[2];
                String report = "Mtime "+time +" MX "+ax+" MY "+ay+" MZ "+az;
                m_printWriter.println(report);
                if(finalTime==0){
                    finalTime = time;
                    tx.setText(report);
                    return;
                }else{
                    if(time-finalTime>=MainActivity.refreshTime){
                        finalTime = time;
                        tx.setText(tx.getText()+"\n"+report);
                    }
                }
            }else if(sensorID==Sensor.TYPE_ORIENTATION){
                String val = event.timestamp + " " + event.values[0] + " " + event.values[1] + " " + event.values[2];
                m_printWriter.println(val);
                tx.setText(val);
            }
        }
    };
}