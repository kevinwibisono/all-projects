package my.istts.finalproject.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Akun;
import my.istts.finalproject.models.AkunDBAccess;
import my.istts.finalproject.models.Diskusi;
import my.istts.finalproject.models.DiskusiDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.DiscussItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DiscussListViewModel {
    private DiskusiDBAccess discussDB;
    private AkunDBAccess akunDB;
    private Application app;

    public DiscussListViewModel(Application app){
        this.app = app;
        discussDB = new DiskusiDBAccess();
        akunDB = new AkunDBAccess(app);
    }

    private String[] jenisHewan = {"Anjing", "Kucing", "Kelinci", "Burung", "Ikan", "Hamster", "Reptil", "Lainnya"};
    private MutableLiveData<ArrayList<String>> activePetType = new MutableLiveData<>();
    private ArrayList<Integer> activePetTypeInt = new ArrayList<>();
    public MutableLiveData<boolean[]> chipPetTypesChecked = new MutableLiveData<>(new boolean[8]);

    private String[] sortTypes = {"like", "tanggal"};
    private String[] sortNames = {"Paling Disukai", "Terbaru"};
    private Query.Direction[] sortDirections = {Query.Direction.DESCENDING, Query.Direction.DESCENDING};
    private String fieldSorted = "";
    private Query.Direction directionSort = Query.Direction.ASCENDING;
    private MutableLiveData<String> activeSort = new MutableLiveData<>("");
    public MutableLiveData<boolean[]> chipSortChecked = new MutableLiveData<>(new boolean[2]);

    public boolean hpCriteria = false;
    public String email;

    public MutableLiveData<String> searchKeyword = new MutableLiveData<>("");

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<DiscussItemViewModel>> discussVMs = new MutableLiveData<>();

    public void initFilters(){
        activePetType.setValue(new ArrayList<>());

        boolean[] initPetTypes = new boolean[8];
        boolean[] initSorts = new boolean[2];
        for (int i = 0; i < 8; i++) {
            if(i < 2){
                initSorts[i] = false;
            }
            initPetTypes[i] = false;
        }
        chipPetTypesChecked.setValue(initPetTypes);
        chipSortChecked.setValue(initSorts);
    }

    public void getDiscussFiltered(){
        loading.setValue(true);
        discussVMs.setValue(new ArrayList<>());

        akunDB.setGetCompleteListener(new AkunDBAccess.onCompleteGetListener() {
            @Override
            public void onComplete(List<Akun> accountsGot) {
                if(accountsGot.size() > 0){
                    email = accountsGot.get(0).getEmail();

                    discussDB.getDiscussion(fieldSorted, directionSort, email, hpCriteria).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.getDocuments().size() > 0){
                                ArrayList<Diskusi> discussions = new ArrayList<>();
                                for (DocumentSnapshot doc:
                                     queryDocumentSnapshots.getDocuments()) {
                                    Diskusi diskusi = new Diskusi(doc);
                                    if(categoriesFulfilled(diskusi, activePetTypeInt)){
                                        discussions.add(diskusi);
                                    }
                                }
                                addDiscussionsToVM(discussions);
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

    private boolean categoriesFulfilled(Diskusi d, ArrayList<Integer> catsSelected){
        if(catsSelected.size() <= 0){
            return true;
        }
        else{
            int sameCategory = 0;
            for (int category:
                    catsSelected) {
                if(d.getTopik_hewan() == category){
                    sameCategory++;
                }
            }
            if(sameCategory > 0){
                return true;
            }
            else{
                return false;
            }
        }
    }

    private void addDiscussionsToVM(ArrayList<Diskusi> diskusi){
        ArrayList<DiscussItemViewModel> currentVMs = discussVMs.getValue();
        for (Diskusi disk:
             diskusi) {
            currentVMs.add(new DiscussItemViewModel(disk));
        }
        discussVMs.setValue(currentVMs);
        loading.setValue(false);

    }

    public void setFilters(){
        //untuk tampilan quick filter
        activePetType.setValue(new ArrayList<>());
        activeSort.setValue("");

        //untuk keperluan query db
        activePetTypeInt = new ArrayList<>();
        fieldSorted = "";
        directionSort = Query.Direction.ASCENDING;

        for (int i = 0; i < 8; i++) {
            if(i < 2 && chipSortChecked.getValue()[i]){
                activeSort.setValue(sortNames[i]);
                fieldSorted = sortTypes[i];
                directionSort = sortDirections[i];
            }
            if(chipPetTypesChecked.getValue()[i]){
                addCheckedPetType(i);
            }
        }

        getDiscussFiltered();
    }

    private void addCheckedPetType(int index){
        ArrayList<String> currentPetTypes = activePetType.getValue();
        currentPetTypes.add(jenisHewan[index]);
        activePetTypeInt.add(index);
        activePetType.setValue(currentPetTypes);
    }

    public void removeSort(){
        activeSort.setValue("");
        fieldSorted = "";
        directionSort = Query.Direction.ASCENDING;

        boolean[] genderChecked = new boolean[2];
        for (int i = 0; i < 2; i++) {
            genderChecked[i] = false;
        }
        chipSortChecked.setValue(genderChecked);

        getDiscussFiltered();
    }

    public void removePetTypes(int position){
        ArrayList<String> currentPetTypes = activePetType.getValue();
        int petTypeNum = activePetTypeInt.get(position);
        currentPetTypes.remove(position);
        activePetTypeInt.remove(position);
        activePetType.setValue(currentPetTypes);

        boolean[] types = chipPetTypesChecked.getValue();
        types[petTypeNum] = false;
        chipPetTypesChecked.setValue(types);

        getDiscussFiltered();
    }


    public LiveData<ArrayList<String>> getActivePetTypes() {
        return activePetType;
    }

    public LiveData<String> getActiveSort() {
        return activeSort;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<ArrayList<DiscussItemViewModel>> getPetDiscussions() {
        return discussVMs;
    }

    public void setHpCriteria(boolean hpCriteria) {
        this.hpCriteria = hpCriteria;

        getDiscussFiltered();
    }
}
