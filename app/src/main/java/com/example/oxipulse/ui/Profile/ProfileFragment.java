package com.example.oxipulse.ui.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.oxipulse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    // declaracion de variables
    private ProfileViewModel profileViewModel;

    //declaracion de variables
    EditText tName,tLast1,tLast2,tBirthdate,tWeight,tHeight;
    Spinner tSex;
    CheckBox tAsthma,tDiabetes,tHypertension;
    FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        user = FirebaseAuth.getInstance().getCurrentUser();

        //se instancia el viewmodel, y se le pasan los parametros del ProfileViewModel
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        //se infla el layout de este fragmento
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        //inflar los widgets
        tName=v.findViewById(R.id.et_name);
        tLast1=v.findViewById(R.id.et_name2);
        tLast2=v.findViewById(R.id.et_name3);
        tBirthdate=v.findViewById(R.id.s_birthdate);
        tWeight=v.findViewById(R.id.np_weight);
        tHeight=v.findViewById(R.id.np_height);
        tSex=v.findViewById(R.id.s_sexo);
        tAsthma=v.findViewById(R.id.chk_asthma);
        tDiabetes=v.findViewById(R.id.chk_diabetes);
        tHypertension=v.findViewById(R.id.chk_hypertension);





        /*final TextView textView = root.findViewById(R.id.text_home);
        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

         */
        return v;
    }
}