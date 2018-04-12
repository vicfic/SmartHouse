package com.udc.muei.apm.apm_smarthouse.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.activities.Permisos;
import com.udc.muei.apm.apm_smarthouse.adapters.UsuarioAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.UsuarioListClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.UsuarioLight;

import java.util.ArrayList;

public class Usuarios extends android.support.v4.app.ListFragment implements AdapterView.OnItemClickListener {

    private static final String USUARIOS_TAG = "ACTIVIDAD LUGARES";
    private static UsuarioAdapter usuarioAdapter;
    private ArrayList<UsuarioLight> usuarioArray;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Lista de rutinas */
        usuarioArray = new ArrayList<>();

        /* ************************************************************************************** */
        /* Fake Data */
        usuarioArray.add(new UsuarioLight(1, "Victor"));
        usuarioArray.add(new UsuarioLight(2, "Manuel"));
        usuarioArray.add(new UsuarioLight(3, "Elias"));
        usuarioArray.add(new UsuarioLight(4, "David"));
        usuarioArray.add(new UsuarioLight(5, "Gabriel"));
        usuarioArray.add(new UsuarioLight(6, "Victor"));
        usuarioArray.add(new UsuarioLight(7, "Manuel"));
        usuarioArray.add(new UsuarioLight(8, "Elias"));
        usuarioArray.add(new UsuarioLight(9, "David"));
        usuarioArray.add(new UsuarioLight(10, "Gabriel"));
        /* ************************************************************************************** */

        usuarioAdapter = new UsuarioAdapter(usuarioArray, getContext(), new UsuarioListClicksListeners() {
            @Override
            public void onButtonUserClick(int position) {
                UsuarioLight usuarioLight = (UsuarioLight) getListView().getItemAtPosition(position);
                Toast.makeText(getContext(),"Usuario "+ usuarioLight.getName() +" pulsado",Toast.LENGTH_LONG).show();

                Intent i = new Intent(getContext(), Permisos.class);
                i.putExtra("usuarioLight", usuarioLight);
                i.putExtra("root", true);
                startActivity(i);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        /* ************************************************************************************** */
        /* Aqui se haria la petición al servidor */
        Toast.makeText(getContext(),"Petición GET. Usuarios:",Toast.LENGTH_LONG).show();
        setListAdapter(usuarioAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
