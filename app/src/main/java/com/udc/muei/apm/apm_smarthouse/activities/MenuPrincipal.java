package com.udc.muei.apm.apm_smarthouse.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.udc.muei.apm.apm_smarthouse.AsyncTasks.HttpsRequestAsyncTask;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.PagerAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;

import de.hdodenhof.circleimageview.CircleImageView;


public class MenuPrincipal extends AppCompatActivity {


    private static final String LOGIN_TAG = "LOGIN_TAG";

    //Requests codes
    private static final Integer REQUEST_CONFIG_SERV = 9563;
    private static final Integer REQUEST_LOGIN = 9564;
    //Properties needed for the navigation bar
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private TextView textView;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;

    //Info de cuanta logueada
    private String nombre ;
    private String email;
    private String photo_url;
    private String token;

    //Datos de usuario logeado


    //Buttons needed for select current fragment
    Button fragmentPlaces;
    Button fragmentFav;
    Button fragmentRut;
    GoogleSignInClient mGoogleSignInClient;
    ViewPager simpleViewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        toolbar = (Toolbar) findViewById(R.id.menu_principal_toolbar);
        textView = findViewById(R.id.toolbar_menu_princiapl_titulo);
        toolbar.setTitle("");


        /* Configuracion Toolbar y Barra de navegación*/
        textView.setText(getString(R.string.toolbar_menu_principal_name));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }

        setupNavigationDrawerContent(navigationView);


        /* Configuración parte de fragmentos*/
        simpleViewPager = (ViewPager) findViewById(R.id.visor_fragmentos);
        tabLayout = (TabLayout) findViewById(R.id.selector_fragmento);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), this);

        for(int i=0; i<adapter.getNombresFragmentos().size();i++){
            if (i==0)
                toolbar.setTitle(adapter.getNombresFragmentos().get(i));
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setText(adapter.getNombresFragmentos().get(i));
            tabLayout.addTab(tab);
        }

        simpleViewPager.setAdapter(adapter);

        simpleViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                toolbar.setTitle(tab.getText());
                simpleViewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /* Creación del cliente para hacer login con Google*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    /* Se comprueba si hay algún usuario logueado previamente o no*/
    @Override
    protected void onStart() {
        super.onStart();
        /* Comprobación de configuración de servidor */
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
        String ip_serv = sharedPref.getString(getString(R.string.key_shared_IP), getResources().getString(R.string.default_value_IP));
        String port_serv = sharedPref.getString(getString(R.string.key_shared_port), getResources().getString(R.string.default_value_port));
        if ((ip_serv.equals(getResources().getString(R.string.default_value_IP)))||
                (port_serv.equals(getResources().getString(R.string.default_value_port)))){
            Log.d(LOGIN_TAG, "No hai configuración de servidor previa");
            Intent intent = new Intent(this,ConfiguracionServidor.class);
            startActivityForResult(intent, REQUEST_CONFIG_SERV);
        }else{
            Log.d(LOGIN_TAG, "Configuración Servidor:\n\t-IP: "+ip_serv+"\n\t-Puerto: "+port_serv);
        }
        /* Comprobación de usuario logueado */
        mGoogleSignInClient.silentSignIn()
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                Log.d(LOGIN_TAG, "No hai autenticacion previa");
                handleNonSignIn();
                }
            })
            .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                Log.d(LOGIN_TAG, "Hai autenticacion previa");
                handleSignInResult(task);
                }
            });
    }

    /* Se lanza cuando no hay un usuario logueado. Lanza la actividad de login */
    private void handleNonSignIn() {
        Intent intent = new Intent(this, GoogleLogin.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    /* Se lanza cuando hay un usuario previamente logueado */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // Capturamos la info de la cuenta de google
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            nombre = account.getDisplayName();
            email = account.getEmail();
            photo_url = account.getPhotoUrl().toString();
            token = account.getIdToken();

            /* Ejemplo de petición http al servidor, no tiene porque ir aqui*/
            /*HttpsRequestAsyncTask task = new HttpsRequestAsyncTask(this, new HttpsRequestResult() {
                @Override
                public void processFinish(String result) {
                }
            });
            task.execute(account.getIdToken());*/

        } catch (ApiException e) {
            Log.w(LOGIN_TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /* Selector de cada una de las opciones de la barra de navegación */
    private void setupNavigationDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_navigation_usuarios:
                                // Cargar el fragment de usuarios
                                drawerLayout.closeDrawer(GravityCompat.START);
                                simpleViewPager.setCurrentItem(3);
                                return true;
                            case R.id.item_navigation_favoritos:
                                // Cargar el fragment de favoritos
                                drawerLayout.closeDrawer(GravityCompat.START);
                                simpleViewPager.setCurrentItem(0);
                                return true;
                            case R.id.item_navigation_lugares:
                                // Cargar el fragment de lugares
                                drawerLayout.closeDrawer(GravityCompat.START);
                                simpleViewPager.setCurrentItem(1);
                                return true;
                            case R.id.item_navigation_rutinas:
                                // Cargar el fragment de rutinas
                                drawerLayout.closeDrawer(GravityCompat.START);
                                simpleViewPager.setCurrentItem(2);
                                return true;
                            case R.id.item_navigation_config_serv:
                                // Opción de configuración del servidor
                                Intent intent = new Intent(navigationView.getContext(),ConfiguracionServidor.class);
                                startActivityForResult(intent, REQUEST_CONFIG_SERV);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_logout:
                                // Opción de logOut
                                signOut();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                        }
                        return true;
                    }
                });
    }

    /* Función que crea el Menu de opciones. En este caso solo hay un item, el de favoritos. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Se lanza cuando se pulsa el botón de la barra de navegación.
     * En ella se establecen la imformación del usuario logueado  */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    /* Se lanza cuando se pulsa una de las opciones de la toolbar (nav, update, fav) */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.favorite_heart:
                simpleViewPager.setCurrentItem(0);
                return true;
            case R.id.refresh:
                Toast.makeText(getApplicationContext(),"Refrescar información del fragmento "+simpleViewPager.getCurrentItem() , Toast.LENGTH_LONG).show();
                return true;
            case android.R.id.home:
                iniciarNav();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Tratamiento de las respuestas recibidad de las actividades lanzadas */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CONFIG_SERV){
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), getString(R.string.new_config_serv), Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == REQUEST_LOGIN){
            if (resultCode == Activity.RESULT_OK){
                Toast.makeText(getApplicationContext(), getString(R.string.login_ok), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* Función que realiza el proceso de LOGOUT con Google */
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Mensaje de info "Desconectado"
                        Toast.makeText(getApplicationContext(), getString(R.string.logout_toast), Toast.LENGTH_SHORT).show();
                        //Borrado de la información guardada globalmente
                        nombre = null;
                        email = null;
                        photo_url = null;
                        token = null;
                        resetearConfigServ();            //Opcional para debug. despues sacarlo
                        //Inicio de pantalla de login
                        handleNonSignIn();
                    }
                });
    }

    private  void resetearConfigServ(){
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.key_shared_IP), getString(R.string.default_value_IP));
        editor.putString(getString(R.string.key_shared_port), getString(R.string.default_value_port));
        editor.commit();
    }

    /* Actualización del UI en función del usuario conectado*/
    private void iniciarNav(){
        // Se establece el nombre del usuario logueado
        TextView name_signin = findViewById(R.id.name_signin);
        if (nombre != null)
            name_signin.setText(nombre);

        // Se establece el email del usuario logueado
        TextView email_signin = findViewById(R.id.email_signin);
        if (email!= null)
            email_signin.setText(email);

        // Se carga la imagen del usuario logueado
        CircleImageView imageViewPerson = findViewById(R.id.login_image);
        if (photo_url!=null) {
            Glide.with(getApplicationContext()).load(photo_url)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPerson);
        }
        drawerLayout.openDrawer(GravityCompat.START);
    }




































    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    /*@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }*/

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }*/

    /* The click listner for ListView in the navigation drawer */
    /*private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(view, position);
        }
    }*/

    /*private void selectItem(View v, int position) {
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
                //LogOut
                signOut();
                break;
        }
    }*/




        /*private void configureNavigationMenu(Bundle savedInstanceState){

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
                this,                  // host Activity
                mDrawerLayout,         // DrawerLayout object
                R.drawable.ic_drawer,  // nav drawer image to replace 'Up' caret
                R.string.drawer_open,  // "open drawer" description for accessibility
                R.string.drawer_close  // "close drawer" description for accessibility
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

    }*/
}
