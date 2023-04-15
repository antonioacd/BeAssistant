package com.example.beassistant.fragments.mainpages;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.beassistant.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    View view;
    FloatingActionButton btn_filter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        btn_filter = (FloatingActionButton) view.findViewById(R.id.btn_filter);

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter();
            }
        });


        // Inflate the layout for this fragment
        return view;


    }

    private void filter(){

        AlertDialog dialog;

        AlertDialog.Builder ventana = new AlertDialog.Builder(getContext());

        ventana.setTitle("Filtrar");

        View v = getLayoutInflater().inflate(R.layout.filter_layout, null);

        /**EditText eNombre = v.findViewById(R.id.etNombre);
         EditText eIp = v.findViewById(R.id.etIp);
         Button aceptar = v.findViewById(R.id.btnConfirmar);
         Button cancelar = v.findViewById(R.id.btnCancelar);

         aceptar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        if (eIp.getText().toString().isEmpty() || eNombre.getText().toString().isEmpty()){
        Toast.makeText(getApplicationContext(), "Debe rellenar todos los campos", Toast.LENGTH_LONG).show();
        }else{
        Contacto c = new Contacto(eIp.getText().toString(), eNombre.getText().toString(), "Hola", "https://www.softzone.es/app/uploads-softzone.es/2018/04/guest.png");

        //Llamamos al metodo insert para añadir el usuario a la base de datos
        if(dbController.insert(c.getNombre(), c.getUltimoMensaje(), c.getIp(), c.getImg()) != -1){

        recAdapterChat.listaChats.add(c);

        recAdapterChat.notifyDataSetChanged();

        dbController.insert(c.getNombre(), c.getUltimoMensaje(), c.getIp(), c.getImg());

        }else{

        Toast.makeText(getApplicationContext(), "Esa id ya esta registrada", Toast.LENGTH_SHORT).show();

        }

        dialog.dismiss();
        }
        }
        });

         cancelar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        dialog.dismiss();
        }
        });*/

        ventana.setView(v);

        dialog = ventana.create();

        dialog.show();
    }
}