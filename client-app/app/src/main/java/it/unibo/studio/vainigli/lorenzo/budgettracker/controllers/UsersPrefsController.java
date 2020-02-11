package it.unibo.studio.vainigli.lorenzo.budgettracker.controllers;

import android.content.Context;
import android.content.SharedPreferences;

public class UsersPrefsController {

    public static final String LOGIN_PREFS = "LoginPreferences";
    private static final String LOGGED = "logged";
    public static final String FULLNAME = "fullname";
    public static final String USERNAME = "username";
    public static final String MD5_PASSWORD = "md5_password";

    public static boolean isAlrealdyLoggedIn(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LOGGED, false);
    }

    public static void prefsLogin(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED, true);
        editor.apply();
    }

    public static void prefsLogout(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGGED, false);
        editor.apply();
    }

    public static void setFullname(Context context, String fullname){
        SharedPreferences.Editor editor = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(FULLNAME, fullname).apply();
    }

    public static void setUsername(Context context, String username){
        SharedPreferences.Editor editor = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(USERNAME, username).apply();
    }

    public static void setMD5Password(Context context, String md5Password){
        SharedPreferences.Editor editor = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE).edit();
        editor.putString(MD5_PASSWORD, md5Password).apply();
    }

    public static String getFullname(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(FULLNAME, null);
    }

    public static String getUsername(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USERNAME, null);
    }

    public static String getMD5Password(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(MD5_PASSWORD, null);
    }

}
