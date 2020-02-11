package it.unibo.studio.vainigli.lorenzo.budgettracker.controllers;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.LoginActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.RegisterActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks.CheckDatabaseAsyncTask;
import it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks.DownloadAsyncTask;
import it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks.LoginAsyncTask;
import it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks.RegisterAsyncTask;
import it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks.UploadAsyncTask;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.RegisterDetailDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.CryptoUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.HashUtils;

import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_DB_SYNCHRONIZED;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_DOWNLOAD_OK;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_ERROR_UPLOADING_FILE;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_LOCAL_DB_OUT_OF_DATE;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_LOGIN_OK;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_REGISTRATION_OK;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_REMOTE_DB_NOT_FOUND;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_REMOTE_DB_OUT_OF_DATE;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_UPLOAD_OK;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_WRONG_CREDENTIALS;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_WRONG_PARAMETERS;

/**
 * Created by Lorenzo on 22/11/2016.
 */

public class ServerController {

    private static final String serverBaseUrl = "http://site1709.web.cs.unibo.it/api";
    public static final String loginUrl = serverBaseUrl + "/login.php";
    public static final String registerUrl = serverBaseUrl + "/register.php";
    public static final String uploadUrl = serverBaseUrl + "/upload_db.php";
    public static final String downloadUrl = serverBaseUrl + "/download_db.php";
    public static final String compareDatabaseUrl = serverBaseUrl + "/compare_db.php";
    private static final String zipUrl = serverBaseUrl + "/lorenzo/sync.zip";
    private static Context mContext;
    private LoginActivity mLoginActivity;
    private RegisterActivity mRegisterActivity;

    public class ResponseCodes {

        // Success responses
        public static final int RESPONSE_LOGIN_OK = 0;
        public static final int RESPONSE_REGISTRATION_OK = 1;
        public static final int RESPONSE_UPLOAD_OK = 2;
        public static final int RESPONSE_DOWNLOAD_OK = 3;
        public static final int RESPONSE_DB_SYNCHRONIZED = 4;
        public static final int RESPONSE_LOCAL_DB_OUT_OF_DATE = 5;
        public static final int RESPONSE_REMOTE_DB_OUT_OF_DATE = 6;
        // Communication error responses
        public static final int RESPONSE_WRONG_PARAMETERS = -1;
        public static final int RESPONSE_WRONG_CREDENTIALS = -2;
        // Server error responses
        public static final int RESPONSE_ERROR_UPLOADING_FILE = -3;
        public static final int RESPONSE_REMOTE_DB_NOT_FOUND = -4;
        public static final int RESPONSE_CONNECTION_ERROR = -5;
    }


    public ServerController(Context context) {
        mContext = context;
    }

    public ServerController(LoginActivity activity, Context context) {
        mLoginActivity = activity;
        mContext = context;
    }

    public ServerController(RegisterActivity activity, Context context) {
        mRegisterActivity = activity;
        mContext = context;
    }

    public static boolean parseResponse(String response){
        boolean result = false;
        try {
            JSONObject json = new JSONObject(response);
            switch (json.getInt("response_id")){
                case RESPONSE_WRONG_PARAMETERS:
                    DialogUtils.showSimpleDialog(mContext, "Errore", "Parametri non corretti.");
                    break;
                case RESPONSE_LOGIN_OK:
                    DialogUtils.showSimpleDialog(mContext, "Successo", "Login effettuato.");
                    result = true;
                    break;
                case RESPONSE_REGISTRATION_OK:
                    DialogUtils.showSimpleDialog(mContext, "Successo", "Registrazione effettuata.");
                    result = true;
                    break;
                case RESPONSE_UPLOAD_OK:
                    DialogUtils.showSimpleDialog(mContext, "Successo", "Upload effettuato.");
                    result = true;
                    break;
                case RESPONSE_DOWNLOAD_OK:
                    result = true;
                    break;
                case RESPONSE_ERROR_UPLOADING_FILE:
                    DialogUtils.showSimpleDialog(mContext, "Errore", "Non è stato possibile caricare il file");
                    break;
                case RESPONSE_WRONG_CREDENTIALS:
                    DialogUtils.showSimpleDialog(mContext, "Errore", "Credenziali errate.");
                    break;
                case RESPONSE_DB_SYNCHRONIZED:
                    DialogUtils.showSimpleDialog(mContext, "Successo", "Il database è sincronizzato.");
                    break;
                case RESPONSE_LOCAL_DB_OUT_OF_DATE:
                    DialogUtils.showSimpleDialog(mContext, "Successo", "E' disponibile una nuova versione.");
                    break;
                case RESPONSE_REMOTE_DB_OUT_OF_DATE:
                    DialogUtils.showSimpleDialog(mContext, "Successo", "Il file sul server è obsoleto.");
                    break;
                case RESPONSE_REMOTE_DB_NOT_FOUND:
                    DialogUtils.showSimpleDialog(mContext, "Errore", "Il file remoto non è stato trovato.");
                    break;
                default:
                    DialogUtils.showSimpleDialog(mContext, "Errore", response);
                    break;
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return result;
    }

    public void authenticate(String username, String password) {
        LoginAsyncTask task = new LoginAsyncTask(mContext, mLoginActivity, username, password);
        task.execute();
    }

    public void register(String fullname, String username, String password) {
        RegisterAsyncTask task = new RegisterAsyncTask(mContext, mRegisterActivity, fullname, username, password);
        task.execute();
    }

    public void upload(Register database) {
        database.setPathEncryptedVersion(database.getPath() + ".enc");
        CryptoUtils.encrypt(database.getMD5Password(), database.getPath(), database.getPathEncryptedVersion());
        String username = UsersPrefsController.getUsername(mContext);
        String md5password = UsersPrefsController.getMD5Password(mContext);
        UploadAsyncTask task = new UploadAsyncTask(mContext, username, md5password, database);
        task.execute();
    }

    public void download(String registerId, String registerPassword) {
        //CryptoUtils.decrypt(database.getMD5Password(), database.getPath(), database.getPathEncryptedVersion());
        String username = UsersPrefsController.getUsername(mContext);
        String md5password = UsersPrefsController.getMD5Password(mContext);
        DownloadAsyncTask task = new DownloadAsyncTask(mContext, username, md5password, registerId, HashUtils.md5(registerPassword));
        task.execute();
    }

    public void checkDatabase(RegisterDetailDialog dialog, Register database) {
        if (database.getRemoteId() == null){
            dialog.setResponseView(RESPONSE_REMOTE_DB_NOT_FOUND);
        } else {
            database.setPathEncryptedVersion(database.getPath() + ".enc");
            String username = UsersPrefsController.getUsername(mContext);
            String md5password = UsersPrefsController.getMD5Password(mContext);
            CheckDatabaseAsyncTask task = new CheckDatabaseAsyncTask(mContext, dialog, username, md5password, database);
            task.execute();
        }
    }

    // Crea uno zip cifrato con tutti i file utili e lo manda al server (database, backup del database, report, preferenze)
    // Sincronizza da app a server
    public void syncLocal(){
        String DATA_PATH = mContext.getApplicationInfo().dataDir;
        String SYNC_PATH = DATA_PATH + "/sync";
        String SYNC_ZIP_PATH = DATA_PATH + "/sync.zip";
        File srcFolder = new File(DATA_PATH + FileUtils.BACKUPS_PATH);
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(new File(SYNC_PATH));
            new File(SYNC_ZIP_PATH).delete();
            // Create the temporary folder that will be compressed
            File tmpFolder = FileUtils.createDirectory(SYNC_PATH);
            // Add subfolders
            org.apache.commons.io.FileUtils.copyDirectoryToDirectory(srcFolder, tmpFolder);
            srcFolder = new File(DATA_PATH + FileUtils.DATABASE_PATH);
            org.apache.commons.io.FileUtils.copyDirectoryToDirectory(srcFolder, tmpFolder);
            srcFolder = new File(DATA_PATH + FileUtils.REPORTS_PATH);
            org.apache.commons.io.FileUtils.copyDirectoryToDirectory(srcFolder, tmpFolder);
            srcFolder = new File(DATA_PATH + FileUtils.SHARED_PREFS_PATH);
            org.apache.commons.io.FileUtils.copyDirectoryToDirectory(srcFolder, tmpFolder);
            // Create the zip
            FileUtils.compress(mContext, SYNC_PATH, SYNC_ZIP_PATH);
            // Open the zip
            //FileUtils.decompress(mContext, DATA_PATH + "/syncLocal.zip", SYNC_PATH);
            org.apache.commons.io.FileUtils.deleteDirectory(tmpFolder);
        } catch (IOException e){
            e.printStackTrace();
        }
        CryptoUtils.encrypt("secretKey0000000", SYNC_ZIP_PATH, SYNC_ZIP_PATH);
    }

    // Sincronizza da server ad app
    public void syncRemote(){
        String DATA_PATH = mContext.getApplicationInfo().dataDir;
        String SYNC_PATH = DATA_PATH + "/sync";
        String SYNC_ZIP_PATH = DATA_PATH + "/sync.zip";
        FileUtils.downloadFile(this, mContext, zipUrl, SYNC_ZIP_PATH);
    }

    public void syncRemoteFinish(){
        String DATA_PATH = mContext.getApplicationInfo().dataDir;
        String SYNC_PATH = DATA_PATH + "/sync";
        String SYNC_PATH_SUB = SYNC_PATH + "/sync";
        String SYNC_ZIP_PATH = DATA_PATH + "/sync.zip";
        // Create the folders
        File tmpFolder = FileUtils.createDirectory(SYNC_PATH);
        // Decrypt and decompress the file
        CryptoUtils.decrypt("secretKey0000000", SYNC_ZIP_PATH, SYNC_ZIP_PATH);
        FileUtils.decompress(mContext, SYNC_ZIP_PATH, SYNC_PATH);
        // Copy folders
        try {
            String[] paths = {FileUtils.BACKUPS_PATH, FileUtils.DATABASE_PATH, FileUtils.REPORTS_PATH, FileUtils.SHARED_PREFS_PATH};
            for (int i = 0; i < paths.length; i++){
                File srcFolder = new File(SYNC_PATH_SUB + paths[i]);
                File dstFolder = new File(DATA_PATH + paths[i]);
                org.apache.commons.io.FileUtils.copyDirectory(srcFolder, dstFolder);
            }
            org.apache.commons.io.FileUtils.deleteDirectory(tmpFolder);
            new File(SYNC_ZIP_PATH).delete();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
