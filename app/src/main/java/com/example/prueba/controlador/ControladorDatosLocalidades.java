package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prueba.R;
import com.example.prueba.modelo.Localidad;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ControladorDatosLocalidades extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private final String APIKEY = "AIzaSyBczeFo_0Q3Cbo3xnIJ_zdNhYJgDGimv20";
    Localidad localidad;
    EditText txtNombre;
    EditText txtProvincia;
    ImageView imagen;
    String user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.datos_de_localidades);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("user");
            localidad = (Localidad) extras.get("localidad");
        }
        mapView = findViewById(R.id.mapViewLocalidades);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.datosLocalidades), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void init() {
        txtNombre = findViewById(R.id.txtLocNombre);
        txtProvincia = findViewById(R.id.txtProvincia);
        imagen = findViewById(R.id.imageLoc);
        txtNombre.setText(localidad.getNombre());
        txtProvincia.setText(localidad.getProvincia());
        Bitmap bmimagen = BitmapFactory.decodeByteArray(localidad.getImage(), 0, localidad.getImage().length);
        imagen.setImageBitmap(bmimagen);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        new GeocodeTask().execute(localidad.getNombre(), localidad.getProvincia());
    }

    public void atras(View view) {
        finish();
    }

    public void volverInicio(View view) {
        Intent intent = new Intent(this, ControladorVistaUsuarios.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private class GeocodeTask extends AsyncTask<String, Void, LatLng> {

        @Override
        protected LatLng doInBackground(String... params) {
            String city = params[0];
            String province = params[1];
            String query = city + "," + province;
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + query + "&key=" + APIKEY;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject json = new JSONObject(response.toString());
                JSONArray results = json.getJSONArray("results");
                if (results.length() > 0) {
                    JSONObject location = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    return new LatLng(lat, lng);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            if (latLng != null && googleMap != null) {
                googleMap.addMarker(new MarkerOptions().position(latLng).title(localidad.getNombre() + ", " + localidad.getProvincia()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}

