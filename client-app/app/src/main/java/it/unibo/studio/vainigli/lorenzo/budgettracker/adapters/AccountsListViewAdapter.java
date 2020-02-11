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
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoAccounts;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Account;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.NumberUtils;

public class AccountsListViewAdapter extends ArrayAdapter<Account> {

    Activity mActivity;
    Const.MovementType mType;
    Const.Period mPeriod;

    public AccountsListViewAdapter(Context context, List<Account> resource, Const.MovementType type, Const.Period period) {
        super(context, R.layout.listview_categories, resource);
        mType = type;
        mPeriod = period;
        mActivity = (Activity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.listview_accounts, parent, false);
        }
        Account singleItem = getItem(position);
        TextView id = (TextView) convertView.findViewById(R.id.CLVId);
        ImageView icon = (ImageView) convertView.findViewById(R.id.CLVIcon);
        TextView description = (TextView) convertView.findViewById(R.id.CLVDesc);
        TextView amount = (TextView) convertView.findViewById(R.id.CLVAmount);
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.CLVBar);
        TextView textViewpercentage = (TextView) convertView.findViewById(R.id.CLVPerc);
        id.setText(singleItem.getId());
        icon.setImageResource(singleItem.getIcon());
        description.setText(singleItem.getDescription());
        amount.setText(NumberUtils.doubleToCurrency(singleItem.getAmount(), true));
        //DaoCalendar daoCalendar = new DaoCalendar(Const.DBMode.READ, mActivity);
        //double total = daoCalendar.getTotal(mType, mPeriod, new Date());
        DaoAccounts daoAccounts = new DaoAccounts(Const.DBMode.READ, mActivity);
        double total = daoAccounts.getTotal(mType, mPeriod);
        progressBar.setMax((int) total);
        progressBar.setProgress((int) singleItem.getAmount());
        double percentage = (singleItem.getAmount() * 100) / total;
        textViewpercentage.setText(NumberUtils.setDecimals(percentage, 1) + " %");
        //Log.i("TOTALE", Double.toString(total));
        return convertView;
    }
}
