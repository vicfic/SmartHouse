package com.udc.muei.apm.apm_smarthouse.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.udc.muei.apm.apm_smarthouse.BuildConfig;
import com.udc.muei.apm.apm_smarthouse.R;

import com.udc.muei.apm.apm_smarthouse.broadcastReceivers.GeofenceBroadcastReceiver;
import com.udc.muei.apm.apm_smarthouse.util.Constants;
import com.udc.muei.apm.apm_smarthouse.util.GeofenceErrorMessages;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * REVISADA: José Manuel González on 12/06/2018.
 */

public class ComprobacionGPS extends AppCompatActivity implements OnCompleteListener<Void>, OnMapReadyCallback {

    private static final String TAG = ComprobacionGPS.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;


    //Operaciones disponibles
    private enum PendingGeofenceTask {
        ADD, REMOVE, RESET, NONE
    }

    private GeofencingClient mGeofencingClient;
    private FusedLocationProviderClient mFusedLocationClient;

    private ArrayList<Geofence> mGeofenceList;

    private PendingIntent mGeofencePendingIntent;

    private SeekBar seekBar_calefaccion;
    private EditText editText_radio;
    private CheckBox checkBox_auto_calefaccion;


    private Location mLastKnownLocation = null;
    private CameraPosition mCameraPosition = null;
    private static final float DEFAULT_ZOOM = 11f;



    private HashMap<LatLng, Circle> circleHashMap = new HashMap<LatLng, Circle>();
    MapFragment mapFragment;

    GoogleMap mapGoogle;

    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprobacion_gps);


        /**************** Esta información se recibirá a través del servidor***********************/
        final LatLng positionCasa = new LatLng(43.3555288930694543, -8.408066779375076);
        //Despues de hacer la petición HTTPS al servidor es necesario guardar la ubitación en PREFS
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putFloat(Constants.LOCATION_LAT_HOME_KEY, (float)positionCasa.latitude)
                .putFloat(Constants.LOCATION_LNG_HOME_KEY, (float)positionCasa.longitude)
                .apply();
        /******************************************************************************************/



        Toolbar toolbar = (Toolbar) findViewById(R.id.comprobacion_gps_toolbar);

        toolbar.setTitle(getString(R.string.titulo_rutina_calefaccion));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapa_google);
        mapFragment.getMapAsync(this);
        mapFragment.onResume();

        /** Configuración del seekbar */
        seekBar_calefaccion = findViewById(R.id.seekBar_calefaccion);
        seekBar_calefaccion.setMax((int)Constants.MAX_RADIUS_CALEFACCION);
        seekBar_calefaccion.setProgress((int)getSavedRadiusCircle());

        /** Configuracion editText */
        editText_radio = findViewById(R.id.editText_radio);
        editText_radio.setText((int)getSavedRadiusCircle()+"");


        /** Configuración del checkbox de opción automática*/
        checkBox_auto_calefaccion = findViewById(R.id.checkBox_auto_calefaccion);
        Boolean auto_calefaccion = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.OPTION_AUTO_CALEFACCION_KEY, false);
        checkBox_auto_calefaccion.setChecked(auto_calefaccion);
        checkBox_auto_calefaccion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putBoolean(Constants.OPTION_AUTO_CALEFACCION_KEY, isChecked)
                        .apply();
            }
        });
        mGeofenceList = new ArrayList<>();

        mGeofencePendingIntent = null;

        setButtonsEnabledState();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mGeofencingClient = LocationServices.getGeofencingClient(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions(); // Petición de permisos si estos no están autorizados
        } else {
            performPendingGeofenceTask();  //Task NONE, no se realiza ninguna task
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapFragment.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapFragment.onLowMemory();
    }

    /**
     * En esta función se deshabilitará parte de los controles si la rutina no está activada, y
     * se habilitará si está activada
     */
    private void setButtonsEnabledState() {
        if (getGeofencesAdded()) {
            //Activar zona inferior
        } else {
            //Desactivar zona inferior
        }
    }

    /**
     * Esta función crea una lista de geofences, en este caso de un solo elemento (la casa). Para
     * conseguir la posición de la casa, sería necesario realizar una petición de dicha posición
     * al servidor cuando se inicie o se pare la rutina
     */
    private void populateGeofenceList() {

        float radius = ((getSavedRadiusCircle()<1.0)? (float)1.0 : getSavedRadiusCircle());

        mGeofenceList = new ArrayList<>();
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(Constants.GEOFENCE_ID_STRING)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        getSavedHomePosition().latitude,
                        getSavedHomePosition().longitude,
                        radius
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build());
        Log.d(TAG, "Geofence "+Constants.GEOFENCE_ID_STRING+" raidus: "+radius+" meters.");
    }

    /**
     * Crea un switch en el toolbar y controla el servicio de comprobación de localizacion
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rutina_calefaccion, menu);

        final MenuItem toggleservice = menu.findItem(R.id.switchRutina);
        final Switch switchRutina = (Switch) toggleservice.getActionView();

        if (getGeofencesAdded()){
            Log.d(TAG, "Geofences Added");
            switchRutina.setChecked(true);
        }else{
            Log.d(TAG, "Geofences no añadidos");
            switchRutina.setChecked(false);
        }

        switchRutina.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    addGeofencesButtonHandler(buttonView);
                }else{
                    removeGeofencesButtonHandler(buttonView);
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handler para el botón "atrás"
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Función que enfoca la posición de la casa
     * @param home coordenadas de la casa
     */
    private void focusHome(LatLng home){
        if (mCameraPosition != null) {
            mapGoogle.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (home != null) {
            //Situamos la cámara en la posición de la casa
            mapGoogle.moveCamera(CameraUpdateFactory.newLatLngZoom( home, DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        requestPermissions();
        if (checkPermissions()) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null) {
                        mLastKnownLocation = location;
                    }
                }
            });
        }
    }

    private LatLng getSavedHomePosition(){
        float latitud = PreferenceManager.getDefaultSharedPreferences(this).getFloat(Constants.LOCATION_LAT_HOME_KEY, 0);
        float longitud = PreferenceManager.getDefaultSharedPreferences(this).getFloat(Constants.LOCATION_LNG_HOME_KEY, 0);
        final LatLng positionHome = new LatLng(latitud, longitud);
        return positionHome;
    }

    private float getSavedRadiusCircle(){
        float radius = PreferenceManager.getDefaultSharedPreferences(this).getFloat(Constants.RADIUS_KEY, Constants.DEFAULT_RADIUS_CALEFACCION);
        return radius;
    }

    /**
     * Función requerida para implementar el mapa de Google
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapGoogle = googleMap;

        try {
            if (!checkPermissions()){
                mapGoogle.setMyLocationEnabled(false);
                mapGoogle.getUiSettings().setMyLocationButtonEnabled(false);
            }else{
                getDeviceLocation();
                mapGoogle.setMyLocationEnabled(true);
                mapGoogle.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }catch (SecurityException e){
            Log.d(TAG," No están autorizados los permisos necesario para determinar la posición actual");
        }


        googleMap.addMarker(new MarkerOptions()
                .position(getSavedHomePosition())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_automation_black_24dp))
                .title(getString(R.string.casa)));

        float progress_saved = PreferenceManager.getDefaultSharedPreferences(this).getFloat(
                Constants.RADIUS_KEY, Constants.DEFAULT_RADIUS_CALEFACCION);

        Circle circle = mapGoogle.addCircle(new CircleOptions()
                                        .center(getSavedHomePosition())
                                        .radius(progress_saved)
                                        .fillColor(getColor(R.color.naranja_claro_transparente))
                                        .strokeColor(getColor(R.color.naranja_oscuro_transparente)));

        circleHashMap.put(getSavedHomePosition(), circle);

        seekBar_calefaccion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Circle c = circleHashMap.get(getSavedHomePosition());
                c.setRadius(progress);
                editText_radio.setText(progress+"");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, (float)circleHashMap.get(getSavedHomePosition()).getRadius()+"");
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putFloat(Constants.RADIUS_KEY, (float)circleHashMap.get(getSavedHomePosition()).getRadius())
                        .apply();
                resetGeofencesButtonHandler();

            }
        });

        editText_radio.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (!event.isShiftPressed()) {
                        switch (v.getId()) {
                            case R.id.editText_radio:
                                if(seekBar_calefaccion.getProgress()!=Integer.parseInt(editText_radio.getText().toString())) {
                                    if (Integer.parseInt(editText_radio.getText().toString()) > Constants.MAX_RADIUS_CALEFACCION) {
                                        editText_radio.setText((Constants.MAX_RADIUS_CALEFACCION - 1) + "");
                                        seekBar_calefaccion.setProgress((int) Constants.MAX_RADIUS_CALEFACCION);
                                        Toast.makeText(getApplicationContext(), getString(R.string.error_max_value_radius) + " " + (int) Constants.MAX_RADIUS_CALEFACCION + ".", Toast.LENGTH_SHORT).show();
                                    } else
                                        seekBar_calefaccion.setProgress(Integer.parseInt(editText_radio.getText().toString()));
                                    Log.d(TAG, (float) circleHashMap.get(getSavedHomePosition()).getRadius() + "");
                                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                            .edit()
                                            .putFloat(Constants.RADIUS_KEY, (float) circleHashMap.get(getSavedHomePosition()).getRadius())
                                            .apply();
                                    resetGeofencesButtonHandler();
                                }
                                break;
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        mPendingGeofenceTask = PendingGeofenceTask.NONE;

        focusHome(getSavedHomePosition());
    }

    /**
     * Función para pedir al usuario que autorice ciertos permisos que necesita la app. En este
     * caso se requiere el acceso al permiso de localización
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(ComprobacionGPS.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Pidiendo permiso");
            ActivityCompat.requestPermissions(ComprobacionGPS.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Lee las preferencias y determina si el geofence ha sido añadido o no, por lo tando se
     * determina si la rutina está activada o no
     */
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
    }

    /**
     * Devuelve si el estado del permiso de localización está autorizado
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Se devulve un PendingIntent para enviar con las peticiones de añadir y eliminar Geofences.
     * El Location Service emite el intent dentro de este PendingIntent cada vez que se produzca una
     * transición para la lista de Geofences que se hayan añadidos.
     * @return El PendingIntent para el IntentSerivce que manejará las transiciones
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);

        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    /**
     * Se construye y se devuelve una GeofencingRequest. Esta especifica la lista de geofences (en
     * este caso solo una, la de la casa) que serán monitorizadas.
     * También se especifica los triggers que serán lanzados inicialmente.
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // INITIAL_TRIGGER_ENTER flag indica que el servicio de geofencing debería lanzar una
        // notificacion GEOFENCE_TRANSITION_ENTER  cuando el geofence es añadido y el dispositivo
        // está todavía dentro dl geofence
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Añadimos los geofences
        builder.addGeofences(mGeofenceList);

        // Devolvemos la GeofencingRequest.
        return builder.build();
    }

    /**
     * Función que muestra un mensaje cuando los permisos no son suficientes. Según el control que
     * ahora tenemos implementado, esta función no debería lanzarse nunca, se ha implementado como
     * mecanismo de control en última instancia.
     * @param text Texto del SnackBar
     */
    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Función encargada de iniciar la tarea para añadir el "geofence" de la casa. Esta función
     * debería ser llamada después de comprobar que se poseen los permisos de localización
     */
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {
        populateGeofenceList();
        if (!checkPermissions()) {
            // SnackBar, permisos insuficientes
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    /**
     * Función encargada de iniciar la tarea para eliminar el "geofence" de la casa. Esta función
     * debería ser llamada después de comprobar que se poseen los permisos de localización
     */
    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        if (!checkPermissions()) {
            // SnackBar, permisos insuficientes
            showSnackbar(getString(R.string.insufficient_permissions));
            return;
        }
        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    /**
     * Esta actividad se ejecutará cuando los permisos estén autorizados. Entonces se procederá
     * a ejecutar las tareas de añadir o eliminar el "Geofence" de la casa y actualizar el mapa
     * con la posición actual
     */
    @SuppressLint("MissingPermission")
    private void performPendingGeofenceTask() {
        //Esta función no se llama sin tener los permisos concedidos
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if ((mPendingGeofenceTask == PendingGeofenceTask.REMOVE)||(mPendingGeofenceTask == PendingGeofenceTask.RESET)) {
            removeGeofences();
        }

        //Proceso de actualización de mapa y obtención de posición actual
        if (mapGoogle!= null) {
            mapGoogle.setMyLocationEnabled(true);
            mapGoogle.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    /**
     * Callback para manejar la respuesta a la peticion de permisos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "Interacción con el usuario cancelada.");  // Respuesta interrumpida
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permiso autorizado.");  //Permiso autorizado
                performPendingGeofenceTask();
            } else {
                // Permiso denegado
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                mPendingGeofenceTask = PendingGeofenceTask.NONE;
            }
        }
    }

    /**
     * Se construye un snackbar alertando de que no están autorizados todos los permisos necesarios
     * @param mainTextStringId id del recurso string para el texto del SnackBar.
     * @param actionStringId   id del recurso string para la acción.
     * @param listener         Listener asociado a la acción llevaba a cabo en el SnackBar
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId, View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Se ejecuta cuando se produce un resultado de llamar a {@link #addGeofences()} o
     * {@link #removeGeofences()}
     * @param task tarea que se ha llevado a cabo (ADD, REMOVE, NONE)
     */
    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (mPendingGeofenceTask == PendingGeofenceTask.RESET) {
            mPendingGeofenceTask = PendingGeofenceTask.ADD;
            if (task.isSuccessful()) {
                updateGeofencesAdded(!getGeofencesAdded());  // Actualización del estado del "geofence"
                setButtonsEnabledState(); //Actualización de la UI
                performPendingGeofenceTask();  //Se vuelve a añadir el geofence
            } else {
                //La tarea no se ha ejecutado correctamente
                Log.w(TAG, GeofenceErrorMessages.getErrorString(this, task.getException()));
            }

        } else {
            mPendingGeofenceTask = PendingGeofenceTask.NONE;
            if (task.isSuccessful()) {
                updateGeofencesAdded(!getGeofencesAdded());  // Actualización del estado del "geofence"
                setButtonsEnabledState(); //Actualización de la UI

                int messageId = getGeofencesAdded() ?
                        R.string.rutina_calefaccion_activada :
                        R.string.rutina_calefaccion_desactivada;
                Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
            } else {
                //La tarea no se ha ejecutado correctamente
                Log.w(TAG, GeofenceErrorMessages.getErrorString(this, task.getException()));
            }
        }
    }

    /**
     * Se guarda si el "geofence" de la cas ha sido añadido o no al sistema de localizacion
     * @param added estado a actualizar.
     */
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    /**
     * Handler para el switch, iniciará el proceso para eliminar el "geofence" de la casa.
     * Si no se poseen los permisos necesarios, los solicitará y si ya los posee llamará
     * a removeGeofences()
     * @param view view del componente visual (no utilizado).
     */
    public void removeGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.REMOVE;
            requestPermissions();
            return;
        }
        removeGeofences();
    }

    /**
     * Handler para el switch, iniciará el proceso para añadir el "geofence" de la casa
     * Si no se poseen los permisos necesarios, los solicitará y si ya los posee llamará
     * a addGeofences()
     * @param view view del componente visual (no utilizado).
     */
    public void addGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = PendingGeofenceTask.ADD;
            requestPermissions();
            return;
        }
        addGeofences();
    }

    /**
     * Función que reseteará la rutina para actualizar el nuevo radio del geofencing. Sólo se
     * reseteará si la rutina estaba previamente iniciada. Si no no tendría sentido reiniciarla
     */
    public void resetGeofencesButtonHandler() {
        if (getGeofencesAdded()) {
            mPendingGeofenceTask = PendingGeofenceTask.RESET;
            if (!checkPermissions()) {
                requestPermissions();
                return;
            }
            performPendingGeofenceTask();
        }
    }
}
