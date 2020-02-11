package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.DateDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NotificationUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by lorenzo on 4/30/16.
 */
public class HomeFragment extends Fragment {

    private TextView referenceDateTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        NotificationUtils.notifyForNextMovements(getContext());
        referenceDateTextView = (TextView) rootView.findViewById(R.id.referenceDate);
        referenceDateTextView.setText(DateUtils.dateToString(new Date(), DateUtils.FORMAT_IT));
        final PieChartView chart = (PieChartView) rootView.findViewById(R.id.piechart);
        generateData(chart);
        referenceDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog dialog = new DateDialog((TextView) view);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        });
        referenceDateTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                generateData(chart);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        // Prossimi movimenti
        MdaoMovements mdaoMovements = new MdaoMovements(Const.DBMode.READ, getContext());
        List<Movement> movements = mdaoMovements.getUpcomings(null, 3, null);
        ViewGroup containerLayout = (ViewGroup) rootView.findViewById(R.id.layout_next_movements);
        if (movements != null && movements.size() > 0){
            for (int i = 0; i < movements.size(); i++){
                containerLayout.addView(getSummaryMovementView(movements.get(i)));
            }
        } else {
            TextView textView = new TextView(getContext());
            textView.setText(R.string.no_data_to_show);
            containerLayout.addView(textView);
        }
        // Ultimi movimenti
        movements = mdaoMovements.getPrevious(null, 3, Const.ORDER_DESC, null);
        containerLayout = (ViewGroup) rootView.findViewById(R.id.layout_last_movements);
        if (movements != null && movements.size() > 0){
            for (int i = 0; i < movements.size(); i++){
                containerLayout.addView(getSummaryMovementView(movements.get(i)));
            }
        } else {
            TextView textView = new TextView(getContext());
            textView.setText(R.string.no_data_to_show);
            containerLayout.addView(textView);
        }
        return rootView;
    }

    // Questa funzione funge da semplice adapter
    private LinearLayout getSummaryMovementView(Movement movement) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.textview_desc_amount, null);
        TextView textViewDate = (TextView) layout.findViewById(R.id.textView_date);
        TextView textViewDesc = (TextView) layout.findViewById(R.id.textView_desc);
        TextView textViewAmount = (TextView) layout.findViewById(R.id.textView_amount);
        textViewDate.setText(movement.getStringDate());
        textViewDesc.setText(movement.getDescription());
        textViewAmount.setText(NumberUtils.doubleToCurrency(movement.getAmount(), true));
        return layout;
    }

    private void generateData(PieChartView chart) {
        chart.setChartRotationEnabled(false);
        chart.setChartRotation(270, false);
        List<SliceValue> values = new ArrayList<SliceValue>();
        MdaoMovements mdaoMovements = new MdaoMovements(Const.DBMode.READ, getContext());
        Date referenceDate = DateUtils.stringToDate(referenceDateTextView.getText().toString(), DateUtils.FORMAT_IT);
        float totalIncomes = (float) mdaoMovements.getTotal(Const.MovementType.INCOME, Const.Period.ALLTIME, referenceDate);
        float currentIncomes = (float) mdaoMovements.getTotal(Const.MovementType.INCOME, Const.Period.CURRENT, referenceDate);
        float totalExpenses = (float) Math.abs(mdaoMovements.getTotal(Const.MovementType.EXPENSE, Const.Period.ALLTIME, referenceDate));
        float currentExpenses = (float) Math.abs(mdaoMovements.getTotal(Const.MovementType.EXPENSE, Const.Period.CURRENT, referenceDate));
        float currentBalance = currentIncomes - currentExpenses;
        SliceValue sliceValue = new SliceValue(currentIncomes, Color.argb(200, 0, 200, 0));
        values.add(sliceValue);
        sliceValue = new SliceValue(totalIncomes - currentIncomes, Color.argb(200, 0, 150, 0));
        values.add(sliceValue);
        sliceValue = new SliceValue(currentExpenses, Color.argb(200, 200, 0, 0));
        values.add(sliceValue);
        sliceValue = new SliceValue(totalExpenses - currentExpenses, Color.argb(200, 150, 0, 0));
        values.add(sliceValue);
        boolean hasLabels = true;
        boolean hasLabelForSelected = true;
        boolean hasLabelsOutside = true;
        boolean hasCenterCircle = true;
        boolean isExploded = true;
        boolean hasCenterText1 = true;
        boolean hasCenterText2 = true;
        PieChartData data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);
        if (isExploded) {
            data.setSlicesSpacing(10);
        }
        if (hasCenterText1) {
            data.setCenterText1(NumberUtils.doubleToCurrency((double) currentBalance, true));
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        }
        if (hasCenterText2) {
            data.setCenterText2(getResources().getString(R.string.current_balance));
            data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
        }
        chart.setPieChartData(data);
    }
}