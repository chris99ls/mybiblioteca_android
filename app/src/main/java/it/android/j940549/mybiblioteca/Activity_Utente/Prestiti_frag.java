package it.android.j940549.mybiblioteca.Activity_Utente;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti.Fragment_Gia_Letti;
import it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti.Fragment_In_Prestito;
import it.android.j940549.mybiblioteca.Activity_Utente.fragment_prestiti.Fragment_Prenotati;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;


public class Prestiti_frag extends Fragment {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Utente utenteLogin =null;

    public Prestiti_frag(){

    }
    public static Prestiti_frag newInstance(Utente utenteLogin) {
        Prestiti_frag fragment = new Prestiti_frag();
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Prestiti");
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
                    fragment.setArguments(args);

                    break;
                }
                case 1: {
                    fragment= new Fragment_In_Prestito();
                    fragment.setArguments(args);

                    break;

                }
                case 2: {
                    fragment= new Fragment_Gia_Letti();
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

}