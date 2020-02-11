package it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.util.Date;

import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs.StatisticsAllTabFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;

public class StatisticsAsyncTask extends AsyncTask<Void, Void, StatisticsAsyncTask.Result> {

    public static class Result {
        public double totalExpenses;
        public double currentExpenses;
        public double totalIncomings;
        public double currentIncomings;
        public double lastWeekExpenses;
        public double lastMonthExpenses;
        public double lastYearExpenses;
        public double avgWeekExpenses;
        public double avgMonthExpenses;
        public double avgYearExpenses;
        public double lastWeekIncomings;
        public double lastMonthIncomings;
        public double lastYearIncomings;
        public double avgWeekIncomings;
        public double avgMonthIncomings;
        public double avgYearIncomings;
    }

    private StatisticsAllTabFragment mFragment;
    private ProgressDialog mDialog;
    private Context mContext;

    public StatisticsAsyncTask(StatisticsAllTabFragment fragment, Context context){
        mContext = context;
        mFragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = DialogUtils.showLoadingDialog(mContext, "Calcolo", "Abbi pazienza...");
    }

    @Override
    protected Result doInBackground(Void... voids) {
        Result result = new Result();
        DaoMovements daoMovements = new DaoMovements(Const.DBMode.READ, mContext);
        Date today = new Date();
        result.totalExpenses = daoMovements.getTotal(Const.MovementType.EXPENSE, Const.Period.ALLTIME, today);
        result.currentExpenses = daoMovements.getTotal(Const.MovementType.EXPENSE, Const.Period.CURRENT, today);
        result.totalIncomings = daoMovements.getTotal(Const.MovementType.INCOME, Const.Period.ALLTIME, today);
        result.currentIncomings = daoMovements.getTotal(Const.MovementType.INCOME, Const.Period.CURRENT, today);
        result.lastWeekExpenses = daoMovements.getTotal(Const.MovementType.EXPENSE, Const.Period.WEEKLY, today);
        result.lastMonthExpenses = daoMovements.getTotal(Const.MovementType.EXPENSE, Const.Period.MONTHLY, today);
        result.lastYearExpenses = daoMovements.getTotal(Const.MovementType.EXPENSE, Const.Period.YEARLY, today);
        result.avgWeekExpenses = daoMovements.getAverage(Const.MovementType.EXPENSE, Const.Period.WEEKLY, today);
        result.avgMonthExpenses = daoMovements.getAverage(Const.MovementType.EXPENSE, Const.Period.MONTHLY, today);
        result.avgYearExpenses = daoMovements.getAverage(Const.MovementType.EXPENSE, Const.Period.YEARLY, today);
        result.lastWeekIncomings = daoMovements.getTotal(Const.MovementType.INCOME, Const.Period.WEEKLY, today);
        result.lastMonthIncomings = daoMovements.getTotal(Const.MovementType.INCOME, Const.Period.MONTHLY, today);
        result.lastYearIncomings = daoMovements.getTotal(Const.MovementType.INCOME, Const.Period.YEARLY, today);
        result.avgWeekIncomings = daoMovements.getAverage(Const.MovementType.INCOME, Const.Period.WEEKLY, today);
        result.avgMonthIncomings = daoMovements.getAverage(Const.MovementType.INCOME, Const.Period.MONTHLY, today);
        result.avgYearIncomings = daoMovements.getAverage(Const.MovementType.INCOME, Const.Period.YEARLY, today);
        return result;
    }

    @Override
    protected void onPostExecute(Result statisticResult) {
        super.onPostExecute(statisticResult);
        mFragment.displayData(statisticResult);
        mDialog.dismiss();
    }
}
