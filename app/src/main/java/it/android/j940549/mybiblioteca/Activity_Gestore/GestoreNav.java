package it.android.j940549.mybiblioteca.Activity_Gestore;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Switch;

import it.android.j940549.mybiblioteca.Model.Utente;
import it.android.j940549.mybiblioteca.R;

public class GestoreNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout mDrawer;
    FragmentManager fragmentManager;
    String qualeFragment="";
    String isbn_daRicercaGoogle="";
    private Utente gestoreLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestore_nav);
        qualeFragment="";

        if(savedInstanceState!=null){
            gestoreLogin= (Utente) savedInstanceState.getSerializable("gestore");
            qualeFragment="";
        }else {
            gestoreLogin=(Utente) getIntent().getSerializableExtra("gestore");
try {
    qualeFragment = getIntent().getExtras().getString("qualeFragment");
    if (qualeFragment.equals("InserisciBook")) {
        isbn_daRicercaGoogle = getIntent().getExtras().get("isbn").toString();

    }
}catch (Exception e){

}
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

        Fragment fragment =null;
                switch(qualeFragment){
                    case "InserisciBook":
                        fragment=InserisciBook.newInstance(isbn_daRicercaGoogle);
                        break;
                    case "Gestisci_catalogo":
                        fragment=Gestisci_Catalogo_frag.newInstance();
                        break;
                    case "Add_New_gestore":
                        fragment=Add_New_Gestore_frag.newInstance();
                        break;
                        default:
                            fragment=Gestione_Utenti_frag.newInstance();

                }

        //inserisci il fragment rimpiazzando i frgment esitente
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent_gestore, fragment).commit();

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
//        Fragment fragment= BlankFragment.newInstance(alunno,annosc);
        Class fragmentClass;



        switch (item.getItemId()) {

            case R.id.nav_inserisci_Gestore:
                fragment = Add_New_Gestore_frag.newInstance();

                break;
            case R.id.nav_gestione_catalogo:
                fragment = Gestisci_Catalogo_frag.newInstance();

                break;
            case R.id.nav_gestione_utenti:
                fragment = Gestione_Utenti_frag.newInstance();

                break;

            case R.id.nav_inserisci:
                fragment = InserisciBook.newInstance("");

                break;



            default:
                fragment =   Gestione_Utenti_frag.newInstance();

                break;
        }

        //inserisci il fragment rimpiazzando i frgment esitente
       // FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent_gestore, fragment).commit();

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
        //getMenuInflater().inflate(R.menu.gestore_nav, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectDrawerItem(item);

        return true;
    }
}
