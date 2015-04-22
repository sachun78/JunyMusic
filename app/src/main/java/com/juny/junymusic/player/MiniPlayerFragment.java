package com.juny.junymusic.player;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juny.junymusic.R;
import com.juny.junymusic.service.MediaPlaybackService;
import com.juny.junymusic.util.Utils;

/**
 * Created by Administrator on 2015-04-05.
 */
public class MiniPlayerFragment extends Fragment {

    private View rootView;
    private RelativeLayout mMiniPlayerLayout;
    private ImageView mMiniPlayerArtWork;
    private TextView mMiniPlayerTitle;
    private ProgressBar mMiniPlayerProgressBar;
    private ImageView mMiniPlayerPlayBtn;

    private Activity mActivity;

    public MiniPlayerFragment() {}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("hjbae", "Mini: onAttach");
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("hjbae", "Mini: onCreate");
        IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
        f.addAction(MediaPlaybackService.PLAY_START);
        mActivity.registerReceiver(mReceiver, new IntentFilter(f));
        showHide(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("hjbae", "Mini: onCreateView");
        rootView = inflater.inflate(R.layout.mini_player_fragment, container, false);
        mMiniPlayerLayout = (RelativeLayout) rootView.findViewById(R.id.mini_player_layout);
        mMiniPlayerArtWork = (ImageView) rootView.findViewById(R.id.mini_player_img);
        mMiniPlayerTitle = (TextView) rootView.findViewById(R.id.mini_player_txt);
        mMiniPlayerProgressBar = (ProgressBar) rootView.findViewById(R.id.mini_player_progress);
        mMiniPlayerPlayBtn = (ImageView) rootView.findViewById(R.id.mini_player_btn);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("hjbae", "Mini: onActivityCreated");
    }

    private void loadCurrentArtWork() {
        if (Utils.sService == null) {
            Log.e("hjbae", "Mini :: loadCurrentArtWork is Fail @@@@");
            return;
        }
        Log.d("hjbae", "Mini :: loadCurrentArtWork ### ");
        try {
            long album_id = Utils.sService.getAlbumId();
            Uri uri = Utils.getAlbumartUri(album_id);
            Utils.setImage(uri, mMiniPlayerArtWork);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showHide(boolean paramBoolean) {
        if (paramBoolean) {
            getFragmentManager().beginTransaction().show(this).commitAllowingStateLoss();
            return;
        }
        getFragmentManager().beginTransaction().hide(this).commitAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        mActivity.unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("hjbae", "action: " + action);
            if (action.equals(MediaPlaybackService.PLAY_START)) {
                showHide(true);
                loadCurrentArtWork();
            }
        }
    };
}
