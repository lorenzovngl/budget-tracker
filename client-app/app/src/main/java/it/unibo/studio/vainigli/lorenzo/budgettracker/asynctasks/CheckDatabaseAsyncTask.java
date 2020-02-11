package it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks;

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

import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.dialogs.RegisterDetailDialog;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Register;

import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_CONNECTION_ERROR;

public class CheckDatabaseAsyncTask extends AsyncTask<Void, Integer, Void> {

    private Context mContext;
    private String mUsername, mUserPassword;
    private Register mDatabase;
    private RegisterDetailDialog mDialog;

    public CheckDatabaseAsyncTask(Context context, RegisterDetailDialog dialog, String username, String userPassword, Register database){
        mContext = context;
        mDialog = dialog;
        mUsername = username;
        mUserPassword = userPassword;
        mDatabase = database;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        // Response Listener
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("RESPONSE", response);
                try {
                    JSONObject json = new JSONObject(response);
                    publishProgress(json.getInt("response_id"));
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        // Response Error Listener
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                publishProgress(RESPONSE_CONNECTION_ERROR);
            }
        };
        // Request a string response from the provided URL.
        SimpleMultiPartRequest multiPartRequest = new SimpleMultiPartRequest(Request.Method.POST, ServerController.compareDatabaseUrl, responseListener, errorListener);
        Log.i("PARAM username", mUsername);
        Log.i("PARAM md5_user_password", mUserPassword);
        Log.i("PARAM md5_file_password", mDatabase.getMD5Password());
        Log.i("PARAM file_time", Long.toString(mDatabase.getLastEdit()));
        multiPartRequest.addStringParam("username", mUsername);
        multiPartRequest.addStringParam("md5_user_password", mUserPassword);
        multiPartRequest.addStringParam("file_id", mDatabase.getRemoteId());
        String time = Long.toString(mDatabase.getLastEdit());
        // Convert time in milliseconds to time in seconds
        time = time.substring(0, time.length() - 3);
        multiPartRequest.addStringParam("file_time", time);
        multiPartRequest.addStringParam("md5_file_password", mDatabase.getMD5Password());
        // Add the request to the RequestQueue.
        queue.add(multiPartRequest);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mDialog.setResponseView(values[0]);
    }
}
