package it.android.j940549.mybiblioteca.Model;

import android.os.Parcelable;

import java.io.Serializable;

public class Utente implements Serializable{
    private String nome;
    private String cognome;
    private String nrtessera;
    private String email;
    private String password;
    private int is_superuser;
    private int is_staff;
    public Utente(){

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNrtessera() {
        return nrtessera;
    }

    public void setNrtessera(String nrtessera) {
        this.nrtessera = nrtessera;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIs_superuser(int is_superuser) {
        return this.is_superuser;
    }

    public void setIs_superuser(int is_superuser) {
        this.is_superuser = is_superuser;
    }

    public int getIs_staff(int is_staff) {
        return this.is_staff;
    }

    public void setIs_staff(int is_staff) {
        this.is_staff = is_staff;
    }
}
