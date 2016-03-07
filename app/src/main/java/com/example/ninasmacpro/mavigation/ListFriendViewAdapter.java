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
public class ListFriendViewAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;

    private List<Population> friendPopulation = null;
    private ArrayList<Population> arraylist;

    public ListFriendViewAdapter(Context context,
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

    public List<Population> getData(){
        return   friendPopulation;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteFriendConfirmDialog(view, nickname, objectId, position);
                Log.i("in friend list", "on long Click: loading... 23333 ");

                return false;

            }
        });
        return convertView;
    }



    private AlertDialog.Builder builder;
    private void deleteFriendConfirmDialog(final View view, String nickname, final String objectId, final int position){
        builder=new AlertDialog.Builder(view.getContext());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Unfriend");
        builder.setMessage("Are you sure you want to delete contact " + nickname);

        //listen to yes/no
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ParseUser user = ParseUser.getCurrentUser();
                ParseRelation<ParseObject> relation = user.getRelation("friends");
                relation.remove(ParseObject.createWithoutData("_User", objectId));
                user.saveInBackground(new SaveCallback() {
                    public void done(ParseException e){
                        showCallBackInfo(view, position);
                    }
                });
                Log.i("add friend", " successful");

            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();

    }
    private void showCallBackInfo(View view, final int position){

        AlertDialog.Builder alert;
        alert=new AlertDialog.Builder(view.getContext());
        alert.setIcon(R.mipmap.ic_launcher);
        alert.setTitle("Delete Contact");
        alert.setMessage("successfully");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("Delete Contact", " successful");
//                refreshFriendList();
                friendPopulation.remove(position);
                notifyDataSetChanged();

            }
        });

        alert.setCancelable(true);
        AlertDialog dialog=alert.create();
        dialog.show();
    }

}
