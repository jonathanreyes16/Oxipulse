package com.example.oxipulse.ui.Profile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.oxipulse.R;
import com.example.oxipulse.model.patient;
import com.example.oxipulse.ui.DatePicker.DatePickerFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    // declaracion de variables
    ProfileViewModel profileViewModel;
    //declaracion de variables
    TextView tv_name,tv_last1,tv_last2,tv_birth,tv_height,tv_weight,tv_sex,tv_asthma,tv_diabetes,tv_hypertension;
    EditText tName,tLast1,tLast2,tBirthdate,tWeight,tHeight;
    Button btn_edit, btn_accept,btn_cancel,btn_calendar;
    Spinner tGender;
    Bitmap tempbit,bitmap;

    CircleImageView profilePic;
    ImageView profilePicIcon;

    CheckBox tAsthma,tDiabetes,tHypertension;
    String uid,isdoc,imageUrl;
    final int PICK_IMAGE=12;
    GridLayout gridLayout;
    FirebaseUser user;

    FirebaseDatabase Database;
    DatabaseReference ref;

    FirebaseStorage storage;

    StorageReference storageReference;
    StorageReference storageReferenceref;
    StorageReference httpsReference;

    patient temp;


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
        profilePicIcon=v.findViewById(R.id.imgUploadIcon);
        tHypertension=v.findViewById(R.id.chk_hypertension);
        btn_edit=v.findViewById(R.id.btn_edit);
        btn_accept=v.findViewById(R.id.btn_accept);
        btn_cancel=v.findViewById(R.id.btn_cancel);
        btn_calendar=v.findViewById(R.id.btn_calendar);
        gridLayout =v.findViewById(R.id.grid);

        tv_name=v.findViewById(R.id.tv_name);
        tv_last1=v.findViewById(R.id.tv_last_n1);
        tv_last2=v.findViewById(R.id.tv_last_n2);
        tv_height=v.findViewById(R.id.tv_height);
        tv_weight=v.findViewById(R.id.tv_weight);
        tv_birth=v.findViewById(R.id.tv_birthdate);
        tv_sex=v.findViewById(R.id.tv_sex);
        tv_asthma=v.findViewById(R.id.tv_asma);
        tv_diabetes=v.findViewById(R.id.tv_diabetes);
        tv_hypertension=v.findViewById(R.id.tv_hipertension);


        profilePic.setDrawingCacheEnabled(true);
        //metodo para desabilitar los textos
        setAllDisabled();
        //se obtiene el usuario actual
        user = FirebaseAuth.getInstance().getCurrentUser();
        //si el usuario no es nulo se ejecuta el codigo
        if (user!=null){
            //obtenemos el uid del usuario actual  para despues tener todos los datos buscando solo ese id
            uid=user.getUid();

            //path de la base de datos que usaremos
            Database= FirebaseDatabase.getInstance();
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            // en este caso solo nos interesa el usuario actual, En User/uid, donde uid es el usuario actual
            ref = Database.getReference().child("Users").child(uid);
            //obtenemos el uid del usuario actual  para despues tener todos los datos buscando solo ese id

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


                        //checamos que si el usuario es personal de salud o paciente
                        //modifica el UI para el personal de salud
                        if(Boolean.parseBoolean(p.getIsDoc()) ){
                            hideUIDoc();

                            tHeight.setText(user.getEmail());
                            tWeight.setText(R.string.health_personnel);
                            tHeight.setTypeface(null, Typeface.BOLD);
                            tWeight.setTypeface(null,Typeface.BOLD);


                        } else{
                            //vista para el paciente
                            PatientView();
                        }



                        //log para debug
                        Log.d("firebase",String.valueOf(Objects.requireNonNull(task.getResult()).getValue()));
                    }
                }
            });
        }
        return v;
    }


    //metodo para que cambie la vista dependiendo si es paciente o no
    private void PatientView(){
        getProfilePhoto();


        //evento del boton edit
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //objeto que se usa para almacenar datos
                temp= new patient();
                setTemp();
                enableEdit();
            }
        });

        profilePicIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePhoto();
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

                        String selectedDate=twoDigits(dayOfMonth)+"/"+twoDigits(month+1)+"/"+year;
                        //Al TextView birthdate se le asigna el valor de la fecha seleccionada
                        tBirthdate.setText(selectedDate);
                    }
                });
                //se muestra el fragment
                newFragment.show(getParentFragmentManager(),"datePicker");
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
                profilePic.setImageBitmap(bitmap);
                uploadProfilePhoto();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void selectProfilePhoto(){

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(pickIntent, PICK_IMAGE);

    }

    private void uploadProfilePhoto(){
        if (bitmap!=null){

            //String imgID=UUID.randomUUID().toString();

            storageReferenceref=storageReference.child("images/"+uid);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imgdata = baos.toByteArray();

            UploadTask uploadTask = storageReferenceref.putBytes(imgdata);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        Toast.makeText(getContext(), "Error al subir archivo", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Imagen Subida Correctamente", Toast.LENGTH_SHORT).show();
                    }
                    return storageReferenceref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        assert downloadUri != null;
                        imageUrl=downloadUri.toString();

                        ref.child("imageUrl").setValue(downloadUri.toString());
                    }
                }
            });
        }

    }

    private void getProfilePhoto() {

            File rootPath = new File(Environment.getExternalStorageDirectory(), "profilePic");
            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }

                File localFile1 = new File(rootPath, "profilePic");
                Bitmap bitmap1 = BitmapFactory.decodeFile(localFile1.getPath());
                profilePic.setImageBitmap(bitmap1);


            httpsReference = storage.getReferenceFromUrl(imageUrl);
            File localFile = new File(rootPath, "profilePic");

            httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
                    profilePic.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Log.e("firebase", "local item not created" + e.toString());
                }
            });




    }

    private void hideUIDoc() {
        tBirthdate.setVisibility(View.INVISIBLE);
        profilePic.setVisibility(View.INVISIBLE);

        tGender.setVisibility(View.INVISIBLE);
        tAsthma.setVisibility(View.INVISIBLE);
        tDiabetes.setVisibility(View.INVISIBLE);
        tHypertension.setVisibility(View.INVISIBLE);

        tv_height.setVisibility(View.INVISIBLE);
        tv_weight.setVisibility(View.INVISIBLE);
        tv_birth.setVisibility(View.INVISIBLE);
        tv_sex.setVisibility(View.INVISIBLE);
        tv_asthma.setVisibility(View.INVISIBLE);
        tv_diabetes.setVisibility(View.INVISIBLE);
        tv_hypertension.setVisibility(View.INVISIBLE);

        btn_edit.setVisibility(View.INVISIBLE);
    }


    private void enableEdit(){
        btn_edit.setEnabled(false);
        setAllEnabled();
        btn_accept.setVisibility(View.VISIBLE);
        btn_calendar.setVisibility(View.VISIBLE);
        profilePicIcon.setVisibility(View.VISIBLE);
        btn_cancel.setVisibility(View.VISIBLE);

    }
    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    private void disableEdit(){
        btn_edit.setEnabled(true);
        profilePicIcon.setVisibility(View.INVISIBLE);
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
        //temp.setImageUrl(imageUrl);
        //tempbit = profilePic.getDrawingCache();
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
        //profilePic.setImageBitmap(tempbit);
        //imageUrl=temp.getImageUrl();
        //profilePic.setImageResource(temp.getImageUrl());
        if (temp.getGender().equals("Hombre")){
            tGender.setSelection(0);
        }
        else {tGender.setSelection(1);}
        tAsthma.setChecked(Boolean.parseBoolean(temp.getAsma()));
        tDiabetes.setChecked(Boolean.parseBoolean(temp.getDiabetes()));
        tHypertension.setChecked(Boolean.parseBoolean(temp.getHipertension()));

    }

}