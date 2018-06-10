package pl.friturillo;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private JSONArray route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            route = new JSONArray(getIntent().getStringExtra("JSON_ROUTE"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        JSONObject place;
        LatLng point = null;
        for (int i = 0; i < route.length(); i++) {
            try {
                place = (JSONObject)route.get(i);
                point = new LatLng(place.getDouble("longitude"), place.getDouble("latitude"));

                mMap.addMarker(new MarkerOptions().position(point).title(place.getString("name")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (point != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            mMap.animateCamera(CameraUpdateFactory.zoomTo( 10.0f ));
        }
    }
}
