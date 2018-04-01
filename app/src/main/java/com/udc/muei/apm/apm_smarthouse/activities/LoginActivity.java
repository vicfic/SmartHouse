package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.udc.muei.apm.apm_smarthouse.R;

/**
 * Created by DavidPC on 14/03/2018.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button button_login;
    Button button_twitter;
    Button button_google;
    Button button_facebook;
    TextView username;
    TextView password;

    ImageButton button_server_settings;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.edittext_username);
        password = findViewById(R.id.edittext_password);

        button_login = findViewById(R.id.button_login);
        button_facebook = findViewById(R.id.button_facebook);
        button_google = findViewById(R.id.button_google);
        button_twitter = findViewById(R.id.button_twitter);
        button_server_settings = findViewById(R.id.server_settings_button);

        button_login.setOnClickListener(this);
        button_facebook.setOnClickListener(this);
        button_twitter.setOnClickListener(this);
        button_google.setOnClickListener(this);
        button_server_settings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login:
                if ((username.getText().length() == 0) ||(password.getText().length() == 0))
                    Toast.makeText(getApplicationContext(), "Introduzca usuario y contraseña", Toast.LENGTH_LONG).show();
                else
                    loginUsernamePassword(username.getText().toString(), password.getText().toString());
                break;
            case R.id.button_facebook:
                loginFacebook();
                break;
            case R.id.button_twitter:
                loginTwitter();
                break;
            case R.id.button_google:
                loginGoogle();
                break;
            case R.id.server_settings_button:
                returnToServerSettings();
                break;
        }
    }

    private void returnToServerSettings(){
        Intent intent = new Intent(this, ConfiguracionServidor.class);
        startActivity(intent);
    }

    private void makeIntent(){
        Intent intent = new Intent(this, MenuPrincipal.class);
        startActivity(intent);
    }

    private void loginUsernamePassword(String username, String password){
        Toast.makeText(getApplicationContext(), "Logeando con usuario: "+ username+" y contraseña: "+password,Toast.LENGTH_LONG).show();
        makeIntent();
    }

    private void loginFacebook(){
        Toast.makeText(getApplicationContext(), "Proceso de login con FACEBOOK", Toast.LENGTH_LONG).show();
        makeIntent();
    }

    private void loginTwitter(){
        Toast.makeText(getApplicationContext(), "Proceso de login con TWITTER", Toast.LENGTH_LONG).show();
        makeIntent();
    }

    private void loginGoogle(){
        Toast.makeText(getApplicationContext(), "Proceso de login con GOOGLE", Toast.LENGTH_LONG).show();
        makeIntent();
    }


}
