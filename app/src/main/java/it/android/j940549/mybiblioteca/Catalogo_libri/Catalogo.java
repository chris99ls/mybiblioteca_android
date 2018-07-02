package it.android.j940549.mybiblioteca.Catalogo_libri;

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

import it.android.j940549.mybiblioteca.Controller_DB.Carica_Catalogo;
import it.android.j940549.mybiblioteca.Controller_DB.Carica_Catalogo_piu_letti;
import it.android.j940549.mybiblioteca.Controller_DB.Cerca_libro_in_DB;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;


public class Catalogo extends Fragment {

    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewpiuletti;
    private RecyclerView.Adapter mAdapter;
    private MyAdapter_piuletti mAdapterpiuletti;
    private RecyclerView.LayoutManager mGridLayoutManager;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private ArrayList<Libro_catalogo> myDataset = new ArrayList<Libro_catalogo>();
    private Utente utenteLogin;


    public Catalogo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment Gestisci_Catalogo_frag.
     */
    // TODO: Rename and change types and number of parameters
    public static Catalogo newInstance(Utente utenteLogin) {
        Catalogo fragment = new Catalogo();
        Bundle args = new Bundle();
        args.putSerializable("utente", utenteLogin);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            utenteLogin = (Utente) getArguments().getSerializable("utente");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_catalogo, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_catalogo);
        mRecyclerViewpiuletti = (RecyclerView) view.findViewById(R.id.recycler_view_catalogo_piuletti);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerViewpiuletti.setHasFixedSize(true);

        // use a grid layout manager
        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        // use a linear layout manager
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewpiuletti.setLayoutManager(mLinearLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset, getActivity(),utenteLogin);
        mAdapterpiuletti = new MyAdapter_piuletti(myDataset, getActivity(),utenteLogin);

        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment
        mRecyclerViewpiuletti.setAdapter(mAdapterpiuletti);

        creaLibreria();


        searchView = (SearchView) view.findViewById(R.id.searchView); // inititate a search view
        final CharSequence mquery = searchView.getQuery(); // get the query string currently in the text field
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Cerca_libro_in_DB cerca_in_db=new Cerca_libro_in_DB(getActivity(),mRecyclerView,mAdapter,utenteLogin);
                cerca_in_db.execute("","","","",mquery.toString());
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
        getActivity().setTitle("Catalogo");

    }

    private void creaLibreria() {
        Carica_Catalogo carica_catalogo= new Carica_Catalogo(getActivity(),mRecyclerView,mAdapter,utenteLogin);
        carica_catalogo.execute();
        Carica_Catalogo_piu_letti carica_catalogo_piu_leti= new Carica_Catalogo_piu_letti(getActivity(),mRecyclerViewpiuletti,mAdapterpiuletti,utenteLogin);
        carica_catalogo_piu_leti.execute();
    }




}
