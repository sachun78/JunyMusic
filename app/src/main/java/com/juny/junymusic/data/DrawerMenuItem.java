package com.juny.junymusic.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2015-04-02.
 */
public class DrawerMenuItem implements Parcelable{

    public int mIndex;
    public int mType;
    public int mResId;
    public String mName;

    public static final Parcelable.Creator<DrawerMenuItem> CREATOR = new Parcelable.Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new DrawerMenuItem(source);
        }

        @Override
        public DrawerMenuItem[] newArray(int size) {
            return new DrawerMenuItem[size];
        }
    };

    public DrawerMenuItem (int idx, int type, int resid, String name) {
        this.mIndex = idx;
        this.mType = type;
        this.mResId = resid;
        this.mName = name;
    }

    public DrawerMenuItem (Parcel paramParcel) {
        readToParcel(paramParcel);
    }

    public int getmIndex() {
        return this.mIndex;
    }

    public int getmType() {
        return this.mType;
    }

    public int getmResId() {
        return this.mResId;
    }

    public String getmName() {
        return this.mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readToParcel(Parcel src) {
        this.mIndex = src.readInt();
        this.mType = src.readInt();
        this.mResId = src.readInt();
        this.mName = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mIndex);
        dest.writeInt(this.mType);
        dest.writeInt(this.mResId);
        dest.writeString(this.mName);
    }
}
