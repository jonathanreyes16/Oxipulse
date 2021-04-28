package com.example.oxipulse.ui.Profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    FirebaseAuth Auth;
    private String First_Name, id, imageUrl,LastName,isDoc,MiddleName;



    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        Auth= FirebaseAuth.getInstance();

    }

    public LiveData<String> getText() {
        return mText;
    }
}