package radarlivre.com.radarlivreandroid.application.activities;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import radarlivre.com.radarlivreandroid.R;
import radarlivre.com.radarlivreandroid.model.Airline;
import radarlivre.com.radarlivreandroid.model.Airplane;
import radarlivre.com.radarlivreandroid.model.AirplanePosition;
import radarlivre.com.radarlivreandroid.network.UpdaterService;
import radarlivre.com.radarlivreandroid.network.interfaces.IUpdaterService;

public class MapFragment extends Fragment implements IUpdaterService.OnAirplanesUpdateListener {
    private static String KEY_SAVED_LOCATION_LAT = "SAVED_LOCATION_LAT";
    private static String KEY_SAVED_LOCATION_LNG = "SAVED_LOCATION_LNG";
    private static String KEY_SAVED_LOCATION_ZOOM = "SAVED_LOCATION_ZOOM";

    private LatLng savedLocation = null;
    private double savedZoon = 1.0;

    RelativeLayout loadingLayout = null;

    private SupportMapFragment mapFragment = null;
    private GoogleMap map = null;

    private List<Marker> markers = new ArrayList<>();
    private Map<String, Airplane> airplanes = new HashMap<>();
    private Map<Integer, Polyline> currentPolyTrack = new HashMap<>();
    private Airplane currentAirplaneSelected = null;

    ServiceConnection serviceConnection = null;

    private IUpdaterService updaterService = null;

    // To draw track
    List<Integer> colors = null;

    private GoogleApiClient mGoogleApiClient = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        loadingLayout = (RelativeLayout) view.findViewById(R.id.loadingLayout);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble(KEY_SAVED_LOCATION_LAT, map.getCameraPosition().target.latitude);
        outState.putDouble(KEY_SAVED_LOCATION_LNG, map.getCameraPosition().target.longitude);
        outState.putDouble(KEY_SAVED_LOCATION_ZOOM, map.getCameraPosition().zoom);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_SAVED_LOCATION_LAT) && savedInstanceState.containsKey(KEY_SAVED_LOCATION_LNG)) {
                savedLocation = new LatLng(
                        savedInstanceState.getDouble(KEY_SAVED_LOCATION_LAT),
                        savedInstanceState.getDouble(KEY_SAVED_LOCATION_LNG)
                );
            }

            if (savedInstanceState.containsKey(KEY_SAVED_LOCATION_ZOOM)) {
                savedZoon = savedInstanceState.getDouble(KEY_SAVED_LOCATION_ZOOM);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        savedZoon = map.getCameraPosition().zoom;
        savedLocation = new LatLng(
                map.getCameraPosition().target.latitude,
                map.getCameraPosition().target.longitude
        );
    }

    @Override
    public void onResume() {
        super.onResume();

        preSetupMap();

        markers.clear();
        airplanes.clear();
        currentPolyTrack.clear();

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("DEBUG", "Service conectado");
                updaterService = ((UpdaterService.Controller) service).getUpdaterService();
                updaterService.setOnAirplanesUpdateListener(MapFragment.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("DEBUG", "Service desconectado");
            }
        };

        Intent intent = new Intent("SOLVER_HELPER_SERVICE");
        intent.setPackage("radarlivre.com.radarlivreandroid");
        getActivity().bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    public void preSetupMap() {
        GoogleMapOptions options = new GoogleMapOptions();
        options.zOrderOnTop(true);
        mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mapView, mapFragment);
        fragmentTransaction.commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapFragment.this.map = googleMap;
                postSetupMap();
            }
        });
    }

    public void postSetupMap() {
        LatLng location = null;
        double zoom = 1.0;

        if(savedLocation == null) {
            location = new LatLng(-14.950841,-52.1189968);
            zoom = 3.5f;
        } else {
            location = savedLocation;
            zoom = savedZoon;
        }

        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom((float) zoom).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        map.moveCamera(cameraUpdate);

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = inflater.inflate(R.layout.airplane_info, null);

                TextView tvVoo = (TextView) view.findViewById(R.id.tvVoo);
                TextView tvAir = (TextView) view.findViewById(R.id.tvAirline);
                TextView tvLat = (TextView) view.findViewById(R.id.tvLatitude);
                TextView tvLng = (TextView) view.findViewById(R.id.tvLongitude);
                TextView tvAlt = (TextView) view.findViewById(R.id.tvAltitude);
                TextView tvSpe = (TextView) view.findViewById(R.id.tvSpeed);

                Airplane airplane = airplanes.get(marker.getTitle());
                Airline airline = Airline.getAirLineFron(airplane.getId());

                if (airline == null) {
                    tvVoo.setText("Sem identificação");
                    tvAir.setText("");
                } else {
                    tvVoo.setText(airline.getIdVoo() + " / " + airplane.getId());
                    tvAir.setText(airline.getAirline() + " - " + airline.getCountry());
                }

                tvLat.setText("" + airplane.getLastPosition().getLatitude());
                tvLng.setText("" + airplane.getLastPosition().getLongitude());
                tvAlt.setText("" + airplane.getLastPosition().getAltitude() + " ft " + (float) ((int) (airplane.getLastPosition().getAltitude() * 30.48)) / 100 + " m");
                tvSpe.setText("" + airplane.getSpeed() + " knots " + (float) ((int) (airplane.getSpeed() * 185.2)) / 100 + " k.m/h\n");

                return view;
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                removeAirplaneTrack();
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                removeAirplaneTrack();
                marker.showInfoWindow();
                Airplane airplane = airplanes.get(marker.getTitle());
                currentAirplaneSelected = airplane;
                addAirplaneTrack(airplane);

                return false;
            }
        });

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                for (Marker m : markers) {
                    //float angle = (float)airplanes.get(m.getTitle()).getHead() + cameraPosition.bearing;
                    //m.setIcon(BitmapDescriptorFactory.fromBitmap(getAirplaneIcon(angle)));
                }
            }
        });

        //buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Location location = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                        if (location != null) {
                            LatLng mapsLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(mapsLocation).zoom(7).build();
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                            map.animateCamera(cameraUpdate);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();
    }

    public void addMarker(Airplane airplane) {
        AirplanePosition position = airplane.getLastPosition();
        LatLng location = new LatLng(position.getLatitude(), position.getLongitude());
        float angle = (float)airplane.getHead() + map.getCameraPosition().bearing;

        MarkerOptions options = new MarkerOptions();
        options.position(location);
        options.title(airplane.getHex());
        options.icon(BitmapDescriptorFactory.fromBitmap(getAirplaneIcon(angle)));
        options.anchor(0.5f, 0.5f);

        Marker marker = map.addMarker(options);
        markers.add(marker);
    }

    public void updateMarker(Airplane airplane) {
        for(Marker m: markers) {
            boolean infoShow = m.isInfoWindowShown();
            if(m.getTitle().equals(airplane.getHex())) {
                AirplanePosition position = airplane.getLastPosition();
                LatLng location = new LatLng(position.getLatitude(), position.getLongitude());
                float angle = (float)airplane.getHead() + map.getCameraPosition().bearing;
                m.setPosition(location);
                m.setIcon(BitmapDescriptorFactory.fromBitmap(getAirplaneIcon(angle)));

                if(infoShow)
                    m.showInfoWindow();
                break;
            }
        }
    }

    public void removeMarker(Airplane airplane) {
        synchronized (markers) {
            for (Marker m : markers) {
                if (m.getTitle().equals(airplane.getHex())) {
                    m.remove();
                    markers.remove(m);
                    airplanes.remove(airplane.getHex());
                    break;
                }
            }
        }
    }

    public void addAirplaneTrack(Airplane airplane) {
        updaterService.doGetRouteFrom(airplane);
        List<AirplanePosition> track = airplane.getRoute();

        for(int i = 0; i < track.size() - 1; i++) {
            Log.i("DEBUG", "Criando linha para o avião");
            AirplanePosition pos1 = track.get(i);
            AirplanePosition pos2 = track.get(i + 1);
            Polyline line = map.addPolyline(new PolylineOptions()
                            .add(new LatLng(pos1.getLatitude(), pos1.getLongitude()), new LatLng(pos2.getLatitude(), pos2.getLongitude()))
                            .width(5)
                            .color(getColorFromAltitude((float) pos1.getAltitude()))
            );

            currentPolyTrack.put(pos1.getId(), line);
        }

    }

    public void updateAirplaneTrack(Airplane airplane) {
        if(currentAirplaneSelected != null && currentAirplaneSelected.getHex().equals(airplane.getHex())) {
            List<AirplanePosition> track = airplane.getRoute();

            for (int i = 0; i < track.size() - 1; i++) {
                AirplanePosition pos1 = track.get(i);
                AirplanePosition pos2 = track.get(i + 1);

                if (!currentPolyTrack.containsKey(pos1.getId())) {
                    Log.i("DEBUG", "Atualizando linha para o avião");
                    Polyline line = map.addPolyline(new PolylineOptions()
                                    .add(new LatLng(pos1.getLatitude(), pos1.getLongitude()), new LatLng(pos2.getLatitude(), pos2.getLongitude()))
                                    .width(5)
                                    .color(getColorFromAltitude((float) pos1.getAltitude()))
                    );
                    currentPolyTrack.put(pos1.getId(), line);
                }
            }
        }
    }

    public void removeAirplaneTrack() {
        if(currentAirplaneSelected != null)
            synchronized (currentPolyTrack) {
                updaterService.doRemoveRouteFrom(currentAirplaneSelected);

                for (Polyline line : currentPolyTrack.values()) {
                    Log.i("DEBUG", "Removendo Linha");
                    line.remove();
                }

                currentPolyTrack.clear();
                currentAirplaneSelected = null;
            }
    }

    private int getColorFromAltitude(float altitude) {
        if(colors == null) {
            colors = new ArrayList<>();
            for (int i = 0; i < 256; i++) {
                int color = Color.argb(255, 255, i, 0);
                colors.add(color);
            }

            for (int i = 255; i >= 0; i--) {
                int color = Color.argb(255, i, 255, 0);
                colors.add(color);
            }

            for (int i = 0; i < 256; i++) {
                int color = Color.argb(255, 0, 255, i);
                colors.add(color);
            }

            for (int i = 255; i >= 0; i--) {
                int color = Color.argb(255, 0, i, 255);
                colors.add(color);
            }
        }
        int colorSize = colors.size();
        int altMax = 50000;

        int colorIndex = (int)(colorSize * altitude/altMax);

        return colors.get(colorIndex);
    }

    private Bitmap getAirplaneIcon(float angle) {
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.airplane);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(myBitmap, dpToPx(32), dpToPx(32), true);

        Matrix matrix = new Matrix();
        matrix.postRotate(angle, scaledBitmap.getWidth(), scaledBitmap.getHeight());

        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        return rotatedBitmap;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public void doHideLoading() {
        if(loadingLayout != null)
            loadingLayout.setVisibility(View.GONE);
    }

    public void doShowLoading() {
        if(loadingLayout != null)
            loadingLayout.setVisibility(View.VISIBLE);
    }

    // Monitoring updater service

    @Override
    public void onAirplanesUpdated(List<Airplane> airplanes) {

    }

    @Override
    public void onAirplaneAdded(final Airplane airplane) {
        if(getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!airplanes.containsKey(airplane.getHex())) {
                        airplanes.put(airplane.getHex(), airplane);
                        addMarker(airplane);
                    } else {
                        updateMarker(airplane);
                    }
                }
            });

    }

    @Override
    public void onAirplaneUpdated(final Airplane airplane) {
        if(getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!airplanes.containsKey(airplane.getHex())) {
                        airplanes.put(airplane.getHex(), airplane);
                        addMarker(airplane);
                    } else {
                        updateMarker(airplane);
                    }
                    updateAirplaneTrack(airplane);
                }
            });

    }

    @Override
    public void onAirplaneRemoved(final Airplane airplane) {
        if(getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    airplanes.remove(airplane.getHex());
                    removeMarker(airplane);
                    removeAirplaneTrack();

                }
            });

    }

    @Override
    public void onServerConnected() {
        if(getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doHideLoading();
                }
            });

    }

    @Override
    public void onServerConnecting() {
        if(getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doShowLoading();
                }
            });
    }

    @Override
    public void onServerDisconnected() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
