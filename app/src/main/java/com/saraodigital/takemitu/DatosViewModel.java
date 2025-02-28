package com.saraodigital.takemitu;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DatosViewModel extends ViewModel {

    private MutableLiveData<Location> mText;

    public DatosViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is home fragment");
    }

    public void setLocalizacion(Location input) {
        mText.setValue(input);

        //System.out.println("Pone el texto: "+mText.getValue().getProvider());
    }

    public LiveData<Location> getLocalizacion() {


        return mText;
    }
}