package it.android.j940549.mybiblioteca.Activity_Gestore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import it.android.j940549.mybiblioteca.R;

public class Add_New_Gestore extends Fragment {
    private EditText editPassword, editEmail;
    private EditText editnomeGes, editUsername;
    private EditText editcognomeGes;
    private EditText editPassword2;
    boolean registrato=false;
    public static final String TAG = "KeyStore";
    private ProgressDialog progressDialog;


    public Add_New_Gestore() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Add_New_Gestore newInstance() {
        Add_New_Gestore fragment = new Add_New_Gestore();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
          }

        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_new__gestore, container, false);

        editnomeGes = (EditText) view.findViewById(R.id.registra_nome_user);
        editcognomeGes = (EditText) view.findViewById(R.id.registra_cognome_user);
        editUsername = (EditText) view.findViewById(R.id.registra_username_gestore);
        editEmail=(EditText) view.findViewById(R.id.registraemail);
        editPassword = (EditText) view.findViewById(R.id.registrapassword);
        editPassword2 = (EditText) view.findViewById(R.id.registra2password);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
      /*  if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
           // mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    public void registraGestore(View v) {

        String nomeuser = editnomeGes.getText().toString();
        String cognomeuser = editcognomeGes.getText().toString();
        String username = "m_"+editUsername.getText().toString();
        String email=editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String password2 = editPassword2.getText().toString();
        String passwordCrypto="";
        if(password.equals(password2)) {
            Crypto_new crypto=new Crypto_new(getContext());


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


            Add_New_Gestore.Inserisci_new_Gestore_InDB inserisci_new_gestore_inDB=new Add_New_Gestore.Inserisci_new_Gestore_InDB();
            inserisci_new_gestore_inDB.execute(nomeuser,cognomeuser,username,email,passwordCrypto,"1");


        }else{
            Toast.makeText(getActivity(), "le due password non coincidono", Toast.LENGTH_SHORT).show();
        }

    }

    private class Inserisci_new_Gestore_InDB extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("caricamento dati in corso");
            progressDialog.setCancelable(false);
            progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
         //   progressDialog.show();
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
                Intent vaiaLogin = new Intent(getContext(), GestoreNav.class);
                //vaiaMenu.putExtra("user", user);
                startActivity(vaiaLogin);
                // USER=user;
                getActivity().finish();
            } else {
                Toast.makeText(getContext(), "Ops! registrazione non avvenuta", Toast.LENGTH_SHORT).show();
            }

        progressDialog.dismiss();

        }
    }
}

