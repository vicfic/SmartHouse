package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.NavigationBarAdapter;
import com.udc.muei.apm.apm_smarthouse.fragments.Favoritos;
import com.udc.muei.apm.apm_smarthouse.fragments.Lugares;
import com.udc.muei.apm.apm_smarthouse.fragments.Rutinas;
import com.udc.muei.apm.apm_smarthouse.model.NavigationOption;
import com.udc.muei.apm.apm_smarthouse.model.TipoNavigationOption;

import java.util.ArrayList;
import java.util.List;


public class MenuPrincipal extends AppCompatActivity {

    //Properties needed for the navigation bar
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    //Buttons needed for select current fragment
    Button fragmentPlaces;
    Button fragmentFav;
    Button fragmentRut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        /* Toolbar de la actividad */
        Toolbar toolbarRoutine = (Toolbar) findViewById(R.id.menu_principal_toolbar);
        TextView titulo_toolbar = findViewById(R.id.toolbar_menu_princiapl_titulo);
        toolbarRoutine.setTitle("");

        titulo_toolbar.setText(getString(R.string.toolbar_menu_principal_name));
        toolbarRoutine.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbarRoutine);

        configureNavigationMenu(savedInstanceState);

        fragmentFav = (Button) findViewById(R.id.fragmentFav);
        fragmentFav.setText("Favoritos");
        fragmentFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Create new fragment and transaction
                Favoritos newFragment = new Favoritos();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).addToBackStack(null).commit();
            }
        });

        fragmentPlaces = (Button) findViewById(R.id.fragmentPlaces);
        fragmentPlaces.setText("Lugares");
        fragmentPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Create new fragment and transaction
                Lugares newFragment = new Lugares();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).addToBackStack(null).commit();
            }
        });

        fragmentRut = (Button) findViewById(R.id.fragmentRut);
        fragmentRut.setText("Rutinas");
        fragmentRut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Create new fragment and transaction
                Rutinas newFragment = new Rutinas();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).addToBackStack(null).commit();
            }
        });

        //Configure initial fragment
        if(findViewById(R.id.fragment_container) != null){
            if(savedInstanceState!= null){
                return;
            }
            Favoritos firstFragment = new Favoritos();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }

    }

    private void configureNavigationMenu(Bundle savedInstanceState){

        mTitle = mDrawerTitle = "Menú principal";

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        List<NavigationOption> navigations = new ArrayList<NavigationOption>();
        navigations.add(new NavigationOption("Imagen", TipoNavigationOption.IMAGEN));
        navigations.add(new NavigationOption("Configuración servidor", TipoNavigationOption.TEXTO));
        navigations.add(new NavigationOption("Usuarios", TipoNavigationOption.TEXTO));
        navigations.add(new NavigationOption("Logout", TipoNavigationOption.TEXTO));

        NavigationBarAdapter adapter = new NavigationBarAdapter(this, navigations);
        // Set the adapter for the list view
        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.favorite_heart).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.favorite_heart:
                Favoritos newFragment = new Favoritos();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFragment).addToBackStack(null).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(view, position);
        }
    }

    private void selectItem(View v, int position) {
        switch(position){
            case 1 :
                //Configuracion servidor
                Intent i = new Intent(v.getContext(),ConfiguracionServidor.class);
                startActivity(i);
                break;
            case 2 :
                //Usuarios
                Intent i2 = new Intent(v.getContext(),Usuarios.class);
                startActivity(i2);
                break;
            case 3 :
                //Usuarios
                Intent i3 = new Intent(v.getContext(),LoginActivity.class);
                startActivity(i3);
                break;
        }
    }
}
