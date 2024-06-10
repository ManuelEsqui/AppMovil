package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prueba.R;
import com.example.prueba.modelo.Evento;
import com.example.prueba.modelo.Localidad;
import com.example.prueba.utiles.constantes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ControladorVistaUsuarios extends AppCompatActivity {

    String user;
    Toolbar toolbar;
    ListView listaEventos;
    ArrayList<Evento> arrayEventos=new ArrayList<>();
    ArrayList<String> arrDatos;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new extraerEventos().execute("extraerEventos.php");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.usuariosView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initDatos() {
        listaEventos=findViewById(R.id.listViewEventos);
        // Adaptador para llenar el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrDatos);
        listaEventos.setAdapter(adapter);
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            user=extras.getString("user");
        }
    }

    private ArrayList<String> llenarArraylistString() {
        ArrayList<String> data= new ArrayList<>();
        for (Evento e:arrayEventos) {
            data.add(e.getNombre()+"  -  "+e.getLocalidad());
        }
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu) {
            System.out.println(user);
            Intent intent = new Intent(this, ControladorEdicionDatos.class);
            intent.putExtra("user", user);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu2) {
            Intent intent=new Intent(this, listaLocalidades.class);
            intent.putExtra("user", user);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu3) {
            arrayEventos.clear();
            new extraerEventos().execute("mostrarEventosPago.php");
            return true;
        } else if (id == R.id.menu4) {
            arrayEventos.clear();
            new extraerEventos().execute("mostrarEventosGratis.php");
            return true;
        }else if (id == R.id.menu5) {
            Intent i=new Intent(this, ControladorMisEventos.class);
            i.putExtra("user", user);
            startActivity(i);
            return true;
        }else if (id == R.id.menu6) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class extraerEventos extends AsyncTask<String, Void, String> {
        ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Inicializar y mostrar el ProgressDialog
            progreso= new ProgressDialog(ControladorVistaUsuarios.this);
            progreso.setMessage("Cargando datos...");
            progreso.show();
        }
        @Override
        protected String doInBackground(String... params) {
            StringBuilder response = new StringBuilder();
            //String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/extraerEventos.php";
            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/"+params[0];

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
                // Primero separa por la / y luego por el guion
                String[] EventosArray = result.split("/");
                for (String EventoStr : EventosArray) {
                    String[] datos = EventoStr.split(" - ");
                    //1 - Feria de Artesanía - Exposición de artesanías locales - 2024-06-22 - Parque - Don Benito
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    String descripcion=datos[2];
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fecha = null;
                    try {
                        fecha=dateFormat.parse(datos[3]);
                    } catch (ParseException e) {
                        Toast.makeText(ControladorVistaUsuarios.this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
                    }
                    String ubicacion=datos[4];
                    String localidad=datos[5];
                    //int id,String nombre, String descripcion, String localidad, String ubicacion, Date fecha
                    Evento e=new Evento(id, nombre, descripcion, localidad, ubicacion, fecha);
                    arrayEventos.add(e);
                    arrDatos =llenarArraylistString();
                    initDatos();
                    progreso.dismiss();
                    listaEventos.setOnItemClickListener((parent, view, position, id1) -> {
                        Evento ev =arrayEventos.get(position);
                        Intent i=new Intent(ControladorVistaUsuarios.this, ControladorDatosEventos.class);
                        i.putExtra("user", user);
                        i.putExtra("evento", ev);
                        startActivity(i);
                    });
                }
            } else {
                Toast.makeText(ControladorVistaUsuarios.this, "Error cargando los eventos", Toast.LENGTH_LONG).show();
            }
        }
    }
    //sobreescribe el metodo de pulsar en la fecha de atras
    @Override
    public void onBackPressed() {
        super.onBackPressed();
                Intent intent = new Intent(ControladorVistaUsuarios.this, LogIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
    }
}
