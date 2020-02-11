package it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.IconsActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoAccounts;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Account;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.AccountsFragment;

// TODO da eliminare
public class AccountDialog extends DialogFragment {

    private EditText accountName;
    private ImageView accountIcon;
    private int accountIconId = 0;
    private String accountIconName;
    private AccountsFragment mFragment;
    private Dialog mDialog;
    private Context mContext;
    private Const.Action mAction;
    private Account mAccount;

    public AccountDialog(AccountsFragment fragment) {
        mFragment = fragment;
        mAction = Const.Action.ADD;
    }

    public AccountDialog(AccountsFragment fragment, Account account) {
        mFragment = fragment;
        mAction = Const.Action.EDIT;
        mAccount = account;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        mContext = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_add_account, null);
        accountName = (EditText) view.findViewById(R.id.accountName);
        accountIcon = (ImageView) view.findViewById(R.id.accountIcon);
        accountIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), IconsActivity.class);
                if (accountIconId > 0){
                    intent.putExtra(IconsActivity.CHOOSED_ICON_ID, accountIconId);
                } else if (mAction == Const.Action.EDIT){
                    intent.putExtra(IconsActivity.CHOOSED_ICON_ID, mAccount.getIcon());
                }
                startActivityForResult(intent, 1);
            }
        });
        if (mAction == Const.Action.EDIT){
            builder.setTitle("Modifica Account");
            accountName.setText(mAccount.getDescription());
            accountIcon.setImageResource(mAccount.getIcon());
            accountIconName = mAccount.getIconName();
        } else {
            builder.setTitle("Aggiungi Account");
            accountIcon.setImageResource(R.drawable.ic_add_black);
        }
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
        mDialog = builder.create();
        return mDialog;
    }

    private void saveChanges(){
        DaoAccounts daoAccounts = new DaoAccounts(Const.DBMode.WRITE, mContext);
        boolean result;
        if (mAction == Const.Action.ADD){
            result = daoAccounts.insert(accountName.getText().toString(), accountIconName);
        } else {
            result = daoAccounts.update(mAccount.getId(), accountName.getText().toString(), accountIconName);
        }
        Toast toast;
        if (result){
            if (mAction == Const.Action.ADD){
                toast = Toast.makeText(getContext(), "Deposito inserito", Toast.LENGTH_SHORT);
            } else {
                toast = Toast.makeText(getContext(), "Deposito aggiornato", Toast.LENGTH_SHORT);
            }
            mFragment.updateAdapter();
        } else {
            toast = Toast.makeText(getContext(), "Si è verificato un problema", Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                accountIconName = data.getExtras().getString(IconsActivity.CHOOSED_ICON_NAME);
                accountIconId = data.getExtras().getInt(IconsActivity.CHOOSED_ICON_ID);
                accountIcon.setImageResource(accountIconId);
            }
        }
    }
}
