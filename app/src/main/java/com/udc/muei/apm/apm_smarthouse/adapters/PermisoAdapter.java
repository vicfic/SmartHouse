package com.udc.muei.apm.apm_smarthouse.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.TextView;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.model.Permiso;


import java.util.ArrayList;

/**
        * Created by José Manuel González on 14/03/2018.
        */

public class PermisoAdapter extends ArrayAdapter<Permiso> {

    //private PermisoListClicksListeners permisoListClicksListeners = null;
    private ArrayList<Permiso> dataSet;
    Context mContext;


    private static class ViewHolder {
        TextView name_row;
        Button check_row;
    }

    public PermisoAdapter(ArrayList<Permiso> data, Context context) {
        super(context, R.layout.permiso_adapter_layout, data);
        this.dataSet = data;
        this.mContext=context;
        //this.routineListClicksListeners = listener;
    }

    public Permiso getItem(int position){
        return dataSet.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Permiso permiso = getItem(position);  //Obtención datos del permiso
        PermisoAdapter.ViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new PermisoAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.permiso_adapter_layout, parent, false);

            viewHolder.name_row = convertView.findViewById(R.id.permiso_name_adapter);
            viewHolder.check_row = convertView.findViewById(R.id.permiso_check_adapter);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PermisoAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.check_row.setTag(position);

        viewHolder.name_row.setText(permiso.getName());

        if (permiso.getPermiso()){
            viewHolder.check_row.setBackgroundResource(R.drawable.ic_check_black_24dp);
        }else{
            viewHolder.check_row.setBackgroundResource(R.drawable.ic_close_black_24dp);
        }

        return convertView;
    }
}
