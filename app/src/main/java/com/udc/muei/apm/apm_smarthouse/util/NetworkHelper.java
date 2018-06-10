package com.udc.muei.apm.apm_smarthouse.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.WIFI_SERVICE;

//FIXME  WIFI: ver cómo hacer para que este broadcast receiver sea independiente de la activity, o sustituirlo por un servicio
//Devuelve 0 si está desactivado el wifi, 1 si está activado y no conectado y 2 si está activado y conectado
public class NetworkHelper extends BroadcastReceiver {
    public static int checkWifiOnAndConnected(Context contex) {
        WifiManager wifi = (WifiManager) contex.getSystemService(WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            //el wifi está activado
            //comprobación para saber si el WiFi está conectado a algún punto de acceso
            WifiInfo wifiInfo = wifi.getConnectionInfo();
            if (wifiInfo.getNetworkId() == -1) {
                Toast.makeText(contex, "WiFi activo: No está conectado a ningún punto de acceso", Toast.LENGTH_LONG).show();
                return 1; // No está conectado a ningún punto de acceso
            }
            Toast.makeText(contex, "WiFi activo: Está conectado a algún punto de acceso", Toast.LENGTH_LONG).show();
            return 2; // Conectado a un punto de acceso
        } else {
            Toast.makeText(contex, "El WiFi está desactivado", Toast.LENGTH_LONG).show(); //WiFi desconectado
            return 0;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (checkWifiOnAndConnected(context) == 2) {
            //se establece conexión wifi
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = connManager.getAllNetworks();

            for (Network network : networks) { //recorro la lista de redes, en las válidas muestro el SSID
                NetworkInfo ntkInfo = connManager.getNetworkInfo(network);
                if (ntkInfo.getType() == ConnectivityManager.TYPE_WIFI && ntkInfo.isConnected()) {
                    final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    if (connectionInfo != null) {
                        Toast.makeText(context, "El WiFi al que estás conectado es " + ntkInfo.getExtraInfo() + "con SSID:" + connectionInfo.getSSID(), Toast.LENGTH_LONG).show();
                        Log.d(NetworkHelper.class.getName(), ntkInfo.getExtraInfo() + "con SSID:" + connectionInfo.getSSID());
                    }
                }
            }
        }
    }
}
