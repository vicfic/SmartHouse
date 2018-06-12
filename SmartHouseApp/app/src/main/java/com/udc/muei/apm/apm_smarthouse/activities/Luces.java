package com.udc.muei.apm.apm_smarthouse.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.AsyncTasks.HttpsRequestAsyncTask;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.Services.BoundService;
import com.udc.muei.apm.apm_smarthouse.adapters.BeaconAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;
import com.udc.muei.apm.apm_smarthouse.model.BeaconCustom;
import com.udc.muei.apm.apm_smarthouse.util.BeaconsBDHandler;
import com.udc.muei.apm.apm_smarthouse.util.Constants;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.udc.muei.apm.apm_smarthouse.Services.BoundService.KEY_RCV_MSG;
import static com.udc.muei.apm.apm_smarthouse.util.Constants.MAP_PETICIONES;


/**
 * REVISADA: José Manuel González on 12/06/2018.
 */

public class Luces extends AppCompatActivity{

    private static final String LUCES_TAG = "ACTIVIDAD_LUCES";
    public static final String MSG_KEY = "hola mundo";
    private static final int REQUEST_PERMISION = 1;

    /** Messenger for communicating with the service. */
    Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luces);


        Toolbar toolbar = findViewById(R.id.luces_toolbar);

        toolbar.setTitle("Rutina Luces");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    public void cargarBeacons(){
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.FLAG_NOTIFICATION_LIGTHS, -1);
        editor.apply();

        JSONObject parametrosPeticion = new JSONObject();

        final LoadToast lt = new LoadToast(this);

        try {
            lt.show();
            lt.setText("Cargando Beacons");
            lt.setTranslationY(1400);
            lt.setTextColor(getResources().getColor(R.color.naranja_oscuro)).setProgressColor(getResources().getColor(R.color.naranja_claro));

            parametrosPeticion.put("tokenId", sharedPref.getString(getString(R.string.key_token_id),""));
            HttpsRequestAsyncTask task = new HttpsRequestAsyncTask(this, new HttpsRequestResult() {
                @Override
                public void processFinish(String result) {

                    try {
                        Log.d(LUCES_TAG, result);
                        JSONObject resultJSON = new JSONObject(result);

                        if (resultJSON.getBoolean("error")){
                            if (resultJSON.getBoolean("forbidden")){
                                //buttonView.setChecked(last_state);
                                Toast.makeText(getApplicationContext()," No tiene permiso para realizar la acción", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext()," Error durante la conexión", Toast.LENGTH_SHORT).show();
                            }
                            lt.error();
                            lt.hide();
                        }else{
                            //Recuperamos los beacons y los añadimos a una base de datos, Ademas iniciamos el servicio de comprobacion de beacons
                            Log.d(LUCES_TAG, result);
                            JSONArray beacons = resultJSON.getJSONArray("beacons");
                            ArrayList beaconArray = new ArrayList<>();
                            for (int i = 0; i < beacons.length(); i++) {
                                JSONObject beaconsJSONObject = beacons.getJSONObject(i);
                                String uuid = beaconsJSONObject.getString("uuid");
                                String grupoid = beaconsJSONObject.getString("grupoId");
                                String beaconid = beaconsJSONObject.getString("beaconId");
                                int lugarId = beaconsJSONObject.getInt("lugarId");
                                int rango = beaconsJSONObject.getInt("rango");
                                String nombre_lugar = beaconsJSONObject.getString("nombreLugar");
                                BeaconCustom beaconCustom = new BeaconCustom(-1, uuid, grupoid, beaconid, lugarId, rango, nombre_lugar, 0);
                                beaconArray.add(beaconCustom);
                            }


                            //dbHandler.deleteTable();
                            BeaconsBDHandler dbHandler = new BeaconsBDHandler(getApplicationContext(),null,null,1);
                            dbHandler.deleteBeacons();
                            dbHandler.addBeacons(beaconArray);

                            final ListView listBeacons = findViewById(R.id.list_beacons);
                            BeaconAdapter beaconAdapter = new BeaconAdapter(getApplicationContext(), beaconArray);
                            listBeacons.setAdapter(beaconAdapter);

                            Intent intent = new Intent(getApplicationContext(), BoundService.class);
                            startService(intent);
                            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                            mBound = true;
                            lt.success();
                            lt.hide();
                            updateBeaconsAdded(true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
                        lt.error();
                        lt.hide();
                    }
                }
            });

            task.execute(MAP_PETICIONES.get("ObtenerBeacons"), parametrosPeticion.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            lt.hide();
        }
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rutina_calefaccion, menu);

        final MenuItem toggleservice = menu.findItem(R.id.switchRutina);
        final Switch switchRutina = (Switch) toggleservice.getActionView();

        if (getBeaconsAdded()){
            Log.d(LUCES_TAG, "Beacons Added");
            switchRutina.setChecked(true);
        }else{
            Log.d(LUCES_TAG, "Beacons no añadidos");
            switchRutina.setChecked(false);
        }

        switchRutina.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    addBeaconsButtonHandler(buttonView);
                }else{
                    removeBeaconsButtonHandler(buttonView);
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void addBeaconsButtonHandler(View view) {
        if (!checkPermissions()) {
            requestPermisions();
            return;
        }
        cargarBeacons();
    }

    public void removeBeaconsButtonHandler(View view) {
        if (!checkPermissions()) {
            requestPermisions();
            return;
        }
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        Intent intent = new Intent(getApplicationContext(),
                BoundService.class);
        stopService(intent);
        updateBeaconsAdded(false);
    }


    private void updateBeaconsAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(Constants.BEACONS_ADDED_KEY, added)
                .apply();
    }

    private boolean getBeaconsAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                Constants.BEACONS_ADDED_KEY, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(KEY_RCV_MSG));
    }

    public  void mostrarBeacons(){
        BeaconsBDHandler dbHandler = new BeaconsBDHandler(getApplicationContext(),null,null,1);
        ArrayList<BeaconCustom> beaconArray = dbHandler.loadHandler();
        ListView listBeacons = findViewById(R.id.list_beacons);
        BeaconAdapter beaconAdapter = new BeaconAdapter(getApplicationContext(), beaconArray);
        listBeacons.setAdapter(beaconAdapter);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mostrarBeacons();
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    addBeaconsButtonHandler(null);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return (permissionState ==PackageManager.PERMISSION_GRANTED) && (permissionState1 == PackageManager.PERMISSION_GRANTED);
    }

    public void requestPermisions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISION);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
