package it.unibo.studio.vainigli.lorenzo.budgettracker.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;

public class RegistrersController extends SQLiteOpenHelper {

    // TODO spostare progressivamente tutte le costanti in Const
    public static final String TABLE_MOVEMENTS = "movements";
    public static final String TABLE_LOCATIONS = "locations";
    public static final String COL_DATE = "date";
    public static final int ALL = 0;
    public static final int EXPENSE = 1;
    public static final int INCOME = 2;

    private boolean isCreating = false;
    private SQLiteDatabase currentDB = null;
    private Context mContext;

    public RegistrersController(Context context) {
        // Create the database
        super(context, Const.DATABASE_NAME, null, 1);
        mContext = context;
    }

    public RegistrersController(Context context, String databaseName) {
        // Create the database
        super(context, FileUtils.newDatabaseName(context, databaseName), null, 1);
        mContext = context;
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
        db.execSQL("CREATE TABLE " + Const.TABLE_CATEGORIES + " ("
                + Const.Categories.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Const.Categories.COL_DESC + " TEXT, "
                + Const.Categories.COL_PARENT + " INTEGER, "
                + Const.Categories.COL_TYPE + " INTEGER, "
                + Const.Categories.COL_ICON + " TEXT) ");
        db.execSQL("CREATE TABLE " + Const.TABLE_MOVEMENTS + " ("
                + Const.Movements.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Const.Movements.COL_DATE + " DATE, "
                + Const.Movements.COL_DESC + " TEXT, "
                + Const.Movements.COL_SRCCAT + " INTEGER, "
                + Const.Movements.COL_DSTCAT + " INTEGER, "
                + Const.Movements.COL_AMOUNT + " DOUBLE, "
                + Const.Movements.COL_START_DATE + " DATE, "
                + Const.Movements.COL_END_DATE + " DATE) ");
        // Sviluppo futuro
        db.execSQL("CREATE TABLE " + Const.TABLE_CALENDAR + " ("
                + Const.Calendar.COL_DATE + " DATE, "
                + Const.Calendar.COL_ID_MOV + " INTEGER, "
                + Const.Calendar.COL_AMOUNT + " DOUBLE,"
                + "PRIMARY KEY (" + Const.Calendar.COL_DATE + ", " + Const.Calendar.COL_ID_MOV + ") ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    public boolean isEmpty() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MOVEMENTS;
        Cursor res = db.rawQuery(query, null);
        if (res.moveToNext()){
            return false;
        } else {
            return true;
        }
    }

    public void deleteDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_MOVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_CALENDAR);
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_LOCATIONS);
        onCreate(db);
    }

    /*public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_MOVEMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_CALENDAR);
        db.execSQL("DROP TABLE IF EXISTS " + Const.TABLE_LOCATIONS);
        onCreate(db);
        // Add default value for all tables
        isCreating = true;
        currentDB = db;
        this.insertExampleData();
        // release var
        isCreating = false;
        currentDB = null;
    }*/
}
