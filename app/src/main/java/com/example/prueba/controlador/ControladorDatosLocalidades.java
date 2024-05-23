package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class ControladorDatosLocalidades extends AppCompatActivity {

    Localidad localidad;
    EditText txtNombre;
    EditText txtProvincia;
    ImageView imagen;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.datos_de_localidades);
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            localidad=(Localidad) extras.get("localidad");
        }
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.datosLocalidades), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void init(){
        txtNombre=findViewById(R.id.txtLocNombre);
        txtProvincia=findViewById(R.id.txtProvincia);
        imagen=findViewById(R.id.imageLoc);
        txtNombre.setText(localidad.getNombre());
        txtProvincia.setText(localidad.getProvincia());
        Bitmap bmimagen= BitmapFactory.decodeByteArray(localidad.getImage(), 0, localidad.getImage().length);
        imagen.setImageBitmap(bmimagen);
    }

    public void atras(View view) {
        Intent intent = new Intent(this, listaLocalidades.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void volverInicio(View view){
        Intent intent = new Intent(this, ControladorVistaUsuarios.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
