package com.saraodigital.takemitu;

import android.os.Parcel;
import android.os.Parcelable;

public class SitiosLog implements Parcelable {  // metemos toda la info en un Parcel para enviar la informacion

    public int id;
    public String nombre;
    public String detalle;
    public String latitud;
    public String longitud;
    //public byte[] imagen;


    public SitiosLog(){}

    private SitiosLog(Parcel parcel) {

        int manufacturerDataLength = parcel.readInt();
        byte[] manufacturerData = new byte[manufacturerDataLength];

        id=parcel.readInt();
        nombre=parcel.readString();
        detalle=parcel.readString();
        latitud=parcel.readString();
        longitud=parcel.readString();
       // imagen=parcel.readByteArray(byte);

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {  // metemos en el parcel toda la info (las variables que hemos declarado en la clase)

        parcel.writeInt(id);
        parcel.writeString(nombre); // usamos typedarray para poder escribir un array
        parcel.writeString(detalle);
        parcel.writeString(latitud);
        parcel.writeString(longitud);
        //parcel.writeByteArray(imagen);

    }

    public static final Creator<SitiosLog> CREATOR=new Creator<SitiosLog>(){

        @Override
        public SitiosLog createFromParcel(Parcel paquete) {
            return new SitiosLog(paquete);
        }

        @Override
        public SitiosLog[] newArray(int tamagno) {
            return new SitiosLog[tamagno];
        }
    };

}
