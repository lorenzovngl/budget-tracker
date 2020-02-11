package it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoRegisters;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.UsersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;

public class UploadAsyncTask extends AsyncTask<Void, Integer, Void> {

    private Context mContext;
    private String mUsername, mUserPassword, mFilePassword, mFilePath, mFilename, mFiletime;
    private ProgressDialog mDialog;

    public UploadAsyncTask(Context context, String username, String md5UserPassword, Register database){
        mContext = context;
        mUsername = username;
        mUserPassword = md5UserPassword;
        mFilePassword = database.getMD5Password();
        mFilePath = database.getPathEncryptedVersion();
        mFilename = database.getName() + ".enc";
        mFiletime = Long.toString(database.getLastEdit());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = DialogUtils.showLoadingDialog(mContext, "Sincronizzazione", "Sincronizzazione del database in corso");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        // Response Listener
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();
                if(ServerController.parseResponse(response)){
                    DaoRegisters daoRegisters = new DaoRegisters(Const.DBMode.WRITE, mContext);
                    try {
                        JSONObject json = new JSONObject(response);
                        Log.i("RECORD_ID", Integer.toString(json.getInt("record_id")));
                        Log.i("DEC_FILENAME", mFilename.substring(0, mFilename.length()-4));
                        daoRegisters.update(Integer.toString(json.getInt("record_id")), mFilename.substring(0, mFilename.length()-4), mFilePassword);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                new File(mFilePath).delete();
            }
        };
        // Response Error Listener
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                DialogUtils.showSimpleDialog(mContext, "Errore", "Impossibile connettersi al server.");
                new File(mFilePath).delete();
            }
        };
        // Request a string response from the provided URL.
        Log.i("PARAM", "username: " + mUsername);
        Log.i("PARAM", "user_password: " + mUserPassword);
        Log.i("PARAM", "file_password: " + mFilePassword);
        Log.i("PARAM", "filetime: " + mFiletime);
        Log.i("PARAM", "filename: " + mFilename);
        Log.i("PARAM", "filepath: " + mFilePath);
        SimpleMultiPartRequest multiPartRequest = new SimpleMultiPartRequest(Request.Method.POST, ServerController.uploadUrl, responseListener, errorListener);
        multiPartRequest.addStringParam("username", mUsername);
        multiPartRequest.addStringParam("md5_user_password", mUserPassword);
        multiPartRequest.addStringParam("md5_file_password", mFilePassword);
        multiPartRequest.addStringParam("filename", mFilename);
        // Convert time in milliseconds to time in seconds
        mFiletime = mFiletime.substring(0, mFiletime.length() - 3);
        multiPartRequest.addStringParam("filetime", mFiletime);
        multiPartRequest.addFile("database", mFilePath);
        // Add the request to the RequestQueue.
        queue.add(multiPartRequest);
        return null;
    }
}
