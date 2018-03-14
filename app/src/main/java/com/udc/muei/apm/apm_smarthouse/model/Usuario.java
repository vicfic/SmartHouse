package com.udc.muei.apm.apm_smarthouse.model;

import java.util.ArrayList;

/**
 * Created by José Manuel González on 14/03/2018.
 */

public class Usuario {
    int idUsuarioDjango;
    String name;
    Boolean root;
    ArrayList<Permiso> permisos;

    public Usuario(int idUsuarioDjango, String name, Boolean root, ArrayList<Permiso> permisos) {
        this.idUsuarioDjango = idUsuarioDjango;
        this.name = name;
        this.root = root;
        this.permisos = permisos;
    }

    public Usuario(int idUsuarioDjango,String name, Boolean root) {
        this.idUsuarioDjango = idUsuarioDjango;
        this.name = name;
        this.root = root;
        this.permisos = new ArrayList<Permiso>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRoot() {
        return root;
    }

    public void setRoot(Boolean root) {
        this.root = root;
    }

    public ArrayList<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(ArrayList<Permiso> permisos) {
        this.permisos = permisos;
    }
}
