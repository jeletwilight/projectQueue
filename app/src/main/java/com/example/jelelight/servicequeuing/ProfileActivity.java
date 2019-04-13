package com.example.jelelight.servicequeuing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabasae;
    private DatabaseReference mReferenceProfile;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private TextView nameView,genderView,bDayView,
                        bMonthView,bYearView,heightView,
                        weightView,bloodView,cautionView,
                        uidView,phoneView;

    private Button editBtn,backBtn;
    private ImageButton copyBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);
        bind();
        pageManage();
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.editProfile_Btn:
                    startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class));
                    break;
                case R.id.backProfile_Btn:
                    startActivity(new Intent(ProfileActivity.this,LobbyActivity.class));
                    finish();
                    break;
                case R.id.copy_profile:
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("UserID", uidView.getText());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(ProfileActivity.this, "Copied to Clipboard.",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void bind(){
        nameView = findViewById(R.id.name_profile);
        genderView = findViewById(R.id.gender_profile);
        bloodView = findViewById(R.id.blood_profile);
        bDayView = findViewById(R.id.day_profile);
        bMonthView = findViewById(R.id.month_profile);
        bYearView = findViewById(R.id.year_profile);
        weightView = findViewById(R.id.weight_profile);
        heightView = findViewById(R.id.height_profile);
        cautionView = findViewById(R.id.caution_profile);
        uidView = findViewById(R.id.uid_profile);
        phoneView = findViewById(R.id.phone_profile);

        copyBtn = findViewById(R.id.copy_profile);
        editBtn = findViewById(R.id.editProfile_Btn);
        backBtn = findViewById(R.id.backProfile_Btn);

        copyBtn.setOnClickListener(onClickListener);
        editBtn.setOnClickListener(onClickListener);
        backBtn.setOnClickListener(onClickListener);

    }

    private void pageManage(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabasae = FirebaseDatabase.getInstance();
        mReferenceProfile = mDatabasae.getReference().child("Profile");

        uidView.setText(mUser.getUid());
        readProfiles();

    }


    public void readProfiles(){
        mReferenceProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    if(keyNode.getKey().equals(String.valueOf(mUser.getUid()))){
                        nameView.setText(keyNode.child("name").getValue(String.class));
                        checkProfileValue(keyNode,"gender",genderView);
                        checkProfileValue(keyNode,"blood",bloodView);
                        checkProfileValue(keyNode,"bdate",bDayView);
                        checkProfileValue(keyNode,"bmonth",bMonthView);
                        checkProfileValue(keyNode,"byear",bYearView);
                        checkProfileValue(keyNode,"phone",phoneView);
                        checkProfileValue(keyNode,"height",heightView);
                        checkProfileValue(keyNode,"weight",weightView);
                        checkProfileValue(keyNode,"caution",cautionView);
                    }
                }

                if(!keys.contains(String.valueOf(mUser.getUid()))){
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("name").setValue(mUser.getDisplayName());
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("gender").setValue("NULL");
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("blood").setValue("NULL");
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("bdate").setValue("NULL");
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("bmonth").setValue("NULL");
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("byear").setValue("NULL");
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("phone").setValue("NULL");
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("age").setValue("NULL");
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("height").setValue("NULL");
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("weight").setValue("NULL");
                    mReferenceProfile.child(String.valueOf(mUser.getUid())).child("caution").setValue("NULL");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkProfileValue(DataSnapshot node,String child,TextView tv){
        if(node.child(child).exists()){
            if(!node.child(child).getValue(String.class).equals("NULL")){
                tv.setText(node.child(child).getValue(String.class));
            }else{
                tv.setText(" - ");
            }
        }else{
            mReferenceProfile.child(mUser.getUid()).child(child).setValue("NULL");
            tv.setText(" - ");
        }
    }
}
