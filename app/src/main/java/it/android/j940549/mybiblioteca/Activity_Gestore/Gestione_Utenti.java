package it.android.j940549.mybiblioteca.Activity_Gestore;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Esito_Ricerche.Esito_Ricerca;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Gestione_Utenti.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Gestione_Utenti#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Gestione_Utenti extends Fragment {

    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private ArrayList<Utente> myDataset = new ArrayList<Utente>();

    private String utente;


   // private OnFragmentInteractionListener mListener;

    public Gestione_Utenti() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param utente Parameter 1.

     * @return A new instance of fragment Gestione_Utenti.
     */
    // TODO: Rename and change types and number of parameters
    public static Gestione_Utenti newInstance(String utente) {
        Gestione_Utenti fragment = new Gestione_Utenti();
        Bundle args = new Bundle();
        args.putString("utente", utente);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            utente = getArguments().getString("utente");

        }
        creaListaUtenti();
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
// do something on text submit
//Toast.makeText(view.getContext(), newText, Toast.LENGTH_SHORT).show();
            return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
            // do something when text changes
                //Toast.makeText(view.getContext(), newText, Toast.LENGTH_SHORT).show();
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

    private void creaListaUtenti() {
        for (int i = 0; i <= 10; i++) {
            Utente utente = new Utente();
            utente.setNrtessera(""+i*2541);
            utente.setNome("nomeutente" + i);
            utente.setCognome(i + "_cognome");
            utente.setEmail("cognome.nome"+i+"@gmail.com");
            myDataset.add(utente);

            //  mAdapter.notifyDataSetChanged();
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
