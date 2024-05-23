package com.example.prueba.modelo;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Localidad implements Serializable {
    private int id;
    private String nombre, provincia;
    private byte[] image;

    public Localidad(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Localidad(int id, String nombre, String provincia, byte[] image) {
        this.id = id;
        this.nombre = nombre;
        this.provincia = provincia;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Localidad{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", provincia='" + provincia + '\'' +
                ", image=" + image +
                '}';
    }
}
