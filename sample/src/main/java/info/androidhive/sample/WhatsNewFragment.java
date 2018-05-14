package info.androidhive.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class WhatsNewFragment extends Fragment {

    public WhatsNewFragment() {
        // Required empty public constructor
    }

    public static WhatsNewFragment newInstance() {
        WhatsNewFragment fragment = new WhatsNewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_whats_new, container, false);
        ButterKnife.bind(this, view);

        TopSliderFragment fragment = new TopSliderFragment();

        getFragmentManager().beginTransaction()
                .add(R.id.frame_container, fragment)
                .commit();

        return view;
    }
}
