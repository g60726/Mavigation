package com.example.ninasmacpro.mavigation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengyu on 16-02-20.
 */
public class FriendListViewAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
//    ImageLoader imageLoader;
    private List<Population> friendPopulation = null;
    private ArrayList<Population> arraylist;

    public FriendListViewAdapter(Context context,
                           List<Population> friendPopulation) {
        this.context = context;
        this.friendPopulation = friendPopulation;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Population>();
        this.arraylist.addAll(friendPopulation);
//        imageLoader = new ImageLoader(context);
    }

    public class ViewHolder {
        TextView username;
        String objectId;
        TextView nickname;
//        ImageView flag;
    }

    @Override
    public int getCount() {
        return friendPopulation.size();
    }

    @Override
    public Object getItem(int position) {
        return friendPopulation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.friendlistview_item, null);

            //locate TextViews in friendlistview_item.xml
            holder.username = (TextView) convertView.findViewById(R.id.userNameTextView);
            holder.nickname = (TextView)  convertView.findViewById(R.id.nickNameTextView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Set the results into TextViews
        holder.username.setText(friendPopulation.get(position).getUsername());
        holder.nickname.setText(friendPopulation.get(position).getNickname());
        holder.objectId = friendPopulation.get(position).getObjectId();
        // Listen for ListView Item Click
        convertView.setOnClickListener(new View.OnClickListener() {
            private String TAG = "in the friend list view page";
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: loading... ");
            }
        });
        return null;
    }
}
