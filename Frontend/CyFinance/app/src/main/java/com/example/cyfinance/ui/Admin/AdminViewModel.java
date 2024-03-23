package com.example.cyfinance.ui.Admin;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Boolean> mButton;
    public AdminViewModel() {
        mText = new MutableLiveData<>();
        mButton = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<Boolean> getButtonClicked() {
        return mButton;
    }
    public void onButtonClick() {
        mButton.setValue(true);
    }
}