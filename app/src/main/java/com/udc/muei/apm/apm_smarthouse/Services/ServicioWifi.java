package com.udc.muei.apm.apm_smarthouse.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;
//FIXME WIFI: arreglar este servicio para que funcione independientemente de la activity
public class ServicioWifi extends Service {

    public ServicioWifi() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context contex = getApplicationContext();
        WifiManager wifi = (WifiManager) contex.getSystemService(WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            //el wifi está activado
            //comprobación para saber si el WiFi está conectado a algún punto de acceso
            WifiInfo wifiInfo = wifi.getConnectionInfo();
            if (wifiInfo.getNetworkId() == -1) {
                Toast.makeText(contex, "WiFi activo: No está conectado a ningún punto de acceso", Toast.LENGTH_LONG).show();
            }
            Toast.makeText(contex, "WiFi activo: Está conectado a algún punto de acceso", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(contex, "El WiFi está desactivado", Toast.LENGTH_LONG).show(); //WiFi desconectado
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setAction("com.udc.muei.apm.apm_smarthouse.Services.COMPROBACION_LOCALIZACION_DESTROY");
        sendBroadcast(intent);
    }
}
