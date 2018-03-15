package com.udc.muei.apm.apm_smarthouse.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.R;

public class ConfiguracionServidor extends AppCompatActivity implements View.OnClickListener{

    TextView ipAddress;
    TextView port;
    Button cancel;
    Button save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_servidor);

        /* Toolbar de la actividad */
        Toolbar toolbarRoutine = (Toolbar) findViewById(R.id.configuracion_toolbar);
        toolbarRoutine.setTitle(getString(R.string.toolbar_usuarios_name));
        toolbarRoutine.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbarRoutine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Toast.makeText(getApplicationContext(),"Lectura/Modificación de preferencias de la aplicación.", Toast.LENGTH_LONG).show();

        ipAddress = findViewById(R.id.edittext_ip_servidor);
        port = findViewById(R.id.edittext_puerto);
        save = findViewById(R.id.button_save_config);
        cancel = findViewById(R.id.button_cancel_config);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);


    }

    @Override
    public boolean onSupportNavigateUp() {
        Toast.makeText(getApplicationContext(),"Botón Back presionado " , Toast.LENGTH_SHORT).show();
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_save_config:
                if ((ipAddress.getText().length() == 0) ||(port.getText().length() == 0))
                    Toast.makeText(getApplicationContext(), "Introduzca IP y puerto", Toast.LENGTH_LONG).show();
                else
                    saveConfigServer(ipAddress.getText().toString(), port.getText().toString());
                break;
            case R.id.button_cancel_config:
                onBackPressed();
                break;
        }
    }

    private void saveConfigServer(String ipAddress, String port){
        Toast.makeText(getApplicationContext(), "Configuración con IP: "+ ipAddress+" y puerto: "+port+" guardada.", Toast.LENGTH_LONG).show();
    }
}
