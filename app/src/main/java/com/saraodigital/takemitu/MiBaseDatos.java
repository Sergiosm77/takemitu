package com.saraodigital.takemitu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class MiBaseDatos {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    public MiBaseDatos() {}

    /* Inner class that defines the table contents */
    // public static class Estructura_BBDD implements BaseColumns {
    static final String TABLE_NAME = "destinoGuardados";
    static final String NOMBRE_COLUMNA1 = "Id";
    static final String NOMBRE_COLUMNA2 = "Destino";
    static final String NOMBRE_COLUMNA3 = "Latitud";
    static final String NOMBRE_COLUMNA4 = "Longitud";

//  -------------- Metodo de operaciones

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MiBaseDatos.TABLE_NAME + " (" +
                    MiBaseDatos.NOMBRE_COLUMNA1 + " INTEGER PRIMARY KEY," +
                    MiBaseDatos.NOMBRE_COLUMNA2 + " TEXT," +
                    MiBaseDatos.NOMBRE_COLUMNA3 + " TEXT," +
                    MiBaseDatos.NOMBRE_COLUMNA4 + " TEXT)";

    static final String SQL_DELETE_ENTRIES =  // Elimina la tabla si ya existiese una con el mismo nombre
            "DROP TABLE IF EXISTS " + MiBaseDatos.TABLE_NAME;

}

// ------  Clase de ayuda

class BBDD_Helper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1; // Informa de la version de esta base de datos, por si queremos informar
    private static final String DATABASE_NAME = "misDestinos.db";

    public BBDD_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MiBaseDatos.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(MiBaseDatos.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
