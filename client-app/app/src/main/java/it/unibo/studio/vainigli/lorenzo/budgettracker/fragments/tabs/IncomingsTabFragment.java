package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.CategoriesListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.EmptyFragment;

public class IncomingsTabFragment extends CategoriesTabFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories_tab, container, false);
        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        registerForContextMenu(listView);
        daoCategories = new DaoCategories(Const.DBMode.READ, getContext());
        categoriesList = daoCategories.getPrimaryGroups(Const.Categories.Type.INCOMES, Const.Period.CURRENT);
        if (categoriesList != null){
            adapter = new CategoriesListViewAdapter(getContext(), categoriesList, Const.Categories.Type.INCOMES, Const.Period.CURRENT);
            setListAdapter(adapter);
        } else {
            getActivity().getSupportFragmentManager().beginTransaction().replace(android.R.id.tabcontent, new EmptyFragment()).commit();
        }
        return rootView;
    }

    public static void updateAdapter(){
        adapter.clear();
        categoriesList.clear();
        categoriesList.addAll(daoCategories.getPrimaryGroups(Const.Categories.Type.INCOMES, Const.Period.CURRENT));
        adapter.notifyDataSetChanged();
    }
}