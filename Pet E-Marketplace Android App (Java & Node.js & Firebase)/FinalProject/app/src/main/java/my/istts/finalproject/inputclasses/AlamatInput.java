package my.istts.finalproject.inputclasses;

import androidx.lifecycle.MutableLiveData;

public class AlamatInput {
    public MutableLiveData<String> nama = new MutableLiveData<>("");
    public MutableLiveData<String> hp = new MutableLiveData<>("");
    public MutableLiveData<String> alamat = new MutableLiveData<>("");
    public MutableLiveData<String> kelurahan = new MutableLiveData<>("");
    public MutableLiveData<String> kecamatan = new MutableLiveData<>("");
    public MutableLiveData<String> kota = new MutableLiveData<>("");
    public MutableLiveData<String> kodepos = new MutableLiveData<>("");
    public MutableLiveData<String> catatan = new MutableLiveData<>("");
    private String koordinat = "";

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public int emptyField(){
        int index = -1;
        String[] inputs = {nama.getValue(), hp.getValue(), alamat.getValue()};
        for (int i=0;i<3;i++){
            if(inputs[i].equals("") && index == -1){
                index = i;
            }
        }
        return index;
    }

    public Boolean phoneInvalid(){
        if(hp.getValue().length() < 10 || hp.getValue().length() > 12){
            return true;
        }
        return false;
    }
}
