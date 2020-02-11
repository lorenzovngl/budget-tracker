package it.unibo.studio.vainigli.lorenzo.budgettracker.models;

import java.util.Date;

public class Backup {

    private String mDescription;
    private Date mDate;
    private String mPath;

    public void setDescription(String description){
        mDescription = description;
    }

    public void setDate(Date date){
        mDate = date;
    }

    public void setPath(String path){
        mPath = path;
    }

    public String getDescription(){
        return mDescription;
    }

    public Date getDate(){
        return mDate;
    }

    public String getPath(){
        return mPath;
    }
}
