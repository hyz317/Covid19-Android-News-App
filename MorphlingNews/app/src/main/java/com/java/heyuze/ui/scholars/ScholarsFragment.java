package com.java.heyuze.ui.scholars;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.tabs.TabLayout;
import com.java.heyuze.Adapter.KnowledgeAdapter;
import com.java.heyuze.Adapter.ScholarsAdapter;
import com.java.heyuze.InfoManager;
import com.java.heyuze.NewsData;
import com.java.heyuze.R;
import com.java.heyuze.ScholarData;
import com.java.heyuze.ScholarListHandler;

import java.util.Vector;

public class ScholarsFragment extends Fragment {

    private ScholarsViewModel scholarsViewModel;
    private ScholarsAdapter adapter;
    private Vector<ScholarData> data;
    private ScholarData.ScholarType nowType;
    private boolean dataLoaded = false;

    public void getData(ScholarData.ScholarType type) {
        data = InfoManager.getInstance().getScholarData(type);
        ListView listView = getActivity().findViewById(R.id.scholar_info_sheet);
        adapter = new ScholarsAdapter(getActivity(), data);
        listView.setAdapter(adapter);
        dataLoaded = true;
    }

    public void showHighScholars() {
        if (!dataLoaded) return;
        data = InfoManager.getInstance().getScholarData(ScholarData.ScholarType.HIGHATTENTION);
        adapter.changeData(data);
        adapter.notifyDataSetChanged();
    }

    public void showPassedScholars() {
        if (!dataLoaded) return;
        data = InfoManager.getInstance().getScholarData(ScholarData.ScholarType.PASSAWAY);
        adapter.changeData(data);
        adapter.notifyDataSetChanged();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        scholarsViewModel =
                ViewModelProviders.of(this).get(ScholarsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_scholars, container, false);
        /*final TextView textView = root.findViewById(R.id.text_notifications);
        scholarsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        ScholarListHandler scholarListHandler = new ScholarListHandler(this, root);
        Thread thread = new Thread(scholarListHandler);
        thread.start();

        final TabLayout tabLayout = root.findViewById(R.id.scholar_tab);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("高关注学者")) showHighScholars();
                else showPassedScholars();

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return root;
    }
}