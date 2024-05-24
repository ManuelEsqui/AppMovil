package com.example.prueba.controlador;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.prueba.R;

public class EventosPagoFragment extends Fragment {
    private static final String ARG_USER = "user";
    private String user;

    public EventosPagoFragment() {
        // Constructor vacío requerido
    }

    public static EventosPagoFragment newInstance(String user) {
        EventosPagoFragment fragment = new EventosPagoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventos_pago, container, false);
        TextView textView = view.findViewById(R.id.textViewPago);
        textView.setText("Eventos de pago para " + user);
        return view;
    }
}

