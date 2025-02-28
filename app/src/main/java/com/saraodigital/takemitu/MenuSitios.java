package com.saraodigital.takemitu;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonRequest;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class MenuSitios extends Fragment{


    public static NavController navController;

    private ProgressBar barraProgreso;

    private LayoutInflater inflador;
    private LinearLayout rutaContenedor;

    private androidx.appcompat.app.ActionBar menu;

    Toast toast1;

    public MenuSitios() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        navController = Navigation.findNavController(getActivity(), R.id.content_frame);

        View v= inflater.inflate(R.layout.fragment_menu, container, false);

        inflador=(LayoutInflater) requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        menu=((AppCompatActivity)getActivity()).getSupportActionBar();
        menu.setTitle("Selecciona");

        barraProgreso=v.findViewById(R.id.progressBar_menu);

        barraProgreso.setVisibility(View.VISIBLE);

        toast1 = Toast.makeText(getActivity().getApplicationContext(), R.string.sin_internet, Toast.LENGTH_LONG);

        rutaContenedor=v.findViewById(R.id.pantalla_menu);

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("LLEGA ONRESUME");

        rutaContenedor.removeAllViews();
        ponListado();
        barraProgreso.setVisibility(View.GONE);


    }

    public void ponListado(){

        if(ContenedorInicio.menus.length!=0){

            for(int i=0;i<ContenedorInicio.menus.length;i++){

                ponMenu(ContenedorInicio.menus[i].getNombre(),ContenedorInicio.menus[i].getBase(),ContenedorInicio.menus[i].getImagen());

            }

        }
    }



    public void ponMenu(final String nombreMenu, final String queBaseMenu, String queimagen){

        RelativeLayout barra=(RelativeLayout) inflador.inflate(R.layout.barra_menus,null);

        ImageView imagen=barra.findViewById(R.id.imagenmenu);

        Glide.with(getContext())
                .load(queimagen)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.notfound)
                .into(imagen);

        TextView nombre=barra.findViewById(R.id.nombremenu);
        nombre.setText(nombreMenu);
        rutaContenedor.addView(barra);

        barra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(compruebaRed()) {

                    ContenedorInicio.queMenu = nombreMenu;
                    ContenedorInicio.queMenuBase = queBaseMenu;
                    Navigation.findNavController(v).navigate(R.id.action_nav_menu_to_nav_cargasitio);

                }else{

                    sinInternet();
                }


            }
        });

    }

    public void sinInternet(){

        if(!toast1.getView().isShown()) {

            toast1.setGravity(Gravity.CENTER, 0, 0);
            TextView mensaje = toast1.getView().findViewById(android.R.id.message);
            mensaje.setGravity(Gravity.CENTER);

            toast1.show();
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


}
