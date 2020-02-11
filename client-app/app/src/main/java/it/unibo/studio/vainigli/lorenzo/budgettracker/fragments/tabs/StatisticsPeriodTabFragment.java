package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Date;
import java.util.List;
import java.util.Map;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.MovementsGroupedListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCalendar;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.GroupDialog;

public class StatisticsPeriodTabFragment extends ListFragment {

    private StatisticsPeriodTabFragment mFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragment = this;
        View rootView = inflater.inflate(R.layout.fragment_categories_tab, container, false);
        setHasOptionsMenu(true);
        setGroupedMode(Const.GroupingMode.MONTH, null, null);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.statistics_period_menu, menu);
        menu.findItem(R.id.action_group).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final GroupDialog groupDialog = new GroupDialog(mFragment);
                FragmentTransaction fm = getFragmentManager().beginTransaction();
                groupDialog.show(fm, "Raggruppa Movimenti");
                return true;
            }
        });
    }

    public boolean setGroupedMode(Const.GroupingMode mode, Date startDate, Date endDate) {
        LinearLayout layout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.fragment_empty, null);
        //PinnedSectionListView listView = (PinnedSectionListView) rootView.findViewById(android.R.id.list);
        DaoMovements daoMovements = new DaoMovements(Const.DBMode.READ, getContext());
        List<Map.Entry<String, Double>> buffer = daoMovements.getGrouped(mode, startDate, endDate);
        if (buffer == null || buffer.size() == 0) {
            //listView.setVisibility(View.GONE);
            //((ViewGroup) rootView).addView(layout);
            return false;
        } else {
            //((ViewGroup) rootView).removeView(layout);
            //listView.setVisibility(View.VISIBLE);
            setListAdapter(new MovementsGroupedListViewAdapter(getActivity(), buffer, false));
            return true;
        }
    }
}
