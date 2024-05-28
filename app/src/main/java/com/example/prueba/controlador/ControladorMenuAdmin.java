package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prueba.R;

public class ControladorMenuAdmin extends AppCompatActivity {

    String user;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.menu_admins);
        init();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menuAdmin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    TextView textoSaludo;
    private void init(){
        textoSaludo=findViewById(R.id.txtSaludo);
        Bundle extras=getIntent().getExtras();
        if (extras!=null){
            user=extras.getString("user");
            textoSaludo.setText("Bienvenido "+user);
        }
    }
    public void adminUsuarios(View view) {
        Toast.makeText(this, "pa la ventana de administrar usuarios", Toast.LENGTH_SHORT).show();
    }

    public void adminCreateUser(View view) {
        Intent ventana = new Intent(this, ControladorRegistro.class);
        ventana.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ventana.putExtra("admin", true);
        startActivity(ventana);
    }

    public void editarCuenta(View view) {
        Intent intent = new Intent(this, ControladorEdicionDatos.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void adminEventos(View view) {
        Intent intent = new Intent(this, ControladorGestionEventos.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void cerrarSesion(View view) {
        Intent intent = new Intent(this, LogIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
