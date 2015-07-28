package com.example.tarasantoshchuk.vimeoapp.entity.group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.example.tarasantoshchuk.vimeoapp.R;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupListAdapter extends BaseAdapter {
    private ArrayList<Group> mList;
    private LayoutInflater mInflater;
    private GroupListActivity mActivity;

    private HashMap<GroupViewHolder, BitmapDownloadTask> mHolderMap =
            new HashMap<GroupViewHolder, BitmapDownloadTask>();

    public GroupListAdapter(LayoutInflater inflater, GroupListActivity activity) {
        mList = new ArrayList<Group>();
        mInflater = inflater;
        mActivity = activity;
    }

    private Group getGroup(int position) {
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
        final Group currGroup = getGroup(position);
        GroupViewHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.group_item, null);

            holder = new GroupViewHolder();

            holder.imgGroupItem = (ImageView) convertView.findViewById(R.id.imgGroupItem);

            holder.txtGroupItemName = (TextView) convertView.findViewById(R.id.txtGroupItemName);
            holder.txtGroupItemOwner = (TextView) convertView.findViewById(R.id.txtGroupItemOwner);

            holder.txtGroupItemUserCount = (TextView)
                    convertView.findViewById(R.id.txtGroupItemUserCount);

            holder.txtGroupItemVideoCount = (TextView)
                    convertView.findViewById(R.id.txtGroupItemVideoCount);

            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        holder.txtGroupItemName.setText(currGroup.getName());
        holder.txtGroupItemOwner.setText(currGroup.getOwner().getName());

        holder.txtGroupItemUserCount.setText(Integer.toString(currGroup.getUsersCount()));
        holder.txtGroupItemVideoCount.setText(Integer.toString(currGroup.getVideosCount()));

        if(currGroup.isPictureLoaded()) {
            holder.imgGroupItem.setImageBitmap(currGroup.getPicture());
        } else {
            BitmapDownloadTask task = mHolderMap.get(holder);

            if(task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                task.cancel(false);
            }

            mHolderMap.put(holder, (BitmapDownloadTask)
                    new BitmapDownloadTask(holder, currGroup, mActivity).execute());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupActivityIntent = new Intent(mActivity, GroupActivity.class);

                groupActivityIntent.putExtras(GroupActivity.getStartExtras(currGroup));

                mActivity.startActivity(groupActivityIntent);
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

    public void updateList(ArrayList<Group> list) {
        cancelAllTasks();

        mList.clear();
        mList.addAll(list);

        notifyDataSetChanged();
    }


    private static class BitmapDownloadTask extends AsyncTask<String, Void, Bitmap> {
        private GroupViewHolder mHolder;
        private Group mGroup;
        private Context mContext;

        public BitmapDownloadTask(GroupViewHolder holder, Group group, Context context) {
            mHolder = holder;
            mGroup = group;
            mContext = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {

                URL url = new URL(mGroup.getPictureUrl());
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap == null) {
                Toast.makeText(mContext, mContext.getString(R.string.txt_bitmap_load_fail),
                        Toast.LENGTH_LONG).show();
            } else {

                mHolder.imgGroupItem.setImageBitmap(bitmap);
                mGroup.setPicture(bitmap);

            }
        }
    }
}
