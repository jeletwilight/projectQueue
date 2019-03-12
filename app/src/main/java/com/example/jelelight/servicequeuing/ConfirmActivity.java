package com.example.jelelight.servicequeuing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConfirmActivity extends AppCompatActivity {

    Button okBtn,cancelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_confirm);
        bindView();
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.okBtn:
                    okClick();
                    break;
                case R.id.cancelBtn:
                    cancelClick();
                    break;
            }
        }
    };

    void bindView(){
        okBtn = findViewById(R.id.okBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(onClickListener);
        cancelBtn.setOnClickListener(onClickListener);
    }

    void okClick(){
        Intent okIntent = new Intent(ConfirmActivity.this,LobbyActivity.class);
        startActivity(okIntent);
    }

    void cancelClick(){
        Intent cancelIntent = new Intent(ConfirmActivity.this,LobbyActivity.class);
        startActivity(cancelIntent);
    }

}
