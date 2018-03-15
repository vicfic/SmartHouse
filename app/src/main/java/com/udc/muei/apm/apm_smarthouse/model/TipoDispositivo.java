package com.udc.muei.apm.apm_smarthouse.model;

/**
 * Created by José Manuel González on 14/03/2018.
 */

public enum TipoDispositivo {

    LUZ("Luz",1),
    ESTUFA("Estufa",2),
    TELEVISOR("Televisor",3);

    private String nombreTipoDispositivo;
    private int idTipoDispositivo;

    private TipoDispositivo(String toString, int value){
        nombreTipoDispositivo = toString;
        idTipoDispositivo = value;
    }

    @Override
    public String toString() {
        return nombreTipoDispositivo;
    }
}
