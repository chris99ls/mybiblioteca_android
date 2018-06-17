package it.android.j940549.mybiblioteca.Controller_DB;

import android.app.Activity;
import android.app.ProgressDialog;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti.MyRecyclerViewAdapter_Prenotati;
import it.android.j940549.mybiblioteca.Model.Libri_In_Prestito;
import it.android.j940549.mybiblioteca.Model.Libri_Prenotati;
import it.android.j940549.mybiblioteca.Model.Utente;

public class Carica_prenotati extends AsyncTask<String,String,String> {
    Activity myActivity;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progressDialog;
    ArrayList<Libri_Prenotati> mDataset ;
    private Utente utenteLogin;

    public Carica_prenotati(Activity myActivity, RecyclerView mRecyclerView, RecyclerView.Adapter mAdapter, Utente utenteLogin){
        this.myActivity=myActivity;
        this.mRecyclerView=mRecyclerView;
        this.mAdapter=mAdapter;
        this.utenteLogin=utenteLogin;
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
        String stringaFinale = " ";
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("nrtessera",params[0]));


        InputStream is = null;

        //http post
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/prenotati_utente.php");
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
        Log.i("log_tag", "parsing data on postExec prenotati"+result.toString());

        try{

            Log.i("log_tag", "dato da parsare in json "+result);
            JSONArray jArray = new JSONArray(result);
            for(int i=0;i<jArray.length();i++){
                Log.i("log_tag", "ciclo parsing data on postExec .."+i);

                JSONObject json_data = jArray.getJSONObject(i);

                Libri_Prenotati libro_prenotato = new Libri_Prenotati();
                libro_prenotato.setPatch_img(json_data.getString("tumbnail"));
                libro_prenotato.setIsbn(json_data.getString("isbn"));
                libro_prenotato.setTitolo(json_data.getString("title"));


                mDataset.add(libro_prenotato);
            }


            Log.i("log_tag", "results... prenot" + mDataset.size());

            mAdapter = new MyRecyclerViewAdapter_Prenotati(mDataset,myActivity,utenteLogin);
            mRecyclerView.setAdapter(mAdapter);
            progressDialog.dismiss();

        }

        catch(JSONException e){
            Log.e("log_tag", "Error parsing data "+e.toString());
            mDataset.clear();
            //mAdapter = new MyRecyclerViewAdapter_gia_letti(mDataset);
            //mRecyclerView.setAdapter(mAdapter);
            progressDialog.dismiss();

        }

    }
}
