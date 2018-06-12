package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.AsyncTasks.HttpsRequestAsyncTask;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.DeviceAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;
import com.udc.muei.apm.apm_smarthouse.model.Device;
import com.udc.muei.apm.apm_smarthouse.model.Lugar;
import com.udc.muei.apm.apm_smarthouse.model.TipoDispositivo;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * REVISADA: José Manuel González on 11/06/2018.
 */

import static com.udc.muei.apm.apm_smarthouse.util.Constants.MAP_PETICIONES;

public class DispositivosLugar extends AppCompatActivity {

    private static final String DISPOSITIVOS_LUGAR_TAG = "DISPOSITIVOS LUGAR";

    private ListView deviceList;
    private static DeviceAdapter deviceAdapter;
    private ArrayList<Device> deviceArray;
    private Lugar lugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_lugar);

        lugar =  getIntent().getParcelableExtra("lugar");

        /* Toolbar de la actividad */
        Toolbar toolbarRoutine = findViewById(R.id.dispositivos_lugar_toolbar);
        toolbarRoutine.setTitle(getString(R.string.toolbar_dispositivos_lugar_name)+" "+lugar.getName());
        toolbarRoutine.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarRoutine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        deviceList = findViewById(R.id.dispositivos_lugar_list);

        registerForContextMenu(deviceList);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Encender");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Apagar");
        menu.add(0, v.getId(), 0, "Ajustar temperatura");
    }

    @Override
    protected void onStart() {
        super.onStart();
        cargarDispositivos();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void cargarDispositivos(){
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
        final LoadToast lt = new LoadToast(this);
        lt.setText("Actualizando");
        lt.setTranslationY(1400);
        lt.setTextColor(getResources().getColor(R.color.naranja_oscuro)).setProgressColor(getResources().getColor(R.color.naranja_claro));
        lt.show();
        JSONObject parametrosPeticion = new JSONObject();
        try {
            parametrosPeticion.put("tokenId", sharedPref.getString(getString(R.string.key_token_id),""));
            parametrosPeticion.put("lugarId", lugar.getIdDjango());
            HttpsRequestAsyncTask task = new HttpsRequestAsyncTask(this, new HttpsRequestResult() {
                @Override
                public void processFinish(String result) {
                    Log.d(DISPOSITIVOS_LUGAR_TAG, result);
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

                                JSONArray favoritos = resultJSON.getJSONArray("dispositivos");
                                deviceArray = new ArrayList<>();
                                for (int i = 0; i < favoritos.length(); i++) {
                                    JSONObject favorito = favoritos.getJSONObject(i);
                                    String nombre = favorito.getString("dispositivo_nombre");
                                    int dispositivo_id = favorito.getInt("dispositivo_id");
                                    boolean isActivated = (favorito.getInt("dispositivo_activado")!=0);
                                    int tipo_id = favorito.getInt("dispositivo_tipo_id");
                                    int lugar_id = favorito.getInt("dispositivo_lugar_id");
                                    boolean dispositivo_favorito = favorito.getBoolean("favorito");
                                    Device dev = new Device(nombre,TipoDispositivo.getTipoDispositivo(tipo_id),dispositivo_favorito, isActivated, dispositivo_id, lugar_id);

                                    deviceArray.add(dev);

                                    lt.success();
                                    lt.hide();
                                }

                                deviceAdapter = new DeviceAdapter(getApplicationContext(), deviceArray);
                                deviceList.setAdapter(deviceAdapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            lt.hide();
                        }
                    }
            });

            task.execute(MAP_PETICIONES.get("Dispositivos"), parametrosPeticion.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.only_refresh:
                cargarDispositivos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
