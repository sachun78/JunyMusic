package com.juny.junymusic.mysongs;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juny.junymusic.R;

/**
 * Created by Administrator on 2015-04-04.
 */
public class TabArtistFragment extends Fragment {

    public static TabArtistFragment newInstance() {
        TabArtistFragment fragment = new TabArtistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tabartist_fragment, container, false);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
