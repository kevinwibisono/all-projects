package my.istts.finalproject;

public class SearchItem {
    private String search;
    private String searchType;
    private int index;
    private int jenis;

    public SearchItem(String search, String searchType, int index, int jenis) {
        this.search = search;
        this.searchType = searchType;
        this.index = index;
        this.jenis = jenis;
    }

    public String getSearch() {
        return search;
    }

    public String getSearchType() {
        return searchType;
    }

    public int getIndex() {
        return index;
    }

    public int getJenis() {
        return jenis;
    }
}
