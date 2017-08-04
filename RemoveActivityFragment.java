package com.impactapp.vishnu.timesync;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Vishnu on 8/4/2017.
 */

public class RemoveActivityFragment extends DialogFragment {

    LayoutInflater mInflater;
    View mView;
    Spinner mSpinner;
    private ArrayList<String> ActivityList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;

    public interface RemoveActivityFragmentListener {
        void onRemoveActivityDialogPositiveClick(DialogFragment dialog);
        void onRemoveActivityDialogNegativeClick(DialogFragment dialog);
    }

    RemoveActivityFragmentListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle arguments = getArguments();

        mInflater = getActivity().getLayoutInflater();
        mView = mInflater.inflate(R.layout.remove_dialog,null);
        mSpinner = (Spinner) mView.findViewById(R.id.Rmspinner);

        int limit = arguments.getInt("Size");
        for(int i = 0;i<limit;i++) {
            ActivityList.add(arguments.getString(Integer.toString(i)));
        }

        mAdapter = new ArrayAdapter(getActivity(),R.layout.support_simple_spinner_dropdown_item,ActivityList);
        mAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        mSpinner.setPrompt("Select Activity");
        mSpinner.setAdapter(mAdapter);

        builder.setTitle("Remove Activity");

        builder.setPositiveButton("  Ok  ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                mListener.onRemoveActivityDialogPositiveClick(RemoveActivityFragment.this);
            }
        });

        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,int which) {
                mListener.onRemoveActivityDialogNegativeClick(RemoveActivityFragment.this);
            }
        });


        builder.setView(mView);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (RemoveActivityFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement NoticeDialogListener");
        }
    }


    public String getSelectedActivity() {
        return ((Spinner) mView.findViewById(R.id.Rmspinner)).getSelectedItem().toString();
    }

}
