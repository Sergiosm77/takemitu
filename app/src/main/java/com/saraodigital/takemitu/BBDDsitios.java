package com.saraodigital.takemitu;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class BBDDsitios extends SQLiteOpenHelper {

    private String rutaAlmacenamiento;
    private SQLiteDatabase bbdd;
    private int nuevaVersion=1;

    public BBDDsitios(Context contexto){

        super(contexto,null,null,1);

        /* ---- ELIMINADO PARA BORRAR LAS BASES DE DATOS DB3 ------------------------------

        super(contexto,"sitiosLog.db3",null,1);


        String packageName = contexto.getPackageName();
        String DB_PATH = "/data/data/" + packageName + "/databases/";
//Create the directory if it does not exist
        File directory = new File(DB_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String DB_NAME = "sitiosLog.db3"; //The name of the source sqlite file
        rutaAlmacenamiento = DB_PATH + DB_NAME;

         */

    }



    public void aperturaBBDD(Context contexto) {


        File file = new File(rutaAlmacenamiento);

        if(file.exists()){

            bbdd=SQLiteDatabase.openDatabase(rutaAlmacenamiento,null,SQLiteDatabase.OPEN_READONLY);

            if(bbdd.getVersion()<nuevaVersion){

                //System.out.println("SITIOS - ENCONTRADA VERSION MAS ACTUAL");

                copiaBBDD(contexto);
                bbdd=SQLiteDatabase.openDatabase(rutaAlmacenamiento,null,SQLiteDatabase.OPEN_READONLY);
                //System.out.println("SITIOS - ACTUALIZADO A VERSION: "+bbdd.getVersion());

            }

            //System.out.println("------------- SITIOS - EXISTE - Version: "+bbdd.getVersion());

        }else{

            //System.out.println("------------- SITIOS - NO EXISTE");
            copiaBBDD(contexto);
            bbdd=SQLiteDatabase.openDatabase(rutaAlmacenamiento,null,SQLiteDatabase.OPEN_READONLY);

        }

    }

    private void copiaBBDD(Context contexto){ //esto copia el archivo db3 en otro sitio dentro de la APP

            try{

            //InputStream datosEntrada = contexto.getAssets().open("sitiosLog.db3");
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
                //System.out.println("///////Base de datos SITIOS copiada");

        }catch(Exception e){
                System.out.println("///////Problema al copiar base datos SITIOS");
            }

    }

    public String dimenombre(int id){

        Cursor miCursor=null;
        miCursor=bbdd.rawQuery("SELECT * FROM sitiosLog WHERE id LIKE "+id,null);

        miCursor.moveToFirst();

        String nombre=miCursor.getString(5);
        byte[] image = miCursor.getBlob(4);

        Bitmap imagen =BitmapFactory.decodeByteArray(image, 0, image.length);



        return nombre;
    }

    public Bitmap dimeimagen(int id){

        Cursor miCursor=null;
        miCursor=bbdd.rawQuery("SELECT * FROM sitiosLog WHERE id LIKE "+id,null);

        miCursor.moveToFirst();

        byte[] image = miCursor.getBlob(4);

        Bitmap imagen =BitmapFactory.decodeByteArray(image, 0, image.length);


        return imagen;
    }

    public SitiosLog[] dameSitios(){

        Cursor miCursor=null;
        miCursor=bbdd.rawQuery("SELECT * ",null);

        SitiosLog[] misSitios=new SitiosLog[miCursor.getCount()];  // crea un array con la longitud del array pasado por parámetros

        miCursor.moveToFirst();

        for(int i=0;i<miCursor.getCount();i++) {

            misSitios[i].nombre = miCursor.getString(0);
            misSitios[i].nombre = miCursor.getString(1);
            misSitios[i].detalle = miCursor.getString(2);
            misSitios[i].latitud = miCursor.getString(3);
            misSitios[i].longitud = miCursor.getString(4);
            //misSitios[i].imagen = miCursor.getBlob(5);

            miCursor.moveToNext();
        }

        if(miCursor!=null && !miCursor.isClosed()){  // nos aseguramos que el cursor no es nulo

            miCursor.close();
        }

        return misSitios;  // DEVUELVE EL ARRAY CON LA INFO DE TODAS LAS LINEAS

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
