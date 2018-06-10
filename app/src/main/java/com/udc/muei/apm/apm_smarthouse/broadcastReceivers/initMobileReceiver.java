package com.udc.muei.apm.apm_smarthouse.broadcastReceivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.Services.ComprobacionLocalizacion;
import com.udc.muei.apm.apm_smarthouse.Services.ComprobacionWifi;

public class initMobileReceiver extends BroadcastReceiver {
    public static String TAG_INIT_MOBILE_RECEIVER = "initMobileReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        //Iniciamos el servicio de comprobación de localización
        Log.d(TAG_INIT_MOBILE_RECEIVER,"Se ha recibido un mensaje: "+intent.getAction());


        //Comprobamos la preferencia de la rutina de calefaccion, y si procede iniciamos el servicio
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
        Boolean rutinaCalefaccion = sharedPref.getBoolean(context.getString(R.string.key_shared_rutina_calefaccion), false);
        if (rutinaCalefaccion){
            Intent intentService = new Intent(context, ComprobacionLocalizacion.class);
            context.startService(intentService);
        }
        Boolean rutinaWifi = sharedPref.getBoolean(context.getString(R.string.key_shared_rutina_wifi), false);
        if (rutinaWifi){
            Intent intentService = new Intent(context, ComprobacionWifi.class);
            context.startService(intentService);
        }
    }
}
