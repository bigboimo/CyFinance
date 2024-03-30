package com.example.cyfinance.ui.Admin;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Boolean> mButton;
    private final MutableLiveData<String> aText;
    public AdminViewModel() {
        mText = new MutableLiveData<>();
        mButton = new MutableLiveData<>();
        aText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getText1() {
        return aText;
    }
    public LiveData<Boolean> getButtonClicked() {
        return mButton;
    }
    public void onButtonClick() {
        mButton.setValue(true);
    }
}