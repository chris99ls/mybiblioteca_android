package it.android.j940549.mybiblioteca.Controller_DB;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Gestore.Dettaglio_Utente;
import it.android.j940549.mybiblioteca.Activity_Gestore.GestoreNav;
import it.android.j940549.mybiblioteca.Model.Libri_gia_letti;
import it.android.j940549.mybiblioteca.Model.Utente;

public class Restituisci_Libro extends AsyncTask<String,String,String> {

    Activity myActivity;
    Utente utenteLogin;
    private ProgressDialog progressDialog;


    public Restituisci_Libro (Activity myActivity, Utente utenteLogin){
        this.myActivity=myActivity;
        this.utenteLogin=utenteLogin;

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
        String stringaFinale = " ";
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("nrtessera",params[0]));
        nameValuePairs.add(new BasicNameValuePair("isbn", params[1]));


        InputStream is = null;

        //http post
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/restituisci_book.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        }catch(Exception e){
            Log.e("TEST", "Errore nella connessione http "+e.toString());
        }
        if(is != null) {
            //converto la risposta in stringa
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

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
        // aggiorno la textview con il risultato ottenuto
        Log.i("log_tag", "result presta..."+result.toString());

        if(result.contains("successfully")){
            Intent refresh = new Intent(myActivity, Dettaglio_Utente.class);
            refresh.putExtra("utente",utenteLogin);
            myActivity.startActivity(refresh);
            myActivity.finish();

        }
        progressDialog.dismiss();

    }

}
