package com.udc.muei.apm.apm_smarthouse.model;

/**
 * Created by Elías on 12/03/2018.
 */

public class Device {

    private String name;
    private TipoDispositivo type;
    private boolean isFavorite;
    //Anhadir ID DJANGO


    public Device(String name, TipoDispositivo type, boolean favorite) {
        this.name = name;
        this.type = type;
        this.isFavorite = favorite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TipoDispositivo getType() {
        return type;
    }

    public void setType(TipoDispositivo type) {
        this.type = type;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }
}
