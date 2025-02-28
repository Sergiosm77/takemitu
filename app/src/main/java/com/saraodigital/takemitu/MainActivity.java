package com.saraodigital.takemitu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity{

    int mesActualizar=5;
    int diaActualizar=5;
    int anioActualizar=2020;
    Calendar calendario;
    TextView fechaFinal;

    boolean guardado=true;

    private String numVersion="";
    int fallo=0;

    ListaMenus[] menus;
    ListaNotas[] notas;

    Lineas[] lineas;
    private ProgressBar barra;

    private RequestQueue rq;
    private JsonRequest jrq;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barra=findViewById(R.id.progressBar);
        fechaFinal=findViewById(R.id.fechahoy);

        calendario = new GregorianCalendar(TimeZone.getTimeZone("Europe/Madrid"));

        Sincroniza comienza = new Sincroniza();
        comienza.execute();

    }

    private class Sincroniza extends AsyncTask<String,Integer,String> {  // carga en memoria la base de datos

        @Override
        protected String doInBackground(String... strings) {

            String packagename=getApplicationContext().getPackageName();
            Boolean version=false;
            String newVersion="";

            BDlineas bbdd=new BDlineas();


            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo estadoRed = connectivityManager.getActiveNetworkInfo();

            if (estadoRed == null || !estadoRed.isConnected()) {

                if(bbdd.compruebaGuardado(getApplicationContext())) {

                    bbdd.leeDatosLineas(getApplicationContext());
                    bbdd.leeDatosEstaciones(getApplicationContext());
                    bbdd.leeDatosMenus(getApplicationContext());
                    bbdd.leeDatosNotas(getApplicationContext());
                    lineas = bbdd.lasLineas;
                    menus=bbdd.dameMenus();
                    notas=bbdd.dameNotas();

                }else{

                    guardado=false;
                }

            }else {

                try {
                    Document document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packagename).timeout(3000).get();
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

                // -------------

                //lineas=bbdd.dameinfoLineas(getApplicationContext(),LINEAS);
                lineas=bbdd.dameinfoLineas2(getApplicationContext());

                menus=bbdd.dameMenus();
                notas=bbdd.dameNotas();

                if(lineas==null){

                    guardado=false;

                }

            }

// ----------------- BASES DE DATOS INTERNA ANULADAS -------------------------
/*
            ManejoBBDD bbdd=new ManejoBBDD(getApplicationContext());

            //BBDDsitios bbddsitios=new BBDDsitios(getApplicationContext());

            // ------ BASE DE DATOS DE LINEAS -----------------------------

            try{
                bbdd.aperturaBBDD(getApplicationContext());

                lineas=bbdd.dameinfoLineas(LINEAS);  // Guardamos en lineas toda la info, que obtenemos con el metodo dameinfolineas

                 bbdd.cerrarBBDD();

                //System.out.println("------------------ HA PODIDO ABRIR LA BASE DE DATOS LINEAS");

           }catch(Exception e){

                //System.out.println("------------------ NO HA PODIDO ABRIR LA BASE DE DATOS LINEAS");


            }

            try{
                bbddsitios.aperturaBBDD(getApplicationContext());

                bbddsitios.cerrarBBDD();

                //System.out.println("------------------ HA PODIDO ABRIR LA BASE DE DATOS SITIOS");

            }catch(Exception e){

                System.out.println("------------------ NO HA PODIDO ABRIR LA BASE DE DATOS SITIOS");


            }
*/

            return newVersion;
        }

        protected void onProgressUpdate(Integer... valores){

            //barra.setVisibility(View.VISIBLE);

        }

        protected void onPostExecute(String newVersion){

            //System.out.println("FECHA ACTUAL: dia:"+calendario.get(Calendar.DAY_OF_MONTH)+" mes: "+(calendario.get(Calendar.MONTH)+1)+" año: "+calendario.get(Calendar.YEAR));

            if(!guardado){

                errorbbdd();

            }else if(newVersion.equals("") || compruebaVersion(newVersion)){
                //comenzar();
                iniciarTakemitu();
            }else{

                nuevaVersion();
            }

        }




    }


    public void comenzar(){

        //System.out.println("--------------------- Va a Buscador");


        CountDownTimer temporizador = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                //textoinicio.setText(String.format(Locale.getDefault(), "Iniciando en %d segundos", millisUntilFinished / 1000L));
            }

            public void onFinish() {

/*
                if(calendario.get(Calendar.DAY_OF_MONTH)>=diaActualizar && (calendario.get(Calendar.MONTH)+1)>=mesActualizar && calendario.get(Calendar.YEAR)>=(anioActualizar)){

                    barra.setVisibility(View.GONE);

                    necesitaActualizar();

                }else
                if(calendario.get(Calendar.DAY_OF_MONTH)<diaActualizar && calendario.get(Calendar.DAY_OF_MONTH)>=(diaActualizar-2) && (calendario.get(Calendar.MONTH)+1)>=mesActualizar && calendario.get(Calendar.YEAR)>=(anioActualizar)){

                    barra.setVisibility(View.GONE);

                    pocoParaActualizar();

                }else{
*/
                    iniciarTakemitu();

               // }
            }
        };

        temporizador.start();

    }

    public void iniciarTakemitu(){

        Intent miIntent = new Intent(MainActivity.this, ContenedorInicio.class);

        miIntent.putExtra("LINEAS", lineas);
        miIntent.putExtra("MENUS",menus);
        miIntent.putExtra("NOTAS",notas);

        barra.setVisibility(View.GONE);

        startActivity(miIntent);

        finish();

    }

    public void onResume(){

        super.onResume();

    }

    public void pocoParaActualizar(){

        LayoutInflater inflador = this.getLayoutInflater();

        final View alertLayout = inflador.inflate(R.layout.actualizar, null);

        final Button irActualizar=alertLayout.findViewById(R.id.actualizar);
        final Button aceptar=alertLayout.findViewById(R.id.noactualizar);
        final TextView info=alertLayout.findViewById(R.id.infoActualizar);
        final ImageView icono=alertLayout.findViewById(R.id.iconoinfo);

        aceptar.setText(getString(R.string.continuar));

        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            int gira;
            float x=1.5f;
            float y=1.5f;
            @Override
            public void run() {

                //if(gira==360){gira=0;}
                //else if(gira==0){gira=360;}

                //icono.animate().rotation(gira);

                icono.animate().scaleY(y);
                icono.animate().scaleX(x);

                if(x==1){x=1.5f;y=1.5f;}
                else{x=1;y=1;}

                handler.postDelayed(this,300);
            }
        },0);

        int dias=(diaActualizar-calendario.get(Calendar.DAY_OF_MONTH));

        info.setText(getString(R.string.info3)+" "+dias+" "+getString(R.string.dias));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        alert.setCancelable(false);

        final AlertDialog dialog = alert.create();



        irActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
                irActualizarWeb();

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
                iniciarTakemitu();

            }
        });

        dialog.show();

    }

    public void necesitaActualizar(){

        LayoutInflater inflador = this.getLayoutInflater();

        final View alertLayout = inflador.inflate(R.layout.actualizar, null);

        final Button irActualizar=alertLayout.findViewById(R.id.actualizar);
        final Button aceptar=alertLayout.findViewById(R.id.noactualizar);
        final TextView info=alertLayout.findViewById(R.id.infoActualizar);
        final ImageView icono=alertLayout.findViewById(R.id.iconoinfo);

        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            int gira;
            float x=1.5f;
            float y=1.5f;
            @Override
            public void run() {

                //if(gira==360){gira=0;}
                //else if(gira==0){gira=360;}

                //icono.animate().rotation(gira);

                icono.animate().scaleY(y);
                icono.animate().scaleX(x);

                if(x==1){x=1.5f;y=1.5f;}
                else{x=1;y=1;}

                handler.postDelayed(this,300);
            }
        },0);


        aceptar.setText(getString(R.string.salir));

        info.setText(Html.fromHtml(getResources().getString(R.string.info2)));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        alert.setCancelable(false);

        final AlertDialog dialog = alert.create();

        irActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
                irActualizarWeb();

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
                finish();

            }
        });

        dialog.show();

    }

    public void errorbbdd(){

        LayoutInflater inflador = this.getLayoutInflater();

        final View alertLayout = inflador.inflate(R.layout.actualizar, null);

        Button continuar=alertLayout.findViewById(R.id.actualizar);
        Button aceptar=alertLayout.findViewById(R.id.noactualizar);
        TextView info=alertLayout.findViewById(R.id.infoActualizar);
        final ImageView icono=alertLayout.findViewById(R.id.iconoinfo);

        continuar.setEnabled(false);
        continuar.setAlpha(0);

        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            int gira;
            float x=1.5f;
            float y=1.5f;
            @Override
            public void run() {

                //if(gira==360){gira=0;}
                //else if(gira==0){gira=360;}

                //icono.animate().rotation(gira);

                icono.animate().scaleY(y);
                icono.animate().scaleX(x);

                if(x==1){x=1.5f;y=1.5f;}
                else{x=1;y=1;}

                handler.postDelayed(this,600);
            }
        },0);


        aceptar.setText(getString(R.string.salir));

        info.setText(Html.fromHtml(getResources().getString(R.string.errorbbdd)));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        alert.setCancelable(false);

        final AlertDialog dialog = alert.create();

        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
                //irActualizarWeb();
                iniciarTakemitu();

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
                finish();


            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

    }

    public void nuevaVersion(){

        LayoutInflater inflador = this.getLayoutInflater();

        final View alertLayout = inflador.inflate(R.layout.actualizar, null);

        final Button irActualizar=alertLayout.findViewById(R.id.actualizar);
        final Button aceptar=alertLayout.findViewById(R.id.noactualizar);
        final TextView info=alertLayout.findViewById(R.id.infoActualizar);
        final ImageView icono=alertLayout.findViewById(R.id.iconoinfo);

        final Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            int gira;
            float x=1.5f;
            float y=1.5f;
            @Override
            public void run() {

                //if(gira==360){gira=0;}
                //else if(gira==0){gira=360;}

                //icono.animate().rotation(gira);

                icono.animate().scaleY(y);
                icono.animate().scaleX(x);

                if(x==1){x=1.5f;y=1.5f;}
                else{x=1;y=1;}

                handler.postDelayed(this,600);
            }
        },0);


        aceptar.setText(getString(R.string.continuar));

        info.setText(Html.fromHtml(getResources().getString(R.string.haynuevaversion)));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        alert.setCancelable(false);

        final AlertDialog dialog = alert.create();

        irActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
                irActualizarWeb();

            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();
                //comenzar();
                iniciarTakemitu();


            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

    }

    protected Boolean compruebaVersion (String newVersion) {

        //newVersion="1.9";
        Boolean version=false;

        if (!newVersion.isEmpty()) {

            try {

                numVersion=this.getPackageManager().getPackageInfo(this.getPackageName(),0).versionName;

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if(Double.parseDouble(numVersion)>=Double.parseDouble(newVersion)){

                version=true;

            }
        }

        return version;
    }

    public void irActualizarWeb(){

        try{

            //String packagename="com.saraodigital.takemitu";

            String packagename=this.getPackageName();

            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="+packagename);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //intent.setPackage("com.android.vending");
            startActivity(intent);

        }catch (Exception e){

            System.out.println("ERROR ACTUALIZACION");

        }

        finish();

    }

    /*public void iniciaTodo(){

        String url=getString(R.string.servidor)+"&quebase=paradas";

        rq= Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap();
        params.put("clave", "hola");
        params.put("clave2", "hola");

        JSONObject parametros = new JSONObject(params);

        jrq = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        //ArrayList<Bitmap> imagenestotal=new ArrayList<>();
                        ArrayList<String> imagenestotal=new ArrayList<>();
                        ArrayList<String> nombres=new ArrayList<>();
                        ArrayList<String> detalles=new ArrayList<>();
                        ArrayList<String> latitud=new ArrayList<>();
                        ArrayList<String> longitud=new ArrayList<>();

                        System.out.println("VA BIEN ");
                        try {
                            JSONArray jsonArray = response.getJSONArray("datos");
                            //JSONObject jsonObject = jsonArray.getJSONObject(0);

                            //JSONObject jsonObject1 = jsonObject.getJSONObject("datos");
                            //JSONArray jsonArray1 = jsonObject1.getJSONArray("detalles");

                            // Recorrer los datos del usuario
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject object = jsonArray.getJSONObject(i);

                                String nombre_bd = object.getString("nombre");
                                String detalles_bd = object.getString("detalles");
                                String latitud_bd=object.getString("latitud");
                                String longitud_bd=object.getString("longitud");
                                String imagen_bd=object.getString("imagen");

                                nombres.add(nombre_bd);
                                detalles.add(detalles_bd);
                                imagenestotal.add(imagen_bd);
                                //imagenestotal.add(BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.infoicon));
                                latitud.add(latitud_bd);
                                longitud.add(longitud_bd);

                                //System.out.println("Numero: "+i+" "+nombre_bd);

                                //System.out.println(detalles_bd);
                                //System.out.println(" ");
                                //System.out.println(" ");
                            }

                            LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
                            RecyclerView reciclador=getView().findViewById(R.id.envios);
                            reciclador.setLayoutManager(layoutManager);
                            Adaptador_Sitio adaptador=new Adaptador_Sitio(imagenestotal,nombres,detalles,latitud,longitud,getView().getContext(),getView());
                            reciclador.setAdapter(adaptador);

                            progressBarSitios.setVisibility(View.GONE);


                        } catch (JSONException e) {

                            System.out.println("FALLO");

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("HAY UN PROBLEMA "+error.toString());

                    }
                });

        rq.add(jrq);

    }
    */

}
