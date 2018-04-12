package com.udc.muei.apm.apm_smarthouse.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.udc.muei.apm.apm_smarthouse.R;


import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by José Manuel González on 10/04/2018.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button button_login;
    Button button_twitter;
    Button button_google;
    Button button_facebook;
    TextView username;
    TextView password;


    private static final String LOGIN_TAG = "LOGIN TAG";
    private static final int RC_SIGN_IN = 1;
    ImageButton button_server_settings;
    GoogleSignInClient mGoogleSignInClient;


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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        //Aqui iniciar actividad de Login! eL IntenT vamos

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        handleSignInResult(task);
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();
            String token = account.getIdToken();

            Log.d(LOGIN_TAG, personName+"\n"+personGivenName+"\n"+personFamilyName+"\n"+personEmail+"\n"+personId+"\n"+personPhoto+"\n"+token);
        } catch (ApiException e) {
            Log.w(LOGIN_TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login:
                if ((username.getText().length() == 0) ||(password.getText().length() == 0))
                    Toast.makeText(getApplicationContext(), "Introduzca usuario y contraseña", LENGTH_LONG).show();
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
        Toast.makeText(getApplicationContext(), "Logeando con usuario: "+ username+" y contraseña: "+password, LENGTH_LONG).show();
        makeIntent();
    }

    private void loginFacebook(){
        Toast.makeText(getApplicationContext(), "Proceso de login con FACEBOOK", LENGTH_LONG).show();
        makeIntent();
    }

    private void loginTwitter(){
        //Toast.makeText(getApplicationContext(), "Proceso de login con TWITTER", LENGTH_LONG).show();
        //makeIntent();
        signOut();

    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Deslogueado!", LENGTH_LONG).show();
                    }
                });
    }

    private void loginGoogle(){
        Toast.makeText(getApplicationContext(), "Proceso de login con GOOGLE", LENGTH_LONG).show();
        makeIntent();
    }


}
