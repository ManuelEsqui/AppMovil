//Manuel Esquivel sevillano 2ºDAM
package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.prueba.R;
import com.example.prueba.utiles.constantes;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class ControladorAsistentes extends AppCompatActivity {
    int idEvento; // ID del evento
    String user; // Usuario
    TextView totalAsistentes; // TextView para mostrar el total de asistentes
    ListView listaAsistentes; // ListView para mostrar la lista de asistentes
    ArrayList<String> usuarios = new ArrayList<>(); // Lista de usuarios que asisten al evento
    boolean admin; // Booleano para verificar si el usuario es administrador

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilitar diseño EdgeToEdge
        EdgeToEdge.enable(this);

        // Obtener extras del Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("user");
            idEvento = extras.getInt("idEvento");
            admin = extras.getBoolean("admin");
        }

        // Ejecutar tarea asíncrona para obtener asistentes
        new AsistenciasAsyncTask().execute();

        // Establecer el contenido de la vista
        setContentView(R.layout.asistentes_view);

        // Configurar insets de ventana
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.asistencias), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Inicializar componentes de la interfaz
    private void init() {
        totalAsistentes = findViewById(R.id.totalPersonas);
        listaAsistentes = findViewById(R.id.listaPersonasAsistentes);
    }

    // Volver al inicio según el tipo de usuario
    public void volverInicio(View view) {
        if (admin) {
            Intent intent = new Intent(this, ControladorMenuAdmin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("user", user);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ControladorVistaUsuarios.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }

    // Volver a la actividad anterior
    public void vueltaAtras(View view) {
        finish();
    }

    // Clase asíncrona para obtener la lista de asistentes desde el servidor
    private class AsistenciasAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/listaDeAsistentes.php?id_evento=" + idEvento;
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
            // Separar la respuesta en un array y agregar a la lista de usuarios
            String[] AsistentesArray = result.split("/");
            usuarios.addAll(Arrays.asList(AsistentesArray));

            // Inicializar componentes de la interfaz y el ListView
            init();
            initListView();
        }
    }

    @SuppressLint("SetTextI18n")
    private void initListView() {
        // Crear y configurar el adaptador para el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usuarios);
        listaAsistentes.setAdapter(adapter);

        // Actualizar el TextView con el total de asistentes
        totalAsistentes.setText("Asisten " + usuarios.size() + " usuarios al evento");
    }
}

