package corso.java.tesiintercettazionitelefoniche;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class CallRecorder {
    private static final String LOG_TAG = "AudioRecordTest";
    private static String path;
    private static String lastRecordedFile = "/test01";
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    public boolean imRecording;

    public CallRecorder(String path){
        this.path = path;
        imRecording = false;
    }

    public CallRecorder(){
        this.path = Environment.getExternalStorageDirectory().getAbsolutePath();
        imRecording = false;
    }

    public void onRecord(boolean start) {
        if (start) {
            startRecording("/test01");
        } else {
            stopRecording();
        }
    }

    public void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    public void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(path+lastRecordedFile);
            player.prepare();
            player.start();
            Log.d("AudioRecordTest", "Riproduzione registrazione: "+path+lastRecordedFile);
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    public void startRecording(String fileName) {
        if(imRecording)
                return;
        recorder = new MediaRecorder();
        //recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(path+fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            Log.e(LOG_TAG, e.getMessage());
        }

        recorder.start();
        imRecording = true;
        lastRecordedFile = fileName;
        Log.d("AudioRecordTest", "Registrazione avviata: "+path+fileName);
    }

    public void stopRecording() {
        if(!imRecording)
            return;
        recorder.stop();
        recorder.release();
        recorder = null;
        imRecording = false;
        Log.d("AudioRecordTest", "Registrazione interrotta");
    }

    public String getLocation() {
        return path;
    }
}
