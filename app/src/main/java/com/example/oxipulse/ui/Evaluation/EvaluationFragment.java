package com.example.oxipulse.ui.Evaluation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.oxipulse.R;
import com.google.android.material.textfield.TextInputEditText;

public class EvaluationFragment extends Fragment {

    private EvaluationViewModel evaluationViewModel;
    //declaracion de variables
    TextInputEditText et_oxigenSat,et_heartRate,et_csv;
    Button btn_eval;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        evaluationViewModel =
                new ViewModelProvider(this).get(EvaluationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_evaluation, container, false);
        

        return root;
    }
}