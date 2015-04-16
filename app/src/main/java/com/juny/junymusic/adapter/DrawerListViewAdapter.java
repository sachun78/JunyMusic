package com.juny.junymusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juny.junymusic.R;
import com.juny.junymusic.data.DrawerMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015-04-01.
 */
public class DrawerListViewAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    private List<DrawerMenuItem> mMenuItems = new ArrayList();

    public DrawerListViewAdapter (Context context, List<DrawerMenuItem> menuItems) {
        mInflater = LayoutInflater.from(context);
        mMenuItems = menuItems;
    }

    @Override
    public int getCount() {
        return mMenuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            View rootView = mInflater.inflate(R.layout.drawerlistview_item, null);
            vh.mDrawerHeader = (TextView) rootView.findViewById(R.id.drawerlist_header);
            vh.mDrawerLayout = (RelativeLayout) rootView.findViewById(R.id.drawerlist_layout);
            vh.mDrawerImg = (ImageView) rootView.findViewById(R.id.drawerlist_img);
            vh.mDrawerTxt = (TextView) rootView.findViewById(R.id.drawerlist_txt);
            convertView = rootView;
            convertView.setTag(vh);
        }
        else {
            vh = (ViewHolder) convertView.getTag();
        }

        DrawerMenuItem tmpItem = mMenuItems.get(position);
        if (tmpItem.getmType() == 0) {
            vh.mDrawerHeader.setVisibility(View.VISIBLE);
            vh.mDrawerLayout.setVisibility(View.GONE);

            vh.mDrawerHeader.setText(tmpItem.getmName());
            vh.mDrawerHeader.setOnClickListener(null);
        }
        else {
            vh.mDrawerHeader.setVisibility(View.GONE);
            vh.mDrawerLayout.setVisibility(View.VISIBLE);

            vh.mDrawerImg.setImageResource(tmpItem.getmResId());
            vh.mDrawerTxt.setText(tmpItem.getmName());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView mDrawerHeader;

        RelativeLayout mDrawerLayout;
        ImageView mDrawerImg;
        TextView mDrawerTxt;

        public ViewHolder() {};
    }
}
