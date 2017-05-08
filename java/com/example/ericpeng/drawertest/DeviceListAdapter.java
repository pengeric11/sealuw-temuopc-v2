package com.example.ericpeng.drawertest;

import android.widget.ArrayAdapter;
import java.util.List;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.lang.reflect.Array;

/**
 * Created by ericpeng on 5/3/17.
 */

public class DeviceListAdapter extends ArrayAdapter<DeviceItem>{

    private Context context;
    private BluetoothAdapter bTAdapter;

    public DeviceListAdapter(Context context, List items, BluetoothAdapter btAdapter){
        super(context, android.R.layout.simple_expandable_list_item_1, items);
        this.bTAdapter = btAdapter;
        this.context = context;
    }

    private class ViewHolder{
        TextView titleText;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View line = null;
        DeviceItem item = (DeviceItem)getItem(position);
        final String name = item.getDeviceName();
        TextView macAddress = null;
        View viewToUse = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        viewToUse = mInflater.inflate(R.layout.device_list_item, null);
        holder = new ViewHolder();
        holder.titleText = (TextView)viewToUse.findViewById(R.id.titleTextView);
        viewToUse.setTag(holder);

        macAddress = (TextView)viewToUse.findViewById(R.id.macAddress);
        line = (View)viewToUse.findViewById(R.id.line);
        holder.titleText.setText(item.getDeviceName());
        macAddress.setText(item.getAddress());

        if ( item.getDeviceName().toString() == "No Devices") {
            macAddress.setVisibility(View.INVISIBLE);
            line.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams
                    ((int) RelativeLayout.LayoutParams.WRAP_CONTENT, (int) RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            holder.titleText.setLayoutParams(params);
        }

        return viewToUse;
    }
}
