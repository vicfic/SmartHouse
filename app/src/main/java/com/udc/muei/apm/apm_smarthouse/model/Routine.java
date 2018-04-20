package com.udc.muei.apm.apm_smarthouse.model;

import android.content.Intent;

import com.udc.muei.apm.apm_smarthouse.interfaces.RutineHandlerONOFF;

/**
 * Created by José Manuel González on 13/03/2018.
 */

public class Routine {

    String name;
    Boolean state;
    Intent intent;
    RutineHandlerONOFF rutineHandlerONOFF = null;

    public Routine(String name, Boolean state) {
        this.name=name;
        this.state=state;
    }

    public Routine(String name, Boolean state, Intent  intent) {
        this.name=name;
        this.state=state;
        this.intent=intent;
    }

    public Routine(String name, Boolean state, Intent  intent, RutineHandlerONOFF rutineHandlerONOFF) {
        this.name=name;
        this.state=state;
        this.intent=intent;
        this.rutineHandlerONOFF = rutineHandlerONOFF;
    }

    public void setRutineHandlerONOFF(RutineHandlerONOFF rutineHandlerONOFF) {
        this.rutineHandlerONOFF = rutineHandlerONOFF;
    }

    public RutineHandlerONOFF getRutineHandlerONOFF() {
        return rutineHandlerONOFF;
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

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
