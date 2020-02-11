package it.unibo.studio.vainigli.lorenzo.budgettracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.UsersPrefsController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Category;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;

public class DaoCategories {

    private SQLiteDatabase mDatabase;
    private String mDatabaseName;
    private Context mContext;
    private static final String SELECT_ALL = "SELECT A." + Const.Categories.COL_ID + ", A." + Const.Categories.COL_DESC
            + ", A." + Const.Categories.COL_ICON + ", A." + Const.Categories.COL_TYPE + ", B." + Const.Categories.COL_DESC +
            ", B." + Const.Categories.COL_ICON + " FROM " + Const.TABLE_CATEGORIES + " AS A LEFT JOIN " + Const.TABLE_CATEGORIES + " AS B" +
            " ON A." + Const.Categories.COL_PARENT + " = B." + Const.Categories.COL_ID;

    public DaoCategories(SQLiteDatabase db, Context context){
        mDatabase = db;
        mContext = context;
    }

    public DaoCategories(Const.DBMode mode, Context context){
        RegistrersController registrersController = new RegistrersController(context);
        mContext = context;
        if (mode == Const.DBMode.READ){
            mDatabase = registrersController.getReadableDatabase();
        } else {
            mDatabase = registrersController.getWritableDatabase();
        }
    }

    public DaoCategories(String databaseName, Const.DBMode mode, Context context){
        RegistrersController registrersController = new RegistrersController(context, databaseName);
        mDatabaseName = databaseName;
        Log.i("NOME_DB", databaseName);
        mContext = context;
        if (mode == Const.DBMode.READ){
            mDatabase = registrersController.getReadableDatabase();
        } else {
            mDatabase = registrersController.getWritableDatabase();
        }
    }

    public String getDatabaseName(){
        return mDatabaseName;
    }

    public List<Category> getAll() {
        Cursor res = mDatabase.rawQuery(SELECT_ALL, null);
        List<Category> buffer = new ArrayList<Category>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            Category category = new Category();
            category.setDatabase(mDatabaseName);
            category.setId(res.getString(0));
            category.setDescription(res.getString(1));
            category.setIcon(mContext, res.getString(2));
            category.setType(res.getString(3));
            category.setParent(res.getString(4));
            category.setParentIcon(mContext, res.getString(5));
            buffer.add(category);
        }
        return buffer;
    }

    public List<Category> getAll(Const.Categories.Role role) {
        List<Category> list = getAll();
        if (list == null){
            return null;
        }
        List<Category> buffer = new ArrayList<Category>();
        switch (role){
            case SOURCES:
                for (Category category : list){
                    if (!category.getType().equals(Const.Categories.EXPENSE)){
                        buffer.add(category);
                    }
                }
                break;
            case DESTINATIONS:
                for (Category category : list){
                    if (!category.getType().equals(Const.Categories.INCOME)){
                        buffer.add(category);
                    }
                }
                break;
        }
        return buffer;
    }

    public Category getOne(String id) {
        String query = SELECT_ALL + " WHERE A." + Const.Categories.COL_ID + " = " + id;
        Cursor res = mDatabase.rawQuery(query, null);
        Log.i("QUERY ONE CAT", SELECT_ALL + " WHERE A." + Const.Categories.COL_ID + " = " + id);
        Category category = new Category();
        if (res.getCount() == 0) {
            return null;
        }
        if (res.moveToNext()) {
            category.setDatabase(mDatabaseName);
            category.setId(res.getString(0));
            Log.i("RES", res.getString(1));
            category.setDescription(res.getString(1));
            category.setIcon(mContext, res.getString(2));
            category.setType(res.getString(3));
            category.setParent(res.getString(4));
            category.setParentIcon(mContext, res.getString(5));
        }
        return category;
    }

    public Category getOneByDesc(String description) {
       return getOne(getId(description));
    }

    public Category getOneWithParams(String id) {
        Category category = getOne(id);
        String query = " SELECT res." + Const.Categories.COL_ID + ", SUM(res." + Const.Movements.COL_AMOUNT + "), AVG(res." + Const.Movements.COL_AMOUNT + ")" +
            " FROM (" +
                " SELECT " + Const.Categories.TCOL_ID + ", " + Const.Movements.TCOL_AMOUNT +
                " FROM " + Const.TABLE_CATEGORIES + " INNER JOIN " + Const.TABLE_MOVEMENTS + " ON "
                + Const.Movements.TCOL_SRCCAT + " = " + Const.Categories.TCOL_ID +
                " WHERE " + Const.Movements.TCOL_SRCCAT + " = " + id + " UNION " +
                " SELECT " + Const.Categories.TCOL_ID + ", " + Const.Movements.TCOL_AMOUNT +
                " FROM " + Const.TABLE_CATEGORIES + " INNER JOIN " + Const.TABLE_MOVEMENTS + " ON "
                + Const.Movements.TCOL_DSTCAT + " = " + Const.Categories.TCOL_ID +
                " WHERE " + Const.Movements.TCOL_DSTCAT + " = " + id + " " +
            ") as res GROUP BY res." + Const.Categories.COL_ID;
        Log.i("QUERY2 ONE CAT", query);
        Cursor res = mDatabase.rawQuery(query, null);
        if (res.getCount() == 0) {
            return null;
        }
        if (res.moveToNext()) {
            category.setTotal(res.getString(1));
            category.setAverage(res.getString(2));
            DaoMovements daoMovements = new DaoMovements(mDatabase, mContext);
            List<Movement> movementList = daoMovements.getByCategory(id);
            category.setStdDeviation(movementList);
            category.setAvgPeriodicity(movementList);
            category.setStdDevPeriodicity(movementList);
        }
        return category;
    }

    public String getId(String description) {
        Cursor res = mDatabase.rawQuery(SELECT_ALL + " WHERE A." + Const.Categories.COL_DESC + " = ?",  new String[]{description});
        Log.i("CAT getId", SELECT_ALL + " WHERE A." + Const.Categories.COL_DESC + " = " + description);
        if (res.getCount() == 0) {
            return null;
        }
        res.moveToNext();
        return res.getString(0);
    }

    public String getDescription(String id) {
        Cursor res = mDatabase.rawQuery(SELECT_ALL + " WHERE A." + Const.Categories.COL_ID + " = ?",  new String[]{id});
        if (res.getCount() == 0) {
            return null;
        }
        res.moveToNext();
        return res.getString(0);
    }

    public List<String> getDescriptions(Const.Categories.Hierarchy hierarchy, Const.Categories.Type type, String parentName) {
        List<Category> list = new ArrayList<Category>();
        switch (hierarchy){
            case ALL:
                list = getAll();
                break;
            case PRIMARY:
                list = getPrimaryGroups(type, Const.Period.ALLTIME);
                break;
            case SECONDARY:
                list = getSecondaryGroups(parentName, type, Const.Period.ALLTIME);
                break;
        }
        if (list == null){
            return null;
        }
        List<String> buffer = new ArrayList<String>();
        for (final Category category : list){
            buffer.add(category.getDescription());
        }
        return buffer;
    }

    public List<String> getDescriptions(Const.Categories.Role role, Const.Categories.Type type, String parentName) {
        List<Category> list = getAll();
        List<String> buffer = new ArrayList<String>();
        switch (role){
            case SOURCES:
                for (Category category : list){
                    if (!category.getType().equals(Const.Categories.EXPENSE)){
                        buffer.add(category.getDescription());
                    }
                }
                break;
            case DESTINATIONS:
                for (Category category : list){
                    if (!category.getType().equals(Const.Categories.INCOME)){
                        buffer.add(category.getDescription());
                    }
                }
                break;
        }
        return buffer;
    }

    public List<Category> getPrimaryGroups(Const.Categories.Type type, Const.Period period) {
        String today = DateUtils.dateToString(new Date(), DateUtils.FORMAT_SQL);
        String query = "SELECT " + Const.Categories.TCOL_ID + ", " + Const.Categories.TCOL_DESC + ", ABS(SUM(amount)) as amount, " + Const.Categories.TCOL_ICON + ", " + Const.Categories.TCOL_TYPE +
                " FROM (" +
                    " SELECT " + Const.Categories.TCOL_ID + ", " + Const.Categories.TCOL_DESC + ", IFNULL(SUM(childs.amount),0) AS amount, " + Const.Categories.TCOL_ICON + ", " + Const.Categories.TCOL_TYPE +
                    " FROM categories LEFT JOIN (" +
                " SELECT " + Const.Categories.TCOL_ID + ", " + Const.Categories.TCOL_PARENT + ", IFNULL(-SUM(" + Const.Movements.TCOL_AMOUNT + "),0) AS amount, " + Const.Categories.TCOL_TYPE +
                " FROM " + Const.TABLE_CATEGORIES + " LEFT JOIN " + Const.TABLE_MOVEMENTS + " ON "
                + Const.Movements.TCOL_SRCCAT + " = " + Const.Categories.TCOL_ID +
                " WHERE ";
        if (period == Const.Period.CURRENT) {
            query = query.concat(" IFNULL(" + Const.Movements.TCOL_DATE + ", '" + today + "') <= '" + today + "' AND ");
        }
        query = query.concat(Const.Categories.TCOL_PARENT + " IS NOT NULL" +
                " GROUP BY " + Const.Categories.TCOL_ID + " UNION " +
                " SELECT " + Const.Categories.TCOL_ID + ", " + Const.Categories.TCOL_PARENT + ", IFNULL(SUM(" + Const.Movements.TCOL_AMOUNT + "),0) AS amount, " + Const.Categories.TCOL_TYPE +
                " FROM " + Const.TABLE_CATEGORIES + " LEFT JOIN " + Const.TABLE_MOVEMENTS + " ON "
                + Const.Movements.TCOL_DSTCAT + " = " + Const.Categories.TCOL_ID +
                " WHERE ");
        if (period == Const.Period.CURRENT) {
            query = query.concat(" IFNULL(" + Const.Movements.TCOL_DATE + ", '" + today + "') <= '" + today + "' AND ");
        }
        query = query.concat(Const.Categories.TCOL_PARENT + " IS NOT NULL" +
                " GROUP BY " + Const.Categories.TCOL_ID + ")" +
                " AS childs ON childs." + Const.Categories.COL_PARENT + " = " + Const.Categories.TCOL_ID +
                " WHERE " + Const.Categories.TCOL_PARENT + " IS NULL" +
                " GROUP BY " + Const.Categories.TCOL_ID +
                " UNION" +
                " SELECT categories.id, categories.description, IFNULL(SUM(categories.amount),0) AS amount, categories.icon, categories.type " +
                " FROM ( " +
                " SELECT src_category." + Const.Categories.COL_ID + ", src_category." + Const.Categories.COL_DESC + ", IFNULL(-SUM(" + Const.Movements.TCOL_AMOUNT + "),0) AS amount, src_category." + Const.Categories.COL_ICON + ", src_category." + Const.Categories.COL_TYPE +
                " FROM " + Const.TABLE_CATEGORIES + " AS src_category LEFT JOIN " + Const.TABLE_CATEGORIES + " AS parent ON src_category." + Const.Categories.COL_PARENT + " = parent." + Const.Categories.COL_ID +
                " LEFT JOIN " + Const.TABLE_MOVEMENTS + " ON " + Const.Movements.TCOL_SRCCAT + " = src_category." + Const.Categories.COL_ID +
                " WHERE ");
        if (period == Const.Period.CURRENT) {
            query = query.concat(" IFNULL(" + Const.Movements.TCOL_DATE + ", '" + today + "') <= '" + today + "' AND ");
        }
        query = query.concat(" src_category." + Const.Categories.COL_PARENT + " IS NULL" +
                " GROUP BY src_category." +  Const.Categories.COL_ID + " UNION" +
                " SELECT dst_category." + Const.Categories.COL_ID + ", dst_category." + Const.Categories.COL_DESC + ", IFNULL(SUM(" + Const.Movements.TCOL_AMOUNT + "),0) AS amount, dst_category." + Const.Categories.COL_ICON + ", dst_category." + Const.Categories.COL_TYPE +
                " FROM " + Const.TABLE_CATEGORIES + " AS dst_category LEFT JOIN " + Const.TABLE_CATEGORIES + " AS parent ON dst_category." + Const.Categories.COL_PARENT + " = parent." + Const.Categories.COL_ID +
                " LEFT JOIN " + Const.TABLE_MOVEMENTS + " ON " + Const.Movements.TCOL_DSTCAT + " = dst_category." + Const.Categories.COL_ID +
                " WHERE ");
        if (period == Const.Period.CURRENT) {
            query = query.concat(" IFNULL(" + Const.Movements.TCOL_DATE + ", '" + today + "') <= '" + today + "' AND ");
        }
        query = query.concat(" dst_category." + Const.Categories.COL_PARENT + " IS NULL" +
                        " GROUP BY  dst_category." + Const.Categories.COL_ID + ") AS categories GROUP BY categories.id ) AS " + Const.TABLE_CATEGORIES +
                " WHERE " + Const.Categories.TCOL_TYPE + " = ");
        switch (type){
            case ALL:
                query = query.concat(Const.Categories.EXPENSE + " OR " + Const.Categories.TCOL_TYPE + " = " + Const.Categories.INCOME);
                break;
            case EXPENSES:
                query = query.concat(Const.Categories.EXPENSE);
                break;
            case INCOMES:
                query = query.concat(Const.Categories.INCOME);
                break;
            case ACCOUNTS:
                query = query.concat(Const.Categories.ACCOUNT);
                break;
        }
        query = query.concat(" GROUP BY " + Const.Categories.TCOL_DESC +
                " ORDER BY amount DESC");
        Log.i("Query", query);
        Log.i("Args", today);
        Cursor res = mDatabase.rawQuery(query, null);
        List<Category> buffer = new ArrayList<Category>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            Category category = new Category();
            category.setDatabase(mDatabaseName);
            category.setId(res.getString(0));
            category.setDescription(res.getString(1));
            category.setTotal(res.getString(2));
            category.setIcon(mContext, res.getString(3));
            Log.i("AMOUNT", String.valueOf(category.getTotal()));
            buffer.add(category);
        }
        return buffer;
    }

    public List<Category> getSecondaryGroups(String parentName, Const.Categories.Type type, Const.Period period) {
        String today = DateUtils.dateToString(new Date(), DateUtils.FORMAT_SQL);
        String query = "SELECT " + Const.Categories.TCOL_ID + ", " + Const.Categories.TCOL_DESC + ", IFNULL(SUM(" + Const.Movements.TCOL_AMOUNT + "),0), " + Const.Categories.TCOL_ICON +
                ", PARENT." + Const.Categories.COL_DESC +", PARENT." + Const.Categories.COL_ICON +
                " FROM " + Const.TABLE_CATEGORIES + " LEFT JOIN "  + Const.TABLE_CATEGORIES + " AS parent ON " + Const.Categories.TCOL_PARENT + " = parent." + Const.Categories.COL_ID +
                " LEFT JOIN " + Const.TABLE_MOVEMENTS + " ON " + Const.Movements.TCOL_DSTCAT + " = " + Const.Categories.TCOL_ID +
                " WHERE ";
        if (period == Const.Period.CURRENT) {
            // La stringa today, se passata come parametro con il simbolo "?", non funziona... boh
            query = query.concat(" IFNULL(" + Const.Movements.TCOL_DATE + ", '" + today + "') <= '" + today + "' AND ");
        }
        query = query.concat(Const.Categories.TCOL_PARENT + " = " + getId(parentName)
                + " GROUP BY " + Const.Categories.TCOL_DESC + " ORDER BY ABS(SUM(" + Const.Movements.TCOL_AMOUNT + ")) DESC");
        Log.i("Query", query);
        Cursor res = mDatabase.rawQuery(query, null);
        List<Category> buffer = new ArrayList<Category>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            Category category = new Category();
            category.setDatabase(mDatabaseName);
            category.setId(res.getString(0));
            category.setDescription(res.getString(1));
            category.setTotal(res.getString(2));
            category.setIcon(mContext, res.getString(3));
            category.setParent(res.getString(4));
            category.setParentIcon(mContext, res.getString(5));
            Log.i("AMOUNT", String.valueOf(category.getTotal()));
            buffer.add(category);
        }
        return buffer;
    }

    public double getTotal(Const.Categories.Type type, Const.Period period, String parentName){
        double total = 0;
        List<Category> categories;
        if (parentName == null) {
            categories = getPrimaryGroups(type, period);
        } else {
            categories = getSecondaryGroups(parentName, type, period);
        }
        for (int i = 0; i < categories.size(); i++){
            total += categories.get(i).getTotal();
        }
        Log.i("TOTAL CAT", Double.toString(total));
        return total;
    }

    public boolean insert(String name, String icon, String parentName, String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.Categories.COL_DESC, name);
        if (parentName != null) {
            contentValues.put(Const.Categories.COL_PARENT, getId(parentName));
        }
        contentValues.put(Const.Categories.COL_TYPE, type);
        contentValues.put(Const.Categories.COL_ICON, icon);
        long result = mDatabase.insert(Const.TABLE_CATEGORIES, null, contentValues);
        return result > -1;
    }

    public boolean update(String id, String name, String icon, String parentName, String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.Categories.COL_DESC, name);
        if (parentName != null) {
            contentValues.put(Const.Categories.COL_PARENT, getId(parentName));
        }
        contentValues.put(Const.Categories.COL_TYPE, type);
        contentValues.put(Const.Categories.COL_ICON, icon);
        long result = mDatabase.update(Const.TABLE_CATEGORIES, contentValues, "ID = ?", new String[]{id});
        return result > 0;
    }

    public boolean delete(String id) {
        int result = mDatabase.delete(Const.TABLE_CATEGORIES, "ID = ?", new String[]{id});
        return result > 0;
    }

    public List<String> replaceWithIds(List<String> categories) {
        List<Category> listAll = getAll();
        for (final Category item : listAll){
            // Il foreach non è adatto perché la variabile di iterazione andrebbe modificata
            for (int i = 0; i < categories.size(); i++){
                if (item.getDescription().equals(categories.get(i))){
                    // Per semplicità non faccio la conversione ad intero
                    categories.set(i, String.valueOf(item.getId()));
                }
            }
        }
        return categories;
    }

}
