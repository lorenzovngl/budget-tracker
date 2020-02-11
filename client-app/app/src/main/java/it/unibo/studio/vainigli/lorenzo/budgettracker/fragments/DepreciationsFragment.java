package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.DepreciationListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.ListItem;

public class DepreciationsFragment extends ListFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_depreciations, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Ammortamenti");
        DaoMovements daoMovements = new DaoMovements(Const.DBMode.READ, getContext());
        List<ListItem> buffer = DaoMovements.sectionedByMonth(getContext(), daoMovements.getOnesWithDepreciation(Const.Depreciation.CURRENT));
        if (buffer != null){
            DepreciationListViewAdapter adapter = new DepreciationListViewAdapter(getContext(), buffer, true);
            setListAdapter(adapter);
        } else {
            getActivity().getSupportFragmentManager().beginTransaction().replace(android.R.id.tabcontent, new EmptyFragment()).commit();
        }
        return rootView;
    }
}