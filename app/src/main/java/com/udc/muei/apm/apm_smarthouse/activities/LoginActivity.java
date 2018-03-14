package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.Actividad_Inicial;
import com.udc.muei.apm.apm_smarthouse.R;

/**
 * Created by DavidPC on 14/03/2018.
 */

public class LoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Context context = getApplicationContext();
        CharSequence text = "Botón en LoginActivity Pulsado!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Intent intent = new Intent(this, Actividad_Inicial.class);
        startActivity(intent);
    }
}
