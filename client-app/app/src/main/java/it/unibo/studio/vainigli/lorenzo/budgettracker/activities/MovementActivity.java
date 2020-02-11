package it.unibo.studio.vainigli.lorenzo.budgettracker.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Category;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.DateDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.SpinnerUtils;

public class MovementActivity extends AppCompatActivity {

    public static final String ID = "ID";
    public static final String MODE = "MODE";
    public static final int ADD_MODE = 0;
    public static final int EDIT_MODE = 1;

    private int mMode;
    private EditText editTextDate, editTextDesc, editTextAmount, editTextStartDate, editTextEndDate;
    private CheckBox checkBoxPeriodic;
    private Spinner spinnerDatabase, spinnerSrcCat, spinnerDstCat, spinnerPeriodic, spinnerTimes;
    private LinearLayout layoutPeriodic;
    private String mId, newDate, newDesc, mAddress;
    private double mAmount;
    private Button buttonOk;
    private DaoCategories daoCategories;
    private String mDatabaseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movement);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextDesc = (EditText) findViewById(R.id.editTextDesc);
        spinnerDatabase = (Spinner) findViewById(R.id.spinner_database);
        spinnerSrcCat = (Spinner) findViewById(R.id.spinner_src_cat);
        spinnerDstCat = (Spinner) findViewById(R.id.spinner_dst_cat);
        checkBoxPeriodic = (CheckBox) findViewById(R.id.checkbox_periodic);
        layoutPeriodic = (LinearLayout) findViewById(R.id.layout_periodic);
        layoutPeriodic.setVisibility(View.GONE);
        spinnerPeriodic = (Spinner) findViewById(R.id.spinner_period);
        spinnerTimes = (Spinner) findViewById(R.id.spinner_times);
        editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        editTextStartDate = (EditText) findViewById(R.id.editTextStartDate);
        editTextEndDate = (EditText) findViewById(R.id.editTextEndDate);
        buttonOk = (Button) findViewById(R.id.button_active);
        setListeners();
        Bundle extras = getIntent().getExtras();
        ArrayAdapter<String> adapterDatabase = new ArrayAdapter<String>(this, R.layout.spinner_item, FileUtils.getDatabasesList(this));
        spinnerDatabase.setAdapter(adapterDatabase);
        spinnerDatabase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mDatabaseName = spinnerDatabase.getSelectedItem().toString();
                Log.i("DB_NAME", mDatabaseName + ", " + mDatabaseName.equals(Const.DB1_NAME));
                loadCategories();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mDatabaseName = FileUtils.getDatabasesList(this).get(0);
        loadCategories();
        if (extras != null) {
            mMode = extras.getInt(MODE);
            if (mMode == ADD_MODE){
                setTitle(R.string.new_movement);
                buttonOk.setText(R.string.insert);
            } else {
                fillFieldsOnEditMode(extras);
                layoutPeriodic.setVisibility(View.GONE);
                setTitle(R.string.edit_movement);
                buttonOk.setText(R.string.update);
            }
        }
        editTextDesc.requestFocus();
    }

    public void loadCategories(){
        daoCategories = new DaoCategories(mDatabaseName, Const.DBMode.READ, this);
        ArrayAdapter<String> adapterSrcCat = new ArrayAdapter<String>(this, R.layout.spinner_item, daoCategories.getDescriptions(Const.Categories.Role.SOURCES, Const.Categories.Type.ALL, null));
        spinnerSrcCat.setAdapter(adapterSrcCat);
        ArrayAdapter<String> adapterCategories = new ArrayAdapter<String>(this, R.layout.spinner_item, daoCategories.getDescriptions(Const.Categories.Role.DESTINATIONS, Const.Categories.Type.ALL, null));
        spinnerDstCat.setAdapter(adapterCategories);
    }

    private void setListeners(){
        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    DateDialog dialog = new DateDialog((EditText) view);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");
                }
            }
        };
        editTextDate.setOnFocusChangeListener(listener);
        editTextStartDate.setOnFocusChangeListener(listener);
        editTextEndDate.setOnFocusChangeListener(listener);
        checkBoxPeriodic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBoxPeriodic.isChecked()){
                    layoutPeriodic.setVisibility(View.VISIBLE);
                } else {
                    layoutPeriodic.setVisibility(View.GONE);
                }
            }
        });
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
    }

    private void fillFieldsOnEditMode(Bundle extras){
        DaoMovements daoMovements = new DaoMovements(Const.DBMode.READ, this);
        mId = extras.getString(ID);
        Movement movement = daoMovements.getOne(mId);
        editTextDate.setText(movement.getStringDate());
        editTextDesc.setText(movement.getDescription());
        SpinnerUtils.selectByString(spinnerSrcCat, movement.getSrcCategory());
        SpinnerUtils.selectByString(spinnerDstCat, movement.getDstCategory());
        editTextAmount.setText(Double.toString(movement.getAmount()));
        editTextStartDate.setText(movement.getStringStartDate());
        editTextEndDate.setText(movement.getStringEndDate());
    }

    public boolean checkChoosenCategories(){
        Category sourceCat = daoCategories.getOneByDesc(spinnerSrcCat.getSelectedItem().toString());
        Category destinationCat = daoCategories.getOneByDesc(spinnerDstCat.getSelectedItem().toString());
        switch (sourceCat.getType()){
            case Const.Categories.INCOME:
                return destinationCat.getType().equals(Const.Categories.ACCOUNT);
            case Const.Categories.ACCOUNT:
                return destinationCat.getType().equals(Const.Categories.EXPENSE);
            case Const.Categories.EXPENSE:
                return false;
        }
        return false;
    }

    private void saveChanges(){
        DaoMovements daoMovements = new DaoMovements(mDatabaseName, Const.DBMode.WRITE, this);
        boolean allIsOk = true;
        String strAmount = "";
        String srcCat = spinnerSrcCat.getSelectedItem().toString();
        String dstCat = spinnerDstCat.getSelectedItem().toString();
        String periodOfRepeat = spinnerPeriodic.getSelectedItem().toString();
        int times = Integer.valueOf(spinnerTimes.getSelectedItem().toString());
        Const.Period period = null;
        if (periodOfRepeat.equals(getResources().getString(R.string.day))){
            period = Const.Period.DAILY;
        } else if (periodOfRepeat.equals(getResources().getString(R.string.week))){
            period = Const.Period.WEEKLY;
        } else if (periodOfRepeat.equals(getResources().getString(R.string.month))){
            period = Const.Period.MONTHLY;
        } else if (periodOfRepeat.equals(getResources().getString(R.string.year))){
            period = Const.Period.YEARLY;
        }
        if (editTextDate.getText().toString().equals("")){
            allIsOk = false;
        } else {
            newDate = DateUtils.convert(editTextDate.getText().toString(), DateUtils.FORMAT_IT, DateUtils.FORMAT_SQL);
        }
        if (editTextDesc.getText().toString().equals("")){
            allIsOk = false;
        } else {
            newDesc = editTextDesc.getText().toString();
        }
        if (editTextAmount.getText().toString().equals("")){
            allIsOk = false;
        } else {
            strAmount = editTextAmount.getText().toString();
            mAmount = Double.parseDouble(strAmount);
        }
        if (!checkChoosenCategories()){
            DialogUtils.showSimpleDialog(this, "Categorie errate", "Non possono essere registati movimenti con questa configurazione.");
        }
        if (allIsOk) {
            Movement movement = new Movement();
            movement.setDatabase(mDatabaseName);
            movement.setDate(newDate);
            movement.setDescription(newDesc);
            movement.setSrcCategory(srcCat);
            movement.setDstCategory(dstCat);
            movement.setAmount(strAmount);
            if (mMode == ADD_MODE){
                if (checkBoxPeriodic.isChecked()){
                    daoMovements.insertPeriodic(movement, period, times);
                } else {
                    daoMovements.insert(movement);
                }
            } else {
                daoMovements.update(mId, movement);
            }
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            DialogUtils.showSimpleDialog(this, getResources().getString(R.string.missed_data), getResources().getString(R.string.insert_all_data_required));
        }
    }

}
