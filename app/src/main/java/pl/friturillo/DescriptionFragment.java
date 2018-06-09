package pl.friturillo;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DescriptionFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_description, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView descriptionText = getActivity().findViewById(R.id.description_text);

        SpannableString[] description = new SpannableString[3];
        description[0] = new SpannableString(getResources().getString(R.string.description_header) + "\n\n");
        description[1]= new SpannableString(getResources().getString(R.string.project_description) + "\n\n");
        description[2] = new SpannableString(getResources().getString(R.string.description_bottom));

        description[0].setSpan(new StyleSpan(Typeface.BOLD), 0, description[0].length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (SpannableString ss : description)
            builder.append(ss);

        descriptionText.setText(builder);
    }
}
