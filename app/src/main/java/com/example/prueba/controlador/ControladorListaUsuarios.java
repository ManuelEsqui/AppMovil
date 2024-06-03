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

    ArrayList<Usuario> arrayUsuarios=new ArrayList<>();
    ArrayList<String> stringArrayList=new ArrayList<>();
    ListView listaUsuarios;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lista_usuarios);
        new ObtenerUsuariosAsyncTask().execute();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ventanaListaUsuarios), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void volverMenuAdmin(View view) {
        finish();
    }
    private class ObtenerUsuariosAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String requestURL = "http://"+ constantes.LOCALHOST+"/extreventos/obtenerPersonas.php";
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
        protected void onPostExecute(String content) {
            if (!content.isEmpty()) {
                // Primero separa por la / y luego por el guion
                String[] PersonasArray = content.split("/");
                for (String PersonaStr : PersonasArray) {
                    String[] datos = PersonaStr.split(" - ");
                    Usuario u=new Usuario(datos[0], datos[1], datos[2]);
                    String nombresConcat="Nombre: "+datos[0]+" "+datos[1]+"\nUsuario: "+datos[2]+"\n";
                    arrayUsuarios.add(u);
                    stringArrayList.add(nombresConcat);
                }
                initDatos();
                listaUsuarios.setOnItemClickListener((parent, view, position, id) -> {
                    Usuario u =arrayUsuarios.get(position);
                    Intent intent=new Intent(ControladorListaUsuarios.this, ControladorEdicionDatos.class);
                    intent.putExtra("usuarioEditar", u);
                    startActivity(intent);
                });
            }
        }


    }
    private void initDatos() {
        listaUsuarios=findViewById(R.id.listaUsuarios);
        // Adaptador para llenar el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringArrayList);
        listaUsuarios.setAdapter(adapter);
    }
}
