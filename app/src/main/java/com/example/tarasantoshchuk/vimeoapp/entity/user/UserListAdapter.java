package com.example.tarasantoshchuk.vimeoapp.entity.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tarasantoshchuk.vimeoapp.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UserListAdapter extends BaseAdapter {
    private static final String TAG = UserListAdapter.class.getSimpleName();

    private UserList mList;
    private LayoutInflater mInflater;
    private UserListActivity mActivity;

    private HashMap<UserViewHolder, BitmapDownloadTask> mHolderMap =
            new HashMap<UserViewHolder, BitmapDownloadTask>();

    public UserListAdapter(LayoutInflater inflater, UserListActivity activity) {
        mList = new UserList();
        mInflater = inflater;
        mActivity = activity;
    }

    private User getUser(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView");

        final User currUser = getUser(position);
        UserViewHolder holder;

        if(convertView == null) {
            Log.d(TAG, "getView: inflate view");
            convertView = mInflater.inflate(R.layout.user_item, null);

            holder = new UserViewHolder();

            holder.imgUserItem = (ImageView) convertView.findViewById(R.id.imgUserItem);

            holder.txtUserItemName  = (TextView) convertView.findViewById(R.id.txtUserItemName);
            holder.txtUserItemLocation = (TextView) convertView
                    .findViewById(R.id.txtUserItemLocation);
            holder.txtUserItemJoined = (TextView) convertView.findViewById(R.id.txtUserItemJoined);

            convertView.setTag(holder);
        } else {
            Log.d(TAG, "getView: reuse view");
            holder = (UserViewHolder) convertView.getTag();
        }

        holder.txtUserItemName.setText(currUser.getName());
        holder.txtUserItemLocation.setText(currUser.getLocation());
        holder.txtUserItemJoined.setText(currUser.getJoinedString());

        if(currUser.isPictureLoaded()) {
            holder.imgUserItem.setImageBitmap(currUser.getPicture());
        } else {
            BitmapDownloadTask task = mHolderMap.get(holder);

            if(task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                Log.d(TAG, "cancel async task");
                task.cancel(false);
            }

            Log.d(TAG, "create async task");
            mHolderMap.put(holder, (BitmapDownloadTask)
                    new BitmapDownloadTask(holder, currUser, mActivity).execute());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userActivityIntent = new Intent(mActivity, UserActivity.class);

                userActivityIntent.putExtras(UserActivity.getStartExtras(currUser));

                Log.d(TAG, "convertView onClick: start UserActivity");
                mActivity.startActivity(userActivityIntent);
            }
        });

        return convertView;
    }

    public void cancelAllTasks() {
        Log.d(TAG, "cancelAllTasks");
        for(BitmapDownloadTask task: mHolderMap.values()) {
            if(task.getStatus() != AsyncTask.Status.FINISHED) {
                task.cancel(false);
            }
        }
    }

    public void updateList(UserList list) {
        Log.d(TAG, "updateList");

        cancelAllTasks();
        mList.update(list);
        notifyDataSetChanged();
    }

    private static class BitmapDownloadTask extends AsyncTask<Void, Void, Bitmap> {
        private UserViewHolder mHolder;
        private User mUser;
        private Context mContext;

        public BitmapDownloadTask(UserViewHolder holder, User user, Context context) {
            mHolder = holder;
            mUser = user;
            mContext = context;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {

                URL url = new URL(mUser.getPictureUrl());
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "BitmapDownloadTask.opPostExecute");
            if (bitmap == null) {

                Toast.makeText(mContext, mContext.getString(R.string.txt_bitmap_load_fail),
                        Toast.LENGTH_LONG).show();

            } else {
                mHolder.imgUserItem.setImageBitmap(bitmap);
                mUser.setPicture(bitmap);
            }
        }
    }
}
