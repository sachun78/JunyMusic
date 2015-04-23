package com.juny.junymusic.player;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juny.junymusic.R;

/**
 * Created by Administrator on 2015-04-19.
 */
public class NowPlayingMain extends FragmentActivity {

    private String mTag = "nowPlaying";

    private TextView mNowPlayTitle;
    private ImageView mNowPlayCloseBtn;
    private ImageView mNowPlayListPlus;

    private static final int MODE_NOW_PLAYING = 0;
    private static final int MODE_LYRIC_VIEW = 1;
    private int mMode = MODE_NOW_PLAYING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying_main);

        Bundle bundle = getIntent().getExtras();
        String mode = bundle.getString("VIEWMODE");
        if (mode.equals("nowplaying")) {
            mMode = MODE_NOW_PLAYING;
        }
        else {
            mMode = MODE_LYRIC_VIEW;
        }

        if (savedInstanceState == null) {
            if (mMode == MODE_NOW_PLAYING) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.now_play_main, NowPlayingFragment.newInstance(), mTag).commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.now_play_main, LyricViewFragment.newInstance(), "LyricView").commit();
            }
        }

        mNowPlayListPlus = (ImageView) findViewById(R.id.now_play_listplus);
        mNowPlayTitle = (TextView) findViewById(R.id.now_play_title);
        if (mMode == MODE_NOW_PLAYING) {
            mNowPlayListPlus.setVisibility(View.VISIBLE);
            mNowPlayTitle.setText(R.string.now_playing_list);
        } else {
            mNowPlayListPlus.setVisibility(View.GONE);
            mNowPlayTitle.setText(R.string.lyric_viewer);
        }
        mNowPlayCloseBtn = (ImageView) findViewById(R.id.now_play_closebtn);
        mNowPlayCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackAndTransition();
            }
        });
    }

    @Override
    public void onBackPressed() {
        goBackAndTransition();
    }

    private void goBackAndTransition() {
        Intent intent = new Intent(this, MusicPlayerMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        if (mMode == MODE_NOW_PLAYING ) {
            overridePendingTransition(R.anim.slide_in_from_top, R.anim.abc_slide_out_bottom);
        } else {
            overridePendingTransition(R.anim.juny_slide_in_left, R.anim.juny_slide_out_left);
        }
    }
}
