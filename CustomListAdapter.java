package com.impactapp.vishnu.timesync;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.TintableBackgroundView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.api.Result;

import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.Time;
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

    private Main2Activity act;
    private ListView LV;
    private View TouchedViewChild;
    private TextView TouchedTV;


    private long MillisecondTime, TimeBuff, UpdateTime = 0L, UpdateTime2 = 0L;
    private int Seconds, Minutes, Hours, MilliSeconds;
    private int TotSec;
    public int storedIndex;
    public long StartTime;
    private String UpdateTimeValue;

    private Handler myHandler = new Handler();
    private Runnable CountRunnable;
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
        act = (Main2Activity) mContext;
        LV = act.getStored_LV();
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
                    storedIndex = index;
                    StartTime = SystemClock.uptimeMillis();
                    switchSelected(index);
                } else {
                    Log.d("#####","Changing to uncheck");
                    State.set(index,false);
                    EnableAll();
                    Log.d("CLICKTIME",TimeTexts.get(index));
                    notifyDataSetChanged();
                }
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

    public void updateView(int index, String up) {
        Main2Activity act = (Main2Activity) mContext;
        View v = act.getStored_LV().getChildAt(index-act.getStored_LV().getFirstVisiblePosition());

        if (v == null)
            return;

        TextView Tx = (TextView) v.findViewById(R.id.TextViewTime);
        Tx.setText(up);
    }

    public void switchSelected(final int index){
        if (State.get(index) && Enable.get(index)) {
            CountRunnable = new Runnable() {
                @Override
                public void run() {
                    StartRunning(index);
                }
            };

            //AsyncTask.execute(CountRunnable);
            new Thread(CountRunnable).start();
        }
    }

    private void StartRunning(final int index) {
        sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.SharedPref), Context.MODE_PRIVATE);
        //StartTime = sharedPref.getLong("StartTime", 0);
        UpdateTime = returnSeconds(TimeTexts.get(index))*1000;

        /*
        if (!arestatesfalse()) {
            Log.d("Resetting StartTime"," ");
            StartTime = SystemClock.uptimeMillis();
        }*/
        Log.d("StartTimeUsed",String.valueOf(StartTime));
        Log.d("SystemTime",String.valueOf(SystemClock.uptimeMillis()));
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int prevSec = 0;
        while (State.get(index)) {
            MillisecondTime = UpdateTime + SystemClock.uptimeMillis() - StartTime;


            Seconds = (int) (MillisecondTime / 1000);
            Minutes = Seconds / 60;
            Hours = Minutes / 60;
            Seconds = Seconds % 60;
            Minutes = Minutes % 60;
            Hours = Hours % 60;
            UpdateTimeValue = "" + String.format(Locale.US, "%02d", Hours) + ":"
                    + String.format(Locale.US, "%02d", Minutes) + ":"
                    + String.format(Locale.US, "%02d", Seconds);


            if (Seconds-prevSec == 1) {
                TotSec = TotSec+1;
                prevSec = Seconds;
            }
            TimeTexts.set(index, UpdateTimeValue);

            //Log.d("TOTAL",Integer.toString(TotSec));
            //Log.d(TimeTexts.get(index)," ");
            myHandler.post(callnotify);

        }
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                //updatetotaltime();
                notifyDataSetChanged();
            }
        });
        StartTime = 0;
    }

    private Runnable callnotify = new Runnable() {
        @Override
        public void run() {

            TouchedViewChild = LV.getChildAt(storedIndex-LV.getFirstVisiblePosition());
            if (TouchedViewChild == null){
                return;
            }
            TouchedTV = (TextView) TouchedViewChild.findViewById(R.id.TextViewTime);
            TouchedTV.setText(UpdateTimeValue);

        }
    };

    private void updatetotaltime() {
        //Activity act = (Activity) getContext();
        if (act != null) {
            int sumSec = TotSec;
            int min = sumSec / 60;
            int hour = min/60;
            int sec = sumSec % 60;
            min = min % 60;
            hour = hour % 60;

            TextView total = (TextView) act.findViewById(R.id.TimeTotal);
            total.setText(""+ String.format(Locale.US,"%02d",hour) +":"
                    + String.format(Locale.US,"%02d",min) + ":"
                    + String.format(Locale.US,"%02d",sec));
        }
    }

    public int returnSeconds(String m_string) {
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

    public boolean arestatesfalse() {
        int limit = State.size();
        boolean rtnstate = true;
        for (int i = 0; i < limit; i++) {
            if (State.get(i)) {
                rtnstate = false;
            }
        }
        return rtnstate;
    }

    public void AddNewElement(Object Value) {
        Main2Activity.myNode Val = (Main2Activity.myNode) Value;
        Switches.add(Val.SwitchString);
        TimeTexts.add(Val.TextString);
        State.add(Val.state);
        if (!arestatesfalse()) {
            Enable.add(false);
        } else {
            Enable.add(Val.enable);
        }
    }

    public Boolean RemoveElement(Object Value) {
        Boolean removed = false;
        String Val = (String) Value;
        Log.d("#####",Val);
        int index = getIndex(Val);
        if (!State.get(index)) {
            Switches.remove(index);
            TimeTexts.remove(index);
            State.remove(index);
            Enable.remove(index);
            notifyDataSetChanged();
            removed = true;
        }
        return removed;
    }

    public void clearAll() {
        int limit = Switches.size();
        for (int i = 0;i<limit;i++) {
            State.set(i,false);
            Enable.set(i,true);
            TimeTexts.set(i,"00:00:00");
        }
        notifyDataSetChanged();
    }


}
