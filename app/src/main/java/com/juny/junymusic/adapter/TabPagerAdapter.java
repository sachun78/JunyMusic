package com.juny.junymusic.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.juny.junymusic.data.ConstantData;
import com.juny.junymusic.mysongs.TabAlbumFragment;
import com.juny.junymusic.mysongs.TabArtistFragment;
import com.juny.junymusic.mysongs.TabFolderFragment;
import com.juny.junymusic.mysongs.TabSongFragment;

/**
 * Created by Administrator on 2015-03-25.
 */
public class TabPagerAdapter extends FragmentPagerAdapter{
    private final String[] TITLES = { "All Songs", "Albums", "Artists", "Folder" };

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ConstantData.TAB_ITEM_ALL:
                return TabSongFragment.newInstance();
            case ConstantData.TAB_ITEM_ALBUM:
                return TabAlbumFragment.newInstance();
            case ConstantData.TAB_ITEM_ARTIST:
                return TabArtistFragment.newInstance();
            case ConstantData.TAB_ITEM_FOLDER:
                return TabFolderFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}
