//Manuel Esquivel sevillano 2ºDAM
package com.example.prueba.controlador;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

public class LogIn extends AppCompatActivity {
    String usuario; // Nombre de usuario
    String contra;  // Contraseña

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilitar diseño EdgeToEdge
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_view);

        // Configurar insets de ventana para la vista principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para registrar un nuevo usuario
    public void registrarUsuario(View view) {
        Intent ventana = new Intent(this, ControladorRegistro.class);
        ventana.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(ventana);
    }

    // Método para iniciar sesión
    public void logIn(View view) {
        // Obtener el nombre de usuario y la contraseña de los campos de texto
        EditText txtUsuaio = findViewById(R.id.txtUsuario);
        EditText txtContra = findViewById(R.id.txtContraseña);
        usuario = txtUsuaio.getText().toString();
        contra = txtContra.getText().toString();

        // Verificar que los campos no estén vacíos
        if (usuario.isEmpty() || contra.isEmpty()) {
            Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            // Ejecutar la tarea asíncrona para validar el usuario
            new LoginUsuarioAsyncTask().execute(usuario, contra);
        }
    }

    // Clase AsyncTask para validar el usuario en segundo plano
    private class LoginUsuarioAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/validar_usuario.php?usuario=" + params[0] + "&contrasena=" + params[1];
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
            // Manejar la respuesta del servidor después de la validación del usuario
            if (result.equals("[{\"admin\":0}]")) {
                Toast.makeText(LogIn.this, "Bienvenido " + usuario, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LogIn.this, ControladorVistaUsuarios.class);
                intent.putExtra("user", usuario);
                startActivity(intent);

            } else if (result.equals("[{\"admin\":1}]")) {
                Toast.makeText(LogIn.this, "Bienvenido " + usuario, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LogIn.this, ControladorMenuAdmin.class);
                intent.putExtra("user", usuario);
                startActivity(intent);

            } else {
                Toast.makeText(LogIn.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}