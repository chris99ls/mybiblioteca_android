package it.android.j940549.mybiblioteca.Activity_Gestore;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import it.android.j940549.mybiblioteca.Controller_DB.Inserisci_new_Gestore_InDB;
import it.android.j940549.mybiblioteca.Crypto.Crypto_new;
import it.android.j940549.mybiblioteca.R;

public class Add_New_Gestore_frag extends Fragment {
    private EditText editPassword, editEmail;
    private EditText editnomeGes, editUsername;
    private EditText editcognomeGes;
    private EditText editPassword2;
    public static final String TAG = "KeyStore";


    public Add_New_Gestore_frag() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Add_New_Gestore_frag newInstance() {
        Add_New_Gestore_frag fragment = new Add_New_Gestore_frag();
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

        editnomeGes = (EditText) view.findViewById(R.id.registra_nome_gestore);
        editcognomeGes = (EditText) view.findViewById(R.id.registra_cognome_gestore);
        editUsername = (EditText) view.findViewById(R.id.registra_username_gestore);
        editEmail=(EditText) view.findViewById(R.id.registraemail);
        editPassword = (EditText) view.findViewById(R.id.registrapassword);
        editPassword2 = (EditText) view.findViewById(R.id.registra2password);
        Button btn_registraGestore= view.findViewById(R.id.registrati);
        btn_registraGestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registraGestore(view);
            }
        });
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void registraGestore(View v) {

        String nomeuser = editnomeGes.getText().toString();
        String cognomeuser = editcognomeGes.getText().toString();
        String username = "m_"+editUsername.getText().toString();
        String email=editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String password2 = editPassword2.getText().toString();

        if(password.equals(password2)) {

            Inserisci_new_Gestore_InDB inserisci_new_gestore_inDB=new Inserisci_new_Gestore_InDB(getActivity());
            inserisci_new_gestore_inDB.execute(nomeuser,cognomeuser,username,email,password,"1");

        }
        else{
            Toast.makeText(getActivity(), "le due password non coincidono", Toast.LENGTH_SHORT).show();
        }

    }
}

