package com.saraodigital.takemitu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Adaptador_Sitio extends RecyclerView.Adapter<Adaptador_Sitio.ViewHolder> {

    //private ArrayList<Bitmap> imagenes;
    private ArrayList<String> imagenes;
    private ArrayList<String> nombres;
    private ArrayList<String> detalles;
    private ArrayList<String> latitud;
    private ArrayList<String> longitud;

    private Context contexto;
    private View v;
    private LinearLayout rutaContenedor;
    private LayoutInflater inflador;
    private FloatingActionButton irlugar;
    private ScrollView scrolldescripcion;
    private DecimalFormat formato1=new DecimalFormat("#.0");

    //public Adaptador_Sitio(ArrayList<Bitmap> imagenes,ArrayList<String> nombres,ArrayList<String> detalles,ArrayList<String> latitud,ArrayList<String> longitud, Context contexto,View v) {
    public Adaptador_Sitio(ArrayList<String> imagenes,ArrayList<String> nombres,ArrayList<String> detalles,ArrayList<String> latitud,ArrayList<String> longitud, Context contexto,View v) {
        this.imagenes = imagenes;
        this.contexto = contexto;
        this.nombres = nombres;
        this.detalles = detalles;
        this.latitud = latitud;
        this.longitud = longitud;
        this.v=v;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.imagen_sitios,parent,false);


        rutaContenedor =  v.findViewById(R.id.pantalla_descr);
        scrolldescripcion=v.findViewById(R.id.scroll_descripcion);
        irlugar=v.findViewById(R.id.irlugar);
        inflador = (LayoutInflater) parent.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        //holder.imagen.setImageBitmap(imagenes.get(position));
        /*
        Picasso.with(v.getContext()).setIndicatorsEnabled(true);
        Picasso.with(v.getContext())
                .load(imagenes.get(position))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .error(R.drawable.notfound)
                .into(holder.imagen);

         */

        Glide.with(v.getContext())
                .load(imagenes.get(position))
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.notfound)
                .into(holder.imagen);



        holder.imagen.setImageResource(R.drawable.search); // NUEVO
        holder.imagen.setMaxHeight(5);
        holder.describe_sitio.setText(nombres.get(position));

        holder.imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rutaContenedor.removeAllViews();

                final double dissitio;

                Location dondeesta=new Location("");
                dondeesta.setLatitude(Double.parseDouble(latitud.get(position)));
                dondeesta.setLongitude(Double.parseDouble(longitud.get(position)));

                if(Buscador.migps.getLongitude()!=0) {

                    dissitio=Buscador.migps.distanceTo(dondeesta);

                }else{dissitio=0;}

                ponImagen(inflador, imagenes.get(position), dissitio,rutaContenedor);

                Sitios.irA=new Location(nombres.get(position));

                Sitios.irA.setLatitude(Double.parseDouble(latitud.get(position)));
                Sitios.irA.setLongitude(Double.parseDouble(longitud.get(position)));

                ponDescripcion(inflador, nombres.get(position), rutaContenedor,2);
                ponDescripcion(inflador, detalles.get(position), rutaContenedor,0);
                ponDescripcion(inflador, "\n"+"\n", rutaContenedor,0);
                scrolldescripcion.fullScroll(ScrollView.FOCUS_UP);

                irlugar.show();



            }
        });

    }


    @Override
    public int getItemCount() {
        return imagenes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imagen;
        TextView describe_sitio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imagen=itemView.findViewById(R.id.icono_sitios);
            describe_sitio=itemView.findViewById(R.id.descr_imagen);
        }
    }

    //private void ponImagen(LayoutInflater inflador, Bitmap imagen, double dissitio, LinearLayout contenedor){
    private void ponImagen(LayoutInflater inflador, String imagen, double dissitio, LinearLayout contenedor){

        String dis;

        if(dissitio>1000){

            dis=contexto.getString(R.string.estasA)+" "+formato1.format(dissitio/1000)+" Km";

        }else{

            if(dissitio==0){

                dis="";

            }else{dis=contexto.getString(R.string.estasA)+" "+ Math.round(dissitio)+" "+contexto.getString(R.string.metros);}
        }

        LinearLayout imagenes=(LinearLayout)inflador.inflate(R.layout.imagenes,null);

        //((ImageView)imagenes.findViewById(R.id.imagen)).setImageBitmap(imagen); //NUEVO
        /*
        Picasso.with(v.getContext())
                .load(imagen)
                .error(R.drawable.notfound)
        .into((ImageView)imagenes.findViewById(R.id.imagen));

         */

        Glide.with(v.getContext())
                .load(imagen)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.notfound)
                .into((ImageView)imagenes.findViewById(R.id.imagen));

        ((TextView)imagenes.findViewById(R.id.nombre_dissitios)).setText(dis);



        //((TextView)descripciones.findViewById(R.id.texto_desc)).setText(texto);

        contenedor.addView(imagenes); // vamos añadiendo imagenes y textos al contenedor

        (imagenes.findViewById(R.id.imagen)).setAlpha(0.5f);
        (imagenes.findViewById(R.id.imagen)).animate().alpha(1);
        //imagenes.findViewById(R.id.nombre_dissitios).setScaleX(1.5f);
        //imagenes.findViewById(R.id.nombre_dissitios).animate().scaleX(1);

    }

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
            ((TextView) descripciones.findViewById(R.id.texto_desc)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) descripciones.findViewById(R.id.texto_desc)).setTextSize(16);
            (descripciones.findViewById(R.id.texto_desc)).animate().translationY(20);
            (descripciones.findViewById(R.id.texto_desc)).setAlpha(0.5f);
            (descripciones.findViewById(R.id.texto_desc)).animate().alpha(1f);
        }

    }
}
