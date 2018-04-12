package com.udc.muei.apm.apm_smarthouse.activities;

import com.udc.muei.apm.apm_smarthouse.adapters.UsuarioAdapter;
import com.udc.muei.apm.apm_smarthouse.model.UsuarioLight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Usuarios extends AppCompatActivity {

    private static final String USUARIOS_TAG = "ACTIVIDAD USUARIOS";
    private ListView usuariosList;
    private static UsuarioAdapter usuariosAdapter;
    private ArrayList<UsuarioLight> usuariosArray;
    private Boolean root = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_usuarios);

        root =  getIntent().getBooleanExtra("root", false);

        /* Toolbar de la actividad */
        /*Toolbar toolbarRoutine = (Toolbar) findViewById(R.id.usuarios_toolbar);
        toolbarRoutine.setTitle(getString(R.string.toolbar_usuarios_name));
        toolbarRoutine.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarRoutine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /* Lista de usuarios 'light' */
        /*usuariosList= findViewById(R.id.usuarios_list);
        usuariosArray= new ArrayList<>();

        /*****************************************************************************************/
        /* Aqui se haria la petición al servidor */
        /*Toast.makeText(getApplicationContext(),"Petición GET. Lista de usuarios del sistema",Toast.LENGTH_LONG).show();

        /* Fake Data */
        /*usuariosArray.add(new UsuarioLight(1, "Victor"));
        usuariosArray.add(new UsuarioLight(2, "Manuel"));
        usuariosArray.add(new UsuarioLight(3, "Elias"));
        usuariosArray.add(new UsuarioLight(4, "David"));
        usuariosArray.add(new UsuarioLight(5, "Gabriel"));
        usuariosArray.add(new UsuarioLight(6, "Victor"));
        usuariosArray.add(new UsuarioLight(7, "Manuel"));
        usuariosArray.add(new UsuarioLight(8, "Elias"));
        usuariosArray.add(new UsuarioLight(9, "David"));
        usuariosArray.add(new UsuarioLight(10, "Gabriel"));
        /*****************************************************************************************/

        /*usuariosAdapter = new UsuarioAdapter(usuariosArray, getApplicationContext(), new UsuarioListClicksListeners() {
            @Override
            public void onButtonUserClick(int position) {
                UsuarioLight usuarioLight = (UsuarioLight) usuariosList.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"Usuario "+ usuarioLight.getName() +" pulsado",Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(), Permisos.class);
                i.putExtra("usuarioLight", usuarioLight);
                i.putExtra("root", root);
                startActivity(i);
            }
        });
        usuariosList.setAdapter(usuariosAdapter);*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        Toast.makeText(getApplicationContext(),"Botón Back presionado " , Toast.LENGTH_SHORT).show();
        onBackPressed();
        return true;
    }
}
