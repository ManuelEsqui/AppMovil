package com.example.prueba;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ControladorRegistro extends AppCompatActivity {
    EditText txtnombre;
    EditText txtapellidos;
    EditText txtedad;
    EditText txtsexo;
    EditText txtestadoCivil;
    EditText txtusuario;
    EditText txtcontrasena;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registro_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            initComp();
            return insets;
        });
    }

    private void initComp() {
        txtnombre=findViewById(R.id.txtNombre);
        txtapellidos=findViewById(R.id.txtApellidos);
        txtedad=findViewById(R.id.txtEdad);
        txtsexo=findViewById(R.id.txtsexo);
        txtestadoCivil=findViewById(R.id.txtEstadoCivil);
        txtusuario=findViewById(R.id.txtUsuario);
        txtcontrasena=findViewById(R.id.txtContraseña);
    }

    public void registrarse(View vista) throws IOException {
        //Spinner splocalidad=findViewById(R.id.spinner);
        String nombre=txtnombre.getText().toString();
        String apellidos=txtapellidos.getText().toString();
        int edad=0;
        try {
            edad=Integer.parseInt(txtedad.getText().toString());
        }catch (Exception e){
            //mesaje error
            return;
        }
        String sexo=txtsexo.getText().toString();
        String estadoCivil=txtestadoCivil.getText().toString();
        String usuario=txtusuario.getText().toString();
        String contrasena=txtcontrasena.getText().toString();
        //String localidad= String.valueOf(splocalidad);//No se si funciona xd
        //int idLocalidad=sacarIdLocalidad(localidad);
        int idLocalidad=sacarIdLocalidad("azuaga");
        if(idLocalidad==-1){
            //mensaje de que no se encontro la localidad
            return;
        }
        txtnombre.setText("funciona");
        //insertarPersona(nombre, apellidos, edad, sexo, estadoCivil, usuario, contrasena, idLocalidad);
    }

    private int sacarIdLocalidad(String localidad) throws IOException {
        //falta por programar
        int idLocalidad=-1;
        // Codificar el nombre de la localidad para que pueda ser enviado en la URL
        String nombreLocalidadEncoded = URLEncoder.encode(localidad, "UTF-8");

        // URL del script PHP con el parámetro del nombre de la localidad
        String url = "http://localhost/extreventos/sacarIdLocalidades.php?nombre_localidad=" + nombreLocalidadEncoded;

        // Crear conexión
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        // Configurar la conexión
        con.setRequestMethod("GET");

        // Leer la respuesta
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            //(ID de la localidad)
            idLocalidad=Integer.parseInt(response.toString());
        }

        // Cerrar conexión
        con.disconnect();
        //xd
        return idLocalidad;
    }

    public void volverLogin(View vista){
        finish();
    }
    public static void insertarPersona(String nombre, String apellidos, int edad, String sexo, String estadoCivil,
                                       String usuario, String contrasena, int idLocalidad) {
        try {
            // URL del script PHP
            URL url = new URL("http://localhost/ExtrEventos/insertarUsuarios.php");

            // Abrir conexión
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Configurar la conexión
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);

            // Crear parámetros para enviar
            String parametros = "nombre=" + nombre +
                    "&apellidos=" + apellidos +
                    "&edad=" + edad +
                    "&sexo=" + sexo +
                    "&estado_civil=" + estadoCivil +
                    "&usuario=" + usuario +
                    "&contrasena=" + contrasena +
                    "&id_localidad=" + idLocalidad;

            // Enviar datos
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                byte[] postData = parametros.getBytes(StandardCharsets.UTF_8);
                wr.write(postData);
            }

            // Leer respuesta
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // Imprimir respuesta
                System.out.println(response.toString());
            }

            // Cerrar conexión
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
