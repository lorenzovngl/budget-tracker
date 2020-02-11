package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.BackupsListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.builders.BackupBuilder;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Backup;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;

public class BackupsFragment extends ListFragment {

    View rootView;
    List<Backup> bufferItem;
    BackupsListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_report_list, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Backup");
        setHasOptionsMenu(true);
        bufferItem = FileUtils.getFileList(getContext(), Backup.class);
        adapter = new BackupsListViewAdapter(getActivity(), bufferItem);
        setListAdapter(adapter);
        registerForContextMenu(rootView.findViewById(android.R.id.list));
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.backups_menu, menu);
        menu.findItem(R.id.action_add_backup).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new BackupBuilder(getContext());
                adapter.clear();
                bufferItem.clear();
                bufferItem.addAll(FileUtils.getFileList(getContext(), Backup.class));
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = new MenuInflater(getActivity());
        inflater.inflate(R.menu.backups_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String path = ((TextView) info.targetView.findViewById(R.id.CLVPath)).getText().toString();
        switch (item.getItemId()) {
            case R.id.action_delete:
                bufferItem.remove(info.position);
                adapter.notifyDataSetChanged();
                boolean deleted = (new File(path)).delete();
                if (deleted) {
                    Toast.makeText(getActivity(), "File deleted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "File not deleted!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_export:
                FileUtils.copy(getContext(), new File(path), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }
}