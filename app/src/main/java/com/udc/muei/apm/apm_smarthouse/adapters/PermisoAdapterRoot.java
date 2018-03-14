package com.udc.muei.apm.apm_smarthouse.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import android.widget.Switch;
import android.widget.TextView;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.interfaces.PermisoListRootClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.Permiso;


import java.util.ArrayList;

/**
 * Created by José Manuel González on 14/03/2018.
 */

public class PermisoAdapterRoot extends ArrayAdapter<Permiso> {

    private static final String PERMISOS_TAG = "ACTIVIDAD PERMISOS";
    private PermisoListRootClicksListeners permisoListRootClicksListeners = null;
    private ArrayList<Permiso> dataSet;
    Context mContext;


    private static class ViewHolder {
        TextView nameroot_row;
        Switch switch_row;
    }

    public PermisoAdapterRoot(ArrayList<Permiso> data, Context context, PermisoListRootClicksListeners listener) {
        super(context, R.layout.permiso_root_adapter_layout, data);
        this.dataSet = data;
        this.mContext=context;
        this.permisoListRootClicksListeners = listener;
    }

    public Permiso getItem(int position){
        return dataSet.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Permiso permiso = getItem(position);  //Obtención datos del permiso
        PermisoAdapterRoot.ViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new PermisoAdapterRoot.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.permiso_root_adapter_layout, parent, false);

            viewHolder.nameroot_row = convertView.findViewById(R.id.permiso_name_root_adapter);
            viewHolder.switch_row = convertView.findViewById(R.id.permiso_switch_root_adapter);

            viewHolder.switch_row.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (permisoListRootClicksListeners != null){

                        permisoListRootClicksListeners.onCheckedBox((Integer) buttonView.getTag(), isChecked);
                    }
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PermisoAdapterRoot.ViewHolder) convertView.getTag();
        }

        viewHolder.switch_row.setTag(position);

        viewHolder.nameroot_row.setText(permiso.getName());
        viewHolder.switch_row.setChecked(permiso.getPermiso());


        return convertView;
    }
}