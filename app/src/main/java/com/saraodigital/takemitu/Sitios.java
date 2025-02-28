package com.saraodigital.takemitu;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class Sitios extends Fragment{


    BBDDsitios bdsitios;

    private HorizontalScrollView listasitios;
    private LinearLayout pantalla;

    public static Location irA;

    private NavController navController;

    private DatosViewModel viewModel;

    private LinearLayout rutaContenedor,rutacontenedorImagen;
    private LayoutInflater inflador;
    private FloatingActionButton irlugar;
    private androidx.appcompat.app.ActionBar menu;
    private ScrollView scrolldescripcion;

    private SQLiteDatabase bbdd;

    private ProgressBar progressBarSitios;

    private String rutaAlmacenamiento;

    private DecimalFormat formato1;

    private Cursor miCursor;

    private ImageView[] imagenes;

    private RequestQueue rq;
    private JsonRequest jrq;



    public Sitios() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //viewModel = ViewModelProviders.of(getActivity()).get(DatosViewModel.class);
        viewModel = new ViewModelProvider(getActivity()).get(DatosViewModel.class);

        View v= inflater.inflate(R.layout.fragment_sitios, container, false);


        rutaContenedor = v.findViewById(R.id.pantalla_descr);

        //rutacontenedorImagen=v.findViewById(R.id.pantalla_sitios);

        inflador = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        irlugar=v.findViewById(R.id.irlugar);

        progressBarSitios=v.findViewById(R.id.progressBar_sitios);



        irlugar.hide();

        formato1 = new DecimalFormat("#.0");

        //listasitios=v.findViewById(R.id.envios);

        scrolldescripcion=v.findViewById(R.id.scroll_descripcion);

        menu=((AppCompatActivity)getActivity()).getSupportActionBar();
        //((ContenedorInicio)getActivity()).getSupportActionBar().hide();

        menu.hide();

        bdsitios=new BBDDsitios(getContext());

        ponImagen(inflador, BitmapFactory.decodeResource(getResources(),(R.drawable.mapa_general)), 0,rutaContenedor);
        ponDescripcion(inflador, getString(R.string.seleccionadonde), rutaContenedor,2);
        ponDescripcion(inflador, "", rutaContenedor,0);
        ponDescripcion(inflador, getString(R.string.info_tu_ruta), rutaContenedor,0);


        irlugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                viewModel.setLocalizacion(irA);

                navController = Navigation.findNavController(getActivity(), R.id.content_frame);

                navController.navigateUp();

            }
        });

     /*

        String packageName = getContext().getPackageName();
        String DB_PATH = "/data/data/" + packageName + "/databases/";
//Create the directory if it does not exist
        File directory = new File(DB_PATH);
        String DB_NAME = "sitiosLog.db3"; //The name of the source sqlite file
        rutaAlmacenamiento = DB_PATH + DB_NAME;

        bbdd=SQLiteDatabase.openDatabase(rutaAlmacenamiento,null,SQLiteDatabase.OPEN_READONLY);

        Cursor miCursor=null;
        miCursor=bbdd.rawQuery("SELECT * FROM sitiosLog",null);

        for(int i=0;i<miCursor.getCount();i++){


            miCursor.moveToPosition(i);

            // for(int i=0;i<miCursor.getCount();i++){


            final String detalle=miCursor.getString(1);
            final  String latitud=miCursor.getString(2);
            final  String longitud=miCursor.getString(3);
            byte[] image = miCursor.getBlob(4);
            final  String nombre=miCursor.getString(5);

            Location dondeesta=new Location("");
            dondeesta.setLatitude(Double.parseDouble(latitud));
            dondeesta.setLongitude(Double.parseDouble(longitud));

            final Bitmap imagen = BitmapFactory.decodeByteArray(image, 0, image.length);

            LinearLayout icono=(LinearLayout)inflador.inflate(R.layout.imagen_sitios,null);
            final ImageView iconoImagen=icono.findViewById(R.id.icono_sitios);

            final double dissitio;

            if(Buscador.migps.getLongitude()!=0) {

                dissitio=Buscador.migps.distanceTo(dondeesta);

            }else{dissitio=0;}

            //((ImageView) icono.findViewById(R.id.icono_sitios)).setImageBitmap(imagen);

            rutacontenedorImagen.addView(icono); // vamos añadiendo imagenes y textos al contenedor

            iconoImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    irA=new Location(nombre);

                    irA.setLatitude(Double.parseDouble(latitud));
                    irA.setLongitude(Double.parseDouble(longitud));

                    irlugar.show();

                    rutaContenedor.removeAllViews();

                    ponImagen(inflador, imagen, dissitio,rutaContenedor);
                    ponDescripcion(inflador, nombre, rutaContenedor,2);
                    ponDescripcion(inflador, detalle, rutaContenedor,0);

                    ponDescripcion(inflador, "\n"+"\n", rutaContenedor,0);

                    scrolldescripcion.fullScroll(ScrollView.FOCUS_UP);

                }
            });

        }


        bbdd.close();

 */

       iniciaTodo();


       //CargaPagina empieza=new CargaPagina(); // QUITADO DEL ORIGINAL
      //empieza.execute();

     //CargaServidor ponPrueba=new CargaServidor();
     //ponPrueba.execute();


        return v;

    }


    public void iniciaTodo(){

        String url=getString(R.string.servidor)+"&quebase=sitioslog";

        rq=Volley.newRequestQueue(getActivity());
/*
        Map<String, String> params = new HashMap();
        params.put("clave", "hola");
        params.put("clave2", "hola");

        JSONObject parametros = new JSONObject(params);
*/
        jrq = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        //ArrayList<Bitmap> imagenestotal=new ArrayList<>();
                        ArrayList<String> imagenestotal=new ArrayList<>();
                        ArrayList<String> nombres=new ArrayList<>();
                        ArrayList<String> detalles=new ArrayList<>();
                        ArrayList<String> latitud=new ArrayList<>();
                        ArrayList<String> longitud=new ArrayList<>();

                        System.out.println("TODO VA BIEN ");
                        try {
                            JSONArray jsonArray = response.getJSONArray("datos");
                            //JSONObject jsonObject = jsonArray.getJSONObject(0);

                                //JSONObject jsonObject1 = jsonObject.getJSONObject("datos");
                                //JSONArray jsonArray1 = jsonObject1.getJSONArray("detalles");

                                // Recorrer los datos del usuario
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String nombre_bd = object.getString("nombre");
                                    String detalles_bd = object.getString("detalles");
                                    String latitud_bd=object.getString("latitud");
                                    String longitud_bd=object.getString("longitud");
                                    String imagen_bd=object.getString("imagen");

                                    nombres.add(nombre_bd);
                                    detalles.add(detalles_bd);
                                    imagenestotal.add(imagen_bd);
                                    //imagenestotal.add(BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.infoicon));
                                    latitud.add(latitud_bd);
                                    longitud.add(longitud_bd);

                                    //System.out.println("Numero: "+i+" "+nombre_bd);

                                    //System.out.println(detalles_bd);
                                    //System.out.println(" ");
                                    //System.out.println(" ");
                                }

                            LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
                            RecyclerView reciclador=getView().findViewById(R.id.envios);
                            reciclador.setLayoutManager(layoutManager);
                            Adaptador_Sitio adaptador=new Adaptador_Sitio(imagenestotal,nombres,detalles,latitud,longitud,getView().getContext(),getView());
                            reciclador.setAdapter(adaptador);

                            progressBarSitios.setVisibility(View.GONE);


                        } catch (JSONException e) {

                            System.out.println("FALLO");

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("HAY UN PROBLEMA "+error.toString());

                    }
                });

        rq.add(jrq);

    }




/*
// QUITADO DEL ORIGINAL

    private class CargaPagina extends AsyncTask<String,Integer,Cursor> {  // carga en memoria la base de datos

        private ArrayList<Bitmap> imagenestotal=new ArrayList<>();
        private ArrayList<String> nombres=new ArrayList<>();
        private ArrayList<String> detalles=new ArrayList<>();
        private ArrayList<String> latitud=new ArrayList<>();
        private ArrayList<String> longitud=new ArrayList<>();

        @Override
        protected Cursor doInBackground(String... imagen) {

            String packageName = getContext().getPackageName();
            String DB_PATH = "/data/data/" + packageName + "/databases/";
//Create the directory if it does not exist
            File directory = new File(DB_PATH);
            String DB_NAME = "sitiosLog.db3"; //The name of the source sqlite file
            rutaAlmacenamiento = DB_PATH + DB_NAME;

            bbdd=SQLiteDatabase.openDatabase(rutaAlmacenamiento,null,SQLiteDatabase.OPEN_READONLY);

            int offset=0;

            miCursor = bbdd.rawQuery("SELECT * FROM sitiosLog LIMIT 5", null);

            while(miCursor.getCount()>0) {

                miCursor.close();

                //System.out.println("OFFSET: "+offset);

                miCursor = bbdd.rawQuery("SELECT * FROM sitiosLog LIMIT 5 OFFSET "+offset, null);

                for (int i = 0; i < miCursor.getCount(); i++) {

                    miCursor.moveToPosition(i);

                    nombres.add(miCursor.getString(5));
                    detalles.add(miCursor.getString(1));
                    latitud.add(miCursor.getString(2));
                    longitud.add(miCursor.getString(3));

                    imagenestotal.add(BitmapFactory.decodeByteArray(miCursor.getBlob(4), 0, miCursor.getBlob(4).length));

                }

                offset=offset+5;


            }

            return null;

        }

        protected void onProgressUpdate(Integer... valores){


            progressBarSitios.setVisibility(View.VISIBLE);



        }

        protected void onPostExecute(Cursor miCursor){

            LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
            RecyclerView reciclador=getView().findViewById(R.id.envios);
            reciclador.setLayoutManager(layoutManager);
            Adaptador_Sitio adaptador=new Adaptador_Sitio(imagenestotal,nombres,detalles,latitud,longitud,getView().getContext(),getView());
            reciclador.setAdapter(adaptador);

            bbdd.close();

            //System.out.println("CIERRA BASE DATOS");

            progressBarSitios.setVisibility(View.GONE);




        }



    }
*/




    private void ponDescripcion(LayoutInflater inflador, String texto, LinearLayout contenedor,int color){

        LinearLayout descripciones=(LinearLayout)inflador.inflate(R.layout.descripciones,null);



        //((ImageView)distanciaEstaciones.findViewById(R.id.icono)).setImageResource(imagen);
        ((TextView)descripciones.findViewById(R.id.texto_desc)).setText(texto);

        if(color==1){
            ((TextView)descripciones.findViewById(R.id.texto_desc)).setTextColor(Color.DKGRAY);
        }
        if(color==2){
            ((TextView)descripciones.findViewById(R.id.texto_desc)).setTextColor(Color.CYAN);
        }


        contenedor.addView(descripciones); // vamos añadiendo imagenes y textos al contenedor

        if(color==2) {
            (descripciones.findViewById(R.id.texto_desc)).animate().translationY(20);
            (descripciones.findViewById(R.id.texto_desc)).setAlpha(0.5f);
            (descripciones.findViewById(R.id.texto_desc)).animate().alpha(1f);
        }

    }

    private void ponImagen(LayoutInflater inflador, Bitmap imagen, double dissitio, LinearLayout contenedor){

        String dis;

        if(dissitio>1000){

            dis=getString(R.string.estasA)+" "+formato1.format(dissitio/1000)+" Km";

        }else{

            if(dissitio==0){

                dis="";

            }else{dis=getString(R.string.estasA)+" "+ Math.round(dissitio)+" "+getString(R.string.metros);}
        }

        LinearLayout imagenes=(LinearLayout)inflador.inflate(R.layout.imagenes,null);

        ((ImageView)imagenes.findViewById(R.id.imagen)).setImageBitmap(imagen);
        ((TextView)imagenes.findViewById(R.id.nombre_dissitios)).setText(dis);



        //((TextView)descripciones.findViewById(R.id.texto_desc)).setText(texto);

        contenedor.addView(imagenes); // vamos añadiendo imagenes y textos al contenedor

        (imagenes.findViewById(R.id.imagen)).setAlpha(0.5f);
        (imagenes.findViewById(R.id.imagen)).animate().alpha(1);
        //imagenes.findViewById(R.id.nombre_dissitios).setScaleX(1.5f);
        //imagenes.findViewById(R.id.nombre_dissitios).animate().scaleX(1);

    }



    // ----------------- SERVIDOR ---------------
/*
    private class CargaServidor extends AsyncTask<String,Integer,String> {


        @Override
        protected String doInBackground(String... imagen) {

            String URL="http://localhost/takemitu/conexion.php";

            StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if(!response.isEmpty()){

                        System.out.println("SIN RESUPUESTA");

                    }

                    ponDescripcion(inflador, response, rutaContenedor,0);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getContext(),"ERROR DE RESPUESTA",Toast.LENGTH_SHORT).show();

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parametros=new HashMap<String,String>();
                    parametros.put("","uno");
                    return parametros;
                }
            };

            RequestQueue queue=Volley.newRequestQueue(getContext());
            queue.add(stringRequest);

            System.out.println("FIN EJECUCION");




            return null;

        }

        protected void onProgressUpdate(Integer... valores){


        }

        protected void onPostExecute(String respuesta){




        }


    }

 */





}
