package pl.friturillo;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.util.Consumer;
import android.util.Log;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InputAutocompleteTask extends AsyncTask<String, Integer, List<String>> {
    private GeoDataClient geoDataClient;
    private int waitTimeInMilisec;
    private Consumer<List<String>> onSuccess;
    private static LatLngBounds warsawLatLngBounds;

    InputAutocompleteTask(Activity activity, int waitTimeInMilisec, Consumer<List<String>> onSuccess) {
        this.onSuccess = onSuccess;
        geoDataClient = Places.getGeoDataClient(activity);
        this.waitTimeInMilisec = waitTimeInMilisec;
        warsawLatLngBounds = new LatLngBounds(
                new LatLng(52.087037, 20.803345), new LatLng(52.316750, 21.118700));
    }

    @Override
    protected List<String> doInBackground(String... urls) {
        List<String> result = new ArrayList<>();
        AutocompleteFilter noneFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS).build();

        for (String url : urls) {
            Task<AutocompletePredictionBufferResponse> request = geoDataClient.getAutocompletePredictions(
                    url, warsawLatLngBounds, noneFilter);



            try {
                Tasks.await(request, waitTimeInMilisec, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                Log.w("WARNING", "Places autocomplete task did not work.");
                continue;
            }

            for (AutocompletePrediction ap : request.getResult())
                result.add(ap.getFullText(null).toString());
            request.getResult().release();
        }

        return result;
    }

    @Override
    protected void onPostExecute(List<String> result) {
        super.onPostExecute(result);

        onSuccess.accept(result);
    }
}
