package com.example.jelelight.servicequeuing;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.util.Calendar;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUser,mReferenceCurrentUser;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private Button submitBtn,backBtn;
    private Button calenView;
    private EditText nameEdit,heightEdit,weightEdit,birthEdit,cautionEdit,phoneEdit;
    private RadioButton bGen,bBlood;
    private RadioButton genM,genF,bloodA,bloodB,bloodAB,bloodO;
    private RadioGroup rGend,rBlood;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private String bDay,bMonth,bYear;
    private String userID;
    //private boolean hasKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);
        bind();
        pageManage();

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                birthEdit.setText(dayOfMonth+"/"+month+"/"+year);
                bDay = String.valueOf(dayOfMonth);
                bMonth = String.valueOf(month);
                bYear = String.valueOf(year);
            }
        };

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
        calenView = findViewById(R.id.select_date_btn);

        genM = findViewById(R.id.gender_m);
        genF = findViewById(R.id.gender_f);
        bloodA = findViewById(R.id.blood_a);
        bloodB = findViewById(R.id.blood_b);
        bloodAB = findViewById(R.id.blood_ab);
        bloodO = findViewById(R.id.blood_o);

        rGend = findViewById(R.id.radio_gend);
        rBlood = findViewById(R.id.radio_blood);
        submitBtn = findViewById(R.id.editSubmit_Btn);
        backBtn = findViewById(R.id.backEdit_Btn);

        submitBtn.setOnClickListener(onClickListener);
        backBtn.setOnClickListener(onClickListener);
        calenView.setOnClickListener(onClickListener);

        birthEdit.setEnabled(false);
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
                case R.id.select_date_btn:
                    onClickCalendar();
                    break;
                case R.id.backEdit_Btn:
                    startActivity(new Intent(EditProfileActivity.this,ProfileActivity.class));
                    finish();
                    break;
            }

        }
    };

    private void onClickCalendar(){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                EditProfileActivity.this,
                android.R.style.Theme_Holo_Dialog_MinWidth,
                mDateSetListener,year,month,day
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void genderCheck(){
        if(genM.isChecked() || genF.isChecked()) {
            int radioId = rGend.getCheckedRadioButtonId();
            bGen = findViewById(radioId);
            //Toast.makeText(this,bGen.getText(),Toast.LENGTH_SHORT).show();
            if (bGen.getText().equals("Male")) {
                mReferenceUser.child(mUser.getUid()).child("gender").setValue("Male");
            } else if (bGen.getText().equals("Female")) {
                mReferenceUser.child(mUser.getUid()).child("gender").setValue("Female");
            } else {
                mReferenceUser.child(mUser.getUid()).child("gender").setValue("NULL");
            }
        }
    }

    private void bloodCheck(){
        if(bloodA.isChecked() || bloodB.isChecked() || bloodAB.isChecked() || bloodO.isChecked()) {
            int radioId = rBlood.getCheckedRadioButtonId();
            bBlood = findViewById(radioId);
            //Toast.makeText(this,bBlood.getText(),Toast.LENGTH_SHORT).show();
            if(bBlood.getText().equals("A")){
                mReferenceUser.child(mUser.getUid()).child("blood").setValue("A");
            }else if(bBlood.getText().equals("B")){
                mReferenceUser.child(mUser.getUid()).child("blood").setValue("B");
            }else if(bBlood.getText().equals("AB")){
                mReferenceUser.child(mUser.getUid()).child("blood").setValue("AB");
            }else if(bBlood.getText().equals("O")){
                mReferenceUser.child(mUser.getUid()).child("blood").setValue("O");
            }else{
                mReferenceUser.child(mUser.getUid()).child("blood").setValue("NULL");
            }
        }
    }

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
                        if(keyNode.child("blood").exists()){
                            String b = keyNode.child("blood").getValue().toString();
                            if(b.equals("A")){
                                bloodA.setChecked(true);
                            }else if(b.equals("B")){
                                bloodB.setChecked(true);
                            }else if(b.equals("AB")){
                                bloodAB.setChecked(true);
                            }else if(b.equals("O")){
                                bloodO.setChecked(true);
                            }
                        }
                        if(keyNode.child("gender").exists()){
                            String g = keyNode.child("gender").getValue().toString();
                            if(g.equals("Male")){
                                genM.setChecked(true);
                            }else if(g.equals("Female")){
                                genF.setChecked(true);
                            }
                        }
                        if(keyNode.child("bdate").exists()){
                            String bd = keyNode.child("bdate").getValue().toString()+"/"+
                                    keyNode.child("bmonth").getValue().toString()+"/"+
                                    keyNode.child("byear").getValue().toString();
                            birthEdit.setText(bd);
                            bDay = keyNode.child("bdate").getValue().toString();
                            bMonth = keyNode.child("bmonth").getValue().toString();
                            bYear = keyNode.child("byear").getValue().toString();
                        }else{
                            bDay = "1";
                            bMonth = "1";
                            bYear = "1991";
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveProfile(){

        genderCheck();
        bloodCheck();
        if(!birthEdit.getText().equals("") || !birthEdit.getText().equals(null)){
            mReferenceCurrentUser.child("bdate").setValue(bDay.toString());
            mReferenceCurrentUser.child("bmonth").setValue(bMonth.toString());
            mReferenceCurrentUser.child("byear").setValue(bYear.toString());
        }
        if(!phoneEdit.getText().equals("") || !phoneEdit.getText().equals(null)){
            mReferenceCurrentUser.child("phone").setValue(phoneEdit.getText().toString());
        }
        if(!weightEdit.getText().equals("") || !weightEdit.getText().equals(null)){
            mReferenceCurrentUser.child("weight").setValue(weightEdit.getText().toString());
        }
        if(!heightEdit.getText().equals("") || !heightEdit.getText().equals(null)){
            mReferenceCurrentUser.child("height").setValue(heightEdit.getText().toString());
        }
        if(!cautionEdit.getText().equals("") || !cautionEdit.getText().equals(null)){
            mReferenceCurrentUser.child("caution").setValue(cautionEdit.getText().toString());
        }
        if(!nameEdit.getText().equals("") || !nameEdit.getText().equals(null)){
            mReferenceCurrentUser.child("name").setValue(String.valueOf(nameEdit.getText()));
            UserProfileChangeRequest req = new UserProfileChangeRequest.Builder().setDisplayName(String.valueOf(nameEdit.getText())).build();
            mUser.updateProfile(req);
        }
        Toast.makeText(this,"Save Success",Toast.LENGTH_SHORT);

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
