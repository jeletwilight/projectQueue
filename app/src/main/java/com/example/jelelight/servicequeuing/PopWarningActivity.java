package com.example.jelelight.servicequeuing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PopWarningActivity extends Activity {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReferenceQueue;

    private Boolean cancelable;
    private Integer qid;
    private Button yBtn,nBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_warning);

        bind();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.4));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -10;
        getWindow().setAttributes(params);
        //getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void bind(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        mReferenceQueue = mDatabase.getReference().child("Clinic").child("queues");


        yBtn = findViewById(R.id.yes_btn);
        nBtn = findViewById(R.id.no_btn);

        yBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(cancelable == Boolean.TRUE){
                    mReferenceQueue.child(qid.toString()).child("case").setValue("UserCancel");
                    mReferenceQueue.child(qid.toString()).child("status").setValue("canceled");
                }
                Intent i = new Intent(getApplicationContext(),LobbyActivity.class);
                startActivity(i);
                finish();
            }
        });

        nBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void readQID(){
        mReferenceQueue.orderByChild("user").equalTo(String.valueOf(mUser.getUid())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                    String key = childsnapshot.getKey();
                    if(childsnapshot.child("status").getValue().equals("waiting")) {
                        cancelable = Boolean.TRUE;
                        qid = Integer.valueOf(key);
                    }else{
                        cancelable = Boolean.FALSE;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
