package com.juny.junymusic.player;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

import com.juny.junymusic.IMediaPlaybackService;
import com.juny.junymusic.R;
import com.juny.junymusic.service.MediaPlaybackService;
import com.juny.junymusic.util.Utils;

/**
 * Created by Administrator on 2015-04-05.
 */
public class MiniPlayerFragment extends Fragment {

    private final int PRO_MAX = 100;
    private final int REFRESH = 0;
    private long mDuration;

    private View rootView;
    private RelativeLayout mMiniPlayerLayout;
    private ImageView mMiniPlayerArtWork;
    private TextView mMiniPlayerTitle;
    private ProgressBar mMiniPlayerProgressBar;
    private ImageView mMiniPlayerPlayBtn;

    private Activity mActivity;

    private Utils.ServiceToken mToken;
    private IMediaPlaybackService sService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sService = IMediaPlaybackService.Stub.asInterface(service);
            checkShowHide();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sService = null;
        }
    };

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

        mToken = Utils.bindToService(getActivity(), conn);

        IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.META_CHANGED);
        f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
        mActivity.registerReceiver(mReceiver, new IntentFilter(f));
//        showHide(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("hjbae", "Mini: onCreateView");
        rootView = inflater.inflate(R.layout.mini_player_fragment, container, false);
        mMiniPlayerLayout = (RelativeLayout) rootView.findViewById(R.id.mini_player_layout);
        mMiniPlayerLayout.setOnClickListener(mBodyListener);
        mMiniPlayerArtWork = (ImageView) rootView.findViewById(R.id.mini_player_img);
        mMiniPlayerTitle = (TextView) rootView.findViewById(R.id.mini_player_txt);
        mMiniPlayerProgressBar = (ProgressBar) rootView.findViewById(R.id.mini_player_progress);
        mMiniPlayerProgressBar.setMax(PRO_MAX);
        mMiniPlayerPlayBtn = (ImageView) rootView.findViewById(R.id.mini_player_btn);
        mMiniPlayerPlayBtn.setOnClickListener(mPlayBtnListener);

        return rootView;
    }

    private View.OnClickListener mBodyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent("com.juny.junymusic.PLAYBACK_VIEWER")
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
    };

    private View.OnClickListener mPlayBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Utils.sService == null)
                return;

            try {
                if (Utils.sService.isPlaying()) {
                    mHandler.removeMessages(REFRESH);
                    Utils.sService.pause();
                }
                else {
                    queueNextRefresh(1);
                    Utils.sService.play();
                }
                refreshPlayBtn();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private void refreshPlayBtn() {
        if (Utils.sService == null)
            return;

        try {
            if (Utils.sService.isPlaying()) {
                mMiniPlayerPlayBtn.setImageResource(R.drawable.btn_pausesmall_default);
            }
            else {
                mMiniPlayerPlayBtn.setImageResource(R.drawable.btn_play02_default);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("hjbae", "Mini: onActivityCreated");
    }

    private void init_UI() {
        if (Utils.sService == null) {
            Log.e("hjbae", "Mini :: loadCurrentArtWork is Fail @@@@");
            return;
        }

        try {
            // Album Art
            long album_id = Utils.sService.getAlbumId();
            Uri uri = Utils.getAlbumartUri(album_id);
            Utils.setImage(uri, mMiniPlayerArtWork);

            // Title
            String title = Utils.sService.getTrackName();
            String artist = Utils.sService.getArtistName();
            mMiniPlayerTitle.setText(title + " - " + artist);
            mMiniPlayerTitle.setSelected(true);

            mDuration = Utils.sService.duration();
            int delay = refreshNow();
            queueNextRefresh(delay);

            refreshPlayBtn();
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
        Utils.unbindFromService(mToken);
        mActivity.unregisterReceiver(mReceiver);
        mHandler.removeMessages(REFRESH);
        super.onDestroy();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaPlaybackService.META_CHANGED)) {
                checkShowHide();
            }
        }
    };

    private void checkShowHide() {
        try {
            if (sService != null && sService.getAudioId() != -1) {
                init_UI();
                showHide(true);
                return;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        showHide(false);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH:
                    int delay = refreshNow();
                    queueNextRefresh(delay);
                    break;
            }
        }
    };

    private void queueNextRefresh(int delay) {
        Message msg = mHandler.obtainMessage(REFRESH);
        mHandler.removeMessages(REFRESH);
        mHandler.sendMessageDelayed(msg, delay);
    }

    private int refreshNow() {
        if (Utils.sService == null)
            return 500;

        try {
            long curPos = Utils.sService.position() ;
            int progress = (int)(PRO_MAX * curPos / mDuration);
            mMiniPlayerProgressBar.setProgress(progress);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return 500;
    }
}
