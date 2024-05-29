package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prueba.R;
import com.example.prueba.modelo.Localidad;
import com.example.prueba.utiles.constantes;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class agregarEventos extends AppCompatActivity {

    private TextView textViewDate;
    private TextInputLayout lyPuntoVenta, lyDescripcionAdicional;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switch1;
    private EditText txtNombre, txtDescripcion, txtLugar, txtPrecioTipo, txtDescripcionAdicional, txtPuntoVenta;
    private Spinner spCiudades;
    Date fecha;
    ArrayList <Localidad> localidades=new ArrayList<>();
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.agregar_eventos);
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.agregarEventos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void init(){
        txtNombre=findViewById(R.id.txtNombre);
        txtDescripcion=findViewById(R.id.editTextDescripcion);
        txtLugar=findViewById(R.id.txtLugar);
        spCiudades=findViewById(R.id.spinnerLocalidades);
        new SacarLocalidades().execute();
        txtPrecioTipo=findViewById(R.id.txttipoPrecio);
        txtDescripcionAdicional=findViewById(R.id.txtDescripcionAdicional);
        txtPuntoVenta=findViewById(R.id.puntoDeVenta);
        Button buttonSelectDate = findViewById(R.id.button_select_date);
        lyPuntoVenta = findViewById(R.id.lyPuntoVenta);
        lyDescripcionAdicional=findViewById(R.id.textInputLayoutDA);
        textViewDate = findViewById(R.id.textview_date);
        buttonSelectDate.setOnClickListener(v -> showDatePickerDialog());
        switch1 =findViewById(R.id.switchTipoEvento);
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch está en estado "on"
                switch1.setText("Evento de pago");
                camposEventoPago();
            } else {
                // Switch está en estado "off"
                switch1.setText("Evento gratis");
                camposEventoGratis();
            }
        });
    }

    private void camposEventoPago() {
        lyPuntoVenta.setVisibility(View.VISIBLE);
        lyDescripcionAdicional.setVisibility(View.INVISIBLE);
        txtPrecioTipo.setHint("Precio");
    }

    private void camposEventoGratis() {
        lyPuntoVenta.setVisibility(View.INVISIBLE);
        lyDescripcionAdicional.setVisibility(View.VISIBLE);
        txtPrecioTipo.setHint("Tipo");
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
                    String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    textViewDate.setText(selectedDate);

            // Guardar la fecha seleccionada en un objeto Date
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year1, month1, dayOfMonth);
            fecha = selectedCalendar.getTime();
                }, year, month, day);

        // Mostrar el DatePickerDialog
        datePickerDialog.show();
    }

    public void agregarEvento(View view) {
        String nombre=txtNombre.getText().toString();
        String descripcion=txtDescripcion.getText().toString();
        String ubicacion=txtLugar.getText().toString();
        int posicion = spCiudades.getSelectedItemPosition();
        Localidad localidad= localidades.get(posicion);
        if(nombre.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty()){
            Toast.makeText(this, "Deben de estar todos los campos rellenos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (switch1.isChecked()){
            String puntoVenta=txtPuntoVenta.getText().toString();
            float precio;
            try {
                precio=Float.parseFloat(txtPrecioTipo.getText().toString());
            }catch (Exception e){
                Toast.makeText(this, "El precio debe ser un numero", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!puntoVenta.isEmpty()){
                //añadir evento de pago
            }else{
                Toast.makeText(this, "Tienes que escribir el lugar donde se vende la entrada", Toast.LENGTH_SHORT).show();
            }
        }else{
            String tipo=txtPrecioTipo.getText().toString();
            String descripcionAdicional=txtDescripcionAdicional.getText().toString();
            if (tipo.isEmpty() || descripcionAdicional.isEmpty()){
                Toast.makeText(this, "Debes rellenar el campo tipo y descripcion adicional", Toast.LENGTH_SHORT).show();
            }else{
                //añadir evento gratuito
            }
        }
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
                Toast.makeText(agregarEventos.this, "Error loading localidades", Toast.LENGTH_LONG).show();
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
        spCiudades.setAdapter(adapter);
        int i=0;
        for (Localidad l: localidades) {
            if (l.getId()==6){
                spCiudades.setSelection(i);
            }
            i++;
        }
    }

}
