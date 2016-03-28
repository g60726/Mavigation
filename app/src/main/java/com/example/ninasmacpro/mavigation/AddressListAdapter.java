package com.example.ninasmacpro.mavigation;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by eric on 3/25/2016.
 */
public class AddressListAdapter extends ArrayAdapter<Address>{
    private final Context contex;
    private final List<Address> values;
    public AddressListAdapter(Context context, List<Address> values){
        super(context,-1,values);
        this.contex = context;
        this.values = values;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.address_list_layout,parent,false);
        TextView addressText = (TextView) rowView.findViewById(R.id.addressText);
        String result = "";
        for(int i = 0; i < values.get(position).getMaxAddressLineIndex(); i++){
            result+=values.get(position).getAddressLine(i);
            result+=" ";
        }
        addressText.setText(result);
        return rowView;
    }
}
