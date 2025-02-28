package com.saraodigital.takemitu;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.graphics.drawable.IconCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class AdaptadorPublicidad extends SliderViewAdapter<AdaptadorPublicidad.Holder> {

    ListaMenus[] imagenes;
    final Context context;

    public AdaptadorPublicidad(ListaMenus[] imagenes, Context context){


        this.imagenes = imagenes;
        this.context= context;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {

        View vista= LayoutInflater.from(parent.getContext()).inflate(R.layout.publi_item, parent, false);

        return new Holder(vista);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {


        if(position==0){

            viewHolder.imagenPubli.setImageResource(R.drawable.publi_aqui);

        }else {

            Glide.with(context)
                    .load(imagenes[position].getImagen())
                    .error(R.drawable.notfound)
                    .into(viewHolder.imagenPubli);

        }


            viewHolder.imagenPubli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(position==0) {

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "saraodigital@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mi publicidad en Takemitu");
                    context.startActivity(Intent.createChooser(emailIntent, null));

                    }

                }
            });


    }

    @Override
    public int getCount() {
        return imagenes.length;
    }

    public class Holder extends SliderViewAdapter.ViewHolder{

        ImageView imagenPubli;

        public Holder (View itemView){

            super(itemView);

            imagenPubli=itemView.findViewById(R.id.imagen_publicidad);




        }

    }

}
