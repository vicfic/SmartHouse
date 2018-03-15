package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.udc.muei.apm.apm_smarthouse.R;


public class MenuPrincipal extends AppCompatActivity {

    Button routineButton;
    Button usuariosRoot;
    Button usuarios;
    Button actividadElias;
    Button lugares;
    Button configuracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        /* Toolbar de la actividad */
        Toolbar toolbarRoutine = (Toolbar) findViewById(R.id.menu_principal_toolbar);
        TextView titulo_toolbar = findViewById(R.id.toolbar_menu_princiapl_titulo);
        toolbarRoutine.setTitle("");

        titulo_toolbar.setText(getString(R.string.toolbar_menu_principal_name));
        toolbarRoutine.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarRoutine);


        routineButton = (Button) findViewById(R.id.button_routine_list);
        routineButton.setText("Rutinas");
        routineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Rutinas.class);
                startActivity(intent);
            }
        });


        usuarios = (Button) findViewById(R.id.button_usuarios);
        usuarios.setText("Usuarios");
        usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Usuarios.class);
                i.putExtra("root", false);
                startActivity(i);

            }
        });

        usuariosRoot = (Button) findViewById(R.id.button_usuarios_root);
        usuariosRoot.setText("Usuarios (ver como ROOT)");
        usuariosRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Usuarios.class);
                i.putExtra("root", true);
                startActivity(i);

            }
        });

        actividadElias = (Button) findViewById(R.id.button_elias);
        actividadElias.setText("Favoritos");
        actividadElias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Favoritos.class);

                startActivity(i);

            }
        });

        lugares = (Button) findViewById(R.id.button_lugares);
        lugares.setText("Lugares");
        lugares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Lugares.class);

                startActivity(i);

            }
        });

        configuracion = (Button) findViewById(R.id.button_configuracion);
        configuracion.setText("Configuración");
        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),ConfiguracionServidor.class);

                startActivity(i);

            }
        });



    }
}
