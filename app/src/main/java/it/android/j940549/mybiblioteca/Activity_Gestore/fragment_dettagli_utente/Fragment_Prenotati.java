package it.android.j940549.mybiblioteca.Activity_Gestore.fragment_dettagli_utente;

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

import it.android.j940549.mybiblioteca.Model.Libri_Prenotati;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;


public class Fragment_Prenotati extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Utente utenteLogin;
    ArrayList mDataset;// = new ArrayList<Libri_Prenotati>();




    public Fragment_Prenotati() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @ param param1 Parameter 1.
     * @ param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Situazione_Prenotati.
     */
    // TODO: Rename and change types and number of parameters

    public static Fragment_Prenotati newInstance(Utente utenteLogin){//},ArrayList<Libri_Prenotati> libri_prenotati) {
        Fragment_Prenotati fragment = new Fragment_Prenotati();
        Bundle args = new Bundle();
        args.putSerializable("utente", utenteLogin);
//        args.putSerializable("dataset", libri_prenotati);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataset = new ArrayList<Libri_Prenotati>();

        if (getArguments() != null) {
            this.utenteLogin = (Utente) getArguments().getSerializable("utente");
  //          this.mDataset= (ArrayList) getArguments().getSerializable("dataset");
        }
        Log.i("log_tag_arg","argumets "+utenteLogin.getNrtessera());


      caricaDati(utenteLogin.getNrtessera());



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;

        rootView = inflater.inflate(R.layout.fragment_prenotati, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_prenotati);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyRecyclerViewAdapter_Prenotati(getDataSet(),getActivity(),utenteLogin);
        mRecyclerView.setAdapter(mAdapter);


        return rootView;
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
/*        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
  */  }

    @Override
    public void onDetach() {
        super.onDetach();
   //     mListener = null;
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


    private ArrayList<Libri_Prenotati> getDataSet() {
        return mDataset;
    }

    public void caricaDati(String nrtessera) {
        Log.i("log_tag_argcar","arumets "+nrtessera);

//        mDataset.removeAll(mDataset);
        Fragment_Prenotati.HttpGetTaskPrenotati task1 = new Fragment_Prenotati.HttpGetTaskPrenotati();

        task1.execute(nrtessera);
    }


    private class HttpGetTaskPrenotati extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String stringaFinale = " ";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("nrtessera",params[0]));


            InputStream is = null;

            //http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/prenotati_utente.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            }catch(Exception e){
                Log.e("TEST", "Errore nella connessione http "+e.toString());
            }
            if(is != null) {
                //converto la risposta in stringa
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();

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
            Log.i("log_tag", "parsing data on postExec prenotati"+result.toString());

            try{

                Log.i("log_tag", "dato da parsare in json "+result);
                JSONArray jArray = new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    Log.i("log_tag", "ciclo parsing data on postExec .."+i);

                    JSONObject json_data = jArray.getJSONObject(i);

                    Libri_Prenotati libro_prenotato = new Libri_Prenotati();
                    libro_prenotato.setPatch_img(json_data.getString("tumbnail"));
                    libro_prenotato.setIsbn(json_data.getString("isbn"));
                    libro_prenotato.setTitolo(json_data.getString("title"));


                    mDataset.add(libro_prenotato);
                }


                Log.i("log_tag", "results... prenot" + mDataset.size());

                mAdapter = new MyRecyclerViewAdapter_Prenotati(getDataSet(),getActivity(),utenteLogin);
                mRecyclerView.setAdapter(mAdapter);
            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
                mDataset.clear();
                //mAdapter = new MyRecyclerViewAdapter_gia_letti(mDataset);
                //mRecyclerView.setAdapter(mAdapter);

            }

        }
    }

}
