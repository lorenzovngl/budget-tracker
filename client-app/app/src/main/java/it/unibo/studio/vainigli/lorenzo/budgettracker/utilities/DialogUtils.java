package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.RegisterActivity;

public class DialogUtils {

    public static void showSimpleDialog(Context context, String title, String msg){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void showRegistratioOKDialog(Context context, final RegisterActivity registerActivity, String title, String msg){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        registerActivity.finish();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static ProgressDialog showLoadingDialog(Context context, String title, String msg){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.show();
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static ProgressDialog showProgessDialog(Context context, String title, String msg){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    // TODO inutile
    public static void dismissDialog(AlertDialog dialog){
        dialog.dismiss();
    }

}
