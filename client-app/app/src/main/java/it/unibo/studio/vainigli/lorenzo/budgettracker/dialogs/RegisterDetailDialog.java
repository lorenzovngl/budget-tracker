package it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.UsersPrefsController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoRegisters;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.RegistersFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;

import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_CONNECTION_ERROR;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_DB_SYNCHRONIZED;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_LOCAL_DB_OUT_OF_DATE;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_REMOTE_DB_NOT_FOUND;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_REMOTE_DB_OUT_OF_DATE;

public class RegisterDetailDialog extends DialogFragment {

    private Context mContext;
    private Register mDatabase;
    private Dialog mDialog;
    private LinearLayout mLayoutConnection, mLayoutConnError, mLayoutSyncOk, mLayoutNeverSync, mLayoutUpload, mLayoutDownload;
    private RegistersFragment mFragment;

    public RegisterDetailDialog(Context context, RegistersFragment fragment, String name) {
        DaoRegisters daoRegisters = new DaoRegisters(Const.DBMode.READ, context);
        mDatabase = daoRegisters.getOne(name, UsersPrefsController.getUsername(context));
        mContext = context;
        mFragment = fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Dettagli database");
        final LayoutInflater inflater = (LayoutInflater)getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_detail_database, null);
        TextView name = (TextView) view.findViewById(R.id.text_view_db_name);
        TextView id = (TextView) view.findViewById(R.id.text_view_db_id);
        TextView remoteId = (TextView) view.findViewById(R.id.text_view_db_remote_id);
        TextView share = (TextView) view.findViewById(R.id.text_view_db_share);
        TextView size = (TextView) view.findViewById(R.id.text_view_db_size);
        TextView lastEdit = (TextView) view.findViewById(R.id.text_view_db_last_edit);
        mLayoutConnection = (LinearLayout) view.findViewById(R.id.layout_connection);
        mLayoutConnError = (LinearLayout) view.findViewById(R.id.layout_connection_error);
        mLayoutSyncOk = (LinearLayout) view.findViewById(R.id.layout_sync_ok);
        mLayoutNeverSync = (LinearLayout) view.findViewById(R.id.layout_never_sync);
        mLayoutUpload = (LinearLayout) view.findViewById(R.id.layout_upload_required);
        mLayoutDownload = (LinearLayout) view.findViewById(R.id.layout_download_required);
        mLayoutNeverSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                ServerController serverController = new ServerController(mContext);
                DaoRegisters daoRegisters = new DaoRegisters(Const.DBMode.READ, mContext);
                Register database = daoRegisters.getOne(mDatabase.getName(), UsersPrefsController.getUsername(mContext));
                serverController.upload(database);
            }
        });
        mLayoutConnError.setVisibility(View.GONE);
        mLayoutSyncOk.setVisibility(View.GONE);
        mLayoutNeverSync.setVisibility(View.GONE);
        mLayoutUpload.setVisibility(View.GONE);
        mLayoutDownload.setVisibility(View.GONE);
        id.setText(mDatabase.getId());
        if (mDatabase.getRemoteId() != null){
            remoteId.setText(mDatabase.getRemoteId());
        } else {
            remoteId.setText("-");
        }
        name.setText(mDatabase.getName());
        if (mDatabase.getSharedNames() != null){
            share.append("condiviso con ");
            for (int i = 0; i<mDatabase.getSharedNames().size(); i++){
                share.append(" " + mDatabase.getSharedNames().get(i));
            }
            share.append(".");
        } else {
            share.append("privato");
        }
        size.setText(NumberUtils.bytesToKB(mDatabase.getSize()));
        lastEdit.setText(DateUtils.longToTimeString(mDatabase.getLastEdit()));
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
            }
        });
        mDialog = builder.create();
        ServerController serverController = new ServerController(mContext);
        serverController.checkDatabase(this, mDatabase);
        return mDialog;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void setResponseView(int responseCode){
        mLayoutConnection.setVisibility(View.GONE);
        mLayoutConnError.setVisibility(View.GONE);
        mLayoutSyncOk.setVisibility(View.GONE);
        mLayoutNeverSync.setVisibility(View.GONE);
        mLayoutUpload.setVisibility(View.GONE);
        mLayoutDownload.setVisibility(View.GONE);
        switch (responseCode){
            case RESPONSE_DB_SYNCHRONIZED:
                mLayoutSyncOk.setVisibility(View.VISIBLE);
                break;
            case RESPONSE_LOCAL_DB_OUT_OF_DATE:
                mLayoutDownload.setVisibility(View.VISIBLE);
                break;
            case RESPONSE_REMOTE_DB_OUT_OF_DATE:
                mLayoutUpload.setVisibility(View.VISIBLE);
                break;
            case RESPONSE_REMOTE_DB_NOT_FOUND:
                mLayoutNeverSync.setVisibility(View.VISIBLE);
                break;
            case RESPONSE_CONNECTION_ERROR:
                mLayoutConnError.setVisibility(View.VISIBLE);
                break;
            default:
                mLayoutConnError.setVisibility(View.VISIBLE);
                break;
        }
    }
}
