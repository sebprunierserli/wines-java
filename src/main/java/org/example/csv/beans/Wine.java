package org.example.csv.beans;

import com.google.gson.Gson;
import com.opencsv.bean.CsvBindByName;

public class Wine {

    @CsvBindByName(column = "Nom")
    private String nom;

    @CsvBindByName(column = "Couleur")
    private String couleur;

    @CsvBindByName(column = "Région")
    private String region;

    @CsvBindByName(column = "Appellation")
    private String appellation;

    @CsvBindByName(column = "Millésime")
    private String millesime;

    @CsvBindByName(column = "Pays")
    private String pays;

    public String toJson() {
        return new Gson().toJson(this);
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    public String getMillesime() {
        return millesime;
    }

    public void setMillesime(String millesime) {
        this.millesime = millesime;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }
}


