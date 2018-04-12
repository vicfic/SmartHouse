package com.udc.muei.apm.apm_smarthouse.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.udc.muei.apm.apm_smarthouse.R;
import com.udc.muei.apm.apm_smarthouse.fragments.Usuarios;
import com.udc.muei.apm.apm_smarthouse.fragments.Favoritos;
import com.udc.muei.apm.apm_smarthouse.fragments.Lugares;
import com.udc.muei.apm.apm_smarthouse.fragments.Rutinas;

import java.util.ArrayList;

public class PagerAdapter  extends FragmentStatePagerAdapter {

    private ArrayList<String> nombres_fragmentos;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        nombres_fragmentos = new ArrayList<>();
        nombres_fragmentos.add(context.getString(R.string.nav_favoritos));
        nombres_fragmentos.add(context.getString(R.string.nav_lugares));
        nombres_fragmentos.add(context.getString(R.string.nav_rutinas));
        nombres_fragmentos.add(context.getString(R.string.nav_usuarios));
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new Favoritos();
            case 1:
                return new Lugares();
            case 2:
                return new Rutinas();
            case 3:
                return new Usuarios();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return nombres_fragmentos.size();
    }

    public ArrayList<String> getNombresFragmentos(){
        return nombres_fragmentos;
    }
}
