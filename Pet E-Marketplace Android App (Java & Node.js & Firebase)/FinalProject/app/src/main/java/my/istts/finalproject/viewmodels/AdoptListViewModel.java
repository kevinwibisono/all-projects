package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Adoption;
import my.istts.finalproject.models.AdoptionDBAccess;
import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.AdoptionItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdoptListViewModel {
    private AdoptionDBAccess adoptDB;
    private AkunDBAccess akunDB;
    private Application app;

    public AdoptListViewModel(Application app){
        this.app = app;
        adoptDB = new AdoptionDBAccess();
        akunDB = new AkunDBAccess(app);
    }

    private String[] petTypeList = {"Anjing", "Kucing", "Kelinci", "Burung", "Ikan", "Hamster", "Reptil", "Lainnya"};
    private MutableLiveData<ArrayList<String>> activePetTypes = new MutableLiveData<>();
    private MutableLiveData<ArrayList<Integer>> activePetTypeInt = new MutableLiveData<>();
    public MutableLiveData<boolean[]> chipPetType = new MutableLiveData<>(new boolean[8]);

    private String[] genderTypes = {"Jantan", "Betina"};
    private int jenis_kelamin = -1;
    private MutableLiveData<String> activePetGender = new MutableLiveData<>("");
    public MutableLiveData<boolean[]> chipPetGender = new MutableLiveData<>(new boolean[2]);
    
    public MutableLiveData<String> ageMin = new MutableLiveData<>("");
    public MutableLiveData<String> ageMax = new MutableLiveData<>("");

    public int ageType = 0;
    private String email;

    public boolean hpCriteria = false;

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<AdoptionItemViewModel>> adoptVMs = new MutableLiveData<>();

    public void initFilters(){
        activePetTypes.setValue(new ArrayList<>());
        activePetTypeInt.setValue(new ArrayList<>());

        boolean[] initPetType = new boolean[8];
        boolean[] initGender = new boolean[2];
        for (int i = 0; i < 8; i++) {
            if(i < 2){
                initGender[i] = false;
            }
            initPetType[i] = false;
        }
        chipPetType.setValue(initPetType);
        chipPetGender.setValue(initGender);

        getAdoptionsFiltered();
    }

    private void getAdoptionsFiltered(){
        loading.setValue(true);
        adoptVMs.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();

                    adoptDB.getPetAdoptsFiltered(activePetTypeInt.getValue(), jenis_kelamin, email, hpCriteria).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                ArrayList<Adoption> adopts = new ArrayList<>();
                                for (DocumentSnapshot adoptDoc:
                                        queryDocumentSnapshots.getDocuments()) {
                                    Adoption adoption = new Adoption(adoptDoc);
                                    int umurAdopt = adoption.getUmur();
                                    if(adoption.getSatuan_umur() == 1){
                                        umurAdopt = umurAdopt * 12;
                                    }
                                    if(umurValid(umurAdopt)){
                                        adopts.add(adoption);
                                    }
                                }
                                addAdoptsToVM(adopts, email);
                            }
                            else{
                                loading.setValue(false);
                            }
                        }
                    });
                }
            }
        });

        akunDB.getSavedAccounts();
    }

    private boolean umurValid(int umurAdopt){
        int umurFilterMin = 0;
        int umurFilterMax = 99999999;
        if(!ageMax.getValue().equals("")){
            umurFilterMax = Integer.parseInt(ageMax.getValue());
        }
        if(!ageMin.getValue().equals("")){
            umurFilterMin = Integer.parseInt(ageMin.getValue());
        }
        if(ageType == 1){
            umurFilterMax = umurFilterMax * 12;
            umurFilterMin = umurFilterMin * 12;
        }
        return umurAdopt <= umurFilterMax && umurAdopt >= umurFilterMin;
    }

    private void addAdoptsToVM(ArrayList<Adoption> adopts, String email){
        ArrayList<AdoptionItemViewModel> currentVMs = adoptVMs.getValue();
        for (Adoption adopt:
             adopts) {
            currentVMs.add(new AdoptionItemViewModel(adopt, email, app));
        }
        adoptVMs.setValue(currentVMs);
        loading.setValue(false);
    }

    public void setFilters(){
        //untuk tampilan quick filter
        activePetTypes.setValue(new ArrayList<>());
        activePetGender.setValue("");

        //untuk keperluan query db
        activePetTypeInt.setValue(new ArrayList<>());

        for (int i = 0; i < 8; i++) {
            if(i < 2 && chipPetGender.getValue()[i]){
                activePetGender.setValue(genderTypes[i]);
                jenis_kelamin = i;
            }
            if(chipPetType.getValue()[i]){
                addCheckedPetType(i);
            }
        }

        getAdoptionsFiltered();
    }

    private void addCheckedPetType(int index){
        ArrayList<String> currentPetTypes = activePetTypes.getValue();
        ArrayList<Integer> currentPetTypesInt = activePetTypeInt.getValue();
        currentPetTypes.add(petTypeList[index]);
        currentPetTypesInt.add(index);
        activePetTypes.setValue(currentPetTypes);
        activePetTypeInt.setValue(currentPetTypesInt);
    }

    public void removeGenderFilter(){
        activePetGender.setValue("");

        boolean[] genderChecked = new boolean[2];
        for (int i = 0; i < 2; i++) {
            genderChecked[i] = false;
        }
        chipPetGender.setValue(genderChecked);
        jenis_kelamin = -1;

        getAdoptionsFiltered();
    }

    public void removePetTypes(int position){
        ArrayList<String> currentPetTypes = activePetTypes.getValue();
        ArrayList<Integer> currentPetTypesInt = activePetTypeInt.getValue();
        int petTypeNum = currentPetTypesInt.get(position);
        currentPetTypes.remove(position);
        currentPetTypesInt.remove(position);
        activePetTypes.setValue(currentPetTypes);
        activePetTypeInt.setValue(currentPetTypesInt);

        boolean[] types = chipPetType.getValue();
        types[petTypeNum] = false;
        chipPetType.setValue(types);

        getAdoptionsFiltered();
    }


    public LiveData<ArrayList<String>> getActivePetTypes() {
        return activePetTypes;
    }

    public LiveData<String> getActiveGender() {
        return activePetGender;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<ArrayList<AdoptionItemViewModel>> getPetAdopts() {
        return adoptVMs;
    }

    public void setAgeType(int ageType) {
        this.ageType = ageType;
    }

    public void setHpCriteria(boolean hpCriteria) {
        this.hpCriteria = hpCriteria;

        getAdoptionsFiltered();
    }
}
