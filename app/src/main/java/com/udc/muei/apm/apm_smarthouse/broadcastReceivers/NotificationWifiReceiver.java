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

        int wifiStatus = checkWifiOnAndConnected(context);

        if (wifiStatus == 2) {
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
                        Log.d(NotificationWifiReceiver.class.getName(), ntkInfo.getExtraInfo() + "con SSID:" + connectionInfo.getSSID());
                    }
                }
            }
        } else if (wifiStatus == 1) {
            //el wifi no está conectado
            sendNotificationAuto(context);
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

        //Configuración dela notificacion
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
        mNotificationManager.notify(Constants.NOTIFICATION_ID, builder.build());
    }
}
