package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.prueba.R;
import com.example.prueba.modelo.Usuario;
import com.example.prueba.utiles.constantes;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ControladorListaUsuarios extends AppCompatActivity {

    // ArrayList para almacenar objetos Usuario y cadenas de texto
    ArrayList<Usuario> arrayUsuarios = new ArrayList<>();
    ArrayList<String> stringArrayList = new ArrayList<>();
    ListView listaUsuarios;
    String user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Habilitar la funcionalidad EdgeToEdge
        setContentView(R.layout.lista_usuarios); // Establecer el diseño de la actividad
        Bundle extras=getIntent().getExtras(); // Obtener los extras pasados a la actividad
        if (extras!=null){ // Verificar si existen extras
            user=extras.getString("user"); // Obtener el nombre de usuario
            System.out.println(user);
        }
        new ObtenerUsuariosAsyncTask().execute(); // Ejecutar tarea asincrónica para obtener usuarios

        // Ajustar los márgenes para respetar las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ventanaListaUsuarios), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para volver al menú de administrador
    public void volverMenuAdmin(View view) {
        Intent intent = new Intent(this, ControladorMenuAdmin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("user", user);
        startActivity(intent); // Finalizar la actividad
    }

    // Clase interna para obtener usuarios de manera asincrónica
    private class ObtenerUsuariosAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/obtenerPersonas.php";
            StringBuilder response = new StringBuilder();

            try {
                // Establecer conexión con el servidor
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
        protected void onPostExecute(String content) {
            if (!content.isEmpty()) {
                // Separar los datos de usuario obtenidos y actualizar los ArrayLists
                String[] PersonasArray = content.split("/");
                for (String PersonaStr : PersonasArray) {
                    String[] datos = PersonaStr.split(" - ");
                    Usuario u = new Usuario(datos[0], datos[1], datos[2]);
                    String nombresConcat = "Nombre: " + datos[0] + " " + datos[1] + "\nUsuario: " + datos[2] + "\n";
                    arrayUsuarios.add(u);
                    stringArrayList.add(nombresConcat);
                }
                initDatos(); // Inicializar la visualización de datos
                // Configurar el listener para los items de la lista
                listaUsuarios.setOnItemClickListener((parent, view, position, id) -> {
                    Usuario u = arrayUsuarios.get(position);
                    Intent intent = new Intent(ControladorListaUsuarios.this, ControladorEdicionDatos.class);
                    intent.putExtra("usuarioEditar", u);
                    intent.putExtra("usuAdmin", user);
                    startActivity(intent);
                });
            }
        }
    }

    // Método para inicializar la visualización de datos en el ListView
    private void initDatos() {
        listaUsuarios = findViewById(R.id.listaUsuarios);
        // Adaptador para llenar el ListView con las cadenas de texto
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringArrayList);
        listaUsuarios.setAdapter(adapter);
    }
}

