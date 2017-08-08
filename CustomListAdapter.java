package com.impactapp.vishnu.timesync;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Vishnu on 7/28/2017.
 */

public class CustomListAdapter extends ArrayAdapter<Main2Activity.myNode>{

    private Context mContext;
    private int id;
    private ArrayList<String> Switches = new ArrayList<>();
    private ArrayList<String> TimeTexts = new ArrayList<>();
    private ArrayList<Boolean> State = new ArrayList<>();
    private ArrayList<Boolean> Enable = new ArrayList<>();


    private long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    private int Seconds, Minutes, Hours, MilliSeconds;

    private Handler myHandler = new Handler();

    private SharedPreferences sharedPref;

    public CustomListAdapter(Context context, int resource, int textViewResourceId, List<Main2Activity.myNode> OBJECTS) {
        super(context,resource,textViewResourceId,OBJECTS);
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



    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View mView = v;
        ActivityHolder holder;

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


        holder.TimeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("#####index","Called");
                Switch myS = (Switch) v.findViewById(R.id.SwitchTime);
                int index = getIndex((String) myS.getText());

                if (!State.get(index)) {
                    State.set(index,true);
                    disableAll(index);
                    notifyDataSetChanged();
                    switchSelected(index);
                    Log.d("######","State Checked");
                } else {
                    Log.d("#####","Changing to uncheck");
                    State.set(index,false);
                    EnableAll();
                }
                notifyDataSetChanged();
            }
        });

        return mView;
    }


    public Object requestVal(int index,String Val) {
        if (Val.equals("time")) {
            return TimeTexts.get(index);
        } else if (Val.equals("enable")) {
            return Enable.get(index);
        } else {
            return State.get(index);
        }
    }

    public void resumeChecked(int index) {
        switchSelected(index);
    }


    private void switchSelected(final int index){
        if (State.get(index) && Enable.get(index)) {
            Runnable CountRunnable = new Runnable() {
                @Override
                public void run() {
                    StartRunning(index);
                }
            };
            Thread countThread = new Thread(CountRunnable);
            countThread.start();
        }
    }

    private void StartRunning(final int index) {
        sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.SharedPref),Context.MODE_PRIVATE);
        StartTime = sharedPref.getLong("StartTime",0);
        UpdateTime = sharedPref.getLong("UpdateTime",0);
        if (!arestatesfalse()) {
            StartTime = SystemClock.uptimeMillis();
            UpdateTime = returnSeconds(TimeTexts.get(index)) * 1000;
        }
        Log.d("######",Long.toString(Thread.currentThread().getId()));
        while(State.get(index)) {
            MillisecondTime = UpdateTime + SystemClock.uptimeMillis() - StartTime;


            Seconds = (int) (MillisecondTime / 1000);
            Minutes = Seconds / 60;
            Hours = Minutes/60;
            Seconds = Seconds % 60;
            Minutes = Minutes % 60;
            Hours = Hours % 60;

            TimeTexts.set(index,""+ String.format(Locale.US,"%02d",Hours) +":"
                    + String.format(Locale.US,"%02d",Minutes) + ":"
                    + String.format(Locale.US,"%02d",Seconds));


            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            myHandler.post(callnotify);
        }
        StartTime = 0;
    }

    private Runnable callnotify = new Runnable() {
        @Override
        public void run() {
            notifyDataSetChanged();
            updatetotaltime();
        }
    };

    private void updatetotaltime() {
        Activity act = (Activity) getContext();
        if (act != null) {
            TextView total = (TextView) act.findViewById(R.id.TimeTotal);
            int sumSec = 0;
            int limit = TimeTexts.size()-2;
            for (int i = 0;i<limit;i=i+3) {
                sumSec = sumSec+returnSeconds(TimeTexts.get(i));
                sumSec = sumSec+returnSeconds(TimeTexts.get(i+1));
                sumSec = sumSec+returnSeconds(TimeTexts.get(i+2));
            }

            int min = sumSec / 60;
            int hour = min/60;
            int sec = sumSec % 60;
            min = min % 60;
            hour = hour % 60;

            total.setText(""+ String.format(Locale.US,"%02d",hour) +":"
                    + String.format(Locale.US,"%02d",min) + ":"
                    + String.format(Locale.US,"%02d",sec));
        }
    }

    private int returnSeconds(String m_string) {
        int totalSeconds;
        int Hour = Integer.valueOf(m_string.substring(0,2));
        int Minute = Integer.valueOf(m_string.substring(3,5));
        int Seconds = Integer.valueOf(m_string.substring(6,8));
        totalSeconds = (Hour*3600)+(Minute*60)+Seconds;
        return totalSeconds;
    }

    private int getIndex(String TxT) {
        int index = -1;
        int limit = Switches.size();
        for(int i = 0;i<limit;i++) {
            if ((Switches.get(i).equals(TxT))) {
                index = i;
            }
        }
        return index;
    }

    private void disableAll(int position) {
        int limit = Switches.size();
        for(int i=0;i<limit;i++) {
            if (i != position) {
                Enable.set(i,false);
            }
        }
    }

    private void EnableAll() {
        int limit = Switches.size();
        for(int i = 0; i<limit;i++) {
            Enable.set(i,true);
        }
    }

    static private class ActivityHolder {
        TextView TimeTxTBox;
        Switch TimeSwitch;
    }

    public long returnStartTime() {
        return StartTime;
    }

    public long returnUpdateTime() {
        return UpdateTime;
    }

    private boolean arestatesfalse() {
        int limit = State.size();
        boolean rtnstate = true;
        for (int i = 0; i < limit; i++) {
            if (State.get(i)) {
                rtnstate = false;
            }
        }
        return rtnstate;
    }

}
