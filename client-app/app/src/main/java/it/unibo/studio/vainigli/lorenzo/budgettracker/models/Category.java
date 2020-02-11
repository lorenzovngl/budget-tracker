package it.unibo.studio.vainigli.lorenzo.budgettracker.models;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DateUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.ResourceUtilities;

/**
 * Contains details of each category;
 */
public class Category {

    private String mDatabase;
    private String mId;
    private String mDescription;
    private String mParent;
    private int mIcon, mParentIcon, mAvgPeriodicity, mStdDevPeriodicity;
    private String mIconName;
    private double mTotal, mAverage, mStdDeviation, mVarCoeff, mVarCoeffPeriodicity;
    private Date mLastMovementDate;
    private String mType;

    public void setDatabase(String name){
        mDatabase = name;
    }

    public void setId(String id){
        mId = id;
    }

    public void setDescription(String description){
        mDescription = description;
    }

    public void setParent(String parent){
        mParent = parent;
    }

    public void setIcon(Context context, String icon){
        mIconName = icon;
        icon = ResourceUtilities.getResName(icon);
        if (icon != null){
            mIcon = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
        } else {
            mIcon = 0;
        }
    }

    public void setParentIcon(Context context, String icon){
        if (icon != null){
            Log.i("ICON NAME", icon);
            icon = ResourceUtilities.getResName(icon);
            if (icon != null){
                mParentIcon = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
            } else {
                mParentIcon = 0;
            }
        }
    }

    public void setType(String type){
        mType = type;
    }

    public void setTotal(String amount){
        mTotal = Double.parseDouble(amount);
    }

    public void setAverage(String avg){
        mAverage = Double.parseDouble(avg);
    }

    public void setStdDeviation(List<Movement> movements){
        double avg = getAverage();
        double sum = 0;
        for (int i = 0; i < movements.size(); i++){
            sum += Math.pow(Math.abs(movements.get(i).getAmount()) - avg, 2);
            Log.i("STDDEV", " + (" + Double.toString(movements.get(i).getAmount()) + " - " + Double.toString(avg) + ")^2");
        }
        Log.i("STDDEV", " SUM = " + Double.toString(sum));
        mStdDeviation = Math.sqrt(sum / movements.size());
        mVarCoeff = mStdDeviation / getAverage();
    }

    public void setAvgPeriodicity(List<Movement> movements){
        int sum = 0;
        for (int i = 0; i < movements.size()-1; i++){
            Date date1 = movements.get(i).getDate();
            Date date2 = movements.get(i+1).getDate();
            sum += DateUtils.getDateDiff(date1, date2, TimeUnit.DAYS);
        }
        // Sono ordinati per data crescente
        mLastMovementDate = movements.get(movements.size() - 1).getDate();
        mAvgPeriodicity = sum / movements.size();
    }

    public void setStdDevPeriodicity(List<Movement> movements){
        int avg = getAvgPeriodicity();
        int sum = 0;
        for (int i = 0; i < movements.size()-1; i++){
            Date date1 = movements.get(i).getDate();
            Date date2 = movements.get(i+1).getDate();
            int days = DateUtils.getDateDiff(date1, date2, TimeUnit.DAYS);
            Log.i("DEV_STD_PERIOD", Integer.toString(days));
            sum += Math.pow(Math.abs(days) - avg, 2);
        }
        // Sono ordinati per data crescente
        mLastMovementDate = movements.get(movements.size() - 1).getDate();
        mStdDevPeriodicity = (int) Math.sqrt(sum / movements.size());
        mVarCoeffPeriodicity = (double) mStdDevPeriodicity / getAvgPeriodicity();
        Log.i("VAR COEFF", Integer.toString(mStdDevPeriodicity) + " / " + Integer.toString(getAvgPeriodicity()) +
                " = " +mVarCoeffPeriodicity);
    }

    public String getDatabase(){
        return mDatabase;
    }

    public String getId(){
        return mId;
    }

    public String getDescription(){
        return mDescription;
    }

    public String getParent(){
        return mParent;
    }

    public int getIcon(){
        return mIcon;
    }

    public int getParentIcon(){
        return mParentIcon;
    }

    public String getIconName(){
        return mIconName;
    }

    public String getType(){
        return mType;
    }

    public double getTotal(){
        return mTotal;
    }

    public double getAverage(){
        return mAverage;
    }

    public double getStdDeviation(){
        return mStdDeviation;
    }

    public double getVariationCoeff(){
        return mVarCoeff;
    }

    public int getAvgPeriodicity(){
        return mAvgPeriodicity;
    }

    public int getStdDevPeriodicity(){
        return mStdDevPeriodicity;
    }

    public double getVarCoeffPeriodicity(){
        return mVarCoeffPeriodicity;
    }

    public Date getLastMovementDate(){
        return mLastMovementDate;
    }
}
