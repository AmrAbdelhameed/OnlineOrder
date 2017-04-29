package com.example.amr.onlineorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginAsUser extends AppCompatActivity {

    private EditText txtEmailLogin;
    private EditText txtPwd;
    private FirebaseAuth firebaseAuth;
    Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_as_user);

        txtEmailLogin = (EditText) findViewById(R.id.input_email_log_user);
        txtPwd = (EditText) findViewById(R.id.input_password_log_user);
        firebaseAuth = FirebaseAuth.getInstance();

        btn_login = (Button) findViewById(R.id.btn_login_user);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtEmailLogin.getText().toString().isEmpty()) {
                    txtEmailLogin.setError("Please Enter Email");
                }
                if (txtPwd.getText().toString().isEmpty()) {
                    txtPwd.setError("Please Enter Password");
                } else {
                    final ProgressDialog progressDialog = ProgressDialog.show(LoginAsUser.this, "Please wait...", "Proccessing...", true);

                    (firebaseAuth.signInWithEmailAndPassword(txtEmailLogin.getText().toString(), txtPwd.getText().toString()))
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();

                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginAsUser.this, "Login successful", Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.e("ERROR", task.getException().toString());
                                        Toast.makeText(LoginAsUser.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void GoRegisterUser(View view) {
        Intent i = new Intent(LoginAsUser.this, RegisterAsUser.class);
        startActivity(i);
    }

    public void ForgetUser(View view) {
        Intent i = new Intent(LoginAsUser.this, ForgetPassword.class);
        startActivity(i);
    }
}