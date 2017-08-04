package com.impactapp.vishnu.timesync;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

public class Main2Activity extends AppCompatActivity implements AddActivityFragment.AddActivityFragmentListener, RemoveActivityFragment.RemoveActivityFragmentListener {

    /**
     * Nested Class
     */
    public class myNode{
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
        myAdapter = new CustomListAdapter(this,R.layout.listviewlayout,Stored_LV.getId(),NodesArray);
        Stored_LV.setAdapter(myAdapter);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    private void addNewActivity() {
        DialogFragment newFragment = new AddActivityFragment();
        newFragment.show(getFragmentManager(),"Add Activity");
        //newFragment.getDialog().setTitle("Add New Activity");
    }

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
        DialogFragment newFragment = new RemoveActivityFragment();
        Bundle arguments = new Bundle();
        int limit = NodesArray.size();
        arguments.putInt("Size",limit);
        for(int i=0;i<limit;i++) {
            arguments.putString(Integer.toString(i),NodesArray.get(i).SwitchString);
        }
        newFragment.setArguments(arguments);
        newFragment.show(getFragmentManager(),"Remove Activity");
    }

    private void updateListViewAdapter() {
        myAdapter = null;
        myAdapter = new CustomListAdapter(Main2Activity.this,R.layout.listviewlayout,Stored_LV.getId(),NodesArray);
        Stored_LV.setAdapter(myAdapter);
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


        N1.SwitchString = "Social";
        N2.SwitchString = "Class";
        N3.SwitchString = "Homework";
        N4.SwitchString = "Study";
        N5.SwitchString = "ECWork";
        N6.SwitchString = "Gym";
        N7.SwitchString = "Sleep";
        N8.SwitchString = "Other";

        N1.TextString = INITIAL_SETTING;
        N2.TextString = INITIAL_SETTING;
        N3.TextString = INITIAL_SETTING;
        N4.TextString = INITIAL_SETTING;
        N5.TextString = INITIAL_SETTING;
        N6.TextString = INITIAL_SETTING;
        N7.TextString = INITIAL_SETTING;
        N8.TextString = INITIAL_SETTING;


        N1.state = false;
        N2.state = false;
        N3.state = false;
        N4.state = false;
        N5.state = false;
        N6.state = false;
        N7.state = false;
        N8.state = false;



        N1.enable = true;
        N2.enable = true;
        N3.enable = true;
        N4.enable = true;
        N5.enable = true;
        N6.enable = true;
        N7.enable = true;
        N8.enable = true;


        NodesArray.add(N1);
        NodesArray.add(N2);
        NodesArray.add(N3);
        NodesArray.add(N4);
        NodesArray.add(N5);
        NodesArray.add(N6);
        NodesArray.add(N7);
        NodesArray.add(N8);


        /*
        //TestNodes

        myNode N9 = new myNode();
        myNode N10 = new myNode();
        myNode N11 = new myNode();
        myNode N12 = new myNode();
        myNode N13 = new myNode();

        N9.SwitchString = "dadsfaf";
        N9.TextString = "asddaf";
        N9.state = false;
        N9.enable = true;

        N10.SwitchString = "daadsff";
        N10.TextString = "dadaff";
        N10.state = false;
        N10.enable = true;

        N11.SwitchString = "dadaff";
        N11.TextString = "dasfdaf";
        N11.state = false;
        N11.enable = true;

        N12.SwitchString = "dadfaf";
        N12.TextString = "dafdaf";
        N12.state = false;
        N12.enable = true;

        N13.SwitchString = "dfadaf";
        N13.TextString = "ddaf";
        N13.state = false;
        N13.enable = true;

        NodesArray.add(N9);
        NodesArray.add(N10);
        NodesArray.add(N11);
        NodesArray.add(N12);
        NodesArray.add(N13);*/

    }

    private int getIndexOfNode(String TxT) {
        int limit = NodesArray.size();
        int index = -1;
        for(int i = 0;i<limit;i++) {
            if((NodesArray.get(i).SwitchString).equals(TxT)) {
                index = i;
            }
        }
        return index;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        AddActivityFragment myFrag = (AddActivityFragment) dialog;

        myNode newNode = new myNode();
        newNode.enable = true;
        newNode.state = false;
        newNode.TextString = INITIAL_SETTING;
        newNode.SwitchString = myFrag.getETxTInput();
        NodesArray.add(newNode);
        updateListViewAdapter();
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onRemoveActivityDialogPositiveClick(DialogFragment dialog) {
        RemoveActivityFragment myFrag = (RemoveActivityFragment) dialog;
        String removeAct = myFrag.getSelectedActivity();
        Log.d("#####",removeAct);
        int index = getIndexOfNode(removeAct);
        NodesArray.remove(index);
        updateListViewAdapter();
    }

    @Override
    public void onRemoveActivityDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
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
