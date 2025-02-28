package com.saraodigital.takemitu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

// ---------------- PINTA LAS ESTACIONES EN LA RUTA ----------------------------------
// -----------------------------------------------------------------------------------

public class Rutas extends AppCompatActivity {

    // ------------------------------- VARIABLES MODIFICABLES -------------------------
    double tiempoEntreEstaciones=1.35;
    int minespera=21; // minutos de espera para las lineas 6 y 7
    int maximoAndar=500;  // metros maximos a andar de origen a bus y bus a destino
    int maximoAndarTotal=1000;
    int maximoAndarenUno=400;
    int maximoAndarTransbordo = 150;
    int minutos_cortesia_inicio=20;
    // --- Horas de inicio de los buses en minutos:
    int inicio_laboral=415-minutos_cortesia_inicio;
    int inicio_sabado=430-minutos_cortesia_inicio;
    int inicio_festivo=435-minutos_cortesia_inicio;
    int minparadas=2;



    // ------------------------------- VARIABLES MODIFICABLES -------------------------

    int horaenmin;
    int horaenmin_comparar;

    public String nombreLinea = null;
    public String sentido = "";
    public String linea;
    private String nombrerutauno,nombrerutaunoA,nombrerutaunoB,nombrerutadosA,nombrerutadosB,nombrerutatresA,nombrerutatresB,nombrerutacuatro;

    private ScrollView scroll;

    public SentidoParadas dimeNombres;
    private int rutaseleccionada;

    private Button botonruta1,botonruta2,botonruta3;

    private Parcelable[] todasLineasParcel,ruta1,ruta1A,ruta1B,ruta2A,ruta2B,ruta3A,ruta3B;
    private int minutos1,minutos2,minutos3,minutos4,minutosHoraReal;
    private int intervalo1, intervalo2, intervalo3, intervalo4;
    private int ruta1Bsiguiente,ruta2Bsiguiente,ruta3Bsiguiente;

    private LinearLayout rutaContenedor;
    private LayoutInflater inflador;

    public Lineas mejorLineaUno,mejorLineaDos,mejorLineaDosA,mejorLineaDosB,mejorLineaTresA,mejorLineaTresB,mejorLineaCuatro,mejorLineaCuatroA,mejorLineaCuatroB,mejorLineaCincoA,mejorLineaCincoB;

    Lineas linea_fuerahora=null;

    private Lineas[] mLineaSolouna=new Lineas[5];
    private Lineas[] mLineaOrigen=new Lineas[5];
    private Lineas[] mLineaDestino=new Lineas[5];

    private Lineas[] todasLineas;

    int min_hasta_bus;
    int h_inicio=0,h_fin=0,m_inicio=0,m_fin=0;
    int min_esta_estacion;
    int hora_comparar=0;
    static public int pulsa=0;

    public int cual_es;

    int d;
    int o;
    int s;
    int tiempollegada1,tiempollegada2,tiempollegada3;

    int mejorLinea_origen_fin=0;
    int mejorLinea_destino_inicio=0;
    public int fechaHoy;
    public int h_actual,m_actual;

    public Location[][] paradastot; // aqui almacenaremos la ruta que tendriamos que seguir

    Location encuentro_origen;
    Location encuentro_destino;

    boolean andando;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas);

        botonruta1=findViewById(R.id.ruta1);
        botonruta2=findViewById(R.id.ruta2);
        botonruta3=findViewById(R.id.ruta3);
        scroll=findViewById(R.id.scroll_rutas);

        rutaContenedor = findViewById(R.id.pantalla);
        inflador = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        //getSupportActionBar().hide();

        dimeNombres=new SentidoParadas();

        // ------------------------------------------------

        Bundle lasRutas = getIntent().getExtras();

        if (lasRutas != null) {

            todasLineasParcel = lasRutas.getParcelableArray("TODASLINEAS");
            todasLineas = Arrays.copyOf(todasLineasParcel, todasLineasParcel.length, Lineas[].class);

            minutosHoraReal=lasRutas.getInt("MINUTOSHORAREAL");

            ruta1A = lasRutas.getParcelableArray("RUTA1A");
            nombrerutaunoA = lasRutas.getString("NOMBRE1A");
            minutos1 = lasRutas.getInt("MINUTOS1");
            ruta1B = lasRutas.getParcelableArray("RUTA1B");
            nombrerutaunoB = lasRutas.getString("NOMBRE1B");

           // ruta1 = lasRutas.getParcelableArray("RUTA1");
           // nombrerutauno = lasRutas.getString("NOMBRE1");
           // minutos1 = lasRutas.getInt("MINUTOS1");

            ruta2A = lasRutas.getParcelableArray("RUTA2A");
            nombrerutadosA = lasRutas.getString("NOMBRE2A");
            minutos2 = lasRutas.getInt("MINUTOS2");
            ruta2B = lasRutas.getParcelableArray("RUTA2B");
            nombrerutadosB = lasRutas.getString("NOMBRE2B");

            ruta3A = lasRutas.getParcelableArray("RUTA3A");
            nombrerutatresA = lasRutas.getString("NOMBRE3A");
            minutos3 = lasRutas.getInt("MINUTOS3");
            ruta3B = lasRutas.getParcelableArray("RUTA3B");
            nombrerutatresB = lasRutas.getString("NOMBRE3B");

            nombrerutacuatro = lasRutas.getString("NOMBRE4");
            minutos4 = lasRutas.getInt("MINUTOS4");

            ruta1Bsiguiente = lasRutas.getInt("LINEA1BSIGUIENTE"); //----------------

            ruta2Bsiguiente = lasRutas.getInt("LINEA2BSIGUIENTE");
            ruta3Bsiguiente = lasRutas.getInt("LINEA3BSIGUIENTE");

        }


        intervalo1=(minutos1/10000);
        minutos1=(minutos1%10000);

        intervalo2=(minutos2/10000);
        minutos2=(minutos2%10000);

        intervalo3=(minutos3/10000);
        minutos3=(minutos3%10000);

        intervalo4=(minutos4/10000);
        minutos4=(minutos4%10000);

        String r1t="";
        String r2t="";
        String r3t="";

        if(ruta1A==null) {
            botonruta1.setAlpha(0);
            botonruta1.setVisibility(View.GONE);

        }else{

            r1t=getString(R.string.ruta1);

        }

        if(ruta2A==null) {
            botonruta2.setAlpha(0);
            botonruta2.setVisibility(View.GONE);

        }else{

            if (r1t.equals("")) {r2t=getString(R.string.ruta1);}else{r2t=getString(R.string.ruta2);}

        }

        if(ruta3A==null){

            botonruta3.setAlpha(0);
            botonruta3.setVisibility(View.GONE);
        }else{

            if (r1t.equals("") && r2t.equals("")) {r3t=getString(R.string.ruta1);}
            if(!r1t.equals("") && !r2t.equals("")){r3t=getString(R.string.ruta3);}else{r3t=getString(R.string.ruta2);}

        }

        botonruta1.setText(r1t);
        botonruta2.setText(r2t);
        botonruta3.setText(r3t);
        botonruta1.setTextColor(Color.WHITE);
        botonruta2.setTextColor(Color.WHITE);
        botonruta3.setTextColor(Color.WHITE);

        final String r1=r1t;
        final String r2=r2t;
        final String r3=r3t;

        botonruta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PonRutaUno(r1);
                    //PonRutaUno(r1,1);
                    if(rutaseleccionada==2){
                        if(botonruta2.getRotation()==0){botonruta2.animate().rotation(360);}else{botonruta2.animate().rotation(0);}
                        botonruta2.setText(r2);
                        botonruta2.setAlpha(1);
                        botonruta2.setEnabled(true);
                    }
                    if(rutaseleccionada==3){
                        if(botonruta3.getRotation()==0){botonruta3.animate().rotation(360);}else{botonruta3.animate().rotation(0);}
                        botonruta3.setText(r3);
                        botonruta3.setAlpha(1);
                        botonruta3.setEnabled(true);
                    }
                    rutaseleccionada=1;
                    //botonruta1.animate().rotation(giro);
                    botonruta1.setText(r1);
                    botonruta1.setAlpha(0.3f);
                botonruta1.setEnabled(false);
                    if(botonruta1.getRotation()==0){botonruta1.animate().rotation(360);}else{botonruta1.animate().rotation(0);}


            }
        });
        botonruta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    PonRutaDos(r2);
                    if(rutaseleccionada==1){
                        if(botonruta1.getRotation()==0){botonruta1.animate().rotation(360);}else{botonruta1.animate().rotation(0);}
                        botonruta1.setText(r1);
                        botonruta1.setAlpha(1);
                        botonruta1.setEnabled(true);
                    }
                    if(rutaseleccionada==3){
                        if(botonruta3.getRotation()==0){botonruta3.animate().rotation(360);}else{botonruta3.animate().rotation(0);}
                        botonruta3.setText(r3);
                        botonruta3.setAlpha(1);
                        botonruta3.setEnabled(true);
                    }
                    rutaseleccionada=2;
                    //botonruta2.animate().rotation(giro);
                    botonruta2.setText(r2);
                    botonruta2.setAlpha(0.3f);
                    botonruta2.setEnabled(false);
                    if(botonruta2.getRotation()==0){botonruta2.animate().rotation(360);}else{botonruta2.animate().rotation(0);}

            }
        });
        botonruta3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    PonRutaTres(r3);

                    if(rutaseleccionada==1){
                        if(botonruta1.getRotation()==0){botonruta1.animate().rotation(360);}else{botonruta1.animate().rotation(0);}
                        botonruta1.setText(r1);
                        botonruta1.setAlpha(1);
                        botonruta1.setEnabled(true);
                    }
                    if(rutaseleccionada==2){
                        if(botonruta2.getRotation()==0){botonruta2.animate().rotation(360);}else{botonruta2.animate().rotation(0);}
                        botonruta2.setText(r2);
                        botonruta2.setAlpha(1);
                        botonruta2.setEnabled(true);
                    }
                    rutaseleccionada=3;
                    //botonruta3.animate().rotation(giro);
                    botonruta3.setText(r3);
                    botonruta3.setAlpha(0.3f);
                    botonruta3.setEnabled(false);
                    if(botonruta3.getRotation()==0){botonruta3.animate().rotation(360);}else{botonruta3.animate().rotation(0);}

            }
        });

            if (ruta1A != null) {
                botonruta1.callOnClick();
            } else if (ruta2A != null) {
                botonruta2.callOnClick();
            } else if (ruta3B != null) {
                botonruta3.callOnClick();
            }

    }

    public void onResume() {

        if(pulsa==1){botonruta1.callOnClick();}
        if(pulsa==2){botonruta2.callOnClick();}
        if(pulsa==3){botonruta3.callOnClick();}

        super.onResume();
    }

    public void PonRutaUno1(String nombre,int cual){

        //System.out.println("PINTA RUTA 1");

        String nombreruta="";
        int minutos=0;
        int intervalo=0;

        Location[] rutaUno=null;

        rutaContenedor.removeAllViews();

        if(cual==1){
            rutaUno = Arrays.copyOf(ruta1A, ruta1A.length, Location[].class);
            nombreruta=nombrerutaunoA;
            minutos=minutos1;
            intervalo=intervalo1;

        }

        if(cual==2){
            rutaUno = Arrays.copyOf(ruta2A, ruta2A.length, Location[].class);
            nombreruta=nombrerutadosA;
            minutos=minutos2;
            intervalo=intervalo2;

        }
        if(cual==3){
            rutaUno = Arrays.copyOf(ruta3A, ruta3A.length, Location[].class);
            nombreruta=nombrerutatresA;
            minutos=minutos3;
            intervalo=intervalo3;

        }
/*
        if(cual==1){
            rutaUno = Arrays.copyOf(ruta1, ruta1.length, Location[].class);
            nombreruta=nombrerutauno;
            minutos=minutos1;
            intervalo=intervalo1;

        }


        if(cual==2){
            rutaUno = Arrays.copyOf(ruta2A, ruta2A.length, Location[].class);
            nombreruta=nombrerutacuatro;
            minutos=minutos4;
            intervalo=intervalo4;

        }
        if(cual==3){
            rutaUno = Arrays.copyOf(ruta3A, ruta3A.length, Location[].class);
            nombreruta=nombrerutacuatro;
            minutos=minutos4;
            intervalo=intervalo4;

        }

 */


        String describe_ruta=nombre+": "+getString(R.string.linea)+"   "+dimeNombres.dimeNombres(nombreruta)+
                " | "+getString(R.string.total_a_pie)+" "+(int)(rutaUno[0].distanceTo(rutaUno[1])+rutaUno[rutaUno.length-2].distanceTo(rutaUno[rutaUno.length-1]))+" "+getString(R.string.metros);

        TextView describe=findViewById(R.id.describe_ruta);

        describe.setText(describe_ruta);

        String texto;

        pintaEtapa(inflador, R.drawable.location, rutaUno[0].getProvider(), rutaContenedor,2);

        if (rutaUno.length < 4) {  // si los puntos de ruta es mayor que 2, significa que vamos en metro, entonces dibujamos

            texto = getString(R.string.a_pie) + " " + (int) (rutaUno[0].distanceTo((rutaUno[rutaUno.length-1])));
            texto += " " + getString(R.string.metros)+" "+getString(R.string.hasta);
            pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);
        }else {

            texto = getString(R.string.a_pie) + " " + (int) (rutaUno[0].distanceTo(rutaUno[1])) + " " + getString(R.string.metros)+" "+getString(R.string.hasta);

            pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

            //dimeNombres(dimeLinea);  // busca el nombre de la linea y cambia los valores

            nombreLinea=dimeNombres.dimeNombres(nombreruta);
            sentido=dimeSentido(nombreruta);


            for (int i = 1; i < rutaUno.length - 1; i++) {

                if (i == 1) {  // si va en metro, es la etapa 2, en esta etapa decimos que linea coger

                    pintaNotas(inflador,R.drawable.bustop1, getString(R.string.parada)+" "+rutaUno[i].getProvider(), rutaContenedor);

                    pintaNotas(inflador,0, getString(R.string.subeLinea)+" "+getString(R.string.linea)+" "+nombreLinea, rutaContenedor);
                    pintaNotas2(inflador, getString(R.string.direccion)+" "+sentido, rutaContenedor,0);

                    pintaNotas2(inflador, getString(R.string.pasaEn)+" "+minutos+" "+getString(R.string.minutos)+" "+getString(R.string.intervalo)+" "+intervalo+" min", rutaContenedor,0);

                }else if(i==rutaUno.length-2){

                    pintaNotas(inflador, R.drawable.bustop1,getString(R.string.bajaLinea)+" "+rutaUno[i].getProvider(), rutaContenedor);

                }else{pintaEtapa(inflador, R.drawable.bustop3, rutaUno[i].getProvider(), rutaContenedor,0);

                }

            }

            texto = getString(R.string.a_pie) + " " + (int) (rutaUno[rutaUno.length - 2].distanceTo((rutaUno[rutaUno.length - 1]))) + " " + getString(R.string.metros)+" "+getString((R.string.hasta));
            pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        }
        // dibujamos final
        pintaEtapa(inflador, R.drawable.finish, rutaUno[rutaUno.length - 1].getProvider(), rutaContenedor,2);

        scroll.fullScroll(ScrollView.FOCUS_UP);


    }

    public void PonRutaSola(String nombre,int cual){

        //System.out.println("PINTA RUTA SOLA");

        String nombreruta="";
        int minutos=0;
        int intervalo=0;
        String horaLlega="";

        Location[] rutaUno=null;

        rutaContenedor.removeAllViews();

        if(cual==1){
            rutaUno = Arrays.copyOf(ruta1A, ruta1A.length, Location[].class);
            nombreruta=nombrerutaunoA;
            minutos=minutos1+minutosHoraReal;
            intervalo=intervalo1;

        }

        if(cual==2){
            rutaUno = Arrays.copyOf(ruta2A, ruta2A.length, Location[].class);
            nombreruta=nombrerutadosA;
            minutos=minutos2+minutosHoraReal;
            intervalo=intervalo2;

        }
        if(cual==3){
            rutaUno = Arrays.copyOf(ruta3A, ruta3A.length, Location[].class);
            nombreruta=nombrerutatresA;
            minutos=minutos3+minutosHoraReal;
            intervalo=intervalo3;

        }


        if(minutos%60<10){

            horaLlega=minutos/60+":0"+minutos%60;
        }else {

            horaLlega=minutos/60+":"+minutos%60;
        }


/*
        if(cual==1){
            rutaUno = Arrays.copyOf(ruta1, ruta1.length, Location[].class);
            nombreruta=nombrerutauno;
            minutos=minutos1;
            intervalo=intervalo1;

        }


        if(cual==2){
            rutaUno = Arrays.copyOf(ruta2A, ruta2A.length, Location[].class);
            nombreruta=nombrerutacuatro;
            minutos=minutos4;
            intervalo=intervalo4;

        }
        if(cual==3){
            rutaUno = Arrays.copyOf(ruta3A, ruta3A.length, Location[].class);
            nombreruta=nombrerutacuatro;
            minutos=minutos4;
            intervalo=intervalo4;

        }

 */


        String describe_ruta=nombre+":  "+getString(R.string.linea)+" "+dimeNombres.dimeNombres(nombreruta)+
                "  |  "+getString(R.string.total_a_pie)+" "+(int)(rutaUno[0].distanceTo(rutaUno[1])+rutaUno[rutaUno.length-2].distanceTo(rutaUno[rutaUno.length-1]))+" "+getString(R.string.metros);


        TextView describe=findViewById(R.id.describe_ruta);

        describe.setText(describe_ruta);

        String texto;

        pintaEtapa(inflador, R.drawable.location, rutaUno[0].getProvider(), rutaContenedor,2);

        if (rutaUno.length < 4) {  // si los puntos de ruta es mayor que 2, significa que vamos en metro, entonces dibujamos

            texto = getString(R.string.a_pie) + " " + (int) (rutaUno[0].distanceTo((rutaUno[rutaUno.length-1])));
            texto += " " + getString(R.string.metros)+" "+getString(R.string.hasta);
            pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);
        }else {

            texto = getString(R.string.a_pie) + " " + (int) (rutaUno[0].distanceTo(rutaUno[1])) + " " + getString(R.string.metros)+" "+getString(R.string.hasta);

            pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

            //dimeNombres(dimeLinea);  // busca el nombre de la linea y cambia los valores

            nombreLinea=dimeNombres.dimeNombres(nombreruta);
            sentido=dimeSentido(nombreruta);


            for (int i = 1; i < rutaUno.length - 1; i++) {

                if (i == 1) {  // si va en metro, es la etapa 2, en esta etapa decimos que linea coger

                    pintaNotas(inflador,R.drawable.bustop1, getString(R.string.parada)+" "+rutaUno[i].getProvider(), rutaContenedor);

                    pintaNotas(inflador,0, getString(R.string.subeLinea)+" "+getString(R.string.linea)+" "+nombreLinea, rutaContenedor);
                    pintaNotas2(inflador, getString(R.string.direccion)+" "+sentido, rutaContenedor,0);


                    pintaNotas2(inflador, getString(R.string.sobrelas) + " "+horaLlega+ "   " + getString(R.string.intervalo) + " " + intervalo + " min", rutaContenedor, 0);


                }else if(i==rutaUno.length-2){

                    pintaNotas(inflador, R.drawable.bustop1,getString(R.string.bajaLinea)+" "+rutaUno[i].getProvider(), rutaContenedor);

                }else{pintaEtapa(inflador, R.drawable.bustop3, rutaUno[i].getProvider(), rutaContenedor,0);

                }

            }

            texto = getString(R.string.a_pie) + " " + (int) (rutaUno[rutaUno.length - 2].distanceTo((rutaUno[rutaUno.length - 1]))) + " " + getString(R.string.metros)+" "+getString((R.string.hasta));
            pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        }
        // dibujamos final
        pintaEtapa(inflador, R.drawable.finish, rutaUno[rutaUno.length - 1].getProvider(), rutaContenedor,2);

        scroll.fullScroll(ScrollView.FOCUS_UP);


    }

    public void PonRutaUno(String nombre){

        if(ruta1B==null){

            PonRutaSola(nombre,1);
            return;
        }

        //System.out.println("PINTA RUTA 1");

        Location[] rutaUnoA = Arrays.copyOf(ruta1A, ruta1A.length, Location[].class);
        Location[] rutaUnoB = Arrays.copyOf(ruta1B, ruta1B.length, Location[].class);

        rutaContenedor.removeAllViews();

        // ----- pinta la linea 1 de la ruta 2

        String horaLlega="";

        if((minutosHoraReal+minutos1)%60<10){

            horaLlega=(minutosHoraReal+minutos1)/60+":0"+(minutosHoraReal+minutos1)%60;
        }else {

            horaLlega = (minutosHoraReal + minutos1) / 60 + ":" + (minutosHoraReal + minutos1) % 60;
        }

        String describe_ruta=nombre+":  "+getString(R.string.linea)+" "+dimeNombres.dimeNombres(nombrerutaunoA)+" y "+dimeNombres.dimeNombres(nombrerutaunoB)+
                "  |  "+getString(R.string.total_a_pie)+" "+(int)(rutaUnoA[0].distanceTo(rutaUnoA[1])+rutaUnoB[rutaUnoB.length-2].distanceTo(rutaUnoB[rutaUnoB.length-1])+rutaUnoA[rutaUnoA.length-1].distanceTo(rutaUnoB[0]))
                +" "+getString(R.string.metros);

        TextView describe=findViewById(R.id.describe_ruta);

        describe.setText(describe_ruta);


        pintaEtapa(inflador, R.drawable.location, rutaUnoA[0].getProvider(), rutaContenedor,2);

        String texto = getString(R.string.a_pie) + " " + (int) (rutaUnoA[0].distanceTo(rutaUnoA[1])) + " " + getString(R.string.metros)+" "+getString((R.string.hasta));

        pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        nombreLinea=dimeNombres.dimeNombres(nombrerutaunoA);
        sentido=dimeSentido(nombrerutaunoA);

        for (int i = 1; i < rutaUnoA.length; i++) {

            if (i == 1) {  // si va en metro, es la etapa 2, en esta etapa decimos que linea coger

                pintaNotas(inflador, R.drawable.bustop1,getString(R.string.parada)+" "+rutaUnoA[i].getProvider(), rutaContenedor);

                pintaNotas(inflador, 0,getString(R.string.subeLinea)+" "+getString(R.string.linea)+" "+nombreLinea, rutaContenedor);
                pintaNotas2(inflador, getString(R.string.direccion)+" "+sentido, rutaContenedor,0);

                pintaNotas2(inflador, getString(R.string.sobrelas)+" "+horaLlega+"  "+getString(R.string.intervalo)+" "+intervalo1+" min", rutaContenedor,0);

            }else if(i==rutaUnoA.length-1){

                pintaNotas(inflador,R.drawable.bustop1, getString(R.string.bajaLinea)+" "+rutaUnoA[i].getProvider(), rutaContenedor);

            }else{pintaEtapa(inflador, R.drawable.bustop3, rutaUnoA[i].getProvider(), rutaContenedor,0);

            }

        }

        // ----- pinta la linea 2 de la ruta 1

        //dimeNombres(dimeLinea2);
        nombreLinea=dimeNombres.dimeNombres(nombrerutaunoB);
        sentido=dimeSentido(nombrerutaunoB);

        texto = getString(R.string.a_pie) + " " + (int) (rutaUnoA[rutaUnoA.length-1].distanceTo(rutaUnoB[0])) + " " + getString(R.string.metros)+" "+getString(R.string.hasta);
        pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        for (int i = 0; i < rutaUnoB.length-1; i++) {

            if(i==0){

                int intervalo=ruta1Bsiguiente/10000;
                int horabuena=ruta1Bsiguiente%10000;

                if(horabuena>1440){horabuena=horabuena-1440;}
                String minutos=(horabuena%60)+"";
                if(Integer.parseInt(minutos)<10){

                    minutos="0"+minutos;
                }


                String nota=getString(R.string.sobrelas)+" "+(horabuena/60)+":"+minutos+" "+getString(R.string.intervalo)+" "+intervalo+" "+getString(R.string.minutos);

                pintaNotas(inflador, R.drawable.bustop1,getString(R.string.parada)+" "+rutaUnoB[i].getProvider(), rutaContenedor);
                pintaNotas(inflador, 0,getString(R.string.subeLinea)+" "+getString(R.string.linea)+" "+nombreLinea, rutaContenedor);
                pintaNotas2(inflador, getString(R.string.direccion)+" "+sentido, rutaContenedor,0);
                pintaNotas2(inflador, nota, rutaContenedor,1);


            }else if(i==(rutaUnoB.length-2)){

                pintaNotas(inflador, R.drawable.bustop1,getString(R.string.bajaLinea)+" "+rutaUnoB[i].getProvider(), rutaContenedor);


            }else{

                pintaEtapa(inflador, R.drawable.bustop3, rutaUnoB[i].getProvider(), rutaContenedor,0);

            }

        }

        texto = getString(R.string.a_pie) + " " + (int) (rutaUnoB[rutaUnoB.length - 2].distanceTo((rutaUnoB[rutaUnoB.length - 1]))) + " " + getString(R.string.metros)+" "+getString((R.string.hasta));
        pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        pintaEtapa(inflador, R.drawable.finish, rutaUnoB[rutaUnoB.length - 1].getProvider(), rutaContenedor,2);

        scroll.fullScroll(ScrollView.FOCUS_UP);

    }

    public void PonRutaDos(String nombre){

        if(ruta2B==null){

            PonRutaSola(nombre,2);
            return;
        }

        String horaLlega="";

        if((minutosHoraReal+minutos2)%60<10){

            horaLlega=(minutosHoraReal+minutos2)/60+":0"+(minutosHoraReal+minutos2)%60;
        }else {

            horaLlega = (minutosHoraReal + minutos2) / 60 + ":" + (minutosHoraReal + minutos2) % 60;
        }

        //System.out.println("PINTA RUTA 2");

        Location[] rutaDosA = Arrays.copyOf(ruta2A, ruta2A.length, Location[].class);
        Location[] rutaDosB = Arrays.copyOf(ruta2B, ruta2B.length, Location[].class);

        rutaContenedor.removeAllViews();

        // ----- pinta la linea 1 de la ruta 2

        String describe_ruta=nombre+":  "+getString(R.string.linea)+" "+dimeNombres.dimeNombres(nombrerutadosA)+" y "+getString(R.string.linea)+" "+dimeNombres.dimeNombres(nombrerutadosB)+
        "  |  "+getString(R.string.total_a_pie)+" "+(int)(rutaDosA[0].distanceTo(rutaDosA[1])+rutaDosB[rutaDosB.length-2].distanceTo(rutaDosB[rutaDosB.length-1])+rutaDosA[rutaDosA.length-1].distanceTo(rutaDosB[0]))
                +" "+getString(R.string.metros);

        TextView describe=findViewById(R.id.describe_ruta);

        describe.setText(describe_ruta);


        pintaEtapa(inflador, R.drawable.location, rutaDosA[0].getProvider(), rutaContenedor,2);

        String texto = getString(R.string.a_pie) + " " + (int) (rutaDosA[0].distanceTo(rutaDosA[1])) + " " + getString(R.string.metros)+" "+getString((R.string.hasta));

        pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        nombreLinea=dimeNombres.dimeNombres(nombrerutadosA);
        sentido=dimeSentido(nombrerutadosA);

        for (int i = 1; i < rutaDosA.length; i++) {

            if (i == 1) {  // si va en metro, es la etapa 2, en esta etapa decimos que linea coger

                pintaNotas(inflador, R.drawable.bustop1,getString(R.string.parada)+" "+rutaDosA[i].getProvider(), rutaContenedor);

                pintaNotas(inflador, 0,getString(R.string.subeLinea)+" "+getString(R.string.linea)+" "+nombreLinea, rutaContenedor);
                pintaNotas2(inflador, getString(R.string.direccion)+" "+sentido, rutaContenedor,0);

                pintaNotas2(inflador, getString(R.string.sobrelas)+" "+horaLlega+"  "+getString(R.string.intervalo)+" "+intervalo2+" min", rutaContenedor,0);

            }else if(i==rutaDosA.length-1){

                pintaNotas(inflador,R.drawable.bustop1, getString(R.string.bajaLinea)+" "+rutaDosA[i].getProvider(), rutaContenedor);

            }else{pintaEtapa(inflador, R.drawable.bustop3, rutaDosA[i].getProvider(), rutaContenedor,0);

            }

        }

        // ----- pinta la linea 2 de la ruta 2

        //dimeNombres(dimeLinea2);
        nombreLinea=dimeNombres.dimeNombres(nombrerutadosB);
        sentido=dimeSentido(nombrerutadosB);

        texto = getString(R.string.a_pie) + " " + (int) (rutaDosA[rutaDosA.length-1].distanceTo(rutaDosB[0])) + " " + getString(R.string.metros)+" "+getString(R.string.hasta);
        pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        for (int i = 0; i < rutaDosB.length-1; i++) {

            if(i==0){

                int intervalo=ruta2Bsiguiente/10000;
                int horabuena=ruta2Bsiguiente%10000;
                if(horabuena>1440){horabuena=horabuena-1440;}
                String minutos=(horabuena%60)+"";
                if(Integer.parseInt(minutos)<10){

                    minutos="0"+minutos;
                }


                String nota=getString(R.string.sobrelas)+" "+(horabuena/60)+":"+minutos+" "+getString(R.string.intervalo)+" "+intervalo+" "+getString(R.string.minutos);

                pintaNotas(inflador, R.drawable.bustop1,getString(R.string.parada)+" "+rutaDosB[i].getProvider(), rutaContenedor);
                pintaNotas(inflador, 0,getString(R.string.subeLinea)+" "+getString(R.string.linea)+" "+nombreLinea, rutaContenedor);
                pintaNotas2(inflador, getString(R.string.direccion)+" "+sentido, rutaContenedor,0);
                pintaNotas2(inflador, nota, rutaContenedor,1);


            }else if(i==(rutaDosB.length-2)){

                pintaNotas(inflador, R.drawable.bustop1,getString(R.string.bajaLinea)+" "+rutaDosB[i].getProvider(), rutaContenedor);


            }else{

                pintaEtapa(inflador, R.drawable.bustop3, rutaDosB[i].getProvider(), rutaContenedor,0);

            }

        }

        texto = getString(R.string.a_pie) + " " + (int) (rutaDosB[rutaDosB.length - 2].distanceTo((rutaDosB[rutaDosB.length - 1]))) + " " + getString(R.string.metros)+" "+getString((R.string.hasta));
        pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        pintaEtapa(inflador, R.drawable.finish, rutaDosB[rutaDosB.length - 1].getProvider(), rutaContenedor,2);

        scroll.fullScroll(ScrollView.FOCUS_UP);

    }

    public void PonRutaTres(String nombre){

        if(ruta3B==null){

            PonRutaSola(nombre,3);
            return;
        }

        String horaLlega="";

        if((minutosHoraReal+minutos3)%60<10){

            horaLlega=(minutosHoraReal+minutos3)/60+":0"+(minutosHoraReal+minutos3)%60;
        }else {

            horaLlega = (minutosHoraReal + minutos3) / 60 + ":" + (minutosHoraReal + minutos3) % 60;
        }

        //System.out.println("PINTA RUTA 3");

        rutaContenedor.removeAllViews();

        Location[] rutaTresA = Arrays.copyOf(ruta3A, ruta3A.length, Location[].class);
        Location[] rutaTresB = Arrays.copyOf(ruta3B, ruta3B.length, Location[].class);

        String describe_ruta=nombre+":  "+getString(R.string.linea)+" "+dimeNombres.dimeNombres(nombrerutatresA)+" y "+getString(R.string.linea)+" "+dimeNombres.dimeNombres(nombrerutatresB)+
        "  |  "+getString(R.string.total_a_pie)+" "+(int)(rutaTresA[0].distanceTo(rutaTresA[1])+rutaTresB[rutaTresB.length-2].distanceTo(rutaTresB[rutaTresB.length-1])+rutaTresA[rutaTresA.length-1].distanceTo(rutaTresB[0]))
                +" "+getString(R.string.metros);

        TextView describe=findViewById(R.id.describe_ruta);

        describe.setText(describe_ruta);



        // ----- pinta la linea 1 de la ruta 3

        pintaEtapa(inflador, R.drawable.location, rutaTresA[0].getProvider(), rutaContenedor,2);

        String texto = getString(R.string.a_pie) + " " + (int) (rutaTresA[0].distanceTo(rutaTresA[1])) + " " + getString(R.string.metros)+" "+getString((R.string.hasta));

        pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        nombreLinea=dimeNombres.dimeNombres(nombrerutatresA);
        sentido=dimeSentido(nombrerutatresA);

        for (int i = 1; i < rutaTresA.length; i++) {

            if (i == 1) {  // si va en metro, es la etapa 2, en esta etapa decimos que linea coger

                pintaNotas(inflador, R.drawable.bustop1,getString(R.string.parada)+" "+rutaTresA[i].getProvider(), rutaContenedor);

                pintaNotas(inflador, 0,getString(R.string.subeLinea)+" "+getString(R.string.linea)+" "+nombreLinea, rutaContenedor);
                pintaNotas2(inflador, getString(R.string.direccion)+" "+sentido, rutaContenedor,0);

                pintaNotas2(inflador, getString(R.string.sobrelas)+" "+horaLlega+"  "+getString(R.string.intervalo)+" "+intervalo3+" min", rutaContenedor,0);

            }else if(i==rutaTresA.length-1){

                pintaNotas(inflador,R.drawable.bustop1, getString(R.string.bajaLinea)+" "+rutaTresA[i].getProvider(), rutaContenedor);

            }else{pintaEtapa(inflador, R.drawable.bustop3, rutaTresA[i].getProvider(), rutaContenedor,0);

            }

        }

        // ----- pinta la linea 2 de la ruta 3

        //dimeNombres(dimeLinea2);
        nombreLinea=dimeNombres.dimeNombres(nombrerutatresB);
        sentido=dimeSentido(nombrerutatresB);

        texto = getString(R.string.a_pie) + " " + (int) (rutaTresA[rutaTresA.length-1].distanceTo(rutaTresB[0])) + " " + getString(R.string.metros)+" "+getString(R.string.hasta);
        pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        for (int i = 0; i < rutaTresB.length-1; i++) {

            if(i==0){

                int intervalo=ruta3Bsiguiente/10000;
                int horabuena=ruta3Bsiguiente%10000;
                if(horabuena>1440){horabuena=horabuena-1440;}
                String minutos=(horabuena%60)+"";
                if(Integer.parseInt(minutos)<10){

                    minutos="0"+minutos;
                }

                String nota=getString(R.string.sobrelas)+" "+(horabuena/60)+":"+minutos+" "+getString(R.string.intervalo)+" "+intervalo+" "+getString(R.string.minutos);

                pintaNotas(inflador, R.drawable.bustop1,getString(R.string.parada)+" "+rutaTresB[i].getProvider(), rutaContenedor);
                pintaNotas(inflador, 0,getString(R.string.subeLinea)+" "+getString(R.string.linea)+" "+nombreLinea, rutaContenedor);
                pintaNotas2(inflador, getString(R.string.direccion)+" "+sentido, rutaContenedor,0);
                pintaNotas2(inflador, nota, rutaContenedor,1);

            }else if(i==(rutaTresB.length-2)){

                pintaNotas(inflador, R.drawable.bustop1,getString(R.string.bajaLinea)+" "+rutaTresB[i].getProvider(), rutaContenedor);

            }else{

                pintaEtapa(inflador, R.drawable.bustop3, rutaTresB[i].getProvider(), rutaContenedor,0);

            }

        }

        texto = getString(R.string.a_pie) + " " + (int) (rutaTresB[rutaTresB.length - 2].distanceTo((rutaTresB[rutaTresB.length - 1]))) + " " + getString(R.string.metros)+" "+getString((R.string.hasta));
        pintaEtapa(inflador, R.drawable.walk, texto, rutaContenedor,1);

        pintaEtapa(inflador, R.drawable.finish, rutaTresB[rutaTresB.length - 1].getProvider(), rutaContenedor,2);

        scroll.fullScroll(ScrollView.FOCUS_UP);

    }

    private void pintaEtapa(LayoutInflater inflador, int imagen, String texto,LinearLayout contenedor, int color){

        LinearLayout distanciaEstaciones=(LinearLayout)inflador.inflate(R.layout.distancia_estaciones,null);

        ((ImageView)distanciaEstaciones.findViewById(R.id.icono)).setImageResource(imagen);
        ((TextView)distanciaEstaciones.findViewById(R.id.texto)).setText(" "+texto);

        //if(color==1){((TextView)distanciaEstaciones.findViewById(R.id.texto)).setTextColor(getResources().getColor(R.color.colorVerdeClaro,null));}
        if(color==2){

            //((TextView)distanciaEstaciones.findViewById(R.id.texto)).setTextColor(getResources().getColor(R.color.colorAzul,null));

            ((TextView)distanciaEstaciones.findViewById(R.id.texto)).setTypeface(Typeface.DEFAULT_BOLD);

        }

        contenedor.addView(distanciaEstaciones); // vamos añadiendo imagenes y textos al contenedor

    }

    private void pintaNotas2(LayoutInflater inflador, String texto,LinearLayout contenedor,int tamano){

        /*
        LinearLayout distanciaEstaciones=(LinearLayout)inflador.inflate(R.layout.barra_notas2,null);

        ((TextView)distanciaEstaciones.findViewById(R.id.textonotas2)).setText(" "+texto);

        if(tamano==1){((TextView)distanciaEstaciones.findViewById(R.id.textonotas2)).setTextSize(12);}
        */

        LinearLayout distanciaEstaciones=(LinearLayout)inflador.inflate(R.layout.barra_notas,null);

        ((TextView)distanciaEstaciones.findViewById(R.id.textonotas)).setText(" "+texto);

        contenedor.addView(distanciaEstaciones); // vamos añadiendo imagenes y textos al contenedor

    }

    private void pintaNotas(LayoutInflater inflador, int imagen, String texto,LinearLayout contenedor){

        LinearLayout distanciaEstaciones=(LinearLayout)inflador.inflate(R.layout.barra_notas,null);

        ((ImageView)distanciaEstaciones.findViewById(R.id.icono2)).setImageResource(imagen);

        ((TextView)distanciaEstaciones.findViewById(R.id.textonotas)).setText(" "+texto);

        contenedor.addView(distanciaEstaciones); // vamos añadiendo imagenes y textos al contenedor

    }

    public void mapa(View vista){

        Bundle lasRutas=getIntent().getExtras();
        Intent miIntento=new Intent(this,MapsActivity.class);
        ContenedorInicio.queMapa=1;
        miIntento.putExtra("QUERUTA",rutaseleccionada);
        miIntento.putExtras(lasRutas);
        startActivity(miIntento);
    }



    public Location[][] BuscaLasLineas(Location origen, Location destino,Lineas[] lasLineas){

        //System.out.println("----------------------------------------HOY ES " + fechaHoy + " SON LAS " + h_actual + ":" + m_actual);

        paradastot = new Location[8][1];
        //horaCambiada=false;
        andando=false;

        o=0;
        d=0;
        s=0;

        cual_es=0;

        mejorLinea_origen_fin = 0;
        mejorLinea_destino_inicio = 0;

        paradastot[0]=null;
        paradastot[1]=null;
        paradastot[2]=null;
        paradastot[3]=null;
        paradastot[4]=null;
        paradastot[5]=null;
        paradastot[6]=null;
        paradastot[7]=null;

        for(int i=0;i<5;i++){

            mLineaOrigen[i]=null;
            mLineaDestino[i]=null;
            mLineaSolouna[i]=null;
        }

        //System.out.println("INICIA BUSQUEDA - LA HORA ES: "+h_actual+":"+m_actual);

        boolean hayLineaDisponible = false;


        for (Lineas linea : lasLineas) {

            linea.distancias(origen,destino);

        }

        for (Lineas linea : lasLineas) {    // ------------------------- BUSCA LAS LINEAS DE LA LINEA SOLA -------------------------

            if(MiraLaHora(linea, linea.origenRuta) && linea.activa==1){

                // BUSCA LINEA UNICA

                if(linea.datosParadaOrigen<maximoAndar && linea.datosParadaDestino<maximoAndar && linea.finalRuta-linea.origenRuta>0){

                if (mLineaSolouna[0] == null) {

                    hayLineaDisponible = true;

                    mLineaSolouna[0] = linea;

                } else if (linea.origenRuta < mLineaSolouna[0].origenRuta) {

                        //if (linea.datosParadaOrigen < mLineaOrigen[0].datosParadaOrigen) {
                        //if (linea.distancia_origen(origen) < mLineaOrigen[0].distancia_origen(origen)) {
                        mLineaSolouna[4] = mLineaSolouna[3];
                        mLineaSolouna[3] = mLineaSolouna[2];
                        mLineaSolouna[2] = mLineaSolouna[1];
                        mLineaSolouna[1] = mLineaSolouna[0];
                        mLineaSolouna[0] = linea;

                } else if (mLineaSolouna[1] == null) {

                        mLineaSolouna[1] = linea;

                } else if (linea.datosParadaOrigen < mLineaSolouna[1].datosParadaOrigen) {

                        mLineaSolouna[4] = mLineaSolouna[3];
                        mLineaSolouna[3] = mLineaSolouna[2];
                        mLineaSolouna[2] = mLineaSolouna[1];
                        mLineaSolouna[1] = linea;

                } else if (mLineaSolouna[2] == null) {

                        mLineaSolouna[2] = linea;

                } else if (linea.datosParadaOrigen < mLineaSolouna[2].datosParadaOrigen) {

                        mLineaSolouna[4] = mLineaSolouna[3];
                        mLineaSolouna[3] = mLineaSolouna[2];
                        mLineaSolouna[2] = linea;

                } else if (mLineaSolouna[3] == null) {

                        mLineaSolouna[3] = linea;

                } else if (linea.datosParadaOrigen < mLineaSolouna[3].datosParadaOrigen) {

                        mLineaSolouna[4] = mLineaSolouna[3];
                        mLineaSolouna[3] = linea;

                } else if (mLineaSolouna[4] == null) {

                        mLineaSolouna[4] = linea;

                } else if (linea.datosParadaOrigen < mLineaSolouna[4].datosParadaOrigen) {

                        mLineaSolouna[4] = linea;

                }
            }

                // BUSCA LINEAS ORIGEN DE RUTA CON DOS LINEAS

                if(linea.datosParadaOrigen<maximoAndarTotal && linea.activa==1) {

                    if (mLineaOrigen[0] == null) {

                        hayLineaDisponible = true;

                        mLineaOrigen[0] = linea;

                    }else

                    //if (linea.origenRuta < mLineaOrigen[0].origenRuta) {

                        if (linea.datosParadaOrigen < mLineaOrigen[0].datosParadaOrigen) {

                            //if (linea.datosParadaOrigen < mLineaOrigen[0].datosParadaOrigen) {
                            //if (linea.distancia_origen(origen) < mLineaOrigen[0].distancia_origen(origen)) {
                            mLineaOrigen[4] = mLineaOrigen[3];
                            mLineaOrigen[3] = mLineaOrigen[2];
                            mLineaOrigen[2] = mLineaOrigen[1];
                            mLineaOrigen[1] = mLineaOrigen[0];
                            mLineaOrigen[0] = linea;

                    } else if (mLineaOrigen[1] == null) {

                            mLineaOrigen[1] = linea;

                    } else if (linea.datosParadaOrigen < mLineaOrigen[1].datosParadaOrigen) {

                            mLineaOrigen[4] = mLineaOrigen[3];
                            mLineaOrigen[3] = mLineaOrigen[2];
                            mLineaOrigen[2] = mLineaOrigen[1];
                            mLineaOrigen[1] = linea;

                    } else if (mLineaOrigen[2] == null) {

                            mLineaOrigen[2] = linea;

                    } else if (linea.datosParadaOrigen < mLineaOrigen[2].datosParadaOrigen) {

                            mLineaOrigen[4] = mLineaOrigen[3];
                            mLineaOrigen[3] = mLineaOrigen[2];
                            mLineaOrigen[2] = linea;

                    } else if (mLineaOrigen[3] == null) {

                            mLineaOrigen[3] = linea;

                    } else if (linea.datosParadaOrigen < mLineaOrigen[3].datosParadaOrigen) {

                            mLineaOrigen[4] = mLineaOrigen[3];
                            mLineaOrigen[3] = linea;

                    } else if (mLineaOrigen[4] == null) {

                            mLineaOrigen[4] = linea;

                    } else if (linea.datosParadaOrigen < mLineaOrigen[4].datosParadaOrigen) {

                            mLineaOrigen[4] = linea;

                    }
                }

                // BUSCA LINEAS DESTINO ORDENANDOLAS POR DISTANCIA A DESTINO

                //if (origenCercaEstDestino(linea, origen)  && linea.datosParadaDestino<maximoAndar &&
                if (linea.datosParadaDestino<maximoAndarTotal &&
                        (linea!=mLineaOrigen[0] && linea!=mLineaOrigen[1] && linea!=mLineaOrigen[2] && linea!=mLineaOrigen[3] &&linea!=mLineaOrigen[4]) && linea.activa==1) {

                    if (mLineaDestino[0] == null) {

                        mLineaDestino[0] = linea;
                        linea_fuerahora = linea;
                    }else if (linea.datosParadaDestino < mLineaDestino[0].datosParadaDestino) {

                        mLineaDestino[4] = mLineaDestino[3];
                        mLineaDestino[3] = mLineaDestino[2];
                        mLineaDestino[2] = mLineaDestino[1];
                        mLineaDestino[1] = mLineaDestino[0];
                        mLineaDestino[0] = linea;

                    } else if (mLineaDestino[1] == null) {
                        mLineaDestino[1] = linea;
                    } else if (linea.datosParadaDestino < mLineaDestino[1].datosParadaDestino) {

                        mLineaDestino[4] = mLineaDestino[3];
                        mLineaDestino[3] = mLineaDestino[2];
                        mLineaDestino[2] = mLineaDestino[1];
                        mLineaDestino[1] = linea;

                    } else if (mLineaDestino[2] == null) {
                        mLineaDestino[2] = linea;
                    } else if (linea.datosParadaDestino < mLineaDestino[2].datosParadaDestino) {

                        mLineaDestino[4] = mLineaDestino[3];
                        mLineaDestino[3] = mLineaDestino[2];
                        mLineaDestino[2] = linea;

                    } else if (mLineaDestino[3] == null) {
                        mLineaDestino[3] = linea;
                    } else if (linea.datosParadaDestino < mLineaDestino[3].datosParadaDestino) {

                        mLineaDestino[4] = mLineaDestino[3];
                        mLineaDestino[3] = linea;

                    } else if (mLineaDestino[4] == null) {
                        mLineaDestino[4] = linea;
                    } else if (linea.datosParadaDestino < mLineaDestino[4].datosParadaDestino) {

                        mLineaDestino[4] = linea;

                    }
                }

            }

        }

        if(!hayLineaDisponible){

            //System.out.println("NO HAY NINGUNA LINEA A ESTA HORA");

            return paradastot;
        }

        for(int i=0; i<mLineaSolouna.length;i++){

            if(mLineaSolouna[i]!=null){
                //System.out.println("LINEAS SOLO UNA: "+s+" "+mLineaSolouna[i].nombre+" Origen ruta: "+mLineaSolouna[i].origenRuta+" Distancia a origen: "+mLineaSolouna[i].datosParadaOrigen);
                s++;
            }else{break;}

        }

        for(int i=0; i<mLineaOrigen.length;i++){

            if(mLineaOrigen[i]!=null){
                //System.out.println("LINEAS ORIGEN: "+o+" "+mLineaOrigen[i].nombre+" Origen ruta: "+mLineaOrigen[i].origenRuta+" Distancia a origen: "+mLineaOrigen[i].datosParadaOrigen);
                o++;
            }else{break;}

        }

        for(int i=0; i<mLineaDestino.length;i++){

            if(mLineaDestino[i]!=null){
                //System.out.println("LINEAS DESTINO: "+d+" "+mLineaDestino[i].nombre+" Final ruta: "+mLineaDestino[i].finalRuta+" Distancia a destino: "+mLineaDestino[i].datosParadaDestino);
                d++;
            }else{break;}

        }


        BuscaRutaUno(origen, destino,lasLineas);  // ---- AÑADE RUTA UNO ------------------------------------
        if (andando){

            return paradastot;
        }
        BuscaRutaDos(origen, destino,lasLineas);  // ---- AÑADE RUTA DOS ------------------------------------



        if(mLineaDestino[0]==null){  // ------ SI NO HAY SEGUNDA LINEAS DISPONIBLES

            //System.out.println("NO HAY LINEA DE DESTINO DISPONIBLE A ESTA HORA");

            return paradastot;

        }

        for(int i=0;i<mLineaOrigen.length;i++){
            if(mLineaOrigen[i]!=null){

                System.out.println("ORIGEN "+i+" "+mLineaOrigen[i].nombre);
            }
            if(mLineaDestino[i]!=null){

                System.out.println("DESTINO "+i+" "+mLineaDestino[i].nombre);
            }
        }

        BuscaRutaTres(origen, destino);  // ---- AÑADE RUTA TRES ------------------------------------

        BuscaRutaCuatro(origen, destino);  // ---- AÑADE RUTA CUATRO ------------------------------------

        BuscaRutaCinco(origen, destino);  // ---- AÑADE RUTA CINCO ------------------------------------

    return paradastot;

    }

    public void BuscaRutaUno(Location origen, Location destino, Lineas[] lasLineas){

      //System.out.println(" -------------------------------------- RUTA 1 (una linea)");
       //System.out.println("----------------------------------------HOY ES " + fechaHoy + " SON LAS " + h_actual + ":" + m_actual);

        mejorLineaUno=null;
        int parTot=200;

        for(int i=0;i<s;i++){

            if (mejorLineaUno == null && (mLineaSolouna[i].finalRuta-mLineaSolouna[i].origenRuta>0)){

                mejorLineaUno = mLineaSolouna[i];

            } else if (mejorLineaUno!=null && mLineaSolouna[i].finalRuta-mLineaSolouna[i].origenRuta<parTot && ((mLineaSolouna[i].datosParadaOrigen+mLineaSolouna[i].datosParadaDestino) < (mejorLineaUno.datosParadaOrigen+mejorLineaUno.datosParadaDestino))
                    && (mLineaSolouna[i].finalRuta-mLineaSolouna[i].origenRuta>0)
                    && (mLineaSolouna[i].datosParadaOrigen<maximoAndar && mLineaSolouna[i].datosParadaDestino<maximoAndar)

            ) {

                mejorLineaUno = mLineaSolouna[i];
                parTot=mejorLineaUno.finalRuta-mejorLineaUno.origenRuta;

            }

        }

/*
        for(int i=0;i<o;i++){

            if (mejorLineaUno == null && (mLineaOrigen[i].finalRuta-mLineaOrigen[i].origenRuta>0)){

                mejorLineaUno = mLineaOrigen[i];
                //System.out.println("-----------------MEJOR LINEA UNO: "+mejorLineaUno);

            } else if (mejorLineaUno!=null && ((mLineaOrigen[i].datosParadaOrigen+mLineaOrigen[i].datosParadaDestino) < (mejorLineaUno.datosParadaOrigen+mejorLineaUno.datosParadaDestino))
                    && (mLineaOrigen[i].finalRuta-mLineaOrigen[i].origenRuta>0)
                    && (mLineaOrigen[i].datosParadaOrigen<maximoAndar && mLineaOrigen[i].datosParadaDestino<maximoAndar)

            ) {

                mejorLineaUno = mLineaOrigen[i];
                //System.out.println("-------------------MEJOR LINEA UNO: "+mejorLineaUno);

            }

        }

 */



        /*

        for(Lineas linea: lasLineas) {

            if(MiraLaHora(linea,linea.origenRuta)) {


                //System.out.println("LINEA UNO: "+linea.nombre+" "+(linea.finalRuta-linea.origenRuta));
                if (mejorLineaUno == null && (linea.finalRuta-linea.origenRuta>0)){

                    mejorLineaUno = linea;
                    //System.out.println("-----------------MEJOR LINEA UNO: "+mejorLineaUno);

                } else if (mejorLineaUno!=null && ((linea.datosParadaOrigen+linea.datosParadaDestino) < (mejorLineaUno.datosParadaOrigen+mejorLineaUno.datosParadaDestino))
                        && (linea.finalRuta-linea.origenRuta>0)
                        && (linea.datosParadaOrigen<maximoAndar && linea.datosParadaDestino<maximoAndar)

                ) {

                    mejorLineaUno = linea;
                    //System.out.println("-------------------MEJOR LINEA UNO: "+mejorLineaUno);

                }

            }

        }

         */

        if(mejorLineaUno==null){

          //System.out.println("NINGUNA LINEA EN 1");

            paradastot[0]=null;

            return;

        }

       //System.out.println("-------------------MEJOR LINEA UNO: "+mejorLineaUno.nombre);

       //System.out.println("HORA INICIO: "+mejorLineaUno.horarioViernes_inicio);

        if(origen.distanceTo(destino)<maximoAndar) {

            // -------------------  Si es mejor ir andando --------------------------------

           //System.out.println("VA ANDANDO 1"+origen.distanceTo(destino)+" "+maximoAndar);

            andando=true;

            Location[] uno = new Location[2];
            paradastot[0] = uno;
            uno[0] = origen;
            uno[1] = destino;

            return;
        }



        if(mejorLineaUno.finalRuta-mejorLineaUno.origenRuta<0){

           //System.out.println("SENTIDO INVERSO"+(mejorLineaUno.finalRuta-mejorLineaUno.origenRuta));

            mejorLineaUno= null;

            paradastot[0]=null;

            return;

        }

        if(mejorLineaUno.finalRuta-mejorLineaUno.origenRuta<minparadas && origen.distanceTo(destino)<maximoAndar){// || origen.distanceTo(destino)<(mejorLineaUno.datosParadaOrigen+mejorLineaUno.datosParadaDestino)) {

            // -------------------  Si es mejor ir andando --------------------------------

            //System.out.println("VA ANDANDO 2: ");

            Location[] uno = new Location[2];
            paradastot[0] = uno;
            uno[0] = origen;
            uno[1] = destino;

            return;
        }

        if(mejorLineaUno.datosParadaOrigen>maximoAndar || mejorLineaUno.datosParadaDestino>maximoAndar){

           //System.out.println("HAY QUE ANDAR MUCHO");

            mejorLineaUno= null;

            paradastot[0]=null;

            return;

        }

        Location[] uno=new Location[mejorLineaUno.finalRuta - mejorLineaUno.origenRuta + 3];
        paradastot[0] = uno;
        uno[0]=origen;
        uno[uno.length-1]=destino;


        for (int i = 1; i < uno.length-1; i++) {

            uno[i] = mejorLineaUno.estaciones[i + mejorLineaUno.origenRuta-1];

        }

    }

    public void BuscaRutaDos(Location origen, Location destino, Lineas[] lasLineas){

      //System.out.println(" -------------------------------------- RUTA 2 (una linea)");

        mejorLineaDos=null;
        int parTot=200;

        int maximoAndarL2;

        for(int i=0;i<s;i++){

            if (mLineaSolouna[i] != mejorLineaUno) {

                if (mejorLineaDos == null && (mLineaSolouna[i].finalRuta-mLineaSolouna[i].origenRuta>0)) {

                    mejorLineaDos = mLineaSolouna[i];

                    //} else if (linea.distancias(origen, destino) + linea.proximoBus < mejorLineaCuatro.distancias(origen, destino) + mejorLineaCuatro.proximoBus) {
                } else if (mejorLineaDos!=null && ((mLineaSolouna[i].datosParadaOrigen+mLineaSolouna[i].datosParadaDestino) < (mejorLineaDos.datosParadaOrigen+mejorLineaDos.datosParadaDestino))
                        && (mLineaSolouna[i].finalRuta-mLineaSolouna[i].origenRuta>0  && mLineaSolouna[i].finalRuta-mLineaSolouna[i].origenRuta<parTot)
                        //&& (mLineaSolouna[i].datosParadaOrigen<maximoAndar && mLineaSolouna[i].datosParadaDestino<maximoAndar)

                ) {

                    mejorLineaDos = mLineaSolouna[i];
                    parTot=mejorLineaDos.finalRuta-mejorLineaDos.origenRuta;

                }

            }

        }

        /*

        for(int i=0;i<o;i++){

            if (mLineaOrigen[i] != mejorLineaUno) {

                if (mejorLineaDos == null && (mLineaOrigen[i].finalRuta-mLineaOrigen[i].origenRuta>0)) {

                    mejorLineaDos = mLineaOrigen[i];

                    //} else if (linea.distancias(origen, destino) + linea.proximoBus < mejorLineaCuatro.distancias(origen, destino) + mejorLineaCuatro.proximoBus) {
                } else if (mejorLineaDos!=null && ((mLineaOrigen[i].datosParadaOrigen+mLineaOrigen[i].datosParadaDestino) < (mejorLineaDos.datosParadaOrigen+mejorLineaDos.datosParadaDestino))
                        && (mLineaOrigen[i].finalRuta-mLineaOrigen[i].origenRuta>0)
                        && (mLineaOrigen[i].datosParadaOrigen<maximoAndarL2 && mLineaOrigen[i].datosParadaDestino<maximoAndarL2)


                ) {

                    mejorLineaDos = mLineaOrigen[i];

                }

            }

        }

         */


        if (mejorLineaDos == null) {

          //System.out.println("NINGUNA LINEA EN 2");

            paradastot[1]=null;

            return;


        }

        else if (mejorLineaDos.datosParadaOrigen > maximoAndar || mejorLineaDos.datosParadaDestino > maximoAndar) {

           //System.out.println("HAY QUE ANDAR MAS DE "+maximoAndarL2);

            paradastot[1]=null;

            return;

        }

        else if (mejorLineaDos.finalRuta - mejorLineaDos.origenRuta < 1 || (origen.distanceTo(destino) < maximoAndar) || origen.distanceTo(destino) < (mejorLineaDos.datosParadaOrigen+mejorLineaDos.datosParadaDestino)) {

           //System.out.println("SALE MEJOR IR ANDANDO: "+(mejorLineaDos.finalRuta - mejorLineaDos.origenRuta)+" "+origen.distanceTo(destino));

            paradastot[1]=null;

            return;

        }

           //System.out.println("MEJOR LINEA 2 UNA LINEA: " + mejorLineaDos.nombre);


            Location[] uno = new Location[mejorLineaDos.finalRuta - mejorLineaDos.origenRuta + 3];
            paradastot[1] = uno;
            uno[0] = origen;
            uno[uno.length - 1] = destino;


            for (int i = 1; i < uno.length - 1; i++) {

                uno[i] = mejorLineaDos.estaciones[i + mejorLineaDos.origenRuta - 1];

            }

    }

    public void BuscaRutaTres(Location origen, Location destino) {

      //System.out.println(" -------------------------------------- RUTA 3");

        int cont = 0;
        int parTot = 200;
        int distot = maximoAndarTotal;  // ---------- metros maximos que quiero andar
        int disEncu = maximoAndarTotal;

        int disOrigen=maximoAndarTotal;
        int disDestino=maximoAndarTotal;
        encuentro_origen = null;
        mejorLinea_origen_fin =0;
        encuentro_destino = null;
        mejorLinea_destino_inicio =0;
        mejorLineaTresA = null;
        mejorLineaTresB = null;

        int no_dos=10;
        int no_uno=10;

        for (int a = 0; a < o; a++) {

            for (int i = mLineaOrigen[a].origenRuta; i < mLineaOrigen[a].estaciones.length; i++) {

                for (int b = 0; b < d; b++) {

                    for (int e = mLineaDestino[b].finalRuta; e > -1; e--) {

                        //if(i-mLineaOrigen[a].origenRuta<minparadas && mLineaOrigen[a].estaciones[i].distanceTo(mLineaDestino[b].estaciones[e]) < maximoAndarTransbordo){

                            //no_dos=b;
                        //}

                        if(mLineaDestino[b].datosParadaOrigen<maximoAndar && mLineaDestino[b].origenRuta<e){

                            no_dos=b;
                        }


                        if(mLineaOrigen[a].datosParadaDestino<maximoAndar && mLineaOrigen[a].finalRuta>i){

                            no_uno=a;
                        }


                        if(
                                mLineaOrigen[a].estaciones[i].distanceTo(mLineaDestino[b].estaciones[e]) < disEncu

                                        && no_dos!=b
                                        && no_uno!=a

                                        && ((i - mLineaOrigen[a].origenRuta)>=minparadas
                                        && (mLineaDestino[b].finalRuta-e)>=minparadas)

                                        //&& mLineaOrigen[a].datosParadaOrigen<maximoAndar
                                        //&& mLineaDestino[b].datosParadaDestino<maximoAndar

                                        //&& mLineaOrigen[a].datosParadaDestino>maximoAndar
                                        && mLineaDestino[b].estaciones[e].distanceTo(origen)>maximoAndar // añadido

                                        && (i - mLineaOrigen[a].origenRuta) + (mLineaDestino[b].finalRuta - e)<parTot
                                        && (mLineaOrigen[a].datosParadaOrigen+mLineaDestino[b].datosParadaDestino)<distot
                                        //&& (mLineaOrigen[a].datosParadaOrigen < disOrigen || mLineaDestino[b].datosParadaDestino < disDestino)

                                        && !mLineaOrigen[a].nombre.equals(mLineaDestino[b].nombre)
                                        && !mLineaOrigen[a].nombre.equals(mLineaDestino[b].nombre+"Vuelta")
                                        && !(mLineaOrigen[a].nombre+"Vuelta").equals(mLineaDestino[b].nombre)
                                        && (mLineaOrigen[a].estaciones[i].distanceTo(mLineaDestino[b].estaciones[e]) < maximoAndarTransbordo)

                        ) {

                            if(disOrigen>mLineaOrigen[a].datosParadaOrigen){disOrigen=(int)mLineaOrigen[a].datosParadaOrigen;}
                            if(disDestino>mLineaDestino[b].datosParadaDestino){disOrigen=(int)mLineaOrigen[a].datosParadaOrigen;}

                            //System.out.println("LINEAS POSIBLES: "+mLineaOrigen[a].nombre+" "+mLineaDestino[b].nombre+" "+((i - mLineaOrigen[a].origenRuta) + (mLineaDestino[b].finalRuta - e)));

                            //disEncu = mLineaOrigen[a].estaciones[i].distanceTo(mLineaDestino[b].estaciones[e]);

                            encuentro_origen = mLineaOrigen[a].estaciones[i];
                            encuentro_destino = mLineaDestino[b].estaciones[e];

                            disEncu = (int)encuentro_origen.distanceTo(encuentro_destino);
                            distot=(int)mLineaOrigen[a].datosParadaOrigen+(int)mLineaDestino[b].datosParadaDestino;
                            parTot = (i - mLineaOrigen[a].origenRuta) + (mLineaDestino[b].finalRuta - e);

                            mejorLinea_origen_fin = i;
                            mejorLinea_destino_inicio = e;

                            mejorLineaTresA = mLineaOrigen[a];
                            mejorLineaTresB = mLineaDestino[b];

                            //System.out.println("numero lineas origen "+(mejorLinea_origen_fin - mejorLineaTresA.origenRuta));

                            cont = 1;



                        }if(disEncu<maximoAndarTransbordo){break;}

                    }

                }
            }
        }

        //System.out.println("PARADAS TOTALES: "+parTot);
        //System.out.println("DISTANCIA CENTRO: "+disEncu);



        if(cont==0){

           //System.out.println("NINGUNA BUENA");

            paradastot[2]=null;
            paradastot[3]=null;
            return;
        }

      System.out.println("LINEAS BUENAS DE RUTA 3: "+mejorLineaTresA.nombre+" "+mejorLineaTresB.nombre);


        // SON LA MISMA LINEA? -------------------------------------------------------
        if(mejorLineaTresA.nombre.equals(mejorLineaTresB.nombre)) {

            //System.out.println("ORIGEN Y VUELTA IGUALES");
            paradastot[2]=null;
            paradastot[3]=null;
            return;
        }

        if((mejorLinea_origen_fin - mejorLineaTresA.origenRuta)<minparadas){

            //System.out.println("PARADAS DE ORIGEN MENOR QUE "+maxparadas);

            paradastot[2]=null;
            paradastot[3]=null;
            return;
        }
        if(( mejorLineaTresB.finalRuta-mejorLinea_destino_inicio)<minparadas){
            //System.out.println("PARADAS DE DESTINO MENOR QUE "+maxparadas);

            paradastot[2]=null;
            paradastot[3]=null;
            return;

        }

        tiempollegada1=dimeHoraProxima(mejorLineaTresB.proximoBus,(int)mejorLineaTresA.datosParadaOrigen,mejorLineaTresA.origenRuta,mejorLinea_destino_inicio,mejorLinea_origen_fin,(int)disEncu,mejorLineaTresA.min_hastaBus,mejorLineaTresB.finalBus);

        if(tiempollegada1==0){

            //System.out.println("LA SEGUNDA LINEA NO LLEGA A TIEMPO PARA EL TRANSBORDO");

            paradastot[2]=null;
            paradastot[3]=null;
            return;

        }

        Location[] uno= new Location[mejorLinea_origen_fin - mejorLineaTresA.origenRuta + 2];
        paradastot[2] = uno;
        Location[] dos = new Location[mejorLineaTresB.finalRuta- mejorLinea_destino_inicio+2];
        paradastot[3]=dos;
        uno[0] = origen;
        dos[dos.length-1]=destino;

        for (int i = 1; i < uno.length; i++) {

            uno[i] = mejorLineaTresA.estaciones[i + mejorLineaTresA.origenRuta-1];

        }
        for (int i = 0; i < dos.length-1; i++) {

            dos[i] = mejorLineaTresB.estaciones[i + mejorLinea_destino_inicio];

        }

    }

    public void BuscaRutaCuatro(Location origen, Location destino) {

       //System.out.println(" -------------------------------------- RUTA 4");

        int cont = 0;
        int parTot = 200;
        int distot = maximoAndarTotal;  // ---------- metros maximos que quiero andar
        int disEncu = maximoAndarTotal;
        encuentro_origen = null;
        mejorLinea_origen_fin =0;
        encuentro_destino = null;
        mejorLinea_destino_inicio =0;

        mejorLineaCuatroA = null;
        mejorLineaCuatroB = null;
        int no_dos=10;
        int no_uno=10;

        for (int a = 0; a < o; a++) {

            for (int i = mLineaOrigen[a].origenRuta; i < mLineaOrigen[a].estaciones.length; i++) {

                for (int b = 0; b < d; b++) {

                    for (int e = mLineaDestino[b].finalRuta; e > -1; e--) {

                        //if(i-mLineaOrigen[a].origenRuta<minparadas && mLineaOrigen[a].estaciones[i].distanceTo(mLineaDestino[b].estaciones[e]) < maximoAndarTransbordo){

                            //no_dos=b;
                        //}

                        if(mLineaDestino[b].datosParadaOrigen<maximoAndar && mLineaDestino[b].origenRuta<e){

                            no_dos=b;
                        }


                        if(mLineaOrigen[a].datosParadaDestino<maximoAndar && mLineaOrigen[a].finalRuta>i){

                            no_uno=a;
                        }

                        if (

                                mLineaOrigen[a].estaciones[i].distanceTo(mLineaDestino[b].estaciones[e]) < disEncu

                                        && no_dos!=b
                                        && no_uno!=a

                                        && (i - mLineaOrigen[a].origenRuta)>=minparadas
                                        && (mLineaDestino[b].finalRuta-e)>=minparadas


                                        //&& mLineaOrigen[a].datosParadaDestino>maximoAndarTransbordo

                                        //&& mLineaOrigen[a].datosParadaOrigen<maximoAndar
                                        //&& mLineaDestino[b].datosParadaDestino<maximoAndar

                                        //&& mLineaOrigen[a].datosParadaDestino>maximoAndar // añadido
                                        //&& mLineaDestino[b].datosParadaOrigen>maximoAndarenUno // añadido
                                        && mLineaDestino[b].estaciones[e].distanceTo(origen)>maximoAndar // añadido

                                        //&& (i - mLineaOrigen[a].origenRuta) + (mLineaDestino[b].finalRuta - e)<parTot
                                        && (mLineaOrigen[a].datosParadaOrigen+mLineaDestino[b].datosParadaDestino)<distot

                                        //&& !mLineaDestino[b].nombre.equals("Linea3Vuelta")
                                        //&& (((mejorLineaUno==null || !mejorLineaUno.nombre.equals(mLineaDestino[b].nombre)) && (mejorLineaDosA==null || !mejorLineaDosA.nombre.equals(mLineaDestino[b].nombre))) || (paradastot[0]==null && (mejorLineaDosA==null || !mejorLineaDosA.nombre.equals(mLineaDestino[b].nombre))))

                                        && (mejorLineaDos==null || !mejorLineaDos.nombre.equals(mLineaOrigen[a].nombre))
                                        && (mejorLineaTresA==null || !mejorLineaTresA.nombre.equals(mLineaOrigen[a].nombre))

                                        //&& (mejorLineaDosA==null || !mejorLineaDosA.nombre.equals(mLineaOrigen[a].nombre))//  || (mejorLineaDosB==null || !mejorLineaDosB.nombre.equals(mLineaDestino[b].nombre)))
                                        //&& ((mejorLineaDosA==null || !mejorLineaDosA.nombre.equals(mLineaOrigen[a].nombre))  || (mejorLineaDosB==null || !mejorLineaDosB.nombre.equals(mLineaOrigen[a].nombre)))

                                        && !mLineaOrigen[a].nombre.equals(mLineaDestino[b].nombre)
                                        && !mLineaOrigen[a].nombre.equals(mLineaDestino[b].nombre+"Vuelta")
                                        && !(mLineaOrigen[a].nombre+"Vuelta").equals(mLineaDestino[b].nombre)
                                        && (mLineaOrigen[a].estaciones[i].distanceTo(mLineaDestino[b].estaciones[e]) < maximoAndarTransbordo)

                        ) {

                            encuentro_origen = mLineaOrigen[a].estaciones[i];

                            mejorLinea_origen_fin = i;

                            encuentro_destino = mLineaDestino[b].estaciones[e];

                            mejorLinea_destino_inicio = e;

                            mejorLineaCuatroA = mLineaOrigen[a];
                            mejorLineaCuatroB = mLineaDestino[b];

                            disEncu = (int)encuentro_origen.distanceTo(encuentro_destino);
                            parTot = (i - mLineaOrigen[a].origenRuta) + (mLineaDestino[b].finalRuta - e);
                            distot =(int)mLineaOrigen[a].datosParadaOrigen + (int)mLineaDestino[b].datosParadaDestino;

                            cont = 1;

                        } if(disEncu<maximoAndarTransbordo){break;}

                    }

                }
            }
        }

        if(cont==0){

            System.out.println("NINGUNA BUENA");

            paradastot[4]=null;
            paradastot[5]=null;
            return;
        }

       System.out.println("LINEAS BUENAS DE RUTA 4: "+mejorLineaCuatroA.nombre+" "+mejorLineaCuatroB.nombre);

        // SON LA MISMA LINEA? -------------------------------------------------------
        if(mejorLineaCuatroA.nombre.equals(mejorLineaCuatroB.nombre)) {

            //System.out.println("ORIGEN Y VUELTA IGUALES");

            paradastot[4]=null;
            paradastot[5]=null;
            return;
        }

        if((mejorLinea_origen_fin - mejorLineaCuatroA.origenRuta)<minparadas){

            //System.out.println("PARADAS DE ORIGEN MENOR QUE "+maxparadas);

            paradastot[4]=null;
            paradastot[5]=null;
            return;
        }
        if(( mejorLineaCuatroB.finalRuta-mejorLinea_destino_inicio)<minparadas){
            //System.out.println("PARADAS DE DESTINO MENOR QUE "+maxparadas);

            paradastot[4]=null;
            paradastot[5]=null;
            return;

        }

        tiempollegada2=dimeHoraProxima(mejorLineaCuatroB.proximoBus,(int)mejorLineaCuatroA.datosParadaOrigen,mejorLineaCuatroA.origenRuta,mejorLinea_destino_inicio,mejorLinea_origen_fin,(int)disEncu,mejorLineaCuatroA.min_hastaBus,mejorLineaCuatroB.finalBus);

        if(tiempollegada2==0){

            //System.out.println("LA SEGUNDA LINEA NO LLEGA A TIEMPO PARA EL TRANSBORDO");

            paradastot[4]=null;
            paradastot[5]=null;
            return;

        }

        Location[] uno= new Location[mejorLinea_origen_fin - mejorLineaCuatroA.origenRuta + 2];
        paradastot[4] = uno;
        Location[] dos = new Location[mejorLineaCuatroB.finalRuta- mejorLinea_destino_inicio+2];
        paradastot[5]=dos;
        uno[0] = origen;
        dos[dos.length-1]=destino;

        for (int i = 1; i < uno.length; i++) {

            uno[i] = mejorLineaCuatroA.estaciones[i + mejorLineaCuatroA.origenRuta-1];

        }
        for (int i = 0; i < dos.length-1; i++) {

            dos[i] = mejorLineaCuatroB.estaciones[i + mejorLinea_destino_inicio];

        }

    }

    public void BuscaRutaCinco(Location origen, Location destino){

       //System.out.println(" -------------------------------------- RUTA 5");

        mejorLineaCincoA=null;
        mejorLineaCincoB=null;
        int cont = 0;
        int parTot = 200;
        int disEncu = maximoAndarTotal;
        int distot = maximoAndarTotal;  // ---------- metros maximos que quiero andar
        encuentro_origen = null;
        mejorLinea_origen_fin =0;
        encuentro_destino = null;
        mejorLinea_destino_inicio =0;

        int no_dos=10;
        int no_uno=10;

            for (int a = 0; a < o; a++) {

                System.out.println("DISTANCIA "+mLineaOrigen[a].nombre+" "+mLineaOrigen[a].datosParadaDestino);

                for (int i = mLineaOrigen[a].origenRuta; i < mLineaOrigen[a].estaciones.length; i++) {

                    for (int b = 0; b < d; b++) {

                        for (int e = mLineaDestino[b].finalRuta; e > -1; e--) {

                            //if(i-mLineaOrigen[a].origenRuta<minparadas && mLineaOrigen[a].estaciones[i].distanceTo(mLineaDestino[b].estaciones[e]) < maximoAndarTransbordo){

                              //  no_dos=b;
                            //}

                            if(mLineaDestino[b].datosParadaOrigen<maximoAndar && mLineaDestino[b].origenRuta<e){

                                no_dos=b;
                            }


                            if(mLineaOrigen[a].datosParadaDestino<maximoAndar && mLineaOrigen[a].finalRuta>i){

                                no_uno=a;
                            }



                            if (

                                    mLineaOrigen[a].estaciones[i].distanceTo(mLineaDestino[b].estaciones[e]) < disEncu

                                            && ((i - mLineaOrigen[a].origenRuta)>=minparadas && (mLineaDestino[b].finalRuta-e)>=minparadas)

                                            && no_dos!=b
                                            && no_uno!=a

                                            //&& mLineaOrigen[a].datosParadaOrigen<maximoAndar
                                            //&& mLineaDestino[b].datosParadaDestino<maximoAndar

                                            //&& (mLineaOrigen[a].datosParadaDestino>maximoAndar && mLineaOrigen[a].finalRuta>i)
                                            //&& mLineaDestino[b].datosParadaOrigen>maximoAndar // añadido
                                            && mLineaDestino[b].estaciones[e].distanceTo(origen)>maximoAndar // añadido

                                            //&& (i - mLineaOrigen[a].origenRuta) + (mLineaDestino[b].finalRuta - e)<parTot
                                            //&& (mLineaOrigen[a].datosParadaOrigen+mLineaDestino[b].datosParadaDestino)<distot

                                            && (mejorLineaUno==null || !mLineaOrigen[a].nombre.equals(mejorLineaUno.nombre))
                                            && (((mejorLineaTresB==null || !mejorLineaTresB.nombre.equals(mLineaDestino[b].nombre)) || (mejorLineaTresA==null || !mejorLineaTresA.nombre.equals(mLineaOrigen[a].nombre))))
                                            && (((mejorLineaCuatroB==null || !mejorLineaCuatroB.nombre.equals(mLineaDestino[b].nombre)) || (mejorLineaCuatroA==null || !mejorLineaCuatroA.nombre.equals(mLineaOrigen[a].nombre))))
                                            && !mLineaOrigen[a].nombre.equals(mLineaDestino[b].nombre)
                                            && !mLineaOrigen[a].nombre.equals(mLineaDestino[b].nombre+"Vuelta")
                                            && !(mLineaOrigen[a].nombre+"Vuelta").equals(mLineaDestino[b].nombre)


                            ) {

                                if (mLineaOrigen[a].estaciones[i].distanceTo(mLineaDestino[b].estaciones[e]) < maximoAndarTransbordo) {

                                    encuentro_origen = mLineaOrigen[a].estaciones[i];

                                    mejorLinea_origen_fin = i;

                                    encuentro_destino = mLineaDestino[b].estaciones[e];

                                    mejorLinea_destino_inicio = e;

                                    mejorLineaCincoA = mLineaOrigen[a];
                                    mejorLineaCincoB = mLineaDestino[b];

                                    disEncu = (int)encuentro_origen.distanceTo(encuentro_destino);
                                    parTot = (i - mLineaOrigen[a].origenRuta) + (mLineaDestino[b].finalRuta - e);
                                    distot =(int)mLineaOrigen[a].datosParadaOrigen + (int)mLineaDestino[b].datosParadaDestino;

                                    cont = 1;

                                }
                            }if(disEncu<maximoAndarTransbordo){break;}

                        }

                    }
                }
            }

            if(cont==0){

                //System.out.println("NINGUNA BUENA");

                paradastot[6]=null;
                paradastot[7]=null;
                return;
            }

       System.out.println("LINEAS BUENAS DE RUTA 5: "+mejorLineaCincoA.nombre+" "+mejorLineaCincoB.nombre);

            // SON LA MISMA LINEA? -------------------------------------------------------
            if(mejorLineaCincoA.nombre.equals(mejorLineaCincoB.nombre)) {

                //System.out.println("MISMA LINEA");

                paradastot[6]=null;
                paradastot[7]=null;
                return;
            }

            if((mejorLinea_origen_fin - mejorLineaCincoA.origenRuta)<minparadas){

                //System.out.println("PARADAS DE ORIGEN MENOR QUE "+maxparadas);

                paradastot[6]=null;
                paradastot[7]=null;
                return;
            }
            if(( mejorLineaCincoB.finalRuta-mejorLinea_destino_inicio)<minparadas){
                //System.out.println("PARADAS DE DESTINO MENOR QUE "+maxparadas);

                paradastot[6]=null;
                paradastot[7]=null;
                return;

            }

            tiempollegada3=dimeHoraProxima(mejorLineaCincoB.proximoBus,(int)mejorLineaCincoA.datosParadaOrigen,mejorLineaCincoA.origenRuta,mejorLinea_destino_inicio,mejorLinea_origen_fin,(int)disEncu,mejorLineaCincoA.min_hastaBus,mejorLineaCincoB.finalBus);

            if(tiempollegada3==0){

                //System.out.println("LA SEGUNDA LINEA NO LLEGA A TIEMPO PARA EL TRANSBORDO");

                paradastot[6]=null;
                paradastot[7]=null;
                return;

            }

            Location[] uno= new Location[mejorLinea_origen_fin - mejorLineaCincoA.origenRuta + 2];
            paradastot[6] = uno;
            Location[] dos = new Location[mejorLineaCincoB.finalRuta- mejorLinea_destino_inicio+2];
            paradastot[7]=dos;
            uno[0] = origen;
            dos[dos.length-1]=destino;

            for (int i = 1; i < uno.length; i++) {

                uno[i] = mejorLineaCincoA.estaciones[i + mejorLineaCincoA.origenRuta-1];

            }
            for (int i = 0; i < dos.length-1; i++) {

                dos[i] = mejorLineaCincoB.estaciones[i + mejorLinea_destino_inicio];

            }


    }

    public boolean MiraLaHora2(Lineas linea, int queparada) {

        min_hasta_bus=0;

        if(h_actual>23){h_actual=h_actual-24;}  //  reseteamos la hora actual a su estado original antes de volver a consultar dias

        horaenmin=(h_actual*60)+m_actual;

        //int horaenmin=(h_actual*60)+m_actual;

        if((fechaHoy<6 && fechaHoy>1 && horaenmin<inicio_laboral) || (fechaHoy<5 && fechaHoy>0 && horaenmin>inicio_laboral)){ // --- SI ES LUNES A JUEVES hasta las 6:59 del dia siguiente ------------------------------------------------------------------

            //System.out.println("HOY ES LUNES A JUEVES");
            if(!linea.horarioLaboral_inicio.equals("especial") && !linea.horarioLaboral_inicio.equals("no")){

                minutosHastaBus(queparada,Integer.parseInt(linea.horarioLaboral_intervalo),linea.horarioLaboral_inicio,linea.horarioLaboral_fin);

                int horainicio_enmin=(h_inicio*60)+m_inicio;
                int horafin_enmin=(h_fin*60)+m_fin;

                linea.min_hastaBus=min_hasta_bus;
                linea.proximoBus=horainicio_enmin+(Integer.parseInt(linea.horarioLaboral_intervalo)*10000);
                linea.finalBus=horafin_enmin;

                if(horaenmin_comparar>=horainicio_enmin-minutos_cortesia_inicio && horaenmin_comparar<=horafin_enmin+(tiempoEntreEstaciones*queparada)){
                    //if((hora_comparar*60)+m_actual>=(h_inicio*60)+m_inicio-15 && (hora_comparar*60)+m_actual<=(h_fin*60)+m_fin+(tiempoEntreEstaciones*linea.origenRuta)){

                    return true;
                }

                //System.out.println("TARDE FUERA DE HORA");

                return false;
            }else if(linea.horarioLaboral_inicio.equals("especial")){

                if(linea.nombre.equals("Linea6")){

                    ArrayList<Integer> horario_linea6=dimeHoras(linea.especialLab);

                    for(int numero:horario_linea6){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }

                    return false;

                }

                if(linea.nombre.equals("Linea6Vuelta")){

                    ArrayList<Integer> horario_linea6v=dimeHoras(linea.especialLab);

                    for(int numero:horario_linea6v){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }

                    return false;

                }

                if(linea.nombre.equals("Linea7")){

                    ArrayList<Integer> horario_linea7=dimeHoras(linea.especialLab);

                    for(int numero:horario_linea7){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }

                    return false;

                }

                if(linea.nombre.equals("Linea7Vuelta")){

                    ArrayList<Integer> horario_linea7v=dimeHoras(linea.especialLab);

                    for(int numero:horario_linea7v){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }

                    return false;

                }

                //System.out.println("ESPECIAL");
                return false;
            }

            //System.out.println("NO HAY SERVICIO ESTE DIA");
            return false;

        }
        if((fechaHoy==6 && horaenmin<inicio_laboral) || (fechaHoy==5 && horaenmin>inicio_laboral)){  // --- ES VIERNES hasta las 6:45 (405 minutos) del dia siguiente ------------------------------------------------------------------

            //System.out.println("HOY ES VIERNES");
            //System.out.println("COMPROBANDO: "+linea.nombre);

            if(!linea.horarioViernes_inicio.equals("especial") && !linea.horarioViernes_inicio.equals("no")){

                minutosHastaBus(queparada,Integer.parseInt(linea.horarioViernes_intervalo),linea.horarioViernes_inicio,linea.horarioViernes_fin);

                int horainicio_enmin=(h_inicio*60)+m_inicio;
                int horafin_enmin=(h_fin*60)+m_fin;

                linea.min_hastaBus=min_hasta_bus;
                linea.proximoBus=horainicio_enmin+(Integer.parseInt(linea.horarioViernes_intervalo)*10000);
                linea.finalBus=horafin_enmin;


                if(horaenmin_comparar>=horainicio_enmin-minutos_cortesia_inicio && horaenmin_comparar<=horafin_enmin+(tiempoEntreEstaciones*queparada)){
                    //if((hora_comparar*60)+m_actual>=(h_inicio*60)+m_inicio-15 && (hora_comparar*60)+m_actual<=(h_fin*60)+m_fin+(tiempoEntreEstaciones*linea.origenRuta)){

                    //System.out.println("ESTA SI VALE");
                    return true;
                }

                //System.out.println("TARDE FUERA DE HORA");

                return false;
            }else if(linea.horarioViernes_inicio.equals("especial")){


                if(linea.nombre.equals("Linea6")){

                    ArrayList<Integer> horario_linea6=dimeHoras(linea.especialVie);

                    for(int numero:horario_linea6){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }

                    return false;

                }

                if(linea.nombre.equals("Linea6Vuelta")){

                    ArrayList<Integer> horario_linea6v=dimeHoras(linea.especialVie);

                    for(int numero:horario_linea6v){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }


                    return false;

                }

                if(linea.nombre.equals("Linea7")){

                    ArrayList<Integer> horario_linea7=dimeHoras(linea.especialVie);

                    for(int numero:horario_linea7){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }

                    return false;

                }

                if(linea.nombre.equals("Linea7Vuelta")){

                    ArrayList<Integer> horario_linea7v=dimeHoras(linea.especialVie);

                    for(int numero:horario_linea7v){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }


                    return false;

                }


                //System.out.println("ESPECIAL");
                return false;
            }
            //System.out.println("NO HAY SERVICIO ESTE DIA");

            return false;

        }

        if((fechaHoy==0 && horaenmin<(inicio_sabado)) || (fechaHoy==6 && horaenmin>inicio_sabado)) {  // -- ES SABADO hasta las 7:10 (430 minutos) del dia siguiente ----------------------------------------------------------------

            //System.out.println("HOY ES SABADO");

            if (!linea.horarioSabado_inicio.equals("especial") && !linea.horarioSabado_inicio.equals("no")) {

                minutosHastaBus(queparada,Integer.parseInt(linea.horarioSabado_intervalo),linea.horarioSabado_inicio,linea.horarioSabado_fin);
                int horainicio_enmin=(h_inicio*60)+m_inicio;
                int horafin_enmin=(h_fin*60)+m_fin;

                linea.min_hastaBus=min_hasta_bus;
                linea.proximoBus=horainicio_enmin+(Integer.parseInt(linea.horarioSabado_intervalo)*10000);
                linea.finalBus=horafin_enmin;


                if(horaenmin_comparar>=horainicio_enmin-minutos_cortesia_inicio && horaenmin_comparar<=horafin_enmin+(tiempoEntreEstaciones*queparada)){
                    //if((hora_comparar*60)+m_actual>=(h_inicio*60)+m_inicio-15 && (hora_comparar*60)+m_actual<=(h_fin*60)+m_fin+(tiempoEntreEstaciones*linea.origenRuta)){

                    //System.out.println("ESTA SI VALE");
                    return true;
                }else {

                    //System.out.println("TARDE FUERA DE HORA");

                    return false;
                }
            }else if(linea.horarioSabado_inicio.equals("especial")){

                if(linea.nombre.equals("Linea6")){

                    ArrayList<Integer> horario_linea6=dimeHoras(linea.especialSab);

                    for(int numero:horario_linea6){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }

                    return false;

                }

                if(linea.nombre.equals("Linea6Vuelta")){

                    ArrayList<Integer> horario_linea6v=dimeHoras(linea.especialSab);

                    for(int numero:horario_linea6v){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }

                    return false;

                }

                //System.out.println("ESPECIAL");
                return false;
            }

            //System.out.println("NO HAY SERVICIO ESTE DIA");
            return false;

        }

        if((fechaHoy==1 && horaenmin<inicio_festivo) || (fechaHoy==0 && horaenmin>inicio_festivo)){  // --- ES FESTIVO hasta las 7:00 (420 minutos) del dia siguiente ----------------------------------------------------------------

            //System.out.println("HOY ES FESTIVO");
            if(!linea.horarioFestivo_inicio.equals("especial") && !linea.horarioFestivo_inicio.equals("no")){

                minutosHastaBus(queparada,Integer.parseInt(linea.horarioFestivo_intervalo),linea.horarioFestivo_inicio,linea.horarioFestivo_fin);
                int horainicio_enmin=(h_inicio*60)+m_inicio;
                int horafin_enmin=(h_fin*60)+m_fin;

                linea.min_hastaBus=min_hasta_bus;
                linea.proximoBus=horainicio_enmin+(Integer.parseInt(linea.horarioFestivo_intervalo)*10000);
                linea.finalBus=horafin_enmin;

                if(horaenmin_comparar>=horainicio_enmin-minutos_cortesia_inicio && horaenmin_comparar<=horafin_enmin+(tiempoEntreEstaciones*queparada)){
                    //if((hora_comparar*60)+m_actual>=(h_inicio*60)+m_inicio-15 && (hora_comparar*60)+m_actual<=(h_fin*60)+m_fin+(tiempoEntreEstaciones*linea.origenRuta)){

                    //System.out.println("ESTA SI VALE");
                    return true;
                }

                //System.out.println("TARDE FUERA DE HORA");

                return false;
            }else if(linea.horarioFestivo_inicio.equals("especial")){

                if(linea.nombre.equals("Linea6")){

                    ArrayList<Integer> horario_linea6=dimeHoras(linea.especialFes);

                    for(int numero:horario_linea6){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }


                    return false;

                }

                if(linea.nombre.equals("Linea6Vuelta")){

                    ArrayList<Integer> horario_linea6v=dimeHoras(linea.especialFes);

                    for(int numero:horario_linea6v){

                        if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                            linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                            linea.min_hastaBus=numero-horaenmin;
                            return true;}

                    }

                    return false;

                }

                //System.out.println("ESPECIAL");
                return false;
            }

            //System.out.println("NO ES FESTIVO");
            return false;

        }

        //System.out.println("NO HAY SERVICIO ESTE DIA");
        return false;

    }

    public boolean MiraLaHora(Lineas linea, int queparada){

        min_hasta_bus=0;

        if(h_actual>23){h_actual=h_actual-24;}  //  reseteamos la hora actual a su estado original antes de volver a consultar dias

        horaenmin=(h_actual*60)+m_actual;

        String inicioLinea="";
        String finLinea="";
        String intervalo="";

        if((fechaHoy<6 && fechaHoy>1 && horaenmin<inicio_laboral) || (fechaHoy<5 && fechaHoy>0 && horaenmin>inicio_laboral)){

            inicioLinea=linea.horarioLaboral_inicio;
            finLinea=linea.horarioLaboral_fin;
            intervalo=linea.horarioLaboral_intervalo;

        }else if((fechaHoy==6 && horaenmin<inicio_laboral) || (fechaHoy==5 && horaenmin>inicio_laboral)){

            inicioLinea=linea.horarioViernes_inicio;
            finLinea=linea.horarioViernes_fin;
            intervalo=linea.horarioViernes_intervalo;

        }else if((fechaHoy==0 && horaenmin<(inicio_sabado)) || (fechaHoy==6 && horaenmin>inicio_sabado)){

            inicioLinea=linea.horarioSabado_inicio;
            finLinea=linea.horarioSabado_fin;
            intervalo=linea.horarioSabado_intervalo;

        }else if((fechaHoy==1 && horaenmin<inicio_festivo) || (fechaHoy==0 && horaenmin>inicio_festivo)){

            inicioLinea=linea.horarioFestivo_inicio;
            finLinea=linea.horarioFestivo_fin;
            intervalo=linea.horarioFestivo_intervalo;

        }

        if(!inicioLinea.equals("especial") && !inicioLinea.equals("no") && !inicioLinea.equals("")){

            minutosHastaBus(queparada,Integer.parseInt(intervalo),inicioLinea,finLinea);

            int horainicio_enmin=(h_inicio*60)+m_inicio;
            int horafin_enmin=(h_fin*60)+m_fin;

            linea.min_hastaBus=min_hasta_bus;
            linea.proximoBus=horainicio_enmin+(Integer.parseInt(intervalo)*10000);
            linea.finalBus=horafin_enmin;

            if(horaenmin_comparar>=horainicio_enmin-minutos_cortesia_inicio && horaenmin_comparar<=horafin_enmin+(tiempoEntreEstaciones*queparada)){

                return true;
            }

            //System.out.println("TARDE FUERA DE HORA");
            return false;

        }else if(inicioLinea.equals("especial")){

            ArrayList<Integer> horario_especial=dimeHoras(linea.especialLab);

            for(int numero:horario_especial){

                if(numero-horaenmin>-1 && numero-horaenmin<minespera){
                    linea.proximoBus=numero+(int)(queparada*tiempoEntreEstaciones);
                    linea.min_hastaBus=numero-horaenmin;
                    return true;}

            }

            //System.out.println("ESPECIAL");
            return false;
        }

        //System.out.println("NO HAY SERVICIO ESTE DIA");
        return false;

    }

    public void minutosHastaBus(int origen_de_ruta,int intervalo,String hinicio,String hfin){


        Calendar hora_inicio=Calendar.getInstance();
        Calendar hora_fin=Calendar.getInstance();
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");


        try {
            hora_inicio.setTime(formatoHora.parse(hinicio));
            hora_fin.setTime(formatoHora.parse(hfin));
            h_inicio =hora_inicio.get(Calendar.HOUR_OF_DAY);
            m_inicio =hora_inicio.get(Calendar.MINUTE);

            h_fin =hora_fin.get(Calendar.HOUR_OF_DAY);
            m_fin =hora_fin.get(Calendar.MINUTE);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Pone hora de salida de la primera linea e intervalo

        // ----------- La hora de inicio de linea es entre 0 y 5
        if(h_inicio>-1 && h_inicio<6){

            h_inicio=h_inicio+24;
            h_fin=h_fin+24;

        }else if(h_inicio==23){

            h_fin=h_fin+24;

        }

        if(horaenmin<360){ // la hora actual es menor que las 6:00
            //if(h_actual<7 && h_actual>=0){

            horaenmin_comparar=((h_actual+24)*60)+m_actual;

        }else{

            hora_comparar=h_actual;
            horaenmin_comparar=horaenmin;
        }

        min_esta_estacion=(int)(tiempoEntreEstaciones*origen_de_ruta);
        //min_esta_estacion=(int)(m_inicio+(tiempoEntreEstaciones*origen_de_ruta));
        int horaenmin_inicio=(h_inicio*60)+m_inicio;


        if(horaenmin_comparar<horaenmin_inicio){  // --- Todavia no ha empezado el servicio de Bus

            //System.out.println("TODAVIA NO HA EMPEZADO EL SERVICIO "+horaenmin_comparar+" < "+horaenmin_inicio);

            //min_hasta_bus=(horaenmin_inicio-horaenmin_comparar)+min_esta_estacion + (intervalo * 10000);
            min_hasta_bus=(horaenmin_inicio-horaenmin_comparar)+min_esta_estacion + (intervalo * 10000);

        }else {

            if (horaenmin_comparar < horaenmin_inicio + min_esta_estacion) {

                while (horaenmin_comparar < horaenmin_inicio + min_esta_estacion) {

                    min_esta_estacion = min_esta_estacion - intervalo;
                }

                min_hasta_bus = (horaenmin_comparar - (horaenmin_inicio + min_esta_estacion)) + (intervalo * 10000);

            }

            if (horaenmin_comparar >= horaenmin_inicio + min_esta_estacion) {

                while (horaenmin_comparar >= horaenmin_inicio + min_esta_estacion) {

                    min_esta_estacion = min_esta_estacion + intervalo;
                }

                min_hasta_bus = (horaenmin_inicio + (min_esta_estacion - horaenmin_comparar)) + (intervalo * 10000);

            }
        }

    }

    public ArrayList<Integer> dimeHoras(String linea){

        ArrayList<Integer> numeros=new ArrayList<>();


        char[] cadena=linea.toCharArray();
        String n="";

        for(int e=0;e<cadena.length;e++){

            if(Character.isDigit(cadena[e])){

                n+=cadena[e];
            }else if(cadena[e]==','){

                numeros.add(Integer.parseInt(n));
                n="";
            }
            if(e==(cadena.length-1)){

                numeros.add(Integer.parseInt(n));

            }

        }

        return numeros;


    }

    public int dimeHoraProxima(int proximoBus,int distanciaOrigen, int origenRuta, int destino_inicio, int origen_fin, int disEncuentro,int minHastaBus,int finalBus){

        int intervalo=(proximoBus/10000);
        int horainicio=proximoBus-(intervalo*10000);

        //System.out.println("HORA ACTUAL: "+h_actual+":"+m_actual);
        int horaactual=(h_actual*60)+m_actual;

        if(horaactual<360){  // SI LA HORA DE INICIO DEL BUS ES MENOR DE LAS 6:00 AÑADE 24 H

            horaactual=horaactual+(24*60);
        }

        int tiempo_llegar=(distanciaOrigen/65)+(int)((origen_fin-origenRuta)*tiempoEntreEstaciones)+(disEncuentro/65)+(minHastaBus%10000);
        int horafinalmax=finalBus+(int)(tiempoEntreEstaciones*destino_inicio);
        horainicio=horainicio+(int)(tiempoEntreEstaciones*destino_inicio);
        horaactual=horaactual+tiempo_llegar;

        //System.out.println("PROXIMO BUS: "+proximoBus);

        //System.out.println("INTERVALO: "+intervalo);
        //System.out.println("PARADAS DESDE INICIO: "+destino_inicio);
        //System.out.println("HORA INICIO: "+(horainicio/60)+":"+horainicio%60);
        //System.out.println("TIEMPO EN LLEGAR A LINEA 2: "+tiempo_llegar);

        if(horaactual>horainicio) {

            //System.out.println(" ------------- ENTRA 1");

            if(intervalo!=0) {

                while (horaactual >= horainicio) {

                    horainicio = horainicio + intervalo;

                }
            }

            //System.out.println("------ PROXIMO BUS LINEA B: "+(horainicio/60)+":"+(horainicio%60));

            horainicio=horainicio+(10000*intervalo);

            //System.out.println("------ TIEMPO EN LLEGAR: "+horaactual);

            //System.out.println("------ HORA FINAL MAX: "+horafinalmax);

           if(horaactual>horafinalmax){return 0;}
             return horainicio;

        }else

        if(horaactual<horainicio) {

            //System.out.println(" ------------- ENTRA 2");

            //System.out.println("------- PROXIMO BUS LINEA B: "+(horainicio/60)+":"+(horainicio%60));

            horainicio=horainicio+(10000*intervalo);


             return horainicio;
        }

        //System.out.println(" ------------- ENTRA 3");

        horainicio=horainicio+(10000*intervalo);

        return horainicio;

    }

    public boolean origenCercaEstDestino(Lineas linea,Location origen){

        for(int i=0;i<linea.estaciones.length;i++){

            if(origen.distanceTo(linea.estaciones[i])<200){

                return false;
            }
        }

        return true;
    }

    public String dimeSentido(String nombre){

        String resultado="";

        for(int i=0;i<todasLineas.length;i++){

            if(todasLineas[i].nombre.equals(nombre)){

                resultado=todasLineas[i].sentido;
            }

        }

        return resultado;

    }

}