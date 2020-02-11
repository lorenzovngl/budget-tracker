package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController;

/**
 * Created by lorenzo on 4/30/16.
 */
public class SyncFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sync, container, false);
        LinearLayout syncLocal = (LinearLayout) rootView.findViewById(R.id.layoutSyncLocal);
        LinearLayout syncRemote = (LinearLayout) rootView.findViewById(R.id.layoutSyncRemote);
        final ServerController serverController = new ServerController(getContext());
        syncLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverController.syncLocal();
            }
        });
        syncRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverController.syncRemote();
            }
        });
        return rootView;
    }
}