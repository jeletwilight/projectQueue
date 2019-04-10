package com.example.jelelight.servicequeuing;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private EditText idField,pwField;
    private Button loginBtn,guestBtn;
    private TextView regView;
    private ProgressBar progress;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        bindView();
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(MainActivity.this,LobbyActivity.class));
            finish();
        }
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loginBtn:
                    login();
                    break;
                case R.id.guestBtn:
                    guest();
                    break;
                case R.id.reg:
                    regist();
                    break;
            }
            hideKeyboardInput(v);
        }
    };

    void bindView(){
        regView = findViewById(R.id.reg);
        idField = findViewById(R.id.idText);
        pwField = findViewById(R.id.pwText);
        loginBtn = findViewById(R.id.loginBtn);
        guestBtn = findViewById(R.id.guestBtn);
        progress = findViewById(R.id.progressBarLogin);
        idField.requestFocus();
        pwField.requestFocus();
        regView.setOnClickListener(onClickListener);
        loginBtn.setOnClickListener(onClickListener);
        guestBtn.setOnClickListener(onClickListener);
    }

    public void login(){
        String email = idField.getText().toString();
        String password = pwField.getText().toString();
        progress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MainLogin", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            progress.setVisibility(View.GONE);
                            Intent loginIntent = new Intent(MainActivity.this , LobbyActivity.class);
                            MainActivity.this.startActivity(loginIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progress.setVisibility(View.GONE);
                            Log.w("MainLogin", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        //Intent loginIntent = new Intent(MainActivity.this , LobbyActivity.class);
        //MainActivity.this.startActivity(loginIntent);
    }

    public void guest(){
        Intent loginIntent = new Intent(MainActivity.this , LobbyActivity.class);
        MainActivity.this.startActivity(loginIntent);
    }

    public void regist(){
        Intent regIntent = new Intent(MainActivity.this , SignUpActivity.class);
        MainActivity.this.startActivity(regIntent);
    }

    private void hideKeyboardInput(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
