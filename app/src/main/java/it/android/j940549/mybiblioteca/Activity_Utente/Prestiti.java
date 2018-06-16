package it.android.j940549.mybiblioteca.Activity_Utente;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti.Fragment_Gia_Letti;
import it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti.Fragment_In_Prestito;
import it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti.Fragment_Prenotati;
import it.android.j940549.mybiblioteca.Model.Libri_In_Prestito;
import it.android.j940549.mybiblioteca.Model.Libri_Prenotati;
import it.android.j940549.mybiblioteca.Model.Libri_gia_letti;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;


public class Prestiti extends Fragment {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Utente utenteLogin =null;
/*    ArrayList<Libri_Prenotati> mDatasetPrenotati ;
    ArrayList<Libri_gia_letti> mDatasetGialetti;
    ArrayList<Libri_In_Prestito> mDatasetinPrestito;
    private ProgressDialog progressDialog;
  */

    public Prestiti(){

    }
    public static Prestiti newInstance(Utente utenteLogin) {
        Prestiti fragment = new Prestiti ();
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
    /*    mDatasetPrenotati = new ArrayList<Libri_Prenotati>();
        mDatasetGialetti = new ArrayList<Libri_gia_letti>();
        mDatasetinPrestito = new ArrayList<Libri_In_Prestito>();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("caricamento dati in corso");
        progressDialog.setCancelable(false);
        progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);

    caricaDati(utenteLogin.getNrtessera());
*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_prestiti,container,false);



        TextView nomealunno= (TextView) view.findViewById(R.id.nomeutente_prestiti);
        nomealunno.setText(utenteLogin.getCognome().toUpperCase()+" "+utenteLogin.getNome().toUpperCase());
        TextView nrTessera= (TextView) view.findViewById(R.id.nr_tessera_utente_prestiti);
        nrTessera.setText("Nr. Tessera: "+utenteLogin.getNrtessera());


        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.container_prestiti);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs_prestiti);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putSerializable("utente", utenteLogin);

            Fragment fragment = null;
            switch (position) {
                case 0: {
                    fragment= new Fragment_Prenotati();
  //                  args.putSerializable("dataset", mDatasetPrenotati);
                    fragment.setArguments(args);

                    break;
                }
                case 1: {
                    fragment= new Fragment_In_Prestito();
    //                args.putSerializable("dataset",mDatasetinPrestito);
                    fragment.setArguments(args);

                    break;

                }
                case 2: {
                    fragment= new Fragment_Gia_Letti();
      //              args.putSerializable("dataset",mDatasetGialetti);
                    fragment.setArguments(args);

                    break;

                }
                default:
                    fragment=null;

            }
            return fragment;

        }
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Prenotati";
                case 1:
                    return "In prestito";
                case 2:
                    return "Già letti";
            }
            return null;
        }
    }
    /*
    public void caricaDati(String nrtessera) {
        Log.i("log_tag_argcar","arumets "+nrtessera);

        mDatasetinPrestito.removeAll(mDatasetinPrestito);
        mDatasetGialetti.removeAll(mDatasetGialetti);
        mDatasetPrenotati.removeAll(mDatasetPrenotati);
        Prestiti.HttpGetTaskPrestiti task1 = new Prestiti.HttpGetTaskPrestiti();

        task1.execute(nrtessera);
        Prestiti.HttpGetTaskPrenotati task2 = new Prestiti.HttpGetTaskPrenotati();

        task2.execute(nrtessera);
        Prestiti.HttpGetTaskGiavisti task3 = new Prestiti.HttpGetTaskGiavisti();

        task3.execute(nrtessera);
    }


    private class HttpGetTaskPrestiti extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

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
                HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/prestiti_utente.php");
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
                        Libri_In_Prestito libro_in_prestito= new Libri_In_Prestito();
                        libro_in_prestito.setPatch_img(json_data.getString("tumbnail"));
                        libro_in_prestito.setIsbn(json_data.getString("isbn"));
                        libro_in_prestito.setTitolo(json_data.getString("title"));
                        libro_in_prestito.setData_prestito(json_data.getString("dal"));

                        mDatasetinPrestito.add(libro_in_prestito);



                }
                Log.i("log_tag", "results... --inpre-"+mDatasetinPrestito);

                //mAdapter = new MyRecyclerViewAdapter_gia_letti(getDataSet());
                //mRecyclerView.setAdapter(mAdapter);
                progressDialog.dismiss();
            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
                mDatasetinPrestito.clear();
                //mAdapter = new MyRecyclerViewAdapter_gia_letti(mDataset);
                //mRecyclerView.setAdapter(mAdapter);

            }

        }
    }

    private class HttpGetTaskPrenotati extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

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
            Log.i("log_tag", "parsing data on postExec "+result.toString());

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


                        mDatasetPrenotati.add(libro_prenotato);
                    }


                Log.i("log_tag", "results... prenot" + mDatasetPrenotati.size());

                //mAdapter = new MyRecyclerViewAdapter_gia_letti(getDataSet());
                //mRecyclerView.setAdapter(mAdapter);
                progressDialog.dismiss();
            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
                mDatasetPrenotati.clear();
                //mAdapter = new MyRecyclerViewAdapter_gia_letti(mDataset);
                //mRecyclerView.setAdapter(mAdapter);

            }

        }
    }

    private class HttpGetTaskGiavisti extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

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
                HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/giavisti_utente.php");
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
                        Libri_gia_letti libro_gia_letti = new Libri_gia_letti();
                        libro_gia_letti.setPatch_img(json_data.getString("tumbnail"));
                        libro_gia_letti.setIsbn(json_data.getString("isbn"));
                        libro_gia_letti.setTitolo(json_data.getString("title"));


                        mDatasetGialetti.add(libro_gia_letti);


                }
                Log.i("log_tag", "results... gialet" + mDatasetGialetti.size());

                //mAdapter = new MyRecyclerViewAdapter_gia_letti(getDataSet());
                //mRecyclerView.setAdapter(mAdapter);
                mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

                progressDialog.dismiss();
            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());

                mDatasetGialetti.clear();
                //mAdapter = new MyRecyclerViewAdapter_gia_letti(mDataset);
                //mRecyclerView.setAdapter(mAdapter);

            }

        }
    }
*/
}