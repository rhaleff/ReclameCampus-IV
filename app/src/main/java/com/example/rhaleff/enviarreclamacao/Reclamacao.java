package com.example.rhaleff.enviarreclamacao;

/**
 * Created by Rhaleff on 27/04/2016.
 */
public class Reclamacao {

    private String reclamacao;
    private String email;

    public Reclamacao(String reclamacao, String email){
        this.reclamacao = reclamacao;
        this.email = email;
    }

    public String toString(){
        String text;
        text = "Olá!"+"\n\n"+reclamacao+"\n\n"+"Att, "+email;
        return text;
    }
}
