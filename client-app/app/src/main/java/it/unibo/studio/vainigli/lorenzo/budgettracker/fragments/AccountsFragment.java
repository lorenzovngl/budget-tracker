package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.HomeActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.AccountsListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.CategoriesListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoAccounts;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.CategoryDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Account;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.AccountDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Category;

public class AccountsFragment extends ListFragment {

    public AccountsFragment mFragment;
    private static DaoCategories daoCategories;
    private static List<Category> categoryList;
    private static CategoriesListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.accounts);
        setHasOptionsMenu(true);
        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        registerForContextMenu(listView);
        daoCategories = new DaoCategories(Const.DBMode.READ, getActivity());
        //categoryList = daoCategories.getPrimaryGroups(Const.Categories.Type.ACCOUNTS, Const.Period.CURRENT);
        MdaoCategories mdaoCategories = new MdaoCategories(Const.DBMode.READ, getContext());
        categoryList = mdaoCategories.getPrimaryGroups(Const.Categories.Type.ACCOUNTS, Const.Period.CURRENT);
        if (categoryList != null) {
            adapter = new CategoriesListViewAdapter(getActivity(), categoryList, Const.Categories.Type.ACCOUNTS, Const.Period.CURRENT);
            setListAdapter(adapter);
        } else {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new EmptyFragment()).commit();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedDB = ((TextView) view.findViewById(R.id.CLVDatabase)).getText().toString();
                String clickedSubCategory = ((TextView) view.findViewById(R.id.CLVDesc)).getText().toString();
                final String clickedSubCategoryId = daoCategories.getId(clickedSubCategory);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new CategoryDetailFragment(clickedDB, clickedSubCategoryId, Const.Categories.Type.ACCOUNTS)).commit();
                        //.hide(mFragment).commit();
                ((HomeActivity) getActivity()).setDrawerState(false, new AccountsFragment());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.accounts_menu, menu);
        menu.findItem(R.id.action_add_acc).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AccountDialog dialog = new AccountDialog(mFragment);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "Accounts");
                return true;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = new MenuInflater(getActivity());
        inflater.inflate(R.menu.account_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String id = ((TextView) info.targetView.findViewById(R.id.CLVId)).getText().toString();
        switch (item.getItemId()) {
            case R.id.action_delete:
                categoryList.remove(info.position);
                adapter.notifyDataSetChanged();
                boolean res = daoCategories.delete(id);
                if (res) {
                    Toast.makeText(getActivity(), "Data deleted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Data not deleted!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_edit:
                Category category = daoCategories.getOne(id);
                //CategoryDialog dialog = new CategoryDialog(mFragment, category);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                //dialog.show(ft, "Account");
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    public static void updateAdapter() {
        adapter.clear();
        categoryList.clear();
        categoryList.addAll(daoCategories.getPrimaryGroups(Const.Categories.Type.ACCOUNTS, Const.Period.CURRENT));
        adapter.notifyDataSetChanged();
    }
}