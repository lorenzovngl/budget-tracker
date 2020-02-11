package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.CategoryDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs.ExpensesTabFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs.IncomingsTabFragment;

public class CategoriesFragment extends Fragment {

    public static CategoriesFragment mFragment;
    public static FragmentTabHost mTabHost;
    private static View mRootView;
    public ExpensesTabFragment expensesTabFragment;
    public IncomingsTabFragment incomingsTabFragment;
    public static final String EXPENSES_TAB = "expensesTab";
    public static final String INCOMINGS_TAB = "incomingsTab";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragment = this;
        mRootView = inflater.inflate(R.layout.fragment_categories, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.categories);
        setHasOptionsMenu(true);
        FragmentManager fragmentManager = getChildFragmentManager();
        mTabHost = (FragmentTabHost) mRootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), fragmentManager, android.R.id.tabcontent);
        expensesTabFragment = new ExpensesTabFragment();
        incomingsTabFragment = new IncomingsTabFragment();
        mTabHost.addTab(mTabHost.newTabSpec(EXPENSES_TAB).setIndicator(getContext().getResources().getString(R.string.expenses), null), expensesTabFragment.getClass(), null);
        mTabHost.addTab(mTabHost.newTabSpec(INCOMINGS_TAB).setIndicator(getContext().getResources().getString(R.string.incomings), null), incomingsTabFragment.getClass(), null);
        return mRootView;
    }

    public static void toggleTabWidget(){
        TabWidget tabWidget = (TabWidget) mRootView.findViewById(android.R.id.tabs);
        if (tabWidget.isShown()){
            tabWidget.setVisibility(View.GONE);
        } else {
            tabWidget.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.categories_menu, menu);
        menu.findItem(R.id.action_add_cat).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CategoryDialog dialog = new CategoryDialog(mFragment, mTabHost);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "Categories");
                return true;
            }
        });
    }
}