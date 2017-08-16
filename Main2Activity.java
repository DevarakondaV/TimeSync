package com.impactapp.vishnu.timesync;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.Map;
import java.util.SortedMap;

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


    private SharedPreferences sharedPref;
    private SharedPreferences.Editor PrefEditor;
    /**
     * Data Structures
     */
    ArrayList<myNode> NodesArray = new ArrayList<>();
    TextView totalTime;


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

        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.SharedPref),Context.MODE_PRIVATE);

        //InsertStandardNodes();

        Stored_LV = (ListView) findViewById(R.id.ActivitiesList);
        totalTime = (TextView) findViewById(R.id.TimeTotal);
        //myAdapter = new CustomListAdapter(this,R.layout.listviewlayout,Stored_LV.getId(),NodesArray);
        //Stored_LV.setAdapter(myAdapter);


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
        Intent newAct = new Intent(Main2Activity.this,DataActivity.class);
        startActivity(newAct);
    }

    private void ClearAll() {
        int limit = NodesArray.size();
        for(int i = 0;i<limit;i++) {
            NodesArray.get(i).state = false;
            NodesArray.get(i).enable = true;
            NodesArray.get(i).TextString = INITIAL_SETTING;
        }
        updateListViewAdapter();
        totalTime.setText(INITIAL_SETTING);
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
    public void onPause() {
        super.onPause();
        StoreVals();
    }

    private void StoreVals() {
        int limit = NodesArray.size();
        boolean en, st;
        String Sstring;
        String Tstring;

        PrefEditor = sharedPref.edit();
        PrefEditor.putInt(getString(R.string.TotalNumberActivity), limit);


        for (int i = 0; i < limit; i++) {
            Sstring = NodesArray.get(i).SwitchString;

            Tstring = (String) myAdapter.requestVal(i, getString(R.string.time));
            en = (Boolean) myAdapter.requestVal(i, getString(R.string.enable));
            st = (Boolean) myAdapter.requestVal(i, getString(R.string.state));

            PrefEditor.putString(Integer.toString(i), Sstring);
            PrefEditor.putString(Sstring.concat(getString(R.string.time)), Tstring);
            PrefEditor.putBoolean(Sstring.concat(getString(R.string.enable)), en);
            PrefEditor.putBoolean(Sstring.concat(getString(R.string.state)), st);
        }

        PrefEditor.putLong("StartTime", myAdapter.returnStartTime());
        PrefEditor.putLong("UpdateTime", myAdapter.returnUpdateTime());
        PrefEditor.apply();
        NodesArray.clear();
    }


    private void LoadDefaults() {
        if (!NodesArray.isEmpty()) {NodesArray.clear();}
        //PrefEditor = sharedPref.edit();
        //PrefEditor.clear();
        //PrefEditor.putBoolean(getString(R.string.SetDefault),true);
        //PrefEditor.commit();

        myNode N1,N2,N3,N4,N5,N6,N7,N8;
        N1 = new myNode(); N1.SwitchString = "Social";N1.TextString = INITIAL_SETTING;N1.enable = true; N1.state = false;
        N2 = new myNode(); N2.SwitchString = "Class";N2.TextString = INITIAL_SETTING;N2.enable = true; N2.state = false;
        N3 = new myNode(); N3.SwitchString = "Homework";N3.TextString = INITIAL_SETTING;N3.enable = true; N3.state = false;
        N4 = new myNode(); N4.SwitchString = "Study";N4.TextString = INITIAL_SETTING;N4.enable = true; N4.state = false;
        N5 = new myNode(); N5.SwitchString = "Gym";N5.TextString = INITIAL_SETTING;N5.enable = true; N5.state = false;
        N6 = new myNode(); N6.SwitchString = "Sleep";N6.TextString = INITIAL_SETTING;N6.enable = true; N6.state = false;
        N7 = new myNode(); N7.SwitchString = "Work";N7.TextString = INITIAL_SETTING;N7.enable = true; N7.state = false;
        N8 = new myNode(); N8.SwitchString = "Other";N8.TextString = INITIAL_SETTING;N8.enable = true; N8.state = false;

        //NodesArray.add(0,N1);
        NodesArray.add(N1);NodesArray.add(N2);NodesArray.add(N3);NodesArray.add(N4);
        NodesArray.add(N5);NodesArray.add(N6);NodesArray.add(N7);NodesArray.add(N8);
    }

    private int LoadStoredVals() {
        int checked = -1;
        int limit = sharedPref.getInt(getString(R.string.TotalNumberActivity),-1);
        Log.d("#####LimitWhenLoad",Integer.toString(limit));
        String pos;
        for(int i = 0;i<limit;i++) {
            pos = Integer.toString(i);
            myNode newNode = new myNode();
            newNode.SwitchString = sharedPref.getString(pos,"None");
            newNode.TextString = sharedPref.getString(newNode.SwitchString.concat(getString(R.string.time)),INITIAL_SETTING);
            newNode.enable = sharedPref.getBoolean(newNode.SwitchString.concat(getString(R.string.enable)),false);
            newNode.state = sharedPref.getBoolean(newNode.SwitchString.concat(getString(R.string.state)),false);
            if (newNode.state) {
                checked = i;
            }
            NodesArray.add(newNode);
        }
        return checked;
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onResume(){
        super.onResume();

        int checked = -1;

        if (sharedPref.getBoolean("FirstRun",false) || sharedPref.getInt(getString(R.string.TotalNumberActivity),0) == 0) {
            LoadDefaults();
            sharedPref.edit().putBoolean("FirstRun",false).apply();
        } else {
            checked = LoadStoredVals();
        }
        updateListViewAdapter();


        if (checked != -1) {
            myAdapter.resumeChecked(checked);
        }
    }
}
