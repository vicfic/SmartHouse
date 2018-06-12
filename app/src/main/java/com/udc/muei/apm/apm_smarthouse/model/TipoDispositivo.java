package com.udc.muei.apm.apm_smarthouse.model;

/**
 * Created by José Manuel González on 14/03/2018.
 */

public enum TipoDispositivo {

    LUZ("Luz",1),
    ESTUFA("Estufa",2),
    TELEVISOR("Televisor",3),
    SENSOR("Sensor",4);

    private String nombreTipoDispositivo;
    private int idTipoDispositivo;

    private TipoDispositivo(String toString, int value){
        nombreTipoDispositivo = toString;
        idTipoDispositivo = value;
    }

    public static TipoDispositivo getTipoDispositivo(int i) {
        switch(i) {
            case 1:
                return TipoDispositivo.LUZ;
            case 2:
                return TipoDispositivo.ESTUFA;
            case 3:
                 return TipoDispositivo.TELEVISOR;
            case 4:
                 return TipoDispositivo.SENSOR;
        }
        return null;
    }

    @Override
    public String toString() {
        return nombreTipoDispositivo;
    }
}
