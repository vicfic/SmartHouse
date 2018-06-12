package com.udc.muei.apm.apm_smarthouse.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.udc.muei.apm.apm_smarthouse.R;


public class Eula extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eula);


        Button agree = findViewById(R.id.button_agree_eula);
        Button cancel = findViewById(R.id.button_cancel_eula);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.key_eula_accepted), true);
                editor.putBoolean(getString(R.string.key_eula_edited), true);
                editor.commit();
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.key_eula_accepted), false);
                editor.putBoolean(getString(R.string.key_eula_edited), true);
                editor.commit();
                finish();
            }
        });
    }
}
