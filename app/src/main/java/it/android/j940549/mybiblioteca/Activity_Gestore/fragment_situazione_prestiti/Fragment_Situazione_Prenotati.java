package it.android.j940549.mybiblioteca.Activity_Gestore.fragment_situazione_prestiti;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Model.Libri_Prenotati;
import it.android.j940549.mybiblioteca.R;


public class Fragment_Situazione_Prenotati extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private String utente;
    ArrayList myDataset = new ArrayList<Libri_Prenotati>();




    public Fragment_Situazione_Prenotati() {
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

    public static Fragment_Situazione_Prenotati newInstance(String utente) {
        Fragment_Situazione_Prenotati fragment = new Fragment_Situazione_Prenotati();
        Bundle args = new Bundle();
        args.putString("utente", utente);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.utente = getArguments().getString("utente");

        }
        Log.i("log_tag_arg","argumets "+utente);
        caricaDati(utente);



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
        // specify an adapter (see also next example)
        mAdapter = new MyRecyclerViewAdapter_situazione_Prenotati(myDataset);
        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment



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
        return myDataset;
    }

    public void caricaDati(String utente) {


        for (int i = 0; i <= 10; i++) {
            Libri_Prenotati libro = new Libri_Prenotati();
            libro.setIsbn("ISBN "+i*2541);
            libro.setPatch_img("patch." + i);
            libro.setTitolo(i + "titolo");
            myDataset.add(libro);
            //Log.i("libro", libro.getIsbn()+"--"+libro.getAutore() + " - " + libro.getTitolo());
            //  mAdapter.notifyDataSetChanged();
        }
    }
}

/*
        Log.i("log_tag_argcar","arumets "+utente);

        myDataset.removeAll(myDataset);
        Fragment_Prenotati.HttpGetTask task = new Fragment_Prenotati.HttpGetTask();

        task.execute(utente);
    }


    private class HttpGetTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String stringaFinale = " ";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("utente",params[0]));


            InputStream is = null;

            //http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/myre/richiesta_compiti.php");
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
            Log.i("log_tag", "parsing data on postExec "+result.toString());

            try{

                Log.i("log_tag", "dato da parsare in json "+result);
                JSONArray jArray = new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    Log.i("log_tag", "ciclo parsing data on postExec .."+i);

                    JSONObject json_data = jArray.getJSONObject(i);
                    Log.i("log_tag","data: "+json_data.getString("data")+
                            ", compiti: "+json_data.getString("compiti"));

                    String a = json_data.getString("data");
                    String b = json_data.getString("compiti");
                    Libri_Prenotati obj = new Libri_Prenotati();
                   // Log.i("log_tag", "datobject inserito " + obj.getData() );

                    myDataset.add(obj);

                }
                Log.i("log_tag", "myDataset... " + myDataset.size());

                mAdapter = new MyRecyclerViewAdapter_Prenotati(getDataSet());
                mRecyclerView.setAdapter(mAdapter);
            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
                myDataset.clear();
                mAdapter = new MyRecyclerViewAdapter_Prenotati(myDataset);
                mRecyclerView.setAdapter(mAdapter);

            }

        }
    }
}
*/