package com.example.rhaleff.enviarreclamacao;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by Rhaleff on 27/04/2016.
 */
public class Autenticador extends Authenticator {
    private String user;
    private String password;

    public Autenticador(String user){
        this.user = user;
        this.password = Config.PASSWORD;
    }

    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(user, password);
    }
}
