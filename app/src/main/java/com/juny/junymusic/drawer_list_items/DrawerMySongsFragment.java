package com.juny.junymusic.drawer_list_items;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.juny.junymusic.MainActivity;
import com.juny.junymusic.R;
import com.juny.junymusic.adapter.TabPagerAdapter;
import com.juny.junymusic.data.ConstantData;
import com.juny.junymusic.util.Utils;

/**
 * Created by Administrator on 2015-03-25.
 */
public class DrawerMySongsFragment extends android.support.v4.app.Fragment {

    private Context mContext;
    private int ActionBarColor = 0xFFC74B46;
    private Utils.ServiceToken mToken;

    public static DrawerMySongsFragment newInstance(int sectionNumber) {
        DrawerMySongsFragment fragment = new DrawerMySongsFragment();
        Bundle args = new Bundle();
        args.putInt("ARG_INDEX", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt("ARG_INDEX"));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mToken = Utils.bindToService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.drawer_mysong_fragment, container, false);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) rootView.findViewById(R.id.pager);
        pager.setAdapter(new TabPagerAdapter(this.getChildFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.indicator_tab);
        tabs.setViewPager(pager);
        tabs.setIndicatorColor(ConstantData.ACTIONBAR_AND_TABSELECT_COLOR);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
//        Utils.unbindFromService(mToken);
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
