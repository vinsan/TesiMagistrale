package corso.java.tesiintercettazionitelefoniche;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;

import androidx.core.content.FileProvider;

public class MailSender {
    Context context;
    private String address;
    private String location;
    private String fileName;

    public MailSender(Context context, String address){
        this.context = context;
        this.address = address;
    }

    public void setFileName(String location, String fileName){
        this.location = location;
        this.fileName = fileName;
    }

    public void sendMail(String myNumber, String savedPhoneNumber){
        File filelocation = new File(location, fileName);
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // set the type to 'email'
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {address};
        emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        Uri path = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", filelocation);
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
        // the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Invio intercettazione telefonica "+fileName);
        //body
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "In riferimento alla tesi magistrale sostenuta da Santoro Vincenzo con il Porf. De prisco, si invia ai fini di studio l'intercettazione telefonica tra "+myNumber+" e "+savedPhoneNumber);
        try{
            context.startActivity(emailIntent);
        }catch(ActivityNotFoundException e){
            Log.e("AudioRecordTest", e.getMessage());
        }
    }
}
