package it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    //EditText mTxtDate;
    View mTxtDate;
    String mDate;
    int mYear, mMonth, mDay;

    /*public DateDialog(EditText editTextDate) {
        mTxtDate = editTextDate;
        if (!editTextDate.getText().toString().equals("")){
            mDate = editTextDate.getText().toString();
            String[] values = mDate.split("\\/");
            mDay = Integer.parseInt(values[0]);
            mMonth = Integer.parseInt(values[1])-1;
            mYear = Integer.parseInt(values[2]);
        } else {
            Calendar calendar = Calendar.getInstance();
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mMonth = calendar.get(Calendar.MONTHLY);
            mYear = calendar.get(Calendar.YEAR);
        }
    }*/

    public DateDialog(EditText editTextDate) {
        mTxtDate = editTextDate;
        if (!editTextDate.getText().toString().equals("")){
            mDate = editTextDate.getText().toString();
            String[] values = mDate.split("\\/");
            mDay = Integer.parseInt(values[0]);
            mMonth = Integer.parseInt(values[1])-1;
            mYear = Integer.parseInt(values[2]);
        } else {
            Calendar calendar = Calendar.getInstance();
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mMonth = calendar.get(Calendar.MONTH);
            mYear = calendar.get(Calendar.YEAR);
        }
    }

    public DateDialog(TextView textViewDate) {
        mTxtDate = textViewDate;
        if (!textViewDate.getText().toString().equals("")){
            mDate = textViewDate.getText().toString();
            String[] values = mDate.split("\\/");
            mDay = Integer.parseInt(values[0]);
            mMonth = Integer.parseInt(values[1])-1;
            mYear = Integer.parseInt(values[2]);
        } else {
            Calendar calendar = Calendar.getInstance();
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mMonth = calendar.get(Calendar.MONTH);
            mYear = calendar.get(Calendar.YEAR);
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
    }

    public void onDateSet(DatePicker view, int year, int month, int day){
        int tmpMonth = month+1;
        String date = day + "/" + tmpMonth + "/" + year;
        if (mTxtDate instanceof EditText){
            ((EditText)mTxtDate).setText(date);
        } else if (mTxtDate instanceof TextView){
            ((TextView)mTxtDate).setText(date);
        }
    }
}
