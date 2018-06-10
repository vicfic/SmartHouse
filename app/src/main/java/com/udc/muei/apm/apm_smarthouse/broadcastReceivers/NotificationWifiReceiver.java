package com.udc.muei.apm.apm_smarthouse.broadcastReceivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.activities.ComprobacionWifi;
import com.udc.muei.apm.apm_smarthouse.util.Constants;

import static android.content.Context.WIFI_SERVICE;

//FIXME  WIFI: ver cómo hacer para que este broadcast receiver sea independiente de la activity, o sustituirlo por un servicio
//Devuelve 0 si está desactivado el wifi, 1 si está activado y no conectado y 2 si está activado y conectado
public class NotificationWifiReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "channel_02";
    private static final String TAG = NotificationWifiReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // Vemos si las luces se deben apagar automaticamente
        boolean lucesAuto = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.OPTION_AUTO_LUCES_KEY, false);
        // Vemos si nos ha enviado el usuario una respuesta de "Si"
        int int_actionType  = intent.getIntExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, -1);
        // Si las luces se deben apagar automaticamente o bien si nos ha llegado un "Si", enviamos notificacion para informar al usuario
        if(lucesAuto) {
            // Apagamos las luces
            Toast.makeText(context, "Apagamos las luces", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Apagamos las luces");
            sendNotificationAuto(context);
        } else {
            if(int_actionType == Constants.ACTIONS_NOTIFICATIONS.YES.ordinal()){
                // Si nos han respondido con "Si", borramos la notificación al usuario
                Toast.makeText(context, "Nos han respondido que si, cancelamos notificacion", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Nos han respondido que si, cancelamos notificacion");
            } else{
                // Si nos han respondido con "No", borramos la notificación al usuario
                Toast.makeText(context, "Nos han respondido que no, cancelamos notificacion", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Nos han respondido que no, cancelamos notificacion");
            }

            cancelNotificacion(context, Constants.LUCES_NOTIFICATION_ID);
            return;
        }
    }

    private void sendNotificationAuto(Context context){
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requiere un Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            // Creación del canal para la notificación
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Enlazar el canal creado con el Notification Manager
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Creamos un Intent implicito para iniciar la actividad de la rutina de la calefaccion
        Intent notificationIntent = new Intent(context.getApplicationContext(), ComprobacionWifi.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(ComprobacionWifi.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // Configuración de la notificación automática
        builder.setSmallIcon(R.drawable.home)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.home))
                .setColor(context.getColor(R.color.naranja_oscuro))
                .setContentTitle(context.getString(R.string.title_notification_auto_ON_wifi))
                .setContentText(context.getString(R.string.msg_notification_auto_ON_wifi))
                .setContentIntent(notificationPendingIntent)
                // Le añadimos vibracion para asegurar que el usuario ve la notificacion
                .setVibrate((new long[] { 1000, 1000, 1000, 1000, 1000 }))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.msg_notification_auto_ON_wifi)))
                .setAutoCancel(true);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
        mNotificationManager.notify(Constants.LUCES_NOTIFICATION_ID, builder.build());
    }

    private void cancelNotificacion(Context context, int id){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(id);
    }
}
