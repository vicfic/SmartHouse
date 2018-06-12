package com.udc.muei.apm.apm_smarthouse.model;

/**
 * Created by Elías on 01/04/2018.
 */

public enum TipoNavigationOption {
    IMAGEN("Imagen",1),
    TEXTO("Texto",2);

    private String nombreTipoNavigation;
    private int idTipoNavigation;

    private TipoNavigationOption(String toString, int value){
        nombreTipoNavigation = toString;
        idTipoNavigation = value;
    }

    @Override
    public String toString() {
        return nombreTipoNavigation;
    }
}
