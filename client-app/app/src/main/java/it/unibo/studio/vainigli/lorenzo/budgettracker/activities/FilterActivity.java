package it.unibo.studio.vainigli.lorenzo.budgettracker.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Category;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.DateDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.FiltersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.StringUtils;

public class FilterActivity extends AppCompatActivity {

    private Switch databasesSwitch, srcCatSwitch, dstCatSwitch, periodSwitch, amountsSwitch;
    private LinearLayout databasesLayout, srcCatLayout, dstCatLayout, periodLayout, amountsLayout;
    private List<CheckBox> databasesCheckBoxes, srcCatCheckBoxes, dstCatCheckBoxes;
    private String startDate, endDate, startAmount, endAmount;
    private EditText editTextDateStart, editTextDateEnd, editTextAmountStart, editTextAmountEnd;
    private FiltersController filtersController;

    public static final String ACTIVE = "ACTIVE";

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        filtersController = new FiltersController(this);
        editTextDateStart = (EditText) findViewById(R.id.editText_start_date);
        editTextDateEnd = (EditText) findViewById(R.id.editText_end_date);
        editTextAmountStart = (EditText) findViewById(R.id.editText_start_amount);
        editTextAmountEnd = (EditText) findViewById(R.id.editText_end_amount);
        databasesSwitch = (Switch) findViewById(R.id.switch_databases);
        databasesLayout = (LinearLayout) findViewById(R.id.filter_databases_layout);
        srcCatSwitch = (Switch) findViewById(R.id.switch_src_cat);
        srcCatLayout = (LinearLayout) findViewById(R.id.filter_src_cat_layout);
        dstCatSwitch = (Switch) findViewById(R.id.switch_dst_cat);
        dstCatLayout = (LinearLayout) findViewById(R.id.filter_dst_cat_layout);
        periodSwitch = (Switch) findViewById(R.id.switch_period);
        periodLayout = (LinearLayout) findViewById(R.id.filter_period_layout);
        amountsSwitch = (Switch) findViewById(R.id.switch_amount);
        amountsLayout = (LinearLayout) findViewById(R.id.filter_amount_layout);
        setSwitchAndLayout(filtersController.isEnabledDatabases(), databasesSwitch, databasesLayout);
        setSwitchAndLayout(filtersController.isEnabledSrcCategories(), srcCatSwitch, srcCatLayout);
        setSwitchAndLayout(filtersController.isEnabledDstCategories(), dstCatSwitch, dstCatLayout);
        setSwitchAndLayout(filtersController.isEnabledDate(), periodSwitch, periodLayout);
        setSwitchAndLayout(filtersController.isEnabledAmount(), amountsSwitch, amountsLayout);
        displayStoredData();
        setListeners();
    }

    private void setSwitchAndLayout(boolean condition, Switch mswitch, LinearLayout layout){
        if (condition){
            mswitch.setChecked(true);
            layout.setVisibility(View.VISIBLE);
        } else {
            mswitch.setChecked(false);
            layout.setVisibility(View.GONE);
        }
    }

    private void displayStoredData(){
        // Registers
        LinearLayout layout = (LinearLayout) findViewById(R.id.filter_databases_layout);
        List<Register> databases = FileUtils.getDatabases(this);
        Set<String> activeDatabases = filtersController.getDatabases();
        Log.i("ACTIVE DB", StringUtils.printStringList(StringUtils.setToList(activeDatabases)));
        databasesCheckBoxes = new ArrayList<>();
        if (databases != null){
            for(Register database : databases){
                Log.i("DB", database.getName());
                View view = addCheckBox(database.getName(), R.drawable.ic_database);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                databasesCheckBoxes.add(checkBox);
                if (activeDatabases.contains(database.getName())){
                    checkBox.setChecked(true);
                }
                layout.addView(view);
            }
            Log.i("DB CHECKBOXES", Integer.toString(databasesCheckBoxes.size()));
        } else {
            TextView textView = new TextView(this);
            textView.setText("Nessun registro");
            layout.addView(textView);
        }
        // Source categories
        layout = (LinearLayout) findViewById(R.id.filter_src_cat_layout);
        DaoCategories daoCategories = new DaoCategories(Const.DB1_NAME, Const.DBMode.READ, this);
        List<Category> sourceCategories = daoCategories.getAll(Const.Categories.Role.SOURCES);
        Set<String> activeCategories = filtersController.getSrcCategories();
        srcCatCheckBoxes = new ArrayList<>();
        if (sourceCategories != null){
            for(Category category : sourceCategories){
                View view = addCheckBox(category.getDescription(), category.getIcon());
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                srcCatCheckBoxes.add(checkBox);
                if (activeCategories.contains(category.getDescription())){
                    checkBox.setChecked(true);
                }
                layout.addView(view);
            }
        } else {
            TextView textView = new TextView(this);
            textView.setText(R.string.no_categories);
            layout.addView(textView);
        }
        // Destination Categories
        layout = (LinearLayout) findViewById(R.id.filter_dst_cat_layout);
        List<Category> destinationCategories = daoCategories.getAll(Const.Categories.Role.DESTINATIONS);
        activeCategories = filtersController.getDstCategories();
        dstCatCheckBoxes = new ArrayList<>();
        if (destinationCategories != null){
            for(Category category : destinationCategories){
                View view = addCheckBox(category.getDescription(), category.getIcon());
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                dstCatCheckBoxes.add(checkBox);
                if (activeCategories.contains(category.getDescription())){
                    checkBox.setChecked(true);
                }
                layout.addView(view);
            }
        } else {
            TextView textView = new TextView(this);
            textView.setText(R.string.no_categories);
            layout.addView(textView);
        }
        // Date
        editTextDateStart.setText(filtersController.getStartDate());
        editTextDateEnd.setText(filtersController.getEndDate());
        // Amount
        editTextAmountStart.setText(NumberUtils.setDecimals(filtersController.getStartAmount(), 0));
        editTextAmountEnd.setText(NumberUtils.setDecimals(filtersController.getEndAmount(), 0));
    }

    private View addCheckBox(String text, int icon){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_checkbox, null);
        TextView textView = (TextView) view.findViewById(R.id.label);
        textView.setText(text);
        ImageView imageView = (ImageView) view.findViewById(R.id.categoryIcon);
        imageView.setImageResource(icon);
        return view;
    }

    private String getCheckBoxText(CheckBox checkBox){
        View view = (View) checkBox.getParent();
        TextView textView = (TextView) view.findViewById(R.id.label);
        return textView.getText().toString();
    }

    private void updateData(){
        if (databasesSwitch.isChecked()){
            filtersController.enableDatabases();
            // Registers
            List<String> checkedDatabases = new ArrayList<>();
            for(CheckBox checkBox : databasesCheckBoxes){
                if (checkBox.isChecked()){
                    Log.i("Checked", checkBox.getText().toString());
                    checkedDatabases.add(getCheckBoxText(checkBox));
                }
            }
            filtersController.setDatabases(new HashSet<>(checkedDatabases));
        } else {
            filtersController.disableDatabases();
        }
        if (srcCatSwitch.isChecked()){
            filtersController.enableSrcCategories();
            // Source Categories
            List<String> checkedSrcCategories = new ArrayList<>();
            for(CheckBox checkBox : srcCatCheckBoxes){
                if (checkBox.isChecked()){
                    Log.i("Checked:", checkBox.getText().toString());
                    checkedSrcCategories.add(getCheckBoxText(checkBox));
                }
            }
            filtersController.setSrcCategories(new HashSet<>(checkedSrcCategories));
        } else {
            filtersController.disableSrcCategories();
        }
        if (dstCatSwitch.isChecked()){
            filtersController.enableDstCategories();
            // Destination Categories
            List<String> checkedDstCategories = new ArrayList<>();
            for(CheckBox checkBox : dstCatCheckBoxes){
                if (checkBox.isChecked()){
                    Log.i("Checked:", checkBox.getText().toString());
                    checkedDstCategories.add(getCheckBoxText(checkBox));
                }
            }
            filtersController.setDstCategories(new HashSet<>(checkedDstCategories));
        } else {
            filtersController.disableDstCategories();
        }
        if (periodSwitch.isChecked()){
            filtersController.enableDate();
            // Date
            startDate = editTextDateStart.getText().toString();
            if (startDate != null && !startDate.equals("")) {
                filtersController.setStartDate(startDate);
            }
            endDate = editTextDateEnd.getText().toString();
            if (endDate != null && !endDate.equals("")) {
                filtersController.setEndDate(endDate);
            }
        } else {
            filtersController.disableDate();
        }
        if (amountsSwitch.isChecked()){
            filtersController.enableAmount();
            // Amount
            startAmount = editTextAmountStart.getText().toString();
            if (startAmount != null && !startAmount.equals("")) {
                filtersController.setStartAmount(Double.parseDouble(startAmount));
            }
            endAmount = editTextAmountEnd.getText().toString();
            if (endAmount != null && !endAmount.equals("")) {
                filtersController.setEndAmount(Double.parseDouble(endAmount));
            }
        } else {
            filtersController.disableAmount();
        }
    }

    private void setListeners(){
        databasesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    databasesLayout.setVisibility(View.VISIBLE);
                } else {
                    databasesLayout.setVisibility(View.GONE);
                }
            }
        });
        srcCatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    srcCatLayout.setVisibility(View.VISIBLE);
                } else {
                    srcCatLayout.setVisibility(View.GONE);
                }
            }
        });
        dstCatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dstCatLayout.setVisibility(View.VISIBLE);
                } else {
                    dstCatLayout.setVisibility(View.GONE);
                }
            }
        });
        periodSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    periodLayout.setVisibility(View.VISIBLE);
                } else {
                    periodLayout.setVisibility(View.GONE);
                }
            }
        });
        amountsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    amountsLayout.setVisibility(View.VISIBLE);
                } else {
                    amountsLayout.setVisibility(View.GONE);
                }
            }
        });
        final View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if (hasFocus){
                    DateDialog dialog = new DateDialog((EditText) v);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }
        };
        final View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog((EditText) v);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
                // Imposto qua il listener per evitare che in datedialog si apra quando c'è l'autofocus
                v.setOnFocusChangeListener(focusChangeListener);
            }
        };
        editTextDateStart.setOnClickListener(clickListener);
        disableKeyboard(editTextDateStart);
        editTextDateEnd.setOnClickListener(clickListener);
        disableKeyboard(editTextDateEnd);
    }

    public void disableKeyboard(EditText view){
        try {
            view.setShowSoftInputOnFocus(false);
        } catch (Exception e){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                updateData();
                Intent returnIntent = new Intent();
                returnIntent.putExtra(ACTIVE, filtersController.isEnabled());
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}