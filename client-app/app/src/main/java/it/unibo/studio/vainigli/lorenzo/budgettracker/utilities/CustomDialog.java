package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public class CustomDialog {

    public static View setContentView(Activity activity, AlertDialog alertDialog, int layout){
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(layout, null);
        alertDialog.setView(dialogView);
        return dialogView;
    }
}
