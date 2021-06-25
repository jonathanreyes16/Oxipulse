package com.example.oxipulse.ui.Evaluation;



import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.oxipulse.R;
import com.example.oxipulse.api.ApiAdapter;
import com.example.oxipulse.model.EvalResponse;
import com.example.oxipulse.model.SpinnerPatientAdapter;
import com.example.oxipulse.model.patient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class EvaluationFragment extends Fragment implements View.OnFocusChangeListener/* implements Callback<EvalResponse>*/ {


    private static final int REQUEST_CODE_FILE = 2;
    private static final int ACTIVITY_CHOOSE_FILE1 = 1;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 12;
    private static final String TEMP_FILE = "temp";
    //declaracion de variables
    //private EvaluationViewModel evaluationViewModel;
    TextInputEditText et_oxigenSat,et_heartRate;
    TextInputLayout etl_oxigen,etl_heart;
    Button btn_eval,btn_csv;
    String oxi,sat,uid,date,isD;

    TextView tv,lbl_selectPatient,tMensajeTriage,lbl_hint_oxi,lbl_hint_rate;
    Uri uri;
    FirebaseUser user;
    FirebaseDatabase Database;
    DatabaseReference refdoc, ref,ref2;
    Spinner namesSpinner;
    private static final int PICK_PDF_FILE = 2;
    private static Uri contentUri = null;
    View triagealert;
    ImageView triageColor;
    Dialog d;
    SpinnerPatientAdapter spinnerPatientAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //inflar widgets
        //evaluationViewModel = new ViewModelProvider(this).get(EvaluationViewModel.class);
        //se infla la vista del fragment
        View root = inflater.inflate(R.layout.fragment_evaluation, container, false);

        lbl_selectPatient=root.findViewById(R.id.tv_Select_Patient);
        namesSpinner =root.findViewById(R.id.spinnerSelectPatient);

        et_oxigenSat=root.findViewById(R.id.text_input_oxigen);
        et_heartRate=root.findViewById(R.id.text_input_heartrate);
        etl_heart=root.findViewById(R.id.text_input_layout_heartrate);
        etl_oxigen=root.findViewById(R.id.text_input_layout_oxigen);
        lbl_hint_oxi=root.findViewById(R.id.lbl_hint_heartrate);
        lbl_hint_rate=root.findViewById(R.id.lbl_hint_oxigen);

        tv = root.findViewById(R.id.textView2);

        btn_csv =root.findViewById(R.id.btn_input_csv);
        btn_eval= root.findViewById(R.id.button_evaluation);

        (root.findViewById(R.id.text_input_heartrate)).setOnFocusChangeListener(this);
        (root.findViewById(R.id.text_input_oxigen)).setOnFocusChangeListener(this);



        //final ImageView triageColor = (ImageView)
        triagealert =inflater.inflate(R.layout.eval_dialog_layout,null);
        triageColor = triagealert.findViewById(R.id.img_triage_color);
        tMensajeTriage = triagealert.findViewById(R.id.tv_mensaje);

        //firebase logic
        user= FirebaseAuth.getInstance().getCurrentUser();
        Database= FirebaseDatabase.getInstance();
        uid=user.getUid();

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        //se checa si es doctor y si es doctor se muestra el boton de subir csv
        refdoc = Database.getReference("Users").child(uid);
        refdoc.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                Log.e("Error", Objects.requireNonNull(task.getException()).toString());
            }
            else {
                isD= task.getResult().getValue(patient.class).getIsDoc();
                if (isD.equals("true")){
                    btn_csv.setVisibility(View.VISIBLE);
                    fillPatientSpinners();
                    lbl_selectPatient.setVisibility(View.VISIBLE);
                    Log.d ("D",  task.getResult().toString());
                }
                else {
                    oxirateVisible();
                    btn_eval.setVisibility(View.VISIBLE);
                }

            }
        });
        //se crea un alertbuilder, que se encarga de hacer el alertDialog, al cual le daremos parametros
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder
                .setView(triagealert)
                .setTitle("Triage")
                .setCancelable(false)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    //Evento despues de dar ok
                    et_oxigenSat.requestFocus();
                    et_heartRate.setText("");
                    et_oxigenSat.setText("");


                });
        d = alertDialogBuilder.create();

        //Se crea un activityResultLauncher que toma un intent y espera su resultado.
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //si el codigo de respuesta del resultado es RESULT_OK
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        btn_eval.setEnabled(true);
                        //se le asigna al uri el uri del archivo seleccionado
                        uri = result.getData().getData();
                        
                        //se llena el spinner con los datos de los pacientes
                        //se ocultan los campos de oxi y saturacion y se muestra el spinner para
                        //seleccionar el usuario al que se le asignara la lectura
                        CSVVisibilityAfterselectON();

                        }

                });



        btn_eval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //el uri se asigna al completar el intent y que el resultado sea RESULT_OK,
                // entonces si no es nulo significa que se escojio un archivo CSV
                if (uri != null) {
                    //se sube el archivo con la funcion UploadFile
                    uploadFile(uri);

                    CSVVisibilityAfterselectOFF();
                } else {

                    //si es nulo se introdujo un valor a los campos de oxi y sat
                    DirectEval();
                }
            }

        });


        btn_csv.setOnClickListener(v -> {
            //al dar clic se crea un intent.ACTION_GET_CONTENT que
            //abre un explorador de archivos para seleccionar el archivo a subir
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/*");
            //se envia a un activity result launcher donde espera la respuesta
            someActivityResultLauncher.launch(intent);

        });
        return root;
    }

    private void oxirateVisible() {
        lbl_hint_rate.setVisibility(View.VISIBLE);
        lbl_hint_oxi.setVisibility(View.VISIBLE);
        etl_heart.setVisibility(View.VISIBLE);
        etl_oxigen.setVisibility(View.VISIBLE);
        et_oxigenSat.setVisibility(View.VISIBLE);
        et_heartRate.setVisibility(View.VISIBLE);
    }


    private void DirectEval() {

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
                   showDialogOnResponse(response);
                    //Guardar resultado en base de datos
                    save_eval(response);

                }
            }
            //si la respuesta es incorrecta
            @Override
            public void onFailure(@NotNull Call<EvalResponse> call, @NotNull Throwable t) {
                Log.e("Error",t.getMessage());
                btn_eval.setEnabled(true);
            }
        });
    }
    private void uploadFile(Uri file) {

        File f = getFile(getContext(),file);

        RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"),new File(f.getPath()));
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", f.getName(),requestFile);//MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(), requestFile);//MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(),requestFile);//MultipartBody.Part.createFormData();


        Call<EvalResponse> evalResponseCall = ApiAdapter.getApiService().postEvalCsv(body);

        evalResponseCall.enqueue(new Callback<EvalResponse>() {
            @Override
            public void onResponse(Call<EvalResponse> call, Response<EvalResponse> response) {
                if (response.isSuccessful()){
                    //switch en caso de cada respuesta, cambia el color del cuadro y el texto del triage
                    showDialogOnResponse(response);
                    //Guardar resultado en base de datos
                   SaveEvalCsv(response,uid);
                }
            }

            @Override
            public void onFailure(Call<EvalResponse> call, Throwable t) {
                Log.e("Error",t.getMessage());
            }
        });

    }
    private void showDialogOnResponse(Response<EvalResponse> response){
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
        btn_eval.setEnabled(true);
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

    private void fillPatientSpinners() {
        namesSpinner.setVisibility(View.VISIBLE);

        ref=Database.getReference("Users");
        ref.orderByChild("isDoc").equalTo("false").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                final List<patient> patientsList = new ArrayList<patient>();
                for (DataSnapshot patients: snapshot.getChildren()) {
                    patient p = patients.getValue(patient.class);
                    patientsList.add(p);
                }
                spinnerPatientAdapter= new SpinnerPatientAdapter(getContext(),R.layout.textview, patientsList);
                namesSpinner.setAdapter(spinnerPatientAdapter);
                namesSpinner.setSelection(spinnerPatientAdapter.NO_SELECTION,false);
                namesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        patient p = spinnerPatientAdapter.getItem(position);
                        uid=p.getId();
                        oxirateVisible();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

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




    private void CSVVisibilityAfterselectON() {
        et_heartRate.setVisibility(View.INVISIBLE);
        et_oxigenSat.setVisibility(View.INVISIBLE);
        etl_oxigen.setVisibility(View.INVISIBLE);
        etl_heart.setVisibility(View.INVISIBLE);
        lbl_selectPatient.setVisibility(View.VISIBLE);
        namesSpinner.setVisibility(View.VISIBLE);
    }
    private void CSVVisibilityAfterselectOFF() {
        et_heartRate.setVisibility(View.VISIBLE);
        et_oxigenSat.setVisibility(View.VISIBLE);
        etl_oxigen.setVisibility(View.VISIBLE);
        etl_heart.setVisibility(View.VISIBLE);
        lbl_selectPatient.setVisibility(View.INVISIBLE);
        namesSpinner.setVisibility(View.VISIBLE);
    }


    public <T> List<T> getList(String jsonArray, Class<T> EvalResponse) {
        Type typeOfT = TypeToken.getParameterized(List.class, EvalResponse).getType();
        return new Gson().fromJson(jsonArray, typeOfT);

    }
    public static File getFile(Context context, Uri uri) {
        if (uri != null) {
            String path = getPath(context, uri);
            if (path != null && isLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }

    public static boolean isLocal(String url) {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            return true;
        }
        return false;
    }

    public static String getPath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                  // final String id = DocumentsContract.getDocumentId(uri);
                  // final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                  // return getDataColumn(context, contentUri, null, null);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        final String id;
                        Cursor cursor = null;
                        try {
                            cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                String fileName = cursor.getString(0);
                                String path = Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                                if (!TextUtils.isEmpty(path)) {
                                    return path;
                                }
                            }
                        }
                        finally {
                            if (cursor != null)
                                cursor.close();
                        }
                        id = DocumentsContract.getDocumentId(uri);
                        if (!TextUtils.isEmpty(id)) {
                            if (id.startsWith("raw:")) {
                                return id.replaceFirst("raw:", "");
                            }
                            String[] contentUriPrefixesToTry = new String[]{
                                    "content://downloads/public_downloads",
                                    "content://downloads/my_downloads"
                            };
                            for (String contentUriPrefix : contentUriPrefixesToTry) {
                                try {
                                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));


                                    return getDataColumn(context, contentUri, null, null);
                                } catch (NumberFormatException e) {
                                    //In Android 8 and Android P the id is not a number
                                    return uri.getPath().replaceFirst("^/document/raw:", "").replaceFirst("^raw:", "");
                                }
                            }


                        }
                    }
                    else {
                        final String id = DocumentsContract.getDocumentId(uri);

                        if (id.startsWith("raw:")) {
                            return id.replaceFirst("raw:", "");
                        }
                        try {
                            contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        }
                        catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (contentUri != null) {

                            return getDataColumn(context, contentUri, null, null);
                        }
                    }
                }

                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    private static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



}