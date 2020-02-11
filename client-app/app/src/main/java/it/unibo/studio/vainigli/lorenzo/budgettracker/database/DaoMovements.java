package it.unibo.studio.vainigli.lorenzo.budgettracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.UsersPrefsController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.ListItem;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.FiltersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.StringUtils;

public class DaoMovements {

    private RegistrersController mDbManager;
    private SQLiteDatabase mDatabase;
    private String mDatabaseName;
    private Context mContext;
    private static final String SELECT_ALL = "SELECT " + Const.Movements.TCOL_ID + ", " + Const.Movements.TCOL_DATE + ", " +
            Const.Movements.TCOL_DESC + ", src_category." + Const.Categories.COL_DESC + ", dst_category." + Const.Categories.COL_DESC +
            ", " + Const.Movements.TCOL_AMOUNT + ", " + Const.Movements.TCOL_START_DATE + ", " + Const.Movements.TCOL_END_DATE +
            " FROM " + Const.TABLE_MOVEMENTS + " INNER JOIN "  + Const.TABLE_CATEGORIES + " AS src_category ON "
            + Const.Movements.TCOL_SRCCAT + " = src_category." + Const.Categories.COL_ID +
            " INNER JOIN "  + Const.TABLE_CATEGORIES + " AS dst_category ON " + 
            Const.Movements.TCOL_DSTCAT + " = dst_category." + Const.Categories.COL_ID;

    public DaoMovements(SQLiteDatabase db, Context context){
        mDatabase = db;
        mContext = context;
    }

    public DaoMovements(Const.DBMode mode, Context context){
        mDbManager = new RegistrersController(context);
        mContext = context;
        if (mode == Const.DBMode.READ){
            mDatabase = mDbManager.getReadableDatabase();
        } else {
            mDatabase = mDbManager.getWritableDatabase();
        }
    }

    public DaoMovements(String databaseName, Const.DBMode mode, Context context){
        mDbManager = new RegistrersController(context, databaseName);
        mDatabaseName = databaseName;
        mContext = context;
        if (mode == Const.DBMode.READ){
            mDatabase = mDbManager.getReadableDatabase();
        } else {
            mDatabase = mDbManager.getWritableDatabase();
        }
    }

    public String getDatabaseName(){
        return mDatabaseName;
    }

    public List<Movement> getAll() {
        Log.i("QUERY: ", SELECT_ALL);
        Cursor res = mDatabase.rawQuery(SELECT_ALL + " ORDER BY " + Const.Movements.TCOL_DATE, null);
        List<Movement> buffer = new ArrayList<Movement>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            Movement movement = new Movement();
            movement.setDatabase(mDatabaseName);
            movement.setId(res.getString(0));
            movement.setDate(res.getString(1));
            movement.setDescription(res.getString(2));
            movement.setSrcCategory(res.getString(3));
            movement.setDstCategory(res.getString(4));
            movement.setAmount(res.getString(5));
            movement.setPeriod(res.getString(6), res.getString(7));
            //movement.setAddress(res.getString(8));
            buffer.add(movement);
        }
        return buffer;
    }

    public List<Movement> getUpcomings(String categoryId, int limit, Const.Categories.Type type) {
        String today = DateUtils.dateToString(new Date(), DateUtils.FORMAT_SQL);
        String query;
        if (type != Const.Categories.Type.ACCOUNTS){
            query = SELECT_ALL + " WHERE " + Const.Movements.TCOL_DATE + " > '" + today + "'";
            if (categoryId != null){
                query = query.concat(" AND (" + Const.Movements.TCOL_SRCCAT + " = " + categoryId +
                        " OR " + Const.Movements.TCOL_DSTCAT + " = " + categoryId + ")");
            }
        } else {
            // Cambia solo il segno dell'importo
            String SELECT_ALL_MINUS = "SELECT " + Const.Movements.TCOL_ID + ", " + Const.Movements.TCOL_DATE + ", " +
                    Const.Movements.TCOL_DESC + ", src_category." + Const.Categories.COL_DESC + ", dst_category." + Const.Categories.COL_DESC +
                    ", -" + Const.Movements.TCOL_AMOUNT + ", " + Const.Movements.TCOL_START_DATE + ", " + Const.Movements.TCOL_END_DATE +
                    " FROM " + Const.TABLE_MOVEMENTS + " INNER JOIN "  + Const.TABLE_CATEGORIES + " AS src_category ON "
                    + Const.Movements.TCOL_SRCCAT + " = src_category." + Const.Categories.COL_ID +
                    " INNER JOIN "  + Const.TABLE_CATEGORIES + " AS dst_category ON " +
                    Const.Movements.TCOL_DSTCAT + " = dst_category." + Const.Categories.COL_ID;
            query = " SELECT * FROM (" + SELECT_ALL + " WHERE " + Const.Movements.TCOL_DATE + " > '" + today + "'";
            if (categoryId != null){
                query = query.concat(" AND (" + Const.Movements.TCOL_DSTCAT + " = " + categoryId + ")");
            }
            query = query.concat(" UNION " + SELECT_ALL_MINUS + " WHERE " + Const.Movements.TCOL_DATE + " > '" + today + "'");
            if (categoryId != null){
                query = query.concat(" AND (" + Const.Movements.TCOL_SRCCAT + " = " + categoryId + ")");
            }
            query = query.concat(") as " + Const.TABLE_MOVEMENTS);
        }
        query = query.concat(" ORDER BY " + Const.Movements.TCOL_DATE + " ASC");
        if (limit > 0){
            query = query.concat(" LIMIT " + String.valueOf(limit));
        }
        Cursor res = mDatabase.rawQuery(query, null);
        Log.i("GETUPCOMINGS", query);
        List<Movement> buffer = new ArrayList<Movement>();
        while (res.moveToNext()) {
            Movement movement = new Movement();
            movement.setDatabase(mDatabaseName);
            movement.setId(res.getString(0));
            movement.setDate(res.getString(1));
            movement.setDescription(res.getString(2));
            movement.setSrcCategory(res.getString(3));
            movement.setDstCategory(res.getString(4));
            movement.setAmount(res.getString(5));
            movement.setPeriod(res.getString(6), res.getString(7));
            //movement.setAddress(res.getString(8));
            buffer.add(movement);
        }
        return buffer;
    }

    public List<Movement> getPrevious(String categoryId, int limit, String order, Const.Categories.Type type) {
        String today = DateUtils.dateToString(new Date(), DateUtils.FORMAT_SQL);
        String query;
        if (type != Const.Categories.Type.ACCOUNTS){
            query = SELECT_ALL + " WHERE " + Const.Movements.TCOL_DATE + " <= '" + today + "'";
            if (categoryId != null){
                query = query.concat(" AND (" + Const.Movements.TCOL_SRCCAT + " = " + categoryId +
                        " OR " + Const.Movements.TCOL_DSTCAT + " = " + categoryId + ")");
            }
        } else {
            // Cambia solo il segno dell'importo
            String SELECT_ALL_MINUS = "SELECT " + Const.Movements.TCOL_ID + ", " + Const.Movements.TCOL_DATE + ", " +
                    Const.Movements.TCOL_DESC + ", src_category." + Const.Categories.COL_DESC + ", dst_category." + Const.Categories.COL_DESC +
                    ", -" + Const.Movements.TCOL_AMOUNT + ", " + Const.Movements.TCOL_START_DATE + ", " + Const.Movements.TCOL_END_DATE +
                    " FROM " + Const.TABLE_MOVEMENTS + " INNER JOIN "  + Const.TABLE_CATEGORIES + " AS src_category ON "
                    + Const.Movements.TCOL_SRCCAT + " = src_category." + Const.Categories.COL_ID +
                    " INNER JOIN "  + Const.TABLE_CATEGORIES + " AS dst_category ON " +
                    Const.Movements.TCOL_DSTCAT + " = dst_category." + Const.Categories.COL_ID;
            query = " SELECT * FROM (" + SELECT_ALL + " WHERE " + Const.Movements.TCOL_DATE + " <= '" + today + "'";
            if (categoryId != null){
                query = query.concat(" AND (" + Const.Movements.TCOL_DSTCAT + " = " + categoryId + ")");
            }
            query = query.concat(" UNION " + SELECT_ALL_MINUS + " WHERE " + Const.Movements.TCOL_DATE + " <= '" + today + "'");
            if (categoryId != null){
                query = query.concat(" AND (" + Const.Movements.TCOL_SRCCAT + " = " + categoryId + ")");
            }
            query = query.concat(") as " + Const.TABLE_MOVEMENTS);
        }
        query = query.concat(" ORDER BY " + Const.Movements.TCOL_DATE + " " + order);
        if (limit > 0){
            query = query.concat(" LIMIT " + String.valueOf(limit));
        }
        Cursor res = mDatabase.rawQuery(query, null);
        Log.i("GETPREVIOUS", query);
        List<Movement> buffer = new ArrayList<Movement>();
        while (res.moveToNext()) {
            Movement movement = new Movement();
            movement.setDatabase(mDatabaseName);
            movement.setId(res.getString(0));
            movement.setDate(res.getString(1));
            movement.setDescription(res.getString(2));
            movement.setSrcCategory(res.getString(3));
            movement.setDstCategory(res.getString(4));
            movement.setAmount(res.getString(5));
            movement.setPeriod(res.getString(6), res.getString(7));
            //movement.setAddress(res.getString(8));
            buffer.add(movement);
        }
        return buffer;
    }

    public double getTotal(Const.MovementType type, Const.Period period, Date referenceDate) {
        String query = "SELECT SUM(balance) FROM (" +
                " SELECT movements.amount*(src_category.type+dst_category.type) as balance " +
                " FROM movements INNER JOIN categories AS src_category ON movements.src_category = src_category.id " +
                " INNER JOIN categories AS dst_category ON movements.dst_category = dst_category.id ";
        switch (type) {
            case ALL:
                // Aggiunta utile solo per evitare problemi di sintassi nella query
                query = query.concat(" WHERE 1");
                break;
            case EXPENSE:
                query = query.concat(" WHERE balance < 0");
                break;
            case INCOME:
                query = query.concat(" WHERE balance > 0");
                break;
            default:
                break;
        }
        String dateString;
        switch (period) {
            case CURRENT:
                dateString = DateUtils.dateToString(referenceDate, DateUtils.FORMAT_SQL);
                query = query.concat(" AND " + Const.Movements.TCOL_DATE + " < '" + dateString + "'");
                break;
            case YEARLY:
                referenceDate = DateUtils.decrement(referenceDate, Const.Period.YEARLY, 1);
                dateString = DateUtils.dateToString(referenceDate, DateUtils.FORMAT_SQL);
                query = query.concat(" AND " + Const.Movements.TCOL_DATE + " > '" + dateString + "'");
                break;
            case MONTHLY:
                referenceDate = DateUtils.decrement(referenceDate, Const.Period.MONTHLY, 1);
                dateString = DateUtils.dateToString(referenceDate, DateUtils.FORMAT_SQL);
                query = query.concat(" AND " + Const.Movements.TCOL_DATE + " > '" + dateString + "'");
                break;
            case WEEKLY:
                referenceDate = DateUtils.decrement(referenceDate, Const.Period.WEEKLY, 1);
                dateString = DateUtils.dateToString(referenceDate, DateUtils.FORMAT_SQL);
                query = query.concat(" AND " + Const.Movements.TCOL_DATE + " > '" + dateString + "'");
                break;
            default:
                break;
        }
        Cursor res = mDatabase.rawQuery(query.concat(")"), null);
        if (res.getCount() == 0) {
            return 0;
        }
        res.moveToNext();
        if (res.getString(0) == null) {
            return 0;
        }
        return Double.parseDouble(res.getString(0));
    }

    public double getMin(List<Movement> movementList){
        double min = Math.abs(movementList.get(0).getAmount());
        for (int i = 0; i < movementList.size(); i++){
            if (Math.abs(movementList.get(i).getAmount()) < min){
                min = Math.abs(movementList.get(i).getAmount());
            }
        }
        return min;
    }

    public double getMin(List<Movement> movementList, double balance){
        double min = balance + movementList.get(0).getAmount();
        for (int i = 0; i < movementList.size(); i++){
            if (balance + movementList.get(i).getAmount() < min){
                min = balance + movementList.get(i).getAmount();
            }
        }
        return min;
    }

    public double getMax(List<Movement> movementList){
        double max = Math.abs(movementList.get(0).getAmount());
        for (int i = 0; i < movementList.size(); i++){
            if (Math.abs(movementList.get(i).getAmount()) > max){
                max = Math.abs(movementList.get(i).getAmount());
            }
        }
        return max;
    }

    public double getMax(List<Movement> movementList, double balance){
        double max = balance + movementList.get(0).getAmount();
        for (int i = 0; i < movementList.size(); i++){
            if (balance + movementList.get(i).getAmount() > max){
                max = balance + movementList.get(i).getAmount();
            }
        }
        return max;
    }

    public double getAverage(Const.MovementType type, Const.Period period, Date date) {
        double total = getTotal(type, period, date);
        int count = DateUtils.getCountOfPeriods(mContext, period);
        return total / count;
    }

    /*public List<Map.Entry<String, Double>> getGrouped(Const.GroupingMode mode) {
        String query = "";
        switch (mode){
            case DAY:
                query = "SELECT SUM(amount), strftime('%Y', date) as year, strftime('%m', date) as month, strftime('%W', date) as week, strftime('%d', date) as day " +
                        "FROM "+ Const.TABLE_MOVEMENTS +" GROUP BY year, week, month, day";
                break;
            case WEEK:
                query = "SELECT SUM(amount), strftime('%Y', date) as year, strftime('%m', date) as month, strftime('%W', date) as week " +
                        "FROM "+ Const.TABLE_MOVEMENTS +" GROUP BY year, month, week";
                break;
            case MONTH:
                query = "SELECT SUM(amount), strftime('%Y', date) as year, strftime('%m', date) as month " +
                        "FROM "+ Const.TABLE_MOVEMENTS +" GROUP BY year, month";
                break;
            case YEAR:
                query = "SELECT SUM(amount), strftime('%Y', date) as year " +
                        "FROM "+ Const.TABLE_MOVEMENTS +" GROUP BY year";
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
            switch (mode){
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
            Double amount = Double.parseDouble(res.getString(0));
            Map.Entry<String, Double> entry = new AbstractMap.SimpleEntry<String, Double>(description, amount);
            buffer.add(entry);
        }
        return buffer;
    }*/

    public List<Movement> getOnesWithDepreciation(Const.Depreciation depreciation) {
        String today = DateUtils.dateToString(new Date(), DateUtils.FORMAT_SQL);
        String query = SELECT_ALL + " WHERE NOT " + Const.Movements.TCOL_START_DATE + " IS NULL";
        switch (depreciation){
            case EXPIRED:
                query = query.concat(" AND " + Const.Movements.TCOL_END_DATE + " < '" + today + "'");
                break;
            case CURRENT:
                query = query.concat(" AND " + Const.Movements.TCOL_START_DATE + " <= '" + today + "' AND " + Const.Movements.TCOL_END_DATE + " >= '" + today + "'");
                break;
            case FUTURE:
                query = query.concat(" AND " + Const.Movements.TCOL_START_DATE + " > '" + today + "'");
                break;
            default:
                break;
        }
        query = query.concat(" ORDER BY " + Const.Movements.TCOL_DATE);
        Log.i("QUERY: ", query);
        Cursor res = mDatabase.rawQuery(query, null);
        List<Movement> buffer = new ArrayList<Movement>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            Movement movement = new Movement();
            movement.setId(res.getString(0));
            movement.setDate(res.getString(1));
            movement.setDescription(res.getString(2));
            movement.setSrcCategory(res.getString(3));
            movement.setDstCategory(res.getString(4));
            movement.setAmount(res.getString(5));
            movement.setPeriod(res.getString(6), res.getString(7));
            //movement.setAddress(res.getString(8));
            buffer.add(movement);
        }
        return buffer;
    }

    public Movement getOne(String id) {
        Cursor res = mDatabase.rawQuery(SELECT_ALL + " WHERE " + Const.Movements.TCOL_ID + " = ?", new String[]{id});
        Movement movement = new Movement();
        if (res.getCount() == 0) {
            return null;
        }
        if (res.moveToNext()) {
            movement.setId(res.getString(0));
            movement.setDate(res.getString(1));
            movement.setDescription(res.getString(2));
            movement.setSrcCategory(res.getString(3));
            movement.setDstCategory(res.getString(4));
            movement.setAmount(res.getString(5));
            movement.setPeriod(res.getString(6), res.getString(7));
            //movement.setAddress(res.getString(8));
        }
        return movement;
    }

    public String getId(String description) {
        Cursor res = mDatabase.rawQuery(SELECT_ALL + " WHERE " + Const.Categories.COL_DESC + " = ?",  new String[]{description});
        if (res.getCount() == 0) {
            return null;
        }
        res.moveToNext();
        return res.getString(0);
    }

    public String getDescription(String id) {
        Cursor res = mDatabase.rawQuery(SELECT_ALL + " WHERE " + Const.Categories.COL_ID + " = ?",  new String[]{id});
        if (res.getCount() == 0) {
            return null;
        }
        res.moveToNext();
        return res.getString(0);
    }

    public List<Movement> getFilteredData(String columnToGroupBy, String columnToOrderBy, String order) {
        FiltersController filtersController = new FiltersController(mContext);
        if (filtersController.isEnabled()){
            String[] srcCategories = null;
            String[] dstCategories = null;
            String startDate = null;
            String endDate = null;
            String startAmount = null;
            String endAmount = null;
            if (filtersController.isEnabledSrcCategories()){
                srcCategories = StringUtils.setToArray(filtersController.getSrcCategories());
            }
            if (filtersController.isEnabledDstCategories()){
                dstCategories = StringUtils.setToArray(filtersController.getDstCategories());
            }
            if (filtersController.isEnabledDate()){
                startDate = filtersController.getStartDate();
                endDate = filtersController.getEndDate();
            }
            if (filtersController.isEnabledDate()){
                startAmount = filtersController.getStartAmount()!=-1 ? String.valueOf(filtersController.getStartAmount()) : null;
                endAmount = filtersController.getEndAmount()!=-1 ? String.valueOf(filtersController.getEndAmount()) : null;
            }
            return getData(srcCategories, dstCategories, startDate, endDate, startAmount, endAmount, columnToGroupBy, columnToOrderBy, order, -1);
        } else {
            return getAll();
        }
    }

    public List<Movement> getByCategory(String categoryId) {
        return getByCategories(new String[]{categoryId});
    }

    public List<Movement> getByCategories(String[] categoryIds) {
        String query = SELECT_ALL + " WHERE ";
        for (int i = 0; i < categoryIds.length; i++) {
            if (i != 0) {
                query = query.concat(" OR ");
            }
            query = query.concat(" (src_category = " + categoryIds[i] + " OR dst_category = " + categoryIds[i] + ") ");
        }
        query = query.concat(" ORDER BY date(" + Const.Movements.TCOL_DATE + ")");
        Log.i("CAT QUERY", query);
        Cursor res = mDatabase.rawQuery(query, null);
        List<Movement> buffer = new ArrayList<Movement>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            Movement movement = new Movement();
            movement.setId(res.getString(0));
            movement.setDate(res.getString(1));
            movement.setDescription(res.getString(2));
            movement.setSrcCategory(res.getString(3));
            movement.setDstCategory(res.getString(4));
            movement.setAmount(res.getString(5));
            buffer.add(movement);
        }
        return buffer;
    }

    public List<Movement> getData(String[] srcCategories, String[] dstCategories, String startDate, String endDate, String startAmount, String endAmount, String columnToGroupBy, String columnToOrderBy, String order, int limit) {
        boolean srcCategIsSet = srcCategories != null && srcCategories.length > 0;
        boolean dstCategIsSet = dstCategories != null && dstCategories.length > 0;
        boolean startDateIsSet = startDate != null && !startDate.equals("");
        boolean endDateIsSet = endDate != null && !endDate.equals("");
        boolean startAmountIsSet = startAmount != null && !startAmount.equals("");
        boolean endAmountIsSet = endAmount != null && !endAmount.equals("");
        boolean columnToGroupByIsSet = columnToGroupBy != null && !columnToGroupBy.equals("");
        boolean columnToOrderByIsSet = columnToOrderBy != null && !columnToOrderBy.equals("");
        boolean limitIsSet = limit > 0;
        boolean whereClauseIsRequired = srcCategIsSet || dstCategIsSet || startDateIsSet || endDateIsSet || startAmountIsSet || endAmountIsSet;
        boolean queryIsRequired = whereClauseIsRequired || columnToGroupByIsSet || columnToOrderByIsSet || limitIsSet;
        List<String> args = new ArrayList<String>();
        if (queryIsRequired) {
            String query = SELECT_ALL;
            if (whereClauseIsRequired) {
                query = query.concat(" WHERE ");
            }
            // Condizioni sulle categorie di fonti
            if (srcCategIsSet) {
                boolean first = true;
                for (int i = 0; i < srcCategories.length; i++) {
                    // Non inserisce l'or davanti alla prima condizione
                    if (!first) {
                        query = query.concat(" OR ");
                    } else {
                        query = query.concat("( ");
                        first = false;
                    }
                    query = query.concat(" src_category." + Const.Movements.COL_DESC + " = ?");
                    if (i == srcCategories.length-1){
                        query = query.concat(" )");
                    }
                }
                args.addAll(StringUtils.arrayToList(srcCategories));
            }
            if (srcCategIsSet && dstCategIsSet) {
                query = query.concat(" AND ");
            }
            // Condizioni sulle categorie di destinazione
            if (dstCategIsSet) {
                boolean first = true;
                for (int i = 0; i < dstCategories.length; i++) {
                    // Non inserisce l'or davanti alla prima condizione
                    if (!first) {
                        query = query.concat(" OR ");
                    } else {
                        query = query.concat("( ");
                        first = false;
                    }
                    query = query.concat(" dst_category." + Const.Movements.COL_DESC + " = ?");
                    if (i == dstCategories.length-1){
                        query = query.concat(" )");
                    }
                }
                args.addAll(StringUtils.arrayToList(dstCategories));
            }
            if (dstCategIsSet && startDateIsSet) {
                query = query.concat(" AND ");
            }
            // Condizioni sul periodo
            if (startDateIsSet) {
                query = query.concat(Const.Movements.TCOL_DATE + " >= ?");
                startDate = DateUtils.convert(startDate, DateUtils.FORMAT_IT, DateUtils.FORMAT_SQL);
                args.addAll(new ArrayList<String>(StringUtils.arrayToList(new String[]{startDate})));
            }
            if (startDateIsSet && endDateIsSet) {
                query = query.concat(" AND ");
            }
            if (endDateIsSet) {
                query = query.concat(Const.Movements.TCOL_DATE + " <= ?");
                endDate = DateUtils.convert(endDate, DateUtils.FORMAT_IT, DateUtils.FORMAT_SQL);
                args.addAll(new ArrayList<String>(StringUtils.arrayToList(new String[]{endDate})));
            }
            if (endDateIsSet && startAmountIsSet) {
                query = query.concat(" AND ");
            }
            // Condizioni sugli importi
            if (startAmountIsSet) {
                query = query.concat(Const.Movements.TCOL_AMOUNT + " >= ?");
                args.addAll(new ArrayList<String>(StringUtils.arrayToList(new String[]{startAmount})));
            }
            if (startAmountIsSet && endAmountIsSet) {
                query = query.concat(" AND ");
            }
            if (endAmountIsSet) {
                query = query.concat(Const.Movements.TCOL_AMOUNT + " <= ?");
                args.addAll(new ArrayList<String>(StringUtils.arrayToList(new String[]{endAmount})));
            }
            if (columnToGroupByIsSet) {
                query = query.concat(" GROUP BY " + columnToGroupBy);
            }
            if (columnToOrderByIsSet) {
                if (columnToOrderBy.equals(Const.Movements.TCOL_DATE)) {
                    query = query.concat(" ORDER BY date(" + columnToOrderBy + ")");
                } else {
                    query = query.concat(" ORDER BY " + columnToOrderBy);
                }
                if (order != null && !order.equals("")) {
                    query = query.concat(" " + order);
                }
            }
            if (limitIsSet) {
                query = query.concat(" LIMIT " + limit);
            }
            Log.i("Query:", query);
            Log.d("Query args:", StringUtils.printStringList(args));
            Cursor res = mDatabase.rawQuery(query, StringUtils.listToArray(args));
            //Cursor res = mDatabase.rawQuery(query, null);
            List<Movement> buffer = new ArrayList<Movement>();
            while (res.moveToNext()) {
                Movement movement = new Movement();
                movement.setDatabase(mDatabaseName);
                movement.setId(res.getString(0));
                movement.setDate(res.getString(1));
                movement.setDescription(res.getString(2));
                movement.setSrcCategory(res.getString(3));
                movement.setDstCategory(res.getString(4));
                movement.setAmount(res.getString(5));
                buffer.add(movement);
            }
            return buffer;
        } else {
            return getAll();
        }
    }

    public static List<ListItem> sectionedByMonth(Context context, List<Movement> bufferMovements) {
        List<ListItem> buffer = new ArrayList<ListItem>();
        if (bufferMovements == null || bufferMovements.size() == 0) {
            return null;
        }
        int mainMonth = -1;
        int i = 0;
        while (i < bufferMovements.size()) {
            int currentMonth = DateUtils.getValueFromDate(bufferMovements.get(i).getDate(), Calendar.MONTH);
            ListItem item;
            if (currentMonth != mainMonth) {
                item = new ListItem(ListItem.SECTION, DateUtils.getMonthName(context, currentMonth), null);
                mainMonth = currentMonth;
            } else {
                item = new ListItem(ListItem.ITEM, null, bufferMovements.get(i));
                i++;
            }
            buffer.add(item);
        }
        return buffer;
    }

    public ContentValues toContentValues(Movement movement){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.Movements.COL_DATE, DateUtils.dateToString(movement.getDate(), DateUtils.FORMAT_SQL));
        contentValues.put(Const.Movements.COL_DESC, movement.getDescription());
        /*DaoAccounts daoAccounts = new DaoAccounts(mDatabase, mContext);
        String idAccount = daoAccounts.getId(movement.getAccount());
        contentValues.put(Const.Movements.COL_ACCOUNT, idAccount);*/
        DaoCategories daoCategories = new DaoCategories(mDatabase, mContext);
        String idCategory = daoCategories.getId(movement.getSrcCategory());
        Log.i("CATEGORY", idCategory);
        contentValues.put(Const.Movements.COL_SRCCAT, idCategory);
        idCategory = daoCategories.getId(movement.getDstCategory());
        Log.i("CATEGORY", idCategory);
        contentValues.put(Const.Movements.COL_DSTCAT, idCategory);
        contentValues.put(Const.Movements.COL_AMOUNT, movement.getAmount());
        contentValues.put(Const.Movements.COL_START_DATE, movement.getStringStartDate(DateUtils.FORMAT_SQL));
        contentValues.put(Const.Movements.COL_END_DATE, movement.getStringEndDate(DateUtils.FORMAT_SQL));
        return contentValues;
    }


    public boolean insert(Movement movement) {
        ContentValues contentValues = toContentValues(movement);
        long result = mDatabase.insert(Const.TABLE_MOVEMENTS, null, contentValues);
        movement.setId(String.valueOf((int) result));
        //DaoCalendar daoCalendar = new DaoCalendar(Const.DBMode.READ, mContext);
        //daoCalendar.insert(movement);
        return result > -1;
    }

    public boolean insertPeriodic(Movement movement, Const.Period period, int count) {
        Date date = movement.getDate();
        boolean result = false;
        for (int i = 0; i < count; i++) {
            movement.setDate(DateUtils.dateToString(date, DateUtils.FORMAT_SQL));
            if (movement.getStartDate() != null && movement.getEndDate() != null){
                movement.setPeriod(movement.getPeriod(), movement.getPeriodCount());
            }
            result = insert(movement);
            date = DateUtils.increment(date, period, 1);
            // La seguente riga permette di mantenere la logica della data di fine inclusa nel periodo
            date = DateUtils.decrement(date, Const.Period.DAILY, 1);
        }
        return result;
    }

    public boolean update(String id, Movement movement) {
        ContentValues contentValues = toContentValues(movement);
        long result = mDatabase.update(Const.TABLE_MOVEMENTS, contentValues, "ID = ?", new String[]{id});
        return result > -1;
    }

    public boolean delete(String id) {
        int result = mDatabase.delete(Const.TABLE_MOVEMENTS, "ID = ?", new String[]{id});
        return result > 0;
    }

    public Date getFirstDate() {
        String query = "SELECT MIN(" + Const.Movements.TCOL_DATE + ") FROM " + Const.TABLE_MOVEMENTS;
        Cursor res = mDatabase.rawQuery(query, null);
        Date firstDate = null;
        while (res.moveToNext()){
            firstDate = DateUtils.stringToDate(res.getString(0), DateUtils.FORMAT_SQL);
        }
        return firstDate;
    }

    public List<Map.Entry<String, Double>> getGrouped(Const.GroupingMode mode) {
        return getGrouped(mode, null, null);
    }

    public List<Map.Entry<String, Double>> getGrouped(Const.GroupingMode mode, Date startDate, Date endDate) {
        String query = "";
        switch (mode) {
            case DAY:
                query = "SELECT SUM(" + Const.Movements.TCOL_AMOUNT + "), " +
                        "strftime('%Y', " + Const.Movements.TCOL_DATE + ") as year, " +
                        "strftime('%m', " + Const.Movements.TCOL_DATE + ") as month, " +
                        "strftime('%W', " + Const.Movements.TCOL_DATE + ") as week, " +
                        "strftime('%d', " + Const.Movements.TCOL_DATE + ") as day " +
                        "FROM " + Const.TABLE_MOVEMENTS;
                break;
            case WEEK:
                query = "SELECT SUM(" + Const.Movements.TCOL_AMOUNT + "), " +
                        "strftime('%Y', " + Const.Movements.TCOL_DATE + ") as year, " +
                        "strftime('%m', " + Const.Movements.TCOL_DATE + ") as month, " +
                        "strftime('%W', " + Const.Movements.TCOL_DATE + ") as week " +
                        "FROM " + Const.TABLE_MOVEMENTS;
                break;
            case MONTH:
                query = "SELECT SUM(" + Const.Movements.TCOL_AMOUNT + "), " +
                        "strftime('%Y', " + Const.Movements.TCOL_DATE + ") as year, " +
                        "strftime('%m', " + Const.Movements.TCOL_DATE + ") as month " +
                        "FROM " + Const.TABLE_MOVEMENTS;
                break;
            case YEAR:
                query = "SELECT SUM(" + Const.Movements.TCOL_AMOUNT + "), " +
                        "strftime('%Y', " + Const.Movements.TCOL_DATE + ") as year " +
                        "FROM " + Const.TABLE_MOVEMENTS;
                break;
            default:
                break;
        }
        if (startDate != null && endDate != null) {
            String startDateString = DateUtils.dateToString(startDate, DateUtils.FORMAT_SQL);
            String endDateString = DateUtils.dateToString(endDate, DateUtils.FORMAT_SQL);
            query = query.concat(" WHERE " + Const.Movements.TCOL_DATE + " >= '" + startDateString + "' AND " +
                    Const.Movements.TCOL_DATE + " <= '" + endDateString + "'");
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
