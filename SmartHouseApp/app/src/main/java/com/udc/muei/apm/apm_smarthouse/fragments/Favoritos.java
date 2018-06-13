package com.udc.muei.apm.apm_smarthouse.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.AsyncTasks.HttpsRequestAsyncTask;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.DeviceAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;
import com.udc.muei.apm.apm_smarthouse.interfaces.Updateable;
import com.udc.muei.apm.apm_smarthouse.model.Device;
import com.udc.muei.apm.apm_smarthouse.model.TipoDispositivo;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.udc.muei.apm.apm_smarthouse.util.Constants.MAP_PETICIONES;

/**
 * Created by Elías on 12/03/2018.
 */

public class Favoritos extends ParentFragment {

    private static final String FRAG_FAVORITOS_TAG = "FRAG_FAVORITOS_TAG";

    private static DeviceAdapter deviceAdapter;
    private ArrayList<Device> deviceArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarFavoritos(false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void cargarFavoritos(Boolean dialog){
        SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        JSONObject parametrosPeticion = new JSONObject();

        final LoadToast lt = new LoadToast(getContext());

        try {
            if (dialog)
                lt.show();
            lt.setText("Actualizando");
            lt.setTranslationY(1400);
            lt.setTextColor(getResources().getColor(R.color.naranja_oscuro)).setProgressColor(getResources().getColor(R.color.naranja_claro));

            parametrosPeticion.put("tokenId", sharedPref.getString(getString(R.string.key_token_id),""));
            HttpsRequestAsyncTask task = new HttpsRequestAsyncTask(getContext(), new HttpsRequestResult() {
                @Override
                public void processFinish(String result) {
                    Log.d(FRAG_FAVORITOS_TAG, result);
                    if (isAdded()){
                        try {

                            JSONObject resultJSON = new JSONObject(result);

                            try{
                                resultJSON.getString("Error");
                                Toast.makeText(getContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
                                lt.error();
                                lt.hide();
                            }catch (JSONException e){
                                SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt(getString(R.string.key_bd_django), resultJSON.getInt(getString(R.string.key_bd_django)));
                                editor.apply();

                                JSONArray favoritos = resultJSON.getJSONArray("favoritos");
                                deviceArray = new ArrayList<>();
                                for (int i = 0; i < favoritos.length(); i++) {
                                    JSONObject favorito = favoritos.getJSONObject(i);
                                    String nombre = favorito.getString("dispositivo_nombre");
                                    int dispositivo_id = favorito.getInt("dispositivo_id");
                                    boolean isActivated = (favorito.getInt("dispositivo_activado") != 0);
                                    Log.d(FRAG_FAVORITOS_TAG, "------------------------------> "+isActivated);
                                    int tipo_id = favorito.getInt("dispositivo_tipo_id");
                                    int lugar_id = favorito.getInt("dispositivo_lugar_id");
                                    Device dev = new Device(nombre,TipoDispositivo.getTipoDispositivo(tipo_id),true, isActivated, dispositivo_id, lugar_id);

                                    deviceArray.add(dev);
                                }

                                deviceAdapter = new DeviceAdapter(getContext(), deviceArray);
                                setListAdapter(deviceAdapter);
                                lt.success();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            lt.hide();
                        }
                    }
                }
            });

            task.execute(MAP_PETICIONES.get("Favoritos"), parametrosPeticion.toString());
        } catch (JSONException e) {
            lt.hide();
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        cargarFavoritos(true);
    }
}

