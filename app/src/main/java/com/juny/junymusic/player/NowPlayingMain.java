package com.juny.junymusic.player;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.juny.junymusic.R;

/**
 * Created by Administrator on 2015-04-19.
 */
public class NowPlayingMain extends FragmentActivity {

    private String mTag = "nowPlaying";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.now_play_main, NowPlayingFragment.newInstance(), mTag).commit();
        }
    }
}
