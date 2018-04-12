package com.udc.muei.apm.apm_smarthouse.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.DeviceAdapter;
import com.udc.muei.apm.apm_smarthouse.model.Device;
import com.udc.muei.apm.apm_smarthouse.model.Lugar;
import com.udc.muei.apm.apm_smarthouse.model.TipoDispositivo;

import java.util.ArrayList;
import java.util.List;

public class DispositivosLugar extends AppCompatActivity {

    private ListView deviceList;
    private static DeviceAdapter deviceAdapter;
    private ArrayList<Device> deviceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_lugar);

        Integer idLugarDjango = getIntent().getIntExtra("idLugar",-1);
        String nombreLugar = getIntent().getStringExtra("stringLugar");
        Lugar lugar =  getIntent().getParcelableExtra("lugar");
        Toast.makeText(getApplicationContext(),"Petición GET al servidor. Dispositivos que se encuentren en "+ lugar.getName()+" ("+lugar.getIdDjango()+")" , Toast.LENGTH_SHORT).show();

        /* Toolbar de la actividad */
        Toolbar toolbarRoutine = (Toolbar) findViewById(R.id.dispositivos_lugar_toolbar);
        toolbarRoutine.setTitle(getString(R.string.toolbar_dispositivos_lugar_name)+" "+lugar.getName());
        toolbarRoutine.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarRoutine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /* Lista de dispositivos favoritos*/
        deviceList = findViewById(R.id.dispositivos_lugar_list);
        deviceArray = new ArrayList<>();


        List<Device> devices = new ArrayList<Device>();
        deviceArray.add(new Device("Bombilla Salón", TipoDispositivo.LUZ, true));
        deviceArray.add(new Device("Estufa Salón", TipoDispositivo.ESTUFA, false));
        deviceArray.add(new Device("Televisión Salón", TipoDispositivo.TELEVISOR, true));
        deviceArray.add(new Device("Bombilla Salón2", TipoDispositivo.LUZ, false));
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
