package it.android.j940549.mybiblioteca.Activity_Gestore;


import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class MyAdapter_Gestisci_catalogo extends RecyclerView.Adapter<MyAdapter_Gestisci_catalogo.MyViewHolder> {
    private Activity myActivity;

    private ArrayList<Libro_catalogo> booksList;

    public class MyViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener{
        public TextView titolo,  isbn;
        public ImageView tumbnail;

        public MyViewHolder(View view) {
            super(view);
            isbn= (TextView) view.findViewById(R.id.isbn_libro);
            titolo = (TextView) view.findViewById(R.id.titolo_libro);
            tumbnail = (ImageView) view.findViewById(R.id.copertina_libro);
        }

      /*  @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "This is my Toast message!",
                    Toast.LENGTH_LONG).show();
        }*/
    }


    public MyAdapter_Gestisci_catalogo(ArrayList<Libro_catalogo> booksList, Activity activity) {
        this.booksList = booksList;
        this.myActivity=activity;
        Log.i("mylibreria","dataset.size--"+getItemCount());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_libro_x_catalogo, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView isbn=view.findViewById(R.id.isbn_libro);
                String isbn_libro=isbn.getText().toString();
                Intent vaiaModificaDato=new Intent(view.getContext(), Modifica_Dato.class);
                vaiaModificaDato.putExtra("isbn",isbn_libro);
                myActivity.startActivity(vaiaModificaDato);
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
            //bitmap= BitmapFactory.decodeStream(url.openStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        holder.tumbnail.setImageBitmap(bitmap);


    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }
    public void vaiaDettagli(String isbn){

    }
}
