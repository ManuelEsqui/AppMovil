package com.example.prueba.controlador;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private String user;

    public ViewPagerAdapter(FragmentManager fm, String user) {
        super(fm);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return EventosPagoFragment.newInstance(user);
            case 1:
                return EventosGratisFragment.newInstance(user);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Eventos de pago";
            case 1:
                return "Eventos gratis";
            default:
                return null;
        }
    }
}


