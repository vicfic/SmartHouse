package com.udc.muei.apm.apm_smarthouse.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.udc.muei.apm.apm_smarthouse.BuildConfig;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.activities.ComprobacionGPS;
import com.udc.muei.apm.apm_smarthouse.activities.ComprobacionWifi;
import com.udc.muei.apm.apm_smarthouse.activities.Luces;
import com.udc.muei.apm.apm_smarthouse.adapters.RoutineAdapter;
import com.udc.muei.apm.apm_smarthouse.broadcastReceivers.GeofenceBroadcastReceiver;
import com.udc.muei.apm.apm_smarthouse.interfaces.RoutineListClicksListeners;
import com.udc.muei.apm.apm_smarthouse.interfaces.RutineHandlerONOFF;
import com.udc.muei.apm.apm_smarthouse.model.Routine;
import com.udc.muei.apm.apm_smarthouse.util.Constants;
import com.udc.muei.apm.apm_smarthouse.util.GeofenceErrorMessages;

import java.util.ArrayList;

public class Rutinas extends android.support.v4.app.ListFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = Rutinas.class.getSimpleName();

    private static RoutineAdapter routineAdapter;
    private ArrayList<Routine> routineArray;


    /************************** Variables para la rutina de calefaccion****************************/
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    //Operaciones disponibles
    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;

    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
    /**********************************************************************************************/



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        /* Lista de rutinas */
        routineArray= new ArrayList<>();


        /******************************************************************************************/
        /* Esta información está introducida manualmente de momento, a posterior, estos datos serán
        enviados por el servidor */
        routineArray.add(new Routine("Luces", true, new Intent(getContext(), Luces.class)));
        routineArray.add(new Routine("Calefacción", PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false), new Intent(getContext(), ComprobacionGPS.class), new RutineHandlerONOFF() {
            @Override
            public void onSwitchClick(boolean isChecked) {
                rutineCalefaccionONOFF(isChecked);
            }
        }));
        routineArray.add(new Routine("Televisor", false, new Intent(getContext(), ComprobacionWifi.class)));
        routineArray.add(new Routine("Desumificador", true));
        /******************************************************************************************/

        routineAdapter= new RoutineAdapter(routineArray, getContext(), new RoutineListClicksListeners() {
            @Override
            public void onSettingClick(int position) {
                Routine routine = (Routine) getListView().getItemAtPosition(position);
                Toast.makeText(getContext(),"Rutina "+ routine.getName() +". Botón configuración pulsado" , Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Rutina "+ routine.getName() +". Botón configuración pulsado");
                if(routine.getIntent()!=null)
                    startActivity(routine.getIntent());
            }

            @Override
            public void onSwitchClick(int position, boolean isChecked) {
                Vibrator vibe = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
                Routine routine = (Routine) getListView().getItemAtPosition(position);
                if (routine.getState() != isChecked) {
                    routine.setState(isChecked);
                    if (routine.getRutineHandlerONOFF()==null) {
                        Toast.makeText(getContext(), "Rutina " + routine.getName() + " " + (routine.getState() ? "activada" : "desactivada"), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Rutina " + routine.getName() + " " + (routine.getState() ? "activada" : "desactivada"));
                    }else{
                        routine.getRutineHandlerONOFF().onSwitchClick(isChecked);
                    }
                }
            }
        });
        setListAdapter(routineAdapter);
    }

    /********************** FUNCIONES ON/OFF rutina calefaccion ***********************************/

    private void rutineCalefaccionONOFF(Boolean  isChecked){
        Log.d(TAG, isChecked?"activado":"desactivado");

        /**************** Esta información se recibirá a través del servidor***********************/
        final LatLng positionCasa = new LatLng(43.3555288930694543, -8.408066779375076);


        //Despues de hacer la petición HTTPS al servidor es necesario guardar la ubitación en PREFS
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .edit()
                .putFloat(Constants.LOCATION_LAT_HOME_KEY, (float)positionCasa.latitude)
                .putFloat(Constants.LOCATION_LNG_HOME_KEY, (float)positionCasa.longitude)
                .apply();
        /******************************************************************************************/


        mGeofenceList = new ArrayList<>();
        mGeofencingClient = LocationServices.getGeofencingClient(getContext());
        mGeofencePendingIntent = null;

        if (!isChecked)
            removeGeofencesButtonHandler(null);
        else
            addGeofencesButtonHandler(null);
    }

    /**
     * Handler para el switch, iniciará el proceso para eliminar el "geofence" de la casa.
     * Si no se poseen los permisos necesarios, los solicitará y si ya los posee llamará
     * a removeGeofences()
     * @param view view del componente visual (no utilizado).
     */
    public void removeGeofencesButtonHandler(View view) {
        if (!checkPermissions()) {
            mPendingGeofenceTask = Rutinas.PendingGeofenceTask.REMOVE;
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
            mPendingGeofenceTask = Rutinas.PendingGeofenceTask.ADD;
            requestPermissions();
            return;
        }
        addGeofences();
    }

    /**
     * Devuelve si el estado del permiso de localización está autorizado
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Función para pedir al usuario que autorice ciertos permisos que necesita la app. En este
     * caso se requiere el acceso al permiso de localización
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Pidiendo permiso");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Función que muestra un mensaje cuando los permisos no son suficientes. Según el control que
     * ahora tenemos implementado, esta función no debería lanzarse nunca, se ha implementado como
     * mecanismo de control en última instancia.
     * @param text Texto del SnackBar
     */
    private void showSnackbar(final String text) {
        View container = getActivity().findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
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
                getActivity().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
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
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mPendingGeofenceTask = PendingGeofenceTask.NONE;
                    if (task.isSuccessful()) {
                        updateGeofencesAdded(!getGeofencesAdded());  // Actualización del estado del "geofence"

                        int messageId = getGeofencesAdded() ?
                                R.string.rutina_calefaccion_activada :
                                R.string.rutina_calefaccion_desactivada;
                        Toast.makeText(getContext(), getString(messageId), Toast.LENGTH_SHORT).show();
                    } else {
                        //La tarea no se ha ejecutado correctamente
                        Log.w(TAG, GeofenceErrorMessages.getErrorString(getContext(), task.getException()));
                    }
                }
            });
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
        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mPendingGeofenceTask = PendingGeofenceTask.NONE;
                if (task.isSuccessful()) {
                    updateGeofencesAdded(!getGeofencesAdded());  // Actualización del estado del "geofence"


                    int messageId = getGeofencesAdded() ?
                            R.string.rutina_calefaccion_activada :
                            R.string.rutina_calefaccion_desactivada;
                    Toast.makeText(getContext(), getString(messageId), Toast.LENGTH_SHORT).show();
                } else {
                    //La tarea no se ha ejecutado correctamente
                    Log.w(TAG, GeofenceErrorMessages.getErrorString(getContext(), task.getException()));
                }
            }
        });
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
        Intent intent = new Intent(getActivity(), GeofenceBroadcastReceiver.class);

        mGeofencePendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
     * Esta función crea una lista de geofences, es este caso de un solo elemento (la casa). Para
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

    private LatLng getSavedHomePosition(){
        float latitud = PreferenceManager.getDefaultSharedPreferences(getActivity()).getFloat(Constants.LOCATION_LAT_HOME_KEY, 0);
        float longitud = PreferenceManager.getDefaultSharedPreferences(getActivity()).getFloat(Constants.LOCATION_LNG_HOME_KEY, 0);
        final LatLng positionHome = new LatLng(latitud, longitud);
        return positionHome;
    }

    private float getSavedRadiusCircle(){
        float radius = PreferenceManager.getDefaultSharedPreferences(getActivity()).getFloat(Constants.RADIUS_KEY, 0);
        return radius;
    }

    /**
     * Se guarda si el "geofence" de la cas ha sido añadido o no al sistema de localizacion
     * @param added estado a actualizar.
     */
    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .edit()
                .putBoolean(Constants.GEOFENCES_ADDED_KEY, added)
                .apply();
    }

    /**
     * Lee las preferencias y determina si el geofence ha sido añadido o no, por lo tando se
     * determina si la rutina está activada o no
     */
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(
                Constants.GEOFENCES_ADDED_KEY, false);
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
     * Esta actividad se ejecutará cuando los permisos estén autorizados. Entonces se procederá
     * a ejecutar las tareas de añadir o eliminar el "Geofence" de la casa y actualizar el mapa
     * con la posición actual
     */
    @SuppressLint("MissingPermission")
    private void performPendingGeofenceTask() {
        //Esta función no se llama sin tener los permisos concedidos
        if (mPendingGeofenceTask == PendingGeofenceTask.ADD) {
            addGeofences();
        } else if (mPendingGeofenceTask == PendingGeofenceTask.REMOVE) {
            removeGeofences();
        }
    }
    /***************** FIN  FUNCIONES ON/OFF rutina calefaccion ***********************************/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
