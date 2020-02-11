package it.unibo.studio.vainigli.lorenzo.budgettracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hb.views.PinnedSectionListView;

import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoAccounts;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.ListItem;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;

public class MovementListViewAdapter extends ArrayAdapter<ListItem> implements PinnedSectionListView.PinnedSectionListAdapter{

    Activity mActivity;
    boolean mPinned;

    public MovementListViewAdapter(Context context, List<ListItem> resource, boolean pinned) {
        super(context, R.layout.listview_movements, resource);
        mPinned = pinned;
        mActivity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.listview_movements, parent, false);
        }
        ListItem singleItem = getItem(position);
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.list_body);
        TextView sectionHeader = (TextView) convertView.findViewById(R.id.list_section);
        if (singleItem.type == ListItem.ITEM){
            TextView id = (TextView) convertView.findViewById(R.id.CLVid);
            TextView database = (TextView) convertView.findViewById(R.id.CLVDatabase);
            TextView date = (TextView) convertView.findViewById(R.id.CLVDate);
            TextView description = (TextView) convertView.findViewById(R.id.CLVDesc);
            ImageView account = (ImageView) convertView.findViewById(R.id.CLVSrcCategory);
            ImageView category = (ImageView) convertView.findViewById(R.id.CLVDstCategory);
            TextView amount = (TextView) convertView.findViewById(R.id.CLVAmount);
            database.setText(singleItem.movement.getDatabase());
            id.setText(singleItem.movement.getId());
            date.setText(singleItem.movement.getStringDate());
            description.setText(singleItem.movement.getDescription());
            DaoCategories daoCategories = new DaoCategories(singleItem.movement.getDatabase(), Const.DBMode.READ, mActivity);
            String srcCategoryId = daoCategories.getId(singleItem.movement.getSrcCategory());
            int icon = daoCategories.getOne(srcCategoryId).getIcon();
            account.setImageResource(icon);
            String dstCategoryId = daoCategories.getId(singleItem.movement.getDstCategory());
            icon = daoCategories.getOne(dstCategoryId).getIcon();
            category.setImageResource(icon);
            amount.setText(NumberUtils.doubleToCurrency(singleItem.movement.getAmount(), true));
            String srcType = daoCategories.getOne(srcCategoryId).getType();
            String dstType = daoCategories.getOne(dstCategoryId).getType();
            if (srcType.equals(Const.Categories.INCOME)){
                amount.setTextColor(ContextCompat.getColor(getContext(), R.color.incoming));
            } else if (srcType.equals(Const.Categories.ACCOUNT)){
                if (dstType.equals(Const.Categories.ACCOUNT)){
                    amount.setTextColor(ContextCompat.getColor(getContext(), R.color.transfer));
                } else {
                    amount.setTextColor(ContextCompat.getColor(getContext(), R.color.expense));
                }
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
