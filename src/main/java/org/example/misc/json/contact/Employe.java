package org.example.misc.json.contact;

public class Employe extends JsonBaseObject {

    private String civilite;
    private String nom;
    private String prenom;
    private String mail;
    private String telephone;
    private String sexe;

    public Employe() {
    }

    public Employe(String civilite, String nom, String prenom, String mail, String telephone) {
        this.civilite = civilite;
        this.nom = nom;
        this.prenom = prenom;
        this.mail = mail;
        this.telephone = telephone;

        switch (civilite.toUpperCase()) {
            case "M":
                sexe = "H";
                break;
            case "MME":
                sexe = "F";
                break;
            default:
                sexe = "I";
                break;
        }
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

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }
}
