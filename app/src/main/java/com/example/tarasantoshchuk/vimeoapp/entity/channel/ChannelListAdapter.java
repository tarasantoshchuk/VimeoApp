package com.example.tarasantoshchuk.vimeoapp.entity.channel;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class ChannelListAdapter extends BaseAdapter {
    private static final String TAG = ChannelListAdapter.class.getSimpleName();

    private ArrayList<Channel> mList;
    private LayoutInflater mInflater;
    private ChannelListActivity mActivity;

    private HashMap<ChannelViewHolder, BitmapDownloadTask> mHolderMap =
            new HashMap<ChannelViewHolder, BitmapDownloadTask>();

    public ChannelListAdapter(LayoutInflater inflater, ChannelListActivity activity) {
        mList = new ArrayList<Channel>();
        mInflater = inflater;
        mActivity = activity;
    }

    private Channel getChannel(int position) {
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
        final Channel currChannel = getChannel(position);
        ChannelViewHolder holder;

        if(convertView == null) {
            Log.d(TAG, "getView: inflate view");
            convertView = mInflater.inflate(R.layout.channel_item, null);

            holder = new ChannelViewHolder();

            holder.imgChannelItem = (ImageView) convertView.findViewById(R.id.imgChannelItem);

            holder.txtChannelItemName = (TextView)
                    convertView.findViewById(R.id.txtChannelItemName);
            holder.txtChannelItemOwner = (TextView)
                    convertView.findViewById(R.id.txtChannelItemOwner);

            holder.txtChannelItemUserCount = (TextView)
                    convertView.findViewById(R.id.txtChannelItemUsersCount);

            holder.txtChannelItemVideoCount = (TextView)
                    convertView.findViewById(R.id.txtChannelItemVideoCount);

            convertView.setTag(holder);
        } else {
            Log.d(TAG, "getView: reuse view");
            holder = (ChannelViewHolder) convertView.getTag();
        }

        holder.txtChannelItemName.setText(currChannel.getName());
        holder.txtChannelItemOwner.setText(currChannel.getOwner().getName());

        holder.txtChannelItemUserCount.setText(Integer.toString(currChannel.getUsersCount()));
        holder.txtChannelItemVideoCount.setText(Integer.toString(currChannel.getVideosCount()));

        if(currChannel.isPictureLoaded()) {
            holder.imgChannelItem.setImageBitmap(currChannel.getPicture());
        } else {
            BitmapDownloadTask task = mHolderMap.get(holder);

            if(task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
                Log.d(TAG, "getView: cancel async task");
                task.cancel(false);
            }

            Log.d(TAG, "getView: start new async task");
            mHolderMap.put(holder, (BitmapDownloadTask)
                new BitmapDownloadTask(holder, currChannel, mActivity).execute());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent channelActivityIntent = new Intent(mActivity, ChannelActivity.class);

                channelActivityIntent.putExtras(ChannelActivity.getStartExtras(currChannel));

                mActivity.startActivity(channelActivityIntent);
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

    public void updateList(ArrayList<Channel> list) {
        Log.d(TAG, "updateList");
        cancelAllTasks();

        mList.clear();
        mList.addAll(list);

        notifyDataSetChanged();
    }


    private static class BitmapDownloadTask extends AsyncTask<Void, Void, Bitmap> {
        private ChannelViewHolder mHolder;
        private Channel mChannel;
        private Context mContext;

        public BitmapDownloadTask(ChannelViewHolder holder, Channel channel, Context context) {
            mHolder = holder;
            mChannel = channel;
            mContext = context;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {

                URL url = new URL(mChannel.getPictureUrl());
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "BitmapDownloadTask.onPostExecute");
            if(bitmap == null) {
                Toast.makeText(mContext, mContext.getString(R.string.txt_bitmap_load_fail),
                        Toast.LENGTH_LONG).show();
            } else {

                mHolder.imgChannelItem.setImageBitmap(bitmap);
                mChannel.setPicture(bitmap);

            }
        }
    }
}
