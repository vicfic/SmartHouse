package com.udc.muei.apm.apm_smarthouse.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import static android.content.Context.WIFI_SERVICE;

public class NetworkHelper {
    public static boolean checkWifiOnAndConnected(Context contex) {
        WifiManager wifi = (WifiManager) contex.getSystemService(WIFI_SERVICE);
        if (wifi.isWifiEnabled()) {
            //el wifi está activado
            //comprobación para saber si el WiFi está conectado a algún punto de acceso
            WifiInfo wifiInfo = wifi.getConnectionInfo();

            if (wifiInfo.getNetworkId() == -1) {
                Toast.makeText(contex, "WiFi activo: No está conectado a ningún punto de acceso", Toast.LENGTH_LONG).show();
                return false; // No está conectado a ningún punto de acceso
            }
            Toast.makeText(contex, "WiFi activo: Está conectado a algún punto de acceso", Toast.LENGTH_LONG).show();
            return true; // Conectado a un punto de acceso
        } else {
            Toast.makeText(contex, "El WiFi está desactivado", Toast.LENGTH_LONG).show(); //WiFi desconectado
            return false;
        }
    }
}
