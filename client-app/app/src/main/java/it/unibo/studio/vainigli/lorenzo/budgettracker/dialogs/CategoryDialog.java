package it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.IconsActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Category;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.CategoriesFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;

public class CategoryDialog extends DialogFragment {

    private EditText categoryName;
    private ImageView categoryIcon;
    private int categoryIconId = 0;
    private String categoryIconName, mDatabaseName;
    private CategoriesFragment mFragment;
    private FragmentTabHost mTabHost;
    private Dialog mDialog;
    private Context mContext;
    private Const.Action mAction;
    private Category mCategory;
    private Spinner categoryParent;
    private LinearLayout layoutParent;
    private RadioButton radioSecondary;

    public CategoryDialog(CategoriesFragment fragment, FragmentTabHost tabHost) {
        mFragment = fragment;
        mTabHost = tabHost;
        mAction = Const.Action.ADD;
        mCategory = new Category();
    }

    public CategoryDialog(CategoriesFragment fragment, FragmentTabHost tabHost, Category category) {
        mFragment = fragment;
        mTabHost = tabHost;
        mAction = Const.Action.EDIT;
        mCategory = category;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        mContext = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Aggiungi Categoria");
        final LayoutInflater inflater = (LayoutInflater)getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_add_category, null);
        categoryName = (EditText) view.findViewById(R.id.categoryName);
        layoutParent = (LinearLayout) view.findViewById(R.id.layout_parent);
        RadioButton radioPrimary = (RadioButton) view.findViewById(R.id.radio_primary);
        radioSecondary = (RadioButton) view.findViewById(R.id.radio_secondary);
        RadioButton radioExpense = (RadioButton) view.findViewById(R.id.radio_expense);
        final RadioButton radioAccount = (RadioButton) view.findViewById(R.id.radio_account);
        RadioButton radioIncome = (RadioButton) view.findViewById(R.id.radio_income);
        final Spinner categoryDatabase = (Spinner) view.findViewById(R.id.categoryDatabase);
        categoryParent = (Spinner) view.findViewById(R.id.categoryParentName);
        categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);
        categoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), IconsActivity.class);
                if (categoryIconId > 0){
                    intent.putExtra(IconsActivity.CHOOSED_ICON_ID, categoryIconId);
                } else if (mAction == Const.Action.EDIT){
                    intent.putExtra(IconsActivity.CHOOSED_ICON_ID, mCategory.getIcon());
                }
                startActivityForResult(intent, 1);
            }
        });
        radioPrimary.setChecked(true);
        layoutParent.setVisibility(View.GONE);
        radioExpense.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mCategory.setType(Const.Categories.EXPENSE);
                    loadPrimaryCategories();
                }
            }
        });
        radioAccount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mCategory.setType(Const.Categories.ACCOUNT);
                    loadPrimaryCategories();
                }
            }
        });
        radioIncome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mCategory.setType(Const.Categories.INCOME);
                    loadPrimaryCategories();
                }
            }
        });
        radioPrimary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    layoutParent.setVisibility(View.GONE);
                }
            }
        });
        radioSecondary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    layoutParent.setVisibility(View.VISIBLE);
                }
            }
        });
        List<String> databases = FileUtils.getDatabasesList(mContext);
        ArrayAdapter<String> adapterDatabases = new ArrayAdapter<String>(mContext, R.layout.spinner_item, databases);
        categoryDatabase.setAdapter(adapterDatabases);
        categoryDatabase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mDatabaseName = categoryDatabase.getSelectedItem().toString();
                Log.i("DB_NAME", mDatabaseName + ", " + mDatabaseName.equals(Const.DB1_NAME));
                loadPrimaryCategories();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (mAction == Const.Action.EDIT){
            builder.setTitle("Modifica Categoria");
            categoryName.setText(mCategory.getDescription());
            categoryIcon.setImageResource(mCategory.getIcon());
            categoryIconName = mCategory.getIconName();
        } else {
            builder.setTitle("Aggiungi Categoria");
            categoryIcon.setImageResource(R.drawable.ic_add_black);
        }
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                saveChanges();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        mDialog = builder.create();
        return mDialog;
    }

    private void loadPrimaryCategories(){
        Const.Categories.Type target = Const.Categories.Type.ALL;
        if (mCategory != null && mCategory.getType() != null){
            switch (mCategory.getType()){
                case Const.Categories.ACCOUNT:
                    target = Const.Categories.Type.ACCOUNTS;
                    break;
                case Const.Categories.EXPENSE:
                    target = Const.Categories.Type.EXPENSES;
                    break;
                case Const.Categories.INCOME:
                    target = Const.Categories.Type.INCOMES;
                    break;
            }
        }
        final DaoCategories daoCategories = new DaoCategories(mDatabaseName, Const.DBMode.READ, mContext);
        List<String> descriptions = daoCategories.getDescriptions(Const.Categories.Hierarchy.PRIMARY, target, null);
        if (descriptions != null){
            ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(mContext, R.layout.spinner_item, descriptions);
            categoryParent.setAdapter(adapterCategories);
            radioSecondary.setEnabled(true);
        } else {
            radioSecondary.setEnabled(false);
            layoutParent.setVisibility(View.GONE);
        }
    }

    private void saveChanges(){
        DaoCategories daoCategories = new DaoCategories(mDatabaseName, Const.DBMode.WRITE, mContext);
        boolean result;
        String parent = categoryParent.getSelectedItem().toString();
        if (mAction == Const.Action.ADD){
            result = daoCategories.insert(categoryName.getText().toString(), categoryIconName, parent, mCategory.getType());
        } else {
            result = daoCategories.update(mCategory.getId(), categoryName.getText().toString(), categoryIconName, parent, mCategory.getType());
        }
        Toast toast;
        if (result){
            if (mAction == Const.Action.ADD){
                toast = Toast.makeText(getContext(), "Categoria inserita", Toast.LENGTH_SHORT);
            } else {
                toast = Toast.makeText(getContext(), "Categoria aggiornata", Toast.LENGTH_SHORT);
            }
            if (mTabHost.getCurrentTabTag().equals(CategoriesFragment.EXPENSES_TAB)){
                mFragment.expensesTabFragment.updateAdapter();
            } else {
                mFragment.incomingsTabFragment.updateAdapter();
            }
        } else {
            toast = Toast.makeText(getContext(), "Si è verificato un problema", Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                categoryIconName = data.getExtras().getString(IconsActivity.CHOOSED_ICON_NAME);
                Log.i("CATNAME", categoryIconName);
                categoryIconId = data.getExtras().getInt(IconsActivity.CHOOSED_ICON_ID);
                categoryIcon.setImageResource(categoryIconId);
            }
        }
    }
}
