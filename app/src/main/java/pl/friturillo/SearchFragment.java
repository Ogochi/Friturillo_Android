package pl.friturillo;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Consumer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.util.List;

public class SearchFragment extends Fragment {
    private void addChangeListener(AutoCompleteTextView autocomplete, final ArrayAdapter<String> adapter) {
        autocomplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 2) {
                    new InputAutocompleteTask(getActivity(), 10000,
                            new Consumer<List<String>>() {
                                @Override
                                public void accept(List<String> strings) {
                                    adapter.clear();
                                    adapter.addAll(strings);
                                }
                            }).execute(editable.toString());
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ArrayAdapter<String> adapterStart = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line);
        final ArrayAdapter<String> adapterEnd = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line);

        final AutoCompleteTextView textView = getActivity().findViewById(R.id.start_autocomplete);
        final AutoCompleteTextView textView2 = getActivity().findViewById(R.id.end_autocomplete);

        textView.setAdapter(adapterStart);
        textView2.setAdapter(adapterEnd);
        addChangeListener(textView, adapterStart);
        addChangeListener(textView2, adapterEnd);

        getActivity().findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyboard hk = (HideKeyboard)getActivity();
                hk.hideKeyboard();

                String start = textView.getText().toString();
                String end = textView2.getText().toString();

                if (start.equals("") || end.equals("")) {
                    Snackbar.make(view, "Nie wybrales trasy!", Snackbar.LENGTH_LONG)
                            .setAction("Wrong input", null).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("start", start);
                bundle.putString("end", end);
                Fragment newFragment = new LoaderFragment();
                newFragment.setArguments(bundle);

                FragmentChangeable fg = (FragmentChangeable)getActivity();
                fg.setFragment(newFragment, false);
            }
        });
    }
}
