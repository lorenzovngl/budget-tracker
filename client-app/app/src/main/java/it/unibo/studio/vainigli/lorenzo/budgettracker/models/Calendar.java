package it.unibo.studio.vainigli.lorenzo.budgettracker.models;

import android.content.Context;

import java.util.Date;

import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.ResourceUtilities;

/**
 * Contains details of each account;
 */
public class Calendar {

    private Date mDate;
    private String mIdMov;
    private double mAmount;

    public void setDate(String date){
        mDate = DateUtils.stringToDate(date, DateUtils.FORMAT_SQL);
    }

    public void setIdMov(String idMov){
        mIdMov = idMov;
    }

    public void setAmount(String amount){
        mAmount = Double.parseDouble(amount);
    }

    public Date getDate(){
        return mDate;
    }

    public String getStringDate(){
        return DateUtils.dateToString(mDate, DateUtils.FORMAT_IT);
    }

    public String getIdMov(){
        return mIdMov;
    }

    public double getAmount(){
        return mAmount;
    }
}
