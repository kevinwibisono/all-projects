package my.istts.finalproject.inputclasses;

import androidx.lifecycle.MutableLiveData;

public class RegisterInput {
    public MutableLiveData<String> password = new MutableLiveData<>("");
    public MutableLiveData<String> confirmPass = new MutableLiveData<>("");
    public MutableLiveData<String> email = new MutableLiveData<>("");
    public MutableLiveData<String> nama = new MutableLiveData<>("");

    public Boolean isEmpty(){
        if(password.getValue().equals("") || email.getValue().equals("") || nama.getValue().equals("")){
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

    public void setPassword(String password) {
        this.password.setValue(password);
        this.confirmPass.setValue(password);
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public void setNama(String nama) {
        this.nama.setValue(nama);
    }
}
