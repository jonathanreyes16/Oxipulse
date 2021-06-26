package com.example.oxipulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    //variables
    EditText email,password;
    Button btn_lgn;
    FirebaseAuth auth;
    TextView forgot_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //inflar los widgets
        email=findViewById(R.id.itext_email_login);
        password=findViewById(R.id.itext_pass_login);
        btn_lgn=findViewById(R.id.btn_ingresar);
        forgot_password = findViewById(R.id.lkforgot_pass);

        //evento del textview de olvidaste la contrasena
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //crea un nuevo intent, que seria la pantalla de olvide mi contrasena
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });

        //se obtiene la instancia firebase que se encarga de las credenciales
        auth = FirebaseAuth.getInstance();

        //evento del boton login
        btn_lgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se guardan en variables los valores de email y password
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                //si los campos estan vacios
                if ( TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password))
                {   //mensaje de "los campos estan vacios"
                    Toast.makeText(LoginActivity.this, "All Fields Are Required !", Toast.LENGTH_SHORT).show();
                } else {
                    //sino estan vacios,
                    //se inicia sesion con correo y password
                    auth.signInWithEmailAndPassword(txt_email,txt_password).addOnCompleteListener(task -> {
                        //si es completado con exito el inicio de sesion
                        if (task.isSuccessful()) {
                            //se abre la pantalla principal
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }  else {
                            //sino la autenticacion fallo
                            Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}