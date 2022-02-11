package my.istts.finalproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.SearchItem;

import java.util.ArrayList;

public class SearchViewModel {

    private String[] kategoriList = {"Makanan Anjing", "Makanan Kucing", "Makanan Kelinci", "Makanan Burung", "Makanan Ikan",
            "Makanan Hamster", "Makanan Reptil", "Pakan Ternak", "Peralatan Grooming", "Leash dan Handler", "Treats/Snack (Kudapan)",
            "Peralatan Kesehatan", "Mainan/Alat Ketangkasan", "Peralatan Kebersihan", "Kandang/Tempat Tidur"};

    private String[] facilitiesList = {"Makanan & Minuman", "Ber-AC", "Kamar Privat", "Akses CCTV", "Update Harian", "Taman Bermain",
            "Training & Olahraga", "Antar Jemput", "Grooming"};

    private String[] articlesPetList = {"Anjing", "Kucing", "Kelinci", "Burung", "Ikan", "Hamster", "Reptil", "Lainnya"};
    private String[] articleCategories = {"Informasi", "Tips & Trik", "Acara", "Komunitas", "Peristiwa", "Cerita Pemilik"};
    private String[] articlesTarget = {"Semua Pemilik", "Pemilik Baru", "Pemilik Berpengalaman"};
    private ArrayList<SearchItem> queriesList = new ArrayList<>();


    private MutableLiveData<ArrayList<SearchItem>> queriesRecommendations = new MutableLiveData<>();

    public LiveData<ArrayList<SearchItem>> getQueriesRecommendation(){
        return queriesRecommendations;
    }

    public void setQueriesList(int tipe){
        if(tipe == 0){
            for (int i = 0; i < kategoriList.length; i++) {
                queriesList.add(new SearchItem(kategoriList[i], "Kategori", i, 0));
            }
        }
        else if(tipe == 1){
            for (int i = 0; i < facilitiesList.length; i++) {
                queriesList.add(new SearchItem(facilitiesList[i], "Fasilitas", i, 1));
            }
        }
        else{
            setQueriesListArticles();
        }
    }

    private void setQueriesListArticles(){
        for (int i = 0; i < articlesPetList.length; i++) {
            queriesList.add(new SearchItem(articlesPetList[i], "Topik Hewan", i, 2));
        }

        for (int i = 0; i < articleCategories.length; i++) {
            queriesList.add(new SearchItem(articleCategories[i], "Kategori Artikel", i, 3));
        }
        for (int i = 0; i < articlesTarget.length; i++) {
            queriesList.add(new SearchItem(articlesTarget[i], "Target Pembaca", i, 4));
        }
    }

    public void arrangeSearchReccomendations(String keyword){
        queriesRecommendations.setValue(new ArrayList<>());
        for (SearchItem search:
             queriesList) {
            if(search.getSearch().toLowerCase().contains(keyword.toLowerCase())){
                addQueriesRecc(search);
            }
        }
    }

    private void addQueriesRecc(SearchItem searchItem){
        ArrayList<SearchItem> currentSearchs = queriesRecommendations.getValue();
        currentSearchs.add(searchItem);
        queriesRecommendations.setValue(currentSearchs);
    }


}
