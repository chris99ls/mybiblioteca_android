package it.android.j940549.mybiblioteca;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.ArrayList;


import it.android.j940549.mybiblioteca.Crypto.Crypto_new;

public class Add_new_UtenteActivity extends AppCompatActivity {
    private EditText editPassword, editEmail;
    private EditText editnomeUser;
    private EditText editcognomeUser, editUsernameUser;
    private EditText editPassword2;
    boolean registrato=false;
    public static final String TAG = "KeyStore";
    private ProgressDialog progressDialog;
    //private String USER="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_utente);

        editnomeUser = (EditText) findViewById(R.id.registra_nome_user);
        editcognomeUser = (EditText) findViewById(R.id.registra_cognome_user);
        editUsernameUser = (EditText) findViewById(R.id.registra_username_utente);
        editEmail=(EditText) findViewById(R.id.registraemail);
        editPassword = (EditText) findViewById(R.id.registrapassword);
        editPassword2 = (EditText) findViewById(R.id.registra2password);


    }


    public void registrati(View v) {

        String nomeuser = editnomeUser.getText().toString();
        String cognomeuser = editcognomeUser.getText().toString();
        String username="m_"+editUsernameUser.getText().toString();
        String email=editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String password2 = editPassword2.getText().toString();
        String passwordCrypto="";
        if(password.equals(password2)) {
            Crypto_new crypto=new Crypto_new(getBaseContext());


/*            passwordCrypto=crypto.encrypt(password);
                Log.i("registrami",password);
            Log.i("registrami",passwordCrypto);
            passwordCrypto=password;
  */
            Log.w(TAG, "password da ciptare..."+password);

            try {
                                passwordCrypto=crypto.signData(password);
                } catch (KeyStoreException e) {
                    Log.w(TAG, "KeyStore not Initialized", e);
                } catch (UnrecoverableEntryException e) {
                    Log.w(TAG, "KeyPair not recovered", e);
                } catch (NoSuchAlgorithmException e) {
                    Log.w(TAG, "RSA not supported", e);
                } catch (InvalidKeyException e) {
                    Log.w(TAG, "Invalid Key", e);
                } catch (SignatureException e) {
                    Log.w(TAG, "Invalid Signature", e);
                } catch (IOException e) {
                    Log.w(TAG, "IO Exception", e);
                } catch (CertificateException e) {
                    Log.w(TAG, "Error occurred while loading certificates", e);
                }
                //Log.d(TAG, "Signature: " + mSignatureStr);


            Inserisci_new_Utente_InDB inserisci_new_utente_inDB=new Inserisci_new_Utente_InDB();
            inserisci_new_utente_inDB.execute(nomeuser,cognomeuser,username, email,passwordCrypto,"0");


        }else{
            Toast.makeText(this, "le due password non coincidono", Toast.LENGTH_SHORT).show();
        }

    }

private class Inserisci_new_Utente_InDB extends AsyncTask<String,String,String> {

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(getBaseContext());
        progressDialog.setMessage("caricamento dati in corso");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
//        progressDialog.show();
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
            HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/registra_utente.php");
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
            Intent vaiaLogin = new Intent(getBaseContext(), Login_Ute_Ges_Activity.class);
            //vaiaMenu.putExtra("user", user);
            startActivity(vaiaLogin);
            // USER=user;
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Ops! registrazione non avvenuta", Toast.LENGTH_SHORT).show();
        }


            progressDialog.dismiss();
    }
    }
}

