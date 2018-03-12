package com.muei.apm.holaandroid;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Elías on 26/02/2018.
 */


public class DeviceAdapter extends BaseAdapter {

    private Context context; //context
    private List<Device> deviceList;

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
        View v = convertView;
        int type = getItemViewType(position);
        if (v == null) {
            // Inflate the layout according to the view type
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (type == 0) {
                // Inflate the layout with switch
                v = inflater.inflate(R.layout.list_view_switch, parent, false);
            }
            else {
                v = inflater.inflate(R.layout.list_view_text, parent, false);
            }
            Device d = deviceList.get(position);
            TextView textViewItemName = (TextView)
                    v.findViewById(R.id.deviceName);
            textViewItemName.setText(d.getName());

            final ImageButton ButtonStar = (ImageButton) v.findViewById(R.id.favorite);
            final boolean favorite = d.isFavorite();
            final int position_final = position;
            ButtonStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(favorite) {
                        ButtonStar.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.btn_star_big_off));
                    }else{
                        ButtonStar.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.btn_star_big_on));
                    }
                    favoriteDevice(position_final);
                }
            });
        }
        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (deviceList.get(position).getType() == Device.DEVICE_TYPE_LIGHTBULB) ? 0 : 1;
    }

    private void favoriteDevice(int position){
        Device d = deviceList.get(position);
        d.setFavorite(!(d.isFavorite()));
        deviceList.set(position, d);
    }
}
