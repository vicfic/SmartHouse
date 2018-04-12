package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.R;


public class ComprobacionGPS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprobacion_gps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.comprobacion_gps_toolbar);

        toolbar.setTitle("Comprobación Posición");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button gpsButton = (Button) findViewById(R.id.button_comprobar_gps);
        gpsButton.setText("GPS");
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoordenadasGPS();
            }
        });
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


    /**
     * Comprueba si el gps está activado y obtiene las coordenadas geográficas
     */
    public void getCoordenadasGPS() {
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error al comprobar el estado del GPS_PROVIDER", Toast.LENGTH_LONG).show();
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error al comprobar el estado del NETWORK_PROVIDER", Toast.LENGTH_LONG).show();
        }

        if (!gps_enabled && !network_enabled) {
            buildAlertMessageNoGps();
        }


        Location location = null;
        double latitude;
        double longitude;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PackageManager.PERMISSION_GRANTED);
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PackageManager.PERMISSION_GRANTED);
        }

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            TextView longitud = findViewById(R.id.textview_longitud);
            TextView latitud = findViewById(R.id.textview_latitud);
            longitud.setText(location.getLongitude()+"");
            latitud.setText(location.getLatitude()+"");
        }
    }

    /**
     * Muestra el mensaje de que no está activado el gps y permite activarlo
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El GPS está desactivado ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
