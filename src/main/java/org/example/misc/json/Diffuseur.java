package org.example.misc.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Diffuseur {

    private String nom;
    private String prenom;
    private String fonction;
    private String mail;
    private String telephone;

    /*
     * Sérialisation JSON avec la librairie Gson de Google.
     * Exemple de sérialisation custom avec un attribut calculé pour la "complétude" du profil du diffuseur.
     */
    public String toJson() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonElement jsonElement = gson.toJsonTree(this);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.addProperty("completude", getCompletude());
        return gson.toJson(jsonObject);
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

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
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

    public static void main(String[] args) {
        Diffuseur diffuseur = new Diffuseur();
        diffuseur.setNom("PRUNIER");
        diffuseur.setPrenom("Sébastien");
        diffuseur.setFonction("Développeur");
        //diffuseur.setMail("sebastien@me.com");
        //diffuseur.setTelephone("0000000000");

        System.out.println(diffuseur.toJson());
    }

}
