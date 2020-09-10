package com.java.heyuze.ui.covid19;

import android.graphics.Typeface;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.core.SmartTable;
import com.java.heyuze.Adapter.CovidInfoAdapter;
import com.java.heyuze.Adapter.NewsAdapter;
import com.java.heyuze.InfectData;
import com.java.heyuze.InfoManager;
import com.java.heyuze.R;
import com.java.heyuze.SimpleInfectData;

import java.util.Vector;
import java.util.stream.Collectors;

public class Covid19Fragment extends Fragment {

    private Covid19ViewModel covid19ViewModel;
    private boolean dataLoaded = false;
    private Vector<InfectData> worldData = new Vector<>();
    private Vector<InfectData> chinaData = new Vector<>();
    private Vector<SimpleInfectData> simpleWorldData = new Vector<>();
    private Vector<SimpleInfectData> simpleChinaData = new Vector<>();
    private InfectData worldTotalData = null, chinaTotalData = null;
    private Typeface typeFace;
    private CovidInfoAdapter adapter;

    private void showChinaData(View root) {
        TextView title = root.findViewById(R.id.china_covid_title);
        title.setTypeface(typeFace);
        ListView listView = root.findViewById(R.id.covid_info_sheet);
        System.out.println(simpleChinaData.size());
        adapter = new CovidInfoAdapter(getActivity(), simpleChinaData);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        covid19ViewModel =
                ViewModelProviders.of(this).get(Covid19ViewModel.class);
        View root = inflater.inflate(R.layout.fragment_covid19, container, false);
        /*final TextView textView = root.findViewById(R.id.text_dashboard);
        covid19ViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        if (!dataLoaded) {
            typeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/msyhbd.ttc");
            Vector<InfectData> data = InfoManager.getInstance().getInfectData();
            for (InfectData d : data) {
                if (d.location.size() == 1) {
                    if (d.location.get(0).equals("World")) worldTotalData = d;
                    else if (d.location.get(0).equals("China")) chinaTotalData = d;
                    else {
                        worldData.add(d);
                        simpleWorldData.add(new SimpleInfectData(d, "World"));
                    }
                } else if (d.location.size() == 2 && d.location.get(0).equals("China")) {
                    chinaData.add(d);
                    simpleChinaData.add(new SimpleInfectData(d, "China"));
                }
            }
            dataLoaded = true;
        }

        showChinaData(root);

        return root;
    }
}