package com.example.prueba.controlador;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prueba.R;
import com.example.prueba.modelo.Evento;
import com.example.prueba.utiles.constantes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EventosPagoFragment extends Fragment {
    private static final String ARG_USER = "user";
    private String user;
    ListView listaEventosPago;
    ArrayList<Evento> arrEventosPago=new ArrayList<>();
    ArrayList<String> arrNombreEventosPago=new ArrayList<>();

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

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventos_pago, container, false);
        TextView textView = view.findViewById(R.id.textViewPago);
        listaEventosPago=view.findViewById(R.id.listaeventosPago);
        textView.setText("Eventos de pago para " + user);
        new listarAsyncTask().execute();
        return view;
    }
    private void init(){
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrNombreEventosPago);//requireActivity()
        listaEventosPago.setAdapter(adapter);
        listaEventosPago.setOnItemClickListener((parent, view, position, id1) -> {
            Evento evento=arrEventosPago.get(position);
            eliminarAsistencia(evento.getId());
        });
    }

    public void eliminarAsistencia(int id) {
        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de que deseas ser eliminado de este evento?");

        // Botón Aceptar
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getActivity(), "Opción aceptada", Toast.LENGTH_SHORT).show();
                new desapuntarAsyncTask().execute(id);
            }
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Opción cancelada", Toast.LENGTH_SHORT).show();
            }
        });

        // Crear y mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @SuppressLint("StaticFieldLeak")
    private class listarAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String requestURL = "http://"+ constantes.LOCALHOST+"/extreventos/eventosPagoUsuario.php?usuario=" + user;
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(requestURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Leer la respuesta del servidor
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                } else {
                    response.append("Error en la respuesta del servidor. Código: ").append(responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                response.append("Excepción: ").append(e.getMessage());
            }

            return response.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            if (!result.isEmpty()) {
                // Primero separa por la / y luego por el guion
                String[] EventosArray = result.split("/");
                for (String EventoStr : EventosArray) {
                    String[] datos = EventoStr.split(" - ");
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    arrEventosPago.add(new Evento(id,nombre));
                    arrNombreEventosPago.add(nombre);
                    init();
                }
            }
        }


    }
    class desapuntarAsyncTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... params) {
            String requestURL = "http://"+ constantes.LOCALHOST+"/extreventos/desapuntarseEvento.php?usuario=" + user+"&id_evento="+params[0];
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(requestURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Leer la respuesta del servidor
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                } else {
                    response.append("Error en la respuesta del servidor. Código: ").append(responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                response.append("Excepción: ").append(e.getMessage());
            }

            return response.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            if (!result.isEmpty()) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getActivity(), ControladorVistaUsuarios.class);
                intent.putExtra("user", user);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }


    }
}

