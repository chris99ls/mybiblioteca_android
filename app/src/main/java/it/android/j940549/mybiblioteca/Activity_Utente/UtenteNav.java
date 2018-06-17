package it.android.j940549.mybiblioteca.Activity_Utente;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import it.android.j940549.mybiblioteca.Catalogo_libri.Catalogo;
import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;

public class UtenteNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawer;
    private Utente utenteLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utente_nav);

        if(savedInstanceState!=null){
            utenteLogin= (Utente) savedInstanceState.getSerializable("utente");
        }else {
            utenteLogin=(Utente) getIntent().getSerializableExtra("utente");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Fragment fragment;
        fragment= Catalogo.newInstance(utenteLogin);

        //inserisci il fragment rimpiazzando i frgment esitente
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent_utente, fragment).commit();

    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });

    }

    public void selectDrawerItem(MenuItem item) {

        Fragment fragment=null;



            switch (item.getItemId()) {

                case R.id.nav_catalogo:
                    fragment = Catalogo.newInstance(utenteLogin);

                    break;

                case R.id.nav_ricerca:
                    fragment = Ricerca_frag.newInstance(utenteLogin);

                    break;


                case R.id.nav_prestiti:

                    fragment = Prestiti_frag.newInstance(utenteLogin);
                    break;

                case R.id.nav_profilo:

                    fragment = Profilo_frag.newInstance(utenteLogin);
                    break;




                default:
                    fragment =   Ricerca_frag.newInstance(utenteLogin);

                    break;
            }

            //inserisci il fragment rimpiazzando i frgment esitente
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent_utente, fragment).commit();

        //evidenzio  l'item che è stato selezionato nel Navigationview
        item.setChecked(true);

        //imposto il titolo dellìaction bar
        setTitle(item.getTitle());
        //chiudo il navigationdrawer
        mDrawer.closeDrawers();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
   //         mDrawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.utente_nav, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectDrawerItem(item);

        return true;
    }
}
