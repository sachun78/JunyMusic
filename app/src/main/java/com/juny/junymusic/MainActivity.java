package com.juny.junymusic;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.juny.junymusic.data.ConstantData;
import com.juny.junymusic.data.DrawerMenuItem;
import com.juny.junymusic.drawer_list_items.DrawerMyFavoriteFragment;
import com.juny.junymusic.drawer_list_items.DrawerMyListFragment;
import com.juny.junymusic.drawer_list_items.DrawerMySongsFragment;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private FrameLayout mLeftDrawer;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    // "뒤로"버튼 한번 더 누르시면 종료됩니다.
    private boolean checkBack = false;

    private TestListener mCallback;
    public static abstract interface TestListener {
        public abstract void onMetaChanged();
    }

    public void addTestListener(TestListener mTestListener) {
        mCallback = mTestListener;
    }

    private static DrawerMenuItem[] mMenuItems;
    static {
        DrawerMenuItem[] arrayOfDrawerMenuItem = new DrawerMenuItem[4];
        arrayOfDrawerMenuItem[0] = new DrawerMenuItem(-1, ConstantData.DRAWER_TYPE_HEADER, 0, "My Music");
        arrayOfDrawerMenuItem[1] = new DrawerMenuItem(1, ConstantData.DRAWER_TYPE_ITEMS, R.drawable.btn_songs_off_default, "My Songs");
        arrayOfDrawerMenuItem[2] = new DrawerMenuItem(2, ConstantData.DRAWER_TYPE_ITEMS, R.drawable.btn_mylist_off_default, "My Lists");
        arrayOfDrawerMenuItem[3] = new DrawerMenuItem(3, ConstantData.DRAWER_TYPE_ITEMS, R.drawable.btn_like_off_default, "My Favorites");
        mMenuItems = arrayOfDrawerMenuItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mLeftDrawer = (FrameLayout) findViewById(R.id.navigation_drawer);

        mNavigationDrawerFragment = new NavigationDrawerFragment();
        Bundle args = new Bundle();
        args.putParcelableArray("left_drawer_list", mMenuItems);
        mNavigationDrawerFragment.setArguments(args);

        mTitle = getTitle();

        // 액션바 바탕색을 바꿔준다.
        Drawable colorDrawable = new ColorDrawable(ConstantData.ACTIONBAR_AND_TABSELECT_COLOR);
        Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
        LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });
        getSupportActionBar().setBackgroundDrawable(ld);

        getSupportFragmentManager().beginTransaction().replace(R.id.navigation_drawer, mNavigationDrawerFragment).commit();

        // Set up the drawer.
//        if (mNavigationDrawerFragment != null) {
//            mNavigationDrawerFragment.setUp(
//                    R.id.navigation_drawer,
//                    (DrawerLayout) findViewById(R.id.drawer_layout));
//        }
    }

    @Override
    public void onBackPressed() {

        if (this.checkBack == true) {
            finish();
            return;
        }

        this.checkBack = true;
        Toast.makeText(this, "\"뒤로\"버튼 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.checkBack = false;
            }
        }, 2000L);
    }

    public void leftDrawer_setUp() {
        if (mNavigationDrawerFragment != null) {
            mNavigationDrawerFragment.setUp(
                    R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            // 노래탭 화면 (앨범, 가수, 폴더, 전체)
            case ConstantData.DRAWER_ITEM_SECTION_MEDIASTORE:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, DrawerMySongsFragment.newInstance(1))
                        .commit();
                break;
            // 내 목록
            case ConstantData.DRAWER_ITEM_SECTION_MYLIST:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, DrawerMyListFragment.newInstance())
                        .commit();
                // Temp Check
                mCallback.onMetaChanged();
                break;
            // 즐겨찾기
            case ConstantData.DRAWER_ITEM_SECTION_FAVORITE:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, DrawerMyFavoriteFragment.newInstance())
                        .commit();
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!mNavigationDrawerFragment.isDrawerOpen()) {
                mNavigationDrawerFragment.DrawerOpen();
            }
            else {
                mNavigationDrawerFragment.DrawerClose();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addmylist) {
//            Intent intent = new Intent(this, NowPlayingMain.class);
//            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_search) {
            Toast.makeText(this, "TEST Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        checkBack = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        checkBack = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        checkBack = false;
        super.onDestroy();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
