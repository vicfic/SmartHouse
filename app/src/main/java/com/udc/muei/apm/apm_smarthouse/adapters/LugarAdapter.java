package com.udc.muei.apm.apm_smarthouse.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.interfaces.LugarListClicksListeners;
import com.udc.muei.apm.apm_smarthouse.model.Lugar;

import java.util.ArrayList;

/**
 * Created by José Manuel González on 15/03/2018.
 */

public class LugarAdapter extends ArrayAdapter<Lugar> {

    private LugarListClicksListeners lugarListClicksListeners = null;
    private ArrayList<Lugar> dataSet;
    Context mContext;


    private static class ViewHolder {
        Button button_row;
    }


    public LugarAdapter(ArrayList<Lugar> data, Context context, LugarListClicksListeners listener) {
        super(context, R.layout.lugar_adapter_layout, data);
        this.dataSet = data;
        this.mContext=context;
        this.lugarListClicksListeners = listener;
    }


    public Lugar getItem(int position){
        return dataSet.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Lugar lugar  = getItem(position);  //Obtención datos del usuario light
        LugarAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new LugarAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.lugar_adapter_layout, parent, false);

            viewHolder.button_row = convertView.findViewById(R.id.lugar_button_adapter);
            viewHolder.button_row.setBackgroundColor(ContextCompat.getColor(convertView.getContext(), R.color.colorPrimary));
            viewHolder.button_row.setTextColor(ContextCompat.getColor(convertView.getContext(), R.color.white));
            viewHolder.button_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lugarListClicksListeners != null)
                        lugarListClicksListeners.onButtonLugaresClick((Integer) v.getTag());
                }
            });


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LugarAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.button_row.setTag(position);
        viewHolder.button_row.setText(lugar.getName());
        return convertView;
    }
}
