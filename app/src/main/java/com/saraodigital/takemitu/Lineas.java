package com.saraodigital.takemitu;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Lineas implements Parcelable {  // metemos toda la info en un Parcel para enviar la informacion


    public String nombre;
    public Location[] estaciones;
    public int origenRuta,finalRuta,origenRuta_fin,finalRuta_fin,min_hastaBus,proximoBus,finalBus;
    public double datosParadaOrigen, datosParadaDestino,datosParadaOrigen_fin, datosParadaDestino_fin;

    public String horarioLaboral_inicio,horarioLaboral_fin,horarioLaboral_intervalo;
    public String horarioSabado_inicio,horarioSabado_fin,horarioSabado_intervalo;
    public String horarioFestivo_inicio,horarioFestivo_fin,horarioFestivo_intervalo;
    public String horarioViernes_inicio,horarioViernes_fin,horarioViernes_intervalo;
    public String detalleNota;
    public String sentido, color;
    public int valorNota;
    public String especialLab,especialVie,especialSab,especialFes;
    public int activa;

    public Lineas(){}

    private Lineas(Parcel parcel) {

        nombre=parcel.readString();
        sentido=parcel.readString();
        color=parcel.readString();

        estaciones=parcel.createTypedArray(Location.CREATOR);
        origenRuta=parcel.readInt();
        finalRuta=parcel.readInt();
        min_hastaBus=parcel.readInt();
        proximoBus=parcel.readInt();
        finalBus=parcel.readInt();

        datosParadaOrigen=parcel.readDouble();
        datosParadaDestino=parcel.readDouble();

        origenRuta_fin=parcel.readInt();
        finalRuta_fin=parcel.readInt();
        datosParadaOrigen_fin= parcel.readDouble();
        datosParadaDestino_fin=parcel.readDouble();

        horarioLaboral_inicio=parcel.readString();
        horarioLaboral_fin=parcel.readString();
        horarioLaboral_intervalo=parcel.readString();
        horarioViernes_inicio=parcel.readString();
        horarioViernes_fin=parcel.readString();
        horarioViernes_intervalo=parcel.readString();
        horarioSabado_inicio=parcel.readString();
        horarioSabado_fin=parcel.readString();
        horarioSabado_intervalo=parcel.readString();
        horarioFestivo_inicio=parcel.readString();
        horarioFestivo_fin=parcel.readString();
        horarioFestivo_intervalo=parcel.readString();

        detalleNota=parcel.readString();
        valorNota=parcel.readInt();

        especialLab=parcel.readString();
        especialVie=parcel.readString();
        especialSab=parcel.readString();
        especialFes=parcel.readString();

        activa=parcel.readInt();

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {  // metemos en el parcel toda la info (las variables que hemos declarado en la clase)

        parcel.writeString(nombre);
        parcel.writeString(sentido);
        parcel.writeString(color);

        parcel.writeTypedArray(estaciones,0); // usamos typedarray para poder escribir un array
        parcel.writeInt(origenRuta);
        parcel.writeInt(finalRuta);
        parcel.writeDouble(datosParadaOrigen);
        parcel.writeDouble(datosParadaDestino);

        parcel.writeInt(origenRuta_fin);
        parcel.writeInt(finalRuta_fin);
        parcel.writeInt(min_hastaBus);
        parcel.writeInt(proximoBus);
        parcel.writeInt(finalBus);

        parcel.writeDouble(datosParadaOrigen_fin);
        parcel.writeDouble(datosParadaDestino_fin);

        parcel.writeString(horarioLaboral_inicio);
        parcel.writeString(horarioLaboral_fin);
        parcel.writeString(horarioLaboral_intervalo);
        parcel.writeString(horarioViernes_inicio);
        parcel.writeString(horarioViernes_fin);
        parcel.writeString(horarioViernes_intervalo);
        parcel.writeString(horarioSabado_inicio);
        parcel.writeString(horarioSabado_fin);
        parcel.writeString(horarioSabado_intervalo);
        parcel.writeString(horarioFestivo_inicio);
        parcel.writeString(horarioFestivo_fin);
        parcel.writeString(horarioFestivo_intervalo);

        parcel.writeString(detalleNota);
        parcel.writeInt(valorNota);

        parcel.writeString(especialLab);
        parcel.writeString(especialVie);
        parcel.writeString(especialSab);
        parcel.writeString(especialFes);

        parcel.writeInt(activa);

    }

    public static final Parcelable.Creator<Lineas> CREATOR=new Parcelable.Creator<Lineas>(){

        @Override
        public Lineas createFromParcel(Parcel paquete) {
            return new Lineas(paquete);
        }

        @Override
        public Lineas[] newArray(int tamagno) {
            return new Lineas[tamagno];
        }
    };


    public void distancias(Location origen,Location destino){

        datosParadaOrigen=origen.distanceTo(estaciones[0]); // distanceTo da (en metros) la distancia entre dos Location (origen y estaciones[0] que es la primera estacion de la linea actual)
        datosParadaDestino=destino.distanceTo(estaciones[0]);
        origenRuta=0;
        finalRuta=0;

        for(int i=1;i<estaciones.length;i++){  // recorremos todas las estaciones comparando la distancia entre el origen y destino y dichas estaciones, vamos guardando la distancia menor

            if(origen.distanceTo(estaciones[i])<datosParadaOrigen){

                origenRuta=i; // guardamos el numero de esa estación

                datosParadaOrigen=origen.distanceTo(estaciones[i]);
            }
            if(destino.distanceTo(estaciones[i])<datosParadaDestino){

                finalRuta=i; // guardamos el numero de esa estación

                datosParadaDestino=destino.distanceTo(estaciones[i]);
            }
        }
        //if(finalRuta-origenRuta<0){return 2000000.0;}

        //return datosParadaOrigen+datosParadaDestino;
    }


    // ------------- PARADA MAS CERCANA A ORIGEN -------------------------
    public double distancia_origen(Location origen){

        datosParadaOrigen=origen.distanceTo(estaciones[0]);

        origenRuta=0;

        for(int i=1;i<estaciones.length;i++){

            if(origen.distanceTo(estaciones[i])<datosParadaOrigen){

                origenRuta=i;

                datosParadaOrigen=origen.distanceTo(estaciones[i]);
            }

        }

        return datosParadaOrigen;
    }

    // ------------- PARADA MAS CERCANA A DESTINO -------------------------
    public double distancia_destino(Location destino){

        datosParadaDestino=destino.distanceTo(estaciones[0]);

        finalRuta=0;

        for(int i=1;i<estaciones.length;i++){

            if(destino.distanceTo(estaciones[i])<datosParadaDestino){


                finalRuta=i;
                datosParadaDestino=destino.distanceTo(estaciones[i]);
            }

        }
        //System.out.println("DISTANCIA DESTINO: "+nombre+" "+datosParadaDestino);

        return datosParadaDestino;
    }

    public double distancia_sinlinea(Location origen,Location destino){


        datosParadaOrigen=origen.distanceTo(estaciones[0]);
        datosParadaDestino=destino.distanceTo(estaciones[0])/1000;
        origenRuta=0;
        finalRuta=0;

        for(int i=1;i<estaciones.length;i++){

            if(origen.distanceTo(estaciones[i])<datosParadaOrigen){

                origenRuta=i;

                datosParadaOrigen=origen.distanceTo(estaciones[i]);
            }
            if(destino.distanceTo(estaciones[i])/1000<datosParadaDestino){

                finalRuta=i;

                datosParadaDestino=destino.distanceTo(estaciones[i])/1000;
            }

        }

        if(finalRuta-origenRuta<0){return 20000000.0;}

        return datosParadaOrigen+(datosParadaDestino);
    }

}
