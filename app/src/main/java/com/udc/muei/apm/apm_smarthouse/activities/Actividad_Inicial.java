package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.activities.*;
import com.udc.muei.apm.apm_smarthouse.model.Permiso;
import com.udc.muei.apm.apm_smarthouse.model.Usuario;
import com.udc.muei.apm.apm_smarthouse.model.UsuarioLight;

import java.util.ArrayList;

public class Actividad_Inicial extends AppCompatActivity {

    Button routineButton;
    Button permisosJuanButton;
    Button permisosJuanButtonROOT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad__inicial);
        routineButton = (Button) findViewById(R.id.button_routine_list);
        routineButton.setText("Rutinas");
        routineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Rutinas.class);
                startActivity(intent);
            }
        });


        permisosJuanButton = (Button) findViewById(R.id.button_permisos_juan);
        permisosJuanButton.setText("Permisos de Juan");
        permisosJuanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se simula que se selecciona un usuario en la lista de usuarios
                UsuarioLight JuanLight = new UsuarioLight(12,"Juan");

                Intent i = new Intent(v.getContext(), Permisos.class);
                i.putExtra("usuarioLight", JuanLight);
                i.putExtra("root", false);
                startActivity(i);

            }
        });

        permisosJuanButtonROOT = (Button) findViewById(R.id.button_permisos_juan_root);
        permisosJuanButtonROOT.setText("Permisos de Juan (Modificables)");
        permisosJuanButtonROOT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se simula que se selecciona un usuario en la lista de usuarios
                UsuarioLight JuanLight = new UsuarioLight(12,"Juan");

                Intent i = new Intent(v.getContext(), Permisos.class);
                i.putExtra("usuarioLight", JuanLight);
                i.putExtra("root", true);
                startActivity(i);

            }
        });



    }
}
