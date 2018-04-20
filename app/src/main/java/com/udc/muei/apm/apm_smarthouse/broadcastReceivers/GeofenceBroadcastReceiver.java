package com.udc.muei.apm.apm_smarthouse.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.udc.muei.apm.apm_smarthouse.Services.GeofenceRegistrationService;

/**
 * Receiver para los cambios en las transiciones de geofences
 * Se reciben los eventos de transición desde el LocationServices en forma de Intent conteniendo el
 * tipo de transición y el id(s) del geofence que ha lanzado la transición.
 * Se crea un JobIntentService que manejará el intent en background.
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();
    /**
     * @param context contexto de la aplicación
     * @param intent  enviado por el Location Service. Este Intent contiene en su interior el
     *                PendingIntent configurado en la actividad de configuración de la rutina,
     *                cuando se llamó a addGeofences()
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Se ha recibido un evento de transición Geofence");
        GeofenceRegistrationService.enqueueWork(context, intent);
    }
}