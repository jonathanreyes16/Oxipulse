package com.example.oxipulse.ui.Evaluation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.oxipulse.R;
import com.example.oxipulse.api.ApiAdapter;
import com.example.oxipulse.model.EvalResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluationFragment extends Fragment implements View.OnFocusChangeListener/* implements Callback<EvalResponse>*/ {

    //declaracion de variables
    private EvaluationViewModel evaluationViewModel;
    TextInputEditText et_oxigenSat,et_heartRate,et_csv;
    Button btn_eval;
    String oxi,sat;
    String uid,date;
    FirebaseUser user;
    FirebaseDatabase Database;
    DatabaseReference ref,ref2;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //inflar widgets
        evaluationViewModel =
                new ViewModelProvider(this).get(EvaluationViewModel.class);
        //se infla la vista del fragment
        View root = inflater.inflate(R.layout.fragment_evaluation, container, false);
        et_oxigenSat=root.findViewById(R.id.text_input_oxigen);
        et_heartRate=root.findViewById(R.id.text_input_heartrate);
        btn_eval= root.findViewById(R.id.button_evaluation);
        et_csv=root.findViewById(R.id.text_input_csv);
        ((EditText)root.findViewById(R.id.text_input_heartrate)).setOnFocusChangeListener(this);
        ((EditText)root.findViewById(R.id.text_input_oxigen)).setOnFocusChangeListener(this);
        user= FirebaseAuth.getInstance().getCurrentUser();

        //final ImageView triageColor = (ImageView)
        final View triagealert =inflater.inflate(R.layout.eval_dialog_layout,null);
         ImageView triageColor = (ImageView) triagealert.findViewById(R.id.img_triage_color);
        TextView tMensajeTriage = (TextView) triagealert.findViewById(R.id.tv_mensaje);

        //firebase logic
        uid=user.getUid();
        Database= FirebaseDatabase.getInstance();
        ref=Database.getReference("Records");
        ref2=Database.getReference("User-Records");

        //se crea un alertbuilder, que se encarga de hacer el alertDialog, al cual le daremos parametros
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder
                .setView(triagealert)
                .setTitle("Triage")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Evento despues de dar ok
                        et_oxigenSat.requestFocus();
                        et_heartRate.setText("");
                        et_oxigenSat.setText("");

                    }
                });
        Dialog d = alertDialogBuilder.create();

        btn_eval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_heartRate.clearFocus();
                et_oxigenSat.clearFocus();
                oxi=et_oxigenSat.getText().toString();
                sat=et_heartRate.getText().toString();
                Call<EvalResponse> responseCall = ApiAdapter.getApiService().getEval(oxi,sat);
                responseCall.enqueue(new Callback<EvalResponse>() {
                    @Override
                    public void onResponse(Call<EvalResponse> call, Response<EvalResponse> response) {
                        //si la respuesta se obtiene
                        if (response.isSuccessful()){
                            //switch en caso de cada respuesta, cambia el color del cuadro y el texto del triage
                            switch (response.body().getData().get(0).getCodigo()){
                                case "Verde":
                                    triageColor.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.etverde,null));
                                    tMensajeTriage.setText(R.string.eval_res_green);
                                    break;
                                case "amarillo":
                                    triageColor.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.etamar,null));
                                    tMensajeTriage.setText(R.string.eval_res_yellow);
                                    break;
                                case "naranja":
                                    triageColor.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.etnaranja,null));
                                    tMensajeTriage.setText(R.string.eval_res_orange);
                                    break;
                                case "rojo":
                                    triageColor.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.etrojo,null));
                                    tMensajeTriage.setText(R.string.eval_res_red);
                                    break;
                            }
                            d.show();
                            //Guardar resultado en base de datos
                            save_eval(response);
                        }
                    }
                    //si la respuesta es incorrecta
                    @Override
                    public void onFailure(Call<EvalResponse> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                    }
                });
            }
        });
        return root;
    }

    private void save_eval(Response<EvalResponse> response) {
        date= java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("date",date);
        hashMap.put("tag",response.body().getData().get(0).getCodigo());
        hashMap.put("hr",sat);
        hashMap.put("oxi",oxi);
        hashMap.put("degree_of_urgency",String.valueOf(response.body().getData().get(0).getGradoDeUrgencia()));
        hashMap.put(uid,"true");
        ref.push().setValue(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(getContext(), "Registro Guardado", Toast.LENGTH_SHORT).show();
            }else {
                Log.e("error","Error saving evaluation",task.getException());
            }
        });

        //Todo guardar valores a user-records
         HashMap<String,String> hashMap2 = new HashMap<>();
         hashMap2.put(uid,"true");
         hashMap2.put(ref.getKey(),"true");
         //ref2.push().setValue(hashMap2);

         ref2.push().setValue(hashMap2).addOnCompleteListener(task -> {
             if (task.isSuccessful()) {
                 Toast.makeText(getContext(), "Registro Guardado", Toast.LENGTH_SHORT).show();
             } else {
                 Log.e("error", "Error saving record", task.getException());
             }
         });

    }



    @Override
    public void onFocusChange(View view, boolean b) {
        if (!b) {
            switch (view.getId()) {
                case R.id.text_input_heartrate:
                    if (!TextUtils.isEmpty(et_heartRate.getText())) {
                        if (!et_heartRate.getText().toString().contains(".")) {
                            et_heartRate.setText(et_heartRate.getText().append(".0"));
                        }
                    }
                    break;
                case R.id.text_input_oxigen:
                    if (!TextUtils.isEmpty(et_oxigenSat.getText())){
                        if (!et_oxigenSat.getText().toString().contains(".")){
                            et_oxigenSat.setText(et_oxigenSat.getText().append(".0"));
                        }
                    }
                    break;
            }
        }
    }
}