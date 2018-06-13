package com.udc.muei.apm.apm_smarthouse.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.AsyncTasks.HttpsRequestAsyncTask;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.activities.Permisos;
import com.udc.muei.apm.apm_smarthouse.adapters.UsuarioAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;
import com.udc.muei.apm.apm_smarthouse.interfaces.UsuarioListClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.UsuarioLight;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.udc.muei.apm.apm_smarthouse.util.Constants.MAP_PETICIONES;

/**
 * REVISADA: José Manuel González on 11/06/2018.
 */

public class Usuarios extends ParentFragment {

    private static final String USUARIOS_TAG = "ACTIVIDAD LUGARES";
    private static UsuarioAdapter usuarioAdapter;
    private ArrayList<UsuarioLight> usuarioArray;


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
        cargarUsuarios(false);
    }

    public void cargarUsuarios(Boolean dialog){
        SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);

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
                    Log.d(USUARIOS_TAG, result);
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

                                JSONArray usuarios = resultJSON.getJSONArray("usuarios");
                                usuarioArray = new ArrayList<>();
                                for (int i = 0; i < usuarios.length(); i++) {
                                    JSONObject usuarioTmp = usuarios.getJSONObject(i);
                                    String nombre = usuarioTmp.getString("nombre_usuario");
                                    int usuario_id =  usuarioTmp.getInt("id_usuario");
                                    String urlPhoto = usuarioTmp.getString("photo_usuario");
                                    UsuarioLight usuarioLight = new UsuarioLight(usuario_id, nombre, urlPhoto);
                                    usuarioArray.add(usuarioLight);
                                }
                                usuarioAdapter = new UsuarioAdapter(usuarioArray, getContext(), new UsuarioListClicksListeners() {
                                    @Override
                                    public void onButtonUserClick(int position) {
                                        UsuarioLight usuarioLight = (UsuarioLight) getListView().getItemAtPosition(position);
                                        Intent i = new Intent(getContext(), Permisos.class);
                                        i.putExtra("usuarioLight", usuarioLight);
                                        startActivity(i);
                                    }
                                });
                                setListAdapter(usuarioAdapter);

                                lt.success();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            lt.hide();
                        }
                    }
                }
            });

            task.execute(MAP_PETICIONES.get("Usuarios"), parametrosPeticion.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            lt.hide();
        }
    }

    @Override
    public void update() {
        cargarUsuarios(true);
    }
}
