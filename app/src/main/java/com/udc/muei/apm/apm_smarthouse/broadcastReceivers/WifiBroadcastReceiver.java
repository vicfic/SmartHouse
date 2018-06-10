package com.udc.muei.apm.apm_smarthouse.broadcastReceivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
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
import com.udc.muei.apm.apm_smarthouse.activities.MenuPrincipal;
import com.udc.muei.apm.apm_smarthouse.util.Constants;

import static android.content.Context.WIFI_SERVICE;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = WifiBroadcastReceiver.class.getSimpleName();
    private static final String CHANNEL_ID = "channel_03";

    /**
     * @param context contexto de la aplicación
     * @param intent  enviado por el Location Service. Este Intent contiene en su interior el
     *                PendingIntent configurado en la actividad de configuración de la rutina,
     *                cuando se llamó a addGeofences()
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Se ha recibido un evento de transición wifi");
        // Comprobamos si la rutina de wifi está activa
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean rutinaWifi = sharedPref.getBoolean(Constants.WIFI_ADDED_KEY, false);
        if (!rutinaWifi){
            return;
        }
        // Comprobamos si el cambio de la conexion se debe a que nos hemos quedado sin conexion
        int wifiStatus = checkWifiOnAndConnected(context);
        if(wifiStatus == 0) {
            sendNotification(context);
        }
    }

    private void sendNotification(Context context){
        boolean lucesAuto = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.OPTION_AUTO_LUCES_KEY, false);

        // Notification manager
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
        Intent notificationIntent = new Intent(context, WifiBroadcastReceiver.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(ComprobacionWifi.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder;

        if(lucesAuto) {
            Intent intent = new Intent(context, NotificationWifiReceiver.class);
            intent.putExtra(Constants.NOTIFICATION_LUCES_AUTO_KEY, lucesAuto);
            context.sendBroadcast(intent);
            return;
        } else {
            Toast.makeText(context, "Mandamos notificacion preguntando si quiere apagar las luces", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Mandamos notificacion preguntando si quiere apagar las luces");
            // Si no tenemos respuesta del usuario y no se deben apagar automáticamente, enviamos una notificación preguntándole si quiere apagar las luces
            builder = sendNotificationAsking(context, notificationPendingIntent);
        }

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        if (!lucesAuto)
            mNotificationManager.notify(Constants.LUCES_NOTIFICATION_ID, builder.build());

    }

    private NotificationCompat.Builder sendNotificationAsking(Context context, PendingIntent notificationPendingIntent){
        // Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.home)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.home))
                .setColor(context.getColor(R.color.naranja_oscuro))
                .setContentTitle(context.getString(R.string.title_notification_auto_ON_wifi));
        //Intents para las acciones
        Intent intentYes = new Intent(context, NotificationWifiReceiver.class);
        Intent intentNo = new Intent(context, NotificationWifiReceiver.class);
        intentYes.putExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, Constants.ACTIONS_NOTIFICATIONS.YES.ordinal());
        intentNo.putExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, Constants.ACTIONS_NOTIFICATIONS.NO.ordinal());

        PendingIntent buttonsActionPendingIntent_YES = PendingIntent.getBroadcast(context, 0, intentYes, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent buttonsActionPendingIntent_NO = PendingIntent.getBroadcast(context, 0, intentNo, PendingIntent.FLAG_UPDATE_CURRENT);
        // Configuración de la notificación no automática
        builder.addAction(R.drawable.explosion, context.getString(R.string.button_notification_YES), buttonsActionPendingIntent_YES)
                .addAction(R.drawable.fire, context.getString(R.string.button_notification_NO), buttonsActionPendingIntent_NO)
                .setContentIntent(notificationPendingIntent)
                .setContentText(context.getString(R.string.lights_transition_notification_text_OFF))
                .setVibrate((new long[] { 1000, 1000, 1000, 1000, 1000 }));

        return builder;
    }

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
}
