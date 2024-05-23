package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.net.URL;;

public class ControladorDatosEventos extends AppCompatActivity {

    String user;
    Evento evento;

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
        }
        new ObtenerEventoCompletoAsyncTask().execute();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.infoEventos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void rellenarDatosEventoGratis(Evento_Gratis eventoGratis){
        Toast.makeText(this, user+" "+eventoGratis.getId(), Toast.LENGTH_SHORT).show();
    }
    private void rellenarDatosEventoPago(Evento_Pago eventoPago){
        Toast.makeText(this, user+" "+eventoPago.getId(), Toast.LENGTH_SHORT).show();
    }




    private class ObtenerEventoCompletoAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String requestURL = "http://"+ constantes.LOCALHOST+"/extreventos/obtenerEventoCompleto.php";
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
                    Evento_Gratis eventoGratis=new Evento_Gratis(evento, tipo, descripcionAdicional);
                    rellenarDatosEventoGratis(eventoGratis);
                }
            }else{
                Toast.makeText(ControladorDatosEventos.this, "No se han econtrado los datos", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
