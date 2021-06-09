package com.example.oxipulse.ui.Evaluation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EvaluationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EvaluationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}