package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.INotificationSideChannel;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.HomeActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.CategoriesListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.CategoriesFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.CategoryDetailFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.EmptyFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.MovementsListFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;

public class ExpensesTabFragment extends CategoriesTabFragment {

    private View mRootView;
    private Fragment mFragment;
    private static MdaoCategories mMdaoCategories;

    // TODO mettere costruttore con parametro in modo da poter scegliere se visualizzare primarie o secondarie

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragment = this;
        mRootView = inflater.inflate(R.layout.fragment_categories_tab, container, false);
        ListView listView = (ListView) mRootView.findViewById(android.R.id.list);
        registerForContextMenu(listView);
        daoCategories = new DaoCategories(Const.DBMode.READ, getContext());
        //categoriesList = daoCategories.getPrimaryGroups(Const.Categories.Type.EXPENSES, Const.Period.CURRENT);
        mMdaoCategories = new MdaoCategories(Const.DBMode.READ, getContext());
        categoriesList = mMdaoCategories.getPrimaryGroups(Const.Categories.Type.EXPENSES, Const.Period.CURRENT);
        if (categoriesList != null){
            adapter = new CategoriesListViewAdapter(getContext(), categoriesList, Const.Categories.Type.EXPENSES, Const.Period.CURRENT);
            setListAdapter(adapter);
        } else {
            getActivity().getSupportFragmentManager().beginTransaction().replace(android.R.id.tabcontent, new EmptyFragment()).commit();
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (categoriesList != null){
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final String clickedDB = ((TextView)view.findViewById(R.id.CLVDatabase)).getText().toString();
                    Log.i("CLICKED DB", clickedDB);
                    String clickedCategory = ((TextView)view.findViewById(R.id.CLVDesc)).getText().toString();
                    final String clickedCategoryId = mMdaoCategories.getId(clickedDB, clickedCategory);
                    categoriesList = mMdaoCategories.getSecondaryGroups(clickedDB, clickedCategory, Const.Categories.Type.EXPENSES, Const.Period.CURRENT);
                    final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    if (categoriesList != null){
                        adapter = new CategoriesListViewAdapter(getContext(), categoriesList, Const.Categories.Type.EXPENSES, Const.Period.CURRENT);
                        setListAdapter(adapter);
                        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String clickedSubCategory = ((TextView)view.findViewById(R.id.CLVDesc)).getText().toString();
                                final String clickedSubCategoryId = mMdaoCategories.getId(clickedDB, clickedSubCategory);
                                // TODO nel premere il tasto back vorrei poter tornare alle categorie secondarie e non alle primarie
                                if (mMdaoCategories.getOneWithParams(clickedDB, clickedSubCategoryId) != null){
                                    fragmentManager.beginTransaction()
                                            .replace(android.R.id.tabhost, new CategoryDetailFragment(clickedDB, clickedSubCategoryId, Const.Categories.Type.EXPENSES))
                                            .hide(mFragment).commit();
                                } else {
                                    DialogUtils.showSimpleDialog(getContext(), "Nessun dato", "Non ci sono movimenti registrati per questa categoria");
                                }
                            }
                        });
                        ((HomeActivity) getActivity()).setDrawerState(false, new CategoriesFragment());
                        CategoriesFragment.toggleTabWidget();
                        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
                        toolbar.getMenu().clear();
                    } else if (mMdaoCategories.getOneWithParams(clickedDB, clickedCategoryId) != null) {
                        fragmentManager.beginTransaction()
                                .replace(android.R.id.tabhost, new CategoryDetailFragment(clickedDB, clickedCategoryId, Const.Categories.Type.EXPENSES))
                                .hide(mFragment).commit();
                        fragmentManager.executePendingTransactions();
                        ((HomeActivity) getActivity()).setDrawerState(false, new CategoriesFragment());
                        CategoriesFragment.toggleTabWidget();
                    } else {
                        DialogUtils.showSimpleDialog(getContext(), "Nessun dato", "Non ci sono movimenti registrati per questa categoria");
                    }
                }
            });
        }
    }

    public static void updateAdapter(){
        adapter.clear();
        if (categoriesList != null){
            categoriesList.clear();
            categoriesList.addAll(mMdaoCategories.getPrimaryGroups(Const.Categories.Type.EXPENSES, Const.Period.CURRENT));
        }
        adapter.notifyDataSetChanged();
    }
}