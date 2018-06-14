package it.android.j940549.mybiblioteca.Catalogo_libri;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Gestore.Dettaglio_Utente;
import it.android.j940549.mybiblioteca.Activity_Utente.UtenteNav;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;

public class Dettaglio_libro extends AppCompatActivity {
    ImageView copertina;
    TextView isbn_txt;
    TextView titolo_txt;
    TextView autore_txt, genere_txt;
    TextView descrizione_txt;
    Button btn_prenotalibro;
    Utente utenteLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettaglio_libro);
        final String isbn = getIntent().getExtras().get("isbn").toString();
         utenteLogin= (Utente) getIntent().getExtras().get("utente");
        copertina = findViewById(R.id.copertina_libro);
        isbn_txt = findViewById(R.id.isbn_libro);
        titolo_txt = findViewById(R.id.titolo_libro);
        genere_txt = findViewById(R.id.genere_libro);
        autore_txt = findViewById(R.id.autore_libro);
        descrizione_txt = findViewById(R.id.descrizione_libro);
        btn_prenotalibro=findViewById(R.id.prenota_libro);
        btn_prenotalibro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                Dettaglio_libro.HttpGetTaskprenota prenotato_libro=new Dettaglio_libro.HttpGetTaskprenota();
                prenotato_libro.execute(utenteLogin.getNrtessera(),isbn);
            }
        });

        setTitle("Dettaglio Libro");
        caricadato(isbn);
    }



    private void caricadato(String isbn){
        Dettaglio_libro.HttpGetTask task = new Dettaglio_libro.HttpGetTask();

        task.execute(isbn);
    }


    private class HttpGetTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            InputStream inputStream = null;
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("isbn",params[0]));

            //http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost= new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/dettaglio_libro.php");
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

                    isbn_txt.setText(json_data.getString("isbn"));
                    titolo_txt.setText(json_data.getString("title"));
                    autore_txt.setText(json_data.getString("author"));
                    genere_txt.setText(json_data.getString("subject"));
                    descrizione_txt.setText(json_data.getString("description"));
                    URL url = null;Bitmap bitmap=null;
                    try {
                        url = new URL(json_data.getString("tumbnail"));

                        Glide.with(getBaseContext())
                                .load(url)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(copertina);
                        //bitmap= BitmapFactory.decodeStream(url.openStream());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Log.i("log_tag", "datobject inserito " + obj.getData() );



                }



            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());

            }

        }
    }

    private class HttpGetTaskprenota extends AsyncTask<String,String,String> {

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
                HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/prenota_book.php");
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

            if(result.contains("successfully")){
                Intent refresh = new Intent(getBaseContext(), UtenteNav.class);
                refresh.putExtra("utente", utenteLogin);
                startActivity(refresh);
                finish();
                //              mAdapter = new MyRecyclerViewAdapter_gia_letti(getDataSet(),getActivity());
//                mRecyclerView.setAdapter(mAdapter);
            }

        }

    }
}

