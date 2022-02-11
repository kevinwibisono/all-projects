package my.istts.finalproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AddAppoPetViewModel {
    public MutableLiveData<String> petName = new MutableLiveData<>("");
    public MutableLiveData<String> petAge = new MutableLiveData<>("");
    private MutableLiveData<String> petType = new MutableLiveData<>("");
    private MutableLiveData<String[]> errors = new MutableLiveData<>();
    private MutableLiveData<Integer> tlNumber = new MutableLiveData<>();
    private String[] petTypes = {"Anjing", "Kucing", "Kelinci", "Burung", "Ikan", "Hamster", "Reptil", "Ternak", "Lainnya"};
    private String petAgeType = "Bulan";

    public void setPetType(int optsChosen){
        if(optsChosen < petTypes.length){
            petType.setValue(petTypes[optsChosen]);
        }
    }

    public void setPetAgeType(String tipe){
        petAgeType = tipe;
    }

    public String getPetAgeType() {
        return petAgeType;
    }

    public boolean isValid(){
        if(petName.getValue().equals("")){
            errors.setValue(new String[]{"Nama Tidak Boleh Kosong", "", ""});
            tlNumber.setValue(0);
            return false;
        }
        else if(petAge.getValue().equals("") || Integer.parseInt(petAge.getValue()) < 1){
            errors.setValue(new String[]{"", "Usia Minimal Adalah 1", ""});
            tlNumber.setValue(1);
            return false;
        }
        else if(petType.getValue().equals("")){
            errors.setValue(new String[]{"", "", "Tipe Hewan Peliharaan Harus Dipilih"});
            tlNumber.setValue(2);
            return false;
        }
        return true;
    }

    public LiveData<String[]> getErrors() {
        return errors;
    }

    public LiveData<Integer> getTlNumber() {
        return tlNumber;
    }

    public LiveData<String> getTypes() {
        return petType;
    }
}
