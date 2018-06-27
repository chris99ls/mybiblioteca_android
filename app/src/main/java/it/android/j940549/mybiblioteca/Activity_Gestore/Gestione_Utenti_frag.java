package it.android.j940549.mybiblioteca.Activity_Gestore;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Controller_DB.Carica_Lista_Utenti;
import it.android.j940549.mybiblioteca.Controller_DB.Cerca_Utente_in_DB;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;



public class Gestione_Utenti_frag extends Fragment {

    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private ArrayList<Utente> myDataset = new ArrayList<Utente>();


    public Gestione_Utenti_frag() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Gestione_Utenti_frag newInstance() {
        Gestione_Utenti_frag fragment = new Gestione_Utenti_frag();

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
        final View view = inflater.inflate(R.layout.fragment_gestisci_utenti, container, false);

        //settaggi del widget di ricerca utenti

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
                Cerca_Utente_in_DB cerca_in_db=new Cerca_Utente_in_DB(getActivity(),mRecyclerView,mAdapter);
                cerca_in_db.execute(newText);

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

        caricaListaUtenti();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Gestisci Utenti");
    }

    private void caricaListaUtenti() {

        Carica_Lista_Utenti carica_lista_utenti = new Carica_Lista_Utenti(getActivity(),mRecyclerView,mAdapter);
        carica_lista_utenti.execute();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
      //  mListener = null;
    }


}
