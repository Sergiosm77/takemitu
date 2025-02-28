package com.saraodigital.takemitu;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BDlineas {

    private int fallo=0, fallo2=0;

    private RequestQueue rq;
    private JsonRequest jrq,jrq2;

    public String[] nombresLineas;
    public Lineas[] lasLineas;

    private ListaNotas[] notas;

    private ListaMenus[] menus;

    public Lineas[] dameinfoLineas2(final Context v) {

        String url=v.getString(R.string.servidor);

        rq= Volley.newRequestQueue(v.getApplicationContext());

        jrq = new JsonObjectRequest
                (Request.Method.GET, url + "&quebase=horarios&quebase2=lineas&quebase3=menus&quebase4=notas", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray_horarios = response.getJSONArray("datos");

                            guardaDatosLineas(jsonArray_horarios,v);

                            estableceLineas(jsonArray_horarios);

                            JSONArray jsonArray_lineas = response.getJSONArray("datos2");

                            guardaDatosEstaciones(jsonArray_lineas,v);

                            estableceEstaciones(jsonArray_lineas);

                            JSONArray jsonArray_menus = response.getJSONArray("datos3");

                            guardaDatosMenus(jsonArray_menus,v);

                            estableceMenus(jsonArray_menus);

                            JSONArray jsonArray_notas = response.getJSONArray("datos4");

                            guardaDatosNotas(jsonArray_notas,v);

                            estableceNotas(jsonArray_notas);


                            fallo=2;

                        } catch (JSONException e) {

                            System.out.println("FALLO 1");
                            fallo=1;

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("HAY UN PROBLEMA 1: "+error);
                        fallo=1;

                    }
                });

        rq.add(jrq);

        while(fallo==0) {

            try {
                Thread.sleep(300);
            } catch (Exception e) {
                System.out.println(e);
            }

        }

        if(fallo==2){

            return lasLineas;
        }else{

            return null;
        }

    }

    public ListaMenus[] dameMenus(){


        return menus;

    }

    public ListaNotas[] dameNotas(){

        return notas;

    }

    public Lineas[] dameinfoLineas3(final Context v) {

        String url=v.getString(R.string.servidor);



        rq= Volley.newRequestQueue(v.getApplicationContext());

        jrq2 = new JsonObjectRequest
                (Request.Method.GET, url + "&quebase=horarios", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonArray = response.getJSONArray("datos");

                            guardaDatosLineas(jsonArray,v);

                            estableceLineas(jsonArray);

                            fallo2=2;

                        } catch (JSONException e) {

                            System.out.println("FALLO 1");
                            fallo2=1;

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("HAY UN PROBLEMA 1");
                        fallo2=1;

                    }
                });

        rq.add(jrq2);

        while(fallo2!=2) {

            try {
                Thread.sleep(1 * 1000);
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }

        }

        jrq = new JsonObjectRequest
                (Request.Method.GET, url + "&quebase=lineas", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        //System.out.println("VA BIEN");
                        try {
                            JSONArray jsonArray = response.getJSONArray("datos");

                            guardaDatosEstaciones(jsonArray,v);

                            estableceEstaciones(jsonArray);

                            fallo=2;

                        } catch (JSONException e) {

                            fallo=1;

                            System.out.println("FALLO 2");

                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("HAY UN PROBLEMA 2");
                        fallo=1;

                    }
                });

        rq.add(jrq);



        while(fallo==0 || fallo2==0) {

            try {
                Thread.sleep(1 * 1000);
            } catch (Exception e) {
                System.out.println(e);
            }

        }

        if(fallo==2 && fallo2==2){

            return lasLineas;
        }else{

            return null;
        }

    }

    private void estableceMenus(JSONArray datosMenus){

        menus=new ListaMenus[datosMenus.length()];

        try{

            for(int i=0;i<datosMenus.length();i++){

                JSONObject object = datosMenus.getJSONObject(i);

                menus[i]=new ListaMenus(object.getString("nombre"),object.getString("basedatos"),object.getString("imagen"));


            }


        }catch(Exception e){


        }


    }

    private void estableceNotas(JSONArray datosNotas){

        notas=new ListaNotas[datosNotas.length()];

        try{

            for(int i=0;i<datosNotas.length();i++){

                JSONObject object = datosNotas.getJSONObject(i);

                notas[i]=new ListaNotas(object.getString("nota"),object.getString("detalle"),object.getString("valor"));

            }


        }catch(Exception e){


        }


    }


    public void estableceLineas(JSONArray datosEstaciones){

        try{

            nombresLineas=new String[datosEstaciones.length()];

            lasLineas=new Lineas[datosEstaciones.length()];

            for(int i=0;i<lasLineas.length;i++) {

                lasLineas[i]=new Lineas();

                JSONObject object = datosEstaciones.getJSONObject(i);

                lasLineas[i].nombre = object.getString("Linea");
                lasLineas[i].sentido = object.getString("sentido");
                lasLineas[i].color = object.getString("colorlinea");

                lasLineas[i].horarioFestivo_inicio = object.getString("iniciofest");
                lasLineas[i].horarioFestivo_fin = object.getString("finfest");
                lasLineas[i].horarioFestivo_intervalo = object.getString("intervalofest");
                lasLineas[i].horarioLaboral_inicio = object.getString("iniciolab");
                lasLineas[i].horarioLaboral_fin = object.getString("finlab");
                lasLineas[i].horarioLaboral_intervalo = object.getString("intervalolab");
                lasLineas[i].horarioViernes_inicio = object.getString("inicioviernes");
                lasLineas[i].horarioViernes_fin = object.getString("finviernes");
                lasLineas[i].horarioViernes_intervalo = object.getString("intervaloviernes");
                lasLineas[i].horarioSabado_inicio = object.getString("iniciosabado");
                lasLineas[i].horarioSabado_fin = object.getString("finsabado");
                lasLineas[i].horarioSabado_intervalo = object.getString("intervalosabado");

                lasLineas[i].detalleNota=object.getString("detalle");
                lasLineas[i].valorNota=object.getInt("valornota");

                lasLineas[i].activa=object.getInt("activa");

                if(lasLineas[i].horarioLaboral_inicio.equals("especial")){lasLineas[i].especialLab=object.getString("especialLaboral");}
                if(lasLineas[i].horarioViernes_inicio.equals("especial")){lasLineas[i].especialVie=object.getString("especialViernes");}
                if(lasLineas[i].horarioSabado_inicio.equals("especial")){lasLineas[i].especialSab=object.getString("especialSabado");}
                if(lasLineas[i].horarioFestivo_inicio.equals("especial")){lasLineas[i].especialFes=object.getString("especialFestivo");}

            }

        }catch (JSONException e){


        }



    }

    public void estableceEstaciones(JSONArray datosLineas){

        try {

            for (int i = 0; i < nombresLineas.length; i++) {

                int contador = 0;

                for (int e = 0; e < datosLineas.length(); e++) {

                    JSONObject object = datosLineas.getJSONObject(e);

                    if (object.getString("Linea").equals(lasLineas[i].nombre)) {
                        contador++;
                    }

                }

                lasLineas[i].estaciones = new Location[contador];

            }

            for (int i = 0; i < lasLineas.length; i++) {

                int contador = 0;

                for (int e = 0; e < datosLineas.length(); e++) {

                    JSONObject object = datosLineas.getJSONObject(e);

                    if (object.getString("Linea").equals(lasLineas[i].nombre)) {

                        lasLineas[i].estaciones[contador] = datosEstacion(e, object);

                        contador++;

                    }

                }
            }
        }catch (JSONException e){


        }


    }

    public void guardaDatosNotas(JSONArray datos,Context v){
        SharedPreferences guarda= PreferenceManager.getDefaultSharedPreferences(v);

        SharedPreferences.Editor mieditor=guarda.edit();

        mieditor.putString("DATOSNOTAS",datos.toString());

        System.out.println("GUARDA NOTAS");

        mieditor.apply();

    }

    public void leeDatosNotas(Context v){

        SharedPreferences guarda= PreferenceManager.getDefaultSharedPreferences(v);
        String recupera = guarda.getString("DATOSNOTAS","0");

        JSONArray respuesta=null;

        System.out.println("LEE NOTAS");


        if (recupera != null) {
            try {
                respuesta = new JSONArray(recupera);

            } catch (JSONException e) {

            }
        }

        estableceNotas(respuesta);

    }

    public void guardaDatosMenus(JSONArray datos,Context v){
        SharedPreferences guarda= PreferenceManager.getDefaultSharedPreferences(v);

        SharedPreferences.Editor mieditor=guarda.edit();

        mieditor.putString("DATOSMENUS",datos.toString());

        System.out.println("GUARDA MENUS");

        mieditor.apply();

    }

    public void leeDatosMenus(Context v){

        SharedPreferences guarda= PreferenceManager.getDefaultSharedPreferences(v);
        String recupera = guarda.getString("DATOSMENUS","0");

        JSONArray respuesta=null;

        System.out.println("LEE MENUS");


        if (recupera != null) {
            try {
                respuesta = new JSONArray(recupera);

            } catch (JSONException e) {

            }
        }

        estableceMenus(respuesta);

    }

    public void guardaDatosEstaciones(JSONArray datos,Context v){
        SharedPreferences guarda= PreferenceManager.getDefaultSharedPreferences(v);

        SharedPreferences.Editor mieditor=guarda.edit();

        mieditor.putString("DATOSESTACIONES",datos.toString());

        System.out.println("GUARDA ESTACIONES");

        mieditor.apply();


    }

    public void leeDatosEstaciones(Context v){

        SharedPreferences guarda= PreferenceManager.getDefaultSharedPreferences(v);

        String recupera = guarda.getString("DATOSESTACIONES","0");

        System.out.println("LEE ESTACIONES");

        JSONArray respuesta=null;


        SharedPreferences.Editor mieditor=guarda.edit();

        if (recupera != null) {
            try {
                respuesta = new JSONArray(recupera);

            } catch (JSONException e) {

            }
        }

       estableceEstaciones(respuesta);


    }

    public void guardaDatosLineas(JSONArray datos,Context v){

        SharedPreferences guarda= PreferenceManager.getDefaultSharedPreferences(v);

        SharedPreferences.Editor mieditor=guarda.edit();

        mieditor.putString("DATOSLINEAS",datos.toString());

        System.out.println("GUARDA LINEAS");

        mieditor.apply();


    }

    public void leeDatosLineas(Context v){


        SharedPreferences guarda= PreferenceManager.getDefaultSharedPreferences(v);
        String recupera = guarda.getString("DATOSLINEAS","0");

        JSONArray respuesta=null;

        System.out.println("LEE LINEAS");




        if (recupera != null) {
            try {
                respuesta = new JSONArray(recupera);

            } catch (JSONException e) {

            }
        }

        estableceLineas(respuesta);


    }

    public boolean compruebaGuardado(Context v){

        SharedPreferences guarda= PreferenceManager.getDefaultSharedPreferences(v);

        if(guarda.contains("DATOSLINEAS")){

            return true;

        }else{

            return false;
        }


    }

    public Lineas[] dameinfoLineas(final Context v, final String[] nombresLineas) {


        String url=v.getString(R.string.servidor);

        rq= Volley.newRequestQueue(v.getApplicationContext());

        final Lineas[] lasLineas=new Lineas[nombresLineas.length];

        for(int i=0;i<nombresLineas.length;i++) {

            lasLineas[i] = new Lineas();
            lasLineas[i].nombre = nombresLineas[i];

            //System.out.println(""+lasLineas[i].nombre);

        }

        jrq2 = new JsonObjectRequest
                (Request.Method.GET, url + "&quebase=horarios", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("TODO VA BIEN ");
                        try {
                            JSONArray jsonArray = response.getJSONArray("datos");

                            for(int i=0;i<lasLineas.length;i++) {

                                for (int e = 0; e < jsonArray.length(); e++) {

                                    JSONObject object = jsonArray.getJSONObject(e);

                                    if (object.getString("Linea").equals(nombresLineas[i])) {

                                        lasLineas[i].horarioFestivo_inicio = object.getString("iniciofest");
                                        lasLineas[i].horarioFestivo_fin = object.getString("finfest");
                                        lasLineas[i].horarioFestivo_intervalo = object.getString("intervalofest");
                                        lasLineas[i].horarioLaboral_inicio = object.getString("iniciolab");
                                        lasLineas[i].horarioLaboral_fin = object.getString("finlab");
                                        lasLineas[i].horarioLaboral_intervalo = object.getString("intervalolab");
                                        lasLineas[i].horarioViernes_inicio = object.getString("inicioviernes");
                                        lasLineas[i].horarioViernes_fin = object.getString("finviernes");
                                        lasLineas[i].horarioViernes_intervalo = object.getString("intervaloviernes");
                                        lasLineas[i].horarioSabado_inicio = object.getString("iniciosabado");
                                        lasLineas[i].horarioSabado_fin = object.getString("finsabado");
                                        lasLineas[i].horarioSabado_intervalo = object.getString("intervalosabado");

                                        lasLineas[i].detalleNota=object.getString("detalle");
                                        lasLineas[i].valorNota=object.getInt("valornota");

                                        if(lasLineas[i].horarioLaboral_inicio.equals("especial")){lasLineas[i].especialLab=object.getString("especialLaboral");}
                                        if(lasLineas[i].horarioViernes_inicio.equals("especial")){lasLineas[i].especialVie=object.getString("especialViernes");}
                                        if(lasLineas[i].horarioSabado_inicio.equals("especial")){lasLineas[i].especialSab=object.getString("especialSabado");}
                                        if(lasLineas[i].horarioFestivo_inicio.equals("especial")){lasLineas[i].especialFes=object.getString("especialFestivo");}

                                    }

                                }

                            }

                            fallo2=2;

                        } catch (JSONException e) {

                            System.out.println("FALLO");
                            fallo2=1;

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("HAY UN PROBLEMA 2");
                        fallo2=1;

                    }
                });

        rq.add(jrq2);

        jrq = new JsonObjectRequest
                (Request.Method.GET, url + "&quebase=lineas", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("TODO VA BIEN");
                        try {
                            JSONArray jsonArray = response.getJSONArray("datos");

                            for(int i=0;i<lasLineas.length;i++) {

                                int contador = 0;

                                for (int e = 0; e < jsonArray.length(); e++) {

                                    JSONObject object = jsonArray.getJSONObject(e);

                                    if (object.getString("Linea").equals(nombresLineas[i])) {
                                        contador++;
                                    }

                                }

                                lasLineas[i].estaciones = new Location[contador];

                            }

                            for(int i=0;i<lasLineas.length;i++) {

                                int contador=0;

                                for (int e = 0; e < jsonArray.length(); e++) {

                                    JSONObject object = jsonArray.getJSONObject(e);

                                    if (object.getString("Linea").equals(nombresLineas[i])) {

                                        lasLineas[i].estaciones[contador] = datosEstacion(e, object);

                                        contador++;

                                    }

                                }
                            }

                            fallo=2;

                        } catch (JSONException e) {

                            fallo=1;

                            System.out.println("FALLO");

                        }
                    }
                }, new Response.ErrorListener() {


                    @Override
                    public void onErrorResponse(VolleyError error) {

                        System.out.println("HAY UN PROBLEMA 1");
                        fallo=1;

                    }
                });

        rq.add(jrq);



        while(fallo==0 && fallo2==0) {

            try {
                Thread.sleep(1 * 1000);
            } catch (Exception e) {
                System.out.println(e);
            }

        }

        if(fallo==1 || fallo2==1){

            return null;

        }else {
            return lasLineas;
        }

    }


    private Location datosEstacion(int id, JSONObject object){

        Location estacion=null;

        try {
            estacion=new Location(object.getString("Estacion"));

        estacion.setLatitude(Double.parseDouble(object.getString("Latitud")));
        estacion.setLongitude(Double.parseDouble(object.getString("Longitud")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return estacion;
    }


}
