package com.example.prueba.modelo;

import java.io.Serializable;
import java.util.Date;

public class Evento_Gratis extends Evento implements Serializable {
    private String descripcionAdicional;
    private String tipo;

    public Evento_Gratis(Evento e, String descripcionAdicional, String tipo) {
        super(e.getId(), e.getNombre(), e.getDescripcion(), e.getLocalidad(), e.getUbicacion(), e.getFecha());
        this.descripcionAdicional = descripcionAdicional;
        this.tipo = tipo;
    }

    public Evento_Gratis(int id, String nombre, String descripcion, String localidad, String ubicacion, Date fecha, String descripcionAdicional, String tipo) {
        super(id, nombre, descripcion, localidad, ubicacion, fecha);
        this.descripcionAdicional = descripcionAdicional;
        this.tipo = tipo;
    }
    public String getDescripcionAdicional() {
        return descripcionAdicional;
    }
    public void setDescripcionAdicional(String descripcionAdicional) {
        this.descripcionAdicional = descripcionAdicional;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}
