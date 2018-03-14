package com.udc.muei.apm.apm_smarthouse.adapters;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.interfaces.UsuarioListClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.UsuarioLight;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;


import java.util.ArrayList;



/**
 * Created by José Manuel González on 14/03/2018.
 */


public class UsuarioAdapter extends ArrayAdapter<UsuarioLight> {

    private UsuarioListClicksListeners usuarioListClicksListeners = null;
    private ArrayList<UsuarioLight> dataSet;
    Context mContext;


    private static class ViewHolder {

        Button button_row;
    }


    public UsuarioAdapter(ArrayList<UsuarioLight> data, Context context,  UsuarioListClicksListeners listener) {
        super(context, R.layout.usuario_adapter_layout, data);
        this.dataSet = data;
        this.mContext=context;
        this.usuarioListClicksListeners = listener;
    }


    public UsuarioLight getItem(int position){
        return dataSet.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UsuarioLight usuarioLight = getItem(position);  //Obtención datos del usuario light
        ViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.usuario_adapter_layout, parent, false);

            viewHolder.button_row = convertView.findViewById(R.id.usuario_button_adapter);
            viewHolder.button_row.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), R.color.colorPrimary));
            viewHolder.button_row.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.white));
            viewHolder.button_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(usuarioListClicksListeners != null)
                        usuarioListClicksListeners.onButtonUserClick((Integer) v.getTag());
                }
            });


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.button_row.setTag(position);
        viewHolder.button_row.setText(usuarioLight.getName());
        return convertView;
    }
}
