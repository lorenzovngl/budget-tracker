package it.unibo.studio.vainigli.lorenzo.budgettracker.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsersController extends SQLiteOpenHelper {

    private boolean isCreating = false;
    private SQLiteDatabase currentDB = null;
    private Context mContext;

    public UsersController(Const.DBMode mode, Context context){
        super(context, Const.DATABASES_INFO, null, 1);
        mContext = context;
        switch (mode){
            case READ:
                currentDB = getReadableDatabase();
                break;
            case WRITE:
                currentDB = getWritableDatabase();
                break;
        }
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        // TODO Auto-generated method stub
        if (isCreating && currentDB != null) {
            return currentDB;
        }
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        // TODO Auto-generated method stub
        if (isCreating && currentDB != null) {
            return currentDB;
        }
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Const.TABLE_USERS + " ("
                + Const.Users.COL_FULLNAME + " TEXT , "
                + Const.Users.COL_USERNAME + " TEXT PRIMARY KEY, "
                + Const.Users.COL_MD5_PASSWORD + " TEXT) ");
        db.execSQL("CREATE TABLE " + Const.TABLE_REGISTERS + " ("
                + Const.Registers.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Const.Registers.COL_REMOTE_ID + " INTEGER , "
                + Const.Registers.COL_NAME + " TEXT, "
                + Const.Registers.COL_MD5_PASSWORD + " TEXT, "
                + Const.Registers.COL_OWNER + " TEXT) ");
        // Sviluppo futuro
        db.execSQL("CREATE TABLE " + Const.TABLE_SHARED_USERS + " ("
                + Const.SharedUsers.COL_ID + " INTEGER, "
                + Const.SharedUsers.COL_NAME + " TEXT,"
                + "PRIMARY KEY (" + Const.SharedUsers.COL_ID + ", " + Const.SharedUsers.COL_NAME + ") ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteDatabase() {
        currentDB.execSQL("DELETE FROM " + Const.TABLE_REGISTERS);
    }
}
