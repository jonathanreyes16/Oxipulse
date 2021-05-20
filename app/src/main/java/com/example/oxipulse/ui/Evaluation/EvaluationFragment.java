package com.example.oxipulse.ui.Evaluation;

import android.os.Bundle;
import android.util.Log;
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
import com.example.oxipulse.classes.ApiAdapter;
import com.example.oxipulse.classes.EvalResponse;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluationFragment extends Fragment implements Callback<EvalResponse> {

    private EvaluationViewModel evaluationViewModel;
    //declaracion de variables
    TextInputEditText et_oxigenSat,et_heartRate,et_csv;
    Button btn_eval;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        evaluationViewModel =
                new ViewModelProvider(this).get(EvaluationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_evaluation, container, false);
        et_oxigenSat=root.findViewById(R.id.text_input_oxigen);
        et_heartRate=root.findViewById(R.id.text_input_heartrate);
        btn_eval= root.findViewById(R.id.button_evaluation);

        btn_eval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<EvalResponse> responseCall=ApiAdapter.getApiService().getEval(et_oxigenSat.getText().toString(),et_heartRate.getText().toString());
                responseCall.enqueue(EvaluationFragment.this);

            }
        });


        return root;
    }

    @Override
    public void onResponse(Call<EvalResponse> call, Response<EvalResponse> response) {
        if (response.isSuccessful()){
            EvalResponse eval = response.body();
            Log.d("call","size of "+eval.toString());
        }

    }

    @Override
    public void onFailure(Call<EvalResponse> call, Throwable t) {


    }
}