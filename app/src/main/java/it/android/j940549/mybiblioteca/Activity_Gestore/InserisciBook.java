package it.android.j940549.mybiblioteca.Activity_Gestore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Esito_Ricerche.Esito_Ricerca;
import it.android.j940549.mybiblioteca.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link InserisciBook#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InserisciBook extends Fragment {


    // TODO: Rename and change types of parameters
    static final String ACTION_SCAN="com.google.zxing.client.android.SCAN";
    private String utente="UtenteXX";
    private EditText txtview_isbn,txtview_descrizione,txtview_titolo,txtview_autore,txtview_genere;
    private ConnectivityManager mConnectivityManager;
    private String image_link, resultInsert,isbn_da_ricercaGoogle;
    private ImageView imageView;   // private OnFragmentInteractionListener mListener;

    public InserisciBook() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static InserisciBook newInstance( String isbn_daRicercaGoogle) {
        InserisciBook fragment = new InserisciBook();

            Bundle args = new Bundle();
            args.putSerializable("isbn_da_ricercaGoogle", isbn_daRicercaGoogle);
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isbn_da_ricercaGoogle="";
        if (getArguments() != null) {
            isbn_da_ricercaGoogle= getArguments().getString("isbn_da_ricercaGoogle");

        }
        resultInsert="";
        if(!isbn_da_ricercaGoogle.equals("")){
            CaricaDatidaGoogleBook caricaDatidaGogleBook=new CaricaDatidaGoogleBook();
            caricaDatidaGogleBook.execute(isbn_da_ricercaGoogle);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inserisci_book, container, false);

        txtview_isbn=(EditText) view.findViewById(R.id.isbn_libro);
        txtview_descrizione=(EditText)view.findViewById(R.id.descrizione_libro);
        txtview_titolo=(EditText)view.findViewById(R.id.titolo_libro);
        txtview_autore=(EditText)view.findViewById(R.id.autore_libro);
        txtview_genere=(EditText)view.findViewById(R.id.genere_libro);
        imageView=view.findViewById(R.id.copertina_libro);

        //istanzia e gestisci onclick di btn_scanner
        ImageButton btn_scan_codbar=(ImageButton) view.findViewById(R.id.btn_scan_codebar);
        btn_scan_codbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                scanBar(v);
            }
        });

        //istanzia e gestisci onclick di btn_cerca_titolos GoogleBooksscanner
        ImageButton btn_cerca_titolo_onGoogleBooks=(ImageButton) view.findViewById(R.id.btn_cerca_x_titolo);
        btn_cerca_titolo_onGoogleBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getContext(), Esito_Ricerca.class);
                intent.putExtra("isbn","");
                intent.putExtra("autore","");
                intent.putExtra("genere","");
                intent.putExtra("fulltext","");

                intent.putExtra("titolo",txtview_titolo.getText().toString());
                intent.putExtra("ricerca","google");
                startActivity(intent);
            }
        });


        //istanzia e gestisci onclic di btn_inserisc
        Button btn_inserisci=(Button) view.findViewById(R.id.btn_inserisci);
        btn_inserisci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String isbn = txtview_isbn.getText().toString();
                String descrizione = txtview_descrizione.getText().toString();
                 descrizione = descrizione.replaceAll("'","\'");

                String titolo = txtview_titolo.getText().toString();
                String autore = txtview_autore.getText().toString();
                String subject = txtview_genere.getText().toString();

                InserisciInDB inserisciInDB = new InserisciInDB();
                inserisciInDB.execute(titolo, autore, subject, descrizione, image_link, isbn);
                if (resultInsert.contains("successfully")) {
                    Fragment fragment =   Gestisci_Catalogo_frag.newInstance();

                    //inserisci il fragment rimpiazzando i frgment esitente
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent_gestore, fragment).commit();

                }
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Inserisci un nuovo Libro");
    }

    public void scanBar(View view){
        try {
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        }catch(ActivityNotFoundException e){
            mostraDialog(getActivity(),"nessuno scanner installato", "Vuoi installare uno Scanner code?", "si", "no").show();
        }
    }
    public void onActivityResult(int requestCode, int resultCode,Intent intent){
        if (requestCode == 0) {
            if(resultCode==RESULT_OK){

                String resultisbn = intent.getStringExtra("SCAN_RESULT");
                txtview_isbn.setText(resultisbn);
                 CaricaDatidaGoogleBook caricaDatidaGoogleBook=new CaricaDatidaGoogleBook();
                 caricaDatidaGoogleBook.execute(resultisbn);

            }
        }
    }

    private AlertDialog mostraDialog(final Activity activity, CharSequence title, CharSequence message,
                                     CharSequence btnYes, CharSequence btnNo){
        AlertDialog.Builder downloadDialog= new AlertDialog.Builder(activity);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(btnYes,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri= Uri.parse("market://search?q=pname:"+"com.google.zxing.client.android");
                Intent intent= new Intent(Intent.ACTION_VIEW,uri);
                try{
                    activity.startActivity(intent);
                }catch (ActivityNotFoundException e){

                }
            }
        });
        downloadDialog.setNegativeButton(btnNo,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        return downloadDialog.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    private class CaricaDatidaGoogleBook extends AsyncTask <String,Object,JSONObject>{
        @Override
        protected void onPreExecute() {
            // Check network connection.
            if(isNetworkConnected() == false){
                // Cancel request.
                Log.i("inserisciBook", "Not connected to the internet");
                cancel(true);
                return;
            }
        }
        @Override
        protected JSONObject doInBackground(String... isbns) {
            // Stop if cancelled
            if(isCancelled()){
                return null;
            }

            String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbns[0];
            Log.i("inserisciBook",apiUrlString);
            try{
                HttpURLConnection connection = null;
                // Build Connection.
                try{
                    URL url = new URL(apiUrlString);
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
                    Log.i("inserisciBook", "GoogleBooksAPI request failed. Response Code: " + responseCode);
                    connection.disconnect();
                    return null;
                }
                Log.i("inserisciBook", "GoogleBooksAPI request succesfully. Response Code: " + responseCode);
                // Read data from response.
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = responseReader.readLine();
                while (line != null){
                    builder.append(line);
                    line = responseReader.readLine();
                }
                String responseString = builder.toString();
                Log.i("inserisciBook", "Response String: " + responseString);
                JSONObject responseJson = new JSONObject(responseString);
                // Close connection and return response code.
                connection.disconnect();
                return responseJson;
            } catch (SocketTimeoutException e) {
                Log.w("inserisciBook", "Connection timed out. Returning null");
                return null;
            } catch(IOException e){
                Log.d("inserisciBook", "IOException when connecting to Google Books API.");
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                Log.d("inserisciBook", "JSONException when connecting to Google Books API.");
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(JSONObject responseJson) {

            String titolo="";
            String autore="";
            String genere="";
            String descrizione="";
            if(isCancelled()){
                // Request was cancelled due to no network connection.

            } else if(responseJson == null){
                //teshowSimpleDialog(getResources().getString(R.string.dialog_null_response));
            }
            else{
                try{
                    txtview_isbn.setText(isbn_da_ricercaGoogle);
                    JSONArray arr = responseJson.getJSONArray("items");
                    JSONObject volumeInfo=arr.getJSONObject(0).getJSONObject("volumeInfo");
                    titolo= volumeInfo.getString("title");
                    txtview_titolo.setText(titolo);
                    if(volumeInfo.toString().contains("authors")) {
                        JSONArray autori = volumeInfo.getJSONArray("authors");
                     //   autore = autori.toString();
                        if (autori.length() == 1) {
                            autore = autori.get(0).toString();
                        } else {
                            for (int i = 0; i <= autori.length() - 1; i++) {
                                autore = autore + " " + autori.get(i).toString();
                            }

                        }
                    }
                    txtview_autore.setText(autore);
                    if(volumeInfo.toString().contains("description")) {
                        descrizione = volumeInfo.getString("description");


                    }

                txtview_descrizione.setText(descrizione);

                    if(volumeInfo.toString().contains("categories")) {

                        JSONArray generi = volumeInfo.getJSONArray("categories");
                        //   autore = autori.toString();
                        if (generi.length() == 1) {
                            genere = generi.get(0).toString();
                        } else {
                            for (int i = 0; i <= generi.length() - 1; i++) {
                                genere = genere + " " + generi.get(i).toString();
                            }

                        }


                        //genere = volumeInfo.getString("categories");
                    }
                    txtview_genere.setText(genere);
                    if(volumeInfo.toString().contains("imageLinks")) {
                        image_link = volumeInfo.getJSONObject("imageLinks").getString("smallThumbnail");
                        // txtview_genere.setText(image_link);
                        URL url = new URL(image_link);
                        Glide.with(getActivity())
                                .load(url)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(imageView);
                    }
                  //  Bitmap bitmap=BitmapFactory.decodeStream(url.openConnection() .getInputStream());
                   /*  imageView.setImageBitmap(bitmap);
*/
                }
                catch(JSONException e){
                    Log.e("inserisciBook", "Error parsing data "+e.toString());
                /*    results.clear();
                    mAdapter = new MyRecyclerViewAdapter_ElencoAssenze(results);
                    mRecyclerView.setAdapter(mAdapter);*/

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                 }
        }
    }

    protected boolean isNetworkConnected(){

        // Instantiate mConnectivityManager if necessary
        if(mConnectivityManager == null){
            mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        // Is device connected to the Internet?
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        } else {
            return false;
        }
    }
    private class InserisciInDB extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String stringaFinale = " ";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("title",params[0]));
            nameValuePairs.add(new BasicNameValuePair("author",params[1]));
            nameValuePairs.add(new BasicNameValuePair("subject",params[2]));
            nameValuePairs.add(new BasicNameValuePair("description",params[3]));
            nameValuePairs.add(new BasicNameValuePair("tumbnail",params[4]));
            nameValuePairs.add(new BasicNameValuePair("isbn",params[5]));

            Log.i("inserisciBook", "dati" + nameValuePairs.toString());
            InputStream is = null;

            //http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/inserisci_new_book.php");
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

                Log.i("inserisciBook", "risultato " + result);
                //System.out.println(result);

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
            Log.i("inserisciBook", "risultato "+result.toString());
            resultInsert=result;



        }
    }
}


