package com.udc.muei.apm.apm_smarthouse.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

public class ComprobacionLocalizacion extends Service {

    private static String LOG_COMPROBACION_LOCALIZACION_TAG = "ComprobacionLocalizacion";
    public static final int MSG_SAY_HELLO = 1;
    private IBinder mBinder = new ComprobacionLocalizacion.MyBinder();




    public ComprobacionLocalizacion() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_COMPROBACION_LOCALIZACION_TAG, "in onBind");
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_COMPROBACION_LOCALIZACION_TAG, "in onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_COMPROBACION_LOCALIZACION_TAG, "in oStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setAction("com.udc.muei.apm.apm_smarthouse.Services.COMPROBACION_LOCALIZACION_DESTROY");
        sendBroadcast(intent);

        Log.d(LOG_COMPROBACION_LOCALIZACION_TAG, "in onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_COMPROBACION_LOCALIZACION_TAG, "in onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOG_COMPROBACION_LOCALIZACION_TAG, "in onRebind");
        super.onRebind(intent);
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(getApplicationContext(), "hello!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    public class MyBinder extends Binder {
        public ComprobacionLocalizacion getService() {
            return ComprobacionLocalizacion.this;
        }
    }
}
