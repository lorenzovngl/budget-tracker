package it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.UsersPrefsController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoRegisters;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.RegistersFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;

public class NewRegisterDialog extends DialogFragment {

    private RegistersFragment mFragment;
    private Dialog mDialog;
    private Context mContext;
    private Const.Registers.Action mAction;
    private String mDatabaseName;

    public NewRegisterDialog(RegistersFragment fragment, String name, Const.Registers.Action action) {
        mFragment = fragment;
        mAction = action;
        mDatabaseName = name;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        mContext = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Aggiungi Registro");
        final LayoutInflater inflater = (LayoutInflater)getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_database, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.databaseName);
        final EditText idEditText = (EditText) view.findViewById(R.id.databaseId);
        final EditText passwordEditText = (EditText) view.findViewById(R.id.databasePassword);
        final EditText passwordRepeatEditText = (EditText) view.findViewById(R.id.databasePasswordRepeat);
        if (mAction.equals(Const.Registers.Action.ADD_LOCAL)){
            view.findViewById(R.id.textDatabaseId).setVisibility(View.GONE);
            idEditText.setVisibility(View.GONE);
            view.findViewById(R.id.textDatabaseName).setVisibility(View.VISIBLE);
            nameEditText.setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.textDatabaseName).setVisibility(View.GONE);
            nameEditText.setVisibility(View.GONE);
            view.findViewById(R.id.textDatabaseId).setVisibility(View.VISIBLE);
            idEditText.setVisibility(View.VISIBLE);
        }
        builder.setView(view);
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        mDialog = builder.create();
        switch (mAction){
            case ADD_LOCAL:
                // Caso in cui si vuole creare un registro in locale
                builder.setTitle("Crea registro");
                mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = nameEditText.getText().toString();
                                String password = passwordEditText.getText().toString();
                                String passwordRepeat = passwordRepeatEditText.getText().toString();
                                Log.i("PSW", password);
                                Log.i("REP_PSW", passwordRepeat);
                                if (name.equals("") || password.equals("") || passwordRepeat.equals("")){
                                    Toast.makeText(mContext, "Riempi bene i campi.", Toast.LENGTH_SHORT).show();
                                } else if (!password.equals(passwordRepeat)){
                                    Toast.makeText(mContext, "La password non coincide.", Toast.LENGTH_SHORT).show();
                                } else if (password.length() > 16){
                                    Toast.makeText(mContext, "La password non deve essere più lunga di 16 caratteri.", Toast.LENGTH_SHORT).show();
                                } else {
                                    // La password deve avere esattamente 16 caratteri
                                    while (password.length() < 16){
                                        password = password.concat("0");
                                    }
                                    Log.i("DB_PATH", FileUtils.DATABASE_PATH + mDatabaseName);
                                    //String path = FileUtils.getPath(mContext, Register.class) + mDatabaseName;
                                    DaoRegisters daoRegisters = new DaoRegisters(Const.DBMode.WRITE, mContext);
                                    daoRegisters.insert(name, password, UsersPrefsController.getUsername(mContext));
                                    mFragment.updateAdapter(mContext);
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                break;
            case ADD_REMOTE:
                // Caso in cui si vuole scaricare un registro condiviso
                builder.setTitle("Aggiungi registro");
                mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String id = idEditText.getText().toString();
                                String password = passwordEditText.getText().toString();
                                String passwordRepeat = passwordRepeatEditText.getText().toString();
                                Log.i("PSW", password);
                                Log.i("REP_PSW", passwordRepeat);
                                if (id.equals("") || password.equals("") || passwordRepeat.equals("")){
                                    Toast.makeText(mContext, "Riempi bene i campi.", Toast.LENGTH_SHORT).show();
                                } else if (!password.equals(passwordRepeat)){
                                    Toast.makeText(mContext, "La password non coincide.", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    ServerController serverController = new ServerController(mContext);
                                    serverController.download(id, password);
                                }
                            }
                        });
                    }
                });
                break;
        }
        return mDialog;
    }
}
