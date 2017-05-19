package com.example.pau.talaya;

/**
 * Created by Pau on 9/5/17.
 */

public class UsuariActiu {
    private int IdUsuari;
    private String Nom, Cognom;
    private int Token;

    public String getNom() {
        return Nom;
    }

    public String getCognom() {
        return Cognom;
    }

    public int getIdUsuari() {
        return IdUsuari;
    }

    public UsuariActiu(int IdUsuari, String Nom, String Cognom){

        this.IdUsuari = IdUsuari;
        this.Nom = Nom;
        this.Cognom = Cognom;

    }
}
