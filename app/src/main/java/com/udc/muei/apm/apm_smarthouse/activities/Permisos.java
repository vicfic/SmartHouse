package com.udc.muei.apm.apm_smarthouse.activities;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.PermisoAdapter;
import com.udc.muei.apm.apm_smarthouse.adapters.PermisoAdapterRoot;
import com.udc.muei.apm.apm_smarthouse.interfaces.PermisoListRootClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.Permiso;
import com.udc.muei.apm.apm_smarthouse.model.Usuario;
import com.udc.muei.apm.apm_smarthouse.model.UsuarioLight;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Permisos extends AppCompatActivity {

    private static final String PERMISOS_TAG = "ACTIVIDAD PERMISOS";
    private ListView permisoList;
    private static PermisoAdapter permisoAdapter;
    private static PermisoAdapterRoot permisoAdapterRoot;
    private ArrayList<Permiso> permisoArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permisos);

        /* Recuperamos la información sobre el usuario seleccionado */
        UsuarioLight usuarioLight =  getIntent().getParcelableExtra("usuarioLight");
        Boolean root =  getIntent().getBooleanExtra("root", false);


        /* Toolbar de la actividad */
        Toolbar toolbarPermisos = findViewById(R.id.permisos_toolbar);
        toolbarPermisos.setTitleTextColor(Color.WHITE);


        if (usuarioLight != null) {
            /* En caso de que el usuarioLight halla sido pasado, se podrá hacer
               una consulta al servidor */
            Log.d(PERMISOS_TAG, "usuarioLight recibido");
            toolbarPermisos.setTitle(getString(R.string.toolbar_permisos_name)+" "+usuarioLight.getName());
            setSupportActionBar(toolbarPermisos);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            /**************************************************************************************/
            /* Aqui se haria la petición al servidor */
            Toast.makeText(getApplicationContext(),"Petición GET. Permisos para el usuario con ID:"+usuarioLight.getIdUsuarioDjango(),Toast.LENGTH_LONG).show();

            /* Fake Data*/
            permisoArray = new ArrayList<>();
            permisoArray.add(new Permiso(1,"luces", true));
            permisoArray.add(new Permiso(2,"televisor", false));
            permisoArray.add(new Permiso(3,"aire acondicionado", true));
            permisoArray.add(new Permiso(4,"rutinas", false));
            permisoArray.add(new Permiso(5,"calefacción", true));
            permisoArray.add(new Permiso(6,"luces", true));
            permisoArray.add(new Permiso(7,"televisor", false));
            permisoArray.add(new Permiso(8,"aire acondicionado", true));
            permisoArray.add(new Permiso(9,"rutinas", false));
            permisoArray.add(new Permiso(10,"calefacción", true));
            permisoArray.add(new Permiso(11,"luces", true));
            permisoArray.add(new Permiso(12,"televisor", false));
            permisoArray.add(new Permiso(13,"aire acondicionado", true));
            permisoArray.add(new Permiso(14,"rutinas", false));
            permisoArray.add(new Permiso(15,"calefacción", true));
            Usuario usuarioJuan = new Usuario(12, "Juan", false,  permisoArray);
            /**************************************************************************************/

            /* Relleno de la lista*/
            permisoList =  findViewById(R.id.permiso_list);

            if (!root) {
                permisoAdapter = new PermisoAdapter(permisoArray, getApplicationContext());
                permisoList.setAdapter(permisoAdapter);
            }else {
                permisoAdapterRoot = new PermisoAdapterRoot(permisoArray, getApplicationContext(), new PermisoListRootClicksListeners() {
                    @Override
                    public void onCheckedBox(int position, boolean isChecked) {
                        Permiso permiso = (Permiso) permisoList.getItemAtPosition(position);
                        if (permiso.getPermiso() != isChecked) {
                            permiso.setPermiso(isChecked);
                            Toast.makeText(getApplicationContext(), "Enviando modificación al servidor. ID PERMISO: " + permiso.getIdPermisoDjango() + " " + (permiso.getPermiso() ? "ACTIVADO" : "DESACTIVADO"), Toast.LENGTH_LONG).show();
                            Log.d(PERMISOS_TAG, "Permiso control " + permiso.getName() + " " + (permiso.getPermiso() ? "activado" : "desactivado"));
                        }
                    }
                });
                permisoList.setAdapter(permisoAdapterRoot);
            }
        }else{
            /* En caso de que el usuarioLight no halla sido pasado, no se mostrará nada en el
            toolbar ni el la listview */
            Log.d(PERMISOS_TAG, "usuarioLight no recibido");
            toolbarPermisos.setTitle(getString(R.string.toolbar_permisos_name_error));
            setSupportActionBar(toolbarPermisos);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Toast.makeText(getApplicationContext(),"Botón Back presionado" , Toast.LENGTH_SHORT).show();
        onBackPressed();
        return true;
    }
}
