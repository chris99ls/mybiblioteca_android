package it.android.j940549.mybiblioteca.Controller_DB;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Esito_Ricerche.MyAdapter_x_ricerca;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;


public class Cerca_titolo_inGoogleBooks extends AsyncTask<String,Object,JSONObject> {


    Activity myActivity;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progressDialog;
    ArrayList<Libro_catalogo> myDataset = new ArrayList<>();

    public Cerca_titolo_inGoogleBooks (Activity myActivity, RecyclerView mRecyclerView,RecyclerView.Adapter mAdapter){
        this.myActivity=myActivity;
        this.mRecyclerView=mRecyclerView;
        this.mAdapter=mAdapter;

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
    protected JSONObject doInBackground(String... params) {
        // Stop if cancelled
        if(isCancelled()){
            return null;
        }

        String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q={title:" + params[0]+"}";
        Log.i("inserisciBook",apiUrlString);
        try{
            HttpURLConnection connection = null;
            // Build Connection.
            try{
                URL url = new URL(apiUrlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000); // 5 seconds
                connection.setConnectTimeout(5000); // 5 seconds
            } catch (MalformedURLException e) {
                // Impossible: The only two URLs used in the app are taken from string resources.
                e.printStackTrace();
            } catch (ProtocolException e) {
                // Impossible: "GET" is a perfectly valid request method.
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int responseCode = connection.getResponseCode();
            if(responseCode != 200){
                Log.i("inserisciBook", "GoogleBooksAPI request failed. Response Code: " + responseCode);
                connection.disconnect();
                return null;
            }
            Log.i("inserisciBook", "GoogleBooksAPI request succesfully. Response Code: " + responseCode);
            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null){
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            Log.i("inserisciBook", "Response String: " + responseString);
            JSONObject responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();
            return responseJson;
        } catch (SocketTimeoutException e) {
            Log.w("inserisciBook", "Connection timed out. Returning null");
            return null;
        } catch(IOException e){
            Log.d("inserisciBook", "IOException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            Log.d("inserisciBook", "JSONException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject responseJson) {
        // aggiorno la textview con il risultato ottenuto
        //Log.i("log_tag", "parsing data on postExec "+result.toString());

        try{

            Log.i("log_tag", "dato da parsare in json "+responseJson);
           // JSONArray jArray = new JSONArray(responseJson);
            JSONArray jArray = responseJson.getJSONArray("items");

            for(int i=0;i<jArray.length();i++){
                Log.i("log_tag", "ciclo parsing data on postExec .."+i);

                JSONObject json_data = jArray.getJSONObject(i);

                Libro_catalogo libro_catalogo= new Libro_catalogo();

                JSONObject volumeInfo=json_data.getJSONObject("volumeInfo");
                JSONArray isbnInfos=volumeInfo.getJSONArray("industryIdentifiers");
                if(isbnInfos.toString().contains("ISBN_13")) {

                    for (int y = 0; y < isbnInfos.length(); y++) {
                        JSONObject isbn_data = isbnInfos.getJSONObject(y);
                        if (isbn_data.toString().contains("ISBN_13")) {
                            libro_catalogo.setIsbn(isbn_data.getString("identifier"));
                        }

                    }
                }else {

                    for (int y = 0; y < isbnInfos.length(); y++) {
                        JSONObject isbn_data = isbnInfos.getJSONObject(y);
                        libro_catalogo.setIsbn(isbn_data.getString("identifier"));

                    }
                }

                libro_catalogo.setTitolo(volumeInfo.getString("title"));
                String autore="";
                if(volumeInfo.toString().contains("authors")) {
                    JSONArray autori = volumeInfo.getJSONArray("authors");
                    //   autore = autori.toString();
                    if (autori.length() == 1) {
                        autore = autori.get(0).toString();
                    } else {
                        for (int in = 0; in <= autori.length() - 1; in++) {
                            autore = autore + " " + autori.get(in).toString();
                        }
                    }
                }
                libro_catalogo.setAutore(autore);

                String genere="";
                if(volumeInfo.toString().contains("categories")) {

                    JSONArray generi = volumeInfo.getJSONArray("categories");
                    //   autore = autori.toString();
                    if (generi.length() == 1) {
                        genere = generi.get(0).toString();
                    } else {
                        for (int inx = 0; inx <= generi.length() - 1; inx++) {
                            genere = genere + " " + generi.get(inx).toString();
                        }

                    }


                }
                libro_catalogo.setGenere(genere);
                if(volumeInfo.toString().contains("imageLinks")) {
                    libro_catalogo.setTumbnail(volumeInfo.getJSONObject("imageLinks").getString("smallThumbnail"));
                }

                myDataset.add(libro_catalogo);

            }
            Log.i("log_tag", "results... " + myDataset.size());
                mAdapter = new MyAdapter_x_ricerca(myDataset,myActivity);
                mRecyclerView.setAdapter(mAdapter);
        }

        catch(JSONException e){
            Log.e("log_tag", "Error parsing data "+e.toString());
            myDataset.clear();
                mAdapter = new MyAdapter_x_ricerca(myDataset,myActivity);
                mRecyclerView.setAdapter(mAdapter);
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


