package corso.java.tesiintercettazionitelefoniche;

import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder {
    private static final String LOG_TAG = "AudioRecordTest";
    private static String path;
    private MediaRecorder recorder = null;
    public boolean imRecording;

    public AudioRecorder(String path){
        this.path = path;
        imRecording = false;
    }

    public void startRecording(String fileName) {
        if(imRecording)
                return;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(path+fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (Exception e) {
            Log.e(LOG_TAG, "prepare() failed");
            Log.e(LOG_TAG, e.getMessage());
            return;
        }

        recorder.start();
        imRecording = true;
    }

    public Boolean stopRecording() {
        if(!imRecording)
            return false;
        recorder.stop();
        recorder.release();
        recorder = null;
        imRecording = false;
        return true;
    }

    public String getLocation() {
        return path;
    }
}
