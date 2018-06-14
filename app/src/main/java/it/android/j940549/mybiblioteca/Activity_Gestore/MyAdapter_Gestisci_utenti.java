package it.android.j940549.mybiblioteca.Activity_Gestore;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Utente.Prestiti;
import it.android.j940549.mybiblioteca.Activity_Utente.UtenteNav;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;

public class MyAdapter_Gestisci_utenti extends RecyclerView.Adapter<MyAdapter_Gestisci_utenti.MyViewHolder> {
    private Activity myActivity;
    private ArrayList<Utente> utentiList;

    public class MyViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener{
        public TextView nrtessera, cognome, nome, email;

        public MyViewHolder(View view) {
            super(view);
            nrtessera= (TextView) view.findViewById(R.id.nr_tessera);
            cognome = (TextView) view.findViewById(R.id.cognome_utente);
            nome = (TextView) view.findViewById(R.id.nome_utente);
            email = (TextView) view.findViewById(R.id.email_utente);
        }

      /*  @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "This is my Toast message!",
                    Toast.LENGTH_LONG).show();
        }*/
    }


    public MyAdapter_Gestisci_utenti(ArrayList<Utente> utentiList, Activity activity) {
        this.utentiList = utentiList;
        this.myActivity=activity;
        Log.i("mylibreria","dataset.size--"+getItemCount());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_utente, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView nrtessera=view.findViewById(R.id.nr_tessera);
                String str_nr_tessera=nrtessera.getText().toString();
                Intent vaiaDettaglio_Utente=new Intent(view.getContext(), Dettaglio_Utente.class);
                vaiaDettaglio_Utente.putExtra("nr_tessera",str_nr_tessera);

                myActivity.startActivity(vaiaDettaglio_Utente);


            }
        });
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Utente utente=utentiList.get(position);
        holder.nrtessera.setText(utente.getNrtessera());
        holder.nome.setText(utente.getNome());
        holder.cognome.setText(utente.getCognome());
        holder.email.setText(utente.getEmail());
    }

    @Override
    public int getItemCount() {
        return utentiList.size();
    }
    public void vaiaDettagli(String isbn){

    }
}
