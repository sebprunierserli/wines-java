package org.example.misc.json.contact;

public class Diffuseur extends JsonBaseObject {

    private String nom;
    private String prenom;
    private String mail;
    private String telephone;

    public Diffuseur() {
    }

    public Diffuseur(String nom, String prenom, String mail, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.telephone = telephone;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
