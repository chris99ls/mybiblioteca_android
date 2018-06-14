package it.android.j940549.mybiblioteca.Activity_Esito_Ricerche;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Controller_DB.Cerca_in_DB;
import it.android.j940549.mybiblioteca.Model.Libro_catalogo;
import it.android.j940549.mybiblioteca.R;

public class Esito_Ricerca extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Libro_catalogo> myDataset= new ArrayList<Libro_catalogo>();
    private String isbn,titolo,autore,genere,fulltext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isbn=getIntent().getExtras().get("isbn").toString();
        titolo=getIntent().getExtras().get("titolo").toString();
        autore=getIntent().getExtras().get("autore").toString();
        genere=getIntent().getExtras().get("genere").toString();
        fulltext=getIntent().getExtras().get("fulltext").toString();

        setContentView(R.layout.activity_esito__ricerca);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i("mylibreria","dataset_prima"+myDataset.size());

        Log.i("mylibreria","dataset_dopocrea"+myDataset.size());
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_esito_ricerca);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //crea dataset

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter_x_ricerca(myDataset,this);
        mRecyclerView.setAdapter(mAdapter);
        eseguiRicerca();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void eseguiRicerca(){
    Cerca_in_DB cerca_in_db =new Cerca_in_DB(this,mRecyclerView,mAdapter,myDataset);
    cerca_in_db.execute(isbn,titolo,autore,genere,fulltext);

    }

    /*private class Cerca_in_DB extends AsyncTask<String,Object,String > {

        @Override
        protected void onPreExecute() {
            // Check network connection.
            if(isNetworkConnected() == false){
                // Cancel request.
                Log.i("Esito_Ricerca", "Not connected to the internet");
                cancel(true);
                return;
            }
        }
        @Override
        protected String doInBackground(String... parametri) {
            // Stop if cancelled
            if(isCancelled()){
                return null;
            }
            String Url="http://lisiangelovpn.ddns.net/mybiblioteca/ricerca_book.php?";
            Url=Url+"isbn="+parametri[0]+"&";
            Url=Url+"titolo="+parametri[1]+"&";
            Url=Url+"autore="+parametri[2]+"&";
            Url=Url+"genere="+parametri[3]+"&";
            Url=Url+"fulltext="+parametri[4];

            //String url_ricera= creaUrl_ricerca(parametri[0],parametri[1],parametri[2],parametri[3],parametri[4]);

            Log.i("Esito_Ricerca ::url ",Url);
            try{
                HttpURLConnection connection = null;
                // Build Connection.
                try{
                    URL url = new URL(Url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000); // 5 seconds
                    connection.setConnectTimeout(5000); // 5 seconds
                } catch (MalformedURLException e) {
                    // Impossible: The only two URLs used in the app are taken from string resources.
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    // Impossible: "GET" is a perfectly valid request method.
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int responseCode = connection.getResponseCode();
                if(responseCode != 200){
                    Log.i("Esito_Ricerca", "ricerca request failed. Response Code: " + responseCode);
                    connection.disconnect();
                    return null;
                }
                Log.i("Esito_Ricerca", "ricerca request succesfully. Response Code: " + responseCode);
                // Read data from response.
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = responseReader.readLine();
                while (line != null){
                    builder.append(line);
                    line = responseReader.readLine();
                }
                String responseString = builder.toString();
                Log.i("Esito_Ricerca", "Response String: " + responseString);
                //JSONObject responseJson = new JSONObject(responseString);
                // Close connection and return response code.
                connection.disconnect();
                return responseString;
            } catch (SocketTimeoutException e) {
                Log.w("Esito_Ricerca", "Connection timed out. Returning null");
                return null;
            } catch(IOException e){
                Log.d("Esito_Ricerca", "IOException when connecting to Google Books API.");
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String responseString) {

            Log.i("log_tag", "parsing data on postExec "+responseString.toString());

            try{

              //  Log.i("log_tag", "dato da parsare in json "+responseJson.toString());
                JSONArray jArray = new JSONArray(responseString);
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

                mAdapter = new MyAdapter_x_ricerca(myDataset,Esito_Ricerca.this);
                mRecyclerView.setAdapter(mAdapter);
            }

            catch(JSONException e){
                Log.e("log_tag", "Error parsing data "+e.toString());
                myDataset.clear();
                mAdapter = new MyAdapter_x_ricerca(myDataset,Esito_Ricerca.this);
                mRecyclerView.setAdapter(mAdapter);
            }

        }
    }

        private String creaUrl_ricerca(String... params){

            String Url="http://lisiangelovpn.ddns.net/mybiblioteca/inserisci_new_book.php?";
            String xisbn="isbn="+params[0];
            String xtitolo="titolo="+params[1];
            String xautore="autore="+params[2];
            String xgenere="genere="+params[3];
            String xfulltext="fulltext="+params[4];
            /*if(params.length==1) {
                if (!isbn.equals("")) {
                    Url = Url + xisbn;
                }
                if (!titolo.equals("")) {
                    Url = Url + xtitolo;
                }
                if (!autore.equals("")) {
                    Url = Url + xautore;
                }
                if (!genere.equals("")) {
                    Url = Url + xgenere;
                }
                if (!fulltext.equals("")) {
                    Url = Url + xfulltext;
                }
            }else{*/
                /*if (!isbn.equals("")) {
                    Url = Url + xisbn+"&";
                }
                if (!titolo.equals("")) {
                    Url = Url + xtitolo+"&";
                }
                if (!autore.equals("")) {
                    Url = Url + xautore+"&";
                }
                if (!genere.equals("")) {
                    Url = Url + xgenere+"&";
                }
                if (!fulltext.equals("")) {
                    Url = Url + xfulltext+"&";
                }
                Url=Url.substring(0,Url.length()-1);

            //}
            return Url;

        }

    protected boolean isNetworkConnected(){
        ConnectivityManager mConnectivityManager=null;
        // Instantiate mConnectivityManager if necessary
        if(mConnectivityManager == null){
            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        // Is device connected to the Internet?
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        } else {
            return false;
        }
    }*/
}




