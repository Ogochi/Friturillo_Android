package pl.friturillo;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RouteFragment extends Fragment {
    private void displayRouteDescription() throws JSONException {
        TextView textView = getActivity().findViewById(R.id.routeText);
        SpannableStringBuilder routeString = new SpannableStringBuilder("\n\n\n");
        JSONArray route = new JSONArray((String)getArguments().get("route"));

        int prevETA = 0;
        for (int i = 0; i < route.length(); i++) {
            JSONObject place = (JSONObject)route.get(i);

            if (i != 0) {
                routeString.append("\n\t\tCzas okolo pomiedzy: " + (((Integer)place.get("ETA") - prevETA) / 60) + " minut\n\n");
            }
            SpannableString string = new SpannableString(place.get("name") + "\n");
            string.setSpan(new StyleSpan(Typeface.BOLD), 0, string.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            routeString.append(string);
            prevETA = (Integer)place.get("ETA");
        }

        textView.setText(routeString);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ShowingRoute activity = (ShowingRoute)getActivity();
        activity.showingRoute();

        return inflater.inflate(R.layout.fragment_route, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("ROUTE", (String)getArguments().get("route"));

        try {
            displayRouteDescription();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        getActivity().findViewById(R.id.showMapButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFromJSON activity = (MapFromJSON)getActivity();
                activity.showMapFromJSON((String)getArguments().get("route"));
            }
        });
    }
}
