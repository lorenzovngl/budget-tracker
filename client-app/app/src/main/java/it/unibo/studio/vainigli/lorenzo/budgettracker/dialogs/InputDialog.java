package it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;

public class InputDialog extends DialogFragment {

    private EditText mEditText;
    private String mTitle, mValue;
    private TextView mView;

    public InputDialog(TextView view, String title) {
        mView = view;
        mTitle = title;
    }

    public InputDialog(TextView view, String title, String value) {
        mView = view;
        mTitle = title;
        mValue = value;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final LayoutInflater inflater = (LayoutInflater)getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_edittext, null);
        mEditText = (EditText) view.findViewById(R.id.editText);
        builder.setTitle(mTitle);
        builder.setView(view);
        if (mValue != null){
            mEditText.setText(mValue);
        }
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                mView.setText(mEditText.getText().toString());
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
