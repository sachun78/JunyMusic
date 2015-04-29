package com.juny.junymusic.data;

/**
 * Created by Administrator on 2015-04-29.
 */
public class FavoriteItem {
    public long     mAudioID;
    public String   mTitle;
    public String   mData;
    public String   mAlbum;
    public long     mAlbum_id;
    public String   mArtist;
    public long     mDuration;

    public FavoriteItem() {
        mAudioID = -1;
        mTitle = "";
        mData = "";
        mAlbum = "";
        mAlbum_id = -1;
        mArtist = "";
        mDuration = 0;
    }
}
