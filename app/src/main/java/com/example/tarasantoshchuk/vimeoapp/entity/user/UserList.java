package com.example.tarasantoshchuk.vimeoapp.entity.user;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class UserList implements Parcelable {
    private static final String TAG = UserList.class.getSimpleName();

    private ArrayList<User> mList;

    public UserList() {
        mList = new ArrayList<User>();
    }

    public UserList(ArrayList<User> list) {
        mList = list;
    }

    private UserList(Parcel source) {
        mList = new ArrayList<User>();

        int length = source.readInt();

        for(int i = 0; i < length; i++) {
            mList.add((User) source.readParcelable(User.class.getClassLoader()));
        }
    }

    public User get(int position) {
        return mList.get(position);
    }

    public int size() {
        return mList.size();
    }

    public void update(UserList list) {
        Log.d(TAG, "update");
        mList.clear();
        mList.addAll(list.mList);
    }

    public static final Parcelable.Creator<UserList> CREATOR =
            new Parcelable.Creator<UserList>() {

                @Override
                public UserList createFromParcel(Parcel source) {
                    Log.d(TAG, "CREATOR.createFromParcel");
                    return new UserList(source);
                }

                @Override
                public UserList[] newArray(int size) {
                    return new UserList[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d(TAG, "writeToParcel");

        dest.writeInt(mList.size());

        for(User user: mList) {
            dest.writeParcelable(user, 0);
        }
    }

}
