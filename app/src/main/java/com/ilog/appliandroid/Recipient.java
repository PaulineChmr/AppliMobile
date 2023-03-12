package com.ilog.appliandroid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recipient {

    public static ArrayList<Recipient> recipientArrayList = new ArrayList<>();
    public static String RECIPIENT_EDIT_EXTRA = "recipientEdit";
    private int id;
    private String prenom;
    private String nom;
    private String numero;
    private Date deleted;

    public Recipient(int id, String prenom, String nom, String numero, Date deleted) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.numero = numero;
        this.deleted = deleted;
    }

    public Recipient(int id, String prenom, String nom, String numero) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.numero = numero;
        deleted = null;
    }

    public static Recipient getRecipientForID(int passedRecipientID) {
        for (Recipient recipient : recipientArrayList){
            if(recipient.getId() == passedRecipientID)
                return recipient;
        }
        return null;
    }

    public static ArrayList<Recipient> nonDeleteRecipients(){
        ArrayList<Recipient> nonDeleted = new ArrayList<>();
        for(Recipient recipient : recipientArrayList){
            if(recipient.getDeleted() == null){
                nonDeleted.add(recipient);
            }
        }
        return nonDeleted;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }
}
