package com.udc.muei.apm.apm_smarthouse.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by José Manuel González on 23/03/2018.
 */

public class HttpsRequestAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String GET_TAG = "GET USUARIOS";

    public HttpsRequestResult mListener = null;
    private ProgressDialog pdia;


    private static final HostnameVerifier DUMMY_VERIFIER = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    private Context mContext;

    public HttpsRequestAsyncTask(Context context, HttpsRequestResult interfaz) {
        mListener = interfaz;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pdia = new ProgressDialog(mContext);
        pdia.setTitle("Cargando...");
        pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdia.setCancelable(false);
        pdia.setIcon(android.R.drawable.btn_plus);
        pdia.setMessage("Realizando petición al servidor...");
        pdia.show();
        Log.d(GET_TAG,"<--> OnPreExecute <-->");

        // Dummy trust manager that trusts all certificates
        TrustManager localTrustmanager = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        };

        // Create SSLContext and set the socket factory as default
        try {
            SSLContext sslc = SSLContext.getInstance("TLS");
            sslc.init(null, new TrustManager[] { localTrustmanager },
                    new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslc
                    .getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        pdia.show();

        try {
            // This is getting the url from the string we passed in
            List<String> session = null;
            HttpsURLConnection writeConn;
            Log.d(GET_TAG,"Cambiando");


            //URL url = new URL("https://192.168.43.76:8000/SmartHouse/");
            URL url = new URL("https://192.168.0.25:8000/SmartHouse/");

            writeConn = (HttpsURLConnection) url.openConnection();


            try {
                //Estableciendo propiedades de la conexión
                writeConn.setRequestProperty("X-CSRF-Token", "Fetch");
                writeConn.setRequestMethod("POST");  //Cambiar esto para GET o POST
                writeConn.setDoOutput(true);
                writeConn.setDoInput(true);
                writeConn.setRequestProperty("USER_AGENT","Mozilla/5.0");
                writeConn.setRequestProperty("ACCEPT-LANGUAGE","en-US,en;0.5");
                //writeConn.addRequestProperty("REFERER", "https://192.168.43.76:8000/"); //Cambiar esto con la dirección del servidor (get preferences)
                writeConn.addRequestProperty("REFERER", "https://192.168.0.25:8000/"); //Cambiar esto con la dirección del servidor (get preferences)
                writeConn.setHostnameVerifier(DUMMY_VERIFIER);
                //session = getSessionCookies(writeConn);
                setSessionCookies(writeConn, session); // set request header "cookie"
                writeConn.connect();

                //Steam de salida (Si la petición el PUT)
                DataOutputStream dOut = new DataOutputStream(writeConn.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dOut, "UTF-8"));
                JSONObject jsonEnv = new JSONObject();
                jsonEnv.put("idUSUARIO",111);
                jsonEnv.put("nombre","manuel");
                writer.write(jsonEnv.toString());  // Escribir aqui la info en formato JSON
                writer.flush();
                writer.close();

                //Stream de entrada para la respuesta (tanto GET como POST)
                dOut.close();
                InputStream in = new BufferedInputStream(writeConn.getInputStream());

                String s = readStream(in);

                JSONObject json = new JSONObject(s);
                String result = json.getString("message");
                Log.d(GET_TAG,"Cambiando   RESULT -->"+json.toString());
                in.close();

                return json.toString();


            } finally {
                writeConn.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String result){
        //super.onPostExecute(result);

        Log.d(GET_TAG,"<--> OnPostExecute <-->");
        pdia.dismiss();
        mListener.processFinish(result);

    }

    private String readStream(InputStream in) {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return total.toString();
        }
    }

    private static final List<String> getSessionCookies(HttpURLConnection conn) {
        Map<String, List<String>> response_headers = conn.getHeaderFields();
        Iterator<String> keys = response_headers.keySet().iterator();
        String key;
        while (keys.hasNext()) {
            key = keys.next();
            if ("set-cookie".equalsIgnoreCase(key)) {
                List<String> session = response_headers.get(key);
                return session;
            }
        }

        // no session
        return null;
    }

    private static final void setSessionCookies(HttpURLConnection conn, List<String> session) {
        if (session != null) {
            String agregated_cookies = "";
            for (String cookie: session) {
                agregated_cookies += cookie + "; ";
            }
            conn.setRequestProperty("cookie", agregated_cookies);
        }
    }
}