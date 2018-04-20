package com.udc.muei.apm.apm_smarthouse.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by José Manuel González on 18/04/2018
 */

public class ServiceTools {

    public static boolean isServiceRunning(String serviceClassName, Context context){
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services;
        if (activityManager != null) {
            services = activityManager.getRunningServices(Integer.MAX_VALUE);
            for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
                if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
