package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpinnerUtils {

    public static boolean selectByString(Spinner spnr, String string) {
        Adapter adapter = spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItem(position).equals(string)) {
                spnr.setSelection(position);
                return true;
            }
        }
        return false;
    }

}
