package com.saraodigital.takemitu;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.animation.IntEvaluator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Arrays;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_APPEND;

public class Paradas extends Fragment {

    // VARIABLES MODIFICABLES ----------------------

    int maxCerca=400;

    // -------------------------------------------

    private Lineas[] lineas;

    private SentidoParadas infoParadas;

    private String sentido, numero;

    private Bundle miBundle;

    private Location migps;

    private ListaNotas infonota;

    private int iconocerca=0;
    private int icononotas=0;

    private int lineasentido;

    private FrameLayout cajabus;

    private androidx.appcompat.app.ActionBar menu;

    private TextView notainfo;

    private SwipeRefreshLayout swipe;

    private LinearLayout rutaContenedor;

    private LayoutInflater inflador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_paradas, container, false);

        rutaContenedor = v.findViewById(R.id.pantalla_lineas);

        inflador = (LayoutInflater) requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        migps=new Location("");

        swipe=v.findViewById(R.id.swipe);

        notainfo=v.findViewById(R.id.notainfo);

        ponDatos(v);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(migps.distanceTo(ContenedorInicio.migps)>50){

                    rutaContenedor.removeAllViews();

                    ponDatos(v);

                }

                swipe.setRefreshing(false);


            }
        });





        // -------------------------------


        /*
        for (int i=0;i<lineas.length;i++){

            icononotas=0;
            iconocerca=0;

            if(i>19){

                lineasentido=3;
            }else{

                lineasentido=10;
            }

            if(!lineas[i].nombre.contains("Vuelta")) {

                if(lineas[i].valorNota==1 || lineas[i+lineasentido].valorNota==1){

                    icononotas=1;
                }

                sentido = infoParadas.dimeNombres(lineas[i].nombre)[1]+" - ";
                numero = infoParadas.dimeNombres(lineas[i].nombre)[0];

                for(int a=0;a<lineas[i].estaciones.length;a++){

                    if(lineas[i].estaciones[a].distanceTo(migps)<maxCerca){

                        iconocerca=1;


                        //dimecerca=getString(R.string.cerca);
                    }
                }

                for (int e = i + 1; e < lineas.length; e++) {

                    if (lineas[e].nombre.contains("Vuelta") && infoParadas.dimeNombres(lineas[e].nombre)[0] == numero) {

                        sentido=sentido+(infoParadas.dimeNombres(lineas[e].nombre)[1]);
                    }
                }

                final int numero_envio=i;

                pintaLinea(inflador, numero, sentido, rutaContenedor,i, iconocerca, icononotas);


                v.findViewById(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        irParadas(numero_envio);

                        // ------- SITIOS ---------------------------

                    }
                });

            }

        }

         */

        return v;

    }

    private void ponDatos(View v){

        migps.setLatitude(ContenedorInicio.migps.getLatitude());
        migps.setLongitude(ContenedorInicio.migps.getLongitude());

        // ---- PONE LA NOTA ----------



        infonota = ContenedorInicio.notas[0];

        if(infonota.getValor().equals("1")) {

            notainfo.setText(infonota.getDetalle());
        }else{

            notainfo.setVisibility(View.GONE);
        }



        // ---------------------------------


        miBundle=getActivity().getIntent().getExtras();

        Parcelable[] datos=miBundle.getParcelableArray("LINEAS");

        lineas= Arrays.copyOf(datos,datos.length,Lineas[].class);

        infoParadas =new SentidoParadas();

        menu=((ContenedorInicio)getActivity()).getSupportActionBar();

        menu.setTitle(getString(R.string.seleccionalinea));

        // NUEVO ------------------------

        for (int i=0;i<lineas.length;i++){

            icononotas=0;
            iconocerca=0;


            if(!lineas[i].nombre.contains("Vuelta")) {

                if(lineas[i].valorNota==1 || lineas[i+1].valorNota==1){

                    icononotas=1;
                }

                sentido = lineas[i].sentido+" - ";
                numero = infoParadas.dimeNombres(lineas[i].nombre);

                for(int a=0;a<lineas[i].estaciones.length;a++){

                    if(lineas[i].estaciones[a].distanceTo(migps)<maxCerca){

                        iconocerca=1;


                        //dimecerca=getString(R.string.cerca);
                    }
                }

                for (int e = i + 1; e < lineas.length; e++) {

                    if (lineas[e].nombre.contains("Vuelta") && infoParadas.dimeNombres(lineas[e].nombre).equals(numero)) {

                        sentido=sentido+lineas[e].sentido;
                    }
                }

                final int numero_envio=i;
                final String a_que_linea=lineas[i].nombre;

                pintaLinea(inflador, numero, sentido, rutaContenedor,i, iconocerca, icononotas,lineas[i].color);


                v.findViewById(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        irParadas(a_que_linea);

                        // ------- SITIOS ---------------------------

                    }
                });

            }

        }



    }

    private void pintaLinea(LayoutInflater inflador, String numero, String sentido, LinearLayout contenedor,int i, int cerca, int nota,String colorlinea){

        LinearLayout distanciaEstaciones=(LinearLayout)inflador.inflate(R.layout.barra_lineas,null);

        Location gps=ContenedorInicio.migps;




        // CAMBIA DE COLOR A LA LINEA -------------------------------------
        //cajabus=distanciaEstaciones.findViewById(R.id.cajabus);
        //int color=Color.parseColor(colorlinea);
        //cajabus.setBackgroundTintList(ColorStateList.valueOf(color));



        Float poncerca=0f;
        Float ponnota=0f;


        if(cerca==1){poncerca=1f;}
        if(nota==1){ponnota=1f;}





        ((TextView)distanciaEstaciones.findViewById(R.id.num_linea)).setText(numero);
        ((TextView)distanciaEstaciones.findViewById(R.id.sentido_linea)).setText(sentido);
        distanciaEstaciones.findViewById(R.id.icono_cerca).setAlpha(poncerca);
        distanciaEstaciones.findViewById(R.id.icono_notas).setAlpha(ponnota);

        distanciaEstaciones.setId(i);

        contenedor.addView(distanciaEstaciones); // vamos añadiendo imagenes y textos al contenedor

    }


    public void irParadas(String aquelinea){

        ContenedorInicio.aquelinea=aquelinea;

        Navigation.findNavController(getView()).navigate(R.id.action_nav_lineas_to_nav_listaparadas);

    }

}
