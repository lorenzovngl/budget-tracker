package it.unibo.studio.vainigli.lorenzo.budgettracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.models.User;

/**
 * Created by Lorenzo on 22/02/2017.
 */

public class DaoUsers {

    private Context mContext;
    private UsersController mDbManager;
    private SQLiteDatabase mDatabase;

    private static final String SELECT_ALL = "SELECT " + Const.Users.TCOL_FULLNAME + ", " +
            Const.Users.TCOL_USERNAME + ", " + Const.Users.TCOL_MD5_PASSWORD +
            " FROM " + Const.TABLE_USERS;

    public DaoUsers(Const.DBMode mode, Context context){
        mDbManager = new UsersController(mode, context);
        mContext = context;
        if (mode == Const.DBMode.READ){
            mDatabase = mDbManager.getReadableDatabase();
        } else {
            mDatabase = mDbManager.getWritableDatabase();
        }
    }

    public List<User> getAll(){
        Log.i("QUERY: ", SELECT_ALL);
        Cursor res = mDatabase.rawQuery(SELECT_ALL, null);
        List<User> buffer = new ArrayList<>();
        while (res.moveToNext()) {
            User user = new User();
            user.setFullname(res.getString(0));
            user.setUsername(res.getString(1));
            user.setMD5Password(res.getString(2));
            buffer.add(user);
        }
        return buffer;
    }

    public User getOne(String username){
        Cursor res = mDatabase.rawQuery(SELECT_ALL + " WHERE " + Const.Users.TCOL_USERNAME + " = ?",  new String[]{username});
        User user = new User();
        if (res.moveToNext()){
            user.setFullname(res.getString(0));
            user.setUsername(res.getString(1));
            user.setMD5Password(res.getString(2));
        }
        return user;
    }

    public boolean insert(String fullname, String username, String md5password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.Users.COL_FULLNAME, fullname);
        contentValues.put(Const.Users.COL_USERNAME, username);
        contentValues.put(Const.Users.COL_MD5_PASSWORD, md5password);
        long result = mDatabase.insert(Const.TABLE_USERS, null, contentValues);
        return result > -1;
    }

    public boolean update(User user) {
        if (user.getUsername() == null){
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.Users.COL_FULLNAME, user.getFullname());
        contentValues.put(Const.Users.COL_USERNAME, user.getUsername());
        contentValues.put(Const.Users.COL_MD5_PASSWORD, user.getMD5Password());
        long result = mDatabase.update(Const.TABLE_USERS, contentValues, Const.Users.COL_USERNAME + " = ?", new String[]{user.getUsername()});
        return result > -1;
    }

    public boolean delete(String username) {
        int result = mDatabase.delete(Const.TABLE_USERS, Const.Users.COL_USERNAME + " = ?", new String[]{username});
        return result > 0;
    }

}
