package it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
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
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.FileUtils;

public class DownloadAsyncTask extends AsyncTask<Void, Integer, Void> {

    private Context mContext;
    private String mUsername, mUserPassword, mFilePassword, mFileId;
    private ProgressDialog mDialog;

    public DownloadAsyncTask(Context context, String username, String md5UserPassword, String fileId, String filePassword){
        mContext = context;
        mUsername = username;
        mUserPassword = md5UserPassword;
        mFilePassword = filePassword;
        mFileId = fileId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = DialogUtils.showLoadingDialog(mContext, "Download", "Scaricamento del file in corso...");
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
                    try {
                        JSONObject json = new JSONObject(response);
                        Log.i("LINK", json.getString("url"));
                        String url = json.getString("url");
                        String newFileName = url.substring(url.lastIndexOf("/"), url.lastIndexOf(".enc") - 1);
                        Log.i("NEW NAME", newFileName);
                        FileUtils.downloadFile(mContext, json.getString("url"), mContext.getApplicationInfo().dataDir + "/downloaded.db");
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        // Response Error Listener
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mDialog.dismiss();
                DialogUtils.showSimpleDialog(mContext, "Errore", "Impossibile connettersi al server.");
            }
        };
        // Request a string response from the provided URL.
        Log.i("PARAM", "username: " + mUsername);
        Log.i("PARAM", "user_password: " + mUserPassword);
        Log.i("PARAM", "file_password: " + mFilePassword);
        Log.i("PARAM", "file_id: " + mFileId);
        SimpleMultiPartRequest multiPartRequest = new SimpleMultiPartRequest(Request.Method.POST, ServerController.downloadUrl, responseListener, errorListener);
        multiPartRequest.addStringParam("username", mUsername);
        multiPartRequest.addStringParam("md5_user_password", mUserPassword);
        multiPartRequest.addStringParam("md5_file_password", mFilePassword);
        multiPartRequest.addStringParam("file_id", mFileId);
        // Add the request to the RequestQueue.
        queue.add(multiPartRequest);
        return null;
    }
}
