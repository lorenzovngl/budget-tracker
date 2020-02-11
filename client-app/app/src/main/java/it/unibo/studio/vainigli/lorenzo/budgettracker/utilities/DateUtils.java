package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoMovements;

public class DateUtils {

    public final static String FORMAT_IT = "d/M/yyyy";
    public final static String FORMAT_SQL = "yyyy-MM-dd";
    public static final int DAYS_IN_WEEK = 7;
    public static final int DAYS_IN_MONTH = 30;
    public static final int DAYS_IN_YEAR = 365;

    public static int getValueFromDate(Date date, int what){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int result = calendar.get(what);
        return result;
    }

    public static String getDateFromFormat(String format){
        SimpleDateFormat s = new SimpleDateFormat(format);
        return s.format(new Date());
    }

    public static String getMonthName(Context context, int month){
        String[] namesOfMonths = context.getResources().getStringArray(R.array.months);
        return namesOfMonths[month];
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String longToTimeString(long time){
        return dateToString(new Date(time), "yyyy-MM-dd HH:mm:ss");
    }

    public static Date stringToDate(String string, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            return dateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date stringToDate(String string) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_IT, Locale.getDefault());
        try {
            return dateFormat.parse(string);
        } catch (ParseException e) {
            dateFormat = new SimpleDateFormat(FORMAT_SQL, Locale.getDefault());
            try {
                return dateFormat.parse(string);
            } catch (ParseException ex){
                ex.printStackTrace();
                return null;
            }
        }
    }

    public static String convert(String string, String formatBefore, String formatAfter) {
        Date date = stringToDate(string, formatBefore);
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatAfter, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String normalizeLabel(String dateString, Const.GroupingMode mode){
        String result = "";
        switch (mode){
            case DAY:
                result = DateUtils.convert(dateString, "yyyy-MM-dd", "dd MMMM yyyy");
                break;
            case WEEK:
                String startOfWeek = DateUtils.convert(dateString, "yyyy-ww", "dd MMMM yyyy");
                Date dateStartOfWeek = DateUtils.stringToDate(startOfWeek, "dd MMMM yyyy");
                Date dateEndOfWeek = DateUtils.increment(dateStartOfWeek, Const.Period.DAILY, 6);
                String endOfWeek = DateUtils.dateToString(dateEndOfWeek, "dd MMMM yyyy");
                result = startOfWeek.substring(0, 2) + "-" + endOfWeek;
                break;
            case MONTH:
                result = DateUtils.convert(dateString, "yyyy-MM", "MMMM yyyy");
                break;
            case YEAR:
                result = dateString;
                break;
            default:
                break;
        }
        return result;
    }

    public static Date increment(Date date, Const.Period period, int count){
        Calendar calendar = Calendar.getInstance(); // this would default to now
        calendar.setTime(date);
        switch (period){
            case DAILY:
                calendar.add(Calendar.DAY_OF_MONTH, count);
                break;
            case WEEKLY:
                calendar.add(Calendar.WEEK_OF_MONTH, count);
                break;
            case MONTHLY:
                calendar.add(Calendar.MONTH, count);
                break;
            case TWO_MONTHLY:
                calendar.add(Calendar.MONTH, count*2);
                break;
            case THREE_MONTHLY:
                calendar.add(Calendar.MONTH, count*3);
                break;
            case FOUR_MONTHLY:
                calendar.add(Calendar.MONTH, count*4);
                break;
            case HALF_YEARLY:
                calendar.add(Calendar.MONTH, count*6);
                break;
            case YEARLY:
                calendar.add(Calendar.YEAR, count);
                break;
            default:
                break;
        }
        return calendar.getTime();
    }

    public static Date decrement(Date date, Const.Period period, int count){
        return increment(date, period, -count);
    }

    public static int getCountOfPeriods(Context context, Const.Period period){
        int count = 0;
        MdaoMovements mdaoMovements = new MdaoMovements(Const.DBMode.READ, context);
        Date firstDate = mdaoMovements.getFirstDate();
        if (firstDate == null){
            return -1;
        }
        Date today = new Date();
        long diff = today.getTime() - firstDate.getTime();
        long millisPerDay = 24 * 60 * 60 * 1000;
        switch (period){
            case DAILY:
                count = (int) (diff / (millisPerDay));
                break;
            case WEEKLY:
                count = (int) (diff / (7 * millisPerDay));
                break;
            case MONTHLY:
                count = (int) (diff / (30 * millisPerDay));
                break;
            case YEARLY:
                count = (int) (diff / (365 * millisPerDay ));
                break;
            default:
                break;
        }
        return count+1;
    }

    public static int getDateDiff(Date begin, Date end, TimeUnit timeUnit) {
        long diffInMillies = end.getTime() - begin.getTime();
        return (int) timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static int getDiffDays(Date begin, Date end) {
        if (getDateDiff(begin, end, TimeUnit.HOURS) > 0){
            return getDateDiff(begin, end, TimeUnit.DAYS) + 1;
        } else {
            return getDateDiff(begin, end, TimeUnit.DAYS);
        }
    }
}
