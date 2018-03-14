package com.udc.muei.apm.apm_smarthouse.model;

/**
 * Created by José Manuel González on 13/03/2018.
 */

public class Routine {

    String name;
    Boolean state;

    public Routine(String name, Boolean state) {
        this.name=name;
        this.state=state;
    }

    public String getName() {
        return name;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public void setName(String name) {
        this.name = name;
    }
}
