package com.example.prueba.modelo;

import androidx.annotation.NonNull;

public class Usuario {
    private String nombre,apellidos,sexo,estadoCivil,user,passwrd;
    private int edad;
    private boolean admin;
    private String localidad;

    public Usuario(String nombre, String apellidos, String sexo, String estadoCivil, String user, String passwrd, int edad, boolean admin) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.sexo = sexo;
        this.estadoCivil = estadoCivil;
        this.user = user;
        this.passwrd = passwrd;
        this.edad = edad;
        this.admin = admin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswrd() {
        return passwrd;
    }

    public void setPasswrd(String passwrd) {
        this.passwrd = passwrd;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    @NonNull
    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", sexo='" + sexo + '\'' +
                ", estadoCivil='" + estadoCivil + '\'' +
                ", user='" + user + '\'' +
                ", passwrd='" + passwrd + '\'' +
                ", edad=" + edad +
                ", admin=" + admin +
                ", localidad='" + localidad + '\'' +
                '}';
    }
}
