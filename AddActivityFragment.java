package com.impactapp.vishnu.timesync;

import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * Created by Vishnu on 8/2/2017.
 */

public class AddActivityFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View myView = inflater.inflate(R.layout.adddialog,null);
        Spinner mySpinner = (Spinner) myView.findViewById(R.id.myActList);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.ListNumb, R.layout.support_simple_spinner_dropdown_item);


        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);

        builder.setPositiveButton("  Ok  ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {

            }
        });
        builder.setView(myView);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Button NegBut = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE);
        Button PosBut = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);


        //NegBut.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        NegBut.setBackgroundColor(getResources().getColor(R.color.Red));
        PosBut.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        NegBut.setTextSize((float) 20.0);
        PosBut.setTextSize((float) 20.0);

        NegBut.setTextColor(getResources().getColor(R.color.White));
        PosBut.setTextColor(getResources().getColor(R.color.White));
    }

}
