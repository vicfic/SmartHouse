package com.udc.muei.apm.apm_smarthouse.model;

import android.content.Intent;

/**
 * Created by José Manuel González on 13/03/2018.
 */

public class Routine {

    String name;
    Boolean state;
    Intent intent;

    public Routine(String name, Boolean state) {
        this.name=name;
        this.state=state;
    }

    public Routine(String name, Boolean state, Intent  intent) {
        this.name=name;
        this.state=state;
        this.intent=intent;
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
