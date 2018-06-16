package it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti;

/**
 * Created by J940549 on 30/12/2017.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Gestore.GestoreNav;
import it.android.j940549.mybiblioteca.Model.Libri_In_Prestito;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;


public class MyRecyclerViewAdapter_in_prestito extends RecyclerView.Adapter<MyRecyclerViewAdapter_in_prestito.DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<Libri_In_Prestito> mDataset;
    private static Activity mActivity;
    private Utente utenteLogin;

    //    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder{
    //        implements View
  //          .OnClickListener {
        TextView isbn;{}
        TextView titolo,dal;
        ImageView img;
        ImageButton btn;

        public DataObjectHolder(View itemView) {
            super(itemView);
            isbn = (TextView) itemView.findViewById(R.id.isbn_libro);
            titolo = (TextView) itemView.findViewById(R.id.titolo_libro);
            dal = (TextView) itemView.findViewById(R.id.data_prestito);
            img = itemView.findViewById(R.id.copertina_libro);
            if (mActivity.getTitle().equals("Dettaglio_Utente")) {
                btn = itemView.findViewById(R.id.btn_reso_prestito);

                Log.i(LOG_TAG, "Adding Listener");
                // itemView.setOnClickListener(this);
            }
        }
        /*@Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }*/
    }

    /*public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }*/

    public MyRecyclerViewAdapter_in_prestito(ArrayList<Libri_In_Prestito> myDataset,Activity activity, Utente utenteLogin) {
        mDataset = myDataset;
        mActivity=activity;
        this.utenteLogin=utenteLogin;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =null;
        if(mActivity.getTitle().equals("Prestiti")) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_libro_in_prestito, parent, false);
        }

        if(mActivity.getTitle().equals("Dettaglio_Utente")) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_libro_in_prestito_gestore, parent, false);
        }
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        final String isbn=mDataset.get(position).getIsbn();
        holder.isbn.setText(mDataset.get(position).getIsbn());
        holder.titolo.setText(mDataset.get(position).getTitolo());
        holder.dal.setText(mDataset.get(position).getData_prestito());

        URL url = null;
        Bitmap image=null;
        try {
            url = new URL(mDataset.get(position).getPatch_img());

            Glide.with(mActivity)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.img);

            //image= BitmapFactory.decodeStream(url.openStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.img.setImageBitmap(image);
    if(mActivity.getTitle().equals("Dettaglio_Utente")){
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyRecyclerViewAdapter_in_prestito.HttpGetTaskReso reso= new MyRecyclerViewAdapter_in_prestito.HttpGetTaskReso();
                reso.execute(utenteLogin.getNrtessera(),isbn);
            }

        });

    }
    }

    public void addItem(Libri_In_Prestito dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private class HttpGetTaskReso extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String stringaFinale = " ";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("nrtessera",params[0]));
            nameValuePairs.add(new BasicNameValuePair("isbn", params[1]));


            InputStream is = null;

            //http post
            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/restituisci_book.php");
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
            Log.i("log_tag", "result presta..."+result.toString());

            if(result.contains("successfully")){
                Intent refresh = new Intent(mActivity, GestoreNav.class);
                //refresh.putExtra("utente", utenteLogin);
                mActivity.startActivity(refresh);
                mActivity.finish();
                //              mAdapter = new MyRecyclerViewAdapter_gia_letti(getDataSet(),getActivity());
//                mRecyclerView.setAdapter(mAdapter);
            }

        }

    }

}
