package it.unibo.studio.vainigli.lorenzo.budgettracker.database;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Calendar;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.Statistic;

/**
 * Created by Lorenzo on 07/12/2016.
 */

public class DaoCalendar {

    private RegistrersController mRegistrersController;
    private SQLiteDatabase mDatabase;
    private Context mContext;
    private static final String SELECT_ALL = "SELECT " + Const.Calendar.TCOL_DATE + ", " + Const.Calendar.TCOL_ID_MOV + ", " + Const.Calendar.COL_AMOUNT +
            " FROM " + Const.TABLE_CALENDAR;

    public DaoCalendar(Const.DBMode mode, Context context) {
        mRegistrersController = new RegistrersController(context);
        mContext = context;
        if (mode == Const.DBMode.READ) {
            mDatabase = mRegistrersController.getReadableDatabase();
        } else {
            mDatabase = mRegistrersController.getWritableDatabase();
        }
    }

    public static class Builder extends AsyncTask<Void, Integer, Void> {

        private Context mContext;
        private ProgressDialog mDialog;
        private SQLiteDatabase mDatabase;
        private DaoMovements mDaoMovements;

        public Builder(Context context) {
            RegistrersController registrersController = new RegistrersController(context);
            mDatabase = registrersController.getWritableDatabase();
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = DialogUtils.showProgessDialog(mContext, "Calcolo", "Sto calcolando gli importi giorno per giorno...");
            mDialog.setProgress(0);
            mDaoMovements = new DaoMovements(Const.DBMode.READ, mContext);
            int daysCount = DateUtils.getDiffDays(mDaoMovements.getFirstDate(), DateUtils.increment(new Date(), Const.Period.MONTHLY, 1));
            mDialog.setMax(daysCount);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDatabase.delete(Const.TABLE_CALENDAR, null, null);
            List<Movement> movementList = mDaoMovements.getAll();
            Statistic statistic = new Statistic(mContext);
            Date end = DateUtils.increment(new Date(), Const.Period.MONTHLY, 1);
            Date begin = mDaoMovements.getFirstDate();
            Date i = mDaoMovements.getFirstDate();
            while (i.before(end)) {
                for (int j = 0; j < movementList.size(); j++) {
                    //Log.i("CALENDAR", "Date: " + DateUtils.dateToString(i, DateUtils.FORMAT_IT) + " | Movement: " + j + "/" +movementList.size());
                    double amount = statistic.amountOfTheDay(i, movementList.get(j));
                    if (amount != 0) {
                        ContentValues values = new ContentValues();
                        values.put(Const.Calendar.COL_DATE, DateUtils.dateToString(i, DateUtils.FORMAT_SQL));
                        values.put(Const.Calendar.COL_ID_MOV, movementList.get(j).getId());
                        values.put(Const.Calendar.COL_AMOUNT, amount);
                        mDatabase.insert(Const.TABLE_CALENDAR, null, values);
                    }
                    int currentDaysCount = DateUtils.getDiffDays(begin, i);
                    publishProgress(currentDaysCount);
                }
                i = DateUtils.increment(i, Const.Period.DAILY, 1);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDialog.dismiss();
        }
    }

    public void insert(Movement movement) {
        Date end = movement.getEndDate();
        if (movement.getStartDate() != null && movement.getEndDate() != null) {
            for (Date i = movement.getStartDate(); !i.after(end); i = DateUtils.increment(i, Const.Period.DAILY, 1)) {
                double amount = Statistic.amountOfTheDay(i, movement);
                if (amount != 0) {
                    ContentValues values = new ContentValues();
                    values.put(Const.Calendar.COL_DATE, DateUtils.dateToString(i, DateUtils.FORMAT_SQL));
                    values.put(Const.Calendar.COL_ID_MOV, movement.getId());
                    values.put(Const.Calendar.COL_AMOUNT, amount);
                    mDatabase.insert(Const.TABLE_CALENDAR, null, values);
                }
            }
        } else {
            ContentValues values = new ContentValues();
            values.put(Const.Calendar.COL_DATE, DateUtils.dateToString(movement.getDate(), DateUtils.FORMAT_SQL));
            values.put(Const.Calendar.COL_ID_MOV, movement.getId());
            values.put(Const.Calendar.COL_AMOUNT, movement.getAmount());
            mDatabase.insert(Const.TABLE_CALENDAR, null, values);
        }
    }

    public List<Calendar> getAll() {
        Cursor res = mDatabase.rawQuery(SELECT_ALL, null);
        List<Calendar> buffer = new ArrayList<Calendar>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            Calendar calendar = new Calendar();
            calendar.setDate(res.getString(0));
            calendar.setIdMov(res.getString(1));
            calendar.setAmount(res.getString(2));
            buffer.add(calendar);
        }
        return buffer;
    }

    public Calendar getOne(Date date) {
        String dateString = DateUtils.dateToString(date, DateUtils.FORMAT_IT);
        Cursor res = mDatabase.rawQuery(SELECT_ALL + " GROUP BY " + Const.Calendar.TCOL_DATE + " HAVING " + Const.Calendar.TCOL_DATE + " = ?", new String[]{dateString});
        Calendar calendar = new Calendar();
        if (res.getCount() == 0) {
            return null;
        }
        if (res.moveToNext()) {
            calendar.setDate(res.getString(0));
            calendar.setIdMov(res.getString(1));
            calendar.setAmount(res.getString(2));
        }
        return calendar;
    }

    public double getTotal(Const.MovementType type, Const.Period period, Date referenceDate) {
        String query = "SELECT SUM(" + Const.Calendar.TCOL_AMOUNT + ") FROM " + Const.TABLE_CALENDAR;
        switch (type) {
            case ALL:
                // Aggiunta utile solo per evitare problemi di sintassi nella query
                query = query.concat(" WHERE 1");
                break;
            case EXPENSE:
                query = query.concat(" WHERE " + Const.Calendar.TCOL_AMOUNT + " < 0");
                break;
            case INCOME:
                query = query.concat(" WHERE " + Const.Calendar.TCOL_AMOUNT + " > 0");
                break;
            default:
                break;
        }
        String dateString;
        switch (period) {
            case CURRENT:
                dateString = DateUtils.dateToString(referenceDate, DateUtils.FORMAT_SQL);
                query = query.concat(" AND " + Const.Calendar.TCOL_DATE + " < '" + dateString + "'");
                break;
            case YEARLY:
                referenceDate = DateUtils.decrement(referenceDate, Const.Period.YEARLY, 1);
                dateString = DateUtils.dateToString(referenceDate, DateUtils.FORMAT_SQL);
                query = query.concat(" AND " + Const.Calendar.TCOL_DATE + " > '" + dateString + "'");
                break;
            case MONTHLY:
                referenceDate = DateUtils.decrement(referenceDate, Const.Period.MONTHLY, 1);
                dateString = DateUtils.dateToString(referenceDate, DateUtils.FORMAT_SQL);
                query = query.concat(" AND " + Const.Calendar.TCOL_DATE + " > '" + dateString + "'");
                break;
            case WEEKLY:
                referenceDate = DateUtils.decrement(referenceDate, Const.Period.WEEKLY, 1);
                dateString = DateUtils.dateToString(referenceDate, DateUtils.FORMAT_SQL);
                query = query.concat(" AND " + Const.Calendar.TCOL_DATE + " > '" + dateString + "'");
                break;
            default:
                break;
        }
        Cursor res = mDatabase.rawQuery(query, null);
        if (res.getCount() == 0) {
            return 0;
        }
        res.moveToNext();
        if (res.getString(0) == null) {
            return 0;
        }
        return Double.parseDouble(res.getString(0));
    }

    public double getAverage(Const.MovementType type, Const.Period period, Date date) {
        double total = getTotal(type, period, date);
        int count = DateUtils.getCountOfPeriods(mContext, period);
        return total / count;
    }

    public List<Map.Entry<String, Double>> getGrouped(Const.GroupingMode mode) {
        return getGrouped(mode, null, null);
    }

    public List<Map.Entry<String, Double>> getGrouped(Const.GroupingMode mode, Date startDate, Date endDate) {
        String query = "";
        switch (mode) {
            case DAY:
                query = "SELECT SUM(" + Const.Calendar.TCOL_AMOUNT + "), " +
                        "strftime('%Y', " + Const.Calendar.TCOL_DATE + ") as year, " +
                        "strftime('%m', " + Const.Calendar.TCOL_DATE + ") as month, " +
                        "strftime('%W', " + Const.Calendar.TCOL_DATE + ") as week, " +
                        "strftime('%d', " + Const.Calendar.TCOL_DATE + ") as day " +
                        "FROM " + Const.TABLE_CALENDAR;
                break;
            case WEEK:
                query = "SELECT SUM(" + Const.Calendar.TCOL_AMOUNT + "), " +
                        "strftime('%Y', " + Const.Calendar.TCOL_DATE + ") as year, " +
                        "strftime('%m', " + Const.Calendar.TCOL_DATE + ") as month, " +
                        "strftime('%W', " + Const.Calendar.TCOL_DATE + ") as week " +
                        "FROM " + Const.TABLE_CALENDAR;
                break;
            case MONTH:
                query = "SELECT SUM(" + Const.Calendar.TCOL_AMOUNT + "), " +
                        "strftime('%Y', " + Const.Calendar.TCOL_DATE + ") as year, " +
                        "strftime('%m', " + Const.Calendar.TCOL_DATE + ") as month " +
                        "FROM " + Const.TABLE_CALENDAR;
                break;
            case YEAR:
                query = "SELECT SUM(" + Const.Calendar.TCOL_AMOUNT + "), " +
                        "strftime('%Y', " + Const.Calendar.TCOL_DATE + ") as year " +
                        "FROM " + Const.TABLE_CALENDAR;
                break;
            default:
                break;
        }
        if (startDate != null && endDate != null) {
            String startDateString = DateUtils.dateToString(startDate, DateUtils.FORMAT_SQL);
            String endDateString = DateUtils.dateToString(endDate, DateUtils.FORMAT_SQL);
            query = query.concat(" WHERE " + Const.Calendar.TCOL_DATE + " >= '" + startDateString + "' AND " +
                    Const.Calendar.TCOL_DATE + " <= '" + endDateString + "'");
        }
        switch (mode) {
            case DAY:
                query = query.concat(" GROUP BY year, week, month, day");
                break;
            case WEEK:
                query = query.concat(" GROUP BY year, week, month");
                break;
            case MONTH:
                query = query.concat(" GROUP BY year, month");
                break;
            case YEAR:
                query = query.concat(" GROUP BY year");
                break;
            default:
                break;
        }
        Cursor res = mDatabase.rawQuery(query, null);
        List<Map.Entry<String, Double>> buffer = new ArrayList<Map.Entry<String, Double>>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            String description = "";
            switch (mode) {
                case DAY:
                    description = res.getString(1) + "-" + res.getString(2) + "-" + res.getString(4);
                    break;
                case WEEK:
                    description = res.getString(1) + "-" + res.getString(3);
                    break;
                case MONTH:
                    description = res.getString(1) + "-" + res.getString(2);
                    break;
                case YEAR:
                    description = res.getString(1);
                    break;
                default:
                    break;
            }
            description = DateUtils.normalizeLabel(description, mode);
            Double amount = Double.parseDouble(res.getString(0));
            Map.Entry<String, Double> entry = new AbstractMap.SimpleEntry<String, Double>(description, amount);
            buffer.add(entry);
        }
        return buffer;
    }

}
