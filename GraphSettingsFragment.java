package com.impactapp.vishnu.timesync;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by devar on 8/18/2017.
 */

public class GraphSettingsFragment extends DialogFragment {
    Spinner DataTitleSpinner;
    LayoutInflater inflater;
    View mView;
    CheckBox mBox;
    ArrayAdapter<String> Dataadapter;
    ArrayList<String> ActTitles = new ArrayList<>();
    SharedPreferences sharedPref;

    public interface GraphSettingsFragmentListener{
        void onGraphSettingsPositiveClick(DialogFragment dialog);
        void onGraphSettingsNegativeClick(DialogFragment dialog);
    }

    GraphSettingsFragmentListener mListener;


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.graph_settings_frag,null);

        sharedPref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.SharedPref), Context.MODE_PRIVATE);
        DataTitleSpinner = (Spinner) mView.findViewById(R.id.GSspinner);
        mBox = (CheckBox) mView.findViewById(R.id.MulPlots);

        LoadTitleDataIntoArrayList();
        Dataadapter = new ArrayAdapter<>(getActivity(),R.layout.support_simple_spinner_dropdown_item,ActTitles);
        Dataadapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        DataTitleSpinner.setAdapter(Dataadapter);

        builder.setTitle("Settings");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onGraphSettingsPositiveClick(GraphSettingsFragment.this);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onGraphSettingsNegativeClick(GraphSettingsFragment.this);
            }
        });
        builder.setView(mView);
        return builder.create();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    public void LoadTitleDataIntoArrayList() {
        int limit =  sharedPref.getInt(getString(R.string.TotalNumberActivity),0);
        for (int i = 0; i< limit;i++) {
            ActTitles.add(sharedPref.getString(Integer.toString(i),"NULL"));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (GraphSettingsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement NoticeDialogListener");
        }
    }

    public String getSelectedAct() {
        return ((Spinner) mView.findViewById(R.id.GSspinner)).getSelectedItem().toString();
    }

    public Boolean isBoxChecked() {
        return ((CheckBox) mView.findViewById(R.id.MulPlots)).isChecked();
    }
}
