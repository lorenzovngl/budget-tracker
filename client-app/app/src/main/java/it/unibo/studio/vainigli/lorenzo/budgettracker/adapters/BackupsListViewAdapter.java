package it.unibo.studio.vainigli.lorenzo.budgettracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Backup;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;

public class BackupsListViewAdapter extends ArrayAdapter<Backup> {

    Activity mActivity;

    public BackupsListViewAdapter(Context context, List<Backup> resource) {
        super(context, R.layout.listview_reports, resource);
        mActivity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.listview_reports, parent, false);
        }
        Backup singleItem = getItem(position);
        TextView description = (TextView) convertView.findViewById(R.id.CLVDesc);
        TextView date = (TextView) convertView.findViewById(R.id.CLVDate);
        TextView path = (TextView) convertView.findViewById(R.id.CLVPath);
        description.setText(singleItem.getDescription());
        date.setText(DateUtils.dateToString(singleItem.getDate(), DateUtils.FORMAT_IT));
        path.setText(singleItem.getPath());
        return convertView;
    }
}
