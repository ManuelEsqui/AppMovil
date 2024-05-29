package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prueba.R;
import com.example.prueba.modelo.Evento;
import com.example.prueba.utiles.constantes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ControladorGestionEventos extends AppCompatActivity {
    String user;
    ListView listaEventos;
    ArrayList<Evento> arrayEventos=new ArrayList<>();
    ArrayList<String> arrDatos=new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.gestion_eventos);
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            user=extras.getString("user");
        }
        new extraerEventos().execute();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gestionEventos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void init(){
        listaEventos=findViewById(R.id.listaEventos);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrDatos);
        listaEventos.setAdapter(adapter);
    }
    public void volver(View view) {
        Intent intent = new Intent(this, ControladorMenuAdmin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void insertarEvento(View view) {
        Intent intent = new Intent(this, agregarEventos.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
    private class extraerEventos extends AsyncTask<Void, Void, String> {
        ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Inicializar y mostrar el ProgressDialog
            progreso= new ProgressDialog(ControladorGestionEventos.this);
            progreso.setMessage("Cargando datos...");
            progreso.show();
        }
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder response = new StringBuilder();
            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/extraerEventos.php";

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
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    String descripcion=datos[2];
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fecha = null;
                    try {
                        fecha=dateFormat.parse(datos[3]);
                    } catch (ParseException e) {
                        Toast.makeText(ControladorGestionEventos.this, "Ocurrio un error", Toast.LENGTH_SHORT).show();
                    }
                    String ubicacion=datos[4];
                    String localidad=datos[5];
                    //int id,String nombre, String descripcion, String localidad, String ubicacion, Date fecha
                    Evento e=new Evento(id, nombre, descripcion, localidad, ubicacion, fecha);
                    arrayEventos.add(e);
                    String pattern = "dd/MM/yyyy";
                    @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat(pattern);
                    arrDatos.add(nombre+" - "+df.format(fecha));
                }
                init();
                progreso.dismiss();
                listaEventos.setOnItemClickListener((parent, view, position, id1) -> {
                    Evento ev =arrayEventos.get(position);
                    Intent i=new Intent(ControladorGestionEventos.this, ControladorDatosEventos.class);
                    i.putExtra("user", user);
                    i.putExtra("evento", ev);
                    i.putExtra("admin", true);
                    startActivity(i);
                });
            } else {
                Toast.makeText(ControladorGestionEventos.this, "Error cargando los eventos", Toast.LENGTH_LONG).show();
            }
        }
    }
}
