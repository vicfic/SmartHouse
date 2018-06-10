package com.udc.muei.apm.apm_smarthouse.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.udc.muei.apm.apm_smarthouse.R;

public class ConfiguracionServidor extends AppCompatActivity implements View.OnClickListener{

    TextView ipAddress;
    TextView port;
    Button cancel;
    Button save;
    ImageView back;

    GoogleSignInClient mGoogleSignInClient;
    private static final String LOGIN_TAG = "LOGIN_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configuracion_servidor);

        ipAddress = findViewById(R.id.edittext_ip_servidor);
        port = findViewById(R.id.edittext_puerto);

        save = findViewById(R.id.button_save_config);
        cancel = findViewById(R.id.button_cancel_config);
        back = findViewById(R.id.boton_back);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        back.setOnClickListener(this);

        /* Actualización de la información guardada en shared preferences */
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
        String ip_serv = sharedPref.getString(getString(R.string.key_shared_IP), getString(R.string.default_value_IP));
        String port_serv = sharedPref.getString(getString(R.string.key_shared_port), getString(R.string.default_value_port));
       if (!ip_serv.equals(getString(R.string.default_value_IP)))
           ipAddress.setText(ip_serv);
       if (!port_serv.equals(getString(R.string.default_value_port)))
           port.setText(port_serv);

    }

    @Override
    public boolean onSupportNavigateUp() {
        Toast.makeText(getApplicationContext(),"Botón Back presionado " , Toast.LENGTH_SHORT).show();
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
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
                finish();
                break;
            case R.id.boton_back:
                finish();
                break;
        }
    }

    private void saveConfigServer(String ipAddress, String port){
        /* Guardamos la información introducida */
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.key_shared_IP), ipAddress);
        editor.putString(getString(R.string.key_shared_port), port);
        editor.commit();


        /* Creación del cliente para comprobar el login con Google*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /* Comprobación de usuario logueado */
        mGoogleSignInClient.silentSignIn()
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(LOGIN_TAG, "No hai autenticacion previa");
                        handleNonSignIn();
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        Log.d(LOGIN_TAG, "Hai autenticacion previa");
                        handleSignInResult(task);
                    }
                });



    }

    /* Se lanza cuando no hay un usuario logueado. Lanza la actividad de login */
    private void handleNonSignIn() {
        /* Lanzamos un intent para la ventana de login de google */
        Intent intent = new Intent(this, GoogleLogin.class);
        startActivity(intent);
        finish();
    }

    /* Se lanza cuando hay un usuario previamente logueado */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        /* Se acaba la activity y se vuelve a la actividad principal */
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
