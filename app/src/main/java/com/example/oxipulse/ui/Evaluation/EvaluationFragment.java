package com.example.oxipulse.ui.Evaluation;

import android.app.Activity;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.documentfile.provider.DocumentFile;
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
import okio.BufferedSink;
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
    TextInputLayout etl_oxigen,etl_heart;
    Button btn_eval,btn_csv;
    String oxi,sat;
    String uid,date;
    String isD;
    ActivityResult r;
    File fileToUpload;
    TextView tv;
    Uri uri;
    FirebaseUser user;
    FirebaseDatabase Database;
    DatabaseReference refdoc, ref,ref2;
    Spinner spinner ;
    private static final int PICK_PDF_FILE = 2;
    View triagealert;
    ImageView triageColor;
    TextView tMensajeTriage;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //inflar widgets
        //evaluationViewModel = new ViewModelProvider(this).get(EvaluationViewModel.class);
        //se infla la vista del fragment
        View root = inflater.inflate(R.layout.fragment_evaluation, container, false);
        et_oxigenSat=root.findViewById(R.id.text_input_oxigen);
        et_heartRate=root.findViewById(R.id.text_input_heartrate);
        btn_eval= root.findViewById(R.id.button_evaluation);
        etl_heart=root.findViewById(R.id.text_input_layout_heartrate);
        etl_oxigen=root.findViewById(R.id.text_input_layout_oxigen);
        tv = root.findViewById(R.id.textView2);
        btn_csv =root.findViewById(R.id.btn_input_csv);
        spinner=root.findViewById(R.id.spinnerSelectPatient);
        (root.findViewById(R.id.text_input_heartrate)).setOnFocusChangeListener(this);
        (root.findViewById(R.id.text_input_oxigen)).setOnFocusChangeListener(this);
        user= FirebaseAuth.getInstance().getCurrentUser();


        //final ImageView triageColor = (ImageView)
        triagealert =inflater.inflate(R.layout.eval_dialog_layout,null);
        triageColor = triagealert.findViewById(R.id.img_triage_color);
        tMensajeTriage = triagealert.findViewById(R.id.tv_mensaje);

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

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK){
                        uri = result.getData().getData();

                        //fileToUpload = getFile(result);
                        //File f = new File(result.getData().getData().getPath());
                        File f = new File(uri.getPath());
                        r=result;
                        fileToUpload=f;
                        tv.setText(fileToUpload.getName());
                        tv.setVisibility(View.VISIBLE);
                        et_heartRate.setVisibility(View.INVISIBLE);
                        et_oxigenSat.setVisibility(View.INVISIBLE);
                        etl_oxigen.setVisibility(View.INVISIBLE);
                        etl_heart.setVisibility(View.INVISIBLE);
                        //uploadFile(result);
                    }
                }
        );


        btn_eval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fileToUpload!=null){
                    uploadFile(uri,d,r);
                }
                else {
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
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/*");
            someActivityResultLauncher.launch(intent);

        });
        return root;
    }

    private File getFile(ActivityResult result){

        Intent da = result.getData();
        //DocumentFile f = DocumentFile.fromSingleUri(getContext(),da.getData());  working
        File file = new File(da.getData().getPath());
        //File file = new File(f.getName());
        //Log.d("ERROR",f.getName());
        return file;
    }
    private RequestBody stripLength(RequestBody delegate) {
        return new RequestBody() {
            @Override public @Nullable MediaType contentType() {
                return delegate.contentType();
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                delegate.writeTo(sink);
            }
        };
    }
    private void uploadFile(Uri file,Dialog d, ActivityResult result){

        //Intent da = result.getData();
        DocumentFile f = DocumentFile.fromSingleUri(getContext(),file);
        //oast.makeText(getContext(), file.getScheme().toString(), Toast.LENGTH_SHORT).show();
        //File file4 = new File(f.getName());
        //File file5 = new File(getReal);
        //File file = new File(f.getName());
        //Log.d("ERROR",f.getName());
        //ApiService service = ApiAdapter.getApiService().
        RequestBody requestFile = RequestBody.create(MediaType.parse("text/csv"),f.getName());
        //RequestBody requestFile = RequestBody.create(MediaType.parse("text/csv"),f.getName());
        //RequestBody.create(MediaType.parse("application/octet-stream"),file.getName());
       MultipartBody body = new MultipartBody.Builder("12345")
               .addPart(stripLength(requestFile))
               .build();
       // MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(),body);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(),stripLength(requestFile));
        //MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(),requestFile);
        //MultipartBody.Part.createFormData();


       // Toast.makeText(getContext(), f.getName() +"  2"+f.getName(), Toast.LENGTH_SHORT).show();

        Call<EvalResponse> evalResponseCall = ApiAdapter.getApiService().postEvalCsv(fileToUpload);
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
                    Log.d("ERROR", "Response"+ response.toString());

                    //Guardar resultado en base de datos
                   // SaveEvalCsv(response,);
                    d.show();
                    save_eval(response);
                }
            }

            @Override
            public void onFailure(Call<EvalResponse> call, Throwable t) {
                Log.e("ERROR",t.getMessage(),t);
                Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

       // Toast.makeText(getContext(), "File Selected: " + uri.getPath().toString(), Toast.LENGTH_SHORT).show();

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
    private void SaveEvalCsv(Response<EvalResponse> response,String UID){
        date= java.text.DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,Locale.getDefault()).format(Calendar.getInstance().getTime());
        // String dates = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault()).format(date);
        String d = UID;
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
        ref2.child(d).child(key).setValue("true").addOnCompleteListener(task -> {
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