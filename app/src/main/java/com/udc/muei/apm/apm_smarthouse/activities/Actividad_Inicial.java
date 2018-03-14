package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.udc.muei.apm.apm_smarthouse.R;


public class Actividad_Inicial extends AppCompatActivity {

    Button routineButton;
    Button usuariosRoot;
    Button usuarios;

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



    }
}
