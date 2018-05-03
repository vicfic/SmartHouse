
package com.udc.muei.apm.apm_smarthouse.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.model.Device;
import com.udc.muei.apm.apm_smarthouse.model.TipoDispositivo;

import java.util.List;

/**
 * Created by Elías on 12/03/2018.
 */

public class DeviceAdapter extends BaseAdapter {


    private static final String ADAPTER_DEVICES_TAG = "ADAPTER DEVICES";
    private Context context; //context
    private List<Device> deviceList;
    private TextView textViewItemName;
    private Switch switch_device;

    //public constructor
    public DeviceAdapter(Context context, List<Device> items) {
        this.context = context;
        this.deviceList = items;
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Device device = deviceList.get(position);
        if (convertView == null) {
            // Inflate the layout according to the view type
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (device.getType() == TipoDispositivo.LUZ) {
                convertView = inflater.inflate(R.layout.list_view_switch, parent, false);
                switch_device = convertView.findViewById(R.id.switch_device);
                switch_device.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Device device = deviceList.get((Integer) buttonView.getTag());
                        //FIXME Hemos detectado que al seleccionar un switch, muestra el nombre de un dispositivo que no es el actual
                        Toast.makeText(buttonView.getContext(), " Switch de "+ device.getName()+" "+ (isChecked?"activado":"desactivado"+". Enviando POST al servidor"), Toast.LENGTH_LONG).show();
                    }
                });
                switch_device.setTag(position);
            }else {
                convertView = inflater.inflate(R.layout.list_view_text, parent, false);
            }
            textViewItemName = convertView.findViewById(R.id.deviceName);
            textViewItemName.setText(device.getName());

            final ImageButton button_fav;
            button_fav = convertView.findViewById(R.id.favorite);
            if(!device.isFavorite()) {
                button_fav.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.btn_star_big_off));
            }else{
                button_fav.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.btn_star_big_on));
            }
            button_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Device device = deviceList.get((Integer) view.getTag());
                    if(device.isFavorite()) {
                        device.setFavorite(false);
                        button_fav.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.btn_star_big_off));
                        Toast.makeText(view.getContext(),device.getName()+" eliminado de favoritos. POST al servidor", Toast.LENGTH_LONG).show();
                    }else{
                        device.setFavorite(true);
                        button_fav.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.btn_star_big_on));
                        Toast.makeText(view.getContext(),device.getName()+" incluido en favoritos. POST al servidor", Toast.LENGTH_LONG).show();
                    }
                }
            });
            switch_device.setTag(position);
            button_fav.setTag(position);

        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}