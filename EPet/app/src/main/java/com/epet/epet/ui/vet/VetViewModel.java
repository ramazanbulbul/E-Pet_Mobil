package com.epet.epet.ui.vet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VetViewModel extends ViewModel {

        private MutableLiveData<String> mText;

    public VetViewModel() {
            mText = new MutableLiveData<>();
            mText.setValue("This is vet fragment");
        }

        public LiveData<String> getText() {
            return mText;
        }
}
