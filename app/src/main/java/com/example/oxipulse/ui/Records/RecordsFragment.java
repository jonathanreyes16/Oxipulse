package com.example.oxipulse.ui.Records;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.oxipulse.R;
import com.example.oxipulse.model.CustomAdapter;
import com.example.oxipulse.model.PatientAdapter;
import com.example.oxipulse.model.RecordAdapter;
import com.example.oxipulse.model.patient;
import com.example.oxipulse.model.record;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import kotlinx.coroutines.AwaitKt;

public class RecordsFragment extends Fragment {

    private RecordsViewModel recordsViewModel;
    //ArrayList<String> patientdata = new ArrayList<>(Arrays.asList("fecha1", "hr1","rc1","etiqueta1","fecha2", "hr2","rc2","etiqueta2"));
    //ArrayList<String> headers = new ArrayList<>(Arrays.asList("Fecha","Ritmo Cardiaco(ppm)","Saturacion de oxigeno(%)","Etiqueta" ));
    ArrayList<record> PatientRecords = new ArrayList<>();
    //ArrayList<String> recordids = new ArrayList<>();
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference actualUserRef, User_RecordReference,RecordReference, UsersReference;
    String uid;
    RecordAdapter recordAdapter;
    PatientAdapter patientAdapter;
    //DatabaseReference RecordReference2;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recordsViewModel =
                new ViewModelProvider(this).get(RecordsViewModel.class);
        View v = inflater.inflate(R.layout.fragment_records, container, false);

        //Firebase current user
        user = FirebaseAuth.getInstance().getCurrentUser();


        //Si el usuario no es nullo se sigue con la aplicacion
        if (user != null) {
            //uid es igual al uid del usuario actual
            uid = user.getUid();

            //Se obtiene la instancia de la base de datos
            database = FirebaseDatabase.getInstance();
            actualUserRef = database.getReference("Users").child(uid);
            UsersReference = database.getReference("Users");

            User_RecordReference = database.getReference("User-Records").child(uid);
            RecordReference = database.getReference("Records");


            RecyclerView recyclerView =  v.findViewById(R.id.datarecycler);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);

            FirebaseRecyclerOptions<patient> options1 = new FirebaseRecyclerOptions.Builder<patient>().setQuery(UsersReference.orderByChild("isDoc").equalTo("false"), patient.class).build();
            patientAdapter = new PatientAdapter(options1);

            FirebaseRecyclerOptions<record> options2 = new FirebaseRecyclerOptions.Builder<record>().setIndexedQuery(User_RecordReference, RecordReference, record.class).build();
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
                       // UsersReference.orderByChild("isDoc").equalTo(false);
                        //UsersReference.orderByChild("isDoc").
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

        }


        return v;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (recordAdapter!=null){
            recordAdapter.stopListening();
        }
        if(patientAdapter!=null){
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
        if(patientAdapter!=null){
            patientAdapter.startListening();
        }
    }
}