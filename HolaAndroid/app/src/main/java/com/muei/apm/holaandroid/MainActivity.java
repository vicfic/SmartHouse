package com.muei.apm.holaandroid;

import android.app.ListActivity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

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
