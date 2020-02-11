package it.unibo.studio.vainigli.lorenzo.budgettracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.User;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.HashUtils;

public class DaoRegisters {

    private Context mContext;
    private UsersController mDbManager;
    private SQLiteDatabase mDatabase;

    private static final String SELECT_ALL = "SELECT " + Const.Registers.TCOL_ID + ", " + Const.Registers.TCOL_REMOTE_ID + ", " +
            Const.Registers.TCOL_NAME + ", " + Const.Registers.TCOL_MD5_PASSWORD + ", " + Const.Registers.TCOL_OWNER +
            " FROM " + Const.TABLE_REGISTERS + " LEFT JOIN "  + Const.TABLE_SHARED_USERS + " ON "
            + Const.Registers.TCOL_ID + " = " + Const.SharedUsers.COL_ID;

    public DaoRegisters(Const.DBMode mode, Context context){
        mDbManager = new UsersController(mode, context);
        mContext = context;
        if (mode == Const.DBMode.READ){
            mDatabase = mDbManager.getReadableDatabase();
        } else {
            mDatabase = mDbManager.getWritableDatabase();
        }
    }

    public List<Register> getAll(){
        Log.i("QUERY: ", SELECT_ALL);
        Cursor res = mDatabase.rawQuery(SELECT_ALL, null);
        List<Register> buffer = new ArrayList<>();
        while (res.moveToNext()) {
            Register register = new Register();
            register.setId(res.getString(0));
            register.setRemoteId(res.getString(1));
            register.setName(res.getString(2));
            register.setMD5Password(res.getString(3));
            register.setOwner(res.getString(4));
            register.setPath(FileUtils.newDatabasePath(mContext, register.getName()));
            File file = new File(FileUtils.newDatabasePath(mContext, register.getName()));
            register.setSize(file.length());
            register.setLastEdit(file.lastModified());
            buffer.add(register);
        }
        return buffer;
    }

    public Register getOne(String name, String owner){
        String query = SELECT_ALL + " WHERE " + Const.Registers.TCOL_NAME + " = ? AND " + Const.Registers.TCOL_OWNER + " = ?";
        Cursor res = mDatabase.rawQuery(query,  new String[]{name, owner});
        Register register = new Register();
        if (res.moveToNext()){
            register.setId(res.getString(0));
            register.setRemoteId(res.getString(1));
            register.setName(res.getString(2));
            register.setMD5Password(res.getString(3));
            register.setOwner(res.getString(4));
            register.setPath(FileUtils.newDatabasePath(mContext, register.getName()));
            File file = new File(FileUtils.newDatabasePath(mContext, register.getName()));
            register.setSize(file.length());
            register.setLastEdit(file.lastModified());
        }
        return register;
    }

    public boolean insert(String name, String password, String owner) {
        String query = SELECT_ALL + " WHERE " + Const.Registers.TCOL_NAME + " = ? AND " + Const.Registers.TCOL_OWNER + " = ?";
        Cursor res = mDatabase.rawQuery(query,  new String[]{name, owner});
        if (res.getCount() > 0){
            // Non ci possono essere due database con lo stesso nome per un singolo utente
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.Registers.COL_NAME, name);
        contentValues.put(Const.Registers.COL_OWNER, owner);
        contentValues.put(Const.Registers.COL_MD5_PASSWORD, HashUtils.md5(password));
        long result = mDatabase.insert(Const.TABLE_REGISTERS, null, contentValues);
        new RegistrersController(mContext, name).getReadableDatabase();
        return result > -1;
    }

    public boolean update(String remote_id, String name, String password) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.Registers.COL_REMOTE_ID, remote_id);
        contentValues.put(Const.Registers.COL_NAME, name);
        contentValues.put(Const.Registers.COL_MD5_PASSWORD, password);
        long result = mDatabase.update(Const.TABLE_REGISTERS, contentValues, Const.Registers.COL_NAME + " = ?", new String[]{name});
        return result > -1;
    }

    public boolean delete(String name, String owner) {
        return delete(getOne(name, owner).getName());
    }

    public boolean delete(String id) {
        int result = mDatabase.delete(Const.TABLE_REGISTERS, Const.Registers.COL_ID + " = ?", new String[]{id});
        return result > 0;
    }

}
