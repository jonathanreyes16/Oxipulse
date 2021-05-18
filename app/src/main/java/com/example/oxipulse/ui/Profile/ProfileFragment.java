package com.example.oxipulse.ui.Profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.oxipulse.R;
import com.example.oxipulse.classes.patient;
import com.example.oxipulse.ui.DatePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.snapshot.Index;

import java.util.Objects;
import java.util.TreeMap;

public class ProfileFragment extends Fragment {

    // declaracion de variables
    ProfileViewModel profileViewModel;
    //declaracion de variables
    EditText tName,tLast1,tLast2,tBirthdate,tWeight,tHeight;
    Spinner tGender,tWeight_int,tWeight_dec,tHeight_int,tHeight_dec;
    CheckBox tAsthma,tDiabetes,tHypertension;
    String uid,isdoc,imageUrl;
    ImageView profilePic;
    Boolean op;
    DatabaseReference database;
    patient temp;
    Button Edit, btn_accept,btn_cancel;
    private static final String BARRA = "/";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //user = FirebaseAuth.getInstance().getCurrentUser();

        //se instancia el viewmodel, y se le pasan los parametros del ProfileViewModel
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        //se infla el layout de este fragmento
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        //inflar los widgets
        tName=v.findViewById(R.id.et_name);
        tLast1=v.findViewById(R.id.et_name2);
        tLast2=v.findViewById(R.id.et_name3);
        tBirthdate=v.findViewById(R.id.s_birthdate);
        tWeight_int=v.findViewById(R.id.s_weight_int);
        tWeight_dec=v.findViewById(R.id.s_weight_dec);


        tHeight=v.findViewById(R.id.np_height);
        tGender=v.findViewById(R.id.s_gender);
        tAsthma=v.findViewById(R.id.chk_asthma);
        tDiabetes=v.findViewById(R.id.chk_diabetes);
        profilePic=v.findViewById(R.id.imgProfilePic);
        tHypertension=v.findViewById(R.id.chk_hypertension);
        Edit=v.findViewById(R.id.btn_edit);
        btn_accept=v.findViewById(R.id.btn_accept);
        btn_cancel=v.findViewById(R.id.btn_cancel);

        //metodo para desabilitar los textos
        setAllDisabled();
        //se obtiene el usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //si el usuario no es nulo se ejecuta el codigo
        if (user!=null){
            //obtenemos el uid del usuario actual  para despues tener todos los datos buscando solo ese id
            uid=user.getUid();

            //path de la base de datos que usaremos, en este caso solo nos interesa el usuario actual, En User/uid, donde uid es el usuario actual
            database= FirebaseDatabase.getInstance().getReference();
            //evento para leer de la base de datos
            database.child("Users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    //si la tarea no es completada, no se puede obtener el snapshot de la base de datos se marca un error en el log
                    if (!task.isSuccessful()){
                        Log.e("firebase","Error Getting data",task.getException());
                    }
                    //sino, significa que la tarea se cumple y podemos seguir
                    else {
                        //creamos un nuevo objeto de la clase paciente y este tendra los datos del resultado de la tarea(task), que en este caso son los datos del usuario
                        patient p = Objects.requireNonNull(task.getResult()).getValue(patient.class);
                        //se le asignan los valores de paciente o doctor a la interfaz
                        isdoc=p.getIsDoc();
                        imageUrl=p.getImageUrl();

                        tName.setText(p.getFirstName());
                        tLast1.setText(p.getLastName());
                        tLast2.setText(p.getMiddleName());
                        tBirthdate.setText(p.getBirthdate());
                        tWeight.setText(p.getWeight());
                        tHeight.setText(p.getHeight());
                        if (p.getGender().equals("Hombre")){
                            tGender.setSelection(0);
                        }
                        else {tGender.setSelection(1);}
                        tAsthma.setChecked(Boolean.parseBoolean(p.getAsma()));
                        tDiabetes.setChecked(Boolean.parseBoolean(p.getDiabetes()));
                        tHypertension.setChecked(Boolean.parseBoolean(p.getHipertension()));

                        //poner la imager de perfil, no funciona aun
                        //if (p.getImageUrl()!=null){
                        //    profilePic.setImageURI(Uri.parse(p.getImageUrl())); //agregar la imagen
                        //}

                        //log para debug
                        Log.d("firebase",String.valueOf(Objects.requireNonNull(task.getResult()).getValue()));
                    }
                }
            });
        }

        //evento del boton edit
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si es verdadero se deshabilitan
                if (op){
                    setAllDisabled();
                }
                //si es falso se habilitan
                else {
                    setAllEnabled();
                    Edit.setEnabled(false);
                    btn_accept.setVisibility(View.VISIBLE);
                    btn_cancel.setVisibility(View.VISIBLE);
                }
            }
        });

        //evento del boton accept
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si los campos estan vacios, no se actualiza (Se actualiza aunque esten vacios, Fix later)
                if ((TextUtils.isEmpty(tName.getText()))||(TextUtils.isEmpty(tLast1.getText())) ||
                        (TextUtils.isEmpty(tLast2.getText()))||(TextUtils.isEmpty(tBirthdate.getText()))||
                        (TextUtils.isEmpty(tWeight.getText()))||(TextUtils.isEmpty(tHeight.getText()))||
                        (TextUtils.isEmpty(tGender.getSelectedItem().toString()))||(TextUtils.isEmpty(tHypertension.getText()))||
                        (TextUtils.isEmpty(tDiabetes.getText()))||(TextUtils.isEmpty(tAsthma.getText()))){

                    //se crea un paciente para asignarle los valores de los campos
                    patient u = new patient();
                    u.setFirstName(tName.getText().toString());
                    u.setLastName(tLast1.getText().toString());
                    u.setMiddleName(tLast2.getText().toString());
                    u.setBirthdate(tBirthdate.getText().toString());
                    u.setWeight(tWeight.getText().toString());
                    u.setHeight(tHeight.getText().toString());
                    u.setGender(tGender.getSelectedItem().toString());
                    u.setAsma(String.valueOf(tAsthma.isChecked()));
                    u.setDiabetes(String.valueOf(tDiabetes.isChecked()));
                    u.setHipertension(String.valueOf(tHypertension.isChecked()));
                    u.setImageUrl(imageUrl);
                    u.setIsDoc(isdoc);
                    u.setId(uid);
                    //database.child("firstName").setValue(u.getFirstName());
                    //se envia a la base de datos el paciente generado para que se actualicen los datos
                    database.child("Users").child(uid).setValue(u);
                    //se crea un mensage que dice que se guardo
                    Toast.makeText(getContext(), "Guardado", Toast.LENGTH_SHORT).show();
                }
                //si no estan vacios los campos, se le notificara que deben llenarlos
                else {
                    Toast.makeText(getContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
                disableEdit();
            }
        });
        //evento del boton cancel
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si se cancela se regresan los valores y se pone invisibles los botones
                disableEdit();
                
                //getTemp();
            }
        });

        /*
        tBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(),"datePicker");
            }


        });
         */
        /*final TextView textView = root.findViewById(R.id.text_home);
        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

         */
        return v;
    }
    //metodo para que caambie la vista dependiendo si es doctor o no
    private void DoctorView(){
    }
    //metodo para que cambie la vista dependiendo si es paciente o no
    private void PatientView(){
    }

    private void disableEdit(){
        Edit.setEnabled(true);
        setAllDisabled();
        btn_accept.setVisibility(View.INVISIBLE);
        btn_cancel.setVisibility(View.INVISIBLE);
    }
    //evento para habilitar la interfaz
    private void setAllEnabled(){
        tName.setEnabled(true);
        tLast1.setEnabled(true);
        tLast2.setEnabled(true);
        tBirthdate.setEnabled(true);
        tWeight.setEnabled(true);
        tHeight.setEnabled(true);
        tGender.setEnabled(true);
        tAsthma.setEnabled(true);
        tDiabetes.setEnabled(true);
        tHypertension.setEnabled(true);
        op=true;
    }
    //evento para desabilitar la interfaz
    private void setAllDisabled(){
        tName.setEnabled(false);
        tLast1.setEnabled(false);
        tLast2.setEnabled(false);
        tBirthdate.setEnabled(false);
        tWeight.setEnabled(false);
        tHeight.setEnabled(false);
        tGender.setEnabled(false);
        tAsthma.setEnabled(false);
        tDiabetes.setEnabled(false);
        tHypertension.setEnabled(false);
        op=false;
    }
    //metodo almacena los valores de los textos para al cancelar regresarlos a como estaban
    private void setTemp(){
        temp = new patient();
        temp.setFirstName(tName.getText().toString());
        temp.setLastName(tLast1.getText().toString());
        temp.setMiddleName(tLast2.getText().toString());
        temp.setBirthdate(tBirthdate.getText().toString());
        temp.setWeight(tWeight.getText().toString());
        temp.setHeight(tHeight.getText().toString());
        temp.setGender(tGender.getSelectedItem().toString());
        temp.setAsma(tAsthma.toString());
        temp.setDiabetes(tDiabetes.toString());
        temp.setHipertension(tHypertension.toString());
    }
    private patient getTemp(patient temp){
        tName.setText(temp.getFirstName());
        tLast1.setText(temp.getLastName());
        tLast2.setText(temp.getMiddleName());
        tBirthdate.setText(temp.getBirthdate());
        tWeight.setText(temp.getWeight());
        tHeight.setText(temp.getHeight());
        if (temp.getGender().equals("Hombre")){
            tGender.setSelection(0);
        }
        else {tGender.setSelection(1);}
        tAsthma.setChecked(Boolean.parseBoolean(temp.getAsma()));
        tDiabetes.setChecked(Boolean.parseBoolean(temp.getDiabetes()));
        tHypertension.setChecked(Boolean.parseBoolean(temp.getHipertension()));
        return temp;
    }

}