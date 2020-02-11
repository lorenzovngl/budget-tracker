package it.unibo.studio.vainigli.lorenzo.budgettracker.controllers;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FiltersController {

    private final String FILTER_PREFS = "FilterPreferences";
    private final String ACTIVE = "active";
    private final String ACTIVE_DATABASES = "active_databases";
    private final String ACTIVE_SRC_CATEGORIES = "active_src_categories";
    private final String ACTIVE_DST_CATEGORIES = "active_dst_categories";
    private final String ACTIVE_DATE = "active_date";
    private final String ACTIVE_AMOUNT = "active_amount";
    private final String DATABASES_SET = "databases";
    private final String SRC_CATEGORIES_SET = "src_categories";
    private final String DST_CATEGORIES_SET = "dst_categories";
    private final String START_DATE = "startDate";
    private final String END_DATE = "endDate";
    private final String START_AMOUNT = "startAmount";
    private final String END_AMOUNT = "endAmount";
    public static final String STRING_NOT_EXISTS = null;
    public static final Set<String> STRINGSET_NOT_EXISTS = new HashSet<String>();
    public static final boolean BOOL_NOT_EXISTS = false;
    public static final float FLOAT_NOT_EXISTS = -1;

    private SharedPreferences mSharedPreferences;

    public FiltersController(Context context){
        mSharedPreferences = context.getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
    }

    public void enable(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE, true);
        editor.apply();
    }

    public void disable(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE, false);
        editor.apply();
    }

    public boolean isEnabled(){
        return mSharedPreferences.getBoolean(ACTIVE, BOOL_NOT_EXISTS);
    }

    public void enableDatabases(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_DATABASES, true);
        editor.apply();
    }

    public void disableDatabases(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_DATABASES, false);
        editor.apply();
    }

    public boolean isEnabledDatabases(){
        return mSharedPreferences.getBoolean(ACTIVE_DATABASES, BOOL_NOT_EXISTS);
    }

    public void enableSrcCategories(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_SRC_CATEGORIES, true);
        editor.apply();
    }

    public void disableSrcCategories(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_SRC_CATEGORIES, false);
        editor.apply();
    }

    public boolean isEnabledSrcCategories(){
        return mSharedPreferences.getBoolean(ACTIVE_SRC_CATEGORIES, BOOL_NOT_EXISTS);
    }

    public void enableDstCategories(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_DST_CATEGORIES, true);
        editor.apply();
    }

    public void disableDstCategories(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_DST_CATEGORIES, false);
        editor.apply();
    }

    public boolean isEnabledDstCategories(){
        return mSharedPreferences.getBoolean(ACTIVE_DST_CATEGORIES, BOOL_NOT_EXISTS);
    }

    public void enableDate(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_DATE, true);
        editor.apply();
    }

    public void disableDate(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_DATE, false);
        editor.apply();
    }

    public boolean isEnabledDate(){
        return mSharedPreferences.getBoolean(ACTIVE_DATE, BOOL_NOT_EXISTS);
    }

    public void enableAmount(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_AMOUNT, true);
        editor.apply();
    }

    public void disableAmount(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(ACTIVE_AMOUNT, false);
        editor.apply();
    }

    public boolean isEnabledAmount(){
        return mSharedPreferences.getBoolean(ACTIVE_AMOUNT, BOOL_NOT_EXISTS);
    }

    public void setStartDate(String startDate){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(START_DATE, startDate);
        editor.apply();
    }

    public void setEndDate(String endDate){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(END_DATE, endDate);
        editor.apply();
    }

    public void setStartAmount(double startAmount){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(START_AMOUNT, (float) startAmount);
        editor.apply();
    }

    public void setEndAmount(double endAmount){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(END_AMOUNT, (float) endAmount);
        editor.apply();
    }

    public void setDatabases(Set<String> databases){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(DATABASES_SET, databases);
        editor.apply();
    }

    public void setSrcCategories(Set<String> categories){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(SRC_CATEGORIES_SET, categories);
        editor.apply();
    }

    public void setDstCategories(Set<String> accounts){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putStringSet(DST_CATEGORIES_SET, accounts);
        editor.apply();
    }

    public String getStartDate(){
        return mSharedPreferences.getString(START_DATE, STRING_NOT_EXISTS);
    }

    public String getEndDate(){
        return mSharedPreferences.getString(END_DATE, STRING_NOT_EXISTS);
    }

    public float getStartAmount(){
        return mSharedPreferences.getFloat(START_AMOUNT, FLOAT_NOT_EXISTS);
    }

    public float getEndAmount(){
        return mSharedPreferences.getFloat(END_AMOUNT, FLOAT_NOT_EXISTS);
    }

    public Set<String> getDatabases(){
        return mSharedPreferences.getStringSet(DATABASES_SET, STRINGSET_NOT_EXISTS);
    }

    public Set<String> getSrcCategories(){
        return mSharedPreferences.getStringSet(SRC_CATEGORIES_SET, STRINGSET_NOT_EXISTS);
    }

    public Set<String> getDstCategories(){
        return mSharedPreferences.getStringSet(DST_CATEGORIES_SET, STRINGSET_NOT_EXISTS);
    }
}
