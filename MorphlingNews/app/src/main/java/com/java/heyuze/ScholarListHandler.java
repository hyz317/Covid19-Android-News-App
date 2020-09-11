package com.java.heyuze;

import android.view.View;

import androidx.fragment.app.Fragment;

import com.java.heyuze.ui.covid19.Covid19Fragment;
import com.java.heyuze.ui.scholars.ScholarsFragment;

public class ScholarListHandler implements Runnable {
    Fragment fragment;
    View root;

    public ScholarListHandler(Fragment fragment, View root) {
        this.fragment = fragment;
        this.root = root;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (!InfoManager.getInstance().hasScholarData()) {
                    try {
                        wait(200);
                        System.out.println("scholar loading...");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
        synchronized (InfoManager.getInstance()) {
            System.out.println(fragment.getActivity());
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((ScholarsFragment) fragment).getData(ScholarData.ScholarType.HIGHATTENTION);
                }
            });
        }
    }
}

