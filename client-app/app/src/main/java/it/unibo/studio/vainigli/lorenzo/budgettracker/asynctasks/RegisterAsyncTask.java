package it.unibo.studio.vainigli.lorenzo.budgettracker.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import it.unibo.studio.vainigli.lorenzo.budgettracker.activities.RegisterActivity;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.HashUtils;

import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.ResponseCodes.RESPONSE_REGISTRATION_OK;
import static it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController.registerUrl;

public class RegisterAsyncTask extends AsyncTask<Void, Integer, Void> {

    private Context mContext;
    private String mFullname, mUsername, mPassword;
    private RegisterActivity mRegisterActivity;
    private ProgressDialog mDialog;

    public RegisterAsyncTask(Context context, RegisterActivity activity, String fullname, String username, String password){
        mContext = context;
        mFullname = fullname;
        mUsername = username;
        mPassword = password;
        mRegisterActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = DialogUtils.showLoadingDialog(mContext, "Registrazione", "Connessione in corso");
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
                if (response.equals(Integer.toString(RESPONSE_REGISTRATION_OK))){
                    DialogUtils.showRegistratioOKDialog(mContext, mRegisterActivity, "Successo", "Registrazione effettuata!");
                } else {
                    DialogUtils.showSimpleDialog(mContext, "Errore", "Errore di comunicazione con il server.");
                }
            }
        };
        // Response Error Listener
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.showSimpleDialog(mContext, "Errore", "Impossibile connettersi al server.");
            }
        };
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, registerUrl, responseListener, errorListener){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("fullname", mFullname);
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
