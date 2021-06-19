package com.example.oxipulse.ui.Records;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oxipulse.R;
import com.example.oxipulse.model.PatientAdapter;
import com.example.oxipulse.model.RecordAdapter;
import com.example.oxipulse.model.patient;
import com.example.oxipulse.model.record;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RecordsFragment extends DialogFragment {

    //private RecordsViewModel recordsViewModel;

    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference actualUserRef, User_RecordReference,RecordReference, UsersReference;
    String uid;
    RecordAdapter recordAdapter;
    PatientAdapter patientAdapter;

    public static RecordsFragment newInstance(String uid){
        RecordsFragment fragment = new RecordsFragment();
        Bundle args = new Bundle();
        args.putString("UID", uid);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //recordsViewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        View v = inflater.inflate(R.layout.fragment_records, container, false);

        if(this.getArguments()!=null){
            Bundle args = this.getArguments();
            uid=args.get("UID").toString();
        }
        else {
            //Firebase current user
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                //uid es igual al uid del usuario actual
                uid = user.getUid();
            }
        }

        //Se obtiene la instancia de la base de datos
        database = FirebaseDatabase.getInstance();
        actualUserRef = database.getReference("Users").child(uid);
        UsersReference = database.getReference("Users");
        User_RecordReference = database.getReference("User-Records").child(uid);
        RecordReference = database.getReference("Records");


        RecyclerView recyclerView =  v.findViewById(R.id.datarecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        FirebaseRecyclerOptions<patient> options1 = new FirebaseRecyclerOptions.Builder<patient>()
                .setQuery(UsersReference.orderByChild("isDoc").equalTo("false"), patient.class).build();

        patientAdapter = new PatientAdapter(options1);

        FirebaseRecyclerOptions<record> options2 = new FirebaseRecyclerOptions.Builder<record>()
                .setIndexedQuery(User_RecordReference, RecordReference, record.class).build();

        recordAdapter = new RecordAdapter(options2);


        actualUserRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                //si la tarea no es completada, no se puede obtener el snapshot de la base de datos se marca un error en el log
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error Getting data", task.getException());
                }
                //sino, significa que la tarea se cumple y podemos seguir
                //si
                else if (Boolean.parseBoolean(Objects.requireNonNull((Objects.requireNonNull(task.getResult())).getValue(patient.class)).getIsDoc())) {
                    TextView Nombre = v.findViewById(R.id.lbl_date);
                    TextView FechaNac = v.findViewById(R.id.lbl_heartRate);
                    TextView Peso = v.findViewById(R.id.lbl_oxi_sat);
                    TextView Estatura = v.findViewById(R.id.lbl_label);
                    Nombre.setText(R.string.first_name);
                    FechaNac.setText(R.string.fecha_de_nacimiento);
                    Peso.setText(R.string.peso);
                    Estatura.setText(R.string.estatura);
                    recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(patientAdapter);


                } else {
                    //datos y configuraion del recycler view que se encarga de los datos de los pacientes
                    recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(recordAdapter);

                    //Log.e("list", PatientRecords.toString());
                }

            }
        });

       return v;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (recordAdapter!=null){
            recordAdapter.stopListening();
        }
        if(patientAdapter !=null){
            patientAdapter.stopListening();
        }

    }

    @Override
    public void onStart()
    {
        super.onStart();
        if(recordAdapter!=null){
            recordAdapter.startListening();
        }
        if(patientAdapter !=null){
            patientAdapter.startListening();
        }
    }
}