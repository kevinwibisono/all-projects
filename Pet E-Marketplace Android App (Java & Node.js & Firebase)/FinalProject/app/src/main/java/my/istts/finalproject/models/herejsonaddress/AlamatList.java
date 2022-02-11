package my.istts.finalproject.models.herejsonaddress;

import java.util.List;

public class AlamatList {
    private List<AlamatHere> items;

    public AlamatList(List<AlamatHere> items) {
        this.items = items;
    }

    public List<AlamatHere> getListAlamat() {
        return items;
    }
}
