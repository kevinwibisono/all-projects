package my.istts.finalproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.Artikel;
import my.istts.finalproject.models.ArtikelDBAccess;
import my.istts.finalproject.viewmodels.itemviewmodels.ArticleItemViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ArticleListViewModel {
    private ArtikelDBAccess artikelDB;

    public ArticleListViewModel(){
        artikelDB = new ArtikelDBAccess();
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

    private String[] articleCategories = {"Informasi", "Tips & Trik", "Acara", "Komunitas", "Peristiwa", "Cerita Pemilik"};
    private MutableLiveData<String> activeCategory = new MutableLiveData<>("");
    private int activeCategoryInt = -1;
    public MutableLiveData<boolean[]> chipCategoryChecked = new MutableLiveData<>(new boolean[6]);

    private String[] articlesTarget = {"Semua Pemilik", "Pemilik Baru", "Pemilik Berpengalaman"};
    private MutableLiveData<String> activeTarget = new MutableLiveData<>("");
    private int activeTargetInt = -1;
    public MutableLiveData<boolean[]> chipTargetChecked = new MutableLiveData<>(new boolean[3]);

    public MutableLiveData<Boolean> likedIncluded = new MutableLiveData<>(false);

    private String searchKeyword = "";

    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ArticleItemViewModel>> articleVMs = new MutableLiveData<>();

    public void initFilters(){
        activePetType.setValue(new ArrayList<>());

        boolean[] initPetTypes = new boolean[8];
        boolean[] initSorts = new boolean[2];
        boolean[] initCategory = new boolean[6];
        boolean[] initTarget = new boolean[3];
        for (int i = 0; i < 8; i++) {
            if(i < 2){
                initSorts[i] = false;
            }
            if(i < 3){
                initTarget[i] = false;
            }
            if(i < 6){
                initCategory[i] = false;
            }
            initPetTypes[i] = false;
        }
        chipPetTypesChecked.setValue(initPetTypes);
        chipSortChecked.setValue(initSorts);
        chipTargetChecked.setValue(initTarget);
        chipCategoryChecked.setValue(initCategory);

    }

    public void getArticleFiltered(){
        loading.setValue(true);
        articleVMs.setValue(new ArrayList<>());

        artikelDB.getArticles(fieldSorted, directionSort, activeCategoryInt, activeTargetInt).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    ArrayList<Artikel> artikels = new ArrayList<>();
                    for (DocumentSnapshot articleDoc:
                            queryDocumentSnapshots.getDocuments()) {
                        Artikel artikel = new Artikel(articleDoc);
                        if(artikel.getJudul().toLowerCase().contains(searchKeyword.toLowerCase()) && petTypesFulfilled(artikel)){
                            artikels.add(artikel);
                        }
                    }
                    addArticlesToVM(artikels);
                }
                else{
                    loading.setValue(false);
                }
            }
        });
    }

    private boolean petTypesFulfilled(Artikel artikel){
        if(activePetTypeInt.size() <= 0){
            return true;
        }
        else{
            int samePetType = 0;
            for (int petType:
                    activePetTypeInt) {
                if(artikel.getTopik_hewan().contains(petType+"")){
                    samePetType++;
                }
            }
            if(samePetType > 0){
                return true;
            }
            else{
                return false;
            }
        }
    }

    private void addArticlesToVM(ArrayList<Artikel> artikelList){
        ArrayList<ArticleItemViewModel> currentVMs = articleVMs.getValue();
        for (Artikel artikel:
             artikelList) {
            currentVMs.add(new ArticleItemViewModel(artikel));
        }
        articleVMs.setValue(currentVMs);
        loading.setValue(false);

    }

    public void setFilters(){
        //untuk tampilan quick filter
        activePetType.setValue(new ArrayList<>());
        activePetTypeInt = new ArrayList<>();

        activeSort.setValue("");
        activeCategory.setValue("");
        activeTarget.setValue("");
        activeCategory.setValue("");

        //untuk keperluan query db
        fieldSorted = "";
        directionSort = Query.Direction.ASCENDING;
        activeCategoryInt = -1;
        activeTargetInt = -1;

        for (int i = 0; i < 8; i++) {
            if(i < 6){
                checkCategory(i);
                if(i < 3){
                    checkTarget(i);
                    if(i < 2){
                        checkSort(i);
                    }
                }
            }
            if(chipPetTypesChecked.getValue()[i]){
                addCheckedPetTypes(i);
            }
        }

        getArticleFiltered();
    }

    private void checkCategory(int index){
        if(chipCategoryChecked.getValue()[index]){
            activeCategoryInt = index;
            activeCategory.setValue(articleCategories[index]);
        }
    }

    private void checkTarget(int index){
        if(chipTargetChecked.getValue()[index]){
            activeTargetInt = index;
            activeTarget.setValue(articlesTarget[index]);
        }
    }

    private void checkSort(int index){
        if(chipSortChecked.getValue()[index]){
            fieldSorted = sortTypes[index];
            directionSort = sortDirections[index];
            activeSort.setValue(sortNames[index]);
        }
    }

    public void setTopikHewan(int topikHewan){
        initFilters();

        ArrayList<String> currentPetTypes = activePetType.getValue();
        currentPetTypes.add(jenisHewan[topikHewan]);
        activePetTypeInt.add(topikHewan);

        boolean[] currentChecked = chipPetTypesChecked.getValue();
        currentChecked[topikHewan] = true;
        chipPetTypesChecked.setValue(currentChecked);

        getArticleFiltered();
    }

    public void setCategory(int kategori){
        initFilters();
        activeCategory.setValue(articleCategories[kategori]);
        activeCategoryInt = kategori;
        getArticleFiltered();
    }

    public void setTargetReader(int targetReader){
        initFilters();
        activeTarget.setValue(articlesTarget[targetReader]);
        activeTargetInt = targetReader;
        getArticleFiltered();
    }

    public void setSearchKeyword(String searchKeyword){
        initFilters();
        this.searchKeyword = searchKeyword;

        getArticleFiltered();
    }

    private void addCheckedPetTypes(int index){
        ArrayList<String> currentPetTypes = activePetType.getValue();
        currentPetTypes.add(jenisHewan[index]);
        activePetType.setValue(currentPetTypes);
        activePetTypeInt.add(index);
    }

    public void removeSort(){
        activeSort.setValue("");
        fieldSorted = "";
        directionSort = Query.Direction.ASCENDING;

        boolean[] sortChecked = new boolean[2];
        for (int i = 0; i < 2; i++) {
            sortChecked[i] = false;
        }
        chipSortChecked.setValue(sortChecked);

        getArticleFiltered();
    }

    public void removeCategory(){
        activeCategory.setValue("");
        activeCategoryInt = -1;

        boolean[] categoryChecked = new boolean[6];
        for (int i = 0; i < 6; i++) {
            categoryChecked[i] = false;
        }
        chipCategoryChecked.setValue(categoryChecked);

        getArticleFiltered();
    }

    public void removeTarget(){
        activeTarget.setValue("");
        activeTargetInt = -1;

        boolean[] targetChecked = new boolean[3];
        for (int i = 0; i < 3; i++) {
            targetChecked[i] = false;
        }
        chipTargetChecked.setValue(targetChecked);

        getArticleFiltered();
    }

    public void removePetType(int position){
        ArrayList<String> currentPetTypes = activePetType.getValue();
        int petChipNum = activePetTypeInt.get(position);
        currentPetTypes.remove(position);
        activePetTypeInt.remove(position);
        activePetType.setValue(currentPetTypes);

        boolean[] categories = chipPetTypesChecked.getValue();
        categories[petChipNum] = false;
        chipPetTypesChecked.setValue(categories);

        getArticleFiltered();
    }


    public LiveData<ArrayList<String>> getActivePetTypes() {
        return activePetType;
    }

    public LiveData<String> getActiveSort() {
        return activeSort;
    }

    public LiveData<String> getActiveCategory() {
        return activeCategory;
    }

    public LiveData<String> getActiveTarget() {
        return activeTarget;
    }

    public LiveData<Boolean> isLoading() {
        return loading;
    }

    public LiveData<ArrayList<ArticleItemViewModel>> getArticles() {
        return articleVMs;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }
}
