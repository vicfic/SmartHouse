package com.udc.muei.apm.apm_smarthouse.fragments;

import android.content.Context;
import android.content.Intent;

import com.udc.muei.apm.apm_smarthouse.AsyncTasks.HttpsRequestAsyncTask;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.activities.DispositivosLugar;
import com.udc.muei.apm.apm_smarthouse.adapters.LugarAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;
import com.udc.muei.apm.apm_smarthouse.interfaces.LugarListClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.Lugar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.udc.muei.apm.apm_smarthouse.util.Constants.MAP_PETICIONES;

/**
 * REVISADA: José Manuel González on 10/06/2018.
 */

public class Lugares extends ParentFragment implements AdapterView.OnItemClickListener {

    private static final String LUGARES_TAG = "FRAGMENTO LUGARES";
    private static LugarAdapter lugaresAdapter;
    private ArrayList<Lugar> lugarArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarLugares(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    public void cargarLugares(Boolean dialog){
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
                    Log.d(LUGARES_TAG, result);
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

                                JSONArray lugares = resultJSON.getJSONArray("lugares");
                                lugarArray = new ArrayList<>();
                                for (int i = 0; i < lugares.length(); i++) {
                                    JSONObject lugarTmp = lugares.getJSONObject(i);
                                    String nombre = lugarTmp.getString("nombre_lugar");
                                    int lugar_id =  lugarTmp.getInt("id_lugar");
                                    String urlPhoto = lugarTmp.getString("photo_lugar");
                                    Lugar lugar = new Lugar(nombre, lugar_id, urlPhoto);
                                    lugarArray.add(lugar);
                                }
                                lugaresAdapter = new LugarAdapter(lugarArray, getContext(), new LugarListClicksListeners() {
                                    @Override
                                    public void onButtonLugaresClick(int position) {
                                        Lugar lugar = (Lugar) getListView().getItemAtPosition(position);
                                        Intent i = new Intent(getContext(), DispositivosLugar.class);
                                        i.putExtra("lugar", lugar);
                                        startActivity(i);
                                    }
                                });
                                setListAdapter(lugaresAdapter);

                                lt.success();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            lt.hide();
                        }
                    }
                }
            });

            task.execute(MAP_PETICIONES.get("Lugares"), parametrosPeticion.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            lt.hide();
        }
    }

    @Override
    public void update() {
        cargarLugares(false);
    }
}
