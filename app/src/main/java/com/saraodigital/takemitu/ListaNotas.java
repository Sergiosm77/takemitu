package com.saraodigital.takemitu;

import java.io.Serializable;

public class ListaNotas implements Serializable {

    private String nota;
    private String detalle;
    private String valor;

    public ListaNotas(String nota, String detalle, String valor) {
        this.nota = nota;
        this.detalle = detalle;
        this.valor=valor;
    }

    public String getNota() {
        return this.nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getDetalle() {
        return this.detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getValor() {
        return this.valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }




}
