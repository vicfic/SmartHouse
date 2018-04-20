package com.udc.muei.apm.apm_smarthouse.util;

import com.google.android.gms.maps.model.LatLng;
import com.udc.muei.apm.apm_smarthouse.R;

import java.util.HashMap;

/**
 * Created by José Manuel González on 18/04/2018 -- 17:01.
 */


public class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String NOTIFICATION_CALEFACCION_AUTO_KEY = PACKAGE_NAME + ".NOTIFICATION_CALEFACCION_AUTO_KEY";
    public static final String NOTIFICACION_TRANSITION_KEY = PACKAGE_NAME + ".NOTIFICACION_TRANSITION_KEY";
    public static final String NOTIFICACION_TRANSITION_ACTION_KEY = PACKAGE_NAME + ".NOTIFICACION_TRANSITION_ACTION_KEY";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    public static final String RADIUS_KEY = PACKAGE_NAME + ".RADIUS_KEY";
    public static final String OPTION_AUTO_CALEFACCION_KEY = PACKAGE_NAME + ".OPTION_AUTO_CALEFACCION_KEY";
    public static final String LOCATION_LAT_HOME_KEY = PACKAGE_NAME + ".LOCATION_LAT_HOME_KEY";
    public static final String LOCATION_LNG_HOME_KEY = PACKAGE_NAME + ".LOCATION_LNG_HOME_KEY";
    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 4600;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    /**
     * Minimo de radio del circulo para la calefaccion en metros
     */
    public static final float MIN_RADIUS_CALEFACCION = 50;  //50 metros

    /**
     * Máximo de radio del círculo para la calefaccion en metros
     */
    public static final float MAX_RADIUS_CALEFACCION = 10000;  // 10 Km

    public static final float DEFAULT_RADIUS_CALEFACCION = 1000;  // 1 Km

    public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km

    public static final String GEOFENCE_ID_STRING = "Casa";
    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<>();

    static {
        // Casa
        BAY_AREA_LANDMARKS.put("Casa", new LatLng(43.3555288930694543, -8.408066779375076));
    }

    public enum ACTIONS_NOTIFICATIONS {
        YES, NO
    }

    public static final int NOTIFICATION_ID = 1992;
}