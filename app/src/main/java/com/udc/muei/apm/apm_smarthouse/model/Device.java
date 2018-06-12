package com.udc.muei.apm.apm_smarthouse.model;

/**
 * Created by Elías on 12/03/2018.
 */

public class Device {

    private String name;
    private TipoDispositivo type;
    private boolean isFavorite;
    private boolean isActive;
    private int id_Django;
    private int id_Lugar;
    //Anhadir ID DJANGO


    public Device(String name, TipoDispositivo type, boolean favorite) {
        this.name = name;
        this.type = type;
        this.isFavorite = favorite;
    }

    public Device(String name, TipoDispositivo type, boolean favorite, boolean isActive, int id_Django, int id_Lugar) {
        this.name = name;
        this.type = type;
        this.isFavorite = favorite;
        this.isActive = isActive;
        this.id_Lugar = id_Lugar;
        this.id_Django = id_Django;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getId_Django() {
        return id_Django;
    }

    public void setId_Django(int id_Django) {
        this.id_Django = id_Django;
    }

    public int getId_Lugar() {
        return id_Lugar;
    }

    public void setId_Lugar(int id_Lugar) {
        this.id_Lugar = id_Lugar;
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
