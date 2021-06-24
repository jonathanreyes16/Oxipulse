package com.example.oxipulse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText edtEmail;
    private Button btnRecuper;

    private String email = "";

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        mAuth = FirebaseAuth.getInstance();
        mDialog=new ProgressDialog(this);
        edtEmail = (EditText) findViewById(R.id.texEmail);
        btnRecuper = (Button) findViewById(R.id.btnRecuperar);
        btnRecuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "Debe ingresar el Email", Toast.LENGTH_SHORT).show();
                } else {

                    mDialog.setMessage("Procesando porfavor espere");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    resetPassword();
                }
                mDialog.dismiss();
            }

        });
    }

    private void resetPassword() {
        mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this, "Se ha enviado un correo para reestablecer tu contraseña", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Ese Email no esta registrado", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
}