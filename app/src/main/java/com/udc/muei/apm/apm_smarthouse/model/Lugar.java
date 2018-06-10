package com.udc.muei.apm.apm_smarthouse.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by José Manuel González on 15/03/2018.
 */

public class Lugar implements Parcelable {
    private String name;
    private int idDjangoLugar;
    String photoUrl;

    public Lugar(String name, int idDjango) {
        this.name = name;
        this.idDjangoLugar = idDjango;
    }

    public Lugar(String name, int idDjangoLugar, String photoUrl) {
        this.name = name;
        this.idDjangoLugar = idDjangoLugar;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdDjango() {
        return idDjangoLugar;
    }

    public void setIdDjango(int idDjango) {
        this.idDjangoLugar = idDjango;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idDjangoLugar);
        dest.writeString(name);
    }

    private Lugar (Parcel in){
        this.idDjangoLugar = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Lugar> CREATOR = new Creator<Lugar>() {
        @Override
        public Lugar createFromParcel(Parcel source) {
            return new Lugar(source);
        }

        @Override
        public Lugar[] newArray(int size) {
            return new Lugar[size];
        }
    };

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
