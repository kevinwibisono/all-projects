package my.istts.finalproject.viewmodels.itemviewmodels;

import my.istts.finalproject.models.Alamat;

public class AddressItemViewModel {
    private Alamat alamat;

    public AddressItemViewModel(Alamat alamat) {
        this.alamat = alamat;
    }

    public String getId(){
        return alamat.getId();
    }

    public String getNama(){
        return alamat.getNama();
    }

    public String getPhone(){
        return alamat.getNo_hp();
    }

    public String getFullAddress(){
        return alamat.getAlamat_lengkap();
    }

    public String getAddressDetail(){
        return alamat.toString();
    }

    public String getNote(){
        return alamat.getCatatan();
    }

    public String getCoordinate(){
        return alamat.getKoordinat();
    }

    public Boolean isSelected(){
        return alamat.isDipilih();
    }
}
