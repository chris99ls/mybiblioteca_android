package it.android.j940549.mybiblioteca.Controller_DB;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Esito_Ricerche.MyAdapter_x_ricerca;
import it.android.j940549.mybiblioteca.Activity_Gestore.MyAdapter_Gestisci_catalogo;
import it.android.j940549.mybiblioteca.Activity_Gestore.MyAdapter_Gestisci_utenti;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.Model.Utente;


public class Cerca_Utente_in_DB extends AsyncTask<String, Object, String> {
    String query;
    Activity myActivity;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressDialog progressDialog;
    ArrayList<Utente> myDataset;

    public Cerca_Utente_in_DB(Activity myActivity, RecyclerView mRecyclerView, RecyclerView.Adapter mAdapter){
        this.myActivity=myActivity;
        this.mRecyclerView=mRecyclerView;
        this.mAdapter=mAdapter;
        myDataset= new ArrayList<>();

    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(myActivity);
        progressDialog.setMessage("caricamento dati in corso");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        // Check network connection.
        if (isNetworkConnected() == false) {
            // Cancel request.
            Log.i("Esito_Ricerca", "Not connected to the internet");
            cancel(true);
            return;
        }
    }

    @Override
    protected String doInBackground(String ...param) {
        this.query = param[0];
        // Stop if cancelled
        if (isCancelled()) {
            return null;
        }
        String Url = "http://lisiangelovpn.ddns.net/mybiblioteca/cerca_utenti.php?";
        Url = Url + "query=" + query;

        //String url_ricera= creaUrl_ricerca(parametri[0],parametri[1],parametri[2],parametri[3],parametri[4]);

        Log.i("Esito_Ricerca ::url ", Url);
        try {
            HttpURLConnection connection = null;
            // Build Connection.
            try {
                URL url = new URL(Url);
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
            if (responseCode != 200) {
                Log.i("Esito_Ricerca", "ricerca request failed. Response Code: " + responseCode);
                connection.disconnect();
                return null;
            }
            Log.i("Esito_Ricerca", "ricerca request succesfully. Response Code: " + responseCode);
            // Read data from response.
            StringBuilder builder = new StringBuilder();
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = responseReader.readLine();
            while (line != null) {
                builder.append(line);
                line = responseReader.readLine();
            }
            String responseString = builder.toString();
            Log.i("Esito_Ricerca", "Response String: " + responseString);
            //JSONObject responseJson = new JSONObject(responseString);
            // Close connection and return response code.
            connection.disconnect();

            return responseString;

        } catch (SocketTimeoutException e) {
            Log.w("Esito_Ricerca", "Connection timed out. Returning null");
            return null;
        } catch (IOException e) {
            Log.d("Esito_Ricerca", "IOException when connecting to Google Books API.");
            e.printStackTrace();
            return null;
        }
        //return myDataset;
    }
    @Override
    protected void onPostExecute(String responseString) {
        myDataset.clear();
        Log.i("log_tag", "parsing data on postExec " + responseString.toString());

        try {

            //  Log.i("log_tag", "dato da parsare in json "+responseJson.toString());
            JSONArray jArray = new JSONArray(responseString);
            for (int i = 0; i < jArray.length(); i++) {
                Log.i("log_tag", "ciclo parsing data on postExec .." + i);

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

                myDataset.add(utente);

            }
            Log.i("log_tag", "results... " + myDataset.size());

            mAdapter = new MyAdapter_Gestisci_utenti(myDataset,myActivity);
            mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment

        }

        catch(JSONException e){
            Log.e("log_tag", "Error parsing data "+e.toString());
            myDataset.clear();
            mAdapter = new MyAdapter_Gestisci_utenti(myDataset,myActivity);
            mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment
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
