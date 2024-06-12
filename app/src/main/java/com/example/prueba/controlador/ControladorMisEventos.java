//Manuel Esquivel sevillano 2ºDAM
package com.example.prueba.controlador;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.prueba.R;
import com.google.android.material.tabs.TabLayout;

public class ControladorMisEventos extends AppCompatActivity {

    String user; // Variable para almacenar el nombre de usuario

    // Método onCreate, se llama cuando la actividad se crea
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Habilitar la funcionalidad EdgeToEdge para la pantalla completa
        setContentView(R.layout.mis_eventos); // Establecer el diseño de la actividad
        Bundle extras=getIntent().getExtras(); // Obtener los extras pasados a la actividad
        if (extras!=null){ // Verificar si existen extras
            user=extras.getString("user"); // Obtener el nombre de usuario
        }
        ViewPager viewPager = findViewById(R.id.viewPager); // Referencia al ViewPager en el layout
        TabLayout tabLayout = findViewById(R.id.tabLayout); // Referencia al TabLayout en el layout
        // Crear y establecer un adaptador para el ViewPager
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), user);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager); // Configurar el TabLayout para trabajar con el ViewPager
        // Ajustar los márgenes para respetar las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.misEventos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para volver al menú de usuarios al presionar el botón de retroceso
    public void volverMenuUsuarios(View view) {
        finish(); // Finalizar la actividad actual y volver a la actividad anterior
    }
}
