package com.udc.muei.apm.apm_smarthouse.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.AsyncTasks.HttpsRequestAsyncTask;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;
import com.udc.muei.apm.apm_smarthouse.model.BeaconCustom;
import com.udc.muei.apm.apm_smarthouse.util.BeaconsBDHandler;
import com.udc.muei.apm.apm_smarthouse.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

import static com.udc.muei.apm.apm_smarthouse.util.Constants.MAP_PETICIONES;

/**
 * Created by José Manuel González on 11/06/2018 -- 21:09.
 */

public class NotificationLucesReceiver extends BroadcastReceiver {

    private static final String TAG_NOTIF_LUCES = NotificationCalefaccionReceiver.class.getSimpleName();

    private static final String CHANNEL_ID_LUCES = "channel_03";

    @Override
    public void onReceive(Context context, Intent intent) {

        int int_actionType  = intent.getIntExtra(Constants.NOTIFICACION_TRANSITION_ACTION_KEY, -1);
        int idBeacon = intent.getIntExtra(Constants.NOTIFICATION_LUCES_ID_BEACON_KEY,-1);

        if (idBeacon != -1){
            Log.d(TAG_NOTIF_LUCES, "ID BEACON: "+idBeacon);
            Constants.ACTIONS_NOTIFICATIONS actionType = Constants.ACTIONS_NOTIFICATIONS.values()[int_actionType];
            if (actionType == Constants.ACTIONS_NOTIFICATIONS.YES) {
                Log.d(TAG_NOTIF_LUCES, "Encender: "+idBeacon);
                encenderApagarLuces(context, idBeacon, true);
            }else if (actionType == Constants.ACTIONS_NOTIFICATIONS.NO) {
                Log.d(TAG_NOTIF_LUCES, "Apagar: "+idBeacon);
                encenderApagarLuces(context, idBeacon, false);
            }
        }
    }

    public void encenderApagarLuces(final Context context, int beaconId, final Boolean encender){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);

        JSONObject parametrosPeticion = new JSONObject();
        try {
            parametrosPeticion.put("tokenId", sharedPref.getString(context.getString(R.string.key_token_id),""));
            parametrosPeticion.put("estado", encender);
            BeaconsBDHandler dbHandler = new BeaconsBDHandler(context,null,null,1);
            BeaconCustom beaconCustom = dbHandler.getBeacon(beaconId);
            parametrosPeticion.put("uuid", beaconCustom.getUuid());
            parametrosPeticion.put("grupoId", beaconCustom.getIdGrupo());
            parametrosPeticion.put("beaconId", beaconCustom.getIdBeacon());

            HttpsRequestAsyncTask task = new HttpsRequestAsyncTask(context, new HttpsRequestResult() {
                @Override
                public void processFinish(String result) {
                    try {
                        Log.d(TAG_NOTIF_LUCES, result);
                        JSONObject resultJSON = new JSONObject(result);

                        if (resultJSON.getBoolean("error")){
                            if (resultJSON.getBoolean("forbidden")){

                                Toast.makeText(context," No tiene permiso para realizar la acción", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(context," Error durante la conexión", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(context," Luces "+(encender?"encendidas":"apagadas"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            task.execute(MAP_PETICIONES.get("Beacon"), parametrosPeticion.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}
