package pl.friturillo;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoaderFragment extends Fragment {
    private String start, end;
    private static String ROUTE_API_URL = "https://friturillo.pl/dijkstra";

    @SuppressLint("StaticFieldLeak")
    private void getAddresses(final Geocoder geocoder) {
        try {
            new AsyncTask<Void, Void, List<Address>>() {
                @Override
                protected List<Address> doInBackground(Void... voids) {
                    try {
                        List<Address> result = geocoder.getFromLocationName(start, 1);
                        result.addAll(geocoder.getFromLocationName(end, 1));
                        return result;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                }

                @Override
                protected void onPostExecute(List<Address> addresses) {
                    super.onPostExecute(addresses);

                    if (addresses.size() == 2) {
                        getRoute(addresses.get(0), addresses.get(1));
                    } else {
                        Log.w("GEO", "Geocoding failed");
                        returnToSearch("Blad Geocodingu");
                    }
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("GEO", "Geocoding failed");
            returnToSearch("Blad Geocodingu");
        }
    }

    private void returnToSearch(String msg) {
        Snackbar.make(getActivity().findViewById(R.id.progressBar), msg, Snackbar.LENGTH_LONG)
                .setAction("Geocoding api return empty", null).show();

        FragmentChangeable fg = (FragmentChangeable)getActivity();
        fg.setFragment(new SearchFragment());
    }

    private void getRoute(Address start, Address end) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                createUrlWithParams(start, end), null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        receivedRoute(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERR", "JSON req failed");
                        returnToSearch("Nie udalo sie uzyskac trasy");
                    }
                });

        queue.add(jsonArrayRequest);
    }

    private void receivedRoute(JSONArray route) {
        Bundle bundle = new Bundle();
        bundle.putString("route", route.toString());
        Fragment newFragment = new RouteFragment();
        newFragment.setArguments(bundle);

        FragmentChangeable fg = (FragmentChangeable)getActivity();
        fg.setFragment(newFragment);
    }

    private String createUrlWithParams(Address start, Address end) {
        return ROUTE_API_URL + "?station_a=" + start.getLatitude() + "|" + start.getLongitude() +
                "&station_b=" + end.getLatitude() + "|" + end.getLongitude();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        start = getArguments().getString("start");
        end = getArguments().getString("end");

        return inflater.inflate(R.layout.fragment_loader, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Geocoder geocoder = new Geocoder(getActivity());
        if (!Geocoder.isPresent()) {
            returnToSearch("Nie znaleziono funkcjonalnosci Geocoding");
            return;
        }

        getAddresses(geocoder);
    }
}
