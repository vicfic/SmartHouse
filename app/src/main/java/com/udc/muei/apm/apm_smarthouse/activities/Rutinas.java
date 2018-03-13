package com.udc.muei.apm.apm_smarthouse.activities;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.adapters.RoutineAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.RoutineListClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.Routine;

import java.util.ArrayList;

public class Rutinas extends AppCompatActivity {

    private static final String RUTINAS_TAG = "ACTIVIDAD RUTINAS";
    private ListView routineList;
    private static RoutineAdapter routineAdapter;
    private ArrayList<Routine> routineArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutinas);

        /* Toolbar de la actividad */
        Toolbar toolbarRoutine = (Toolbar) findViewById(R.id.routine_toolbar);
        toolbarRoutine.setTitle(getString(R.string.toolbar_routine_name));
        toolbarRoutine.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarRoutine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /* Lista de rutinas */
        routineList =(ListView)findViewById(R.id.routine_list);
        routineArray= new ArrayList<>();


        /******************************************************************************************/
        /* Esta información está introducida manualmente de momento, a posterior, estos datos serán
        enviados por el servidor */
        routineArray.add(new Routine("Luces", true));
        routineArray.add(new Routine("Calefacción", false));
        routineArray.add(new Routine("Televisor", false));
        routineArray.add(new Routine("Desumificador", true));
        /******************************************************************************************/

        routineAdapter= new RoutineAdapter(routineArray, getApplicationContext(), new RoutineListClicksListeners() {
            @Override
            public void onSettingClick(int position) {
                Routine routine = (Routine) routineList.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),"Rutina "+ routine.getName() +". Botón configuración pulsado" , Toast.LENGTH_SHORT).show();
                Log.d(RUTINAS_TAG, "Rutina "+ routine.getName() +". Botón configuración pulsado");
            }

            @Override
            public void onSwitchClick(int position, boolean isChecked) {
                Routine routine = (Routine) routineList.getItemAtPosition(position);
                if (routine.getState() != isChecked) {
                    routine.setState(isChecked);
                    Toast.makeText(getApplicationContext(),"Rutina "+ routine.getName() +" "+ (routine.getState()?"activada":"desactivada") , Toast.LENGTH_SHORT).show();
                    Log.d(RUTINAS_TAG, "Rutina "+ routine.getName() +" "+ (routine.getState()?"activada":"desactivada") );
                }
            }
        });
        routineList.setAdapter(routineAdapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        Toast.makeText(getApplicationContext(),"Botón Back presionado " , Toast.LENGTH_SHORT).show();
        onBackPressed();
        return true;
    }

}
