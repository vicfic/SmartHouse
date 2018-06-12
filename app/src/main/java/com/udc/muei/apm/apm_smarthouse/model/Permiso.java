package com.udc.muei.apm.apm_smarthouse.model;

/**
 * Created by José Manuel González on 14/03/2018.
 *
 * REVISADA: José Manuel González on 11/06/2018.
 */

public class Permiso {

    private int idPermisoDjango;
    private String name;
    private Boolean permiso;


    public Permiso(int idPermisoDjango, String name, Boolean permiso) {
        this.idPermisoDjango = idPermisoDjango;
        this.name = name;
        this.permiso = permiso;
    }

    public String getName() {
        return name;
    }

    public Boolean getPermiso() {
        return permiso;
    }

    public void setNombre(String nombre) {

        this.name = nombre;
    }

    public void setPermiso(Boolean permiso) {
        this.permiso = permiso;
    }

    public int getIdPermisoDjango() {
        return idPermisoDjango;
    }
}
