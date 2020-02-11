package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoMovements;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.RegistrersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;

/**
 * Crea delle statistiche sui risultati di query sul db
 */
public class Statistic {

    private RegistrersController db;
    private Context mContext;
    private DaoMovements mDaoMovements;
    private List<Movement> mMovementList;

    public Statistic(Context context) {
        db = new RegistrersController(context);
        mContext = context;
        mDaoMovements = new DaoMovements(db.getReadableDatabase(), context);
        mMovementList = mDaoMovements.getAll();
    }

    public Statistic(Context context, List<Movement> list) {
        db = new RegistrersController(context);
        mContext = context;
        mDaoMovements = new DaoMovements(db.getReadableDatabase(), context);
        mMovementList = list;
    }

    // Calcola il livello di maturazione di una spesa: se non ci sono date la spesa è tutta maturata
    public static double getDepreciation(Movement movement) {
        Date today = new Date();
        Date dateStart = movement.getStartDate();
        Date dateEnd = movement.getEndDate();
        if (dateEnd != null && today.before(dateEnd)) {
            if (today.after(dateStart)) {
                // La spesa sta maturando
                // Calcolo il riparto per giorno dell'importo totale (aggiungo un giorno poiché dateEnd è compresa)
                Date dateEndPlusOne = DateUtils.increment(dateEnd, Const.Period.DAILY, 1);
                int daysDiff = DateUtils.getDateDiff(dateStart, dateEndPlusOne, TimeUnit.DAYS);
                double sumPerDay = Math.abs(movement.getAmount()) / daysDiff;
                // Calcolo i giorni già passati dal tempo di inizio, cioè quelli maturati
                int daysValid = DateUtils.getDateDiff(dateStart, today, TimeUnit.DAYS);
                return sumPerDay * daysValid;
            } else {
                // La spesa non è ancora maturata
                return 0;
            }
        } else {
            // Tutta la spesa è maturata
            return movement.getAmount();
        }
    }

    // Dato un giorno, esamina un movimento per calcolare gli importi di competenza di tale giorno
    public static double amountOfTheDay(Date currentDay, Movement movement) {
        Date date = movement.getDate();
        Date dateStart = movement.getStartDate();
        Date dateEnd = movement.getEndDate();
        if (dateStart != null && dateEnd != null){
            // La spesa ha un periodo di competenza e va ripartita
            if (!currentDay.after(dateEnd) && !currentDay.before(dateStart)) {
                // Una parte della spesa è di competenza di questo giorno
                // Calcolo il riparto per giorno dell'importo totale (aggiungo un giorno poiché dateEnd è compresa)
                Date dateEndPlusOne = DateUtils.increment(dateEnd, Const.Period.DAILY, 1);
                int daysDiff = DateUtils.getDateDiff(dateStart, dateEndPlusOne, TimeUnit.DAYS);
                return movement.getAmount() / daysDiff;
            } else {
                return 0;
            }
        } else {
            // La spesa non ha un periodo di competenza e non va ripartita
            if (date.compareTo(currentDay) == 0){
                // La spesa è di competenza totale di questo giorno
                return movement.getAmount();
            } else {
                // La spesa non è di competenza di questo giorno
                return 0;
            }
        }
    }

    public double amountOfTheDay(Date currentDay) {
        double total = 0;
        for (int i = 0; i < mMovementList.size(); i++) {
            total += amountOfTheDay(currentDay, mMovementList.get(i));
        }
        return total;
    }

    public double getTotal(Const.MovementType type, Const.Period period, Date referenceDate) {
        if (db.isEmpty()){
            return 0;
        }
        Date i = new Date();
        switch (period) {
            case ALLTIME:
                i = mDaoMovements.getFirstDate();
                break;
            case MONTHLY:
                i = DateUtils.decrement(referenceDate, Const.Period.MONTHLY, 1);
                break;
            case WEEKLY:
                i = DateUtils.decrement(referenceDate, Const.Period.WEEKLY, 1);
                break;
            default:
                break;
        }
        double total = 0;
        while (i.before(referenceDate)){
            total += amountOfTheDay(i);
            i = DateUtils.increment(i, Const.Period.DAILY, 1);
        }
        return total;
    }

    public double getAverage(Const.MovementType type, Const.Period period) {
        if (db.isEmpty()) {
            return 0;
        }
        double total = getTotal(type, period, new Date());
        int count = DateUtils.getCountOfPeriods(mContext, period);
        return total / count;
    }

    // Dato un movimento esami se la data rientra nel periodo desiderato per il calcolo del totale
    // Non effettua ripartizioni
    private double processMovement(Movement movement, Const.MovementType type, Const.Period period) {
        double total = 0;
        double amount = movement.getAmount();
        //Log.i("MOV", movement.getStringDate() + " " + String.valueOf(movement.getTotal()));
        Date dateOfMovement = DateUtils.stringToDate(movement.getStringDate(), DateUtils.FORMAT_IT);
        switch (type){
            case ALL:
                switch (period){
                    case ALLTIME:
                        total += amount;
                        break;
                    case CURRENT:
                        if (!dateOfMovement.after(new Date())) {
                            total += amount;
                        }
                        break;
                    case YEARLY:
                        if (isPartOfLastPeriod(dateOfMovement, DateUtils.DAYS_IN_YEAR)) {
                            total += amount;
                        }
                        break;
                    case MONTHLY:
                        if (isPartOfLastPeriod(dateOfMovement, DateUtils.DAYS_IN_MONTH)) {
                            total += amount;
                        }
                        break;
                    case WEEKLY:
                        if (isPartOfLastPeriod(dateOfMovement, DateUtils.DAYS_IN_WEEK)) {
                            total += amount;
                        }
                        break;
                    default:
                        break;
                }
                break;
            case EXPENSE:
                switch (period){
                    case ALLTIME:
                        if (amount < 0) {
                            total += amount;
                        }
                        break;
                    case CURRENT:
                        if (amount < 0 && !dateOfMovement.after(new Date())) {
                            total += amount;
                        }
                        break;
                    case YEARLY:
                        if (amount < 0 && isPartOfLastPeriod(dateOfMovement, DateUtils.DAYS_IN_YEAR)) {
                            total += amount;
                        }
                        break;
                    case MONTHLY:
                        if (amount < 0 && isPartOfLastPeriod(dateOfMovement, DateUtils.DAYS_IN_MONTH)) {
                            total += amount;
                        }
                        break;
                    case WEEKLY:
                        if (amount < 0 && isPartOfLastPeriod(dateOfMovement, DateUtils.DAYS_IN_WEEK)) {
                            total += amount;
                        }
                        break;
                    default:
                        break;
                }
                break;
            case INCOME:
                switch (period){
                    case ALLTIME:
                        if (amount > 0) {
                            total += amount;
                        }
                        break;
                    case CURRENT:
                        if (amount > 0 && !dateOfMovement.after(new Date())) {
                            total += amount;
                        }
                        break;
                    case YEARLY:
                        if (amount > 0 && isPartOfLastPeriod(dateOfMovement, DateUtils.DAYS_IN_YEAR)) {
                            total += amount;
                        }
                        break;
                    case MONTHLY:
                        if (amount > 0 && isPartOfLastPeriod(dateOfMovement, DateUtils.DAYS_IN_MONTH)) {
                            total += amount;
                        }
                        break;
                    case WEEKLY:
                        if (amount > 0 && isPartOfLastPeriod(dateOfMovement, DateUtils.DAYS_IN_WEEK)) {
                            total += amount;
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return total;
    }

    public boolean isCurrent(Date dateOfMovement) {
        Date today = new Date();
        //Log.i("Data movimento", DateUtils.dateToString(dateOfMovement, DateUtils.FORMAT_IT));
        //Log.i("Data oggi", DateUtils.dateToString(today, DateUtils.FORMAT_IT));
        if (!dateOfMovement.after(today)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isPartOfLastPeriod(Date dateOfMovement, int days) {
        Date today = new Date();
        Date pastDate = DateUtils.decrement(today, Const.Period.DAILY, days);
        /*Log.i("Data movimento", DateUtils.dateToString(dateOfMovement, DateUtils.FORMAT_IT));
        Log.i("Data oggi", DateUtils.dateToString(today, DateUtils.FORMAT_IT));
        Log.i("Data passata", DateUtils.dateToString(pastDate, DateUtils.FORMAT_IT));*/
        if (!dateOfMovement.before(pastDate) && !dateOfMovement.after(today)) {
            Log.i("Data", DateUtils.dateToString(pastDate, DateUtils.FORMAT_IT));
            return true;
        } else {
            return false;
        }
    }
}
