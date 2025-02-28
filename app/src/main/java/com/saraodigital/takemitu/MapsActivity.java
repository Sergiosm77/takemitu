package com.saraodigital.takemitu;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Bitmap finPeq,andapeq,inicioPeq,paradaPeq,inicioPeq2,elbus,gpsPeq;

    private DatosViewModel viewModel;

    TextView titulomapa,titulomapalineas,nombreL1,nombreL2;
    ImageView icono;

    Lineas lineamapa;

    private Button botonruta1,botonruta2,botonruta3;
    private LinearLayout cajalineas;

    SentidoParadas infoParadas;

    public String nombrerutauno,nombrerutaunoA,nombrerutaunoB,nombrerutadosA,nombrerutadosB,nombrerutatresA,nombrerutatresB,nombrerutacuatro;
    private Parcelable[] ruta1,ruta1A,ruta1B,ruta2A,ruta2B,ruta3A,ruta3B;
    private Location[] ruta1_loc,ruta1A_loc,ruta1B_loc,ruta2A_loc,ruta2B_loc,ruta3A_loc,ruta3B_loc;
    Location migps;
    private int queruta;

    Location gps;

    SentidoParadas dimeNombres;

    int queMapa;

    LatLng posiciongps;

    Marker markerNuevo,markerViejo;

    int primero;

    LocationManager mlocManager;
    Localizacion Local;


    // -------- Para hacer linea discontinua:
    int PATTERN_DASH_LENGTH_PX = 20;
    int PATTERN_GAP_LENGTH_PX = 20;
    PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);
    // ----------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //System.out.println("NO TIENE ACCESO A LA LOCALIZACION 1");
            //ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION }, 1222);
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION }, 1222);
        }

        mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Local=new Localizacion();


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        queMapa=ContenedorInicio.queMapa;

        cajalineas=findViewById(R.id.caja_lineas);
        botonruta1=findViewById(R.id.ruta1mapa);
        botonruta2=findViewById(R.id.ruta2mapa);
        botonruta3=findViewById(R.id.ruta3mapa);
        nombreL1=findViewById(R.id.nombre_linea1);
        nombreL2=findViewById(R.id.nombre_linea2);
        titulomapa=findViewById(R.id.titulo_mapa);
        titulomapalineas=findViewById(R.id.titulo_mapa_lineas);
        icono=findViewById(R.id.iconobus);

        posiciongps=new LatLng(Buscador.migps.getLatitude(), Buscador.migps.getLongitude());

        infoParadas=new SentidoParadas();
        dimeNombres=new SentidoParadas();

        // cambio de tamaño a iconos:
        BitmapDrawable bus = (BitmapDrawable) getResources().getDrawable(R.drawable.bustop, null);
        Bitmap f = bus.getBitmap();
        elbus = Bitmap.createScaledBitmap(f, 100, 100, false);

        BitmapDrawable fin = (BitmapDrawable) getResources().getDrawable(R.drawable.finish1, null);
        Bitmap b = fin.getBitmap();
        finPeq = Bitmap.createScaledBitmap(b, 100, 100, false);

        BitmapDrawable anda = (BitmapDrawable) getResources().getDrawable(R.drawable.walk1, null);
        Bitmap e = anda.getBitmap();
        andapeq = Bitmap.createScaledBitmap(e, 100, 100, false);

        BitmapDrawable inicio = (BitmapDrawable) getResources().getDrawable(R.drawable.walk1, null);
        Bitmap c = inicio.getBitmap();
        inicioPeq = Bitmap.createScaledBitmap(c, 100, 100, false);

        BitmapDrawable inicio2 = (BitmapDrawable) getResources().getDrawable(R.drawable.bus2, null);
        Bitmap c2 = inicio2.getBitmap();
        inicioPeq2 = Bitmap.createScaledBitmap(c2, 100, 100, false);

        BitmapDrawable parada = (BitmapDrawable) getResources().getDrawable(R.drawable.bustop2, null);
        Bitmap d = parada.getBitmap();
        paradaPeq = Bitmap.createScaledBitmap(d, 90, 90, false);

        BitmapDrawable gps = (BitmapDrawable) getResources().getDrawable(R.drawable.location3, null);
        Bitmap g = gps.getBitmap();
        gpsPeq = Bitmap.createScaledBitmap(g, 90, 90, false);
        // ------------------------------

        if(queMapa==1){deBuscador();}
        if(queMapa==2){deLineas();}
        if(queMapa==3){deSitios();}
        if(queMapa==4){deParadas();}


    }

    public void deBuscador(){

        Bundle lasRutas=getIntent().getExtras();

        if(lasRutas!=null) {

            ruta1A = lasRutas.getParcelableArray("RUTA1A");
            nombrerutaunoA = lasRutas.getString("NOMBRE1A");
            ruta1B = lasRutas.getParcelableArray("RUTA1B");
            nombrerutaunoB = lasRutas.getString("NOMBRE1B");

            ruta2A = lasRutas.getParcelableArray("RUTA2A");
            nombrerutadosA = lasRutas.getString("NOMBRE2A");
            ruta2B = lasRutas.getParcelableArray("RUTA2B");
            nombrerutadosB = lasRutas.getString("NOMBRE2B");

            ruta3A = lasRutas.getParcelableArray("RUTA3A");
            nombrerutatresA = lasRutas.getString("NOMBRE3A");
            ruta3B = lasRutas.getParcelableArray("RUTA3B");
            nombrerutatresB = lasRutas.getString("NOMBRE3B");

            nombrerutacuatro = lasRutas.getString("NOMBRE4");

            queruta=lasRutas.getInt("QUERUTA");

        }

        primero=0;


        //if(ruta1!=null){ruta1_loc=Arrays.copyOf(ruta1,ruta1.length,Location[].class);}else{botonruta1.setVisibility(View.GONE);}
        if(ruta1A!=null){ruta1A_loc=Arrays.copyOf(ruta1A,ruta1A.length,Location[].class);}else{botonruta1.setVisibility(View.GONE);}
        if(ruta1B!=null){ruta1B_loc=Arrays.copyOf(ruta1B,ruta1B.length,Location[].class);}
        if(ruta2A!=null){ruta2A_loc=Arrays.copyOf(ruta2A,ruta2A.length,Location[].class);}else{botonruta2.setVisibility(View.GONE);}
        if(ruta2B!=null){ruta2B_loc=Arrays.copyOf(ruta2B,ruta2B.length,Location[].class);}
        if(ruta3A!=null){ruta3A_loc=Arrays.copyOf(ruta3A,ruta3A.length,Location[].class);}else{botonruta3.setVisibility(View.GONE);}
        if(ruta3B!=null){ruta3B_loc=Arrays.copyOf(ruta3B,ruta3B.length,Location[].class);}

        String r1t="";
        String r2t="";
        String r3t="";

        if(ruta1A==null){

            botonruta1.setVisibility(View.GONE);
        }else{
            r1t=getString(R.string.ruta1);
        }

        if(ruta2A==null) {

            botonruta2.setVisibility(View.GONE);

        }else{

            if (r1t.equals("")) {r2t=getString(R.string.ruta1);}else{r2t=getString(R.string.ruta2);}

        }

        if(ruta3A==null){

            botonruta3.setVisibility(View.GONE);
        }else{

            if (r1t.equals("") && r2t.equals("")) {r3t=getString(R.string.ruta1);}
            if(!r1t.equals("") && !r2t.equals("")){r3t=getString(R.string.ruta3);}else{r3t=getString(R.string.ruta2);}

        }

        botonruta1.setText(r1t);
        botonruta2.setText(r2t);
        botonruta3.setText(r3t);

        final String r1=r1t;
        final String r2=r2t;
        final String r3=r3t;



        botonruta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //mapauno(ruta1_loc,null,nombrerutauno,null);
                if(ruta1B==null){
                    mapauno(ruta1A_loc,null,nombrerutaunoA,null);

                }else{
                    mapauno(ruta1A_loc,ruta1B_loc,nombrerutaunoA,nombrerutaunoB);

                }

                if(queruta==2){
                    if(botonruta2.getRotation()==0){botonruta2.animate().rotation(360);}else{botonruta2.animate().rotation(0);}
                    botonruta2.setText(r2);
                    botonruta2.setAlpha(1);
                    botonruta2.setEnabled(true);
                }
                if(queruta==3){
                    if(botonruta3.getRotation()==0){botonruta3.animate().rotation(360);}else{botonruta3.animate().rotation(0);}
                    botonruta3.setText(r3);
                    botonruta3.setAlpha(1);
                    botonruta3.setEnabled(true);
                }
                queruta=1;
                Rutas.pulsa=1;

                //botonruta1.animate().rotation(giro);
                botonruta1.setText(r1);
                botonruta1.setAlpha(0.3f);
                botonruta1.setEnabled(false);
                if(botonruta1.getRotation()==0){botonruta1.animate().rotation(360);}else{botonruta1.animate().rotation(0);}

            }
        });

        botonruta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ruta2B==null){
                    mapauno(ruta2A_loc,null,nombrerutadosA,null);

                }else{
                    mapauno(ruta2A_loc,ruta2B_loc,nombrerutadosA,nombrerutadosB);

                }


                if(queruta==1){
                    if(botonruta1.getRotation()==0){botonruta1.animate().rotation(360);}else{botonruta1.animate().rotation(0);}
                    botonruta1.setText(r1);
                    botonruta1.setAlpha(1);
                    botonruta1.setEnabled(true);
                }
                if(queruta==3){
                    if(botonruta3.getRotation()==0){botonruta3.animate().rotation(360);}else{botonruta3.animate().rotation(0);}
                    botonruta3.setText(r3);
                    botonruta3.setAlpha(1);
                    botonruta3.setEnabled(true);
                }
                queruta=2;
                Rutas.pulsa=2;
                //botonruta2.animate().rotation(giro);
                botonruta2.setText(r2);
                botonruta2.setAlpha(0.3f);
                botonruta2.setEnabled(false);
                if(botonruta2.getRotation()==0){botonruta2.animate().rotation(360);}else{botonruta2.animate().rotation(0);}

            }
        });

        botonruta3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ruta3B==null){
                    mapauno(ruta3A_loc,null,nombrerutatresA,null);

                }else{
                    mapauno(ruta3A_loc,ruta3B_loc,nombrerutatresA,nombrerutatresB);

                }

                if(queruta==1){
                    if(botonruta1.getRotation()==0){botonruta1.animate().rotation(360);}else{botonruta1.animate().rotation(0);}
                    botonruta1.setText(r1);
                    botonruta1.setAlpha(1);
                    botonruta1.setEnabled(true);
                }
                if(queruta==2){
                    if(botonruta2.getRotation()==0){botonruta2.animate().rotation(360);}else{botonruta2.animate().rotation(0);}
                    botonruta2.setText(r2);
                    botonruta2.setAlpha(1);
                    botonruta2.setEnabled(true);
                }
                queruta=3;

                Rutas.pulsa=3;
                //botonruta3.animate().rotation(giro);
                botonruta3.setText(r3);
                botonruta3.setAlpha(0.3f);
                botonruta3.setEnabled(false);
                if(botonruta3.getRotation()==0){botonruta3.animate().rotation(360);}else{botonruta3.animate().rotation(0);}

            }
        });

        if(queruta==1){botonruta1.callOnClick();}
        if(queruta==2){botonruta2.callOnClick();}
        if(queruta==3){botonruta3.callOnClick();}

    }

    public void deLineas(){

        Bundle lasRutas=getIntent().getExtras();

        if(lasRutas!=null) {

            lineamapa = lasRutas.getParcelable("LINEAMAPA");
        }

        botonruta1.setVisibility(View.GONE);
        botonruta2.setVisibility(View.GONE);
        botonruta3.setVisibility(View.GONE);

        cajalineas.setVisibility(View.GONE);
        mapados(null,"");

    }

    public void deSitios(){

        Bundle lasRutas=getIntent().getExtras();
        Location elSitio=null;
        String distancia="";

        if(lasRutas!=null) {

            elSitio = lasRutas.getParcelable("SITIO");
            distancia = lasRutas.getString("DISTANCIA");

        }

        botonruta1.setVisibility(View.GONE);
        botonruta2.setVisibility(View.GONE);
        botonruta3.setVisibility(View.GONE);

        //cajalineas.setVisibility(View.GONE);

        mapatres(elSitio, distancia);

    }

    public void deParadas(){

        Bundle lasRutas=getIntent().getExtras();
        Location parada=null;
        String distancia="";

        if(lasRutas!=null) {

            parada = lasRutas.getParcelable("PARADA");
            lineamapa = lasRutas.getParcelable("LINEAMAPA");
            distancia = lasRutas.getString("DISTANCIA");
        }

        botonruta1.setVisibility(View.GONE);
        botonruta2.setVisibility(View.GONE);
        botonruta3.setVisibility(View.GONE);

        //cajalineas.setVisibility(View.GONE);

        mapados(parada,distancia);

    }

    public void mapauno(Location[] lineaA,Location[] lineaB, String nombreA,String nombreB){

        mMap.clear();

        markerNuevo = mMap.addMarker(new MarkerOptions().position(posiciongps).title(getString(R.string.ubicacionahora)).icon(BitmapDescriptorFactory.fromBitmap(gpsPeq)));

        icono.setImageResource(R.drawable.ruta);

        if (lineaB == null) { // -------------------------- UNA LINEA -------------------------------------------------

            titulomapa.setText(lineaA[0].getProvider()+" - "+lineaA[lineaA.length-1].getProvider());
            //nombreL1.setText(getString(R.string.linea)+" "+dimeNombres.dimeNombres(nombreA)[0]);
            nombreL1.setText(getString(R.string.linea)+" "+dimeNombres.dimeNombres(nombreA));
            nombreL2.setVisibility(View.GONE);


             // despues de cargar una nueva ruta, borra las rutas que ya estuvieran de otra consulta

            LatLng posicion, anteriorPosicion, inicioMapa; // creo dos variables de tipo LatLng que almacenarán los puntos de latitud y longitud para dibujar

            float matizColor = BitmapDescriptorFactory.HUE_RED; // crea iconos de puntos de ruta

            posicion = new LatLng(lineaA[0].getLatitude(), lineaA[0].getLongitude());


            // pinta punto de SALIDA
            mMap.addMarker(new MarkerOptions().position(posicion).title(lineaA[0].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(inicioPeq)));


            int color = Color.BLUE; // color para lineas intermedias
            //matizColor= BitmapDescriptorFactory.HUE_GREEN;

            inicioMapa = posicion;

            anteriorPosicion = new LatLng(lineaA[0].getLatitude(), lineaA[0].getLongitude());
            posicion = new LatLng(lineaA[1].getLatitude(), lineaA[1].getLongitude());

            mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(Color.DKGRAY).geodesic(true));  // pintamos una linea entre los dos puntos

            if (lineaA.length == 2) { // ----------------------------------------- SIN LINEA ------------------------------------------------------------------


                mMap.addMarker(new MarkerOptions().position(posicion).title(lineaA[1].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(finPeq)));


            } else {

                mMap.addMarker(new MarkerOptions().position(posicion).title(lineaA[1].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(paradaPeq)));
                for (int i = 2; i < lineaA.length; i++) {

                    anteriorPosicion = posicion; // ponemos la posicion anterior con el punto actual
                    posicion = new LatLng(lineaA[i].getLatitude(), lineaA[i].getLongitude()); // y el punto actual le asignamos el nuevo punto

                    if (i == (lineaA.length - 1)) {
                        mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(Color.DKGRAY).geodesic(true));
                        mMap.addMarker(new MarkerOptions().position(posicion).title(lineaA[i].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(finPeq)));
                    } else {
                        mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(color).geodesic(true));  // pintamos una linea entre los dos puntos
                        mMap.addMarker(new MarkerOptions().position(posicion).title(lineaA[i].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(paradaPeq)));
                    }
                }


            }

            if(primero!=1) {
                float zoomLevel = 13.0f; //This goes up to 21
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inicioMapa, zoomLevel));
                primero=1;
            }


        }else {  // -------------------- DOS LINEAS ---------------------------------------------------------------------------------------


            titulomapa.setText(lineaA[0].getProvider()+" - "+lineaB[lineaB.length-1].getProvider());
            nombreL1.setText(getString(R.string.linea)+" "+dimeNombres.dimeNombres(nombreA));
            nombreL2.setVisibility(View.VISIBLE);
            nombreL2.setText(getString(R.string.linea)+" "+dimeNombres.dimeNombres(nombreB));

            LatLng posicion, anteriorPosicion, inicioMapa; // creo dos variables de tipo LatLng que almacenarán los puntos de latitud y longitud para dibujar

            posicion = new LatLng(lineaA[0].getLatitude(), lineaA[0].getLongitude());


            // pinta punto de SALIDA
            mMap.addMarker(new MarkerOptions().position(posicion).title(lineaA[0].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(inicioPeq)));


            int color = Color.BLUE; // color para lineas intermedias
            //matizColor= BitmapDescriptorFactory.HUE_GREEN;

            inicioMapa = posicion;

            anteriorPosicion = new LatLng(lineaA[0].getLatitude(), lineaA[0].getLongitude());
            posicion = new LatLng(lineaA[1].getLatitude(), lineaA[1].getLongitude());

            mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(Color.DKGRAY).geodesic(true));  // pintamos una linea entre los dos puntos

            if (lineaA.length == 2) {


                mMap.addMarker(new MarkerOptions().position(posicion).title(lineaA[1].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(finPeq)));


            } else {

                mMap.addMarker(new MarkerOptions().position(posicion).title(lineaA[1].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(paradaPeq)));
                for (int i = 2; i < lineaA.length-1; i++) {

                    anteriorPosicion = posicion; // ponemos la posicion anterior con el punto actual
                    posicion = new LatLng(lineaA[i].getLatitude(), lineaA[i].getLongitude()); // y el punto actual le asignamos el nuevo punto

                    mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(color).geodesic(true));  // pintamos una linea entre los dos puntos
                    mMap.addMarker(new MarkerOptions().position(posicion).title(lineaA[i].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(paradaPeq)));
                }

                anteriorPosicion = posicion; // ponemos la posicion anterior con el punto actual
                posicion = new LatLng(lineaA[lineaA.length-1].getLatitude(), lineaA[lineaA.length-1].getLongitude()); // y el punto actual le asignamos el nuevo punto

                mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(color).geodesic(true));  // pintamos una linea entre los dos puntos
                mMap.addMarker(new MarkerOptions().position(posicion).title(lineaA[lineaA.length-1].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(andapeq)));

                // -- CAMBIO DE LINEA -----------


                anteriorPosicion = posicion;
                posicion = new LatLng(lineaB[0].getLatitude(), lineaB[0].getLongitude());

                mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(Color.DKGRAY).geodesic(true)).setPattern(PATTERN_POLYGON_ALPHA);

                //mMap.addMarker(new MarkerOptions().position(posicion).title(lineaB[0].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(andapeq)));  QUITADO SEGUNDO MUÑECO ANDANDO

                // ------------------------


                color = Color.MAGENTA;
                for (int i = 1; i < lineaB.length; i++) {

                    anteriorPosicion = posicion; // ponemos la posicion anterior con el punto actual
                    posicion = new LatLng(lineaB[i].getLatitude(), lineaB[i].getLongitude()); // y el punto actual le asignamos el nuevo punto
                    if (i == (lineaB.length - 1)) {
                        mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(Color.DKGRAY).geodesic(true));
                        mMap.addMarker(new MarkerOptions().position(posicion).title(lineaB[i].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(finPeq)));
                    } else {
                        mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(color).geodesic(true));  // pintamos una linea entre los dos puntos
                        mMap.addMarker(new MarkerOptions().position(posicion).title(lineaB[i].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(paradaPeq)));
                    }
                }

            }
            if(primero!=1) {
                float zoomLevel = 13.0f; //This goes up to 21
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inicioMapa, zoomLevel));
                primero=1;
            }

        }
    }

    public void mapados(Location parada,String laDistancia){

        if(parada==null){

            parada=new Location("");
        }

        mMap.clear();

        markerNuevo = mMap.addMarker(new MarkerOptions().position(posiciongps).title(getString(R.string.ubicacionahora)).icon(BitmapDescriptorFactory.fromBitmap(gpsPeq)));

        icono.setImageResource(R.drawable.bustop);

        titulomapalineas.setText(getString(R.string.linea)+" "+infoParadas.dimeNombres(lineamapa.nombre));
        titulomapa.setText(getString(R.string.sentido)+" "+lineamapa.sentido);

        LatLng posicion, anteriorPosicion, inicioMapa,laParada;

        laParada=null;

        /*if(gps.getLatitude()!=0.0) {
            posicion = new LatLng(gps.getLatitude(), gps.getLongitude());
            mMap.addMarker(new MarkerOptions().position(posicion).title("Mi ubicación").icon(BitmapDescriptorFactory.fromBitmap(inicioPeq)));
        }*/

        posicion = new LatLng(lineamapa.estaciones[0].getLatitude(), lineamapa.estaciones[0].getLongitude());

        inicioMapa=posicion;

        MarkerOptions marcador;

        marcador=new MarkerOptions().position(posicion).title(lineamapa.estaciones[0].getProvider()+getString(R.string.iniciolinea)).icon(BitmapDescriptorFactory.fromBitmap(inicioPeq2));

        if(marcador.getTitle().equals(parada.getProvider()+getString(R.string.iniciolinea))){

            mMap.addMarker(marcador).showInfoWindow();
            laParada=marcador.getPosition();

        }else{

            mMap.addMarker(marcador);

        }



        for(int i=1;i<lineamapa.estaciones.length-1;i++){


            anteriorPosicion=posicion;
            posicion = new LatLng(lineamapa.estaciones[i].getLatitude(), lineamapa.estaciones[i].getLongitude());

            marcador=new MarkerOptions().position(posicion).title(lineamapa.estaciones[i].getProvider()).icon(BitmapDescriptorFactory.fromBitmap(paradaPeq));
            if(marcador.getTitle().equals(parada.getProvider())){

                mMap.addMarker(marcador).showInfoWindow();
                laParada=marcador.getPosition();

            }else{

                mMap.addMarker(marcador);

            }
            mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(Color.BLACK).geodesic(true));

        }

        anteriorPosicion=posicion;
        posicion = new LatLng(lineamapa.estaciones[lineamapa.estaciones.length-1].getLatitude(), lineamapa.estaciones[lineamapa.estaciones.length-1].getLongitude());
        marcador=new MarkerOptions().position(posicion).title(lineamapa.estaciones[lineamapa.estaciones.length-1].getProvider()+getString(R.string.finlinea)).icon(BitmapDescriptorFactory.fromBitmap(finPeq));
        if(marcador.getTitle().equals(parada.getProvider()+getString(R.string.finlinea))){

            mMap.addMarker(marcador).showInfoWindow();
            laParada=marcador.getPosition();

        }else{

            mMap.addMarker(marcador);

        }

        mMap.addPolyline(new PolylineOptions().add(anteriorPosicion, posicion).width(7).color(Color.BLACK).geodesic(true));


        float zoomLevel = 13.0f; //This goes up to 21

        if(queMapa==4){

            if(laParada!=null && posiciongps.latitude!=0){mMap.addPolyline(new PolylineOptions().add(posiciongps, laParada).width(7).color(Color.RED).geodesic(true));}

            LatLng posicionParada=new LatLng(parada.getLatitude(), parada.getLongitude());

            zoomLevel = 14.0f;

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicionParada, zoomLevel));

            nombreL1.setText(laDistancia);
            nombreL2.setVisibility(View.GONE);

        }else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(inicioMapa, zoomLevel));
        }

    }

    public void mapatres(Location elsitio,String distancia){

        mMap.clear();

        if(elsitio==null){

            return;
        }

        if(posiciongps.latitude!=0) {
            mMap.addMarker(new MarkerOptions().position(posiciongps).title(getString(R.string.ubicacionahora)).icon(BitmapDescriptorFactory.fromBitmap(gpsPeq)));
        }

        icono.setImageResource(R.drawable.infoicon);

        titulomapa.setText(ContenedorInicio.nombre_sitio);
        //titulomapa.setText(getString(R.string.sentido)+" "+distancia);
        nombreL1.setText(distancia);
        nombreL2.setVisibility(View.GONE);

        String nombre="";

        LatLng sitio=new LatLng(elsitio.getLatitude(),elsitio.getLongitude());
        if(queMapa==4){

            nombre = "Parada: " + elsitio.getProvider();

        }else {

            nombre = ContenedorInicio.queMenu + ": " + elsitio.getProvider();

        }

        //mMap.addMarker(new MarkerOptions().position(posiciongps).title(getString(R.string.miUbi)).icon(BitmapDescriptorFactory.fromBitmap(inicioPeq2)));


            mMap.addMarker(new MarkerOptions().position(sitio).title(nombre).icon(BitmapDescriptorFactory.fromBitmap(finPeq))).showInfoWindow();
        if(posiciongps.latitude!=0) {
            mMap.addPolyline(new PolylineOptions().add(posiciongps, sitio).width(7).color(Color.BLACK).geodesic(true));
        }

        float zoomLevel = 13.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sitio, zoomLevel));

    }

    public class Localizacion implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            posiciongps = new LatLng(loc.getLatitude(), loc.getLongitude());

            if(loc.getLongitude()!=0) {

                //System.out.println("GPS: "+posiciongps);

            if(markerNuevo!=null){markerViejo=markerNuevo;}

            markerNuevo = mMap.addMarker(new MarkerOptions().position(posiciongps).title(getString(R.string.ubicacionahora)).icon(BitmapDescriptorFactory.fromBitmap(gpsPeq)));

            if(markerViejo!=null){markerViejo.remove();}

            }

            //mMap.addMarker(new MarkerOptions().position().title(migps.getProvider()).icon(BitmapDescriptorFactory.fromBitmap(gpsPeq)));

            //System.out.println("Ha puesto el gps");
            //System.out.println("POSICION GPS: "+migps.getLatitude()+" "+migps.getLongitude());
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado

        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    System.out.println("LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    System.out.println( "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    System.out.println( "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    public void onResume(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //System.out.println("NO TIENE ACCESO A LA LOCALIZACION 1");
            //ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION }, 1222);
            super.onResume();
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0,  Local);
        super.onResume();
    }

    @Override
    public void onStop() {

        mlocManager.removeUpdates(Local);  // ------------- DETIENE EL GPS ----------------------

        super.onStop();
    }


}
