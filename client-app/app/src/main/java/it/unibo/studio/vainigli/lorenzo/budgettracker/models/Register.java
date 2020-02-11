package it.unibo.studio.vainigli.lorenzo.budgettracker.models;


import java.util.List;

public class Register {

    private String mId;
    private String mRemoteId;
    private String mName;
    private String mMD5Password;
    private String mOwner;
    private List<String> mSharedNames;
    private String mPath;
    private String mPathEncryptedVersion;
    private long mByteSize;
    private long mLastEdit;

    public void setId(String id){
        mId = id;
    }

    public void setRemoteId(String id){
        mRemoteId = id;
    }

    public void setName(String description){
        mName = description;
    }

    public void setMD5Password(String password){
        mMD5Password = password;
    }

    public void setSharedNames(List<String> sharedNames){
        mSharedNames = sharedNames;
    }

    public void setOwner(String owner){
        mOwner = owner;
    }

    public void setPath(String path){
        mPath = path;
    }

    public void setPathEncryptedVersion(String path){
        mPathEncryptedVersion = path;
    }

    public void setSize(long sizeInBytes){
        mByteSize = sizeInBytes;
    }

    public void setLastEdit(long time){
        mLastEdit = time;
    }

    public String getId(){
        return mId;
    }

    public String getRemoteId(){
        return mRemoteId;
    }

    public String getName(){
        return mName;
    }

    public String getMD5Password(){
        return mMD5Password;
    }

    public List<String> getSharedNames(){
        return mSharedNames;
    }

    public String getOwner(){
        return mOwner;
    }

    public String getPath(){
        return mPath;
    }

    public String getPathEncryptedVersion(){
        return mPathEncryptedVersion;
    }

    public long getSize(){
        return mByteSize;
    }

    public long getLastEdit(){
        return mLastEdit;
    }
}
