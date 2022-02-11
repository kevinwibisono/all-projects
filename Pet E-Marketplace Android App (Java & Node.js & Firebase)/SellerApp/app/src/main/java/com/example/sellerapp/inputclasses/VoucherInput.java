package com.example.sellerapp.inputclasses;

import androidx.lifecycle.MutableLiveData;

public class VoucherInput {
    public MutableLiveData<String> judul = new MutableLiveData<>("");
    public MutableLiveData<String> minimum = new MutableLiveData<>("");
    public MutableLiveData<String> maksimum = new MutableLiveData<>("");
    public MutableLiveData<String> jumlah = new MutableLiveData<>("");

    public int lookForErrors(){
        if(judul.getValue().equals("")){
            return 0;
        }
        else if(minimum.getValue().equals("")){
            return 2;
        }
        else if(maksimum.getValue().equals("")){
            return 3;
        }
        else if(jumlah.getValue().equals("")){
            return 5;
        }
        return -1;
    }
}
