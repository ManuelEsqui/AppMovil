package com.example.prueba;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void registrarUsuario(View view) {
        Intent ventana = new Intent(this, ControladorRegistro.class);
        ventana.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(ventana);
    }

    public void editarCuenta(View view) {
        EditText txtUsuaio=findViewById(R.id.txtUsuario);
        EditText txtContra=findViewById(R.id.txtContraseña);
        String usuario=txtUsuaio.getText().toString();
        String contra=txtContra.getText().toString();
        if (usuario.isEmpty() || contra.isEmpty()){
            //mensaje campos no rellenos
            return;
        }else{
            //comprobar que esten el la bd
        }
    }

    public void logIn(View view) {
        EditText txtUsuaio=findViewById(R.id.txtUsuario);
        EditText txtContra=findViewById(R.id.txtContraseña);
        String usuario=txtUsuaio.getText().toString();
        String contra=txtContra.getText().toString();
        if (usuario.isEmpty() || contra.isEmpty()){
            //mensaje campos no rellenos
            return;
        }else{
            //comprobar que esten el la bd
        }
    }
}