package com.saraodigital.takemitu;

import android.location.Location;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


// ------ CLASE PARA BUSCAR LAS DIRECCIONES VIA GOOGLE GEOCODER -------------------------
// --------------------------------------------------------------------------------------


public class OptimizacionBusqueda {  // buscamos la localizacion mas cercana al centro de la ciudad

    public static Location busca(String direccion,String key){

        Location centroCiudad=new Location("");

        centroCiudad.setLatitude(40.4378698);
        centroCiudad.setLongitude(-3.8196193);

        direccion=direccion+", Logroño"; // cuando el metodo reciba la dirección, al buscar en google maps especificamos que la busque en Madrid,
                                    // por si hubiera nombres de calles iguales en otras ciudades

        Location localizacion;

        Log.i("Nota: ","Llega hasta optimiza busqueda");

        try{

            localizacion=consultaLocalizacion("calle " + direccion, centroCiudad,key);

            Log.i("Nota: ","Localizacion: "+localizacion);

            return localizacion;

        }catch(Exception e){
            Log.i("Nota: ","Localizacion: ERROR");
            return null;}
    }



    private static Location consultaLocalizacion(String direccion, Location centroCiudad, String key) throws Exception {

        Location localizacion=null;

        InputStream entradaDatos;

        // Establecer conexión con web Google Maps

        // Añadimos la direccion de google maps mas nuestra dirección, convertida al sistema utf-8 por si hay caracteres latinos


        URL url=new URL("https://maps.google.com/maps/api/geocode/json?address=" + URLEncoder.encode(direccion, "UTF-8")+"&key="+key);

        HttpURLConnection cliente = (HttpURLConnection) url.openConnection();

        cliente.connect();


        // ----------

        entradaDatos= new BufferedInputStream(cliente.getInputStream());

        Log.i("Nota: ","Va a leer cliente ");

        StringBuilder cadena=new StringBuilder();

        int caracter;
        while((caracter=entradaDatos.read())!=-1){  // mientras haya caracteres en entradaDatos hace...

            cadena.append(((char)caracter)); // añade a cadena el caracter

        }
        cliente.disconnect();

        // transformamos la info obtenida del stream en objeto JSON

        JSONObject objetoJSON=new JSONObject(cadena.toString());

        Log.i("Nota: ","Objeto JSON: "+objetoJSON);

        if (!(objetoJSON.getString("status").equals("OK"))){

            Log.i("Nota: ","Devuelve: uno");

            return null;  // Si el objeto JSON NO tenga un estado OK, devuelve null
        }

        JSONArray direcciones=objetoJSON.getJSONArray("results");

        if(direcciones==null || direcciones.length()==0){

            Log.i("Nota: ","Devuelve: dos");
            return null; // si direcciones es nulo o está vacio devuelve null
        }

        localizacion=getLocalizacion(direcciones.getJSONObject(0));

        return localizacion;

    }

    private static Location getLocalizacion(JSONObject dire) throws Exception{ // NUESTRA POSICION ACTUAL: Saca del JSON la info que necesitamos, latitud y longitud

        String direccion=dire.getString("formatted_address"); // nos lo devuelve en un formato que no es UTF-8

        Log.i("Nota: ","En getLocalizacion: "+direccion);

        direccion=new String(direccion.getBytes("ISO8859-1"), "UTF-8"); // convierte la info extraida de ISO8859 que nos da google maps a utf-8

        Location localizacion=new Location(direccion);

        double latitud=dire.getJSONObject("geometry").getJSONObject("location").getDouble("lat"); // extraemos de dire la latitud y la longitud
        double longitud=dire.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

        localizacion.setLatitude(latitud);  // metemos en localizacion la latitud y longitud obtenidas
        localizacion.setLongitude(longitud);

        return localizacion;
    }
}
