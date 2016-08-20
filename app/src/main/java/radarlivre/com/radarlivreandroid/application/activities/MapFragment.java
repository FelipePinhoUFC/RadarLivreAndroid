package radarlivre.com.radarlivreandroid.application.activities;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import radarlivre.com.radarlivreandroid.R;

public class MapFragment extends Fragment {

    private SupportMapFragment mapFragment = null;
    private GoogleMap map = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
        super.onResume();

        preSetupMap();
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
        // CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom((float) zoom).build();
        // CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        //map.moveCamera(cameraUpdate);

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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
