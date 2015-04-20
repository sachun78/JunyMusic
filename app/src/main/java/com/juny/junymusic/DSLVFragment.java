package com.juny.junymusic;

import android.app.Activity;
import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juny.junymusic.util.Utils;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.Arrays;

public class DSLVFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

//    ArrayAdapter<String> adapter;
    TabSongsCursorAdapter adapter;

    private String[] array;
    private ArrayList<String> list;
    private String [] mColumn;

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {

                    if (from != to) {
                        if (Utils.sService == null) {
                            return;
                        }
                        Log.d("hjbae", "onDrop:: from: " + from + " to: " + to);
                        try {
                            Utils.sService.moveQueueItem((int)from, (int)to);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

    private DragSortListView.RemoveListener onRemove =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {

                    if (Utils.sService == null) {
                        return;
                    }
                    Log.d("hjbae", "onRemove:: which: " + which);
                    try {
                        Cursor c = (Cursor)adapter.getItem(which);
                        int idx = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                        long _id = c.getLong(idx);
                        Utils.sService.removeTrack(_id);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };

    protected int getLayout() {
        // this DSLV xml declaration does not call for the use
        // of the default DragSortController; therefore,
        // DSLVFragment has a buildController() method.
        return R.layout.dslv_fragment_main;
    }

    /**
     * Return list item layout resource passed to the ArrayAdapter.
     */
    protected int getItemLayout() {
        /*if (removeMode == DragSortController.FLING_LEFT_REMOVE || removeMode == DragSortController.SLIDE_LEFT_REMOVE) {
            return R.layout.list_item_handle_right;
        } else */
        if (removeMode == DragSortController.CLICK_REMOVE) {
            return R.layout.list_item_click_remove;
        } else {
            return R.layout.list_item_handle_left;
        }
    }

    private DragSortListView mDslv;
    private DragSortController mController;

    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = true;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;

    public static DSLVFragment newInstance(int headers, int footers) {
        DSLVFragment f = new DSLVFragment();

        Bundle args = new Bundle();
        args.putInt("headers", headers);
        args.putInt("footers", footers);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mColumn = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA
        };
    }

    public DragSortController getController() {
        return mController;
    }

    /**
     * Called from DSLVFragment.onActivityCreated(). Override to
     * set a different adapter.
     */
    public void setListAdapter() {
        array = getResources().getStringArray(R.array.jazz_artist_names);
        list = new ArrayList<String>(Arrays.asList(array));

//        adapter = new ArrayAdapter<String>(getActivity(), getItemLayout(), R.id.text, list);
        adapter = new TabSongsCursorAdapter(getActivity(), null, 0);
        setListAdapter(adapter);
    }

    /**
     * Called in onCreateView. Override this to provide a custom
     * DragSortController.
     */
    public DragSortController buildController(DragSortListView dslv) {
        // defaults are
        //   dragStartMode = onDown
        //   removeMode = flingRight
        Log.d("hjbae", "== buildController ==");
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        return controller;
    }


    /** Called when the activity is first created. */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("hjbae", "== onCreateView ==");
        mDslv = (DragSortListView) inflater.inflate(getLayout(), container, false);

        mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(dragEnabled);

        return mDslv;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDslv = (DragSortListView) getListView();

        mDslv.setDropListener(onDrop);
        mDslv.setRemoveListener(onRemove);

        Bundle args = getArguments();
        int headers = 0;
        int footers = 0;
        if (args != null) {
            headers = args.getInt("headers", 0);
            footers = args.getInt("footers", 0);
        }

        for (int i = 0; i < headers; i++) {
            addHeader(getActivity(), mDslv);
        }
        for (int i = 0; i < footers; i++) {
            addFooter(getActivity(), mDslv);
        }

        setListAdapter();
        this.getLoaderManager().initLoader(0, null, this);
    }


    public static void addHeader(Activity activity, DragSortListView dslv) {
        LayoutInflater inflater = activity.getLayoutInflater();
        int count = dslv.getHeaderViewsCount();

        TextView header = (TextView) inflater.inflate(R.layout.header_footer, null);
        header.setText("Header #" + (count + 1));

        dslv.addHeaderView(header, null, false);
    }

    public static void addFooter(Activity activity, DragSortListView dslv) {
        LayoutInflater inflater = activity.getLayoutInflater();
        int count = dslv.getFooterViewsCount();

        TextView footer = (TextView) inflater.inflate(R.layout.header_footer, null);
        footer.setText("Footer #" + (count + 1));

        dslv.addFooterView(footer, null, false);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String [] arrayOfString01 = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA
        };
        String [] arrayOfString02 = {"1"};

        return new CursorLoader(getActivity(), uri, arrayOfString01, "is_music = ?", arrayOfString02, "title COLLATE LOCALIZED ASC");
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        this.adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        this.adapter.swapCursor(null);
    }

    private class NowPlayCursor extends AbstractCursor {

        private String [] mCols;
        private Cursor mCurrentPlaylistCursor;     // updated in onMove
        private int mSize;          // size of the queue
        private long[] mNowPlaying;
        private long[] mCursorIdxs;
        private int mCurPos;
        private IMediaPlaybackService mService;

        public NowPlayCursor (IMediaPlaybackService service, String [] cols) {
            mService = service;
            mCols = cols;
            makeNowPlayingCursor();
        }

        private void makeNowPlayingCursor() {
            mCurrentPlaylistCursor = null;

            try {
                mNowPlaying = mService.getQueue();
            } catch (RemoteException e) {
                mNowPlaying = new long[0];
            }

            mSize = mNowPlaying.length;
            if (mSize <= 0) {
                return;
            }

            StringBuilder where = new StringBuilder();
            where.append(MediaStore.Audio.Media._ID + " IN (");
            for (int i = 0; i < mSize; i++) {
                where.append(mNowPlaying[i]);
                if (i < mSize -1) {
                    where.append(",");
                }
            }
            where.append(")");

            mCurrentPlaylistCursor = Utils.query(getActivity(),
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    mCols, where.toString(), null, MediaStore.Audio.Media._ID);

            if (mCurrentPlaylistCursor == null) {
                mSize = 0;
                return;
            }

            int size = mCurrentPlaylistCursor.getCount();
            mCursorIdxs = new long[size];
            int cloumnIdx = mCurrentPlaylistCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            mCurrentPlaylistCursor.moveToFirst();
            for (int i = 0; i < size; i++) {
                mCursorIdxs[i] = mCurrentPlaylistCursor.getLong(cloumnIdx);
                mCurrentPlaylistCursor.moveToNext();
            }
            mCurrentPlaylistCursor.moveToFirst();
            mCurPos = -1;

            try {
                int removed = 0;
                for (int i = mNowPlaying.length -1; i >= 0; i--) {
                    long trackID = mNowPlaying[i];
                    int crsidx = Arrays.binarySearch(mCursorIdxs, trackID);
                    if (crsidx < 0) {
                        removed += mService.removeTrack(trackID);
                    }
                }

                if (removed > 0) {
                    mNowPlaying = mService.getQueue();
                    mSize = mNowPlaying.length;
                    if (mSize <= 0) {
                        mCursorIdxs = null;
                        return;
                    }
                }
            } catch (RemoteException e) {
                mNowPlaying = new long[0];
            }
        }

        @Override
        public boolean onMove(int oldPosition, int newPosition) {
            if (oldPosition == newPosition) {
                return true;
            }
            if (mNowPlaying == null || mCursorIdxs == null || newPosition >= mNowPlaying.length) {
                return false;
            }

            long newId = mNowPlaying[newPosition];
            int idx = Arrays.binarySearch(mCursorIdxs, newId);
            mCurrentPlaylistCursor.moveToPosition(idx);
            mCurPos = newPosition;

            return true;
        }

        public boolean removeItem(int which) {
            try {
                if (mService.removeTracks(which, which) == 0) {
                    return false;   // delete failed
                }

                int i = which;
                mSize--;
                while (i < mSize) {
                    mNowPlaying[i] = mNowPlaying[i+1];
                    i++;
                }
                onMove(-1, (int)mCurPos);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }

        public void moveItem(int from, int to) {
            try {
                mService.moveQueueItem(from, to);
                mNowPlaying = mService.getQueue();
                onMove(-1, (int)mCurPos);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getCount() {
            return mCurrentPlaylistCursor.getCount();
        }

        @Override
        public String[] getColumnNames() {
            return mCols;
        }

        @Override
        public String getString(int column) {
            return mCurrentPlaylistCursor.getString(column);
        }

        @Override
        public short getShort(int column) {
            return mCurrentPlaylistCursor.getShort(column);
        }

        @Override
        public int getInt(int column) {
            return mCurrentPlaylistCursor.getInt(column);
        }

        @Override
        public long getLong(int column) {
            return mCurrentPlaylistCursor.getLong(column);
        }

        @Override
        public float getFloat(int column) {
            return mCurrentPlaylistCursor.getFloat(column);
        }

        @Override
        public double getDouble(int column) {
            return mCurrentPlaylistCursor.getDouble(column);
        }

        @Override
        public boolean isNull(int column) {
            return mCurrentPlaylistCursor.isNull(column);
        }
    }

    private class TabSongsCursorAdapter extends CursorAdapter {

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
            vh.mDragHandle = (ImageView) rootView.findViewById(R.id.drag_handle);
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
            _vh.mDragHandle.setVisibility(View.VISIBLE);
            _vh.mArtist.setText(cursor.getString(idx_artist));
            _vh.mDuration.setText(Utils.makeTimeString(context, cursor.getLong(idx_duration)/1000));
        }

        private class ViewHolder {
            ImageView mAlbumArt;
            TextView mTitle;
            ImageView mDragHandle;
            TextView mArtist;
            ImageView mCurrentFlag;
            TextView mDuration;
        }
    }
}