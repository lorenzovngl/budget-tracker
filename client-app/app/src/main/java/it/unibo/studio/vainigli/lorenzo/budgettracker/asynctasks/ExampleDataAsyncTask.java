package it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.UsersPrefsController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoAccounts;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoRegisters;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.RegistrersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.UsersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoCategories;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.MdaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;

// Questo task non è utilizzabile dall'utente finale

public class ExampleDataAsyncTask extends AsyncTask<Void, Integer, Void> {

    private Context mContext;
    private DaoAccounts mDaoAccounts;
    private DaoCategories mDaoCategories, mDaoCategories2;
    private MdaoCategories mMdaoCategories;
    private MdaoMovements mMdaoMovements;
    private DaoMovements mDaoMovements1, mDaoMovements2;
    private SQLiteDatabase mDatabase, mDB1, mDB2;
    private ProgressDialog mDialog;
    private int mProgessMax, mProgessCount;
    private long mStartTime, mEndTime;

    public ExampleDataAsyncTask(Context context){
        mContext = context;
        mProgessMax = 30;
        mProgessCount = 0;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = DialogUtils.showProgessDialog(mContext, "Calcolo", "Sto inserendo i dati di esempio");
        mDialog.setProgress(0);
        // Il calcolo è manuale
        mDialog.setMax(mProgessMax);
        mStartTime = System.currentTimeMillis();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        UsersController usersController = new UsersController(Const.DBMode.WRITE, mContext);
        usersController.deleteDatabase();
        FileUtils.deleteDatabases(mContext);
        DaoRegisters daoRegisters = new DaoRegisters(Const.DBMode.WRITE, mContext);
        daoRegisters.insert(Const.DB1_NAME, "database1", UsersPrefsController.getUsername(mContext));
        daoRegisters.insert(Const.DB2_NAME, "database2", UsersPrefsController.getUsername(mContext));
        RegistrersController registrersController1 = new RegistrersController(mContext, Const.DB1_NAME);
        mDB1 = registrersController1.getWritableDatabase();
        registrersController1.deleteDatabase();
        RegistrersController registrersController2 = new RegistrersController(mContext, Const.DB2_NAME);
        mDB2 = registrersController2.getWritableDatabase();
        registrersController2.deleteDatabase();
        mDaoCategories = new DaoCategories(mDB1, mContext);
        mDaoCategories2 = new DaoCategories(mDB2, mContext);
        mMdaoCategories = new MdaoCategories(Const.DBMode.WRITE, mContext);
        mMdaoMovements = new MdaoMovements(Const.DBMode.WRITE, mContext);
        mMdaoCategories.insert(Const.DB1_NAME, "Portafoglio", Const.Icons.WALLET, null, Const.Categories.ACCOUNT);
        publishProgress(++mProgessCount); // 1
        mMdaoCategories.insert(Const.DB1_NAME, "Conto bancario", Const.Icons.BANK, null, Const.Categories.ACCOUNT);
        publishProgress(++mProgessCount); // 2
        mMdaoCategories.insert(Const.DB1_NAME, "Stipendio", Const.Icons.WORK, null, Const.Categories.INCOME);
        publishProgress(++mProgessCount); // 3
        mMdaoCategories.insert(Const.DB1_NAME, "Affitto", Const.Icons.HOME, null, Const.Categories.EXPENSE);
        publishProgress(++mProgessCount); // 4
        mMdaoCategories.insert(Const.DB1_NAME, "Viaggi", Const.Icons.BUS, null, Const.Categories.EXPENSE);
        publishProgress(++mProgessCount); // 5
        mMdaoCategories.insert(Const.DB1_NAME, "Bollette", Const.Icons.EMAIL, null, Const.Categories.EXPENSE);
        publishProgress(++mProgessCount); // 6
        mMdaoCategories.insert(Const.DB1_NAME, "Bollette Elettricità", Const.Icons.LIGHTBULB, "Bollette", Const.Categories.EXPENSE);
        publishProgress(++mProgessCount); // 7
        mMdaoCategories.insert(Const.DB1_NAME, "Bollette Acqua", Const.Icons.WATER, "Bollette", Const.Categories.EXPENSE);
        publishProgress(++mProgessCount); // 8
        mMdaoCategories.insert(Const.DB1_NAME, "Bollette Gas", Const.Icons.GAS, "Bollette", Const.Categories.EXPENSE);
        publishProgress(++mProgessCount); // 9
        mMdaoCategories.insert(Const.DB1_NAME, "Bollette Telefono", Const.Icons.PHONE, "Bollette", Const.Categories.EXPENSE);
        publishProgress(++mProgessCount); // 10
        mMdaoCategories.insert(Const.DB1_NAME, "Supermercato", Const.Icons.STORE, null, Const.Categories.EXPENSE);
        publishProgress(++mProgessCount); // 11
        mMdaoCategories.insert(Const.DB2_NAME, "Conto postale", Const.Icons.BANK, null, Const.Categories.ACCOUNT);
        publishProgress(++mProgessCount); // 12
        mMdaoCategories.insert(Const.DB2_NAME, "Bonifici IN", Const.Icons.WALLET, null, Const.Categories.INCOME);
        publishProgress(++mProgessCount); // 13
        mMdaoCategories.insert(Const.DB2_NAME, "Auto", Const.Icons.CAR, null, Const.Categories.EXPENSE);
        publishProgress(++mProgessCount); // 14
        mMdaoCategories.insert(Const.DB2_NAME, "Cellulare", Const.Icons.PHONE_ANDROID, null, Const.Categories.EXPENSE);
        publishProgress(++mProgessCount); // 15
        Movement movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("27/1/2016");
        movement.setDescription("Stipendio");
        movement.setSrcCategory("Stipendio");
        movement.setDstCategory("Conto bancario");
        movement.setAmount("800");
        insertPeriodic(movement, Const.Period.MONTHLY, 12);
        publishProgress(++mProgessCount); // 16
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("30/3/2016");
        movement.setDescription("Prelevamento");
        movement.setSrcCategory("Conto bancario");
        movement.setDstCategory("Portafoglio");
        movement.setAmount("1000");
        insertPeriodic(movement, Const.Period.MONTHLY, 4);
        publishProgress(++mProgessCount); // 17
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("1/1/2016");
        movement.setDescription("Affitto");
        movement.setSrcCategory("Conto bancario");
        movement.setDstCategory("Affitto");
        movement.setAmount("385");
        movement.setPeriod(Const.Period.MONTHLY, 1);
        insertPeriodic(movement, Const.Period.MONTHLY, 24);
        publishProgress(++mProgessCount); // 18
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("11/9/2016");
        movement.setDescription("Bolletta Enel");
        movement.setSrcCategory("Conto bancario");
        movement.setDstCategory("Bollette Elettricità");
        movement.setAmount("50");
        movement.setPeriod(Const.Period.MONTHLY, 2);
        mMdaoMovements.insert(movement);
        publishProgress(++mProgessCount); // 19
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("9/10/2016");
        movement.setDescription("Bolletta Enel");
        movement.setSrcCategory("Conto bancario");
        movement.setDstCategory("Bollette Elettricità");
        movement.setAmount("75");
        movement.setPeriod(Const.Period.MONTHLY, 2);
        mMdaoMovements.insert(movement);
        publishProgress(++mProgessCount); // 20
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("15/11/2016");
        movement.setDescription("Bolletta Enel");
        movement.setSrcCategory("Conto bancario");
        movement.setDstCategory("Bollette Elettricità");
        movement.setAmount("60");
        movement.setPeriod(Const.Period.MONTHLY, 2);
        mMdaoMovements.insert(movement);
        publishProgress(++mProgessCount); // 21
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("12/12/2016");
        movement.setDescription("Bolletta Enel");
        movement.setSrcCategory("Conto bancario");
        movement.setDstCategory("Bollette Elettricità");
        movement.setAmount("55");
        mMdaoMovements.insert(movement);
        publishProgress(++mProgessCount); // 22
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("10/1/2017");
        movement.setDescription("Bolletta Enel");
        movement.setSrcCategory("Conto bancario");
        movement.setDstCategory("Bollette Elettricità");
        movement.setAmount("40");
        mMdaoMovements.insert(movement);
        publishProgress(++mProgessCount); // 23
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("15/1/2016");
        movement.setDescription("Bolletta Tim");
        movement.setSrcCategory("Conto bancario");
        movement.setDstCategory("Bollette Telefono");
        movement.setAmount("45");
        movement.setPeriod(Const.Period.MONTHLY, 2);
        insertPeriodic(movement, Const.Period.TWO_MONTHLY, 2);
        publishProgress(++mProgessCount); // 24
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("1/1/2017");
        movement.setDescription("Spesa");
        movement.setSrcCategory("Portafoglio");
        movement.setDstCategory("Supermercato");
        movement.setAmount("45");
        insertPeriodic(movement, Const.Period.WEEKLY, 8);
        publishProgress(++mProgessCount); // 25
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("9/9/2016");
        movement.setDescription("Spesa");
        movement.setSrcCategory("Portafoglio");
        movement.setDstCategory("Supermercato");
        movement.setAmount("30");
        insertPeriodic(movement, Const.Period.WEEKLY, 8);
        publishProgress(++mProgessCount); // 26
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("3/11/2016");
        movement.setDescription("Spesa");
        movement.setSrcCategory("Portafoglio");
        movement.setDstCategory("Supermercato");
        movement.setAmount("25");
        insertPeriodic(movement, Const.Period.WEEKLY, 4);
        publishProgress(++mProgessCount); // 27
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("20/1/2017");
        movement.setDescription("Biglietto autobus");
        movement.setSrcCategory("Portafoglio");
        movement.setDstCategory("Viaggi");
        movement.setAmount("12");
        mMdaoMovements.insert(movement);
        publishProgress(++mProgessCount); // 28
        movement = new Movement();
        movement.setDatabase(Const.DB1_NAME);
        movement.setDate("10/2/2017");
        movement.setDescription("Biglietto autobus");
        movement.setSrcCategory("Portafoglio");
        movement.setDstCategory("Viaggi");
        movement.setAmount("14");
        mMdaoMovements.insert(movement);
        publishProgress(++mProgessCount); // 29
        movement = new Movement();
        movement.setDatabase(Const.DB2_NAME);
        movement.setDate("10/1/2017");
        movement.setDescription("Tariffa mensile");
        movement.setSrcCategory("Conto postale");
        movement.setDstCategory("Cellulare");
        movement.setAmount("10");
        mMdaoMovements.insert(movement);
        publishProgress(++mProgessCount); // 30
        return null;
    }

    public boolean insertPeriodic(Movement movement, Const.Period period, int count) {
        Date date = movement.getDate();
        boolean result = false;
        for (int i = 0; i < count; i++) {
            movement.setDate(DateUtils.dateToString(date, DateUtils.FORMAT_SQL));
            if (movement.getStartDate() != null && movement.getEndDate() != null){
                movement.setPeriod(movement.getPeriod(), movement.getPeriodCount());
            }
            result = mMdaoMovements.insert(movement);
            date = DateUtils.increment(date, period, 1);
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mEndTime = System.currentTimeMillis();
        mDialog.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mDialog.dismiss();
    }
}
