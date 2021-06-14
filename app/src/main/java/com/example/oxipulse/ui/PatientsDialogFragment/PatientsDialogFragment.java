package com.example.oxipulse.ui.PatientsDialogFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oxipulse.R;


public class PatientsDialogFragment extends Fragment {


    public PatientsDialogFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_patients_dialog, container, false);




        // Inflate the layout for this fragment
        return v ;
    }
}