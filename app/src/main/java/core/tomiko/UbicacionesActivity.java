package core.tomiko;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import core.tomiko.adapters.PlaceAutoSuggestAdapter;

/**
 * By: El Bryant
 */

public class UbicacionesActivity extends FragmentActivity implements OnMapReadyCallback, DirectionCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener,
        View.OnClickListener  {
    @BindView(R.id.flayCargando) FrameLayout flayCargando;
    AutoCompleteTextView autocomplete;
    Button btnAceptar;
    CardView cvBusqueda;
    Double latitud = 0.0, longitud = 0.0;
    Geocoder geocoder;
    GoogleMap mMap;
    LatLng origen, destino;
    Marker markerRecojo, markerEntrega;
    Polyline ruta;
    private boolean MOVER_CAMARA = false;
    public static String ubicacion, direccion;
    TextView tvDireccionRecojo, tvDireccionEntrega, tvNombreLugar, tvLimpiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicaciones);
        if (getIntent().getExtras() != null) {
            ubicacion = getIntent().getStringExtra("ubicacion");
        }
        ButterKnife.bind(this);
        initComponents();
        flayCargando.setVisibility(View.VISIBLE);
        initListener();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        geocoder = new Geocoder(this, Locale.getDefault());
        Places.initialize(this, "AIzaSyAd4MY2O9ntD3N9Vl6MWn-ySDG7DQ4iQw0");
        mapFragment.getMapAsync(this);
        if (ubicacion.equals("entrega")) {
            tvDireccionRecojo.setText(SolicitarEnvioActivity.tietDireccionRecojo.getText().toString());
        }
        autocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (autocomplete.getText().length() > 0) {
                    tvLimpiar.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        autocomplete.setAdapter(new PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1));
        autocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String description = (String) parent.getItemAtPosition(position);
                FindLatLong(description);
                LatLng posicion = new LatLng(Double.parseDouble(String.valueOf(latitud)), Double.parseDouble(String.valueOf(longitud)));
                if (ubicacion.equals("recojo")) {
                    if (markerRecojo == null) {
                        MarkerOptions markerOptions = new MarkerOptions().position(posicion);
                        markerRecojo = mMap.addMarker(markerOptions);
                    } else {
                        markerRecojo.setPosition(posicion);
                    }
                } else if (ubicacion.equals("entrega")) {
                    if (markerEntrega == null) {
                        MarkerOptions markerOptions = new MarkerOptions().position(posicion);
                        markerEntrega = mMap.addMarker(markerOptions);
                    } else {
                        markerEntrega.setPosition(posicion);
                    }
                }
            }
        });
        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        final RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-6.940396, -79.969844),
                new LatLng(-6.629670, -79771765)
        );
        cvBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .setLocationBias(bounds)
                        .build(UbicacionesActivity.this);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void initComponents() {
        autocomplete = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        btnAceptar = (Button) findViewById(R.id.btnAceptar);
        cvBusqueda = (CardView) findViewById(R.id.cvBusqueda);
        tvDireccionEntrega = (TextView) findViewById(R.id.tvDireccionEntrega);
        tvDireccionRecojo = (TextView) findViewById(R.id.tvDireccionRecojo);
        tvNombreLugar = (TextView) findViewById(R.id.tvNombreLugar);
        tvLimpiar = (TextView) findViewById(R.id.tvLimpiar);
    }

    public void initListener() {
        btnAceptar.setOnClickListener(this);
        tvLimpiar.setOnClickListener(this);
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
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveListener(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        try {
            if (direction.isOK() && mMap != null) {
                if (ruta != null) {
                    ruta.remove();
                }
                Route route = direction.getRouteList().get(0);
                Leg leg = route.getLegList().get(0);
                ArrayList<LatLng> pointList = leg.getDirectionPoint();
                if (pointList != null) {
                    if (pointList.size() > 0) {
                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(this, pointList, 5, R.color.azul);
                        ruta = mMap.addPolyline(polylineOptions);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onCameraIdle() {
        if (MOVER_CAMARA) {
            if (ubicacion.equals("recojo")) {
                if (markerRecojo == null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng miPosicion = new LatLng(latitud, longitud);
                    markerOptions.position(miPosicion);
                    markerRecojo = mMap.addMarker(markerOptions);
                } else {
                    markerRecojo.setPosition(mMap.getCameraPosition().target);
                }
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.removeUpdates(locListener);
                new DireccionRecojoGeocoder().execute(markerRecojo.getPosition().latitude,
                        markerRecojo.getPosition().longitude);
            } else if (ubicacion.equals("entrega")) {
                if (markerEntrega == null) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng miPosicion = new LatLng(latitud, longitud);
                    markerOptions.position(miPosicion);
                    markerEntrega = mMap.addMarker(markerOptions);
                } else {
                    markerEntrega.setPosition(mMap.getCameraPosition().target);
                }
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.removeUpdates(locListener);
                new DireccionEntregaGeocoder().execute(markerEntrega.getPosition().latitude,
                        markerEntrega.getPosition().longitude);
            }
        }
    }

    @Override
    public void onCameraMove() {
        if (ubicacion.equals("recojo")) {
            if (markerRecojo == null) {
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng miPosicion = new LatLng(latitud, longitud);
                markerOptions.position(miPosicion)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador));
                markerRecojo = mMap.addMarker(markerOptions);
            } else {
                if (MOVER_CAMARA) {
                    markerRecojo.setPosition(mMap.getCameraPosition().target);
                }
            }
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(locListener);
        } else if (ubicacion.equals("entrega")) {
            LatLng posicionRecojo = new LatLng(Double.parseDouble(SolicitarEnvioActivity.tvLatitudRecojo.getText().toString()),
                    Double.parseDouble(SolicitarEnvioActivity.tvLongitudRecojo.getText().toString()));
            if (markerRecojo == null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(posicionRecojo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador));
                markerRecojo = mMap.addMarker(markerOptions);
            } else {
                markerRecojo.setPosition(posicionRecojo);
            }
            if (markerEntrega == null) {
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng miPosicion = new LatLng(latitud, longitud);
                markerOptions.position(miPosicion)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador));
                markerEntrega = mMap.addMarker(markerOptions);
            } else {
                if (MOVER_CAMARA) {
                    markerEntrega.setPosition(mMap.getCameraPosition().target);
                }
            }
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(locListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAceptar:
                if (ubicacion.equals("recojo")) {
                    SolicitarEnvioActivity.tietDireccionRecojo.setText(tvDireccionRecojo.getText().toString());
                    SolicitarEnvioActivity.tvLatitudRecojo.setText("" + markerRecojo.getPosition().latitude);
                    SolicitarEnvioActivity.tvLongitudRecojo.setText("" + markerRecojo.getPosition().longitude);
                    finish();
                } else if (ubicacion.equals("entrega")) {
                    SolicitarEnvioActivity.tietDireccionEntrega.setText(tvDireccionEntrega.getText().toString());
                    SolicitarEnvioActivity.tvLatitudEntrega.setText("" + markerEntrega.getPosition().latitude);
                    SolicitarEnvioActivity.tvLongitudEntrega.setText("" + markerEntrega.getPosition().longitude);
                    Location recojo = new Location("Recojo");
                    recojo.setLatitude(Double.parseDouble(SolicitarEnvioActivity.tvLatitudRecojo.getText().toString()));
                    recojo.setLongitude(Double.parseDouble(SolicitarEnvioActivity.tvLongitudRecojo.getText().toString()));
                    Location entrega = new Location("Entrega");
                    entrega.setLatitude(Double.parseDouble(SolicitarEnvioActivity.tvLatitudEntrega.getText().toString()));
                    entrega.setLongitude(Double.parseDouble(SolicitarEnvioActivity.tvLongitudEntrega.getText().toString()));
                    float distance = recojo.distanceTo(entrega) / 1000;
                    Double precio = 0.0;
                    if (distance < 3.0) {
                        precio = 3.0;
                    } else if (distance > 3 && distance <= 6) {
                        precio = 2.4;
                    } else if (distance > 6 && distance <= 9) {
                        precio = 2.2;
                    } else if (distance > 9 && distance <= 12) {
                        precio = 2.0;
                    } else if (distance > 12 && distance <= 15) {
                        precio = 1.8;
                    } else if (distance > 15 && distance <= 18) {
                        precio = 1.6;
                    } else if (distance > 18 && distance <= 21) {
                        precio = 1.4;
                    } else if (distance > 21) {
                        precio = 1.4;
                    }
                    SolicitarEnvioActivity.tvPrecio.setText(String.format("%.2f", (precio * distance)));
                    SolicitarEnvioActivity.btnRealizarSolicitud.setText("Realizar solicitud (S/ " + String.format("%.2f", precio * distance) + ")");
                    finish();
                }
                break;
            case R.id.tvLimpiar:
                autocomplete.setText("");
                tvLimpiar.setVisibility(View.GONE);
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DireccionRecojoGeocoder extends AsyncTask<Double, Integer, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        protected String doInBackground(Double... geopoint) {
            String direccion = "";
            Double mLatitud = geopoint[0];
            Double mLongitud = geopoint[1];
            String mDireccion = "";
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLatitud, mLongitud, 3);
            } catch (IOException e) {
                mDireccion = "Tu ubicación actual.";
            }
            if (addresses != null) {
                if (addresses.size() > 0) {
                    for (int i = 0; i < addresses.size(); i++) {
                        if (addresses.get(i).getAddressLine(0) != null) {
                            mDireccion = addresses.get(i).getAddressLine(0);
                        }
                        if (!mDireccion.equals("")) {
                            break;
                        }
                    }
                }
            }
            direccion = mDireccion;
            return direccion;
        }
        protected void onPostExecute(String result) {
            tvDireccionRecojo.setText(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DireccionEntregaGeocoder extends AsyncTask<Double, Integer, String> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        protected String doInBackground(Double... geopoint) {
            String direccion = "";
            Double mLatitud = geopoint[0];
            Double mLongitud = geopoint[1];
            String mDireccion="";
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(mLatitud, mLongitud, 3);
            } catch (IOException e) {
                mDireccion="Direccion no encontrada.";
            }
            if (addresses != null) {
                if (addresses.size() > 0) {
                    for (int i = 0; i<addresses.size(); i++) {
                        if (addresses.get(i).getAddressLine(0) != null) {
                            mDireccion = addresses.get(i).getAddressLine(0);
                        }
                        if (!mDireccion.equals("")) {
                            break;
                        }
                    }
                }
            }
            direccion = mDireccion;
            return direccion;
        }
        protected void onPostExecute(String result) {
            tvDireccionEntrega.setText(result);
        }
    }

    LocationListener locListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                latitud = location.getLatitude();
                longitud = location.getLongitude();
            }
            MarkerOptions markerOptions = new MarkerOptions();
            if (latitud != 0.0 && longitud != 0.0) {
                LatLng miPosicion = new LatLng(latitud, longitud);
                markerOptions.position(miPosicion)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador));
                if (ubicacion.equals("recojo")) {
                    if (markerRecojo == null) {
                        markerRecojo = mMap.addMarker(markerOptions);
                    } else {
                        markerRecojo.setPosition(miPosicion);
                    }
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .zoom(14)
                            .target(markerRecojo.getPosition())
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    flayCargando.setVisibility(View.GONE);
                    new DireccionRecojoGeocoder().execute(markerRecojo.getPosition().latitude,
                            markerRecojo.getPosition().longitude);
                } else if (ubicacion.equals("entrega")) {
                    if (markerEntrega == null) {
                        markerEntrega = mMap.addMarker(markerOptions);
                    } else {
                        markerEntrega.setPosition(miPosicion);
                    }
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .zoom(14)
                            .target(markerEntrega.getPosition())
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    flayCargando.setVisibility(View.GONE);
                    new DireccionEntregaGeocoder().execute(markerEntrega.getPosition().latitude,
                            markerEntrega.getPosition().longitude);
                }
            }
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @SuppressLint("DefaultLocale")
    public void FindLatLong(String place_id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Uri.parse("https://maps.googleapis.com/maps/api/geocode/json")
                .buildUpon()
                .appendQueryParameter("key", "AIzaSyAd4MY2O9ntD3N9Vl6MWn-ySDG7DQ4iQw0")
                .appendQueryParameter("place_id", URLEncoder.encode(place_id))
                .build().toString();
        StringRequest sr = new StringRequest(com.android.volley.Request.Method.GET, url, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getJSONArray("results") != null) {
                    JSONArray destination_addresses = jsonObject.getJSONArray("results");
                    JSONObject geometry = (JSONObject) destination_addresses.get(0);
                    String lat = String.valueOf(geometry.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                    String lng = String.valueOf(geometry.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                    double latitud = Double.parseDouble(lat);
                    double longitud = Double.parseDouble(lng);
                    destino = new LatLng(latitud, longitud);
                    origen = new LatLng(latitud, longitud);
                    if (ubicacion.equals("recojo")) {
                        if (markerRecojo == null) {
                            markerRecojo = mMap.addMarker(new MarkerOptions()
                                    .position(origen)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador))
                            );
                        } else {
                            markerRecojo.setPosition(origen);
                        }
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(origen)
                                .bearing(mMap.getCameraPosition().bearing)
                                .tilt(mMap.getCameraPosition().tilt)
                                .zoom(14)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        flayCargando.setVisibility(View.GONE);
                        tvDireccionRecojo.setText(direccion);
                    } else if (ubicacion.equals("entrega")) {
                        if (markerEntrega == null) {
                            markerEntrega = mMap.addMarker(new MarkerOptions()
                                    .position(destino)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador))
                            );
                        } else {
                            markerEntrega.setPosition(destino);
                        }
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(destino)
                                .bearing(mMap.getCameraPosition().bearing)
                                .tilt(mMap.getCameraPosition().tilt)
                                .zoom(14)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        flayCargando.setVisibility(View.GONE);
                        tvDireccionEntrega.setText(direccion);
                    }
                }
                onPlaceSelected(autocomplete.getText().toString(), destino);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, error -> Log.i("Errors", String.valueOf(error)));
        queue.add(sr);
        sr.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("onActivityResult", "Place: " + place.getName() + ", " + place.getId());
                FindLatLong(place.getId());
                direccion = place.getName();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("onActivityResult", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    public void onPlaceSelected(String lugar, LatLng latlng) {
        MOVER_CAMARA = false;
        try {
            if (mMap != null && latlng != null) {
                LatLng posicion = new LatLng(latlng.latitude, latlng.longitude);
                if (ubicacion.equals("recojo")) {
                    if (markerRecojo != null) {
                        markerRecojo.setPosition(posicion);
                    } else {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(posicion)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador));
                        markerOptions.title(lugar);
                        markerRecojo = mMap.addMarker(markerOptions);
                    }
                } else if (ubicacion.equals("entrega")) {
                    if (markerRecojo != null) {
                        markerRecojo.setPosition(new LatLng(Double.parseDouble(SolicitarEnvioActivity.tvLatitudRecojo.getText().toString()),
                                Double.parseDouble(SolicitarEnvioActivity.tvLongitudRecojo.getText().toString())));
                    } else {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(new LatLng(Double.parseDouble(SolicitarEnvioActivity.tvLatitudRecojo.getText().toString()),
                                Double.parseDouble(SolicitarEnvioActivity.tvLongitudRecojo.getText().toString())))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador));
                        markerOptions.title(lugar);
                        markerRecojo = mMap.addMarker(markerOptions);
                    }
                    if (markerEntrega != null) {
                        markerEntrega.setPosition(posicion);
                    } else {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(posicion)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcador));
                        markerOptions.title(lugar);
                        markerEntrega = mMap.addMarker(markerOptions);
                    }
                    if (ruta != null) {
                        ruta.remove();
                    }
                    GoogleDirection.withServerKey("AIzaSyAd4MY2O9ntD3N9Vl6MWn-ySDG7DQ4iQw0")
                            .from(markerRecojo.getPosition())
                            .to(markerEntrega.getPosition())
                            .transportMode(TransportMode.DRIVING)
                            .execute(this);
                }
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(posicion)
                        .zoom(14)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                flayCargando.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}