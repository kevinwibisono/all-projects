package my.istts.finalproject.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.istts.finalproject.models.AlamatDBAccess;
import my.istts.finalproject.models.herejsonaddress.AlamatHere;
import my.istts.finalproject.models.herejsonaddress.AlamatList;
import my.istts.finalproject.models.herejsonaddress.DetailAlamatHere;
import my.istts.finalproject.models.herejsonaddress.KoordinatAlamatHere;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressSearchViewModel {
    private AlamatDBAccess alamatDB;

    public AddressSearchViewModel() {
        this.alamatDB = new AlamatDBAccess();
    }

    private MutableLiveData<Boolean> addressFound = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> hereAddress;
    private ArrayList<DetailAlamatHere> hereAddressDetails;
    private ArrayList<KoordinatAlamatHere> hereCoordinates;
    private MutableLiveData<String> errorMsg;
    private MutableLiveData<KoordinatAlamatHere> koordinat;
    private String hereChosenAddress, hereCity, herePostalCode, hereDistrict, hereSubDistrict;
    private AlamatList list;

    public LiveData<Boolean> isAddressFound(){
        return addressFound;
    }

    public LiveData<ArrayList<String>> getHereAddresses(){
        if(hereAddress == null){
            hereAddress = new MutableLiveData<>(new ArrayList<>());
        }
        return hereAddress;
    }

    public LiveData<String> getErrorMsg(){
        if(errorMsg == null){
            errorMsg = new MutableLiveData<>();
        }
        return errorMsg;
    }

    public LiveData<KoordinatAlamatHere> getKoordinat(){
        if(koordinat == null){
            koordinat = new MutableLiveData<>();
        }
        return koordinat;
    }

    public String getHereChosenAddress(){
        return hereChosenAddress;
    }

    public String getHereCity() {
        return hereCity;
    }

    public String getHerePostalCode() {
        return herePostalCode;
    }

    public String getHereDistrict() {
        return hereDistrict;
    }

    public String getHereSubDistrict() {
        return hereSubDistrict;
    }

    public void searchAddressHere(String address){
        //procedure utk mendapatkan daftar address dari HERE
        //1. reset kembali list address yang akan ditampilkan di listview
        //2. terima string dari inputan yang diberikan afterTextChanged
        //3. akses function repos untuk search alamat yang plg cocok di HERE Api
        //4. muat hasil di ArrayList
        addressFound.setValue(false);

        alamatDB.getHereAddresses(address, 10).enqueue(new Callback<AlamatList>() {
            @Override
            public void onResponse(Call<AlamatList> call, Response<AlamatList> response) {
                list = response.body();
                ArrayList<String> addresses = new ArrayList<>();
                resetArrayList();
                if(list != null){
                    for (AlamatHere address:
                            list.getListAlamat()) {
                        addresses.add(address.getTitle());
                        hereAddressDetails.add(address.getAddress());
                        hereCoordinates.add(address.getPosition());
                    }
                }

                addressFound.setValue(true);
                hereAddress.setValue(addresses);
            }

            @Override
            public void onFailure(Call<AlamatList> call, Throwable t) {
                addressFound.setValue(true);
                errorMsg.setValue("Terjadi masalah dalam mendapatkan daftar alamat. Mohon coba beberapa saat lagi");
            }
        });
    }

    public void searchAddressWithCoordinates(double latitude, double longitude){
        addressFound.setValue(false);

        alamatDB.getHereAddressByCoordinates(latitude, longitude).enqueue(new Callback<AlamatList>() {
            @Override
            public void onResponse(Call<AlamatList> call, Response<AlamatList> response) {
                list = response.body();
                ArrayList<String> addresses = new ArrayList<>();
                resetArrayList();
                if(list != null){
                    for (AlamatHere address:
                            list.getListAlamat()) {
                        addresses.add(address.getTitle());
                        hereAddressDetails.add(address.getAddress());
                        hereCoordinates.add(address.getPosition());
                    }
                }

                addressFound.setValue(true);
                hereAddress.setValue(addresses);
            }

            @Override
            public void onFailure(Call<AlamatList> call, Throwable t) {
                addressFound.setValue(true);
                errorMsg.setValue("Terjadi masalah dalam mendapatkan daftar alamat. Mohon coba beberapa saat lagi");
            }
        });
    }

    public void pickAddress(int index){
        koordinat.setValue(hereCoordinates.get(index));
        hereChosenAddress = "";
        if(list.getListAlamat().get(index).getAddress().getHouseNumber() != null){
            hereChosenAddress += list.getListAlamat().get(index).getAddress().getHouseNumber()+", ";
        }
        hereChosenAddress += list.getListAlamat().get(index).getAddress().getStreet();
        hereSubDistrict = hereAddressDetails.get(index).getSubdistrict();
        hereDistrict = hereAddressDetails.get(index).getDistrict();
        hereCity = hereAddressDetails.get(index).getCity();
        herePostalCode = hereAddressDetails.get(index).getPostalCode();
        resetArrayList();
    }

    private void resetArrayList(){
        hereAddress.setValue(new ArrayList<>());
        hereAddressDetails = new ArrayList<>();
        hereCoordinates = new ArrayList<>();
    }

    public String getStringCoordinate(){
        return koordinat.getValue().getLat()+","+koordinat.getValue().getLng();
    }
}
