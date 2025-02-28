package com.saraodigital.takemitu;

import android.os.Parcel;
import android.os.Parcelable;

public class MisEventos implements Parcelable {  // metemos toda la info en un Parcel para enviar la informacion


    public String nombre;
    public String lugarEvento;
    public String fechaEvento;
    public String horaEvento;
    public String latitud;
    public String longitud;
    public String descripcion;
    public String imagen;
    public int activo;

    public MisEventos() {
    }

    private MisEventos(Parcel parcel) {

        nombre = parcel.readString();
        lugarEvento = parcel.readString();
        fechaEvento = parcel.readString();

        horaEvento = parcel.readString();
        latitud = parcel.readString();
        longitud = parcel.readString();

        descripcion = parcel.readString();
        imagen = parcel.readString();
        activo = parcel.readInt();

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {  // metemos en el parcel toda la info (las variables que hemos declarado en la clase)

        parcel.writeString(nombre);
        parcel.writeString(lugarEvento);
        parcel.writeString(fechaEvento);

        parcel.writeString(horaEvento);
        parcel.writeString(latitud);
        parcel.writeString(longitud);

        parcel.writeString(descripcion);
        parcel.writeString(imagen);
        parcel.writeInt(activo);

    }

    public static final Creator<MisEventos> CREATOR = new Creator<MisEventos>() {

        @Override
        public MisEventos createFromParcel(Parcel paquete) {
            return new MisEventos(paquete);
        }

        @Override
        public MisEventos[] newArray(int tamagno) {
            return new MisEventos[tamagno];
        }
    };

}
