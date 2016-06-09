package com.example.rhaleff.enviarreclamacao;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

//Class is extending AsyncTask because this class is going to perform a networking operation
public class EnviarEmail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;

    private String assunto;
    private Reclamacao r;
    private String pth;

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;

    //Class Constructor
    public EnviarEmail(Context context, String assunto, Reclamacao r, String pth){
        //Initializing variables
        this.context = context;
        this.assunto = assunto;
        this.r = r;
        this.pth = pth;
    }


    @Override
    protected Void doInBackground(Void... params) {

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", "465");

        Autenticador autenticador = new Autenticador("reclameampusiv");
        session = Session.getDefaultInstance(props,autenticador);

        MimeMessage mm = new MimeMessage(session);
        MimeBodyPart anexo = new MimeBodyPart();
        FileDataSource source = new FileDataSource(pth);
        Multipart mp = new MimeMultipart();

        try {
            mm.setFrom(new InternetAddress(Config.EMAIL));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(Config.EMAIL));
            mm.setSubject(assunto);
            mm.setText(r.toString());

            if(!pth.equals(" ")){
                anexo.setDataHandler(new DataHandler(source));
                anexo.setFileName(source.getName());
                mp.addBodyPart(anexo);
                mm.setContent(mp);
            }



        } catch (MessagingException e) {
            e.printStackTrace();
        }

        Transport transport;
        try {
            transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com",465,"reclameampusiv",Config.PASSWORD);
            transport.sendMessage(mm, mm.getAllRecipients());
            transport.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Showing progress dialog while sending email
        progressDialog = ProgressDialog.show(context,"Enviando Reclamação","Aguarde um momento...",false,false);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismissing the progress dialog
        progressDialog.dismiss();
        //Showing a success message
        try{
            Intent intent = new Intent(context,Main2Activity.class);
            context.startActivity(intent);
            ((Activity)context).finish();


        }catch (Exception e){
            e.getMessage();
        }

    }


}