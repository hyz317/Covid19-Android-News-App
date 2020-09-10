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
import com.google.android.material.tabs.TabLayout;
import com.java.heyuze.Adapter.CovidInfoAdapter;
import com.java.heyuze.Adapter.NewsAdapter;
import com.java.heyuze.CovidDataHandler;
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
    private InfectData worldTotalData = null, chinaTotalData = null;
    private Typeface typeFace;
    private CovidInfoAdapter adapter;

    public void firstLoad() {
        if (!dataLoaded) {
            typeFace = Typeface.createFromAsset(getActivity().getAssets(),"fonts/msyhbd.ttc");
            Vector<InfectData> data = InfoManager.getInstance().getInfectData();
            for (InfectData d : data) {
                if (d.location.size() == 1) {
                    if (d.location.get(0).equals("World")) worldTotalData = d;
                    else if (d.location.get(0).equals("China")) chinaTotalData = d;
                    else {
                        worldData.add(d);
                    }
                } else if (d.location.size() == 2 && d.location.get(0).equals("China")) {
                    chinaData.add(d);
                }
            }
            dataLoaded = true;
        }
    }

    public void showChinaData(View root) {
        TextView title = root.findViewById(R.id.covid_title);
        title.setText("国内疫情");
        title.setTypeface(typeFace);
        TextView regionField = root.findViewById(R.id.region_field);
        regionField.setText("省份");

        if (!dataLoaded) return;

        TextView number1 = root.findViewById(R.id.now_increase);
        TextView number2 = root.findViewById(R.id.now_confirmed);
        TextView number3 = root.findViewById(R.id.now_cured);
        TextView number4 = root.findViewById(R.id.now_dead);

        number1.setText(String.valueOf(chinaTotalData.confirmed.get(chinaTotalData.confirmed.size() - 1) - chinaTotalData.confirmed.get(chinaTotalData.confirmed.size() - 2)));
        number2.setText(String.valueOf(chinaTotalData.confirmed.get(chinaTotalData.confirmed.size() - 1)));
        number3.setText(String.valueOf(chinaTotalData.cured.get(chinaTotalData.cured.size() - 1)));
        number4.setText(String.valueOf(chinaTotalData.dead.get(chinaTotalData.dead.size() - 1)));

        ListView listView = root.findViewById(R.id.covid_info_sheet);
        adapter = new CovidInfoAdapter(getActivity(), chinaData);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void showWorldData(View root) {
        TextView title = root.findViewById(R.id.covid_title);
        title.setText("全球疫情");
        title.setTypeface(typeFace);
        TextView regionField = root.findViewById(R.id.region_field);
        regionField.setText("国家");

        if (!dataLoaded) return;

        TextView number1 = root.findViewById(R.id.now_increase);
        TextView number2 = root.findViewById(R.id.now_confirmed);
        TextView number3 = root.findViewById(R.id.now_cured);
        TextView number4 = root.findViewById(R.id.now_dead);

        number1.setText(String.valueOf(worldTotalData.confirmed.get(worldTotalData.confirmed.size() - 1) - worldTotalData.confirmed.get(worldTotalData.confirmed.size() - 2)));
        number2.setText(String.valueOf(worldTotalData.confirmed.get(worldTotalData.confirmed.size() - 1)));
        number3.setText(String.valueOf(worldTotalData.cured.get(worldTotalData.cured.size() - 1)));
        number4.setText(String.valueOf(worldTotalData.dead.get(worldTotalData.dead.size() - 1)));

        ListView listView = root.findViewById(R.id.covid_info_sheet);
        adapter = new CovidInfoAdapter(getActivity(), worldData);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        covid19ViewModel =
                ViewModelProviders.of(this).get(Covid19ViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_covid19, container, false);
        /*final TextView textView = root.findViewById(R.id.text_dashboard);
        covid19ViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        final TabLayout tabLayout = root.findViewById(R.id.covid_tab);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().equals("国内疫情")) showChinaData(root);
                else showWorldData(root);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        CovidDataHandler covidDataHandler = new CovidDataHandler(this, root);
        Thread thread = new Thread(covidDataHandler);
        thread.start();

        return root;
    }
}