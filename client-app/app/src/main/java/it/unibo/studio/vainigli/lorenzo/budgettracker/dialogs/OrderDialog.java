package it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import java.util.Arrays;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.fragments.MovementsListFragment;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;

public class OrderDialog extends DialogFragment {

    private RadioButton radioDateAsc, radioDateDesc, radioAmountAsc, radioAmountDesc;
    private MovementsListFragment mFragment;
    private Dialog mDialog;

    public OrderDialog(MovementsListFragment fragment) {
        mFragment = fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ordina Movimenti");
        final LayoutInflater inflater = (LayoutInflater)getContext().getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_order, null);
        radioDateAsc = (RadioButton) view.findViewById(R.id.radio_date_asc);
        radioDateDesc = (RadioButton) view.findViewById(R.id.radio_date_desc);
        radioAmountAsc = (RadioButton) view.findViewById(R.id.radio_amount_asc);
        radioAmountDesc = (RadioButton) view.findViewById(R.id.radio_amount_desc);
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

    private void saveChanges(){
        MdaoMovements mdaoMovements = new MdaoMovements(Const.DBMode.READ, getContext());
        String columnOrderBy = null;
        String order = null;
        boolean pinned = MovementsListFragment.PINNED;
        if(radioDateAsc.isChecked()){
            columnOrderBy = Const.Movements.COL_DATE;
            order = Const.ORDER_ASC;
        } else if(radioDateDesc.isChecked()){
            columnOrderBy = Const.Movements.COL_DATE;
            order = Const.ORDER_DESC;
        } else if(radioAmountAsc.isChecked()){
            columnOrderBy = Const.Movements.COL_AMOUNT;
            order = Const.ORDER_ASC;
            pinned = MovementsListFragment.UNPINNED;
        } else if(radioAmountDesc.isChecked()){
            columnOrderBy = Const.Movements.COL_AMOUNT;
            order = Const.ORDER_DESC;
            pinned = MovementsListFragment.UNPINNED;
        }
        List<Movement> bufferM = mdaoMovements.getFilteredData(null, columnOrderBy, order);
        Log.d("BufferM:", Arrays.toString(bufferM.toArray(new Movement[bufferM.size()])));
        mFragment.loadAdapter(bufferM, pinned);
    }
}
