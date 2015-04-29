package com.juny.junymusic.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015-04-24.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "juny_database.db";
    public static final String TABLE_NAME = "juny_favorite";
    public static final int DATABASE_VERSION = 1;
    public static final int FAVORITE_MAX = 50;

    public static final String KEY_INDEX = "_id";
    public static final String KEY_AUDIO_ID = "audio_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATA = "_data";
    public static final String KEY_ALBUM = "album";
    public static final String KEY_ALBUM_ID = "album_id";
    public static final String KEY_ARTIST = "artist";
    public static final String KEY_DURATION = "duration";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
            + KEY_INDEX + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_AUDIO_ID + " INTEGER NOT NULL, "
            + KEY_TITLE + " TEXT, "
            + KEY_DATA + " TEXT NOT NULL, "
            + KEY_ALBUM + " TEXT, "
            + KEY_ALBUM_ID + " INTEGER, "
            + KEY_ARTIST + " TEXT, "
            + KEY_DURATION + " INTEGER "
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
