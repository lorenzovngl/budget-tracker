package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.RegistersListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.builders.ReportBuilder;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.UsersPrefsController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoRegisters;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.RegisterDetailDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.NewRegisterDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;

public class RegistersFragment extends ListFragment {

    private View rootView;
    private static List<Register> bufferItem;
    private static ArrayAdapter<Register> adapter;
    private RegistersFragment mFragment;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragment = this;
        mContext = getContext();
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Registri");
        setHasOptionsMenu(true);
        DaoRegisters daoRegisters = new DaoRegisters(Const.DBMode.READ, getContext());
        bufferItem = daoRegisters.getAll();
        adapter = new RegistersListViewAdapter(getContext(), bufferItem);
        setListAdapter(adapter);
        registerForContextMenu(rootView.findViewById(android.R.id.list));
        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = new MenuInflater(getActivity());
        inflater.inflate(R.menu.databases_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String path;
        String name = ((TextView) info.targetView.findViewById(R.id.CLVDesc)).getText().toString();
        FragmentTransaction ft;
        switch (item.getItemId()) {
            case R.id.action_details:
                RegisterDetailDialog dialogDetails = new RegisterDetailDialog(mContext, this, name);
                ft = getFragmentManager().beginTransaction();
                dialogDetails.show(ft, null);
                break;
            case R.id.action_delete:
                bufferItem.remove(info.position);
                adapter.notifyDataSetChanged();
                path = ((TextView) info.targetView.findViewById(R.id.CLVPath)).getText().toString();
                boolean deleted = (new File(path)).delete();
                boolean journalExists = (new File(path + "-journal")).exists();
                boolean deletedJournal = (new File(path + "-journal")).delete();
                DaoRegisters daoRegisters = new DaoRegisters(Const.DBMode.WRITE, mContext);
                daoRegisters.delete(name, UsersPrefsController.getUsername(mContext));
                if (deleted && (journalExists || deletedJournal)) {
                    Toast.makeText(getActivity(), "Files deleted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Files not deleted!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_remove:
                path = ((TextView) info.targetView.findViewById(R.id.CLVPath)).getText().toString();
                name = ((TextView) info.targetView.findViewById(R.id.CLVDesc)).getText().toString();
                String dst = ReportBuilder.DOWNLOADS_DIR.getAbsolutePath();
                File copy = FileUtils.copy(getContext(), path, dst, name);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(copy), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.databases_menu, menu);
        menu.findItem(R.id.action_add_database).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NewRegisterDialog dialog = new NewRegisterDialog(mFragment, null, Const.Registers.Action.ADD_REMOTE);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, null);
                return true;
            }
        });
        menu.findItem(R.id.action_new_database).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                NewRegisterDialog dialog = new NewRegisterDialog(mFragment, null, Const.Registers.Action.ADD_LOCAL);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, null);
                /*AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final LayoutInflater inflater = (LayoutInflater)getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_edittext, null);
                final EditText editText = (EditText) view.findViewById(R.id.editText);
                builder.setTitle("Inserisci nome");
                builder.setView(view);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String name = editText.getText().toString();
                        new RegistrersController(mContext, name, UsersPrefsController.getUsername(mContext));
                        updateAdapter();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                    }
                });
                builder.create().show();*/
                return true;
            }
        });
    }

    public static void updateAdapter(Context context){
        adapter.clear();
        if (bufferItem != null){
            bufferItem.clear();
            DaoRegisters daoRegisters = new DaoRegisters(Const.DBMode.READ, context);
            bufferItem.addAll(daoRegisters.getAll());
        }
        adapter.notifyDataSetChanged();
    }

    public void openPasswordDialog(String name){
        NewRegisterDialog dialog = new NewRegisterDialog(mFragment, name, Const.Registers.Action.ADD_LOCAL);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, null);
    }
}