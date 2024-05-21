package com.example.prueba;

import org.junit.Test;

import static org.junit.Assert.*;

import android.widget.Toast;

import com.example.prueba.controlador.ControladorVistaUsuarios;
import com.example.prueba.modelo.Evento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testEventos(){
        Evento ev = null;
        String result="1 - Feria de Artesanía - Exposición de artesanías locales - 2024-06-22 - Parque - Don Benito/";
        Date fecha2=new Date();
        Date fecha = new Date();
        Evento eventoEsperado=new Evento(1, "Feria de Artesanía", "Exposición de artesanías locales", "Don Benito", "Parque",fecha2);
        String[] EventosArray = result.split("/");
        for (String EventoStr : EventosArray) {
            String[] datos = EventoStr.split(" - ");
            //1 - Feria de Artesanía - Exposición de artesanías locales - 2024-06-22 - Parque - Don Benito
            int id = Integer.parseInt(datos[0]);
            String nombre = datos[1];
            String descripcion=datos[2];
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String ubicacion=datos[4];
            String localidad=datos[5];
            //int id,String nombre, String descripcion, String localidad, String ubicacion, Date fecha
            ev=new Evento(id, nombre, descripcion, localidad, ubicacion, fecha);
        }
        assertEquals(eventoEsperado, ev);
    }
}