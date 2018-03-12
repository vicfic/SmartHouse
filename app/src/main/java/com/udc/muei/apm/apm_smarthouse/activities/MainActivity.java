package com.udc.muei.apm.apm_smarthouse.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ListView;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.DeviceAdapter;
import com.udc.muei.apm.apm_smarthouse.model.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elías on 12/03/2018.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Device> devices = new ArrayList<Device>();
        devices.add(new Device("Bombilla Salón", Device.DEVICE_TYPE_LIGHTBULB, false));
        devices.add(new Device("Estufa Salón", Device.DEVICE_TYPE_STOVE, false));
        devices.add(new Device("Televisión Salón", Device.DEVICE_TYPE_STOVE, false));

        ListView listView = (ListView)findViewById(R.id.listDevices);

        DeviceAdapter adapter = new DeviceAdapter(listView.getContext(), devices);
        listView.setAdapter(adapter);
        // Register the ListView  for Context menu
        registerForContextMenu(listView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Encender");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Apagar");
        menu.add(0, v.getId(), 0, "Ajustar temperatura");
    }

}

