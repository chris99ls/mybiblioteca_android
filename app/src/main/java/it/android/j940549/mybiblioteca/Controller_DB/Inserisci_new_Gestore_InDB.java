package it.android.j940549.mybiblioteca.Controller_DB;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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

import it.android.j940549.mybiblioteca.Activity_Gestore.GestoreNav;
import it.android.j940549.mybiblioteca.Model.Utente;

public class Inserisci_new_Gestore_InDB extends AsyncTask<String,String,String> {
    private Activity myActivity;
    private ProgressDialog progressDialog;
    private boolean registrato;

    public Inserisci_new_Gestore_InDB(Activity myActivity){
        this.myActivity=myActivity;
    }
    @Override
    protected void onPreExecute() {
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
        nameValuePairs.add(new BasicNameValuePair("nome",params[0]));
        nameValuePairs.add(new BasicNameValuePair("cognome",params[1]));
        nameValuePairs.add(new BasicNameValuePair("username",params[2]));
        nameValuePairs.add(new BasicNameValuePair("email",params[3]));
        nameValuePairs.add(new BasicNameValuePair("password",params[4]));
        nameValuePairs.add(new BasicNameValuePair("is_staff",params[5]));

        Log.i("inserisciUtente", "dati" + nameValuePairs.toString());
        InputStream is = null;

        //http post
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/registra_gestore.php");
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

            Log.i("inserisciUtente", "risultato " + result);
            //System.out.println(result);

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
        Log.i("inserisciUtente_post", "risultato "+result.toString());
        if(result.toString().contains("successfully")) {
            registrato=true;
        }else{
            registrato=false;
        }

        if (registrato == true) {
            Intent vaiaLogin = new Intent(myActivity, GestoreNav.class);
            //vaiaMenu.putExtra("user", user);
            myActivity.startActivity(vaiaLogin);
            // USER=user;
            myActivity.finish();
        } else {
            Toast.makeText(myActivity, "Ops! registrazione non avvenuta", Toast.LENGTH_SHORT).show();
        }

        progressDialog.dismiss();

    }
}
