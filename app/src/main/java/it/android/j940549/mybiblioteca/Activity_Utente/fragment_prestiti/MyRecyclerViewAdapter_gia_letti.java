package it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti;

/**
 * Created by J940549 on 30/12/2017.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import it.android.j940549.mybiblioteca.Model.Libri_Prenotati;
import it.android.j940549.mybiblioteca.Model.Libri_gia_letti;
import it.android.j940549.mybiblioteca.R;


public class MyRecyclerViewAdapter_gia_letti extends RecyclerView.Adapter<MyRecyclerViewAdapter_gia_letti.DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<Libri_gia_letti> mDataset;
    private Activity mActivity;

    public static class DataObjectHolder extends RecyclerView.ViewHolder{

        TextView isbn;
        TextView titolo;
        ImageView img;

        public DataObjectHolder(View itemView) {
            super(itemView);
            isbn= (TextView) itemView.findViewById(R.id.isbn_libro);
            titolo= (TextView) itemView.findViewById(R.id.titolo_libro);
            img=itemView.findViewById(R.id.copertina_libro);
            Log.i(LOG_TAG, "Adding Listener");
        }


    }


    public MyRecyclerViewAdapter_gia_letti(ArrayList<Libri_gia_letti> myDataset, Activity activity) {
        mDataset = myDataset;
        mActivity=activity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =null;
        if(mActivity.getTitle().equals("Prestiti")) {
                view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_libro_gia_letti, parent, false);
        }
        if(mActivity.getTitle().equals("Dettaglio_Utente")) {
            view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_libro_gia_letti, parent, false);
        }
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addItem(Libri_gia_letti dataObj, int index) {
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


}
