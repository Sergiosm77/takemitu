package com.saraodigital.takemitu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ContenedorInicio extends AppCompatActivity{

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;
    private NavController navController;
    private AppBarConfiguration mAppBarConfiguration;

    private DatosViewModel viewModel;

    Toolbar mitoolbar;

    private LocationManager mlocManager;
    private Localizacion Local;
    public static Location migps=new Location("");

    public static String aquelinea;

    // VARIABLES PARA FRAGMENTOS -------------------

    public static String detalle_sitio, nombre_sitio, imagen_sitio, distancia_sitio, fecha, hora;
    public static Double latitud, longitud;
    public static int posicionReciclador=0;
    public static String queMenu="";
    public static String queMenuBase="";
    public static  int queMapa;
    public static ListaMenus[] menus;
    public static ListaNotas[] notas;
    public static String telefono;
    public static String direccion;


    @Override
    public void onBackPressed(){


        //System.out.println("OBSERVADORES"+viewModel.getLocalizacion().hasActiveObservers());

        if(!viewModel.getLocalizacion().hasActiveObservers()){

            navController.navigateUp();
        }else {
            if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){


                super.onBackPressed();
                return;
            }else {
                Toast.makeText(this, R.string.salirAPP, Toast.LENGTH_SHORT).show();
            }
            tiempoPrimerClick = System.currentTimeMillis();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenedor_inicio);

        //viewModel = ViewModelProviders.of(this).get(DatosViewModel.class);
        viewModel = new ViewModelProvider(this).get(DatosViewModel.class);

        mitoolbar = findViewById(R.id.appbar);

        setSupportActionBar(mitoolbar);

        Intent intento=getIntent();

        menus = (ListaMenus[])intento.getSerializableExtra("MENUS");
        notas = (ListaNotas[])intento.getSerializableExtra("NOTAS");

        System.out.println("CARGA TODOS DATOS");

        //DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        // NavigationView navView = findViewById(R.id.navview);
/*
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setDrawerLayout(drawerLayout)
                .build();
*/
        navController = Navigation.findNavController(this, R.id.content_frame);
        //       NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        //     NavigationUI.setupWithNavController(navView, navController);

        //getSupportActionBar().hide();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
         ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            //System.out.println("NO TIENE ACCESO A LA LOCALIZACION 2");

            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION }, 1222);
        }

        mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Local = new Localizacion();
/*
        if(leeinicio()==0){

            miraInformacion();
            guardainicio(1);
        }

 */


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


    public class Localizacion implements LocationListener {


        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion


            Buscador.migps.setLatitude(loc.getLatitude());
            Buscador.migps.setLongitude(loc.getLongitude());
            migps.setLatitude(loc.getLatitude());
            migps.setLongitude(loc.getLongitude());

            //System.out.println("Ha puesto el gps");
            //System.out.println("POSICION GPS: "+migps.getLatitude()+" "+migps.getLongitude());
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado

            Buscador.panelInfo.setText("GPS desactivado");

        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado

            Buscador.panelInfo.setText("GPS activado");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navview, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem opcion_menu){

        int id=opcion_menu.getItemId();
        if(id==R.id.ve_a_info){
            //Navigation.findNavController(ContenedorInicio.this,R.id.mobile_navigation).navigate(R.id.action_nav_home_to_nav_info);

            navController.navigate(R.id.nav_info);
            return true;
        }

        if(id==R.id.ve_a_compartir){
            //Navigation.findNavController(ContenedorInicio.this,R.id.mobile_navigation).navigate(R.id.action_nav_home_to_nav_info);
            compartir();
            return true;
        }

        if(id==R.id.info){

            return true;

        }
        if(id==R.id.ve_a_nuevolugar){
            //Navigation.findNavController(ContenedorInicio.this,R.id.mobile_navigation).navigate(R.id.action_nav_home_to_nav_info);
            nuevoLugar();
            return true;
        }
        return super.onOptionsItemSelected(opcion_menu);
    }



  /*  @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.content_frame);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

*/

  public void compartir(){

      String mensaje;

      try{

          mensaje=getString(R.string.comparte_mensaje)+" https://play.google.com/store/apps/details?id="+(this.getPackageName());

      }catch(NullPointerException e){

          mensaje=getString(R.string.comparte_mensaje)+" https://play.google.com/store/apps/details?id=com.saraodigital.takemitu";

      }

      Intent compartir = new Intent(android.content.Intent.ACTION_SEND);
      compartir.setType("text/plain");
      compartir.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.comparte_subject));
      compartir.putExtra(android.content.Intent.EXTRA_TEXT, mensaje);
      startActivity(Intent.createChooser(compartir, getString(R.string.comparte_via)));


  }

    public void nuevoLugar(){

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","saraodigital@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.nuevo_lugar));
        startActivity(Intent.createChooser(emailIntent,  null));

    }

    public void miraInformacion(){

        LayoutInflater inflador = this.getLayoutInflater();

        final View alertLayout = inflador.inflate(R.layout.actualizar, null);

        final Button irActualizar=alertLayout.findViewById(R.id.actualizar);
        final Button aceptar=alertLayout.findViewById(R.id.noactualizar);
        final TextView info=alertLayout.findViewById(R.id.infoActualizar);
        final ImageView icono=alertLayout.findViewById(R.id.iconoinfo);

        aceptar.setText(getString(R.string.aceptar));

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

                handler.postDelayed(this,500);
            }
        },0);


        info.setText(Html.fromHtml(getString(R.string.info11)));

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        alert.setCancelable(false);

        final AlertDialog dialog = alert.create();

        irActualizar.setVisibility(View.GONE);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

            }
        });

        dialog.show();

    }

    private void guardainicio(int inicio){

        SharedPreferences datosinicio= PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor mieditor=datosinicio.edit();

        mieditor.putInt("MENSAJEINICIO",inicio);

        mieditor.apply();
    }

    private int leeinicio(){

        SharedPreferences datosinicio=PreferenceManager.getDefaultSharedPreferences(this);

        return datosinicio.getInt("MENSAJEINICIO",0);

    }

}
