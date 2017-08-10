package com.impactapp.vishnu.timesync;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Data;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Sheet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.sql.Array;
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
    private static final String PREF_ACCOUNT_NAME = "accountName";

    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_PERMISSION_GET_ACCOUNT = 1003;

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


        //Testing graph View
        gView = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0,1),new DataPoint(1,5),new DataPoint(2,3),
                new DataPoint(4,6)
        });
        gView.addSeries(series);

        // Getting Account Credentials
        mCredentials = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPE)).setBackOff(new ExponentialBackOff());


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
            new MakeRequestTask(mCredentials).execute();
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
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@


    //Private Class
    private class MakeRequestTask extends AsyncTask<Void,Void,List<String>> {
        private Sheets mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential mCredentials) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Sheets.Builder(transport,jsonFactory,mCredentials).setApplicationName("TimeSync").build();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return null;
            } catch (Exception e) {
                mLastError = e;
                LaunchDialogWithMessage(e.getMessage());
                cancel(true);
                return null;
            }
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
