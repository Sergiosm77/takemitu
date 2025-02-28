package com.saraodigital.takemitu;

import android.os.Bundle;

public class SentidoParadas {

    public String nombreLinea, sentido;

    public Lineas[] lineas;

    public String dimeNombres(String nombre){

        String resultado;

        resultado = nombre.substring(5);
        if(nombre.contains("Vuelta")) {
            resultado = resultado.substring(0,(resultado.length() - 6));
        }

        return resultado;


    }



/*
    public String[] dimeNombres(String nombre) {

        String[] resultado = new String[2];

        if (nombre.equals("Linea1")) {
            nombreLinea = "1";
            sentido = "LARDERO";
        }
        if (nombre.equals("Linea1Vuelta")) {
            nombreLinea = "1";
            sentido = "HOSPITAL SAN PEDRO";
        }
        if (nombre.equals("Linea2")) {
            nombreLinea = "2";
            sentido = "VAREA";
        }
        if (nombre.equals("Linea2Vuelta")) {
            nombreLinea = "2";
            sentido = "YAGÜE";
        }
        if (nombre.equals("Linea3")) {
            nombreLinea = "3";
            sentido = "LAS NORIAS";
        }
        if (nombre.equals("Linea3Vuelta")) {
            nombreLinea = "3";
            sentido = "VILLAMEDIANA";
        }
        if (nombre.equals("Linea4")) {
            nombreLinea = "4";
            sentido = "PALACIO DE CONGRESOS";
        }
        if (nombre.equals("Linea4Vuelta")) {
            nombreLinea = "4";
            sentido = "PRADOVIEJO";
        }
        if (nombre.equals("Linea5")) {
            nombreLinea = "5";
            sentido = "VALDEGASTEA";
        }
        if (nombre.equals("Linea5Vuelta")) {
            nombreLinea = "5";
            sentido = "MADRE DE DIOS";
        }
        if (nombre.equals("Linea6")) {
            nombreLinea = "6";
            sentido = "EL CORTIJO";
        }
        if (nombre.equals("Linea6Vuelta")) {
            nombreLinea = "6";
            sentido = "CENTRO";
        }
        if (nombre.equals("Linea7")) {
            nombreLinea = "7";
            sentido = "POLíGONO CANTABRIA";
        }
        if (nombre.equals("Linea7Vuelta")) {
            nombreLinea = "7";
            sentido = "EL ARCO";
        }
        if (nombre.equals("Linea9")) {
            nombreLinea = "9";
            sentido = "LAS NORIAS";
        }
        if (nombre.equals("Linea9Vuelta")) {
            nombreLinea = "9";
            sentido = "PRADOVIEJO";
        }
        if (nombre.equals("Linea10")) {
            nombreLinea = "10";
            sentido = "VAREA";
        }
        if (nombre.equals("Linea10Vuelta")) {
            nombreLinea = "10";
            sentido = "EL ARCO";
        }
        if (nombre.equals("Linea11")) {
            nombreLinea = "11";
            sentido = "CENTRO";
        }
        if (nombre.equals("Linea11Vuelta")) {
            nombreLinea = "11";
            sentido = "HOSPITAL SAN PEDRO";
        }
        if (nombre.equals("LineaB1")) {
            nombreLinea = "B1";
            sentido = "LA CAVA";
        }
        if (nombre.equals("LineaB1Vuelta")) {
            nombreLinea = "B1";
            sentido = "VAREA";
        }
        if (nombre.equals("LineaB2")) {
            nombreLinea = "B2";
            sentido = "LA ESTRELLA";
        }
        if (nombre.equals("LineaB2Vuelta")) {
            nombreLinea = "B2";
            sentido = "EL ARCO";
        }
        if (nombre.equals("LineaB3")) {
            nombreLinea = "B3";
            sentido = "EL CAMPILLO";
        }
        if (nombre.equals("LineaB3Vuelta")) {
            nombreLinea = "B3";
            sentido = "LARDERO";
        }

        resultado[0] = nombreLinea;
        resultado[1] = sentido;

        return resultado;


    }

 */
}
