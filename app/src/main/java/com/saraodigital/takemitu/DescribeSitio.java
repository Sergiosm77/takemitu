package com.saraodigital.takemitu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;


import static android.Manifest.permission.CALL_PHONE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class DescribeSitio extends Fragment{

    TextView distancia;
    ImageView imagenSitio;

    private Button irlugar;

    private Location irA;
    private DatosViewModel viewModel;

    private NavController navController;

    private LinearLayout rutaContenedor;
    private LayoutInflater inflador;
    private androidx.appcompat.app.ActionBar menu;

    public DescribeSitio() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_sitioelegido, container, false);

        distancia=v.findViewById(R.id.distancia_sitio);

        distancia.setText(ContenedorInicio.distancia_sitio);

        rutaContenedor = v.findViewById(R.id.sitios_descripcion);

        imagenSitio=v.findViewById(R.id.imagen_sitio);

        viewModel = new ViewModelProvider((ViewModelStoreOwner) v.getContext()).get(DatosViewModel.class);

        navController = Navigation.findNavController(getActivity(), R.id.content_frame);

        imagenSitio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContenedorInicio.queMapa=3;
                Intent miIntent=new Intent(getActivity(), MapsActivity.class);

                Location sitio=new Location(ContenedorInicio.nombre_sitio);
                String distancia=ContenedorInicio.distancia_sitio;
                sitio.setLatitude(ContenedorInicio.latitud);
                sitio.setLongitude(ContenedorInicio.longitud);

                miIntent.putExtra("SITIO",sitio);
                miIntent.putExtra("DISTANCIA",distancia);

                startActivity(miIntent);


            }
        });

        Glide.with(v.getContext())
                .load(ContenedorInicio.imagen_sitio)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.notfound)
                .into(imagenSitio);

        inflador = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        ponDescripcion(inflador, "\r", rutaContenedor, 0);
        ponDescripcion(inflador, ContenedorInicio.nombre_sitio, rutaContenedor, 2);
        ponLinea(inflador,rutaContenedor);
        if(!ContenedorInicio.direccion.equals("")){

            ponDescripcion(inflador,ContenedorInicio.direccion,rutaContenedor,4);
        }
        if(!ContenedorInicio.telefono.equals("")){

            ponDescripcion(inflador,ContenedorInicio.telefono,rutaContenedor,3);
            ponLinea(inflador,rutaContenedor);
        }
        if(!ContenedorInicio.fecha.equals("")){

            ponDescripcion(inflador,"Fecha: "+ContenedorInicio.fecha,rutaContenedor,4);
            ponLinea(inflador,rutaContenedor);
        }
        if(!ContenedorInicio.hora.equals("")){

            ponDescripcion(inflador,"Hora: "+ContenedorInicio.hora,rutaContenedor,4);
            ponLinea(inflador,rutaContenedor);
        }
        ponDescripcion(inflador, ContenedorInicio.detalle_sitio, rutaContenedor, 0);
        ponDescripcion(inflador, "\n", rutaContenedor, 0);

        irlugar=v.findViewById(R.id.ir_al_sitio);

        menu=((AppCompatActivity)getActivity()).getSupportActionBar();
        menu.show();
        //((ContenedorInicio)getActivity()).getSupportActionBar().hide();

        menu.setTitle(ContenedorInicio.queMenu);

        //menu.hide();

        irlugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                irA=new Location(ContenedorInicio.nombre_sitio);
                irA.setLatitude(ContenedorInicio.latitud);
                irA.setLongitude(ContenedorInicio.longitud);
                viewModel.setLocalizacion(irA);

                navController.navigate(R.id.action_nav_quesitio_to_home);

            }
        });

        return v;

    }



    private void ponDescripcion(LayoutInflater inflador, String texto, LinearLayout contenedor,int color){

        LinearLayout descripciones=(LinearLayout)inflador.inflate(R.layout.descripciones,null);

        ((TextView)descripciones.findViewById(R.id.texto_desc)).setText(texto);
        descripciones.findViewById(R.id.icono_desc).setVisibility(View.GONE);

        if(color==1){
            ((TextView)descripciones.findViewById(R.id.texto_desc)).setTextColor(Color.DKGRAY);

        }
        if(color==4){
            ((TextView)descripciones.findViewById(R.id.texto_desc)).setTextColor(Color.WHITE);
            ((TextView)descripciones.findViewById(R.id.texto_desc)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
        if(color==2){
            ((TextView)descripciones.findViewById(R.id.texto_desc)).setTextColor(Color.CYAN);
            ((TextView)descripciones.findViewById(R.id.texto_desc)).setTypeface(null, Typeface.BOLD);
            ((TextView)descripciones.findViewById(R.id.texto_desc)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
        if(color==3){

            descripciones.findViewById(R.id.icono_desc).setVisibility(View.VISIBLE);

            ((TextView)descripciones.findViewById(R.id.texto_desc)).setTextColor(Color.GREEN);
            ((TextView)descripciones.findViewById(R.id.texto_desc)).setTypeface(null, Typeface.BOLD);

            descripciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        Intent llama = new Intent(Intent.ACTION_CALL);
                        llama.setData(Uri.parse("tel:"+ContenedorInicio.telefono));

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{CALL_PHONE}, 1);

                        // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    } else {
                        //You already have permission
                        try {
                            startActivity(llama);
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });

        }

        contenedor.addView(descripciones); // vamos añadiendo imagenes y textos al contenedor

        if(color==2) {
            (descripciones.findViewById(R.id.texto_desc)).setY(30f);
            (descripciones.findViewById(R.id.texto_desc)).animate().translationY(0).setDuration(1000);
            (descripciones.findViewById(R.id.texto_desc)).setAlpha(0.5f);
            (descripciones.findViewById(R.id.texto_desc)).animate().alpha(1f);
        }

    }

    private void ponLinea(LayoutInflater inflador, LinearLayout contenedor){

        LinearLayout descripciones=(LinearLayout)inflador.inflate(R.layout.linea_separadora,null);

        contenedor.addView(descripciones); // vamos añadiendo imagenes y textos al contenedor

    }

}
