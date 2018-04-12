package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.util.NetworkHelper;

public class ComprobacionWifi extends AppCompatActivity {

    Boolean isWifiActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprobacion_wifi);


        Toolbar toolbar = (Toolbar) findViewById(R.id.comprobacion_wifi_toolbar);

        toolbar.setTitle("Comprobación Wifi");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        isWifiActive = NetworkHelper.checkWifiOnAndConnected(getApplicationContext()) != 0;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.EXTRA_SUPPLICANT_CONNECTED);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.EXTRA_NEW_STATE);
        registerReceiver(new NetworkHelper(), intentFilter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
