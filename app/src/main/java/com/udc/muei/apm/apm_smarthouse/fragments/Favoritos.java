package com.udc.muei.apm.apm_smarthouse.fragments;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.adapters.DeviceAdapter;
import com.udc.muei.apm.apm_smarthouse.model.Device;
import com.udc.muei.apm.apm_smarthouse.model.TipoDispositivo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elías on 12/03/2018.
 */

public class Favoritos extends android.support.v4.app.ListFragment {

    private static DeviceAdapter deviceAdapter;
    private ArrayList<Device> deviceArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Lista de dispositivos favoritos*/
        deviceArray = new ArrayList<>();

        /*****************************************************************************************/
        /* Aqui se haria la petición al servidor */
        Toast.makeText(getContext(),"Petición GET. Dispositivos favoritos del usuario logueado", Toast.LENGTH_LONG).show();

        List<Device> devices = new ArrayList<Device>();
        deviceArray.add(new Device("Bombilla Salón", TipoDispositivo.LUZ, true));
        deviceArray.add(new Device("Estufa Salón", TipoDispositivo.ESTUFA, true));
        deviceArray.add(new Device("Televisión Salón", TipoDispositivo.TELEVISOR, true));
        deviceArray.add(new Device("Bombilla Salón2", TipoDispositivo.LUZ, true));
        deviceArray.add(new Device("Bombilla Salón3", TipoDispositivo.LUZ, true));


        deviceAdapter = new DeviceAdapter(getContext(), deviceArray);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public void onStart() {
        super.onStart();
        setListAdapter(deviceAdapter);
    }

}

