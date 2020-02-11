package it.unibo.studio.vainigli.lorenzo.budgettracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Category;

import static java.lang.Math.abs;
import static java.lang.Math.sin;

public class CategoriesListViewAdapter extends ArrayAdapter<Category> {

    private Activity mActivity;
    private Const.Categories.Type mType;
    private Const.Period mPeriod;

    public CategoriesListViewAdapter(Context context, List<Category> resource, Const.Categories.Type type, Const.Period period) {
        super(context, R.layout.listview_categories, resource);
        mType = type;
        mPeriod = period;
        mActivity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.listview_categories, parent, false);
        }
        Category singleItem = getItem(position);
        //Log.i("CAT NAME", singleItem.getDescription());
        TextView id = (TextView) convertView.findViewById(R.id.CLVId);
        TextView database = (TextView) convertView.findViewById(R.id.CLVDatabase);
        ImageView parentIconIV = (ImageView) convertView.findViewById(R.id.CLVParentIcon);
        ImageView iconIV = (ImageView) convertView.findViewById(R.id.CLVIcon);
        TextView descriptionTV = (TextView) convertView.findViewById(R.id.CLVDesc);
        TextView parentDescriptionTV = (TextView) convertView.findViewById(R.id.CLVParentDesc);
        TextView amount = (TextView) convertView.findViewById(R.id.CLVAmount);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.CLVBar);
        TextView textViewpercentage = (TextView) convertView.findViewById(R.id.CLVPerc);
        id.setText(singleItem.getId());
        database.setText(singleItem.getDatabase());
        int parentIcon = singleItem.getParentIcon();
        if (parentIcon != 0){
            parentIconIV.setImageResource(parentIcon);
        } else {
            parentIconIV.setVisibility(View.GONE);
        }
        iconIV.setImageResource(singleItem.getIcon());
        descriptionTV.setText(singleItem.getDescription());
        String parentName = singleItem.getParent();
        if (parentName != null){
            parentDescriptionTV.setText(parentName);
        } else {
            parentDescriptionTV.setVisibility(View.GONE);
        }
        amount.setText(NumberUtils.doubleToCurrency(singleItem.getTotal(), true));
        MdaoCategories mdaoCategories = new MdaoCategories(Const.DBMode.READ, mActivity);
        double total = mdaoCategories.getTotal(singleItem.getDatabase(), mType, mPeriod, parentName);
        progressBar.setMax((int) abs(total));
        progressBar.setProgress((int) abs(singleItem.getTotal()));
        double percentage = (abs(singleItem.getTotal() * 100)) / abs(total);
        textViewpercentage.setText(NumberUtils.setDecimals(percentage, 1) + " %");
        //Log.i("TOTALE", Double.toString(total));
        return convertView;
    }
}
