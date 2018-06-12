package com.udc.muei.apm.apm_smarthouse.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by José Manuel González on 12/04/2018.
 */

public class BeaconCustom implements Parcelable {

    private int distance;
    private String uuid;
    private String idGrupo;
    private String idBeacon;
    private int lugarId = -1;
    private int distanciaRango = 0;
    private String nombre_lugar;
    private int notificado = 0;

    public int getLugarId() {
        return lugarId;
    }

    public void setLugarId(int lugarId) {
        this.lugarId = lugarId;
    }

    public BeaconCustom(int distance, String uuid, String grupoid, String beaconid, int lugarId, int distanciaRango, String nombre_lugar, int notificado) {
        this.distance = distance;

        this.uuid = uuid;
        this.idGrupo = grupoid;
        this.idBeacon = beaconid;
        this.lugarId = lugarId;
        this.distanciaRango = distanciaRango;
        this.nombre_lugar = nombre_lugar;
        this.notificado = notificado;
    }

    public int getDistance() {
        return distance;
    }
    public String getUuid() {
        return uuid;
    }

    public String getNombre_lugar() {
        return nombre_lugar;
    }

    public void setNombre_lugar(String nombre_lugar) {
        this.nombre_lugar = nombre_lugar;
    }

    public int getNotificado() {
        return notificado;
    }

    public void setNotificado(int notificado) {
        this.notificado = notificado;
    }

    public int getDistanciaRango() {
        return distanciaRango;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setDistanciaRango(int distanciaRango) {
        this.distanciaRango = distanciaRango;
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public String getIdBeacon() {
        return idBeacon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.distance);
        dest.writeString(this.uuid);
        dest.writeString(this.idGrupo);
        dest.writeString(this.idBeacon);
        dest.writeInt(this.distanciaRango);
        dest.writeInt(this.lugarId);
        dest.writeString(this.nombre_lugar);
        dest.writeInt(this.notificado);
    }

    private BeaconCustom(Parcel in){
        this.distance = in.readInt();
        this.uuid = in.readString();
        this.idGrupo = in.readString();
        this.idBeacon = in.readString();
        this.distanciaRango = in.readInt();
        this.lugarId = in.readInt();
        this.nombre_lugar = in.readString();
        this.notificado = in.readInt();
    }


    public static final Parcelable.Creator<BeaconCustom> CREATOR = new Creator<BeaconCustom>() {
        @Override
        public BeaconCustom createFromParcel(Parcel source) {
            return new BeaconCustom(source);
        }

        @Override
        public BeaconCustom[] newArray(int size) {
            return new BeaconCustom[size];
        }
    };
}
