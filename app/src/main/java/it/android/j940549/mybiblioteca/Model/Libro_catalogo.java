package it.android.j940549.mybiblioteca.Model;

public class Libro_catalogo {
    private String isbn;
    private String titolo;
    private String autore;
    private String genere;
    private String tumbnail;

    public Libro_catalogo(){

    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getTumbnail() {
        return tumbnail;
    }

    public void setTumbnail(String tumbnail) {
        this.tumbnail = tumbnail;
    }
}
