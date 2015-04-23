package com.juny.junymusic.player;

import android.content.Intent;
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MusicPlayerMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_top, R.anim.abc_slide_out_bottom);
    }
}
