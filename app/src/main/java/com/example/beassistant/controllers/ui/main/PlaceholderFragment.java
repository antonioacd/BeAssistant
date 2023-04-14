package com.example.beassistant.controllers.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beassistant.MainActivity;
import com.example.beassistant.R;
import com.example.beassistant.controllers.fragments.CaraFragment;
import com.example.beassistant.controllers.fragments.CejasFragment;
import com.example.beassistant.controllers.fragments.LabiosFragment;
import com.example.beassistant.controllers.fragments.MainFragment;
import com.example.beassistant.controllers.fragments.OjosFragment;
import com.example.beassistant.databinding.FragmentScreenBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;
    private FragmentScreenBinding binding;

    public static Fragment newInstance(int index) {

        Fragment fragment = null;

        switch (index){

            case 1: fragment = new CaraFragment();
                break;
            case 2: fragment = new MainFragment();
                break;
            case 3: fragment = new OjosFragment();
                break;
            case 4: fragment = new CejasFragment();
                break;

        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_screen, container, false);

        /*final TextView textView = binding.sectionLabel;
        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}