
package com.udc.muei.apm.apm_smarthouse.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.AsyncTasks.HttpsRequestAsyncTask;
import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;
import com.udc.muei.apm.apm_smarthouse.model.Device;
import com.udc.muei.apm.apm_smarthouse.model.TipoDispositivo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.udc.muei.apm.apm_smarthouse.util.Constants.MAP_PETICIONES;

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

            if ((device.getType() == TipoDispositivo.LUZ)
                    ||(device.getType() == TipoDispositivo.ESTUFA)
                    || (device.getType() == TipoDispositivo.TELEVISOR)){
                convertView = inflater.inflate(R.layout.list_view_switch, parent, false);
                switch_device = convertView.findViewById(R.id.switch_device);
                switch_device.setChecked(device.isActive());
                switch_device.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Device device = deviceList.get((Integer) buttonView.getTag());
                        onoffDispositivo(device, isChecked, buttonView);

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
                    final MediaPlayer mp = MediaPlayer.create(context, R.raw.fav_button);
                    mp.start();
                    Device device = deviceList.get((Integer) view.getTag());
                    if(device.isFavorite()) {
                        device.setFavorite(false);
                        button_fav.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.btn_star_big_off));
                        Toast.makeText(view.getContext()," Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                        modificarFavorito(device, false);
                    }else{
                        device.setFavorite(true);
                        button_fav.setImageDrawable(ContextCompat.getDrawable(context,android.R.drawable.btn_star_big_on));
                        Toast.makeText(view.getContext()," Añadido a favoritos", Toast.LENGTH_SHORT).show();
                        modificarFavorito(device, true);
                    }
                }
            });
            if ((device.getType() == TipoDispositivo.LUZ)
                    ||(device.getType() == TipoDispositivo.ESTUFA)
                    || (device.getType() == TipoDispositivo.TELEVISOR)){
                switch_device.setTag(position);
            }
            button_fav.setTag(position);
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void modificarFavorito(Device device, Boolean estado){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);

        JSONObject parametrosPeticion = new JSONObject();
        try {
            parametrosPeticion.put("tokenId", sharedPref.getString(context.getString(R.string.key_token_id),""));
            parametrosPeticion.put("dispositivoId", device.getId_Django());
            parametrosPeticion.put("accion", estado);
            HttpsRequestAsyncTask task = new HttpsRequestAsyncTask(context, new HttpsRequestResult() {
                @Override
                public void processFinish(String result) {
                    Log.d(ADAPTER_DEVICES_TAG, result);
                }
            });

                    task.execute(MAP_PETICIONES.get("ModificarFavorito"), parametrosPeticion.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void onoffDispositivo(Device device, final Boolean isChecked, final CompoundButton buttonView){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);

        JSONObject parametrosPeticion = new JSONObject();
        try {
            parametrosPeticion.put("tokenId", sharedPref.getString(context.getString(R.string.key_token_id),""));
            parametrosPeticion.put("dispositivoId", device.getId_Django());
            JSONArray acciones = new JSONArray();
            JSONObject accion = new JSONObject();
            accion.put("tipoAccion", 1);
            accion.put("valor", isChecked);
            acciones.put(accion);
            parametrosPeticion.put("acciones", acciones);
            HttpsRequestAsyncTask task = new HttpsRequestAsyncTask(context, new HttpsRequestResult() {
                @Override
                public void processFinish(String result) {
                    try {
                        Log.d(ADAPTER_DEVICES_TAG, result);
                        JSONObject resultJSON = new JSONObject(result);

                        if (resultJSON.getBoolean("error")){
                            if (resultJSON.getBoolean("forbidden")){
                                boolean last_state = (resultJSON.getInt("estado")!=0);
                                buttonView.setChecked(last_state);
                                Toast.makeText(context," No tiene permiso para realizar la acción", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context," Error durante la conexión", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            task.execute(MAP_PETICIONES.get("ModificarDispositivo"), parametrosPeticion.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}