package com.impactapp.vishnu.timesync;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vishnu on 7/28/2017.
 */

public class CustomListAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int id;
    private ArrayList<String> Switches = new ArrayList<String>();
    private ArrayList<String> TimeTexts = new ArrayList<String>();
    private ArrayList<Boolean> State = new ArrayList<Boolean>();
    private ArrayList<Boolean> Enable = new ArrayList<Boolean>();



    public CustomListAdapter(Context context, int resource, int textViewResourceId, List<Main2Activity.myNode> OBJECTS) {
        super(context,resource,textViewResourceId,(List) OBJECTS);
        for(int i=0;i<OBJECTS.size();i++) {
            //Switches.add((String) OBJECTS.get(i).TimeSwitch.getText());
            //TimeTexts.add((String) OBJECTS.get(i).TimeTXT.getText());
            //mContext = context;
            Switches.add(OBJECTS.get(i).SwitchString);
            TimeTexts.add(OBJECTS.get(i).TextString);
            State.add(OBJECTS.get(i).state);
            Enable.add(OBJECTS.get(i).enable);
        }
        id = resource;
        mContext = context;
    }

    /*
    public CustomListAdapter(Context context,int resource,int textViewResourceId,List<String> objects,List<String> TId) {
        super(context,resource,textViewResourceId,objects);
        mContext = context;
        Switches = objects;
        TimeTexts = TId;
        id = resource;
    }*/


    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View mView = v;
        ActivityHolder holder = null;

        if (mView == null) {
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id,null,true);

            holder = new ActivityHolder();
            holder.TimeTxTBox = (TextView) mView.findViewById(R.id.TextViewTime);
            holder.TimeSwitch = (Switch) mView.findViewById(R.id.SwitchTime);



            mView.setTag(holder);
        } else {
            holder = (ActivityHolder)mView.getTag();
            holder.TimeSwitch.setOnCheckedChangeListener(null);
        }

        holder.TimeSwitch.setText(Switches.get(position));
        holder.TimeSwitch.setClickable(false);
        holder.TimeSwitch.setFocusable(false);
        holder.TimeSwitch.setFocusableInTouchMode(false);

        holder.TimeTxTBox.setText(TimeTexts.get(position));
        holder.TimeTxTBox.setClickable(false);
        holder.TimeTxTBox.setFocusable(false);
        holder.TimeTxTBox.setFocusableInTouchMode(false);

        holder.TimeSwitch.setChecked(State.get(position));
        holder.TimeSwitch.setEnabled(Enable.get(position));


        return mView;
    }


    static class ActivityHolder {
        TextView TimeTxTBox;
        Switch TimeSwitch;
    }
}
