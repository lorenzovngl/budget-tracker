package it.unibo.studio.vainigli.lorenzo.budgettracker.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.DateDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.Statistic;

/**
 * Created by lorenzo on 4/30/16.
 */
public class PrevisionsFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_previsions, container, false);
        final EditText editTextDate = (EditText) rootView.findViewById(R.id.editTextDate);
        final TextView textViewAvg = (TextView) rootView.findViewById(R.id.textViewAvg);
        final TextView textViewTotal = (TextView) rootView.findViewById(R.id.textViewTotal);
        final TextView textViewTotalPrevision = (TextView) rootView.findViewById(R.id.textViewTotalPrevision);
        Button button = (Button) rootView.findViewById(R.id.buttonOk);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialog dialog = new DateDialog((EditText) view);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = DateUtils.stringToDate(editTextDate.getText().toString(), DateUtils.FORMAT_IT);
                Statistic statistic = new Statistic(getContext());
                double avg = statistic.getAverage(Const.MovementType.ALL, Const.Period.ALLTIME);
                Log.i("IMPORTI GIORNO", String.valueOf(statistic.amountOfTheDay(date)));
                Log.i("MEDIA GIORNO", String.valueOf(statistic.getAverage(Const.MovementType.ALL, Const.Period.ALLTIME)));
                Log.i("MEDIA SETTIMANA", String.valueOf(statistic.getAverage(Const.MovementType.ALL, Const.Period.WEEKLY)));
                Log.i("MEDIA MESE", String.valueOf(statistic.getAverage(Const.MovementType.ALL, Const.Period.MONTHLY)));
                Log.i("MEDIA ANNO", String.valueOf(statistic.getAverage(Const.MovementType.ALL, Const.Period.YEARLY)));
                double realTotal = statistic.getTotal(Const.MovementType.ALL, Const.Period.ALLTIME, new Date());
                int days = DateUtils.getDiffDays(new Date(), date);
                double previsionTotal = realTotal + avg * days;
                textViewTotal.setText("Saldo reale: " + String.valueOf(realTotal) + " GIORNI " + String.valueOf(days));
                textViewAvg.setText("Media reale: " + String.valueOf(avg));
                textViewTotalPrevision.setText("Proiezione saldo: " + String.valueOf(previsionTotal));
            }
        });
        return rootView;
    }
}