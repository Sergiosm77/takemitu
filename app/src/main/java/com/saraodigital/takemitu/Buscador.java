package com.saraodigital.takemitu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Buscador extends Fragment {

        // VARIABLES
    private int tiempoMargen=3;

    private Location puntoOrigen,puntoDestino;
    private String miubi;
    static Location migps=new Location("");
    private String key;
    private Lineas[] lineas;
    private int contador=0;
    private TextView origen,destino,horaActual,diahoy;
    private LinearLayout rutaContenedor;
    private LayoutInflater inflador;
    private Location[] daRuta1A,daRuta1B,daRuta2A,daRuta2B,daRuta3A,daRuta3B;
    private String nombreRuta1A,nombreRuta1B,nombreRuta2A,nombreRuta2B,nombreRuta3A,nombreRuta3B,nombreRuta4;
    private int minutos1,minutos2,minutos3,minutos4;
    private int ruta1Bsiguiente,ruta2Bsiguiente,ruta3Bsiguiente,ruta4Bsiguiente;
    private Location irA;
    private Rutas ruta;
    private Button enviar,sitios,informacion;
    private FloatingActionButton cambiaruta;
    private String direccionOrigen, direccionDestino;
    private WebView browser;
    private String url;
    private DatosViewModel viewModel;
    private boolean hora_cambiada=false;
    private boolean dia_cambiado=false;
    private int h_actual,m_actual,h_nueva,m_nueva,fechaHoy,minutosHoraReal;
    private Calendar cal;
    private BBDD_Helper helper;
    private AlertDialog esperaAlerta;
    NavigationView nView;
    private Toast toast1;
    private String mensajeAlerta="";
    private ProgressBar progresoEspera;
    private TextView pulsaAceptar;


    // -- Adaptador publicidad -----------------

    SliderView sliderPubli;

    ListaMenus[] imagenes;

    // ---------------------------------


    static TextView panelInfo;

    int contadorCargas=1;

    private androidx.appcompat.app.ActionBar menu;
// VOLVER A PONER PARA GPS ----------------------
    //private LocationManager mlocManager;
    //private Localizacion Local;

    private int inicio =0; // -------------- EJECUCION DE APP POR PRIMERA VEZ ---------

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_buscador, container, false);

        //System.out.println("EJECUTA ONCREATEVIEW");

        panelInfo=v.findViewById(R.id.panel_info);

        destino=v.findViewById(R.id.destino);
        origen=v.findViewById(R.id.origen);
        enviar=v.findViewById(R.id.enviar);
        sitios=v.findViewById(R.id.sitios);
        informacion=v.findViewById(R.id.info);
        horaActual=v.findViewById(R.id.hora_actual);
        cambiaruta=v.findViewById(R.id.cambiaruta);
        diahoy=v.findViewById(R.id.dia_de_hoy);

        // -- Adaptador publicidad -----------------


        Intent intento=getActivity().getIntent();
        imagenes = (ListaMenus[])intento.getSerializableExtra("MENUS");

        sliderPubli=v.findViewById(R.id.slider_publi);

        int[] imagenesPubli= {R.drawable.publi_aqui, R.drawable.anuncio};
        AdaptadorPublicidad adaptadorPublicidad=new AdaptadorPublicidad(imagenes, getContext());

        sliderPubli.setSliderAdapter(adaptadorPublicidad);
        sliderPubli.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderPubli.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderPubli.startAutoCycle();



        // ---------------------------

        helper = new BBDD_Helper(getActivity());
        inflador = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        progresoEspera=v.findViewById(R.id.progreso_espera);
        progresoEspera.setVisibility(View.GONE);

        key = "";//getString(R.string.google_maps_key);
        miubi=getString(R.string.miUbi);

        toast1 = Toast.makeText(getActivity().getApplicationContext(), mensajeAlerta, Toast.LENGTH_SHORT);

        Locale l = new Locale("es","ES");
        cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"),l);

        // VOLVER A PONER PARA GPS ----------------------
        //mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //Local = new Localizacion();

        if(!dia_cambiado){fechaHoy=cal.get(Calendar.DAY_OF_WEEK)-1;}

        migps.setProvider(miubi);

        //viewModel = ViewModelProviders.of(getActivity()).get(DatosViewModel.class);

        viewModel =new ViewModelProvider(getActivity()).get(DatosViewModel.class);  // ---- Clase para compartir datos --------------

/*
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION }, 1222);
        }
*/
        informacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_lineas);

            }
        });

        sitios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ------- SITIOS ---------------------------

                if(compruebaRed()) {

                    Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_menu);
                }else{

                    mensajeAlerta=getString(R.string.sin_internet);
                    ponAlerta();

                    //TextView mensaje = toast1.getView().findViewById(android.R.id.message);
                    //mensaje.setGravity(Gravity.CENTER);

                }

            }
        });


        cambiaruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cambialaruta();

            }
        });

        horaActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarHora();

            }
        });

        origen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                entradaDatosOrigen();

            }
        });

        diahoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

          cambiaDia();

            }
        });

        destino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                entradaDatosDestino();

            }
        });

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!compruebaGps() && (origen.getText().equals(miubi) || destino.getText().equals(miubi))){

                    panelInfo.setText(getString(R.string.activa_gps));
                    //ponAlerta();

                }else {

                    panelInfo.setText("");
                    buscaLaRuta();
                }

            }
        });


        return v;  // this replaces 'setContentView'

    }


    @Override
    public void onStop() {

        //mlocManager.removeUpdates(Local);  // ------------- DETIENE EL GPS ----------------------

        super.onStop();
    }


    @Override
    public void onResume() {

        if(!compruebaGps()){

            panelInfo.setText(R.string.activagps);
        }else{
            panelInfo.setText("");
        }

        try {
            menu = ((AppCompatActivity) getActivity()).getSupportActionBar();

            menu.setTitle(getString(R.string.calculaRuta));

            menu.show();
        }catch (Exception e){


        }

        //System.out.println("---------------------EJECUTA ON RESUME");

        if(lineas==null){

            Bundle miBundle=getActivity().getIntent().getExtras();

            if(miBundle!=null) {
                Parcelable[] datos = miBundle.getParcelableArray("LINEAS");

                if(datos!=null) {

                    lineas = Arrays.copyOf(datos, datos.length, Lineas[].class); // copiamos el array del paquete en el array lineas
                }else{

                    mensajeAlerta="Sin acceso a la base de datos";
                    ponAlerta();
                    enviar.setEnabled(false);

                }

            }else{

                mensajeAlerta="Sin acceso a la base de datos";
                ponAlerta();
                enviar.setEnabled(false);
            }

        }

        if(dia_cambiado){

            ponElDia(getString(R.string.buscar_en));

        }else{

            ponElDia(getString(R.string.hoy_es));

        }

        ruta=new Rutas();

        viewModel.getLocalizacion().removeObservers(this);

        viewModel.getLocalizacion().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location irAlugar) {

                //System.out.println("---------------------EJECUTA ONCHANGED");

                if(irAlugar!=null) {
                    irA = irAlugar;
                    viewModel.setLocalizacion(null);
                }
            }
        });

        if(inicio==0){

            puntoOrigen=migps;
            origen.setText(puntoOrigen.getProvider());

            if(!compruebaGps()){
                panelInfo.setText(getString(R.string.activagps));
                //ponAlerta();
            }else{

                panelInfo.setText("");
            }

            inicio=1;

        }

        if (puntoOrigen != null) {

            //System.out.println("direccion de origen no es null");
            origen.setText(puntoOrigen.getProvider());

        } else {
            //System.out.println("contenido direccion origen: "+direccionOrigen);
            origen.setText("");
        }

        if(hora_cambiada){

            String min;
            if(m_nueva<10){
                min="0"+m_nueva;
            }else{
                min=Integer.toString(m_nueva);
            }

            String horanueva=getString(R.string.saliralas)+" "+h_nueva+":"+min;

            horaActual.setText(horanueva);

        }

        if(irA==null && puntoDestino!=null){

            destino.setText(puntoDestino.getProvider());
        }else if(irA!=null){
            destino.setText(irA.getProvider());
            puntoDestino=irA;
            irA=null;
        }

        super.onResume();
    }

    private void cambialaruta(){

        String temp=origen.getText().toString();
        Location intermedio;

        origen.setText(destino.getText().toString());
        destino.setText(temp);

        intermedio=puntoOrigen;
        puntoOrigen=puntoDestino;
        puntoDestino=intermedio;

    }

    private void buscaLaRuta(){

        Rutas.pulsa=0;

        if(origen.getText().equals(miubi)){

            puntoOrigen=migps;
        }
        if(destino.getText().equals(miubi)){

            puntoDestino=migps;
        }

        //System.out.println("AL EMPEZAR, EL INICIO ES: "+ puntoOrigen.getLatitude());

        if(!hora_cambiada) {
            cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
            h_actual = cal.get(Calendar.HOUR_OF_DAY);
            m_actual = cal.get(Calendar.MINUTE);

            //ruta.h_actual = h_actual;
            //ruta.m_actual = m_actual;
            minutosHoraReal=(h_actual*60-tiempoMargen)+m_actual;

            ruta.h_actual=((h_actual*60)+m_actual-tiempoMargen)/60;
            ruta.m_actual=((h_actual*60)+m_actual-tiempoMargen)%60;


        }else{

            //ruta.h_actual=h_nueva;
            //ruta.m_actual=m_nueva;
            minutosHoraReal=(h_nueva*60)+m_nueva-tiempoMargen;
            ruta.h_actual=((h_nueva*60)+m_nueva-tiempoMargen)/60;
            ruta.m_actual=((h_nueva*60)+m_nueva-tiempoMargen)%60;

        }
        ruta.fechaHoy=fechaHoy;

        //System.out.println("HORA EN RUTAS: "+ruta.h_actual+":"+ruta.m_actual);

        direccionOrigen=origen.getText().toString();
        direccionDestino=destino.getText().toString();

        if(direccionOrigen.equals("")) {

            panelInfo.setText(getString(R.string.intro_origen));
            //ponAlerta();

            enviar.setVisibility(View.VISIBLE);
            progresoEspera.setVisibility(View.GONE);

            return;
        }else{

            panelInfo.setText("");

        }

            if(direccionDestino.equals("")){

                panelInfo.setText(getString(R.string.intro_destino));
                //ponAlerta();

                enviar.setVisibility(View.VISIBLE);
                progresoEspera.setVisibility(View.GONE);

                return;
        }else{

                panelInfo.setText("");
            }

        enviar.setVisibility(View.GONE);
            progresoEspera.setVisibility(View.VISIBLE);

            // ------- quita el teclado al pulsar el boton buscar ----------------
            // InputMethodManager introduce = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            // introduce.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            //--------------------------------------------------------------

            EjecutaSegundoPlano tarea = new EjecutaSegundoPlano();
            tarea.execute();

    }

    private class EjecutaSegundoPlano extends AsyncTask <String,String,String> {


        @Override
        protected String doInBackground(String... strings) {  // TAREA EN SEGUNDO PLANO

            panelInfo.setText("");
            Location[][] daTodasRutas;
/*
            // comprobamos el estado del WIFI del movil:

            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo estadoRed = connectivityManager.getActiveNetworkInfo();

            if (estadoRed == null || !estadoRed.isConnected()) {

                return getString(R.string.error_red);

            }

 */

            if(puntoOrigen.getLatitude()==0.0 || puntoOrigen==null ||puntoDestino.getLatitude()==0.0 || puntoDestino==null){

                return "CERO";
            }

            if(puntoOrigen.getLatitude()==1.1 || puntoOrigen==null){

                return "UNO";
            }
            if(puntoDestino.getLatitude()==1.1 || puntoDestino==null){

                return "DOS";
            }

            // -------- GUARDA EL DESTINO EN LA BASE DATOS INTERNA ----------------------

            if(!puntoDestino.getProvider().equals(migps.getProvider())){

                guardaRegistro(puntoDestino.getProvider(),String.valueOf(puntoDestino.getLatitude()),String.valueOf(puntoDestino.getLongitude()));

            }

            //---------------------------------------------------------------------------

            daTodasRutas=ruta.BuscaLasLineas(puntoOrigen,puntoDestino,lineas);

            if(daTodasRutas==null || (daTodasRutas[0]==null && daTodasRutas[1]==null && daTodasRutas[3]==null && daTodasRutas[5]==null && daTodasRutas[7]==null)){

            //if(daTodasRutas==null || (daTodasRutas[0]==null && daTodasRutas[1]==null && daTodasRutas[3]==null)){
/*
                String minutos_prox;


                if(ruta.hora_proximobus_activo%60<10){

                    minutos_prox="0"+(ruta.hora_proximobus_activo%60);
                }else{

                    minutos_prox=""+ruta.hora_proximobus_activo%60;
                }
                mejor_fuerahora=(ruta.hora_proximobus_activo/60)+":"+minutos_prox;

                System.out.println(mejor_fuerahora+" "+ruta.hora_proximobus_activo+" "+ruta.linea_fuerahora_buena);
*/
                return "NOLINEA";

            }

            daRuta1A=null;
            daRuta1B=null;
            daRuta2A=null;
            daRuta2B=null;
            daRuta3A=null;
            daRuta3B=null;

            for(int i=0;i<7;i++){

                if(daTodasRutas[i]!=null){

                    if(i<2) {

                        if (daRuta1A == null) {

                            daRuta1A = daTodasRutas[i];
                            if(i==0){minutos1=ruta.mejorLineaUno.min_hastaBus;}
                            if(i==0){nombreRuta1A=ruta.mejorLineaUno.nombre;}
                            if(i==1){minutos1=ruta.mejorLineaDos.min_hastaBus;}
                            if(i==1){nombreRuta1A=ruta.mejorLineaDos.nombre;}

                        } else if (daRuta2A == null) {

                            daRuta2A = daTodasRutas[i];
                            minutos2=ruta.mejorLineaDos.min_hastaBus;
                            nombreRuta2A=ruta.mejorLineaDos.nombre;

                        }

                    }else

                    if(i!=3 && i!=5) {

                        if (daRuta1A == null) {

                            daRuta1A = daTodasRutas[i];
                            if(i==2){minutos1 = ruta.mejorLineaTresA.min_hastaBus;}
                            if(i==2){nombreRuta1A = ruta.mejorLineaTresA.nombre;}
                            if(i==4){minutos1 = ruta.mejorLineaCuatroA.min_hastaBus;}
                            if(i==4){nombreRuta1A = ruta.mejorLineaCuatroA.nombre;}
                            if(i==6){minutos1 = ruta.mejorLineaCincoA.min_hastaBus;}
                            if(i==6){nombreRuta1A = ruta.mejorLineaCincoA.nombre;}

                            daRuta1B = daTodasRutas[i + 1];
                            if(i==2){nombreRuta1B = ruta.mejorLineaTresB.nombre;}
                            if(i==2){ruta1Bsiguiente = ruta.tiempollegada1;}
                            if(i==4){nombreRuta1B = ruta.mejorLineaCuatroB.nombre;}
                            if(i==4){ruta1Bsiguiente = ruta.tiempollegada2;}
                            if(i==6){nombreRuta1B = ruta.mejorLineaCincoB.nombre;}
                            if(i==6){ruta1Bsiguiente = ruta.tiempollegada3;}

                        } else if (daRuta2A == null) {

                            daRuta2A = daTodasRutas[i];
                            if(i==2){minutos2 = ruta.mejorLineaTresA.min_hastaBus;}
                            if(i==2){nombreRuta2A = ruta.mejorLineaTresA.nombre;}
                            if(i==4){minutos2 = ruta.mejorLineaCuatroA.min_hastaBus;}
                            if(i==4){nombreRuta2A = ruta.mejorLineaCuatroA.nombre;}
                            if(i==6){minutos2 = ruta.mejorLineaCincoA.min_hastaBus;}
                            if(i==6){nombreRuta2A = ruta.mejorLineaCincoA.nombre;}

                            daRuta2B = daTodasRutas[i + 1];
                            if(i==2){nombreRuta2B = ruta.mejorLineaTresB.nombre;}
                            if(i==2){ruta2Bsiguiente = ruta.tiempollegada1;}
                            if(i==4){nombreRuta2B = ruta.mejorLineaCuatroB.nombre;}
                            if(i==4){ruta2Bsiguiente = ruta.tiempollegada2;}
                            if(i==6){nombreRuta2B = ruta.mejorLineaCincoB.nombre;}
                            if(i==6){ruta2Bsiguiente = ruta.tiempollegada3;}


                        } else if (daRuta3A == null) {

                            daRuta3A = daTodasRutas[i];
                            if(i==2){minutos3 = ruta.mejorLineaTresA.min_hastaBus;}
                            if(i==2){nombreRuta3A = ruta.mejorLineaTresA.nombre;}
                            if(i==4){minutos3 = ruta.mejorLineaCuatroA.min_hastaBus;}
                            if(i==4){nombreRuta3A = ruta.mejorLineaCuatroA.nombre;}
                            if(i==6){minutos3 = ruta.mejorLineaCincoA.min_hastaBus;}
                            if(i==6){nombreRuta3A = ruta.mejorLineaCincoA.nombre;}

                            daRuta3B = daTodasRutas[i + 1];
                            if(i==2){nombreRuta3B = ruta.mejorLineaTresB.nombre;}
                            if(i==2){ruta3Bsiguiente = ruta.tiempollegada1;}
                            if(i==4){nombreRuta3B = ruta.mejorLineaCuatroB.nombre;}
                            if(i==4){ruta3Bsiguiente = ruta.tiempollegada2;}
                            if(i==6){nombreRuta3B = ruta.mejorLineaCincoB.nombre;}
                            if(i==6){ruta3Bsiguiente = ruta.tiempollegada3;}

                        }

                    }

                }

            }

            return "OK";

        }


        protected void onPostExecute(String resultado) {



            if(resultado.equals("CERO")){

                panelInfo.setText(getString(R.string.buscando_gps));
                //ponAlerta();
                enviar.setVisibility(View.VISIBLE);
                progresoEspera.setVisibility(View.GONE);
                return;

            }
            if(resultado.equals("UNO")){

                panelInfo.setText(getString(R.string.error_origen));
                //ponAlerta();
                enviar.setVisibility(View.VISIBLE);
                progresoEspera.setVisibility(View.GONE);
                return;

            }
            if(resultado.equals("DOS")){

                panelInfo.setText(getString(R.string.error_destino));
               //ponAlerta();
                enviar.setVisibility(View.VISIBLE);
                progresoEspera.setVisibility(View.GONE);
                return;

            }

            if(resultado.equals("NOLINEA")){
/*
                Spanned mensaje; //getString(R.string.sinLinea)+"/n Proxima linea activa "+ruta.linea_fuerahora_buena.nombre+" pasa a las "+mejor_fuerahora;


                mensaje= (Html.fromHtml("<u>"+getString(R.string.sinLinea) +"</u><br><br>"
                        +getString(R.string.proximalinea)+" "+ruta.linea_fuerahora_buena.nombre+ "<br><br>"
                        +"<b>"+getString(R.string.pasaalas)+" "+mejor_fuerahora+"</b>"));

 */

                String mensaje=getString(R.string.nohaylinea);
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());

                builder.setMessage(mensaje)
                        .setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                builder.show();

                enviar.setVisibility(View.VISIBLE);
                progresoEspera.setVisibility(View.GONE);
                return;

            }

            // activamos el boton enviar:
            enviar.setVisibility(View.VISIBLE);
            progresoEspera.setVisibility(View.GONE);

            Log.i("Nota: ","Mi GPS: "+migps);

            muestraRuta();

        }
    }

    private void muestraRuta(){

        Intent miIntent=new Intent(getActivity(), Rutas.class);

        miIntent.putExtra("TODASLINEAS", lineas);

        miIntent.putExtra("RUTA1A", daRuta1A);
        miIntent.putExtra("NOMBRE1A", nombreRuta1A);
        miIntent.putExtra("RUTA1B", daRuta1B);
        miIntent.putExtra("NOMBRE1B", nombreRuta1B);

       // miIntent.putExtra("RUTA1", daRuta1);
       // miIntent.putExtra("NOMBRE1", nombreRuta1);

        miIntent.putExtra("RUTA2A", daRuta2A);
        miIntent.putExtra("NOMBRE2A", nombreRuta2A);
        miIntent.putExtra("RUTA2B", daRuta2B);
        miIntent.putExtra("NOMBRE2B", nombreRuta2B);

        miIntent.putExtra("RUTA3A", daRuta3A);
        miIntent.putExtra("NOMBRE3A", nombreRuta3A);
        miIntent.putExtra("RUTA3B", daRuta3B);
        miIntent.putExtra("NOMBRE3B", nombreRuta3B);

        miIntent.putExtra("MINUTOS1", minutos1);
        miIntent.putExtra("MINUTOS2", minutos2);
        miIntent.putExtra("MINUTOS3", minutos3);

        miIntent.putExtra("NOMBRE4", nombreRuta4);
        miIntent.putExtra("MINUTOS4", minutos4);

        miIntent.putExtra("LINEA1BSIGUIENTE", ruta1Bsiguiente); // ---------

        miIntent.putExtra("LINEA2BSIGUIENTE", ruta2Bsiguiente);
        miIntent.putExtra("LINEA3BSIGUIENTE", ruta3Bsiguiente);

        miIntent.putExtra("MINUTOSHORAREAL", minutosHoraReal);


        startActivity(miIntent);

    }

    private void busca_origen(){

        esperando();

        //String direccion="calle "+direccionOrigen+" Logroño";
        String direccion=direccionOrigen;

        try {
            url="https://www.google.es/maps/search/"+ URLEncoder.encode(direccion, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // TODO esto quitado
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setLoadsImagesAutomatically(false);
        //browser.getSettings().setDomStorageEnabled(true);

        //browser.setLayerType(View.LAYER_TYPE_NONE, null);


        //browser.setWebViewClient(new MyWebViewClient());
        //browser.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        browser.setWebViewClient(new WebViewClient()
        {
            int cont=1;

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);


            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);



                if (cont == 1) {
                    cont = 2;
                } else if (cont == 2) {

                    if (url.contains("/data")) {
                        //browser.loadUrl("");

                        StringBuilder lat = new StringBuilder();
                        StringBuilder lon = new StringBuilder();
                        StringBuilder dire = new StringBuilder();
                        char[] caracteres = url.toCharArray();

                        for (int i = 32; caracteres[i+1] != '/'; i++) { // guarda la direccion

                                    dire.append(caracteres[i+1]);

                        }

                        for (int i = 0; i < caracteres.length; i++) {
                            if (caracteres[i] == '@') {

                                for (int e = 0;caracteres[i + 1] != ','; e++) {
                                    lat.append(caracteres[i + 1]);

                                    i++;

                                }

                            }

                        }
                        for (int i = 0; i < caracteres.length; i++) {
                            if (caracteres[i] == '-') {

                                for (int e = 0;caracteres[i] != ','; e++) {

                                    lon.append(caracteres[i]);
                                    i++;

                                }

                                break;

                            }

                        }
                        cont=3;

                        String dire_final="";

                        try {
                            dire_final=URLDecoder.decode(dire.toString(), "UTF-8");

                            if(dire_final.contains("La Rioja")){

                                dire.setLength(0);

                                caracteres=dire_final.toCharArray();

                                for(int i=0;i<dire_final.length()-10;i++){

                                    dire.append(caracteres[i]);

                                }

                                dire_final=dire.toString();

                            }

                            if(dire_final.matches(".*[^0-9][0-9]{5}[^0-9].*")) {

                                String dire_sinCP = "";
                                contador = 0;

                                dire.setLength(0);
                                caracteres = dire_final.toCharArray();

                                for (int i = caracteres.length - 1; i > -1; i--) {

                                    if (caracteres[i] != '0'
                                            && caracteres[i] != '1'
                                            && caracteres[i] != '2'
                                            && caracteres[i] != '3'
                                            && caracteres[i] != '4'
                                            && caracteres[i] != '5'
                                            && caracteres[i] != '6'
                                            && caracteres[i] != '7'
                                            && caracteres[i] != '8'
                                            && caracteres[i] != '9'
                                    ){

                                        if(contador==0 || contador==6){dire_sinCP = caracteres[i] + dire_sinCP;}else{contador++;}

                                    }else if (contador < 6) {
                                        contador++;
                                    }else{dire_sinCP = caracteres[i] + dire_sinCP;}

                                }

                                dire_final=dire_sinCP;
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                       // System.out.println("DIRECCION: " + dire_final);
                        //puntoOrigen=new Location(direccionOrigen);

                        enviar.setText(R.string.buscar);
                        enviar.setEnabled(true);
                        puntoOrigen = new Location(dire_final);
                        origen.setText(dire_final);
                        try {
                            puntoOrigen.setLatitude(Double.parseDouble(lat.toString()));
                            puntoOrigen.setLongitude(Double.parseDouble(lon.toString()));
                        } catch (Exception e) {
                            puntoOrigen.setLatitude(1.1);
                        }
                        browser.loadUrl("about:blank");
                        browser.getSettings().setJavaScriptEnabled(false);
                        esperaAlerta.cancel();

                        guardaRegistro(dire_final,lat.toString(),lon.toString());

                    }else{ // no encuentra la direccion
                        cont=3;
                        enviar.setText(R.string.buscar);
                        enviar.setEnabled(true);
                        puntoOrigen = new Location(direccionOrigen);
                        puntoOrigen.setLatitude(1.1);
                        browser.loadUrl("about:blank");
                        browser.getSettings().setJavaScriptEnabled(false);
                        origen.setText(null);
                        esperaAlerta.cancel();
                        noExiste();

                    }

                }
            }

        });

        browser.loadUrl(url);

    }

    private void busca_destino(){

        esperando();

        //origen.setEnabled(false);

        //String direccion="calle "+direccionOrigen+" Logroño";
        String direccion=direccionDestino;

        try {
            url="https://www.google.es/maps/search/"+ URLEncoder.encode(direccion, "UTF-8");
            //url="https://www.20minutos.es";

            System.out.println(URLEncoder.encode(direccion, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("VOY A BUSCAR: " + url);

        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setLoadsImagesAutomatically(false);

        contadorCargas=0;

        browser.setWebViewClient(new WebViewClient()
        {

            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("CLICK_ON_URL");

                browser.setVisibility(View.GONE);
                pulsaAceptar.setVisibility(View.GONE);

                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                System.out.println("URL RESPUESTA: " + url);

                if(!url.contains("consent")) {

                    //CookieManager.getInstance().removeAllCookie();
                }

                if(url.contains("consent")){

                    browser.setVisibility(View.VISIBLE);
                    pulsaAceptar.setVisibility(View.VISIBLE);

                }else{

                    contadorCargas++;

                }

                System.out.println("CONTADOR: "+contadorCargas);

               if(contadorCargas==2){

                    System.out.println("ENTRA EN 2");

                    if (url.contains("/data")) {

                        System.out.println("CONTENIDO: " + url);

                        StringBuilder lat = new StringBuilder();
                        StringBuilder lon = new StringBuilder();
                        StringBuilder dire = new StringBuilder();
                        char[] caracteres = url.toCharArray();

                        for (int i = 32; caracteres[i+1] != '/'; i++) { // guarda la direccion

                            dire.append(caracteres[i+1]);

                        }

                        for (int i = 0; i < caracteres.length; i++) {

                            if (caracteres[i] == '@') {

                                for (int e = 0;caracteres[i + 1] != ','; e++) {

                                    //if(caracteres[i+1]!=','&&caracteres[i+1]!='-'){lat.append(caracteres[i+1]);}
                                    lat.append(caracteres[i + 1]);

                                    i++;

                                }

                            }

                        }

                        for (int i = 0; i < caracteres.length; i++) {
                            if (caracteres[i] == '-') {

                                for (int e = 0;caracteres[i] != ','; e++) {

                                    System.out.println("Longitud: "+caracteres.length+" Contiene: " + caracteres[i]);

                                    //if(caracteres[i]!=','){lon.append(caracteres[i]);}
                                    lon.append(caracteres[i]);
                                    i++;

                                }

                                break;

                            }

                        }

                        String dire_final="";

                        try {
                            dire_final=URLDecoder.decode(dire.toString(), "UTF-8");

                            if(dire_final.contains("La Rioja")){

                                dire.setLength(0);

                                caracteres=dire_final.toCharArray();

                                for(int i=0;i<dire_final.length()-10;i++){

                                    dire.append(caracteres[i]);

                                }

                                dire_final=dire.toString();

                            }

                            if(dire_final.matches(".*[^0-9][0-9]{5}[^0-9].*")) {

                                String dire_sinCP = "";
                                contador = 0;

                                dire.setLength(0);
                                caracteres = dire_final.toCharArray();

                                for (int i = caracteres.length - 1; i > -1; i--) {

                                    if (caracteres[i] != '0'
                                            && caracteres[i] != '1'
                                            && caracteres[i] != '2'
                                            && caracteres[i] != '3'
                                            && caracteres[i] != '4'
                                            && caracteres[i] != '5'
                                            && caracteres[i] != '6'
                                            && caracteres[i] != '7'
                                            && caracteres[i] != '8'
                                            && caracteres[i] != '9'
                                    ){

                                        if(contador==0 || contador==6){dire_sinCP = caracteres[i] + dire_sinCP;}else{contador++;}

                                    }else if (contador < 6) {
                                        contador++;
                                    }else{dire_sinCP = caracteres[i] + dire_sinCP;}

                                }
                                //System.out.println("DIRECCION SIN CP: " + dire_sinCP);
                                dire_final=dire_sinCP;
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        //Log.i("NOTA", "LOCALIZACION: " + lat.toString() + " " + lon.toString());
                        //puntoOrigen=new Location(direccionOrigen);

                        enviar.setText(R.string.buscar);
                        enviar.setEnabled(true);
                        puntoDestino = new Location(dire_final);
                        destino.setText(dire_final);
                        try {
                            puntoDestino.setLatitude(Double.valueOf(lat.toString()));
                            puntoDestino.setLongitude(Double.valueOf(lon.toString()));
                        } catch (Exception e) {
                            puntoDestino.setLatitude(1.1);
                        }
                        browser.loadUrl("about:blank");
                        browser.getSettings().setJavaScriptEnabled(false);
                        //origen.setEnabled(true);
                        esperaAlerta.cancel();

                        // ----------- Guarda la dirección si ha sido válida -----------

                        guardaRegistro(dire_final,lat.toString(),lon.toString());



                    }else{  // no encuentra la direccion

                        enviar.setText(R.string.buscar);
                        enviar.setEnabled(true);
                        puntoDestino = new Location(direccionDestino);
                        puntoDestino.setLatitude(1.1);
                        browser.loadUrl("about:blank");
                        browser.getSettings().setJavaScriptEnabled(false);
                        //origen.setEnabled(true);
                        destino.setText(null);
                        esperaAlerta.cancel();
                        noExiste();
                    }

                }
            }

        });

        browser.loadUrl(url);

    }

    private void cambiaDia(){

        final View alertLayout = inflador.inflate(R.layout.cambia_dia, null);

        final TextView eligeDia = alertLayout.findViewById(R.id.elige_dia);
        final RadioGroup quedia=alertLayout.findViewById(R.id.quedia);
        RadioButton dia_elegido;

        eligeDia.setText(getString(R.string.cambiadia));

        if(fechaHoy>0 && fechaHoy<5){

            quedia.check(R.id.laboral);

        }else if(fechaHoy==5){

            quedia.check(R.id.viernes);

        }else if(fechaHoy==6){

            quedia.check(R.id.sabado);

        }else if(fechaHoy==0){

            quedia.check(R.id.festivo);
        }

        Button cambiar=alertLayout.findViewById(R.id.aceptar_dia);
        Button hoy=alertLayout.findViewById(R.id.cancelar_dia);


        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("");
        alert.setView(alertLayout);
        alert.setCancelable(false);


        final AlertDialog dialog = alert.create();

        hoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fechaHoy=cal.get(Calendar.DAY_OF_WEEK)-1;

                ponElDia(getString(R.string.hoy_es));

                dia_cambiado=false;

                dialog.cancel();

            }
        });

        cambiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton dia_elegido = alertLayout.findViewById(quedia.getCheckedRadioButtonId());

                if(dia_elegido.getText().equals(getString(R.string.laborable))){

                    fechaHoy=1;

                }else if(dia_elegido.getText().equals(getString(R.string.viernes))){

                    fechaHoy=5;

                }else if(dia_elegido.getText().equals(getString(R.string.sabado))){

                    fechaHoy=6;

                }else if(dia_elegido.getText().equals(getString(R.string.festivo))){

                    fechaHoy=0;

                }
                ponElDia(getString(R.string.buscar_en));

                System.out.println(dia_elegido);

                dia_cambiado=true;

                dialog.cancel();


            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(200,400);
        dialog.show();
    }

    private void ponElDia(String nota){

        if(fechaHoy>0 && fechaHoy<5){

            diahoy.setText(nota+" "+getString(R.string.laborable));

        }else if(fechaHoy==5){

            diahoy.setText(nota+" "+getString(R.string.viernes));

        }else if(fechaHoy==6){

            diahoy.setText(nota+" "+getString(R.string.sabado));

        }else if(fechaHoy==0){

            diahoy.setText(nota+" "+getString(R.string.festivo));
        }


    }

    private void entradaDatosOrigen(){

            //LayoutInflater inflater = getLayoutInflater();
            final View alertLayout = inflador.inflate(R.layout.entrada_datos, null);

            final EditText desde = alertLayout.findViewById(R.id.et_username);
            final EditText numero = alertLayout.findViewById(R.id.numero);
            final RadioGroup tipovia=alertLayout.findViewById(R.id.tipovia);
            final RadioGroup ciudad=alertLayout.findViewById(R.id.ciudad);
            final StringBuilder dirtotal=new StringBuilder();

        if(!compruebaRed()){

            desde.setHint(getString(R.string.sin_internet));
            numero.setHint(getString(R.string.elija_donde));

            desde.setEnabled(false);
            numero.setEnabled(false);
            tipovia.setEnabled(false);
            ciudad.setEnabled(false);

        }else{

            desde.setEnabled(true);
            numero.setEnabled(true);
            tipovia.setEnabled(true);
            ciudad.setEnabled(true);
        }

            //final Switch gps=alertLayout.findViewById(R.id.gps);

        SQLiteDatabase db = helper.getReadableDatabase();

        Button aceptar=alertLayout.findViewById(R.id.aceptar_datos);
        Button cancelar=alertLayout.findViewById(R.id.cancelar_datos);
        Button gps=alertLayout.findViewById(R.id.gps);

        TextView info_rutas=alertLayout.findViewById(R.id.info_rutas);

        if(puntoDestino!=null && puntoDestino.getProvider().equals(miubi)){
            gps.setEnabled(false);
            gps.setAlpha(0.5f);
        }

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("");
            // this is set the view from XML inside AlertDialog
            alert.setView(alertLayout);
            // disallow cancel of AlertDialog on click of back button and outside touch
            alert.setCancelable(true);

        rutaContenedor = alertLayout.findViewById(R.id.pantalla_entradaDatos);

            final AlertDialog dialog = alert.create();

        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                dialog.cancel();

            }
        });

        try {  // Ponemos un trycatch por si el registro buscado no existiera y no nos de error

            Cursor cursor = db.query(
                    MiBaseDatos.TABLE_NAME,   // Tabla a consultar
                    null,             // array con las columnas a devolver creado antes
                    null,              // el criterio WHERE
                    null,          // argumentos del criterio
                    null,                   // agrupar o no los registros
                    null,                   // filtrar o no por columnas
                    null               // ordenamiento (sortOrder)
            );

            if (cursor.getCount() > 0) {

                info_rutas.setText(getString(R.string.busqanteriores));

                for (int i = cursor.getCount()-1;i>-1; i--) {

                    cursor.moveToPosition(i);

                    final String queBorro=cursor.getString(1);

                    final LinearLayout rutasAnteriores = (LinearLayout) inflador.inflate(R.layout.barra_rutasvistas, null);

                    final TextView textoRuta = rutasAnteriores.findViewById(R.id.nombre_ruta);
                    final ImageView borraRuta = rutasAnteriores.findViewById(R.id.borra_ruta);

                    ((TextView) rutasAnteriores.findViewById(R.id.nombre_ruta)).setText(cursor.getString(1));
                    rutaContenedor.addView(rutasAnteriores);

                    //System.out.println("NUMERO DE CONTENEDOR "+ rutaContenedor.getChildCount());

                    borraRuta.setId(rutaContenedor.getChildCount()-1);

                    final String desde_sitio = cursor.getString(1);
                    final double desde_lat = Double.valueOf(cursor.getString(2));
                    final double desde_lon = Double.valueOf(cursor.getString(3));

                    borraRuta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            rutaContenedor.removeViewAt(borraRuta.getId());

                            int cont=0;
                            for(int e=0;e<rutaContenedor.getChildCount()+1;e++){


                                if(rutaContenedor.findViewById(e)!=null) {
                                    rutaContenedor.findViewById(e).setId(cont);
                                    cont++;
                                }

                            }

                            borraRegistro(queBorro);

                        }
                    });

                    textoRuta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //desde.setText(desde_sitio);

                            puntoOrigen = new Location(desde_sitio);
                            origen.setText(desde_sitio);
                            puntoOrigen.setLatitude(desde_lat);
                            puntoOrigen.setLongitude(desde_lon);

                            dialog.cancel();

                        }
                    });
                }
            }

        }catch (Exception e){

            Toast.makeText(getActivity().getApplicationContext(),"Error 1",Toast.LENGTH_SHORT).show();

        }

        db.close();

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                origen.setText(miubi);

                puntoOrigen=new Location(miubi);

                dialog.cancel();

            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (desde.getText().toString().equals("")) {

                        mensajeAlerta=getString(R.string.intro_origen);
                        ponAlerta();
                    } else {
                        RadioButton ciudad_ok = alertLayout.findViewById(ciudad.getCheckedRadioButtonId());
                        RadioButton tipovia_ok = alertLayout.findViewById(tipovia.getCheckedRadioButtonId());

                        dirtotal.append(tipovia_ok.getText());
                        dirtotal.append(" ");
                        dirtotal.append(desde.getText().toString());
                        dirtotal.append(", ");
                        dirtotal.append(numero.getText().toString());
                        dirtotal.append(" ");
                        dirtotal.append(ciudad_ok.getText().toString());
                        //destino.setText(dirtotal.toString());
                        direccionOrigen = dirtotal.toString();

                        //enviar.setEnabled(false);

                        busca_origen();

                        dialog.cancel();
                    }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void entradaDatosDestino(){

        Button aceptar, cancelar,gps;

        InputMethodManager introduce = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        introduce.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.SHOW_FORCED);

        irA=null;

        LayoutInflater inflater = this.getLayoutInflater();

        final View alertLayout = inflador.inflate(R.layout.entrada_datos, null);
        final EditText desde = alertLayout.findViewById(R.id.et_username);
        final EditText numero = alertLayout.findViewById(R.id.numero);
        final RadioGroup tipovia=alertLayout.findViewById(R.id.tipovia);
        final RadioGroup ciudad=alertLayout.findViewById(R.id.ciudad);
        final StringBuilder dirtotal=new StringBuilder();

        if(!compruebaRed()){

            desde.setHint(getString(R.string.sin_internet));
            numero.setHint(getString(R.string.elija_donde));

            desde.setEnabled(false);
            numero.setEnabled(false);
            tipovia.setEnabled(false);
            ciudad.setEnabled(false);

        }else{

            desde.setEnabled(true);
            numero.setEnabled(true);
            tipovia.setEnabled(true);
            ciudad.setEnabled(true);
        }

        rutaContenedor = alertLayout.findViewById(R.id.pantalla_entradaDatos);

        SQLiteDatabase db = helper.getReadableDatabase(); // Hace que la BBDD sea de lectura

        // El valor que queremos buscar con WHERE
        String selection = MiBaseDatos.NOMBRE_COLUMNA1 + " = *";
        String[] selectionArgs = {};  // metemos (convertido a String) el contenido de



        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);

        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);

        gps=alertLayout.findViewById(R.id.gps);
        aceptar=alertLayout.findViewById(R.id.aceptar_datos);
        cancelar=alertLayout.findViewById(R.id.cancelar_datos);
        TextView info_rutas=alertLayout.findViewById(R.id.info_rutas);

        if(puntoOrigen!=null && puntoOrigen.getProvider().equals(miubi)){
            gps.setEnabled(false);
            gps.setAlpha(0.5f);
        }

        final AlertDialog dialog = alert.create();

        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                dialog.cancel();

            }
        });

        try {  // Ponemos un trycatch por si el registro buscado no existiera y no nos de error

            Cursor cursor = db.query(
                    MiBaseDatos.TABLE_NAME,   // Tabla a consultar
                    null,             // array con las columnas a devolver creado antes
                    null,              // el criterio WHERE
                    null,          // argumentos del criterio
                    null,                   // agrupar o no los registros
                    null,                   // filtrar o no por columnas
                    null               // ordenamiento (sortOrder)
            );

            //System.out.println("CURSOR: "+cursor.getCount());

            if (cursor.getCount() > 0) {

            info_rutas.setText(getString(R.string.busqanteriores));

            for (int i = cursor.getCount()-1;i>-1; i--) {

                cursor.moveToPosition(i);

                final String queBorro=cursor.getString(1);
                final LinearLayout rutasAnteriores = (LinearLayout) inflador.inflate(R.layout.barra_rutasvistas, null);
                final TextView textoRuta = rutasAnteriores.findViewById(R.id.nombre_ruta);
                final ImageView borraRuta = rutasAnteriores.findViewById(R.id.borra_ruta);

                ((TextView) rutasAnteriores.findViewById(R.id.nombre_ruta)).setText(cursor.getString(1));
                rutaContenedor.addView(rutasAnteriores);

                borraRuta.setId(rutaContenedor.getChildCount()-1);

                final String desde_sitio = cursor.getString(1);
                final double desde_lat = Double.valueOf(cursor.getString(2));
                final double desde_lon = Double.valueOf(cursor.getString(3));

                borraRuta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        rutaContenedor.removeViewAt(borraRuta.getId());

                        int cont=0;
                        for(int e=0;e<rutaContenedor.getChildCount()+1;e++){


                            if(rutaContenedor.findViewById(e)!=null) {
                                rutaContenedor.findViewById(e).setId(cont);
                                cont++;
                            }

                        }

                        borraRegistro(queBorro);

                    }
                });

                textoRuta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //desde.setText(desde_sitio);

                        puntoDestino = new Location(desde_sitio);
                        destino.setText(desde_sitio);
                        puntoDestino.setLatitude(desde_lat);
                        puntoDestino.setLongitude(desde_lon);

                        dialog.cancel();

                    }
                });
            }
        }


        }catch (Exception e){

            Toast.makeText(getActivity().getApplicationContext(),"No existe el registro",Toast.LENGTH_SHORT).show();

        }
        db.close();

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                destino.setText(miubi);

                puntoDestino=new Location(miubi);

                dialog.cancel();

            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (desde.getText().toString().equals("")) {

                        mensajeAlerta=getString(R.string.intro_destino);
                        ponAlerta();
                    } else {
                        RadioButton ciudad_ok = alertLayout.findViewById(ciudad.getCheckedRadioButtonId());
                        RadioButton tipovia_ok = alertLayout.findViewById(tipovia.getCheckedRadioButtonId());

                        dirtotal.append(tipovia_ok.getText());
                        dirtotal.append(" ");
                        dirtotal.append(desde.getText().toString());
                        dirtotal.append(", ");
                        dirtotal.append(numero.getText().toString());
                        dirtotal.append(" ");
                        dirtotal.append(ciudad_ok.getText().toString());
                        //destino.setText(dirtotal.toString());
                        direccionDestino = dirtotal.toString();

                        //enviar.setEnabled(false);

                        busca_destino();

                        dialog.cancel();
                    }
                }

        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void cambiarHora(){

        cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));

        h_actual = cal.get(Calendar.HOUR_OF_DAY);
        m_actual = cal.get(Calendar.MINUTE);

        InputMethodManager introduce = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        introduce.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), InputMethodManager.SHOW_FORCED);

        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.cambiar_hora, null);

        int lahora=0;
        int elminuto=0;

        TextView sumahora=alertLayout.findViewById(R.id.hora_mas);
        final TextView horacambiada=alertLayout.findViewById(R.id.horaCambiar);
        TextView restahora=alertLayout.findViewById(R.id.hora_menos);
        TextView sumaminuto=alertLayout.findViewById(R.id.min_mas);
        final TextView minutocambiado=alertLayout.findViewById(R.id.minCambiar);
        TextView restaminuto=alertLayout.findViewById(R.id.min_menos);

        if(!hora_cambiada) {
            h_nueva = h_actual;
            m_nueva = m_actual;
        }
        horacambiada.setText(""+h_nueva);
        if(m_nueva<10){
            minutocambiado.setText("0" + m_nueva);
        }else {
            minutocambiado.setText("" + m_nueva);
        }

        sumahora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(h_nueva<23){h_nueva++;}else{h_nueva=0;}
                horacambiada.setText(""+h_nueva);
            }
        });
        restahora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(h_nueva>0){h_nueva--;}else{h_nueva=23;}
                horacambiada.setText(""+h_nueva);
            }
        });
        sumaminuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_nueva<59){m_nueva++;}else{m_nueva=0;}
                if(m_nueva<10){
                    minutocambiado.setText("0" + m_nueva);
                }else {
                    minutocambiado.setText("" + m_nueva);
                }
            }
        });
        restaminuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_nueva>0){m_nueva--;}else{m_nueva=59;}
                if(m_nueva<10){
                    minutocambiado.setText("0" + m_nueva);
                }else {
                    minutocambiado.setText("" + m_nueva);
                }
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        alert.setNegativeButton(R.string.horaActual, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //ruta.h_actual=h_actual;
                //ruta.m_actual=m_actual;

                hora_cambiada=(false);
                horaActual.setText(getString(R.string.salirahora));

            }
        });

        alert.setPositiveButton(R.string.cambiar, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                String min;
                if(m_nueva<10){
                    min="0"+m_nueva;
                }else{
                    min=Integer.toString(m_nueva);
                }

                horaActual.setText(getString(R.string.saliralas)+" "+h_nueva+":"+min);
                hora_cambiada=(true);
                ruta.m_actual=m_nueva;
                ruta.h_actual=h_nueva;

            }
        });

        AlertDialog dialog = alert.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);

    }

    private void esperando(){

        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.espera, null);

        browser=alertLayout.findViewById(R.id.miweb);

        browser.setVisibility(View.GONE);

        pulsaAceptar=alertLayout.findViewById(R.id.pulsa_aceptar);

        pulsaAceptar.setVisibility(View.GONE);

        AlertDialog.Builder alertaEspera = new AlertDialog.Builder(getActivity());

        alertaEspera.setTitle("");
        // this is set the view from XML inside AlertDialog
        alertaEspera.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch

        alertaEspera.setCancelable(false);

        esperaAlerta = alertaEspera.create();

        esperaAlerta.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        esperaAlerta.show();

    }

    private void noExiste() {

        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.noexiste, null);

        AlertDialog.Builder alertaNoexiste = new AlertDialog.Builder(getActivity());
        alertaNoexiste.setTitle("");
        // this is set the view from XML inside AlertDialog
        alertaNoexiste.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alertaNoexiste.setCancelable(false);
        alertaNoexiste.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        esperaAlerta = alertaNoexiste.create();
        esperaAlerta.show();

    }

    private void borraRegistro(String nombre){

        SQLiteDatabase db = helper.getWritableDatabase();

        String selection = MiBaseDatos.NOMBRE_COLUMNA2 + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {nombre};
        // Issue SQL statement.
        db.delete(MiBaseDatos.TABLE_NAME, selection, selectionArgs);

       //System.out.println("REGISTRO BORRADO");

       db.close();

    }

    public void actualizaRegistros(int id,String nombre,String lat, String lon){

        SQLiteDatabase db = helper.getWritableDatabase();


        // Nuevo valor de la(s) columna(s)
        ContentValues values = new ContentValues();
        values.put(MiBaseDatos.NOMBRE_COLUMNA2, nombre);
        values.put(MiBaseDatos.NOMBRE_COLUMNA3, lat);
        values.put(MiBaseDatos.NOMBRE_COLUMNA4, lon);

        // Columna a buscar donde hacer los cambios
        String selection = MiBaseDatos.NOMBRE_COLUMNA1 + " LIKE ?";
        String[] selectionArgs = {""+id};

        int count = db.update(
                MiBaseDatos.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        //System.out.println("DATOS ACTUALIZADOS: "+count);

        db.close();

    }

    private void insertarRegistros(String nombre,String lat, String lon){

        SQLiteDatabase db = helper.getWritableDatabase();

        // Metemos los valores de los edittext en los valores de la BBDD
        ContentValues values = new ContentValues();
        values.put(MiBaseDatos.NOMBRE_COLUMNA2, nombre);
        values.put(MiBaseDatos.NOMBRE_COLUMNA3, lat);
        values.put(MiBaseDatos.NOMBRE_COLUMNA4, lon);

// Insert the new row, returning the primary key value of the new row
        long datos=db.insert(MiBaseDatos.TABLE_NAME, null, values);

        //System.out.println("REGISTRO INSERTADO");

        db.close();

    }

    private void guardaRegistro(String nombre, String lat, String lon){

        if(buscaRegistro(nombre)) {

            SQLiteDatabase db = helper.getReadableDatabase();


            try {  // Ponemos un trycatch por si el registro buscado no existiera y no nos de error

                Cursor cursor = db.query(
                        MiBaseDatos.TABLE_NAME,   // Tabla a consultar
                        null,             // array con las columnas a devolver creado antes
                        null,              // el criterio WHERE
                        null,          // argumentos del criterio
                        null,                   // agrupar o no los registros
                        null,                   // filtrar o no por columnas
                        null               // ordenamiento (sortOrder)
                );

                if (cursor.getCount() > 0) {

                    cursor.moveToPosition(cursor.getCount());
                    insertarRegistros(nombre, lat, lon);
                } else {

                    insertarRegistros(nombre, lat, lon);
                }


            } catch (Exception e) {

                Toast.makeText(getActivity().getApplicationContext(), "Error 1", Toast.LENGTH_SHORT).show();

            }

            db.close();

        }


    }

    private boolean buscaRegistro(String nombre){

       SQLiteDatabase db = helper.getReadableDatabase(); // Hace que la BBDD sea de lectura

       String[] projection = {  // dice qué columnas nos debe devolver la consulta
               // (no ponemos la primera porque es la que usaremos para buscar)
               //MiBaseDatos.NOMBRE_COLUMNA2,
               //MiBaseDatos.NOMBRE_COLUMNA3
       };

       // El valor que queremos buscar con WHERE
       String selection = MiBaseDatos.NOMBRE_COLUMNA2 + " = ?";
       String[] selectionArgs = {nombre};  // metemos (convertido a String) el contenido de

       try {  // Ponemos un trycatch por si el registro buscado no existiera y no nos de error

           Cursor cursor = db.query(
                   MiBaseDatos.TABLE_NAME,   // Tabla a consultar
                   projection,             // array con las columnas a devolver creado antes
                   selection,              // el criterio WHERE
                   selectionArgs,          // argumentos del criterio
                   null,                   // agrupar o no los registros
                   null,                   // filtrar o no por columnas
                   null               // ordenamiento (sortOrder)
           );

           cursor.moveToFirst();

           if(cursor.getCount()==0){

               //System.out.println("NO EXISTE ESTE NOMBRE: "+cursor.getCount()+" "+nombre);
               db.close();
               return true;
           }
           //System.out.println("SI EXISTE ESTE NOMBRE");
           // Borra el registro para que lo guarde luego en la primera posicion

           selection = MiBaseDatos.NOMBRE_COLUMNA2 + " LIKE ?";
           // Specify arguments in placeholder order.
           // Issue SQL statement.
           db.delete(MiBaseDatos.TABLE_NAME, selection, selectionArgs);

           db.close();
           return true;


       }catch (Exception e){

          //System.out.println("NO EXISTE ESTE NOMBRE - ERROR");
           db.close();
          return true;

       }



   }

   private boolean compruebaRed(){

       ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo estadoRed = connectivityManager.getActiveNetworkInfo();

       if (estadoRed == null || !estadoRed.isConnected()) {

           return false;

       }else{

           return true;
       }

   }

   private boolean compruebaGps(){
       LocationManager lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
       if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){

           return false;

       }else{

           return true;
       }

   }

    public void ponAlerta(){

    try{

        if(!toast1.getView().isShown()) {

            toast1.setText(mensajeAlerta);

            toast1.setGravity(Gravity.CENTER, 0, 0);
            TextView mensaje = toast1.getView().findViewById(android.R.id.message);
            mensaje.setGravity(Gravity.CENTER);

            toast1.show();

        }

    }catch (Exception e){

    toast1.setText(mensajeAlerta);

    toast1.show();
    }

    }

}

