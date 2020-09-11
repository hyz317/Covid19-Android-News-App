package com.java.heyuze.ui.knowledge;

import android.content.Intent;
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

import com.java.heyuze.Activity.KnowledgeActivity;
import com.java.heyuze.Activity.SearchActivity;
import com.java.heyuze.Activity.SearchContentActivity;
import com.java.heyuze.InfoManager;
import com.java.heyuze.R;

import scut.carson_ho.searchview.ICallBack;
import scut.carson_ho.searchview.SearchView;

public class KnowledgeFragment extends Fragment {
    private SearchView searchView;
    private KnowledgeViewModel knowledgeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        knowledgeViewModel =
                ViewModelProviders.of(this).get(KnowledgeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_knowledge, container, false);
        /*final TextView textView = root.findViewById(R.id.text_notifications);
        knowledgeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        searchView = (SearchView) root.findViewById(R.id.search_view_knowledge);
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                Intent intent = new Intent(getActivity(), KnowledgeActivity.class);
                intent.putExtra("word", string);
                getActivity().startActivity(intent);
            }
        });

        return root;
    }
}