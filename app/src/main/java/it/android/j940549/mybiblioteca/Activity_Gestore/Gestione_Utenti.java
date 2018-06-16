package it.android.j940549.mybiblioteca.Activity_Gestore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Esito_Ricerche.Esito_Ricerca;
import it.android.j940549.mybiblioteca.Catalogo_libri.Catalogo;
import it.android.j940549.mybiblioteca.Catalogo_libri.MyAdapter;
import it.android.j940549.mybiblioteca.Controller_DB.Cerca_Utente_in_DB;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;



public class Gestione_Utenti extends Fragment {

    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private ArrayList<Utente> myDataset = new ArrayList<Utente>();



   // private OnFragmentInteractionListener mListener;

    public Gestione_Utenti() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Gestione_Utenti newInstance() {
        Gestione_Utenti fragment = new Gestione_Utenti();
     /*   Bundle args = new Bundle();
        args.putString("utente", utente);

        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           // utente = getArguments().getString("utente");

        }
        caricaListaUtenti();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gestisci_utenti, container, false);

        searchView = (SearchView) view.findViewById(R.id.searchView); // inititate a search view
        CharSequence query = searchView.getQuery(); // get the query string currently in the text field
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Cerca_Utente_in_DB cerca_in_db=new Cerca_Utente_in_DB(getActivity(),mRecyclerView,mAdapter);
                cerca_in_db.execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    caricaListaUtenti();
                }
                return false;
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_utenti);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
//

        // use a linear layout manager
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter_Gestisci_utenti(myDataset,getActivity());
        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment
        //   mRecyclerViewpiuletti.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Gestisci_Catalogo");
    }

    private void caricaListaUtenti() {
//        Carica_Catalogo carica_catalogo= new Carica_Catalogo(getActivity(),mRecyclerView,mRecyclerViewpiuletti,mAdapter,myDataset);
        //      carica_catalogo.execute();
        Gestione_Utenti.HttpGetTask task = new Gestione_Utenti.HttpGetTask();
        task.execute();
    }


    private class HttpGetTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            InputStream inputStream = null;

            //http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet= new HttpGet("http://lisiangelovpn.ddns.net/mybiblioteca/carica_utenti.php");
                HttpResponse response = httpclient.execute(httpGet);
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
            myDataset.clear();
            // aggiorno la textview con il risultato ottenuto
            Log.i("log_tag", "parsing data on postExec "+result.toString());

            try{

                Log.i("log_tag", "dato da parsare in json "+result);
                JSONArray jArray = new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    Log.i("log_tag", "ciclo parsing data on postExec .."+i);

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

                mAdapter = new MyAdapter_Gestisci_utenti(myDataset,getActivity());
                mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment

            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
                myDataset.clear();
                mAdapter = new MyAdapter_Gestisci_utenti(myDataset,getActivity());
                mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment
            }

        }
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
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
      //  mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
