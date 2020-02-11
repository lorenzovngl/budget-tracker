package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.HomeActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.CategoriesListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.MovementsListFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Category;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.CategoryDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.CategoriesFragment;

/**
 * Created by lorenzo on 4/30/16.
 */
public class CategoriesTabFragment extends ListFragment {

    protected static List<Category> categoriesList;
    protected static CategoriesListViewAdapter adapter;
    protected static DaoCategories daoCategories;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = new MenuInflater(getActivity());
        inflater.inflate(R.menu.account_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String database = ((TextView) info.targetView.findViewById(R.id.CLVDatabase)).getText().toString();
        String id = ((TextView) info.targetView.findViewById(R.id.CLVId)).getText().toString();
        String description = ((TextView) info.targetView.findViewById(R.id.CLVDesc)).getText().toString();
        daoCategories = new DaoCategories(database, Const.DBMode.WRITE, getContext());
        switch (item.getItemId()) {
            case R.id.action_mov_list:
                categoriesList = daoCategories.getSecondaryGroups(description, Const.Categories.Type.EXPENSES, Const.Period.CURRENT);
                if (categoriesList != null) {
                    adapter = new CategoriesListViewAdapter(getContext(), categoriesList, Const.Categories.Type.EXPENSES, Const.Period.CURRENT);
                    setListAdapter(adapter);
                } else {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    // TODO nel premere il tasto back vorrei poter tornare alle categorie secondarie e non alle primarie
                    fragmentManager.beginTransaction()
                            .replace(android.R.id.tabhost, new MovementsListFragment(null, id))
                            .hide(this).commit();
                    fragmentManager.executePendingTransactions();
                }
                ((HomeActivity) getActivity()).setDrawerState(false, new CategoriesFragment());
                CategoriesFragment.toggleTabWidget();
                break;
            case R.id.action_delete:
                categoriesList.remove(info.position);
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
                CategoryDialog dialog = new CategoryDialog(CategoriesFragment.mFragment, CategoriesFragment.mTabHost, category);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "Categories");
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }
}