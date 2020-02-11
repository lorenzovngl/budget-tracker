package it.unibo.studio.vainigli.lorenzo.budgettracker.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Category;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;

public class MdaoCategories {

    private SQLiteDatabase mDatabase;
    private Context mContext;
    private static final String SELECT_ALL = "SELECT A." + Const.Categories.COL_ID + ", A." + Const.Categories.COL_DESC
            + ", A." + Const.Categories.COL_ICON + ", B." + Const.Categories.COL_DESC + ", B." + Const.Categories.COL_ICON +
            " FROM " + Const.TABLE_CATEGORIES + " AS A LEFT JOIN " + Const.TABLE_CATEGORIES + " AS B" +
            " ON A." + Const.Categories.COL_PARENT + " = B." + Const.Categories.COL_ID;

    private List<DaoCategories> mDaoCategories;

    public MdaoCategories(Const.DBMode mode, Context context){
        mDaoCategories = new ArrayList<>();
        List<Register> list = FileUtils.getFileList(context, Register.class);
        for (Register register : list){
            Log.i("MDAO DB_NAME", register.getName());
            // TODO togliere il seguente controllo e risolvere il problema
            if (!register.getName().equals(Const.DATABASE_NAME) && !register.getName().equals(Const.DATABASES_INFO) ){
                mDaoCategories.add(new DaoCategories(register.getName(), mode, context));
            }
        }
        mContext = context;
    }

    public DaoCategories getByDatabase(String databaseName){
        DaoCategories target = null;
        for (int i = 0; i<mDaoCategories.size(); i++){
            Log.i("MDAO DB_NAME", mDaoCategories.get(i).getDatabaseName());
            if (mDaoCategories.get(i).getDatabaseName().equals(databaseName)){
                target = mDaoCategories.get(i);
            }
        }
        return target;
    }

    public List<Category> getAll() {
        Cursor res = mDatabase.rawQuery(SELECT_ALL, null);
        List<Category> buffer = new ArrayList<Category>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            Category category = new Category();
            category.setId(res.getString(0));
            category.setDescription(res.getString(1));
            category.setIcon(mContext, res.getString(2));
            category.setParent(res.getString(3));
            category.setParentIcon(mContext, res.getString(4));
            buffer.add(category);
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
            category.setId(res.getString(0));
            Log.i("RES", res.getString(1));
            category.setDescription(res.getString(1));
            category.setIcon(mContext, res.getString(2));
            category.setParent(res.getString(3));
            category.setParentIcon(mContext, res.getString(4));
        }
        return category;
    }

    public Category getOneWithParams(String databaseName, String id) {
        return getByDatabase(databaseName).getOneWithParams(id);
    }

    public String getId(String databaseName, String description) {
        return getByDatabase(databaseName).getId(description);
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
        List<String> buffer = new ArrayList<String>();
        for (DaoCategories daoCategories : mDaoCategories){
            buffer.addAll(daoCategories.getDescriptions(hierarchy, type, parentName));
        }
        return buffer;
    }

    public List<Category> getPrimaryGroups(Const.Categories.Type type, Const.Period period) {
        List<Category> buffer = new ArrayList<>();
        for (int i = 0; i<mDaoCategories.size(); i++){
            buffer.addAll(mDaoCategories.get(i).getPrimaryGroups(type, period));
        }
        return buffer;
    }

    public List<Category> getSecondaryGroups(String databaseName, String parentName, Const.Categories.Type type, Const.Period period) {
        Log.i("DB_NAME", databaseName);
        DaoCategories target = getByDatabase(databaseName);
        return target.getSecondaryGroups(parentName, type, period);
    }

    public double getTotal(String databaseName, Const.Categories.Type type, Const.Period period, String parentName){
        List<Category> categories = new ArrayList<Category>();;
        if (parentName == null) {
            for (int i = 0; i<mDaoCategories.size(); i++){
                categories.addAll(mDaoCategories.get(i).getPrimaryGroups(type, period));
            }
        } else {
            categories = getSecondaryGroups(databaseName, parentName, type, period);
        }
        double total = 0;
        for (int i = 0; i < categories.size(); i++){
            total += categories.get(i).getTotal();
        }
        return total;
    }

    public boolean insert(String databaseName, String name, String icon, String parentName, String type) {
        Log.i("DB-NAME", databaseName);
        return getByDatabase(databaseName).insert(name, icon, parentName, type);
    }

    public boolean update(String databaseName, String id, String name, String icon, String parentName, String type) {
        return getByDatabase(databaseName).update(id, name, icon, parentName, type);
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
