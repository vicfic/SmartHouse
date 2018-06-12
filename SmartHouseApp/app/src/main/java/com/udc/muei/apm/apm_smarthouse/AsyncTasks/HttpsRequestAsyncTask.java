package com.udc.muei.apm.apm_smarthouse.AsyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.interfaces.HttpsRequestResult;

import org.json.JSONException;
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
 *
 * REVISADA: José Manuel González on 10/06/2018.
 */

public class HttpsRequestAsyncTask extends AsyncTask<String, Void, String> {

    private static final String GET_TAG = "GET ASYCK TASK";

    public HttpsRequestResult mListener = null;
    private Context mContext;

    private static final HostnameVerifier DUMMY_VERIFIER = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public HttpsRequestAsyncTask(Context context, HttpsRequestResult interfaz) {
        mListener = interfaz;
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
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
    protected String doInBackground(String... params) {
        try {
            List<String> session = null;
            HttpsURLConnection writeConn;

            String ruta = params[0];
            String parametros = params[1];

            try {
                JSONObject parametrosJSON = new JSONObject(parametros);
                Log.d(GET_TAG,"Parametros:  "+parametrosJSON);

                //Recopilación de Dirección IP y Puerto del servidor
                SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.key_for_shared_preferences), Context.MODE_PRIVATE);
                String ip_serv = sharedPref.getString(mContext.getString(R.string.key_shared_IP), mContext.getString(R.string.default_value_IP));
                String port_serv = sharedPref.getString(mContext.getString(R.string.key_shared_port), mContext.getString(R.string.default_value_port));

                //Construcción de la ruta a la API
                URL url = new URL("https://"+ip_serv+":"+port_serv+"/"+ruta);
                Log.d(GET_TAG,"Ruta al servidor:  "+url);

                //Creación de la conexión
                writeConn = (HttpsURLConnection) url.openConnection();

                try {
                    //Estableciendo propiedades de la conexión
                    writeConn.setRequestProperty("X-CSRF-Token", "Fetch");
                    writeConn.setRequestMethod("POST");  //Cambiar esto para GET o POST
                    writeConn.setDoOutput(true);
                    writeConn.setDoInput(true);
                    writeConn.setConnectTimeout(3500); //Timeout para la conexion de 5 segundos;
                    writeConn.setRequestProperty("USER_AGENT","Mozilla/5.0");
                    writeConn.setRequestProperty("ACCEPT-LANGUAGE","en-US,en;0.5");
                    writeConn.addRequestProperty("REFERER", "https://+"+ip_serv+":"+port_serv+"/"); //Cambiar esto con la dirección del servidor (get preferences)
                    writeConn.setHostnameVerifier(DUMMY_VERIFIER);

                    setSessionCookies(writeConn, session); // set request header "cookie"

                    writeConn.connect();

                    //Steam de salida
                    DataOutputStream dOut = new DataOutputStream(writeConn.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dOut, "UTF-8"));
                    writer.write(parametrosJSON.toString());  //Informacion en JSON
                    writer.flush();
                    writer.close();

                    //Stream de entrada para la respuesta
                    dOut.close();
                    InputStream in = new BufferedInputStream(writeConn.getInputStream());
                    String respuesta = readStream(in);
                    in.close();

                    return respuesta;

                } catch (Throwable t) {
                    Log.e(GET_TAG, "Respuesta malformada.");
                    JSONObject error = new JSONObject();
                    error.put("Error",1);
                    return error.toString();
                } finally {
                    writeConn.disconnect();
                }

            } catch (Throwable t) {
                Log.e(GET_TAG, "No se puede parsear la información de entrada: \"" + parametros + "\"");
                JSONObject error = new JSONObject();
                error.put("Error",1);
                return error.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject error = new JSONObject();
        try {
            error.put("Error",1);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return error.toString();
    }

    protected void onPostExecute(String result){
        //Retorno de resultado de la conexión
        Log.d(GET_TAG,"Resultado de la conexión: \""+result+"\"");
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