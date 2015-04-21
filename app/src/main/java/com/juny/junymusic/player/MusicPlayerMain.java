package com.juny.junymusic.player;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.juny.junymusic.IMediaPlaybackService;
import com.juny.junymusic.service.MediaPlaybackService;
import com.juny.junymusic.R;
import com.juny.junymusic.util.Utils;


public class MusicPlayerMain extends ActionBarActivity {

    private static final int PROGRESS_MAX = 100;

    private static final int REFRESH = 1;
    private static final int QUIT = 2;
    private static final int ALBUM_ART_DECODE = 3;
    private static final int GET_ALBUM_ART = 4;

    private AlbumArtHandler mAlbumArtHandler;
    private Worker mWorker;

    private Bitmap mCurrentPlayerArtwork;
    private long mDuration;
    private long mPosOverride = -1;

    private ImageView mPlayerCurrPlaylist;
    private ImageView mPlayerArtwork;
    private TextView mPlayerTtile;
    private TextView mPlayerArtist;
    private ImageView mPlayerVolumeBtn;
    private ImageView mPlayerFavoriteBtn;
    private TextView mPlayerDurationCurrent;
    private TextView mPlayerDurationTotal;
    private SeekBar mPlayerProgressBar;
    private ImageView mPlayerRepeatBtn;
    private ImageView mPlayerRevBtn;
    private ImageView mPlayerPlayBtn;
    private ImageView mPlayerNextBtn;
    private ImageView mPlayerShuffleBtn;

    private Utils.ServiceToken mToken;
    private IMediaPlaybackService sService = null;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sService = IMediaPlaybackService.Stub.asInterface(service);
            StartPlayback();

            try {
                if (sService.getAudioId() >= 0 ||
                    sService.isPlaying() ||
                    sService.getPath() != null)
                {
                    setPauseButtonImage();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            sService = null;
        }
    };

    private BroadcastReceiver mPlayStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaPlaybackService.META_CHANGED)) {
                updateTrackInfo();
                setPauseButtonImage();
                queueNextRefresh(1);
            }
            else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
                setPauseButtonImage();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player_main);

        mWorker = new Worker("Album Art Worker");
        mAlbumArtHandler = new AlbumArtHandler(mWorker.getLooper());

        mPlayerCurrPlaylist = (ImageView) findViewById(R.id.player_curr_list);
        mPlayerCurrPlaylist.setOnClickListener(mCurrPlayListBtn);
        mPlayerArtwork = (ImageView) findViewById(R.id.player_artwork);
        mPlayerTtile = (TextView) findViewById(R.id.player_title);
        /**
         * xml 에서
         * android:ellipsize="marquee"
         * 설정후에도 marquee 동작하지 않을 경우 setSelected 해주어 Focus 받도록 해준다.
         */
        mPlayerTtile.setSelected(true);
        mPlayerArtist = (TextView) findViewById(R.id.player_artist);
        mPlayerVolumeBtn = (ImageView) findViewById(R.id.player_volume_img);
        mPlayerFavoriteBtn = (ImageView) findViewById(R.id.player_favorite_img);
        mPlayerDurationCurrent = (TextView) findViewById(R.id.player_duration_curr);
        mPlayerDurationTotal = (TextView) findViewById(R.id.player_duration_total);
        mPlayerProgressBar = (SeekBar) findViewById(R.id.player_progressbar);
        mPlayerProgressBar.setOnSeekBarChangeListener(mSeekbarChgListener);
        mPlayerProgressBar.setMax(PROGRESS_MAX);
        mPlayerShuffleBtn = (ImageView) findViewById(R.id.player_ctrl_shuffle);
        mPlayerRepeatBtn = (ImageView) findViewById(R.id.player_ctrl_repeat);
        mPlayerRevBtn = (ImageView) findViewById(R.id.player_ctrl_prev);
        mPlayerRevBtn.setOnClickListener(mPrevBtnListenr);
        mPlayerNextBtn = (ImageView) findViewById(R.id.player_ctrl_next);
        mPlayerNextBtn.setOnClickListener(mNextBtnListener);
        mPlayerPlayBtn = (ImageView) findViewById(R.id.player_ctrl_play);
        mPlayerPlayBtn.setOnClickListener(mPlayBtnListener);

    }

    private View.OnClickListener mCurrPlayListBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MusicPlayerMain.this, NowPlayingMain.class);
            startActivity(intent);
        }
    };

    private boolean mSeekbarFromUser = false;
    private SeekBar.OnSeekBarChangeListener mSeekbarChgListener = new SeekBar.OnSeekBarChangeListener() {
        private int mProgress = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            Log.d("hjbae", "## onProgressChanged :: progress: " + progress);
            long currPos = progress * mDuration / PROGRESS_MAX;
            mPlayerDurationCurrent.setText(Utils.makeTimeString(MusicPlayerMain.this, currPos / 1000));

            mSeekbarFromUser = fromUser;
            mProgress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
//            Log.d("hjbae", "@@ onStartTrackingTouch :: progress: " + mProgress);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!mSeekbarFromUser || sService == null)
                return;

            long pos = mProgress * mDuration / PROGRESS_MAX;
            try {
                sService.seek(pos);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            mSeekbarFromUser = false;
//            Log.d("hjbae", "** onStopTrackingTouch :: progress: " + mProgress);
        }
    };

    private View.OnClickListener mPlayBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (sService == null)
                return;

            try {
                if (sService.isPlaying()) {
                    mHandler.removeMessages(REFRESH);
                    sService.pause();
                }
                else {
                    queueNextRefresh(1);
                    sService.play();
                }
                setPauseButtonImage();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener mPrevBtnListenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (sService == null)
                return;

            long pos = 0l;
            try {
                pos = sService.position();
                if (pos > 2000L) {
                    sService.seek(0);
                    sService.play();
                }
                else {
                    sService.prev();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener mNextBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (sService == null)
                return;

            try {
                sService.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mToken = Utils.bindToService(this, conn);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
        intentFilter.addAction(MediaPlaybackService.META_CHANGED);
        registerReceiver(mPlayStateReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPauseButtonImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_player_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void StartPlayback() {
        if (sService == null) {
            return;
        }

        Intent intent = getIntent();
        String filename = "";
        Uri uri = intent.getData();
        if (uri != null && uri.toString().length() > 0) {
            // If this is a file:// URI, just use the path directly instead
            // of going through the open-from-filedescriptor codepath.
            String scheme = uri.getScheme();
            if ("file".equals(scheme)) {
                filename = uri.getPath();
            } else {
                filename = uri.toString();
            }
            try {
                sService.stop();
                sService.openFile(filename);
                sService.play();
                setIntent(new Intent());
            } catch (Exception ex) {
                Log.d("MediaPlaybackActivity", "couldn't start playback: " + ex);
            }
        }

        updateTrackInfo();
        long next = refreshNow();
        queueNextRefresh(next);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case ALBUM_ART_DECODE:
                    /**
                     * ArtWork을 자체 Thread 에서 가져오도록 하였으므로
                     * setImageBitmap는 Handler를 이용해서 UI Thread에서 하도록 해줌
                     */
                    mPlayerArtwork.setImageBitmap((Bitmap)msg.obj);
                    mPlayerArtwork.getDrawable().setDither(true);
                    break;

                case REFRESH:
                    long next = refreshNow();
                    queueNextRefresh(next);
                    break;

                case QUIT:
                    break;
            }
        }
    };

    private static class SongAlbumIdWrapper {
        private long songId;
        private long albumId;

        SongAlbumIdWrapper(long sId, long aId) {
            this.songId = sId;
            this.albumId = aId;
        }
    }

    private class AlbumArtHandler extends Handler {
        private long mBeforeAlbumID = -1;

        private AlbumArtHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            long _songId = ((SongAlbumIdWrapper)msg.obj).songId;
            long _albumId = ((SongAlbumIdWrapper)msg.obj).albumId;

            if (msg.what == GET_ALBUM_ART && (mBeforeAlbumID != _albumId || _albumId < 0)) {
                /**
                 * 새로운 이미지를 디코딩하는 동안 xml에 설정한 디폴트 이미지를 보여주도록 한다.
                 */
                Message _msg = mHandler.obtainMessage(ALBUM_ART_DECODE, null);
                mHandler.removeMessages(ALBUM_ART_DECODE);
                mHandler.sendMessageDelayed(_msg, 300);

                // 표시할 이미지를 불러온다.
                mCurrentPlayerArtwork = Utils.getArtwork(MusicPlayerMain.this, _songId, _albumId);
                if (mCurrentPlayerArtwork == null) {
                    mCurrentPlayerArtwork = Utils.getArtwork(MusicPlayerMain.this, _songId, -1);
                    mBeforeAlbumID = -1;
                }

                if (mCurrentPlayerArtwork != null) {
                    _msg = mHandler.obtainMessage(ALBUM_ART_DECODE, mCurrentPlayerArtwork);
                    mHandler.removeMessages(ALBUM_ART_DECODE);
                    mHandler.sendMessage(_msg);
                }
                mBeforeAlbumID = _albumId;
            }
        }
    }

    private class Worker implements Runnable {

        private final Object mLock = new Object();
        private Looper mLooper;

        Worker(String name) {
            Thread t = new Thread(null, this, name);
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();

            synchronized (mLock) {
                while (mLooper == null) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public Looper getLooper() {
            return mLooper;
        }

        @Override
        public void run() {
            synchronized (mLock) {
                Looper.prepare();
                mLooper = Looper.myLooper();
                mLock.notifyAll();
            }
            Looper.loop();
        }

        public void quit() {
            mLooper.quit();
        }
    }

    private void updateTrackInfo() {

        if (sService == null)
            return;

        try {
            long _songid = sService.getAudioId();
            long _albumid = sService.getAlbumId();
            mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
            mAlbumArtHandler.obtainMessage(GET_ALBUM_ART, new SongAlbumIdWrapper(_songid, _albumid)).sendToTarget();

            mPlayerTtile.setText(sService.getTrackName());
            mPlayerTtile.requestFocus();
            mPlayerArtist.setText(sService.getArtistName());
            mPlayerArtist.requestFocus();

            mDuration = sService.duration();
            mPlayerDurationTotal.setText(Utils.makeTimeString(this, mDuration/1000));
        } catch (RemoteException e) {
            if (mCurrentPlayerArtwork != null) {
                mCurrentPlayerArtwork.recycle();
                mCurrentPlayerArtwork = null;
            }
            e.printStackTrace();
        }
    }

    private void queueNextRefresh(long delay) {
        Message msg = mHandler.obtainMessage(REFRESH);
        mHandler.removeMessages(REFRESH);
        mHandler.sendMessageDelayed(msg, delay);
    }

    private long refreshNow() {

        if (sService == null)
            return 500;

        try {
            long pos = mPosOverride < 0 ? sService.position() : mPosOverride;

            // SeekBar가 User에 의해 움질일 경우에는 업데이트 하지 않음
            if (!mSeekbarFromUser) {
                if (pos >= 0 && mDuration > 0) {
                    mPlayerDurationCurrent.setText(Utils.makeTimeString(this, pos / 1000));
                    int progress = (int) (PROGRESS_MAX * pos / mDuration);
                    mPlayerProgressBar.setProgress(progress);
                } else {
                    mPlayerDurationCurrent.setText("--:--");
                    mPlayerProgressBar.setProgress(PROGRESS_MAX);
                }
            }
            long remains = PROGRESS_MAX - (pos % PROGRESS_MAX);

            int width = mPlayerProgressBar.getWidth();
            if (width <= 0) {
                width = 320;
            }
//            Log.d("hjbae", "refreshNow :: width: " + width + " duration: " + mDuration);

            long smothRefreshTime = mDuration / width;

            if (smothRefreshTime > remains) {
                return remains;
            }

            if (smothRefreshTime < 20) {
                return 20;
            }
            return smothRefreshTime;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 500;
    }

    private void setPauseButtonImage() {
        if (sService == null)
            return;

        try {
            if (sService.isPlaying()) {
                mPlayerPlayBtn.setImageResource(R.drawable.btn_pause_default);
            } else {
                mPlayerPlayBtn.setImageResource(R.drawable.btn_play_default);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        mHandler.removeMessages(REFRESH);
        unregisterReceiver(mPlayStateReceiver);
        Utils.unbindFromService(mToken);
        sService = null;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mCurrentPlayerArtwork != null) {
            mCurrentPlayerArtwork.recycle();
            mCurrentPlayerArtwork = null;
        }
        mWorker.quit();
        super.onDestroy();
    }
}
