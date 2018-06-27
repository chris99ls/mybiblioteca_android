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

import it.android.j940549.mybiblioteca.Controller_DB.Carica_Catalogo;
import it.android.j940549.mybiblioteca.Controller_DB.Cerca_libro_in_DB;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;


public class Gestisci_Catalogo_frag extends Fragment {

    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mGridLayoutManager;
    private ArrayList<Libro_catalogo> myDataset = new ArrayList<Libro_catalogo>();




    public Gestisci_Catalogo_frag() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Gestisci_Catalogo_frag newInstance() {
        Gestisci_Catalogo_frag fragment = new Gestisci_Catalogo_frag();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        creaLibreria();
        if (getArguments() != null) {

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

        //setaggi del widget di ricerca dei libri nel catalogo

        searchView = (SearchView) view.findViewById(R.id.searchView); // inititate a search view
        CharSequence query = searchView.getQuery(); // get the query string currently in the text field
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Cerca_libro_in_DB cerca_in_db=new Cerca_libro_in_DB(getActivity(),mRecyclerView,mAdapter);
                cerca_in_db.execute("",query,"","","");
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
        getActivity().setTitle("Gestisci Catalogo");
    }
    private void creaLibreria() {
        Carica_Catalogo carica_catalogo=new Carica_Catalogo(getActivity(),mRecyclerView,mAdapter,new Utente());
        carica_catalogo.execute();
    }


}
