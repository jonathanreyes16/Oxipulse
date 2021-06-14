package com.example.oxipulse.ui.Profile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.oxipulse.R;
import com.example.oxipulse.model.patient;
import com.example.oxipulse.ui.DatePicker.DatePickerFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    // declaracion de variables
    ProfileViewModel profileViewModel;
    EditText tName,tLast1,tLast2,tBirthdate,tWeight,tHeight;
    Button btn_edit, btn_accept,btn_cancel,btn_calendar;
    Spinner tGender;
    //Spinner tWeight_int,tWeight_dec,tHeight_int,tHeight_dec;
    CheckBox tAsthma,tDiabetes,tHypertension;
    String uid,isdoc,imageUrl;
    ImageView profilePic;
    //Boolean op;
    FirebaseDatabase Database;
    DatabaseReference ref;
    patient temp;
    private static final String BARRA = "/";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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
        tWeight=v.findViewById(R.id.np_weight);
        tHeight=v.findViewById(R.id.np_height);
        tGender=v.findViewById(R.id.s_gender);
        tAsthma=v.findViewById(R.id.chk_asthma);
        tDiabetes=v.findViewById(R.id.chk_diabetes);
        profilePic=v.findViewById(R.id.imgProfilePic);
        tHypertension=v.findViewById(R.id.chk_hypertension);
        btn_edit=v.findViewById(R.id.btn_edit);
        btn_accept=v.findViewById(R.id.btn_accept);
        btn_cancel=v.findViewById(R.id.btn_cancel);
        btn_calendar=v.findViewById(R.id.btn_calendar);
        //objeto que se usa para almacenar datos
        temp= new patient();
        //metodo para desabilitar los textos
        setAllDisabled();
        //se obtiene el usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //si el usuario no es nulo se ejecuta el codigo
        if (user!=null){
            //obtenemos el uid del usuario actual  para despues tener todos los datos buscando solo ese id
            uid=user.getUid();
            //path de la base de datos que usaremos
            Database= FirebaseDatabase.getInstance();

            // en este caso solo nos interesa el usuario actual, En User/uid, donde uid es el usuario actual
            ref = Database.getReference().child("Users").child(uid);
            //evento para leer los datos
            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

                        if ( p.getBirthdate()!=null && p.getWeight()!=null && p.getHeight()!=null
                                && p.getGender()!=null &&p.getAsma()!=null && p.getDiabetes()!=null && p.getHipertension()!=null
                                ){
                            tBirthdate.setText(p.getBirthdate());
                            tWeight.setText(p.getWeight());
                            tHeight.setText(p.getHeight());
                            if (p.getGender().equals("Hombre")) {
                                tGender.setSelection(0);
                            } else {tGender.setSelection(1);}
                            tAsthma.setChecked(Boolean.parseBoolean(p.getAsma()));
                            tDiabetes.setChecked(Boolean.parseBoolean(p.getDiabetes()));
                            tHypertension.setChecked(Boolean.parseBoolean(p.getHipertension()));
                        }
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
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTemp();
                enableEdit();
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
                        (TextUtils.isEmpty(tGender.getSelectedItem().toString()))){

                    Toast.makeText(getContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                }
                //si no estan vacios los campos, se creara un paciente, se le asignaran los datos y se enviara a la base de datos
                else {
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
                    //se envia a la base de datos el paciente generado para que se actualicen los datos
                    ref.setValue(u);
                    //se crea un mensage que dice que se guardo
                    Toast.makeText(getContext(), "Guardado", Toast.LENGTH_SHORT).show();
                    disableEdit();
                }

            }
        });
        //evento del boton cancel
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si se cancela se regresan los valores y se pone invisibles los botones
                disableEdit();
                getTemp();

            }
        });

        //evento del boton calendar
        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se crea un nuevo fragmento de tipo Datepicker
                DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                    //se sobrecarga su metodo de onDateSet
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //la fecha seleccionada se asigna a la variable selectedDate
                        String selectedDate=dayOfMonth+"/"+(month+1)+"/"+year;
                        //Al TextView birthdate se le asigna el valor de la fecha seleccionada
                        tBirthdate.setText(selectedDate);
                    }
                });
                //se muestra el fragment
                newFragment.show(getParentFragmentManager(),"datePicker");
            }
        });

        return v;
    }


    //metodo para que caambie la vista dependiendo si es doctor o no
    private void DoctorView(){
    }
    //metodo para que cambie la vista dependiendo si es paciente o no
    private void PatientView(){
    }


    private void enableEdit(){
        btn_edit.setEnabled(false);
        setAllEnabled();
        btn_accept.setVisibility(View.VISIBLE);
        btn_calendar.setVisibility(View.VISIBLE);
        btn_cancel.setVisibility(View.VISIBLE);

    }
    private void disableEdit(){
        btn_edit.setEnabled(true);
        setAllDisabled();
        btn_accept.setVisibility(View.INVISIBLE);
        btn_calendar.setVisibility(View.INVISIBLE);
        btn_cancel.setVisibility(View.INVISIBLE);
    }
    //evento para habilitar la interfaz
    private void setAllEnabled(){
        tName.setEnabled(true);
        tLast1.setEnabled(true);
        tLast2.setEnabled(true);
        //tBirthdate.setEnabled(true);
        tWeight.setEnabled(true);
        tHeight.setEnabled(true);
        tGender.setEnabled(true);
        tAsthma.setEnabled(true);
        tDiabetes.setEnabled(true);
        tHypertension.setEnabled(true);
        //op=true;
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
        //op=false;
    }
    //metodo almacena los valores de los textos para al cancelar regresarlos a como estaban
    private void setTemp(){
       // temp = new patient();
        temp.setFirstName(tName.getText().toString());
        temp.setLastName(tLast1.getText().toString());
        temp.setMiddleName(tLast2.getText().toString());
        temp.setBirthdate(tBirthdate.getText().toString());
        temp.setWeight(tWeight.getText().toString());
        temp.setHeight(tHeight.getText().toString());
        temp.setGender(tGender.getSelectedItem().toString());
        temp.setAsma(String.valueOf(tAsthma.isChecked()));
        temp.setDiabetes(String.valueOf(tDiabetes.isChecked()));
        temp.setHipertension(String.valueOf(tHypertension.isChecked()));
    }

    private void getTemp(){
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

    }

}