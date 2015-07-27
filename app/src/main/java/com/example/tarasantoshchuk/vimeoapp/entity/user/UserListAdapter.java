package com.example.tarasantoshchuk.vimeoapp.entity.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tarasantoshchuk.vimeoapp.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UserListAdapter extends BaseAdapter {
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
        final User currUser = getUser(position);
        UserViewHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.user_item, null);

            holder = new UserViewHolder();

            holder.imgUserItem = (ImageView) convertView.findViewById(R.id.imgUserItem);

            holder.txtUserItemName  = (TextView) convertView.findViewById(R.id.txtUserItemName);
            holder.txtUserItemLocation = (TextView) convertView
                    .findViewById(R.id.txtUserItemLocation);
            holder.txtUserItemJoined = (TextView) convertView.findViewById(R.id.txtUserItemJoined);

            convertView.setTag(holder);
        } else {
            holder = (UserViewHolder) convertView.getTag();
        }

        holder.txtUserItemName.setText(currUser.getName());
        holder.txtUserItemLocation.setText(currUser.getLocation());
        holder.txtUserItemJoined.setText(currUser.getJoinedString());

        if(currUser.isPictureLoaded()) {
            holder.imgUserItem.setImageBitmap(currUser.getPicture());
        } else {
            BitmapDownloadTask task = mHolderMap.get(holder);

            if( task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                task.cancel(false);
            }

            mHolderMap.put(holder, (BitmapDownloadTask)
                    new BitmapDownloadTask(holder, currUser).execute());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent userActivityIntent = new Intent(mActivity, UserActivity.class);

                userActivityIntent.putExtras(UserActivity.getStartExtras(currUser));

                mActivity.startActivity(userActivityIntent);
            }
        });

        return convertView;
    }

    public void cancelAllTasks() {
        for(BitmapDownloadTask task: mHolderMap.values()) {
            if(task.getStatus() != AsyncTask.Status.FINISHED) {
                task.cancel(false);
            }
        }
    }

    public void updateList(UserList list) {
        cancelAllTasks();
        mList.update(list);
        notifyDataSetChanged();
    }

    private class BitmapDownloadTask extends AsyncTask<String, Void, Bitmap> {
        private UserViewHolder mHolder;
        private User mUser;

        public BitmapDownloadTask(UserViewHolder holder, User user) {
            mHolder = holder;
            mUser = user;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            URL url = null;

            try {
                url = new URL(mUser.getPictureUrl());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            Bitmap image = null;
            try {
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return image;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mHolder.imgUserItem.setImageBitmap(bitmap);
            mUser.setPicture(bitmap);
        }
    }
}
