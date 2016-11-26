package radarlivre.com.radarlivreandroid.application.activities;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import radarlivre.com.radarlivreandroid.R;
import radarlivre.com.radarlivreandroid.application.maps.MapHolder;
import radarlivre.com.radarlivreandroid.application.service.Task;
import radarlivre.com.radarlivreandroid.application.service.UpdaterScheduler;
import radarlivre.com.radarlivreandroid.application.service.UpdaterService;
import radarlivre.com.radarlivreandroid.model.AbsObject;
import radarlivre.com.radarlivreandroid.model.FlightInfo;
import radarlivre.com.radarlivreandroid.model.Repository;
import radarlivre.com.radarlivreandroid.network.api.FlightInfoAPI;
import radarlivre.com.radarlivreandroid.network.listener.OnReceiveListener;

public class MapFragment extends Fragment {

    @BindView(R.id.map_progressbar)
    ProgressBar mapProgressBar;

    private SupportMapFragment mapFragment = null;
    private GoogleMap map = null;
    private MapHolder mapHolder = null;

    private UpdaterService updaterService = null;
    private ServiceConnection updaterServiceConnection = null;
    private boolean updaterServiceConnected = false;

    public static final String SERVER_HOST = "www.radarlivre.com";
    public static final long UPDATE_FLIGHT_INTERVAL = 5000;
    public static final long UPDATE_AIRPORT_INTERVAL = 5000;
    public static final long UPDATE_ROUTE_INTERVAL = 5000;
    public static final long UPDATE_COLLECTOR_INTERVAL = 5000;

    private Repository repository;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        preSetupMap();

        initRepositories();
        initUpdaterService();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUpdaterService();
    }

    public void preSetupMap() {

        mapProgressBar.setVisibility(View.VISIBLE);

        GoogleMapOptions options = new GoogleMapOptions();
        options.zOrderOnTop(true);
        mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map_view, mapFragment);
        fragmentTransaction.commit();

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                MapFragment.this.map = map;
                MapFragment.this.mapHolder = new MapHolder(map);
                postSetupMap();
            }
        });
    }

    public void postSetupMap() {
        // CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom((float) zoom).build();
        // CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        //map.moveCamera(cameraUpdate);

        /*
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = inflater.inflate(R.layout.airplane_info, null);
                return view;
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // ...
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
        */

    }

    public void initRepositories() {
        repository = Repository.getInstance();
        repository.addListener(new Repository.OnObjectChangedListener() {
            @Override
            public <T extends AbsObject> void onObjectCreated(Class<T> type, T object) {
                Log.i("DEBUG", type.getSimpleName() + " created!");
            }

            @Override
            public <T extends AbsObject> void onObjectUpdated(Class<T> type, T object) {
                Log.i("DEBUG", type.getSimpleName() + " updated!");
            }

            @Override
            public <T extends AbsObject> void onObjectRemoved(Class<T> type, T object) {
                Log.i("DEBUG", type.getSimpleName() + " removed!");
            }
        });
    }

    public void initUpdaterService() {
        Log.i("DEBUG", "MapFragment: Initializing service...");
        Intent intent = new Intent(getContext(), UpdaterService.class);

        getActivity().startService(intent);

        Log.i("DEBUG", "MapFragment: Connecting to service...");
        updaterServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("DEBUG", "MapFragment: Service connected!");
                updaterServiceConnected = true;
                updaterService = ((UpdaterService.UpdaterServiceBinder)iBinder).getService();

                UpdaterScheduler scheduler = updaterService.getUpdaterScheduler();

                scheduler.schedule(new Task(new Runnable() {
                    @Override
                    public void run() {
                        if(map != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getAirplanes();
                                }
                            });
                        }
                    }
                }, UPDATE_FLIGHT_INTERVAL));
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("DEBUG", "MapFragment: Service disconnected!");
                updaterServiceConnected = false;
            }
        };

        getActivity().bindService(intent, updaterServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void getAirplanes() {
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

        Log.i("DEBUG", "Requesting flight infos...");

        final FlightInfoAPI flightInfoAPI = new FlightInfoAPI(SERVER_HOST);
        flightInfoAPI.getFlightInfos(
                7 * 24 * 3600 * 1000,
                getActivity().findViewById(R.id.map_view).getHeight(),
                map.getCameraPosition().zoom,
                bounds.northeast.latitude, bounds.southwest.latitude,
                bounds.southwest.longitude, bounds.northeast.longitude,
                new OnReceiveListener<List<FlightInfo>>() {
                    @Override
                    public void onReceivedSuccessfully(List<FlightInfo> objects) {
                        repository.save(FlightInfo.class, objects, true);
                    }

                    @Override
                    public void onReceivedFailed() {
                        Log.i("DEBUG", "Requesting flight infos failed!");
                    }
                }
        );
    }

    public void stopUpdaterService() {
        Log.i("DEBUG", "MapFragment: Stoping service!");
        getActivity().unbindService(updaterServiceConnection);
        Intent intent = new Intent(getContext(), UpdaterService.class);
        getActivity().stopService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
