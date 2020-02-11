package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.views.PinnedSectionListView;

import java.util.Date;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.MovementActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.FilterActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.MovementsGroupedListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.FiltersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.RegistrersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.OrderDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.adapters.MovementListViewAdapter;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.ListItem;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.builders.ReportBuilder;

public class MovementsListFragment extends ListFragment {

    private static RegistrersController registrersController;
    private PinnedSectionListView listView;
    private List<ListItem> bufferItem;
    private List<Movement> mBufferMovements;
    private MovementListViewAdapter adapter;
    private MovementsGroupedListViewAdapter groupedAdapter;
    private View rootView;
    private String mDatabase, mCategoryId;
    private boolean adapterLoaded;
    private FrameLayout mEmptyLayout;

    public static boolean PINNED = true;
    public static boolean UNPINNED = false;
    private int ACTIVITY_FILTERS = 1;
    private int ACTIVITY_DETAILS = 2;

    public MovementsListFragment(String databaseName, String categoryId){
        mDatabase = databaseName;
        mCategoryId = categoryId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        rootView = inflater.inflate(R.layout.fragment_movements_list, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.movements);
        listView = (PinnedSectionListView) rootView.findViewById(android.R.id.list);
        registrersController = new RegistrersController(getActivity());
        registerForContextMenu(listView);
        setHasOptionsMenu(true);
        listView.setShadowVisible(false);
        adapterLoaded = loadAdapter();
        // TODO valutare se aggiungere un totale e un indicatore di impatto giornaliero
        // Aggiunge il footer alla listview
        /*footerView = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_statistics_all, null, false);
        listView.addFooterView(footerView);
        setFooter(mCursor);*/
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (adapterLoaded){
            focusOnNextMovement();
        }
    }

    private void focusOnNextMovement() {
        boolean focused = false;
        int count = listView.getAdapter().getCount();
        Date today = new Date();
        int i = 0;
        while (!focused && i < count) {
            ListItem item = (ListItem) listView.getItemAtPosition(i);
            if (item.type == ListItem.ITEM) {
                Date date = DateUtils.stringToDate(item.movement.getStringDate(), DateUtils.FORMAT_IT);
                if (date.before(today)){
                    i++;
                } else {
                    focused = true;
                }
            } else {
                i++;
            }
        }
        getListView().setSelection(i);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = new MenuInflater(getActivity());
        inflater.inflate(R.menu.movement_list_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete:
                bufferItem.remove(info.position);
                adapter.notifyDataSetChanged();
                String id = ((TextView) info.targetView.findViewById(R.id.CLVid)).getText().toString();
                String database = ((TextView) info.targetView.findViewById(R.id.CLVDatabase)).getText().toString();
                DaoMovements daoMovements = new DaoMovements(database, Const.DBMode.WRITE, getContext());
                if (daoMovements.delete(id)) {
                    Toast.makeText(getActivity(), "Data deleted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Data not deleted!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.action_details:
                //setDialog(info.targetView);
                Intent intent = new Intent(getActivity(), MovementActivity.class);
                intent.putExtra(MovementActivity.MODE, MovementActivity.EDIT_MODE);
                TextView textId = (TextView) info.targetView.findViewById(R.id.CLVid);
                intent.putExtra(MovementActivity.ID, textId.getText().toString());
                startActivityForResult(intent, ACTIVITY_DETAILS);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (loadAdapter()){
            focusOnNextMovement();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.movement_list_menu, menu);
        menu.findItem(R.id.action_add).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), MovementActivity.class);
                intent.putExtra(MovementActivity.MODE, MovementActivity.ADD_MODE);
                startActivityForResult(intent, ACTIVITY_DETAILS);
                return true;
            }
        });
        final Drawable filter = getResources().getDrawable(R.drawable.ic_filter);
        final Drawable filterOutline = getResources().getDrawable(R.drawable.ic_filter_outline);
        final FiltersController filtersController = new FiltersController(getContext());
        MenuItem filterItem = menu.findItem(R.id.action_filter);
        if (filtersController.isEnabled()){
            filterItem.setIcon(filter);
        } else {
            filterItem.setIcon(filterOutline);
        }
        menu.findItem(R.id.action_filter).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getIcon().getConstantState().equals(filter.getConstantState())){
                    item.setIcon(filterOutline);
                    filtersController.disable();
                } else {
                    item.setIcon(filter);
                    filtersController.enable();
                }
                loadAdapter();
                focusOnNextMovement();
                return true;
            }
        });
        menu.findItem(R.id.action_manage_filters).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), FilterActivity.class);
                startActivityForResult(intent, ACTIVITY_FILTERS);
                return true;
            }
        });
        final MovementsListFragment fragment = this;
        menu.findItem(R.id.action_order).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final OrderDialog orderDialog = new OrderDialog(fragment);
                FragmentTransaction fm = getFragmentManager().beginTransaction();
                orderDialog.show(fm, "Ordina Movimenti");
                return true;
            }
        });
        menu.findItem(R.id.action_export).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new ReportBuilder(getContext());
                return true;
            }
        });
        if (mCategoryId != null){
            menu.findItem(R.id.action_group).setVisible(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_FILTERS) {
            if (loadAdapter()){
                focusOnNextMovement();
            }
        } else if (requestCode == ACTIVITY_DETAILS) {
            if(resultCode == Activity.RESULT_OK){
                if (loadAdapter()){
                    focusOnNextMovement();
                }
            }
        }
    }

    public boolean loadAdapter() {
        MdaoMovements mdaoMovements = new MdaoMovements(Const.DBMode.READ, getContext());
        if (mDatabase != null && mCategoryId != null){
            mBufferMovements = mdaoMovements.getByCategories(mDatabase, new String[] {mCategoryId});
        } else {
            mBufferMovements = mdaoMovements.getFilteredData(null, Const.Movements.COL_DATE, Const.ORDER_ASC);
        }
        return loadAdapter(mBufferMovements, PINNED);
    }

    public boolean loadAdapter(List<Movement> buffer, boolean pinned) {
        bufferItem = DaoMovements.sectionedByMonth(getContext(), buffer);
        ViewGroup viewGroup = (ViewGroup) rootView;
        PinnedSectionListView listView = (PinnedSectionListView) rootView.findViewById(android.R.id.list);
        if (bufferItem == null || bufferItem.size() == 0) {
            listView.setVisibility(View.GONE);
            // TODO not working
            if (mEmptyLayout == null){
                mEmptyLayout = (FrameLayout) getActivity().getLayoutInflater().inflate(R.layout.fragment_empty, null);
                viewGroup.addView(mEmptyLayout);
            }
            return false;
        } else {
            viewGroup.removeView(mEmptyLayout);
            mEmptyLayout = null;
            listView.setVisibility(View.VISIBLE);
            adapter = new MovementListViewAdapter(getActivity(), bufferItem, pinned);
            setListAdapter(adapter);
            return true;
        }
    }
}
