package com.saraodigital.takemitu;


import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.engine.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.Key;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class Sitios2 extends Fragment{


    public static NavController navController;

    private androidx.appcompat.app.ActionBar menu;

    private ProgressBar progressBarSitios;

    private DecimalFormat formato1;

    private String queBase;

    private RequestQueue rq;
    private JsonRequest jrq;

    MisSitios[] misSitios=null;
    MisEventos[] misEventos=null;

    RecyclerView reciclador;
    Adaptador_Sitio2 adaptador;

    private LayoutInflater inflador;
    private LinearLayout rutaContenedor;

    private String haySitios;

    Toast toast1;
    private static String puerta;

    JSONArray jsonArray;

    public Sitios2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        navController = Navigation.findNavController(getActivity(), R.id.content_frame);

        View v= inflater.inflate(R.layout.fragment_sitios2, container, false);

        inflador = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

        rutaContenedor = v.findViewById(R.id.contenedor_sitios);

        progressBarSitios=v.findViewById(R.id.progressBar_sitios2);

        formato1 = new DecimalFormat("#.0");

        menu=((AppCompatActivity)getActivity()).getSupportActionBar();

        menu.show();

        puerta=getString(R.string.cod);

        toast1 = Toast.makeText(getActivity().getApplicationContext(), R.string.sin_internet, Toast.LENGTH_LONG);

        //bdsitios=new BBDDsitios(getContext());

        queBase=ContenedorInicio.queMenuBase;
        menu.setTitle(ContenedorInicio.queMenu);

        cargaSitios start=new cargaSitios();

        if(jsonArray==null){start.execute();}

        return v;

    }

    @Override
    public void onResume() {
        super.onResume();

        if(jsonArray!=null) {

            ponDatos();
            progressBarSitios.setVisibility(View.GONE);
        }

        try {
            reciclador.setVerticalScrollbarPosition(ContenedorInicio.posicionReciclador);

        }catch (Exception e){

        }

    }

    @Override
    public void onPause() {
        super.onPause();

        //ContenedorInicio.posicionReciclador= nested.getVerticalScrollbarPosition();
        //ContenedorInicio.posicionReciclador = ((LinearLayoutManager) reciclador.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        try {

            ContenedorInicio.posicionReciclador = ((LinearLayoutManager) reciclador.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        }catch (Exception e){


        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ContenedorInicio.posicionReciclador = 0;
    }

    private class cargaSitios extends AsyncTask<String,Integer,String> {  // carga en memoria la base de datos

        @Override
        protected String doInBackground(String... strings) {

            progressBarSitios.setVisibility(View.VISIBLE);

            haySitios="no";

            String url=getString(R.string.servidor)+"&quebase="+queBase;

            rq=Volley.newRequestQueue(getActivity());

            jrq = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONArray respuestaServidor = response.getJSONArray("valor");

                                if (respuestaServidor.get(0).equals("ok") && respuestaServidor.length()>0) {

                                    if (queBase.equals("eventos")) {

                                        estableceEventos(response.getJSONArray("datos"));

                                    } else{

                                        estableceSitios(response.getJSONArray("datos"));
                                    }

                                    haySitios="ok";


                                } else {

                                    System.out.println("ERROR: "+respuestaServidor.get(0));
                                    haySitios="error";

                                }

                            } catch (JSONException e) {

                                System.out.println("FALLO DE RESPUESTA 1 "+e.getMessage());

                                haySitios="error";

                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("FALLO DE RESPUESTA 2 "+error.getMessage());

                            haySitios="error";

                        }
                    });


            rq.add(jrq);

            while(haySitios.equals("no")) {

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println(e);
                }

            }

            return haySitios;



        }

        protected void onPostExecute(String result){

            progressBarSitios.setVisibility(View.GONE);

            if(result.equals("ok")){

                System.out.println("PONE DATOS 2");
                ponDatos();


            }else{



            }

        }

    }

    public static JSONObject desencripta(String encrypted) throws Exception {

        //String iv="1234567891234567";
        //String iv2="XkAHgnbNGPIi6Um9znNE8w==";
        String iv2="0000000000000000";
        String algoritmo = "AES/CBC/PKCS5Padding";
        String resultado="";

        //String respuesta=encrypted.replaceAll("\\s","");
        //byte[] respuestaDesc=Base64.getDecoder().decode(respuesta);

        byte[] iv4=Base64.decode(iv2,Base64.DEFAULT);

        byte[] respuestaDesc=Base64.decode(encrypted,Base64.DEFAULT);

        Key key3=new SecretKeySpec(puerta.getBytes(), algoritmo);

        // ---------------- METODO CBC -------------

        try {

            byte[] decryptedString = encryptDecrypt(Cipher.DECRYPT_MODE, key3.getEncoded(), iv2.getBytes(), respuestaDesc);

            resultado=new String(decryptedString);

            System.out.println("RESULTADO: "+resultado);

        }catch(Exception e){

            System.out.println("ERROR1: "+e.getMessage());
        }



/*
        //--------- CODIFICA ----------------------------
        //byte[] cipherText =encryptDecrypt(Cipher.ENCRYPT_MODE, key3.getEncoded(), iv.getBytes(), prueba.getBytes());


        try {

            byte[] decryptedString = encryptDecrypt(Cipher.DECRYPT_MODE, key3.getEncoded(), iv.getBytes(), respuestaDesc);

            resultado=new String(decryptedString);

        }catch(Exception e){

            System.out.println("ERROR: "+e.getMessage());
        }

 */

        return new JSONObject(resultado);
    }

    private static byte[] encryptDecrypt(final int mode, final byte[] key, final byte[] IV, final byte[] message){

        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            final IvParameterSpec ivSpec = new IvParameterSpec(IV);

            cipher.init(mode, keySpec, ivSpec);
            return cipher.doFinal(message);
        }catch(Exception e){

            System.out.println("ERROR2: "+e.getMessage());
            return null;
        }

    }

    private void estableceSitios(JSONArray datosSitios){

        try {

            misSitios = new MisSitios[datosSitios.length()];

            for (int i = 0; i < misSitios.length; i++) {

                misSitios[i] = new MisSitios();

                JSONObject object = datosSitios.getJSONObject(i);

                misSitios[i].nombre = object.getString("nombre");
                misSitios[i].detalle = object.getString("detalles");
                misSitios[i].direccion = object.getString("direccion");
                misSitios[i].latitud = object.getString("latitud");
                misSitios[i].longitud = object.getString("longitud");
                misSitios[i].image = object.getString("imagen");
                misSitios[i].telefono = object.getString("telefono");


            }

        }catch (JSONException e){

            System.out.println("ERROR AL ESTABLECER SITIOS: "+e.getMessage());

        }

    }

    private void estableceEventos(JSONArray datosEventos){

        try {

            misEventos = new MisEventos[datosEventos.length()];

            for (int i = 0; i < misEventos.length; i++) {

                misEventos[i] = new MisEventos();

                JSONObject object = datosEventos.getJSONObject(i);

                misEventos[i].nombre = object.getString("nombre");
                misEventos[i].lugarEvento = object.getString("direccion");
                misEventos[i].fechaEvento = object.getString("fecha");

                misEventos[i].horaEvento = object.getString("hora");
                misEventos[i].descripcion = object.getString("detalles");
                misEventos[i].latitud = object.getString("latitud");
                misEventos[i].longitud = object.getString("longitud");
                misEventos[i].activo = Integer.parseInt(object.getString("activo"));
                misEventos[i].imagen = object.getString("imagen");

            }

        }catch (JSONException e){

            System.out.println("ERROR AL ESTABLECER SITIOS: "+e.getMessage());

        }

    }

    private void ponDatos(){

            try {

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                reciclador = getView().findViewById(R.id.envios2);
                reciclador.setLayoutManager(layoutManager);
                adaptador = new Adaptador_Sitio2(misSitios, misEventos, queBase, getView().getContext(), getView());
                reciclador.setAdapter(adaptador);
                //reciclador.setNestedScrollingEnabled(false);


                if (reciclador != null) {
                    ((reciclador.getLayoutManager())).scrollToPosition(ContenedorInicio.posicionReciclador);
                    //progressBarSitios.setVisibility(View.GONE);

                }

            }catch (Exception e){


            }

    }

    private void sinDatos(){

        LinearLayout sinSitios=(LinearLayout) inflador.inflate(R.layout.barra_sitios_sinsitio,null);

        ((TextView)sinSitios.findViewById(R.id.nombre_sinsitio)).setText(getString(R.string.sinsitios)+"\n"+getString(R.string.sitionuevo));
        ((ImageView)sinSitios.findViewById(R.id.imagen_sinsitio)).setVisibility(View.GONE);
        ((TextView)sinSitios.findViewById(R.id.direccion_sinsitio)).setVisibility(View.GONE);
        ((TextView)sinSitios.findViewById(R.id.acuanto_sinsitio)).setVisibility(View.GONE);
        ((TextView)sinSitios.findViewById(R.id.nombre_sinsitio)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        progressBarSitios.setVisibility(View.GONE);

        sinSitios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","saraodigital@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Me gustaría sugerir un sitio nuevo");
                startActivity(Intent.createChooser(emailIntent,  null));


            }
        });

        rutaContenedor.addView(sinSitios);

    }



}
