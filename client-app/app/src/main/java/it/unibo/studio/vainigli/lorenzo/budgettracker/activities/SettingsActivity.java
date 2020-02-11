package it.unibo.studio.vainigli.lorenzo.budgettracker.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;

public class SettingsActivity extends AppCompatActivity {

    RadioGroup radioGroupPeriod;
    EditText globalStartDate;
    LinearLayout layoutRelativePeriod;

    /* ELENCO IMPOSTAZIONI :
    - Scegli periodo
    - Login ad ogni apertura
    - Cifra i database in locale
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        radioGroupPeriod = (RadioGroup) findViewById(R.id.radioGroupPeriod);
        globalStartDate = (EditText) findViewById(R.id.globalStartDate);
        layoutRelativePeriod = (LinearLayout) findViewById(R.id.layoutRelativePeriod);
        globalStartDate.setVisibility(View.GONE);
        layoutRelativePeriod.setVisibility(View.GONE);
        radioGroupPeriod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radioAbsolute:
                        globalStartDate.setVisibility(View.VISIBLE);
                        layoutRelativePeriod.setVisibility(View.GONE);
                        break;
                    case R.id.radioRelative:
                        globalStartDate.setVisibility(View.GONE);
                        layoutRelativePeriod.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
