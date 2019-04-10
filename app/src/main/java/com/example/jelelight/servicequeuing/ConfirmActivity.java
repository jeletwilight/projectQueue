package com.example.jelelight.servicequeuing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConfirmActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceToken,mReferenceUser,mReferenceMyCount,mReferenceClinic;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Integer token,myCount,qid;
    private boolean inqueue;
    Button okBtn,cancelBtn;
    TextView remainTV,qTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_confirm);
        mDatabase = FirebaseDatabase.getInstance();
        bindView();
        managePage();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser == null){
            startActivity(new Intent(ConfirmActivity.this,MainActivity.class));
            finish();
        }
        managePage();

    }

    @Override
    protected void onResume() {
        super.onResume();
        managePage();
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
        remainTV = findViewById(R.id.remaincount);
        qTV = findViewById(R.id.queueID);
        okBtn = findViewById(R.id.okBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        okBtn.setOnClickListener(onClickListener);
        cancelBtn.setOnClickListener(onClickListener);
    }

    void managePage(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceToken = mDatabase.getReference().child("Clinic").child("queue_token");
        mReferenceUser = mDatabase.getReference().child("Users");
        mReferenceMyCount = mReferenceUser.child(mUser.getUid()).child("queueNo");
        mReferenceClinic = mDatabase.getReference().child("Clinic");

        readInQueue();
        readQID();

        readToken();
        readMyCount();
    }

    void okClick(){
        Intent okIntent = new Intent(ConfirmActivity.this,LobbyActivity.class);
        startActivity(okIntent);
        //finish();
    }

    void cancelClick(){
        Intent cancelIntent = new Intent(ConfirmActivity.this,LobbyActivity.class);
        startActivity(cancelIntent);
        finish();
    }

    private void readQID(){
        mReferenceClinic.child("queues").orderByChild("user").equalTo(String.valueOf(mUser.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                    String key = childsnapshot.getKey();
                    qTV.setText(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readInQueue(){
        mReferenceUser.child("inQueue").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean iq = (Boolean) dataSnapshot.getValue();
                if(false) {
                    startActivity(new Intent(ConfirmActivity.this, LobbyActivity.class));
                    finish();
                }
                //Log.d("CountReadQueue", String.valueOf(inqueue));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMyCount(){
        mReferenceMyCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myCount = dataSnapshot.getValue(Integer.class);
                //remainTV.setText(String.valueOf(myCount-token));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readToken(){
        mReferenceToken.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                token = dataSnapshot.getValue(Integer.class);
                remainTV.setText(String.valueOf(myCount-token));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void currentQueueRead(){}

}
