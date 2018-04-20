package com.udc.muei.apm.apm_smarthouse.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.model.BeaconCustom;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class BoundService extends Service implements BeaconConsumer {
    private static String LOG_TAG = "BoundService";
    public static String KEY_RCV_MSG = "SmartHouse-Beacons";
    public static String KEY_RCV_MSG_beacons = "message_beacons";
    private IBinder mBinder = new MyBinder();
    private Chronometer mChronometer;
    private BeaconManager beaconManager;
    public static final int MSG_SAY_HELLO = 1;
    public static final int MSG_KEY = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");


        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout(BeaconParser.URI_BEACON_LAYOUT));


        beaconManager.bind(this);

        mChronometer = new Chronometer(this);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //Log.v(LOG_TAG, "in onBind");
        //return mBinder;
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();

    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
        beaconManager.unbind(this);
        mChronometer.stop();
    }


    public String getTimestamp() {
        long elapsedMillis = SystemClock.elapsedRealtime()
                - mChronometer.getBase();
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
        int millis = (int) (elapsedMillis - hours * 3600000 - minutes * 60000 - seconds * 1000);
        return hours + ":" + minutes + ":" + seconds + ":" + millis;
    }


    private void sendMessage(ArrayList<BeaconCustom> beaconsCustom) {
        // The string "my-integer" will be used to filer the intent
        Intent intent = new Intent(KEY_RCV_MSG);
        // Adding some data
        intent.putParcelableArrayListExtra(KEY_RCV_MSG_beacons, beaconsCustom);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onBeaconServiceConnect() {
        final Region region = new Region("SmartHouse-Beacons", null, null, null);

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                ArrayList<BeaconCustom> beaconsCustom= new ArrayList<>();

                for(Beacon oneBeacon : beacons) {
                    beaconsCustom.add(new BeaconCustom(oneBeacon.getDistance(),
                            oneBeacon.getId1().toString(),
                            oneBeacon.getId2().toString(),
                            oneBeacon.getId3().toString(),
                            oneBeacon.getBluetoothName(),
                            oneBeacon.getBluetoothAddress(),
                            oneBeacon.getRunningAverageRssi(),
                            oneBeacon.getRssi(),
                            0.1));
                    Log.d(LOG_TAG, "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());
                }
                sendMessage(beaconsCustom);
            }
        });

        try {beaconManager.startRangingBeaconsInRegion(region);
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {   e.printStackTrace(); }
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());


    public class MyBinder extends Binder {
        public BoundService getService() {
            return BoundService.this;
        }
    }
}