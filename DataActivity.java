package com.impactapp.vishnu.timesync;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpEncoding;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Data;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.client.util.StreamingContent;
import com.google.api.client.util.Value;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Arrays;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Vishnu on 8/9/2017.
 */

public class DataActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    private AdView mAdView;
    private GraphView gView;

    @Override
    public void onPermissionsGranted(int myInt, List<String> SList) {}
    @Override
    public void onPermissionsDenied(int myInt, List<String> SList) {}

    GoogleAccountCredential mCredentials;
    ProgressDialog mProgress;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_PERMISSION_GET_ACCOUNT = 1003;


    String DataPreferenceName = "DataPref";
    SharedPreferences myDataPref;
    SharedPreferences.Editor DataPrefEditor;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private static final String [] SCOPE = {SheetsScopes.SPREADSHEETS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_layout);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("9AED419BE2733780159C7FF112BAE873").build();
        mAdView.loadAd(adRequest);

        /*myDataPref = getSharedPreferences(DataPreferenceName,Context.MODE_PRIVATE);
        if (myDataPref.getAll() == null) {
            getAPIResults(MakeRequestTask.LOAD_DATA_FROM_SHEETS);
        }*/

        //Testing graph View
        gView = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0,1),new DataPoint(1,5),new DataPoint(2,3),
                new DataPoint(4,6)
        });
        gView.addSeries(series);


        // Getting Account Credentials
        mCredentials = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPE)).setBackOff(new ExponentialBackOff());
        mProgress = new ProgressDialog(this);
        //mProgress.setMessage("Calling sheets...");

        LogStoredVals();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void getAPIResults() {
        if (!isGooglePlayServiceAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredentials.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()){
            callDeviceOfflineDialog();
        } else {
            String sheetID = getPreferences(Context.MODE_PRIVATE).getString(getString(R.string.SheetID),null);
            if (sheetID == null) {
                new MakeRequestTask(mCredentials,MakeRequestTask.REQUEST_NEW_SHEET).execute();
            } else {
                int requestFromSheets = MakeRequestTask.TESTING;
                new MakeRequestTask(mCredentials,requestFromSheets).execute();
            }
        }
    }

    //Check Device Internet Connection
    //##############################################################################################
    private boolean isDeviceOnline() {
        ConnectivityManager ConMng = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = ConMng.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    private void callDeviceOfflineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
        builder.setTitle("Error");
        builder.setMessage("Device is not connected to the internet");

        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    //Google Play Service methods
    //##############################################################################################
    private boolean isGooglePlayServiceAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(DataActivity.this,connectionStatusCode,REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@


    //Credentials related methods
    //##############################################################################################
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME,null);
            Log.d("#####",getPreferences(Context.MODE_PRIVATE).toString());

            if (accountName != null) {
                mCredentials.setSelectedAccountName(accountName);
                getAPIResults();
            } else {
                startActivityForResult(mCredentials.newChooseAccountIntent(),REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(this,"This app needs to access your Google account",REQUEST_PERMISSION_GET_ACCOUNT,Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);


        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    callGoogleAPIServiceDialog();
                } else {
                    getAPIResults();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME,accountName);
                        editor.apply();
                        mCredentials.setSelectedAccountName(accountName);
                        getAPIResults();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getAPIResults();
                }
                break;
        }

    }

    private void callGoogleAPIServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
        builder.setTitle("Error");
        builder.setMessage("This app requires Google Play Services. Please install Google Play Services on your devices ad relaunch this app.");
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@


    public void TestButtonClick(View v) {
        getAPIResults();
    }

    //Private Class
    private class MakeRequestTask extends AsyncTask<Void,Void,List<String>> {
        private Sheets mService = null;
        private Exception mLastError = null;
        private String sheetID;

        private int COMMAND;

        static final int REQUEST_NEW_SHEET = 1004;
        static final int SAVE_TODAYS_VALUES = 1005;
        static final int LOAD_DATA_FROM_SHEETS = 1006;
        static final int TESTING = 5555;

        MakeRequestTask(GoogleAccountCredential mCredentials,final int COMMANDREQUEST) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            COMMAND = COMMANDREQUEST;
            mService = new Sheets.Builder(transport,jsonFactory,mCredentials).setApplicationName("TimeSync").build();
            sheetID = getPreferences(Context.MODE_PRIVATE).getString(getString(R.string.SheetID),null);
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getResultsFromSheets();
            } catch (Exception e) {
                mLastError = e;
                Log.d("EXCEPTION",e.getMessage());
                cancel(true);
                return null;
            }
        }

        private List<String> getResultsFromSheets() throws IOException,GeneralSecurityException {
            switch(COMMAND) {
                case REQUEST_NEW_SHEET:
                    return requestNewSheet();
                case SAVE_TODAYS_VALUES:
                    return saveValues();
                case LOAD_DATA_FROM_SHEETS:
                    return loadDataFromSheets();
                case TESTING:
                    return TestingMethod("Homework");
                default:
                    return null;
            }
        }

        private List<String> TestingMethod(String RequestedActivity) throws IOException, GeneralSecurityException {
            Log.d("######TestingMethod","Test");

            int col = -1;
            int limit;
            ValueRange range = mService.spreadsheets().values().get(sheetID,"A1:Z").execute();
            List<Object> titles = range.getValues().get(0);
            limit = titles.size();
            for (int i=0;i<limit;i++) {
                if (( titles.get(i)).equals(RequestedActivity)) {
                    col = i;
                }
            }


            range = mService.spreadsheets().values().get(sheetID,getAlphabetVal(col).concat("2:1000")).setMajorDimension("COLUMNS").execute();
            List<Object> reqVals = range.getValues().get(0);
            for (int i = 0;i<reqVals.size();i++) {
                Log.d("#####",((String) reqVals.get(i)));
            }
            return null;
        }

        private String getAlphabetVal(int Col) {
            String vals = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            return Character.toString(vals.charAt(Col));
        }

        private List<String> requestNewSheet() throws IOException,GeneralSecurityException {
            Spreadsheet requestSheet = new Spreadsheet();
            SpreadsheetProperties shProp = new SpreadsheetProperties();
            shProp.set("title",("DailyTimeUsage:     DateCreated:").concat(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime())));
            requestSheet.setProperties(shProp);

            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Sheets.Builder(httpTransport,jsonFactory,mCredentials).setApplicationName("TimeSync").build();

            Sheets.Spreadsheets.Create request = mService.spreadsheets().create(requestSheet);

            Spreadsheet response = request.execute();
            sheetID = response.getSpreadsheetId();
            getPreferences(Context.MODE_PRIVATE).edit().putString("SheetID",sheetID).apply();

            StoreActivityTitles();

            return null;
        }

        private void StoreActivityTitles() throws IOException {
            SharedPreferences actPref = getSharedPreferences(getString(R.string.SharedPref),Context.MODE_PRIVATE);

            ValueRange newVals = new ValueRange();
            List<Object> v = new ArrayList<>();
            v.add("Date");

            for (int i = 0;i<actPref.getInt(getString(R.string.TotalNumberActivity),0);i++) {
                v.add(actPref.getString(Integer.toString(i),null));
            }
            List<List<Object>> vals = new ArrayList<>();
            vals.add(0,v);
            newVals.setValues(vals);
            newVals.setMajorDimension("COLUMNS");

            AppendValuesResponse ap = this.mService.spreadsheets().values().append(sheetID,"A1",newVals).setValueInputOption("RAW").execute();
        }

        private List<String> saveValues() {
            return null;
        }

        private List<String> loadDataFromSheets() {
            return null;
        }

        @Override
        protected void onPreExecute() {
            mProgress.setMessage("Calling Sheets");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.dismiss();
        }


    }

    //Random Dialog launcher
    private void LaunchDialogWithMessage(String Message) {
        AlertDialog.Builder builder = new  AlertDialog.Builder(DataActivity.this);
        builder.setTitle("Exception");
        builder.setMessage(Message);

        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }



    //Helper Vars
    public void LogStoredVals() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.SharedPref),Context.MODE_PRIVATE);
        int limit = pref.getInt(getString(R.string.TotalNumberActivity),-1);
        Log.d("#####Tot",Integer.toString(limit));
        for (int i=0;i<limit;i++) {
            Log.d("#####ACT",pref.getString(Integer.toString(i),null));
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Data Page") // Define a title for the content shown.
                // Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        //getPreferences(Context.MODE_PRIVATE).edit().remove(getString(R.string.SheetID)).commit();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
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
