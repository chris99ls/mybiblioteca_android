package it.android.j940549.mybiblioteca.Controller_DB;

import android.app.Activity;
import android.app.ProgressDialog;
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

import it.android.j940549.mybiblioteca.Activity_Gestore.MyAdapter_Gestisci_utenti;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.Model.Utente;

public class Carica_Lista_Utenti extends AsyncTask<String,String,String> {
    Activity myActivity;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progressDialog;
    ArrayList<Utente> mDataset;


    public Carica_Lista_Utenti(Activity myActivity, RecyclerView mRecyclerView, RecyclerView.Adapter mAdapter){
        this.myActivity=myActivity;
        this.mRecyclerView=mRecyclerView;
        this.mAdapter=mAdapter;
        mDataset= new ArrayList<>();
    }
    @Override
    protected void onPreExecute() {
        // Check network connection.
        progressDialog = new ProgressDialog(myActivity);
        progressDialog.setMessage("caricamento dati in corso");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";

        InputStream inputStream = null;

        //http post
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet= new HttpGet("http://lisiangelovpn.ddns.net/mybiblioteca/carica_utenti.php");
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
    protected void onProgressUpdate(String... values) {

    }

    @Override
    protected void onPostExecute(String result) {
        mDataset.clear();
        // aggiorno la textview con il risultato ottenuto
        Log.i("log_tag", "parsing data on postExec "+result.toString());

        try{

            Log.i("log_tag", "dato da parsare in json "+result);
            JSONArray jArray = new JSONArray(result);
            for(int i=0;i<jArray.length();i++){
                Log.i("log_tag", "ciclo parsing data on postExec .."+i);

                JSONObject json_data = jArray.getJSONObject(i);
                Utente utente= new Utente();
                utente.setNome(json_data.getString("first_name"));
                utente.setCognome(json_data.getString("last_name"));
                utente.setEmail(json_data.getString("email"));
                utente.setIs_superuser(json_data.getInt("is_superuser"));
                utente.setIs_staff(json_data.getInt("is_staff"));
                utente.setNrtessera(json_data.getString("id"));

                String user_name= json_data.getString("username");
                if(user_name.contains("m_")){
                    user_name=user_name.substring(2,user_name.length());}
                utente.setUsername(user_name);

                // Log.i("log_tag", "datobject inserito " + obj.getData() );

                mDataset.add(utente);

            }
            Log.i("log_tag", "results... " + mDataset.size());

            mAdapter = new MyAdapter_Gestisci_utenti(mDataset,myActivity);
            mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment

        }

        catch(JSONException e){
            Log.e("log_tag", "Error parsing data "+e.toString());
            mDataset.clear();
            mAdapter = new MyAdapter_Gestisci_utenti(mDataset,myActivity);
            mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment
        }
        progressDialog.dismiss();

    }
}
