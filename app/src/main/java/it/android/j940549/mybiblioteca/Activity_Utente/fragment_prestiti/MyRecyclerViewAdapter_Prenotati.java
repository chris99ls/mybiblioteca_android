package it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti;

/**
 * Created by J940549 on 30/12/2017.
 */
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Activity_Utente.UtenteNav;
import it.android.j940549.mybiblioteca.Model.Libri_Prenotati;
import it.android.j940549.mybiblioteca.Model.Libri_gia_letti;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;


public class MyRecyclerViewAdapter_Prenotati extends RecyclerView.Adapter<MyRecyclerViewAdapter_Prenotati.DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<Libri_Prenotati> mDataset;
    private Activity mActivity;
    private Utente utenteLogin;
//    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder{
    //        implements View
  //          .OnClickListener {
        TextView isbn;
        TextView titolo;
        ImageView img;
        ImageButton btn;

        public DataObjectHolder(View itemView) {
            super(itemView);
            isbn= (TextView) itemView.findViewById(R.id.isbn_libro);
            titolo= (TextView) itemView.findViewById(R.id.titolo_libro);
            img=itemView.findViewById(R.id.copertina_libro);
            btn=itemView.findViewById(R.id.btn_canc_prenotato);
            Log.i(LOG_TAG, "Adding Listener");
           // itemView.setOnClickListener(this);
        }

        /*@Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }*/
    }

    /*public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }*/

    public MyRecyclerViewAdapter_Prenotati(ArrayList<Libri_Prenotati> myDataset, Activity activity, Utente utenteLogin) {
        mDataset = myDataset;
        mActivity=activity;
        this.utenteLogin=utenteLogin;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_libro_prenotati, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        final String isbn=mDataset.get(position).getIsbn();
        holder.isbn.setText(mDataset.get(position).getIsbn());
        holder.titolo.setText(mDataset.get(position).getTitolo());

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
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(mActivity);
                alertDialog.setCancelable(false);
                alertDialog.setTitle("cancella prenotazione");
                alertDialog.setMessage("Vuoi Cancelare la Prenotazione?");
                alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        MyRecyclerViewAdapter_Prenotati.HttpGetTaskCancella cancella= new MyRecyclerViewAdapter_Prenotati.HttpGetTaskCancella();
                        cancella.execute(utenteLogin.getNrtessera(),isbn);
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                alertDialog.create().show();
            }
        });
    }

    public void addItem(Libri_Prenotati dataObj, int index) {
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


    private class HttpGetTaskCancella extends AsyncTask<String,String,String> {

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
                HttpPost httppost = new HttpPost("http://lisiangelovpn.ddns.net/mybiblioteca/cancella_prenotazione.php");
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
            Log.i("log_tag", "parsing data on postExec giavisti"+result.toString());

            if(result.contains("successfully")){
                Intent refresh = new Intent(mActivity, UtenteNav.class);
                refresh.putExtra("utente", utenteLogin);
                mActivity.startActivity(refresh);
                mActivity.finish();
                //              mAdapter = new MyRecyclerViewAdapter_gia_letti(getDataSet(),getActivity());
//                mRecyclerView.setAdapter(mAdapter);
            }

        }

    }

}


