package com.udc.muei.apm.apm_smarthouse.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.activities.Luces;
import com.udc.muei.apm.apm_smarthouse.broadcastReceivers.NotificationLucesReceiver;
import com.udc.muei.apm.apm_smarthouse.model.BeaconCustom;
import com.udc.muei.apm.apm_smarthouse.util.BeaconsBDHandler;
import com.udc.muei.apm.apm_smarthouse.util.Constants;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

/**
 * REVISADA: José Manuel González on 12/06/2018.
 */

public class BoundService extends Service implements BeaconConsumer {
    private static String LOG_TAG = "BoundService";
    public static String KEY_RCV_MSG = "SmartHouse-Beacons";
    public static String KEY_RCV_MSG_beacons = "message_beacons";
    private BeaconManager beaconManager;
    public static final int MSG_SAY_HELLO = 1;

    private static final String CHANNEL_ID = "channel_01";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");


        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.setBackgroundScanPeriod(3000);

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

    }

    @Override
    public IBinder onBind(Intent intent) {
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
                    BeaconsBDHandler dbHandler = new BeaconsBDHandler(getApplicationContext(),null,null,1);
                    int beaconIdBD = dbHandler.findBeacon(oneBeacon.getId1().toString(), oneBeacon.getId2().toString(), oneBeacon.getId3().toString());
                    if (beaconIdBD>=0){
                        int distancia = (int)(oneBeacon.getDistance()*100);
                        dbHandler.updateDistancia(beaconIdBD, distancia);
                        //Llamamos al broadcast receiver por si la distancia es menor que el rango
                        BeaconCustom beaconCustom = dbHandler.getBeacon(beaconIdBD);
                        Log.d(LOG_TAG, "Beacon coincide con la BD. Actualizando información de distancia. Distancia = "+beaconCustom.getDistance()+", Rango = "+ beaconCustom.getDistanciaRango());
                        if((beaconCustom.getDistanciaRango() >= beaconCustom.getDistance())&&(beaconCustom.getDistanciaRango()>0)&&(beaconCustom.getNotificado()==0)){
                            Log.d(LOG_TAG, "-------------------------------------> Entramos en rango");
                            dbHandler.updateNotificado(beaconIdBD, 1);
                            sendNotification(beaconCustom, beaconIdBD);
                        }else if (beaconCustom.getNotificado()==1){
                            Log.d(LOG_TAG, "-------------------------------------> Salimos del rango");
                            dbHandler.updateNotificado(beaconIdBD, 0);
                        }
                    }
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

    private void sendNotification(BeaconCustom beaconCustom, int idBeacon) {

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
        int last_notif = sharedPref.getInt(Constants.FLAG_NOTIFICATION_LIGTHS, -1);

        if (last_notif!=idBeacon){

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Constants.FLAG_NOTIFICATION_LIGTHS, idBeacon);
            editor.apply();

            // Notification manager
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Android O requiere un Notification Channel.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.app_name);
                // Creación del canal para la notificación
                NotificationChannel mChannel =
                        new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

                // Enlazar el canal creado con el Notification Manager
                mNotificationManager.createNotificationChannel(mChannel);
            }

            // Creamos un Intent implicito para iniciar la actividad de la rutina de la calefaccion
            Intent notificationIntent = new Intent(getApplicationContext(), Luces.class);

            // Construct a task stack.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

            // Add the main Activity to the task stack as the parent.
            stackBuilder.addParentStack(Luces.class);

            // Push the content Intent onto the stack.
            stackBuilder.addNextIntent(notificationIntent);

            // Get a PendingIntent containing the entire back stack.
            PendingIntent notificationPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


            //Intents para las acciones
            Intent intent1 = new Intent(this, NotificationLucesReceiver.class);
            Intent intent2 = new Intent(this, NotificationLucesReceiver.class);
            intent1.putExtra(Constants.NOTIFICATION_LUCES_ID_BEACON_KEY, idBeacon);
            intent2.putExtra(Constants.NOTIFICATION_LUCES_ID_BEACON_KEY, idBeacon);

            // Notification builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            //Configuración dela notificacion
            builder.setSmallIcon(R.drawable.home)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.drawable.home))
                    .setColor(getColor(R.color.naranja_oscuro))
                    .setContentTitle(beaconCustom.getNombre_lugar());


            intent1.putExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, Constants.ACTIONS_NOTIFICATIONS.YES.ordinal());
            intent2.putExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, Constants.ACTIONS_NOTIFICATIONS.NO.ordinal());
            PendingIntent buttonsActionPendingIntent_YES = PendingIntent.getBroadcast(this, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonsActionPendingIntent_NO = PendingIntent.getBroadcast(this, 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.fire, getString(R.string.button_notification_encender), buttonsActionPendingIntent_YES)
                    .addAction(R.drawable.explosion, getString(R.string.button_notification_apagar), buttonsActionPendingIntent_NO)
                    .setContentIntent(notificationPendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentText(getString(R.string.lights_transition_notification_text));


            // Set the Channel ID for Android O.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder.setChannelId(CHANNEL_ID); // Channel ID
            }

            // Dismiss notification once the user touches it.
            builder.setAutoCancel(true);

            mNotificationManager.notify(Constants.CALEFACCION_NOTIFICATION_ID, builder.build());
        }
    }
}