package com.example.oxipulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText txt_email;
    TextInputEditText txt_first_name;
    TextInputEditText txt_last_name;
    TextInputEditText txt_password;
    TextInputEditText txt_confirm_pass;
    MaterialCheckBox chk_health_person;
    MaterialCheckBox chk_legal_notice;
    Button btn_register;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        txt_email=findViewById(R.id.text_input_name);
        txt_first_name = findViewById(R.id.text_input_name);
        txt_last_name= findViewById(R.id.text_input_lastName);
        txt_password=findViewById(R.id.text_input_password);
        txt_confirm_pass=findViewById(R.id.text_input_confirmPass);
        chk_health_person=findViewById(R.id.chk_health_person);
        chk_legal_notice=findViewById(R.id.chk_legal_notice);
        btn_register=findViewById(R.id.button_register);

        auth=FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= Objects.requireNonNull(txt_email.getText()).toString();
                String firstname=Objects.requireNonNull(txt_first_name.getText()).toString();
                String lastname=Objects.requireNonNull(txt_last_name.getText()).toString();
                String password=Objects.requireNonNull(txt_password.getText()).toString();

            }
        });



    }
    private void register(String username, String email,String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            assert firebaseUser !=null;
                            String userid=firebaseUser.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userid);
                           // hashMap.put(username)
                        }
                    }
                })

    }



}