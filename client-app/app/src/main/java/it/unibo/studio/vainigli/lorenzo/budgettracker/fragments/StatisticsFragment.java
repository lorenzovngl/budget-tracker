package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs.StatisticsAllTabFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs.StatisticsPeriodTabFragment;

public class StatisticsFragment extends Fragment {

    private FragmentTabHost mTabHost;
    public static final String ALL_TAB = "allTab";
    public static final String PERIOD_TAB = "periodTab";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabhost, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.statistics);
        setHasOptionsMenu(true);
        FragmentManager fragmentManager = getChildFragmentManager();
        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), fragmentManager, android.R.id.tabcontent);
        StatisticsAllTabFragment allTabFragment = new StatisticsAllTabFragment();
        StatisticsPeriodTabFragment periodTabFragment = new StatisticsPeriodTabFragment();
        mTabHost.addTab(mTabHost.newTabSpec(ALL_TAB)
                .setIndicator("All", null), allTabFragment.getClass(), null);
        mTabHost.addTab(mTabHost.newTabSpec(PERIOD_TAB)
                .setIndicator("Period", null), periodTabFragment.getClass(), null);
        return rootView;
    }
}
