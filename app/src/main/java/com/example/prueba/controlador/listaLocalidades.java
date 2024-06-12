//Manuel Esquivel sevillano 2ºDAM
package com.example.prueba.controlador;


import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

public class listaLocalidades extends AppCompatActivity {
    ArrayList<Localidad> Arraylocalidades=new ArrayList<>();
    ArrayList<String> ArrayNombreLoc;
    ListView listViewLocalidades;
    String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lista_localidades);
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            user=extras.getString("user");
        }
        new ObtenerLocalidadesAsyncTask().execute();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listaLocalidades), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initDatos() {
        listViewLocalidades=findViewById(R.id.listViewLocalidades);
        ArrayNombreLoc=new ArrayList<>();
        for (Localidad l: Arraylocalidades){
            ArrayNombreLoc.add(l.getNombre());
        }
        // Adaptador para llenar el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ArrayNombreLoc);
        listViewLocalidades.setAdapter(adapter);
    }
    public void volverMenuUsuarios(View view) {
        finish();
    }

    private class ObtenerLocalidadesAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String requestURL = "http://"+ constantes.LOCALHOST+"/extreventos/listarLocalidades.php";
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
        protected void onPostExecute(String content) {
            try {
                JSONArray localidades = new JSONArray(content);
                for (int i = 0; i < localidades.length(); i++) {
                    JSONObject localidad = localidades.getJSONObject(i);
                    int id=localidad.getInt("id");
                    String nombre = localidad.getString("nombre");
                    String provincia = localidad.getString("provincia");
                    String imagenBase64 = localidad.getString("imagen");
                    byte[] imageBytes = Base64.getDecoder().decode(imagenBase64);
                    //Bitmap imagen=BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    Localidad l =new Localidad(id, nombre, provincia, imageBytes);
                    Arraylocalidades.add(l);
                }
                initDatos();
                listViewLocalidades.setOnItemClickListener((parent, view, position, id) -> {
                    Localidad loc =Arraylocalidades.get(position);
                    Intent intent=new Intent(listaLocalidades.this, ControladorDatosLocalidades.class);
                    intent.putExtra("localidad", loc);
                    intent.putExtra("user", user);
                    startActivity(intent);
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
