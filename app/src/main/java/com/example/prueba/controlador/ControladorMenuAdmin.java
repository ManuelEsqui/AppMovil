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

    String user; // Variable para almacenar el nombre de usuario

    // Método onCreate, se llama cuando la actividad se crea
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Habilitar la funcionalidad EdgeToEdge
        setContentView(R.layout.menu_admins); // Establecer el diseño de la actividad
        init(); // Inicializar la interfaz de usuario
        // Ajustar los márgenes para respetar las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menuAdmin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    TextView textoSaludo; // TextView para mostrar un saludo personalizado

    // Método para inicializar la interfaz de usuario
    private void init(){
        textoSaludo=findViewById(R.id.txtSaludo); // Enlazar el TextView del layout
        Bundle extras=getIntent().getExtras(); // Obtener los extras pasados a la actividad
        if (extras!=null){ // Verificar si existen extras
            user=extras.getString("user"); // Obtener el nombre de usuario
            textoSaludo.setText("Bienvenido "+user); // Mostrar un saludo personalizado
        }
    }

    // Método para iniciar la actividad de administración de usuarios
    public void adminUsuarios(View view) {
        Intent ventana = new Intent(this, ControladorListaUsuarios.class); // Crear intent para la actividad de usuarios
        ventana.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Establecer bandera para crear una nueva tarea
        ventana.putExtra("admin", true); // Pasar información adicional a la actividad
        ventana.putExtra("user", user);
        startActivity(ventana); // Iniciar la actividad de administración de usuarios
    }

    // Método para iniciar la actividad de creación de usuario
    public void adminCreateUser(View view) {
        Intent ventana = new Intent(this, ControladorRegistro.class); // Crear intent para la actividad de registro
        ventana.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Establecer bandera para crear una nueva tarea
        ventana.putExtra("admin", true); // Pasar información adicional a la actividad
        startActivity(ventana); // Iniciar la actividad de creación de usuario
    }

    // Método para iniciar la actividad de edición de cuenta
    public void editarCuenta(View view) {
        Intent intent = new Intent(this, ControladorEdicionDatos.class); // Crear intent para la actividad de edición de datos
        intent.putExtra("user", user); // Pasar el nombre de usuario a la actividad
        startActivity(intent); // Iniciar la actividad de edición de cuenta
    }

    // Método para iniciar la actividad de gestión de eventos
    public void adminEventos(View view) {
        Intent intent = new Intent(this, ControladorGestionEventos.class); // Crear intent para la actividad de gestión de eventos
        intent.putExtra("user", user); // Pasar el nombre de usuario a la actividad
        startActivity(intent); // Iniciar la actividad de gestión de eventos
    }

    // Método para cerrar sesión y volver al inicio de sesión
    public void cerrarSesion(View view) {
        Intent intent = new Intent(this, LogIn.class); // Crear intent para la actividad de inicio de sesión
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Establecer banderas para iniciar una nueva tarea y borrar la pila de actividades
        startActivity(intent); // Iniciar la actividad de inicio de sesión
    }
}
