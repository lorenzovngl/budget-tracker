package it.unibo.studio.vainigli.lorenzo.budgettracker.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.UsersPrefsController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.DaoUsers;
import it.unibo.studio.vainigli.lorenzo.budgettracker.database.UsersController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.User;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.HashUtils;

public class LoginActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        final ServerController serverController = new ServerController(this, this);
        if (UsersPrefsController.isAlrealdyLoggedIn(mContext)){
            openHome();
        }
        setContentView(R.layout.activity_login);
        final EditText usernameEditText = (EditText) findViewById(R.id.editTextUsername);
        final EditText passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        Button loginButton = (Button) findViewById(R.id.buttonLogin);
        Button registerButton = (Button) findViewById(R.id.buttonRegister);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                DaoUsers daoUsers = new DaoUsers(Const.DBMode.READ, mContext);
                try {
                    User user = daoUsers.getOne(username);
                    String md5Password = HashUtils.md5(password);
                    if (user.getUsername().equals(username) && user.getMD5Password().equals(md5Password)){
                        openHome();
                    } else {
                        DialogUtils.showSimpleDialog(mContext, "Errore", "Credenziali errate!");
                    }
                } catch (NullPointerException e){
                    e.printStackTrace();
                    serverController.authenticate(username, password);
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void openHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
