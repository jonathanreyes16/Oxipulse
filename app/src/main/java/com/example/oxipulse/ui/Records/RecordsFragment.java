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
import com.example.oxipulse.model.record;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecordsFragment extends Fragment {

    private RecordsViewModel recordsViewModel;
    ArrayList<String> patientdata = new ArrayList<>(Arrays.asList("fecha1", "hr1","rc1","etiqueta1","fecha2", "hr2","rc2","etiqueta2"));
    ArrayList<String> headers = new ArrayList<>(Arrays.asList("Fecha","Ritmo Cardiaco(ppm)","Saturacion de oxigeno(%)","Etiqueta" ));

    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference User_RecordReference,RecordReference;
    String uid;
    DatabaseReference RecordReference2;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recordsViewModel =
                new ViewModelProvider(this).get(RecordsViewModel.class);
        View v = inflater.inflate(R.layout.fragment_records, container, false);

        //datos y configuracion de los encabezados de la tabla de usuarios
        RecyclerView headersView = (RecyclerView) v.findViewById(R.id.listheaders);
        GridLayoutManager headerLayoutManager = new GridLayoutManager(getContext(),4);
        headersView.setLayoutManager(headerLayoutManager);
        CustomAdapter headerAdapter = new CustomAdapter(getContext(),headers);
        headersView.setAdapter(headerAdapter);

        //datos y configuraion del recycler view que se encarga de los datos de los pacientes
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.datarecycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),4);
        recyclerView.setLayoutManager(gridLayoutManager);
        CustomAdapter customAdapter = new CustomAdapter(getContext(),patientdata);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(customAdapter);

        //Firebase current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Si el usuario no es nullo se sigue con la aplicacion
        if (user != null){
            //uid es igual al uid del usuario actual
            uid=user.getUid();
            //Se obtiene la instancia de la base de datos
            database = FirebaseDatabase.getInstance();
            User_RecordReference=database.getReference("User-Records").child(uid);
            RecordReference=database.getReference("Records");
            RecordReference2=database.getReference("Records");



            User_RecordReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    //si la tarea falla se crea un log del error
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    //si se completa la tarea correctamente se sigue con la aplicacion, que en este caso es obtener la base de datos
                    else {
                        /*
                        ArrayList<record> PatientRecords = new ArrayList<record>();
                        //PatientRecords=task.getResult().getValue(record.class);
                        for ( DataSnapshot s :task.getResult().getChildren()) {
                            //PatientRecords.add(s.getValue(record.class));
                            record r= new record(s.getValue(record.class).getDate(),
                                    s.getValue(record.class).getDegree_of_urgency(),
                                    s.getValue(record.class).getId(),
                                    s.getValue(record.class).getHr(),
                                    s.getValue(record.class).getOxi(),
                                    s.getValue(record.class).getTag()
                                    );
                            PatientRecords.add(r);

                         */
                           // Log.e("ejemplo",s.getValue(record.class).toString());
                        }
                    }
                });
            }








        return v;
    }
}