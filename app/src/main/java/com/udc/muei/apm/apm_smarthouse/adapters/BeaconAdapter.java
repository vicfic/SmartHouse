package com.udc.muei.apm.apm_smarthouse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.model.BeaconCustom;
import com.udc.muei.apm.apm_smarthouse.model.Device;

import java.util.List;

/**
 * Created by José Manuel González on 12/04/2018.
 */

public class BeaconAdapter extends BaseAdapter {

    private static final String ADAPTER_BEACONS_TAG = "ADAPTER BEACONS";
    private Context context; //context
    private List<BeaconCustom> beaconList;

    public BeaconAdapter(Context context, List<BeaconCustom> deviceList) {
        this.context = context;
        this.beaconList = deviceList;
    }

    @Override
    public int getCount() {
        return beaconList.size();
    }

    @Override
    public Object getItem(int position) {
        return beaconList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BeaconCustom beaconCustom = beaconList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.list_view_beacon, parent, false);

            TextView textViewUuid = convertView.findViewById(R.id.textview_uuid);
            TextView textViewIdGrupo = convertView.findViewById(R.id.textview_idgrupo);
            TextView textViewIdBeacon = convertView.findViewById(R.id.textview_idbeacon);
            TextView textViewDistancia = convertView.findViewById(R.id.textview_distancia);
            TextView textViewRango = convertView.findViewById(R.id.textview_rango);
            LinearLayout indicador = convertView.findViewById(R.id.indicador);
            TextView nombre_lugar = convertView.findViewById(R.id.textview_nombre_lugar);

            nombre_lugar.setText(beaconCustom.getNombre_lugar());
            textViewUuid.setText(beaconCustom.getUuid());
            textViewIdGrupo.setText(beaconCustom.getIdGrupo());
            textViewIdBeacon.setText(beaconCustom.getIdBeacon());
            textViewDistancia.setText( beaconCustom.getDistance()+"");
            textViewRango.setText(beaconCustom.getDistanciaRango()+"");
            if(beaconCustom.getDistance()<=beaconCustom.getDistanciaRango()){
                if(beaconCustom.getDistance()<0)
                    indicador.setBackgroundColor(context.getColor(R.color.indicador_gris_suave));
                else
                    indicador.setBackgroundColor(context.getColor(R.color.indicador_verde_suave));
            }else
                indicador.setBackgroundColor(context.getColor(R.color.indicador_rojo_suave));
        }
        return convertView;
    }
}
