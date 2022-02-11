package com.example.sellerapp.inputclasses;

import androidx.lifecycle.MutableLiveData;

public class RegisterInput {
    public MutableLiveData<String> password = new MutableLiveData<>("");
    public MutableLiveData<String> confirmPass = new MutableLiveData<>("");
    public MutableLiveData<String> email = new MutableLiveData<>("");

    public Boolean isEmpty(){
        if(password.getValue().equals("") || email.getValue().equals("")){
            return true;
        }
        return false;
    }

    public Boolean confirmInvalid(){
        if(!password.getValue().equals(confirmPass.getValue())){
            return true;
        }
        return false;
    }

    public Boolean phoneInvalid(){
        if(email.getValue().length() < 10 || email.getValue().length() > 12){
            return true;
        }
        return false;
    }

}
