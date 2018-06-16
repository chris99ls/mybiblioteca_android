package it.android.j940549.mybiblioteca.Activity_Gestore;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.android.j940549.mybiblioteca.Activity_Gestore.fragment_situazione_prestiti.Fragment_Situazione_In_Prestito;
import it.android.j940549.mybiblioteca.Activity_Gestore.fragment_situazione_prestiti.Fragment_Situazione_Prenotati;
import it.android.j940549.mybiblioteca.R;


public class Situazione_Prestiti extends Fragment {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
  //  private String utente="UtenteXX";

    public Situazione_Prestiti(){

    }
    public static Situazione_Prestiti newInstance() {
        Situazione_Prestiti fragment = new Situazione_Prestiti();
        /*Bundle args = new Bundle();
        args.putString("utente", utente);

        fragment.setArguments(args);*/

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            this.utente = getArguments().getString("utente");

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_situazione_prestiti,container,false);



    //    TextView nomealunno= (TextView) view.findViewById(R.id.nomeutente_prestiti);
      //  if(utente.equals("Christian")){}
           // nomealunno.setText(getResources().getString(R.string.alunnoChris).toUpperCase());}


        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.container_prestiti);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // specify an adapter (see also next example)


        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs_prestiti);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private String unutente;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            this.unutente=unutente;

        }

        @Override
        public Fragment getItem(int position) {
            /*Bundle args = new Bundle();

            args.putString("utente", unutente);*/

            //Fragment fragment = null;
            switch (position) {
                case 0: {
                    Fragment_Situazione_Prenotati fragment_situazione_prenotati = new Fragment_Situazione_Prenotati();
                    //fragment_situazione_prenotati.setArguments(args);

                    return fragment_situazione_prenotati;
                    //break;
                }
                case 1: {
                    Fragment_Situazione_In_Prestito fragment_situazione_in_prestito = new Fragment_Situazione_In_Prestito();
                    //fragment_situazione_in_prestito.setArguments(args);

                    return fragment_situazione_in_prestito;
                    //break;

                }

                default:
                    Fragment fragment=null;
                    return fragment;
            }


        }
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Libri Prenotati";
                case 1:
                    return "Libri In prestito";

            }
            return null;
        }
    }

}