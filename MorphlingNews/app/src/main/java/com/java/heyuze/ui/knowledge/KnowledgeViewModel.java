package com.java.heyuze.ui.knowledge;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class KnowledgeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public KnowledgeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is knowledge fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}