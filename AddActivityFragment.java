package com.impactapp.vishnu.timesync;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ButtonBarLayout;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * Created by Vishnu on 8/2/2017.
 */

public class AddActivityFragment extends DialogFragment{
    Spinner mySpinner;
    EditText myText;
    View myView;
    LayoutInflater inflater;
    ArrayAdapter<CharSequence> adapter;

    public interface AddActivityFragmentListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    AddActivityFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        inflater = getActivity().getLayoutInflater();
        myView = inflater.inflate(R.layout.adddialog,null);
        mySpinner = (Spinner) myView.findViewById(R.id.myActList);
        myText = (EditText) myView.findViewById(R.id.ActivityAddET);
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.ListNumb, R.layout.support_simple_spinner_dropdown_item);


        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);

        builder.setTitle("Add New Activity");

        builder.setPositiveButton("  Ok  ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveClick(AddActivityFragment.this);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                mListener.onDialogNegativeClick(AddActivityFragment.this);
            }
        });
        builder.setView(myView);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        /*Button NegBut = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE);
        Button PosBut = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);


        //NegBut.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        NegBut.setBackgroundColor(getResources().getColor(R.color.Red));
        PosBut.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        NegBut.setTextSize((float) 20.0);
        PosBut.setTextSize((float) 20.0);

        NegBut.setTextColor(getResources().getColor(R.color.White));
        PosBut.setTextColor(getResources().getColor(R.color.White));*/
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (AddActivityFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement NoticeDialogListener");
        }
    }

    public String getETxTInput() {
        return ((EditText) myView.findViewById(R.id.ActivityAddET)).getText().toString();
    }

    public String getPriorityInput() {
        return ((Spinner) myView.findViewById(R.id.myActList)).getSelectedItem().toString();
    }


}
