package com.juny.junymusic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.juny.junymusic.data.FavoriteItem;

import java.sql.SQLException;

/**
 * Created by Administrator on 2015-04-29.
 */
public class Favorite_Database {
    private static final String TAG = "FAVORITE_DATABASE";
    private Context mContext;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDB;

    public Favorite_Database(Context context) {
        mContext = context;
        mDBHelper = new DatabaseHelper(mContext);
    }

    public Favorite_Database open() throws SQLException {
        try {
            mDB = mDBHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            mDB = mDBHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        if (mDBHelper != null) {
            mDBHelper.close();
            mDBHelper = null;
        }
    }

    public Cursor selectData() {
//        Cursor mCursor = mDB.rawQuery("select * from " + DatabaseHelper.TABLE_NAME, null);
        Cursor mCursor = mDB.query(
                DatabaseHelper.TABLE_NAME,
                DatabaseHelper.mColumm,
                null, null, null, null, DatabaseHelper.KEY_INDEX + " asc");
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public long insert(FavoriteItem item) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.KEY_AUDIO_ID, item.mAudioID);
        cv.put(DatabaseHelper.KEY_TITLE, item.mTitle);
        cv.put(DatabaseHelper.KEY_DATA, item.mData);
        cv.put(DatabaseHelper.KEY_ALBUM, item.mAlbum);
        cv.put(DatabaseHelper.KEY_ALBUM_ID, item.mAlbumID);
        cv.put(DatabaseHelper.KEY_ARTIST, item.mArtist);
        cv.put(DatabaseHelper.KEY_DURATION, item.mDuration);

        long retVal = mDB.insert(DatabaseHelper.TABLE_NAME, null, cv);
        Cursor c = mDB.rawQuery("select * from " + DatabaseHelper.TABLE_NAME, null);

        Toast.makeText(mContext, "currentDB count: " + c.getCount(), Toast.LENGTH_SHORT).show();
        if (c.getCount() > DatabaseHelper.FAVORITE_MAX) {
            mDB.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.KEY_INDEX + "=" + retVal, null);
            Toast.makeText(mContext, "Over Favorite Count 50!!!", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return retVal;
    }

    public boolean delete(long index) {
        return mDB.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.KEY_AUDIO_ID + "=" + index, null) > 0;
    }

    public boolean delete(String[] index) {
        String whereCauseIn = "?";

        for (int i = 0; i < index.length; i++) {
            if (i != index.length) {
                whereCauseIn += ", ?";
            }
        }
        return mDB.delete(DatabaseHelper.TABLE_NAME, "IN(" + whereCauseIn +")", null) > 0;
    }

    public void deleteAll() {
        mDB.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_NAME);
        mDB.execSQL(DatabaseHelper.CREATE_TABLE);
    }

    public boolean isDuplicate(long index) {
        Cursor c = mDB.rawQuery("select * from " + DatabaseHelper.TABLE_NAME + " where audio_id=" + index, null);
        return c.getCount() > 0;
    }
}
