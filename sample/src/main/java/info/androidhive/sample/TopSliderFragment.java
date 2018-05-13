package info.androidhive.sample;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopSliderFragment extends Fragment {

    public static final String TAG = TopSliderFragment.class.getSimpleName();

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private List<TopItem> itemList;
    private MyPagerAdapter mAdapter;


    public TopSliderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_slider, container, false);
        ButterKnife.bind(this, view);

        itemList = new ArrayList<>();
        mAdapter = new MyPagerAdapter(getActivity());
        viewPager.setAdapter(mAdapter);

        prepareItems();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void prepareItems() {
        for (int i = 0; i < 6; i++) {
            TopItem item = new TopItem();
            item.setMessage("Hello from INDIA " + i);
            item.setImageUrl("");

            itemList.add(item);
        }

        mAdapter.notifyDataSetChanged();

    }

    class MyPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private Context context;

        public MyPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.top_slider_pager_item, container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
