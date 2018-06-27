package it.android.j940549.mybiblioteca.Activity_Esito_Ricerche;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import java.util.ArrayList;
import it.android.j940549.mybiblioteca.Controller_DB.Cerca_libro_in_DB;
import it.android.j940549.mybiblioteca.Controller_DB.Cerca_titolo_inGoogleBooks;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.R;

public class Esito_Ricerca extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Libro_catalogo> myDataset= new ArrayList<Libro_catalogo>();
    private String isbn,titolo,autore,genere,fulltext, tipoRicerca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isbn=getIntent().getExtras().get("isbn").toString();
        titolo=getIntent().getExtras().get("titolo").toString();
        autore=getIntent().getExtras().get("autore").toString();
        genere=getIntent().getExtras().get("genere").toString();
        fulltext=getIntent().getExtras().get("fulltext").toString();
        tipoRicerca=getIntent().getExtras().get("ricerca").toString();

        setContentView(R.layout.activity_esito__ricerca);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_esito_ricerca);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //crea dataset

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter_x_ricerca(myDataset,this);
        mRecyclerView.setAdapter(mAdapter);

        if(tipoRicerca.equals("google")){
            setTitle("Esito da GoogleBooks");
        }
        eseguiRicerca(tipoRicerca);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void eseguiRicerca(String tipoRicerca){

        if(!tipoRicerca.equals("google")) {

            Cerca_libro_in_DB cerca_in_db = new Cerca_libro_in_DB(this, mRecyclerView, mAdapter);
            cerca_in_db.execute(isbn, titolo, autore, genere, fulltext);
        }else{
            Cerca_titolo_inGoogleBooks cerca_on_googleBooks = new Cerca_titolo_inGoogleBooks(this, mRecyclerView, mAdapter);
            cerca_on_googleBooks.execute(titolo);
        }
    }


}




