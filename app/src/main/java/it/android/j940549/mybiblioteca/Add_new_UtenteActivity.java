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


import it.android.j940549.mybiblioteca.Controller_DB.Inserisci_new_Utente_inDB;
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

        if(password.equals(password2)) {


            Inserisci_new_Utente_inDB inserisci_new_utente_inDB=new Inserisci_new_Utente_inDB(this);
            inserisci_new_utente_inDB.execute(nomeuser,cognomeuser,username, email,password,"0");


        }else{
            Toast.makeText(this, "le due password non coincidono", Toast.LENGTH_SHORT).show();
        }

    }

}

