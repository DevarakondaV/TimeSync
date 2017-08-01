package com.impactapp.vishnu.timesync;

import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.os.Handler;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class Main2Activity extends AppCompatActivity {

    /**
     * Nested Class
     */
    public class myNode{
        //String Name;
        //TextView TimeTXT;
        //Switch TimeSwitch;

        String TextString;
        String SwitchString;
        boolean state;
        boolean enable;
    }
    /**
     * Variables
     */
    static private String INITIAL_SETTING = "00:00:00";
    ListView Stored_LV;
    CustomListAdapter myAdapter;

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    int Seconds, Minutes, Hours, MilliSeconds;

    Handler myHandler = new Handler();


    /**
     * Data Structures
     */
    ArrayList<myNode> NodesArray = new ArrayList<>();


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //String[] itemname = {"Social","Class","Homework","Other","ECWork","Hello","Strings","Helloword"};

        InsertStandardNodes();

        Stored_LV = (ListView) findViewById(R.id.ActivitiesList);
        //myAdapter = new CustomListAdapter(this,R.layout.listviewlayout,R.id.ActivitiesList,String_Names,Times);
        myAdapter = new CustomListAdapter(this,R.layout.listviewlayout,Stored_LV.getId(),NodesArray);
        Log.d("#####",Integer.toString(myAdapter.getCount()));
        Stored_LV.setAdapter(myAdapter);
        //Stored_LV.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        Stored_LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Switch mySwitch = (Switch) view.findViewById(R.id.SwitchTime);
                TextView myText = (TextView) view.findViewById(R.id.TextViewTime);

                int index = getIndexOfNode((String) mySwitch.getText());
                Log.d("#####",(String) mySwitch.getText());
                Log.d("#####Index",Integer.toString(index));
                ChangeSwitchState(index,mySwitch);
                SwitchSelected(NodesArray.get(index));
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void SwitchSelected(final myNode RunningNode) {
        if(RunningNode.enable && RunningNode.state) {
            Runnable CountRunnable = new Runnable() {
                @Override
                public void run() {
                    StartRunning(RunningNode);
                }
            };
            new Thread(CountRunnable).start();
        }
    }

    public void StartRunning(final myNode RunningNode) {
        StartTime = SystemClock.uptimeMillis();
        UpdateTime = returnSeconds(RunningNode.TextString)*1000;

        while(RunningNode.state) {
            MillisecondTime = UpdateTime + SystemClock.uptimeMillis() - StartTime;


            Seconds = (int) (MillisecondTime / 1000);
            Minutes = Seconds / 60;
            Hours = Minutes/60;
            Seconds = Seconds % 60;
            Minutes = Minutes % 60;
            Hours = Hours % 60;

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    RunningNode.TextString = ""+ String.format(Locale.US,"%02d",Hours) +":"
                            + String.format(Locale.US,"%02d",Minutes) + ":"
                            + String.format(Locale.US,"%02d",Seconds);
                    UpdateTotal();
                    updateListViewAdapter();
                }
            });
        }
    }

    private void UpdateTotal() {
    }

    private void ChangeSwitchState(int index,Switch mySwitch) {
        if (NodesArray.get(index).state) {
            NodesArray.get(index).state = false;
            EnableAll();
        } else {
            NodesArray.get(index).state = true;
            Log.d("#####",(String) mySwitch.getText());
            DisAllExcept((String) mySwitch.getText());
        }
        updateListViewAdapter();
    }

    public void Button_Click(View v) {
        switch (v.getId()) {
            case R.id.AddActivityButton:
                addNewActivity();
                break;
            case R.id.RemoveActivityButton:
                removeActivity();
                break;
            case R.id.ClearAllButton:
                ClearAll();
                break;
            case R.id.TotalButton:
                callTotalsActivity();
                break;
        }
    }

    private void addNewActivity() {}

    private void callTotalsActivity() {

    }

    private void ClearAll() {
        int limit = NodesArray.size();
        for(int i = 0;i<limit;i++) {
            NodesArray.get(i).state = false;
            NodesArray.get(i).enable = true;
            NodesArray.get(i).TextString = INITIAL_SETTING;
        }
        updateListViewAdapter();
    }

    private void removeActivity() {

    }

    private int getIndexOfNode(String selectedAct) {
        int index = -1;
        int limit = NodesArray.size();
        for(int i = 0;i<limit;i++) {
            if((NodesArray.get(i).SwitchString).equals(selectedAct)) {
                index = i;
                break;
            } else {
                index = 0;
            }
        }
        return index;
    }

    private void updateListViewAdapter() {
        Stored_LV.setAdapter(new CustomListAdapter(Main2Activity.this,R.layout.listviewlayout,Stored_LV.getId(),NodesArray));
        ((BaseAdapter) Stored_LV.getAdapter()).notifyDataSetChanged();
    }

    private void DisAllExcept(String selectedAct) {
        int limit = NodesArray.size();
        for(int i=0;i<limit;i++){
            if(!((NodesArray.get(i).SwitchString).equals(selectedAct))) {
                NodesArray.get(i).enable = false;
            }
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

    private void EnableAll() {
        int limit = NodesArray.size();
        for(int i = 0;i<limit;i++) {
            NodesArray.get(i).enable = true;
        }
    }

    private void InsertStandardNodes() {
        myNode N1 = new myNode();
        myNode N2 = new myNode();
        myNode N3 = new myNode();
        myNode N4 = new myNode();
        myNode N5 = new myNode();
        myNode N6 = new myNode();
        myNode N7 = new myNode();
        myNode N8 = new myNode();
        myNode N9 = new myNode();
        myNode N10 = new myNode();
        myNode N11 = new myNode();
        myNode N12 = new myNode();
        myNode N13 = new myNode();
        myNode N14 = new myNode();
        myNode N15 = new myNode();
        myNode N16 = new myNode();

        N1.SwitchString = "Social";
        N2.SwitchString = "Class";
        N3.SwitchString = "Homework";
        N4.SwitchString = "Study";
        N5.SwitchString = "ECWork";
        N6.SwitchString = "Gym";
        N7.SwitchString = "Sleep";
        N8.SwitchString = "Other";
        N9.SwitchString = "Elephant";
        N10.SwitchString = "Cook";
        N11.SwitchString = "Zoo";
        N12.SwitchString = "Machine";
        N13.SwitchString = "Home";
        N14.SwitchString = "Telephone";
        N15.SwitchString = "Gamma";
        N16.SwitchString = "Drama";

        N1.TextString = INITIAL_SETTING;
        N2.TextString = INITIAL_SETTING;
        N3.TextString = INITIAL_SETTING;
        N4.TextString = INITIAL_SETTING;
        N5.TextString = INITIAL_SETTING;
        N6.TextString = INITIAL_SETTING;
        N7.TextString = INITIAL_SETTING;
        N8.TextString = INITIAL_SETTING;
        N9.TextString = INITIAL_SETTING;
        N10.TextString = INITIAL_SETTING;
        N11.TextString = INITIAL_SETTING;
        N12.TextString = INITIAL_SETTING;
        N13.TextString = INITIAL_SETTING;
        N14.TextString = INITIAL_SETTING;
        N15.TextString = INITIAL_SETTING;
        N16.TextString = INITIAL_SETTING;

        N1.state = false;
        N2.state = false;
        N3.state = false;
        N4.state = false;
        N5.state = false;
        N6.state = false;
        N7.state = false;
        N8.state = false;
        N9.state = false;
        N10.state = false;
        N11.state = false;
        N12.state = false;
        N13.state = false;
        N14.state = false;
        N15.state = false;
        N16.state = false;


        N1.enable = true;
        N2.enable = true;
        N3.enable = true;
        N4.enable = true;
        N5.enable = true;
        N6.enable = true;
        N7.enable = true;
        N8.enable = true;
        N9.enable = true;
        N10.enable = true;
        N11.enable = true;
        N12.enable = true;
        N13.enable = true;
        N14.enable = true;
        N15.enable = true;
        N16.enable = true;

        NodesArray.add(N1);
        NodesArray.add(N2);
        NodesArray.add(N3);
        NodesArray.add(N4);
        NodesArray.add(N5);
        NodesArray.add(N6);
        NodesArray.add(N7);
        NodesArray.add(N8);
        NodesArray.add(N9);
        NodesArray.add(N10);
        NodesArray.add(N11);
        NodesArray.add(N12);
        NodesArray.add(N13);
        NodesArray.add(N14);
        NodesArray.add(N15);
        NodesArray.add(N16);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main2 Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
