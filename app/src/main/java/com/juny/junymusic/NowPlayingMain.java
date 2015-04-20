package com.juny.junymusic;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;

/**
 * Created by Administrator on 2015-04-19.
 */
public class NowPlayingMain extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String mTag = "nowPlaying";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nowplaying_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.now_play_main, DSLVFragment.newInstance(0,0), mTag).commit();
        }

        this.getSupportLoaderManager().initLoader(0, null, this);
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

        return new CursorLoader(this, uri, arrayOfString01, "is_music = ?", arrayOfString02, "title COLLATE LOCALIZED ASC");
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }
}
