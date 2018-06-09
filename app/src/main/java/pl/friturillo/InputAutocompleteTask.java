package pl.friturillo;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InputAutocompleteTask extends AsyncTask<String, Integer, List<String>> {
    private GeoDataClient geoDataClient;
    private int waitTimeInMilisec;
    private static LatLngBounds warsawLatLngBounds;

    InputAutocompleteTask(Activity activity, int waitTimeInMilisec) {
        geoDataClient = Places.getGeoDataClient(activity);
        this.waitTimeInMilisec = waitTimeInMilisec;
        warsawLatLngBounds = new LatLngBounds(
                new LatLng(52.071978, 20.699142), new LatLng(52.372384, 21.342611));
    }

    @Override
    protected List<String> doInBackground(String... urls) {
        List<String> result = new LinkedList<>();
        AutocompleteFilter noneFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build();

        for (String url : urls) {
            Task<AutocompletePredictionBufferResponse> request = geoDataClient.getAutocompletePredictions(
                    url, warsawLatLngBounds, noneFilter);

            try {
                Tasks.await(request, waitTimeInMilisec, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                continue;
            }

            Log.d("REQUEST", request.getResult().toString());
        }

        return result;
    }
}
