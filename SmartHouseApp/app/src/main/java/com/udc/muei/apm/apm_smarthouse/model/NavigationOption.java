package com.udc.muei.apm.apm_smarthouse.model;

/**
 * Created by Elías on 01/04/2018.
 */

public class NavigationOption {

    private String name;
    private TipoNavigationOption type;

    public NavigationOption(String name, TipoNavigationOption type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TipoNavigationOption getType() {
        return type;
    }

    public void setType(TipoNavigationOption type) {
        this.type = type;
    }
}
