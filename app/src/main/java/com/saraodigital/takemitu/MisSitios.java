package com.saraodigital.takemitu;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Blob;

public class MisSitios implements Parcelable {  // metemos toda la info en un Parcel para enviar la informacion


    public String detalle;
    public String direccion;
    public String telefono;
    public String latitud;
    public String longitud;
    public String image;
    public String nombre;
    public String activo;

    public MisSitios(){}

    private MisSitios(Parcel parcel) {

        detalle=parcel.readString();
        direccion=parcel.readString();
        telefono=parcel.readString();
        latitud=parcel.readString();
        longitud=parcel.readString();

        image=parcel.readString();
        nombre=parcel.readString();
        activo=parcel.readString();

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {  // metemos en el parcel toda la info (las variables que hemos declarado en la clase)

        parcel.writeString(detalle);
        parcel.writeString(direccion);
        parcel.writeString(telefono);
        parcel.writeString(latitud);
        parcel.writeString(longitud);
        parcel.writeString(image);
        parcel.writeString(nombre);
        parcel.writeString(activo);
       
    }

    public static final Creator<MisSitios> CREATOR=new Creator<MisSitios>(){

        @Override
        public MisSitios createFromParcel(Parcel paquete) {
            return new MisSitios(paquete);
        }

        @Override
        public MisSitios[] newArray(int tamagno) {
            return new MisSitios[tamagno];
        }
    };


}
