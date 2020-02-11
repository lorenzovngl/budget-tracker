package it.unibo.studio.vainigli.lorenzo.budgettracker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import it.unibo.studio.vainigli.lorenzo.budgettracker.R;
import it.unibo.studio.vainigli.lorenzo.budgettracker.controllers.ServerController;
import it.unibo.studio.vainigli.lorenzo.budgettracker.utilities.DialogUtils;

public class RegisterActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        final ServerController serverController = new ServerController(this, this);
        final EditText fullnameEditText = (EditText) findViewById(R.id.editTextFullname);
        final EditText usernameEditText = (EditText) findViewById(R.id.editTextUsername);
        final EditText passwordEditText = (EditText) findViewById(R.id.editTextPassword);
        final EditText repeatPasswordEditText = (EditText) findViewById(R.id.editTextPasswordRepeat);
        Button registerButton = (Button) findViewById(R.id.buttonRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordEditText.getText().toString();
                String repeatPassword = repeatPasswordEditText.getText().toString();
                if (password.equals(repeatPassword)){
                    String fullname = fullnameEditText.getText().toString();
                    String username = usernameEditText.getText().toString();
                    serverController.register(fullname, username, password);
                } else {
                    DialogUtils.showSimpleDialog(mContext, "Errore", "La password non coincide.");
                }
            }
        });
    }
}
