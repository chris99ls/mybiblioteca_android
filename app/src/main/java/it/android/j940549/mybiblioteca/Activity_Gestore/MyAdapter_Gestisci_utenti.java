package it.android.j940549.mybiblioteca.Activity_Gestore;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        public TextView nrtessera, cognome, nome, email, username;

        public MyViewHolder(View view) {
            super(view);
            nrtessera= (TextView) view.findViewById(R.id.nr_tessera);
            cognome = (TextView) view.findViewById(R.id.cognome_utente);
            nome = (TextView) view.findViewById(R.id.nome_utente);
            email = (TextView) view.findViewById(R.id.email_utente);
            username= (TextView) view.findViewById(R.id.username);
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
                TextView nrtessera  =view.findViewById(R.id.nr_tessera);
                String str_nr_tessera=nrtessera.getText().toString();
                String nr_tessera= str_nr_tessera.substring(str_nr_tessera.indexOf("nr. tessera: ")+13,str_nr_tessera.length());
                Toast.makeText(myActivity, "click"+nr_tessera, Toast.LENGTH_SHORT).show();

                caricaDatiUtente(nr_tessera);

            }
        });

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Utente utente=utentiList.get(position);
        holder.nrtessera.setText("nr. tessera: "+utente.getNrtessera());
        holder.nome.setText("Nome: "+utente.getNome());
        holder.cognome.setText("Cognome: "+utente.getCognome());
        holder.username.setText("username: "+utente.getUsername());
        holder.email.setText("e-mail: "+utente.getEmail());
    }

    @Override
    public int getItemCount() {
        return utentiList.size();
    }

    private void caricaDatiUtente(String nrtessera){

        MyAdapter_Gestisci_utenti.HttpGetTask task=new MyAdapter_Gestisci_utenti.HttpGetTask();
        task.execute(nrtessera);
    }

    private class HttpGetTask extends AsyncTask<String,String,String> {

        Utente utente=new Utente();

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("nrtessera",params[0]));
            Log.i("log_tag", "param  "+params[0]);

            InputStream inputStream = null;

            //http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost= new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/carica_singolo_utente.php");
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
            }catch(Exception e){
                Log.e("TEST", "Errore nella connessione http "+e.toString());
            }
            if(inputStream != null) {
                //converto la risposta in stringa
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    inputStream.close();

                    result = sb.toString();
                    Log.i("log_tag", "result  "+sb.toString());

                } catch (Exception e) {
                    Log.e("TEST", "Errore nel convertire il risultato " + e.toString());
                }

                System.out.println(result);

            }
            else{//is è null e non ho avuto risposta

            }

            return result;

        }

        @Override
        protected void onProgressUpdate(String... values) {

        }

        @Override
        protected void onPostExecute(String result) {
            // aggiorno la textview con il risultato ottenuto
            Log.i("log_tag", "parsing data on postExec "+result.toString());

            try{

                Log.i("log_tag", "dato da parsare in json "+result);
                JSONArray jArray = new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    Log.i("log_tag", "ciclo parsing data on postExec .."+i);

                    JSONObject json_data = jArray.getJSONObject(i);
                    utente.setNome(json_data.getString("first_name"));
                    utente.setCognome(json_data.getString("last_name"));
                    utente.setEmail(json_data.getString("email"));
                    utente.setIs_superuser(json_data.getInt("is_superuser"));
                    utente.setIs_staff(json_data.getInt("is_staff"));
                    utente.setNrtessera(json_data.getString("id"));
                    utente.setUsername(json_data.getString("username"));


                }
                //Log.i("log_tag", "results... " + myDataset.size());

                Intent vaiaDettaglio_Utente=new Intent(myActivity, Dettaglio_Utente.class);
                vaiaDettaglio_Utente.putExtra("utente",utente);

                myActivity.startActivity(vaiaDettaglio_Utente);
            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
                /*myDataset.clear();
                mAdapter = new MyAdapter_Gestisci_catalogo(myDataset,getActivity());
                mRecyclerView.setAdapter(mAdapter);*/
            }

        }
    }


}
