package com.impactapp.vishnu.timesync;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;


//imports for google sheets
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;


import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.client.util.Value;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class TotalsActivity extends Activity implements EasyPermissions.PermissionCallbacks {


    Integer TotalSoc;
    Integer Total;


    String perSoc;
    String perWork;
    String perOther;

    String Social;
    String tot;
    String classT;
    String Homework;
    String Study;
    String ECWork;
    String Gym;
    String Sleep;
    String Other;
    Integer TotalWorkSeconds;

    boolean callSheetsActive;

    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totals);


        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Sheets...");
    }

    public void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            TotalSoc = extras.getInt("Social");
            Total = extras.getInt("Total");
            TotalWorkSeconds = extras.getInt("Work");

            Social = extras.getString("SocialT");
            tot = extras.getString("Tot");
            classT = extras.getString("Class");
            Homework = extras.getString("Homework");
            Study = extras.getString("Study");
            ECWork = extras.getString("ECWork");
            Gym = extras.getString("Gym");
            Sleep = extras.getString("Sleep");
            Other = extras.getString("Other");
        }


        perSoc = Double.toString((double) ((int) (((double) TotalSoc / Total) * 10000)) / 100);
        perWork = Double.toString((double) ((int) (((double) TotalWorkSeconds / Total) * 10000)) / 100);

        perOther = Double.toString(100-Double.parseDouble(perSoc)-Double.parseDouble(perWork));

        TextView mySoc = (TextView) findViewById(R.id.textView3);
        TextView myWork = (TextView) findViewById(R.id.textView6);
        TextView perSocT = (TextView) findViewById(R.id.textView5);
        TextView perWorkT = (TextView) findViewById(R.id.textView7);

        mySoc.setText(SecToText(TotalSoc));
        myWork.setText(SecToText(TotalWorkSeconds));
        perSocT.setText(perSoc);
        perWorkT.setText(perWork);

        callSheetsActive = true;
    }


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an account
     * was selected and the device currently has online access. If any of the
     * preconditions are not satisfied, the app will promp the user as appropriate
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServiceAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            callDeviceOfflineDialog();
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);

            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(this, "This app needs to access your Google account", REQUEST_PERMISSION_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    callGoogleAPIServiceDialog();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }


    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }


    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
        System.out.println("Denied");
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServiceAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectoinStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectoinStatusCode == ConnectionResult.SUCCESS;
    }


    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(TotalsActivity.this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    /**
     * calls Google sheets API
     */
    private void callGoogleAPIServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error").setMessage("This app requires Google Play Services. Please install Google Play Services on your devices ad relaunch this app.");
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });


        AlertDialog alert = builder.create();
        builder.show();
    }

    /**
     * Method shows device offline dialog
     */
    private void callDeviceOfflineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TotalsActivity.this);
        builder.setTitle("Error");
        builder.setMessage("Device is not connected to the internet");

        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    public void callDialogwithMessage(String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error").setMessage(Message);
        builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Converts m_sec to time String
     *
     * @param m_sec
     * @return
     */
    private String SecToText(Integer m_sec) {
        Integer Hours = m_sec / (3600);
        m_sec = m_sec - (Hours * 3600);
        Integer Min = m_sec / 60;
        m_sec = m_sec - (Min * 60);
        String Time = Integer.toString(Hours) + ":" + Integer.toString(Min) + ":" + Integer.toString(m_sec);
        return Time;
    }


    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                this.finish();
                break;
            case R.id.button4:
                getResultsFromApi();
                break;

        }
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private Sheets mService = null;
        private Exception mLastError = null;


        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Sheets.Builder(transport, jsonFactory, credential).setApplicationName("TimeSync").build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                Log.d("TAG###########",e.getMessage());
                cancel(true);
                return null;
            }
        }


        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
         *
         * @return List of names and majors
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            String spreadsheetsId = "1WU0p8A2xv_Rg8ZaFDv48fFz9BxhaEMbOm-2Ueo68lkA";

            //Get Date
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String strDate = sdf.format(c.getTime());

            //Get Time
            c = Calendar.getInstance(TimeZone.getTimeZone("GMT-8"));
            Integer hour = c.get(Calendar.HOUR_OF_DAY);

            //Declaring results for return inCase function needs to return something
            List<String> results = new ArrayList<String>();


            if (hour >= 21) {
                //ValueRange that will be added to sheet
                ValueRange newVals = new ValueRange();
                List<Object> v = new ArrayList<Object>(); //value range must have List<List<Object>> format
                v.add(0, strDate);
                v.add(1, Social);
                v.add(2, classT);
                v.add(3, Homework);
                v.add(4, Study);
                v.add(5, ECWork);
                v.add(6, Other);
                v.add(7, Gym);
                v.add(8, Sleep);
                v.add(9, tot);
                v.add(10, SecToText(TotalWorkSeconds));
                v.add(11, SecToText(TotalSoc));
                v.add(12, perWork);
                v.add(13, perSoc);
                v.add(14, perOther);
                List<List<Object>> vals = new ArrayList<List<Object>>();
                vals.add(0, v);
                newVals.setValues(vals);


                newVals.setMajorDimension("ROWS");


                AppendValuesResponse ap = this.mService.spreadsheets().values().append(spreadsheetsId, "A5", newVals).setValueInputOption("RAW").execute();

            } else {
                results.add(0,"Do not call sheets until 9PM");
                onPostExecute(results);
                callSheetsActive = false;
            }

            return results;
        }


        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.dismiss();
            if (!callSheetsActive) {
                callDialogwithMessage(output.get(0));
                callSheetsActive = true;
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.dismiss();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            TotalsActivity.REQUEST_AUTHORIZATION);
                } else if (mLastError instanceof GoogleAuthIOException) {

                } else {

                }
            } else {

            }
        }
    }


    public void onResume() {
        super.onResume();
    }
}
