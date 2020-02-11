package it.unibo.studio.vainigli.lorenzo.budgettracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;

public class RegistersListViewAdapter extends ArrayAdapter<Register> {

    Activity mActivity;

    public RegistersListViewAdapter(Context context, List<Register> resource) {
        super(context, R.layout.listview_databases, resource);
        mActivity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.listview_databases, parent, false);
        }
        Register database = getItem(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.CLVIcon);
        TextView path = (TextView) convertView.findViewById(R.id.CLVPath);
        TextView description = (TextView) convertView.findViewById(R.id.CLVDesc);
        TextView sharedNames = (TextView) convertView.findViewById(R.id.CLVShared);
        imageView.setImageResource(R.drawable.ic_database);
        path.setText(database.getPath());
        description.setText(database.getName());
        sharedNames.setText(NumberUtils.bytesToKB(database.getSize()) + " - ");
        if (database.getSharedNames() != null){
            sharedNames.append("Condiviso con ");
            for (int i = 0; i<database.getSharedNames().size(); i++){
                sharedNames.append(" " + database.getSharedNames().get(i));
            }
            sharedNames.append(".");
        } else {
            sharedNames.append("Privato");
        }
        return convertView;
    }
}
