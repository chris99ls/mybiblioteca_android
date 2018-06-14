package it.android.j940549.mybiblioteca.Activity_Gestore;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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

import it.android.j940549.mybiblioteca.Controller_DB.Cerca_in_DB;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.R;


public class Gestisci_Catalogo extends Fragment {

    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mGridLayoutManager;
    private ArrayList<Libro_catalogo> myDataset = new ArrayList<Libro_catalogo>();

    private String utente;




    public Gestisci_Catalogo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param utente Parameter 1.
     *
     * @return A new instance of fragment Gestisci_Catalogo.
     */
    // TODO: Rename and change types and number of parameters
    public static Gestisci_Catalogo newInstance(String utente) {
        Gestisci_Catalogo fragment = new Gestisci_Catalogo();

       Bundle args = new Bundle();
        args.putString("utente", utente);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        creaLibreria();
        if (getArguments() != null) {
            utente = getArguments().getString("utente");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gestisci_catalogo, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_catalogo);
        mRecyclerView.setHasFixedSize(true);

        // use a grid layout manager
        mGridLayoutManager = new GridLayoutManager(getContext(),2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter_Gestisci_catalogo(myDataset,getActivity());
        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment
     //   mRecyclerViewpiuletti.setAdapter(mAdapter);

        creaLibreria();

        searchView = (SearchView) view.findViewById(R.id.searchView); // inititate a search view
        CharSequence query = searchView.getQuery(); // get the query string currently in the text field
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Cerca_in_DB cerca_in_db=new Cerca_in_DB(getActivity(),mRecyclerView,mAdapter,myDataset);
                cerca_in_db.execute("",query,"","","");
               // mAdapter = new MyAdapter_Gestisci_catalogo(myDataset,getActivity());
               // mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")){
                    creaLibreria();
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Gestisci_Catalogo");
    }
    private void creaLibreria() {
//        Carica_Catalogo carica_catalogo=new Carica_Catalogo(getActivity(),mRecyclerView,mRecyclerView,mAdapter,myDataset);
  //      carica_catalogo.execute();
        Gestisci_Catalogo.HttpGetTask task = new Gestisci_Catalogo.HttpGetTask();

        task.execute(utente);
    }


    private class HttpGetTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            InputStream inputStream = null;

            //http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet= new HttpGet("http://lisiangelovpn.ddns.net/mybiblioteca/catalogo.php");
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
            // aggiorno la textview con il risultato ottenuto
            Log.i("log_tag", "parsing data on postExec "+result.toString());

            try{

                Log.i("log_tag", "dato da parsare in json "+result);
                JSONArray jArray = new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    Log.i("log_tag", "ciclo parsing data on postExec .."+i);

                    JSONObject json_data = jArray.getJSONObject(i);
                    Libro_catalogo libro_catalogo= new Libro_catalogo();
                    libro_catalogo.setIsbn(json_data.getString("isbn"));
                    libro_catalogo.setTitolo(json_data.getString("title"));
                    libro_catalogo.setAutore(json_data.getString("author"));
                    libro_catalogo.setGenere(json_data.getString("subject"));
                    //String descrizione = json_data.getString("description");
                    libro_catalogo.setTumbnail(json_data.getString("tumbnail"));

                    // Log.i("log_tag", "datobject inserito " + obj.getData() );

                    myDataset.add(libro_catalogo);

                }
                Log.i("log_tag", "results... " + myDataset.size());

                mAdapter = new MyAdapter_Gestisci_catalogo(myDataset,getActivity());
                mRecyclerView.setAdapter(mAdapter);
            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
                myDataset.clear();
                mAdapter = new MyAdapter_Gestisci_catalogo(myDataset,getActivity());
                mRecyclerView.setAdapter(mAdapter);
            }

        }
    }
}
