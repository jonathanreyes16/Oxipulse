package com.example.oxipulse.ui.Evaluation;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.oxipulse.R;
import com.example.oxipulse.StartActivity;
import com.example.oxipulse.api.ApiAdapter;
import com.example.oxipulse.api.ApiService;
import com.example.oxipulse.api.ServiceGenerator;
import com.example.oxipulse.model.EvalResponse;
import com.example.oxipulse.model.patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opencsv.CSVReader;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class EvaluationFragment extends Fragment implements View.OnFocusChangeListener/* implements Callback<EvalResponse>*/ {


    private static final int REQUEST_CODE_FILE = 2;
    private static final int ACTIVITY_CHOOSE_FILE1 = 1;
    //declaracion de variables
    //private EvaluationViewModel evaluationViewModel;
    TextInputEditText et_oxigenSat,et_heartRate;
    Button btn_eval,btn_csv;
    String oxi,sat;
    String uid,date;
    String isD;
    FirebaseUser user;
    FirebaseDatabase Database;
    DatabaseReference refdoc, ref,ref2;
    private static final int PICK_PDF_FILE = 2;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //inflar widgets
        //evaluationViewModel = new ViewModelProvider(this).get(EvaluationViewModel.class);
        //se infla la vista del fragment
        View root = inflater.inflate(R.layout.fragment_evaluation, container, false);
        et_oxigenSat=root.findViewById(R.id.text_input_oxigen);
        et_heartRate=root.findViewById(R.id.text_input_heartrate);
        btn_eval= root.findViewById(R.id.button_evaluation);

        btn_csv =root.findViewById(R.id.btn_input_csv);

        (root.findViewById(R.id.text_input_heartrate)).setOnFocusChangeListener(this);
        (root.findViewById(R.id.text_input_oxigen)).setOnFocusChangeListener(this);
        user= FirebaseAuth.getInstance().getCurrentUser();


        //final ImageView triageColor = (ImageView)
        final View triagealert =inflater.inflate(R.layout.eval_dialog_layout,null);
        final ImageView triageColor = triagealert.findViewById(R.id.img_triage_color);
        final TextView tMensajeTriage = triagealert.findViewById(R.id.tv_mensaje);

        //firebase logic
        Database= FirebaseDatabase.getInstance();
        uid=user.getUid();


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
        refdoc = Database.getReference("Users").child(uid);
        refdoc.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()){
                    Log.e("Error", Objects.requireNonNull(task.getException()).toString());
                }
                else {
                    //patient p = Objects.requireNonNull(task.getResult()).getValue();
                    isD= task.getResult().getValue(patient.class).getIsDoc();
                    if (isD.equals("true")){
                        btn_csv.setVisibility(View.VISIBLE);
                        Log.d ("D",  task.getResult().toString());
                    }

                }
            }
        });

        btn_csv.setOnClickListener(v -> {
            openFile();
        });






        return root;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FILE && resultCode == RESULT_OK) {

            //Uri uri = data.getData();

            //parseCsvFile(uri);
            //File f = new File(data.getData().getPath());

            File file = new File(data.getData().getPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("text/csv"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("File","file",requestFile);

            Call<EvalResponse> evalResponseCall = ApiAdapter.getApiService().postEvalCsv(body);

          evalResponseCall.enqueue(new Callback<EvalResponse>() {
              @Override
              public void onResponse(Call<EvalResponse> call, Response<EvalResponse> response) {
                  //si la respuesta se obtiene
                  if (response.isSuccessful()) {
                      //switch en caso de cada respuesta, cambia el color del cuadro y el texto del triage
                      switch (response.body().getData().get(0).getCodigo()) {
                          case "Verde":
                              triageColor.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.etverde, null));
                              tMensajeTriage.setText(R.string.eval_res_green);
                              break;
                          case "amarillo":
                              triageColor.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.etamar, null));
                              tMensajeTriage.setText(R.string.eval_res_yellow);
                              break;
                          case "naranja":
                              triageColor.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.etnaranja, null));
                              tMensajeTriage.setText(R.string.eval_res_orange);
                              break;
                          case "rojo":
                              triageColor.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.etrojo, null));
                              tMensajeTriage.setText(R.string.eval_res_red);
                              break;
                      }
                      d.show();
                      //Guardar resultado en base de datos
                      save_eval(response);
                  }
              }

           @Override
           public void onFailure(Call<EvalResponse> call, Throwable t) {
               Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
           }
            });

          Toast.makeText(getContext(), "File Selected: " + uri.getPath().toString(), Toast.LENGTH_SHORT).show();

        }

    }

    private void uploadFile(){

        //ApiService service = ApiAdapter.getApiService().postEvalCsv();
    }

    private void openFile(){
         Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
         //intent.addCategory(Intent.CATEGORY_OPENABLE);
         intent.setType("text/*");
         startActivityForResult(Intent.createChooser(intent,"Abrir CSV"),REQUEST_CODE_FILE);
         //startActivityForResult(intent,REQUEST_CODE_FILE);


    }


    private void parseCsvFile(@NonNull Uri uri) {

        try {

            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            CSVReader csvReader = new CSVReader(bufferedReader);

            String[] nextLine;

            while ((nextLine = csvReader.readNext()) != null) {

                String amount = nextLine[0];
                String name = nextLine[1];

                Toast.makeText(getContext(), amount + " " + name, Toast.LENGTH_SHORT).show();

            }


        } catch (IOException exception) {

            Log.d(TAG, "Parse CSV File -> " + exception.getMessage());
        }

    }


    private void save_eval(Response<EvalResponse> response) {
        date= java.text.DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,Locale.getDefault()).format(Calendar.getInstance().getTime());
       // String dates = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault()).format(date);

        ref=Database.getReference("Records").push();
        ref2=Database.getReference("User-Records");



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