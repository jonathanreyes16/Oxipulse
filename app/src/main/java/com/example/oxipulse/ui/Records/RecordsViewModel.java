package com.example.oxipulse.ui.Records;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecordsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public RecordsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}

    //private MutableLiveData<String> mText;
    ////referencia a la base de datos, Users(usuarios)
    //private static final DatabaseReference PROFILE_REF =
    //        FirebaseDatabase.getInstance().getReference("/Users");
    ////declaracion de variables
    //private String First_Name, id, imageUrl,LastName,isDoc,MiddleName;
    //private String birthdate,weight,sex,diabetes,asma,hipertension ;
//
    ////se declara la variable liveData, es un query hacia la base de datos, Users
    //private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(PROFILE_REF);
//
    ////get que retorna el livedata, esta seria el resultado del query
    //@NonNull
    //public LiveData<DataSnapshot> getDataSnapshotLiveData() {
    //    return liveData;
    //}

