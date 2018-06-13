package com.udc.muei.apm.apm_smarthouse.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.model.NavigationOption;
import com.udc.muei.apm.apm_smarthouse.model.TipoNavigationOption;

import java.util.List;

/**
 * Created by Elías on 01/04/2018.
 */

public class NavigationBarAdapter extends BaseAdapter {

    private static final String ADAPTER_NAVIGATION_TAG = "ADAPTER NAVIGATION";
    private Context context; //context
    private List<NavigationOption> navigationList;
    private TextView textViewItemName;

    //public constructor
    public NavigationBarAdapter(Context context, List<NavigationOption> items) {
        this.context = context;
        this.navigationList = items;
    }

    @Override
    public int getCount() {
        return navigationList.size();
    }

    @Override
    public Object getItem(int position) {
        return navigationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NavigationOption navigationOption = navigationList.get(position);
        if (convertView == null) {
            // Inflate the layout according to the view type
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (navigationOption.getType() == TipoNavigationOption.IMAGEN) {
                convertView = inflater.inflate(R.layout.list_view_navigation_image, parent, false);
            }else {
                convertView = inflater.inflate(R.layout.list_view_navigation_text, parent, false);
                textViewItemName = convertView.findViewById(R.id.navigationName);
                textViewItemName.setText(navigationOption.getName());
            }
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
