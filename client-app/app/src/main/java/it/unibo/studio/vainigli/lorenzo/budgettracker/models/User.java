package it.unibo.studio.vainigli.lorenzo.budgettracker.models;


public class User {

    private String mFullname;
    private String mUsername;
    private String mMD5Password;

    public void setFullname(String name){
        mFullname = name;
    }

    public void setUsername(String username){
        mUsername = username;
    }

    public void setMD5Password(String md5Password){
        mMD5Password = md5Password;
    }

    public String getFullname(){
        return mFullname;
    }

    public String getUsername(){
        return mUsername;
    }

    public String getMD5Password(){
        return mMD5Password;
    }
}
