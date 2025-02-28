package com.saraodigital.takemitu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class Adaptador_Sitio2 extends RecyclerView.Adapter<Adaptador_Sitio2.ViewHolder> {


    private String queBase;

    final private MisSitios[] misSitios;
    final private MisEventos[] misEventos;

    private Context contexto;
    private View v;
    private DecimalFormat formato1=new DecimalFormat("#.0");

    //public Adaptador_Sitio(ArrayList<Bitmap> imagenes,ArrayList<String> nombres,ArrayList<String> detalles,ArrayList<String> latitud,ArrayList<String> longitud, Context contexto,View v) {
    public Adaptador_Sitio2(MisSitios[] misSitios, MisEventos[] misEventos,String queBase, Context contexto, View v) {

        this.contexto = contexto;

        this.misSitios=misSitios;
        this.misEventos=misEventos;

        this.queBase = queBase;

        this.v=v;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if(queBase.equals("eventos")){

            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.barra_eventos,parent,false);

        }else{

            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.barra_sitios,parent,false);

        }


        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if(queBase.equals("eventos")) {

            holder.nombre_sitio.setText(misEventos[position].nombre);
            holder.detalles.setText(misEventos[position].descripcion);
            holder.fecha.setText(misEventos[position].fechaEvento);
            holder.hora.setText(misEventos[position].horaEvento);
            holder.direccion.setText(misEventos[position].lugarEvento);

            if(misEventos[position].activo==1){

                holder.activo.setText("Activo");
            }else{

                holder.activo.setText("YA PASADO");
                holder.activo.setTextColor(Color.RED);
            }

            Glide.with(v.getContext())
                    .load(misEventos[position].imagen)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.notfound)
                    .into(holder.imagen);

            holder.sitio_elegido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ContenedorInicio.nombre_sitio = misEventos[position].nombre;
                    ContenedorInicio.detalle_sitio = misEventos[position].descripcion;
                    ContenedorInicio.imagen_sitio = misEventos[position].imagen;
                    ContenedorInicio.distancia_sitio = aCuanto(position);
                    ContenedorInicio.telefono ="";
                    ContenedorInicio.direccion = misEventos[position].lugarEvento;
                    ContenedorInicio.fecha = misEventos[position].fechaEvento;
                    ContenedorInicio.hora = misEventos[position].horaEvento;

                    ContenedorInicio.latitud = Double.parseDouble(misEventos[position].latitud);
                    ContenedorInicio.longitud = Double.parseDouble(misEventos[position].longitud);


                    Sitios2.navController.navigate(R.id.action_nav_gallery_to_quesitio);

                }
            });


        }else {

            String textoNombre;

            Glide.with(v.getContext())
                    .load(misSitios[position].image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.notfound)
                    .into(holder.imagen);

            if (queBase.equals("farmacias")) {
                textoNombre = "Farmacia " + misSitios[position].nombre;
            } else {
                textoNombre = misSitios[position].nombre;
            }
            holder.nombre_sitio.setText(textoNombre);
            holder.distancia.setText(aCuanto(position));
            holder.direccion.setText(misSitios[position].direccion);

            holder.sitio_elegido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ContenedorInicio.nombre_sitio = misSitios[position].nombre;
                    ContenedorInicio.detalle_sitio = misSitios[position].detalle;
                    ContenedorInicio.imagen_sitio = misSitios[position].image;
                    ContenedorInicio.distancia_sitio = aCuanto(position);
                    ContenedorInicio.telefono = misSitios[position].telefono;
                    ContenedorInicio.direccion = misSitios[position].direccion;

                    ContenedorInicio.fecha = "";
                    ContenedorInicio.hora = "";

                    ContenedorInicio.latitud = Double.parseDouble(misSitios[position].latitud);
                    ContenedorInicio.longitud = Double.parseDouble(misSitios[position].longitud);


                    Sitios2.navController.navigate(R.id.action_nav_gallery_to_quesitio);

                }
            });
        }


    }

    @Override
    public int getItemCount() {

        if(queBase.equals("eventos")){

            return misEventos.length;

        }else {
            return misSitios.length;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView sitio_elegido;

        ImageView imagen;
        TextView nombre_sitio, detalles, distancia, direccion, fecha, hora, activo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            if(queBase.equals("eventos")){

                sitio_elegido = itemView.findViewById(R.id.queevento);

                nombre_sitio = itemView.findViewById(R.id.nombre_evento);
                detalles = itemView.findViewById(R.id.descripcion);
                imagen = itemView.findViewById(R.id.imagen_evento);
                fecha = itemView.findViewById(R.id.fecha);
                hora = itemView.findViewById(R.id.hora);
                direccion = itemView.findViewById(R.id.direccion);
                activo = itemView.findViewById(R.id.es_activo);

            }else {

                sitio_elegido = itemView.findViewById(R.id.quesitio);

                imagen = itemView.findViewById(R.id.imagensitio);
                nombre_sitio = itemView.findViewById(R.id.nombresitio);
                distancia = itemView.findViewById(R.id.a_cuanto);
                direccion = itemView.findViewById(R.id.direccion);
            }

        }
    }

    private String aCuanto(int position){

        String dis;
        final double dissitio;

        Location dondeesta=new Location("");

        try {

            if(queBase.equals("eventos")){

                dondeesta.setLatitude(Double.parseDouble(misEventos[position].latitud));
                dondeesta.setLongitude(Double.parseDouble(misEventos[position].longitud));

            }else {

                dondeesta.setLatitude(Double.parseDouble(misSitios[position].latitud));
                dondeesta.setLongitude(Double.parseDouble(misSitios[position].longitud));
            }
        }catch (Exception e){

            dondeesta.setLatitude(0);
            dondeesta.setLongitude(0);
        }

        if(Buscador.migps.getLongitude()!=0) {

            dissitio=Buscador.migps.distanceTo(dondeesta);

        }else{dissitio=0;}

        if(dissitio>1000 && dissitio<100001){

            dis=contexto.getString(R.string.estasA)+" "+formato1.format(dissitio/1000)+" Km";

        }else{

            if(dissitio==0 || dissitio>100000){

                dis= contexto.getString(R.string.sin_gps);

            }else{dis=contexto.getString(R.string.estasA)+" "+ Math.round(dissitio)+" "+contexto.getString(R.string.metros);}
        }

        return dis;

    }

}
