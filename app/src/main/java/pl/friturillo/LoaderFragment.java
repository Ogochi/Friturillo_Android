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

import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoaderFragment extends Fragment {
    private String start, end;

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
                        moveToRouteSearch(addresses.get(0), addresses.get(1));
                    } else {
                        Log.w("GEO", "Geocoding failed");
                        returnToSearch();
                    }
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("GEO", "Geocoding failed");
            returnToSearch();
        }
    }

    private void returnToSearch() {
        Snackbar.make(getActivity().findViewById(R.id.progressBar), "Blad!", Snackbar.LENGTH_LONG)
                .setAction("Geocoding api return empty", null).show();

        FragmentChangeable fg = (FragmentChangeable)getActivity();
        fg.setFragment(new SearchFragment(), true);
    }

    private void moveToRouteSearch(Address start, Address end) {
        Bundle bundle = new Bundle();
        bundle.putDouble("startLon", start.getLongitude());
        bundle.putDouble("startLat", start.getLatitude());
        bundle.putDouble("endLon", start.getLongitude());
        bundle.putDouble("endLat", start.getLatitude());
        Fragment newFragment = new RouteFragment();
        newFragment.setArguments(bundle);

        FragmentChangeable fg = (FragmentChangeable)getActivity();
        fg.setFragment(newFragment, false);
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
            returnToSearch();
            return;
        }

        getAddresses(geocoder);
    }
}
