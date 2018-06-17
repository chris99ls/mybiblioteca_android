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

import it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti.MyRecyclerViewAdapter_gia_letti;
import it.android.j940549.mybiblioteca.Model.Libri_gia_letti;
import it.android.j940549.mybiblioteca.Model.Utente;

public class Carica_gia_letti extends AsyncTask<String,String,String> {

    Activity myActivity;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progressDialog;
    ArrayList<Libri_gia_letti> mDataset;


    public Carica_gia_letti(Activity myActivity, RecyclerView mRecyclerView, RecyclerView.Adapter mAdapter ){
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
        String stringaFinale = " ";
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("nrtessera",params[0]));


        InputStream is = null;

        //http post
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/giavisti_utente.php");
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
        Log.i("log_tag", "parsing data on postExec giavisti"+result.toString());

        try{

            Log.i("log_tag", "dato da parsare in json "+result);
            JSONArray jArray = new JSONArray(result);
            for(int i=0;i<jArray.length();i++){
                Log.i("log_tag", "ciclo parsing data on postExec .."+i);

                JSONObject json_data = jArray.getJSONObject(i);
                Libri_gia_letti libro_gia_letti = new Libri_gia_letti();
                libro_gia_letti.setPatch_img(json_data.getString("tumbnail"));
                libro_gia_letti.setIsbn(json_data.getString("isbn"));
                libro_gia_letti.setTitolo(json_data.getString("title"));


                mDataset.add(libro_gia_letti);


            }
            Log.i("log_tag", "results... gialet" + mDataset.size());

            mAdapter = new MyRecyclerViewAdapter_gia_letti(mDataset,myActivity);
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

