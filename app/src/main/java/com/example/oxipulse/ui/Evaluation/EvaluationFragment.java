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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EvaluationFragment extends Fragment implements View.OnFocusChangeListener/* implements Callback<EvalResponse>*/ {

    //declaracion de variables
    //private EvaluationViewModel evaluationViewModel;
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
        //evaluationViewModel = new ViewModelProvider(this).get(EvaluationViewModel.class);
        //se infla la vista del fragment
        View root = inflater.inflate(R.layout.fragment_evaluation, container, false);
        et_oxigenSat=root.findViewById(R.id.text_input_oxigen);
        et_heartRate=root.findViewById(R.id.text_input_heartrate);
        btn_eval= root.findViewById(R.id.button_evaluation);
        et_csv=root.findViewById(R.id.text_input_csv);
        (root.findViewById(R.id.text_input_heartrate)).setOnFocusChangeListener(this);
        (root.findViewById(R.id.text_input_oxigen)).setOnFocusChangeListener(this);
        user= FirebaseAuth.getInstance().getCurrentUser();


        //final ImageView triageColor = (ImageView)
        final View triagealert =inflater.inflate(R.layout.eval_dialog_layout,null);
         ImageView triageColor = triagealert.findViewById(R.id.img_triage_color);
        TextView tMensajeTriage = triagealert.findViewById(R.id.tv_mensaje);

        //firebase logic
        Database= FirebaseDatabase.getInstance();

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
                btn_eval.setEnabled(false);
                et_heartRate.clearFocus();
                et_oxigenSat.clearFocus();
                oxi=et_oxigenSat.getText().toString();
                sat=et_heartRate.getText().toString();
                Call<EvalResponse> responseCall = ApiAdapter.getApiService().getEval(oxi,sat);
                responseCall.enqueue(new Callback<EvalResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<EvalResponse> call, @NotNull Response<EvalResponse> response) {
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
                    public void onFailure(@NotNull Call<EvalResponse> call, @NotNull Throwable t) {
                        Log.e("Error",t.getMessage());
                    }
                });
            }
        });
        return root;
    }

    private void save_eval(Response<EvalResponse> response) {
        date= java.text.DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,Locale.getDefault()).format(Calendar.getInstance().getTime());
       // String dates = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault()).format(date);

        ref=Database.getReference("Records").push();
        ref2=Database.getReference("User-Records");
        uid=user.getUid();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("date",date);
        hashMap.put("tag",response.body().getData().get(0).getCodigo());
        hashMap.put("hr",sat);
        hashMap.put("oxi",oxi);
        hashMap.put("degree_of_urgency",String.valueOf(response.body().getData().get(0).getGradoDeUrgencia()));
        //hashMap.put("id",uid);

        String key = ref.getKey();

        ref.setValue(hashMap);
        ref2.child(uid).child(key).setValue("true").addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(getContext(), "Registro Guardado", Toast.LENGTH_SHORT).show();
            }
            else {
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