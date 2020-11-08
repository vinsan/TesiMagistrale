package corso.java.tesiintercettazionitelefoniche;

import android.util.Log;
import java.security.Security;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMailSender extends javax.mail.Authenticator {
    private static final String LOG_TAG = "AudioRecordTest";
    private String mailhost = "smtp.gmail.com";
    private String port = "465";
    private String user;
    private String password;
    private Session session;
    private Multipart _multipart;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public GMailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.put("mail.smtp.host", mailhost);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
        session.setDebug(true);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients, String filename) throws Exception {
        if(!sender.equals(user))
            throw new Exception("Il mittente è diverso dall'utente: "+sender+" != "+user);

        try{
            MimeMessage message = new MimeMessage(session);
            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);
            if(filename!=null){
                addAttachment(filename, body);
                message.setContent(_multipart);
            }
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            Transport.send(message);
        }catch (AuthenticationFailedException e){
            Log.e(LOG_TAG, "Autenticazione fallita! Utente: "+user+" Password: "+password+". Ricordati di abilitare l'Accesso app meno sicure!");            Log.e(LOG_TAG, e.getClass().toString());
        }catch(Exception e){
            Log.e(LOG_TAG, e.getClass().toString());
            if(e.getMessage()!=null)
                Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void addAttachment(String filename, String body) {
        try{
        _multipart = new MimeMultipart();

        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        _multipart.addBodyPart(messageBodyPart);

        BodyPart messageBodyPart2 = new MimeBodyPart();
        messageBodyPart2.setText(body);

        _multipart.addBodyPart(messageBodyPart2);
        }catch(Exception e){
            Log.e(LOG_TAG, e.getClass().toString());
            if(e.getMessage()!=null)
                Log.e(LOG_TAG, e.getMessage());
        }
    }
}