package com.example.prueba.modelo;

import android.media.Image;

public class Localidad {
    private int id;
    private String nombre, provincia;
    private Image image;

    public Localidad(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Localidad(int id, String nombre, String provincia, Image image) {
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
    public Image getImage() {
        return image;
    }
    public void setImage(Image image) {
        this.image = image;
    }
}
