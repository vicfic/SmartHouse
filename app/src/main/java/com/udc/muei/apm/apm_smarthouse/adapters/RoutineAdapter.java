package com.udc.muei.apm.apm_smarthouse.adapters;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.model.Routine;
import com.udc.muei.apm.apm_smarthouse.interfaces.RoutineListClicksListeners;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


import java.util.ArrayList;



/**
 * Created by José Manuel González on 13/03/2018.
 */


public class RoutineAdapter extends ArrayAdapter<Routine> {

    private RoutineListClicksListeners routineListClicksListeners = null;
    private ArrayList<Routine> dataSet;
    Context mContext;


    private static class ViewHolder {
        TextView name_row;
        Switch check_row;
        Button setting_row;
    }


    public RoutineAdapter(ArrayList<Routine> data, Context context,  RoutineListClicksListeners listener) {
        super(context, R.layout.routine_adapter_layout, data);
        this.dataSet = data;
        this.mContext=context;
        this.routineListClicksListeners = listener;
    }


    public Routine getItem(int position){
        return dataSet.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Routine routine = getItem(position);  //Obtención datos de la rutina
        ViewHolder viewHolder;


        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.routine_adapter_layout, parent, false);

            viewHolder.name_row = (TextView) convertView.findViewById(R.id.routine_name_adapter);
            viewHolder.check_row = (Switch) convertView.findViewById(R.id.routine_switch_adapter);
            viewHolder.setting_row = (Button) convertView.findViewById(R.id.routine_setting_adapter);

            viewHolder.setting_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(routineListClicksListeners != null)
                        routineListClicksListeners.onSettingClick((Integer) v.getTag());
                }
            });
            viewHolder.check_row.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(routineListClicksListeners != null)
                        routineListClicksListeners.onSwitchClick((Integer) buttonView.getTag(), isChecked);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.check_row.setTag(position);
        viewHolder.setting_row.setTag(position);
        viewHolder.name_row.setText(routine.getName());
        viewHolder.check_row.setChecked(routine.getState());
        return convertView;
    }
}
