package com.udc.muei.apm.apm_smarthouse.model;

/**
 * Created by Elías on 12/03/2018.
 */

public class Device {

    private String name;
    private int type;
    private boolean favorite;

    public static int DEVICE_TYPE_LIGHTBULB = 1;
    public static int DEVICE_TYPE_STOVE = 2;

    public Device(String name, int type, boolean favorite) {
        this.name = name;
        this.type = type;
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
