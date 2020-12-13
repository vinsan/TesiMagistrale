package com.example.tesisensori;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import java.io.File;
import androidx.core.app.ActivityCompat;

public class SensorClickListener implements View.OnClickListener {
    Context ctx;
    Activity act;
    String filename;
    int sensor;
    private static String LOG_TAG = "SENSOR_EXAMPLE";

    public SensorClickListener(Context ctx, Activity act, String filename, int sensor){
        this.ctx = ctx;
        this.act = act;
        this.filename = filename;
        this.sensor = sensor;
    }

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
            File gpxfile = new File(file, filename);
            Log.d(LOG_TAG, gpxfile.toString());
            Intent intent = new Intent(ctx, SensorActivity.class);
            intent.putExtra("FILEPATH", gpxfile.toString());
            intent.putExtra("SENSOR", sensor);
            act.startActivity(intent);
        }
    }
}
