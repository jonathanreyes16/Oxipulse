package com.example.oxipulse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    //declaracion de variables
    TextInputEditText txt_email;
    TextInputEditText txt_first_name;
    TextInputEditText txt_last_name;
    TextInputEditText txt_middle_name;
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
        //inflar widgets
        txt_email=findViewById(R.id.text_input_email);
        txt_first_name = findViewById(R.id.text_input_name);
        txt_last_name= findViewById(R.id.text_input_lastName);
        txt_middle_name= findViewById(R.id.text_input_middle_name);
        txt_password=findViewById(R.id.text_input_password);
        txt_confirm_pass=findViewById(R.id.text_input_confirmPass);
        chk_health_person=findViewById(R.id.chk_health_person);
        chk_legal_notice=findViewById(R.id.chk_legal_notice);

        btn_register=findViewById(R.id.button_register);

        auth=FirebaseAuth.getInstance();

        //evento del boton registro
        btn_register.setOnClickListener(v -> {
            String email= Objects.requireNonNull(txt_email.getText()).toString().trim();
            String firstname= Objects.requireNonNull(txt_first_name.getText()).toString().trim();
            String lastname= Objects.requireNonNull(txt_last_name.getText()).toString().trim();
            String middlename= Objects.requireNonNull(txt_middle_name.getText()).toString().trim();
            String password= Objects.requireNonNull(txt_password.getText()).toString().trim();
            String confirmpass = Objects.requireNonNull(txt_confirm_pass.getText()).toString().trim();

            String isd = String.valueOf(chk_health_person.isChecked());
            //si
            if (TextUtils.isEmpty(firstname) || TextUtils.isEmpty(lastname) || TextUtils.isEmpty(middlename) ||
            TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmpass)||
            !chk_legal_notice.isChecked() )
            {//alguno esta vacio monstra un toast que diga "todos los campos son requeridos"
                Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            }else if (password.length()<6){
                //despues, el password debe ser mayor a 6
                Toast.makeText(RegisterActivity.this, "Password Length Is < 6!", Toast.LENGTH_SHORT).show();
            }else if (!password.equals(confirmpass)){
                //verifica que los passwords sean iguales
                Toast.makeText(RegisterActivity.this, "Passwords are different", Toast.LENGTH_SHORT).show();
            }else if(!chk_legal_notice.isChecked()){
                //deben aceptarse los terminos y condiciones
                Toast.makeText(RegisterActivity.this, "You should accept the legal notice to use this app", Toast.LENGTH_SHORT).show();
            }else {
                //si todo lo anterior es correcto se crea un usuario
                register(firstname,lastname,middlename,email,password,isd);
            }
        });



    }
    private void register(final String firstname,String lastname,String middlename, String email,String password,String isdoc){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser=auth.getCurrentUser();
                        assert firebaseUser != null;
                        String userid=firebaseUser.getUid();

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("id",userid);
                        hashMap.put("firstName",firstname);
                        hashMap.put("lastName",lastname);
                        hashMap.put("middleName",middlename);
                        hashMap.put("imageURL","default");
                        hashMap.put("isDoc",isdoc);


                        databaseReference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                //Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                //startActivity(intent);
                                Toast.makeText(this, "Usuario creado con exito", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    } else {

                        Toast.makeText(RegisterActivity.this, "You cant register with this email", Toast.LENGTH_SHORT).show();
                    }
                });

    }



}