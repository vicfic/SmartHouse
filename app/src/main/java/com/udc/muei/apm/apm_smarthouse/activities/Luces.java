package com.udc.muei.apm.apm_smarthouse.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.Services.BoundService;
import com.udc.muei.apm.apm_smarthouse.adapters.BeaconAdapter;
import com.udc.muei.apm.apm_smarthouse.model.BeaconCustom;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

import static com.udc.muei.apm.apm_smarthouse.Services.BoundService.KEY_RCV_MSG;
import static com.udc.muei.apm.apm_smarthouse.Services.BoundService.KEY_RCV_MSG_beacons;


public class Luces extends AppCompatActivity{

    private static final String LUCES_TAG = "ACTIVIDAD_LUCES";
    public static final String MSG_KEY = "hola mundo";
    private static final int REQUEST_PERMISION = 1;
    private BeaconManager beaconManager;
    BoundService mBoundService;
    boolean mServiceBound = false;

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


        Toolbar toolbar = (Toolbar) findViewById(R.id.luces_toolbar);

        toolbar.setTitle("Rutina Luces");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISION);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISION);
        }

        Button stopServiceButon = (Button) findViewById(R.id.stop_service);
        Button startServiceButon =(Button) findViewById(R.id.start_service);

        startServiceButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BoundService.class);
                startService(intent);
                bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                mBound = true;

            }
        });

        stopServiceButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    unbindService(mConnection);
                    mBound = false;
                }
                Intent intent = new Intent(getApplicationContext(),
                        BoundService.class);
                stopService(intent);
            }
        });


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
    public void onResume() {
        super.onResume();
        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,
                        new IntentFilter(KEY_RCV_MSG));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            ArrayList<BeaconCustom> beacons = intent.getParcelableArrayListExtra(KEY_RCV_MSG_beacons);

            if (beacons!=null){

                /*Como por el momento solo hay un beacon, lo replicamos en el array para que se muestre dos veces en la lista*/
                if(beacons.size()>0){
                    BeaconCustom beacon = beacons.get(0);
                    if (beacon!=null){
                        beacon.setDistanciaRango(10.0);
                        BeaconCustom beaconNew = new BeaconCustom(beacon.getDistance(),
                                beacon.getUuid(),
                                beacon.getIdGrupo(),
                                beacon.getIdBeacon()+1,
                                beacon.getBluetoothName(),
                                beacon.getBluetoothAddress(),
                                beacon.getAverageRssi(),
                                beacon.getRssi(),
                                0.0);
                        beacons.add(beaconNew);
                    }
                }
            /* SACAR CUANDO HAYA MAS BEACONS*/


                final ListView listBeacons = findViewById(R.id.list_beacons);
                BeaconAdapter beaconAdapter = new BeaconAdapter(getApplicationContext(), beacons);
                listBeacons.setAdapter(beaconAdapter);
            }
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
