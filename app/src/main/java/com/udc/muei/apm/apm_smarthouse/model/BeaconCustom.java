package com.udc.muei.apm.apm_smarthouse.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by José Manuel González on 12/04/2018.
 */

public class BeaconCustom implements Parcelable {

    private Double distance;
    private String uuid;
    private String idGrupo;
    private String idBeacon;
    private String bluetoothName;
    private String bluetoothAddress;
    private Double averageRssi;
    private int rssi;
    private Double distanciaRango;


    public BeaconCustom(Double distance, String id1, String id2, String id3, String bluetoothName, String bluetoothAddress, Double averageRssi, int rssi, Double distanciaRango) {
        this.distance = distance;
        this.uuid = id1;
        this.idGrupo = id2;
        this.idBeacon = id3;
        this.bluetoothName = bluetoothName;
        this.bluetoothAddress = bluetoothAddress;
        this.averageRssi = averageRssi;
        this.rssi = rssi;
        this.distanciaRango = distanciaRango;
    }

    public Double getDistance() {
        return distance;
    }
    public String getUuid() {
        return uuid;
    }

    public Double getDistanciaRango() {
        return distanciaRango;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setAverageRssi(Double averageRssi) {
        this.averageRssi = averageRssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setDistanciaRango(Double distanciaRango) {
        this.distanciaRango = distanciaRango;
    }

    public String getIdGrupo() {
        return idGrupo;
    }

    public String getIdBeacon() {
        return idBeacon;
    }

    public String getBluetoothName() {
        return bluetoothName;
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }

    public Double getAverageRssi() {
        return averageRssi;
    }

    public int getRssi() {
        return rssi;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.distance);
        dest.writeString(this.uuid);
        dest.writeString(this.idGrupo);
        dest.writeString(this.idBeacon);
        dest.writeString(this.bluetoothName);
        dest.writeString(this.bluetoothAddress);
        dest.writeDouble(this.averageRssi);
        dest.writeInt(this.rssi);
        dest.writeDouble(this.distanciaRango);
    }

    private BeaconCustom(Parcel in){
        this.distance = in.readDouble();
        this.uuid = in.readString();
        this.idGrupo = in.readString();
        this.idBeacon = in.readString();
        this.bluetoothName = in.readString();
        this.bluetoothAddress = in.readString();
        this.averageRssi = in.readDouble();
        this.rssi = in.readInt();
        this.distanciaRango = in.readDouble();
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
