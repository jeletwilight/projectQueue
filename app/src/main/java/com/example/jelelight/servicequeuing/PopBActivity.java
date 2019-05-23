package com.example.jelelight.servicequeuing;

import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

public class PopBActivity extends Activity {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReferenceQueue,mReferenceUser;

    private List<Boolean> cancelable = new ArrayList<>();
    private Integer qid;
    private Button y2Btn,n2Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_b);
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

    @Override
    protected void onStart() {
        super.onStart();
        readQID();
    }

    private void bind(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        mReferenceQueue = mDatabase.getReference().child("Clinic").child("Bqueues");
        mReferenceUser = mDatabase.getReference().child("Users");


        y2Btn = findViewById(R.id.Byes_btn);
        n2Btn = findViewById(R.id.Bno_btn);

        y2Btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(cancelable.get(0) == Boolean.TRUE){
                    mReferenceUser.child(mUser.getUid()).child("inQueue").setValue(Boolean.FALSE);
                    mReferenceUser.child(mUser.getUid()).child("queueNo").setValue(null);
                    mReferenceUser.child(mUser.getUid()).child("queueType").setValue(null);

                    mReferenceQueue.child(qid.toString()).child("case").setValue("UserCancel");
                    mReferenceQueue.child(qid.toString()).child("status").setValue("canceled");
                }
                startActivity(new Intent(getApplicationContext(),SelectCaseActivity.class));
                finish();
            }
        });

        n2Btn.setOnClickListener(new View.OnClickListener(){
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
                cancelable.clear();
                for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                    String key = childsnapshot.getKey();
                    if(childsnapshot.child("status").exists()) {
                        if (childsnapshot.child("status").getValue().equals("waiting")) {
                            cancelable.add(Boolean.TRUE);
                            qid = Integer.valueOf(key);
                        } else {
                            cancelable.add(Boolean.FALSE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
