//Manuel Esquivel sevillano 2ºDAM
package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prueba.R;
import com.example.prueba.modelo.Localidad;
import com.example.prueba.modelo.Usuario;
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

    String user; // Almacena el nombre de usuario
    String userAux;
    Usuario usuarioEditar; // Almacena el objeto Usuario a editar

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Habilitar el modo EdgeToEdge
        EdgeToEdge.enable(this);
        setContentView(R.layout.editar_datos_usuarios);
        // Obtener los extras del Intent
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            user=extras.getString("user");// Obtener el nombre de usuario
            userAux=extras.getString("usuAdmin");// Obtener el nombre de usuario y lo almacena en una variable auxiliar
            usuarioEditar=(Usuario) extras.get("usuarioEditar");// Obtener el usuario a editar
        }
        try {
            initComp();// Inicializar componentes
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Configurar insets de ventana para la vista principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edicionDatos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    // Declaración de componentes de la interfaz de usuario
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
    Usuario usuario;
    TextView txtSaludo;
    int localidad_id;
    // Método para inicializar los componentes de la interfaz
    private void initComp() throws IOException {
        // Inicializar componentes de la interfaz
        localidades=new ArrayList<>();
        txtSaludo=findViewById(R.id.textViewPrincipal);
        txtnombre=findViewById(R.id.txtNombre);
        txtapellidos=findViewById(R.id.txtApellidos);
        txtedad=findViewById(R.id.txtEdad);
        txtsexo=findViewById(R.id.txtsexo);
        txtestadoCivil=findViewById(R.id.txtEstadoCivil);
        txtusuario=findViewById(R.id.txtUsuario);
        txtcontrasena=findViewById(R.id.txtContraseña);
        spLocalidades=findViewById(R.id.spinner);
        admin=findViewById(R.id.admin);
        // Configuración de visibilidad y saludo para el usuario a editar
        if(usuarioEditar==null){
            admin.setVisibility(View.INVISIBLE);
        }else {
            txtSaludo.setText("Edita a "+usuarioEditar.getNombre());
            this.user=usuarioEditar.getUser();
        }
        // Ejecutar tarea asíncrona para obtener localidades
        new SacarLocalidades().execute();
    }
    // Método para volver a la ventana de usuarios
    public void deleteAccount(View view) {
        // Crear el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Estás seguro de que deseas eliminar esta cuenta?");

        // Botón Aceptar
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Método para eliminar una cuenta de usuario
            new DeleteUsuarioAsyncTask().execute();
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", (dialog, which) -> Toast.makeText(this, "Opción cancelada", Toast.LENGTH_SHORT).show());

        // Crear y mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void volverVentanaUsuarios(View view) {
        finish();
    }

    // Clase AsyncTask para obtener localidades desde el servidor
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
                new SacarPersonas().execute();
            } else {
                Toast.makeText(ControladorEdicionDatos.this, "Error loading localidades", Toast.LENGTH_LONG).show();
            }
        }
    }
    //metodo para cargar los datos del spinner
    private void cargarDatosSpinner() {
        ArrayList <String> nombreLoc=new ArrayList<>();
        for(Localidad l:localidades){
            nombreLoc.add(l.getNombre());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, nombreLoc);
        spLocalidades.setAdapter(adapter);
    }
// metodo para rellenar los campos de los usuarios
    private void rellenarCampos() {
        txtnombre.setText(usuario.getNombre());
        txtapellidos.setText(usuario.getApellidos());
        txtedad.setText(usuario.getEdad()+"");
        txtsexo.setText(usuario.getSexo());
        txtestadoCivil.setText(usuario.getEstadoCivil());
        txtusuario.setText(usuario.getUser());
        txtcontrasena.setText(usuario.getPasswrd());
        if (usuario.isAdmin()){
            admin.setVisibility(View.VISIBLE);
            admin.setChecked(true);
        }
        //rellenar los datos del spinner
        int i=0;
        for (Localidad l: localidades) {
            if (l.getId()==localidad_id){
                spLocalidades.setSelection(i);
            }
            i++;
        }
    }

    //método que comprueba que los datos esten rellenenos y haga una consulta para editarlos
    public void editDatos(View vista) throws IOException {
        String nombre=txtnombre.getText().toString();
        String apellidos=txtapellidos.getText().toString();
        int edad;
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
    //metodo para editar la persona en una tarea asincrona
    @SuppressLint("StaticFieldLeak")
    private class editarPersona extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            System.out.println(user);
            String parametros = "nombre=" + params[0] +
                    "&apellidos=" + params[1] +
                    "&edad=" + params[2] +
                    "&sexo=" + params[3] +
                    "&estado_civil=" + params[4] +
                    "&usuario=" + params[5] +
                    "&contrasena=" + params[6] +
                    "&id_localidad=" + params[7] +
                    "&admin=" + admin.isChecked() +
                    "&usuarioActualizar=" + user;

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
            Toast.makeText(ControladorEdicionDatos.this, "Usuario editado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    //Extraer los datos de la persona
    private class SacarPersonas extends AsyncTask<Void, Void, String> {
        ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Inicializar y mostrar el ProgressDialog
            progreso= new ProgressDialog(ControladorEdicionDatos.this);
            progreso.setMessage("Cargando datos...");
            progreso.show();
        }
        @Override
        protected String doInBackground(Void... Voids) {
            StringBuilder response = new StringBuilder();
            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/extraerPersona.php?user="+user;

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
        //Se obtiene el objeto en json y se transforma en un objeto de la clase usuario
        @Override
        protected void onPostExecute(String result) {
            System.out.println(result);
            if (!result.isEmpty()) {
                JSONObject jsonResponse;
                try {
                    jsonResponse = new JSONObject(result);
                    String nombre;
                    String apellidos="";
                    String sexo="";
                    String estadoCivil="";
                    String nickname="";
                    String passwrd="";
                    int edad=-1;
                    int intAdmin=0;
                    boolean admin=false;
                            nombre=jsonResponse.getString("nombre");
                            if (jsonResponse.has("apellidos"))apellidos=jsonResponse.getString("apellidos");
                            if (jsonResponse.has("sexo"))sexo=jsonResponse.getString("sexo");
                            if (jsonResponse.has("estadoCivil"))estadoCivil=jsonResponse.getString("estadoCivil");
                            if (jsonResponse.has("user"))nickname=jsonResponse.getString("user");
                            if (jsonResponse.has("passwrd"))passwrd=jsonResponse.getString("passwrd");
                            if (jsonResponse.has("edad"))edad=jsonResponse.getInt("edad");
                            if (jsonResponse.has("admin"))intAdmin= jsonResponse.getInt("admin");
                            if (jsonResponse.has("localidad_id"))localidad_id= jsonResponse.getInt("localidad_id");
                            if (intAdmin==1) admin=true;
                            usuario=new Usuario(nombre, apellidos, sexo, estadoCivil, nickname, passwrd, edad, admin);
                            rellenarCampos();
                            progreso.dismiss();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                } else {
                Toast.makeText(ControladorEdicionDatos.this, "Error cargando el usuario", Toast.LENGTH_LONG).show();
            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    private class DeleteUsuarioAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String requestURL = "http://"+ constantes.LOCALHOST+"/extreventos/eliminar_usuario.php?usuario=" + user;
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
            if (usuarioEditar!=null) {
                Intent intent = new Intent(ControladorEdicionDatos.this, ControladorListaUsuarios.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("user", userAux);
                startActivity(intent);
            }else{
                Toast.makeText(ControladorEdicionDatos.this, result, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ControladorEdicionDatos.this, LogIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

}
