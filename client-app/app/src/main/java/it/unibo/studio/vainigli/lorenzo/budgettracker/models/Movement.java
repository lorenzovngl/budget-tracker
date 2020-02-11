package it.unibo.studio.vainigli.lorenzo.budgettracker.models;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;

import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;

/**
 * Contains details of each movement;
 */
public class Movement {

    private String mDatabase;
    private String mId;
    private Date mDate;
    private String mDescription;
    private String mSrcCategory;
    private String mDstCategory;
    private double mAmount;
    // In molti casi protrebbero essere null
    private Const.Period mPeriod;
    private int mPeriodCount;
    private Date mStartDate, mEndDate;

    public void setDatabase(String name){
        mDatabase = name;
    }

    public void setId(String id){
        mId = id;
    }

    public void setDate(String date){
        mDate = DateUtils.stringToDate(date);
    }

    public void setDescription(String description){
        mDescription = description;
    }

    public void setSrcCategory(String srcCategory){
        mSrcCategory = srcCategory;
    }

    public void setDstCategory(String dstCategory){
        mDstCategory = dstCategory;
    }

    public void setAmount(String amount){
        mAmount = Double.parseDouble(amount);
    }

    public void setPeriod(String startDate, String endDate){
        if (startDate != null && endDate != null){
            mStartDate = DateUtils.stringToDate(startDate, DateUtils.FORMAT_SQL);
            mEndDate = DateUtils.stringToDate(endDate, DateUtils.FORMAT_SQL);
        }
    }

    public void setPeriod(Const.Period period, int count){
        mPeriod = period;
        mPeriodCount = count;
        // Questo implica che setDate() deve essere sempre chiamata prima di questa funzione
        mStartDate = mDate;
        // La data di fine va inclusa
        mEndDate = DateUtils.decrement(DateUtils.increment(mDate, period, count), Const.Period.DAILY, 1);
    }

    public String getDatabase(){
        return mDatabase;
    }

    public String getId(){
        return mId;
    }

    public String getStringDate(){
        return DateUtils.dateToString(mDate, DateUtils.FORMAT_IT);
    }

    public Date getDate(){
       return mDate;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getSrcCategory(){
        return mSrcCategory;
    }

    public String getDstCategory(){
        return mDstCategory;
    }

    public double getAmount(){
        return mAmount;
    }

    // TODO da eliminare
    public String getStringStartDate(){
        if (mStartDate == null){
            return null;
        }
        return DateUtils.dateToString(mStartDate, DateUtils.FORMAT_IT);
    }

    public String getStringStartDate(String format){
        if (mStartDate == null){
            return null;
        }
        return DateUtils.dateToString(mStartDate, format);
    }

    public Date getStartDate(){
        return mStartDate;
    }

    // TODO da eliminare
    public String getStringEndDate(){
        if (mEndDate == null){
            return null;
        }
        return DateUtils.dateToString(mEndDate, DateUtils.FORMAT_IT);
    }

    public String getStringEndDate(String format){
        if (mEndDate == null){
            return null;
        }
        return DateUtils.dateToString(mEndDate, format);
    }

    public Date getEndDate(){
        return mEndDate;
    }

    public Const.Period getPeriod(){
        return mPeriod;
    }

    public int getPeriodCount() {
        return mPeriodCount;
    }

}
