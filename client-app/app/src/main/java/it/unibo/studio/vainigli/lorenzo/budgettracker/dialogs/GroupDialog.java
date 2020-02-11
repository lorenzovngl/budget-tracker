package it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.Date;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs.StatisticsPeriodTabFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;

public class GroupDialog extends DialogFragment {

    private RadioButton radioDaily, radioWeekly, radioMonthly, radioYearly;
    private EditText editTextStartDate, editTextEndDate;
    private StatisticsPeriodTabFragment mFragment;
    private Dialog mDialog;

    public GroupDialog(StatisticsPeriodTabFragment fragment) {
        mFragment = fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final LayoutInflater inflater = (LayoutInflater)getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_group, null);
        builder.setTitle("Raggruppa Movimenti");
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                saveChanges();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        editTextStartDate = (EditText) view.findViewById(R.id.editTextStartDate);
        editTextEndDate = (EditText) view.findViewById(R.id.editTextEndDate);
        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    DateDialog dialog = new DateDialog((EditText) view);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }
        };
        editTextStartDate.setOnFocusChangeListener(listener);
        editTextEndDate.setOnFocusChangeListener(listener);
        mDialog = builder.create();
        return mDialog;
    }

    private void saveChanges(){
        radioDaily = (RadioButton) mDialog.findViewById(R.id.radioDaily);
        radioWeekly = (RadioButton) mDialog.findViewById(R.id.radioWeekly);
        radioMonthly = (RadioButton) mDialog.findViewById(R.id.radioMonthly);
        radioYearly = (RadioButton) mDialog.findViewById(R.id.radioYearly);
        if (editTextStartDate.getText().toString().equals("") || editTextStartDate.getText().toString().equals("")){
            if(radioDaily.isChecked()){
                mFragment.setGroupedMode(Const.GroupingMode.DAY, null, null);
            } else if(radioWeekly.isChecked()){
                mFragment.setGroupedMode(Const.GroupingMode.WEEK, null, null);
            } else if(radioMonthly.isChecked()){
                mFragment.setGroupedMode(Const.GroupingMode.MONTH, null, null);
            } else if(radioYearly.isChecked()){
                mFragment.setGroupedMode(Const.GroupingMode.YEAR, null, null);
            }
        } else {
            Log.i("DATE", "SETTATE!");
            Date startDate = DateUtils.stringToDate(editTextStartDate.getText().toString(), DateUtils.FORMAT_IT);
            Date endDate = DateUtils.stringToDate(editTextEndDate.getText().toString(), DateUtils.FORMAT_IT);
            if(radioDaily.isChecked()){
                mFragment.setGroupedMode(Const.GroupingMode.DAY, startDate, endDate);
            } else if(radioWeekly.isChecked()){
                mFragment.setGroupedMode(Const.GroupingMode.WEEK, startDate, endDate);
            } else if(radioMonthly.isChecked()){
                mFragment.setGroupedMode(Const.GroupingMode.MONTH, startDate, endDate);
            } else if(radioYearly.isChecked()){
                mFragment.setGroupedMode(Const.GroupingMode.YEAR, startDate, endDate);
            }
        }
    }
}
