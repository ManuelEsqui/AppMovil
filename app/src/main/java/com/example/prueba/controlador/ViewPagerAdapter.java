package com.example.prueba.controlador;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private String user; // Variable para almacenar el nombre de usuario

    // Constructor de la clase
    public ViewPagerAdapter(FragmentManager fm, String user) {
        super(fm); // Llamar al constructor de la superclase
        this.user = user; // Inicializar la variable de nombre de usuario
    }

    // Método para obtener el fragmento en la posición dada
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) { // Seleccionar el fragmento según la posición
            case 0:
                return EventosPagoFragment.newInstance(user); // Crear y devolver un nuevo fragmento de eventos de pago
            case 1:
                return EventosGratisFragment.newInstance(user); // Crear y devolver un nuevo fragmento de eventos gratis
            default:
                return null; // Devolver null si la posición no es válida
        }
    }

    // Método para obtener la cantidad total de fragmentos
    @Override
    public int getCount() {
        return 2; // Hay dos fragmentos en total (eventos de pago y eventos gratis)
    }

    // Método para obtener el título de la pestaña en la posición dada
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) { // Seleccionar el título de la pestaña según la posición
            case 0:
                return "Eventos de pago"; // Devolver el título para los eventos de pago
            case 1:
                return "Eventos gratis"; // Devolver el título para los eventos gratis
            default:
                return null; // Devolver null si la posición no es válida
        }
    }
}


