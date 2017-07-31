package com.impactapp.vishnu.timesync;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.api.services.sheets.v4.Sheets;

import org.w3c.dom.Text;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;
import android.os.Handler;

import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    private class Node {
        String Name;
        TextView Text;
        Switch Text_Switch;
    }



    ArrayList<Node> Stored_Activities = new ArrayList<Node>();


    /**
     * variables for counting time
     */
    static private String INITIAL_SETTING = "00:00:00";
    private String PrefName = "com.ImpactApp.TimeSync3.SavedInstance";
    private SharedPreferences pref;// = getApplicationContext().getSharedPreferences(PrefName,MODE_PRIVATE);
    private SharedPreferences.Editor PrefEditor;// = pref.edit();

    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    int Seconds, Minutes, Hours, MilliSeconds;


    TextView SocialTV, ClassTV, HomeworkTV, StudyTV, ECworkTV, GymTV, SleepTV, OtherTV, TotalTV;

    Switch SSocial, Sclass, Shomework, Sstudy, SECwork, SGym, SSleep, SOther;
    //

    Handler myHandler = new Handler();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        SocialTV = (TextView) findViewById(R.id.textView8);
        ClassTV = (TextView) findViewById(R.id.textView9);
        HomeworkTV = (TextView) findViewById(R.id.textView10);
        StudyTV = (TextView) findViewById(R.id.textView11);
        ECworkTV = (TextView) findViewById(R.id.textView12);
        GymTV = (TextView) findViewById(R.id.textView13);
        SleepTV = (TextView) findViewById(R.id.textView14);
        OtherTV = (TextView) findViewById(R.id.textView15);
        TotalTV = (TextView) findViewById(R.id.textView16);


        SSocial = (Switch) findViewById(R.id.switch1);
        Sclass = (Switch) findViewById(R.id.switch3);
        Shomework = (Switch) findViewById(R.id.switch4);
        Sstudy = (Switch) findViewById(R.id.switch5);
        SECwork = (Switch) findViewById(R.id.switch6);
        SGym = (Switch) findViewById(R.id.switch7);
        SSleep = (Switch) findViewById(R.id.switch8);
        SOther = (Switch) findViewById(R.id.switch9);

        StartTime = 0;
        UpdateTime = 0;

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


    public void onSwitchClicked(View v) {
        switch (v.getId()) {
            case R.id.switch1:
                DisableAllExcept(R.id.switch1);
                switchCall(R.id.switch1, SocialTV.getId());
                break;
            case R.id.switch3:
                DisableAllExcept(R.id.switch3);
                switchCall(R.id.switch3, ClassTV.getId());
                break;
            case R.id.switch4:
                DisableAllExcept(R.id.switch4);
                switchCall(R.id.switch4, HomeworkTV.getId());
                break;
            case R.id.switch5:
                DisableAllExcept(R.id.switch5);
                switchCall(R.id.switch5, StudyTV.getId());
                break;
            case R.id.switch6:
                DisableAllExcept(R.id.switch6);
                switchCall(R.id.switch6, ECworkTV.getId());
                break;
            case R.id.switch7:
                DisableAllExcept(R.id.switch7);
                switchCall(R.id.switch7, GymTV.getId());
                break;
            case R.id.switch8:
                DisableAllExcept(R.id.switch8);
                switchCall(R.id.switch8, SleepTV.getId());
                break;
            case R.id.switch9:
                DisableAllExcept(R.id.switch9);
                switchCall(R.id.switch9, OtherTV.getId());
                break;
            case R.id.button3:
                callTotalActivity();
                break;
            case R.id.button5:
                clearAll();
                break;
        }
    }

    private void clearAll() {

        SocialTV.setText(INITIAL_SETTING);
        ClassTV.setText(INITIAL_SETTING);
        HomeworkTV.setText(INITIAL_SETTING);
        StudyTV.setText(INITIAL_SETTING);
        ECworkTV.setText(INITIAL_SETTING);
        GymTV.setText(INITIAL_SETTING);
        SleepTV.setText(INITIAL_SETTING);
        OtherTV.setText(INITIAL_SETTING);
        TotalTV.setText(INITIAL_SETTING);

    }

    public void UpdateTotal() {
        int TotSeconds = returnSeconds((String) SocialTV.getText()) +
                returnSeconds((String) ClassTV.getText()) +
                returnSeconds((String) HomeworkTV.getText()) +
                returnSeconds((String) StudyTV.getText()) +
                returnSeconds((String) ECworkTV.getText()) +
                returnSeconds((String) GymTV.getText()) +
                returnSeconds((String) SleepTV.getText()) +
                returnSeconds((String) OtherTV.getText());

        int minutes = TotSeconds/60;
        int hours = minutes/60;
        TotSeconds = TotSeconds % 60;
        minutes = minutes % 60;
        hours = hours % 60;

        TotalTV.setText(""+ String.format("%02d",hours) +":"
                + String.format("%02d",minutes) + ":"
                + String.format("%02d",TotSeconds));
    }


    /**
     * Thos function changes the values of the Chronometer
     *
     * @param
     */
    private void switchCall(final int SwitchID, final int TextID) {
        Switch Cur_Switch = (Switch) findViewById(SwitchID);
        if(Cur_Switch.isChecked()) {
            Runnable CountRunnable = new Runnable() {
                @Override
                public void run() {
                    StartRunning(SwitchID, TextID);
                }
            };
            new Thread(CountRunnable).start();
        }
        else {
            EnableAll();
        }

    }

    private void StartRunning(int SwitchID, int TextID) {
        Switch Cur_Switch = (Switch) findViewById(SwitchID);
        final TextView Cur_Text = (TextView) findViewById(TextID);

        pref =  getApplicationContext().getSharedPreferences(PrefName,MODE_PRIVATE);
        if (pref.getInt("CheckedID",0) != Cur_Switch.getId()) {
            StartTime = SystemClock.uptimeMillis();
            UpdateTime = returnSeconds((String) Cur_Text.getText()) * 1000;
        }

        while (Cur_Switch.isChecked()) {
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
                    Cur_Text.setText(""+ String.format("%02d",Hours) +":"
                                        + String.format("%02d",Minutes) + ":"
                                        + String.format("%02d",Seconds));
                    UpdateTotal();
                }
            });
        }
        StartTime = 0;
    }

    private int returnCheckedID() {
        if (SSocial.isChecked()) {
            return SSocial.getId();
        }
        else if (Sclass.isChecked()) {
            return Sclass.getId();
        }
        else if (Shomework.isChecked()) {
            return Shomework.getId();
        }
        else if (Sstudy.isChecked())
        {
            return Sstudy.getId();
        }
        else if (SECwork.isChecked()) {
            return SECwork.getId();
        }
        else if (SGym.isChecked()) {
            return SGym.getId();
        }
        else if (SSleep.isChecked()) {
            return SSleep.getId();
        }
        else if (SOther.isChecked()) {
            return SOther.getId();
        }
        else {
            return 0;
        }
    }
    private int returnCheckedTextID() {
        if (SSocial.isChecked()) {
            return SocialTV.getId();
        } else if (Sclass.isChecked()) {
            return ClassTV.getId();
        } else if (Shomework.isChecked()) {
            return HomeworkTV.getId();
        } else if (Sstudy.isChecked()) {
            return StudyTV.getId();
        } else if (SECwork.isChecked()) {
            return ECworkTV.getId();
        } else if (SGym.isChecked()) {
            return GymTV.getId();
        } else if (SSleep.isChecked()) {
            return SleepTV.getId();
        } else if (SOther.isChecked()) {
            return OtherTV.getId();
        } else {
            return 0;
        }
    }

    /**
     * Disables all switches except ID
     *
     * @param ID
     */
    private void DisableAllExcept(int ID) {
        Switch mySwitch = (Switch) findViewById(ID);

        if (mySwitch != SSocial) {
            SSocial.setEnabled(false);
        }
        if (mySwitch != Sclass) {
            Sclass.setEnabled(false);
        }
        if (mySwitch != Shomework) {
            Shomework.setEnabled(false);
        }
        if (mySwitch != Sstudy) {
            Sstudy.setEnabled(false);
        }
        if (mySwitch != SECwork) {
            SECwork.setEnabled(false);
        }
        if (mySwitch != SGym) {
            SGym.setEnabled(false);
        }
        if (mySwitch != SSleep) {
            SSleep.setEnabled(false);
        }
        if (mySwitch != SOther) {
            SOther.setEnabled(false);
        }
    }


    /**
     * Enables all switches
     */
    private void EnableAll() {

        SSocial.setEnabled(true);
        Sclass.setEnabled(true);
        Shomework.setEnabled(true);
        Sstudy.setEnabled(true);
        SECwork.setEnabled(true);
        SGym.setEnabled(true);
        SSleep.setEnabled(true);
        SOther.setEnabled(true);
    }


    /**
     * calls new Activity
     */
    public void callTotalActivity() {
        /*Intent newActivityCall = new Intent(MainActivity.this, TotalsActivity.class);


        int totWork = returnSeconds((String) ECworkTV.getText())+returnSeconds((String) HomeworkTV.getText()) + returnSeconds((String) StudyTV.getText());

        newActivityCall.putExtra("Social", returnSeconds((String) SocialTV.getText()));
        newActivityCall.putExtra("Work", totWork);
        newActivityCall.putExtra("Total", returnSeconds((String) TotalTV.getText()));

        newActivityCall.putExtra("SocialT",SocialTV.getText());
        newActivityCall.putExtra("Tot",TotalTV.getText());
        newActivityCall.putExtra("Class",ClassTV.getText());
        newActivityCall.putExtra("Homework",HomeworkTV.getText());
        newActivityCall.putExtra("Study",StudyTV.getText());
        newActivityCall.putExtra("ECWork",ECworkTV.getText());
        newActivityCall.putExtra("Gym",GymTV.getText());
        newActivityCall.putExtra("Sleep",SleepTV.getText());
        newActivityCall.putExtra("Other",OtherTV.getText());


        startActivity(newActivityCall);
        */

        Intent newActivityCall = new Intent(MainActivity.this,Main2Activity.class);
        startActivity(newActivityCall);

    }


    /**
     * returns the total seconds in the string below
     *
     * @param m_string
     * @return
     */
    Integer returnSeconds(String m_string) {

        int totalSeconds;
        int Hour = Integer.valueOf(m_string.substring(0,2));
        int Minute = Integer.valueOf(m_string.substring(3,5));
        int Seconds = Integer.valueOf(m_string.substring(6,8));
        totalSeconds = (Hour*3600)+(Minute*60)+Seconds;

        return totalSeconds;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    /*public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }*/
    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();
        // AppIndex.AppIndexApi.start(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onPause() {
        super.onPause();

        pref =  getApplicationContext().getSharedPreferences(PrefName,MODE_PRIVATE);
        PrefEditor = pref.edit();
        PrefEditor.putString("Social",(String) SocialTV.getText());
        PrefEditor.putString("Class",(String) ClassTV.getText());
        PrefEditor.putString("Homework",(String) HomeworkTV.getText());
        PrefEditor.putString("Study",(String) StudyTV.getText());
        PrefEditor.putString("ECWork",(String) ECworkTV.getText());
        PrefEditor.putString("Gym",(String) GymTV.getText());
        PrefEditor.putString("Sleep",(String) SleepTV.getText());
        PrefEditor.putString("Other",(String) OtherTV.getText());
        PrefEditor.putString("Total",(String) TotalTV.getText());
        PrefEditor.putInt("CheckedID",returnCheckedID());
        PrefEditor.putInt("CheckedTextID",returnCheckedTextID());
        PrefEditor.putLong("StartTime",StartTime);
        PrefEditor.putLong("UpdateTime",UpdateTime);
        PrefEditor.commit();
    }


    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //AppIndex.AppIndexApi.end(client, getIndexApiAction());
        //client.disconnect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();

        pref =  getApplicationContext().getSharedPreferences(PrefName,MODE_PRIVATE);
        StartTime = pref.getLong("StartTime",-1);
        UpdateTime = pref.getLong("UpdateTime",-1);
        SocialTV.setText(pref.getString("Social",INITIAL_SETTING));
        ClassTV.setText(pref.getString("Class",INITIAL_SETTING));
        HomeworkTV.setText(pref.getString("Homework",INITIAL_SETTING));
        StudyTV.setText(pref.getString("Study",INITIAL_SETTING));
        ECworkTV.setText(pref.getString("ECWork",INITIAL_SETTING));
        GymTV.setText(pref.getString("Gym",INITIAL_SETTING));
        SleepTV.setText(pref.getString("Sleep",INITIAL_SETTING));
        OtherTV.setText(pref.getString("Other",INITIAL_SETTING));
        TotalTV.setText(pref.getString("Total",INITIAL_SETTING));


        if (pref.getInt("CheckedID",0) != 0) {
            DisableAllExcept(pref.getInt("CheckedID",0));
            switchCall(pref.getInt("CheckedID",0), pref.getInt("CheckedTextID",0));
        }


    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
