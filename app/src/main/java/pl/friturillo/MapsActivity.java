package pl.friturillo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private JSONArray route;
    private List<LatLng> waypoints = new ArrayList<>();

    private void displayMarkers() {
        JSONObject place;
        LatLng point = null;
        for (int i = 0; i < route.length(); i++) {
            try {
                place = (JSONObject)route.get(i);
                point = new LatLng(place.getDouble("longitude"), place.getDouble("latitude"));
                waypoints.add(point);

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

    private void displayPaths(String overviewPolyline) {
        Log.d("POLYLINE", overviewPolyline);
        List<LatLng> path = PolyUtil.decode(overviewPolyline);
        for (LatLng l : path)
            Log.d("LOG", "" + l.latitude);
        mMap.addPolyline(new PolylineOptions().addAll(path).color(Color.BLUE));
    }

    private void getPaths() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                createDirectionsUrl(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("RES", response.toString());
                    response = (JSONObject)((JSONArray)response.get("routes")).get(0);
                    displayPaths(((JSONObject)response.get("overview_polyline")).get("points").toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERR", "Displaying path did not work");
            }
        });

        queue.add(jsonObjectRequest);
    }

    private String createDirectionsUrl() {
        StringBuilder url = new StringBuilder(
                "https://maps.googleapis.com/maps/api/directions/json?mode=bicycling&origin=");

        if (waypoints.size() < 2)
            return "";

        for (int i = 0; i < waypoints.size(); i++) {
            if (i == waypoints.size() - 1) {
                url.append("&destination=");
            } else if (i == 1) {
                url.append("&waypoints=");
            } else if (i != 0) {
                url.append("|");
            }

            url.append(waypoints.get(i).latitude);
            url.append(",");
            url.append(waypoints.get(i).longitude);
        }

        url.append("&key=");
        url.append(getResources().getString(R.string.google_maps_key));

        return  url.toString();
    }

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

        displayMarkers();
        getPaths();
    }
}
