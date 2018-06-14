package it.android.j940549.mybiblioteca.Activity_Utente;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import it.android.j940549.mybiblioteca.Activity_Esito_Ricerche.Esito_Ricerca;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;

import static android.app.Activity.RESULT_OK;


public class Ricerca extends Fragment {

    static final String ACTION_SCAN="com.google.zxing.client.android.SCAN";
    private TextView txt_isbn;
    private TextView txt_titolo;
    private TextView txt_autore;
    private TextView txt_genere;
    private TextView txt_fulltext;
    private Utente utenteLogin;

    public Ricerca() {
        // Required empty public constructor
    }


    public static Ricerca newInstance(Utente utenteLogin) {
        Ricerca fragment = new Ricerca();
        Bundle args = new Bundle();
        args.putSerializable("utente", utenteLogin);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            utenteLogin= (Utente) getArguments().getSerializable("utente");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ricerca, container, false);

        txt_isbn=(TextView)view.findViewById(R.id.xibns);
        txt_titolo=(TextView)view.findViewById(R.id.xtitolo);
        txt_autore=(TextView)view.findViewById(R.id.xautore);
        txt_genere=(TextView)view.findViewById(R.id.xmateria);
        txt_fulltext=(TextView)view.findViewById(R.id.xricercalibera);

        ImageButton btn_scan_codbar=(ImageButton) view.findViewById(R.id.btn_scan_codebar);
        btn_scan_codbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                scanBar(v);
            }
        });
        Button btn=(Button) view.findViewById(R.id.btn_cerca);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
        //        Toast.makeText(getContext(), "aa", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), Esito_Ricerca.class);
                intent.putExtra("isbn",txt_isbn.getText().toString());
                intent.putExtra("titolo",txt_titolo.getText().toString());
                intent.putExtra("autore",txt_autore.getText().toString());
                intent.putExtra("genere",txt_genere.getText().toString());
                intent.putExtra("fulltext",txt_fulltext.getText().toString());

                startActivity(intent);
               // getActivity().finish();
            }
        });



        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Ricerca");
    }

public void scanBar(View view){
    try {
        Intent intent = new Intent(ACTION_SCAN);
        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
        startActivityForResult(intent, 0);
    }catch(ActivityNotFoundException e){
       mostraDialog(getActivity(),"nessuno scanner installato", "Vuoi installare uno Scanner code?", "si", "no").show();
    }
}
   public void onActivityResult(int requestCode, int resultCode,Intent intent){
       if (requestCode == 0) {
           if(resultCode==RESULT_OK){

             String resultisbn = intent.getStringExtra("SCAN_RESULT");
             txt_isbn.setText(resultisbn);

           }
       }
   }

   private AlertDialog mostraDialog(final Activity activity, CharSequence title, CharSequence message,
                                    CharSequence btnYes, CharSequence btnNo){
        AlertDialog.Builder downloadDialog= new AlertDialog.Builder(activity);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(btnYes,new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               Uri uri= Uri.parse("market://search?q=pname:"+"com.google.zxing.client.android");
               Intent intent= new Intent(Intent.ACTION_VIEW,uri);
               try{
                   activity.startActivity(intent);
               }catch (ActivityNotFoundException e){

               }
           }
       });
       downloadDialog.setNegativeButton(btnNo,new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {

           }
       });
       return downloadDialog.show();
   }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

}
