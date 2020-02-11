package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks.StatisticsAsyncTask;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;

public class StatisticsAllTabFragment extends Fragment {

    private View mRootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_statistics_all, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        setHasOptionsMenu(false);
        toolbar.setTitle(R.string.statistics);
        //DaoMovements daoMovements = new DaoMovements(Const.DBMode.READ, getContext());
        //mMovementList = daoMovements.getFilteredData(null, null, null);
        StatisticsAsyncTask task = new StatisticsAsyncTask(this, getContext());
        task.execute();
        return mRootView;
    }

    public void displayData(StatisticsAsyncTask.Result result){
        TextView textViewTotalIncomings = (TextView) mRootView.findViewById(R.id.CLVtotal_incomings);
        TextView textViewCurrentIncomings = (TextView) mRootView.findViewById(R.id.CLVtotal_incomings_current);
        TextView textViewFutureIncomings = (TextView) mRootView.findViewById(R.id.CLVtotal_incomings_future);        
        
        TextView textViewTotalExpenses = (TextView) mRootView.findViewById(R.id.CLVtotal_expenses);
        TextView textViewCurrentExpenses = (TextView) mRootView.findViewById(R.id.CLVtotal_expenses_current);
        TextView textViewFutureExpenses = (TextView) mRootView.findViewById(R.id.CLVtotal_expenses_future);
        
        TextView textViewTotalDifference = (TextView) mRootView.findViewById(R.id.CLVtotal_difference);
        TextView textViewCurrentDifference = (TextView) mRootView.findViewById(R.id.CLVtotal_difference_current);
        TextView textViewFutureDifference = (TextView) mRootView.findViewById(R.id.CLVtotal_difference_future);

        TextView textViewLastWeekExpenses = (TextView) mRootView.findViewById(R.id.textView_last_week_expenses);
        TextView textViewLastMonthExpenses = (TextView) mRootView.findViewById(R.id.textView_last_month_expenses);
        TextView textViewLastYearExpenses = (TextView) mRootView.findViewById(R.id.textView_last_year_expenses);

        TextView textViewAvgWeekExpenses = (TextView) mRootView.findViewById(R.id.textView_avg_week_expenses);
        TextView textViewAvgMonthExpenses = (TextView) mRootView.findViewById(R.id.textView_avg_month_expenses);
        TextView textViewAvgYearExpenses = (TextView) mRootView.findViewById(R.id.textView_avg_year_expenses);

        TextView textViewLastWeekIncomings = (TextView) mRootView.findViewById(R.id.textView_last_week_incomings);
        TextView textViewLastMonthIncomings = (TextView) mRootView.findViewById(R.id.textView_last_month_incomings);
        TextView textViewLastYearIncomings = (TextView) mRootView.findViewById(R.id.textView_last_year_incomings);

        TextView textViewAvgWeekIncomings = (TextView) mRootView.findViewById(R.id.textView_avg_week_incomings);
        TextView textViewAvgMonthIncomings = (TextView) mRootView.findViewById(R.id.textView_avg_month_incomings);
        TextView textViewAvgYearIncomings = (TextView) mRootView.findViewById(R.id.textView_avg_year_incomings);

        double futureIncomings = result.totalIncomings - result.currentIncomings;
        textViewTotalIncomings.setText(NumberUtils.doubleToCurrency(result.totalIncomings, true));
        textViewCurrentIncomings.setText(NumberUtils.doubleToCurrency(result.currentIncomings, true));
        textViewFutureIncomings.setText(NumberUtils.doubleToCurrency(futureIncomings, true));

        double futureExpenses = result.totalExpenses - result.currentExpenses;
        textViewTotalExpenses.setText(NumberUtils.doubleToCurrency(result.totalExpenses, true));
        textViewCurrentExpenses.setText(NumberUtils.doubleToCurrency(result.currentExpenses, true));
        textViewFutureExpenses.setText(NumberUtils.doubleToCurrency(futureExpenses, true));

        double totalDiff = result.totalIncomings + result.totalExpenses;
        double currentDiff = result.currentIncomings + result.currentExpenses;
        double futureDiff = totalDiff - currentDiff;
        textViewTotalDifference.setText(NumberUtils.doubleToCurrency(totalDiff, true));
        textViewCurrentDifference.setText(NumberUtils.doubleToCurrency(currentDiff, true));
        textViewFutureDifference.setText(NumberUtils.doubleToCurrency(futureDiff, true));

        textViewLastWeekExpenses.setText(NumberUtils.doubleToCurrency(Math.abs(result.lastWeekExpenses), true));
        textViewLastMonthExpenses.setText(NumberUtils.doubleToCurrency(Math.abs(result.lastMonthExpenses), true));
        textViewLastYearExpenses.setText(NumberUtils.doubleToCurrency(Math.abs(result.lastYearExpenses), true));

        textViewAvgWeekExpenses.setText(NumberUtils.doubleToCurrency(Math.abs(result.avgWeekExpenses), true));
        textViewAvgMonthExpenses.setText(NumberUtils.doubleToCurrency(Math.abs(result.avgMonthExpenses), true));
        textViewAvgYearExpenses.setText(NumberUtils.doubleToCurrency(Math.abs(result.avgYearExpenses), true));

        textViewLastWeekIncomings.setText(NumberUtils.doubleToCurrency(result.lastWeekIncomings, true));
        textViewLastMonthIncomings.setText(NumberUtils.doubleToCurrency(result.lastMonthIncomings, true));
        textViewLastYearIncomings.setText(NumberUtils.doubleToCurrency(result.lastYearIncomings, true));

        textViewAvgWeekIncomings.setText(NumberUtils.doubleToCurrency(result.avgWeekIncomings, true));
        textViewAvgMonthIncomings.setText(NumberUtils.doubleToCurrency(result.avgMonthIncomings, true));
        textViewAvgYearIncomings.setText(NumberUtils.doubleToCurrency(result.avgYearIncomings, true));
    }
}
