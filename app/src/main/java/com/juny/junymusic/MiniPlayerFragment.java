package com.juny.junymusic;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2015-04-05.
 */
public class MiniPlayerFragment extends Fragment implements MainActivity.TestListener{

    private View rootView;
    private RelativeLayout mMiniPlayerLayout;


    public MiniPlayerFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity)activity).addTestListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHide(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.mini_player_fragment, container, false);
        mMiniPlayerLayout = (RelativeLayout) rootView.findViewById(R.id.mini_player_layout);
        return rootView;
    }

    private void showHide(boolean paramBoolean) {
        if (paramBoolean) {
            getFragmentManager().beginTransaction().show(this).commit();
            return;
        }
        getFragmentManager().beginTransaction().hide(this).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMetaChanged() {
        showHide(true);
    }
}
