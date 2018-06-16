package it.android.j940549.mybiblioteca.Controller_DB;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Gestore.GestoreNav;
import it.android.j940549.mybiblioteca.Activity_Utente.UtenteNav;
import it.android.j940549.mybiblioteca.Crypto.Crypto_new;
import it.android.j940549.mybiblioteca.Model.Utente;


public class Cerca_pw_utente_in_DB extends AsyncTask<String, Object, String> {
    String username, pwcontrollo, pwUtenteCrypt, PWlogin,is_staff, is_superuser;
    Context context;
    ArrayList<Utente> listUtenti=new ArrayList<>();
    Utente utenteLogin;
    public static final String TAG = "KeyStore";
    private ProgressDialog progressDialog;
    //EditText passwordLogin;

    public Cerca_pw_utente_in_DB(Context context){

        this.context=context;

    }

    @Override
    protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
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

        this.username = "m_"+param[0];
        this.pwcontrollo=param[1];
        // Stop if cancelled
        if (isCancelled()) {
            return null;
        }
        String Url = "http://lisiangelovpn.ddns.net/mybiblioteca/check_login.php?username="+username;

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
       // listUtenti.clear();
        Log.i("log_tag", "parsing data on postExec " + responseString.toString());

        try {

            //  Log.i("log_tag", "dato da parsare in json "+responseJson.toString());
            JSONArray jArray = new JSONArray(responseString);
            for (int i = 0; i < jArray.length(); i++) {
                Log.i("log_tag", "ciclo parsing data on postExec .." + i);

                JSONObject json_data = jArray.getJSONObject(i);
                Utente utente= new Utente();
                utente.setNrtessera(json_data.getString("id"));
                utente.setNome(json_data.getString("first_name"));
                utente.setCognome(json_data.getString("last_name"));
                String user_name= json_data.getString("username");
                if(user_name.contains("m_")){
                    user_name=user_name.substring(2,user_name.length());}
                utente.setUsername(user_name);

                utente.setEmail(json_data.getString("email"));
                utente.setIs_staff(json_data.getInt("is_staff"));
                utente.setIs_superuser(json_data.getInt("is_superuser"));
                utente.setPassword(json_data.getString("password"));

                // Log.i("log_tag", "datobject inserito " + obj.getData() );

                listUtenti.add(utente);
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());

        }
            pwUtenteCrypt =listUtenti.get(0).getPassword();
            utenteLogin=listUtenti.get(0);
            Log.i("log_tag", "results... " + listUtenti.size());
            Log.i("log_tag", "passworddbCrypta......"+ pwUtenteCrypt);
            Log.i("log_tag", "password......"+pwcontrollo);
            Log.i("log_tag", "nr tessera ......"+utenteLogin.getNrtessera());
        Log.i("log_tag", "user......"+utenteLogin.getUsername());

            Crypto_new crypto=new Crypto_new(context);

        boolean verified = false;
            try {
                if (pwUtenteCrypt != null) {
                    verified = crypto.verifyData(pwcontrollo, pwUtenteCrypt);
                }
            } catch (KeyStoreException e) {
                Log.w(TAG, "KeyStore not Initialized", e);
            } catch (CertificateException e) {
                Log.w(TAG, "Error occurred while loading certificates", e);
            } catch (NoSuchAlgorithmException e) {
                Log.w(TAG, "RSA not supported", e);
            } catch (IOException e) {
                Log.w(TAG, "IO Exception", e);
            } catch (UnrecoverableEntryException e) {
                Log.w(TAG, "KeyPair not recovered", e);
            } catch (InvalidKeyException e) {
                Log.w(TAG, "Invalid Key", e);
            } catch (SignatureException e) {
                Log.w(TAG, "Invalid Signature", e);
            }

           if (verified) {

               if (utenteLogin.getIs_staff() == 1) {
                   Intent vaiaGestoreNav = new Intent(context, GestoreNav.class);
                   vaiaGestoreNav.putExtra("gestore", (Serializable) utenteLogin);
                   context.startActivity(vaiaGestoreNav);

               }
               if (utenteLogin.getIs_staff() == 0) {


                   Intent vaiaUtenteNav = new Intent(context, UtenteNav.class);
                   vaiaUtenteNav.putExtra("utente", (Serializable) utenteLogin);
                   context.startActivity(vaiaUtenteNav);
                   //  myActivity.finish();
               }
               progressDialog.dismiss();
           }else {
            Toast.makeText(context, "utente o password errati", Toast.LENGTH_SHORT).show();
               progressDialog.dismiss();
            }



    }

    protected boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = null;
        // Instantiate mConnectivityManager if necessary
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        // Is device connected to the Internet?
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public Utente getUtenteLogin() {
        return utenteLogin;
    }

}
