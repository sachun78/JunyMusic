package com.juny.junymusic;

/**
 * Created by Administrator on 2015-03-23.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

public class CursorDSLV extends ActionBarActivity {

    private SimpleDragSortCursorAdapter adapter;
    DragSortListView dslv;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cursor_main);

        String[] cols = {"name"};
        int[] ids = {R.id.text};
        adapter = new MAdapter(this,
                R.layout.list_item_click_remove, null, cols, ids, 0);

        dslv = (DragSortListView) findViewById(android.R.id.list);
        dslv.setAdapter(adapter);

        // build a cursor from the String array
        MatrixCursor cursor = new MatrixCursor(new String[] {"_id", "name"});
        String[] artistNames = getResources().getStringArray(R.array.jazz_artist_names);
        for (int i = 0; i < artistNames.length; i++) {
            cursor.newRow()
                    .add(i)
                    .add(artistNames[i]);
        }
        adapter.changeCursor(cursor);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        Cursor tempCursor = adapter.getCursor();
//        int i = 0;
//        if (tempCursor != null && tempCursor.moveToFirst()) {
//            do {
//                Log.d("hjbae", "cursor idx[" + i + "] " + "data [" + tempCursor.getString(tempCursor.getColumnIndex("name")) + "]");
//                i++;
//            } while (tempCursor.moveToNext());
//        }
    }

    private class MAdapter extends SimpleDragSortCursorAdapter {
        private Context mContext;

        public MAdapter(Context ctxt, int rmid, Cursor c, String[] cols, int[] ids, int something) {
            super(ctxt, rmid, c, cols, ids, something);
            mContext = ctxt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            View tv = v.findViewById(R.id.text);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "text clicked (" + ((TextView)v).getText().toString()+")", Toast.LENGTH_SHORT).show();
                }
            });
            return v;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Cursor getCursor() {
            return super.getCursor();
        }
    }
}