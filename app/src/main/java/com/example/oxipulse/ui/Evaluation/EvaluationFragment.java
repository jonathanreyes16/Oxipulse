package com.example.oxipulse.ui.Evaluation;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.example.oxipulse.R;
import com.example.oxipulse.StartActivity;
import com.example.oxipulse.api.ApiAdapter;
import com.example.oxipulse.api.ApiService;
import com.example.oxipulse.api.ServiceGenerator;
import com.example.oxipulse.model.EvalResponse;
import com.example.oxipulse.model.FileUtils;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
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
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 12;
    private static final String TEMP_FILE = "temp";
    //declaracion de variables
    //private EvaluationViewModel evaluationViewModel;
    TextInputEditText et_oxigenSat,et_heartRate;
    TextInputLayout etl_oxigen,etl_heart;
    Button btn_eval,btn_csv;
    String oxi,sat;
    String uid,date;
    String isD;
    ActivityResult r;
    File fileToUpload = null;
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
    Dialog d;


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
        d = alertDialogBuilder.create();

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        assert result.getData() != null;
                        uri = result.getData().getData();
                            //fileToUpload = getFile(result);
                            //File f = new File(result.getData().getData().getPath());
                            //File f = new File(uri.getPath());
                            DocumentFile documentFile = DocumentFile.fromSingleUri(getContext(),uri);
                           // File f = new File(documentFile.getUri().toString());
                             File f = new File(documentFile.getName());
                            r = result;
                            fileToUpload = f;
                            tv.setText(fileToUpload.getPath());
                            tv.setVisibility(View.VISIBLE);
                            et_heartRate.setVisibility(View.INVISIBLE);
                            et_oxigenSat.setVisibility(View.INVISIBLE);
                            etl_oxigen.setVisibility(View.INVISIBLE);
                            etl_heart.setVisibility(View.INVISIBLE);
                            ///uploadFile(result);
                        }

                });



        btn_eval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fileToUpload!=null){
                    uploadFile(uri);
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
    private void copyFileStream(File dest, Uri uri, Context context)
            throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }
  // private File getFile(ActivityResult result){

  //     Intent da = result.getData();
  //     //DocumentFile f = DocumentFile.fromSingleUri(getContext(),da.getData());  working
  //     File file = new File(da.getData().getPath());
  //     //File file = new File(f.getName());
  //     //Log.d("ERROR",f.getName());
  //     //File file12 = new File();
  //     return file;
  // }
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


    private void uploadFile(Uri file) {

        //uploadFile(File file)
        //   OkHttpClient client = new OkHttpClient();
        //   DocumentFile f = DocumentFile.fromSingleUri(getContext(),result.getData().getData());
        //   MediaType mediaType = MediaType.parse("multipart/form-data; boundary=---011000010111000001101001");
        //   RequestBody body = RequestBody.create(mediaType, "-----011000010111000001101001\r\nContent-Disposition:" +
        //           " form-data;" +
        //           " name=\"file\"; " +
        //           "filename="+f.getName()+"\r\nContent-Type: text/csv" +
        //           "\r\n\r\n\r\n-----011000010111000001101001--\r\n");
        //   Request request = new Request.Builder()
        //           .url("https://oxipulse.herokuapp.com/upload")
        //           .post(body)
        //           .addHeader("content-type", "multipart/form-data; boundary=---011000010111000001101001")
        //           .build();

        //   try {
        //       Gson gson = new Gson();
        //okhttp3.Response response =
        //          //EvalResponse er = new EvalResponse();
        //    Call<EvalResponse> evalResponseCall =client.newCall(request);
        //    ResponseBody responseBody = client.newCall(request).execute().body();
        //    EvalResponse evalResponse = gson.fromJson(responseBody.toString(),EvalResponse.class);

        // evalResponse.getData().g

        //  } catch (IOException e) {
        //      e.printStackTrace();
        //  }
        //Intent da = result.getData();
        //String[] arr = {MediaStore.Images.Media.DATA};
        //Cursor cursor = getContext().getContentResolver().query(uri, arr, null, null, null);
        //if (cursor != null) {
        //    int img_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        //    cursor.moveToFirst();
        //    String img_path = cursor.getString(img_index);
        //    File file15 = new File(img_path); //Get File
        //    cursor.close(); //Release resources
        //} else {
        //    Toast.makeText(getContext(), "Cursor:null", Toast.LENGTH_SHORT).show();
        //}
       File f = getFile(getContext(),file);//new File(Objects.requireNonNull(FileUtils.getPath(file))); //getFile(getContext(),file);
       //DocumentFile f = DocumentFile.fromSingleUri(getContext(),file );
       // //oast.makeText(getContext(), file.getScheme().toString(), Toast.LENGTH_SHORT).show();
       // File f13 = new File(result.getData().getData().getPath());
       // Uri uriii = Uri.fromFile(f13);
       // File file4 = ;


        //File f21 = new File(Objects.requireNonNull(ContentUriUtil.INSTANCE.getFilePath(getContext(), result.getData().getData()))) ;
        //File file5 = new File(getReal);
        //File file1 = f.createFile("text/csv","file");
        //Log.d("ERROR",f.getName());
        //f.getName();
//

        //ApiService service = ApiAdapter.getApiService().postEvalCsv()


      //RequestBody requestFile = RequestBody.create(MediaType.parse("application/octet-stream"),new File(f.getPath()));

      //RequestBody requestFile = RequestBody.create(MediaType.parse("text/csv"),f.getName())
        // RequestBody.create(MediaType.parse("application/octet-stream"),file.getName());
        //RequestBody body = new MultipartBody.Builder()
          //      .setType(MultipartBody.FORM)
            //    .addFormDataPart("file",f.getPath(),requestFile)
              //  .build();
        // MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(),body);//MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(), requestFile);//MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", f.getName(),requestFile);//MultipartBody.Part.createFormData();

/*
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",f.getPath(),RequestBody
                        .create(MediaType.parse("application/octet-stream"),
                                new File(f.getPath())))
                .build();

        Call<EvalResponse> evalResponseCall = ApiAdapter.getApiService().postEvalCsv(formBody);
        evalResponseCall.enqueue(new Callback<EvalResponse>() {
               @Override
               public void onResponse(Call<EvalResponse> call, Response<EvalResponse> response) {
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

               @Override
               public void onFailure(Call<EvalResponse> call, Throwable t) {
                   Log.e("Error",t.getMessage());
               }
           });


                // OkHttpClient client = new OkHttpClient();
                // try {
                //     this.getContext().getContentResolver().openInputStream(file).read();
                // } catch (FileNotFoundException e) {
                //     e.printStackTrace();
                // } catch (IOException e) {
                //     e.printStackTrace();
                // }
                // {
                //     // read bytes and create requestbody here
                // }\



 */

         //   RequestBody requestFile = RequestBody.create(MediaType.parse(getContext().getContentResolver().getType(file)), f.getName());
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();


        MediaType mediaType = MediaType.parse("text/plain");


        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",f.getPath(),RequestBody
                        .create(MediaType.parse("application/octet-stream"),
                                new File(f.getPath())))
                .build();

        Request request = new Request.Builder()
                .url("https://oxipulse.herokuapp.com/upload")
                .post(formBody)
                .build();


        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("ERROR", e.getMessage(), e);
                //Toast.makeText(getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                //si la respuesta se obtiene
               if (response.isSuccessful()) {

                   //Type listType = new TypeToken<ArrayList<EvalResponse>>(){}.getType();
                   List<EvalResponse> yourClassList = getList(response.body().string(),EvalResponse.class);

                   switch (yourClassList.get(0).getData().get(0).getTriage()){
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
                   //d.show();
                   d.show();
               //    String res= response.body().string();
               //    List<EvalResponse> evalResponseList = new EvalResponse();
               //    EvalResponse evalResponse =
               //    //switch en caso de cada respuesta, cambia el color del cuadro y el texto del triage

               //
               //    }
               //    Log.d("ERROR", "Response" + response.toString());

               //    //Guardar resultado en base de datos
               //    // SaveEvalCsv(response,);
               //    d.show();

                }
            }
        });

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
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
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