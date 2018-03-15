package com.udc.muei.apm.apm_smarthouse.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.DeviceAdapter;
import com.udc.muei.apm.apm_smarthouse.model.Device;
import com.udc.muei.apm.apm_smarthouse.model.TipoDispositivo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elías on 12/03/2018.
 */

public class Favoritos extends AppCompatActivity {


    private ListView deviceList;
    private static DeviceAdapter deviceAdapter;
    private ArrayList<Device> deviceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        /* Toolbar de la actividad */
        Toolbar toolbarRoutine = (Toolbar) findViewById(R.id.favoritos_toolbar);
        toolbarRoutine.setTitle(getString(R.string.toolbar_favoritos_name));
        toolbarRoutine.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarRoutine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /* Lista de dispositivos favoritos*/
        deviceList = findViewById(R.id.favoritos_list);
        deviceArray = new ArrayList<>();


        /*****************************************************************************************/
        /* Aqui se haria la petición al servidor */
        Toast.makeText(getApplicationContext(),"Petición GET. Dispositivos favoritos del usuario logueado", Toast.LENGTH_LONG).show();

        List<Device> devices = new ArrayList<Device>();
        deviceArray.add(new Device("Bombilla Salón", TipoDispositivo.LUZ, true));
        deviceArray.add(new Device("Estufa Salón", TipoDispositivo.ESTUFA, true));
        deviceArray.add(new Device("Televisión Salón", TipoDispositivo.TELEVISOR, true));
        deviceArray.add(new Device("Bombilla Salón2", TipoDispositivo.LUZ, true));
        deviceArray.add(new Device("Bombilla Salón3", TipoDispositivo.LUZ, true));


        deviceAdapter = new DeviceAdapter(getApplicationContext(), deviceArray);
        deviceList.setAdapter(deviceAdapter);

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
    public boolean onSupportNavigateUp() {
        Toast.makeText(getApplicationContext(),"Botón Back presionado " , Toast.LENGTH_SHORT).show();
        onBackPressed();
        return true;
    }
}

