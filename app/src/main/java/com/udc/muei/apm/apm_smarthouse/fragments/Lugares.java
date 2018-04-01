package com.udc.muei.apm.apm_smarthouse.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.activities.DispositivosLugar;
import com.udc.muei.apm.apm_smarthouse.adapters.LugarAdapter;
import com.udc.muei.apm.apm_smarthouse.adapters.RoutineAdapter;
import com.udc.muei.apm.apm_smarthouse.interfaces.LugarListClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.Lugar;
import com.udc.muei.apm.apm_smarthouse.model.Routine;
import com.udc.muei.apm.apm_smarthouse.model.UsuarioLight;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Lugares extends android.support.v4.app.ListFragment implements AdapterView.OnItemClickListener {

    private static final String LUGARES_TAG = "ACTIVIDAD LUGARES";
    private static LugarAdapter lugaresAdapter;
    private ArrayList<Lugar> lugarArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Lista de rutinas */
        lugarArray = new ArrayList<>();


        /******************************************************************************************/
        /* Aqui se haria la petición al servidor */
        Toast.makeText(getContext(),"Petición GET. Lugares disponibles en la casa:",Toast.LENGTH_LONG).show();

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

        lugaresAdapter = new LugarAdapter(lugarArray, getContext(), new LugarListClicksListeners() {
            @Override
            public void onButtonLugaresClick(int position) {
                Lugar lugar = (Lugar) getListView().getItemAtPosition(position);
                Toast.makeText(getContext(),"Lugar "+ lugar.getName() +" seleccionado",Toast.LENGTH_LONG).show();

                Intent i = new Intent(getContext(), DispositivosLugar.class);
                i.putExtra("lugar", lugar);
                startActivity(i);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        setListAdapter(lugaresAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
