package com.example.oxipulse.ui.Exit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.oxipulse.R;
import com.example.oxipulse.StartActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ExitFragment extends Fragment {
    private ExitViewModel mViewModel;
    public static ExitFragment newInstance() {
        return new ExitFragment();
    }
    //definicion de boton
    Button btn_logout;
    @Override
 public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                          @Nullable Bundle savedInstanceState) {//el fragmento tendra la vista de exit_fragment
        View v = inflater.inflate(R.layout.exit_fragment, container, false);
        //se infla el widget
        btn_logout=v.findViewById(R.id.btn_signout);
        btn_logout.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View v) {
               //se obtiene la instacia de auth
                FirebaseAuth auth = FirebaseAuth.getInstance();
                //metodo para cerrar sesion
                auth.signOut();
                //se crea un intent con la actividad de inicio
                Intent intent = new Intent(getActivity(),StartActivity.class);
                //se inicia el intent
                startActivity(intent);
                //se cierra esta activity completa
                getActivity().finish();
            }
        });
        return v;
    }

    /*
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ExitViewModel.class);
        // TODO: Use the ViewModel
    }
    */
}