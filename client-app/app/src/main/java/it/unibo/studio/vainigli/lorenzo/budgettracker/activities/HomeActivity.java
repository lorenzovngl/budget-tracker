package it.unibo.studio.vainigli.lorenzo.budgettracker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks.ExampleDataAsyncTask;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.UsersPrefsController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCalendar;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.RegistrersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.AccountsFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.BackupsFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.CategoriesFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.RegistersFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.MovementsListFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.HomeFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.ReportsFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.StatisticsFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout.SimpleDrawerListener mDrawerListenerForBack;
    private UsersPrefsController mUsersPrefsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navigationHeader = navigationView.getHeaderView(0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();

        TextView textViewWelcome = (TextView) navigationHeader.findViewById(R.id.textViewWelcome);
        textViewWelcome.setText("Benvenuto " + UsersPrefsController.getFullname(this) + "!");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else {
            switch (item.getItemId()) {
                // Respond to the action bar's Up/Home button
                case android.R.id.home:
                    finish();
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.executePendingTransactions();
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        } else if (id == R.id.nav_accounts) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new AccountsFragment()).commit();
        } else if (id == R.id.nav_categories) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new CategoriesFragment()).commit();
        } else if (id == R.id.nav_movements) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new MovementsListFragment(null, null)).commit();
        } else if (id == R.id.nav_stat) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new StatisticsFragment()).commit();
        } else if (id == R.id.nav_databases) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new RegistersFragment()).commit();
        } else if (id == R.id.nav_report) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ReportsFragment()).commit();
        } else if (id == R.id.nav_backup) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new BackupsFragment()).commit();
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_reset_db) {
            /*final ProgressDialog dialog = DialogUtils.showLoadingDialog(this, "Reset database", "Reset in corso...");
            // Viene usato un nuovo thread per non bloccare la visualizzazione del dialog
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RegistrersController databaseController = new RegistrersController(getBaseContext());
                    databaseController.resetDatabase();
                    DialogUtils.dismissDialog(dialog);
                }
            }).start();*/
            ExampleDataAsyncTask exampleDataAsyncTask = new ExampleDataAsyncTask(this);
            exampleDataAsyncTask.execute();
        } else if (id == R.id.nav_delete_db) {
            final ProgressDialog dialog = DialogUtils.showLoadingDialog(this, "Cancellazione", "Cancellazione in corso...");
            // Viene usato un nuovo thread per non bloccare la visualizzazione del dialog
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RegistrersController registrersController = new RegistrersController(getBaseContext());
                    registrersController.deleteDatabase();
                    DialogUtils.dismissDialog(dialog);
                }
            }).start();
        } else if (id == R.id.nav_calendar) {
            DaoCalendar.Builder builder = new DaoCalendar.Builder(this);
            builder.execute();
        } else if (id == R.id.nav_logout) {
            mUsersPrefsController.prefsLogout(this);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setDrawerState(boolean isEnabled, final Fragment parentFragment) {
        if (isEnabled) {
            mDrawerLayout.removeDrawerListener(mDrawerListenerForBack);
            mDrawerLayout.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
            //mDrawerToggle.setDrawerIndicatorEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            mDrawerListenerForBack = new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerStateChanged(int newState) {
                    super.onDrawerStateChanged(newState);
                    mDrawerLayout.closeDrawers();
                    Log.i("HOME_BUTTON", "CLICKED!");
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, parentFragment).commit();
                    setDrawerState(true, null);
                }
            };
            mDrawerLayout.removeDrawerListener(mDrawerToggle);
            mDrawerLayout.addDrawerListener(mDrawerListenerForBack);
            mDrawerToggle.syncState();
            //mDrawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
