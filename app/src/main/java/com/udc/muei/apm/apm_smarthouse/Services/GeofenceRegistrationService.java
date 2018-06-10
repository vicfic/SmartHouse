package com.udc.muei.apm.apm_smarthouse.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.activities.ComprobacionGPS;

import java.util.ArrayList;
import java.util.List;

import com.udc.muei.apm.apm_smarthouse.broadcastReceivers.NotificationCalefaccionReceiver;
import com.udc.muei.apm.apm_smarthouse.util.*;

public class GeofenceRegistrationService extends JobIntentService {

    private static final int JOB_ID = 573;

    private static final String TAG = "GeofenceTransitionsIS";

    private static final String CHANNEL_ID = "channel_01";

    /**
     * Se encola trabajo en este servicio
     */
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, GeofenceRegistrationService.class, JOB_ID, intent);
    }

    /**
     * Manejador de los Intents que llegan
     * @param intent  enviado por el Location Service. Este Intent contiene en su interior el
     *                PendingIntent configurado en la actividad de configuración de la rutina,
     *                cuando se llamó a addGeofences()
     */
    @Override
    protected void onHandleWork(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent); //Tipo de evento
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition(); //Tipo de transición

        // Comprobación de que las transiciones sean válidas
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Geofences donde se ha lanzado el evento. Un unico evento puede tener varios geofences
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            // Detalles de la transición como String
            String geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);

            //Mensaje de la notifiación dependiendo del tipo de transición
            String msgTransitionType = getMsgTypeTransition(geofenceTransition);

            // Enviar notificación y log de la transición
            sendNotification( msgTransitionType, geofenceTransition);
            Log.i(TAG, geofenceTransitionDetails);
        } else {
            // Tipo de transición inválida
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    /**
     * Función auxiliar para obtener los detalles de la transición y devolverlos formateados
     * como String
     * @param geofenceTransition    ID de la transición
     * @param triggeringGeofences   Geofences que han lanzado el evento
     * @return                      Detalles de la trasición formateados como String
     */
    private String getGeofenceTransitionDetails(int geofenceTransition, List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }

    /**
     * Lanzamiento de una notificación cuando una transición ha sido detectada.
     */
    private void sendNotification(String notificationDetails, int transitionType) {

        boolean auto_on = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.OPTION_AUTO_CALEFACCION_KEY, false);
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
        Intent notificationIntent = new Intent(getApplicationContext(), ComprobacionGPS.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(ComprobacionGPS.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        //Intents para las acciones
        Intent intent1 = new Intent(this, NotificationCalefaccionReceiver.class);
        Intent intent2 = new Intent(this, NotificationCalefaccionReceiver.class);

        // Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //Configuración dela notificacion
        builder.setSmallIcon(R.drawable.home)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.home))
                .setColor(getColor(R.color.naranja_oscuro))
                .setContentTitle(notificationDetails);
        if ( transitionType == Geofence.GEOFENCE_TRANSITION_EXIT){
            intent1.putExtra(Constants.NOTIFICACION_TRANSITION_KEY, Geofence.GEOFENCE_TRANSITION_EXIT);
            intent2.putExtra(Constants.NOTIFICACION_TRANSITION_KEY, Geofence.GEOFENCE_TRANSITION_EXIT);
            intent1.putExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, Constants.ACTIONS_NOTIFICATIONS.YES.ordinal());
            intent2.putExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, Constants.ACTIONS_NOTIFICATIONS.NO.ordinal());
            PendingIntent buttonsActionPendingIntent_YES = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent buttonsActionPendingIntent_NO = PendingIntent.getBroadcast(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.explosion, getString(R.string.button_notification_YES), buttonsActionPendingIntent_YES)
                    .addAction(R.drawable.fire, getString(R.string.button_notification_NO), buttonsActionPendingIntent_NO)
                    .setContentIntent(notificationPendingIntent)
                    .setContentText(getString(R.string.geofence_transition_notification_text_OFF));
        }else if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            if (!auto_on) {
                intent1.putExtra(Constants.NOTIFICACION_TRANSITION_KEY, Geofence.GEOFENCE_TRANSITION_ENTER);
                intent2.putExtra(Constants.NOTIFICACION_TRANSITION_KEY, Geofence.GEOFENCE_TRANSITION_ENTER);
                intent1.putExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, Constants.ACTIONS_NOTIFICATIONS.YES.ordinal());
                intent2.putExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, Constants.ACTIONS_NOTIFICATIONS.NO.ordinal());
                PendingIntent buttonsActionPendingIntent_YES = PendingIntent.getBroadcast(this, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntent buttonsActionPendingIntent_NO = PendingIntent.getBroadcast(this, 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.addAction(R.drawable.fire, getString(R.string.button_notification_YES), buttonsActionPendingIntent_YES)
                        .addAction(R.drawable.explosion, getString(R.string.button_notification_NO), buttonsActionPendingIntent_NO)
                        .setContentIntent(notificationPendingIntent)
                        .setContentText(getString(R.string.geofence_transition_notification_text_ON));
            }else{
                intent1 = new Intent(this, NotificationCalefaccionReceiver.class);
                intent1.putExtra(Constants.NOTIFICACION_TRANSITION_KEY, Geofence.GEOFENCE_TRANSITION_ENTER);
                intent1.putExtra(Constants.NOTIFICATION_CALEFACCION_AUTO_KEY, auto_on);
                sendBroadcast(intent1);
            }
        }



        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        if (!auto_on)
            mNotificationManager.notify(Constants.CALEFACCION_NOTIFICATION_ID, builder.build());
    }

    /**
     * Mapeo de los tipos de transiciones para que la información sea legible
     * @param transitionType    Tipo de transición
     * @return                  String indicando el tipo de transición
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited);
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return getString(R.string.geofence_transition_dwell);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    /**
     * Función encargada de selección el mensaje a mostrar en la notificación
     * @param transitionType    Tipo de transición
     * @return                  String con el mensaje para la notificación
     */
    private String getMsgTypeTransition(int transitionType){
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return getString(R.string.geofence_transition_entered_msg);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return getString(R.string.geofence_transition_exited_msg);
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return getString(R.string.geofence_transition_dwell_msg);
            default:
                return getString(R.string.unknown_geofence_transition_msg);
        }
    }
}