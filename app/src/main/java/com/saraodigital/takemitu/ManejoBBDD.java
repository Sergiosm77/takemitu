package com.saraodigital.takemitu;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ManejoBBDD extends SQLiteOpenHelper {

    private String rutaAlmacenamiento;
    private SQLiteDatabase bbdd;
    private int nuevaVersion=2;

    ManejoBBDD(Context contexto){

        super(contexto,null,null,1);

        /* ------------- ELIMINADO PARA BORRAR BASE DB3 -----------------------
        super(contexto,"paradas.db3",null,1);

        String packageName = contexto.getPackageName();
        String DB_PATH = "/data/data/" + packageName + "/databases/";
//Create the directory if it does not exist
        File directory = new File(DB_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String DB_NAME = "paradas.db3"; //The name of the source sqlite file
        rutaAlmacenamiento = DB_PATH + DB_NAME;
        */

    }

    void aperturaBBDD(Context contexto) {


        File file = new File(rutaAlmacenamiento);

        if(file.exists()){

            bbdd=SQLiteDatabase.openDatabase(rutaAlmacenamiento,null,SQLiteDatabase.OPEN_READONLY);

            if(bbdd.getVersion()<nuevaVersion){

                //System.out.println("ENCONTRADA VERSION MAS ACTUAL");

                copiaBBDD(contexto);
                bbdd=SQLiteDatabase.openDatabase(rutaAlmacenamiento,null,SQLiteDatabase.OPEN_READONLY);
                //System.out.println("ACTUALIZADO A VERSION: "+bbdd.getVersion());

            }

            //System.out.println("------------- EXISTE - Version: "+bbdd.getVersion());

        }else{

            //System.out.println("------------- NO EXISTE");
            copiaBBDD(contexto);
            bbdd=SQLiteDatabase.openDatabase(rutaAlmacenamiento,null,SQLiteDatabase.OPEN_READONLY);

        }
/*
        try{

            // Esta linea funciona cuando no sea la primera vez que se ejecuta, la primera vez da error y va al CATCH
            //System.out.println("//////////////////Va a comprobar");
            bbdd=SQLiteDatabase.openDatabase(rutaAlmacenamiento,null,SQLiteDatabase.OPEN_READONLY);
            System.out.println("//////////////////Ha abierto la base");


        }catch(SQLiteException e){

            copiaBBDD(contexto);
            bbdd=SQLiteDatabase.openDatabase(rutaAlmacenamiento,null,SQLiteDatabase.OPEN_READONLY);
            System.out.println("//////////////////Ha copiado y abierto la base");

        }
*/
    }

    private void copiaBBDD(Context contexto){ // esto copia el archivo db3 en otro sitio dentro de la APP

            try{

            //InputStream datosEntrada = contexto.getAssets().open("paradas.db3");
                InputStream datosEntrada = contexto.getAssets().open(null);

            OutputStream datosSalida = new FileOutputStream(rutaAlmacenamiento);

//transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = datosEntrada.read(buffer)) > 0) {
                datosSalida.write(buffer, 0, length);
            }

//Close the streams

                datosSalida.flush();
                datosSalida.close();
                datosEntrada.close();
                //System.out.println("///////Base de datos copiada");

        }catch(Exception e){
                //System.out.println("///////Problema al copiar base datos");
            }

    }

    public Location datosEstacion(int id){

        Location estacion;

        Cursor miCursor2;

        miCursor2=bbdd.rawQuery("SELECT * FROM Paradas WHERE Id LIKE "+id,null);

        miCursor2.moveToFirst();

        // guardamos en estacion, de tipo Location, las 3 columnas

        estacion=new Location(miCursor2.getString(1));
        estacion.setLatitude(Double.parseDouble(miCursor2.getString(2))); // cogemos el String de la columna 2 y lo pasamos a Double, para almacenarlo como Latitude
        estacion.setLongitude(Double.parseDouble(miCursor2.getString(3)));

        miCursor2.close();

        return estacion;
    }


    public Lineas[] dameinfoLineas(String[] nombresLineas){

        // Con este método guardaremos la posición gps de cada estación de cada línea de metro dentro de un array
        /////////////////////////////////////

        Lineas[] lasLineas=new Lineas[nombresLineas.length];  // crea un array con la longitud del array pasado por parámetros

        Cursor miCursor=null; // Creamos un cursos para leer recordset

        Cursor miCursor2=null;

        //Log.i("Nota: ","Numero de estaciones de "+nombresLineas.length);

        for(int i=0;i<nombresLineas.length;i++){

            lasLineas[i]=new Lineas();  // crea un objeto tipo lineas a cada vuelta de bucle

            lasLineas[i].nombre=nombresLineas[i]; // guardamos en el array de tipo Lineas el contenido del array pasado por parámetros

            // ------------ HORARIOS -------------------------

            miCursor2=bbdd.rawQuery("SELECT * FROM Laboral WHERE Linea LIKE "+"'"+lasLineas[i].nombre+"'",null);

            miCursor2.moveToFirst();

            lasLineas[i].horarioLaboral_inicio=miCursor2.getString(1);
            lasLineas[i].horarioLaboral_fin=miCursor2.getString(2);
            lasLineas[i].horarioLaboral_intervalo=miCursor2.getString(3);

            miCursor2=bbdd.rawQuery("SELECT * FROM Viernes WHERE Linea LIKE "+"'"+lasLineas[i].nombre+"'",null);

            miCursor2.moveToFirst();

            lasLineas[i].horarioViernes_inicio=miCursor2.getString(1);
            lasLineas[i].horarioViernes_fin=miCursor2.getString(2);
            lasLineas[i].horarioViernes_intervalo=miCursor2.getString(3);

            miCursor2=bbdd.rawQuery("SELECT * FROM Sabados WHERE Linea LIKE "+"'"+lasLineas[i].nombre+"'",null);

            miCursor2.moveToFirst();

            lasLineas[i].horarioSabado_inicio=miCursor2.getString(1);
            lasLineas[i].horarioSabado_fin=miCursor2.getString(2);
            lasLineas[i].horarioSabado_intervalo=miCursor2.getString(3);

            miCursor2=bbdd.rawQuery("SELECT * FROM Festivo WHERE Linea LIKE "+"'"+lasLineas[i].nombre+"'",null);

            miCursor2.moveToFirst();

            lasLineas[i].horarioFestivo_inicio=miCursor2.getString(1);
            lasLineas[i].horarioFestivo_fin=miCursor2.getString(2);
            lasLineas[i].horarioFestivo_intervalo=miCursor2.getString(3);

            // --------------- FIN HORARIOS -------------------------

            miCursor=bbdd.rawQuery("SELECT Id FROM "+ nombresLineas[i],null); // Realizamos una consulta SQL para obtener los IDs de las estaciones

            lasLineas[i].estaciones=new Location[miCursor.getCount()]; // Array location con la cantidad de estaciones de la linea

           //Log.i("Nota: ","Numero de estaciones ("+i+") de "+lasLineas[i].nombre+" es: "+lasLineas[i].estaciones.length);

            int contador=0;

            miCursor.moveToFirst(); // se mueve a la primera posición

            while(!miCursor.isAfterLast()){  // mientras el cursor no esté al final del último registro

                int estacion=Integer.parseInt((miCursor.getString(0))); // dame el texto de la columna 0 (la primera)

                lasLineas[i].estaciones[contador]=datosEstacion(estacion); // pasamos los datos tipo Location de la estación en cuestión al array

                contador++;

                miCursor.moveToNext(); // el cursor se mueve a las siguiente posicion del recordset

            }

        }


        if(miCursor!=null && !miCursor.isClosed()){  // nos aseguramos que el cursor no es nulo

            miCursor.close();
        }
        if(miCursor2!=null && !miCursor2.isClosed()) {  // nos aseguramos que el cursor no es nulo

            miCursor2.close();
        }

        return lasLineas;  // DEVUELVE EL ARRAY CON LA INFO DE TODAS LAS LINEAS

    }

    public void cerrarBBDD(){  // CERRAMOS LA BASE DE DATOS

        bbdd.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
