package com.example.jelelight.servicequeuing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUser,mReferenceCurrentUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private EditText nameEdit,heightEdit,weightEdit,birthEdit,cautionEdit,phoneEdit;
    private RadioButton gendM,gendF,bloodA,bloodB,bloodO,bloodAB;

    private Button submitBtn,backBtn;

    private String userID;
    private boolean hasKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);
        bind();
        pageManage();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userID = mUser.getUid();
        if(mUser == null){
            startActivity(new Intent(EditProfileActivity.this,MainActivity.class));
            finish();
        }
        pageManage();

    }

    private void bind(){
        nameEdit = findViewById(R.id.name_edit);
        birthEdit = findViewById(R.id.birth_edit);
        heightEdit = findViewById(R.id.height_edit);
        weightEdit = findViewById(R.id.weight_edit);
        cautionEdit = findViewById(R.id.caution_edit);
        phoneEdit = findViewById(R.id.phone_edit);
        gendM = findViewById(R.id.gender_m);
        gendF = findViewById(R.id.gender_f);
        bloodA = findViewById(R.id.blood_a);
        bloodB = findViewById(R.id.blood_b);
        bloodO = findViewById(R.id.blood_o);
        bloodAB = findViewById(R.id.blood_ab);

        submitBtn = findViewById(R.id.editSubmit_Btn);
        backBtn = findViewById(R.id.backEdit_Btn);

        submitBtn.setOnClickListener(onClickListener);
        backBtn.setOnClickListener(onClickListener);
    }

    private void pageManage(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUser = mDatabase.getReference().child("Profile");
        mReferenceCurrentUser = mDatabase.getReference().child("Profile").child(mUser.getUid());

        readProfile();

    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.editSubmit_Btn:
                    saveProfile();
                    startActivity(new Intent(EditProfileActivity.this,ProfileActivity.class));
                    finish();
                    break;
                case R.id.backEdit_Btn:
                    startActivity(new Intent(EditProfileActivity.this,ProfileActivity.class));
                    finish();
                    break;
            }
        }
    };

    private void readProfile(){
        mReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    if(keyNode.getKey().equals(String.valueOf(mUser.getUid()))){
                        checkProfileValue(keyNode,"name",nameEdit);
                        checkProfileValue(keyNode,"phone",phoneEdit);
                        checkProfileValue(keyNode,"height",heightEdit);
                        checkProfileValue(keyNode,"weight",weightEdit);
                        checkProfileValue(keyNode,"caution",cautionEdit);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveProfile(){
        mReferenceCurrentUser.child("name").setValue(String.valueOf(nameEdit.getText()));
        UserProfileChangeRequest req = new UserProfileChangeRequest.Builder().setDisplayName(String.valueOf(nameEdit.getText())).build();
        mUser.updateProfile(req);
    }

    private void checkProfileValue(DataSnapshot node, String child, EditText tv){
        if(node.child(child).exists()){
            if(!node.child(child).getValue(String.class).equals("NULL")){
                tv.setText(node.child(child).getValue(String.class));
            }else{
                tv.setText(null);
            }
        }else{
            mReferenceUser.child(mUser.getUid()).child(child).setValue("NULL");
            tv.setText(null);
        }
    }

}
