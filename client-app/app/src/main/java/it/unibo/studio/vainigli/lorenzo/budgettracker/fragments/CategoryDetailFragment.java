package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.CategoryDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Category;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class CategoryDetailFragment extends Fragment {

    private String mDatabaseName, mCategoryId;
    private Category mCategory;
    private View rootView;
    private MdaoMovements mMdaoMovements;
    private int mMinAmount, mMaxAmount;
    private boolean isAPrevision;
    private Const.Categories.Type mType;
    private Movement mNextMovement;

    public CategoryDetailFragment(String databaseName, String categoryId, Const.Categories.Type type) {
        mDatabaseName = databaseName;
        mCategoryId = categoryId;
        mType = type;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail_category, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Dettaglio categoria");
        toolbar.getMenu().clear();
        MdaoCategories mdaoCategories = new MdaoCategories(Const.DBMode.READ, getContext());
        Log.i("DBNAME, CATID", mDatabaseName + " " + mCategoryId);
        mCategory = mdaoCategories.getOneWithParams(mDatabaseName, mCategoryId);
        TextView categoryName = (TextView) rootView.findViewById(R.id.textview_cat_name);
        TextView categoryDesc = (TextView) rootView.findViewById(R.id.textview_cat_desc);
        ImageView categoryIcon = (ImageView) rootView.findViewById(R.id.cat_icon);
        ImageView categoryParentIcon = (ImageView) rootView.findViewById(R.id.cat_parent_icon);
        TextView categoryAvg = (TextView) rootView.findViewById(R.id.avg_cat_amount);
        TextView categoryStdDeviation = (TextView) rootView.findViewById(R.id.std_deviation_cat_amount);
        TextView categoryVariationCoeff = (TextView) rootView.findViewById(R.id.variatio_coeff_cat_amount);
        TextView categoryAvgPeriodicity = (TextView) rootView.findViewById(R.id.avg_cat_days);
        TextView categoryStdDevPeriodicity = (TextView) rootView.findViewById(R.id.std_dev_cat_days);
        TextView categoryVarCoeffPeriodicity = (TextView) rootView.findViewById(R.id.coeff_var_cat_days);
        TextView message = (TextView) rootView.findViewById(R.id.message);
        categoryName.setText(mCategory.getDescription());
        categoryIcon.setImageResource(mCategory.getIcon());
        int parentIcon = mCategory.getParentIcon();
        if (parentIcon != 0) {
            categoryParentIcon.setImageResource(parentIcon);
            categoryDesc.setText("Sottocategoria di " + mCategory.getParent());
        } else {
            categoryParentIcon.setVisibility(View.GONE);
            categoryDesc.setText("Categoria primaria");
        }
        categoryDesc.append(" in " + mCategory.getDatabase());
        categoryAvg.setText(NumberUtils.doubleToCurrency(mCategory.getAverage(), true));
        categoryStdDeviation.setText(NumberUtils.doubleToCurrency(mCategory.getStdDeviation(), true));
        categoryVariationCoeff.setText(NumberUtils.doubleToPercent(mCategory.getVariationCoeff(), 0));
        categoryAvgPeriodicity.setText(Integer.toString(mCategory.getAvgPeriodicity()));
        categoryStdDevPeriodicity.setText(Integer.toString(mCategory.getStdDevPeriodicity()));
        categoryVarCoeffPeriodicity.setText(NumberUtils.doubleToPercent(mCategory.getVarCoeffPeriodicity(), 0));
        mMdaoMovements = new MdaoMovements(Const.DBMode.READ, getContext());
        if (mMdaoMovements.getUpcomings(mDatabaseName, mCategoryId, 1, mType).size() > 0) {
            mNextMovement = mMdaoMovements.getUpcomings(mDatabaseName, mCategoryId, 1, mType).get(0);
            String date = DateUtils.dateToString(mNextMovement.getDate(), DateUtils.FORMAT_IT);
            String amount = NumberUtils.doubleToCurrency(mNextMovement.getAmount(), true);
            if (mType == Const.Categories.Type.ACCOUNTS){
                mMaxAmount = (int) mNextMovement.getAmount();
            } else {
                mMaxAmount = (int) Math.abs(mNextMovement.getAmount());
            }
            mMinAmount = mMaxAmount;
            message.setText("Il prossimo movimento è previsto il " + date + " con un importo di " + amount + " euro.");
            isAPrevision = false;
        } else {
            // Se non c'è nessun movimento programmato in futuro, faccio una previsione
            Date expectedNewDate = DateUtils.increment(mCategory.getLastMovementDate(), Const.Period.DAILY, mCategory.getAvgPeriodicity());
            Date maxDate = DateUtils.increment(expectedNewDate, Const.Period.DAILY, mCategory.getStdDevPeriodicity());
            Date minDate = DateUtils.decrement(expectedNewDate, Const.Period.DAILY, mCategory.getStdDevPeriodicity());
            Log.i("EXP_DATE", DateUtils.dateToString(expectedNewDate, DateUtils.FORMAT_IT));
            Log.i("MIN_DATE", DateUtils.dateToString(minDate, DateUtils.FORMAT_IT));
            Log.i("MAX_DATE", DateUtils.dateToString(maxDate, DateUtils.FORMAT_IT));
            Date today = new Date();
            mMinAmount = (int) (mCategory.getAverage() - mCategory.getStdDeviation());
            mMaxAmount = (int) (mCategory.getAverage() + mCategory.getStdDeviation());
            if (minDate.after(today)) {
                message.setText("Il prossimo movimento è previsto ");
                if (DateUtils.getDateDiff(minDate, maxDate, TimeUnit.DAYS) < 1) {
                    message.append("il " + DateUtils.dateToString(minDate, DateUtils.FORMAT_IT));
                } else {
                    message.append("tra il " + DateUtils.dateToString(minDate, DateUtils.FORMAT_IT) +
                            " e il " + DateUtils.dateToString(maxDate, DateUtils.FORMAT_IT));
                }
                message.append(" con un importo stimato ");
            } else if (maxDate.after(today)) {
                message.setText("Il prossimo movimento è previsto entro il " + DateUtils.dateToString(maxDate, DateUtils.FORMAT_IT)
                        + " con un importo stimato ");
            } else {
                message.setText("L'importo del prossimo movimento è stimato ");
            }
            if (mMaxAmount - mMinAmount < 1) {
                message.append("in " + mMinAmount);
            } else {
                message.append("tra " + mMinAmount + " e " + mMaxAmount);
            }
            message.append(" euro.");
            isAPrevision = true;
        }
        createChart();
        return rootView;
    }

    private void createChart() {
        LineChartView chart;
        LineChartData data;
        boolean hasAxes = true;
        boolean hasAxesNames = false;
        boolean hasLines = true;
        boolean hasPoints = true;
        ValueShape shape = ValueShape.CIRCLE;
        boolean isFilled = false;
        boolean hasLabels = false;
        boolean isCubic = false;
        boolean hasLabelForSelected = false;
        boolean hasGradientToTransparent = false;
        chart = (LineChartView) rootView.findViewById(R.id.linechart);
        List<Movement> movements = mMdaoMovements.getPrevious(mDatabaseName, mCategoryId, -1, Const.ORDER_ASC, mType);
        int movementsToDisplay = 5;
        List<Movement> lastMovements;
        if (movementsToDisplay +1> movements.size()){
            lastMovements = movements;
        } else {
            lastMovements = movements.subList(movements.size() - (movementsToDisplay + 1), movements.size());
        }
        List<Line> lines = new ArrayList<Line>();
        List<PointValue> mainValues = new ArrayList<PointValue>();
        List<PointValue> newMaxValues = new ArrayList<PointValue>();
        List<PointValue> newMinValues = new ArrayList<PointValue>();
        double balance = 0;
        if (mType == Const.Categories.Type.ACCOUNTS){
            for (int j = 0; j < movements.size()-movementsToDisplay; ++j) {
                balance += movements.get(j).getAmount();
            }
        }
        double min = 0;
        double max = 0;
        for (int j = 0; j < lastMovements.size(); ++j) {
            if (mType == Const.Categories.Type.ACCOUNTS){
                double amount = lastMovements.get(j).getAmount();
                mainValues.add(new PointValue(j, (float) (balance + amount)));
                Log.i("BALANCE", Double.toString(balance));
                if (j == 0){
                    min = balance + amount;
                    max = balance + amount;
                } else {
                    min = balance + amount < min ? balance + amount : min;
                    max = balance + amount > max ? balance + amount : max;
                }
                // TODO l'ultimo balance non è computato in min/max
                balance += amount;
            } else {
                mainValues.add(new PointValue(j, (float) Math.abs(lastMovements.get(j).getAmount())));
                min = Math.min(mMinAmount, mMdaoMovements.getMin(mDatabaseName, lastMovements));
                max = Math.max(mMaxAmount, mMdaoMovements.getMax(mDatabaseName, lastMovements));
            }
        }
        int lastMovementIndex = lastMovements.size()-1;
        if (mType != Const.Categories.Type.ACCOUNTS){
            newMaxValues.add(new PointValue(lastMovementIndex, (float) movements.get(movements.size()-1).getAmount()));
            newMaxValues.add(new PointValue(lastMovementIndex+1, (float) mMaxAmount));
            newMinValues.add(new PointValue(lastMovementIndex, (float) movements.get(movements.size()-1).getAmount()));
            newMinValues.add(new PointValue(lastMovementIndex+1, (float) mMinAmount));
        } else {
            newMaxValues.add(new PointValue(lastMovementIndex, (float) balance));
            newMaxValues.add(new PointValue(lastMovementIndex+1, (float) (balance + mMaxAmount)));
            newMinValues.add(new PointValue(lastMovementIndex, (float) balance));
            newMinValues.add(new PointValue(lastMovementIndex+1, (float) (balance + mMinAmount)));
        }
        Line mainLine = new Line(mainValues);
        mainLine.setColor(ChartUtils.COLOR_BLUE);
        mainLine.setShape(shape);
        mainLine.setCubic(isCubic);
        mainLine.setFilled(true);
        mainLine.setHasLabels(hasLabels);
        mainLine.setHasLabelsOnlyForSelected(hasLabelForSelected);
        mainLine.setHasLines(hasLines);
        mainLine.setHasPoints(hasPoints);
        Line newMaxLine = new Line(newMaxValues);
        if (isAPrevision){
            newMaxLine.setColor(ChartUtils.COLOR_ORANGE);
        } else {
            newMaxLine.setColor(ChartUtils.COLOR_BLUE);
        }
        newMaxLine.setShape(shape);
        newMaxLine.setCubic(isCubic);
        newMaxLine.setFilled(false);
        newMaxLine.setHasLabels(hasLabels);
        newMaxLine.setHasLabelsOnlyForSelected(hasLabelForSelected);
        newMaxLine.setHasLines(hasLines);
        newMaxLine.setHasPoints(hasPoints);
        newMaxLine.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        Line newMinLine = new Line(newMinValues);
        if (isAPrevision){
            newMinLine.setColor(ChartUtils.COLOR_ORANGE);
        } else {
            newMinLine.setColor(ChartUtils.COLOR_BLUE);
        }
        newMinLine.setShape(shape);
        newMinLine.setCubic(isCubic);
        newMinLine.setFilled(false);
        newMinLine.setHasLabels(hasLabels);
        newMinLine.setHasLabelsOnlyForSelected(hasLabelForSelected);
        newMinLine.setHasLines(hasLines);
        newMinLine.setHasPoints(hasPoints);
        newMinLine.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        lines.add(newMaxLine);
        lines.add(newMinLine);
        lines.add(mainLine);
        data = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }
        chart.setLineChartData(data);
        chart.setViewportCalculationEnabled(true);
        // Se il prossimo movimento è programmato uno degli estremi potrebbe essere fuori dalla viewport
        // perché non è un valore calcolato da media e scarto
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = (float) (min - min * 0.1);
        v.top = (float) (max + min * 0.1);
        v.left = 0;
        v.right = lastMovementIndex+1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }
}
