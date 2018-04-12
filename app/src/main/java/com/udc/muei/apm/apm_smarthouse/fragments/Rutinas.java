package com.udc.muei.apm.apm_smarthouse.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;


import com.udc.muei.apm.apm_smarthouse.activities.Luces;
import com.udc.muei.apm.apm_smarthouse.adapters.RoutineAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.RoutineListClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.Routine;

import java.util.ArrayList;

public class Rutinas extends android.support.v4.app.ListFragment implements AdapterView.OnItemClickListener {

    private static final String RUTINAS_TAG = "ACTIVIDAD RUTINAS";
    private static RoutineAdapter routineAdapter;
    private ArrayList<Routine> routineArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Lista de rutinas */
        routineArray= new ArrayList<>();


        /******************************************************************************************/
        /* Esta información está introducida manualmente de momento, a posterior, estos datos serán
        enviados por el servidor */
        routineArray.add(new Routine("Luces", true, new Intent(getContext(), Luces.class)));
        routineArray.add(new Routine("Calefacción", false));
        routineArray.add(new Routine("Televisor", false));
        routineArray.add(new Routine("Desumificador", true));
        /******************************************************************************************/

        routineAdapter= new RoutineAdapter(routineArray, getContext(), new RoutineListClicksListeners() {
            @Override
            public void onSettingClick(int position) {
                Routine routine = (Routine) getListView().getItemAtPosition(position);
                Toast.makeText(getContext(),"Rutina "+ routine.getName() +". Botón configuración pulsado" , Toast.LENGTH_SHORT).show();
                Log.d(RUTINAS_TAG, "Rutina "+ routine.getName() +". Botón configuración pulsado");
                if(routine.getIntent()!=null)
                    startActivity(routine.getIntent());
            }

            @Override
            public void onSwitchClick(int position, boolean isChecked) {
                Routine routine = (Routine) getListView().getItemAtPosition(position);
                if (routine.getState() != isChecked) {
                    routine.setState(isChecked);
                    Toast.makeText(getContext(),"Rutina "+ routine.getName() +" "+ (routine.getState()?"activada":"desactivada") , Toast.LENGTH_SHORT).show();
                    Log.d(RUTINAS_TAG, "Rutina "+ routine.getName() +" "+ (routine.getState()?"activada":"desactivada") );
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        setListAdapter(routineAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
