package com.udc.muei.apm.apm_smarthouse.broadcastReceivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.activities.ComprobacionGPS;
import com.udc.muei.apm.apm_smarthouse.util.Constants;

public class NotificationCalefaccionReceiver extends BroadcastReceiver {


    private static final String TAG = NotificationCalefaccionReceiver.class.getSimpleName();

    private static final String CHANNEL_ID = "channel_02";

    @Override
    public void onReceive(Context context, Intent intent) {

        int int_actionType  = intent.getIntExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, -1);
        boolean auto_calefaccion = intent.getBooleanExtra(Constants.NOTIFICATION_CALEFACCION_AUTO_KEY, false);

        if(int_actionType>=0 || auto_calefaccion) {

            int transitionType = intent.getIntExtra(Constants.NOTIFICACION_TRANSITION_KEY, -1);

            if (auto_calefaccion) {
                if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    //Se encenderá la calefaccion automáticamente
                    Toast.makeText(context, "Iniciado el proceso para enviar al servidor la petición de ENCENDER la calefacción", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Iniciado el proceso para enviar al servidor la petición de ENCENDER la calefacción");
                    sendNotificationAuto(context);
                }
            } else {
                Constants.ACTIONS_NOTIFICATIONS actionType = Constants.ACTIONS_NOTIFICATIONS.values()[int_actionType];
                if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    if (actionType == Constants.ACTIONS_NOTIFICATIONS.YES) {
                        Toast.makeText(context, "Se ha iniciado el proceso para enviar al servidor la petición de APAGAR la calefacción", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Se ha iniciado el proceso para enviar al servidor la petición de APAGAR la calefacción");
                        cancelNotificacion(context, Constants.NOTIFICATION_ID);
                    } else if (actionType == Constants.ACTIONS_NOTIFICATIONS.NO) {
                        cancelNotificacion(context, Constants.NOTIFICATION_ID);
                    }
                } else if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    if (actionType == Constants.ACTIONS_NOTIFICATIONS.YES) {
                        Toast.makeText(context, "Iniciado el proceso para enviar al servidor la petición de ENCENDER la calefaccióne", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Iniciado el proceso para enviar al servidor la petición de ENCENDER la calefacción");
                        cancelNotificacion(context, Constants.NOTIFICATION_ID);
                    } else if (actionType == Constants.ACTIONS_NOTIFICATIONS.NO) {
                        cancelNotificacion(context, Constants.NOTIFICATION_ID);
                    }
                }
            }
        }
    }

    private void cancelNotificacion(Context context, int id){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(id);
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
        Intent notificationIntent = new Intent(context.getApplicationContext(), ComprobacionGPS.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(ComprobacionGPS.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //Configuración dela notificacion
        builder.setSmallIcon(R.drawable.home)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.home))
                .setColor(context.getColor(R.color.naranja_oscuro))
                .setContentTitle(context.getString(R.string.title_notification_auto_ON_calefaccion))
                .setContentText(context.getString(R.string.msg_notification_auto_ON_calefaccion))
                .setContentIntent(notificationPendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.msg_notification_auto_ON_calefaccion)))
                .setAutoCancel(true);
        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
        mNotificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
    }
}
