package com.udc.muei.apm.apm_smarthouse.activities;

import com.udc.muei.apm.apm_smarthouse.AsyncTasks.HttpsRequestAsyncTask;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.PermisoAdapter;
import com.udc.muei.apm.apm_smarthouse.adapters.PermisoAdapterRoot;
import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;
import com.udc.muei.apm.apm_smarthouse.interfaces.PermisoListRootClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.Permiso;
import com.udc.muei.apm.apm_smarthouse.model.UsuarioLight;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.udc.muei.apm.apm_smarthouse.util.Constants.MAP_PETICIONES;

/**
 * REVISADA: José Manuel González on 11/06/2018.
 */

public class Permisos extends AppCompatActivity {

    private static final String PERMISOS_TAG = "ACTIVIDAD PERMISOS";
    private ListView permisoList;
    private static PermisoAdapter permisoAdapter;
    private static PermisoAdapterRoot permisoAdapterRoot;
    private ArrayList<Permiso> permisoArray;
    private UsuarioLight usuarioLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permisos);

        /* Recuperamos la información sobre el usuario seleccionado */
        usuarioLight =  getIntent().getParcelableExtra("usuarioLight");

        /* Toolbar de la actividad */
        Toolbar toolbarPermisos = findViewById(R.id.permisos_toolbar);
        toolbarPermisos.setTitleTextColor(Color.WHITE);

        permisoList = findViewById(R.id.permiso_list);

        if (usuarioLight != null) {
            /* En caso de que el usuarioLight halla sido pasado, se podrá hacer una consulta al servidor */
            Log.d(PERMISOS_TAG, "usuarioLight recibido");
            toolbarPermisos.setTitle(getString(R.string.toolbar_permisos_name)+" "+usuarioLight.getName());
        }else {
            toolbarPermisos.setTitle(getString(R.string.toolbar_permisos_name_error));
        }

        toolbarPermisos.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarPermisos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.only_refresh:
                cargarPermisos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarPermisos();
    }

    public void cargarPermisos(){
        if (usuarioLight != null) {
            SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);

            final LoadToast lt = new LoadToast(this);
            lt.setText("Actualizando");
            lt.setTranslationY(1400);
            lt.setTextColor(getResources().getColor(R.color.naranja_oscuro)).setProgressColor(getResources().getColor(R.color.naranja_claro));
            lt.show();
            JSONObject parametrosPeticion = new JSONObject();
            try {
                parametrosPeticion.put("tokenId", sharedPref.getString(getString(R.string.key_token_id),""));
                parametrosPeticion.put("usuarioId", usuarioLight.getIdUsuarioDjango());
                HttpsRequestAsyncTask task = new HttpsRequestAsyncTask(this, new HttpsRequestResult() {
                    @Override
                    public void processFinish(String result) {
                        Log.d(PERMISOS_TAG, result);
                        try {
                            JSONObject resultJSON = new JSONObject(result);
                            try {
                                resultJSON.getString("Error");
                                lt.error();
                                Toast.makeText(getApplicationContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
                            }catch (JSONException e) {
                                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt(getString(R.string.key_bd_django), resultJSON.getInt(getString(R.string.key_bd_django)));
                                editor.apply();
                                Boolean root = resultJSON.getBoolean("root");
                                JSONArray permisos = resultJSON.getJSONArray("permisos");
                                permisoArray = new ArrayList<>();
                                for (int i = 0; i <  permisos.length(); i++) {
                                    JSONObject permisoTemp = permisos.getJSONObject(i);
                                    String nombre = permisoTemp.getString("nombrePermiso");
                                    int idPermiso = permisoTemp.getInt("idPermiso");
                                    boolean isActivated = (permisoTemp.getInt("activado") != 0);
                                    Permiso permiso = new Permiso(idPermiso, nombre, isActivated);

                                    permisoArray.add(permiso);
                                }

                                if(!root){
                                    permisoAdapter = new PermisoAdapter(permisoArray, getApplicationContext());
                                    permisoList.setAdapter(permisoAdapter);
                                }else {
                                    permisoAdapterRoot = new PermisoAdapterRoot(permisoArray, getApplicationContext(), new PermisoListRootClicksListeners() {
                                        @Override
                                        public void onCheckedBox(int position, boolean isChecked) {
                                            Permiso permiso = (Permiso) permisoList.getItemAtPosition(position);
                                            if (permiso.getPermiso() != isChecked) {
                                                permiso.setPermiso(isChecked);
                                                onOffPermiso(permiso);
                                            }
                                        }
                                    });
                                    permisoList.setAdapter(permisoAdapterRoot);
                                }

                                lt.success();
                                lt.hide();



                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            lt.hide();
                        }
                    }
                });

                task.execute(MAP_PETICIONES.get("Permisos"), parametrosPeticion.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void onOffPermiso(Permiso permiso){
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);

        JSONObject parametrosPeticion = new JSONObject();
        try {
            parametrosPeticion.put("tokenId", sharedPref.getString(getResources().getString(R.string.key_token_id),""));
            parametrosPeticion.put("permisoId", permiso.getIdPermisoDjango());
            parametrosPeticion.put("activado", permiso.getPermiso());

            HttpsRequestAsyncTask task = new HttpsRequestAsyncTask(getApplicationContext(), new HttpsRequestResult() {
                @Override
                public void processFinish(String result) {
                    try {
                        Log.d(PERMISOS_TAG, result);
                        JSONObject resultJSON = new JSONObject(result);
                        try {
                            resultJSON.getString("Error");
                            Toast.makeText(getApplicationContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
                        }catch (JSONException e) {
                            if (resultJSON.getBoolean("error")){
                                Toast.makeText(getApplicationContext()," Error durante la conexión", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            task.execute(MAP_PETICIONES.get("ModificarPermiso"), parametrosPeticion.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
