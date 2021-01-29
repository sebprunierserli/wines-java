package org.example.misc.json.contact;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Contact extends JsonBaseObject {

    private String civilite;
    private String nom;
    private String prenom;
    private String mail;
    private String telephone;
    private String fonction;

    private Diffuseur diffuseur;
    private Employe agent;
    private Employe chefDeVente;

    public Contact() {
    }

    public Contact(String civilite, String nom, String prenom, String mail, String telephone, String fonction, Diffuseur diffuseur, Employe agent, Employe chefDeVente) {
        this.civilite = civilite;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.telephone = telephone;
        this.fonction = fonction;
        this.diffuseur = diffuseur;
        this.agent = agent;
        this.chefDeVente = chefDeVente;
    }

    @Override
    protected void customizeJsonObject(JsonObject jsonObject) {
        super.customizeJsonObject(jsonObject);
        jsonObject.addProperty("completude", getCompletude());
    }

    private String getCompletude() {
        if (nom != null && prenom != null && fonction != null && mail != null && telephone != null) {
            return "Complet";
        }
        if (nom != null && prenom != null && fonction == null && mail == null && telephone == null) {
            return "Partiel";
        }
        return "Incomplet";
    }

    public String getCivilite() {
        return civilite;
    }

    public void setCivilite(String civilite) {
        this.civilite = civilite;
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

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public Diffuseur getDiffuseur() {
        return diffuseur;
    }

    public void setDiffuseur(Diffuseur diffuseur) {
        this.diffuseur = diffuseur;
    }

    public Employe getAgent() {
        return agent;
    }

    public void setAgent(Employe agent) {
        this.agent = agent;
    }

    public Employe getChefDeVente() {
        return chefDeVente;
    }

    public void setChefDeVente(Employe chefDeVente) {
        this.chefDeVente = chefDeVente;
    }

    public static void main(String[] args) {
        Employe chef = new Employe("Mme", "Dubois", "Véronique", "vero@mail.com", "0000000000");
        Employe agent = new Employe("M", "Durand", "Charles", "charles@mail.com", "0000000000");
        Diffuseur diffuseur = new Diffuseur("Martin", "Cécile", "cecile@mail.com", "0000000000");

        Contact contact = new Contact(
                "M",
                "Dupont",
                "Jacques",
                "jacques@mail.com",
                "0000000000",
                "Gérant",
                diffuseur,
                agent,
                chef
        );

        System.out.println(contact.toJson());
    }

}
