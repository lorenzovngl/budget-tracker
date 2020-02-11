package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.ReportsListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Report;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.builders.ReportBuilder;

public class ReportsFragment extends ListFragment {

    View rootView;
    List<Report> bufferItem;
    ReportsListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_report_list, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.reports);
        bufferItem = FileUtils.getFileList(getContext(), Report.class);
        adapter = new ReportsListViewAdapter(getActivity(), bufferItem);
        setListAdapter(adapter);
        registerForContextMenu(rootView.findViewById(android.R.id.list));
        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = new MenuInflater(getActivity());
        inflater.inflate(R.menu.report_list_context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String path;
        switch (item.getItemId()) {
            case R.id.action_delete:
                bufferItem.remove(info.position);
                adapter.notifyDataSetChanged();
                path = ((TextView) info.targetView.findViewById(R.id.CLVPath)).getText().toString();
                boolean deleted = (new File(path)).delete();
                if (deleted) {
                    Toast.makeText(getActivity(), "File deleted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "File not deleted!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_view:
                path = ((TextView) info.targetView.findViewById(R.id.CLVPath)).getText().toString();
                String name = ((TextView) info.targetView.findViewById(R.id.CLVDesc)).getText().toString();
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
}