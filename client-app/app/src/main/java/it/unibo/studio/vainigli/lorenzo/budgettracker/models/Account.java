package it.unibo.studio.vainigli.lorenzo.budgettracker.models;

import android.content.Context;

import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.ResourceUtilities;

/**
 * Contains details of each account;
 */
public class Account {

    private String mId;
    private String mDescription;
    private int mIcon;
    private String mIconName;
    private double mAmount;

    public void setId(String id){
        mId = id;
    }

    public void setDescription(String description){
        mDescription = description;
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

    public void setAmount(String amount){
        mAmount = Double.parseDouble(amount);
    }

    public String getId(){
        return mId;
    }

    public String getDescription(){
        return mDescription;
    }

    public int getIcon(){
        return mIcon;
    }

    public String getIconName(){
        return mIconName;
    }

    public double getAmount(){
        return mAmount;
    }
}
