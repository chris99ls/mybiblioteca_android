package it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Controller_DB.Carica_gia_letti;
import it.android.j940549.mybiblioteca.Model.Libri_Prenotati;
import it.android.j940549.mybiblioteca.Model.Libri_gia_letti;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;


public class Fragment_Gia_Letti extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private Utente utenteLogin;
    ArrayList mDataset;


    public Fragment_Gia_Letti() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters

    public static Fragment_Gia_Letti newInstance(Utente utenteLogin){
        Fragment_Gia_Letti fragment = new Fragment_Gia_Letti();
        Bundle args = new Bundle();
        args.putSerializable("utente", utenteLogin);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataset = new ArrayList<Libri_Prenotati>();

        if (getArguments() != null) {
            this.utenteLogin= (Utente) getArguments().getSerializable("utente");
        }
        Log.i("log_tag_arg","argumets "+utenteLogin.getNrtessera());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        rootView = inflater.inflate(R.layout.fragment_gia_letti, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_gialetti);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        caricaDati(utenteLogin.getNrtessera());

        mAdapter = new MyRecyclerViewAdapter_gia_letti(getDataSet(),getActivity());
        mRecyclerView.setAdapter(mAdapter);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
       /* if (mListener != null) {
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
    //    mListener = null;
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


    private ArrayList<Libri_gia_letti> getDataSet() {
        return mDataset;
    }

    public void caricaDati(String nrtessera) {
        Log.i("log_tag_argcar","arumets "+nrtessera);

        Carica_gia_letti carica_gia_letti= new Carica_gia_letti(getActivity(),mRecyclerView,mAdapter);
        carica_gia_letti.execute(nrtessera);
    }

}
