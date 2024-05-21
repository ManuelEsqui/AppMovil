package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prueba.R;
import com.example.prueba.modelo.Localidad;
import com.example.prueba.utiles.constantes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ControladorEdicionDatos extends AppCompatActivity {

    String user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.editar_datos_usuarios);
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            user=extras.getString("user");
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edicionDatos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    EditText txtnombre;
    EditText txtapellidos;
    EditText txtedad;
    EditText txtsexo;
    EditText txtestadoCivil;
    EditText txtusuario;
    EditText txtcontrasena;
    Spinner spLocalidades;
    ArrayList<Localidad> localidades;
    CheckBox admin;
    private void initComp() throws IOException {
        localidades=new ArrayList<>();
        txtnombre=findViewById(R.id.txtNombre);
        txtapellidos=findViewById(R.id.txtApellidos);
        txtedad=findViewById(R.id.txtEdad);
        txtsexo=findViewById(R.id.txtsexo);
        txtestadoCivil=findViewById(R.id.txtEstadoCivil);
        txtusuario=findViewById(R.id.txtUsuario);
        txtcontrasena=findViewById(R.id.txtContraseña);
        spLocalidades=findViewById(R.id.spinner);
        admin=findViewById(R.id.admin);
        admin.setVisibility(View.INVISIBLE);
        new SacarLocalidades().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class SacarLocalidades extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder response = new StringBuilder();
            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/sacarLocalidades.php";

            try {
                URL url = new URL(requestURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                } else {
                    response.append("Error in server response. Code: ").append(responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                response.append("Exception: ").append(e.getMessage());
            }

            return response.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            if (!result.isEmpty()) {
                // Parse the response and update localidades
                String[] localidadesArray = result.split("/");
                for (String localidadStr : localidadesArray) {
                    String[] idNombre = localidadStr.split("-");
                    int id = Integer.parseInt(idNombre[0]);
                    String nombre = idNombre[1];
                    Localidad localidad = new Localidad(id, nombre);
                    localidades.add(localidad);
                }

                // Update the spinner on the UI thread
                cargarDatosSpinner();
            } else {
                Toast.makeText(ControladorEdicionDatos.this, "Error loading localidades", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void cargarDatosSpinner() {
        ArrayList <String> nombreLoc=new ArrayList<>();
        for(Localidad l:localidades){
            nombreLoc.add(l.getNombre());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, nombreLoc);
        spLocalidades.setAdapter(adapter);
        //posicion por defecto
    }
    public void registrarse(View vista) throws IOException {
        String nombre=txtnombre.getText().toString();
        String apellidos=txtapellidos.getText().toString();
        int edad=0;
        try {
            edad=Integer.parseInt(txtedad.getText().toString());
        }catch (Exception e){
            Toast.makeText(this, "Debes introducir un numero en el campo edad", Toast.LENGTH_SHORT).show();
            return;
        }
        String sexo=txtsexo.getText().toString();
        String estadoCivil=txtestadoCivil.getText().toString();
        String usuario=txtusuario.getText().toString();
        String contrasena=txtcontrasena.getText().toString();
        int posicion = spLocalidades.getSelectedItemPosition();
        Localidad localidad= localidades.get(posicion);
        if (nombre.isEmpty() || apellidos.isEmpty() || edad==0 || sexo.isEmpty() || estadoCivil.isEmpty() || usuario.isEmpty()){
            Toast.makeText(this, "Debes introducir todos los campos del registro", Toast.LENGTH_SHORT).show();
            return;
        }
        if(contrasena.length()<8){
            Toast.makeText(this, "La contraseña no es segura", Toast.LENGTH_SHORT).show();
            return;
        }
        new editarPersona().execute(nombre, apellidos, edad+"", sexo, estadoCivil, usuario, contrasena, localidad.getId()+"");
    }
    private class editarPersona extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String parametros = "nombre=" + params[0] +
                    "&apellidos=" + params[1] +
                    "&edad=" + params[2] +
                    "&sexo=" + params[3] +
                    "&estado_civil=" + params[4] +
                    "&usuario=" + params[5] +
                    "&contrasena=" + params[6] +
                    "&id_localidad=" + params[7] +
                    "&admin=" + admin.isChecked();

            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/editarUsuarios.php?"+parametros;
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(requestURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                        Toast.makeText(ControladorEdicionDatos.this, "Usuario editado correctamente", Toast.LENGTH_LONG).show();
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
            finish();
        }
    }
    private class SacarPersonas extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder response = new StringBuilder();
            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/extraerPersona.php?user="+params[0];

            try {
                URL url = new URL(requestURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                } else {
                    response.append("Error in server response. Code: ").append(responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                response.append("Exception: ").append(e.getMessage());
            }

            return response.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            if (!result.isEmpty()) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(result);
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONArray data = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject clienteJson = data.getJSONObject(i);
                            String nombre=clienteJson.getString("nombre");//Falta por terminar
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                } else {
                Toast.makeText(ControladorEdicionDatos.this, "Error cargando el usuario", Toast.LENGTH_LONG).show();
            }
        }
    }

}
