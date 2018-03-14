package com.udc.muei.apm.apm_smarthouse.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by José Manuel González on 14/03/2018.
 */

public class UsuarioLight implements Parcelable{
    int idUsuarioDjango;
    String name;

    public UsuarioLight(int idUsuarioDjango, String name) {
        this.idUsuarioDjango = idUsuarioDjango;
        this.name = name;
    }

    public int getIdUsuarioDjango() {
        return idUsuarioDjango;
    }

    public void setIdUsuarioDjango(int idUsuarioDjango) {
        this.idUsuarioDjango = idUsuarioDjango;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idUsuarioDjango);
        dest.writeString(name);
    }

    private UsuarioLight(Parcel in){
        this.idUsuarioDjango = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<UsuarioLight> CREATOR = new Creator<UsuarioLight>() {
        @Override
        public UsuarioLight createFromParcel(Parcel source) {
            return new UsuarioLight(source);
        }

        @Override
        public UsuarioLight[] newArray(int size) {
            return new UsuarioLight[size];
        }
    };
}
