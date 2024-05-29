package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.prueba.R;
import com.example.prueba.modelo.Evento;
import com.example.prueba.modelo.Evento_Gratis;
import com.example.prueba.modelo.Evento_Pago;
import com.example.prueba.utiles.constantes;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;;

public class ControladorDatosEventos extends AppCompatActivity {

    String user;
    Evento evento;
    EditText txtNombre, txtLocalidad, txtUbicacion, txtFecha, txtPrecioTipo, txtPuntoVenta;
    TextView txtdescripcion, txtDescAdicional;
    TextView puntoVenta, precioTipo, descripcionAdicional;
    boolean admin;
    Button btnEliminarEvento, btnEditarevento;
    Button btnApuntarse;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.info_de_eventos);
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            user=extras.getString("user");
            evento= (Evento) extras.get("evento");
            admin=extras.getBoolean("admin");
        }
        init();
        new ObtenerEventoCompletoAsyncTask().execute();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.infoEventos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void init(){
        txtNombre=findViewById(R.id.txtNombreEv);
        txtLocalidad=findViewById(R.id.txtLocalidadEv);
        txtUbicacion=findViewById(R.id.txtUbicacionEv);
        txtFecha=findViewById(R.id.txtFechaEv);
        txtPrecioTipo=findViewById(R.id.txtPrecioTipo);
        txtPuntoVenta=findViewById(R.id.editTextText6);
        txtDescAdicional=findViewById(R.id.txtDescAdicional);
        txtdescripcion=findViewById(R.id.txtDescripcion);
        puntoVenta=findViewById(R.id.tvPuntoVenta);
        precioTipo=findViewById(R.id.textViewPrecioTipo);
        descripcionAdicional=findViewById(R.id.tvDescAdicional);
        btnEditarevento=findViewById(R.id.btnEditarEvento);
        btnEliminarEvento=findViewById(R.id.btnEliminarEvento);
        btnApuntarse=findViewById(R.id.btnApuntarse);
        if (admin){
            btnEliminarEvento.setVisibility(View.VISIBLE);
            btnEditarevento.setVisibility(View.VISIBLE);
            btnApuntarse.setVisibility(View.INVISIBLE);
        }
    }
    @SuppressLint("SetTextI18n")
    private void rellenarDatosEventoGratis(Evento_Gratis eventoGratis){
        System.out.println(eventoGratis.toString());
        //Se hacen invisible los componentes no necesarios
        txtPuntoVenta.setVisibility(View.INVISIBLE);
        puntoVenta.setVisibility(View.INVISIBLE);
        //se rellena el resto
        txtNombre.setText(eventoGratis.getNombre());
        txtLocalidad.setText(eventoGratis.getLocalidad());
        txtUbicacion.setText(eventoGratis.getUbicacion());
        String pattern = "MM/dd/yyyy";
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat(pattern);
        String fecha=df.format(eventoGratis.getFecha());
        txtFecha.setText(fecha);
        txtdescripcion.setText(eventoGratis.getDescripcion());
        precioTipo.setText("Tipo");
        txtPrecioTipo.setText(eventoGratis.getTipo());
        txtDescAdicional.setText(eventoGratis.getDescripcionAdicional());
    }

    public void atras(View v){
        volverAtras();
    }

    private void volverAtras() {
        /*Intent intent = new Intent(this, ControladorVistaUsuarios.class);
        intent.putExtra("user", user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);*/
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void rellenarDatosEventoPago(Evento_Pago eventoPago){
        System.out.println(eventoPago.toString());
        //Invisible componentes no necesarios
        descripcionAdicional.setVisibility(View.INVISIBLE);
        txtDescAdicional.setVisibility(View.INVISIBLE);
        //Se rellena
        txtNombre.setText(eventoPago.getNombre());
        txtLocalidad.setText(eventoPago.getLocalidad());
        txtUbicacion.setText(eventoPago.getUbicacion());
        String pattern = "MM/dd/yyyy";
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat(pattern);
        String fecha=df.format(eventoPago.getFecha());
        txtFecha.setText(fecha);
        txtdescripcion.setText(eventoPago.getDescripcion());
        txtPrecioTipo.setText(eventoPago.getPrecio()+"");
        precioTipo.setText("Precio");
        txtPuntoVenta.setText(eventoPago.getPuntoDeVenta());
    }

    public void apuntarse(View view) {
        new ApuntarseAsyncTask().execute();
    }

    public void asistenciaEvento(View view) {
        Intent intent=new Intent(this, ControladorAsistentes.class);
        intent.putExtra("idEvento", evento.getId());
        intent.putExtra("user", user);
        intent.putExtra("admin", admin);
        startActivity(intent);
    }

    public void eliminarEvento(View view) {
        new EliminarAsyncTask().execute();
    }

    public void editarEvento(View view) {
    }


    private class ObtenerEventoCompletoAsyncTask extends AsyncTask<Void, Void, String> {
        ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Inicializar y mostrar el ProgressDialog
            progreso= new ProgressDialog(ControladorDatosEventos.this);
            progreso.setMessage("Cargando datos...");
            progreso.setCancelable(false);//no se puede cancelar
            progreso.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            String requestURL = "http://"+ constantes.LOCALHOST+"/extreventos/obtenerEventoCompleto.php?id="+evento.getId();
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

            if (!result.isEmpty()){
                String[] datosArray = result.split("/");
                try {
                    float precio=Float.parseFloat(datosArray[0]);
                    String puntoVenta=datosArray[1];
                    Evento_Pago eventoPago=new Evento_Pago(evento, precio, puntoVenta);
                    rellenarDatosEventoPago(eventoPago);
                }catch (Exception e){
                    String tipo=datosArray[0];
                    String descripcionAdicional=datosArray[1];
                    Evento_Gratis eventoGratis=new Evento_Gratis(evento, descripcionAdicional, tipo);
                    rellenarDatosEventoGratis(eventoGratis);
                }finally {
                    progreso.dismiss();
                }
            }else{
                Toast.makeText(ControladorDatosEventos.this, "No se han econtrado los datos", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private class ApuntarseAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String requestURL = "http://"+ constantes.LOCALHOST+"/extreventos/apuntarseEventos.php?usuario=" + user+"&id_evento="+evento.getId();
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

            if(result.equals("Has sido añadido a los participantes.")){
                Toast.makeText(ControladorDatosEventos.this, result, Toast.LENGTH_SHORT).show();
                volverAtras();
            }else{
                Toast.makeText(ControladorDatosEventos.this, "Ya participas en este evento", Toast.LENGTH_SHORT).show();
            }

        }


    }
    private class EliminarAsyncTask extends AsyncTask<Void, Void, String> {
        ProgressDialog progreso;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Inicializar y mostrar el ProgressDialog
            progreso= new ProgressDialog(ControladorDatosEventos.this);
            progreso.setMessage("Cargando datos...");
            progreso.setCancelable(false);//no se puede cancelar
            progreso.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            String requestURL = "http://"+ constantes.LOCALHOST+"/extreventos/eliminarEventos.php?id_evento="+evento.getId();
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
            Toast.makeText(ControladorDatosEventos.this, result, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
