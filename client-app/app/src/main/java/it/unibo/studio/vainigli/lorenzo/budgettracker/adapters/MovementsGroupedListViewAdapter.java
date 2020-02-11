package it.unibo.studio.vainigli.lorenzo.budgettracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.List;
import java.util.Map;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;

public class MovementsGroupedListViewAdapter extends ArrayAdapter<Map.Entry<String, Double>> implements PinnedSectionListView.PinnedSectionListAdapter{

    Activity mActivity;
    boolean mPinned;

    public MovementsGroupedListViewAdapter(Context context, List<Map.Entry<String, Double>> resource, boolean pinned) {
        super(context, R.layout.listview_movements_grouped, resource);
        mPinned = pinned;
        mActivity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.listview_movements_grouped, parent, false);
        }
        Map.Entry<String, Double> item = getItem(position);
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.list_body);
        TextView sectionHeader = (TextView) convertView.findViewById(R.id.list_section);
        if (true){
            TextView id = (TextView) convertView.findViewById(R.id.CLVid);
            TextView description = (TextView) convertView.findViewById(R.id.CLVDesc);
            TextView amount = (TextView) convertView.findViewById(R.id.CLVAmount);
            String dateString = item.getKey();
            description.setText(item.getKey());
            amount.setText(NumberUtils.doubleToCurrency(item.getValue(), true));
            sectionHeader.setVisibility(View.GONE);
        } else if (mPinned){
            //sectionHeader.setText(singleItem.section);
            layout.setVisibility(View.GONE);
        } else {
            sectionHeader.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showSimpleDialog(getContext(), "Click", "");
            }
        });
        convertView.requestFocus();
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        //Log.i("Position", Integer.toString(position));
        Map.Entry<String, Double> item = getItem(position);
        return 1;
    }

    // We implement this method to return 'true' for all view types we want to pin
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        //Log.i("ViewType", Integer.toString(viewType));
        //Log.i("isItemViewTypePinned", Boolean.toString(viewType == 0));
        return false;
    }
}
