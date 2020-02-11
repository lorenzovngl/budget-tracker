package it.unibo.studio.vainigli.lorenzo.budgettracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoAccounts;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.ListItem;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.Statistic;

public class DepreciationListViewAdapter extends ArrayAdapter<ListItem> implements PinnedSectionListView.PinnedSectionListAdapter{

    Activity mActivity;
    boolean mPinned;

    public DepreciationListViewAdapter(Context context, List<ListItem> resource, boolean pinned) {
        super(context, R.layout.listview_depreciation, resource);
        mPinned = pinned;
        mActivity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.listview_depreciation, parent, false);
        }
        ListItem singleItem = getItem(position);
        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.list_body);
        TextView sectionHeader = (TextView) convertView.findViewById(R.id.list_section);
        if (singleItem.type == ListItem.ITEM){
            TextView id = (TextView) convertView.findViewById(R.id.CLVid);
            TextView date = (TextView) convertView.findViewById(R.id.CLVDate);
            TextView startDate = (TextView) convertView.findViewById(R.id.CLVStartDate);
            TextView endDate = (TextView) convertView.findViewById(R.id.CLVEndDate);
            TextView description = (TextView) convertView.findViewById(R.id.CLVDesc);
            ImageView account = (ImageView) convertView.findViewById(R.id.CLVAccount);
            ImageView category = (ImageView) convertView.findViewById(R.id.CLVCategory);
            TextView amount = (TextView) convertView.findViewById(R.id.CLVAmount);
            ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.CLVBar);
            id.setText(singleItem.movement.getId().toString());
            date.setText(singleItem.movement.getStringDate());
            startDate.setText(singleItem.movement.getStringStartDate());
            endDate.setText(singleItem.movement.getStringEndDate());
            description.setText(singleItem.movement.getDescription());
            DaoAccounts daoAccounts = new DaoAccounts(Const.DBMode.READ, mActivity);
            String accountId = daoAccounts.getId(singleItem.movement.getSrcCategory());
            int icon = daoAccounts.getOne(accountId).getIcon();
            account.setImageResource(icon);
            DaoCategories daoCategories = new DaoCategories(Const.DBMode.READ, mActivity);
            String categoryId = daoCategories.getId(singleItem.movement.getDstCategory());
            icon = daoCategories.getOne(categoryId).getIcon();
            category.setImageResource(icon);
            amount.setText(NumberUtils.doubleToCurrency(singleItem.movement.getAmount(), true));
            progressBar.setMax((int) Math.abs(singleItem.movement.getAmount()));
            progressBar.setProgress((int) Statistic.getDepreciation(singleItem.movement));
            if (singleItem.movement.getAmount() > 0){
                amount.setTextColor(getContext().getResources().getColor(R.color.incoming));
            } else {
                amount.setTextColor(getContext().getResources().getColor(R.color.expense));
            }
            sectionHeader.setVisibility(View.GONE);
        } else if (mPinned){
            sectionHeader.setText(singleItem.section);
            layout.setVisibility(View.GONE);
        } else {
            sectionHeader.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
        }
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
        ListItem item = getItem(position);
        return item.type;
    }

    // We implement this method to return 'true' for all view types we want to pin
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        //Log.i("ViewType", Integer.toString(viewType));
        //Log.i("isItemViewTypePinned", Boolean.toString(viewType == 0));
        return viewType == ListItem.SECTION;
    }
}
