package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.util.Constants;
import com.udc.muei.apm.apm_smarthouse.broadcastReceivers.NotificationWifiReceiver;

public class ComprobacionWifi extends AppCompatActivity {

    Boolean isWifiActive;
    private CheckBox checkBox_auto_luces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprobacion_wifi);

        Toolbar toolbar = (Toolbar) findViewById(R.id.comprobacion_wifi_toolbar);

        toolbar.setTitle(getString(R.string.titulo_rutina_luces));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);


        /** Configuración del checkbox de opción automática*/
        checkBox_auto_luces = findViewById(R.id.checkBox_auto_luces);
        Boolean auto_luces = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.OPTION_AUTO_LUCES_KEY, false);
        checkBox_auto_luces.setChecked(auto_luces);
        checkBox_auto_luces.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(isChecked);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putBoolean(Constants.OPTION_AUTO_LUCES_KEY, isChecked)
                        .apply();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
