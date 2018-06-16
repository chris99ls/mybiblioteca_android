package it.android.j940549.mybiblioteca.Controller_DB;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Gestore.MyAdapter_Gestisci_catalogo;
import it.android.j940549.mybiblioteca.Catalogo_libri.MyAdapter;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.Model.Utente;


public class Carica_Catalogo extends AsyncTask<String, Object, String> {
    String isbn, titolo, autore, genere, fulltext;
    Activity myActivity;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewpiuletti;
    private RecyclerView.Adapter mAdapter;
    private Utente utenteLogin;
    private ProgressDialog progressDialog;
    ArrayList<Libro_catalogo> myDataset = new ArrayList<>();

    public Carica_Catalogo(Activity myActivity, RecyclerView mRecyclerView,RecyclerView mRecyclerViewpiuletti,RecyclerView.Adapter mAdapter,  ArrayList<Libro_catalogo>myDataset, Utente utenteLogin){
        this.myActivity=myActivity;
        this.mRecyclerView=mRecyclerView;
        this.utenteLogin=utenteLogin;

        if(myActivity.getTitle().equals("Catalogo")){

        this.mRecyclerViewpiuletti=mRecyclerViewpiuletti;}
        if(myActivity.getTitle().equals("Gestisci_Catalogo")){

            this.mRecyclerViewpiuletti=mRecyclerView;}

        this.mAdapter=mAdapter;
        this.myDataset=myDataset;

    }

    @Override
    protected void onPreExecute() {
        // Check network connection.
        progressDialog = new ProgressDialog(myActivity);
        progressDialog.setMessage("caricamento dati in corso");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        if (isNetworkConnected() == false) {
            // Cancel request.
            Log.i("Esito_Ricerca", "Not connected to the internet");
            cancel(true);
            return;
        }
    }


        @Override
        protected String doInBackground(String... params) {
            String result = "";

            InputStream inputStream = null;

            //http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet= new HttpGet("http://lisiangelovpn.ddns.net/mybiblioteca/catalogo.php");
                HttpResponse response = httpclient.execute(httpGet);
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
        protected void onPostExecute(String result) {
            // aggiorno la textview con il risultato ottenuto
            Log.i("log_tag", "parsing data on postExec "+result.toString());

            try{

                Log.i("log_tag", "dato da parsare in json "+result);
                JSONArray jArray = new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    Log.i("log_tag", "ciclo parsing data on postExec .."+i);

                    JSONObject json_data = jArray.getJSONObject(i);
                    Libro_catalogo libro_catalogo= new Libro_catalogo();
                    libro_catalogo.setIsbn(json_data.getString("isbn"));
                    libro_catalogo.setTitolo(json_data.getString("title"));
                    libro_catalogo.setAutore(json_data.getString("author"));
                    libro_catalogo.setGenere(json_data.getString("subject"));
                    //String descrizione = json_data.getString("description");
                    libro_catalogo.setTumbnail(json_data.getString("tumbnail"));

                    // Log.i("log_tag", "datobject inserito " + obj.getData() );

                    myDataset.add(libro_catalogo);

                }
                Log.i("log_tag", "results... " + myDataset.size());
                if(myActivity.getTitle().equals("Catalogo")) {
                    mAdapter = new MyAdapter(myDataset, myActivity,utenteLogin);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerViewpiuletti.setAdapter(mAdapter);
                }
                if(myActivity.getTitle().equals("Gestisci_Catalogo")) {
                    mAdapter = new MyAdapter_Gestisci_catalogo(myDataset,myActivity);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
                myDataset.clear();
                if(myActivity.getTitle().equals("Catalogo")) {
                    mAdapter = new MyAdapter(myDataset, myActivity,utenteLogin);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerViewpiuletti.setAdapter(mAdapter);
                }
                if(myActivity.getTitle().equals("Gestisci_Catalogo")) {
                    mAdapter = new MyAdapter_Gestisci_catalogo(myDataset,myActivity);
                    mRecyclerView.setAdapter(mAdapter);
                }

            }
        progressDialog.dismiss();
        }

    protected boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = null;
        // Instantiate mConnectivityManager if necessary
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) myActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        // Is device connected to the Internet?
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    }


