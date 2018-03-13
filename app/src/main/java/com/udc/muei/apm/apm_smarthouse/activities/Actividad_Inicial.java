package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.activities.*;

public class Actividad_Inicial extends AppCompatActivity {

    Button routineList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad__inicial);
    routineList = (Button) findViewById(R.id.button_routine_list);
    routineList.setText("Rutinas");
    routineList.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), Rutinas.class);
            startActivity(intent);
        }
    });

    }
}
