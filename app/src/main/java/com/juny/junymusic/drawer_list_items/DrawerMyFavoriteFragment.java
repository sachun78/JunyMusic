package com.juny.junymusic.drawer_list_items;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.juny.junymusic.R;
import com.juny.junymusic.db.Favorite_Database;
import com.juny.junymusic.util.Utils;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.sql.SQLException;

/**
 * Created by Administrator on 2015-04-03.
 */
public class DrawerMyFavoriteFragment extends ListFragment {

    private DragSortListView mDslv;
    private DragSortController mController;
    private MyFavoriteCursorAdapter mAdapter;

    private Favorite_Database fdb;

    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = true;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;

    public static DrawerMyFavoriteFragment newInstance() {
        DrawerMyFavoriteFragment fragment = new DrawerMyFavoriteFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public DrawerMyFavoriteFragment() {}
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fdb = new Favorite_Database(getActivity());
        try {
            fdb.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("hjbae", "cursor count: " + mAdapter.getCursor().getCount());

        Cursor cursor = fdb.selectData();
        mAdapter.changeCursor(cursor);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDslv = (DragSortListView)inflater.inflate(R.layout.dslv_fragment_main, container, false);

        mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(dragEnabled);

        return mDslv;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDslv = (DragSortListView) getListView();
        mDslv.setDropListener(onDrop);
        mDslv.setRemoveListener(onRemove);

        setListAdapter();
    }

    private void setListAdapter() {
        mAdapter = new MyFavoriteCursorAdapter(getActivity(), fdb.selectData(), 0);
        mDslv.setAdapter(mAdapter);
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {

        @Override
        public void drop(int from, int to) {

        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {

        @Override
        public void remove(int which) {
            Cursor cursor = (Cursor)mAdapter.getItem(which);
            int idx_audioid = cursor.getColumnIndex("audio_id");
            int idx_title = cursor.getColumnIndex("title");

            long audioID = cursor.getLong(idx_audioid);
            String titleText = cursor.getString(idx_title);
            Log.d("hjbae", "title: " + titleText);
            Log.d("hjbae", "iaudioid: " + audioID);

            if (fdb.delete(audioID)) {
                Toast.makeText(getActivity(), "Removed - " + titleText, Toast.LENGTH_SHORT).show();
            }

            mAdapter.changeCursor(fdb.selectData());
        }
    };

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor cursor = this.mAdapter.getCursor();

        if (cursor.getCount() == 0)
            return;

        Utils.playAll(getActivity(), cursor, position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if (fdb != null) {
            fdb.close();
            fdb = null;
        }
        super.onDestroy();
    }

    /**
     * Called in onCreateView. Override this to provide a custom
     * DragSortController.
     */
    public DragSortController buildController(DragSortListView dslv) {
        // defaults are
        //   dragStartMode = onDown
        //   removeMode = flingRight
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        return controller;
    }

    private class MyFavoriteCursorAdapter extends CursorAdapter {

        public MyFavoriteCursorAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        public MyFavoriteCursorAdapter(Context context, Cursor c, int flags) {
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
//            if (sService != null) {
//                try {
//                    if (songid == sService.getAudioId()) {
//                        mAnimDrawable = (AnimationDrawable)_vh.mCurrentFlag.getBackground();
//                        if (sService.isPlaying()) {
//                            _vh.mCurrentFlag.setVisibility(View.VISIBLE);
//                            mAnimDrawable.start();
//                        }
//                        else {
//                            _vh.mCurrentFlag.setVisibility(View.GONE);
//                            mAnimDrawable.stop();
//                        }
//                    }
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }

            _vh.mTitle.setText(cursor.getString(idx_title));
            _vh.mDragHandle.setVisibility(View.VISIBLE);
            _vh.mArtist.setText(cursor.getString(idx_artist));
            _vh.mDuration.setText(Utils.makeTimeString(context, cursor.getLong(idx_duration)/1000));
        }

        @Override
        public Object getItem(int position) {
            Cursor cursor = null;
            if (getCursor().moveToPosition(position)) {
                cursor = getCursor();
            }
            return cursor;
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
