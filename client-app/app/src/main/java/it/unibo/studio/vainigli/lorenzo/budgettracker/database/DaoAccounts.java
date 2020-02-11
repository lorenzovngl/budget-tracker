package it.unibo.studio.vainigli.lorenzo.budgettracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Account;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;

// TODO da eliminare
public class DaoAccounts {

    private SQLiteDatabase mDatabase;
    private Context mContext;
    private static final String SELECT_ALL = "SELECT " + Const.Accounts.TCOL_ID + ", " + Const.Accounts.TCOL_DESC + ", " + Const.Accounts.TCOL_ICON +
            " FROM " + Const.TABLE_ACCOUNTS;

    public DaoAccounts(SQLiteDatabase database, Context context){
        mDatabase = database;
        mContext = context;
    }

    public DaoAccounts(Const.DBMode mode, Context context){
        RegistrersController registrersController = new RegistrersController(context);
        mContext = context;
        if (mode == Const.DBMode.READ){
            mDatabase = registrersController.getReadableDatabase();
        } else {
            mDatabase = registrersController.getWritableDatabase();
        }
    }

    public List<Account> getAll() {
        Cursor res = mDatabase.rawQuery(SELECT_ALL, null);
        List<Account> buffer = new ArrayList<Account>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            Account account = new Account();
            account.setId(res.getString(0));
            account.setDescription(res.getString(1));
            account.setIcon(mContext, res.getString(2));
            buffer.add(account);
        }
        return buffer;
    }

    public Account getOne(String id) {
        Cursor res = mDatabase.rawQuery(SELECT_ALL + " WHERE " + Const.Accounts.TCOL_ID + " = ?", new String[]{id});
        Account account = new Account();
        if (res.getCount() == 0) {
            return null;
        }
        if (res.moveToNext()) {
            account.setId(res.getString(0));
            account.setDescription(res.getString(1));
            account.setIcon(mContext, res.getString(2));
        }
        return account;
    }

    public String getId(String description) {
        Cursor res = mDatabase.rawQuery(SELECT_ALL + " WHERE " + Const.Accounts.COL_DESC + " = ?",  new String[]{description});
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

    public List<String> getDescriptions() {
        List<Account> list = getAll();
        List<String> buffer = new ArrayList<String>();
        for (final Account account : list){
            buffer.add(account.getDescription());
        }
        return buffer;
    }

    public List<Account> getTotals(Const.MovementType type, Const.Period period) {
        String today = DateUtils.dateToString(new Date(), DateUtils.FORMAT_SQL);
        String query = "SELECT " + Const.Accounts.TCOL_ID + ", " + Const.Accounts.TCOL_DESC + ", IFNULL(SUM(" + Const.Movements.TCOL_AMOUNT + "),0), " + Const.Accounts.TCOL_ICON +
                " FROM " + Const.TABLE_ACCOUNTS /*+" LEFT JOIN "  + Const.TABLE_MOVEMENTS + " ON "
                + Const.Movements.TCOL_ACCOUNT + " = " + Const.Accounts.TCOL_ID*/;
        if (period == Const.Period.CURRENT) {
            query = query.concat(" WHERE IFNULL(" + Const.Movements.TCOL_DATE + ", '" + today + "') <= '" + today + "'");
        }
        switch (type){
            case EXPENSE:
                query = query.concat(" AND " + Const.Movements.TCOL_AMOUNT + " < 0");
                break;
            case INCOME:
                query = query.concat(" AND " + Const.Movements.TCOL_AMOUNT + " > 0");
                break;
            default:
                break;
        }
        query = query.concat(" GROUP BY " + Const.Accounts.TCOL_DESC + " ORDER BY SUM(" + Const.Movements.TCOL_AMOUNT + ") DESC");
        Log.i("Query", query);
        Cursor res = mDatabase.rawQuery(query, null);
        List<Account> buffer = new ArrayList<Account>();
        if (res.getCount() == 0) {
            return null;
        }
        while (res.moveToNext()) {
            Account account = new Account();
            account.setId(res.getString(0));
            account.setDescription(res.getString(1));
            account.setAmount(res.getString(2));
            account.setIcon(mContext, res.getString(3));
            buffer.add(account);
        }
        return buffer;
    }

    public double getTotal(Const.MovementType type, Const.Period period){
        double total = 0;
        List<Account> accounts = getTotals(type, period);
        for (int i = 0; i < accounts.size(); i++){
            total += accounts.get(i).getAmount();
        }
        return total;
    }

    public boolean insert(String name, String icon) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.Accounts.COL_DESC, name);
        contentValues.put(Const.Accounts.COL_ICON, icon);
        long result = mDatabase.insert(Const.TABLE_ACCOUNTS, null, contentValues);
        return result > -1;
    }

    public boolean update(String id, String name, String icon) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.Accounts.COL_DESC, name);
        contentValues.put(Const.Accounts.COL_ICON, icon);
        long result = mDatabase.update(Const.TABLE_ACCOUNTS, contentValues, "ID = ?", new String[]{id});
        return result > 0;
    }

    public boolean delete(String id) {
        int result = mDatabase.delete(Const.TABLE_ACCOUNTS, "ID = ?", new String[]{id});
        return result > 0;
    }

    public List<String> replaceWithIds(List<String> accounts) {
        List<Account> listAll = getAll();
        for (final Account item : listAll){
            // Il foreach non è adatto perché la variabile di iterazione andrebbe modificata
            for (int i = 0; i < accounts.size(); i++){
                if (item.getDescription().equals(accounts.get(i))){
                    // Per semplicità non faccio la conversione ad intero
                    accounts.set(i, String.valueOf(item.getId()));
                }
            }
        }
        return accounts;
    }

}
