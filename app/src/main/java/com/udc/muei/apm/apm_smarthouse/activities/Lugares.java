package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.LugarAdapter;
import com.udc.muei.apm.apm_smarthouse.adapters.RoutineAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.LugarListClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.Lugar;
import com.udc.muei.apm.apm_smarthouse.model.Routine;
import com.udc.muei.apm.apm_smarthouse.model.UsuarioLight;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Lugares extends AppCompatActivity {

    private static final String LUGARES_TAG = "ACTIVIDAD LUGARES";
    private ListView lugaresList;
    private static LugarAdapter lugaresAdapter;
    private ArrayList<Lugar> lugarArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares);

        /* Toolbar de la actividad */
        Toolbar toolbarRoutine = (Toolbar) findViewById(R.id.lugares_toolbar);
        toolbarRoutine.setTitle(getString(R.string.toolbar_lugares_name));
        toolbarRoutine.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarRoutine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /* Lista de rutinas */
        lugaresList = findViewById(R.id.lugares_list);
        lugarArray = new ArrayList<>();


        /******************************************************************************************/
        /* Aqui se haria la petición al servidor */
        Toast.makeText(getApplicationContext(),"Petición GET. Lugares disponibles en la casa:",Toast.LENGTH_LONG).show();

        /* Esta información está introducida manualmente de momento, a posterior, estos datos serán
        enviados por el servidor */

        lugarArray.add(new Lugar("Salón", 1));
        lugarArray.add(new Lugar("Cocina", 2));
        lugarArray.add(new Lugar("Baño", 3));
        lugarArray.add(new Lugar("Despacho", 4));
        lugarArray.add(new Lugar("Habitación Principal", 5));
        lugarArray.add(new Lugar("Habitación invitados", 6));
        lugarArray.add(new Lugar("Garaje", 7));
        lugarArray.add(new Lugar("Jardín", 8));
        /******************************************************************************************/

        lugaresAdapter = new LugarAdapter(lugarArray, getApplicationContext(), new LugarListClicksListeners() {
            @Override
            public void onButtonLugaresClick(int position) {
                Lugar lugar = (Lugar) lugaresList.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"Lugar "+ lugar.getName() +" seleccionado",Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(), DispositivosLugar.class);
                i.putExtra("lugar", lugar);
                startActivity(i);
            }
        });



        lugaresList.setAdapter(lugaresAdapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        Toast.makeText(getApplicationContext(),"Botón Back presionado " , Toast.LENGTH_SHORT).show();
        onBackPressed();
        return true;
    }
}
