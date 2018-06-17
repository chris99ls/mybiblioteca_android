package it.android.j940549.mybiblioteca.Activity_Esito_Ricerche;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Catalogo_libri.Dettaglio_libro;

import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.R;

public class MyAdapter_x_ricerca extends RecyclerView.Adapter<MyAdapter_x_ricerca.MyViewHolder> {

    private ArrayList<Libro_catalogo> booksList;
    private Activity myActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titolo, autore, isbn;
        public ImageView tumbnail;

        public MyViewHolder(View view) {
            super(view);
            isbn= (TextView) view.findViewById(R.id.isbn_libro);
            titolo = (TextView) view.findViewById(R.id.titolo_libro);
            autore = (TextView) view.findViewById(R.id.autore_libro);
            tumbnail=(ImageView)view.findViewById(R.id.copertina_libro);

        }

    }


    public MyAdapter_x_ricerca(ArrayList<Libro_catalogo> booksList, Activity myActivity) {
        this.booksList = booksList;
        this.myActivity=myActivity;
        Log.i("mylibreria","dataset.size--"+getItemCount());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_libro_x_ricerca, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView isbn=view.findViewById(R.id.isbn_libro);
                String isbn_libro=isbn.getText().toString();
                Intent vaiaDettagli=new Intent(view.getContext(), Dettaglio_libro.class);
                vaiaDettagli.putExtra("isbn",isbn_libro);
                Bundle bundle=new Bundle();
                bundle.putString("isbn",isbn_libro);
                    ContextCompat.startActivity(view.getContext(),vaiaDettagli,bundle);
                //finish();

            }
        });
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Libro_catalogo libro=booksList.get(position);
        holder.titolo.setText(libro.getTitolo());
        holder.isbn.setText(libro.getIsbn());

        URL url = null;Bitmap bitmap=null;
        try {
            url = new URL(libro.getTumbnail());

            Glide.with(myActivity)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.tumbnail);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }

}
