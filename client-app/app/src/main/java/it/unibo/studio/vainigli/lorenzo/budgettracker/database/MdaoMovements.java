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
import java.util.Set;

import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.FiltersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.ListItem;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.SortUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.StringUtils;

public class MdaoMovements {

    private SQLiteDatabase mDatabase;
    private Context mContext;
    /*private static final String SELECT_ALL = "SELECT " + Const.Movements.TCOL_ID + ", " + Const.Movements.TCOL_DATE + ", " +
            Const.Movements.TCOL_DESC + ", " + Const.Accounts.TCOL_DESC + " as account, " + Const.Categories.TCOL_DESC + " as category, " +
            Const.Movements.TCOL_AMOUNT + ", " + Const.Movements.TCOL_START_DATE + ", " + Const.Movements.TCOL_END_DATE + ", " +
            Const.Movements.TCOL_ADDR + ", " + Const.Locations.TCOL_LAT + ", " + Const.Locations.TCOL_LNG +
            " FROM " + Const.TABLE_MOVEMENTS + " INNER JOIN "  + Const.TABLE_ACCOUNTS + " ON "
            + Const.Movements.TCOL_ACCOUNT + " = " + Const.Accounts.TCOL_ID +
            " INNER JOIN "  + Const.TABLE_CATEGORIES + " ON " + Const.Movements.TCOL_CATEG + " = " + Const.Categories.TCOL_ID +
            " INNER JOIN "  + Const.TABLE_LOCATIONS + " ON " + Const.Movements.TCOL_ADDR + " = " + Const.Locations.TCOL_ADDR;*/
    private static final String SELECT_ALL = "SELECT " + Const.Movements.TCOL_ID + ", " + Const.Movements.TCOL_DATE + ", " +
            Const.Movements.TCOL_DESC + ", src_category." + Const.Categories.COL_DESC + ", dst_category." + Const.Categories.COL_DESC +
            ", " + Const.Movements.TCOL_AMOUNT + ", " + Const.Movements.TCOL_START_DATE + ", " + Const.Movements.TCOL_END_DATE +
            " FROM " + Const.TABLE_MOVEMENTS + " INNER JOIN "  + Const.TABLE_CATEGORIES + " AS src_category ON "
            + Const.Movements.TCOL_SRCCAT + " = src_category." + Const.Categories.COL_ID +
            " INNER JOIN "  + Const.TABLE_CATEGORIES + " AS dst_category ON " +
            Const.Movements.TCOL_DSTCAT + " = dst_category." + Const.Categories.COL_ID;

    private List<DaoMovements> mDaoMovements;

    public MdaoMovements(Const.DBMode mode, Context context){
        mDaoMovements = new ArrayList<>();
        List<Register> list = FileUtils.getFileList(context, Register.class);
        for (Register database : list){
            Log.i("DB_NAME", database.getName());
            // TODO togliere il seguente controllo e risolvere il problema
            if (!database.getName().equals(Const.DATABASE_NAME) && !database.getName().equals(Const.DATABASES_INFO)){
                mDaoMovements.add(new DaoMovements(database.getName(), mode, context));
            }
        }
        mContext = context;
    }

    public List<DaoMovements> getDaos(){
        return mDaoMovements;
    }

    public DaoMovements getByDatabase(String databaseName){
        DaoMovements target = null;
        for (int i = 0; i<mDaoMovements.size(); i++){
            if (mDaoMovements.get(i).getDatabaseName().equals(databaseName)){
                target = mDaoMovements.get(i);
            }
        }
        return target;
    }

    public List<Movement> getAll() {
        List<Movement> movements = new ArrayList<Movement>();
        for (DaoMovements daoMovements : mDaoMovements){
            movements.addAll(daoMovements.getAll());
        }
        return SortUtils.sortListByDate(movements, Const.ORDER_ASC);
    }

    public List<Movement> getUpcomings(String databaseName, String categoryId, int limit, Const.Categories.Type type) {
        DaoMovements target = getByDatabase(databaseName);
        if (target != null){
            return target.getUpcomings(categoryId, limit, type);
        } else {
            return null;
        }
    }

    public List<Movement> getUpcomings(String categoryId, int limit, Const.Categories.Type type) {
        List<Movement> movements = new ArrayList<>();
        for (DaoMovements daoMovements : mDaoMovements){
            movements.addAll(daoMovements.getUpcomings(categoryId, limit, type));
        }
        movements = SortUtils.sortListByDate(movements, Const.ORDER_ASC);
        //movements = movements.subList(0, limit+1);
        return movements;
    }

    public List<Movement> getPrevious(String categoryId, int limit, String order, Const.Categories.Type type) {
        List<Movement> movements = new ArrayList<>();
        for (DaoMovements daoMovements : mDaoMovements){
            movements.addAll(daoMovements.getPrevious(categoryId, limit, order, type));
        }
        movements = SortUtils.sortListByDate(movements, order);
        //movements = movements.subList(0, limit+1);
        return movements;
    }

    public List<Movement> getPrevious(String databaseName, String categoryId, int limit, String order, Const.Categories.Type type) {
        DaoMovements target = getByDatabase(databaseName);
        if (target != null){
            return target.getPrevious(categoryId, limit, order, type);
        } else {
            return null;
        }
    }

    public double getTotal(Const.MovementType type, Const.Period period, Date referenceDate) {
        double total = 0;
        for (DaoMovements daoMovements : mDaoMovements){
            total += daoMovements.getTotal(type, period, referenceDate);
        }
        return total;
    }

    public double getMin(String databaseName, List<Movement> movementList){
        DaoMovements target = getByDatabase(databaseName);
        if (target != null){
            return target.getMin(movementList);
        } else {
            return 0;
        }
    }

    public double getMax(String databaseName, List<Movement> movementList){
        DaoMovements target = getByDatabase(databaseName);
        if (target != null){
            return target.getMax(movementList);
        } else {
            return 0;
        }
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
        List<Movement> movements = new ArrayList<>();
        FiltersController filtersController = new FiltersController(mContext);
        Set<String> databases = filtersController.getDatabases();
        Log.i("ACTIVE DB", StringUtils.printStringList(StringUtils.setToList(databases)));
        for (DaoMovements daoMovements : mDaoMovements){
            Log.i("MDAO DBNAME", daoMovements.getDatabaseName());
            if (filtersController.isEnabled() && filtersController.isEnabledDatabases()){
                Log.i("DATABASE NAME CMP", daoMovements.getDatabaseName() + " " + Boolean.toString(databases.contains(daoMovements.getDatabaseName())));
                if (databases.contains(daoMovements.getDatabaseName())) {
                    movements.addAll(daoMovements.getFilteredData(columnToGroupBy, columnToOrderBy, order));
                }
            } else {
                movements.addAll(daoMovements.getFilteredData(columnToGroupBy, columnToOrderBy, order));
            }
        }
        return SortUtils.sortListByDate(movements, order);
    }

    public List<Movement> getByCategory(String databaseName, String categoryId) {
        return getByCategories(databaseName, new String[]{categoryId});
    }

    public List<Movement> getByCategories(String databaseName, String[] categoryIds) {
        return getByDatabase(databaseName).getByCategories(categoryIds);
    }

    public List<Movement> getData(String[] accounts, String[] categories, String startDate, String endDate, String startAmount, String endAmount, String columnToGroupBy, String columnToOrderBy, String order, int limit) {
        boolean accountsIsSet = accounts != null && accounts.length > 0;
        boolean categoriesIsSet = categories != null && categories.length > 0;
        boolean startDateIsSet = startDate != null && !startDate.equals("");
        boolean endDateIsSet = endDate != null && !endDate.equals("");
        boolean startAmountIsSet = startAmount != null && !startAmount.equals("");
        boolean endAmountIsSet = endAmount != null && !endAmount.equals("");
        boolean columnToGroupByIsSet = columnToGroupBy != null && !columnToGroupBy.equals("");
        boolean columnToOrderByIsSet = columnToOrderBy != null && !columnToOrderBy.equals("");
        boolean limitIsSet = limit > 0;
        boolean whereClauseIsRequired = accountsIsSet || categoriesIsSet || startDateIsSet || endDateIsSet || startAmountIsSet || endAmountIsSet;
        boolean queryIsRequired = whereClauseIsRequired || columnToGroupByIsSet || columnToOrderByIsSet || limitIsSet;
        List<String> args = new ArrayList<String>();
        if (queryIsRequired) {
            String query = SELECT_ALL;
            if (whereClauseIsRequired) {
                query = query.concat(" WHERE ");
            }
            // Condizioni sui depositi
            if (accountsIsSet) {
                boolean first = true;
                for (int i = 0; i < accounts.length; i++) {
                    // Non inserisce l'or davanti alla prima condizione
                    if (!first) {
                        query = query.concat(" OR ");
                    } else {
                        query = query.concat("( ");
                        first = false;
                    }
                    query = query.concat(" account = ?");
                    if (i == accounts.length-1){
                        query = query.concat(" )");
                    }
                }
                args.addAll(StringUtils.arrayToList(accounts));
            }
            if (accountsIsSet && categoriesIsSet) {
                query = query.concat(" AND ");
            }
            // Condizioni sulle categorie
            if (categoriesIsSet) {
                boolean first = true;
                for (int i = 0; i < categories.length; i++) {
                    // Non inserisce l'or davanti alla prima condizione
                    if (!first) {
                        query = query.concat(" OR ");
                    } else {
                        query = query.concat("( ");
                        first = false;
                    }
                    query = query.concat(" category = ?");
                    if (i == categories.length-1){
                        query = query.concat(" )");
                    }
                }
                args.addAll(StringUtils.arrayToList(categories));
            }
            if (categoriesIsSet && startDateIsSet) {
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
        } else {
            return getAll();
        }
    }

    public List<ListItem> getMovementArraySectionedByMonth(List<Movement> bufferMovements) {
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
                item = new ListItem(ListItem.SECTION, DateUtils.getMonthName(mContext, currentMonth), null);
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
        DaoMovements target = null;
        for (DaoMovements daoMovements : mDaoMovements){
            if (daoMovements.getDatabaseName().equals(movement.getDatabase())){
                target = daoMovements;
            }
        }
        if (target != null){
            return target.insert(movement);
        } else {
            return false;
        }
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

    public Date getFirstDate() {
        Date minDate = new Date();
        for (DaoMovements daoMovements : mDaoMovements){
            if (daoMovements.getFirstDate().before(minDate)){
                minDate = daoMovements.getFirstDate();
            }
        }
        return minDate;
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
