package com.example.oxipulse.ui.Profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//Este ViewModel sirve para que almacene los valores durante el ciclo de vida de el fragment,
//separando la parte logica de la visual
public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    //referencia a la base de datos, Users(usuarios)
    private static final DatabaseReference MY_PROFILE_REF =
            FirebaseDatabase.getInstance().getReference("/Users");
    //declaracion de variables
    private String First_Name, id, imageUrl,LastName,isDoc,MiddleName;
    private String birthdate,weight,sex,diabetes,asma,hipertension ;



    //get que retorna el livedata, esta seria el resultado del query



    public ProfileViewModel() {


    }

    //public LiveData<String> getText() {
    //    return mText;
    //}
}