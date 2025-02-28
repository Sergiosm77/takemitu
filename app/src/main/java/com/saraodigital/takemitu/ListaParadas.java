package com.saraodigital.takemitu;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ListaParadas extends Fragment {

    // VARIABLES MODIFICABLES ----------------------

    int maxCerca=400;
    int min_tolerancia=7;

    // -------------------------------------------

    private LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

    private TextView texto,distancia,horarioLaboral,horarioSabados,horarioFestivos,notasLineas;
    private Lineas[] lineas;

    private String nombreparada,ladistancia,horLab,horSab,horFest;

    private NavController navController;

    private int distanciaAparada;

    private SentidoParadas infoParadas;

    private FloatingActionButton invertir;

    private Button verMapa;

    private Location migps,queparada;

    private int numerolinea,numero;

    private DecimalFormat formato1;

    private LinearLayout rutaContenedor;

    private LayoutInflater inflador;

    private ScrollView scroll;

    private boolean sentidoida;

    private DatosViewModel viewModel;

    private String aquelinea=ContenedorInicio.aquelinea;

    private androidx.appcompat.app.ActionBar menu;

    int minutos_cortesia_inicio=20;
    // --- Horas de inicio de los buses en minutos:
    int inicio_laboral=415-minutos_cortesia_inicio;
    int inicio_sabado=430-minutos_cortesia_inicio;
    int inicio_festivo=435-minutos_cortesia_inicio;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_lista_paradas, container, false);

        menu=((ContenedorInicio)getActivity()).getSupportActionBar();

        menu.setTitle(getString(R.string.infodelinea));

        rutaContenedor = v.findViewById(R.id.pantalla_paradas);

        viewModel =new ViewModelProvider(getActivity()).get(DatosViewModel.class);

        inflador = (LayoutInflater) requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        //System.out.println("LLEGA HASTA PARADAS");

        navController = Navigation.findNavController(getActivity(), R.id.content_frame);

        texto=v.findViewById(R.id.nombreLinea);
        distancia=v.findViewById(R.id.distancia);
        horarioLaboral=v.findViewById(R.id.horarioLaboral);
        horarioSabados=v.findViewById(R.id.horarioSabados);
        horarioFestivos=v.findViewById(R.id.horarioFestivos);
        notasLineas=v.findViewById(R.id.notasLinea);

        scroll=v.findViewById(R.id.scroll_paradas);

        invertir=v.findViewById(R.id.sentido_inverso);
        verMapa=v.findViewById(R.id.verLineaMapa);

        migps=new Location("");
        migps.setLatitude(Buscador.migps.getLatitude());
        migps.setLongitude(Buscador.migps.getLongitude());

        formato1 = new DecimalFormat("#.0");

        sentidoida=true;

        Bundle miBundle=getActivity().getIntent().getExtras();

        if(miBundle!=null) {

            Parcelable[] datos = miBundle.getParcelableArray("LINEAS");

            lineas = Arrays.copyOf(datos, datos.length, Lineas[].class);

        }

        for(int i=0;i<lineas.length;i++){

            if(lineas[i].nombre.equals(aquelinea)){

                numerolinea=i;
            }

        }

        verMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                muestraRutaLinea(lineas[numero]);

            }
        });

        invertir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sentidoida){

                    sentidoInverso();
                }else{

                    sentidoIda();
                }

            }
        });


        infoParadas=new SentidoParadas();

        if(lineas!=null){
            sentidoIda();

        }else{

            Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Sin acceso a la base de datos", Toast.LENGTH_LONG);
            toast1.setGravity(0, 0, 0);
            toast1.show();

        }

        return v;
    }

    private void sentidoInverso(){


        numero=numerolinea+1;


        if(aquelinea.equals("Linea6")){


            String horasLab=dimeHoras(lineas[numero].especialLab);
            String horasSab=dimeHoras(lineas[numero].especialSab);
            String horasFes=dimeHoras(lineas[numero].especialFes);

            horLab="Laborables: "+horasLab;
            horSab="Sábados: "+horasSab;
            horFest="Festivos: "+horasFes;

        }else if(aquelinea.equals("Linea7")){

            String horasLab=dimeHoras(lineas[numero].especialLab);
            String horasSab=dimeHoras(lineas[numero].especialSab);
            String horasFes=dimeHoras(lineas[numero].especialFes);

            horLab="Laborables: "+horasLab;
            horSab="Sábados: "+horasSab;
            horFest="Festivos: "+horasFes;

            //horLab="Lunes a Viernes: 13:05, 14:15, 15:15, 18:15, 19:15, 20:15, 22:15";
            //horSab="Sábados sin servicio";
            //horFest="Festivos sin servicio";
        }else {


            if(lineas[numero].horarioLaboral_inicio.equals("no")){
                if(lineas[numero].horarioViernes_inicio.equals("no")) {
                    horLab = "Laborables sin servicio";
                }else{
                    horLab="Viernes: Inicio "+lineas[numero].horarioViernes_inicio+" / Fin "+lineas[numero].horarioViernes_fin+" - Intervalo "+lineas[numero].horarioViernes_intervalo+" min.";
                }
            }else{
                horLab = "Laborables: Inicio " + lineas[numero].horarioLaboral_inicio + " / Fin " + lineas[numero].horarioLaboral_fin + " - Intervalo " + lineas[numero].horarioLaboral_intervalo + " min.";
            }
            if(lineas[numero].horarioSabado_inicio.equals("no")){horSab="Sábados sin servicio";}else{horSab = "Sábados: Inicio " + lineas[numero].horarioSabado_inicio + " / Fin " + lineas[numero].horarioSabado_fin + " - Intervalo " + lineas[numero].horarioSabado_intervalo + " min.";}
            if(lineas[numero].horarioFestivo_inicio.equals("no")){horFest="Festivos sin servicio";}else{horFest = "Festivos: Inicio " + lineas[numero].horarioFestivo_inicio + " / Fin " + lineas[numero].horarioFestivo_fin + " - Intervalo " + lineas[numero].horarioFestivo_intervalo + " min.";}
        }

        horarioLaboral.setText(horLab);
        horarioSabados.setText(horSab);
        horarioFestivos.setText(horFest);

        if(lineas[numero].valorNota==0){

            notasLineas.setEnabled(false);
            notasLineas.setMaxLines(0);
            notasLineas.setAlpha(0);

        }else{

            notasLineas.setEnabled(true);
            notasLineas.setMaxLines(5);
            notasLineas.setAlpha(1);

            notasLineas.setText("Nota: "+lineas[numero].detalleNota);


        }

        rutaContenedor.removeAllViews();

        //texto.setText(getString(R.string.linea)+" "+infoParadas.dimeNombres(lineas[numero].nombre)[0]+"\n"+getString(R.string.sentido)+" "+infoParadas.dimeNombres(lineas[numero].nombre)[1]);
        texto.setText(getString(R.string.linea)+" "+infoParadas.dimeNombres(lineas[numero].nombre)+"\n"+getString(R.string.sentido)+" "+lineas[numero].sentido);

        //pintaParada(inflador,null,0,rutaContenedor, 0,0,null);

        for(int i=0;i<lineas[numero].estaciones.length;i++){

            nombreparada=lineas[numero].estaciones[i].getProvider();

            distanciaAparada=(int)migps.distanceTo(lineas[numero].estaciones[i]);

            if(i==0){pintaParada(inflador,nombreparada,distanciaAparada,rutaContenedor, R.drawable.bustop_linear_start,1,lineas[numero].estaciones[i],lineas[numero],i);}
            if(i==lineas[numero].estaciones.length-1){pintaParada(inflador,nombreparada,distanciaAparada,rutaContenedor, R.drawable.bustop_linear_end,1,lineas[numero].estaciones[i],lineas[numero],i);}
            if(i>0 && i<lineas[numero].estaciones.length-1){pintaParada(inflador,nombreparada,distanciaAparada,rutaContenedor, R.drawable.bustop_linear,0,lineas[numero].estaciones[i],lineas[numero],i);}
        }
        //pintaParada(inflador,null,0,rutaContenedor, 0,0,null);
        //pintaParada(inflador,null,0,rutaContenedor, 0,0,null);
        sentidoida=false;
        scroll.fullScroll(ScrollView.FOCUS_UP);

    }

    private void sentidoIda(){

                numero=numerolinea;

        if(aquelinea.equals("Linea6")){

            String horasLab=dimeHoras(lineas[numero].especialLab);
            String horasSab=dimeHoras(lineas[numero].especialSab);
            String horasFes=dimeHoras(lineas[numero].especialFes);

            horLab="Laborables: "+horasLab;
            horSab="Sábados: "+horasSab;
            horFest="Festivos: "+horasFes;

        }else if(aquelinea.equals("Linea7")){

            String horasLab=dimeHoras(lineas[numero].especialLab);
            String horasSab=dimeHoras(lineas[numero].especialSab);
            String horasFes=dimeHoras(lineas[numero].especialFes);

            horLab="Laborables: "+horasLab;
            horSab="Sábados: "+horasSab;
            horFest="Festivos: "+horasFes;

        }else {

            if(lineas[numero].horarioLaboral_inicio.equals("no")){
                if(lineas[numero].horarioViernes_inicio.equals("no")) {
                    horLab = "Laborables sin servicio";
                }else{
                    horLab="Viernes: Inicio "+lineas[numero].horarioViernes_inicio+" / Fin "+lineas[numero].horarioViernes_fin+" - Intervalo "+lineas[numero].horarioViernes_intervalo+" min.";
                }
            }else{
                horLab = "Laborables: Inicio " + lineas[numero].horarioLaboral_inicio + " / Fin " + lineas[numero].horarioLaboral_fin + " - Intervalo " + lineas[numero].horarioLaboral_intervalo + " min.";
            }
            if(lineas[numero].horarioSabado_inicio.equals("no")){horSab="Sábados sin servicio";}else{horSab = "Sábados: Inicio " + lineas[numero].horarioSabado_inicio + " / Fin " + lineas[numero].horarioSabado_fin + " - Intervalo " + lineas[numero].horarioSabado_intervalo + " min.";}
            if(lineas[numero].horarioFestivo_inicio.equals("no")){horFest="Festivos sin servicio";}else{horFest = "Festivos: Inicio " + lineas[numero].horarioFestivo_inicio + " / Fin " + lineas[numero].horarioFestivo_fin + " - Intervalo " + lineas[numero].horarioFestivo_intervalo + " min.";}
        }

        horarioLaboral.setText(horLab);
        horarioSabados.setText(horSab);
        horarioFestivos.setText(horFest);

        if(lineas[numero].valorNota==0){

            notasLineas.setEnabled(false);
            notasLineas.setMaxLines(0);
            notasLineas.setAlpha(0);

        }else{

            notasLineas.setEnabled(true);
            notasLineas.setMaxLines(4);
            notasLineas.setAlpha(1);

            notasLineas.setText("Nota: "+lineas[numero].detalleNota);


        }

        rutaContenedor.removeAllViews();

        //texto.setText(getString(R.string.linea)+" "+infoParadas.dimeNombres(lineas[numero].nombre)[0]+"\n"+getString(R.string.sentido)+" "+infoParadas.dimeNombres(lineas[numero].nombre)[1]);
        texto.setText(getString(R.string.linea)+" "+infoParadas.dimeNombres(lineas[numero].nombre)+"\n"+getString(R.string.sentido)+" "+lineas[numero].sentido);

        //pintaParada(inflador,null,0,rutaContenedor, 0,0,null);

        for(int i=0;i<lineas[numero].estaciones.length;i++){

            nombreparada=lineas[numero].estaciones[i].getProvider();

            if(migps.getLatitude()==0){

                distanciaAparada=-1;
            }else {

                distanciaAparada = (int) migps.distanceTo(lineas[numero].estaciones[i]);
            }

            if(i==0){pintaParada(inflador,nombreparada,distanciaAparada,rutaContenedor, R.drawable.bustop_linear_start,1,lineas[numero].estaciones[i],lineas[numero],i);}
            if(i==lineas[numero].estaciones.length-1){pintaParada(inflador,nombreparada,distanciaAparada,rutaContenedor, R.drawable.bustop_linear_end,1,lineas[numero].estaciones[i],lineas[numero],i);}
            if(i>0 && i<lineas[numero].estaciones.length-1){pintaParada(inflador,nombreparada,distanciaAparada,rutaContenedor, R.drawable.bustop_linear,0,lineas[numero].estaciones[i],lineas[numero],i);}
        }
        //pintaParada(inflador,null,0,rutaContenedor, 0,0,null);
        //pintaParada(inflador,null,0,rutaContenedor, 0,0,null);
        sentidoida=true;
        scroll.fullScroll(ScrollView.FOCUS_UP);

    }

    private String dimeHoras(String linea){

        ArrayList<Integer> numeros=new ArrayList<>();

        String horas="";

        if(linea==null){

            horas="Sin servicio";
            return horas;
        }


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

        int contador=0;

        for(int numero:numeros){

            horas+=(numero/60)+":";

            if(numero%60<10 && numero%60>0){

                horas+="0"+numero%60;
            }else{
                horas+=numero%60;
            }

            if(numero%60==0){

                horas+="0";
            }

            contador++;

            if(numeros.size()!=contador){

                horas+=", ";
            }

        }

        return horas;


    }


    private void pintaParada(LayoutInflater inflador, final String parada, final int distancia, LinearLayout contenedor, int imagen,int tamtexto,final Location locparada, final Lineas linea, final int numParada){

        if(distancia<maxCerca && distancia>0){

                ladistancia = getString(R.string.estasA) + " " + distancia + " " + getString(R.string.metros);

        }else{ladistancia="";}

        RelativeLayout distanciaEstaciones=(RelativeLayout) inflador.inflate(R.layout.barra_paradas,null);


        distanciaEstaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                queparada=locparada;

                detalleParada(parada, distancia,CuandoPasa(linea, numParada));


            }
        });

        ((TextView)distanciaEstaciones.findViewById(R.id.queparada)).setText(parada);
        ((TextView)distanciaEstaciones.findViewById(R.id.distancia)).setText(ladistancia);
        ((ImageView)distanciaEstaciones.findViewById(R.id.icono_paradas)).setImageResource(imagen);

        if(tamtexto==1){

            ((TextView) distanciaEstaciones.findViewById(R.id.queparada)).setTypeface(Typeface.DEFAULT_BOLD);
            //((TextView) distanciaEstaciones.findViewById(R.id.queparada)).setTextColor(getResources().getColor(R.color.colorNaranja,null));

        }

        contenedor.addView(distanciaEstaciones); // vamos añadiendo imagenes y textos al contenedor

    }

    private void detalleParada(String nota1, int distancia,String cuando){

        LayoutInflater inflater = getLayoutInflater();
        final View detalle = inflater.inflate(R.layout.detalle_parada, null);

        TextView notaParada1=detalle.findViewById(R.id.notaparada1);
        TextView notaParada2=detalle.findViewById(R.id.notaparada2);
        TextView cuandoPasa=detalle.findViewById(R.id.bus_siguiente);
        Button irParada=detalle.findViewById(R.id.irparada);
        Button irMapa=detalle.findViewById(R.id.irmapa);
        Button cancela=detalle.findViewById(R.id.cancela_parada);

        notaParada1.setText(nota1);
        cuandoPasa.setText(cuando);

        String aCuanto="";

        if(distancia==-1){

            aCuanto=getString(R.string.sin_gps);

        }else if(distancia>1000){

            aCuanto=getString(R.string.estasA) + " " + formato1.format(Double.valueOf(distancia)/1000) + " Km";

        }else {
            aCuanto=getString(R.string.estasA) + " " + distancia + " " + getString(R.string.metros);

        }

        notaParada2.setText(aCuanto);

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        // this is set the view from XML inside AlertDialog
        alert.setView(detalle);
        alert.setCancelable(true);

        final AlertDialog dialog = alert.create();
        final String mandadistancia=aCuanto;

        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                dialog.cancel();

            }
        });

        irParada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                queparada.setProvider("Parada: "+queparada.getProvider());

                viewModel.setLocalizacion(queparada);

                navController.navigate(R.id.action_nav_listaparadas_to_home);

                dialog.cancel();

            }
        });

        cancela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

            }
        });

        irMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //viewModel.setLocalizacion(irA);

                //navController.navigate(R.id.action_nav_quesitio_to_home);

                ContenedorInicio.queMapa=4;
                Intent miIntent=new Intent(getActivity(), MapsActivity.class);

                miIntent.putExtra("PARADA",queparada);
                miIntent.putExtra("DISTANCIA",mandadistancia);
                miIntent.putExtra("LINEAMAPA", lineas[numero]);

                startActivity(miIntent);

                dialog.cancel();

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

    }

    public String CuandoPasa(Lineas linea, int queparada){

        int min_hasta_bus=-1;
        double tiempoEntreEstaciones=1.35;

        Calendar hora_inicio=Calendar.getInstance();
        Calendar hora_fin=Calendar.getInstance();
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");

        int h_actual=0;
        int m_actual=0;
        int horaenmin=0;
        int fechaHoy;

        fechaHoy=Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid")).get(Calendar.DAY_OF_WEEK)-1;


        try {
            h_actual =hora_inicio.get(Calendar.HOUR_OF_DAY);
            m_actual =hora_inicio.get(Calendar.MINUTE);


        } catch (Exception e) {
            e.printStackTrace();
        }


        horaenmin=(h_actual*60)+m_actual;

        String inicioLinea="";
        String finLinea="";
        String intervalo="";
        String especial="";

        if((fechaHoy<6 && fechaHoy>1 && horaenmin<inicio_laboral) || (fechaHoy<5 && fechaHoy>0 && horaenmin>inicio_laboral)){

            inicioLinea=linea.horarioLaboral_inicio;
            finLinea=linea.horarioLaboral_fin;
            intervalo=linea.horarioLaboral_intervalo;
            if(linea.especialLab!=null){especial=linea.especialLab;}

        }else if((fechaHoy==6 && horaenmin<inicio_laboral) || (fechaHoy==5 && horaenmin>inicio_laboral)){

            inicioLinea=linea.horarioViernes_inicio;
            finLinea=linea.horarioViernes_fin;
            intervalo=linea.horarioViernes_intervalo;
            if(linea.especialVie!=null){especial=linea.especialVie;}

        }else if((fechaHoy==0 && horaenmin<(inicio_sabado)) || (fechaHoy==6 && horaenmin>inicio_sabado)){

            inicioLinea=linea.horarioSabado_inicio;
            finLinea=linea.horarioSabado_fin;
            intervalo=linea.horarioSabado_intervalo;
            if(linea.especialSab!=null){especial=linea.especialSab;}

        }else if((fechaHoy==1 && horaenmin<inicio_festivo) || (fechaHoy==0 && horaenmin>inicio_festivo)){

            inicioLinea=linea.horarioFestivo_inicio;
            finLinea=linea.horarioFestivo_fin;
            intervalo=linea.horarioFestivo_intervalo;
            if(linea.especialFes!=null){especial=linea.especialFes;}

        }

        if(!inicioLinea.equals("especial") && !inicioLinea.equals("no") && !inicioLinea.equals("")){

            min_hasta_bus=minutosHastaBus(queparada,Integer.parseInt(intervalo),inicioLinea,finLinea,h_actual,m_actual,tiempoEntreEstaciones);

        }else if(inicioLinea.equals("especial") && !especial.equals("")){

            ArrayList<Integer> horario_especial=dimeHorasLinea(especial);

            for(int numero:horario_especial){


                if(numero>horaenmin){

                    min_hasta_bus=(numero+((int)(queparada*tiempoEntreEstaciones)))*10000;
                    break;

                }
/*
               if(numero-horaenmin>-1){

                    min_hasta_bus=(numero+((int)(queparada*tiempoEntreEstaciones)))*10000;
                    break;
                }

 */

            }

            //System.out.println("ESPECIAL");

        }

        //System.out.println("NO HAY SERVICIO ESTE DIA");

        if(min_hasta_bus==-1){

            return getString(R.string.fuera_hora);
        }

        if(min_hasta_bus>60){

            int h=(min_hasta_bus/10000)/60;
            int m=(min_hasta_bus/10000)%60;

            if(h>23){h=0;}
            String m_string=":";
            if(m<10){m_string=":0";}
            if(!intervalo.equals("especial")) {
                int hs=(min_hasta_bus%10000)/60;
                int ms=(min_hasta_bus%10000)%60;
                if(hs>23){hs=hs-24;}
                String ms_string=":";
                if(ms<10){ms_string=":0";}
                if(min_hasta_bus%10000!=0){

                    return getString(R.string.proximo)+" "+h+m_string+m+" - "+getString(R.string.siguiente)+" "+hs+ms_string+ms;
                }else {
                    return getString(R.string.proximo) + " " + h + m_string + m;
                }
            }

            return getString(R.string.proximo)+" "+h+m_string+m;

        }

        return getString(R.string.proximo)+" "+min_hasta_bus+" "+getString(R.string.minutos);

    }

    public ArrayList<Integer> dimeHorasLinea(String linea){

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

    public int minutosHastaBus(int origen_de_ruta,int intervalo,String hinicio,String hfin,int hora,int minuto,double tiempoEntreEstaciones){

        Calendar hora_inicio=Calendar.getInstance();
        Calendar hora_fin=Calendar.getInstance();
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm");

        int h_inicio=0;
        int m_inicio=0;
        int h_fin=0;
        int m_fin=0;
        int horaenmin=(hora*60)+minuto;
        int min_hasta_bus=-1;


        try {
            hora_inicio.setTime(formatoHora.parse(hinicio));
            hora_fin.setTime(formatoHora.parse(hfin));
            h_inicio =hora_inicio.get(Calendar.HOUR_OF_DAY);
            m_inicio =hora_inicio.get(Calendar.MINUTE);

            h_fin =hora_fin.get(Calendar.HOUR_OF_DAY);
            m_fin =hora_fin.get(Calendar.MINUTE);

        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }


        if(h_inicio>21){

            h_fin=h_fin+24;


        }else if(h_inicio<5){

            if(horaenmin>360){ // la hora actual es menor que las 6:00
                //if(h_actual<7 && h_actual>=0){

                horaenmin=((hora-24)*60)+minuto;

            }

        }

        horaenmin=horaenmin-min_tolerancia;

        int iniciobus=(h_inicio*60)+m_inicio;
        int finbus=(h_fin*60)+m_fin;

        int min_esta_estacion=(int)(tiempoEntreEstaciones*origen_de_ruta);

        if(horaenmin>finbus+min_esta_estacion) { // ---- ESTA FUERA DE HORARIO

            return -1;
        }

        if(horaenmin<iniciobus){  // --- Todavia no ha empezado el servicio de Bus

            //System.out.println("TODAVIA NO HA EMPEZADO EL SERVICIO "+horaenmin_comparar+" < "+horaenmin_inicio);

            //min_hasta_bus=(horaenmin_inicio-horaenmin_comparar)+min_esta_estacion + (intervalo * 10000);
            min_hasta_bus=(iniciobus+min_esta_estacion);
            min_hasta_bus=(min_hasta_bus*10000)+(min_hasta_bus+intervalo);

        }else if (horaenmin >= iniciobus) {  // YA HA EMPEZADO


                    while (horaenmin >= iniciobus + min_esta_estacion) {

                        iniciobus = iniciobus + intervalo;
                    }

                    min_hasta_bus = (iniciobus + min_esta_estacion);



                    if(finbus+min_esta_estacion>min_hasta_bus+intervalo){

                        min_hasta_bus=(min_hasta_bus*10000)+(min_hasta_bus+intervalo);
                    }else{

                        min_hasta_bus=(min_hasta_bus*10000);
                    }

        }



        return min_hasta_bus;

    }

    private void muestraRutaLinea(Lineas linea){

        Intent miIntent=new Intent(getActivity(), MapsActivity.class);

        ContenedorInicio.queMapa=2;

        miIntent.putExtra("LINEAMAPA", linea);

        startActivity(miIntent);

    }
}
