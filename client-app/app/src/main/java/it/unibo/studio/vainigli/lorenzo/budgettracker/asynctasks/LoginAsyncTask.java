package it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.LoginActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.UsersPrefsController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoUsers;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.HashUtils;

import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.loginUrl;

public class LoginAsyncTask extends AsyncTask<Void, Integer, Void> {

    private Context mContext;
    private String mUsername, mPassword;
    private LoginActivity mLoginActivity;
    private ProgressDialog mDialog;

    public LoginAsyncTask(Context context, LoginActivity activity, String username, String password){
        mContext = context;
        mUsername = username;
        mPassword = password;
        mLoginActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = DialogUtils.showLoadingDialog(mContext, "Login", "Connessione in corso");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        final String md5Password = HashUtils.md5(mPassword);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        // Response Listener
        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mDialog.dismiss();
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt("response_id") == ResponseCodes.RESPONSE_LOGIN_OK){
                        UsersPrefsController.prefsLogin(mContext);
                        UsersPrefsController.setFullname(mContext, json.getString("name"));
                        UsersPrefsController.setUsername(mContext, mUsername);
                        UsersPrefsController.setMD5Password(mContext, md5Password);
                        DaoUsers daoUsers = new DaoUsers(Const.DBMode.WRITE, mContext);
                        daoUsers.insert(json.getString("name"), mUsername, md5Password);
                        mLoginActivity.openHome();
                    } else if (json.getInt("response_id") == ResponseCodes.RESPONSE_WRONG_CREDENTIALS){
                        DialogUtils.showSimpleDialog(mContext, "Errore", "Credenziali errate!");
                    } else {
                        DialogUtils.showSimpleDialog(mContext, "Errore", "Errore di comunicazione con il server.");
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        // Response Error Listener
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.showSimpleDialog(mContext, "Errore", "Impossibile connettersi al server.");
            }
        };
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, responseListener, errorListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", mUsername);
                params.put("md5_password", md5Password);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return null;
    }
}
