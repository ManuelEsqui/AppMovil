package com.example.prueba.modelo;

import java.io.Serializable;
import java.util.Date;

public class Evento_Pago extends Evento implements Serializable {
    private float precio;
    private String puntoDeVenta;

    public Evento_Pago(Evento e, float precio, String puntoDeVenta) {
        super(e.getId(), e.getNombre(), e.getDescripcion(), e.getLocalidad(), e.getUbicacion(), e.getFecha());
        this.precio = precio;
        this.puntoDeVenta = puntoDeVenta;
    }

    public Evento_Pago(int id, String nombre, String descripcion, String localidad, String ubicacion, Date fecha, float precio, String puntoDeVenta) {
        super(id, nombre, descripcion, localidad, ubicacion, fecha);
        this.precio = precio;
        this.puntoDeVenta = puntoDeVenta;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getPuntoDeVenta() {
        return puntoDeVenta;
    }

    public void setPuntoDeVenta(String puntoDeVenta) {
        this.puntoDeVenta = puntoDeVenta;
    }

    @Override
    public String toString() {
        return "Evento_Pago{" +
                "precio=" + precio +
                ", puntoDeVenta='" + puntoDeVenta + '\'' +
                "} " + super.toString();
    }
}
