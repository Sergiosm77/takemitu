package com.saraodigital.takemitu;

import java.io.Serializable;

public class ListaMenus implements Serializable {

    private String nombre;
    private String base;
    private String imagen;

    public ListaMenus(String nombre, String base, String imagen) {
        this.nombre = nombre;
        this.base = base;
        this.imagen=imagen;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getBase() {
        return this.base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getImagen() {
        return this.imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }




}
