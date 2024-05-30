package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.prueba.modelo.Evento_Gratis;
import com.example.prueba.modelo.Evento_Pago;
import com.example.prueba.modelo.Localidad;
import com.example.prueba.utiles.constantes;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class ControladorEditEvento extends AppCompatActivity {

    String user;
    Evento_Gratis eventoGratis;
    Evento_Pago eventoPago;
    TextView textViewDate;
    Button buttonSelectDate;
    Spinner spLocalidades;
    TextInputLayout lyprecio, lypuntoventa, lydescripcionadicional, lytipo;
    EditText txtnombre, txtdescripcion, txtubicacion, txtprecio, txttipo, txtdescripcionAdicional, txtpuntoDeVenta;
    ArrayList <Localidad> localidades=new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_evento);
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            user=extras.getString("user");
            eventoPago= (Evento_Pago) extras.get("eventoPago");
            eventoGratis=(Evento_Gratis)  extras.get("eventoGratis");
        }
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editEvento), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void init(){
        lypuntoventa=findViewById(R.id.lypuntoVenta);//ver
        lyprecio=findViewById(R.id.lypresio);
        lytipo=findViewById(R.id.lytipo);
        lydescripcionadicional=findViewById(R.id.ly2x);
        textViewDate = findViewById(R.id.textview_date2);
        spLocalidades=findViewById(R.id.spLocalidades);
        new SacarLocalidades().execute();
        buttonSelectDate=findViewById(R.id.button_select_date);
        buttonSelectDate.setOnClickListener(v -> showDatePickerDialog());
        txtnombre=findViewById(R.id.txtnombre);
        txtdescripcion=findViewById(R.id.txtDescripcion2);
        txtubicacion=findViewById(R.id.txtUbicacion2);
        txtprecio=findViewById(R.id.txtPrecio2);
        txttipo=findViewById(R.id.txtTipo2);
        txtdescripcionAdicional=findViewById(R.id.txtDescripcionAdicional2);
        txtpuntoDeVenta=findViewById(R.id.txtPuntoVenta2);
        if (eventoGratis==null){
            rellenarCamposEvPago();
        } else if (eventoPago==null) {
            rellenarCamposEvGratis();
        }else{
            System.out.println("Paranoyaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        }


    }

    private void rellenarCamposEvGratis() {
        lyprecio.setVisibility(View.INVISIBLE);
        lypuntoventa.setVisibility(View.INVISIBLE);
        txtnombre.setText(eventoGratis.getNombre());
        txttipo.setText(eventoGratis.getTipo());
        txtdescripcion.setText(eventoGratis.getDescripcion());
        txtubicacion.setText(eventoGratis.getUbicacion());
        txtdescripcionAdicional.setText(eventoGratis.getDescripcionAdicional());
        textViewDate.setText(eventoGratis.getFecha().toString());
        int i=0;
        for (Localidad l : localidades){
            if (l.getNombre().equals(eventoGratis.getLocalidad())){
                spLocalidades.setSelection(i);
            }
            i++;
        }
    }

    private void rellenarCamposEvPago() {
        lydescripcionadicional.setVisibility(View.INVISIBLE);
        lytipo.setVisibility(View.INVISIBLE);
        txtnombre.setText(eventoPago.getNombre());
        txtdescripcion.setText(eventoPago.getDescripcion());
        txtubicacion.setText(eventoPago.getUbicacion());
        txtprecio.setText(eventoPago.getPrecio()+"");
        txtpuntoDeVenta.setText(eventoPago.getPuntoDeVenta());
        int i=0;
        for (Localidad l : localidades){
            if (l.getNombre().equals(eventoPago.getLocalidad())){
                spLocalidades.setSelection(i);
            }
            i++;
        }
        textViewDate.setText(eventoPago.getFecha().toString());
    }


    private void showDatePickerDialog() {
        // Obtener la fecha actual
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Crear el DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            // Formatear la fecha seleccionada y mostrarla en el TextView
            String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
            textViewDate.setText(selectedDate);
        }, year, month, day);

        // Mostrar el DatePickerDialog
        datePickerDialog.show();
    }

    public void confirmarCambios(View view) {
    }

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
                Toast.makeText(ControladorEditEvento.this, "Error loading localidades", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void cargarDatosSpinner() {
        ArrayList<String> nombreLoc=new ArrayList<>();
        for(Localidad l:localidades){
            nombreLoc.add(l.getNombre());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, nombreLoc);
        spLocalidades.setAdapter(adapter);
    }
}
