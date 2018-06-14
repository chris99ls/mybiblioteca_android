package it.android.j940549.mybiblioteca.Model;

public class Libri_In_Prestito {

    private String isbn;
    private String titolo;
    private String patch_img;
    private String data_prestito;
    public Libri_In_Prestito(){

    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getPatch_img() {
        return patch_img;
    }

    public void setPatch_img(String patch_img) {
        this.patch_img = patch_img;
    }

    public String getData_prestito() {
        return data_prestito;
    }

    public void setData_prestito(String data_prestito) {
        this.data_prestito = data_prestito;
    }
}
