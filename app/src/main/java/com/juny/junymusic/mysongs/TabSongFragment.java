package com.juny.junymusic.mysongs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.juny.junymusic.IMediaPlaybackService;
import com.juny.junymusic.R;
import com.juny.junymusic.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

//import android.content.Loader;

//import android.content.Loader;

/**
 * Created by Administrator on 2015-04-04.
 */
public class TabSongFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected boolean pauseOnFling = false;
    protected boolean pauseOnScroll = false;

    private TabSongsCursorAdapter mAdapter;
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private Utils.ServiceToken mToken;
    private static IMediaPlaybackService mService = null;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMediaPlaybackService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private void applyScrollListener()
    {
        getListView().setOnScrollListener(new PauseOnScrollListener(this.imageLoader, this.pauseOnScroll, this.pauseOnFling));
    }

    public static TabSongFragment newInstance() {
        TabSongFragment fragment = new TabSongFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mAdapter = new TabSongsCursorAdapter(getActivity(), null, 0);
        setListAdapter(this.mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToken = Utils.bindToService(getActivity(), mConn);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetInvalidated();
        applyScrollListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tabsong_fragment, container, false);
        return rootView;
    }

    @Override
    public void onDestroy() {
        Utils.unbindFromService(mToken);
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = this.mAdapter.getCursor();

        if (cursor.getCount() == 0)
            return;

        Utils.playAll(getActivity(), cursor, position);
//        getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);


//        this.mAdapter.getItem(position);
//
//        int idx_id = cursor.getColumnIndex("_id");
//        int idx_data = cursor.getColumnIndex("_data");
//
//        long _id = cursor.getLong(idx_id);
//        String _data = cursor.getString(idx_data);

//        Intent intent = new Intent(getActivity(), MusicPlayerMain.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("SELECT_DATA", _data);
//        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        System.out.println("hjbae:: onCreateLoader");
        Uri localUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] arrayOfString1 = { "_id", "title", "artist", "album", "duration", "album_id", "_data" };
        String[] arrayOfString2 = { "1" };
//        setListShown(false);
        return new CursorLoader(getActivity(), localUri, arrayOfString1, "is_music = ?", arrayOfString2, "title COLLATE LOCALIZED ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        System.out.println("hjbae:: onLoadFinished");
        this.mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        System.out.println("hjbae:: onLoaderReset");
        this.mAdapter.swapCursor(null);
    }

    private class TabSongsCursorAdapter extends CursorAdapter{

        public TabSongsCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        public TabSongsCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater mInflater = LayoutInflater.from(context);
            View rootView = mInflater.inflate(R.layout.listitem_songs_tab, null);

            ViewHolder vh = new ViewHolder();
            vh.mAlbumArt = (ImageView) rootView.findViewById(R.id.list_songs_albumart);
            vh.mTitle = (TextView) rootView.findViewById(R.id.list_song_title);
            vh.mArtist = (TextView) rootView.findViewById(R.id.list_song_artist);
            vh.mCurrentFlag = (ImageView) rootView.findViewById(R.id.list_song_current);
            vh.mDuration = (TextView) rootView.findViewById(R.id.list_song_duration);

            rootView.setTag(vh);
            return rootView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int idx_id = cursor.getColumnIndex("_id");
            int idx_title = cursor.getColumnIndex("title");
            int idx_artist = cursor.getColumnIndex("artist");
            int idx_duration = cursor.getColumnIndex("duration");
            int idx_album_id = cursor.getColumnIndex("album_id");

            ViewHolder _vh = (ViewHolder) view.getTag();

            long songid = cursor.getLong(idx_id);
            long albumid = cursor.getLong(idx_album_id);
//            Bitmap bm = Utils.getArtwork(context, songid, albumid, true, 4);
//            _vh.mAlbumArt.setImageBitmap(bm);

            Uri localUri = Utils.getAlbumartUri(albumid);
            Utils.setImage(localUri, _vh.mAlbumArt);

            // 현재 재생되는 아이템에서만 표시되는 애니메이션 효과
            AnimationDrawable mAnimDrawable;
            _vh.mCurrentFlag.setVisibility(View.GONE);
            if (Utils.sService != null) {
                try {
                    if (songid == Utils.sService.getAudioId()) {
                        mAnimDrawable = (AnimationDrawable)_vh.mCurrentFlag.getBackground();
                        if (Utils.sService.isPlaying()) {
                            _vh.mCurrentFlag.setVisibility(View.VISIBLE);
                            mAnimDrawable.start();
                        }
                        else {
                            _vh.mCurrentFlag.setVisibility(View.GONE);
                            mAnimDrawable.stop();
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            _vh.mTitle.setText(cursor.getString(idx_title));
            _vh.mArtist.setText(cursor.getString(idx_artist));
            _vh.mDuration.setText(Utils.makeTimeString(context, cursor.getLong(idx_duration)/1000));
        }

        private class ViewHolder {
            ImageView mAlbumArt;
            TextView mTitle;
            TextView mArtist;
            ImageView mCurrentFlag;
            TextView mDuration;
        }
    }
}
