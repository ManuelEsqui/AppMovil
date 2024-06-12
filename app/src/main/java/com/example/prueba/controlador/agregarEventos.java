//Manuel Esquivel Sevillano
package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

/**
 * Clase agregarEventos que extiende AppCompatActivity.
 * Esta clase maneja la lógica para agregar eventos, ya sean de pago o gratuitos.
 */
public class agregarEventos extends AppCompatActivity {

    private TextView textViewDate;
    private TextInputLayout lyPuntoVenta, lyDescripcionAdicional, lytipoPrecio;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switch1;
    private EditText txtNombre, txtDescripcion, txtLugar, txtPrecioTipo, txtDescripcionAdicional, txtPuntoVenta;
    private Spinner spCiudades;
    String user;
    String fecha;
    ArrayList<Localidad> localidades = new ArrayList<>();

    /**
     * Método onCreate. Se ejecuta al crear la actividad.
     *
     * @param savedInstanceState Estado de la instancia.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.agregar_eventos);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("user");
        }
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.agregarEventos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Método init. Inicializa los componentes de la interfaz y establece listeners.
     */
    private void init() {
        txtNombre = findViewById(R.id.txtNombre);
        txtDescripcion = findViewById(R.id.editTextDescripcion);
        txtLugar = findViewById(R.id.txtLugar);
        spCiudades = findViewById(R.id.spinnerLocalidades);
        new SacarLocalidades().execute();
        txtPrecioTipo = findViewById(R.id.txttipoPrecio);
        txtDescripcionAdicional = findViewById(R.id.txtDescripcionAdicional2);
        txtPuntoVenta = findViewById(R.id.puntoDeVenta);
        Button buttonSelectDate = findViewById(R.id.button_select_date);
        lyPuntoVenta = findViewById(R.id.lyPuntoVenta);
        lyDescripcionAdicional = findViewById(R.id.textInputLayoutDA);
        lytipoPrecio = findViewById(R.id.lytipoPrecio);
        textViewDate = findViewById(R.id.textview_date2);
        buttonSelectDate.setOnClickListener(v -> showDatePickerDialog());
        switch1 = findViewById(R.id.switchTipoEvento);
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switch1.setText("Evento de pago");
                camposEventoPago();
            } else {
                switch1.setText("Evento gratis");
                camposEventoGratis();
            }
        });
    }

    /**
     * Método para configurar los campos cuando el evento es de pago.
     */
    private void camposEventoPago() {
        lyPuntoVenta.setVisibility(View.VISIBLE);
        lyDescripcionAdicional.setVisibility(View.INVISIBLE);
        lytipoPrecio.setHint("Precio");
    }

    /**
     * Método para configurar los campos cuando el evento es gratuito.
     */
    private void camposEventoGratis() {
        lyPuntoVenta.setVisibility(View.INVISIBLE);
        lyDescripcionAdicional.setVisibility(View.VISIBLE);
        lytipoPrecio.setHint("Tipo");
    }

    /**
     * Método para mostrar el diálogo de selección de fecha.
     */
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
            textViewDate.setText(selectedDate);
            fecha = selectedDate;

            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year1, month1, dayOfMonth);
        }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Método para agregar un evento.
     *
     * @param view La vista desde la cual se invoca el método.
     */
    public void agregarEvento(View view) {
        String nombre = txtNombre.getText().toString();
        String descripcion = txtDescripcion.getText().toString();
        String ubicacion = txtLugar.getText().toString();
        int posicion = spCiudades.getSelectedItemPosition();
        Localidad localidad = localidades.get(posicion);
        if (nombre.isEmpty() || descripcion.isEmpty() || ubicacion.isEmpty()) {
            Toast.makeText(this, "Deben de estar todos los campos rellenos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (switch1.isChecked()) {
            String puntoVenta = txtPuntoVenta.getText().toString();
            if (txtPrecioTipo.getText().toString().contains("-")) {
                Toast.makeText(this, "No se puede poner un numero negativo", Toast.LENGTH_SHORT).show();
                return;
            } else if (txtPrecioTipo.getText().toString().contains(",")) {
                Toast.makeText(this, "Para escribir decimales se debe colocar un punto", Toast.LENGTH_SHORT).show();
                return;
            }
            float precio;
            try {
                precio = Float.parseFloat(txtPrecioTipo.getText().toString());
            } catch (Exception e) {
                Toast.makeText(this, "El precio debe ser un numero", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!puntoVenta.isEmpty() && fecha != null) {
                new insertarEventoPagoTask().execute(nombre, descripcion, ubicacion, localidad.getId() + "", precio + "", puntoVenta);
            } else {
                Toast.makeText(this, "Tienes que escribir el lugar donde se vende la entrada o fecha", Toast.LENGTH_SHORT).show();
            }
        } else {
            String tipo = txtPrecioTipo.getText().toString();
            String descripcionAdicional = txtDescripcionAdicional.getText().toString();
            if (tipo.isEmpty() || descripcionAdicional.isEmpty() || fecha == null) {
                Toast.makeText(this, "Debes rellenar el campo tipo y descripcion adicional o fecha", Toast.LENGTH_SHORT).show();
            } else {
                new insertarEventoGratisTask().execute(nombre, descripcion, ubicacion, localidad.getId() + "", tipo, descripcionAdicional);
            }
        }
    }

    /**
     * Método para volver atrás en la actividad.
     *
     * @param view La vista desde la cual se invoca el método.
     */
    public void atrasX(View view) {
        finish();
    }

    /**
     * Clase interna SacarLocalidades que extiende AsyncTask.
     * Obtiene las localidades desde el servidor.
     */
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
                String[] localidadesArray = result.split("/");
                for (String localidadStr : localidadesArray) {
                    String[] idNombre = localidadStr.split("-");
                    int id = Integer.parseInt(idNombre[0]);
                    String nombre = idNombre[1];
                    Localidad localidad = new Localidad(id, nombre);
                    localidades.add(localidad);
                }

                cargarDatosSpinner();
            } else {
                Toast.makeText(agregarEventos.this, "Error loading localidades", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Método para cargar los datos en el Spinner de ciudades.
     */
    private void cargarDatosSpinner() {
        ArrayList<String> nombreLoc = new ArrayList<>();
        for (Localidad l : localidades) {
            nombreLoc.add(l.getNombre());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                com.google.android.material.R.layout.support_simple_spinner_dropdown_item, nombreLoc);
        spCiudades.setAdapter(adapter);
        int i = 0;
        for (Localidad l : localidades) {
            if (l.getId() == 6) {
                spCiudades.setSelection(i);
            }
            i++;
        }
    }

    /**
     * Clase interna insertarEventoPagoTask que extiende AsyncTask.
     * Inserta un evento de pago en el servidor.
     */


    private class insertarEventoPagoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //http://localhost/extreventos/insertarEventoDePago.php?nombre=si&fecha=2022-07-25&descripcion=si&ubicacion=si&id_loc=2&precio=10&puntoDeVenta=si
            String parametros = "nombre=" + params[0] +
                    "&fecha=" + fecha +
                    "&descripcion=" + params[1] +
                    "&ubicacion=" + params[2] +
                    "&id_loc=" + params[3] +
                    "&precio=" + params[4] +
                    "&puntoDeVenta=" + params[5];

            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/insertarEventoDePago.php?"+parametros;
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
            if (result.equals("El evento de pago ha sido añadido exitosamente.")){
                Toast.makeText(agregarEventos.this, result, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(agregarEventos.this, ControladorGestionEventos.class);
                intent.putExtra("user", user);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else {
                Toast.makeText(agregarEventos.this, "Ha ocurrido un error inesperado insertando el evento de pago", Toast.LENGTH_SHORT).show();
                System.out.println(result);
            }

        }
    }
    private class insertarEventoGratisTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //http://localhost/extreventos/insertarEventoGratis.php?nombre=si&fecha=2022-07-25&descripcion=si&ubicacion=si&id_loc=2&tipo=10&descripcionAdicional=si
            String parametros = "nombre=" + params[0] +
                    "&fecha=" + fecha +
                    "&descripcion=" + params[1] +
                    "&ubicacion=" + params[2] +
                    "&id_loc=" + params[3] +
                    "&tipo=" + params[4] +
                    "&descripcionAdicional=" + params[5];

            String requestURL = "http://" + constantes.LOCALHOST + "/extreventos/insertarEventoGratis.php?"+parametros;
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
            if (result.equals("El evento gratuito ha sido añadido exitosamente.")){
                Toast.makeText(agregarEventos.this, result, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(agregarEventos.this, ControladorGestionEventos.class);
                intent.putExtra("user", user);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else {
                Toast.makeText(agregarEventos.this, "Ha ocurrido un error inesperado insertando el evento gratis", Toast.LENGTH_SHORT).show();
                System.out.println(result);
            }

        }
    }

}
