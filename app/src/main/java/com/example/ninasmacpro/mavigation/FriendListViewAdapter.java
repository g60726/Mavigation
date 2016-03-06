package com.example.ninasmacpro.mavigation;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengyu on 16-02-20.
 */
public class FriendListViewAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;

    private List<Population> friendPopulation = null;
    private ArrayList<Population> arraylist;

    public FriendListViewAdapter(Context context,
                           List<Population> friendPopulation) {
        this.context = context;
        this.friendPopulation = friendPopulation;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<Population>();
        this.arraylist.addAll(friendPopulation);
    }

    public class ViewHolder {
        TextView username;
        String objectId;
        TextView nickname;
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
        final String nickname = friendPopulation.get(position).getNickname();
        final String objectId = friendPopulation.get(position).getObjectId();
        holder.username.setText(friendPopulation.get(position).getUsername());
        holder.nickname.setText(nickname);
        holder.objectId = friendPopulation.get(position).getObjectId();
        // Listen for ListView Item Click
        convertView.setOnClickListener(new View.OnClickListener() {
            private String TAG = "in the friend list view page";
            @Override
            public void onClick(View v) {
                showFriendConfirmDialog(v, nickname, objectId);
                Log.i(TAG, "onClick: loading... ");
            }
        });
        return convertView;
    }

    private AlertDialog.Builder builder;
    private void showFriendConfirmDialog(final View view, String nickname, final String objectId){
        builder=new AlertDialog.Builder(view.getContext());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Follow");
        builder.setMessage("Are you sure you want to add " + nickname);

        //listen to yes/no
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getApplicationContext(), R.string.toast_negative, Toast.LENGTH_SHORT).show();

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getApplicationContext(),R.string.toast_postive, Toast.LENGTH_SHORT).show();
                ParseUser user = ParseUser.getCurrentUser();
                ParseRelation<ParseObject> relation = user.getRelation("friends");
//                ParseUser friend = new ParseUser();
                relation.add(ParseObject.createWithoutData("_User", objectId));
                user.saveInBackground(new SaveCallback() {
                    public void done(ParseException e){
                        showCallBackInfo(view);
                    }
                });
                Log.i("add friend", " successful");

            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    private void showCallBackInfo(View view){
//        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
//        alertDialog.setTitle("Alert Dialog");
//        alertDialog.setMessage("Welcome to dear user.");
//        alertDialog.setIcon(R.mipmap.ic_launcher);
//
//        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
//            }
//        });

//        alertDialog.show();
        AlertDialog.Builder alert;
        alert=new AlertDialog.Builder(view.getContext());
        alert.setIcon(R.mipmap.ic_launcher);
        alert.setTitle("Add friend");
        alert.setMessage("successfully");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getApplicationContext(),R.string.toast_postive, Toast.LENGTH_SHORT).show();
                Log.i("add friend", " successful");

            }
        });

        alert.setCancelable(true);
        AlertDialog dialog=alert.create();
        dialog.show();
    }

}


//***************
//public class FriendListViewAdapter extends ArrayAdapter<Population> {
//    private ArrayList<Population> friendList;
//
//    public FriendListViewAdapter(Context context, int textViewResourceId,
//                           ArrayList<Population> friendList) {
//        super(context, textViewResourceId, friendList);
//        this.friendList = friendList;
//    }
//
//
//    private class ViewHolder {
//        TextView username;
//        String objectId;
//        TextView nickname;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        ViewHolder holder = null;
//
//        if (convertView == null) {
//            LayoutInflater vi = (LayoutInflater)getSystemService(
//                    Context.LAYOUT_INFLATER_SERVICE);
//            convertView = vi.inflate(R.layout.friendlistview_item, null);
//
//            holder = new ViewHolder();
//            holder.username = (TextView) convertView.findViewById(R.id.userNameTextView);
//            holder.nickname = (CheckBox) convertView.findViewById(R.id.nickNameTextView);
//            convertView.setTag(holder);
//
////            holder.name.setOnClickListener( new View.OnClickListener() {
////                public void onClick(View v) {
////                    CheckBox cb = (CheckBox) v;
////                    Friend friend = (Friend) cb.getTag();
////                    friend.setSelected(cb.isChecked());
////                }
////            });
//        }
//        else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        Friend friend = friendList.get(position);
//        holder.code.setText(" (" +  friend.getCode() + ")");
//        holder.name.setText(friend.getName());
//        holder.name.setChecked(friend.isSelected());
//        holder.name.setTag(friend);
//
//        return convertView;
//
//    }
//
//}