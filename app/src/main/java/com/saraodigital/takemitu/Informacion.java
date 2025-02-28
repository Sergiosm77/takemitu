package com.saraodigital.takemitu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Informacion extends Fragment {

    public Informacion() {
        // Required empty public constructor
    }

    private String numVersion="";
    private String mipack;
    private LinearLayout actualiza;
    private ProgressBar barra_actualizando;
    private TextView textoactualizar;
    boolean veaStore=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_informacion, container, false);

        Button email=v.findViewById(R.id.enviaemail);
        actualiza=v.findViewById(R.id.compruebaActualizar);
        barra_actualizando=v.findViewById(R.id.progressBar_store);
        TextView version=v.findViewById(R.id.version_takemitu);
        TextView info1=v.findViewById(R.id.info1);
        textoactualizar=v.findViewById(R.id.texto_actualizar);
        TextView infoversion=v.findViewById(R.id.info_version);

        TextView infoTake1=v.findViewById(R.id.info_take1);
        TextView infoTake2=v.findViewById(R.id.info_take2);
        TextView infoTake3=v.findViewById(R.id.info_take3);
        TextView infoTake4=v.findViewById(R.id.info_take4);

        //infoTake1.setText(Html.fromHtml(getString(R.string.info0)));

        /*
            infoTake1.setText(Html.fromHtml(ContenedorInicio.notas[1].getDetalle()));

        infoTake2.setText(Html.fromHtml(getString(R.string.info8)));
        infoTake3.setText(Html.fromHtml(getString(R.string.info9)));
        infoTake4.setText(Html.fromHtml(getString(R.string.info10)));

         */


        barra_actualizando.setVisibility(View.GONE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        final Handler handler= new Handler();
        final ImageView logo=v.findViewById(R.id.logo);
        handler.postDelayed(new Runnable() {
            int gira;


            @Override
            public void run() {

                if(gira==180){gira=0;}
                else if(gira==0){gira=180;}

                logo.animate().rotationY(gira).setDuration(2000);

                handler.postDelayed(this,5000);
            }
        },0);


        try{

            mipack=getContext().getPackageName();

        }catch(NullPointerException e){

            mipack="com.saraodigital.takemitu";

        }

        try {

       numVersion=getContext().getPackageManager().getPackageInfo(getContext().getPackageName(),0).versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //infoversion.setText(Html.fromHtml(getString(R.string.info4)));


        version.setText("Versión "+numVersion);
        //info1.setText(Html.fromHtml(getResources().getString(R.string.info1)));

        for(int i=0;i<ContenedorInicio.notas.length;i++){

            if(ContenedorInicio.notas[i].getValor().equals("1") && i==1) {
                infoversion.setText(Html.fromHtml(ContenedorInicio.notas[i].getDetalle()));
            }else if(ContenedorInicio.notas[i].getValor().equals("1") && i==2) {
                info1.setText(Html.fromHtml(ContenedorInicio.notas[i].getDetalle()));
            }else if(ContenedorInicio.notas[i].getValor().equals("1") && i==3) {
                infoTake1.setText(Html.fromHtml(ContenedorInicio.notas[i].getDetalle()));
            }else if(ContenedorInicio.notas[i].getValor().equals("1") && i==4) {
                infoTake2.setText(Html.fromHtml(ContenedorInicio.notas[i].getDetalle()));
            }else if(ContenedorInicio.notas[i].getValor().equals("1") && i==5) {
                infoTake3.setText(Html.fromHtml(ContenedorInicio.notas[i].getDetalle()));
            }else if(ContenedorInicio.notas[i].getValor().equals("1") && i==6) {
                infoTake4.setText(Html.fromHtml(ContenedorInicio.notas[i].getDetalle()));
            }

        }

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enviaemail();
            }
        });

        actualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo estadoRed = connectivityManager.getActiveNetworkInfo();

                if (estadoRed == null || !estadoRed.isConnected()) {

                    Toast.makeText(getContext(), getString(R.string.error_conexion), Toast.LENGTH_SHORT).show();

                }else


                if(veaStore){

                    irAstore();

                }else {

                    textoactualizar.setVisibility(View.GONE);
                    barra_actualizando.setVisibility(View.VISIBLE);

                    actualiza.setEnabled(false);
                    miraVersionStore mira = new miraVersionStore();
                    mira.execute();
                }
            }
        });

        return v;
    }



    private void enviaemail(){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","saraodigital@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contacto a través de Takemitu");
        startActivity(Intent.createChooser(emailIntent,  null));

    }


    private class miraVersionStore extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... strings) {

            String newVersion = "0";

            //mipack="com.pecana.iptvextremepro";

            try {
                Document document = Jsoup.connect("https://play.google.com/store/apps/details?id="+mipack).timeout(8000).get();
                if (document != null) {
                    //Log.d("updateAndroid", "Document: " + document);
                    Elements element = document.getElementsContainingOwnText("Current Version");
                    for (Element ele : element) {
                        if (ele.siblingElements() != null) {
                            Elements sibElemets = ele.siblingElements();
                            for (Element sibElemet : sibElemets) {
                                newVersion = sibElemet.text();
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();

            }



            return newVersion;
        }

        protected void onProgressUpdate(Integer... valores){

            //barra.setVisibility(View.VISIBLE);

        }

        protected void onPostExecute(String nuevaVersion){

            //nuevaVersion="1.2";

            if(nuevaVersion.equals("0")){

                barra_actualizando.setVisibility(View.GONE);

                textoactualizar.setVisibility(View.VISIBLE);

                textoactualizar.setText(getString(R.string.info7));

                return;

            }

            try{

                System.out.println("VERSION MIA: "+Double.parseDouble(numVersion)+" VERSION EN WEB ES: "+Double.parseDouble(nuevaVersion));

            }catch(Exception e){

                barra_actualizando.setVisibility(View.GONE);

                textoactualizar.setVisibility(View.VISIBLE);

                textoactualizar.setText(getString(R.string.info7));

                return;
            }

            if (!nuevaVersion.isEmpty()) {
                if(Double.parseDouble(numVersion)>=Double.parseDouble(nuevaVersion)){

                    barra_actualizando.setVisibility(View.GONE);

                    textoactualizar.setVisibility(View.VISIBLE);
                    textoactualizar.setText(getString(R.string.info5));

                }else{

                    barra_actualizando.setVisibility(View.GONE);

                    textoactualizar.setVisibility(View.VISIBLE);
                    textoactualizar.setText(getString(R.string.info6));
                    actualiza.setEnabled(true);
                    veaStore=true;

                }
            }

        }

    }

    public void irAstore(){

        try {

            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + mipack);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //intent.setPackage("com.android.vending");
            startActivity(intent);

        } catch (Exception e) {

            System.out.println("ERROR ACTUALIZACION");

        }

    }

}
