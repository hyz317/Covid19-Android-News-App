package com.java.heyuze.ui.covid19;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.java.heyuze.R;

public class Covid19Fragment extends Fragment {

    private Covid19ViewModel covid19ViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        covid19ViewModel =
                ViewModelProviders.of(this).get(Covid19ViewModel.class);
        View root = inflater.inflate(R.layout.fragment_covid19, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        covid19ViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}