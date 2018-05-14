package info.androidhive.sample;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.androidhive.gradientview.GradientView;
import info.androidhive.sample.model.Offer;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopSliderFragment extends Fragment {

    public static final String TAG = TopSliderFragment.class.getSimpleName();

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.layoutDots)
    LinearLayout dotsLayout;

    TextView[] dots;

    private List<Offer> offerList;
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

        offerList = new ArrayList<>();
        mAdapter = new MyPagerAdapter(getActivity());
        viewPager.setAdapter(mAdapter);

        prepareItems();

        addBottomDots(0);

        ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        };

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        return view;
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[offerList.size()];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getActivity());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(30);
            dots[i].setTextColor(getResources().getColor(R.color.pager_dot_inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.pager_dot_active));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    private void prepareItems() {
        Offer offer = new Offer("CRAZY DEALS", "30-60% off on women clothing",
                "https://api.androidhive.info/images/shop/women-fashion.jpg");
        offerList.add(offer);

        offer = new Offer("UPTO 40% OFF ON", "men shoes - all brands",
                "https://api.androidhive.info/images/shop/men-fashion.jpg");
        offerList.add(offer);

        offer = new Offer("KIDS FESTIVE", "buy 1 get 2 free",
                "https://api.androidhive.info/images/shop/kids-wear.jpg");
        offerList.add(offer);

        offer = new Offer("BUY 1 GET 1", "limited offer on selected products",
                "https://api.androidhive.info/images/shop/daily-discounts.jpg");
        offerList.add(offer);

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

            ((TextView) view.findViewById(R.id.title)).setText(offerList.get(position).getName());
            ((TextView) view.findViewById(R.id.description)).setText(offerList.get(position).getDescription());
            ImageView background = view.findViewById(R.id.background);
            Glide.with(getActivity()).load(offerList.get(position).getImageUlr())
                    .into(background);

            GradientView gradientView = view.findViewById(R.id.gradient_view);
            gradientView.setColorPalette(getColorPalette(position));

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return offerList.size();
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

    private int[] getColorPalette(int position) {
        switch (position) {
            case 0:
                return getResources().getIntArray(R.array.toolbar_color_palette2);

            case 1:
                return getResources().getIntArray(R.array.toolbar_color_palette1);

            case 2:
                return getResources().getIntArray(R.array.toolbar_color_palette3);

            case 3:
                return getResources().getIntArray(R.array.toolbar_color_palette_default);
            default:
                return null;
        }
    }
}
